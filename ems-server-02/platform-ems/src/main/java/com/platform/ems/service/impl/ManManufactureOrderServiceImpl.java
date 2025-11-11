package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FastByteArrayOutputStream;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.entity.SysClient;
import com.platform.common.core.domain.entity.SysDefaultSettingClient;
import com.platform.common.core.domain.model.DictData;
import com.platform.common.exception.base.BaseException;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.config.MinioConfig;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsTable;
import com.platform.ems.domain.*;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.domain.dto.ManWorkOrderProgressForm;
import com.platform.ems.domain.dto.ManWorkOrderProgressFormConcern;
import com.platform.ems.domain.dto.ManWorkOrderProgressFormData;
import com.platform.ems.domain.dto.ManWorkOrderProgressFormProcess;
import com.platform.ems.domain.dto.request.ManManufactureOrderSetRequest;
import com.platform.ems.domain.dto.response.CommonErrMsgResponse;
import com.platform.ems.domain.dto.response.ItemSummary;
import com.platform.ems.domain.dto.response.form.SaleManufactureOrderProcessFormResponse;
import com.platform.ems.mapper.*;
import com.platform.ems.plug.domain.ConBuTypeManufactureOrder;
import com.platform.ems.plug.domain.ConManufactureDepartment;
import com.platform.ems.plug.mapper.ConBuTypeManufactureOrderMapper;
import com.platform.ems.plug.mapper.ConManufactureDepartmentMapper;
import com.platform.ems.service.IManManufactureOrderService;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.ems.util.LightUtil;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.util.data.ComUtil;
import com.platform.system.domain.SysBusinessBcst;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysBusinessBcstMapper;
import com.platform.system.mapper.SysClientMapper;
import com.platform.system.mapper.SysDefaultSettingClientMapper;
import com.platform.system.mapper.SysTodoTaskMapper;
import com.platform.system.service.ISysDictDataService;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.Collator;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.platform.ems.util.LightUtil.*;
import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import static java.util.stream.Collectors.toList;

/**
 * 生产订单Service业务层处理
 *
 * @author qhq
 * @date 2021-04-10
 */
@Service
@SuppressWarnings("all")
public class ManManufactureOrderServiceImpl extends ServiceImpl<ManManufactureOrderMapper, ManManufactureOrder> implements IManManufactureOrderService {
    @Autowired
    private ManManufactureOrderMapper manManufactureOrderMapper;
    @Autowired
    private ISysDictDataService sysDictDataService;
    @Autowired
    private ManManufactureOrderComponentMapper manManufactureOrderComponentMapper;
    @Autowired
    private ManManufactureOrderProcessMapper manManufactureOrderProcessMapper;
    @Autowired
    private ManManufactureOrderProductMapper manManufactureOrderProductMapper;
    @Autowired
    private ManManufactureOrderConcernTaskMapper manManufactureOrderConcernTaskMapper;
    @Autowired
    private ManManufactureOrderAttachMapper manManufactureOrderAttachMapper;
    @Autowired
    private ConBuTypeManufactureOrderMapper conBuTypeManufactureOrderMapper;
    @Autowired
    private BasStaffMapper basStaffMapper;
    @Autowired
    private TecBomHeadMapper tecBomHeadMapper;
    @Autowired
    private TecBomItemMapper tecBomItemMapper;
    @Autowired
    private ManProcessRouteMapper manProcessRouteMapper;
    @Autowired
    private ManProcessRouteItemMapper manProcessRouteItemMapper;
    @Autowired
    private ManProduceConcernTaskGroupItemMapper manProduceConcernTaskGroupItemMapper;
    @Autowired
    private ConManufactureDepartmentMapper departmentMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private SysBusinessBcstMapper sysBusinessBcstMapper;
    @Autowired
    private BasMaterialAttachmentMapper basMaterialAttachmentMapper;
    @Autowired
    private InvInventoryDocumentItemMapper invInventoryDocumentItemMapper;
    @Autowired
    private ManProcessMapper manProcessMapper;
    @Autowired
    private SysClientMapper sysClientMapper;

    @Autowired
    private MinioConfig minioConfig;
    @Autowired
    private MinioClient client;

    private static final String TITLE = "生产订单";

    /**
     * 按订单：获取即将到期生产订单
     *
     * @param manManufactureOrder
     * @return
     */
    @Override
    public List<ManManufactureOrder> selectExpiringOrderForm(ManManufactureOrder manManufactureOrder) {
        List<ManManufactureOrder> list = manManufactureOrderMapper.selectToexpireList(manManufactureOrder);
        if (CollectionUtil.isNotEmpty(list)) {
            list.forEach(item -> {
                Integer toexpireDays = 0;
                if (item.getToexpireDaysDefalut() != null) {
                    toexpireDays = Integer.valueOf(item.getToexpireDaysDefalut().toString());
                }
                item.setLight(ComUtil.lightValue(item.getCompleteStatus(), item.getPlanEndDate(), toexpireDays));
            });
        }
        return list;
    }

    /**
     * 按订单：获取已逾期生产订单
     *
     * @param manManufactureOrder
     * @return
     */
    @Override
    public List<ManManufactureOrder> selectOverdueOrderForm(ManManufactureOrder manManufactureOrder) {
        List<ManManufactureOrder> list = manManufactureOrderMapper.selectOverdueList(manManufactureOrder);
        if (CollectionUtil.isNotEmpty(list)) {
            list.forEach(item -> {
                Integer toexpireDays = 0;
                if (item.getToexpireDaysDefalut() != null) {
                    toexpireDays = Integer.valueOf(item.getToexpireDaysDefalut().toString());
                }
                item.setLight(ComUtil.lightValue(item.getCompleteStatus(), item.getPlanEndDate(), toexpireDays));
            });
        }
        return list;
    }

    /**
     * 查询生产订单
     *
     * @param clientId 生产订单ID
     * @return 生产订单
     */
    @Override
    public ManManufactureOrder selectManManufactureOrderById(Long manufactureOrderSid) {
        ManManufactureOrder order = manManufactureOrderMapper.selectManManufactureOrderById(manufactureOrderSid);
        if (order != null) {
            // 生产订单-组件对象
            order.setManManufactureOrderComponentList(new ArrayList<>());
            ManManufactureOrderComponent manManufactureOrderComponent = new ManManufactureOrderComponent();
            manManufactureOrderComponent.setManufactureOrderSid(order.getManufactureOrderSid());
            List<ManManufactureOrderComponent> manManufactureOrderComponentList = manManufactureOrderComponentMapper.selectManManufactureOrderComponentList(manManufactureOrderComponent);
            if (CollectionUtil.isNotEmpty(manManufactureOrderComponentList)) {
                order.setManManufactureOrderComponentList(manManufactureOrderComponentList);
            }
            //生产订单-工序对象
            order.setManManufactureOrderProcessList(new ArrayList<>());
            ManManufactureOrderProcess manManufactureOrderProcess = new ManManufactureOrderProcess();
            manManufactureOrderProcess.setManufactureOrderSid(order.getManufactureOrderSid());
            List<ManManufactureOrderProcess> manManufactureOrderProcessList = manManufactureOrderProcessMapper.selectManManufactureOrderProcessList(manManufactureOrderProcess);
            if (CollectionUtil.isNotEmpty(manManufactureOrderProcessList)) {
                // 排序
                manManufactureOrderProcessList = manManufactureOrderProcessList.stream().sorted(
                        Comparator.comparing(ManManufactureOrderProcess::getSerialNumDecimal, Comparator.nullsLast(BigDecimal::compareTo))
                                .thenComparing(ManManufactureOrderProcess::getProcessName, Comparator.nullsLast(String::compareTo).thenComparing(Collator.getInstance(Locale.CHINA)))
                                .thenComparing(ManManufactureOrderProcess::getPlantShortName, Comparator.nullsLast(String::compareTo).thenComparing(Collator.getInstance(Locale.CHINA)))
                                .thenComparing(ManManufactureOrderProcess::getWorkCenterName, Comparator.nullsLast(String::compareTo).thenComparing(Collator.getInstance(Locale.CHINA)))
                ).collect(Collectors.toList());
                for (ManManufactureOrderProcess item : manManufactureOrderProcessList) {
                    gtDirector(item);
                    // 图片视频
                    item.setPicturePathList(ComUtil.strToArr(item.getPicturePath()));
                    item.setVideoPathList(ComUtil.strToArr(item.getVideoPath()));
                }
                order.setManManufactureOrderProcessList(manManufactureOrderProcessList);
            }
            //生产订单-产品明细对象
            order.setManManufactureOrderProductList(new ArrayList<>());
            ManManufactureOrderProduct manManufactureOrderProduct = new ManManufactureOrderProduct();
            manManufactureOrderProduct.setManufactureOrderSid(manufactureOrderSid);
            List<ManManufactureOrderProduct> productList =
                    manManufactureOrderProductMapper.selectManManufactureOrderProductList(manManufactureOrderProduct);
            if (CollectionUtil.isNotEmpty(productList)) {
                productList = productList.stream()
                        .sorted(Comparator.comparing(ManManufactureOrderProduct::getContractDate, Comparator.nullsLast(String::compareTo))
                                .thenComparing(ManManufactureOrderProduct::getMaterialCode, Comparator.nullsLast(String::compareTo).thenComparing(Collator.getInstance(Locale.CHINA)))
                                .thenComparing(ManManufactureOrderProduct::getSort1, Comparator.nullsLast(BigDecimal::compareTo))
                                .thenComparing(ManManufactureOrderProduct::getSku1Name, Comparator.nullsLast(String::compareTo).thenComparing(Collator.getInstance(Locale.CHINA)))
                                .thenComparing(ManManufactureOrderProduct::getSort2, Comparator.nullsLast(BigDecimal::compareTo))
                                .thenComparing(ManManufactureOrderProduct::getSku2Name, Comparator.nullsLast(String::compareTo).thenComparing(Comparator.nullsLast(String::compareTo)))
                                .thenComparing(ManManufactureOrderProduct::getSalesOrderCode, Comparator.nullsLast(String::compareTo))).collect(toList());
                order.setManManufactureOrderProductList(productList);
            }
            // 生产关注事项
            order.setConcernTaskList(new ArrayList<>());
            List<ManManufactureOrderConcernTask> taskList =
                    manManufactureOrderConcernTaskMapper.selectManManufactureOrderConcernTaskList(new ManManufactureOrderConcernTask().setManufactureOrderSid(manufactureOrderSid));
            if (CollectionUtil.isNotEmpty(taskList)) {
                taskList.forEach(item -> {
                    // 图片视频
                    item.setPicturePathList(ComUtil.strToArr(item.getPicturePath()));
                    item.setVideoPathList(ComUtil.strToArr(item.getVideoPath()));
                });
                // 排序
                taskList = taskList.stream().sorted(
                        Comparator.comparing(ManManufactureOrderConcernTask::getSerialNum, Comparator.nullsLast(BigDecimal::compareTo))
                                .thenComparing(ManManufactureOrderConcernTask::getConcernTaskName, Comparator.nullsLast(String::compareTo).thenComparing(Collator.getInstance(Locale.CHINA)))
                ).collect(Collectors.toList());
                order.setConcernTaskList(taskList);
            }
            //生产订单-附件
            order.setAttachmentList(new ArrayList<>());
            List<ManManufactureOrderAttach> attachList =
                    manManufactureOrderAttachMapper.selectManManufactureOrderAttachList(new ManManufactureOrderAttach().setManufactureOrderSid(manufactureOrderSid));
            if (CollectionUtil.isNotEmpty(attachList)) {
                order.setAttachmentList(attachList);
            }
            //商品-附件
            order.setBasMaterialAttachmentList(new ArrayList<>());
            List<BasMaterialAttachment> basMaterialAttachmentList =
                    basMaterialAttachmentMapper.selectBasMaterialAttachmentList(new BasMaterialAttachment().setMaterialSid(order.getMaterialSid()));
            if (CollectionUtil.isNotEmpty(basMaterialAttachmentList)) {
                order.setBasMaterialAttachmentList(basMaterialAttachmentList);
            }
            //商品明细汇总
            if (CollUtil.isNotEmpty(productList)) {
                //尺码清单(过滤重复)
                List<String> sizeNameList = productList.stream().map(ManManufactureOrderProduct::getSku2Name).distinct().collect(Collectors.toList());
                //按照'商品编码+颜色+合同交期'维度，汇总各尺码的排产量（仅显示有排产的尺码的数量）
                List<ManManufactureOrderProduct> distinctList =
                        productList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() ->
                                new TreeSet<>(Comparator.comparing(o -> o.getMaterialSid() + ";" + o.getSku1Sid() + ";" + o.getContractDate()))), ArrayList::new));
                for (ManManufactureOrderProduct product : distinctList) {
                    List<ItemSummary> itemSummaryList = new ArrayList<>();
                    for (String size : sizeNameList) {
                        ItemSummary itemSummary = new ItemSummary();
                        itemSummary.setSku2Name(size);
                        //同款同颜色同合同交期(合同交期不为空)
                        if (StrUtil.isNotEmpty(product.getContractDate())) {
                            List<ManManufactureOrderProduct> contractDateY = productList.stream().filter(o -> StrUtil.isNotEmpty(o.getContractDate())).collect(Collectors.toList());
                            List<ManManufactureOrderProduct> sameList = new ArrayList<ManManufactureOrderProduct>();
                            if (product.getSku1Sid() != null) {
                                sameList = contractDateY.stream().filter(o -> o.getMaterialSid().equals(product.getMaterialSid())
                                                && o.getSku1Sid().equals(product.getSku1Sid())
                                                && o.getContractDate().equals(product.getContractDate()))
                                        .collect(Collectors.toList());
                            } else {
                                sameList = contractDateY.stream().filter(o -> o.getMaterialSid().equals(product.getMaterialSid())
                                                && o.getContractDate().equals(product.getContractDate()))
                                        .collect(Collectors.toList());
                            }
                            //汇总同维度排产量
                            setQuantity(size, itemSummary, sameList);
                            //排产量小计
                            setQuantitySum(product, sameList);
                        } else {
                            //同款同颜色同合同交期(合同交期为空)
                            List<ManManufactureOrderProduct> contractDateN = productList.stream()
                                    .filter(o -> StrUtil.isEmpty(o.getContractDate())).collect(Collectors.toList());
                            List<ManManufactureOrderProduct> sameList = new ArrayList<ManManufactureOrderProduct>();
                            if (product.getSku1Sid() != null) {
                                sameList = contractDateN.stream()
                                        .filter(o -> o.getMaterialSid().equals(product.getMaterialSid()) &&
                                                o.getSku1Sid().equals(product.getSku1Sid())).collect(Collectors.toList());
                            } else {
                                sameList = contractDateN.stream()
                                        .filter(o -> o.getMaterialSid().equals(product.getMaterialSid())).collect(Collectors.toList());
                            }
                            //汇总同维度排产量
                            setQuantity(size, itemSummary, sameList);
                            //排产量小计
                            setQuantitySum(product, sameList);
                        }
                        itemSummaryList.add(itemSummary);
                    }
                    itemSummaryList = itemSummaryList.stream().sorted(Comparator.comparing(ItemSummary::getSku2Name)).collect(Collectors.toList());
                    //对尺码排序
                    if (CollectionUtil.isNotEmpty(itemSummaryList)) {
                        itemSummaryList.forEach(li -> {
                            String skuName = li.getSku2Name();
                            if (skuName != null) {
                                String[] nameSplit = nameSplit = skuName.split("/");
                                if (nameSplit.length == 1) {
                                    li.setFirstSort(nameSplit[0]);
                                } else {
                                    String[] name2split = nameSplit[1].split("\\(");
                                    if (name2split.length == 2) {
                                        li.setSecondSort(name2split[0].replaceAll("[a-zA-Z]", ""));

                                        li.setThirdSort(name2split[1]);
                                    } else {
                                        li.setSecondSort(nameSplit[1].replaceAll("[a-zA-Z]", ""));
                                    }
                                    li.setFirstSort(nameSplit[0]);
                                }
                            }
                        });
                        List<ItemSummary> allList = new ArrayList<>();
                        List<ItemSummary> allThirdList = new ArrayList<>();
                        List<ItemSummary> sortThird = itemSummaryList.stream().filter(li -> li.getThirdSort() != null).collect(Collectors.toList());
                        List<ItemSummary> sortThirdNull = itemSummaryList.stream().filter(li -> li.getThirdSort() == null).collect(Collectors.toList());
                        sortThird = sortThird.stream().sorted(Comparator.comparing(li -> li.getThirdSort())).collect(Collectors.toList());
                        allThirdList.addAll(sortThird);
                        allThirdList.addAll(sortThirdNull);
                        List<ItemSummary> sort = allThirdList.stream().filter(li -> li.getSecondSort() != null).collect(Collectors.toList());
                        sort = sort.stream().sorted(Comparator.comparing(li -> Integer.valueOf(li.getSecondSort()))).collect(Collectors.toList());
                        List<ItemSummary> sortNull = allThirdList.stream().filter(li -> li.getSecondSort() == null).collect(Collectors.toList());
                        allList.addAll(sort);
                        allList.addAll(sortNull);
                        itemSummaryList = allList.stream().sorted(Comparator.comparing(item -> item.getFirstSort())
                        ).collect(Collectors.toList());
                    }
                    product.setItemSummaryList(itemSummaryList);
                }
                order.setItemSummaryList(distinctList);
            }
        }
        MongodbUtil.find(order);
        return order;
    }

    /**
     * 得到标签信息
     *
     * @param manufactureOrderSid 生产订单ID
     * @return 得到标签信息
     */
    @Override
    public ManManufactureOrder getLabelInfo(Long manufactureOrderSid) {
        if (manufactureOrderSid == null) {
            return null;
        }
        ManManufactureOrder order = manManufactureOrderMapper.selectManManufactureOrderById(manufactureOrderSid);
        if (order == null) {
            throw new BaseException("找不到该生产订单");
        }
        // 只有一个客户才显示客户简称
        List<ManManufactureOrderProduct> orderProductList = manManufactureOrderProductMapper.selectManManufactureOrderProductList(
                new ManManufactureOrderProduct().setManufactureOrderSid(manufactureOrderSid));
        if (CollectionUtil.isNotEmpty(orderProductList)) {
            orderProductList = orderProductList.stream().filter(o -> StrUtil.isNotBlank(o.getCustomerShortName())).collect(toList());
            if (CollectionUtil.isNotEmpty(orderProductList)) {
                order.setCustomerShortName(orderProductList.get(0).getCustomerShortName());
            }
        }
        SysClient sysClient = sysClientMapper.selectOne(new QueryWrapper<SysClient>()
                .lambda().eq(SysClient::getClientId, ApiThreadLocalUtil.get().getClientId()));
        if (StrUtil.isNotBlank(sysClient.getLogoPicturePath())) {
            GetObjectResponse object = null;
            String path = sysClient.getLogoPicturePath();
            String str1 = path.substring(0, path.indexOf("/" + minioConfig.getBucketName()));
            String str2 = path.substring(str1.length() + 9);
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(str2).build();
            try {
                object = client.getObject(args);
                FastByteArrayOutputStream fos = new FastByteArrayOutputStream();
                BufferedImage image = ImageIO.read(object);
                BufferedImage images = new BufferedImage(55, 55, TYPE_INT_RGB);
                Graphics graphics = images.createGraphics();
                graphics.drawImage(image, 0, 0, 55, 55, null);
                ImageIO.write(images, "png", fos);
                //将Logo转成要在前端显示需要转成Base64
                order.setLogoPicturePath(Base64.getEncoder().encodeToString(fos.toByteArray()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 生成二维码并指定宽高
        BufferedImage generate = QrCodeUtil.generate(order.getManufactureOrderCode().toString(), 80, 80);
        // 转换流信息写出
        FastByteArrayOutputStream os = new FastByteArrayOutputStream();
        try {
            ImageIO.write(generate, "jpg", os);
            //如果二维码要在前端显示需要转成Base64
            String qrcode = Base64.getEncoder().encodeToString(os.toByteArray());
            order.setQrCode(qrcode);
        } catch (IOException e) {
            throw new BaseException(e.getMessage());
        }
        return order;
    }

    /**
     * 复制生产订单
     *
     * @param clientId 生产订单ID
     * @return 生产订单
     */
    @Override
    public ManManufactureOrder copyManManufactureOrderById(Long manufactureOrderSid) {
        ManManufactureOrder response = new ManManufactureOrder();
        // 创建人创建日期
        String creatorAccount = ApiThreadLocalUtil.get().getUsername();
        String creatorAccountName = ApiThreadLocalUtil.get().getSysUser().getNickName();
        Date createDate = new Date();
        ManManufactureOrder manufactureOrder = this.selectManManufactureOrderById(manufactureOrderSid);
        if (manufactureOrder == null) {
            return response;
        }
        // 主表主要复制的内容
        response.setPlantSid(manufactureOrder.getPlantSid()).setMaterialSid(manufactureOrder.getMaterialSid())
                .setDocumentType(manufactureOrder.getDocumentType()).setBusinessType(manufactureOrder.getBusinessType());
        response.setPlantName(manufactureOrder.getPlantName()).setMaterialName(manufactureOrder.getMaterialName())
                .setMaterialCode(manufactureOrder.getMaterialCode()).setUnitBase(manufactureOrder.getUnitBase());
        response.setCreatorAccount(creatorAccount).setCreateDate(createDate)
                .setEnterDimension(manufactureOrder.getEnterDimension()).setCreatorAccountName(creatorAccountName);
        response.setHandleStatus(ConstantsEms.SAVA_STATUS).setCompleteStatus(ConstantsEms.COMPLETE_STATUS_WKS);
        response.setGenjinrenSid(manufactureOrder.getGenjinrenSid()).setGenjinrenCode(manufactureOrder.getGenjinrenCode())
                .setGenjinrenName(manufactureOrder.getGenjinrenName()).setGenjinrenNameCode(manufactureOrder.getGenjinrenNameCode());
        // 产品明细拿走不需要的内容
        if (CollectionUtil.isNotEmpty(manufactureOrder.getManManufactureOrderProductList())) {
            manufactureOrder.getManManufactureOrderProductList().forEach(product -> {
                product.setManufactureOrderSid(null).setManufactureOrderProductSid(null);
                product.setCreatorAccount(creatorAccount).setCreateDate(createDate)
                        .setCreatorAccountName(creatorAccountName);
                product.setUpdaterAccount(null).setUpdateDate(null);
                product.setPlanStartDate(null).setPlanEndDate(null);
            });
            response.setManManufactureOrderProductList(manufactureOrder.getManManufactureOrderProductList());
        }
        // 工序明细拿走不需要的内容
        if (CollectionUtil.isNotEmpty(manufactureOrder.getManManufactureOrderProcessList())) {
            manufactureOrder.getManManufactureOrderProcessList().forEach(process -> {
                process.setManufactureOrderSid(null).setManufactureOrderProcessSid(null);
                process.setInitialPlanEndDate(null);
                process.setCreatorAccount(creatorAccount).setCreateDate(createDate)
                        .setCreatorAccountName(creatorAccountName);
                process.setUpdaterAccount(null).setUpdateDate(null);
            });
            response.setManManufactureOrderProcessList(manufactureOrder.getManManufactureOrderProcessList());
        }
        // 关注事项明细拿走不需要的内容
        if (CollectionUtil.isNotEmpty(manufactureOrder.getConcernTaskList())) {
            manufactureOrder.getConcernTaskList().forEach(item -> {
                item.setManufactureOrderSid(null).setManufactureOrderConcernTaskSid(null);
                item.setInitialPlanEndDate(null);
                item.setCreatorAccount(creatorAccount).setCreateDate(createDate)
                        .setCreatorAccountName(creatorAccountName);
                item.setUpdaterAccount(null).setUpdateDate(null);
            });
            response.setConcernTaskList(manufactureOrder.getConcernTaskList());
        }
        return response;
    }

    private void setQuantitySum(ManManufactureOrderProduct product, List<ManManufactureOrderProduct> sameList) {
        BigDecimal quantitySum = sameList.stream().filter(o -> o.getQuantity() != null).map(ManManufactureOrderProduct::getQuantity).reduce(BigDecimal.ZERO, BigDecimal::add);
        product.setQuantitySum(quantitySum);
    }

    private void setQuantity(String size, ItemSummary itemSummary, List<ManManufactureOrderProduct> sameList) {
        for (ManManufactureOrderProduct o : sameList) {
            if (size != null && o.getSku2Name() != null && size.equals(o.getSku2Name())) {
                BigDecimal quantity = itemSummary.getQuantity();
                if (quantity != null) {
                    quantity = quantity.add(o.getQuantity());
                    itemSummary.setQuantity(quantity);
                } else {
                    itemSummary.setQuantity(o.getQuantity());
                }
            }
        }
    }

    private void inStoreQuantity(ManManufactureOrder order) {
        List<InvInventoryDocumentItem> itemList = invInventoryDocumentItemMapper.selectList(new QueryWrapper<InvInventoryDocumentItem>().lambda()
                .eq(InvInventoryDocumentItem::getReferDocumentSid, order.getManufactureOrderSid()));
        if (CollectionUtil.isNotEmpty(itemList)) {
            BigDecimal inStoreQuantity = itemList.stream().filter(o -> o.getQuantity() != null && o.getQuantity().compareTo(BigDecimal.ZERO) == 1)
                    .map(InvInventoryDocumentItem::getQuantity).reduce(BigDecimal.ZERO, BigDecimal::add);
            order.setInStoreQuantity(new BigDecimal(inStoreQuantity.stripTrailingZeros().toPlainString()));
        } else {
            order.setInStoreQuantity(new BigDecimal(BigDecimal.ZERO.stripTrailingZeros().toPlainString()));
        }
    }

    private void quantity(ManManufactureOrder order) {
        List<ManManufactureOrderProduct> itemList = manManufactureOrderProductMapper.selectList(new QueryWrapper<ManManufactureOrderProduct>().lambda()
                .eq(ManManufactureOrderProduct::getManufactureOrderSid, order.getManufactureOrderSid()));
        if (CollectionUtil.isNotEmpty(itemList)) {
            BigDecimal quantity = itemList.stream().filter(o -> o.getQuantity() != null && o.getQuantity().compareTo(BigDecimal.ZERO) == 1)
                    .map(ManManufactureOrderProduct::getQuantity).reduce(BigDecimal.ZERO, BigDecimal::add);
            order.setQuantity(new BigDecimal(quantity.stripTrailingZeros().toPlainString()));
        } else {
            order.setQuantity(new BigDecimal(BigDecimal.ZERO.stripTrailingZeros().toPlainString()));
        }
    }

    /**
     * 获得完成量校验参考工序code
     *
     * @param manManufactureOrderProcess ManManufactureOrderProcess
     * @return 结果
     */
    private String getQuantityReferProcessCode(ManManufactureOrderProcess manManufactureOrderProcess) {
        String processCode = manManufactureOrderProcess.getQuantityReferProcessCode();
        if (manManufactureOrderProcess.getQuantityReferProcessSid() != null && StrUtil.isBlank(manManufactureOrderProcess.getQuantityTypeReferProcess())) {
            throw new BaseException("“工序列表“页签中，参考工序所引用数量类型不能为空");
        }
        if (StrUtil.isBlank(processCode)) {
            ManProcess manProcess = manProcessMapper.selectById(manManufactureOrderProcess.getQuantityReferProcessSid());
            if (manProcess != null) {
                processCode = manProcess.getProcessCode();
            }
        }
        return processCode;
    }

    /**
     * 查询生产订单列表
     *
     * @param manManufactureOrder 生产订单
     * @return 生产订单
     */
    @Override
    public List<ManManufactureOrder> selectManManufactureOrderList(ManManufactureOrder manManufactureOrder) {
        String enterDimension = manManufactureOrder.getEnterDimension();
        List<ManManufactureOrder> list = manManufactureOrderMapper.selectManManufactureOrderList(manManufactureOrder);
        if (CollectionUtil.isNotEmpty(list)) {
            int i = 0, j = 0;
            for (ManManufactureOrder order : list) {
                Integer toexpireDays = 0;
                if (order.getToexpireDaysDefalut() != null) {
                    toexpireDays = Integer.valueOf(order.getToexpireDaysDefalut().toString());
                }
                order.setLight(ComUtil.lightValue(order.getCompleteStatus(), order.getPlanEndDate(), toexpireDays));
                i = 0;
                j = 0;
                List<ManManufactureOrderConcernTask> taskList = manManufactureOrderConcernTaskMapper.selectManManufactureOrderConcernTaskList(new ManManufactureOrderConcernTask()
                        .setManufactureOrderSid(order.getManufactureOrderSid()).setConcernTaskTypeList(new String[]{"TG", "SP"}));
                if (CollectionUtil.isNotEmpty(taskList)) {
                    for (ManManufactureOrderConcernTask item : taskList) {
                        // 图片视频
                        item.setPicturePathList(ComUtil.strToArr(item.getPicturePath()));
                        item.setVideoPathList(ComUtil.strToArr(item.getVideoPath()));
                        if ("TG".equals(item.getConcernTaskType()) && i == 0) {
                            i = 1;
                            order.setPlanQuantityTg(item.getPlanQuantity());
                            order.setCompleteStatusTg(item.getEndStatus());
                            order.setPlanEndDateTg(item.getPlanEndDate());
                            order.setActualEndDateTg(item.getActualEndDate());
                        } else if ("SP".equals(item.getConcernTaskType()) && j == 0) {
                            j = 1;
                            order.setPlanQuantitySp(item.getPlanQuantity());
                            order.setCompleteStatusSp(item.getEndStatus());
                            order.setPlanEndDateSp(item.getPlanEndDate());
                            order.setActualEndDateSp(item.getActualEndDate());
                        } else {
                        }
                    }
                }
            }
        }
        return list;
    }

    /**
     * 生产进度状态报表
     */
    @Override
    public List<ManManufactureOrder> selectStatusReport(ManManufactureOrder manManufactureOrder) {
        manManufactureOrder.setHandleStatus(ConstantsEms.CHECK_STATUS);
        List<ManManufactureOrder> orderList = manManufactureOrderMapper.selectStatusReport(manManufactureOrder);
        if (CollectionUtil.isNotEmpty(orderList)) {
            LocalDate localDate = LocalDate.now();
            LocalDateTime date = localDate.atStartOfDay();
            for (ManManufactureOrder order : orderList) {
                order.setLight(LIGHT_NULL); // 空白
                // 指示灯：空白-1红色0绿色1橙黄2蓝色3
                if (ConstantsEms.COMPLETE_STATUS_YWG.equals(order.getCompleteStatus())) {
                    order.setLight(LIGHT_BLUE); // 蓝色
                } else if (ConstantsEms.END_STATUS_WKS.equals(order.getCompleteStatus())) {
                    order.setLight(LightUtil.LIGHT_GRY); // 未开始
                } else if (ConstantsEms.END_STATUS_ZG.equals(order.getCompleteStatus())) {
                    order.setLight(LIGHT_GRY_ZG); // 暂搁
                } else if (ConstantsEms.END_STATUS_QX.equals(order.getCompleteStatus())) {
                    order.setLight(LIGHT_GRY_QX); // 取消
                } else if (ConstantsEms.END_STATUS_JXZ.equals(order.getCompleteStatus())) {
                    if (order.getPlanEndDate() == null) {
                        order.setLight(LIGHT_NULL); // 空白
                    } else {
                        LocalDateTime ldt1 = order.getPlanEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                        if (ldt1.isBefore(date)) {
                            order.setLight(LIGHT_RED); // 红灯
                        } else {
                            Duration duration = Duration.between(ldt1, date); // 注意比较是连时间都比较的
                            long durnDay = duration.toDays();      // 计算天数差
                            long days = order.getToexpireDaysDefalut() == null ? 0 : (long) order.getToexpireDaysDefalut();
                            if (Math.abs(durnDay) > days) {
                                order.setLight(LIGHT_GREEN); // 绿灯
                            } else {
                                order.setLight(LIGHT_YELLOW); // 橙灯
                            }
                        }
                    }
                }

                // 生产关注事项
                order.setConcernTaskList(new ArrayList<>());
                List<ManManufactureOrderConcernTask> taskList =
                        manManufactureOrderConcernTaskMapper.selectManManufactureOrderConcernTaskList(new ManManufactureOrderConcernTask()
                                .setManufactureOrderSid(order.getManufactureOrderSid()));
                if (CollectionUtil.isNotEmpty(taskList)) {
                    // 排序
                    taskList = taskList.stream().sorted(
                            Comparator.comparing(ManManufactureOrderConcernTask::getSerialNum, Comparator.nullsLast(BigDecimal::compareTo))
                                    .thenComparing(ManManufactureOrderConcernTask::getConcernTaskName,
                                            Comparator.nullsLast(String::compareTo).thenComparing(Collator.getInstance(Locale.CHINA)))
                    ).collect(Collectors.toList());
                    taskList.forEach(item -> {
                        // 图片视频
                        item.setPicturePathList(ComUtil.strToArr(item.getPicturePath()));
                        item.setVideoPathList(ComUtil.strToArr(item.getVideoPath()));
                        item.setLight(ComUtil.lightValue(item.getEndStatus(), item.getPlanEndDate(), item.getToexpireDaysScddSx()));
                    });
                    order.setConcernTaskList(taskList);
                }

                // 工序里程碑
                order.setManManufactureOrderProcessList(new ArrayList<>());
                List<ManManufactureOrderProcess> processList = manManufactureOrderProcessMapper.selectManManufactureOrderProcessList
                        (new ManManufactureOrderProcess().setManufactureOrderSid(order.getManufactureOrderSid()));
                if (CollectionUtil.isNotEmpty(processList)) {
                    // 取里程碑分组
                    List<ManManufactureOrderProcess> newProcessList = new ArrayList<>();
                    processList = processList.stream().filter(i -> i.getMilestone() != null).collect(toList());
                    if (CollectionUtil.isNotEmpty(processList)) {
                        // 取里程碑分组
                        Map<String, ManManufactureOrderProcess> map = processList.stream()
                                .collect(Collectors.toMap(ManManufactureOrderProcess::getMilestone,
                                        Function.identity(),
                                        (existing, replacement) -> {
                                            // Custom merge function
                                            Date existingDate = existing.getPlanEndDate();
                                            Date replacementDate = replacement.getPlanEndDate();
                                            return existingDate.compareTo(replacementDate) >= 0 ? existing : replacement;
                                        }));
                        for (String key : map.keySet()) {
                            ManManufactureOrderProcess item = map.get(key);
                            // 图片视频
                            item.setPicturePathList(ComUtil.strToArr(item.getPicturePath()));
                            item.setVideoPathList(ComUtil.strToArr(item.getVideoPath()));
                            item.setLight(ComUtil.lightValue(item.getEndStatus(), item.getPlanEndDate(), item.getToexpireDaysScdd()));
                            newProcessList.add(item);
                        }
                    }
                    // 排序
                    newProcessList = newProcessList.stream().sorted(
                            Comparator.comparing(ManManufactureOrderProcess::getSerialNumDecimal, Comparator.nullsLast(BigDecimal::compareTo))
                                    .thenComparing(ManManufactureOrderProcess::getProcessName, Comparator.nullsLast(String::compareTo).thenComparing(Collator.getInstance(Locale.CHINA)))
                                    .thenComparing(ManManufactureOrderProcess::getPlantShortName, Comparator.nullsLast(String::compareTo).thenComparing(Collator.getInstance(Locale.CHINA)))
                                    .thenComparing(ManManufactureOrderProcess::getWorkCenterName, Comparator.nullsLast(String::compareTo).thenComparing(Collator.getInstance(Locale.CHINA)))
                    ).collect(Collectors.toList());
                    order.setManManufactureOrderProcessList(newProcessList);
                }
            }
        }
        return orderList;
    }

    /**
     * 新增生产订单
     * 需要注意编码重复校验
     *
     * @param manManufactureOrder 生产订单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertManManufactureOrder(ManManufactureOrder manManufactureOrder) {
        setConfirmInfo(manManufactureOrder);
        setProcessRouteCode(manManufactureOrder);
        int row = manManufactureOrderMapper.insert(manManufactureOrder);
        ManManufactureOrder response = manManufactureOrderMapper.selectById(manManufactureOrder.getManufactureOrderSid());
        manManufactureOrder.setManufactureOrderCode(response.getManufactureOrderCode());
        //生产订单-产品明细对象
        List<ManManufactureOrderProduct> manManufactureOrderProductList = manManufactureOrder.getManManufactureOrderProductList();
        if (CollectionUtil.isNotEmpty(manManufactureOrderProductList)) {
            addManManufactureOrderProduct(manManufactureOrder, manManufactureOrderProductList);
        }
        //生产订单-工序对象
        List<ManManufactureOrderProcess> manManufactureOrderProcessList = manManufactureOrder.getManManufactureOrderProcessList();
        if (CollectionUtil.isNotEmpty(manManufactureOrderProcessList)) {
            // 工序，同样的“工厂(工序)”不能存在重复的明细行
            processExamine(manManufactureOrderProcessList);
            addManManufactureOrderProcess(manManufactureOrder, manManufactureOrderProcessList);
        }
        //生产订单-关注事项对象
        List<ManManufactureOrderConcernTask> manManufactureOrderConcernTaskList = manManufactureOrder.getConcernTaskList();
        if (CollectionUtil.isNotEmpty(manManufactureOrderConcernTaskList)) {
            addManManufactureOrderConcernTask(manManufactureOrder, manManufactureOrderConcernTaskList);
        }
        //生产订单-组件对象
        List<ManManufactureOrderComponent> manManufactureOrderComponentList = manManufactureOrder.getManManufactureOrderComponentList();
        if (CollectionUtil.isNotEmpty(manManufactureOrderComponentList)) {
            addManManufactureOrderComponent(manManufactureOrder, manManufactureOrderComponentList);
        }
        //生产订单-附件对象
        List<ManManufactureOrderAttach> attachList = manManufactureOrder.getAttachmentList();
        if (CollectionUtil.isNotEmpty(attachList)) {
            addManManufactureOrderAttach(manManufactureOrder, attachList);
        }
        ManManufactureOrder manufactureOrder = manManufactureOrderMapper.selectManManufactureOrderById(manManufactureOrder.getManufactureOrderSid());
        //待办通知
        SysTodoTask sysTodoTask = new SysTodoTask();
        if (ConstantsEms.SAVA_STATUS.equals(manManufactureOrder.getHandleStatus())) {
            sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                    .setTableName(ConstantsTable.TABLE_MANUFACTURE_ORDER)
                    .setDocumentSid(manManufactureOrder.getManufactureOrderSid());
            List<SysTodoTask> sysTodoTaskList = sysTodoTaskMapper.selectSysTodoTaskList(sysTodoTask);
            if (CollectionUtil.isEmpty(sysTodoTaskList)) {
                sysTodoTask.setTitle("生产订单" + manufactureOrder.getManufactureOrderCode() + "当前是保存状态，请及时处理！")
                        .setDocumentCode(manufactureOrder.getManufactureOrderCode())
                        .setNoticeDate(new Date())
                        .setUserId(ApiThreadLocalUtil.get().getUserid());
                sysTodoTaskMapper.insert(sysTodoTask);
            }
        }
        //
        if (ConstantsEms.CHECK_STATUS.equals(manManufactureOrder.getHandleStatus())) {
            // 设置初始计划结束日期
            manManufactureOrderMapper.setInitialPlanEndDate(new ManManufactureOrder()
                    .setManufactureOrderSidList(new Long[]{manManufactureOrder.getManufactureOrderSid()}));
        }
        //插入日志
        List<OperMsg> msgList = new ArrayList<>();
        MongodbDeal.insert(manManufactureOrder.getManufactureOrderSid(), manManufactureOrder.getHandleStatus(), msgList, TITLE, null);
        return row;
    }

    /*
     * 设置工序明细的操作部门编码
     */
    private void setDepartmentCode(ConManufactureDepartment department, ManManufactureOrderProcess process) {
        if (process.getDepartmentSid() != null) {
            department = departmentMapper.selectById(process.getDepartmentSid());
            if (department != null) {
                process.setDepartmentCode(department.getCode());
            }
        }
    }

    /**
     * 设置工艺路线的code
     */
    private void setProcessRouteCode(ManManufactureOrder order) {
        if (order.getProcessRouteSid() != null) {
            ManProcessRoute route = manProcessRouteMapper.selectById(order.getProcessRouteSid());
            if (route != null) {
                order.setProcessRouteCode(route.getProcessRouteCode());
            }
        }
    }

    /**
     * 设置确认信息
     */
    private void setConfirmInfo(ManManufactureOrder o) {
        if (o == null) {
            return;
        }
        if (ConstantsEms.CHECK_STATUS.equals(o.getHandleStatus())) {
            List<ManManufactureOrderProduct> productList = o.getManManufactureOrderProductList();
            if (CollectionUtil.isEmpty(productList)) {
                throw new BaseException("商品明细行为空，无法确认！");
            }
            productVerify(productList);
            List<ManManufactureOrderProcess> processList = o.getManManufactureOrderProcessList();
            if (CollectionUtil.isEmpty(processList)) {
                throw new BaseException("工序明细行为空，无法确认！");
            }
            SysDefaultSettingClient client = getClientSetting();
            if (client != null && ConstantsEms.YES.equals(client.getIsRequiredConcernTask())) {
                List<ManManufactureOrderConcernTask> concernTaskList = o.getConcernTaskList();
                if (CollectionUtil.isEmpty(concernTaskList)) {
                    throw new BaseException("关注事项明细行为空，无法确认！");
                }
            }
            processVerify(processList);
            o.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            o.setConfirmDate(new Date());
            // 确认操作更新完工状态为进行中
            o.setCompleteStatus(ConstantsEms.COMPLETE_STATUS_JXZ);
        }
        if (ConstantsEms.PRICE_K1.equals(o.getEnterDimension()) && CollectionUtil.isNotEmpty(o.getManManufactureOrderProductList())) {
            Map<String, List<ManManufactureOrderProduct>> map = o.getManManufactureOrderProductList().stream()
                    .collect(Collectors.groupingBy(e -> String.valueOf(e.getSku1Sid())));
            if (map.size() > 1) {
                throw new BaseException("“商品明细”页签，明细行的的颜色不一致，请检查！");
            }
            o.setSkuSid(o.getManManufactureOrderProductList().get(0).getSku1Sid());
            o.setSkuCode(o.getManManufactureOrderProductList().get(0).getSku1Code());
            o.setSkuType(o.getManManufactureOrderProductList().get(0).getSku1Type());
        } else if (ConstantsEms.PRICE_K.equals(o.getEnterDimension())) {
            o.setSkuSid(null);
            o.setSkuCode(null);
            o.setSkuType(null);
        }
        // 跟进人
        o.setGenjinrenCode(null);
        if (o.getGenjinrenSid() != null) {
            BasStaff staff = basStaffMapper.selectById(o.getGenjinrenSid());
            if (staff != null) {
                o.setGenjinrenCode(staff.getStaffCode());
            }
        }
    }

    /*
     * 工序明细行 未填写值的校验
     */
    private void processVerify(List<ManManufactureOrderProcess> processList) {
        processList.forEach(process -> {
            if (process.getPlantSid() == null) {
                throw new BaseException("存在工厂未填写的工序明细行，请检查！");
            }
            if (process.getQuantity() == null) {
                throw new BaseException("存在计划产量未填写的工序明细行，请检查！");
            }
        });
    }

    /*
     * 商品明细行 未填写值的校验
     */
    private void productVerify(List<ManManufactureOrderProduct> productList) {
        productList.forEach(product -> {
            if (product.getQuantity() == null) {
                throw new BaseException("存在本次排产量未填写的商品明细行，请检查！");
            }
        });
    }

    /*
    点击“提交”按钮时，需做如下校验：【后端】
    1》“工序总览”页签，必须有且仅有1个工序的“是否第一个工序”的值为“是”；否则报提示信息：“工序总览”页签，“是否第一个工序”的值未填写或填写错误，请检查！
    2》“工序总览”页签，必须有且仅有1个工序的“是否标志成品完工的工序”的值为“是”；否则报提示信息：“工序总览”页签，“是否标志成品完工的工序”的值未填写或填写错误，请检查！
     */
    private void isProcessVerify(List<ManManufactureOrderProcess> processList) {
        if (CollectionUtil.isEmpty(processList)) {
            return;
        }
        Map<String, List<ManManufactureOrderProcess>> map = processList.stream()
                .collect(Collectors.groupingBy(o -> String.valueOf(o.getIsFirstProcess())));
        List<ManManufactureOrderProcess> temp = map.get("Y");
        if (temp != null) {
            Map<String, List<ManManufactureOrderProcess>> map1 = temp.stream()
                    .collect(Collectors.groupingBy(o -> String.valueOf(o.getProcessSid())));
            if (map1.size() > 1) {
                throw new BaseException("“工序总览”页签，“是否第一个工序”的值填写错误，请检查！");
            }
        }
        Map<String, List<ManManufactureOrderProcess>> map2 = processList.stream()
                .collect(Collectors.groupingBy(o -> String.valueOf(o.getIsProduceComplete())));
        List<ManManufactureOrderProcess> temp2 = map2.get("Y");
        if (temp2 != null) {
            Map<String, List<ManManufactureOrderProcess>> map3 = temp2.stream()
                    .collect(Collectors.groupingBy(o -> String.valueOf(o.getProcessSid())));
            if (map3.size() > 1) {
                throw new BaseException("”工序总览”页签，“是否标志成品完工的工序”的值填写错误，请检查！");
            }
        }
    }

    /*
        3》“工序总览”页签，若同一工序存在多行明细数据，且其中一明细行的“是否第一个工序”的值为“是”，则该工序其它明细行的“是否第一个工序”的值也需要自动保存为“是”
        4》“工序总览”页签，若同一工序存在多行明细数据，且其中一明细行的“是否标志成品完工的工序”的值为“是”，则该工序其它明细行的“是否标志成品完工的工序”的值也需要自动保存为“是”
     */
    private void setIsProcessVerify(List<ManManufactureOrderProcess> processList) {
        List<ManManufactureOrderProcess> firstList = processList.stream().filter(o -> ConstantsEms.YES.equals(o.getIsFirstProcess()) && o.getProcessSid() != null).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(firstList)) {
            Long firstSid = firstList.get(0).getProcessSid();
            List<ManManufactureOrderProcess> firstProcessList = processList.stream().filter(o -> firstSid.equals(o.getProcessSid())).collect(Collectors.toList());
            firstProcessList.forEach(process -> {
                process.setIsFirstProcess(ConstantsEms.YES);
            });
        }
        List<ManManufactureOrderProcess> completeList = processList.stream().filter(o -> ConstantsEms.YES.equals(o.getIsProduceComplete()) && o.getProcessSid() != null).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(completeList)) {
            Long completeSid = completeList.get(0).getProcessSid();
            List<ManManufactureOrderProcess> completeProcessList = processList.stream().filter(o -> completeSid.equals(o.getProcessSid())).collect(Collectors.toList());
            completeProcessList.forEach(process -> {
                process.setIsProduceComplete(ConstantsEms.YES);
            });
        }
    }

    /**
     * 生产订单-产品明细对象
     */
    private void addManManufactureOrderProduct(ManManufactureOrder manManufactureOrder, List<ManManufactureOrderProduct> manManufactureOrderProductList) {
        long i = 1;
        Long productMaxItemNum = manManufactureOrder.getProductMaxItemNum();
        if (productMaxItemNum != null) {
            i = productMaxItemNum + i;
        }
        for (ManManufactureOrderProduct o : manManufactureOrderProductList) {
            o.setManufactureOrderSid(manManufactureOrder.getManufactureOrderSid());
            o.setManufactureOrderCode(manManufactureOrder.getManufactureOrderCode());
            o.setItemNum(i);
            i++;
        }
        manManufactureOrderProductMapper.inserts(manManufactureOrderProductList);
    }

    private void inStoreStatus(ManManufactureOrderProduct o) {
        inStoreQuantity(o);
        if (o.getInStoreQuantity() == null || o.getInStoreQuantity().compareTo(BigDecimal.ZERO) == 0) {
            o.setInStoreStatus(ConstantsEms.IN_STORE_STATUS_NOT);//未入库
        } else if (o.getInStoreQuantity().compareTo(o.getQuantity()) == 0) {
            o.setInStoreStatus(ConstantsEms.IN_STORE_STATUS);//全部入库
        } else {
            o.setInStoreStatus(ConstantsEms.IN_STORE_STATUS_LI);//部分入库
        }
    }

    private void inStoreQuantity(ManManufactureOrderProduct product) {
        List<InvInventoryDocumentItem> itemList = invInventoryDocumentItemMapper.selectList(new QueryWrapper<InvInventoryDocumentItem>().lambda()
                .eq(InvInventoryDocumentItem::getReferDocumentItemSid, product.getManufactureOrderProductSid()));
        if (CollectionUtil.isNotEmpty(itemList)) {
            BigDecimal inStoreQuantity = itemList.stream().filter(o -> o.getQuantity() != null && o.getQuantity().compareTo(BigDecimal.ZERO) == 1)
                    .map(InvInventoryDocumentItem::getQuantity).reduce(BigDecimal.ZERO, BigDecimal::add);
            product.setInStoreQuantity(inStoreQuantity);
        }
    }

    private void deleteManManufactureOrderProduct(ManManufactureOrder manManufactureOrder) {
        manManufactureOrderProductMapper.delete(
                new UpdateWrapper<ManManufactureOrderProduct>()
                        .lambda()
                        .eq(ManManufactureOrderProduct::getManufactureOrderSid, manManufactureOrder.getManufactureOrderSid())
        );
    }

    public void gtDirector(ManManufactureOrderProcess orderProcess) {
        if (orderProcess != null && StrUtil.isNotBlank(orderProcess.getDirectorSid())) {
            String[] directorSidList = orderProcess.getDirectorSid().split(";");
            orderProcess.setDirectorSidList(directorSidList);
        }
    }

    /**
     * 生产订单工序明细负责人多选存值
     *
     * @param orderProcess
     */
    public void setDirector(ManManufactureOrderProcess orderProcess) {
        if (orderProcess == null) {
            return;
        }
        // 负责人
        String directorSids = "", directorCodes = "";
        if (ArrayUtil.isNotEmpty(orderProcess.getDirectorSidList())) {
            List<BasStaff> staffList = basStaffMapper.selectList(new QueryWrapper<BasStaff>().lambda()
                    .in(BasStaff::getStaffSid, orderProcess.getDirectorSidList()));
            if (CollectionUtil.isNotEmpty(staffList)) {
                Map<Long, String> staffMap = staffList.stream()
                        .collect(Collectors.toMap(BasStaff::getStaffSid, BasStaff::getStaffCode, (existing, replacement) -> existing));
                for (Long key : staffMap.keySet()) {
                    directorSids = directorSids + String.valueOf(key) + ";";
                    directorCodes = directorCodes + String.valueOf(staffMap.get(key)) + ";";
                }
            }
        }
        orderProcess.setDirectorSid(directorSids).setDirectorCode(directorCodes);
    }

    /**
     * 生产订单-工序对象
     */
    private void addManManufactureOrderProcess(ManManufactureOrder manManufactureOrder, List<ManManufactureOrderProcess> manManufactureOrderProcessList) {
        long i = 1;
        Long ProcessMaxItemNum = manManufactureOrder.getProcessMaxItemNum();
        if (ProcessMaxItemNum != null) {
            i = ProcessMaxItemNum + i;
        }
        ConManufactureDepartment department = null;
        for (ManManufactureOrderProcess orderProcess : manManufactureOrderProcessList) {
            orderProcess.setQuantityReferProcessCode(getQuantityReferProcessCode(orderProcess));
            orderProcess.setManufactureOrderSid(manManufactureOrder.getManufactureOrderSid());
            orderProcess.setManufactureOrderCode(manManufactureOrder.getManufactureOrderCode());
            orderProcess.setItemNum(i);
            i++;
            setDepartmentCode(department, orderProcess);
            setDirector(orderProcess);
        }
        manManufactureOrderProcessMapper.inserts(manManufactureOrderProcessList);
    }

    /**
     * 生产订单-工序对象
     */
    private void addManManufactureOrderConcernTask(ManManufactureOrder manManufactureOrder, List<ManManufactureOrderConcernTask> manManufactureOrderConcernTaskList) {
        for (ManManufactureOrderConcernTask concernTask : manManufactureOrderConcernTaskList) {
            concernTask.setManufactureOrderSid(manManufactureOrder.getManufactureOrderSid());
            concernTask.setManufactureOrderCode(manManufactureOrder.getManufactureOrderCode());
        }
        manManufactureOrderConcernTaskMapper.inserts(manManufactureOrderConcernTaskList);
    }

    /*
     * 工序，同样的“工厂(工序)”不能存在重复的明细行
     */
    private void processExamine(List<ManManufactureOrderProcess> manManufactureOrderProcessList) {
        List<ManManufactureOrderProcess> processList = manManufactureOrderProcessList.stream().filter(p -> p.getProcessSid() != null
                && p.getPlantSid() != null).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(processList)) {
            processList.forEach(orderProcess -> {
                setDirector(orderProcess);
            });
            Map<String, List<ManManufactureOrderProcess>> map = processList.stream()
                    .collect(Collectors.groupingBy(o -> String.valueOf(o.getProcessSid()) + "-" + String.valueOf(o.getPlantSid()) + "-" + String.valueOf(o.getWorkCenterSid())
                            + "-" + String.valueOf(o.getDirectorSid())));
            if (map.size() != processList.size()) {
                for (List<ManManufactureOrderProcess> value : map.values()) {
                    if (value.size() > 1) {
                        throw new BaseException(value.get(0).getProcessName() + "工序，同样的“工厂(工序)+班组+负责人”存在重复的明细行，请检查！");
                    }
                }
            }
        }
    }

    private void deleteManManufactureOrderProcess(ManManufactureOrder manManufactureOrder) {
        manManufactureOrderProcessMapper.delete(
                new UpdateWrapper<ManManufactureOrderProcess>()
                        .lambda()
                        .eq(ManManufactureOrderProcess::getManufactureOrderSid, manManufactureOrder.getManufactureOrderSid())
        );
    }

    private void deleteManManufactureOrderConcernTask(ManManufactureOrder manManufactureOrder) {
        manManufactureOrderConcernTaskMapper.delete(
                new UpdateWrapper<ManManufactureOrderConcernTask>()
                        .lambda()
                        .eq(ManManufactureOrderConcernTask::getManufactureOrderSid, manManufactureOrder.getManufactureOrderSid())
        );
    }

    /**
     * 生产订单-组件对象
     */
    private void addManManufactureOrderComponent(ManManufactureOrder manManufactureOrder, List<ManManufactureOrderComponent> manManufactureOrderComponentList) {
        manManufactureOrderComponentList.forEach(o -> {
            o.setManufactureOrderSid(manManufactureOrder.getManufactureOrderSid());
            manManufactureOrderComponentMapper.insert(o);
        });
    }

    private void deleteManManufactureOrderComponent(ManManufactureOrder manManufactureOrder) {
        manManufactureOrderComponentMapper.delete(
                new UpdateWrapper<ManManufactureOrderComponent>()
                        .lambda()
                        .eq(ManManufactureOrderComponent::getManufactureOrderSid, manManufactureOrder.getManufactureOrderSid())
        );
    }

    /**
     * 生产订单-附件对象
     */
    private void addManManufactureOrderAttach(ManManufactureOrder manManufactureOrder, List<ManManufactureOrderAttach> attachList) {
        attachList.forEach(o -> {
            o.setManufactureOrderSid(manManufactureOrder.getManufactureOrderSid());
        });
        manManufactureOrderAttachMapper.inserts(attachList);
    }

    private void deleteAttach(ManManufactureOrder manManufactureOrder) {
        manManufactureOrderAttachMapper.delete(
                new UpdateWrapper<ManManufactureOrderAttach>()
                        .lambda()
                        .eq(ManManufactureOrderAttach::getManufactureOrderSid, manManufactureOrder.getManufactureOrderSid())
        );
    }

    /**
     * 修改生产订单
     *
     * @param manManufactureOrder 生产订单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateManManufactureOrder(ManManufactureOrder manManufactureOrder) {
        //设置确认信息
        setConfirmInfo(manManufactureOrder);
        ManManufactureOrder response = manManufactureOrderMapper.selectManManufactureOrderById(manManufactureOrder.getManufactureOrderSid());
        manManufactureOrder.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
        if (manManufactureOrder.getProcessRouteSid() != null && !manManufactureOrder.getProcessRouteSid().equals(response.getProcessRouteSid())) {
            setProcessRouteCode(manManufactureOrder);
        }
        int row = manManufactureOrderMapper.updateAllById(manManufactureOrder);
        //生产订单-产品明细对象
        List<ManManufactureOrderProduct> productList = manManufactureOrder.getManManufactureOrderProductList();
        operateProduct(manManufactureOrder, productList);
        //生产订单-工序对象
        List<ManManufactureOrderProcess> processList = manManufactureOrder.getManManufactureOrderProcessList();
        operateProcess(manManufactureOrder, processList);
        //生产订单-工序对象
        List<ManManufactureOrderConcernTask> concernTaskList = manManufactureOrder.getConcernTaskList();
        operateConcernTask(manManufactureOrder, concernTaskList);
        //生产订单-附件对象
        List<ManManufactureOrderAttach> attachList = manManufactureOrder.getAttachmentList();
        operateAttach(manManufactureOrder, attachList);
        if (!ConstantsEms.SAVA_STATUS.equals(manManufactureOrder.getHandleStatus())) {
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB)
                    .eq(SysTodoTask::getTableName, ConstantsTable.TABLE_MANUFACTURE_ORDER)
                    .eq(SysTodoTask::getDocumentSid, manManufactureOrder.getManufactureOrderSid()));
        }
        //
        if (ConstantsEms.CHECK_STATUS.equals(manManufactureOrder.getHandleStatus())) {
            // 设置初始计划结束日期
            manManufactureOrderMapper.setInitialPlanEndDate(new ManManufactureOrder()
                    .setManufactureOrderSidList(new Long[]{manManufactureOrder.getManufactureOrderSid()}));
        }
        //插入日志
        List<OperMsg> msgList = BeanUtils.eq(response, manManufactureOrder);
        MongodbDeal.update(manManufactureOrder.getManufactureOrderSid(), response.getHandleStatus(), manManufactureOrder.getHandleStatus(), msgList, TITLE, null);
        return row;
    }

    /**
     * 生产订单-产品明细
     */
    private void operateProduct(ManManufactureOrder manManufactureOrder, List<ManManufactureOrderProduct> productList) {
        if (CollectionUtil.isNotEmpty(productList)) {
            //最大行号
            List<Long> itemNums = productList.stream().filter(o -> o.getItemNum() != null).map(ManManufactureOrderProduct::getItemNum).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(itemNums)) {
                Long maxItemNum = itemNums.stream().max(Comparator.comparingLong(Long::longValue)).get();
                manManufactureOrder.setProductMaxItemNum(maxItemNum);
            }
            //新增
            List<ManManufactureOrderProduct> addList = productList.stream().filter(o -> o.getManufactureOrderProductSid() == null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(addList)) {
                addManManufactureOrderProduct(manManufactureOrder, addList);
            }
            //编辑
            List<ManManufactureOrderProduct> editList = productList.stream().filter(o -> o.getManufactureOrderProductSid() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(editList)) {
                editList.forEach(o -> {
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                    manManufactureOrderProductMapper.updateAllById(o);
                });
            }
            //原有数据
            List<ManManufactureOrderProduct> itemList = manManufactureOrderProductMapper.selectList(new QueryWrapper<ManManufactureOrderProduct>().lambda()
                    .eq(ManManufactureOrderProduct::getManufactureOrderSid, manManufactureOrder.getManufactureOrderSid()));
            //原有数据ids
            List<Long> originalIds = itemList.stream().map(ManManufactureOrderProduct::getManufactureOrderProductSid).collect(Collectors.toList());
            //现有数据ids
            List<Long> currentIds = productList.stream().map(ManManufactureOrderProduct::getManufactureOrderProductSid).collect(Collectors.toList());
            //清空删除的数据
            List<Long> result = originalIds.stream().filter(id -> !currentIds.contains(id)).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(result)) {
                manManufactureOrderProductMapper.deleteBatchIds(result);
            }
        } else {
            deleteManManufactureOrderProduct(manManufactureOrder);
        }
    }

    /**
     * 生产订单-工序明细
     */
    private void operateProcess(ManManufactureOrder manManufactureOrder, List<ManManufactureOrderProcess> processList) {
        if (CollectionUtil.isNotEmpty(processList)) {
            // 工序，同样的“工厂(工序)”不能存在重复的明细行
            processExamine(processList);
            //最大行号
            List<Long> itemNums = processList.stream().filter(o -> o.getItemNum() != null).map(ManManufactureOrderProcess::getItemNum).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(itemNums)) {
                Long maxItemNum = itemNums.stream().max(Comparator.comparingLong(Long::longValue)).get();
                manManufactureOrder.setProcessMaxItemNum(maxItemNum);
            }
            //新增
            List<ManManufactureOrderProcess> addList = processList.stream().filter(o -> o.getManufactureOrderProcessSid() == null).collect(Collectors.toList());
            //编辑
            List<ManManufactureOrderProcess> editList = processList.stream().filter(o -> o.getManufactureOrderProcessSid() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(addList)) {
                addManManufactureOrderProcess(manManufactureOrder, addList);
            }
            if (CollectionUtil.isNotEmpty(editList)) {
                ConManufactureDepartment department = null;
                for (ManManufactureOrderProcess o : editList) {
                    o.setQuantityReferProcessCode(getQuantityReferProcessCode(o));
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                    setDepartmentCode(department, o);
                    setDirector(o);
                    manManufactureOrderProcessMapper.updateAllById(o);
                }
            }
            //原有数据
            List<ManManufactureOrderProcess> itemList = manManufactureOrderProcessMapper.selectList(new QueryWrapper<ManManufactureOrderProcess>().lambda()
                    .eq(ManManufactureOrderProcess::getManufactureOrderSid, manManufactureOrder.getManufactureOrderSid()));
            //原有数据ids
            List<Long> originalIds = itemList.stream().map(ManManufactureOrderProcess::getManufactureOrderProcessSid).collect(Collectors.toList());
            //现有数据ids
            List<Long> currentIds = processList.stream().map(ManManufactureOrderProcess::getManufactureOrderProcessSid).collect(Collectors.toList());
            //清空删除的数据
            List<Long> result = originalIds.stream().filter(id -> !currentIds.contains(id)).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(result)) {
                manManufactureOrderProcessMapper.deleteBatchIds(result);
            }
        } else {
            deleteManManufactureOrderProcess(manManufactureOrder);
        }
    }

    /**
     * 生产订单-关注事项对象
     */
    private void operateConcernTask(ManManufactureOrder manManufactureOrder, List<ManManufactureOrderConcernTask> concernTaskList) {
        if (CollectionUtil.isNotEmpty(concernTaskList)) {
            //新增
            List<ManManufactureOrderConcernTask> addList = concernTaskList.stream().filter(o -> o.getManufactureOrderConcernTaskSid() == null).collect(Collectors.toList());
            //编辑
            List<ManManufactureOrderConcernTask> editList = concernTaskList.stream().filter(o -> o.getManufactureOrderConcernTaskSid() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(addList)) {
                addManManufactureOrderConcernTask(manManufactureOrder, addList);
            }
            if (CollectionUtil.isNotEmpty(editList)) {
                editList.forEach(o -> {
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                    manManufactureOrderConcernTaskMapper.updateAllById(o);
                });
            }
            //原有数据
            List<ManManufactureOrderConcernTask> itemList = manManufactureOrderConcernTaskMapper.selectList(new QueryWrapper<ManManufactureOrderConcernTask>().lambda()
                    .eq(ManManufactureOrderConcernTask::getManufactureOrderSid, manManufactureOrder.getManufactureOrderSid()));
            //原有数据ids
            List<Long> originalIds = itemList.stream().map(ManManufactureOrderConcernTask::getManufactureOrderConcernTaskSid).collect(Collectors.toList());
            //现有数据ids
            List<Long> currentIds = concernTaskList.stream().map(ManManufactureOrderConcernTask::getManufactureOrderConcernTaskSid).collect(Collectors.toList());
            //清空删除的数据
            List<Long> result = originalIds.stream().filter(id -> !currentIds.contains(id)).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(result)) {
                manManufactureOrderConcernTaskMapper.deleteBatchIds(result);
            }
        } else {
            deleteManManufactureOrderConcernTask(manManufactureOrder);
        }
    }

    /**
     * 生产订单-附件
     */
    private void operateAttach(ManManufactureOrder manManufactureOrder, List<ManManufactureOrderAttach> attachList) {
        if (CollectionUtil.isNotEmpty(attachList)) {
            //新增
            List<ManManufactureOrderAttach> addList = attachList.stream().filter(o -> o.getAttachmentSid() == null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(addList)) {
                addManManufactureOrderAttach(manManufactureOrder, addList);
            }
            //编辑
            List<ManManufactureOrderAttach> editList = attachList.stream().filter(o -> o.getAttachmentSid() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(editList)) {
                editList.forEach(o -> {
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                    manManufactureOrderAttachMapper.updateAllById(o);
                });
            }
            //原有数据
            List<ManManufactureOrderAttach> itemList = manManufactureOrderAttachMapper.selectList(new QueryWrapper<ManManufactureOrderAttach>().lambda()
                    .eq(ManManufactureOrderAttach::getManufactureOrderSid, manManufactureOrder.getManufactureOrderSid()));
            //原有数据ids
            List<Long> originalIds = itemList.stream().map(ManManufactureOrderAttach::getAttachmentSid).collect(Collectors.toList());
            //现有数据ids
            List<Long> currentIds = attachList.stream().map(ManManufactureOrderAttach::getAttachmentSid).collect(Collectors.toList());
            //清空删除的数据
            List<Long> result = originalIds.stream().filter(id -> !currentIds.contains(id)).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(result)) {
                manManufactureOrderAttachMapper.deleteBatchIds(result);
            }
        } else {
            deleteAttach(manManufactureOrder);
        }
    }

    /**
     * 设置合同交期
     */
    public void setContractDate(ManManufactureOrder manManufactureOrder) {
        manManufactureOrder.setContractDate(null);
        if (CollectionUtil.isNotEmpty(manManufactureOrder.getManManufactureOrderProductList())) {
            //
            List<ManManufactureOrderProduct> contractDateList = manManufactureOrder.getManManufactureOrderProductList().
                    stream().filter(e -> e.getContractDate() != null).collect(toList());
            if (CollectionUtil.isNotEmpty(contractDateList)) {
                contractDateList = contractDateList.stream().sorted(Comparator.comparing(ManManufactureOrderProduct::getContractDate).reversed()).collect(Collectors.toList());
                manManufactureOrder.setContractDate(contractDateList.get(0).getContractDate());
            }
        }
    }

    /**
     * 变更生产订单
     *
     * @param manManufactureOrder 生产订单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeManManufactureOrder(ManManufactureOrder manManufactureOrder) {
        setConfirmInfo(manManufactureOrder);
        manManufactureOrder.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
        ManManufactureOrder response = manManufactureOrderMapper.selectManManufactureOrderById(manManufactureOrder.getManufactureOrderSid());
        if (ConstantsEms.COMPLETE_STATUS_YWG.equals(response.getHandleStatus())) {
            throw new BaseException("生产订单" + response.getManufactureOrderCode() + "已完工，无法变更！");
        }
        if (manManufactureOrder.getProcessRouteSid() != null && !manManufactureOrder.getProcessRouteSid().equals(response.getProcessRouteSid())) {
            setProcessRouteCode(manManufactureOrder);
        }

        setContractDate(manManufactureOrder);

        int row = manManufactureOrderMapper.updateAllById(manManufactureOrder);
        //生产订单-产品明细对象
        List<ManManufactureOrderProduct> productList = manManufactureOrder.getManManufactureOrderProductList();
        if (CollectionUtil.isNotEmpty(productList)) {
            productList.forEach(o -> {
                //库存明细
                List<InvInventoryDocumentItem> itemList =
                        invInventoryDocumentItemMapper.selectList(new QueryWrapper<InvInventoryDocumentItem>().lambda()
                                .eq(InvInventoryDocumentItem::getReferDocumentItemSid, o.getManufactureOrderProductSid()));
                if (CollectionUtil.isNotEmpty(itemList)) {
                    BigDecimal quantitySum = itemList.stream().map(InvInventoryDocumentItem::getQuantity).reduce(BigDecimal.ZERO, BigDecimal::add);
                    if (quantitySum != null && o.getQuantity().compareTo(quantitySum) == -1) {
                        throw new BaseException("存在明细行的本次排产量小于累计已入库量，不允许确认！");
                    }
                }
                o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
            });
            operateProduct(manManufactureOrder, productList);
        }
        //生产订单-工序对象
        List<ManManufactureOrderProcess> processList = manManufactureOrder.getManManufactureOrderProcessList();
        setIsProcessVerify(processList);
        isProcessVerify(processList);
        operateProcess(manManufactureOrder, processList);
        //生产订单-工序对象
        List<ManManufactureOrderConcernTask> concernTaskList = manManufactureOrder.getConcernTaskList();
        operateConcernTask(manManufactureOrder, concernTaskList);
        //生产订单-附件对象
        List<ManManufactureOrderAttach> attachList = manManufactureOrder.getAttachmentList();
        operateAttach(manManufactureOrder, attachList);
        SysBusinessBcst sysBusinessBcst = new SysBusinessBcst();
        sysBusinessBcst.setTitle("生产订单" + manManufactureOrder.getManufactureOrderCode() + "已更新")
                .setDocumentSid(manManufactureOrder.getManufactureOrderSid())
                .setDocumentCode(manManufactureOrder.getManufactureOrderCode())
                .setNoticeDate(new Date()).setUserId(ApiThreadLocalUtil.get().getUserid());
        sysBusinessBcstMapper.insert(sysBusinessBcst);
        //插入日志
        List<OperMsg> msgList = BeanUtils.eq(response, manManufactureOrder);
        MongodbDeal.update(manManufactureOrder.getManufactureOrderSid(), response.getHandleStatus(), manManufactureOrder.getHandleStatus(), msgList, TITLE, null);
        return row;
    }

    /**
     * 批量删除生产订单
     *
     * @param clientIds 需要删除的生产订单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteManManufactureOrderByIds(List<Long> manufactureOrderSids) {
        List<String> handleStatusList = new ArrayList<>();
        handleStatusList.add(ConstantsEms.SAVA_STATUS);
        handleStatusList.add(ConstantsEms.BACK_STATUS);
        Integer count = manManufactureOrderMapper.selectCount(new QueryWrapper<ManManufactureOrder>().lambda()
                .in(ManManufactureOrder::getHandleStatus, handleStatusList)
                .in(ManManufactureOrder::getManufactureOrderSid, manufactureOrderSids));
        if (manufactureOrderSids.size() != count) {
            throw new BaseException(ConstantsEms.DELETE_PROMPT_STATEMENT_APPROVE);
        }
        //删除生产订单
        manManufactureOrderMapper.deleteBatchIds(manufactureOrderSids);
        //删除生产订单-产品明细对象
        manManufactureOrderProductMapper.delete(new UpdateWrapper<ManManufactureOrderProduct>().lambda()
                .in(ManManufactureOrderProduct::getManufactureOrderSid, manufactureOrderSids));
        //删除生产订单-工序对象
        manManufactureOrderProcessMapper.delete(new UpdateWrapper<ManManufactureOrderProcess>().lambda()
                .in(ManManufactureOrderProcess::getManufactureOrderSid, manufactureOrderSids));
        manManufactureOrderConcernTaskMapper.delete(new UpdateWrapper<ManManufactureOrderConcernTask>().lambda()
                .in(ManManufactureOrderConcernTask::getManufactureOrderSid, manufactureOrderSids));
        //删除生产订单-组件对象
        manManufactureOrderComponentMapper.delete(new UpdateWrapper<ManManufactureOrderComponent>().lambda()
                .in(ManManufactureOrderComponent::getManufactureOrderSid, manufactureOrderSids));
        //删除生产订单-附件对象
        manManufactureOrderAttachMapper.delete(new UpdateWrapper<ManManufactureOrderAttach>().lambda()
                .in(ManManufactureOrderAttach::getManufactureOrderSid, manufactureOrderSids));
        ManManufactureOrder manManufactureOrder = new ManManufactureOrder();
        sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                .eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB)
                .eq(SysTodoTask::getTableName, ConstantsTable.TABLE_MANUFACTURE_ORDER)
                .in(SysTodoTask::getDocumentSid, manufactureOrderSids));
        return manufactureOrderSids.size();
    }

    @Autowired
    private SysDefaultSettingClientMapper defaultSettingClientMapper;

    /**
     * 获取租户默认设置
     */
    @Override
    public SysDefaultSettingClient getClientSetting() {
        return defaultSettingClientMapper.selectOne(new QueryWrapper<SysDefaultSettingClient>()
                .lambda().eq(SysDefaultSettingClient::getClientId, ApiThreadLocalUtil.get().getSysUser().getClientId()));
    }

    /**
     * 确认按钮前的校验
     */
    @Override
    public EmsResultEntity verifyCheck(ManManufactureOrder manManufactureOrder) {
        SysDefaultSettingClient client = getClientSetting();
        //错误信息
        List<CommonErrMsgResponse> errMsgList = new ArrayList<>();
        List<CommonErrMsgResponse> warMsgList = new ArrayList<>();
        CommonErrMsgResponse errMsg = null;
        CommonErrMsgResponse warMsg = null;
        // 商品明细的合同交期 最小的
        String contractDate = null;
        for (Long LongSid : manManufactureOrder.getManufactureOrderSidList()) {
            // 控制循环重复判断
            int a = 1, b = 1, i = 1;
            ManManufactureOrder order = manManufactureOrderMapper.selectById(LongSid);
            List<ManManufactureOrderProduct> itemList =
                    manManufactureOrderProductMapper.selectManManufactureOrderProductList(new ManManufactureOrderProduct().setManufactureOrderSid(LongSid));
            if (CollectionUtil.isEmpty(itemList)) {
                errMsg = new CommonErrMsgResponse();
                errMsg.setMsg(order.getManufactureOrderCode() + "商品明细行为空，无法确认！");
                errMsgList.add(errMsg);
            } else {
                for (ManManufactureOrderProduct product : itemList) {
                    if (product.getQuantity() == null && i == 1) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setMsg(order.getManufactureOrderCode() + "存在计划产量未填写的商品明细行，无法确认！");
                        errMsgList.add(errMsg);
                        i += 1;
                    }
                    // 如“商品明细”的“合同交期”不为空，则判断“基本信息”页签的“计划完工日期”是否大于“商品明细”中最小的“合同交期”，如是，提示警告：计划完工日期晚于合同交期。
                    if (contractDate != null) {
                        if (product.getContractDate() != null && product.getContractDate().compareTo(contractDate) > 0) {
                            contractDate = product.getContractDate();
                        }
                    } else {
                        contractDate = product.getContractDate();
                    }
                }

                // 如“商品明细”的“合同交期”不为空，则判断“基本信息”页签的“计划完工日期”是否大于“商品明细”中最小的“合同交期”，如是，提示警告：计划完工日期晚于合同交期。
                if (contractDate != null && order.getPlanEndDate() != null) {
                    LocalDate localDate = order.getPlanEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    String planEndDate = localDate.format(dateTimeFormatter);
                    if (planEndDate.compareTo(contractDate) > 0) {
                        warMsg = new CommonErrMsgResponse();
                        warMsg.setMsg(order.getManufactureOrderCode() + "的计划完工日期'" + planEndDate + "'晚于合同交期'" + contractDate + "'，是否继续操作？");
                        warMsgList.add(warMsg);
                    }
                }
            }
            List<ManManufactureOrderProcess> processList =
                    manManufactureOrderProcessMapper.selectList(new QueryWrapper<ManManufactureOrderProcess>().lambda()
                            .eq(ManManufactureOrderProcess::getManufactureOrderSid, LongSid));
            if (CollectionUtil.isEmpty(processList)) {
                errMsg = new CommonErrMsgResponse();
                errMsg.setMsg(order.getManufactureOrderCode() + "工序明细行为空，无法确认！");
                errMsgList.add(errMsg);
            } else {
                for (ManManufactureOrderProcess process : processList) {
                    gtDirector(process);
                    if (process.getPlantSid() == null && a == 1) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setMsg(order.getManufactureOrderCode() + "存在工厂未填写的工序明细行，无法确认！");
                        errMsgList.add(errMsg);
                        a += 1;
                    }
                    if (process.getQuantity() == null && b == 1) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setMsg(order.getManufactureOrderCode() + "存在计划产量未填写的工序明细行，无法确认！");
                        errMsgList.add(errMsg);
                        b += 1;
                    }
                }
                Map<String, List<ManManufactureOrderProcess>> map = processList.stream()
                        .collect(Collectors.groupingBy(o -> String.valueOf(o.getIsFirstProcess())));
                List<ManManufactureOrderProcess> temp = map.get("Y");
                if (temp != null) {
                    Map<String, List<ManManufactureOrderProcess>> map1 = temp.stream()
                            .collect(Collectors.groupingBy(o -> String.valueOf(o.getProcessSid())));
                    if (map1.size() > 1) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setMsg(order.getManufactureOrderCode() + "“工序总览”页签，“是否第一个工序”的值填写错误，无法确认！");
                        errMsgList.add(errMsg);
                    }
                }
                Map<String, List<ManManufactureOrderProcess>> map2 = processList.stream()
                        .collect(Collectors.groupingBy(o -> String.valueOf(o.getIsProduceComplete())));
                List<ManManufactureOrderProcess> temp2 = map2.get("Y");
                if (temp2 != null) {
                    Map<String, List<ManManufactureOrderProcess>> map3 = temp2.stream()
                            .collect(Collectors.groupingBy(o -> String.valueOf(o.getProcessSid())));
                    if (map3.size() > 1) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setMsg(order.getManufactureOrderCode() + "”工序总览”页签，“是否标志成品完工的工序”的值填写错误，无法确认！");
                        errMsgList.add(errMsg);
                    }
                }
                Set<ManManufactureOrderProcess> playerSet = new TreeSet<>(Comparator.comparing(o ->
                        (String.valueOf(o.getPlantSid()) + String.valueOf(o.getProcessSid()) + String.valueOf(o.getWorkCenterSid())
                                + String.valueOf(o.getDirectorSid()))));
                playerSet.addAll(processList);
                if (playerSet.size() != processList.size()) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setMsg(order.getManufactureOrderCode() + "”工序总览”页签" + "，同样的“工序，工厂(工序)，班组，负责人”存在重复的明细行，请检查！");
                    errMsgList.add(errMsg);
                }
                Map<String, List<ManManufactureOrderProcess>> mapProcess = processList.stream()
                        .collect(Collectors.groupingBy(o -> String.valueOf(o.getProcessSid())));
                for (String key : mapProcess.keySet()) {
                    ManProcess manProcess = manProcessMapper.selectManProcessById(new Long(key));
                    //同工序总计划产量订单计划产量
                    BigDecimal quantity = mapProcess.get(key).stream().filter(o -> o.getQuantity() != null && o.getQuantity().compareTo(BigDecimal.ZERO) == 1)
                            .map(ManManufactureOrderProcess::getQuantity).reduce(BigDecimal.ZERO, BigDecimal::add);
                    if (quantity != null && quantity.compareTo(order.getQuantity()) != 0) {
                        warMsg = new CommonErrMsgResponse();
                        warMsg.setMsg(order.getManufactureOrderCode() + "的工序" + manProcess.getProcessName() + "，计划量与该订单的计划产量不一致，是否继续操作？");
                        warMsgList.add(warMsg);
                    }
                }
            }
            //
            if (client != null && ConstantsEms.YES.equals(client.getIsRequiredConcernTask())) {
                List<ManManufactureOrderConcernTask> taskList =
                        manManufactureOrderConcernTaskMapper.selectManManufactureOrderConcernTaskList(
                                new ManManufactureOrderConcernTask().setManufactureOrderSid(LongSid));
                if (CollectionUtil.isEmpty(taskList)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setMsg(order.getManufactureOrderCode() + "关注事项明细行为空，无法确认！");
                    errMsgList.add(errMsg);
                }
            }
        }
        if (CollectionUtil.isNotEmpty(errMsgList)) {
            return EmsResultEntity.error(errMsgList);
        }
        if (CollectionUtil.isNotEmpty(warMsgList)) {
            return EmsResultEntity.warning(warMsgList);
        }
        return EmsResultEntity.success();
    }

    /**
     * 确认按钮前的校验
     */
    @Override
    public EmsResultEntity verifyCheckForm(ManManufactureOrder order) {
        SysDefaultSettingClient client = getClientSetting();
        //错误信息
        List<CommonErrMsgResponse> errMsgList = new ArrayList<>();
        List<CommonErrMsgResponse> warMsgList = new ArrayList<>();
        CommonErrMsgResponse errMsg = null;
        CommonErrMsgResponse warMsg = null;
        // 商品明细的合同交期 最小的
        String contractDate = null;
        // 控制循环重复判断
        int a = 1, b = 1, i = 1;
        List<ManManufactureOrderProduct> itemList =
                order.getManManufactureOrderProductList();
        if (CollectionUtil.isEmpty(itemList)) {
            errMsg = new CommonErrMsgResponse();
            errMsg.setMsg(order.getManufactureOrderCode() + "商品明细行为空，无法确认！");
            errMsgList.add(errMsg);
        } else {
            for (ManManufactureOrderProduct product : itemList) {
                if (product.getQuantity() == null && i == 1) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setMsg(order.getManufactureOrderCode() + "存在计划产量未填写的商品明细行，无法确认！");
                    errMsgList.add(errMsg);
                    i += 1;
                }
                // 如“商品明细”的“合同交期”不为空，则判断“基本信息”页签的“计划完工日期”是否大于“商品明细”中最小的“合同交期”，如是，提示警告：计划完工日期晚于合同交期。
                if (contractDate != null) {
                    if (product.getContractDate() != null && product.getContractDate().compareTo(contractDate) > 0) {
                        contractDate = product.getContractDate();
                    }
                } else {
                    contractDate = product.getContractDate();
                }
            }

            // 如“商品明细”的“合同交期”不为空，则判断“基本信息”页签的“计划完工日期”是否大于“商品明细”中最小的“合同交期”，如是，提示警告：计划完工日期晚于合同交期。
            if (contractDate != null && order.getPlanEndDate() != null) {
                LocalDate localDate = order.getPlanEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String planEndDate = localDate.format(dateTimeFormatter);
                if (planEndDate.compareTo(contractDate) > 0) {
                    warMsg = new CommonErrMsgResponse();
                    warMsg.setMsg(order.getManufactureOrderCode() + "的计划完工日期'" + planEndDate + "'晚于合同交期'" + contractDate + "'，是否继续操作？");
                    warMsgList.add(warMsg);
                }
            }
        }
        List<ManManufactureOrderProcess> processList =
                order.getManManufactureOrderProcessList();
        if (CollectionUtil.isEmpty(processList)) {
            errMsg = new CommonErrMsgResponse();
            errMsg.setMsg(order.getManufactureOrderCode() + "工序明细行为空，无法确认！");
            errMsgList.add(errMsg);
        } else {
            for (ManManufactureOrderProcess process : processList) {
                gtDirector(process);
                if (process.getPlantSid() == null && a == 1) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setMsg(order.getManufactureOrderCode() + "存在工厂未填写的工序明细行，无法确认！");
                    errMsgList.add(errMsg);
                    a += 1;
                }
                if (process.getQuantity() == null && b == 1) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setMsg(order.getManufactureOrderCode() + "存在计划产量未填写的工序明细行，无法确认！");
                    errMsgList.add(errMsg);
                    b += 1;
                }
            }
            Map<String, List<ManManufactureOrderProcess>> map = processList.stream()
                    .collect(Collectors.groupingBy(o -> String.valueOf(o.getIsFirstProcess())));
            List<ManManufactureOrderProcess> temp = map.get("Y");
            if (temp != null) {
                Map<String, List<ManManufactureOrderProcess>> map1 = temp.stream()
                        .collect(Collectors.groupingBy(o -> String.valueOf(o.getProcessSid())));
                if (map1.size() > 1) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setMsg(order.getManufactureOrderCode() + "“工序总览”页签，“是否第一个工序”的值填写错误，无法确认！");
                    errMsgList.add(errMsg);
                }
            }
            Map<String, List<ManManufactureOrderProcess>> map2 = processList.stream()
                    .collect(Collectors.groupingBy(o -> String.valueOf(o.getIsProduceComplete())));
            List<ManManufactureOrderProcess> temp2 = map2.get("Y");
            if (temp2 != null) {
                Map<String, List<ManManufactureOrderProcess>> map3 = temp2.stream()
                        .collect(Collectors.groupingBy(o -> String.valueOf(o.getProcessSid())));
                if (map3.size() > 1) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setMsg(order.getManufactureOrderCode() + "”工序总览”页签，“是否标志成品完工的工序”的值填写错误，无法确认！");
                    errMsgList.add(errMsg);
                }
            }
            Set<ManManufactureOrderProcess> playerSet = new TreeSet<>(Comparator.comparing(o ->
                    (String.valueOf(o.getPlantSid()) + String.valueOf(o.getProcessSid()) + String.valueOf(o.getWorkCenterSid())
                            + String.valueOf(o.getDirectorSid()))));
            playerSet.addAll(processList);
            if (playerSet.size() != processList.size()) {
                errMsg = new CommonErrMsgResponse();
                errMsg.setMsg(order.getManufactureOrderCode() + "”工序总览”页签" + "，同样的“工序，工厂(工序)，班组，负责人”存在重复的明细行，请检查！");
                errMsgList.add(errMsg);
            }
            Map<String, List<ManManufactureOrderProcess>> mapProcess = processList.stream()
                    .collect(Collectors.groupingBy(o -> String.valueOf(o.getProcessSid())));
            for (String key : mapProcess.keySet()) {
                ManProcess manProcess = manProcessMapper.selectManProcessById(new Long(key));
                //同工序总计划产量订单计划产量
                BigDecimal quantity = mapProcess.get(key).stream().filter(o -> o.getQuantity() != null && o.getQuantity().compareTo(BigDecimal.ZERO) == 1)
                        .map(ManManufactureOrderProcess::getQuantity).reduce(BigDecimal.ZERO, BigDecimal::add);
                if (quantity != null && quantity.compareTo(order.getQuantity()) != 0) {
                    warMsg = new CommonErrMsgResponse();
                    warMsg.setMsg(order.getManufactureOrderCode() + "的工序" + manProcess.getProcessName() + "，计划量与该订单的计划产量不一致，是否继续操作？");
                    warMsgList.add(warMsg);
                }
            }
        }
        //
        if (client != null && ConstantsEms.YES.equals(client.getIsRequiredConcernTask())) {
            List<ManManufactureOrderConcernTask> taskList =
                    order.getConcernTaskList();
            if (CollectionUtil.isEmpty(taskList)) {
                errMsg = new CommonErrMsgResponse();
                errMsg.setMsg(order.getManufactureOrderCode() + "关注事项明细行为空，无法确认！");
                errMsgList.add(errMsg);
            }
        }
        if (CollectionUtil.isNotEmpty(errMsgList)) {
            return EmsResultEntity.error(errMsgList);
        }
        if (CollectionUtil.isNotEmpty(warMsgList)) {
            return EmsResultEntity.warning(warMsgList);
        }
        return EmsResultEntity.success();
    }

    /**
     * 批量确认
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int handleStatus(ManManufactureOrder manManufactureOrder) {
        if (manManufactureOrder.getManufactureOrderSidList() == null || manManufactureOrder.getManufactureOrderSidList().length == 0) {
            return 0;
        }
        SysDefaultSettingClient client = getClientSetting();
        for (Long sid : manManufactureOrder.getManufactureOrderSidList()) {
            manManufactureOrder.setManufactureOrderSid(sid);
            List<ManManufactureOrderProduct> itemList = manManufactureOrderProductMapper.selectManManufactureOrderProductList(new ManManufactureOrderProduct()
                    .setManufactureOrderSid(sid));
            if (CollectionUtil.isEmpty(itemList)) {
                throw new BaseException("商品明细行为空，无法确认！");
            }
            if (client != null && ConstantsEms.YES.equals(client.getIsRequiredConcernTask())) {
                List<ManManufactureOrderConcernTask> concernTaskList = manManufactureOrderConcernTaskMapper
                        .selectManManufactureOrderConcernTaskList(new ManManufactureOrderConcernTask()
                                .setManufactureOrderSid(sid));
                if (CollectionUtil.isEmpty(concernTaskList)) {
                    throw new BaseException("关注事项明细行为空，无法确认！");
                }
            }
            // 商品明细行 未填写值的校验
            productVerify(itemList);
            List<ManManufactureOrderProcess> processList =
                    manManufactureOrderProcessMapper.selectList(new QueryWrapper<ManManufactureOrderProcess>().lambda()
                            .eq(ManManufactureOrderProcess::getManufactureOrderSid, sid));
            if (CollectionUtil.isNotEmpty(processList)) {
                // 工序明细行 未填写值的校验
                processVerify(processList);
                setIsProcessVerify(processList);
                // 工序，同样的“工厂(工序)”不能存在重复的明细行
                processList.forEach(i -> {
                    gtDirector(i);
                });
                processExamine(processList);
            }
            ManManufactureOrder o = manManufactureOrderMapper.selectById(manManufactureOrder.getManufactureOrderSid());
            // 设置即将到日天数
            o.setHandleStatus(manManufactureOrder.getHandleStatus());
            if (ConstantsEms.CHECK_STATUS.equals(manManufactureOrder.getHandleStatus())) {
                ConBuTypeManufactureOrder conBuTypeManufactureOrder = conBuTypeManufactureOrderMapper.selectOne(new QueryWrapper<ConBuTypeManufactureOrder>()
                        .lambda().eq(ConBuTypeManufactureOrder::getCode, o.getBusinessType()));
                if (conBuTypeManufactureOrder != null) {
                    o.setToexpireDays(conBuTypeManufactureOrder.getToexpireDays());
                }
                // 确认操作更新完工状态为进行中
                o.setCompleteStatus(ConstantsEms.COMPLETE_STATUS_JXZ);
                o.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getSysUser().getUserName());
            }
            List<ManManufactureOrder> orderList = manManufactureOrderMapper.selectList(new QueryWrapper<ManManufactureOrder>()
                    .lambda().eq(ManManufactureOrder::getMaterialSid, o.getMaterialSid()));
            orderList = orderList.stream()
                    .sorted(Comparator.comparing(
                            (ManManufactureOrder order) -> new BigDecimal(order.getPaichanBatch() == null ? "0" : order.getPaichanBatch()),
                            Comparator.nullsFirst(BigDecimal::compareTo)
                    ).reversed())
                    .collect(Collectors.toList());
            if (orderList.get(0).getPaichanBatch() != null) {
                o.setPaichanBatch(String.valueOf(Long.parseLong(orderList.get(0).getPaichanBatch()) + 1));
            } else {
                o.setPaichanBatch("1");
            }
            //
            o.setManManufactureOrderProductList(itemList);
            setContractDate(o);
            manManufactureOrderMapper.updateAllById(o);
            if (CollectionUtil.isNotEmpty(processList)) {
                processList.forEach(process -> {
                    manManufactureOrderProcessMapper.updateAllById(process);
                });
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbDeal.check(sid, manManufactureOrder.getHandleStatus(), msgList, TITLE, null);
        }
        if (!ConstantsEms.SAVA_STATUS.equals(manManufactureOrder.getHandleStatus())) {
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB)
                    .eq(SysTodoTask::getTableName, ConstantsTable.TABLE_MANUFACTURE_ORDER)
                    .in(SysTodoTask::getDocumentSid, manManufactureOrder.getManufactureOrderSidList()));
        }
        //
        if (ConstantsEms.CHECK_STATUS.equals(manManufactureOrder.getHandleStatus())) {
            // 设置初始计划结束日期
            manManufactureOrderMapper.setInitialPlanEndDate(manManufactureOrder);
        }
        return manManufactureOrder.getManufactureOrderSidList().length;
    }

    /**
     * 提交 前的校验
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public EmsResultEntity verify(Long manufactureOrderSid, String handleStatus) {
        if (ConstantsEms.SAVA_STATUS.equals(handleStatus) || ConstantsEms.BACK_STATUS.equals(handleStatus)) {
            List<ManManufactureOrderProduct> itemList =
                    manManufactureOrderProductMapper.selectList(new QueryWrapper<ManManufactureOrderProduct>().lambda()
                            .eq(ManManufactureOrderProduct::getManufactureOrderSid, manufactureOrderSid));
            if (CollectionUtil.isEmpty(itemList)) {
                throw new BaseException("商品明细行为空，无法提交！");
            }
            // 商品明细行 未填写值的校验
            productVerify(itemList);
            List<ManManufactureOrderProcess> processList =
                    manManufactureOrderProcessMapper.selectList(new QueryWrapper<ManManufactureOrderProcess>().lambda()
                            .eq(ManManufactureOrderProcess::getManufactureOrderSid, manufactureOrderSid));
            if (CollectionUtil.isEmpty(processList)) {
                throw new BaseException("工序明细行为空，无法提交！");
            }
            SysDefaultSettingClient client = getClientSetting();
            if (client != null && ConstantsEms.YES.equals(client.getIsRequiredConcernTask())) {
                List<ManManufactureOrderConcernTask> concernTaskList =
                        manManufactureOrderConcernTaskMapper.selectList(new QueryWrapper<ManManufactureOrderConcernTask>().lambda()
                                .eq(ManManufactureOrderConcernTask::getManufactureOrderSid, manufactureOrderSid));
                if (CollectionUtil.isEmpty(concernTaskList)) {
                    throw new BaseException("关注事项明细行为空，无法提交！");
                }
            }
            // 工序明细行 未填写值的校验
            processVerify(processList);
            // 工序明细行 部分值必须怎么填的校验
            isProcessVerify(processList);
            // 工序，同样的“工厂(工序)”不能存在重复的明细行
            processList.forEach(i -> {
                gtDirector(i);
            });
            processExamine(processList);
        } else {
            throw new BaseException(ConstantsEms.SUBMIT_PROMPT_STATEMENT);
        }
        return EmsResultEntity.success();
    }

    /**
     * 获取BOM明细
     */
    @Override
    public List<TecBomItem> getMaterialInfo(ManManufactureOrder manManufactureOrder) {
        TecBomHead tecBomHead = new TecBomHead();
        tecBomHead.setMaterialSid(manManufactureOrder.getMaterialSid());
        tecBomHead.setSku1Sid(manManufactureOrder.getSku1Sid());
        //BOM
        List<TecBomHead> tecBomHeadList = tecBomHeadMapper.selectTecBomHeadList(tecBomHead);
        if (CollectionUtils.isEmpty(tecBomHeadList)) {
            return null;
        }
        TecBomHead bomHead = tecBomHeadList.get(0);
        TecBomItem tecBomItem = new TecBomItem();
        tecBomItem.setBomSid(bomHead.getBomSid());
        //BOM明细列表
        List<TecBomItem> tecBomItemList = tecBomItemMapper.selectTecBomItemList(tecBomItem);
        if (CollectionUtils.isEmpty(tecBomItemList)) {
            return null;
        }
        //损耗率
        BigDecimal init = BigDecimal.ONE;
        //百分比
        BigDecimal percentage = new BigDecimal("100");
        for (TecBomItem bomItem : tecBomItemList) {
            if (bomItem.getLossRate() != null) {
                init = init.add(bomItem.getLossRate().divide(percentage));
            }
            if (bomItem.getQuantity() != null && bomItem.getQuantity().compareTo(BigDecimal.ZERO) == 1) {
                //需求量含损耗：计划产量*用量*(1+损耗率)
                bomItem.setLossRequireQuantity(manManufactureOrder.getQuantity().multiply(bomItem.getQuantity()).multiply(init));
                //需求量不含损耗：计划产量*用量
                bomItem.setRequireQuantity(manManufactureOrder.getQuantity().multiply(bomItem.getQuantity()));
                //含损耗用量：用量*(1+损耗率)
                bomItem.setLossConfirmQuantity(bomItem.getQuantity().multiply(init));
                bomItem.setMaterialSid(bomItem.getBomMaterialSid());
                bomItem.setSku1Sid(bomItem.getBomMaterialSku1Sid());
                bomItem.setSku2Sid(bomItem.getBomMaterialSku2Sid());
            }
        }
        return tecBomItemList;
    }

    /**
     * 生产订单下拉框列表
     */
    @Override
    public List<ManManufactureOrder> getManufactureOrderList() {
        return manManufactureOrderMapper.getManufactureOrderList();
    }

    /**
     * 作废生产订单
     */
    @Override
    public int cancellationManufactureOrderById(Long manufactureOrderSid) {
        ManManufactureOrder manManufactureOrder = manManufactureOrderMapper.selectManManufactureOrderById(manufactureOrderSid);
        if (!ConstantsEms.CHECK_STATUS.equals(manManufactureOrder.getHandleStatus())) {
            throw new BaseException(ConstantsEms.CONFIRM_CANCELLATION);
        }
        inStoreQuantity(manManufactureOrder);
        BigDecimal inStoreQuantity = manManufactureOrder.getInStoreQuantity();
        if (inStoreQuantity != null && inStoreQuantity.compareTo(BigDecimal.ZERO) == 1) {
            throw new BaseException("所选生产订单已存在入库数据，无法作废！");
        }
        //插入日志
        MongodbUtil.insertUserLog(manufactureOrderSid, BusinessType.CANCEL.getValue(), manManufactureOrder, manManufactureOrder, TITLE);
        manManufactureOrder.setHandleStatus(ConstantsEms.HANDLE_IM);
        manManufactureOrder.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        manManufactureOrder.setUpdateDate(new Date());
        return manManufactureOrderMapper.updateById(manManufactureOrder);
    }

    /**
     * 完工生产订单
     */
    @Override
    public int completionManufactureOrderById(Long manufactureOrderSid) {
        ManManufactureOrder manManufactureOrder = manManufactureOrderMapper.selectManManufactureOrderById(manufactureOrderSid);
        if (!ConstantsEms.CHECK_STATUS.equals(manManufactureOrder.getHandleStatus())) {
            throw new BaseException("所选数据非'已确认'状态，无法进行完工确认操作！");
        }
        manManufactureOrder.setCompleteStatus(ConstantsEms.COMPLETE_STATUS_YWG);
        manManufactureOrder.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        manManufactureOrder.setUpdateDate(new Date());
        manManufactureOrder.setActualEndDate(new Date());
        return manManufactureOrderMapper.updateById(manManufactureOrder);
    }

    /**
     * 工序计划产量校验
     */
    @Override
    public ManManufactureOrderProcess processQuantityVerify(ManManufactureOrder order) {
        ManManufactureOrderProcess process = new ManManufactureOrderProcess();
        if (ConstantsEms.SUBMIT_STATUS.equals(order.getHandleStatus())) {
            List<Long> manufactureOrderSids = order.getManufactureOrderSids();
            if (CollectionUtil.isNotEmpty(manufactureOrderSids)) {
                for (Long manufactureOrderSid : manufactureOrderSids) {
                    List<ManManufactureOrderProcess> processList =
                            manManufactureOrderProcessMapper.selectManManufactureOrderProcessList(new ManManufactureOrderProcess()
                                    .setManufactureOrderSid(manufactureOrderSid));
                    if (CollectionUtil.isNotEmpty(processList)) {
                        ManManufactureOrder manManufactureOrder = manManufactureOrderMapper.selectManManufactureOrderById(manufactureOrderSid);
                        processList(process, manManufactureOrder, processList);
                    }
                }
            } else {
                List<ManManufactureOrderProcess> processList = order.getManManufactureOrderProcessList();
                if (CollectionUtil.isNotEmpty(processList)) {
                    processList(process, order, processList);
                }
            }
        }
        return process;
    }

    private void processList(ManManufactureOrderProcess process, ManManufactureOrder order, List<ManManufactureOrderProcess> processList) {
        List<Long> processSids = processList.stream().map(ManManufactureOrderProcess::getProcessSid).distinct().collect(Collectors.toList());
        processSids.forEach(processSid -> {
            List<ManManufactureOrderProcess> orderList =
                    manManufactureOrderProcessMapper.selectManManufactureOrderProcessList(new ManManufactureOrderProcess()
                            .setProcessSid(processSid).setManufactureOrderSid(order.getManufactureOrderSid()));
            if (CollectionUtil.isNotEmpty(orderList)) {
                ManProcess manProcess = manProcessMapper.selectManProcessById(processSid);
                //同工序总计划产量订单计划产量
                BigDecimal quantity = orderList.stream().filter(o -> o.getQuantity() != null && o.getQuantity().compareTo(BigDecimal.ZERO) == 1)
                        .map(ManManufactureOrderProcess::getQuantity).reduce(BigDecimal.ZERO, BigDecimal::add);
                if (quantity != null && quantity.compareTo(order.getQuantity()) != 0) {
                    process.setMessage("生产订单" + order.getManufactureOrderCode() + "的工序" + manProcess.getProcessName() + "，计划量与该订单的计划产量不一致，是否继续操作？");//
                }
            }
        });
    }

    /**
     * 审批操作 提交/确认
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirm(ManManufactureOrder order) {
        /*  提交时
            3》“工序总览”页签，若同一工序存在多行明细数据，且其中一明细行的“是否第一个工序”的值为“是”，则该工序其它明细行的“是否第一个工序”的值也需要自动保存为“是”
            4》“工序总览”页签，若同一工序存在多行明细数据，且其中一明细行的“是否标志成品完工的工序”的值为“是”，则该工序其它明细行的“是否标志成品完工的工序”的值也需要自动保存为“是”
         */
        if (ConstantsEms.SUBMIT_STATUS.equals(order.getHandleStatus())) {
            List<ManManufactureOrderProcess> processList = manManufactureOrderProcessMapper.selectList(new QueryWrapper<ManManufactureOrderProcess>()
                    .lambda().eq(ManManufactureOrderProcess::getManufactureOrderSid, order.getManufactureOrderSid()));
            if (CollectionUtil.isNotEmpty(processList)) {
                processList.forEach(process -> {
                    manManufactureOrderProcessMapper.updateAllById(process);
                });
            }
        }
        // 设置即将到日天数
        if (ConstantsEms.CHECK_STATUS.equals(order.getHandleStatus())) {
            ManManufactureOrder o = manManufactureOrderMapper.selectById(order.getManufactureOrderSid());
            ConBuTypeManufactureOrder conBuTypeManufactureOrder = conBuTypeManufactureOrderMapper.selectOne(new QueryWrapper<ConBuTypeManufactureOrder>()
                    .lambda().eq(ConBuTypeManufactureOrder::getCode, o.getBusinessType()));
            if (conBuTypeManufactureOrder != null) {
                order.setToexpireDays(conBuTypeManufactureOrder.getToexpireDays());
            }
            // 设置排产批次号  生产订单确认后，获取该生产订单的“商品编码”，然后用“查找到的所有该商品编码的“已确认”状态的生产订单的个数+1”来记录此生产订单的“排产批次号”
            ManManufactureOrder manufactureOrder = manManufactureOrderMapper.selectById(order.getManufactureOrderSid());
            List<ManManufactureOrder> manufactureOrderList = manManufactureOrderMapper.selectList(new QueryWrapper<ManManufactureOrder>().lambda()
                    .eq(ManManufactureOrder::getMaterialSid, manufactureOrder.getMaterialSid()).eq(ManManufactureOrder::getHandleStatus, ConstantsEms.CHECK_STATUS));
            if (CollectionUtil.isNotEmpty(manufactureOrderList)) {
                order.setPaichanBatch(String.valueOf(manufactureOrderList.size() + 1));
            } else {
                order.setPaichanBatch(String.valueOf(1));
            }
        }
        manManufactureOrderMapper.updateById(order);
    }

    /**
     * 设置即将到期提醒天数
     *
     * @param manManufactureOrder
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setToexpireDays(ManManufactureOrder order) {
        if (order.getManufactureOrderSidList().length == 0) {
            throw new BaseException("请选择行！");
        }
        LambdaUpdateWrapper<ManManufactureOrder> updateWrapper = new LambdaUpdateWrapper<>();
        int row = 0;
        //即将到期天数
        updateWrapper.in(ManManufactureOrder::getManufactureOrderSid, order.getManufactureOrderSidList());
        updateWrapper.set(ManManufactureOrder::getToexpireDays, order.getToexpireDays());
        row = manManufactureOrderMapper.update(null, updateWrapper);
        return row;
    }

    /**
     * 设置基本信息/头缸信息/首批信息
     *
     * @param manManufactureOrder
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setDateStatus(ManManufactureOrderSetRequest order) {
        if (order.getManufactureOrderSidList().length == 0) {
            throw new BaseException("请选择行！");
        }
        LambdaUpdateWrapper<ManManufactureOrder> updateWrapper = new LambdaUpdateWrapper<>();
        int row = 0, flag = 0;
        updateWrapper.in(ManManufactureOrder::getManufactureOrderSid, order.getManufactureOrderSidList());
        if ("JB".equals(order.getSetType())) {
            if ("Y".equals(order.getProducePriorityIsUpd())) {
                flag = 1;
                updateWrapper.set(ManManufactureOrder::getProducePriority, order.getProducePriority());
            }
            if ("Y".equals(order.getPlanStartDateIsUpd())) {
                flag = 1;
                updateWrapper.set(ManManufactureOrder::getPlanStartDate, order.getPlanStartDate());
            }
            if ("Y".equals(order.getPlanEndDateIsUpd())) {
                flag = 1;
                updateWrapper.set(ManManufactureOrder::getPlanEndDate, order.getPlanEndDate());
            }
            if ("Y".equals(order.getGenjinrenSidIsUpd())) {
                flag = 1;
                BasStaff staff = basStaffMapper.selectById(order.getGenjinrenSid());
                if (staff != null) {
                    updateWrapper.set(ManManufactureOrder::getGenjinrenCode, staff.getStaffCode());
                }
                updateWrapper.set(ManManufactureOrder::getGenjinrenSid, order.getGenjinrenSid());

            }
        } else if ("TG".equals(order.getSetType())) {
            if ("Y".equals(order.getIsProduceTgIsUpd())) {
                flag = 1;
                updateWrapper.set(ManManufactureOrder::getIsProduceTg, order.getIsProduceTg());
            }
            if ("Y".equals(order.getCompleteStatusTgIsUpd())) {
                flag = 1;
                updateWrapper.set(ManManufactureOrder::getCompleteStatusTg, order.getCompleteStatusTg());
            }
            if ("Y".equals(order.getPlanStartDateTgIsUpd())) {
                flag = 1;
                updateWrapper.set(ManManufactureOrder::getPlanEndDateTg, order.getPlanStartDateTg());
            }
            if ("Y".equals(order.getActualEndDateTgIsUpd())) {
                flag = 1;
                updateWrapper.set(ManManufactureOrder::getActualEndDateTg, order.getActualEndDateTg());
            }
            if ("Y".equals(order.getPlanQuantityTgIsUpd())) {
                flag = 1;
                updateWrapper.set(ManManufactureOrder::getPlanQuantityTg, order.getPlanQuantityTg());
            }
            if ("Y".equals(order.getActualQuantityTgIsUpd())) {
                flag = 1;
                updateWrapper.set(ManManufactureOrder::getActualQuantityTg, order.getActualQuantityTg());
            }
        } else if ("SP".equals(order.getSetType())) {
            if ("Y".equals(order.getIsProduceSpIsUpd())) {
                flag = 1;
                updateWrapper.set(ManManufactureOrder::getIsProduceSp, order.getIsProduceSp());
            }
            if ("Y".equals(order.getCompleteStatusSpIsUpd())) {
                flag = 1;
                updateWrapper.set(ManManufactureOrder::getCompleteStatusSp, order.getCompleteStatusSp());
            }
            if ("Y".equals(order.getPlanStartDateSpIsUpd())) {
                flag = 1;
                updateWrapper.set(ManManufactureOrder::getPlanEndDateSp, order.getPlanStartDateSp());
            }
            if ("Y".equals(order.getActualEndDateSpIsUpd())) {
                flag = 1;
                updateWrapper.set(ManManufactureOrder::getActualEndDateSp, order.getActualEndDateSp());
            }
            if ("Y".equals(order.getPlanQuantitySpIsUpd())) {
                flag = 1;
                updateWrapper.set(ManManufactureOrder::getPlanQuantitySp, order.getPlanQuantitySp());
            }
            if ("Y".equals(order.getActualQuantitySpIsUpd())) {
                flag = 1;
                updateWrapper.set(ManManufactureOrder::getActualQuantitySp, order.getActualQuantitySp());
            }
        } else {
        }
        if (flag == 1) {
            row = manManufactureOrderMapper.update(null, updateWrapper);
        }
        return row;
    }

    /**
     * 设置完工状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setComplateStatus(ManManufactureOrder order) {
        if (order.getManufactureOrderSid() == null) {
            throw new BaseException("请选择行！");
        }
        if (StrUtil.isBlank(order.getCompleteStatus())) {
            throw new BaseException("请选择完工状态！");
        }
        // 旧数据
        ManManufactureOrder manufactureOrder = manManufactureOrderMapper.selectById(order.getManufactureOrderSid());
        // 数据字典
        List<DictData> completeTypeList = sysDictDataService.selectDictData("s_complete_status");
        Map<String, String> completeTypeMaps = completeTypeList.stream().collect(Collectors.toMap(DictData::getDictValue, DictData::getDictLabel, (key1, key2) -> key2));
        //
        LambdaUpdateWrapper<ManManufactureOrder> updateWrapper = new LambdaUpdateWrapper<>();
        int row = 0;
        updateWrapper.eq(ManManufactureOrder::getManufactureOrderSid, order.getManufactureOrderSid());
        updateWrapper.set(ManManufactureOrder::getCompleteStatus, order.getCompleteStatus());
        row = manManufactureOrderMapper.update(null, updateWrapper);
        if (row > 0) {
            String remark = "变更完工状态，原值：";
            if (completeTypeMaps.get(manufactureOrder.getCompleteStatus()) != null) {
                String completeStatus = completeTypeMaps.get(manufactureOrder.getCompleteStatus()) == null ? manufactureOrder.getCompleteStatus()
                        : completeTypeMaps.get(manufactureOrder.getCompleteStatus());
                remark = remark + completeStatus;
            }
            remark = remark + "; 新值：";
            if (completeTypeMaps.get(order.getCompleteStatus()) != null) {
                String completeStatus = completeTypeMaps.get(order.getCompleteStatus()) == null ? order.getCompleteStatus()
                        : completeTypeMaps.get(order.getCompleteStatus());
                remark = remark + completeStatus + ";";
            }
            MongodbUtil.insertUserLog(order.getManufactureOrderSid(), BusinessType.CHANGE.getValue(), null, TITLE, remark);
        }
        return row;
    }

    /**
     * 销售订单进度报表的生产进度报表明细
     */
    @Override
    public List<SaleManufactureOrderProcessFormResponse> getProcessItem(SaleManufactureOrderProcessFormResponse entity) {
        return manManufactureOrderMapper.getProcessItem(entity);
    }

    /**
     * 查询生产进度报表
     *
     * @param
     * @return 生产进度报表
     */
    @Override
    public List<SaleManufactureOrderProcessFormResponse> getProcessForm(SaleManufactureOrderProcessFormResponse entity) {
        return manManufactureOrderMapper.getProcessForm(entity);
    }

    /**
     * 查询班组生产进度报表
     *
     * @param
     * @return 生产进度报表
     */
    @Override
    public ManWorkOrderProgressFormData selectManManufactureOrderWorkProgress(ManManufactureOrder manManufactureOrder) {
        ManWorkOrderProgressFormData response = new ManWorkOrderProgressFormData();
        response.setFormList(new ArrayList<>());
        response.setProcessNameList(new ArrayList<>());
        if (manManufactureOrder.getProcessRouteSid() == null) {
            throw new BaseException("请选择工艺路线");
        }
        // 获取工艺路线下拉框中的工序
        List<ManProcessRouteItem> routeItemList = manProcessRouteItemMapper.selectList(new QueryWrapper<ManProcessRouteItem>().lambda()
                .eq(ManProcessRouteItem::getProcessRouteSid, manManufactureOrder.getProcessRouteSid()).orderByAsc(ManProcessRouteItem::getSerialNum));
        List<Long> processSidList = new ArrayList<>();
        Long[] processSids = null;
        Map<Long, ManWorkOrderProgressFormProcess> routeProcessMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(routeItemList)) {
            List<ManWorkOrderProgressFormProcess> routeProcessNameList = BeanCopyUtils.copyListProperties(routeItemList, ManWorkOrderProgressFormProcess::new);
            response.setProcessNameList(routeProcessNameList);
            processSidList = routeProcessNameList.stream().map(ManWorkOrderProgressFormProcess::getProcessSid).collect(toList());
            processSids = processSidList.toArray(new Long[processSidList.size()]);
            routeProcessMap = routeProcessNameList.stream().collect(Collectors.toMap(ManWorkOrderProgressFormProcess::getProcessSid, Function.identity()));
        }
        // 查询 行数据
        manManufactureOrder.setProcessSidList(processSids);
        List<ManManufactureOrderProcess> list = manManufactureOrderProcessMapper.selectByProcessRouteListGroupByWork(manManufactureOrder);
        if (CollectionUtil.isNotEmpty(list)) {
            List<ManWorkOrderProgressForm> responseList = BeanCopyUtils.copyListProperties(list, ManWorkOrderProgressForm::new);
            Long[] orderSids = responseList.stream().map(ManWorkOrderProgressForm::getManufactureOrderSid).distinct().toArray(Long[]::new);
            // 查询订单和班组所有订单工序明细
            List<ManManufactureOrderProcess> processList = manManufactureOrderProcessMapper.selectByProcessRouteItemListGroupByWork(
                    new ManManufactureOrderProcess().setManufactureOrderSidList(orderSids).setProcessSidList(processSids)
                            .setProcessRouteSid(manManufactureOrder.getProcessRouteSid()).setWorkCenterSidList(manManufactureOrder.getWorkCenterSidList()));
            // 每一行的工序明细
            Map<String, List<ManWorkOrderProgressFormProcess>> processMap = new HashMap<>();
            List<ManWorkOrderProgressFormProcess> processes = new ArrayList<>();
            if (CollectionUtil.isNotEmpty(processList)) {
                processList.forEach(item -> {
                    // 图片视频
                    item.setPicturePathList(ComUtil.strToArr(item.getPicturePath()));
                    item.setVideoPathList(ComUtil.strToArr(item.getVideoPath()));
                });
                List<ManWorkOrderProgressFormProcess> responseProcessList = BeanCopyUtils.copyListProperties(processList, ManWorkOrderProgressFormProcess::new);
                // 每一行的工序明细
                processMap = responseProcessList.stream().collect(Collectors.groupingBy(e -> String.valueOf(e.getManufactureOrderSid()) + "-" + String.valueOf(e.getWorkCenterSid())));
            }
            // 遍历
            if (CollectionUtil.isNotEmpty(processMap)) {
                for (ManWorkOrderProgressForm form : responseList) {
                    processes = processMap.get(String.valueOf(form.getManufactureOrderSid()) + "-" + String.valueOf(form.getWorkCenterSid()));
                    if (processes != null) {
                        // 获取该生产订单下的所有工序
                        List<Long> self = processes.stream().map(ManWorkOrderProgressFormProcess::getProcessSid).collect(toList());
                        // 与总工序名称列表里对比 出 该生产订单没有的工序，并补充进去
                        List<Long> result = processSidList.stream().filter(id -> !self.contains(id)).collect(Collectors.toList());
                        if (CollectionUtil.isNotEmpty(result)) {
                            for (Long sid : result) {
                                processes.add(new ManWorkOrderProgressFormProcess().setProcessName(routeProcessMap.get(sid).getProcessName())
                                        .setProcessSid(routeProcessMap.get(sid).getProcessSid())
                                        .setSerialNumDecimal(routeProcessMap.get(sid).getSerialNum()));
                            }
                        }
                        // 对该生产订单的工序明细排序
                        processes = processes.stream().sorted(Comparator.comparing(ManWorkOrderProgressFormProcess::getSerialNumDecimal,
                                Comparator.nullsLast(BigDecimal::compareTo))).collect(toList());
                        form.setProcessList(processes);
                    } else {
                        processes = new ArrayList<>();
                        for (Long sid : processSidList) {
                            processes.add(new ManWorkOrderProgressFormProcess().setProcessName(routeProcessMap.get(sid).getProcessName())
                                    .setProcessSid(routeProcessMap.get(sid).getProcessSid())
                                    .setSerialNumDecimal(routeProcessMap.get(sid).getSerialNum()));
                        }
                        // 对该生产订单的工序明细排序
                        processes = processes.stream().sorted(Comparator.comparing(ManWorkOrderProgressFormProcess::getSerialNumDecimal,
                                Comparator.nullsLast(BigDecimal::compareTo))).collect(toList());
                        form.setProcessList(processes);
                    }
                }
                response.setFormList(responseList);
            } else {
                response.setFormList(responseList);
            }
        }
        return response;
    }

    /**
     * 查询生产进度报表
     *
     * @param
     * @return 生产进度报表
     */
    @Override
    public ManWorkOrderProgressFormData selectManManufactureOrderProgress(ManManufactureOrder manManufactureOrder) {
        if (manManufactureOrder.getProcessRouteSid() == null) {
            throw new BaseException("请选择工艺路线");
        }
        if (manManufactureOrder.getConcernTaskGroupSid() == null) {
            throw new BaseException("请选择关注事项组");
        }
        // 初始化返回体
        ManWorkOrderProgressFormData response = new ManWorkOrderProgressFormData();
        response.setFormList(new ArrayList<>());
        response.setProcessNameList(new ArrayList<>());
        response.setConcernNameList(new ArrayList<>());
        // 查询生产订单 不过滤关注事项组和工艺路线
        manManufactureOrder.setTotalShicai(ConstantsEms.YES);
        List<ManManufactureOrder> list = manManufactureOrderMapper.selectManManufactureOrderProgressForm(manManufactureOrder);
        if (CollectionUtil.isNotEmpty(list)) {
            // 复制给返回体
            List<ManWorkOrderProgressForm> responseList = BeanCopyUtils.copyListProperties(list, ManWorkOrderProgressForm::new);
            Long[] orderSids = responseList.stream().map(ManWorkOrderProgressForm::getManufactureOrderSid).toArray(Long[]::new);
            // =====================================================================================//
            // 获取 工艺路线 下拉框中所有的 工序
            List<ManProcessRouteItem> routeItemList = manProcessRouteItemMapper.selectList(new QueryWrapper<ManProcessRouteItem>().lambda()
                    .eq(ManProcessRouteItem::getProcessRouteSid, manManufactureOrder.getProcessRouteSid()).orderByAsc(ManProcessRouteItem::getSerialNum));
            List<Long> processSidList = new ArrayList<>();
            Map<Long, ManWorkOrderProgressFormProcess> routeProcessMap = new HashMap<>();
            // 生产订单分组工序明细 键 是 生产订单sid
            Map<String, List<ManWorkOrderProgressFormProcess>> processMap = new HashMap<>();
            // 用来临时存放每一行生产订单对应的工序明细 processMap 的值
            List<ManWorkOrderProgressFormProcess> processes = new ArrayList<>();
            // 如果工艺路线下有工序
            if (CollectionUtil.isNotEmpty(routeItemList)) {
                List<ManWorkOrderProgressFormProcess> routeProcessNameList = BeanCopyUtils.copyListProperties(routeItemList, ManWorkOrderProgressFormProcess::new);
                response.setProcessNameList(routeProcessNameList);
                processSidList = routeProcessNameList.stream().map(ManWorkOrderProgressFormProcess::getProcessSid).collect(toList());
                Long[] processSids = processSidList.toArray(new Long[processSidList.size()]);
                routeProcessMap = routeProcessNameList.stream().collect(Collectors.toMap(ManWorkOrderProgressFormProcess::getProcessSid, Function.identity()));
                // 查询订单下的所有订单工序明细 且 是所选工艺路线里的工序
                List<ManManufactureOrderProcess> processList = manManufactureOrderProcessMapper.selectManManufactureOrderProcessByProcessRouteList(
                        new ManManufactureOrderProcess().setWorkCenterSidList(manManufactureOrder.getWorkCenterSidList())
                                .setProcessRouteSid(manManufactureOrder.getProcessRouteSid()).setProcessSidList(processSids).setManufactureOrderSidList(orderSids));
                if (CollectionUtil.isNotEmpty(processList)) {
                    // 复制
                    List<ManWorkOrderProgressFormProcess> responseProcessList = BeanCopyUtils.copyListProperties(processList, ManWorkOrderProgressFormProcess::new);
                    // 生产订单分组工序明细
                    processMap = responseProcessList.stream().collect(Collectors.groupingBy(e -> String.valueOf(e.getManufactureOrderSid())));
                }
            }
            // =====================================================================================//
            // 获取下拉框关注事项组所有的关注事项明细
            List<ManProduceConcernTaskGroupItem> concernTaskGroupItemList = manProduceConcernTaskGroupItemMapper.selectManProduceConcernTaskGroupItemList(
                    new ManProduceConcernTaskGroupItem().setConcernTaskGroupSid(manManufactureOrder.getConcernTaskGroupSid()));
            List<Long> concernSidList = new ArrayList<>();
            Map<Long, ManWorkOrderProgressFormConcern> concernGroupItemMap = new HashMap<>();
            // 生产订单分组事项明细
            Map<String, List<ManWorkOrderProgressFormConcern>> concernMap = new HashMap<>();
            // 用来临时存放每一行生产订单对应的事项明细
            List<ManWorkOrderProgressFormConcern> concernList = new ArrayList<>();
            // 如果关注事项组里有事项
            if (CollectionUtil.isNotEmpty(concernTaskGroupItemList)) {
                // 复制给返回体
                List<ManWorkOrderProgressFormConcern> responseConcernTaskGroupItemList = BeanCopyUtils.copyListProperties(concernTaskGroupItemList, ManWorkOrderProgressFormConcern::new);
                // 写入放所有事项名称的列表里
                response.setConcernNameList(responseConcernTaskGroupItemList);
                concernGroupItemMap = responseConcernTaskGroupItemList.stream().collect(Collectors.toMap(ManWorkOrderProgressFormConcern::getConcernTaskSid, Function.identity()));
                // 查询出所有包含的事项名称
                concernSidList = concernTaskGroupItemList.stream().map(ManProduceConcernTaskGroupItem::getConcernTaskSid).collect(toList());
                Long[] concernSids = concernSidList.toArray(new Long[concernSidList.size()]);
                // 查询订单下的所有关注事项明细 且 是所选关注事项组里的事项
                List<ManManufactureOrderConcernTask> concernTaskList = manManufactureOrderConcernTaskMapper.selectManManufactureOrderConcernByTaskGroupList(
                        new ManManufactureOrderConcernTask().setConcernTaskSidList(concernSids).setConcernTaskGroupSid(manManufactureOrder.getConcernTaskGroupSid())
                                .setManufactureOrderSidList(orderSids));
                if (CollectionUtil.isNotEmpty(concernTaskList)) {
                    concernTaskList.forEach(item -> {
                        // 图片视频
                        item.setPicturePathList(ComUtil.strToArr(item.getPicturePath()));
                        item.setVideoPathList(ComUtil.strToArr(item.getVideoPath()));
                    });
                    // 复制给返回体
                    List<ManWorkOrderProgressFormConcern> responseConcernList = BeanCopyUtils.copyListProperties(concernTaskList, ManWorkOrderProgressFormConcern::new);
                    // 生产订单分组事项明细
                    concernMap = responseConcernList.stream().collect(Collectors.groupingBy(o -> String.valueOf(o.getManufactureOrderSid())));
                }
            }
            int i = 0, j = 0;
            if (CollectionUtil.isEmpty(processMap)) {
                i = -1;
            }
            if (CollectionUtil.isEmpty(concernGroupItemMap)) {
                j = -1;
            }
            // 循环遍历查询出来的生产订单，写入工序明细和事项明细
            LocalDate localDate = LocalDate.now();
            LocalDateTime date = localDate.atStartOfDay();
            for (ManWorkOrderProgressForm form : responseList) {
                if (form.getPlanEndDate() == null) {
                    form.setLight(LIGHT_NULL); // 空白
                } else {
                    LocalDateTime ldt1 = form.getPlanEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                    if (ldt1.isBefore(date)) {
                        form.setLight(LIGHT_RED); // 红灯
                    } else {
                        Duration duration = Duration.between(ldt1, date); // 注意比较是连时间都比较的
                        long durnDay = duration.toDays();      // 计算天数差
                        long days = form.getToexpireDaysDefalut() == null ? 0 : (long) form.getToexpireDaysDefalut();
                        if (Math.abs(durnDay) > days) {
                            form.setLight(LIGHT_GREEN); // 绿灯
                        } else {
                            form.setLight(LIGHT_YELLOW); // 橙灯
                        }
                    }
                }
                // 工序明细
                if (i == 0) {
                    processes = processMap.get(String.valueOf(form.getManufactureOrderSid()));
                    // 如果有工序明细
                    if (processes != null) {
                        // 获取该生产订单下的所有工序
                        List<Long> self = processes.stream().map(ManWorkOrderProgressFormProcess::getProcessSid).collect(toList());
                        // 与总工序名称列表里对比 出 该生产订单没有的工序，并补充进去
                        List<Long> result = processSidList.stream().filter(id -> !self.contains(id)).collect(Collectors.toList());
                        if (CollectionUtil.isNotEmpty(result)) {
                            for (Long sid : result) {
                                processes.add(new ManWorkOrderProgressFormProcess().setProcessName(routeProcessMap.get(sid).getProcessName())
                                        .setProcessSid(routeProcessMap.get(sid).getProcessSid())
                                        .setSerialNumDecimal(routeProcessMap.get(sid).getSerialNum()));
                            }
                        }
                        // 对该生产订单的工序明细排序
                        processes = processes.stream().sorted(Comparator.comparing(ManWorkOrderProgressFormProcess::getSerialNumDecimal,
                                Comparator.nullsLast(BigDecimal::compareTo))).collect(toList());
                        form.setProcessList(processes);
                    } else {
                        processes = new ArrayList<>();
                        for (Long sid : processSidList) {
                            processes.add(new ManWorkOrderProgressFormProcess().setProcessName(routeProcessMap.get(sid).getProcessName())
                                    .setProcessSid(routeProcessMap.get(sid).getProcessSid())
                                    .setSerialNumDecimal(routeProcessMap.get(sid).getSerialNum()));
                        }
                        // 对该生产订单的工序明细排序
                        processes = processes.stream().sorted(Comparator.comparing(ManWorkOrderProgressFormProcess::getSerialNumDecimal,
                                Comparator.nullsLast(BigDecimal::compareTo))).collect(toList());
                        form.setProcessList(processes);
                    }
                } else {
                    processes = new ArrayList<>();
                    for (Long sid : processSidList) {
                        processes.add(new ManWorkOrderProgressFormProcess().setProcessName(routeProcessMap.get(sid).getProcessName())
                                .setProcessSid(routeProcessMap.get(sid).getProcessSid())
                                .setSerialNumDecimal(routeProcessMap.get(sid).getSerialNum()));
                    }
                    // 对该生产订单的工序明细排序
                    processes = processes.stream().sorted(Comparator.comparing(ManWorkOrderProgressFormProcess::getSerialNumDecimal,
                            Comparator.nullsLast(BigDecimal::compareTo))).collect(toList());
                    form.setProcessList(processes);
                }
                // 事项明细
                if (j == 0) {
                    // 循环遍历查询出来的生产订单，写入事项明细
                    concernList = concernMap.get(String.valueOf(form.getManufactureOrderSid()));
                    // 如果有事项明细
                    if (concernList != null) {
                        // 获取该生产订单下的所有事项
                        List<Long> self = concernList.stream().map(ManWorkOrderProgressFormConcern::getConcernTaskSid).collect(toList());
                        // 与总事项名称列表里对比 出 该生产订单没有的事项，并补充进去
                        List<Long> result = concernSidList.stream().filter(id -> !self.contains(id)).collect(Collectors.toList());
                        if (CollectionUtil.isNotEmpty(result)) {
                            for (Long sid : result) {
                                concernList.add(new ManWorkOrderProgressFormConcern().setConcernTaskSid(concernGroupItemMap.get(sid).getConcernTaskSid())
                                        .setConcernTaskName(concernGroupItemMap.get(sid).getConcernTaskName())
                                        .setSort(concernGroupItemMap.get(sid).getSort()));
                            }
                        }
                        // 对该生产订单的事项明细排序
                        concernList = concernList.stream().sorted(Comparator.comparing(ManWorkOrderProgressFormConcern::getSort,
                                Comparator.nullsLast(Long::compareTo))).collect(toList());
                        form.setConcernList(concernList);
                    } else {
                        concernList = new ArrayList<>();
                        for (Long sid : concernSidList) {
                            concernList.add(new ManWorkOrderProgressFormConcern().setConcernTaskSid(concernGroupItemMap.get(sid).getConcernTaskSid())
                                    .setConcernTaskName(concernGroupItemMap.get(sid).getConcernTaskName())
                                    .setSort(concernGroupItemMap.get(sid).getSort()));
                        }
                        // 对该生产订单的事项明细排序
                        concernList = concernList.stream().sorted(Comparator.comparing(ManWorkOrderProgressFormConcern::getSort,
                                Comparator.nullsLast(Long::compareTo))).collect(toList());
                        form.setConcernList(concernList);
                    }
                } else {
                    concernList = new ArrayList<>();
                    for (Long sid : concernSidList) {
                        concernList.add(new ManWorkOrderProgressFormConcern().setConcernTaskSid(concernGroupItemMap.get(sid).getConcernTaskSid())
                                .setConcernTaskName(concernGroupItemMap.get(sid).getConcernTaskName())
                                .setSort(concernGroupItemMap.get(sid).getSort()));
                    }
                    // 对该生产订单的事项明细排序
                    concernList = concernList.stream().sorted(Comparator.comparing(ManWorkOrderProgressFormConcern::getSort,
                            Comparator.nullsLast(Long::compareTo))).collect(toList());
                    form.setConcernList(concernList);
                }
            }
            response.setFormList(responseList);
        }
        return response;
    }

}
