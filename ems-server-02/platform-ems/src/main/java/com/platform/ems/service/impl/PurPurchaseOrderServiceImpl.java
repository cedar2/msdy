package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FastByteArrayOutputStream;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import cn.hutool.poi.excel.ExcelReader;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.domain.entity.SysClient;
import com.platform.common.core.domain.entity.SysDefaultSettingClient;
import com.platform.common.core.domain.entity.SysUser;
import com.platform.common.core.domain.model.DictData;
import com.platform.common.exception.CustomException;
import com.platform.common.exception.base.BaseException;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.utils.SecurityUtils;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.utils.file.FileUtils;
import com.platform.ems.config.MinioConfig;
import com.platform.ems.constant.*;
import com.platform.ems.domain.*;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.domain.dto.request.*;
import com.platform.ems.domain.dto.response.*;
import com.platform.ems.domain.dto.response.form.PurPurchaseOrderProcessTracking;
import com.platform.ems.enums.DocCategory;
import com.platform.ems.enums.FormType;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.*;
import com.platform.ems.plug.domain.*;
import com.platform.ems.plug.mapper.*;
import com.platform.ems.service.*;
import com.platform.ems.util.ExcelStyleUtil;
import com.platform.ems.util.JudgeFormat;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.workflow.domain.Submit;
import com.platform.ems.workflow.service.IWorkFlowService;
import com.platform.flowable.domain.vo.FormParameter;
import com.platform.system.domain.SysBusinessBcst;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.*;
import com.platform.system.service.ISysDictDataService;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.platform.ems.constant.ConstantsEms.ADVANCE_SETTLE_MODE_DD;
import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import static java.util.stream.Collectors.toList;

/**
 * 采购订单Service业务层处理
 *
 * @author linhongwei
 * @date 2021-04-08
 */
@Service
@SuppressWarnings("all")
public class PurPurchaseOrderServiceImpl extends ServiceImpl<PurPurchaseOrderMapper, PurPurchaseOrder> implements IPurPurchaseOrderService {
    @Autowired
    private PurPurchaseOrderMapper purPurchaseOrderMapper;
    @Autowired
    private PurPurchaseOrderItemMapper purPurchaseOrderItemMapper;
    @Autowired
    private PurPurchaseOrderAttachmentMapper purPurchaseOrderAttachmentMapper;
    @Autowired
    private BasMaterialAttachmentMapper basMaterialAttachmentMapper;
    @Autowired
    private PurPurchaseOrderDataSourceMapper purchaseOrderDataSourceMapper;
    @Autowired
    private TecBomHeadMapper tecBomHeadMapper;
    @Autowired
    private TecBomItemMapper tecBomItemMapper;
    @Autowired
    private FinRecordAdvancePaymentMapper finRecordAdvancePaymentMapper;
    @Autowired
    private FinRecordAdvancePaymentItemMapper finRecordAdvancePaymentItemMapper;
    @Autowired
    private PurPurchaseContractMapper purPurchaseContractMapper;
    @Autowired
    private ConAccountMethodGroupMapper conAccountMethodGroupMapper;
    @Autowired
    private BasMaterialMapper basMaterialMapper;
    @Autowired
    private InvInventoryDocumentMapper invInventoryDocumentMapper;
    @Autowired
    private InvInventoryDocumentItemMapper invInventoryDocumentItemMapper;
    @Autowired
    private IPurPurchasePriceService priceService;

    @Autowired
    private IBasCompanyService basCompanyService;

    @Autowired
    private IBasVendorService basVendorService;

    @Autowired
    private BasVendorMapper basVendorMapper;
    @Autowired
    private BasSkuMapper basSkuMapper;
    @Autowired
    private BasCompanyMapper basCompanyMapper;
    @Autowired
    private BasProductSeasonMapper basProductSeasonMapper;
    @Autowired
    private ConSaleChannelMapper conSaleChannelMapper;
    @Autowired
    private ConDocTypePurchaseOrderMapper conDocTypePurchaseOrderMapper;
    @Autowired
    private ConBuTypePurchaseOrderMapper conBuTypePurchaseOrderMapper;
    @Autowired
    private ISysDictDataService sysDictDataService;
    @Autowired
    private ConMaterialTypeMapper conMaterialTypeMapper;
    @Autowired
    private ConPurchaseGroupMapper conPurchaseGroupMapper;
    @Autowired
    private ConPurchaseOrgMapper conPurchaseOrgMapper;
    @Autowired
    private BasStorehouseMapper basStorehouseMapper;
    @Autowired
    private BasStorehouseLocationMapper basStorehouseLocationMapper;
    @Autowired
    private ConShipmentModeMapper conShipmentModeMapper;
    @Autowired
    private BasMaterialBarcodeMapper basMaterialBarcodeMapper;
    @Autowired
    private ConTaxRateMapper conTaxRateMapper;
    @Autowired
    private ConMeasureUnitMapper conMeasureUnitMapper;
    @Autowired
    private PurRecordVendorConsignServiceImpl purRecordVendorConsignServiceImpl;
    @Autowired
    private FinBookPaymentEstimationServiceImpl finBookPaymentEstimationServiceImpl;
    @Autowired
    private DelDeliveryNoteMapper delDeliveryNoteMapper;
    @Autowired
    private DelDeliveryNoteItemMapper delDeliveryNoteItemMapper;
    @Autowired
    private IPurPurchaseOrderItemService purPurchaseOrderItemService;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private SysBusinessBcstMapper sysBusinessBcstMapper;
    @Autowired
    private BasMaterialSkuMapper basMaterialSkuMapper;
    @Autowired
    private FinBookPaymentEstimationItemMapper finBookPaymentEstimationMapper;
    @Autowired
    private ConDocBuTypeGroupPoMapper conDocBuTypeGroupPoMapper;
    @Autowired
    private IWorkFlowService workflowService;
    @Autowired
    private PurPurchaseOrderDeliveryPlanMapper deliveryPlanMapper;
    @Autowired
    private SalSalesOrderMapper salSalesOrderMapper;
    @Autowired
    private ManManufactureOrderMapper manManufactureOrderMapper;
    @Autowired
    private IBasSkuGroupService basSkuGroupService;
    @Autowired
    private PurPurchaseOrderMaterialProductMapper purPurchaseOrderMaterialProductMapper;
    @Autowired
    private SalSalesOrderItemMapper salSalesOrderItemMapper;
    @Autowired
    private ConMovementTypeMapper conMovementTypeMapper;
    @Autowired
    private IInvInventoryDocumentService invInventoryDocumentService;
    @Autowired
    private SysClientMapper sysClientMapper;
    @Autowired
    private SysDefaultSettingClientMapper settingClientMapper;

    @Autowired
    private MinioConfig minioConfig;
    @Autowired
    private MinioClient client;

    private static final String TITLE = "采购订单";
    private static final String TYPE_MFL = "MFL";
    private static final String YCX_VENDOR = "YCX";

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
     * 查询采购订单
     *
     * @param purchaseOrderSid 采购订单ID
     * @return 采购订单
     */
    @Override
    public PurPurchaseOrder selectPurPurchaseOrderById(Long purchaseOrderSid) {
        PurPurchaseOrder purPurchaseOrder = purPurchaseOrderMapper.selectPurPurchaseOrderById(purchaseOrderSid);
        if (purPurchaseOrder != null) {
            //采购订单-明细
            PurPurchaseOrderItem purPurchaseOrderItem = new PurPurchaseOrderItem();
            purPurchaseOrderItem.setPurchaseOrderSid(purchaseOrderSid);
            List<PurPurchaseOrderItem> purPurchaseOrderItemList = purPurchaseOrderItemMapper.selectPurPurchaseOrderItemList(purPurchaseOrderItem);
            if (CollectionUtil.isNotEmpty(purPurchaseOrderItemList)) {
                purPurchaseOrderItemList = purPurchaseOrderItemService.handleIndex(purPurchaseOrderItemList);
            }
            String documentType = purPurchaseOrder.getDocumentType();
            boolean isGetPrice = false;
            if (DocCategory.RETURN_BACK_PURCHASE.getCode().equals(documentType) || DocCategory.JI_SHOU_RETURN.getCode().equals(documentType) || DocCategory.JI_SHOU.getCode().equals(documentType)) {
                if (ConstantsEms.YES.equals(purPurchaseOrder.getIsConsignmentSettle()) || ConstantsEms.YES.equals(purPurchaseOrder.getIsReturnGoods())) {
                    isGetPrice = true;
                }
            }
            //明细行排序
            List<PurPurchaseOrderItem> items = purPurchaseOrderItemList;
            if (CollectionUtil.isNotEmpty(items)) {
                for (PurPurchaseOrderItem item : items) {
                    List<PurPurchaseOrderDeliveryPlan> purPurchaseOrderDeliveryPlans = deliveryPlanMapper.selectPurPurchaseOrderDeliveryPlanById(item.getPurchaseOrderItemSid());
                    item.setDeliveryPlanList(purPurchaseOrderDeliveryPlans);
                    List<PurPurchaseOrderMaterialProduct> materialProductlist = purPurchaseOrderMaterialProductMapper.selectPurPurchaseOrderMaterialProductById(item.getPurchaseOrderItemSid());
                    item.setMaterialProductList(materialProductlist);

                    if (ConstantsEms.YES.equals(item.getFreeFlag())) {
                        item.setPurchasePriceTax(BigDecimal.ZERO);
                        item.setPurchasePrice(BigDecimal.ZERO);
                    }

                    // 找明细来源
                    List<PurPurchaseOrderDataSource> orderDataSourceList = purchaseOrderDataSourceMapper.selectPurPurchaseOrderDataSourceList
                            (new PurPurchaseOrderDataSource().setPurchaseOrderSid(purchaseOrderSid));
                    if (CollectionUtil.isNotEmpty(orderDataSourceList)) {
                        for (PurPurchaseOrderItem orderItem : purPurchaseOrderItemList) {
                            orderItem.setOrderDataSourceList(orderDataSourceList.stream().filter(o -> o.getPurchaseOrderItemSid().equals(
                                    orderItem.getPurchaseOrderItemSid())).collect(toList()));
                        }
                    }

                    if (isGetPrice) {
                        BigDecimal zipperPurchase = null;
                        PurPurchasePrice purPurchasePrice = new PurPurchasePrice();
                        BeanCopyUtils.copyProperties(purPurchaseOrder, purPurchasePrice);
                        Long vendorSid = purPurchaseOrder.getVendorSid();
                        BeanCopyUtils.copyProperties(item, purPurchasePrice);
                        purPurchasePrice.setPriceDimension(ConstantsEms.PRICE_K1);
                        purPurchasePrice.setVendorSid(vendorSid);
                        //获取有效期内的采购价
                        PurPurchasePriceItem newPurchase = priceService.getNewPurchase(purPurchasePrice);
                        if (newPurchase.getPurchasePriceTax() != null) {
                            item.setReturnPtin(newPurchase.getPurchasePriceTax());
                        }
                    }
                }
            }
            //采购订单-附件
            PurPurchaseOrderAttachment purPurchaseOrderAttachment = new PurPurchaseOrderAttachment();
            purPurchaseOrderAttachment.setPurchaseOrderSid(purchaseOrderSid);
            List<PurPurchaseOrderAttachment> purPurchaseOrderAttachmentList =
                    purPurchaseOrderAttachmentMapper.selectPurPurchaseOrderAttachmentList(purPurchaseOrderAttachment);
            // 汇总
            PurPurchaseOrder count = getCount(items);
            purPurchaseOrder.setSumMoneyAmount(count.getSumMoneyAmount())
                    .setSumQuantity(count.getSumQuantity())
                    .setSumQuantityCode(count.getSumQuantityCode());
            purPurchaseOrder.setPurPurchaseOrderItemList(items);
            purPurchaseOrder.setAttachmentList(purPurchaseOrderAttachmentList);
            // 明细商品的附件清单
            if (CollectionUtil.isNotEmpty(purPurchaseOrder.getPurPurchaseOrderItemList())) {
                Long[] matetialSids = purPurchaseOrder.getPurPurchaseOrderItemList().stream()
                        .map(PurPurchaseOrderItem::getMaterialSid).toArray(Long[]::new);
                List<BasMaterialAttachment> basMaterialAttachmentList = basMaterialAttachmentMapper
                        .selectBasMaterialAttachmentList(new BasMaterialAttachment().setMaterialSidList(matetialSids));
                if (CollectionUtil.isNotEmpty(basMaterialAttachmentList)) {
                    basMaterialAttachmentList = basMaterialAttachmentList.stream().sorted(
                            Comparator.comparing(BasMaterialAttachment::getMaterialCode, Comparator.nullsLast(String::compareTo)
                                    .thenComparing(Collator.getInstance(Locale.CHINA)))).collect(toList());
                }
                purPurchaseOrder.setAttachmentMaterialList(basMaterialAttachmentList);
            }
            MongodbUtil.find(purPurchaseOrder);
        }
        return purPurchaseOrder;
    }

    /**
     * 明细汇总页签
     */
    @Override
    public PurPurchaseOrder getItemTotalList(PurPurchaseOrder purPurchaseOrder, List<PurPurchaseOrderItem> items) {
        //明细页签汇总
        List<PurPurchaseOrderItem> isNUllGroup = items.stream().filter(li -> li.getSku2GroupSid() == null).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(isNUllGroup)) {
            List<Long> Sku2GroupSidList = items.stream().map(m -> m.getSku2GroupSid()).distinct().collect(Collectors.toList());
            //明细汇总
            List<PurPurchaseOrderTotalResponse> itemTotalList = new ArrayList<>();
            List<SalSalesOrderSku2GroupResponse> Sku2GroupList = new ArrayList<>();
            HashMap<Long, List<String>> Sku2GroupHashMap = new HashMap<>();
            //sku数组最大长度
            int max = 0;
            for (Long li : Sku2GroupSidList) {
                BasSkuGroup basSkuGroup = basSkuGroupService.selectBasSkuGroupById(li);
                List<BasSkuGroupItem> itemList = basSkuGroup.getItemList();
                List<String> skuList = itemList.stream().map(m -> m.getSkuName()).collect(Collectors.toList());
                SalSalesOrderSku2GroupResponse sku2 = new SalSalesOrderSku2GroupResponse();
                sku2.setSku2NameList(skuList)
                        .setSku2GroupName(basSkuGroup.getSkuGroupName());
                Sku2GroupList.add(sku2);
                Sku2GroupHashMap.put(li, skuList);
                int size = skuList.size();
                max = max > size ? max : size;
            }
            int maxSize = max;
            //按包含的sku个数降序
            Sku2GroupList = Sku2GroupList.stream().sorted(Comparator.comparing(li -> li.getSku2NameList().size())).collect(Collectors.toList());
            Sku2GroupList.forEach(li -> {
                int size = li.getSku2NameList().size();
                for (int i = 0; i < maxSize - size; i++) {
                    li.getSku2NameList().add(null);
                }
            });
            //分组按商品编码+颜色+销售价(含税)+合同交期
            Map<String, List<PurPurchaseOrderItem>> listMap = items.stream().collect(Collectors.groupingBy(v -> v.getMaterialCode() + "_" + v.getPurchasePriceTax() + "_" + v.getSku1Sid() + "_" + v.getContractDate()));
            listMap.keySet().stream().forEach(h -> {
                List<PurPurchaseOrderItem> PurPurchaseOrderItems = listMap.get(h);
                Long sku2GroupSid = PurPurchaseOrderItems.get(0).getSku2GroupSid();
                PurPurchaseOrderItem orderItem = PurPurchaseOrderItems.get(0);
                //尺码组
                ArrayList<SalSalesOrderTotalSku2Response> sku2QuantityList = new ArrayList<>();
                //获取对应的sku2组的名称-且统一取最长的尺码长度
                List<String> sku2NameList = Sku2GroupHashMap.get(sku2GroupSid);
                // 物料商品code
                String materialCode = PurPurchaseOrderItems.get(0).getMaterialCode();
                for (int i = 0; i < maxSize; i++) {
                    int size = sku2NameList.size();
                    SalSalesOrderTotalSku2Response sku2Quantity = new SalSalesOrderTotalSku2Response();
                    if (i + 1 <= size) {
                        sku2Quantity.setMaterialCode(materialCode);
                        sku2Quantity.setSku2Name(sku2NameList.get(i));
                    }
                    sku2QuantityList.add(sku2Quantity);
                }
                //再次分组按尺码
                Map<String, List<PurPurchaseOrderItem>> itemTempsku2List = PurPurchaseOrderItems.stream().collect(Collectors.groupingBy(v -> v.getMaterialCode() + "_" + v.getSku2Name()));
                itemTempsku2List.keySet().stream().forEach(li -> {
                    List<PurPurchaseOrderItem> tems = itemTempsku2List.get(li);
                    BigDecimal quantity = tems.stream().map(m -> m.getQuantity()).reduce(BigDecimal.ZERO, BigDecimal::add);
                    sku2QuantityList.forEach(n -> {
                        //匹配尺码对应的 数量
                        if (n.getSku2Name() != null) {
                            if ((n.getMaterialCode() + "_" + n.getSku2Name()).equals(li)) {
                                n.setSku2Quantity(quantity);
                            }
                        }
                    });
                });
                //计算数量小计 总金额
                BigDecimal sum = PurPurchaseOrderItems.stream().map(li -> li.getQuantity()).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal sumCu = PurPurchaseOrderItems.stream().map(li -> {
                    BigDecimal price = BigDecimal.ZERO;
                    BigDecimal quantity = BigDecimal.ZERO;
                    if (li.getQuantity() != null) {
                        quantity = li.getQuantity();
                    }
                    if (li.getPurchasePriceTax() != null) {
                        price = li.getPurchasePriceTax();
                    }
                    return price.multiply(quantity);
                }).reduce(BigDecimal.ZERO, BigDecimal::add);
                PurPurchaseOrderTotalResponse totalItem = new PurPurchaseOrderTotalResponse();
                BeanCopyUtils.copyProperties(orderItem, totalItem);
                totalItem.setSumMoneyAmount(sumCu != null ? sumCu.divide(BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_UP) : null)
                        .setSumQuantity(sum)
                        .setSku2TotalList(sku2QuantityList);
                itemTotalList.add(totalItem);
            });
            List<PurPurchaseOrderTotalResponse> AllList = new ArrayList<>();
            List<PurPurchaseOrderTotalResponse> NotNullList = itemTotalList.stream().filter(li -> li.getContractDate() != null)
                    .sorted(Comparator.comparing(PurPurchaseOrderTotalResponse::getContractDate, Comparator.nullsLast(Date::compareTo))).collect(Collectors.toList());
            List<PurPurchaseOrderTotalResponse> NullList = itemTotalList.stream().filter(li -> li.getContractDate() == null).collect(Collectors.toList());
            AllList.addAll(NotNullList);
            AllList.addAll(NullList);

            List<PurPurchaseOrderTotalResponse> sortList = AllList.stream().sorted(
                    Comparator.comparing(PurPurchaseOrderTotalResponse::getMaterialCode, Comparator.nullsLast(String::compareTo).thenComparing(Collator.getInstance(Locale.CHINA)))
                            .thenComparing(PurPurchaseOrderTotalResponse::getSort1, Comparator.nullsLast(BigDecimal::compareTo))
                            .thenComparing(PurPurchaseOrderTotalResponse::getSku1Name, Comparator.nullsLast(String::compareTo).thenComparing(Collator.getInstance(Locale.CHINA)))
            ).collect(toList());

            purPurchaseOrder.setItemTotalList(sortList);
            int size = Sku2GroupList.size();
            if (size < 3) {
                for (int i = 0; i < 3 - size; i++) {
                    SalSalesOrderSku2GroupResponse sku2Group = new SalSalesOrderSku2GroupResponse();
                    List<String> sku2NameExList = new ArrayList<>();
                    for (int n = 0; n < maxSize; n++) {
                        sku2NameExList.add(null);
                    }
                    sku2Group.setSku2NameList(sku2NameExList);
                    Sku2GroupList.add(sku2Group);
                }
            }
            purPurchaseOrder.setSku2GroupList(Sku2GroupList);
        }
        return purPurchaseOrder;
    }

    /**
     * 明细汇总页签
     */
    @Override
    public PurPurchaseOrder getItemTotalListWl(PurPurchaseOrder purPurchaseOrder, List<PurPurchaseOrderItem> items) {
        // 1）数据显示逻辑：
        //   1》按照“物料编码+颜色+采购价(含税)”维度，汇总下单数量、下单金额
        //   2》免费的明细行，采购价(含税)，显示为0
        //   3》“数量小计”取值：将同样“物料编码+颜色+采购价(含税)”维度的明细行的下单数量累加得出
        //   4》“金额小计(含税)”取值：数量小计 * 采购价(含税)
        List<PurPurchaseOrderTotalResponse> itemTotalList = new ArrayList<>();
        //分组按商品编码+颜色+销售价(含税)+合同交期
        Map<String, List<PurPurchaseOrderItem>> listMap = items.stream().collect(Collectors.groupingBy(v ->
                v.getMaterialCode() + "_" + v.getPurchasePriceTax() + "_" + v.getSku1Sid()));
        listMap.keySet().stream().forEach(h -> {
            List<PurPurchaseOrderItem> PurPurchaseOrderItems = listMap.get(h);
            PurPurchaseOrderItem orderItem = PurPurchaseOrderItems.get(0);
            // 物料商品code
            String materialCode = PurPurchaseOrderItems.get(0).getMaterialCode();
            //计算数量小计 总金额
            BigDecimal sum = PurPurchaseOrderItems.stream().map(li -> li.getQuantity()).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal sumCu = PurPurchaseOrderItems.stream().map(li -> {
                BigDecimal price = BigDecimal.ZERO;
                BigDecimal quantity = BigDecimal.ZERO;
                if (li.getQuantity() != null) {
                    quantity = li.getQuantity();
                }
                if (li.getPurchasePriceTax() != null) {
                    price = li.getPurchasePriceTax();
                }
                return price.multiply(quantity);
            }).reduce(BigDecimal.ZERO, BigDecimal::add);
            PurPurchaseOrderTotalResponse totalItem = new PurPurchaseOrderTotalResponse();
            BeanCopyUtils.copyProperties(orderItem, totalItem);
            totalItem.setSumMoneyAmount(sumCu != null ? sumCu.divide(BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_UP) : null)
                    .setSumQuantity(sum);
            itemTotalList.add(totalItem);
        });
        List<PurPurchaseOrderTotalResponse> sortList = itemTotalList.stream().sorted(
                Comparator.comparing(PurPurchaseOrderTotalResponse::getMaterialCode, Comparator.nullsLast(String::compareTo).thenComparing(Collator.getInstance(Locale.CHINA)))
                        .thenComparing(PurPurchaseOrderTotalResponse::getSort1, Comparator.nullsLast(BigDecimal::compareTo))
                        .thenComparing(PurPurchaseOrderTotalResponse::getSku1Name, Comparator.nullsLast(String::compareTo).thenComparing(Collator.getInstance(Locale.CHINA)))
        ).collect(toList());

        purPurchaseOrder.setItemTotalList(sortList);
        return purPurchaseOrder;
    }

    /**
     * 外部系统获取采购订单
     *
     * @param purchaseOrderSid 采购订单ID
     * @return 采购订单
     */
    @Override
    public PurPurchaseOrderOutResponse getOutOrder(Long purchaseOrderSid) {
        PurPurchaseOrderOutResponse order = purPurchaseOrderMapper.getOutPurPurchaseOrderById(purchaseOrderSid);
        List<PurPurchaseOrderItemOutResponse> itemList = purPurchaseOrderItemMapper.getOutPurPurchaseOrderItemById(purchaseOrderSid);
        order.setItemList(itemList);
        return order;
    }

    /**
     * 按照“商品/物料编码+SKU1序号+SKU1名称+SKU2序号+SKU2名称”升序排列
     * （SKU1序号、SKU2序号，取对应商品/物料档案的“SKU1”、“SKU2”页签中的“序号”清单列的值）
     */
    @Override
    public List<PurPurchaseOrderItem> newSort(List<PurPurchaseOrderItem> itemList) {
        if (CollectionUtil.isEmpty(itemList)) {
            return itemList;
        }
        List<PurPurchaseOrderItem> itemMat = itemList.stream().sorted(
                Comparator.comparing(PurPurchaseOrderItem::getPurchaseOrderCode, Comparator.nullsLast(String::compareTo).thenComparing(Comparator.comparingLong(Long::parseLong)).reversed())
                        .thenComparing(PurPurchaseOrderItem::getMaterialCode, Comparator.nullsLast(String::compareTo).thenComparing(Collator.getInstance(Locale.CHINA)))
                        .thenComparing(PurPurchaseOrderItem::getSort1, Comparator.nullsLast(BigDecimal::compareTo))
                        .thenComparing(PurPurchaseOrderItem::getSku1Name, Comparator.nullsLast(String::compareTo).thenComparing(Collator.getInstance(Locale.CHINA)))
                        .thenComparing(PurPurchaseOrderItem::getSort2, Comparator.nullsLast(BigDecimal::compareTo))
                        .thenComparing(PurPurchaseOrderItem::getSku2Name, Comparator.nullsLast(String::compareTo).thenComparing(Collator.getInstance(Locale.CHINA)))
        ).collect(toList());
        return itemMat;
    }

    //面辅料状态、客供料状态
    @Override
    public int changeStatus(OrderItemStatusRequest order) {
        int row = 0;
        if (TYPE_MFL.equals(order.getType())) {
            row = purPurchaseOrderItemMapper.update(new PurPurchaseOrderItem(), new UpdateWrapper<PurPurchaseOrderItem>()
                    .lambda()
                    .in(PurPurchaseOrderItem::getPurchaseOrderItemSid, order.getOrderItemSidList())
                    .set(PurPurchaseOrderItem::getYclBeiliaoStatus, order.getYclBeiliaoStatus())
                    .set(PurPurchaseOrderItem::getYclCaigouxiadanStatus, order.getYclCaigouxiadanStatus())
                    .set(PurPurchaseOrderItem::getYclQitaoRemark, order.getYclQitaoRemark())
                    .set(PurPurchaseOrderItem::getYclQitaoStatus, order.getYclQitaoStatus())
                    .set(PurPurchaseOrderItem::getYclXugouStatus, order.getYclXugouStatus())
            );
        } else {
            row = purPurchaseOrderItemMapper.update(new PurPurchaseOrderItem(), new UpdateWrapper<PurPurchaseOrderItem>()
                    .lambda()
                    .in(PurPurchaseOrderItem::getPurchaseOrderItemSid, order.getOrderItemSidList())
                    .set(PurPurchaseOrderItem::getJglGongliaoStatus, order.getJglGongliaoStatus())
            );
        }
        return row;
    }

    /**
     * 拷贝采购订单
     */
    @Override
    public PurPurchaseOrder copy(Long purchaseOrderSid) {
        PurPurchaseOrder purPurchaseOrder = selectPurPurchaseOrderById(purchaseOrderSid);
        purPurchaseOrder.setCreateDate(new Date())
                .setCreatorAccount(ApiThreadLocalUtil.get().getUsername())
                .setHandleStatus(ConstantsEms.SAVA_STATUS)
                .setPurchaseOrderSid(null)
                .setPurchaseOrderCode(null)
                .setUpdaterAccount(null)
                .setInOutStockStatus(null)
                .setClientId(null)
                .setIsReturnGoods(null)
                .setIsConsignmentSettle(null)
                .setIsFinanceBookYfzg(null)
                .setIsFinanceBookDfyf(null)
                .setYutouAnxuType(null)
                .setTrustorAccount(null)
                .setDeliveryType(null)
                .setInventoryControlMode(null)
                .setTrustorAccountName(null)
                .setIsPushOtherSystem(null)
                .setOtherSystemInOutStockOrder(null)
                .setOtherSystemStorehouseCode(null)
                .setPushResultOtherSystem(null)
                .setPushReturnMsgOtherSystem(null)
                .setPushTimeOtherSystem(null)
                .setCreatorAccount(null)
                .setConfirmerAccount(null)
                .setConfirmerAccountName(null)
                .setConfirmDate(null)
                .setCreatorAccountName(null)
                .setUpdateDate(null)
                .setDocumentDate(new Date());
        List<PurPurchaseOrderItem> list = purPurchaseOrder.getPurPurchaseOrderItemList();
        list.forEach(li -> {
            li.setCreateDate(new Date())
                    .setPurchaseOrderItemSid(null)
                    .setPurchasePriceTax(null)
                    .setPurchasePrice(null)
                    .setUnitBase(null)
                    .setUnitBaseName(null)
                    .setUnitPrice(null)
                    .setUnitPriceName(null)
                    .setUnitConversionRate(null)
                    .setTaxRate(null)
                    .setTaxRateName(null)
                    .setInOutStockStatus(null)
                    .setUnitConversionRate(null)
                    .setPurchaseOrderSid(null)
                    .setClientId(null)
                    .setCreateDate(null)
                    .setUpdateDate(null)
                    .setInitialPurchasePriceTax(null)
                    .setInitialPurchasePrice(null)
                    .setInitialContractDate(null)
                    .setNewPurchasePriceTax(null)
                    .setNewPurchasePrice(null)
                    .setNewTaxRate(null)
                    .setInitialTaxRate(null)
                    .setNewQuantity(null)
                    .setNewContractDate(null)
                    .setToexpireDays(null)
                    .setInitialQuantity(null)
                    .setItemNum(null)
                    .setCreatorAccountName(null)
                    .setUpdaterAccount(null)
                    .setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
            li.setDeliveryPlanList(new ArrayList<>())
                    .setMaterialProductList(new ArrayList<>());
        });
        purPurchaseOrder.setPurPurchaseOrderItemList(list);
        purPurchaseOrder.setIsSkipJudge(ConstantsEms.YES);
        getPurchase(purPurchaseOrder);
        purPurchaseOrder.setAttachmentList(new ArrayList<>());
        return purPurchaseOrder;
    }

    /**
     * 行号赋值
     */
    public void setItemNum(List<PurPurchaseOrderItem> list) {
        int size = list.size();
        if (size > 0) {
            for (int i = 1; i <= size; i++) {
                list.get(i - 1).setItemNum(i);
            }
        }
    }

    /**
     * 关闭
     */
    @Override
    public int close(PurPurchaseOrder purPurchaseOrder) {
        Long[] purchaseOrderSids = purPurchaseOrder.getPurchaseOrderSids();
        int row = purPurchaseOrderMapper.update(new PurPurchaseOrder(), new UpdateWrapper<PurPurchaseOrder>().lambda()
                .in(PurPurchaseOrder::getPurchaseOrderSid, purchaseOrderSids)
                .set(PurPurchaseOrder::getHandleStatus, HandleStatus.CLOSED.getCode())
        );
        // 明细状态
        purPurchaseOrderItemMapper.update(new PurPurchaseOrderItem(), new UpdateWrapper<PurPurchaseOrderItem>().lambda()
                .in(PurPurchaseOrderItem::getPurchaseOrderSid, purchaseOrderSids)
                .set(PurPurchaseOrderItem::getItemStatus, ConstantsEms.STATUS_CLOSE_STATUS));
        // 操作日志
        for (Long orderSid : purchaseOrderSids) {
            MongodbUtil.insertUserLog(orderSid, BusinessType.CLOSE.getValue(), null, TITLE);
        }
        return row;
    }

    // 销售订单明细关闭
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int itemClose(PurPurchaseOrderItem request){
        int row = 0;
        Long[] sids = request.getPurchaseOrderItemSidList();
        if (sids == null || sids.length == 0) {
            throw new BaseException("请选择行！");
        }
        List<PurPurchaseOrderItem> itemList = purPurchaseOrderItemMapper.selectOrderItemListBy(new PurPurchaseOrderItem()
                .setHandleStatus(ConstantsEms.CHECK_STATUS)
                .setPurchaseOrderItemSidList(sids));
        if (CollectionUtil.isNotEmpty(itemList) && itemList.size() == sids.length) {
            // 明细状态
            row = purPurchaseOrderItemMapper.update(new PurPurchaseOrderItem(), new UpdateWrapper<PurPurchaseOrderItem>().lambda()
                    .in(PurPurchaseOrderItem::getPurchaseOrderItemSid, sids)
                    .set(PurPurchaseOrderItem::getItemStatus, ConstantsEms.STATUS_CLOSE_STATUS));
        }
        else {
            throw new BaseException("仅已确认状态的订单明细才可以作此操作！");
        }
        return row;
    }

    /**
     * 查询采购订单列表
     *
     * @param purPurchaseOrder 采购订单
     * @return 采购订单
     */
    @Override
    public List<PurPurchaseOrder> selectPurPurchaseOrderList(PurPurchaseOrder purPurchaseOrder) {
        List<PurPurchaseOrder> orderList = purPurchaseOrderMapper.selectPurPurchaseOrderList(purPurchaseOrder);
        orderList.forEach(item -> {
            List<PurPurchaseOrderItem> purPurchaseOrderItems = purPurchaseOrderItemMapper.selectList(new QueryWrapper<PurPurchaseOrderItem>().lambda()
                    .eq(PurPurchaseOrderItem::getPurchaseOrderSid, item.getPurchaseOrderSid())
            );
            if (CollectionUtil.isNotEmpty(purPurchaseOrderItems)) {
                BigDecimal sumQu = purPurchaseOrderItems.stream().map(li -> {
                            if (li.getQuantity() == null) {
                                return BigDecimal.ZERO;
                            } else {
                                return li.getQuantity();
                            }
                        }
                ).reduce(BigDecimal.ZERO, BigDecimal::add);
                item.setSumQuantity(sumQu);
                BigDecimal sumCu = purPurchaseOrderItems.stream().map(li -> {
                    if (ConstantsEms.YES.equals(li.getFreeFlag())) {
                        return BigDecimal.ZERO;
                    } else {
                        BigDecimal price = li.getPurchasePriceTax() != null ? li.getPurchasePriceTax() : BigDecimal.ZERO;
                        BigDecimal qutatil = li.getQuantity() != null ? li.getQuantity() : BigDecimal.ZERO;
                        return price.multiply(qutatil);
                    }
                }).reduce(BigDecimal.ZERO, BigDecimal::add);
                item.setSumMoneyAmount(sumCu.divide(BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_UP));
                HashSet<Long> longs = new HashSet<>();
                purPurchaseOrderItems.forEach(li -> {
                    longs.add(li.getMaterialSid());
                });
                item.setSumQuantityCode(longs.size());
            }
        });
        return orderList;
    }

    /**
     * 采购订单计算值刷新
     */
    @Override
    public PurPurchaseOrder getCount(List<PurPurchaseOrderItem> purPurchaseOrderItems) {
        PurPurchaseOrder purchaseOrder = new PurPurchaseOrder();
        if (CollectionUtil.isNotEmpty(purPurchaseOrderItems)) {
            BigDecimal sumQu = purPurchaseOrderItems.stream().map(li -> {
                        if (li.getQuantity() == null) {
                            return BigDecimal.ZERO;
                        } else {
                            return li.getQuantity();
                        }
                    }
            ).reduce(BigDecimal.ZERO, BigDecimal::add);
            purchaseOrder.setSumQuantity(sumQu);
            BigDecimal sumCu = purPurchaseOrderItems.stream().map(li -> {
                if (ConstantsEms.YES.equals(li.getFreeFlag())) {
                    return BigDecimal.ZERO;
                } else {
                    BigDecimal price = li.getPurchasePriceTax() != null ? li.getPurchasePriceTax() : BigDecimal.ZERO;
                    BigDecimal qutatil = li.getQuantity() != null ? li.getQuantity() : BigDecimal.ZERO;
                    return price.multiply(qutatil);
                }
            }).reduce(BigDecimal.ZERO, BigDecimal::add);
            purchaseOrder.setSumMoneyAmount(sumCu.divide(BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_UP));
            HashSet<Long> longs = new HashSet<>();
            purPurchaseOrderItems.forEach(li -> {
                longs.add(li.getMaterialSid());
            });
            purchaseOrder.setSumQuantityCode(longs.size());
        }
        return purchaseOrder;
    }

    /**
     * 录入采购合同
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setConstract(PurPurchaseOrder order) {
        List<PurPurchaseOrder> purPurchaseOrderList = purPurchaseOrderMapper.selectList(new QueryWrapper<PurPurchaseOrder>().lambda()
                .in(PurPurchaseOrder::getPurchaseOrderSid, order.getPurchaseOrderSids())
        );
        PurPurchaseContract contract = new PurPurchaseContract();
        String mode = ApiThreadLocalUtil.get().getSysUser().getClient().getPurchaseOrderContractEnterMode();
        if (!ConstantsOrder.CONTRACT_ENTER_MODE_SG.equals(mode)) {
            // 查找出 现在选择的 合同
            if (order.getPurchaseContractSid() != null) {
                contract = purPurchaseContractMapper.selectById(order.getPurchaseContractSid());
                if (contract == null) {
                    throw new BaseException("该合同已被删除，请重新查询");
                }
            }
        }
        // 如果现在的 合同 不是 临时合同，则才考虑要去 清待办
        if (!ConstantsOrder.CONTRACT_PURPOSE_LSGD.equals(contract.getContractPurpose())) {
            // 筛选出旧数据 中有 合同 的 订单
            List<PurPurchaseOrder> purPurchaseOrders = purPurchaseOrderList.stream().filter(o -> o.getPurchaseContractSid() != null).collect(toList());
            if (CollectionUtil.isNotEmpty(purPurchaseOrders)) {
                // 拿出有合同的订单的 所有合同号
                List<Long> oldContractSidList = purPurchaseOrders.stream().map(PurPurchaseOrder::getPurchaseContractSid)
                        .distinct().collect(toList());
                // 得到 原订单的原合同 中 是 临时合同 的 合同
                List<PurPurchaseContract> purchaseContractList = purPurchaseContractMapper.selectList(new QueryWrapper<PurPurchaseContract>().lambda()
                        .in(PurPurchaseContract::getPurchaseContractSid, oldContractSidList)
                        .eq(PurPurchaseContract::getContractPurpose, ConstantsOrder.CONTRACT_PURPOSE_LSGD));
                if (CollectionUtil.isNotEmpty(purchaseContractList)) {
                    Map<Long, PurPurchaseContract> map = purchaseContractList.stream().collect(Collectors.toMap(PurPurchaseContract::getPurchaseContractSid, Function.identity()));
                    // 存放需要删除待办的订单sid
                    List<Long> orderSidList = new ArrayList<>();
                    purPurchaseOrders.forEach(item -> {
                        // 如果该订单的合同 存在于 临时合同
                        if (map.get(item.getPurchaseContractSid()) != null) {
                            orderSidList.add(item.getPurchaseOrderSid());
                        }
                    });
                    if (CollectionUtil.isNotEmpty(orderSidList)) {
                        sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
                                .in(SysTodoTask::getDocumentSid, orderSidList).eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB)
                                .eq(SysTodoTask::getTableName, ConstantsTable.TABLE_PURCHASE_ORDER + "-" + ConstantsTable.TABLE_PURCHASE_CONTRACT)
                                .like(SysTodoTask::getTitle, "使用的是过渡合同"));
                    }
                }
            }
        }
        int row = purPurchaseOrderMapper.update(new PurPurchaseOrder(), new UpdateWrapper<PurPurchaseOrder>().lambda()
                .in(PurPurchaseOrder::getPurchaseOrderSid, order.getPurchaseOrderSids())
                .set(PurPurchaseOrder::getPurchaseContractSid, order.getPurchaseContractSid())
                .set(PurPurchaseOrder::getPurchaseContractCode, order.getPurchaseContractCode())
        );
        purPurchaseOrderList.forEach(item -> {
            if ((item.getPurchaseContractCode() != null && !item.getPurchaseContractCode().equals(order.getPurchaseContractCode()))
                    || (item.getPurchaseContractCode() == null && order.getPurchaseContractCode() != null)) {
                String contractCode = item.getPurchaseContractCode() == null ? "" : item.getPurchaseContractCode();
                String newCode = order.getPurchaseContractCode() == null ? "" : order.getPurchaseContractCode();
                String remark = "采购合同号更新，更新前：" + contractCode + "，更新后：" + newCode;
                MongodbUtil.insertUserLog(item.getPurchaseOrderSid(), BusinessType.QITA.getValue(), null, TITLE, remark);
            }
        });
        return row;
    }

    /**
     * 变更合同号
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setConstractCode(PurPurchaseOrder order) {
        int row = 1;
        PurPurchaseOrder purchaseOrder = purPurchaseOrderMapper.selectById(order.getPurchaseOrderSid());
        if (purchaseOrder == null) {
            throw new BaseException("该采购订单不存在");
        }
        if (purchaseOrder.getHandleStatus().equals(ConstantsEms.SAVA_STATUS)) {
            throw new BaseException("保存状态不允许变更合同号操作");
        }
        if (purchaseOrder.getPurchaseContractCode() != null && purchaseOrder.getPurchaseContractCode().equals(order.getPurchaseContractCode())) {
            return row;
        }
        LambdaUpdateWrapper<PurPurchaseOrder> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(PurPurchaseOrder::getPurchaseContractCode, order.getPurchaseContractCode());
        updateWrapper.set(PurPurchaseOrder::getPurchaseContractSid, order.getPurchaseContractSid());
        updateWrapper.eq(PurPurchaseOrder::getPurchaseOrderSid, order.getPurchaseOrderSid());
        row = purPurchaseOrderMapper.update(new PurPurchaseOrder(), updateWrapper);
        String contractCode = purchaseOrder.getPurchaseContractCode() == null ? "" : purchaseOrder.getPurchaseContractCode();
        String newCode = order.getPurchaseContractCode() == null ? "" : order.getPurchaseContractCode();
        String remark = "采购合同号变更，更新前：" + contractCode + "，更新后：" + newCode;
        MongodbUtil.insertUserLog(order.getPurchaseOrderSid(), BusinessType.QITA.getValue(), null, TITLE, remark);
        return row;
    }

    /**
     * 生成二维码
     */
    @Override
    public List<PurPurchaseOrderItem> getQr(List<PurPurchaseOrderItem> list) {
        // 租户图片
        String logoPicture = "";
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
                logoPicture = Base64.getEncoder().encodeToString(fos.toByteArray());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for (PurPurchaseOrderItem li : list) {
            String codeBase = QrCodeUtil.generateAsBase64(li.getBarcode(), QrConfig.create().setWidth(80).setHeight(80).setMargin(0), "png");
            li.setQrCode(codeBase);
            li.setLogoPicturePath(logoPicture);
        }
        return list;
    }

    /**
     * 创建合同
     */
    @Override
    public EmsResultEntity constractAdd(List<PurPurchaseOrder> list, String jump) {
        if (CollectionUtil.isNotEmpty(list)) {
            // 供料方式校验是否一致 以第一笔为准 保留单号最大的订单写入合同
            PurPurchaseOrder order = list.get(0);
            // 合计金额
            BigDecimal sum = list.get(0).getSumMoneyAmount();
            // 订单的sid
            List<String> orderSidList = new ArrayList<>();
            orderSidList.add(list.get(0).getPurchaseOrderSid().toString());
            if (sum == null) {
                sum = BigDecimal.ZERO;
            }
            for (int i = 1; i < list.size(); i++) {
                if (ConstantsEms.NO.equals(jump)) {
                    // 当第一笔是空的，则存在一笔非空就报错
                    if (list.get(0).getRawMaterialMode() == null && list.get(i).getRawMaterialMode() != null) {
                        return EmsResultEntity.warning(null, "存在甲供料方式不一致的采购订单，是否确认创建采购合同？");
                    }
                    // 当第一笔不是空的，则每一笔都跟第一步判断是否一样
                    if (list.get(0).getRawMaterialMode() != null &&
                            !list.get(0).getRawMaterialMode().equals(list.get(i).getRawMaterialMode())) {
                        return EmsResultEntity.warning(null, "存在甲供料方式不一致的采购订单，是否确认创建采购合同？");
                    }
                }
                // 保留单号最大的订单写入合同
                if (list.get(i).getPurchaseOrderCode().compareTo(order.getPurchaseOrderCode()) > 0) {
                    order = list.get(i);
                }
                sum = sum.add(list.get(i).getSumMoneyAmount() == null ? BigDecimal.ZERO : list.get(i).getSumMoneyAmount());
                orderSidList.add(list.get(i).getPurchaseOrderSid().toString());
            }
            PurPurchaseContract contract = new PurPurchaseContract();
            BeanCopyUtils.copyProperties(order, contract);
            contract.setCurrencyAmountTax(sum);
            contract.setPurchaseOrderSidList(orderSidList);
            return EmsResultEntity.success(contract, null, null);
        } else {
            throw new CustomException("请选择行！");
        }
    }

    /**
     * 审批/确认 执行成功后操作
     */
    public void changeValue(PurPurchaseOrder order) {
        List<PurPurchaseOrderItem> purPurchaseOrderItemList = order.getPurPurchaseOrderItemList();
        purPurchaseOrderItemList.forEach(li -> {
            if (li.getNewContractDate() != null) {
                li.setContractDate(li.getNewContractDate());
                li.setNewContractDate(null);
            }
            if (li.getNewPurchasePriceTax() != null) {
                li.setPurchasePriceTax(li.getNewPurchasePriceTax());
                li.setNewPurchasePriceTax(null);
            }
            if (li.getNewPurchasePrice() != null) {
                li.setPurchasePrice(li.getNewPurchasePrice());
                li.setNewPurchasePrice(null);
            }
            if (li.getNewQuantity() != null) {
                li.setQuantity(li.getNewQuantity());
                li.setNewQuantity(null);
            }
            if (li.getNewTaxRate() != null) {
                li.setTaxRate(li.getNewTaxRate());
                li.setNewTaxRate(null);
            }
            if (li.getInitialPurchasePrice() == null) {
                li.setInitialPurchasePrice(li.getPurchasePrice());
            }
            if (li.getInitialPurchasePriceTax() == null) {
                li.setInitialPurchasePriceTax(li.getPurchasePriceTax());
            }
            if (li.getInitialContractDate() == null) {
                li.setInitialContractDate(li.getContractDate());
            }
            if (li.getInitialQuantity() == null) {
                li.setInitialQuantity(li.getQuantity());
            }
            if (li.getInitialTaxRate() == null) {
                li.setInitialTaxRate(li.getTaxRate());
            }
        });
    }

    /**
     * 新建直接点提交/编辑点提交
     *
     * @param purPurchaseOrder 采购订单
     * @param jump             是否忽略并继续 提示校验  N 第一次校验 ， Y 忽略，
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult submit(PurPurchaseOrder order, String jump) {
        int row = 0;
        if (order.getPurchaseOrderSid() == null) {
            // 新建
            row = this.insertPurPurchaseOrder(order);
        } else {
            row = this.updatePurPurchaseOrder(order);
        }
        if (row == 1) {
            List<ConDocBuTypeGroupPo> conf = conDocBuTypeGroupPoMapper.selectList(new QueryWrapper<ConDocBuTypeGroupPo>().lambda()
                    .eq(ConDocBuTypeGroupPo::getDocTypeCode, order.getDocumentType())
                    .eq(ConDocBuTypeGroupPo::getBuTypeCode, order.getBusinessType())
                    .eq(ConDocBuTypeGroupPo::getClientId, ApiThreadLocalUtil.get().getClientId())
                    .orderByDesc(ConDocBuTypeGroupPo::getCreateDate));
            EmsResultEntity verifyResult = null;
            // 提交校验  这里可能有 连续多次提交的校验 ，每次把 校验的结果 返回给前端，前端再更新 到参数中的 orderErrRequest
            List<Long> sidList = new ArrayList<Long>() {{
                add(order.getPurchaseOrderSid());
            }};
            OrderErrRequest request = new OrderErrRequest();
            request.setSidList(sidList);
            if (ConstantsEms.YES.equals(jump)) {
                verifyResult = this.checkProcessList(request);
                if (verifyResult != null && EmsResultEntity.ERROR_TAG.equals(verifyResult.getTag())) {
                    return AjaxResult.success(verifyResult);
                } else {
                    verifyResult = null;
                }
            } else {
                verifyResult = this.checkProcessList(request);
            }
            // 是否 无需审批 配置
            if (CollectionUtil.isEmpty(conf) || ConstantsEms.NO.equals(conf.get(0).getIsNonApproval()) ||
                    conf.get(0).getIsNonApproval() == null) {
                // 校验通过
                if (verifyResult == null || CollectionUtil.isEmpty(verifyResult.getMsgList())) {
                    Submit submit = new Submit();
                    submit.setFormType(FormType.PurchaseOrder.getCode());
                    List<FormParameter> formParameters = new ArrayList<>();
                    FormParameter formParameter = new FormParameter();
                    formParameter.setParentId(String.valueOf(order.getPurchaseOrderSid()));
                    formParameter.setFormId(String.valueOf(order.getPurchaseOrderSid()));
                    formParameter.setFormCode(String.valueOf(order.getPurchaseOrderCode()));
                    formParameters.add(formParameter);
                    submit.setFormParameters(formParameters);
                    submit.setStartUserId(String.valueOf(ApiThreadLocalUtil.get().getUserid()));
                    workflowService.submitByItem(submit);
                    return AjaxResult.success("操作成功", new PurPurchaseOrder().setPurchaseOrderSid(order.getPurchaseOrderSid()));
                } else {
                    // 手动回滚 新建 或者 暂存 的事务
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return AjaxResult.success(verifyResult);
                }
            } else {
                // 校验通过
                if (verifyResult == null || CollectionUtil.isEmpty(verifyResult.getMsgList())) {
                    // 直接确认
                    Long[] sids = new Long[]{order.getPurchaseOrderSid()};
                    PurPurchaseOrder confirmOrder = new PurPurchaseOrder();
                    confirmOrder.setPurchaseOrderSids(sids);
                    confirmOrder.setHandleStatus(ConstantsEms.CHECK_STATUS);
                    this.confirm(confirmOrder);
                    return AjaxResult.success("操作成功", new PurPurchaseOrder().setPurchaseOrderSid(order.getPurchaseOrderSid()));
                } else {
                    // 手动回滚 新建 或者 暂存 的事务
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return AjaxResult.success(verifyResult);
                }
            }
        }
        return AjaxResult.success("操作成功", new PurPurchaseOrder().setPurchaseOrderSid(order.getPurchaseOrderSid()));
    }

    /**
     * 撤回保存
     *
     * @param purchaseOrder 采购订单
     * @return 结果
     */
    @Override
    public int backSaveVerify(PurPurchaseOrder purchaseOrder) {
        int row = 1;
        if (purchaseOrder.getPurchaseOrderSid() == null) {
            throw new BaseException("请选择采购订单");
        }
        PurPurchaseOrder order = purPurchaseOrderMapper.selectById(purchaseOrder.getPurchaseOrderSid());
        if (order != null && ConstantsEms.CHECK_STATUS.equals(order.getHandleStatus())) {
            // 校验
            if (ConstantsEms.DELIEVER_type_JHD.equals(order.getDeliveryType())) {
                List<DelDeliveryNoteItem> documentItemList = delDeliveryNoteItemMapper.selectList(new QueryWrapper<DelDeliveryNoteItem>()
                        .lambda().eq(DelDeliveryNoteItem::getPurchaseOrderSid, purchaseOrder.getPurchaseOrderSid()));
                if (CollectionUtil.isNotEmpty(documentItemList)) {
                    Long[] sids = documentItemList.stream().map(DelDeliveryNoteItem::getDeliveryNoteSid).toArray(Long[]::new);
                    List<DelDeliveryNote> documentList = delDeliveryNoteMapper.selectList(new QueryWrapper<DelDeliveryNote>()
                            .lambda().in(DelDeliveryNote::getDeliveryNoteSid, sids)
                            .ne(DelDeliveryNote::getHandleStatus, HandleStatus.INVALID.getCode()));
                    if (CollectionUtil.isNotEmpty(documentList)) {
                        throw new BaseException("该采购订单已存在交货数据，无法撤回！");
                    }
                }
            } else if (ConstantsEms.DELIEVER_type_DD.equals(order.getDeliveryType())) {
                List<InvInventoryDocumentItem> documentItemList = invInventoryDocumentItemMapper.selectList(new QueryWrapper<InvInventoryDocumentItem>()
                        .lambda().eq(InvInventoryDocumentItem::getReferDocumentSid, purchaseOrder.getPurchaseOrderSid()));
                if (CollectionUtil.isNotEmpty(documentItemList)) {
                    Long[] sids = documentItemList.stream().map(InvInventoryDocumentItem::getInventoryDocumentSid).toArray(Long[]::new);
                    List<InvInventoryDocument> documentList = invInventoryDocumentMapper.selectList(new QueryWrapper<InvInventoryDocument>()
                            .lambda().in(InvInventoryDocument::getInventoryDocumentSid, sids).eq(InvInventoryDocument::getHandleStatus, HandleStatus.POSTING.getCode())
                            .eq(InvInventoryDocument::getDocumentType, ConstantsEms.DOCUMNET_TYPE_ZG));
                    if (CollectionUtil.isNotEmpty(documentList)) {
                        throw new BaseException("该采购订单已存在交货数据，无法撤回！");
                    }
                }
            }
        } else {
            throw new BaseException("已确认的采购订单才可进行此操作！");
        }
        return row;
    }

    /**
     * 撤回保存
     *
     * @param purchaseOrder 采购订单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int backSave(PurPurchaseOrder purchaseOrder) {
        int row = 0;
        if (purchaseOrder.getPurchaseOrderSid() == null) {
            throw new BaseException("请选择采购订单");
        }
        if (StrUtil.isBlank(purchaseOrder.getComment())) {
            throw new BaseException("撤回说明不能为空");
        }
        this.backSaveVerify(purchaseOrder);
        row = purPurchaseOrderMapper.update(null, new UpdateWrapper<PurPurchaseOrder>().lambda()
                .set(PurPurchaseOrder::getHandleStatus, ConstantsEms.SAVA_STATUS)
                .eq(PurPurchaseOrder::getPurchaseOrderSid, purchaseOrder.getPurchaseOrderSid()));
        // 操作日志
        MongodbUtil.insertUserLog(purchaseOrder.getPurchaseOrderSid(), com.platform.common.log.enums.BusinessType.QITA.getValue(), null, TITLE, "撤回说明：" + purchaseOrder.getComment());
        return row;
    }

    /**
     * 维护纸质合同号
     *
     * @param purPurchaseOrder 订单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public EmsResultEntity setPaperContract(PurPurchaseOrder purPurchaseOrder) {
        int row = 0;
        // 修改
        LambdaUpdateWrapper<PurPurchaseOrder> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(PurPurchaseOrder::getPurchaseOrderSid, purPurchaseOrder.getPurchaseOrderSid())
                .set(PurPurchaseOrder::getPaperPurchaseContractCode, purPurchaseOrder.getPaperPurchaseContractCode());
        row = purPurchaseOrderMapper.update(new PurPurchaseOrder(), updateWrapper);
        MongodbUtil.insertUserLog(purPurchaseOrder.getPurchaseOrderSid(), BusinessType.QITA.getValue(), null, TITLE, "维护纸质合同号");
        if (StrUtil.isNotBlank(purPurchaseOrder.getPaperPurchaseContractCode())) {
            List<PurPurchaseOrderAttachment> attachments = purPurchaseOrderAttachmentMapper.selectList(new QueryWrapper<PurPurchaseOrderAttachment>()
                    .lambda().eq(PurPurchaseOrderAttachment::getFileType, ConstantsOrder.PAPER_CONTRACT_XSDDHT_PUR)
                    .eq(PurPurchaseOrderAttachment::getPurchaseOrderSid, purPurchaseOrder.getPurchaseOrderSid()));
            if (CollectionUtil.isEmpty(attachments)) {
                // 弹出提示框 是否上传“销售订单合同(盖章版)”附件
                return EmsResultEntity.warning(row, null, "是否上传“采购订单合同(盖章版)”附件");
            }
        }
        return EmsResultEntity.success(row, "操作成功");
    }

    /**
     * 新增采购订单
     * 需要注意编码重复校验
     *
     * @param purPurchaseOrder 采购订单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPurPurchaseOrder(PurPurchaseOrder o) {
        Long vendorSid = o.getVendorSid();
        String vendorGroup = basVendorMapper.selectById(vendorSid).getVendorGroup();
        if (YCX_VENDOR.equals(vendorGroup)) {
            if (o.getVendorNameRemark() == null) {
                throw new CustomException("供应商名称备注,不能为空");
            }
        }
        if (ConstantsEms.CHECK_STATUS.equals(o.getHandleStatus())) {
            judgeNull(o);
        }
        // 校验明细数量
        if (CollectionUtil.isNotEmpty(o.getPurPurchaseOrderItemList())) {
            o.getPurPurchaseOrderItemList().forEach(item -> {
                if (item.getQuantity() == null || BigDecimal.ZERO.compareTo(item.getQuantity()) >= 0) {
                    throw new CustomException("订单明细的订单量必须大于0");
                }
            });
        }
        //获取采购价
        getPurchase(o);
        //获取对应采购合同的预付款结算方式
        Long purchaseContractSid = o.getPurchaseContractSid();
        if (purchaseContractSid != null) {
            PurPurchaseContract purPurchaseContract = purPurchaseContractMapper.selectById(purchaseContractSid);
            o.setAdvanceSettleMode(purPurchaseContract.getAdvanceSettleMode());
        }
        setConfirmInfo(o);
        o.setDeliveryStatus("WJH");
        String documentType = o.getDocumentType();
        ConDocTypePurchaseOrder conDocTypePurchaseOrder = conDocTypePurchaseOrderMapper.selectOne(new QueryWrapper<ConDocTypePurchaseOrder>().lambda()
                .eq(ConDocTypePurchaseOrder::getCode, documentType)
        );
        if (ConstantsEms.YES.equals(conDocTypePurchaseOrder.getIsReturnGoods())) {
            o.setInOutStockStatus("WCK");
        } else {
            o.setInOutStockStatus("WRK");
        }
        int row = purPurchaseOrderMapper.insert(o);
        PurPurchaseOrder order = new PurPurchaseOrder();
        if (row > 0) {
            order = purPurchaseOrderMapper.selectById(o.getPurchaseOrderSid());
            //供应商寄售结算单
            if (HandleStatus.CONFIRMED.getCode().equals(o.getHandleStatus()) &&
                    ConstantsEms.VENDOR_SPECIAL_BUS_CATEGORY.equals(o.getSpecialBusCategory())) {
                inventoryDocument(o);
            }
            if (HandleStatus.CONFIRMED.getCode().equals(o.getHandleStatus()) && StringUtils.isEmpty(o.getSpecialBusCategory())) {
                advancesReceived(o);
            }
            //采购订单-明细对象
            List<PurPurchaseOrderItem> purPurchaseOrderItemList = o.getPurPurchaseOrderItemList();
            if (CollectionUtils.isNotEmpty(purPurchaseOrderItemList)) {
                updatePurchasePrice(purPurchaseOrderItemList);
                Set<Long> setGroubp = purPurchaseOrderItemList.stream().map(li -> li.getSku2GroupSid()).collect(Collectors.toSet());
                if (setGroubp.size() > 3) {
                    throw new CustomException("下单商品所属尺码组不允许超过3个，请核查！");
                }
                setItemNum(purPurchaseOrderItemList);
                addPurPurchaseOrderItem(o, purPurchaseOrderItemList);
                // 明细来源类别
                try {
                    List<PurPurchaseOrderDataSource> sourceList = new ArrayList<>();
                    PurPurchaseOrder finalOrder = order;
                    purPurchaseOrderItemList.forEach(i -> {
                        if (i.getReferDocCategory() != null && CollectionUtil.isNotEmpty(i.getOrderDataSourceList())) {
                            i.getOrderDataSourceList().forEach(e -> {
                                e.setPurchaseOrderSid(finalOrder.getPurchaseOrderSid())
                                        .setPurchaseOrderCode(finalOrder.getPurchaseOrderCode())
                                        .setPurchaseOrderItemSid(i.getPurchaseOrderItemSid())
                                        .setPurchaseOrderItemNum(i.getItemNum());
                            });
                            sourceList.addAll(i.getOrderDataSourceList());
                        }
                    });
                    if (CollectionUtil.isNotEmpty(sourceList)) {
                        purchaseOrderDataSourceMapper.inserts(sourceList);
                    }
                } catch (Exception e) {
                    log.error("采购订单明细来源类别写入错误");
                }
            }
            //采购订单-附件对象
            List<PurPurchaseOrderAttachment> purPurchaseOrderAttachmentList = o.getAttachmentList();
            if (CollectionUtils.isNotEmpty(purPurchaseOrderAttachmentList)) {
                addPurPurchaseOrderAttachment(o, purPurchaseOrderAttachmentList);
            }
        }
        //待办通知
        o.setPurchaseOrderCode(order.getPurchaseOrderCode());
        SysTodoTask sysTodoTask = new SysTodoTask();
        if (ConstantsEms.SAVA_STATUS.equals(order.getHandleStatus())) {
            sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                    .setTableName(ConstantsTable.TABLE_PURCHASE_ORDER)
                    .setDocumentSid(order.getPurchaseOrderSid());
            List<SysTodoTask> sysTodoTaskList = sysTodoTaskMapper.selectSysTodoTaskList(sysTodoTask);
            if (CollectionUtil.isEmpty(sysTodoTaskList)) {
                sysTodoTask.setTitle("采购订单" + order.getPurchaseOrderCode() + "当前是保存状态，请及时处理！")
                        .setDocumentCode(order.getPurchaseOrderCode().toString())
                        .setNoticeDate(new Date())
                        .setUserId(ApiThreadLocalUtil.get().getUserid());
                if (ConstantsEms.MATERIAL_CATEGORY_WL.equals(o.getMaterialCategory())) {
                    sysTodoTask.setMenuId(ConstantsWorkbench.purchase_order_wl);
                } else if (ConstantsEms.MATERIAL_CATEGORY_SP.equals(o.getMaterialCategory())) {
                    sysTodoTask.setMenuId(ConstantsWorkbench.purchase_order_sp);
                }
                sysTodoTaskMapper.insert(sysTodoTask);
            }
        } else {
            //校验是否存在待办
            checkTodoExist(o);
        }
        //插入日志
        String type = StrUtil.isBlank(o.getImportType()) ? BusinessType.INSERT.getValue() : o.getImportType();
        MongodbUtil.insertUserLog(o.getPurchaseOrderSid(), type, TITLE);
        return row;
    }

    //更新不含税
    public void updatePurchasePrice(List<PurPurchaseOrderItem> purPurchaseOrderItemList) {
        if (CollectionUtil.isNotEmpty(purPurchaseOrderItemList)) {
            purPurchaseOrderItemList.forEach(o -> {
                if (o.getPurchasePriceTax() != null && o.getTaxRate() != null) {
                    o.setPurchasePrice(o.getPurchasePriceTax().divide(BigDecimal.ONE.add(o.getTaxRate()), 6, BigDecimal.ROUND_HALF_UP));
                } else {
                    o.setPurchasePrice(null);
                }
            });
        }
    }

    //配置档案赋值
    public void setDoc(PurPurchaseOrder o) {
        ConBuTypePurchaseOrder conBuTypePurchaseOrder = conBuTypePurchaseOrderMapper.selectOne(new QueryWrapper<ConBuTypePurchaseOrder>().lambda()
                .eq(ConBuTypePurchaseOrder::getCode, o.getBusinessType()));
        if (conBuTypePurchaseOrder != null) {
            //配置档案赋值
            o.setDeliveryType(conBuTypePurchaseOrder.getDeliveryType())
                    .setInventoryControlMode(conBuTypePurchaseOrder.getInventoryControlMode())
                    .setYutouAnxuType(conBuTypePurchaseOrder.getYutouAnxuType());
            ConDocTypePurchaseOrder conDocTypePurchaseOrder = conDocTypePurchaseOrderMapper.selectOne(new QueryWrapper<ConDocTypePurchaseOrder>().lambda()
                    .eq(ConDocTypePurchaseOrder::getCode, o.getDocumentType())
            );
            if (conDocTypePurchaseOrder != null) {
                o.setIsFinanceBookYfzg(conDocTypePurchaseOrder.getIsFinanceBookYfzg())
                        .setIsFinanceBookDfyf(conDocTypePurchaseOrder.getIsFinanceBookDfyf())
                        .setIsFinanceBookYfzg(conDocTypePurchaseOrder.getIsFinanceBookYfzg())
                        .setIsConsignmentSettle(conDocTypePurchaseOrder.getIsConsignmentSettle())
                        .setIsReturnGoods(conDocTypePurchaseOrder.getIsReturnGoods());
            }
        }
        purPurchaseOrderMapper.updateById(o);
    }

    //校验是否超过合同金额
    @Override
    public int judgeConstract(PurPurchaseOrder order) {
        // 租户配置
        if (ConstantsOrder.CONTRACT_ENTER_MODE_SG.equals(ApiThreadLocalUtil.get().getSysUser().getClient().getPurchaseOrderContractEnterMode())) {
            return 1;
        }
        List<PurPurchaseOrderItem> purPurchaseOrderItems = new ArrayList<>();
        String[] handleList = {HandleStatus.CLOSED.getCode(), HandleStatus.CONFIRMED.getCode(), HandleStatus.SUBMIT.getCode()};
        List<PurPurchaseOrderItem> purPurchaseOrderItemList = order.getPurPurchaseOrderItemList();
        List<PurPurchaseOrder> purPurchaseOrders = purPurchaseOrderMapper.selectList(new QueryWrapper<PurPurchaseOrder>().lambda()
                .eq(PurPurchaseOrder::getPurchaseContractSid, order.getPurchaseContractSid())
                .in(PurPurchaseOrder::getHandleStatus, handleList)
        );

        if (CollectionUtil.isNotEmpty(purPurchaseOrders)) {
            List<PurPurchaseOrder> orders = purPurchaseOrders.stream().filter(li -> !li.getPurchaseOrderSid().toString().equals(order.getPurchaseOrderSid())).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(orders)) {
                orders.forEach(li -> {
                    //关闭的明细
                    if (HandleStatus.CLOSED.getCode().equals(li.getHandleStatus())) {
                        List<PurPurchaseOrderItem> orderItems = purPurchaseOrderItemMapper.selectList(new QueryWrapper<PurPurchaseOrderItem>().lambda()
                                .eq(PurPurchaseOrderItem::getPurchaseOrderSid, li.getPurchaseOrderSid())
                        );
                        if (CollectionUtil.isNotEmpty(orderItems)) {
                            //出入库量
                            orderItems.forEach(m -> {
                                List<InvInventoryDocumentItem> itemList = invInventoryDocumentItemMapper.selectList(new QueryWrapper<InvInventoryDocumentItem>().lambda()
                                        .eq(InvInventoryDocumentItem::getReferDocumentItemSid, m.getPurchaseOrderItemSid())
                                );
                                if (CollectionUtil.isNotEmpty(itemList)) {
                                    BigDecimal sum = itemList.stream().map(i -> {
                                        if (i.getPriceQuantity() != null) {
                                            return i.getQuantity().multiply(i.getUnitConversionRate());
                                        } else {
                                            return i.getQuantity();
                                        }
                                    }).reduce(BigDecimal.ZERO, BigDecimal::add);
                                    if (sum.compareTo(BigDecimal.ZERO) == 0) {
                                        m.setQuantity(BigDecimal.ZERO);
                                    } else {
                                        m.setQuantity(sum);
                                    }
                                }
                            });
                            if (ConstantsEms.YES.equals(li.getIsReturnGoods())) {
                                setChange(orderItems);
                            }
                            purPurchaseOrderItems.addAll(orderItems);
                        }
                    } else {
                        List<PurPurchaseOrderItem> orderItems = purPurchaseOrderItemMapper.selectList(new QueryWrapper<PurPurchaseOrderItem>().lambda()
                                .eq(PurPurchaseOrderItem::getPurchaseOrderSid, li.getPurchaseOrderSid())
                        );
                        if (ConstantsEms.YES.equals(li.getIsReturnGoods())) {
                            if (CollectionUtil.isNotEmpty(orderItems)) {
                                setChange(orderItems);
                            }
                        }
                        purPurchaseOrderItems.addAll(orderItems);
                    }
                });
            }
        }
        Long purchaseContractSid = order.getPurchaseContractSid();
        //所有符合的明细
        purPurchaseOrderItems.addAll(purPurchaseOrderItemList);
        if (CollectionUtil.isNotEmpty(purPurchaseOrderItems)) {
            BigDecimal sum = BigDecimal.ZERO;
            for (PurPurchaseOrderItem purPurchaseOrderItem : purPurchaseOrderItems) {
                if (!ConstantsEms.YES.equals(purPurchaseOrderItem.getFreeFlag())) {
                    if (purPurchaseOrderItem.getNewPurchasePriceTax() != null) {
                        if (purPurchaseOrderItem.getNewQuantity() != null) {
                            sum = purPurchaseOrderItem.getNewPurchasePriceTax().multiply(purPurchaseOrderItem.getNewQuantity()).add(sum);
                        } else {
                            sum = purPurchaseOrderItem.getNewPurchasePriceTax().multiply(purPurchaseOrderItem.getQuantity()).add(sum);
                        }
                    } else {
                        if (purPurchaseOrderItem.getPurchasePriceTax() != null) {
                            if (purPurchaseOrderItem.getNewQuantity() != null) {
                                sum = purPurchaseOrderItem.getPurchasePriceTax().multiply(purPurchaseOrderItem.getNewQuantity()).add(sum);
                            } else {
                                sum = purPurchaseOrderItem.getPurchasePriceTax().multiply(purPurchaseOrderItem.getQuantity()).add(sum);
                            }
                        }
                    }
                }
            }

            PurPurchaseContract contract = purPurchaseContractMapper.selectById(purchaseContractSid);
            BigDecimal currencyAmountTax = contract.getCurrencyAmountTax();
            if (sum.compareTo(currencyAmountTax != null ? currencyAmountTax : BigDecimal.ZERO) == 1) {
                return -1;
            }
        }
        return 1;
    }

    public void setChange(List<PurPurchaseOrderItem> purPurchaseOrderItems) {
        purPurchaseOrderItems.forEach(li -> {
            li.setQuantity(li.getQuantity().multiply(new BigDecimal(-1)));
            if (li.getNewQuantity() != null) {
                li.setNewQuantity(li.getNewQuantity().multiply(new BigDecimal(-1)));
            }
        });
    }

    @Override
    public PurPurchaseOrder getOrder(List<TecBomItemReport> order) {
        List<String> purchaseOrderList = new ArrayList<>();
        List<String> saleOrderList = new ArrayList<>();
        List<String> manOrderList = new ArrayList<>();
        order.forEach(o -> {
            if (o.getPurchaseOrderCode() != null) {
                purchaseOrderList.add(o.getPurchaseOrderCode().toString());
            }
            if (o.getPurchaseOrderCodeRemark() != null) {
                String[] codes = o.getPurchaseOrderCodeRemark().split(";");
                for (String code : codes) {
                    purchaseOrderList.add(code);
                }
            }
            if (o.getSalesOrderCode() != null) {
                saleOrderList.add(o.getSalesOrderCode().toString());
            }
            if (o.getSalesOrderCodeRemark() != null) {
                String[] codes = o.getSalesOrderCodeRemark().split(";");
                for (String code : codes) {
                    saleOrderList.add(code);
                }
            }
            if (o.getManufactureOrderCode() != null) {
                manOrderList.add(o.getManufactureOrderCode().toString());
            }
            if (o.getManufactureOrderCodeRemark() != null) {
                String[] codes = o.getManufactureOrderCodeRemark().split(";");
                for (String code : codes) {
                    manOrderList.add(code);
                }
            }
        });
        if (CollectionUtils.isNotEmpty(purchaseOrderList)) {
            List<PurPurchaseOrder> purPurchaseOrders = purPurchaseOrderMapper.selectList(new QueryWrapper<PurPurchaseOrder>().lambda()
                    .in(PurPurchaseOrder::getPurchaseOrderCode, purchaseOrderList)
            );
            if (CollectionUtils.isNotEmpty(purPurchaseOrders)) {
                purPurchaseOrders.forEach(li -> {
                    if (!ConstantsEms.CHECK_STATUS.equals(li.getHandleStatus())) {
                        throw new CustomException("非已确认状态的订单测算的物料需求，无法点击此按钮！");
                    }
                });
            }

        }
        if (CollectionUtils.isNotEmpty(saleOrderList)) {
            List<SalSalesOrder> salSalesOrders = salSalesOrderMapper.selectList(new QueryWrapper<SalSalesOrder>().lambda()
                    .in(SalSalesOrder::getSalesOrderCode, saleOrderList)
            );
            if (CollectionUtils.isNotEmpty(salSalesOrders)) {
                salSalesOrders.forEach(li -> {
                    if (!ConstantsEms.CHECK_STATUS.equals(li.getHandleStatus())) {
                        throw new CustomException("非已确认状态的订单测算的物料需求，无法点击此按钮！");
                    }
                });
            }
        }
        if (CollectionUtils.isNotEmpty(manOrderList)) {
            List<ManManufactureOrder> manManufactureOrders = manManufactureOrderMapper.selectList(new QueryWrapper<ManManufactureOrder>().lambda()
                    .in(ManManufactureOrder::getManufactureOrderCode, manOrderList)
            );
            if (CollectionUtils.isNotEmpty(manManufactureOrders)) {
                manManufactureOrders.forEach(li -> {
                    if (!ConstantsEms.CHECK_STATUS.equals(li.getHandleStatus())) {
                        throw new CustomException("非已确认状态的订单测算的物料需求，无法点击此按钮！");
                    }
                });
            }
        }
        PurPurchaseOrder purPurchaseOrder = new PurPurchaseOrder();
        Long vendorSid = null;
        List<TecBomItemReport> reports = order.stream().filter(li -> li.getVendorSid() != null).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(reports)) {
            vendorSid = reports.get(0).getVendorSid();
        }
        purPurchaseOrder.setVendorSid(vendorSid)
                .setDocumentType(DocCategory.PURCHASE_ORDER.getCode())
                .setCreateDate(new Date())
                .setCreatorAccount(ApiThreadLocalUtil.get().getUsername())
                .setDeliveryStatus("WJH")
                .setIsConsignmentSettle("N")
                .setInOutStockStatus("WRK")
                .setBuyer(ApiThreadLocalUtil.get().getUsername())
                .setDocumentDate(new Date())
                .setCurrency("CNY")
                .setMaterialCategory("WL")
                .setCurrencyUnit("YUAN")
                .setPurchaseMode("CG")
                .setRawMaterialMode(ConstantsEms.RAW_w)
                .setHandleStatus(ConstantsEms.SAVA_STATUS);
        PurPurchasePrice purchasePrice = new PurPurchasePrice();
        BeanCopyUtils.copyProperties(purPurchaseOrder, purchasePrice);
        List<PurPurchaseOrderItem> purPurchaseOrderItems = new ArrayList<>();
        order.forEach(li -> {
            BasMaterialBarcode basMaterialBarcode = null;
            Long barcodeSid = null;
            String barcode = null;
            PurPurchasePriceItem newPurchase = null;
            BigDecimal tax = null;
            BigDecimal PriceTax = null;
            PurPurchaseOrderItem purPurchaseOrderItem = new PurPurchaseOrderItem();
            Long produceSku1Sid = li.getSaleSku1Sid() == null ? null : Long.valueOf(li.getSaleSku1Sid());
            Long produceSku2Sid = li.getSaleSku2Sid() == null ? null : Long.valueOf(li.getSaleSku2Sid());
            Long produceSid = li.getSaleMaterialSid() == null ? null : Long.valueOf(li.getSaleMaterialSid());
            if (li.getBomMaterialSku2Sid() == null) {
                basMaterialBarcode = basMaterialBarcodeMapper.selectOne(new QueryWrapper<BasMaterialBarcode>().lambda()
                        .eq(BasMaterialBarcode::getSku1Sid, li.getBomMaterialSku1Sid())
                        .eq(BasMaterialBarcode::getMaterialSid, li.getBomMaterialSid())
                        .isNull(BasMaterialBarcode::getSku2Sid)
                );
            } else {
                basMaterialBarcode = basMaterialBarcodeMapper.selectOne(new QueryWrapper<BasMaterialBarcode>().lambda()
                        .eq(BasMaterialBarcode::getSku1Sid, li.getBomMaterialSku1Sid())
                        .eq(BasMaterialBarcode::getMaterialSid, li.getBomMaterialSid())
                        .eq(BasMaterialBarcode::getSku2Sid, li.getBomMaterialSku2Sid())
                );
            }
            if (basMaterialBarcode != null) {
                barcodeSid = basMaterialBarcode.getBarcodeSid();
                barcode = basMaterialBarcode.getBarcode();
            } else {
                throw new CustomException(li.getMaterialCode() + "," + li.getMaterialName() + "没有对应的商品条码，无法创建采购订单");
            }
            purchasePrice.setSku1Sid(li.getBomMaterialSku1Sid())
                    .setSku2Sid(li.getBomMaterialSku2Sid())
                    .setMaterialSid(li.getBomMaterialSid());
            if (purchasePrice.getVendorSid() != null) {
                newPurchase = priceService.getNewPurchase(purchasePrice);
                if (newPurchase.getPurchasePriceTax() != null) {
                    tax = newPurchase.getTaxRate();
                    PriceTax = newPurchase.getPurchasePriceTax();
                }
            }
            purPurchaseOrderItem.setMaterialSid(li.getBomMaterialSid())
                    .setMaterialName(li.getMaterialName())
                    .setMaterialCode(li.getMaterialCode())
                    .setSku1Name(li.getSku1Name())
                    .setTaxRate(tax)
                    .setPurchasePriceTax(PriceTax)
                    .setPurchasePrice(PriceTax == null ? null : PriceTax.divide(BigDecimal.ONE.add(PriceTax), 6, BigDecimal.ROUND_HALF_UP))
                    .setSku1Sid(li.getBomMaterialSku1Sid())
                    .setSku2Sid(li.getBomMaterialSku2Sid())
                    .setBarcodeSid(barcodeSid)
                    .setInOutStockStatus("WRK")
                    .setBarcode(barcode)
                    .setSku2Name(li.getSku2Name())
                    .setQuantity(BigDecimal.valueOf(Double.valueOf(li.getLossRequireQuantity())))
                    .setUnitBase(newPurchase != null ? newPurchase.getUnitBase() : null)
                    .setUnitBaseName(newPurchase != null ? newPurchase.getUnitBaseName() : null)
                    .setUnitPrice(newPurchase != null ? newPurchase.getUnitPrice() : null)
                    .setUnitPriceName(newPurchase != null ? newPurchase.getUnitPriceName() : null)
                    .setUnitConversionRate(newPurchase != null ? newPurchase.getUnitConversionRate() : null)
                    .setProductQuantityRemark(li.getProductQuantity() != null ? li.getProductQuantity().toString() : null)
                    .setProductMoCodes(li.getManufactureOrderCode() != null ? li.getManufactureOrderCode().toString() : li.getManufactureOrderCodeRemark())
                    .setProductPoCodes(li.getPurchaseOrderCode() != null ? li.getPurchaseOrderCode().toString() : li.getPurchaseOrderCodeRemark())
                    .setProductSoCodes(li.getSalesOrderCode() != null ? li.getSalesOrderCode().toString() : li.getSalesOrderCodeRemark())
                    .setCreateDate(new Date())
                    .setCreatorAccount(ApiThreadLocalUtil.get().getUsername())
                    .setProductSku2Name(li.getSaleSku2Name())
                    .setProductSku2Sid(li.getSaleSku2Sid())
                    .setProductCodes(li.getSaleMaterialCode() != null ? li.getSaleMaterialCode() : li.getMaterialCodeRemark())
                    .setProductSku1Names(li.getSaleSku1Name() != null ? li.getSaleSku1Name() : li.getMaterialSkuRemark())
                    .setProductSku2Names(li.getSaleSku2Name() != null ? li.getSaleSku2Name() : li.getMaterialSku2Remark())
                    .setManufactureOrderCode(li.getManufactureOrderCode())
                    .setManufactureOrderSid(li.getManufactureOrderCode() != null ? li.getCommonSid() : null)
                    .setManufactureOrderProductSid(li.getManufactureOrderCode() != null ? li.getCommonItemSid() : null)
                    .setManufactureOrderProductNum(li.getManufactureOrderCode() != null ? li.getCommonItemNum() : null)
                    .setReferPurchaseOrderCode(li.getPurchaseOrderCode())
                    .setReferPurchaseOrderSid(li.getPurchaseOrderCode() != null ? li.getCommonSid() : null)
                    .setReferPurchaseOrderItemSid(li.getPurchaseOrderCode() != null ? li.getCommonItemSid() : null)
                    .setReferPurchaseOrderItemNum(li.getPurchaseOrderCode() != null ? li.getCommonItemNum() : null)
                    .setSalesOrderCode(li.getSalesOrderCode())
                    .setSalesOrderItemSid(li.getSalesOrderCode() != null ? li.getCommonItemSid() : null)
                    .setSalesOrderItemNum(li.getSalesOrderCode() != null ? li.getCommonItemNum() : null)
                    .setSalesOrderSid(li.getSalesOrderCode() != null ? li.getCommonSid() : null);
            String productRequestPartys = null;
            String productRequestBusType = null;
            if (li.getCommonItemSidRemark() != null) {
                String itemSid = li.getCommonItemSidRemark();
                String[] sids = itemSid.split(";");
                if (li.getSalesOrderCode() != null || li.getSalesOrderCodeRemark() != null) {
                    SalSalesOrderItem salSalesOrderItem = new SalSalesOrderItem();
                    salSalesOrderItem.setItemSidList(sids);
                    List<SalSalesOrderItem> itemList = salSalesOrderItemMapper.getItemList(salSalesOrderItem);
                    ArrayList<PurPurchaseOrderMaterialProduct> purPurchaseOrderMaterialProducts = new ArrayList<>();
                    HashMap<String, BigDecimal> quantityMap = li.getQuantityMap();
                    for (SalSalesOrderItem it : itemList) {
                        PurPurchaseOrderMaterialProduct item = new PurPurchaseOrderMaterialProduct();
                        item.setSalesOrderCode(Long.valueOf(it.getSalesOrderCode()))
                                .setSalesOrderItemNum(Long.valueOf(it.getItemNum()))
                                .setSalesOrderItemSid(it.getSalesOrderItemSid())
                                .setProductSid(it.getMaterialSid())
                                .setProductSku1Sid(it.getSku1Sid())
                                .setProductSku2Sid(it.getSku2Sid())
                                .setProductCode(it.getMaterialCode())
                                .setProductName(it.getMaterialName())
                                .setCustomerName(it.getCustomerName())
                                .setProductSku1Name(it.getSku1Name())
                                .setProductSku2Name(it.getSku2Name())
                                .setSalesOrderSid(it.getSalesOrderSid())
                                .setReferDocCategory("SalesOrder")
                                .setReferDocCategoryName("销售订单")
                                .setReferDocCode(Long.valueOf(it.getSalesOrderCode()))
                                .setReferDocItemNum(Long.valueOf(it.getItemNum()))
                                .setReferDocItemSid(it.getSalesOrderItemSid())
                                .setReferDocSid(it.getSalesOrderSid())
                                .setQuantityProduct(it.getQuantity() != null ? it.getQuantity().divide(BigDecimal.ONE, 4, BigDecimal.ROUND_HALF_UP) : null)
                                .setQuantityMaterial(quantityMap.get(it.getSalesOrderItemSid().toString()) != null ? quantityMap.get(it.getSalesOrderItemSid().toString()).divide(BigDecimal.ONE, 4, BigDecimal.ROUND_HALF_UP) : null);
                        purPurchaseOrderMaterialProducts.add(item);
                        if (productRequestPartys != null) {
                            Boolean match = match(productRequestPartys, it.getCustomerShortName());//重复校验
                            if (!match) {
                                productRequestPartys = productRequestPartys + ";" + it.getCustomerShortName();
                            }
                        } else {
                            productRequestPartys = it.getCustomerShortName();
                        }
                        if (productRequestBusType != null) {
                            Boolean match = match(productRequestBusType, it.getBusinessTypeName());//重复校验
                            if (!match) {
                                productRequestBusType = productRequestBusType + ";" + it.getBusinessTypeName();
                            }
                        } else {
                            productRequestBusType = it.getBusinessTypeName();
                        }
                    }
                    purPurchaseOrderItem.setMaterialProductList(purPurchaseOrderMaterialProducts);
                }
                if (li.getPurchaseOrderCode() != null || li.getPurchaseOrderCodeRemark() != null) {
                    PurPurchaseOrderItem orderItem = new PurPurchaseOrderItem();
                    orderItem.setItemSidList(sids);
                    List<PurPurchaseOrderItem> itemList = purPurchaseOrderItemMapper.getItemList(orderItem);
                    ArrayList<PurPurchaseOrderMaterialProduct> purPurchaseOrderMaterialProducts = new ArrayList<>();
                    HashMap<String, BigDecimal> quantityMap = li.getQuantityMap();
                    for (PurPurchaseOrderItem it : itemList) {
                        PurPurchaseOrderMaterialProduct item = new PurPurchaseOrderMaterialProduct();
                        item.setPurchaseOrderCode(Long.valueOf(it.getPurchaseOrderCode()))
                                .setPurchaseOrderItemNum(Long.valueOf(it.getItemNum()))
                                .setPurchaseOrderItemSid(it.getPurchaseOrderItemSid())
                                .setProductSid(it.getMaterialSid())
                                .setProductSku1Sid(it.getSku1Sid())
                                .setProductSku2Sid(it.getSku2Sid())
                                .setProductCode(it.getMaterialCode())
                                .setProductName(it.getMaterialName())
                                .setVendorName(it.getVendorShortName())
                                .setProductSku1Name(it.getSku1Name())
                                .setProductSku2Name(it.getSku2Name())
                                .setPurchaseOrderSid(it.getPurchaseOrderSid())
                                .setReferDocCategory("PurchaseOrder")
                                .setReferDocCategoryName("采购订单")
                                .setReferDocCode(Long.valueOf(it.getPurchaseOrderCode()))
                                .setReferDocItemNum(Long.valueOf(it.getItemNum()))
                                .setReferDocItemSid(it.getPurchaseOrderItemSid())
                                .setReferDocSid(it.getPurchaseOrderSid())
                                .setQuantityProduct(it.getQuantity() != null ? it.getQuantity().divide(BigDecimal.ONE, 4, BigDecimal.ROUND_HALF_UP) : null)
                                .setQuantityMaterial(quantityMap.get(it.getPurchaseOrderItemSid().toString()) != null ? quantityMap.get(it.getPurchaseOrderItemSid().toString()).divide(BigDecimal.ONE, 4, BigDecimal.ROUND_HALF_UP) : null);
                        purPurchaseOrderMaterialProducts.add(item);
                        if (productRequestPartys != null) {
                            Boolean match = match(productRequestPartys, it.getVendorShortName());//重复校验
                            if (!match) {
                                productRequestPartys = productRequestPartys + ";" + it.getVendorShortName();
                            }
                        } else {
                            productRequestPartys = it.getVendorShortName();
                        }
                        if (productRequestBusType != null) {
                            Boolean match = match(productRequestBusType, it.getDocumentTypeName());//重复校验
                            if (!match) {
                                productRequestBusType = productRequestBusType + ";" + it.getDocumentTypeName();
                            }
                        } else {
                            productRequestBusType = it.getDocumentTypeName();
                        }
                    }
                    purPurchaseOrderItem.setMaterialProductList(purPurchaseOrderMaterialProducts);
                }
            }
            purPurchaseOrderItem.setProductRequestPartys(productRequestPartys)
                    .setProductRequestBusType(productRequestBusType);
            purPurchaseOrderItems.add(purPurchaseOrderItem);
        });
        purPurchaseOrder.setPurPurchaseOrderItemList(purPurchaseOrderItems);
        return purPurchaseOrder;
    }

    /**
     * 匹配值 是否重复
     */
    public Boolean match(String remark, String match) {
        String[] remarkList = remark.split(";");
        List remarkListNow = Arrays.asList(remarkList);
        boolean exit = remarkListNow.stream().anyMatch(m -> m.equals(match));
        return exit;
    }

    /**
     * 外部系统接口-修改采购订单状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeHandleOut(List<PurPurchaseOrderHandleRequest> list) {
        list.forEach(item -> {
            PurPurchaseOrder purPurchaseOrder = purPurchaseOrderMapper.selectById(item.getPurchaseOrderSid());
            if (purPurchaseOrder == null) {
                throw new CustomException("没有找到单号为" + item.getPurchaseOrderCode() + "的采购订单");
            }
        });
        list.forEach(item -> {
            purPurchaseOrderMapper.update(new PurPurchaseOrder(), new UpdateWrapper<PurPurchaseOrder>().lambda()
                    .eq(PurPurchaseOrder::getPurchaseOrderSid, item.getPurchaseOrderSid())
                    .set(PurPurchaseOrder::getHandleStatus, item.getHandleStatus())

            );
        });
        return 1;
    }

    //采购订单作废-校验
    @Override
    public int disusejudge(List<Long> sids) {
        sids.forEach(id -> {
            PurPurchaseOrder purchaseOrder = purPurchaseOrderMapper.selectPurPurchaseOrderById(id);
            String shipmentCategory = purchaseOrder.getDeliveryType();
            if (ConstantsEms.PURCHASE_SHIP.equals(shipmentCategory)) {
                List<DelDeliveryNoteItem> documentItemList = delDeliveryNoteItemMapper.selectList(new QueryWrapper<DelDeliveryNoteItem>()
                        .lambda().eq(DelDeliveryNoteItem::getPurchaseOrderSid, purchaseOrder.getPurchaseOrderSid()));
                if (CollectionUtil.isNotEmpty(documentItemList)) {
                    Long[] documentSids = documentItemList.stream().map(DelDeliveryNoteItem::getDeliveryNoteSid).toArray(Long[]::new);
                    List<DelDeliveryNote> documentList = delDeliveryNoteMapper.selectList(new QueryWrapper<DelDeliveryNote>()
                            .lambda().in(DelDeliveryNote::getDeliveryNoteSid, documentSids)
                            .ne(DelDeliveryNote::getHandleStatus, HandleStatus.INVALID.getCode()));
                    if (CollectionUtil.isNotEmpty(documentList)) {
                        throw new BaseException("该采购订单已存在交货数据，无法作废！");
                    }
                }
            } else {
                List<InvInventoryDocumentItem> invInventoryDocumentItems = invInventoryDocumentItemMapper.selectList(new QueryWrapper<InvInventoryDocumentItem>().lambda()
                        .eq(InvInventoryDocumentItem::getReferDocumentSid, id));
                if (CollectionUtil.isNotEmpty(invInventoryDocumentItems)) {
                    List<Long> documentSids = invInventoryDocumentItems.stream().map(InvInventoryDocumentItem::getInventoryDocumentSid).collect(toList());
                    List<InvInventoryDocument> invInventoryDocuments = invInventoryDocumentMapper.selectList(new QueryWrapper<InvInventoryDocument>().lambda()
                            .in(InvInventoryDocument::getInventoryDocumentSid, documentSids)
                            .eq(InvInventoryDocument::getHandleStatus, HandleStatus.POSTING.getCode())
                            .eq(InvInventoryDocument::getDocumentType, ConstantsEms.DOCUMNET_TYPE_ZG));
                    if (CollectionUtil.isNotEmpty(invInventoryDocuments)) {
                        throw new CustomException("此采购订单已存在出入库数据，无法作废！");
                    }
                }

                // 租户配置
                if (!ConstantsOrder.CONTRACT_ENTER_MODE_SG.equals(ApiThreadLocalUtil.get().getSysUser().getClient().getPurchaseOrderContractEnterMode())) {
                    //采购合同
                    PurPurchaseContract purPurchaseContract = purPurchaseContractMapper.selectPurPurchaseContractById(purchaseOrder.getPurchaseContractSid());
                    //预付款方式组合
                    ConAccountMethodGroup accountMethodGroup = conAccountMethodGroupMapper.selectConAccountMethodGroupById(purPurchaseContract.getAccountsMethodGroup());
                    //预付款结算方式
                    if (ADVANCE_SETTLE_MODE_DD.equals(purPurchaseContract.getAdvanceSettleMode()) && Double.parseDouble(accountMethodGroup.getAdvanceRate()) > 0) {
                        List<FinRecordAdvancePayment> finRecordAdvancePayments = finRecordAdvancePaymentMapper.selectList(new QueryWrapper<FinRecordAdvancePayment>().lambda()
                                .eq(FinRecordAdvancePayment::getPurchaseOrderSid, purchaseOrder.getPurchaseOrderSid())
                        );
                        if (CollectionUtils.isNotEmpty(finRecordAdvancePayments)) {
                            List<Long> finRecordAdvancesids = finRecordAdvancePayments.stream().map(li -> li.getRecordAdvancePaymentSid()).collect(Collectors.toList());
                            List<FinRecordAdvancePaymentItem> finRecordAdvancePaymentItems = finRecordAdvancePaymentItemMapper.selectList(new QueryWrapper<FinRecordAdvancePaymentItem>().lambda()
                                    .in(FinRecordAdvancePaymentItem::getRecordAdvancePaymentSid, finRecordAdvancesids)
                            );
                            if (CollectionUtils.isNotEmpty(finRecordAdvancePaymentItems)) {
                                finRecordAdvancePaymentItems.stream().forEach(li -> {
                                    if (!ConstantsEms.CLEAR_STATUS_WHX.equals(li.getClearStatus())) {
                                        throw new CustomException("采购订单" + purchaseOrder.getPurchaseOrderCode() + "，对应的供应商待付预付款流水非“未核销”状态，不允许作废！");
                                    }
                                });
                            }
                        }
                    }
                }
            }
        });
        return 1;
    }

    //采购订单作废
    @Override
    public int disuse(OrderInvalidRequest request) {
        List<Long> sids = request.getSids();
        String explain = request.getExplain();
        sids.forEach(id -> {
            PurPurchaseOrder purPurchaseOrder = new PurPurchaseOrder();
            purPurchaseOrder.setPurchaseOrderSid(id)
                    .setHandleStatus(HandleStatus.INVALID.getCode());
            purPurchaseOrderMapper.updateById(purPurchaseOrder);
            finRecordAdvancePaymentMapper.update(new FinRecordAdvancePayment(), new UpdateWrapper<FinRecordAdvancePayment>().lambda()
                    .eq(FinRecordAdvancePayment::getPurchaseOrderSid, id)
                    .set(FinRecordAdvancePayment::getHandleStatus, HandleStatus.INVALID.getCode())
            );
            // 明细状态
            purPurchaseOrderItemMapper.update(new PurPurchaseOrderItem(), new UpdateWrapper<PurPurchaseOrderItem>().lambda()
                    .eq(PurPurchaseOrderItem::getPurchaseOrderSid, id)
                    .set(PurPurchaseOrderItem::getItemStatus, ConstantsEms.STATUS_INVALID_STATUS));
            // 操作日志
            MongodbUtil.insertApprovalLog(id, BusinessType.CANCEL.getValue(), explain);
        });
        return 1;
    }

    // 订单明细作废
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int itemDisuse(OrderInvalidRequest request){
        int row = 0;
        List<Long> sids = request.getSids();
        if (sids == null || sids.size() == 0) {
            throw new BaseException("请选择行！");
        }
        List<PurPurchaseOrderItem> itemList = purPurchaseOrderItemMapper.selectOrderItemListBy(new PurPurchaseOrderItem()
                .setHandleStatus(ConstantsEms.CHECK_STATUS)
                .setPurchaseOrderItemSidList(sids.stream().toArray(Long[]::new)));
        if (CollectionUtil.isNotEmpty(itemList) && itemList.size() == sids.size()) {
            // 明细状态
            row = purPurchaseOrderItemMapper.update(new PurPurchaseOrderItem(), new UpdateWrapper<PurPurchaseOrderItem>().lambda()
                    .in(PurPurchaseOrderItem::getPurchaseOrderItemSid, sids)
                    .set(PurPurchaseOrderItem::getItemStatus, ConstantsEms.STATUS_INVALID_STATUS));
        }
        else {
            throw new BaseException("仅已确认状态的订单明细才可以作此操作！");
        }
        return row;
    }

    /**
     * 校验是否存在待办
     */
    private void checkTodoExist(PurPurchaseOrder order) {
        List<SysTodoTask> todoTaskList = sysTodoTaskMapper.selectList(new QueryWrapper<SysTodoTask>().lambda()
                .eq(SysTodoTask::getDocumentSid, order.getPurchaseOrderSid()));
        if (CollectionUtil.isNotEmpty(todoTaskList)) {
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getDocumentSid, order.getPurchaseOrderSid())
                    .eq(SysTodoTask::getTableName, ConstantsTable.TABLE_PURCHASE_ORDER));
        }
    }

    @Override
    public void inventoryDocument(PurPurchaseOrder o) {
        List<PurPurchaseOrderItem> itemList = o.getPurPurchaseOrderItemList();
        /*
        itemList.forEach(li->{
            if(li.getPurchasePriceTax()==null&&!ConstantsEms.YES.equals(li.getFreeFlag())){
                throw new CustomException("物料编码"+li.getMaterialCode()+"，采购价未维护，请至采购价页面进行维护");
            }
        });
        */
        advancesReceived(o);
        List<PurPurchaseOrderItem> list = o.getPurPurchaseOrderItemList();
        FinBookPaymentEstimation finBookPaymentEstimation = new FinBookPaymentEstimation();
        BeanCopyUtils.copyProperties(o, finBookPaymentEstimation);
        List<FinBookPaymentEstimationItem> finBookPaymentEstimationItems = new ArrayList<>();
        list.forEach(li -> {
            PurRecordVendorConsign purRecordVendorConsign = new PurRecordVendorConsign();
            BeanCopyUtils.copyProperties(o, purRecordVendorConsign);
            BeanCopyUtils.copyProperties(li, purRecordVendorConsign);
            purRecordVendorConsign.setQuantity(li.getUnitConversionRate() != null ? li.getUnitConversionRate().multiply(li.getQuantity()) : li.getQuantity());
            purRecordVendorConsign.setType(ConstantsEms.CHU_KU);
            //盘盈 供应商寄售待结算台账 扣减
            purRecordVendorConsignServiceImpl.insertPurRecordVendorConsign(purRecordVendorConsign);
            //应付暂估
            FinBookPaymentEstimationItem finBookPaymentEstimationItem = new FinBookPaymentEstimationItem();
            BeanCopyUtils.copyProperties(li, finBookPaymentEstimationItem);
            finBookPaymentEstimationItem.setCreateDate(new Date())
                    .setCreatorAccount(ApiThreadLocalUtil.get().getUsername())
                    .setPriceTax(li.getPurchasePriceTax())
                    .setPrice(li.getPurchasePriceTax().divide(BigDecimal.ONE.add(li.getTaxRate()), 4, BigDecimal.ROUND_HALF_UP))
                    .setCurrencyAmountTax(li.getPurchasePriceTax().multiply(li.getQuantity()))
                    .setReferDocSid(li.getReferDocSid())
                    .setReferDocItemSid(o.getPurchaseOrderSid())
                    .setPurchaseContractSid(o.getPurchaseContractSid())
                    .setPurchaseContractCode(String.valueOf(o.getPurchaseOrderCode()))
                    .setPurchaseOrderSid(o.getPurchaseOrderSid())
                    .setReferDocCategory("PO");
            finBookPaymentEstimationItems.add(finBookPaymentEstimationItem);
        });
        //生成应付暂估
        finBookPaymentEstimation.setBookSourceCategory(ConstantsEms.VEN_INV);
        finBookPaymentEstimation.setItemList(finBookPaymentEstimationItems);
        finBookPaymentEstimationServiceImpl.insertFinBookPaymentEstimation(finBookPaymentEstimation);
    }

    //生成预付款台账
    @Override
    public void advancesReceived(PurPurchaseOrder o) {
        String isBusinessFinance = ApiThreadLocalUtil.get().getSysUser().getIsBusinessFinance();
        if (ConstantsEms.YES.equals(isBusinessFinance)) {
            //1.预付款结算方式是否选择：按订单   2.预付款比例是否大于0   3.合同类型是否选择：标准合同，如是则合同金额需大于0
            if (ConstantsEms.YES.equals(o.getIsFinanceBookDfyf()) && o.getPurchaseContractSid() != null) {
                //采购合同信息
                PurPurchaseContract purPurchaseContract = purPurchaseContractMapper.selectPurPurchaseContractById(o.getPurchaseContractSid());
                if (purPurchaseContract != null) {
                    //预付款方式组合
                    ConAccountMethodGroup accountMethodGroup = conAccountMethodGroupMapper.selectConAccountMethodGroupById(purPurchaseContract.getAccountsMethodGroup());
                    //预付款结算方式
                    if (ADVANCE_SETTLE_MODE_DD.equals(purPurchaseContract.getAdvanceSettleMode()) && Double.parseDouble(accountMethodGroup.getAdvanceRate()) > 0) {
                        //凭证日期
                        FinRecordAdvancePayment finRecordAdvancePayment = new FinRecordAdvancePayment();
                        BeanCopyUtils.copyProperties(o, finRecordAdvancePayment);
                        finRecordAdvancePayment.setDocumentDate(new Date());
                        finRecordAdvancePayment.setBookType(ConstantsFinance.BOOK_TYPE_YUF);
                        finRecordAdvancePayment.setBookSourceCategory(ConstantsFinance.BOOK_SOURCE_CAT_PO);
                        finRecordAdvancePayment.setSettleMode(ADVANCE_SETTLE_MODE_DD);
                        finRecordAdvancePayment.setPurchaseContractSid(purPurchaseContract.getPurchaseContractSid());
                        finRecordAdvancePayment.setPurchaseContractCode(purPurchaseContract.getPurchaseContractCode());
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(o.getDocumentDate());
                        finRecordAdvancePayment.setBuyer(o.getBuyer());
                        finRecordAdvancePayment.setMaterialType(o.getMaterialType());
                        finRecordAdvancePayment.setPaymentYear(calendar.get(Calendar.YEAR));
                        finRecordAdvancePayment.setPaymentMonth(calendar.get(Calendar.MONTH));
                        finRecordAdvancePayment.setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
                        finRecordAdvancePayment.setCreateDate(new Date());
                        finRecordAdvancePayment.setCurrencyAmountTaxContract(purPurchaseContract.getCurrencyAmountTax());//合同金额
                        finRecordAdvancePayment.setAdvanceRate(new BigDecimal(accountMethodGroup.getAdvanceRate()));//预收款比例
                        List<PurPurchaseOrderItem> purchaseOrderItemList = new ArrayList<>();
                        purchaseOrderItemList = o.getPurPurchaseOrderItemList();
                        if (CollectionUtils.isEmpty(purchaseOrderItemList)) {
                            purchaseOrderItemList = purPurchaseOrderItemMapper.selectList(new QueryWrapper<PurPurchaseOrderItem>()
                                    .lambda().eq(PurPurchaseOrderItem::getPurchaseOrderSid, o.getPurchaseOrderSid()));
                        }
                        BigDecimal init = null;
                        BigDecimal amount = BigDecimal.ZERO;
                        FinRecordAdvancePaymentItem finRecordAdvancePaymentItem = new FinRecordAdvancePaymentItem();
                        List<FinRecordAdvancePayment> reportFormList = finRecordAdvancePaymentMapper.getReportForm(new FinRecordAdvancePayment().setPurchaseOrderSid(o.getPurchaseOrderSid()));
                        if (CollectionUtils.isNotEmpty(purchaseOrderItemList)) {
                            for (int i = 0; i < purchaseOrderItemList.size(); i++) {
                                PurPurchaseOrderItem purPurchaseOrderItem = purchaseOrderItemList.get(i);
                                if (purPurchaseOrderItem.getPurchasePriceTax() != null && !ConstantsEms.YES.equals(purPurchaseOrderItem.getFreeFlag())) {

                                    //金额：采购量*采购价（含税）
                                    init = purPurchaseOrderItem.getQuantity().multiply(purPurchaseOrderItem.getPurchasePriceTax());
                                    amount = init.add(amount);
                                }
                                finRecordAdvancePaymentItem.setTaxRate(purPurchaseOrderItem.getTaxRate());
                            }
                        }
                        //订单金额
                        finRecordAdvancePayment.setCurrencyAmountTaxPo(amount);
                        //预付款比例
                        BigDecimal advanceRate = new BigDecimal(accountMethodGroup.getAdvanceRate());
                        //预付款金额：总金额*预付款比例
                        amount = amount.multiply(advanceRate);
                        if (CollectionUtil.isNotEmpty(reportFormList)) {
                            Long recordAdvancePaymentSid = reportFormList.get(0).getRecordAdvancePaymentSid();
                            finRecordAdvancePayment.setRecordAdvancePaymentSid(recordAdvancePaymentSid);
                            finRecordAdvancePaymentMapper.updateById(finRecordAdvancePayment);
                            //原来的流水的待核销金额+已核销金额
                            BigDecimal origin = reportFormList.get(0).getCurrencyAmountTaxYhx().add(reportFormList.get(0).getCurrencyAmountTaxHxz());
                            if (amount.subtract(origin).compareTo(BigDecimal.ZERO) == -1) {
                                throw new CustomException("订单金额小于后续业务金额，不允许确认！");
                            }
                            finRecordAdvancePaymentItemMapper.update(new FinRecordAdvancePaymentItem(), new UpdateWrapper<FinRecordAdvancePaymentItem>().lambda()
                                    .eq(FinRecordAdvancePaymentItem::getRecordAdvancePaymentSid, reportFormList.get(0).getRecordAdvancePaymentSid())
                                    .set(FinRecordAdvancePaymentItem::getCurrencyAmountTaxYingf, amount)
                            );
                        }
                        if (CollectionUtil.isEmpty(reportFormList)) {
                            finRecordAdvancePaymentMapper.insert(finRecordAdvancePayment);
                            //应付金额
                            finRecordAdvancePaymentItem.setCurrencyAmountTaxYingf(amount);
                            finRecordAdvancePaymentItem.setRecordAdvancePaymentSid(finRecordAdvancePayment.getRecordAdvancePaymentSid())
                                    .setCurrencyAmountTaxYhx(BigDecimal.ZERO)
                                    .setCurrencyAmountTaxHxz(BigDecimal.ZERO)
                                    .setClearStatus(ConstantsFinance.CLEAR_STATUS_WHX);
                            if (purPurchaseContract.getYfAccountValidDays() != null) {
                                Date dateValid = new Date();
                                Calendar calendarValid = new GregorianCalendar();
                                calendarValid.setTime(dateValid);
                                calendarValid.add(calendarValid.DATE, purPurchaseContract.getYfAccountValidDays()); //把日期往后增加i天,整数  往后推,负数往前移动
                                dateValid = calendarValid.getTime(); //这个时间就是日期往后推i天的结果
                                finRecordAdvancePaymentItem.setAccountValidDate(dateValid);
                            }
                            finRecordAdvancePaymentItemMapper.insert(finRecordAdvancePaymentItem);
                        }
                    }
                }
            }
        }
    }

    /**
     * 设置确认信息
     */
    private void setConfirmInfo(PurPurchaseOrder o) {
        if (o == null) {
            return;
        }
        if (HandleStatus.CONFIRMED.getCode().equals(o.getHandleStatus())) {
            o.setConfirmerAccount(SecurityUtils.getUsername());
            o.setConfirmDate(new Date());
        }
    }

    /**
     * 采购订单-明细对象
     */
    private void addPurPurchaseOrderItem(PurPurchaseOrder purPurchaseOrder, List<PurPurchaseOrderItem> purPurchaseOrderItemList) {

        purPurchaseOrderItemList.forEach(o -> {
            if (o.getBarcodeSid() == null) {
                throw new BaseException("编码为" + o.getMaterialCode() + "的商品未生成商品条码，请重新添加！");
            }
            o.setPurchaseOrderSid(purPurchaseOrder.getPurchaseOrderSid());
            //来源单据类别：销售订单
            if (StringUtils.isNotEmpty(o.getReferDocCode())) {
                o.setReferDocCategory(ConstantsEms.REFER_DOC_CATEGORY);
            }
            if (o.getPurchasePriceTax() != null && o.getTaxRate() != null) {
                o.setPurchasePrice(o.getPurchasePriceTax().divide(BigDecimal.ONE.add(o.getTaxRate()), 6, BigDecimal.ROUND_HALF_UP));
            } else {
                o.setPurchasePrice(null);
            }
            if (o.getInOutStockStatus() == null) {
                o.setInOutStockStatus(purPurchaseOrder.getInOutStockStatus());
            }
            if (ConstantsEms.YES.equals(o.getFreeFlag())) {
                o.setPurchasePrice(BigDecimal.ZERO).setPurchasePriceTax(BigDecimal.ZERO);
            }
            purPurchaseOrderItemMapper.insert(o);
            List<PurPurchaseOrderDeliveryPlan> deliveryPlanList = o.getDeliveryPlanList();
            if (CollectionUtil.isNotEmpty(deliveryPlanList)) {
                BigDecimal sum = deliveryPlanList.stream().map(li -> li.getPlanQuantity()).reduce(BigDecimal.ZERO, BigDecimal::add);
                if (sum.compareTo(o.getQuantity()) == 1) {
                    throw new BaseException("第" + o.getItemNum() + "行，订单量小于计划发货量总和，请核实！");
                }
                deliveryPlanList.forEach(li -> {
                    li.setPurchaseOrderItemSid(o.getPurchaseOrderItemSid())
                            .setPurchaseOrderSid(o.getPurchaseOrderSid());
                });
                deliveryPlanMapper.inserts(deliveryPlanList);
            }
            List<PurPurchaseOrderMaterialProduct> materialProductList = o.getMaterialProductList();
            if (CollectionUtils.isNotEmpty(materialProductList)) {
                BigDecimal sum = materialProductList.stream().map(li -> li.getQuantityMaterial()).reduce(BigDecimal.ZERO, BigDecimal::add);
                if (sum.compareTo(o.getQuantity()) == 1) {
                    throw new BaseException("第" + o.getItemNum() + "行，“所用商品信息”页签中，面辅料数量总和不能大于订单量，请核实！");
                }
                materialProductList.forEach(li -> {
                    li.setMaterialPurchaseOrderItemSid(o.getPurchaseOrderItemSid())
                            .setMaterialPurchaseOrderSid(o.getPurchaseOrderSid());
                });
                purPurchaseOrderMaterialProductMapper.inserts(materialProductList);
            }
        });
    }


    public void setItemValue(PurPurchaseOrder purPurchaseOrder, List<PurPurchaseOrderItem> purPurchaseOrderItemList) {
        purPurchaseOrderItemList.forEach(o -> {
            if (o.getPurchasePriceTax() != null && o.getTaxRate() != null) {
                o.setPurchasePrice(o.getPurchasePriceTax().divide(BigDecimal.ONE.add(o.getTaxRate()), 6, BigDecimal.ROUND_HALF_UP));
            }
            if (o.getInOutStockStatus() == null) {
                if (ConstantsEms.YES.equals(purPurchaseOrder.getIsReturnGoods())) {
                    o.setInOutStockStatus("WCK");
                } else {
                    o.setInOutStockStatus("WRK");
                }
            }
        });
    }


    /**
     * 采购订单-附件对象
     */
    private void addPurPurchaseOrderAttachment(PurPurchaseOrder purPurchaseOrder, List<PurPurchaseOrderAttachment> purPurchaseOrderAttachmentList) {
        purPurchaseOrderAttachmentList.forEach(o -> {
            o.setPurchaseOrderSid(purPurchaseOrder.getPurchaseOrderSid());
            purPurchaseOrderAttachmentMapper.insert(o);
        });
    }

    @Override
    public void setValueNull(Long sid) {
        List<PurPurchaseOrderItem> purPurchaseOrderItems = purPurchaseOrderItemMapper.selectList(new QueryWrapper<PurPurchaseOrderItem>().lambda()
                .eq(PurPurchaseOrderItem::getPurchaseOrderSid, sid)
        );
        if (CollectionUtil.isNotEmpty(purPurchaseOrderItems)) {
            purPurchaseOrderItems.forEach(li -> {
                li.setNewPurchasePrice(null)
                        .setNewQuantity(null)
                        .setNewTaxRate(null)
                        .setNewPurchasePriceTax(null)
                        .setNewContractDate(null);
                purPurchaseOrderItemMapper.updateAllById(li);
            });
            // 撤回来源变更下单量
            returnSourceNewQuantity(new PurPurchaseOrder().setPurchaseOrderSid(sid));
        }
    }

    /**
     * 设置签收状态
     */
    @Override
    public int setSignStatus(OrderItemStatusSignRequest request) {
        Long[] sidList = request.getSidList();
        String signInStatus = request.getSignInStatus();
        int row = purPurchaseOrderMapper.update(new PurPurchaseOrder(), new UpdateWrapper<PurPurchaseOrder>().lambda()
                .in(PurPurchaseOrder::getPurchaseOrderSid, sidList)
                .set(PurPurchaseOrder::getSignInStatus, signInStatus)
        );
        return row;
    }

    /**
     * 采购订单报表更新采购价
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult updatePrice(List<PurPurchaseOrderItem> purPurchaseOrderItems) {
        List<PurPurchaseOrderItem> item = purPurchaseOrderItems.stream().filter(li -> li.getPurchasePriceTax() != null).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(item)) {
            throw new CustomException("所选择的明细行，采购价不允许有值");
        }
        List<CommonErrMsgResponse> msgList = new ArrayList<>();
        ArrayList<PurPurchaseOrderItem> purPurchasePriceItemList = new ArrayList<>();
        Map<String, List<PurPurchaseOrderItem>> map = purPurchaseOrderItems.stream().collect(Collectors.groupingBy(v -> v.getPurchaseOrderSid() + ";" + v.getPurchaseOrderCode()));
        map.keySet().stream().forEach(key -> {
            String[] arr = key.split(";");
            List<PurPurchaseOrderItem> items = map.get(key);
            List<Long> notSidList = items.stream().map(li -> li.getPurchaseOrderItemSid()).collect(Collectors.toList());
            List<PurPurchaseOrderItem> orderItems = purPurchaseOrderItemMapper.selectList(new QueryWrapper<PurPurchaseOrderItem>().lambda()
                    .notIn(PurPurchaseOrderItem::getPurchaseOrderItemSid, notSidList)
                    .eq(PurPurchaseOrderItem::getPurchaseOrderSid, arr[0])
            );
            HashSet<BigDecimal> set = new HashSet<>();
            if (CollectionUtil.isNotEmpty(orderItems)) {
                Set<BigDecimal> taxSet = orderItems.stream().filter(li -> li.getTaxRate() != null).map(li -> li.getTaxRate()).collect(Collectors.toSet());
                set.addAll(taxSet);
            }
            items.stream().forEach(li -> {
                PurPurchasePrice purchasePrice = new PurPurchasePrice();
                BeanCopyUtils.copyProperties(li, purchasePrice);
                PurPurchasePriceItem purchase = priceService.getNewPurchase(purchasePrice);
                if (purchase.getPurchasePriceTax() != null) {
                    BigDecimal price = purchase.getPurchasePriceTax().divide(BigDecimal.ONE.add(purchase.getTaxRate()), 6, BigDecimal.ROUND_HALF_UP);
                    li.setPurchasePriceTax(purchase.getPurchasePriceTax())
                            .setUnitPrice(purchase.getUnitPrice())
                            .setTaxRate(purchase.getTaxRate())
                            .setUnitBase(purchase.getUnitBase())
                            .setUnitConversionRate(purchase.getUnitConversionRate())
                            .setPurchasePrice(price);
                    set.add(purchase.getTaxRate());
                }
            });
            purPurchasePriceItemList.addAll(items);
            if (set.size() > 1) {
                CommonErrMsgResponse commonErrMsgResponse = new CommonErrMsgResponse();
                commonErrMsgResponse.setCode(Long.valueOf(arr[1]))
                        .setMsg("明细行的税率不一致");
                msgList.add(commonErrMsgResponse);
            }
        });
        if (CollectionUtil.isNotEmpty(msgList)) {
            return AjaxResult.success("500", msgList);
        }
        if (CollectionUtil.isNotEmpty(purPurchasePriceItemList)) {
            purPurchasePriceItemList.forEach(li -> {
                purPurchaseOrderItemMapper.updateById(li);
            });
        }
        return AjaxResult.success(1);
    }

    /**
     * 设置委托人
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setTrustor(OrderTrustorAccountRequest order) {
        List<Long> orderSidList = order.getOrderSidList();
        String trustorAccount = order.getTrustorAccount();
        int row = purPurchaseOrderMapper.update(new PurPurchaseOrder(), new UpdateWrapper<PurPurchaseOrder>().lambda()
                .in(PurPurchaseOrder::getPurchaseOrderSid, orderSidList)
                .set(PurPurchaseOrder::getTrustorAccount, trustorAccount)
        );
        return row;
    }

    /**
     * 修改采购订单
     *
     * @param purPurchaseOrder 采购订单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePurPurchaseOrder(PurPurchaseOrder o) {
        Long vendorSid = o.getVendorSid();
        String vendorGroup = basVendorMapper.selectById(vendorSid).getVendorGroup();
        if (YCX_VENDOR.equals(vendorGroup)) {
            if (o.getVendorNameRemark() == null) {
                throw new CustomException("供应商名称备注,不能为空");
            }
        }
        if (ConstantsEms.CHECK_STATUS.equals(o.getHandleStatus())) {
            judgeNull(o);
        }
        //获取采购价
        getPurchase(o);
        setConfirmInfo(o);
        PurPurchaseOrder old = purPurchaseOrderMapper.selectById(o.getPurchaseOrderSid());
        int row = purPurchaseOrderMapper.updateAllById(o);
        if (row > 0) {
            //供应商寄售结算单
            if (HandleStatus.CONFIRMED.getCode().equals(o.getHandleStatus()) &&
                    ConstantsEms.VENDOR_SPECIAL_BUS_CATEGORY.equals(o.getSpecialBusCategory())) {
                inventoryDocument(o);
            }

            if (HandleStatus.CONFIRMED.getCode().equals(o.getHandleStatus()) && StringUtils.isEmpty(o.getSpecialBusCategory())) {
                advancesReceived(o);
            }
            //采购订单-明细对象
            List<PurPurchaseOrderItem> purPurchaseOrderItemList = o.getPurPurchaseOrderItemList();
            if (CollectionUtils.isNotEmpty(purPurchaseOrderItemList)) {
                updatePurchasePrice(purPurchaseOrderItemList);
                Set<Long> setGroubp = purPurchaseOrderItemList.stream().map(li -> li.getSku2GroupSid()).collect(Collectors.toSet());
                if (setGroubp.size() > 3) {
                    throw new CustomException("下单商品所属尺码组不允许超过3个，请核查！");
                }
                setItemValue(o, purPurchaseOrderItemList);
                List<PurPurchaseOrderItem> purPurchasePriceItems = purPurchaseOrderItemMapper.selectList(new QueryWrapper<PurPurchaseOrderItem>().lambda()
                        .eq(PurPurchaseOrderItem::getPurchaseOrderSid, o.getPurchaseOrderSid())
                );
                List<Long> longs = purPurchasePriceItems.stream().map(li -> li.getPurchaseOrderItemSid()).collect(Collectors.toList());
                List<Long> longsNow = purPurchaseOrderItemList.stream().map(li -> li.getPurchaseOrderItemSid()).collect(Collectors.toList());
                //两个集合取差集
                List<Long> reduce = longs.stream().filter(item -> !longsNow.contains(item)).collect(Collectors.toList());
                //删除明细
                if (CollectionUtil.isNotEmpty(reduce)) {
                    purPurchaseOrderItemMapper.deleteBatchIds(reduce);
                    deliveryPlanMapper.delete(new QueryWrapper<PurPurchaseOrderDeliveryPlan>().lambda()
                            .in(PurPurchaseOrderDeliveryPlan::getPurchaseOrderItemSid, reduce));
                    purPurchaseOrderMaterialProductMapper.delete(new QueryWrapper<PurPurchaseOrderMaterialProduct>().lambda()
                            .in(PurPurchaseOrderMaterialProduct::getMaterialPurchaseOrderItemSid, reduce)
                    );
                    // 删除数据来源记录表
                    purchaseOrderDataSourceMapper.delete(new QueryWrapper<PurPurchaseOrderDataSource>().lambda()
                            .in(PurPurchaseOrderDataSource::getPurchaseOrderItemSid, reduce));
                }
                //修改明细
                List<PurPurchaseOrderItem> exitItem = purPurchaseOrderItemList.stream().filter(li -> li.getPurchaseOrderItemSid() != null).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(exitItem)) {
                    exitItem.forEach(li -> {
                        if (ConstantsEms.YES.equals(li.getFreeFlag())) {
                            li.setPurchasePrice(BigDecimal.ZERO).setPurchasePriceTax(BigDecimal.ZERO);
                        }
                        purPurchaseOrderItemMapper.updateAllById(li);
                        List<PurPurchaseOrderDeliveryPlan> deliveryPlanList = li.getDeliveryPlanList();
                        deliveryPlanMapper.delete(new QueryWrapper<PurPurchaseOrderDeliveryPlan>().lambda()
                                .eq(PurPurchaseOrderDeliveryPlan::getPurchaseOrderItemSid, li.getPurchaseOrderItemSid()));
                        purPurchaseOrderMaterialProductMapper.delete(new QueryWrapper<PurPurchaseOrderMaterialProduct>().lambda()
                                .eq(PurPurchaseOrderMaterialProduct::getMaterialPurchaseOrderItemSid, li.getPurchaseOrderItemSid()));
                        //采购交货计划
                        if (CollectionUtil.isNotEmpty(deliveryPlanList)) {
                            BigDecimal sum = deliveryPlanList.stream().map(h -> h.getPlanQuantity()).reduce(BigDecimal.ZERO, BigDecimal::add);
                            if (sum.compareTo(li.getQuantity()) == 1) {
                                throw new CustomException("第" + li.getItemNum() + "行，订单量小于计划交货量总和，请核实！");
                            }
                            deliveryPlanList.forEach(i -> {
                                i.setPurchaseOrderItemSid(li.getPurchaseOrderItemSid())
                                        .setPurchaseOrderSid(li.getPurchaseOrderSid());
                            });
                            deliveryPlanMapper.inserts(deliveryPlanList);
                        }
                        List<PurPurchaseOrderMaterialProduct> productList = li.getMaterialProductList();
                        if (CollectionUtils.isNotEmpty(productList)) {
                            BigDecimal sum = productList.stream().map(m -> m.getQuantityMaterial()).reduce(BigDecimal.ZERO, BigDecimal::add);
                            if (sum.compareTo(li.getQuantity()) == 1) {
                                throw new BaseException("第" + li.getItemNum() + "行，“所用商品信息”页签中，面辅料数量总和不能大于订单量，请核实！");
                            }
                            productList.forEach(i -> {
                                i.setMaterialPurchaseOrderItemSid(li.getPurchaseOrderItemSid())
                                        .setMaterialPurchaseOrderSid(li.getPurchaseOrderSid());
                            });
                            purPurchaseOrderMaterialProductMapper.inserts(productList);
                        }
                    });
                }
                //新增明细
                List<PurPurchaseOrderItem> nullItem = purPurchaseOrderItemList.stream().filter(li -> li.getPurchaseOrderItemSid() == null).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(nullItem)) {
                    int max = 0;
                    if (CollectionUtils.isNotEmpty(purPurchasePriceItems)) {
                        max = purPurchasePriceItems.stream().mapToInt(li -> li.getItemNum()).max().getAsInt();
                    }
                    for (int i = 0; i < nullItem.size(); i++) {
                        int maxItem = max + i + 1;
                        nullItem.get(i).setItemNum(maxItem);
                        nullItem.get(i).setPurchaseOrderSid(o.getPurchaseOrderSid());
                        if (ConstantsEms.YES.equals(nullItem.get(i).getFreeFlag())) {
                            nullItem.get(i).setPurchasePrice(BigDecimal.ZERO).setPurchasePriceTax(BigDecimal.ZERO);
                        }
                        purPurchaseOrderItemMapper.insert(nullItem.get(i));
                        List<PurPurchaseOrderDeliveryPlan> deliveryPlanList = nullItem.get(i).getDeliveryPlanList();
                        //销售发货计划
                        if (CollectionUtil.isNotEmpty(deliveryPlanList)) {
                            BigDecimal sum = deliveryPlanList.stream().map(h -> h.getPlanQuantity()).reduce(BigDecimal.ZERO, BigDecimal::add);
                            if (sum.compareTo(nullItem.get(i).getQuantity()) == 1) {
                                throw new CustomException("第" + nullItem.get(i).getItemNum() + "行，订单量小于计划交货量总和，请核实！");
                            }
                            for (PurPurchaseOrderDeliveryPlan deliveryPlan : deliveryPlanList) {
                                deliveryPlan.setPurchaseOrderItemSid(nullItem.get(i).getPurchaseOrderItemSid())
                                        .setPurchaseOrderSid(nullItem.get(i).getPurchaseOrderSid());
                            }
                            deliveryPlanMapper.inserts(deliveryPlanList);
                        }
                        List<PurPurchaseOrderMaterialProduct> productList = nullItem.get(i).getMaterialProductList();
                        if (CollectionUtils.isNotEmpty(productList)) {
                            BigDecimal sum = productList.stream().map(m -> m.getQuantityMaterial()).reduce(BigDecimal.ZERO, BigDecimal::add);
                            if (sum.compareTo(nullItem.get(i).getQuantity()) == 1) {
                                throw new BaseException("第" + nullItem.get(i).getItemNum() + "行，“所用商品信息”页签中，面辅料数量总和不能大于订单量，请核实！");
                            }
                            for (PurPurchaseOrderMaterialProduct product : productList) {
                                product.setMaterialPurchaseOrderItemSid(nullItem.get(i).getPurchaseOrderItemSid())
                                        .setMaterialPurchaseOrderSid(nullItem.get(i).getPurchaseOrderSid());
                            }
                            purPurchaseOrderMaterialProductMapper.inserts(productList);
                        }
                    }
                }
            } else {
                purPurchaseOrderItemMapper.delete(new QueryWrapper<PurPurchaseOrderItem>().lambda()
                        .eq(PurPurchaseOrderItem::getPurchaseOrderSid, o.getPurchaseOrderSid())
                );
                // 删除数据来源记录表
                purchaseOrderDataSourceMapper.delete(new QueryWrapper<PurPurchaseOrderDataSource>().lambda()
                        .eq(PurPurchaseOrderDataSource::getPurchaseOrderSid, o.getPurchaseOrderSid()));
            }
            //采购订单-附件对象
            List<PurPurchaseOrderAttachment> purPurchaseOrderAttachmentList = o.getAttachmentList();
            purPurchaseOrderAttachmentMapper.delete(new UpdateWrapper<PurPurchaseOrderAttachment>().lambda()
                    .eq(PurPurchaseOrderAttachment::getPurchaseOrderSid, o.getPurchaseOrderSid()));
            if (CollectionUtils.isNotEmpty(purPurchaseOrderAttachmentList)) {
                purPurchaseOrderAttachmentList.stream().forEach(a -> {
                    a.setUpdateDate(new Date());
                    a.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                });
                addPurPurchaseOrderAttachment(o, purPurchaseOrderAttachmentList);
            }
        }
        if (!ConstantsEms.SAVA_STATUS.equals(o.getHandleStatus())) {
            //校验是否存在待办
            checkTodoExist(o);
        }
        // 判断 新旧合同 的 特殊用途是否 是 临时过渡, 删除该待办
        if (old.getPurchaseContractSid() != null && !old.getPurchaseContractSid().equals(o.getPurchaseContractSid())) {
            List<Long> contractSidList = new ArrayList<Long>() {{
                add(old.getPurchaseContractSid());
            }};
            if (o.getPurchaseContractSid() != null) {
                contractSidList.add(o.getPurchaseContractSid());
            }
            List<PurPurchaseContract> purchaseContractList = purPurchaseContractMapper.selectList(new QueryWrapper<PurPurchaseContract>().lambda()
                    .in(PurPurchaseContract::getPurchaseContractSid, contractSidList));
            if (CollectionUtil.isNotEmpty(purchaseContractList)) {
                Map<Long, PurPurchaseContract> map = purchaseContractList.stream().collect(Collectors.toMap(PurPurchaseContract::getPurchaseContractSid, Function.identity()));
                if (map.containsKey(old.getPurchaseContractSid()) && ConstantsOrder.CONTRACT_PURPOSE_LSGD.equals(map.get(old.getPurchaseContractSid()).getContractPurpose())) {
                    if (!map.containsKey(o.getPurchaseContractSid()) || !ConstantsOrder.CONTRACT_PURPOSE_LSGD.equals(map.get(o.getPurchaseContractSid()).getContractPurpose())) {
                        sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
                                .eq(SysTodoTask::getDocumentSid, o.getPurchaseOrderSid()).eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB)
                                .eq(SysTodoTask::getTableName, ConstantsTable.TABLE_PURCHASE_ORDER + "-" + ConstantsTable.TABLE_PURCHASE_CONTRACT)
                                .like(SysTodoTask::getTitle, "使用的是过渡合同"));
                    }
                }
            }
        }
        //更新通知
        if (ConstantsEms.CHECK_STATUS.equals(o.getHandleStatus())) {
            SysBusinessBcst sysBusinessBcst = new SysBusinessBcst();
            String shortName = basVendorMapper.selectById(o.getVendorSid()).getShortName();
            sysBusinessBcst.setTitle("供应商:" + shortName + "，采购订单编号" + o.getPurchaseOrderCode() + "的信息发生变更，请知悉！")
                    .setDocumentSid(o.getPurchaseOrderSid())
                    .setDocumentCode(o.getPurchaseOrderCode().toString())
                    .setNoticeDate(new Date()).setUserId(ApiThreadLocalUtil.get().getUserid());
            if (ConstantsEms.MATERIAL_CATEGORY_WL.equals(o.getMaterialCategory())) {
                sysBusinessBcst.setMenuId(ConstantsWorkbench.purchase_order_wl);
            } else if (ConstantsEms.MATERIAL_CATEGORY_SP.equals(o.getMaterialCategory())) {
                sysBusinessBcst.setMenuId(ConstantsWorkbench.purchase_order_sp);
            }
            sysBusinessBcstMapper.insert(sysBusinessBcst);
        }
        //审批
        if (ConstantsEms.SUBMIT_STATUS.equals(o.getHandleStatus())) {
            Submit submit = new Submit();
            submit.setStartUserId(ApiThreadLocalUtil.get().getUserid().toString());
            submit.setFormType(FormType.CGDD_BG.getCode());
            List<FormParameter> list = new ArrayList();
            FormParameter formParameter = new FormParameter();
            formParameter.setParentId(o.getPurchaseOrderSid().toString());
            formParameter.setFormId(o.getPurchaseOrderSid().toString());
            formParameter.setFormCode(o.getPurchaseOrderCode().toString());
            list.add(formParameter);
            submit.setFormParameters(list);
            workflowService.change(submit);
        }
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(o.getPurchaseOrderSid(), BusinessType.UPDATE.getValue(), TITLE);
        }
        return row;
    }

    /**
     * 批量删除采购订单
     *
     * @param purchaseOrderSids 需要删除的采购订单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePurPurchaseOrderByIds(Long[] purchaseOrderSids) {
        List<PurPurchaseOrder> purPurchaseOrders = purPurchaseOrderMapper.selectList(new QueryWrapper<PurPurchaseOrder>().lambda()
                .in(PurPurchaseOrder::getPurchaseOrderSid, purchaseOrderSids)
        );
        String userName = ApiThreadLocalUtil.get().getSysUser().getUserName();
        purPurchaseOrders.forEach(li -> {
            if (!userName.equals(li.getCreatorAccount()) && !userName.equals(li.getTrustorAccount())) {
                throw new CustomException("当前登入账号不是订单号" + li.getPurchaseOrderCode() + "的创建人或委托人，不允许此操作！");
            }
        });
        PurPurchaseOrder params = new PurPurchaseOrder();
        params.setPurchaseOrderSids(purchaseOrderSids);
        params.setHandleStatus(HandleStatus.SAVE.getCode());
        for (Long purchaseOrderSid : purchaseOrderSids) {
            PurPurchaseOrder purPurchaseOrder = new PurPurchaseOrder();
            purPurchaseOrder.setPurchaseOrderSid(purchaseOrderSid);
            //校验是否存在待办
            checkTodoExist(purPurchaseOrder);
            //插入日志
            MongodbUtil.insertUserLog(purchaseOrderSid, BusinessType.DELETE.getValue(), TITLE);
        }
        //删除采购订单
        purPurchaseOrderMapper.deletePurPurchaseOrderByIds(purchaseOrderSids);
        //删除采购订单明细
        purPurchaseOrderItemMapper.deletePurPurchaseOrderItemByIds(purchaseOrderSids);
        // 删除数据来源记录表
        purchaseOrderDataSourceMapper.delete(new QueryWrapper<PurPurchaseOrderDataSource>().lambda()
                .in(PurPurchaseOrderDataSource::getPurchaseOrderSid, purchaseOrderSids));
        //删除采购订单附件
        purPurchaseOrderAttachmentMapper.deletePurPurchaseOrderAttachmentByIds(purchaseOrderSids);
        //删除采购交货计划
        deliveryPlanMapper.delete(new QueryWrapper<PurPurchaseOrderDeliveryPlan>().lambda()
                .in(PurPurchaseOrderDeliveryPlan::getPurchaseOrderSid, purchaseOrderSids)
        );
        //删除所用商品信息
        purPurchaseOrderMaterialProductMapper.delete(new QueryWrapper<PurPurchaseOrderMaterialProduct>().lambda()
                .in(PurPurchaseOrderMaterialProduct::getMaterialPurchaseOrderSid, purchaseOrderSids)
        );
        return purchaseOrderSids.length;
    }

    /**
     * 采购订单驳回
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int returnSourceNewQuantity(PurPurchaseOrder purPurchaseOrder) {
        List<PurPurchaseOrderDataSource> sourceList = purchaseOrderDataSourceMapper.selectList(new QueryWrapper<PurPurchaseOrderDataSource>()
                .lambda().eq(PurPurchaseOrderDataSource::getPurchaseOrderSid, purPurchaseOrder.getPurchaseOrderSid())
                .isNotNull(PurPurchaseOrderDataSource::getNewQuantity));
        if (CollectionUtil.isNotEmpty(sourceList)) {
            sourceList.forEach(item -> {
                item.setNewQuantity(null);
            });
            purchaseOrderDataSourceMapper.updatesAllById(sourceList);
        }
        return 1;
    }

    /**
     * 采购订单确认
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int confirm(PurPurchaseOrder purPurchaseOrder) {
        //采购订单sids
        Long[] purchaseOrderSids = purPurchaseOrder.getPurchaseOrderSids();
        for (Long purchaseOrderSid : purchaseOrderSids) {
            PurPurchaseOrder purchaseOrder = purPurchaseOrderMapper.selectById(purchaseOrderSid);
            purPurchaseOrderMapper.update(new PurPurchaseOrder(), new UpdateWrapper<PurPurchaseOrder>().lambda()
                    .eq(PurPurchaseOrder::getPurchaseOrderSid, purchaseOrderSid)
                    .set(PurPurchaseOrder::getConfirmDate, new Date())
                    .set(PurPurchaseOrder::getHandleStatus, ConstantsEms.CHECK_STATUS)
                    .set(PurPurchaseOrder::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername())
            );
            PurPurchaseOrder o = purPurchaseOrderMapper.selectPurPurchaseOrderById(purchaseOrderSid);
            //校验是否存在待办
            checkTodoExist(o);
            PurPurchaseOrderItem purPurchaseOrderItem = new PurPurchaseOrderItem();
            purPurchaseOrderItem.setPurchaseOrderSid(purchaseOrderSid);
            List<PurPurchaseOrderItem> purPurchaseOrderItemList = purPurchaseOrderItemMapper.selectPurPurchaseOrderItemList(purPurchaseOrderItem);
            o.setPurPurchaseOrderItemList(purPurchaseOrderItemList);
            judgeNull(o);
            //采购订单-明细对象
            List<PurPurchaseOrderItem> itemList = o.getPurPurchaseOrderItemList();
            if (CollectionUtils.isNotEmpty(itemList)) {
                ConBuTypePurchaseOrder conBuTypePurchaseOrder = conBuTypePurchaseOrderMapper.selectOne(new QueryWrapper<ConBuTypePurchaseOrder>().lambda()
                        .eq(ConBuTypePurchaseOrder::getCode, o.getBusinessType()));
                itemList.forEach(li -> {
                    li.setToexpireDays(conBuTypePurchaseOrder.getToexpireDays());
                    purPurchaseOrderItemMapper.updateAllById(li);
                });
                // 明细的数据来源
                try {
                    Long[] orderItemSids = itemList.stream().map(PurPurchaseOrderItem::getPurchaseOrderItemSid).toArray(Long[]::new);
                    List<PurPurchaseOrderDataSource> sourceList = purchaseOrderDataSourceMapper.selectList(new QueryWrapper<PurPurchaseOrderDataSource>()
                            .lambda().in(PurPurchaseOrderDataSource::getPurchaseOrderItemSid, orderItemSids));
                    if (CollectionUtil.isNotEmpty(sourceList)) {
                        sourceList.forEach(item -> {
                            if (item.getNewQuantity() != null) {
                                BigDecimal newQuantity = new BigDecimal(String.valueOf(item.getNewQuantity()));
                                item.setQuantity(newQuantity);
                                item.setNewQuantity(null);
                            }
                        });
                        purchaseOrderDataSourceMapper.updatesAllById(sourceList);
                    }
                } catch (Exception e) {
                    log.error("确认采购订单时更新明细数据来源已下单了出现错误，请检查！");
                }
            }
            //配置档案赋值
            setDoc(o);
            //供应商寄售结算单
            if (HandleStatus.CONFIRMED.getCode().equals(o.getHandleStatus()) &&
                    ConstantsEms.VENDOR_SPECIAL_BUS_CATEGORY.equals(o.getSpecialBusCategory())) {
                inventoryDocument(o);
            }
            if (HandleStatus.CONFIRMED.getCode().equals(o.getHandleStatus()) && StringUtils.isEmpty(o.getSpecialBusCategory())) {
                advancesReceived(o);
            }
            if (ConstantsEms.SAVA_STATUS.equals(purchaseOrder.getHandleStatus())) {
                MongodbUtil.insertUserLog(o.getPurchaseOrderSid(), BusinessType.CONFIRM.getValue(), TITLE);
            }
        }
        return 1;
    }

    public void judgeNull(PurPurchaseOrder purPurchaseOrder) {
        String shipmentCategory = purPurchaseOrder.getDeliveryType();
        List<PurPurchaseOrderItem> list = purPurchaseOrder.getPurPurchaseOrderItemList();
        if (CollectionUtils.isEmpty(list)) {
            throw new CustomException("明细行不允许为空");
        }
        if (!ConstantsEms.NO.equals(ApiThreadLocalUtil.get().getSysUser().getClient().getIsRequiredPurchaseOrderContract())
                && StrUtil.isBlank(purPurchaseOrder.getPurchaseContractCode())) {
            throw new CustomException("采购合同号不允许为空");
        }
        if (!ConstantsEms.VENDOR_SPECIAL_BUS_CATEGORY.equals(purPurchaseOrder.getSpecialBusCategory())) {
            list.forEach(li -> {
                if (li.getContractDate() == null) {
                    throw new CustomException("明细行的合同交期不能为空");
                }
            });
        }
        //存在 新采购量(变更中)
        List<PurPurchaseOrderItem> items = list.stream().filter(m -> m.getNewQuantity() != null).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(items)) {
            list.forEach(li -> {
                List<DelDeliveryNoteItem> deliveryList = new ArrayList<>();
                if (li.getNewQuantity() != null) {
                    BigDecimal total = BigDecimal.ZERO;
                    //已出库量
                    BigDecimal sumYCK = BigDecimal.ZERO;
                    if (ConstantsEms.PURCHASE_SHIP.equals(shipmentCategory)) {
                        List<DelDeliveryNote> delDeliveryNotes = delDeliveryNoteMapper.selectList(new QueryWrapper<DelDeliveryNote>().lambda()
                                .eq(DelDeliveryNote::getPurchaseOrderSid, purPurchaseOrder.getPurchaseOrderSid())
                        );
                        if (CollectionUtil.isNotEmpty(delDeliveryNotes)) {
                            //获取该订单的采购发货单的所有明细信息
                            delDeliveryNotes.forEach(de -> {
                                List<DelDeliveryNoteItem> delDeliveryNoteItems = delDeliveryNoteItemMapper.selectList(new QueryWrapper<DelDeliveryNoteItem>().lambda()
                                        .eq(DelDeliveryNoteItem::getDeliveryNoteSid, de.getDeliveryNoteSid())
                                );
                                delDeliveryNoteItems.forEach(l -> {
                                    if (de.getInOutStockStatus() != null) {
                                        l.setInOutStockStatus(de.getInOutStockStatus());
                                    }
                                });
                                deliveryList.addAll(delDeliveryNoteItems);
                            });
                            if (CollectionUtil.isNotEmpty(deliveryList)) {
                                String documentType = purPurchaseOrder.getDocumentType();
                                if (ConstantsEms.YES.equals(purPurchaseOrder.getIsReturnGoods())) {
                                    //未入库量
                                    List<DelDeliveryNoteItem> itemListW = deliveryList.stream()
                                            .filter(m -> m.getPurchaseOrderItemSid().toString().equals(li.getPurchaseOrderItemSid().toString()) && m.getInOutStockStatus().equals(ConstantsEms.OUT_STORE_STATUS_NOT)
                                            ).collect(Collectors.toList());
                                    //已入库量
                                    List<DelDeliveryNoteItem> itemListY = deliveryList.stream()
                                            .filter(m -> m.getPurchaseOrderItemSid().toString().equals(li.getPurchaseOrderItemSid().toString()) && !m.getInOutStockStatus().equals(ConstantsEms.OUT_STORE_STATUS_NOT)
                                            ).collect(Collectors.toList());
                                    if (CollectionUtil.isNotEmpty(itemListW)) {
                                        itemListW = itemListW.stream().filter(h -> h.getDeliveryQuantity() != null).collect(Collectors.toList());
                                        BigDecimal sum = itemListW.stream().map(h -> h.getDeliveryQuantity()).reduce(BigDecimal.ZERO, BigDecimal::add);
                                        total = total.add(sum);
                                    }
                                    if (CollectionUtil.isNotEmpty(itemListY)) {
                                        itemListY = itemListY.stream().filter(h -> h.getInOutStockQuantity() != null).collect(Collectors.toList());
                                        BigDecimal sum = itemListY.stream().map(h -> h.getInOutStockQuantity()).reduce(BigDecimal.ZERO, BigDecimal::add);
                                        sumYCK = sum;
                                        total = total.add(sum);
                                    }
                                } else {
                                    //未入库量
                                    List<DelDeliveryNoteItem> itemListW = deliveryList.stream()
                                            .filter(m -> m.getPurchaseOrderItemSid().toString().equals(li.getPurchaseOrderItemSid().toString()) && m.getInOutStockStatus().equals(ConstantsEms.IN_STORE_STATUS_NOT)
                                            ).collect(Collectors.toList());
                                    //已入库量
                                    List<DelDeliveryNoteItem> itemListY = deliveryList.stream()
                                            .filter(m -> m.getPurchaseOrderItemSid().toString().equals(li.getPurchaseOrderItemSid().toString()) && !m.getInOutStockStatus().equals(ConstantsEms.IN_STORE_STATUS_NOT)
                                            ).collect(Collectors.toList());
                                    if (CollectionUtil.isNotEmpty(itemListW)) {
                                        itemListW = itemListW.stream().filter(h -> h.getDeliveryQuantity() != null).collect(Collectors.toList());
                                        BigDecimal sum = itemListW.stream().map(h -> h.getDeliveryQuantity()).reduce(BigDecimal.ZERO, BigDecimal::add);
                                        total = total.add(sum);
                                    }
                                    if (CollectionUtil.isNotEmpty(itemListY)) {
                                        itemListY = itemListY.stream().filter(h -> h.getInOutStockQuantity() != null).collect(Collectors.toList());
                                        BigDecimal sum = itemListY.stream().map(h -> h.getInOutStockQuantity()).reduce(BigDecimal.ZERO, BigDecimal::add);
                                        sumYCK = sum;
                                        total = total.add(sum);
                                    }
                                }
                            }
                        }
                    } else {
                        List<InvInventoryDocumentItem> invInventoryDocumentList = invInventoryDocumentItemMapper.selectList(new QueryWrapper<InvInventoryDocumentItem>().lambda()
                                .eq(InvInventoryDocumentItem::getReferDocumentItemSid, li.getPurchaseOrderItemSid()));
                        //采购订单入库
                        if (CollectionUtil.isNotEmpty(invInventoryDocumentList)) {
                            List<InvInventoryDocumentItem> qualtityList = invInventoryDocumentList.stream().filter(n -> n.getReferDocumentItemSid().toString().equals(li.getPurchaseOrderItemSid().toString())).collect(Collectors.toList());
                            if (CollectionUtil.isNotEmpty(qualtityList)) {
                                BigDecimal sum = qualtityList.stream().map(q -> {
                                    if (q.getPriceQuantity() != null) {
                                        return q.getPriceQuantity();
                                    } else {
                                        return q.getQuantity();
                                    }
                                }).reduce(BigDecimal.ZERO, BigDecimal::add);
                                total = sum;
                                sumYCK = sum;
                            }
                        }
                    }
                    if (li.getNewQuantity().compareTo(total) == -1) {
                        throw new CustomException("第" + li.getItemNum() + "行，新订单量小于订单已交货量");
                    }
                    if (ConstantsEms.CHECK_STATUS.equals(purPurchaseOrder.getHandleStatus())) {
                        String documentType = purPurchaseOrder.getDocumentType();
                        if (sumYCK.compareTo(BigDecimal.ZERO) == 0) {
                            if (ConstantsEms.YES.equals(purPurchaseOrder.getIsReturnGoods())) {
                                li.setInOutStockStatus(ConstantsEms.OUT_STORE_STATUS_NOT);
                            } else {
                                li.setInOutStockStatus(ConstantsEms.IN_STORE_STATUS_NOT);
                            }
                        } else if (li.getNewQuantity().compareTo(sumYCK) == 1) {
                            if (ConstantsEms.YES.equals(purPurchaseOrder.getIsReturnGoods())) {
                                li.setInOutStockStatus(ConstantsEms.OUT_STORE_STATUS_LI);
                            } else {
                                li.setInOutStockStatus(ConstantsEms.IN_STORE_STATUS_LI);
                            }
                        } else {
                            if (ConstantsEms.YES.equals(purPurchaseOrder.getIsReturnGoods())) {
                                li.setInOutStockStatus(ConstantsEms.OUT_STORE_STATUS);
                            } else {
                                li.setInOutStockStatus(ConstantsEms.IN_STORE_STATUS);
                            }
                        }
                    }
                }

            });
        }
        if (ConstantsEms.CHECK_STATUS.equals(purPurchaseOrder.getHandleStatus())) {
            //确认后对新值操作
            changeValue(purPurchaseOrder);
        }
    }

    //改变主表处理状态
    public void changeInvoutStatus(PurPurchaseOrder o) {
        List<PurPurchaseOrderItem> purPurchaseOrderItemList = o.getPurPurchaseOrderItemList();
        if (CollectionUtil.isNotEmpty(purPurchaseOrderItemList)) {
            String isReturnGoods = o.getIsReturnGoods();
            if (ConstantsEms.NO.equals(isReturnGoods)) {
                //部分出库
                List<PurPurchaseOrderItem> littleQuatial = purPurchaseOrderItemList.stream().filter(m -> m.getInOutStockStatus().equals(ConstantsEms.IN_STORE_STATUS_LI)).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(littleQuatial)) {
                    o.setInOutStockStatus(ConstantsEms.IN_STORE_STATUS_LI);
                } else {
                    List<PurPurchaseOrderItem> allQuatial = purPurchaseOrderItemList.stream().filter(m -> m.getInOutStockStatus().equals(ConstantsEms.IN_STORE_STATUS)).collect(Collectors.toList());
                    if (CollectionUtil.isNotEmpty(allQuatial)) {
                        if (allQuatial.size() == purPurchaseOrderItemList.size()) {
                            o.setInOutStockStatus(ConstantsEms.IN_STORE_STATUS);
                        } else {
                            o.setInOutStockStatus(ConstantsEms.IN_STORE_STATUS_LI);
                        }
                    } else {
                        o.setInOutStockStatus(ConstantsEms.IN_STORE_STATUS_NOT);
                    }
                }
            } else {
                //部分入库
                List<PurPurchaseOrderItem> littleQuatial = purPurchaseOrderItemList.stream().filter(m -> m.getInOutStockStatus().equals(ConstantsEms.OUT_STORE_STATUS_LI)).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(littleQuatial)) {
                    o.setInOutStockStatus(ConstantsEms.OUT_STORE_STATUS_LI);
                } else {
                    List<PurPurchaseOrderItem> allQuatial = purPurchaseOrderItemList.stream().filter(m -> m.getInOutStockStatus().equals(ConstantsEms.OUT_STORE_STATUS)).collect(Collectors.toList());
                    if (CollectionUtil.isNotEmpty(allQuatial)) {
                        if (allQuatial.size() == purPurchaseOrderItemList.size()) {
                            o.setInOutStockStatus(ConstantsEms.OUT_STORE_STATUS);
                        } else {
                            o.setInOutStockStatus(ConstantsEms.OUT_STORE_STATUS_LI);
                        }
                    } else {
                        o.setInOutStockStatus(ConstantsEms.OUT_STORE_STATUS_NOT);
                    }
                }
            }
        }
    }

    /**
     * 采购订单确认时获取 采购价
     */
    public void getPurchase(PurPurchaseOrder purPurchaseOrder) {
        String advanceSettleMode = null;
        String documentType = purPurchaseOrder.getDocumentType();
        String handleStatus = purPurchaseOrder.getHandleStatus();
        Long purchaseContractSid = purPurchaseOrder.getPurchaseContractSid();
        if (purchaseContractSid != null) {
            PurPurchaseContract purPurchaseContract = purPurchaseContractMapper.selectById(purchaseContractSid);
            advanceSettleMode = purPurchaseContract.getAdvanceSettleMode();
        }
        String mode = advanceSettleMode;
        List<PurPurchaseOrderItem> list = purPurchaseOrder.getPurPurchaseOrderItemList();
        if (CollectionUtils.isNotEmpty(list)) {
            if (!HandleStatus.SUBMIT.getCode().equals(purPurchaseOrder.getHandleStatus())) {
                //获取最新采购价
                getNewPrice(purPurchaseOrder, list, advanceSettleMode);
            }
                 /*
                 //不进行一下校验
                 if(ConstantsEms.YES.equals(purPurchaseOrder.getIsSkipJudge())){
                     return;
                 }
                 if(ConstantsEms.CHECK_STATUS.equals(purPurchaseOrder.getHandleStatus())||HandleStatus.SUBMIT.getCode().equals(purPurchaseOrder.getHandleStatus())){
                     if(ADVANCE_SETTLE_MODE_DD.equals(advanceSettleMode)) {
                         if(!exit(documentType)){
                             list.forEach(li -> {
                                 if (li.getPurchasePriceTax() == null&&!ConstantsEms.YES.equals(li.getFreeFlag())) {
                                     throw new CustomException("物料/商品编码" + li.getMaterialCode() + "，采购价未维护，请至采购价页面进行维护");
                                 }
                             });
                         }
                     }
                 }
                  */
        }
        List<PurPurchaseOrderItem> items = list.stream().filter(li -> li.getTaxRate() != null).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(items) && items.size() > 1) {
            for (int i = 0; i < items.size() - 1; i++) {
                if (!items.get(i).getTaxRate().toString().equals(items.get(i + 1).getTaxRate().toString())) {
                    throw new CustomException("存在税率不一致的明细行，请检查！");
                }

            }
        }
    }

    public void getNewPrice(PurPurchaseOrder purPurchaseOrder, List<PurPurchaseOrderItem> list, String advanceSettleMode) {
        String documentType = purPurchaseOrder.getDocumentType();
        ConBuTypePurchaseOrder conBuTypePurchaseOrder = conBuTypePurchaseOrderMapper.selectOne(new QueryWrapper<ConBuTypePurchaseOrder>().lambda()
                .eq(ConBuTypePurchaseOrder::getCode, purPurchaseOrder.getBusinessType())
        );
        PurPurchasePrice purPurchasePrice = new PurPurchasePrice();
        BeanCopyUtils.copyProperties(purPurchaseOrder, purPurchasePrice);
        //默认获取通用税率
        ConTaxRate taxRate = conTaxRateMapper.selectOne(new QueryWrapper<ConTaxRate>().lambda()
                .eq(ConTaxRate::getIsDefault, "Y")
        );
        if (taxRate == null) {
            taxRate = new ConTaxRate();
        }
        ConTaxRate finalTaxRate = taxRate;
        List<ConMeasureUnit> unitList = conMeasureUnitMapper.selectList(new QueryWrapper<>());
        Map<String, String> unitNameMap = unitList.stream().collect(Collectors.toMap(ConMeasureUnit::getCode, ConMeasureUnit::getName,
                (value1, value2) -> {
                    return value2;
                }));
        if (CollectionUtil.isNotEmpty(list)) {
            list.forEach(item -> {
                item.setSystemTaxRate(finalTaxRate.getTaxRateValue());
                if (item.getPurchasePriceTax() == null || (ConstantsEms.NO.equals(conBuTypePurchaseOrder.getIsEditPrice())
                        && ConstantsEms.NO.equals(purPurchaseOrder.getIsConsignmentSettle())
                        && ConstantsEms.NO.equals(purPurchaseOrder.getIsReturnGoods()))
                ) {
                    BigDecimal zipperPurchase = null;
                    Long vendorSid = purPurchaseOrder.getVendorSid();
                    BeanCopyUtils.copyProperties(item, purPurchasePrice);
                    purPurchasePrice.setPriceDimension(ConstantsEms.PRICE_K1);
                    purPurchasePrice.setVendorSid(vendorSid);
                    //获取有效期内的采购价
                    Long materialSid = purPurchasePrice.getMaterialSid();
                    BasMaterial basMaterial = basMaterialMapper.selectById(materialSid);
                    String zipperFlag = basMaterial.getZipperFlag();
                    PurPurchasePriceItem newPurchase = priceService.getNewPurchase(purPurchasePrice);
                    if (newPurchase.getPurchasePriceTax() != null) {
                        item.setPurchasePriceTax(newPurchase.getPurchasePriceTax());
                        item.setUnitBase(newPurchase.getUnitBase())
                                .setUnitBaseName(unitNameMap.get(newPurchase.getUnitBase()))
                                .setUnitConversionRate(newPurchase.getUnitConversionRate())
                                .setUnitPriceName(unitNameMap.get(newPurchase.getUnitPrice()))
                                .setUnitPrice(newPurchase.getUnitPrice());
                        item.setPurchasePrice(newPurchase.getPurchasePriceTax().divide(BigDecimal.ONE.add(newPurchase.getTaxRate()), 6, RoundingMode.HALF_UP));
                        item.setTaxRate(newPurchase.getTaxRate());
                        if (item.getTaxRate() == null || ConstantsEms.NO.equals(conBuTypePurchaseOrder.getIsOncePurchase())) {
                            item.setTaxRate(newPurchase.getTaxRate());
                        }
                        if (ConstantsEms.YES.equals(purPurchaseOrder.getIsConsignmentSettle()) || ConstantsEms.YES.equals(purPurchaseOrder.getIsReturnGoods())) {
                            item.setReturnPtin(newPurchase.getPurchasePriceTax());
                        }
                    } else {
                        /**
                         * 以下5种情况，获取采购价时，若未获取到采购价，则”税率“取值当前系统的默认税率，
                         * “采购价单位、基本计量单位“默认等于对应商品档案的”基本计量单位”，“单位换算比例(销售价单位/基本单位)”默认等于1
                         * 1》勾选“免费”的明细行
                         * 2》订单的单据类型为“备料通知单”（键值：BLTZD）
                         * 3》订单的“业务类型”的”是否允许编辑价格“的值为“是”
                         * 4》订单的“业务类型”的“是否一次性销售”的值为“是”
                         * 5》订单引用的合同的”预付款结算方式“为”按合同“
                         */
                       // if (exit(documentType) || ConstantsEms.YES.equals(conBuTypePurchaseOrder.getIsEditPrice()) || ConstantsEms.YES.equals(item.getFreeFlag()) || ConstantsEms.ADVANCE_SETTLE_MODE_HT.equals(advanceSettleMode) || ConstantsEms.YES.equals(conBuTypePurchaseOrder.getIsOncePurchase())) {//备料通知单
                            item.setUnitBase(basMaterial.getUnitBase());
                            item.setUnitBaseName(unitNameMap.get(basMaterial.getUnitBase()));
                            item.setUnitPrice(basMaterial.getUnitBase());
                            item.setUnitPriceName(unitNameMap.get(basMaterial.getUnitBase()));
                            item.setUnitConversionRate(BigDecimal.ONE);
                            if (item.getTaxRate() == null) {
                                item.setTaxRate(finalTaxRate.getTaxRateValue());
                            }
                      //  }
                    }
                } else {
                    if (item.getUnitBase() == null) {
                        BasMaterial basMaterial = basMaterialMapper.selectById(item.getMaterialSid());
                     //   if (exit(documentType) || ConstantsEms.YES.equals(conBuTypePurchaseOrder.getIsEditPrice()) || ConstantsEms.YES.equals(item.getFreeFlag()) || ConstantsEms.YES.equals(conBuTypePurchaseOrder.getIsOncePurchase()) || ConstantsEms.ADVANCE_SETTLE_MODE_HT.equals(advanceSettleMode)) {//备料通知单
                            item.setUnitBase(basMaterial.getUnitBase());
                            item.setUnitBaseName(unitNameMap.get(basMaterial.getUnitBase()));
                            item.setUnitPrice(basMaterial.getUnitBase());
                            item.setUnitPriceName(unitNameMap.get(basMaterial.getUnitBase()));
                            item.setUnitConversionRate(BigDecimal.ONE);
                            if (item.getTaxRate() == null) {
                                item.setTaxRate(finalTaxRate.getTaxRateValue());
                            }
                     //   }
                    }
                }
            });
        }
    }

    //设置到期天数
    @Override
    public int setToexpireDays(OrderItemToexpireRequest quest) {
        int row = purPurchaseOrderItemMapper.update(new PurPurchaseOrderItem(), new UpdateWrapper<PurPurchaseOrderItem>().lambda()
                .in(PurPurchaseOrderItem::getPurchaseOrderItemSid, quest.getSidList())
                .set(PurPurchaseOrderItem::getToexpireDays, quest.getToexpireDays())
        );
        return row;
    }

    /**
     * 采购订单变更
     */
    @Override
    public int change(PurPurchaseOrder purPurchaseOrder) {
        PurPurchaseOrder old = purPurchaseOrderMapper.selectById(purPurchaseOrder.getPurchaseOrderSid());

        boolean buTypeChange = false;
        if (purPurchaseOrder.getBusinessType() != null && !purPurchaseOrder.getBusinessType().equals(old.getBusinessType())) {
            ConBuTypePurchaseOrder buType = conBuTypePurchaseOrderMapper.selectOne(new QueryWrapper<ConBuTypePurchaseOrder>()
                    .lambda().eq(ConBuTypePurchaseOrder::getCode, purPurchaseOrder.getBusinessType()));
            if (buType == null) {
                throw new BaseException("业务类型不存在");
            }
            purPurchaseOrder.setYutouAnxuType(buType.getYutouAnxuType())
                    .setDeliveryType(buType.getDeliveryType()).setInventoryControlMode(buType.getInventoryControlMode());
            // 单据类型与业务类型
            ConDocBuTypeGroupPo so = conDocBuTypeGroupPoMapper.selectOne(new QueryWrapper<ConDocBuTypeGroupPo>().lambda()
                    .eq(ConDocBuTypeGroupPo::getDocTypeCode, purPurchaseOrder.getDocumentType())
                    .eq(ConDocBuTypeGroupPo::getBuTypeCode, purPurchaseOrder.getBusinessType()));
            if (so != null) {
                purPurchaseOrder.setIsNonApproval(so.getIsNonApproval());
            }
            buTypeChange = true;
        } else if (purPurchaseOrder.getBusinessType() == null) {
            throw new BaseException("业务类型不能为空");
        }
        String isNonApproval = purPurchaseOrder.getIsNonApproval();
        if (ConstantsEms.YES.equals(isNonApproval)) {
            purPurchaseOrder.setHandleStatus(ConstantsEms.CHECK_STATUS);
        } else {
            getPurchase(purPurchaseOrder);
        }
        Long vendorSid = purPurchaseOrder.getVendorSid();
        String vendorGroup = basVendorMapper.selectById(vendorSid).getVendorGroup();
        if (YCX_VENDOR.equals(vendorGroup)) {
            if (purPurchaseOrder.getVendorNameRemark() == null) {
                throw new CustomException("供应商名称备注,不能为空");
            }
        }
        Long purchaseOrderSid = purPurchaseOrder.getPurchaseOrderSid();
        judgeNull(purPurchaseOrder);
        if (ConstantsEms.CHECK_STATUS.equals(purPurchaseOrder.getHandleStatus())) {
            //报表变更
//            changeYF(purPurchaseOrder);
            setConfirmInfo(purPurchaseOrder);
        }
        changeInvoutStatus(purPurchaseOrder);
        int row = purPurchaseOrderMapper.updateAllById(purPurchaseOrder);
        if (HandleStatus.CONFIRMED.getCode().equals(purPurchaseOrder.getHandleStatus()) && StringUtils.isEmpty(purPurchaseOrder.getSpecialBusCategory())) {
            advancesReceived(purPurchaseOrder);
        }
        //采购订单-明细对象
        List<PurPurchaseOrderItem> purPurchaseOrderItemList = purPurchaseOrder.getPurPurchaseOrderItemList();
        if (CollectionUtils.isNotEmpty(purPurchaseOrderItemList)) {
            updatePurchasePrice(purPurchaseOrderItemList);
            setItemValue(purPurchaseOrder, purPurchaseOrderItemList);
            List<PurPurchaseOrderItem> purPurchasePriceItems = purPurchaseOrderItemMapper.selectList(new QueryWrapper<PurPurchaseOrderItem>().lambda()
                    .eq(PurPurchaseOrderItem::getPurchaseOrderSid, purPurchaseOrder.getPurchaseOrderSid())
            );
            List<Long> longs = purPurchasePriceItems.stream().map(li -> li.getPurchaseOrderItemSid()).collect(Collectors.toList());
            List<Long> longsNow = purPurchaseOrderItemList.stream().map(li -> li.getPurchaseOrderItemSid()).collect(Collectors.toList());
            //两个集合取差集
            List<Long> reduce = longs.stream().filter(item -> !longsNow.contains(item)).collect(Collectors.toList());
            //删除明细
            if (CollectionUtil.isNotEmpty(reduce)) {
                purPurchaseOrderItemMapper.deleteBatchIds(reduce);
                deliveryPlanMapper.delete(new QueryWrapper<PurPurchaseOrderDeliveryPlan>().lambda()
                        .in(PurPurchaseOrderDeliveryPlan::getPurchaseOrderItemSid, reduce));
                purPurchaseOrderMaterialProductMapper.delete(new QueryWrapper<PurPurchaseOrderMaterialProduct>().lambda()
                        .in(PurPurchaseOrderMaterialProduct::getMaterialPurchaseOrderItemSid, reduce)
                );
                // 删除数据来源记录表
                purchaseOrderDataSourceMapper.delete(new QueryWrapper<PurPurchaseOrderDataSource>().lambda()
                        .in(PurPurchaseOrderDataSource::getPurchaseOrderItemSid, reduce));
            }
            //修改明细
            List<PurPurchaseOrderItem> exitItem = purPurchaseOrderItemList.stream().filter(li -> li.getPurchaseOrderItemSid() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(exitItem)) {
                exitItem.forEach(li -> {
                    purPurchaseOrderItemMapper.updateAllById(li);
                    List<PurPurchaseOrderDeliveryPlan> deliveryPlanList = li.getDeliveryPlanList();
                    deliveryPlanMapper.delete(new QueryWrapper<PurPurchaseOrderDeliveryPlan>().lambda()
                            .eq(PurPurchaseOrderDeliveryPlan::getPurchaseOrderItemSid, li.getPurchaseOrderItemSid()));
                    purPurchaseOrderMaterialProductMapper.delete(new QueryWrapper<PurPurchaseOrderMaterialProduct>().lambda()
                            .eq(PurPurchaseOrderMaterialProduct::getMaterialPurchaseOrderItemSid, li.getPurchaseOrderItemSid()));
                    //采购交货计划
                    if (CollectionUtil.isNotEmpty(deliveryPlanList)) {
                        BigDecimal sum = deliveryPlanList.stream().map(h -> h.getPlanQuantity()).reduce(BigDecimal.ZERO, BigDecimal::add);
                        if (sum.compareTo(li.getQuantity()) == 1) {
                            throw new CustomException("第" + li.getItemNum() + "行，订单量小于计划发货量总和，请核实！");
                        }
                        deliveryPlanList.forEach(i -> {
                            i.setPurchaseOrderItemSid(li.getPurchaseOrderItemSid())
                                    .setPurchaseOrderSid(li.getPurchaseOrderSid());
                        });
                        deliveryPlanMapper.inserts(deliveryPlanList);
                    }
                    List<PurPurchaseOrderMaterialProduct> productList = li.getMaterialProductList();
                    if (CollectionUtils.isNotEmpty(productList)) {
                        BigDecimal sum = productList.stream().map(m -> m.getQuantityMaterial()).reduce(BigDecimal.ZERO, BigDecimal::add);
                        if (sum.compareTo(li.getQuantity()) == 1) {
                            throw new BaseException("第" + li.getItemNum() + "行，“所用商品信息”页签中，面辅料数量总和不能大于订单量，请核实！");
                        }
                        productList.forEach(i -> {
                            i.setMaterialPurchaseOrderItemSid(li.getPurchaseOrderItemSid())
                                    .setMaterialPurchaseOrderSid(li.getPurchaseOrderSid());
                        });
                        purPurchaseOrderMaterialProductMapper.inserts(productList);
                    }
                });
            }
            //新增明细
            List<PurPurchaseOrderItem> nullItem = purPurchaseOrderItemList.stream().filter(li -> li.getPurchaseOrderItemSid() == null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(nullItem)) {
                int max = purPurchasePriceItems.stream().mapToInt(li -> li.getItemNum()).max().getAsInt();
                for (int i = 0; i < nullItem.size(); i++) {
                    int maxItem = max + i + 1;
                    nullItem.get(i).setItemNum(maxItem);
                    nullItem.get(i).setPurchaseOrderSid(purPurchaseOrder.getPurchaseOrderSid());
                    purPurchaseOrderItemMapper.insert(nullItem.get(i));
                    List<PurPurchaseOrderDeliveryPlan> deliveryPlanList = nullItem.get(i).getDeliveryPlanList();
                    //销售发货计划
                    if (CollectionUtil.isNotEmpty(deliveryPlanList)) {
                        BigDecimal sum = deliveryPlanList.stream().map(h -> h.getPlanQuantity()).reduce(BigDecimal.ZERO, BigDecimal::add);
                        if (sum.compareTo(nullItem.get(i).getQuantity()) == 1) {
                            throw new CustomException("第" + nullItem.get(i).getItemNum() + "行，订单量小于计划发货量总和，请核实！");
                        }
                        for (PurPurchaseOrderDeliveryPlan deliveryPlan : deliveryPlanList) {
                            deliveryPlan.setPurchaseOrderItemSid(nullItem.get(i).getPurchaseOrderItemSid())
                                    .setPurchaseOrderSid(nullItem.get(i).getPurchaseOrderSid());
                        }
                        deliveryPlanMapper.inserts(deliveryPlanList);
                    }
                    List<PurPurchaseOrderMaterialProduct> productList = nullItem.get(i).getMaterialProductList();
                    if (CollectionUtils.isNotEmpty(productList)) {
                        BigDecimal sum = productList.stream().map(m -> m.getQuantityMaterial()).reduce(BigDecimal.ZERO, BigDecimal::add);
                        if (sum.compareTo(nullItem.get(i).getQuantity()) == 1) {
                            throw new BaseException("第" + nullItem.get(i).getItemNum() + "行，“所用商品信息”页签中，面辅料数量总和不能大于订单量，请核实！");
                        }
                        for (PurPurchaseOrderMaterialProduct product : productList) {
                            product.setMaterialPurchaseOrderItemSid(nullItem.get(i).getPurchaseOrderItemSid())
                                    .setMaterialPurchaseOrderSid(nullItem.get(i).getPurchaseOrderSid());
                        }
                        purPurchaseOrderMaterialProductMapper.inserts(productList);
                    }
                }
            }
        }
        //采购订单-附件对象
        List<PurPurchaseOrderAttachment> purPurchaseOrderAttachmentList = purPurchaseOrder.getAttachmentList();
        purPurchaseOrderAttachmentMapper.delete(new UpdateWrapper<PurPurchaseOrderAttachment>().lambda()
                .eq(PurPurchaseOrderAttachment::getPurchaseOrderSid, purPurchaseOrder.getPurchaseOrderSid()));
        if (CollectionUtils.isNotEmpty(purPurchaseOrderAttachmentList)) {
            addPurPurchaseOrderAttachment(purPurchaseOrder, purPurchaseOrderAttachmentList);
        }
        //更新通知
        if (ConstantsEms.CHECK_STATUS.equals(purPurchaseOrder.getHandleStatus())) {
            SysBusinessBcst sysBusinessBcst = new SysBusinessBcst();
            String shortName = basVendorMapper.selectById(purPurchaseOrder.getVendorSid()).getShortName();
            sysBusinessBcst.setTitle(shortName + "，采购订单" + purPurchaseOrder.getPurchaseOrderCode() + "的信息发生变更，请知悉！")
                    .setDocumentSid(purPurchaseOrder.getPurchaseOrderSid())
                    .setDocumentCode(purPurchaseOrder.getPurchaseOrderCode().toString())
                    .setNoticeDate(new Date()).setUserId(ApiThreadLocalUtil.get().getUserid());
            if (ConstantsEms.MATERIAL_CATEGORY_WL.equals(purPurchaseOrder.getMaterialCategory())) {
                sysBusinessBcst.setMenuId(ConstantsWorkbench.purchase_order_wl);
            } else if (ConstantsEms.MATERIAL_CATEGORY_SP.equals(purPurchaseOrder.getMaterialCategory())) {
                sysBusinessBcst.setMenuId(ConstantsWorkbench.purchase_order_sp);
            }
            sysBusinessBcstMapper.insert(sysBusinessBcst);
        }
        if (!ConstantsEms.YES.equals(isNonApproval)) {
            //审批
            if (ConstantsEms.SUBMIT_STATUS.equals(purPurchaseOrder.getHandleStatus())) {
                Submit submit = new Submit();
                submit.setStartUserId(ApiThreadLocalUtil.get().getUserid().toString());
                submit.setFormType(FormType.CGDD_BG.getCode());
                List<FormParameter> list = new ArrayList();
                FormParameter formParameter = new FormParameter();
                formParameter.setParentId(purPurchaseOrder.getPurchaseOrderSid().toString());
                formParameter.setFormId(purPurchaseOrder.getPurchaseOrderSid().toString());
                formParameter.setFormCode(purPurchaseOrder.getPurchaseOrderCode().toString());
                list.add(formParameter);
                submit.setFormParameters(list);
                workflowService.change(submit);
            }
        }
        if (ConstantsEms.CHECK_STATUS.equals(purPurchaseOrder.getHandleStatus())) {
            // 明细的数据来源
            try {
                Long[] orderItemSids = purPurchaseOrder.getPurPurchaseOrderItemList().stream().map(PurPurchaseOrderItem::getPurchaseOrderItemSid).toArray(Long[]::new);
                List<PurPurchaseOrderDataSource> sourceList = purchaseOrderDataSourceMapper.selectList(new QueryWrapper<PurPurchaseOrderDataSource>()
                        .lambda().in(PurPurchaseOrderDataSource::getPurchaseOrderItemSid, orderItemSids));
                if (CollectionUtil.isNotEmpty(sourceList)) {
                    sourceList.forEach(item -> {
                        if (item.getNewQuantity() != null) {
                            BigDecimal newQuantity = new BigDecimal(String.valueOf(item.getNewQuantity()));
                            item.setQuantity(newQuantity);
                            item.setNewQuantity(null);
                        }
                    });
                    purchaseOrderDataSourceMapper.updatesAllById(sourceList);
                }
            } catch (Exception e) {
                log.error("确认采购订单时更新明细数据来源已下单了出现错误，请检查！");
            }
        }
        // 判断 新旧合同 的 特殊用途是否 是 临时过渡, 删除该待办
        if (old.getPurchaseContractSid() != null && !old.getPurchaseContractSid().equals(purPurchaseOrder.getPurchaseContractSid())) {
            List<Long> contractSidList = new ArrayList<Long>() {{
                add(old.getPurchaseContractSid());
            }};
            if (purPurchaseOrder.getPurchaseContractSid() != null) {
                contractSidList.add(purPurchaseOrder.getPurchaseContractSid());
            }
            List<PurPurchaseContract> purchaseContractList = purPurchaseContractMapper.selectList(new QueryWrapper<PurPurchaseContract>().lambda()
                    .in(PurPurchaseContract::getPurchaseContractSid, contractSidList));
            if (CollectionUtil.isNotEmpty(purchaseContractList)) {
                Map<Long, PurPurchaseContract> map = purchaseContractList.stream().collect(Collectors.toMap(PurPurchaseContract::getPurchaseContractSid, Function.identity()));
                if (map.containsKey(old.getPurchaseContractSid()) && ConstantsOrder.CONTRACT_PURPOSE_LSGD.equals(map.get(old.getPurchaseContractSid()).getContractPurpose())) {
                    if (!map.containsKey(purPurchaseOrder.getPurchaseContractSid()) || !ConstantsOrder.CONTRACT_PURPOSE_LSGD.equals(map.get(purPurchaseOrder.getPurchaseContractSid()).getContractPurpose())) {
                        sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
                                .eq(SysTodoTask::getDocumentSid, purPurchaseOrder.getPurchaseOrderSid()).eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB)
                                .eq(SysTodoTask::getTableName, ConstantsTable.TABLE_PURCHASE_ORDER + "-" + ConstantsTable.TABLE_PURCHASE_CONTRACT)
                                .like(SysTodoTask::getTitle, "使用的是过渡合同"));
                    }
                }
            }
        }
        if (row > 0) {
            // 操作日志详情记录
            String remark = "";
            if (buTypeChange) {
                List<ConBuTypePurchaseOrder> listType = conBuTypePurchaseOrderMapper.selectList(new QueryWrapper<>());
                Map<String, String> map = listType.stream().collect(Collectors.toMap(ConBuTypePurchaseOrder::getCode, ConBuTypePurchaseOrder::getName, (key1, key2) -> key2));
                String oldOne = map.containsKey(old.getBusinessType()) ? map.get(old.getBusinessType()) : "";
                String newOne = map.containsKey(purPurchaseOrder.getBusinessType()) ? map.get(purPurchaseOrder.getBusinessType()) : "";
                remark = remark + "业务类型字段变更，更新前：" + oldOne + "，更新后：" + newOne;
            }
            //插入日志
            MongodbUtil.insertUserLog(purPurchaseOrder.getPurchaseOrderSid(), BusinessType.CHANGE.getValue(), null, TITLE, remark);
        }
        return 1;
    }

    public void changeYF(PurPurchaseOrder o) {
        List<PurPurchaseOrderItem> purPurchaseOrderItemList = o.getPurPurchaseOrderItemList();
        if (CollectionUtil.isNotEmpty(purPurchaseOrderItemList)) {
            purPurchaseOrderItemList.forEach(li -> {
                if (li.getNewPurchasePriceTax() != null) {
                    List<FinBookPaymentEstimationItem> finBookReceiptEstimationItems = finBookPaymentEstimationMapper.selectList(new QueryWrapper<FinBookPaymentEstimationItem>().lambda()
                            .eq(FinBookPaymentEstimationItem::getPurchaseOrderSid, o.getPurchaseOrderSid())
                            .eq(FinBookPaymentEstimationItem::getItemNum, li.getItemNum())
                    );
                    finBookReceiptEstimationItems.forEach(m -> {
                        m.setCurrencyAmountTax(m.getQuantity().multiply(li.getNewPurchasePriceTax()));
                        finBookPaymentEstimationMapper.updateById(m);
                    });
                    finBookPaymentEstimationMapper.update(new FinBookPaymentEstimationItem(), new UpdateWrapper<FinBookPaymentEstimationItem>().lambda()
                            .eq(FinBookPaymentEstimationItem::getPurchaseOrderSid, o.getPurchaseOrderSid())
                            .eq(FinBookPaymentEstimationItem::getItemNum, li.getItemNum())
                            .set(FinBookPaymentEstimationItem::getPrice, li.getNewPurchasePriceTax().divide(BigDecimal.ONE.add(li.getTaxRate()), 6, BigDecimal.ROUND_HALF_UP))
                            .set(FinBookPaymentEstimationItem::getPriceTax, li.getNewPurchasePriceTax())
                    );
                }
            });
        }
    }

    /**
     * 提交时校验
     */
    @Override
    public int checkProcess(Long purPurchaseOrderSid) {
        PurPurchaseOrder purPurchaseOrder = selectPurPurchaseOrderById(purPurchaseOrderSid);
        purPurchaseOrder.setHandleStatus(HandleStatus.SUBMIT.getCode());
        List<PurPurchaseOrderItem> list = purPurchaseOrder.getPurPurchaseOrderItemList();
        if (CollectionUtils.isEmpty(list)) {
            throw new CustomException("提交时，明细行不允许为空");
        }
        if (!ConstantsEms.NO.equals(ApiThreadLocalUtil.get().getSysUser().getClient().getIsRequiredPurchaseOrderContract()) &&
                StrUtil.isBlank(purPurchaseOrder.getPurchaseContractCode())) {
            throw new CustomException("提交时，采购合同号不允许为空");
        }
        if (!ConstantsEms.VENDOR_SPECIAL_BUS_CATEGORY.equals(purPurchaseOrder.getSpecialBusCategory())) {
            list.forEach(li -> {
                if (li.getContractDate() == null) {
                    throw new CustomException("提交时，明细行的合同交期不能为空");
                }
            });
        }
        if (!ConstantsOrder.CONTRACT_ENTER_MODE_SG.equals(ApiThreadLocalUtil.get().getSysUser().getClient().getPurchaseOrderContractEnterMode())) {
            List<PurPurchaseContract> contract = purPurchaseContractMapper.selectList(new QueryWrapper<PurPurchaseContract>().lambda()
                    .eq(PurPurchaseContract::getPurchaseContractSid, purPurchaseOrder.getPurchaseContractSid()));
            if (CollectionUtil.isEmpty(contract)) {
                throw new CustomException("采购合同号不存在");
            }
        }
        List<Long> longs = list.stream().map(li -> li.getBarcodeSid()).collect(Collectors.toList());
        List<BasMaterialBarcode> basMaterialBarcodes = basMaterialBarcodeMapper.selectList(new QueryWrapper<BasMaterialBarcode>().lambda()
                .in(BasMaterialBarcode::getBarcodeSid, longs)
        );
        basMaterialBarcodes.forEach(m -> {
            if (!m.getStatus().equals(ConstantsEms.SAVA_STATUS)) {
                throw new CustomException(purPurchaseOrder.getPurchaseOrderCode() + "，存在停用的商品条码" + m.getBarcode() + "，请检查！");
            }
        });
        //采购价维护校验
        getPurchase(purPurchaseOrder);
        return 1;
    }

    //判断核销状态
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int judgeReceipt(PurPurchaseOrder purPurchaseOrder) {
        List<FinBookPaymentEstimationItem> finBookPaymentEstimationItems = finBookPaymentEstimationMapper.selectList(new QueryWrapper<FinBookPaymentEstimationItem>()
                .lambda().eq(FinBookPaymentEstimationItem::getPurchaseOrderSid, purPurchaseOrder.getPurchaseOrderSid())
                .in(FinBookPaymentEstimationItem::getItemNum, purPurchaseOrder.getItemNumList())
        );
        if (CollectionUtil.isNotEmpty(finBookPaymentEstimationItems)) {
            finBookPaymentEstimationItems.forEach(li -> {
                if (!li.getClearStatus().equals(ConstantsFinance.CLEAR_STATUS_WHX)) {
                    throw new CustomException("第" + li.getItemNum() + "行，对应的应付暂估流水非“未核销状态”，不允许更新采购价");
                }
            });
        }
        return 1;
    }

    //多笔提交
    @Override
    public AjaxResult checkListFree(List<Long> sids) {
        for (Long sid : sids) {
            PurPurchaseOrderItem purPurchaseOrderItem = new PurPurchaseOrderItem();
            List<PurPurchaseOrderItem> itemList = purPurchaseOrderItemMapper.getItemList(purPurchaseOrderItem.setPurchaseOrderSid(sid));
            for (PurPurchaseOrderItem order : itemList) {
                if (ConstantsEms.YES.equals(order.getFreeFlag())) {
                    return AjaxResult.success("500", order.getPurchaseOrderCode());
                }
            }
        }
        return AjaxResult.success("200", "1");
    }

    /**
     * 提交时校验
     */
    @Override
    public EmsResultEntity checkProcessList(OrderErrRequest request) {
        List<CommonErrMsgResponse> errList = new ArrayList<>();
        List<CommonErrMsgResponse> warnList = new ArrayList<>();
        for (Long sid : request.getSidList()) {
            PurPurchaseOrder purPurchaseOrder = selectPurPurchaseOrderById(sid);
            purPurchaseOrder.setHandleStatus(HandleStatus.SUBMIT.getCode());
            List<PurPurchaseOrderItem> list = purPurchaseOrder.getPurPurchaseOrderItemList();
            if (CollectionUtils.isEmpty(list)) {
                //throw new CustomException("提交时，明细行不允许为空");
                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                errMsgResponse.setCode(purPurchaseOrder.getPurchaseOrderCode());
                errMsgResponse.setMsg("明细行不允许为空");
                errList.add(errMsgResponse);
            } else {
                List<PurPurchaseOrderItem> fieldList = list.stream().filter(li -> li.getUnitBase() == null || li.getUnitPrice() == null
                        || li.getUnitConversionRate() == null || li.getTaxRate() == null).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(fieldList)) {
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setCode(purPurchaseOrder.getPurchaseOrderCode());
                    errMsgResponse.setMsg("存在明细行的基本单位、价格单位、单位换算比例或税率为空，请检查！");
                    errList.add(errMsgResponse);
                }
                List<Long> longs = list.stream().map(li -> li.getBarcodeSid()).collect(Collectors.toList());
                List<BasMaterialBarcode> basMaterialBarcodes = basMaterialBarcodeMapper.getBasMaterialSkuName(longs);
                for (BasMaterialBarcode m : basMaterialBarcodes) {
                    if (!m.getStatus().equals(ConstantsEms.SAVA_STATUS)) {
                        // throw new CustomException(purPurchaseOrder.getPurchaseOrderCode()+"，存在停用的商品条码"+m.getBarcode()+"，请检查！");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setCode(purPurchaseOrder.getPurchaseOrderCode());
                        String msg = null;
                        if (m.getSku2Name() != null) {
                            msg = "商品/物料编码" + m.getMaterialCode() + "，SKU1名称" + m.getSku1Name() + "，SKU2名称" + m.getSku2Name() + "，已被停用，请检查！";
                        } else {
                            msg = "商品/物料编码" + m.getMaterialCode() + "，SKU1名称" + m.getSku1Name() + "，已被停用，请检查！";
                        }
                        errMsgResponse.setMsg(msg);
                        errList.add(errMsgResponse);
                    }
                }
                if (!ConstantsEms.VENDOR_SPECIAL_BUS_CATEGORY.equals(purPurchaseOrder.getSpecialBusCategory())) {
                    List<PurPurchaseOrderItem> items = list.stream().filter(li -> li.getContractDate() == null).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(items)) {
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setCode(purPurchaseOrder.getPurchaseOrderCode());
                        errMsgResponse.setMsg("明细行的合同交期不能为空");
                        errList.add(errMsgResponse);
                    }
                } else {
                    list.forEach(li -> {
                        PurRecordVendorConsign purRecordVendorConsign = new PurRecordVendorConsign();
                        BeanCopyUtils.copyProperties(purPurchaseOrder, purRecordVendorConsign);
                        BeanCopyUtils.copyProperties(li, purRecordVendorConsign);
                        purRecordVendorConsign.setQuantity(li.getUnitConversionRate() != null ? li.getUnitConversionRate().multiply(li.getQuantity()) : li.getQuantity());
                        purRecordVendorConsign.setType(ConstantsEms.CHU_KU);
                        try {
                            purRecordVendorConsign.setIsSkipInsert(ConstantsEms.YES);
                            //盘盈 供应商寄售待结算台账 扣减
                            purRecordVendorConsignServiceImpl.insertPurRecordVendorConsign(purRecordVendorConsign);
                        } catch (CustomException e) {
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setCode(purPurchaseOrder.getPurchaseOrderCode());
                            errMsgResponse.setMsg(e.getMessage());
                            errList.add(errMsgResponse);
                        }
                    });
                }
                if (!ConstantsEms.NO.equals(ApiThreadLocalUtil.get().getSysUser().getClient().getIsRequiredPurchaseOrderContract())
                        && StrUtil.isBlank(purPurchaseOrder.getPurchaseContractCode())) {
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setCode(purPurchaseOrder.getPurchaseOrderCode());
                    errMsgResponse.setMsg("采购合同号不允许为空");
                    errList.add(errMsgResponse);
                }
                PurPurchaseContract contract = null;
                if (purPurchaseOrder.getPurchaseContractSid() != null && !ConstantsOrder.CONTRACT_ENTER_MODE_SG.equals(ApiThreadLocalUtil.get().getSysUser().getClient().getPurchaseOrderContractEnterMode())) {
                    contract = purPurchaseContractMapper.selectOne(new QueryWrapper<PurPurchaseContract>().lambda()
                            .eq(PurPurchaseContract::getPurchaseContractSid, purPurchaseOrder.getPurchaseContractSid())
                    );
                    if (contract == null) {
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setCode(purPurchaseOrder.getPurchaseOrderCode());
                        errMsgResponse.setMsg("采购合同号不存在");
                        errList.add(errMsgResponse);
                    } else {
                        if (!contract.getHandleStatus().equals(ConstantsEms.CHECK_STATUS)) {
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setCode(purPurchaseOrder.getPurchaseOrderCode());
                            errMsgResponse.setMsg("采购合同号不是已确认状态");
                            warnList.add(errMsgResponse);
                        }
                        // 判断合同的供应商 跟公司 跟订单有没有一致
                        if ((contract.getCompanySid() != null && !contract.getCompanySid().equals(purPurchaseOrder.getCompanySid())
                                || (contract.getCompanySid() == null && purPurchaseOrder.getCompanySid() != null))) {
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setCode(Long.valueOf(purPurchaseOrder.getPurchaseOrderCode()));
                            errMsgResponse.setMsg("所引用采购合同号的“供应商、公司”与订单中的“供应商、公司”不一致！");
                            errList.add(errMsgResponse);
                        } else if ((contract.getVendorSid() != null && !contract.getVendorSid().equals(purPurchaseOrder.getVendorSid())
                                || (contract.getVendorSid() == null && purPurchaseOrder.getVendorSid() != null))) {
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setCode(Long.valueOf(purPurchaseOrder.getPurchaseOrderCode()));
                            errMsgResponse.setMsg("所引用采购合同号的“供应商、公司”与订单中的“供应商、公司”不一致！");
                            errList.add(errMsgResponse);
                        }
                        String documentType = purPurchaseOrder.getDocumentType();
                        /*
                        //采购价维护校验
                        if (ConstantsEms.CHECK_STATUS.equals(purPurchaseOrder.getHandleStatus())
                            || HandleStatus.SUBMIT.getCode().equals(purPurchaseOrder.getImportHandle())
                            || HandleStatus.SUBMIT.getCode().equals(purPurchaseOrder.getHandleStatus())) {
                            String isConsignmentSettle = purPurchaseOrder.getIsConsignmentSettle();
                            if (ADVANCE_SETTLE_MODE_DD.equals(contract.getAdvanceSettleMode()) || ConstantsEms.YES.equals(isConsignmentSettle)) {
                                if (!exit(documentType)) {
                                    list.forEach(li -> {
                                        if (li.getPurchasePriceTax() == null && !ConstantsEms.YES.equals(li.getFreeFlag())) {
                                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                            errMsgResponse.setCode(purPurchaseOrder.getPurchaseOrderCode());
                                            errMsgResponse.setMsg("物料/商品编码" + li.getMaterialCode() + "，采购价未维护，请至采购价页面进行维护");
                                            errList.add(errMsgResponse);
                                        }
                                    });
                                }
                            }
                        }
                         */
                    }
                }
                if (CollectionUtil.isEmpty(errList)) {
                    List<PurPurchaseOrderItem> listFree = list.stream().filter(li -> ConstantsEms.YES.equals(li.getFreeFlag())).collect(Collectors.toList());
                    if (!ConstantsEms.YES.equals(request.getIsSkipFree())) {
                        if (CollectionUtils.isNotEmpty(listFree)) {
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setCode(purPurchaseOrder.getPurchaseOrderCode());
                            errMsgResponse.setMsg("包含免费的明细行");
                            warnList.add(errMsgResponse);
                        }
                    }
                    if (contract != null) {
                        String rawMaterialMode = contract.getRawMaterialMode();
                        if (rawMaterialMode != null) {
                            if (!ConstantsEms.YES.equals(request.getIsSkipRaw())) {
                                if (!rawMaterialMode.equals(purPurchaseOrder.getRawMaterialMode())) {
                                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                    errMsgResponse.setCode(purPurchaseOrder.getPurchaseOrderCode());
                                    errMsgResponse.setMsg("订单的”甲供料方式”与其引用采购合同" + purPurchaseOrder.getPurchaseContractCode() + "的“甲供料方式”不一致");
                                    warnList.add(errMsgResponse);
                                }
                            }
                        }
                    }
                    Long purchaseContractSid = purPurchaseOrder.getPurchaseContractSid();
                    if (purchaseContractSid != null) {
                        if (!ConstantsEms.YES.equals(request.getIsSkipConstract())) {
                            //合同金额校验
                            int i = judgeConstract(purPurchaseOrder);
                            if (i == -1) {
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setCode(purPurchaseOrder.getPurchaseOrderCode());
                                errMsgResponse.setMsg("采购合同号" + purPurchaseOrder.getPurchaseContractCode() + "的订单金额已超过合同金额");
                                warnList.add(errMsgResponse);
                            }
                        }
                    }
                }
            }
        }
        if (CollectionUtil.isNotEmpty(errList)) {
            return EmsResultEntity.error(errList);
        }
        if (CollectionUtil.isNotEmpty(warnList)) {
            return EmsResultEntity.warning(warnList);
        }
        return EmsResultEntity.success();
    }

    //明细报表跳转入库
    @Override
    public InvInventoryDocument exChange(List<PurPurchaseOrderItem> items) {
        Set<String> codes = items.stream().map(li -> li.getPurchaseOrderCode()).collect(Collectors.toSet());
        if (codes.size() > 1) {
            throw new CustomException("勾选的采购订单不符合入库条件，请核实");
        }
        Long purchaseOrderSid = items.get(0).getPurchaseOrderSid();
        PurPurchaseOrder order = purPurchaseOrderMapper.selectById(purchaseOrderSid);
        if (!ConstantsEms.CHECK_STATUS.equals(order.getHandleStatus())
                || !ConstantsEms.DELIEVER_type_DD.equals(order.getDeliveryType())
                || !ConstantsEms.NO.equals(order.getIsReturnGoods())
                || !ConstantsEms.INV_RESH.equals(order.getInventoryControlMode())
                || !ConstantsEms.NO.equals(order.getIsConsignmentSettle())
        ) {
            throw new CustomException("勾选的采购订单不符合入库条件，请核实");
        }
        InvInventoryDocument invInventoryDocument = new InvInventoryDocument();
        if (order != null) {
            Long sid = order.getPurchaseOrderSid();
            String shipmentCategory = order.getDeliveryType();
            String documentType = order.getDocumentType();
            if (DocCategory.PURCHAASE_JI_SHOU.getCode().equals(documentType) || DocCategory.PURCHAASE_JI_SHOU_RETURN.getCode().equals(documentType)) {
                invInventoryDocument.setSpecialStock(ConstantsEms.VEN_CU);
            }
            BeanCopyUtils.copyProperties(order, invInventoryDocument);
            invInventoryDocument.setReferDocCategory(DocCategory.PURCHASE_ORDER.getCode());
            invInventoryDocument.setReferDocumentCode(String.valueOf(order.getPurchaseOrderCode()));
            invInventoryDocument.setReferDocumentSid(order.getPurchaseOrderSid());
            invInventoryDocument.setPurchaseOrderCode(String.valueOf(order.getPurchaseOrderCode()));
            invInventoryDocument.setPurchaseContractSid(order.getPurchaseContractSid());
            invInventoryDocument.setSaleAndPurchaseDocument(order.getDocumentType());
            invInventoryDocument.setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
            invInventoryDocument.setHandleStatus(null);
            invInventoryDocument.setDocumentDate(new Date())
                    .setMovementType("SR01")
                    .setMovementTypeName("按采购订单")
                    .setCreateDate(null)
                    .setAccountDate(new Date());

            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(items)) {
                ArrayList<InvInventoryDocumentItem> invInventoryDocuments = new ArrayList<>();
                items.forEach(item -> {
                    if (ConstantsEms.YES.equals(item.getFreeFlag())) {
                        item.setPurchasePrice(BigDecimal.ZERO)
                                .setPurchasePriceTax(BigDecimal.ZERO);
                    }
                    InvInventoryDocumentItem invInventoryDocumentItem = new InvInventoryDocumentItem();
                    BeanCopyUtils.copyProperties(item, invInventoryDocumentItem);
                    if (item.getPurchasePriceTax() == null) {
                        invInventoryDocumentItem.setBusinessQuantity(item.getQuantity());
                    } else {
                        invInventoryDocumentItem.setInvPrice(item.getPurchasePrice());
                        invInventoryDocumentItem.setPrice(item.getPurchasePriceTax());
                        invInventoryDocumentItem.setInvPriceTax(item.getPurchasePriceTax());
                        invInventoryDocumentItem.setBusinessQuantity(item.getQuantity());
                    }
                    invInventoryDocumentItem.setReferDocumentItemNum(item.getItemNum());
                    invInventoryDocumentItem.setBarcode(item.getBarcode() != null ? item.getBarcode().toString() : null);
                    invInventoryDocumentItem.setReferDocumentSid(order.getPurchaseOrderSid());
                    invInventoryDocumentItem.setReferDocumentCode(String.valueOf(order.getPurchaseOrderSid()));
                    invInventoryDocumentItem.setReferDocumentItemSid(item.getPurchaseOrderItemSid());
                    if (item.getProductCodes() != null) {
                        invInventoryDocumentItem.setProductCodes(item.getProductCodes());
                    } else {
                        invInventoryDocumentItem.setProductCodes(item.getProductCode() == null ? null : item.getProductCode().toString());
                    }
                    invInventoryDocumentItem.setPriceQuantity(item.getQuantity());
                    invInventoryDocumentItem.setQuantity(null)
                            .setItemNum(null)
                            .setCreatorAccountName(null)
                            .setCreatorAccount(null)
                            .setCreateDate(null);
                    invInventoryDocuments.add(invInventoryDocumentItem);
                });
                invInventoryDocument.setInvInventoryDocumentItemList(invInventoryDocuments);
                invInventoryDocumentService.getQuantity(invInventoryDocument);
                ConMovementType conMovementType = conMovementTypeMapper.selectOne(new QueryWrapper<ConMovementType>().lambda()
                        .eq(ConMovementType::getCode, invInventoryDocument.getMovementType())
                );
                String referUnitType = conMovementType.getReferUnitType();
                invInventoryDocument.setType(ConstantsEms.RU_KU);
                invInventoryDocument.setDocumentCategory("RK");
                invInventoryDocument.setDocumentType("CG");
                invInventoryDocument.setReferUnitType(referUnitType);
            }
            invInventoryDocument.setBusinessType(null).setBusinessTypeName(null);
            return invInventoryDocument;
        }
        return null;
    }

    /**
     * 物料需求报表(商品采购订单)
     */
    @Override
    public PurPurchaseOrder getMaterialRequireListByCode(Long purchaseOrderCode) {
        PurPurchaseOrder purPurchaseOrder = purPurchaseOrderMapper.selectPurPurchaseOrderByCode(purchaseOrderCode);
        if (purPurchaseOrder == null) {
            throw new BaseException("采购订单号不存在，请重新输入");
        }
        PurPurchaseOrderItem purPurchaseOrderItem = new PurPurchaseOrderItem();
        purPurchaseOrderItem.setPurchaseOrderSid(purPurchaseOrder.getPurchaseOrderSid());
        List<PurPurchaseOrderItem> purPurchaseOrderItemList = purPurchaseOrderItemMapper.selectPurPurchaseOrderItemList(purPurchaseOrderItem);
        if (CollectionUtils.isEmpty(purPurchaseOrderItemList)) {
            throw new BaseException("该采购订单没有创建明细信息");
        }
        List<TecBomItem> list = new ArrayList<>();
        purPurchaseOrderItemList.stream().forEach(purchaseOrderItem -> {
            if (purchaseOrderItem.getMaterialSid() != null && purchaseOrderItem.getSku1Sid() != null) {
                TecBomHead tecBomHead = new TecBomHead();
                tecBomHead.setMaterialSid(purchaseOrderItem.getMaterialSid());
                tecBomHead.setSku1Sid(purchaseOrderItem.getSku1Sid());
                List<TecBomHead> tecBomHeadList = tecBomHeadMapper.selectTecBomHeadList(tecBomHead);
                //Bom明细列表
                List<TecBomItem> tecBomItemList = tecBomItemMapper.getMaterialRequireList(tecBomHeadList.get(0));
                if (CollectionUtils.isEmpty(tecBomItemList)) {
                    throw new BaseException("该商品没有创建BOM信息");
                }
                tecBomItemList.stream().forEach(bomItem -> {
                    if (bomItem.getConfirmQuantity() == null) {
                        throw new BaseException("BOM确认用量不能为空");
                    }
                    bomItem.setRequireQuantity(bomItem.getConfirmQuantity().multiply(purchaseOrderItem.getQuantity()));
                });
                list.addAll(tecBomItemList);
            }
        });
        purPurchaseOrder.setTecBomItemList(list);
        return purPurchaseOrder;
    }

    /**
     * 物料采购订单 导入
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult importDataM(MultipartFile file) {
        try {
            File toFile = null;
            try {
                toFile = FileUtils.multipartFileToFile(file);
            } catch (Exception e) {
                e.getMessage();
                throw new BaseException("文件转换失败");
            }
            ExcelReader reader = cn.hutool.poi.excel.ExcelUtil.getReader(toFile);
            FileUtils.delteTempFile(toFile);
            List<List<Object>> readAll = reader.read();
            int size = readAll.size();
            if (readAll.size() < 6) {
                throw new BaseException("明细行不能为空，导入失败");
            }
            //采购订单-单据类型
            Map<String, String> purchaseDocumentMaps = conDocTypePurchaseOrderMapper.getList().stream().collect(Collectors.toMap(ConDocTypePurchaseOrder::getName, ConDocTypePurchaseOrder::getCode, (key1, key2) -> key2));
            //采购订单-业务类型
            Map<String, String> purchaseTypeMaps = conBuTypePurchaseOrderMapper.getList().stream().collect(Collectors.toMap(ConBuTypePurchaseOrder::getName, ConBuTypePurchaseOrder::getCode, (key1, key2) -> key2));
            // 租户配置
            SysDefaultSettingClient settingClient = settingClientMapper.selectOne(new QueryWrapper<SysDefaultSettingClient>().lambda()
                    .eq(SysDefaultSettingClient::getClientId, ApiThreadLocalUtil.get().getClientId()));
            //客供料 方式
            sysDictDataService.deleteDictData("s_raw_material_mode");
            List<DictData> rawMaterialMode = sysDictDataService.selectDictData("s_raw_material_mode");
            Map<String, String> rawMaterialModeMaps = rawMaterialMode.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            //采购模式
            sysDictDataService.deleteDictData("s_price_type");
            List<DictData> priceType = sysDictDataService.selectDictData("s_price_type");
            Map<String, String> priceTypeMaps = priceType.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            //销售渠道
            Map<String, String> conSaleChannelMaps = conSaleChannelMapper.getConSaleChannelList().stream().collect(Collectors.toMap(ConSaleChannel::getCode, ConSaleChannel::getName, (key1, key2) -> key2));
            //物料类型
            Map<String, String> materailTypeMaps = conMaterialTypeMapper.getConMaterialTypeList().stream().collect(Collectors.toMap(ConMaterialType::getName, ConMaterialType::getCode, (key1, key2) -> key2));
            //采购组织
            Map<String, String> purchaseOrgMaps = conPurchaseOrgMapper.getConPurchaseOrgList().stream().collect(Collectors.toMap(ConPurchaseOrg::getCode, ConPurchaseOrg::getName, (key1, key2) -> key2));
            //采购组
            Map<String, String> purchaseGroupMaps = conPurchaseGroupMapper.getList().stream().collect(Collectors.toMap(ConPurchaseGroup::getCode, ConPurchaseGroup::getName, (key1, key2) -> key2));
            //配送方式
            Map<String, String> shipmentMaps = conShipmentModeMapper.getList().stream().collect(Collectors.toMap(ConShipmentMode::getName, ConShipmentMode::getCode, (key1, key2) -> key2));

            SysDefaultSettingClient client = getClientSetting();

            ArrayList<PurPurchaseOrderItem> purPurchaseOrderItems = new ArrayList<>();
            PurPurchaseOrder purPurchaseOrder = new PurPurchaseOrder();
            String errMsg = "";
            List<CommonErrMsgResponse> msgList = new ArrayList<>();
            for (int i = 0; i < readAll.size(); i++) {
                Long basStorehouseSid = null;
                Long storehouseLocationSid = null;
                Long companySid = null;
                Long vendorSid = null;
                Long productSeasonSid = null;
                Long sku1Sid = null;
                Long sku2Sid = null;
                Long materialSid = null;
                String materialCode = null;
                String materialCodeK = null;
                Long materialSidK = null;
                Long sku1SidK = null;
                Long sku2SidK = null;
                Long barcodeSid = null;
                Long purchaseContractSid = null;
                String inOutStockStatus = null;
                String materialName = null;
                String isReturn = null;
                BigDecimal purchasePriceTax = null;
                String unitBase = null;
                BasMaterialBarcode basMaterialBarcode = null;
                String materialType = null;
                String valuePurchaseDocument = null;
                String valuePurchaseDocumentTemp = null;
                String valuePurchaseType = null;
                String isReturnGoods = null;
                String valuePriceType = null;
                String purchaseMode = null;
                Date documnetDate = null;
                String valueRawMaterial = null;
                BigDecimal quantity = null;
                Date contractDate = null;
                Date kuanXiadanDate = null;
                Date productContractDate = null;
                BigDecimal qutatiyDd = null;
                String advanceSettleMode = null;
                String valuePurchase = null;
                String vendorGroup = null;
                String conShipmentModeCode = null;
                int num = i + 1;
                if (i < 2 || i == 3 || i == 4) {
                    //前两行跳过
                    continue;
                }
                if (i == 2) {
                    List<Object> objects = readAll.get(i);
                    copy(objects, readAll);
                    if (objects.get(0) == null || objects.get(0) == "") {
                        // throw new BaseException("第"+num+"行,供应商简称，不能为空，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("供应商简称，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    }
                    if (objects.get(0) != null && objects.get(0) != "") {
                        String bendorCode = objects.get(0).toString();
                        BasVendor basVendor = basVendorMapper.selectOne(new QueryWrapper<BasVendor>()
                                .lambda().eq(BasVendor::getShortName, bendorCode));
                        if (basVendor == null) {
                            // throw new BaseException("第"+num+"行,供应商简称为" + bendorCode + "没有对应的供应商，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("供应商简称为" + bendorCode + "没有对应的供应商，导入失败");
                            msgList.add(errMsgResponse);
                        } else {
                            if (ConstantsEms.CHECK_STATUS.equals(basVendor.getHandleStatus()) && ConstantsEms.SAVA_STATUS.equals(basVendor.getStatus())) {
                                vendorSid = basVendor.getVendorSid();
                                vendorGroup = basVendor.getVendorGroup();
                            } else {
                                // throw new BaseException("第"+num+"行,供应商简称必须是启用且已确认状态，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(num);
                                errMsgResponse.setMsg("供应商简称必须是启用且已确认状态，导入失败");
                                msgList.add(errMsgResponse);
                            }
                        }
                    }

                    if (objects.get(1) == null || objects.get(1) == "") {
                        // throw new BaseException("第"+num+"行,单据类型名称，不能为空，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("单据类型名称，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    }
                    if (objects.get(1) != null && objects.get(1) != "") {
                        valuePurchaseDocument = purchaseDocumentMaps.get(objects.get(1).toString());
                        if (valuePurchaseDocument == null) {
                            // throw new BaseException("第"+num+"行,单据类型名称配置错误，请联系管理员，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("单据类型名称配置错误，请联系管理员，导入失败");
                            msgList.add(errMsgResponse);
                        } else {
                            ConDocTypePurchaseOrder conDocTypePurchaseOrder = conDocTypePurchaseOrderMapper.selectOne(new QueryWrapper<ConDocTypePurchaseOrder>().lambda()
                                    .eq(ConDocTypePurchaseOrder::getCode, valuePurchaseDocument)
                            );
                            isReturnGoods = conDocTypePurchaseOrder.getIsReturnGoods();
                            purchaseMode = conDocTypePurchaseOrder.getPurchaseMode();
                            if (!ConstantsEms.CHECK_STATUS.equals(conDocTypePurchaseOrder.getHandleStatus()) || !ConstantsEms.ENABLE_STATUS.equals(conDocTypePurchaseOrder.getStatus())) {
                                //  throw new BaseException("第"+num+"行,单据类型必须是启用&已确认状态，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(num);
                                errMsgResponse.setMsg("单据类型必须是启用&已确认状态，导入失败");
                                msgList.add(errMsgResponse);
                            } else {
                                valuePurchaseDocumentTemp = valuePurchaseDocument;
                            }
                        }
                    }
                    if (objects.get(2) == null || objects.get(2) == "") {
                        //throw new BaseException("第"+num+"行,业务类型名称，不能为空，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("业务类型名称，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    } else {
                        valuePurchaseType = purchaseTypeMaps.get(objects.get(2).toString());
                        if (valuePurchaseType == null) {
                            // throw new BaseException("第"+num+"行,业务类型名称配置错误，请联系管理员，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("业务类型名称配置错误，请联系管理员，导入失败");
                            msgList.add(errMsgResponse);
                        } else {
                            ConBuTypePurchaseOrder conBuTypePurchaseOrder = conBuTypePurchaseOrderMapper.selectOne(new QueryWrapper<ConBuTypePurchaseOrder>().lambda()
                                    .eq(ConBuTypePurchaseOrder::getCode, valuePurchaseType));
                            if (!ConstantsEms.CHECK_STATUS.equals(conBuTypePurchaseOrder.getHandleStatus()) || !ConstantsEms.ENABLE_STATUS.equals(conBuTypePurchaseOrder.getStatus())) {
                                // throw new BaseException("第"+num+"行,业务类型必须是启用&已确认状态，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(num);
                                errMsgResponse.setMsg("业务类型必须是启用&已确认状态，导入失败");
                                msgList.add(errMsgResponse);
                            } else {
                                valuePurchase = valuePurchaseType;
                            }
                        }
                    }
                    if (valuePurchaseDocumentTemp != null && valuePurchase != null) {
                        ConDocBuTypeGroupPo conDocBuTypeGroupPo = conDocBuTypeGroupPoMapper.selectOne(new QueryWrapper<ConDocBuTypeGroupPo>()
                                .lambda()
                                .eq(ConDocBuTypeGroupPo::getDocTypeCode, valuePurchaseDocument)
                                .eq(ConDocBuTypeGroupPo::getBuTypeCode, valuePurchaseType)
                                .eq(ConDocBuTypeGroupPo::getHandleStatus, ConstantsEms.CHECK_STATUS)
                                .eq(ConDocBuTypeGroupPo::getStatus, ConstantsEms.ENABLE_STATUS)
                        );
                        if (conDocBuTypeGroupPo == null) {
                            //throw new BaseException("第"+num+"行,业务类型与单据类型对应关系不匹配，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("业务类型与单据类型对应关系不匹配，导入失败");
                            msgList.add(errMsgResponse);
                        }
                    }
                    ConDocTypePurchaseOrder conDocTypePurchase = conDocTypePurchaseOrderMapper.selectOne(new QueryWrapper<ConDocTypePurchaseOrder>().lambda()
                            .eq(ConDocTypePurchaseOrder::getCode, valuePurchaseDocument)
                    );
                    if (ConstantsEms.YES.equals(isReturnGoods)) {
                        inOutStockStatus = "WCK";
                        isReturn = "Y";
                    } else {
                        inOutStockStatus = "WRK";
                        isReturn = "N";
                    }
                    if (objects.get(3) == null || objects.get(3) == "") {
                        // throw new BaseException("第"+num+"行,公司编码，不能为空，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("公司简称，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    } else {
                        String compamyCode = objects.get(3).toString();
                        BasCompany company = basCompanyMapper.selectOne(new QueryWrapper<BasCompany>()
                                .lambda().eq(BasCompany::getShortName, compamyCode));
                        if (company == null) {
                            // throw new BaseException("第"+num+"行,公司编码为" + compamyCode + "没有对应的公司，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("公司简称为" + compamyCode + "没有对应的公司，导入失败");
                            msgList.add(errMsgResponse);
                        } else {
                            if (ConstantsEms.CHECK_STATUS.equals(company.getHandleStatus()) && ConstantsEms.SAVA_STATUS.equals(company.getStatus())) {
                                companySid = company.getCompanySid();
                            } else {
                                // throw new BaseException("第"+num+"行,公司编码必须是启用且已确认状态，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(num);
                                errMsgResponse.setMsg("公司简称必须是启用且已确认状态，导入失败");
                                msgList.add(errMsgResponse);
                            }
                        }
                    }
                    String productSeasonName = objects.get(4) == null || objects.get(4) == "" ? null : objects.get(4).toString().trim();
                    if (ConstantsEms.YES.equals(client.getIsXiefuIndustry()) && StrUtil.isBlank(productSeasonName)) {
                        //throw new BaseException("第"+num+"行,产品季名称，不能为空，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("下单季名称，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    } else if (StrUtil.isNotBlank(productSeasonName)) {
                        BasProductSeason productSeason = basProductSeasonMapper.selectOne(new QueryWrapper<BasProductSeason>()
                                .lambda().eq(BasProductSeason::getProductSeasonName, productSeasonName));
                        if (productSeason == null) {
                            //throw new BaseException("第"+num+"行,产品季名称为" + productSeasonName + "没有对应的产品季，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("下单季名称为" + productSeasonName + "没有对应的下单季，导入失败");
                            msgList.add(errMsgResponse);
                        } else {
                            if (!ConstantsEms.CHECK_STATUS.equals(productSeason.getHandleStatus()) || !ConstantsEms.ENABLE_STATUS.equals(productSeason.getStatus())) {
                                // throw new BaseException("第"+num+"行,产品季必须是启用&已确认状态，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(num);
                                errMsgResponse.setMsg("下单季必须是启用&已确认状态，导入失败");
                                msgList.add(errMsgResponse);
                            }
                            productSeasonSid = productSeason.getProductSeasonSid();
                        }
                    }
                    if (objects.get(5) == null || objects.get(5) == "") {
                        // throw new BaseException("第"+num+"行,采购员账号，不能为空，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("采购员账号，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    } else {
                        try {
                            SysUser sysUser = sysUserMapper.selectOne(new QueryWrapper<SysUser>().lambda()
                                    .eq(SysUser::getUserName, objects.get(5).toString())
                                    .eq(SysUser::getClientId, ApiThreadLocalUtil.get().getClientId()));
                            if (sysUser == null) {
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(num);
                                errMsgResponse.setMsg("没有账号为" + objects.get(5).toString() + "的采购员,导入失败");
                                msgList.add(errMsgResponse);
                            } else {
                                String status = sysUser.getStatus();
                                if (!"0".equals(status)) {
                                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                    errMsgResponse.setItemNum(num);
                                    errMsgResponse.setMsg("采购员账号必须是启用状态，导入失败");
                                    msgList.add(errMsgResponse);
                                }
                            }
                        } catch (TooManyResultsException e) {
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("采购员账号存在重复，请先检查该采购员账号，导入失败");
                            msgList.add(errMsgResponse);
                        }
                    }
                    if (objects.get(6) == null || objects.get(6) == "") {
                        // throw new BaseException("第"+num+"行,单据日期，不能为空，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("单据日期，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    } else {
                        boolean validDate = JudgeFormat.isValidDate(objects.get(6).toString());
                        if (!validDate) {
                            //  throw new BaseException("第"+num+"行,单据日期，格式错误，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("单据日期，格式错误，导入失败");
                            msgList.add(errMsgResponse);
                        } else {
                            String documnet = objects.get(6).toString();
                            documnetDate = DateUtil.parse(documnet);
                        }
                    }
                    if (objects.get(7) == null || objects.get(7) == "") {
                        // throw new BaseException("第"+num+"行,甲供料方式，不能为空，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("甲供料方式，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    } else {
                        valueRawMaterial = rawMaterialModeMaps.get(objects.get(7).toString());
                        if (valueRawMaterial == null) {
                            //throw new BaseException("第"+num+"行,甲供料方式配置错误，请联系管理员，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("甲供料方式配置错误，请联系管理员，导入失败");
                            msgList.add(errMsgResponse);
                        } else {
                            String value = valueRawMaterial;
                            List<DictData> list = rawMaterialMode.stream()
                                    .filter(m -> ConstantsEms.CHECK_STATUS.equals(m.getHandleStatus()) && "0".equals(m.getStatus()) && value.equals(m.getDictValue()))
                                    .collect(Collectors.toList());
                            if (CollectionUtil.isEmpty(list)) {
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(num);
                                errMsgResponse.setMsg("甲供料方式配置错误，请联系管理员，导入失败");
                                msgList.add(errMsgResponse);
                            }
                        }
                    }
                    if (objects.get(8) == null || objects.get(8) == "") {
                        //  throw new BaseException("第"+num+"行,采购模式，不能为空，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("采购模式，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    } else {
                        valuePriceType = priceTypeMaps.get(objects.get(8).toString());
                        if (valuePriceType == null) {
                            //  throw new BaseException("第"+num+"行,采购模式配置错误，请联系管理员，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("采购模式配置错误，请联系管理员，导入失败");
                            msgList.add(errMsgResponse);
                        } else {
                            String value = valuePriceType;
                            if (CollectionUtil.isNotEmpty(priceType)) {
                                List<DictData> list = priceType.stream()
                                        .filter(m -> ConstantsEms.CHECK_STATUS.equals(m.getHandleStatus()) && "0".equals(m.getStatus()) && value.equals(m.getDictValue()))
                                        .collect(Collectors.toList());
                                if (CollectionUtil.isEmpty(list)) {
                                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                    errMsgResponse.setItemNum(num);
                                    errMsgResponse.setMsg("采购模式配置错误，请联系管理员，导入失败");
                                    msgList.add(errMsgResponse);
                                } else {
                                    if (!purchaseMode.equals(valuePriceType)) {
                                        // throw new BaseException("第"+num+"行,采购模式与单据类型对应关系不匹配，导入失败");
                                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                        errMsgResponse.setItemNum(num);
                                        errMsgResponse.setMsg("采购模式与单据类型对应关系不匹配，导入失败");
                                        msgList.add(errMsgResponse);
                                    }
                                }
                            }
                        }
                    }
                    /*
                    String paperPurchaseContractCode = null;
                    if (objects.get(9) != null && objects.get(9) != "") {
                        paperPurchaseContractCode = objects.get(9).toString();
                    }
                    */
                    String purchaseContractCode = null;
                    if (objects.get(9) == null || objects.get(9) == "") {
                        //throw new BaseException("第"+num+"行,采购合同号，不能为空");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("采购合同号，不能为空");
                        msgList.add(errMsgResponse);
                    } else {
                        purchaseContractCode = objects.get(9).toString();
                        if (settingClient == null || !ConstantsOrder.CONTRACT_ENTER_MODE_SG.equals(settingClient.getPurchaseOrderContractEnterMode())) {
                            List<PurPurchaseContract> purPurchase = purPurchaseContractMapper.selectList(new QueryWrapper<PurPurchaseContract>()
                                    .lambda().eq(PurPurchaseContract::getPurchaseContractCode, purchaseContractCode));
                            if (CollectionUtil.isEmpty(purPurchase)) {
                                //throw new BaseException("第"+num+"行,合同号校验不通过，导入失败！");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(num);
                                errMsgResponse.setMsg("合同号不存在，导入失败！");
                                msgList.add(errMsgResponse);
                            } else {
                                Long finalVendorSid = vendorSid;
                                Long finalCompanySid = companySid;
                                purPurchase = purPurchase.stream().filter(o -> o.getVendorSid().equals(finalVendorSid) && o.getCompanySid().equals(finalCompanySid)).collect(Collectors.toList());
                                if (CollectionUtil.isEmpty(purPurchase)) {
                                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                    errMsgResponse.setItemNum(num);
                                    errMsgResponse.setMsg("合同的“供应商+公司”与订单的“供应商+公司”不一致，导入失败！");
                                    msgList.add(errMsgResponse);
                                } else {
                                    purchaseContractSid = purPurchase.get(0).getPurchaseContractSid();
                                    advanceSettleMode = purPurchase.get(0).getAdvanceSettleMode();
                                }
                            }
                        }
                    }
                    if (objects.get(10) != null && objects.get(10) != "") {
                        ConMaterialType conMaterialType = conMaterialTypeMapper.selectOne(new QueryWrapper<ConMaterialType>().lambda()
                                .eq(ConMaterialType::getName, objects.get(10).toString())
                        );
                        if (conMaterialType == null) {
                            //  throw new BaseException("第"+num+"行,物料类型为" + code + "没有对应的物料类型，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("物料类型为" + objects.get(10).toString() + "没有对应的物料类型，导入失败");
                            msgList.add(errMsgResponse);
                        } else {
                            if (!ConstantsEms.CHECK_STATUS.equals(conMaterialType.getHandleStatus()) || !ConstantsEms.ENABLE_STATUS.equals(conMaterialType.getStatus())) {
                                // throw new BaseException("第"+num+"行,仓库必须是确认且已启用状态，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(num);
                                errMsgResponse.setMsg("物料类型是确认且已启用状态，导入失败");
                                msgList.add(errMsgResponse);
                            } else {
                                materialType = conMaterialType.getCode();
                            }
                        }
                    }
                    if (objects.get(11) != null && objects.get(11) != "") {
                        BasStorehouse basStorehouse = basStorehouseMapper.selectOne(new QueryWrapper<BasStorehouse>().lambda()
                                .eq(BasStorehouse::getStorehouseName, objects.get(11).toString())
                        );
                        if (basStorehouse == null) {
                            // throw new BaseException("第"+num+"行,没有编码为" + objects.get(11).toString() + "的仓库，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("没有名称为" + objects.get(11).toString() + "的仓库，导入失败");
                            msgList.add(errMsgResponse);
                        } else {
                            if (!ConstantsEms.CHECK_STATUS.equals(basStorehouse.getHandleStatus()) || !ConstantsEms.ENABLE_STATUS.equals(basStorehouse.getStatus())) {
                                //  throw new BaseException("第"+num+"行,仓库必须是启用&已确认状态，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(num);
                                errMsgResponse.setMsg("仓库必须是启用&已确认状态，导入失败");
                                msgList.add(errMsgResponse);
                            }
                            basStorehouseSid = Long.valueOf(basStorehouse.getStorehouseSid());
                        }
                    }
                    if (objects.get(12) != null && objects.get(12) != "") {
                        BasStorehouseLocation basStorehouseLocation = basStorehouseLocationMapper.selectOne(new QueryWrapper<BasStorehouseLocation>().lambda()
                                .eq(BasStorehouseLocation::getStorehouseSid, basStorehouseSid)
                                .eq(BasStorehouseLocation::getLocationName, objects.get(12).toString())
                        );
                        if (basStorehouseLocation == null) {
                            //  throw new BaseException("第"+num+"行,没有编码为" + objects.get(12).toString() + "的库位，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg(objects.get(11).toString() + "下，没有名称为" + objects.get(12).toString() + "的库位，导入失败");
                            msgList.add(errMsgResponse);
                        } else {
                            if (!ConstantsEms.CHECK_STATUS.equals(basStorehouseLocation.getHandleStatus()) || !ConstantsEms.ENABLE_STATUS.equals(basStorehouseLocation.getStatus())) {
                                // throw new BaseException("第"+num+"行,库位必须是启用&已确认状态，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(num);
                                errMsgResponse.setMsg("库位必须是启用&已确认状态，导入失败");
                                msgList.add(errMsgResponse);
                            }
                            storehouseLocationSid = Long.valueOf(basStorehouseLocation.getStorehouseLocationSid());
                        }
                    }
                    if (objects.get(16) != null && objects.get(16) != "") {
                        String name = purchaseOrgMaps.get(objects.get(16).toString());
                        if (name == null) {
                            // throw new BaseException("第"+num+"行,没有编码为" + objects.get(16).toString() + "的采购组织，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("没有编码为" + objects.get(16).toString() + "的采购组织，导入失败");
                            msgList.add(errMsgResponse);
                        }
                    }
                    if (objects.get(17) != null && objects.get(17) != "") {
                        String name = purchaseGroupMaps.get(objects.get(17).toString());
                        if (name == null) {
                            // throw new BaseException("第"+num+"行,没有编码为" + objects.get(17).toString() + "的采购组，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("没有编码为" + objects.get(17).toString() + "的采购组，导入失败");
                            msgList.add(errMsgResponse);
                        }
                    }
                    if (objects.get(18) != null && objects.get(18) != "") {
                        ConShipmentMode conShipmentMode = conShipmentModeMapper.selectOne(new QueryWrapper<ConShipmentMode>().lambda()
                                .eq(ConShipmentMode::getName, objects.get(18).toString())
                        );
                        if (conShipmentMode == null) {
                            //throw new BaseException("第"+num+"行,没有名称为" + objects.get(18).toString() + "的配送方式，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("没有名称为" + objects.get(18).toString() + "的配送方式，导入失败");
                            msgList.add(errMsgResponse);
                        } else {
                            if (!ConstantsEms.CHECK_STATUS.equals(conShipmentMode.getHandleStatus()) || !ConstantsEms.ENABLE_STATUS.equals(conShipmentMode.getStatus())) {
                                // throw new BaseException("第"+num+"行,库位必须是启用&已确认状态，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(num);
                                errMsgResponse.setMsg("配送方式必须是启用&已确认状态，导入失败");
                                msgList.add(errMsgResponse);
                            } else {
                                conShipmentModeCode = conShipmentMode.getCode();
                            }
                        }
                    }
                    if (objects.get(14) != null && objects.get(14) != "") {
                        boolean phone = JudgeFormat.isPhone(objects.get(14).toString());
                        if (!phone) {
                            // throw new BaseException("第"+num+"行,手机格式错误，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("收货人联系电话，格式错误，导入失败");
                            msgList.add(errMsgResponse);
                        }
                    }

                    if (YCX_VENDOR.equals(vendorGroup)) {
                        if (objects.get(20) == "" || objects.get(20) == null) {
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("供应商名称备注，不能为空，导入失败");
                            msgList.add(errMsgResponse);
                        }
                    } else {
                        if (vendorSid != null) {
                            if (objects.get(20) != "" && objects.get(20) != null) {
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(num);
                                errMsgResponse.setMsg("供应商为非一次性供应商组时，供应商名称备注必须为空");
                                msgList.add(errMsgResponse);
                            }
                        }
                    }

                    // 纸质下单合同号
                    String paperPurchaseContractCode = objects.get(21) == null || objects.get(21) == "" ? null : objects.get(21).toString().trim();
                    if (StrUtil.isNotBlank(paperPurchaseContractCode)) {
                        if (paperPurchaseContractCode.length() > 120) {
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("纸质下单合同号最多不能超过120位，导入失败");
                            msgList.add(errMsgResponse);
                        }
                    }

                    purPurchaseOrder
                            .setVendorSid(vendorSid)
                            .setAdvanceSettleMode(advanceSettleMode)
                            .setDocumentType(valuePurchaseDocument)
                            .setBusinessType(valuePurchaseType)
                            .setImportHandle(HandleStatus.SUBMIT.getCode())
                            .setInOutStockStatus(inOutStockStatus)
                            .setCompanySid(companySid)
                            .setPurchaseMode(valuePriceType)
                            .setProductSeasonSid(productSeasonSid)
                            .setCurrency("CNY")
                            .setIsConsignmentSettle("N")
                            .setIsReturnGoods(isReturn)
                            .setCurrencyUnit("YUAN")
                            .setBuyer((objects.get(5) == "" || objects.get(5) == null) ? null : objects.get(5).toString())
                            .setDocumentDate(documnetDate)
                            .setRawMaterialMode(valueRawMaterial)
                            .setPurchaseContractSid(purchaseContractSid)
                            .setPurchaseContractCode(purchaseContractCode)
                            .setMaterialType((objects.get(10) == "" || objects.get(10) == null) ? null : materailTypeMaps.get(objects.get(10).toString()))
                            .setStorehouseSid(basStorehouseSid)
                            .setStorehouseLocationSid(storehouseLocationSid)
                            .setConsignee((objects.get(13) == "" || objects.get(13) == null) ? null : objects.get(13).toString())
                            .setConsigneePhone((objects.get(14) == "" || objects.get(14) == null) ? null : objects.get(14).toString())
                            .setConsigneeAddr((objects.get(15) == "" || objects.get(15) == null) ? null : objects.get(15).toString())
                            .setPurchaseOrg((objects.get(16) == "" || objects.get(16) == null) ? null : objects.get(16).toString())
                            .setPurchaseGroup((objects.get(17) == "" || objects.get(17) == null) ? null : objects.get(17).toString())
                            .setShipmentType((objects.get(18) == "" || objects.get(18) == null) ? null : conShipmentModeCode)
                            .setVendorNameRemark((objects.get(20) == "" || objects.get(20) == null) ? null : objects.get(20).toString())
                            .setVendorBusinessman((objects.get(19) == "" || objects.get(19) == null) ? null : objects.get(19).toString())
                            .setPaperPurchaseContractCode(paperPurchaseContractCode)
                            .setRemark((objects.get(22) == "" || objects.get(22) == null) ? null : objects.get(22).toString())
                            .setHandleStatus(ConstantsEms.SAVA_STATUS)
                            .setMaterialCategory(ConstantsEms.MATERIAL_CATEGORY_WL);

                    continue;
                }
                List<Object> objects = readAll.get(i);
                copy(objects, readAll);
                if (objects.get(0) == null || objects.get(0) == "") {
                    //  throw new BaseException("第"+num+"行,商品编码不可为空，导入失败");
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("物料编码不可为空，导入失败");
                    msgList.add(errMsgResponse);
                } else {
                    BasMaterial basMaterial = basMaterialMapper.selectOne(new QueryWrapper<BasMaterial>().lambda()
                            .eq(BasMaterial::getMaterialCode, objects.get(0).toString())
                    );
                    if (basMaterial == null) {
                        //  throw new BaseException("第"+num+"行,没有编码为"+objects.get(0).toString()+"的商品，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("没有编码为" + objects.get(0).toString() + "的物料，导入失败");
                        msgList.add(errMsgResponse);
                    } else {
                        if (ConstantsEms.CHECK_STATUS.equals(basMaterial.getHandleStatus()) && ConstantsEms.SAVA_STATUS.equals(basMaterial.getStatus())) {
                            if (!ConstantsEms.MATERIAL_CATEGORY_WL.equals(basMaterial.getMaterialCategory())) {
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(num);
                                errMsgResponse.setMsg("所填明细必须是物料，导入失败");
                                msgList.add(errMsgResponse);
                            } else {
                                materialSid = basMaterial.getMaterialSid();
                                unitBase = basMaterial.getUnitBase();
                                materialCode = basMaterial.getMaterialCode();
                                materialName = basMaterial.getMaterialName();
                                if (objects.get(1) == null || objects.get(1) == "") {
                                    //  throw new BaseException("第"+num+"行,SKU1名称不可为空，导入失败");
                                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                    errMsgResponse.setItemNum(num);
                                    errMsgResponse.setMsg("SKU1名称不可为空，导入失败");
                                    msgList.add(errMsgResponse);
                                } else {
                                    BasSku basSku = basSkuMapper.selectOne(new QueryWrapper<BasSku>().lambda()
                                            .eq(BasSku::getSkuName, objects.get(1).toString())
                                            .eq(BasSku::getSkuType, basMaterial.getSku1Type())
                                    );
                                    if (basSku == null) {
                                        //throw new BaseException("第"+num+"行,SKU1名称为"+objects.get(1).toString()+",没有对应的SKU1名称，导入失败");
                                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                        errMsgResponse.setItemNum(num);
                                        errMsgResponse.setMsg("SKU1名称为" + objects.get(1).toString() + ",没有对应类型的SKU1名称，导入失败");
                                        msgList.add(errMsgResponse);
                                    } else {
                                        sku1Sid = basSku.getSkuSid();
                                    }
                                }
                                if (materialSid != null && sku1Sid != null) {
                                    BasMaterialSku skuName = basMaterialSkuMapper.selectOne(new QueryWrapper<BasMaterialSku>().lambda()
                                            .eq(BasMaterialSku::getMaterialSid, materialSid)
                                            .eq(BasMaterialSku::getSkuSid, sku1Sid)
                                    );
                                    if (skuName == null) {
                                        //throw new BaseException("第"+num+"行,SKU1名称必须是所填商品当中已启用的颜色，导入失败");
                                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                        errMsgResponse.setItemNum(num);
                                        errMsgResponse.setMsg("SKU1名称必须是所填物料当中的颜色，导入失败");
                                        msgList.add(errMsgResponse);
                                    } else {
                                        if (!ConstantsEms.ENABLE_STATUS.equals(skuName.getStatus())) {
                                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                            errMsgResponse.setItemNum(num);
                                            errMsgResponse.setMsg("SKU1名称必须是所填物料当中已启用的颜色，导入失败");
                                            msgList.add(errMsgResponse);
                                        }
                                    }
                                }
                                if (objects.get(2) != null && objects.get(2) != "") {
                                    BasSku basSku2 = basSkuMapper.selectOne(new QueryWrapper<BasSku>().lambda()
                                            .eq(BasSku::getSkuName, objects.get(2).toString())
                                            .eq(BasSku::getSkuType, basMaterial.getSku2Type())
                                    );
                                    if (basSku2 == null) {
                                        // throw new BaseException("第"+num+"行,SKU2名称为"+objects.get(2).toString()+",没有对应的SKU2名称，导入失败");
                                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                        errMsgResponse.setItemNum(num);
                                        errMsgResponse.setMsg("SKU2名称为" + objects.get(2).toString() + ",没有对应类型的SKU2名称，导入失败");
                                        msgList.add(errMsgResponse);
                                    } else {
                                        sku2Sid = basSku2.getSkuSid();
                                        BasMaterialSku basMaterialSkusSku = basMaterialSkuMapper.selectOne(new QueryWrapper<BasMaterialSku>().lambda()
                                                .eq(BasMaterialSku::getMaterialSid, materialSid)
                                                .eq(BasMaterialSku::getSkuSid, sku2Sid)
                                        );
                                        if (basMaterialSkusSku == null) {
                                            // throw new BaseException("第"+num+"行,SKU2名称必须是所填商品当中已启用的长度或尺码，导入失败");
                                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                            errMsgResponse.setItemNum(num);
                                            errMsgResponse.setMsg("SKU2名称必须是所填物料当中的长度或尺码，导入失败");
                                            msgList.add(errMsgResponse);
                                        } else {
                                            if (!ConstantsEms.ENABLE_STATUS.equals(basMaterialSkusSku.getStatus())) {
                                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                                errMsgResponse.setItemNum(num);
                                                errMsgResponse.setMsg("SKU2名称必须是所填物料当中已启用的长度或尺码，导入失败");
                                                msgList.add(errMsgResponse);
                                            }
                                        }
                                    }
                                }
                                if (sku1Sid != null && sku2Sid != null) {
                                    basMaterialBarcode = basMaterialBarcodeMapper.selectOne(new QueryWrapper<BasMaterialBarcode>().lambda()
                                            .eq(BasMaterialBarcode::getMaterialSid, materialSid)
                                            .eq(BasMaterialBarcode::getSku1Sid, sku1Sid)
                                            .eq(BasMaterialBarcode::getSku2Sid, sku2Sid)
                                    );
                                } else if (sku1Sid != null && sku2Sid == null) {
                                    basMaterialBarcode = basMaterialBarcodeMapper.selectOne(new QueryWrapper<BasMaterialBarcode>().lambda()
                                            .eq(BasMaterialBarcode::getMaterialSid, materialSid)
                                            .isNull(BasMaterialBarcode::getSku2Sid)
                                            .eq(BasMaterialBarcode::getSku1Sid, sku1Sid)
                                    );
                                }
                                if (materialCode != null) {
                                    if (basMaterialBarcode == null) {
                                        //throw new BaseException("第" + num + "行,不存在商品编码为" + objects.get(0) + ",sku1名称为" + objects.get(1) + "" + ",sku2名称为" + objects.get(2) + "的商品条码，导入失败");
                                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                        errMsgResponse.setItemNum(num);
                                        if (objects.get(2) == null || objects.get(2) == "") {
                                            errMsgResponse.setMsg("不存在物料编码为" + objects.get(0) + ",sku1名称为" + objects.get(1) + "的商品条码，导入失败");
                                        } else {
                                            errMsgResponse.setMsg("不存在物料编码为" + objects.get(0) + ",sku1名称为" + objects.get(1) + "" + ",sku2名称为" + objects.get(2) + "的商品条码，导入失败");
                                        }
                                        msgList.add(errMsgResponse);
                                    } else {
                                        if (!ConstantsEms.ENABLE_STATUS.equals(basMaterialBarcode.getStatus())) {
                                            // throw new BaseException("第" + num + "行,商品编码为" + objects.get(0) + ",sku1名称为" + objects.get(1) + "" + ",sku2名称为" + objects.get(2) + "的商品条码不是启用状态，导入失败");
                                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                            errMsgResponse.setItemNum(num);
                                            if (objects.get(2) == null || objects.get(2) == "") {
                                                errMsgResponse.setMsg("商品物料为" + objects.get(0) + ",sku1名称为" + objects.get(1) + "的商品条码不是启用状态，导入失败");
                                            } else {
                                                errMsgResponse.setMsg("商品物料为" + objects.get(0) + ",sku1名称为" + objects.get(1) + "" + ",sku2名称为" + objects.get(2) + "的商品条码不是启用状态，导入失败");
                                            }
                                            msgList.add(errMsgResponse);
                                        }
                                        barcodeSid = basMaterialBarcode.getBarcodeSid();
                                    }
                                }
                            }
                        } else {
                            //  throw new BaseException("第"+num+"行,商品编码必须是启用且已确认状态，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("物料编码必须是启用且已确认状态，导入失败");
                            msgList.add(errMsgResponse);
                        }
                    }
                }
                if (objects.get(3) == null || objects.get(3) == "") {
                    //throw new BaseException("第"+num+"行,订单量 不可为空，导入失败");
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("订单量 不可为空，导入失败");
                    msgList.add(errMsgResponse);
                } else {
                    boolean validDouble = JudgeFormat.isValidDouble(objects.get(3).toString());
                    if (!validDouble) {
                        //throw new BaseException("第"+num+"行,订单量格式错误，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("订单量格式错误，导入失败");
                        msgList.add(errMsgResponse);
                    } else {
                        qutatiyDd = BigDecimal.valueOf(Double.valueOf(objects.get(3).toString()));
                        if (qutatiyDd.compareTo(BigDecimal.ZERO) == -1 || qutatiyDd.compareTo(BigDecimal.ZERO) == 0) {
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("订单量不能小于等于0，导入失败");
                            msgList.add(errMsgResponse);
                        }
                    }
                }
                if (objects.get(4) == null || objects.get(4) == "") {
                    //  throw new BaseException("第"+num+"行,合同交期 不可为空，导入失败");
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("合同交期 不可为空，导入失败");
                    msgList.add(errMsgResponse);
                } else {
                    boolean validDate = JudgeFormat.isValidDate(objects.get(4).toString());
                    if (!validDate) {
                        // throw new BaseException("第"+num+"行,合同交期，格式错误，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("合同交期，格式错误，导入失败");
                        msgList.add(errMsgResponse);
                    } else {
                        contractDate = DateUtil.parse(objects.get(4).toString());
                    }
                }

                if (objects.get(9) == null || objects.get(9) == "") {
                } else {
                    boolean validDate = JudgeFormat.isValidDate(objects.get(9).toString());
                    if (!validDate) {
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("款下单日期，格式错误，导入失败");
                        msgList.add(errMsgResponse);
                    } else {
                        kuanXiadanDate = DateUtil.parse(objects.get(9).toString());
                    }
                }
                if (objects.get(10) == null || objects.get(10) == "") {
                } else {
                    boolean validDate = JudgeFormat.isValidDate(objects.get(10).toString());
                    if (!validDate) {
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("款合同日期，格式错误，导入失败");
                        msgList.add(errMsgResponse);
                    } else {
                        productContractDate = DateUtil.parse(objects.get(10).toString());
                    }
                }

                if (objects.get(13) != null && objects.get(13) != "") {
                    if (!"是".equals(objects.get(13).toString()) && !"否".equals(objects.get(13).toString())) {
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("是否免费只能填是或者否，导入失败");
                        msgList.add(errMsgResponse);
                    }
                }
                //默认获取通用税率
                ConTaxRate taxRate = conTaxRateMapper.selectOne(new QueryWrapper<ConTaxRate>().lambda()
                        .eq(ConTaxRate::getIsDefault, "Y")
                );
                PurPurchaseOrderItem purPurchaseOrderItem = new PurPurchaseOrderItem();
                purPurchaseOrderItem.setSku1Sid(sku1Sid)
                        .setSku2Sid(sku2Sid)
                        .setMaterialCode(materialCode)
                        .setMaterialSid(materialSid)
                        .setInOutStockStatus(inOutStockStatus)
                        .setTaxRate(taxRate.getTaxRateValue())
                        .setBarcodeSid(barcodeSid)
                        .setQuantity(qutatiyDd)
                        .setMaterialName(materialName)
                        .setContractDate(contractDate).setKuanXiadanDate(kuanXiadanDate).setProductContractDate(productContractDate)
                        .setProductCodes((objects.get(5) == "" || objects.get(5) == null) ? null : objects.get(5).toString())
                        .setProductSku1Names((objects.get(6) == "" || objects.get(6) == null) ? null : objects.get(6).toString())
                        .setProductSku2Names((objects.get(7) == "" || objects.get(7) == null) ? null : objects.get(7).toString())
                        .setProductQuantityRemark((objects.get(8) == "" || objects.get(8) == null) ? null : objects.get(8).toString())
                        .setProductPoCodes((objects.get(12) == "" || objects.get(12) == null) ? null : objects.get(12).toString())
                        .setProductSoCodes((objects.get(11) == "" || objects.get(11) == null) ? null : objects.get(11).toString())
                        .setFreeFlag((objects.get(13) == "" || objects.get(13) == null) ? null : ("是".equals(objects.get(13).toString()) ? "Y" : null))
                        .setRemark((objects.get(14) == "" || objects.get(14) == null) ? null : objects.get(14).toString());
//                        .setUnitBase(unitBase);

                purPurchaseOrderItems.add(purPurchaseOrderItem);
            }
            if (CollectionUtil.isNotEmpty(msgList)) {
                return AjaxResult.error("报错信息", msgList);
            }
            purPurchaseOrder.setPurPurchaseOrderItemList(purPurchaseOrderItems);
            try {
                purPurchaseOrder.setImportType(BusinessType.IMPORT.getValue());
                insertPurPurchaseOrder(purPurchaseOrder);
            } catch (CustomException e) {
                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                errMsgResponse.setMsg(e.getMessage());
                msgList.add(errMsgResponse);
            }
            if (CollectionUtil.isNotEmpty(msgList)) {
                return AjaxResult.error("报错信息", msgList);
            }
        } catch (BaseException e) {
            throw new BaseException(e.getDefaultMessage());
        }

        return AjaxResult.success(1);
    }

    /**
     * 物料采购退货订单 导入
     */
    @Override
    public int importDataRe(MultipartFile file) {
        try {
            File toFile = null;
            try {
                toFile = FileUtils.multipartFileToFile(file);
            } catch (Exception e) {
                e.getMessage();
                throw new BaseException("文件转换失败");
            }
            ExcelReader reader = cn.hutool.poi.excel.ExcelUtil.getReader(toFile);
            FileUtils.delteTempFile(toFile);
            List<List<Object>> readAll = reader.read();
            //采购订单-单据类型
            Map<String, String> purchaseDocumentMaps = conDocTypePurchaseOrderMapper.getList().stream().collect(Collectors.toMap(ConDocTypePurchaseOrder::getName, ConDocTypePurchaseOrder::getCode, (key1, key2) -> key2));
            //采购订单-业务类型
            Map<String, String> purchaseTypeMaps = conBuTypePurchaseOrderMapper.getList().stream().collect(Collectors.toMap(ConBuTypePurchaseOrder::getName, ConBuTypePurchaseOrder::getCode, (key1, key2) -> key2));
            // 租户配置
            SysDefaultSettingClient settingClient = settingClientMapper.selectOne(new QueryWrapper<SysDefaultSettingClient>().lambda()
                    .eq(SysDefaultSettingClient::getClientId, ApiThreadLocalUtil.get().getClientId()));
            //客供料 方式
            List<DictData> rawMaterialMode = sysDictDataService.selectDictData("s_raw_material_mode");
            Map<String, String> rawMaterialModeMaps = rawMaterialMode.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            //采购模式
            List<DictData> priceType = sysDictDataService.selectDictData("s_price_type");
            Map<String, String> priceTypeMaps = priceType.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            //销售渠道
            Map<String, String> conSaleChannelMaps = conSaleChannelMapper.getConSaleChannelList().stream().collect(Collectors.toMap(ConSaleChannel::getCode, ConSaleChannel::getName, (key1, key2) -> key2));
            //物料类型
            Map<String, String> materailTypeMaps = conMaterialTypeMapper.getConMaterialTypeList().stream().collect(Collectors.toMap(ConMaterialType::getName, ConMaterialType::getCode, (key1, key2) -> key2));
            //采购组织
            Map<String, String> purchaseOrgMaps = conPurchaseOrgMapper.getConPurchaseOrgList().stream().collect(Collectors.toMap(ConPurchaseOrg::getCode, ConPurchaseOrg::getName, (key1, key2) -> key2));
            //采购组
            Map<String, String> purchaseGroupMaps = conPurchaseGroupMapper.getList().stream().collect(Collectors.toMap(ConPurchaseGroup::getCode, ConPurchaseGroup::getName, (key1, key2) -> key2));
            ArrayList<PurPurchaseOrderItem> purPurchaseOrderItems = new ArrayList<>();
            PurPurchaseOrder purPurchaseOrder = new PurPurchaseOrder();
            for (int i = 0; i < readAll.size(); i++) {
                Long basStorehouseSid = null;
                Long storehouseLocationSid = null;
                Long companySid = null;
                Long vendorSid = null;
                Long productSeasonSid = null;
                Long sku1Sid = null;
                Long sku2Sid = null;
                Long materialSid = null;
                Long barcodeSid = null;
                Long purchaseContractSid = null;
                String materialName = null;
                BigDecimal purchasePriceTax = null;
                String unitBase = null;
                BasMaterialBarcode basMaterialBarcode = null;
                int num = i + 1;
                if (i < 2 || i == 3 || i == 4) {
                    //前两行跳过
                    continue;
                }
                if (i == 2) {
                    List<Object> objects = readAll.get(i);
                    copy(objects, readAll);
                    if (objects.get(0) == null || objects.get(0) == "") {
                        throw new BaseException("第" + num + "行,供应商编码，不能为空，导入失败");
                    }
                    String bendorCode = objects.get(0).toString();
                    BasVendor basVendor = basVendorMapper.selectOne(new QueryWrapper<BasVendor>()
                            .lambda().eq(BasVendor::getVendorCode, bendorCode));
                    if (basVendor == null) {
                        throw new BaseException("第" + num + "行,供应商编码为" + bendorCode + "没有对应的供应商，导入失败");
                    } else {
                        vendorSid = basVendor.getVendorSid();
                    }
                    if (objects.get(1) == null || objects.get(1) == "") {
                        throw new BaseException("第" + num + "行,单据类型名称，不能为空，导入失败");
                    }
                    String valuePurchaseDocument = purchaseDocumentMaps.get(objects.get(1).toString());
                    if (valuePurchaseDocument == null) {
                        throw new BaseException("第" + num + "行,单据类型名称配置错误，请联系管理员，导入失败");
                    }
                    if (objects.get(2) == null || objects.get(2) == "") {
                        throw new BaseException("第" + num + "行,业务类型名称，不能为空，导入失败");
                    }
                    String valuePurchaseType = purchaseTypeMaps.get(objects.get(2).toString());
                    if (valuePurchaseType == null) {
                        throw new BaseException("第" + num + "行,业务类型名称配置错误，请联系管理员，导入失败");
                    }
                    if (objects.get(3) == null || objects.get(3) == "") {
                        throw new BaseException("第" + num + "行,公司编码，不能为空，导入失败");
                    }
                    String compamyCode = objects.get(3).toString();
                    BasCompany company = basCompanyMapper.selectOne(new QueryWrapper<BasCompany>()
                            .lambda().eq(BasCompany::getCompanyCode, compamyCode));
                    if (company == null) {
                        throw new BaseException("第" + num + "行,公司编码为" + compamyCode + "没有对应的公司，导入失败");
                    } else {
                        companySid = company.getCompanySid();
                    }
                    if (objects.get(4) == null || objects.get(4) == "") {
                        throw new BaseException("第" + num + "行,产品季，不能为空，导入失败");
                    }
                    String productSeasonName = objects.get(4).toString();
                    BasProductSeason productSeason = basProductSeasonMapper.selectOne(new QueryWrapper<BasProductSeason>()
                            .lambda().eq(BasProductSeason::getProductSeasonName, productSeasonName));
                    if (productSeason == null) {
                        throw new BaseException("第" + num + "行,产品季名称为" + productSeasonName + "没有对应的产品季，导入失败");
                    } else {
                        productSeasonSid = productSeason.getProductSeasonSid();
                    }
                    if (objects.get(5) == null || objects.get(5) == "") {
                        throw new BaseException("第" + num + "行,销售员账号，不能为空，导入失败");
                    }
                    if (objects.get(6) == null || objects.get(6) == "") {
                        throw new BaseException("第" + num + "行,单据日期，不能为空，导入失败");
                    }
                    boolean validDate = JudgeFormat.isValidDate(objects.get(6).toString());
                    if (!validDate) {
                        throw new BaseException("第" + num + "行,单据日期，格式错误，导入失败");
                    }
                    String documnet = objects.get(6).toString();
                    Date documnetDate = DateUtil.parse(documnet);
                    if (objects.get(7) == null || objects.get(7) == "") {
                        throw new BaseException("第" + num + "行,客供料方式，不能为空，导入失败");
                    }
                    String valueRawMaterial = rawMaterialModeMaps.get(objects.get(7).toString());
                    if (valueRawMaterial == null) {
                        throw new BaseException("第" + num + "行,客供料方式配置错误，请联系管理员，导入失败");
                    }
                    if (objects.get(8) == null || objects.get(8) == "") {
                        throw new BaseException("第" + num + "行,采购模式，不能为空，导入失败");
                    }
                    String valuePriceType = priceTypeMaps.get(objects.get(8).toString());
                    if (valuePriceType == null) {
                        throw new BaseException("第" + num + "行,采购模式配置错误，请联系管理员，导入失败");
                    }
                    /*
                    String paperPurchaseContractCode = null;
                    if (objects.get(9) != null && objects.get(9) != "") {
                        paperPurchaseContractCode = objects.get(9).toString();
                    }
                    */
                    String purchaseContractCode = null;
                    if (settingClient == null || !ConstantsOrder.CONTRACT_ENTER_MODE_SG.equals(settingClient.getPurchaseOrderContractEnterMode())) {
                        List<PurPurchaseContract> purPurchase = purPurchaseContractMapper.selectList(new QueryWrapper<PurPurchaseContract>()
                                .lambda().eq(PurPurchaseContract::getPurchaseContractCode, purchaseContractCode));
                        if (CollectionUtil.isEmpty(purPurchase)) {
                            throw new BaseException("第" + num + "行,合同号不存在，导入失败！");
                        } else {
                            Long finalVendorSid = vendorSid;
                            Long finalCompanySid = companySid;
                            purPurchase = purPurchase.stream().filter(o -> o.getVendorSid().equals(finalVendorSid) && o.getCompanySid().equals(finalCompanySid)).collect(Collectors.toList());
                            if (CollectionUtil.isEmpty(purPurchase)) {
                                throw new BaseException("第" + num + "行,合同的“供应商+公司”与订单的“供应商+公司”不一致，导入失败");
                            } else {
                                purchaseContractSid = purPurchase.get(0).getPurchaseContractSid();
                            }
                        }
                    }
                    if (objects.get(10) != null && objects.get(10) != "") {
                        String code = objects.get(10).toString();
                        String name = materailTypeMaps.get(code);
                        if (name == null) {
                            throw new BaseException("第" + num + "行,物料类型为" + code + "没有对应的物料类型，导入失败");
                        }
                    }
                    if (objects.get(11) != null && objects.get(11) != "") {
                        BasStorehouse basStorehouse = basStorehouseMapper.selectOne(new QueryWrapper<BasStorehouse>().lambda()
                                .eq(BasStorehouse::getStorehouseCode, objects.get(11).toString())
                        );
                        if (basStorehouse == null) {
                            throw new BaseException("第" + num + "行,没有编码为" + objects.get(11).toString() + "的仓库，导入失败");
                        } else {
                            basStorehouseSid = Long.valueOf(basStorehouse.getStorehouseSid());
                        }
                    }
                    if (objects.get(12) != null && objects.get(12) != "") {
                        BasStorehouseLocation basStorehouseLocation = basStorehouseLocationMapper.selectOne(new QueryWrapper<BasStorehouseLocation>().lambda()
                                .eq(BasStorehouseLocation::getStorehouseSid, basStorehouseSid)
                                .eq(BasStorehouseLocation::getLocationCode, objects.get(12).toString())
                        );
                        if (basStorehouseLocation == null) {
                            throw new BaseException("第" + num + "行,没有编码为" + objects.get(12).toString() + "的库位，导入失败");
                        } else {
                            storehouseLocationSid = Long.valueOf(basStorehouseLocation.getStorehouseLocationSid());
                        }
                    }
                    if (objects.get(16) != null && objects.get(16) != "") {
                        String name = purchaseOrgMaps.get(objects.get(16).toString());
                        if (name == null) {
                            throw new BaseException("第" + num + "行,没有编码为" + objects.get(16).toString() + "的采购组织，导入失败");
                        }
                    }
                    if (objects.get(17) != null && objects.get(17) != "") {
                        String name = purchaseGroupMaps.get(objects.get(17).toString());
                        if (name == null) {
                            throw new BaseException("第" + num + "行,没有编码为" + objects.get(17).toString() + "的采购组，导入失败");
                        }
                    }
                    purPurchaseOrder
                            .setVendorSid(Long.valueOf(vendorSid))
                            .setDocumentType(valuePurchaseDocument)
                            .setBusinessType(valuePurchaseType)
                            .setCompanySid(companySid)
                            .setPurchaseMode(valuePriceType)
                            .setProductSeasonSid(productSeasonSid)
                            .setBuyer(objects.get(5).toString())
                            .setDocumentDate(documnetDate)
                            .setRawMaterialMode(valueRawMaterial)
                            .setPurchaseContractSid(purchaseContractSid)
                            .setPurchaseContractCode(purchaseContractCode)
                            .setMaterialType((objects.get(10) == "" || objects.get(10) == null) ? null : objects.get(10).toString())
                            .setStorehouseSid(basStorehouseSid)
                            .setStorehouseLocationSid(storehouseLocationSid)
                            .setConsignee((objects.get(13) == "" || objects.get(13) == null) ? null : objects.get(13).toString())
                            .setConsigneePhone((objects.get(14) == "" || objects.get(14) == null) ? null : objects.get(14).toString())
                            .setConsigneeAddr((objects.get(15) == "" || objects.get(15) == null) ? null : objects.get(15).toString())
                            .setPurchaseOrg((objects.get(16) == "" || objects.get(16) == null) ? null : objects.get(16).toString())
                            .setPurchaseGroup((objects.get(17) == "" || objects.get(17) == null) ? null : objects.get(17).toString())
                            .setRemark((objects.get(18) == "" || objects.get(18) == null) ? null : objects.get(18).toString())
                            .setHandleStatus(ConstantsEms.SAVA_STATUS)
                            .setMaterialCategory(ConstantsEms.MATERIAL_CATEGORY_WL);
                    continue;
                }
                List<Object> objects = readAll.get(i);
                copy(objects, readAll);
                if (objects.get(0) == null || objects.get(0) == "") {
                    throw new BaseException("第" + num + "行,物料编码不可为空，导入失败");
                }
                BasMaterial basMaterial = basMaterialMapper.selectOne(new QueryWrapper<BasMaterial>().lambda()
                        .eq(BasMaterial::getMaterialCode, objects.get(0).toString())
                );
                if (basMaterial == null) {
                    throw new BaseException("第" + num + "行,没有编码为" + objects.get(0).toString() + "的物料，导入失败");
                } else {
                    materialSid = basMaterial.getMaterialSid();
                    unitBase = basMaterial.getUnitBase();
                    materialName = basMaterial.getMaterialName();
                }
                if (objects.get(1) == null || objects.get(1) == "") {
                    throw new BaseException("第" + num + "行,SKU1编码不可为空，导入失败");
                }
                BasSku basSku = basSkuMapper.selectOne(new QueryWrapper<BasSku>().lambda()
                        .eq(BasSku::getSkuCode, objects.get(1).toString())
                );
                if (basSku == null) {
                    throw new BaseException("第" + num + "行,没有编码为" + objects.get(1).toString() + "的sku1，导入失败");
                } else {
                    sku1Sid = basSku.getSkuSid();
                }
                if (objects.get(2) == null || objects.get(2) == "") {
                    throw new BaseException("第" + num + "行,SKU2编码不可为空，导入失败");
                }
                BasSku basSku2 = basSkuMapper.selectOne(new QueryWrapper<BasSku>().lambda()
                        .eq(BasSku::getSkuCode, objects.get(2).toString())
                );
                if (basSku2 == null) {
                    throw new BaseException("第" + num + "行,没有编码为" + objects.get(2).toString() + "的sku2，导入失败");
                } else {
                    sku2Sid = basSku2.getSkuSid();
                }
                if (objects.get(3) == null || objects.get(3) == "") {
                    throw new BaseException("第" + num + "行,订单量 不可为空，导入失败");
                }
                if (objects.get(4) == null || objects.get(4) == "") {
                    throw new BaseException("第" + num + "行,退货价(含税) 不可为空，导入失败");
                }
                if (objects.get(5) == null || objects.get(5) == "") {
                    throw new BaseException("第" + num + "行,合同交期 不可为空，导入失败");
                }
                boolean validDate = JudgeFormat.isValidDate(objects.get(5).toString());
                if (!validDate) {
                    throw new BaseException("第" + num + "行,合同交期，格式错误，导入失败");
                }
                if (sku1Sid != null && sku2Sid != null) {
                    basMaterialBarcode = basMaterialBarcodeMapper.selectOne(new QueryWrapper<BasMaterialBarcode>().lambda()
                            .eq(BasMaterialBarcode::getMaterialSid, materialSid)
                            .eq(BasMaterialBarcode::getSku1Sid, sku1Sid)
                            .eq(BasMaterialBarcode::getSku2Sid, sku2Sid)
                    );
                } else if (sku1Sid != null && sku2Sid == null) {
                    basMaterialBarcode = basMaterialBarcodeMapper.selectOne(new QueryWrapper<BasMaterialBarcode>().lambda()
                            .eq(BasMaterialBarcode::getMaterialSid, materialSid)
                            .eq(BasMaterialBarcode::getSku1Sid, sku1Sid)
                    );
                }
                if (basMaterialBarcode == null) {
                    throw new BaseException("第" + num + "行,不存在物料编码为" + objects.get(0) + "sku1编码为" + objects.get(1) + "" + "sku2编码为" + objects.get(2) + "的商品条码，导入失败");
                } else {
                    barcodeSid = basMaterialBarcode.getBarcodeSid();
                }
                PurPurchasePrice purPurchase = new PurPurchasePrice();
                BeanCopyUtils.copyProperties(purPurchaseOrder, purPurchase);
                purPurchase.setSku1Sid(sku1Sid)
                        .setMaterialSid(materialSid);
                PurPurchasePriceItem purchasePrice = priceService.getPurchasePrice(purPurchase);
                if (purchasePrice != null) {
                    purchasePriceTax = purchasePrice.getPurchasePriceTax();
                }
                PurPurchaseOrderItem purPurchaseOrderItem = new PurPurchaseOrderItem();
                purPurchaseOrderItem.setSku1Sid(sku1Sid)
                        .setSku2Sid(sku2Sid)
                        .setMaterialSid(materialSid)
                        .setBarcodeSid(barcodeSid)
                        .setQuantity(BigDecimal.valueOf(Long.valueOf(objects.get(3).toString())))
                        .setPurchasePriceTax(BigDecimal.valueOf(Long.valueOf(objects.get(4).toString())))
                        .setMaterialName(materialName)
                        .setContractDate(DateUtil.parse(objects.get(5).toString()))
                        .setRemark((objects.get(6) == "" || objects.get(6) == null) ? null : objects.get(6).toString())
                        .setUnitBase(unitBase);
                purPurchaseOrderItems.add(purPurchaseOrderItem);
            }
            purPurchaseOrder.setPurPurchaseOrderItemList(purPurchaseOrderItems);
            purPurchaseOrder.setImportType(BusinessType.IMPORT.getValue());
            insertPurPurchaseOrder(purPurchaseOrder);
        } catch (BaseException e) {
            throw new BaseException(e.getDefaultMessage());
        }

        return 1;
    }

    /**
     * 商品采购订单 导入
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult importDataG(MultipartFile file) {
        try {
            File toFile = null;
            try {
                toFile = FileUtils.multipartFileToFile(file);
            } catch (Exception e) {
                e.getMessage();
                throw new BaseException("文件转换失败");
            }
            ExcelReader reader = cn.hutool.poi.excel.ExcelUtil.getReader(toFile);
            FileUtils.delteTempFile(toFile);
            List<List<Object>> readAll = reader.read();
            //采购订单-单据类型
            Map<String, String> purchaseDocumentMaps = conDocTypePurchaseOrderMapper.getList().stream().collect(Collectors.toMap(ConDocTypePurchaseOrder::getName, ConDocTypePurchaseOrder::getCode, (key1, key2) -> key2));
            //采购订单-业务类型
            Map<String, String> purchaseTypeMaps = conBuTypePurchaseOrderMapper.getList().stream().collect(Collectors.toMap(ConBuTypePurchaseOrder::getName, ConBuTypePurchaseOrder::getCode, (key1, key2) -> key2));
            // 租户配置
            SysDefaultSettingClient settingClient = settingClientMapper.selectOne(new QueryWrapper<SysDefaultSettingClient>().lambda()
                    .eq(SysDefaultSettingClient::getClientId, ApiThreadLocalUtil.get().getClientId()));
            //客供料 方式
            sysDictDataService.deleteDictData("s_raw_material_mode");
            List<DictData> rawMaterialMode = sysDictDataService.selectDictData("s_raw_material_mode");
            Map<String, String> rawMaterialModeMaps = rawMaterialMode.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            //采购模式
            sysDictDataService.deleteDictData("s_price_type");
            List<DictData> priceType = sysDictDataService.selectDictData("s_price_type");
            Map<String, String> priceTypeMaps = priceType.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            //销售渠道
            Map<String, String> conSaleChannelMaps = conSaleChannelMapper.getConSaleChannelList().stream().collect(Collectors.toMap(ConSaleChannel::getCode, ConSaleChannel::getName, (key1, key2) -> key2));
            //物料类型
            Map<String, String> materailTypeMaps = conMaterialTypeMapper.getConMaterialTypeList().stream().collect(Collectors.toMap(ConMaterialType::getName, ConMaterialType::getCode, (key1, key2) -> key2));
            //采购组织
            Map<String, String> purchaseOrgMaps = conPurchaseOrgMapper.getConPurchaseOrgList().stream().collect(Collectors.toMap(ConPurchaseOrg::getCode, ConPurchaseOrg::getName, (key1, key2) -> key2));
            //采购组
            Map<String, String> purchaseGroupMaps = conPurchaseGroupMapper.getList().stream().collect(Collectors.toMap(ConPurchaseGroup::getCode, ConPurchaseGroup::getName, (key1, key2) -> key2));
            //配送方式
            Map<String, String> shipmentMaps = conShipmentModeMapper.getList().stream().collect(Collectors.toMap(ConShipmentMode::getName, ConShipmentMode::getCode, (key1, key2) -> key2));

            SysDefaultSettingClient client = getClientSetting();

            ArrayList<PurPurchaseOrderItem> purPurchaseOrderItems = new ArrayList<>();
            PurPurchaseOrder purPurchaseOrder = new PurPurchaseOrder();
            List<CommonErrMsgResponse> msgList = new ArrayList<>();
            int size = readAll.size();
            if (readAll.size() < 6) {
                throw new BaseException("明细行不能为空，导入失败");
            }
            for (int i = 0; i < readAll.size(); i++) {
                Long basStorehouseSid = null;
                Long storehouseLocationSid = null;
                Long companySid = null;
                Long vendorSid = null;
                Long productSeasonSid = null;
                Long sku1Sid = null;
                Long sku2Sid = null;
                Long materialSid = null;
                String materialCode = null;
                Long barcodeSid = null;
                Long purchaseContractSid = null;
                String materialName = null;
                BigDecimal purchasePriceTax = null;
                String unitBase = null;
                String inOutStockStatus = null;
                String isReturn = null;
                BasMaterialBarcode basMaterialBarcode = null;
                String materialType = null;
                String valuePurchaseDocument = null;
                String valuePurchaseType = null;
                String valuePurchaseDocumentTemp = null;
                String valuePurchaseTypeTemp = null;
                String isReturnGoods = null;
                String valuePriceType = null;
                String purchaseMode = null;
                Date documnetDate = null;
                String valueRawMaterial = null;
                BigDecimal quantity = null;
                Date contractDate = null;
                String advanceSettleMode = null;
                String vendorGroup = null;
                Long skuGoup = null;
                String conShipmentModeCode = null;
                int num = i + 1;
                if (i < 2 || i == 3 || i == 4) {
                    //前两行跳过
                    continue;
                }
                if (i == 2) {
                    List<Object> objects = readAll.get(i);
                    copy(objects, readAll);
                    if (objects.get(0) == null || objects.get(0) == "") {
                        // throw new BaseException("第"+num+"行,供应商简称，不能为空，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("供应商简称，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    } else {
                        String bendorCode = objects.get(0).toString();
                        BasVendor basVendor = basVendorMapper.selectOne(new QueryWrapper<BasVendor>()
                                .lambda().eq(BasVendor::getShortName, bendorCode)
                        );
                        if (basVendor == null) {
                            //throw new BaseException("第"+num+"行,供应商简称为" + bendorCode + "没有对应的供应商，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("供应商简称为" + bendorCode + "没有对应的供应商，导入失败");
                            msgList.add(errMsgResponse);
                        } else {
                            if (ConstantsEms.CHECK_STATUS.equals(basVendor.getHandleStatus()) && ConstantsEms.SAVA_STATUS.equals(basVendor.getStatus())) {
                                vendorSid = basVendor.getVendorSid();
                                vendorGroup = basVendor.getVendorGroup();
                            } else {
                                // throw new BaseException("第"+num+"行,供应商简称必须是启用且已确认状态，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(num);
                                errMsgResponse.setMsg("供应商简称必须是启用&已确认状态，导入失败");
                                msgList.add(errMsgResponse);
                            }
                        }
                    }
                    if (objects.get(1) == null || objects.get(1) == "") {
                        //   throw new BaseException("第"+num+"行,单据类型名称，不能为空，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("单据类型名称，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    } else {
                        valuePurchaseDocument = purchaseDocumentMaps.get(objects.get(1).toString());
                        if (valuePurchaseDocument == null) {
                            // throw new BaseException("第"+num+"行,单据类型名称配置错误，请联系管理员，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("单据类型名称配置错误，请联系管理员，导入失败");
                            msgList.add(errMsgResponse);
                        } else {
                            ConDocTypePurchaseOrder conDocTypePurchaseOrder = conDocTypePurchaseOrderMapper.selectOne(new QueryWrapper<ConDocTypePurchaseOrder>().lambda()
                                    .eq(ConDocTypePurchaseOrder::getCode, valuePurchaseDocument)
                            );
                            if (!ConstantsEms.CHECK_STATUS.equals(conDocTypePurchaseOrder.getHandleStatus()) || !ConstantsEms.ENABLE_STATUS.equals(conDocTypePurchaseOrder.getStatus())) {
                                //  throw new BaseException("第"+num+"行,单据类型必须是启用&已确认状态，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(num);
                                errMsgResponse.setMsg("单据类型必须是启用&已确认状态，导入失败");
                                msgList.add(errMsgResponse);
                            } else {
                                valuePurchaseDocumentTemp = valuePurchaseDocument;
                                purchaseMode = conDocTypePurchaseOrder.getPurchaseMode();
                            }
                        }
                    }
                    if (objects.get(2) == null || objects.get(2) == "") {
                        // throw new BaseException("第"+num+"行,业务类型名称，不能为空，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("业务类型名称，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    } else {
                        valuePurchaseType = purchaseTypeMaps.get(objects.get(2).toString());
                        if (valuePurchaseType == null) {
                            //  throw new BaseException("第"+num+"行,业务类型名称配置错误，请联系管理员，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("业务类型名称配置错误，请联系管理员，导入失败");
                            msgList.add(errMsgResponse);
                        } else {
                            ConBuTypePurchaseOrder conBuTypePurchaseOrder = conBuTypePurchaseOrderMapper.selectOne(new QueryWrapper<ConBuTypePurchaseOrder>().lambda()
                                    .eq(ConBuTypePurchaseOrder::getCode, valuePurchaseType));
                            if (!ConstantsEms.CHECK_STATUS.equals(conBuTypePurchaseOrder.getHandleStatus()) || !ConstantsEms.ENABLE_STATUS.equals(conBuTypePurchaseOrder.getStatus())) {
                                throw new BaseException("第" + num + "行,业务类型必须是启用&已确认状态，导入失败");
                            } else {
                                valuePurchaseTypeTemp = valuePurchaseType;
                            }
                        }
                    }
                    if (valuePurchaseDocumentTemp != null && valuePurchaseTypeTemp != null) {
                        ConDocBuTypeGroupPo conDocBuTypeGroupPo = conDocBuTypeGroupPoMapper.selectOne(new QueryWrapper<ConDocBuTypeGroupPo>()
                                .lambda()
                                .eq(ConDocBuTypeGroupPo::getDocTypeCode, valuePurchaseDocument)
                                .eq(ConDocBuTypeGroupPo::getBuTypeCode, valuePurchaseType)
                                .eq(ConDocBuTypeGroupPo::getHandleStatus, ConstantsEms.CHECK_STATUS)
                                .eq(ConDocBuTypeGroupPo::getStatus, ConstantsEms.ENABLE_STATUS)
                        );
                        if (conDocBuTypeGroupPo == null) {
                            //throw new BaseException("第"+num+"行,业务类型与单据类型对应关系不匹配，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("业务类型与单据类型对应关系不匹配，导入失败");
                            msgList.add(errMsgResponse);
                        }
                    }
                    ConDocTypePurchaseOrder conDocTypePurchase = conDocTypePurchaseOrderMapper.selectOne(new QueryWrapper<ConDocTypePurchaseOrder>().lambda()
                            .eq(ConDocTypePurchaseOrder::getCode, valuePurchaseDocument)
                    );
                    if (ConstantsEms.YES.equals(isReturnGoods)) {
                        inOutStockStatus = "WCK";
                        isReturn = "Y";
                    } else {
                        inOutStockStatus = "WRK";
                        isReturn = "N";
                    }
                    if (objects.get(3) == null || objects.get(3) == "") {
                        // throw new BaseException("第"+num+"行,公司编码，不能为空，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("公司简称，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    } else {
                        String compamyCode = objects.get(3).toString();
                        BasCompany company = basCompanyMapper.selectOne(new QueryWrapper<BasCompany>()
                                .lambda().eq(BasCompany::getShortName, compamyCode));
                        if (company == null) {
                            // throw new BaseException("第"+num+"行,公司编码为" + compamyCode + "没有对应的公司，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("公司简称为" + compamyCode + "没有对应的公司，导入失败");
                            msgList.add(errMsgResponse);
                        } else {
                            if (ConstantsEms.CHECK_STATUS.equals(company.getHandleStatus()) && ConstantsEms.SAVA_STATUS.equals(company.getStatus())) {
                                companySid = company.getCompanySid();
                            } else {
                                // throw new BaseException("第"+num+"行,公司编码必须是启用且已确认状态，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(num);
                                errMsgResponse.setMsg("公司简称必须是启用且已确认状态，导入失败");
                                msgList.add(errMsgResponse);
                            }
                        }
                    }
                    String productSeasonName = objects.get(4) == null || objects.get(4) == "" ? null : objects.get(4).toString().trim();
                    if (ConstantsEms.YES.equals(client.getIsXiefuIndustry()) && StrUtil.isBlank(productSeasonName)) {
                        //throw new BaseException("第"+num+"行,产品季名称，不能为空，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("下单季名称，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    } else if (StrUtil.isNotBlank(productSeasonName)) {
                        BasProductSeason productSeason = basProductSeasonMapper.selectOne(new QueryWrapper<BasProductSeason>()
                                .lambda().eq(BasProductSeason::getProductSeasonName, productSeasonName));
                        if (productSeason == null) {
                            //throw new BaseException("第"+num+"行,产品季名称为" + productSeasonName + "没有对应的产品季，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("下单季名称为" + productSeasonName + "没有对应的下单季，导入失败");
                            msgList.add(errMsgResponse);
                        } else {
                            if (!ConstantsEms.CHECK_STATUS.equals(productSeason.getHandleStatus()) || !ConstantsEms.ENABLE_STATUS.equals(productSeason.getStatus())) {
                                // throw new BaseException("第"+num+"行,产品季必须是启用&已确认状态，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(num);
                                errMsgResponse.setMsg("下单季必须是启用&已确认状态，导入失败");
                                msgList.add(errMsgResponse);
                            }
                            productSeasonSid = productSeason.getProductSeasonSid();
                        }
                    }
                    if (objects.get(5) == null || objects.get(5) == "") {
                        // throw new BaseException("第"+num+"行,采购员账号，不能为空，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("采购员账号，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    } else {
                        try {
                            SysUser sysUser = sysUserMapper.selectOne(new QueryWrapper<SysUser>().lambda()
                                    .eq(SysUser::getUserName, objects.get(5).toString())
                                    .eq(SysUser::getClientId, ApiThreadLocalUtil.get().getClientId()));
                            if (sysUser == null) {
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(num);
                                errMsgResponse.setMsg("没有账号为" + objects.get(5).toString() + "的采购员,导入失败");
                                msgList.add(errMsgResponse);
                            } else {
                                String status = sysUser.getStatus();
                                if (!"0".equals(status)) {
                                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                    errMsgResponse.setItemNum(num);
                                    errMsgResponse.setMsg("采购员账号必须是启用状态，导入失败");
                                    msgList.add(errMsgResponse);
                                }
                            }
                        } catch (TooManyResultsException e) {
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("采购员账号存在重复，请先检查该采购员账号，导入失败");
                            msgList.add(errMsgResponse);
                        }
                    }
                    if (objects.get(6) == null || objects.get(6) == "") {
                        // throw new BaseException("第"+num+"行,单据日期，不能为空，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("单据日期，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    } else {
                        boolean validDate = JudgeFormat.isValidDate(objects.get(6).toString());
                        if (!validDate) {
                            //  throw new BaseException("第"+num+"行,单据日期，格式错误，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("单据日期，格式错误，导入失败");
                            msgList.add(errMsgResponse);
                        } else {
                            String documnet = objects.get(6).toString();
                            documnetDate = DateUtil.parse(documnet);
                        }
                    }
                    if (objects.get(7) == null || objects.get(7) == "") {
                        // throw new BaseException("第"+num+"行,甲供料方式，不能为空，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("甲供料方式，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    } else {
                        valueRawMaterial = rawMaterialModeMaps.get(objects.get(7).toString());
                        if (valueRawMaterial == null) {
                            //throw new BaseException("第"+num+"行,甲供料方式配置错误，请联系管理员，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("甲供料方式配置错误，请联系管理员，导入失败");
                            msgList.add(errMsgResponse);
                        } else {
                            String value = valueRawMaterial;
                            List<DictData> list = rawMaterialMode.stream()
                                    .filter(m -> ConstantsEms.CHECK_STATUS.equals(m.getHandleStatus()) && "0".equals(m.getStatus()) && value.equals(m.getDictValue()))
                                    .collect(Collectors.toList());
                            if (CollectionUtil.isEmpty(list)) {
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(num);
                                errMsgResponse.setMsg("甲供料方式配置错误，请联系管理员，导入失败");
                                msgList.add(errMsgResponse);
                            }
                        }
                    }
                    if (objects.get(8) == null || objects.get(8) == "") {
                        //  throw new BaseException("第"+num+"行,采购模式，不能为空，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("采购模式，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    } else {
                        valuePriceType = priceTypeMaps.get(objects.get(8).toString());
                        if (valuePriceType == null) {
                            //  throw new BaseException("第"+num+"行,采购模式配置错误，请联系管理员，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("采购模式配置错误，请联系管理员，导入失败");
                            msgList.add(errMsgResponse);
                        } else {
                            String value = valuePriceType;
                            if (value != null) {
                                List<DictData> list = priceType.stream()
                                        .filter(m -> ConstantsEms.CHECK_STATUS.equals(m.getHandleStatus()) && "0".equals(m.getStatus()) && value.equals(m.getDictValue()))
                                        .collect(Collectors.toList());
                                if (CollectionUtil.isEmpty(list)) {
                                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                    errMsgResponse.setItemNum(num);
                                    errMsgResponse.setMsg("采购模式配置错误，请联系管理员，导入失败");
                                    msgList.add(errMsgResponse);
                                } else {
                                    if (purchaseMode != null) {
                                        if (!purchaseMode.equals(valuePriceType)) {
                                            // throw new BaseException("第"+num+"行,采购模式与单据类型对应关系不匹配，导入失败");
                                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                            errMsgResponse.setItemNum(num);
                                            errMsgResponse.setMsg("采购模式与单据类型对应关系不匹配，导入失败");
                                            msgList.add(errMsgResponse);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    /*
                    String paperPurchaseContractCode = null;
                    if (objects.get(9) != null && objects.get(9) != "") {
                        paperPurchaseContractCode = objects.get(9).toString();
                    }
                     */
                    String purPurchaseContractCode = null;
                    if (objects.get(9) == null || objects.get(9) == "") {
                        //throw new BaseException("第"+num+"行,采购合同号，不能为空");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("采购合同号，不能为空");
                        msgList.add(errMsgResponse);
                    } else {
                        purPurchaseContractCode = objects.get(9).toString();
                        if (settingClient == null || !ConstantsOrder.CONTRACT_ENTER_MODE_SG.equals(settingClient.getPurchaseOrderContractEnterMode())) {
                            List<PurPurchaseContract> purPurchase = purPurchaseContractMapper.selectList(new QueryWrapper<PurPurchaseContract>()
                                    .lambda().eq(PurPurchaseContract::getPurchaseContractCode, purPurchaseContractCode));
                            if (CollectionUtil.isEmpty(purPurchase)) {
                                //throw new BaseException("第"+num+"行,合同号校验不通过，导入失败！");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(num);
                                errMsgResponse.setMsg("合同号不存在，导入失败！");
                                msgList.add(errMsgResponse);
                            } else {
                                Long finalVendorSid = vendorSid;
                                Long finalCompanySid = companySid;
                                purPurchase = purPurchase.stream().filter(o -> o.getVendorSid().equals(finalVendorSid) && o.getCompanySid().equals(finalCompanySid)).collect(Collectors.toList());
                                if (CollectionUtil.isEmpty(purPurchase)) {
                                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                    errMsgResponse.setItemNum(num);
                                    errMsgResponse.setMsg("合同的“供应商+公司”与订单的“供应商+公司”不一致，导入失败！");
                                    msgList.add(errMsgResponse);
                                } else {
                                    purchaseContractSid = purPurchase.get(0).getPurchaseContractSid();
                                    advanceSettleMode = purPurchase.get(0).getAdvanceSettleMode();
                                }
                            }
                        }
                    }
                    if (objects.get(10) != null && objects.get(10) != "") {
                        ConMaterialType conMaterialType = conMaterialTypeMapper.selectOne(new QueryWrapper<ConMaterialType>().lambda()
                                .eq(ConMaterialType::getName, objects.get(10).toString())
                        );
                        if (conMaterialType == null) {
                            //  throw new BaseException("第"+num+"行,物料类型为" + code + "没有对应的物料类型，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("物料类型为" + objects.get(10).toString() + "没有对应的物料类型，导入失败");
                            msgList.add(errMsgResponse);
                        } else {
                            if (!ConstantsEms.CHECK_STATUS.equals(conMaterialType.getHandleStatus()) || !ConstantsEms.ENABLE_STATUS.equals(conMaterialType.getStatus())) {
                                // throw new BaseException("第"+num+"行,仓库必须是确认且已启用状态，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(num);
                                errMsgResponse.setMsg("物料类型是确认且已启用状态，导入失败");
                                msgList.add(errMsgResponse);
                            } else {
                                materialType = conMaterialType.getCode();
                            }
                        }
                    }
                    if (objects.get(11) != null && objects.get(11) != "") {
                        BasStorehouse basStorehouse = basStorehouseMapper.selectOne(new QueryWrapper<BasStorehouse>().lambda()
                                .eq(BasStorehouse::getStorehouseName, objects.get(11).toString())
                        );
                        if (basStorehouse == null) {
                            // throw new BaseException("第"+num+"行,没有编码为" + objects.get(11).toString() + "的仓库，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("没有名称为" + objects.get(11).toString() + "的仓库，导入失败");
                            msgList.add(errMsgResponse);
                        } else {
                            if (!ConstantsEms.CHECK_STATUS.equals(basStorehouse.getHandleStatus()) || !ConstantsEms.ENABLE_STATUS.equals(basStorehouse.getStatus())) {
                                //  throw new BaseException("第"+num+"行,仓库必须是启用&已确认状态，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(num);
                                errMsgResponse.setMsg("仓库必须是启用&已确认状态，导入失败");
                                msgList.add(errMsgResponse);
                            }
                            basStorehouseSid = Long.valueOf(basStorehouse.getStorehouseSid());
                        }
                    }
                    if (objects.get(12) != null && objects.get(12) != "") {
                        BasStorehouseLocation basStorehouseLocation = basStorehouseLocationMapper.selectOne(new QueryWrapper<BasStorehouseLocation>().lambda()
                                .eq(BasStorehouseLocation::getStorehouseSid, basStorehouseSid)
                                .eq(BasStorehouseLocation::getLocationName, objects.get(12).toString())
                        );
                        if (basStorehouseLocation == null) {
                            //  throw new BaseException("第"+num+"行,没有编码为" + objects.get(12).toString() + "的库位，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg(objects.get(11).toString() + "下，没有名称为" + objects.get(12).toString() + "的库位，导入失败");
                            msgList.add(errMsgResponse);
                        } else {
                            if (!ConstantsEms.CHECK_STATUS.equals(basStorehouseLocation.getHandleStatus()) || !ConstantsEms.ENABLE_STATUS.equals(basStorehouseLocation.getStatus())) {
                                // throw new BaseException("第"+num+"行,库位必须是启用&已确认状态，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(num);
                                errMsgResponse.setMsg("库位必须是启用&已确认状态，导入失败");
                                msgList.add(errMsgResponse);
                            }
                            storehouseLocationSid = Long.valueOf(basStorehouseLocation.getStorehouseLocationSid());
                        }
                    }
                    if (objects.get(16) != null && objects.get(16) != "") {
                        String name = purchaseOrgMaps.get(objects.get(16).toString());
                        if (name == null) {
                            // throw new BaseException("第"+num+"行,没有编码为" + objects.get(16).toString() + "的采购组织，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("没有编码为" + objects.get(16).toString() + "的采购组织，导入失败");
                            msgList.add(errMsgResponse);
                        }
                    }
                    if (objects.get(17) != null && objects.get(17) != "") {
                        String name = purchaseGroupMaps.get(objects.get(17).toString());
                        if (name == null) {
                            // throw new BaseException("第"+num+"行,没有编码为" + objects.get(17).toString() + "的采购组，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("没有编码为" + objects.get(17).toString() + "的采购组，导入失败");
                            msgList.add(errMsgResponse);
                        }
                    }
                    if (objects.get(18) != null && objects.get(18) != "") {
                        ConShipmentMode conShipmentMode = conShipmentModeMapper.selectOne(new QueryWrapper<ConShipmentMode>().lambda()
                                .eq(ConShipmentMode::getName, objects.get(18).toString())
                        );
                        if (conShipmentMode == null) {
                            //throw new BaseException("第"+num+"行,没有名称为" + objects.get(18).toString() + "的配送方式，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("没有名称为" + objects.get(18).toString() + "的配送方式，导入失败");
                            msgList.add(errMsgResponse);
                        } else {
                            if (!ConstantsEms.CHECK_STATUS.equals(conShipmentMode.getHandleStatus()) || !ConstantsEms.ENABLE_STATUS.equals(conShipmentMode.getStatus())) {
                                // throw new BaseException("第"+num+"行,库位必须是启用&已确认状态，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(num);
                                errMsgResponse.setMsg("配送方式必须是启用&已确认状态，导入失败");
                                msgList.add(errMsgResponse);
                            } else {
                                conShipmentModeCode = conShipmentMode.getCode();
                            }
                        }
                    }
                    if (objects.get(14) != null && objects.get(14) != "") {
                        boolean phone = JudgeFormat.isPhone(objects.get(14).toString());
                        if (!phone) {
                            // throw new BaseException("第"+num+"行,手机格式错误，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("收货人联系电话，格式错误，导入失败");
                            msgList.add(errMsgResponse);
                        }
                    }
                    if (YCX_VENDOR.equals(vendorGroup)) {
                        if (objects.get(20) == "" || objects.get(20) == null) {
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("供应商名称备注，不能为空，导入失败");
                            msgList.add(errMsgResponse);
                        }
                    } else {
                        if (vendorSid != null) {
                            if (objects.get(20) != "" && objects.get(20) != null) {
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(num);
                                errMsgResponse.setMsg("供应商为非一次性供应商组时，供应商名称备注必须为空");
                                msgList.add(errMsgResponse);
                            }
                        }
                    }

                    // 纸质下单合同号
                    String paperPurchaseContractCode = objects.get(21) == null || objects.get(21) == "" ? null : objects.get(21).toString().trim();
                    if (StrUtil.isNotBlank(paperPurchaseContractCode)) {
                        if (paperPurchaseContractCode.length() > 120) {
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("纸质下单合同号最多不能超过120位，导入失败");
                            msgList.add(errMsgResponse);
                        }
                    }

                    purPurchaseOrder
                            .setVendorSid(vendorSid)
                            .setAdvanceSettleMode(advanceSettleMode)
                            .setDocumentType(valuePurchaseDocument)
                            .setBusinessType(valuePurchaseType)
                            .setInOutStockStatus(inOutStockStatus)
                            .setCompanySid(companySid)
                            .setPurchaseMode(valuePriceType)
                            .setProductSeasonSid(productSeasonSid)
                            .setBuyer((objects.get(5) == "" || objects.get(5) == null) ? null : objects.get(5).toString())
                            .setImportHandle(HandleStatus.SUBMIT.getCode())
                            .setIsConsignmentSettle("N")
                            .setIsReturnGoods(isReturn)
                            .setDocumentDate(documnetDate)
                            .setCurrency("CNY")
                            .setCurrencyUnit("YUAN")
                            .setRawMaterialMode(valueRawMaterial)
                            .setPurchaseContractSid(purchaseContractSid)
                            .setPurchaseContractCode(purPurchaseContractCode)
                            .setMaterialType((objects.get(10) == "" || objects.get(10) == null) ? null : materailTypeMaps.get(objects.get(10).toString()))
                            .setStorehouseSid(basStorehouseSid)
                            .setStorehouseLocationSid(storehouseLocationSid)
                            .setConsignee((objects.get(13) == "" || objects.get(13) == null) ? null : objects.get(13).toString())
                            .setConsigneePhone((objects.get(14) == "" || objects.get(14) == null) ? null : objects.get(14).toString())
                            .setConsigneeAddr((objects.get(15) == "" || objects.get(15) == null) ? null : objects.get(15).toString())
                            .setPurchaseOrg((objects.get(16) == "" || objects.get(16) == null) ? null : objects.get(16).toString())
                            .setPurchaseGroup((objects.get(17) == "" || objects.get(17) == null) ? null : objects.get(17).toString())
                            .setShipmentType((objects.get(18) == "" || objects.get(18) == null) ? null : conShipmentModeCode)
                            .setVendorBusinessman((objects.get(19) == "" || objects.get(19) == null) ? null : objects.get(19).toString())
                            .setVendorNameRemark((objects.get(20) == "" || objects.get(20) == null) ? null : objects.get(20).toString())
                            .setPaperPurchaseContractCode(paperPurchaseContractCode)
                            .setRemark((objects.get(22) == "" || objects.get(22) == null) ? null : objects.get(22).toString())
                            .setHandleStatus(ConstantsEms.SAVA_STATUS)
                            .setMaterialCategory(ConstantsEms.MATERIAL_CATEGORY_SP);

                    continue;
                }
                List<Object> objects = readAll.get(i);
                copy(objects, readAll);
                if (objects.get(0) == null || objects.get(0) == "") {
                    //  throw new BaseException("第"+num+"行,商品编码不可为空，导入失败");
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("商品编码不可为空，导入失败");
                    msgList.add(errMsgResponse);
                } else {
                    BasMaterial basMaterial = basMaterialMapper.selectOne(new QueryWrapper<BasMaterial>().lambda()
                            .eq(BasMaterial::getMaterialCode, objects.get(0).toString())
                    );
                    if (basMaterial == null) {
                        //  throw new BaseException("第"+num+"行,没有编码为"+objects.get(0).toString()+"的商品，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("没有编码为" + objects.get(0).toString() + "的商品，导入失败");
                        msgList.add(errMsgResponse);
                    } else {
                        if (ConstantsEms.CHECK_STATUS.equals(basMaterial.getHandleStatus()) && ConstantsEms.SAVA_STATUS.equals(basMaterial.getStatus())) {
                            materialSid = basMaterial.getMaterialSid();
                            unitBase = basMaterial.getUnitBase();
                            materialCode = basMaterial.getMaterialCode();
                            materialName = basMaterial.getMaterialName();
                            if (basMaterial.getSku2GroupSid() != null) {
                                skuGoup = basMaterial.getSku2GroupSid();
                            }
                            if (objects.get(1) == null || objects.get(1) == "") {
                                //  throw new BaseException("第"+num+"行,SKU1名称不可为空，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(num);
                                errMsgResponse.setMsg("SKU1名称不可为空，导入失败");
                                msgList.add(errMsgResponse);
                            } else {
                                BasSku basSku = basSkuMapper.selectOne(new QueryWrapper<BasSku>().lambda()
                                        .eq(BasSku::getSkuName, objects.get(1).toString())
                                        .eq(BasSku::getSkuType, basMaterial.getSku1Type())
                                );
                                if (basSku == null) {
                                    //throw new BaseException("第"+num+"行,SKU1名称为"+objects.get(1).toString()+",没有对应的SKU1名称，导入失败");
                                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                    errMsgResponse.setItemNum(num);
                                    errMsgResponse.setMsg("SKU1名称为" + objects.get(1).toString() + ",没有对应类型的SKU1名称，导入失败");
                                    msgList.add(errMsgResponse);
                                } else {
                                    sku1Sid = basSku.getSkuSid();
                                }
                            }
                            if (materialSid != null && sku1Sid != null) {
                                BasMaterialSku skuName = basMaterialSkuMapper.selectOne(new QueryWrapper<BasMaterialSku>().lambda()
                                        .eq(BasMaterialSku::getMaterialSid, materialSid)
                                        .eq(BasMaterialSku::getSkuSid, sku1Sid)
                                );
                                if (skuName == null) {
                                    //throw new BaseException("第"+num+"行,SKU1名称必须是所填商品当中已启用的颜色，导入失败");
                                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                    errMsgResponse.setItemNum(num);
                                    errMsgResponse.setMsg("SKU1名称必须是所填商品当中的颜色，导入失败");
                                    msgList.add(errMsgResponse);
                                } else {
                                    if (!ConstantsEms.ENABLE_STATUS.equals(skuName.getStatus())) {
                                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                        errMsgResponse.setItemNum(num);
                                        errMsgResponse.setMsg("SKU1名称必须是所填商品当中已启用的颜色，导入失败");
                                        msgList.add(errMsgResponse);
                                    }
                                }
                            }
                            if (objects.get(2) == null || objects.get(2) == "") {
                                //  throw new BaseException("第"+num+"行,SKU1名称不可为空，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(num);
                                errMsgResponse.setMsg("SKU2名称不可为空，导入失败");
                                msgList.add(errMsgResponse);
                            }
                            if (objects.get(2) != null && objects.get(2) != "") {
                                BasSku basSku2 = basSkuMapper.selectOne(new QueryWrapper<BasSku>().lambda()
                                        .eq(BasSku::getSkuName, objects.get(2).toString())
                                        .eq(BasSku::getSkuType, basMaterial.getSku2Type())
                                );
                                if (basSku2 == null) {
                                    // throw new BaseException("第"+num+"行,SKU2名称为"+objects.get(2).toString()+",没有对应的SKU2名称，导入失败");
                                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                    errMsgResponse.setItemNum(num);
                                    errMsgResponse.setMsg("SKU2名称为" + objects.get(2).toString() + ",没有对应类型的SKU2名称，导入失败");
                                    msgList.add(errMsgResponse);
                                } else {
                                    sku2Sid = basSku2.getSkuSid();
                                    BasMaterialSku basMaterialSkusSku = basMaterialSkuMapper.selectOne(new QueryWrapper<BasMaterialSku>().lambda()
                                            .eq(BasMaterialSku::getMaterialSid, materialSid)
                                            .eq(BasMaterialSku::getSkuSid, sku2Sid)
                                    );
                                    if (basMaterialSkusSku == null) {
                                        // throw new BaseException("第"+num+"行,SKU2名称必须是所填商品当中已启用的长度或尺码，导入失败");
                                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                        errMsgResponse.setItemNum(num);
                                        errMsgResponse.setMsg("SKU2名称必须是所填商品当中的长度或尺码，导入失败");
                                        msgList.add(errMsgResponse);
                                    } else {
                                        if (!ConstantsEms.ENABLE_STATUS.equals(basMaterialSkusSku.getStatus())) {
                                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                            errMsgResponse.setItemNum(num);
                                            errMsgResponse.setMsg("SKU2名称必须是所填商品当中已启用的长度或尺码，导入失败");
                                            msgList.add(errMsgResponse);
                                        }
                                    }
                                }
                            }
                            if (sku1Sid != null && sku2Sid != null) {
                                basMaterialBarcode = basMaterialBarcodeMapper.selectOne(new QueryWrapper<BasMaterialBarcode>().lambda()
                                        .eq(BasMaterialBarcode::getMaterialSid, materialSid)
                                        .eq(BasMaterialBarcode::getSku1Sid, sku1Sid)
                                        .eq(BasMaterialBarcode::getSku2Sid, sku2Sid)
                                );
                            } else if (sku1Sid != null && sku2Sid == null) {
                                basMaterialBarcode = basMaterialBarcodeMapper.selectOne(new QueryWrapper<BasMaterialBarcode>().lambda()
                                        .eq(BasMaterialBarcode::getMaterialSid, materialSid)
                                        .isNull(BasMaterialBarcode::getSku2Sid)
                                        .eq(BasMaterialBarcode::getSku1Sid, sku1Sid)
                                );
                            }
                            if (materialCode != null) {
                                if (basMaterialBarcode == null) {
                                    //throw new BaseException("第" + num + "行,不存在商品编码为" + objects.get(0) + ",sku1名称为" + objects.get(1) + "" + ",sku2名称为" + objects.get(2) + "的商品条码，导入失败");
                                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                    errMsgResponse.setItemNum(num);
                                    if (objects.get(2) == null || objects.get(2) == "") {
                                        errMsgResponse.setMsg("不存在商品编码为" + objects.get(0) + ",sku1名称为" + objects.get(1) + "的商品条码，导入失败");
                                    } else {
                                        errMsgResponse.setMsg("不存在商品编码为" + objects.get(0) + ",sku1名称为" + objects.get(1) + "" + ",sku2名称为" + objects.get(2) + "的商品条码，导入失败");
                                    }
                                    msgList.add(errMsgResponse);
                                } else {
                                    if (!ConstantsEms.ENABLE_STATUS.equals(basMaterialBarcode.getStatus())) {
                                        // throw new BaseException("第" + num + "行,商品编码为" + objects.get(0) + ",sku1名称为" + objects.get(1) + "" + ",sku2名称为" + objects.get(2) + "的商品条码不是启用状态，导入失败");
                                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                        errMsgResponse.setItemNum(num);
                                        if (objects.get(2) == null || objects.get(2) == "") {
                                            errMsgResponse.setMsg("商品编码为" + objects.get(0) + ",sku1名称为" + objects.get(1) + "的商品条码不是启用状态，导入失败");
                                        } else {
                                            errMsgResponse.setMsg("商品编码为" + objects.get(0) + ",sku1名称为" + objects.get(1) + "" + ",sku2名称为" + objects.get(2) + "的商品条码不是启用状态，导入失败");
                                        }
                                        msgList.add(errMsgResponse);
                                    }
                                    barcodeSid = basMaterialBarcode.getBarcodeSid();
                                }
                            }
                        } else {
                            //  throw new BaseException("第"+num+"行,商品编码必须是启用且已确认状态，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("商品编码必须是启用且已确认状态，导入失败");
                            msgList.add(errMsgResponse);
                        }
                    }
                }
                if (objects.get(3) == null || objects.get(3) == "") {
                    //throw new BaseException("第"+num+"行,订单量 不可为空，导入失败");
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("订单量 不可为空，导入失败");
                    msgList.add(errMsgResponse);
                } else {
                    boolean validDouble = JudgeFormat.isValidDouble(objects.get(3).toString());
                    if (!validDouble) {
                        //throw new BaseException("第"+num+"行,订单量格式错误，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("订单量格式错误，导入失败");
                        msgList.add(errMsgResponse);
                    } else {
                        quantity = BigDecimal.valueOf(Double.valueOf(objects.get(3).toString()));
                        if (quantity.compareTo(BigDecimal.ZERO) == -1 || quantity.compareTo(BigDecimal.ZERO) == 0) {
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("订单量不能小于等于0，导入失败");
                            msgList.add(errMsgResponse);
                        }
                    }
                }
                if (objects.get(4) == null || objects.get(4) == "") {
                    // throw new BaseException("第"+num+"行,合同交期 不可为空，导入失败");
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("合同交期 不可为空，导入失败");
                    msgList.add(errMsgResponse);
                } else {
                    boolean validDate = JudgeFormat.isValidDate(objects.get(4).toString());
                    if (!validDate) {
                        //   throw new BaseException("第"+num+"行,合同交期，格式错误，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("合同交期，格式错误，导入失败");
                        msgList.add(errMsgResponse);
                    } else {
                        contractDate = DateUtil.parse(objects.get(4).toString());
                    }
                }
                if ((objects.get(5) != "" && objects.get(5) != null)) {
                    if (!"是".equals(objects.get(5).toString()) && !"否".equals(objects.get(5).toString())) {
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("是否免费只能填是或者否，导入失败");
                        msgList.add(errMsgResponse);
                    }
                }

                //默认获取通用税率
                ConTaxRate taxRate = conTaxRateMapper.selectOne(new QueryWrapper<ConTaxRate>().lambda()
                        .eq(ConTaxRate::getIsDefault, "Y")
                );
                PurPurchaseOrderItem purPurchaseOrderItem = new PurPurchaseOrderItem();
                purPurchaseOrderItem.setSku1Sid(sku1Sid)
                        .setSku2Sid(sku2Sid)
                        .setMaterialSid(materialSid)
                        .setMaterialCode(materialCode)
                        .setInOutStockStatus(inOutStockStatus)
                        .setTaxRate(taxRate.getTaxRateValue())
                        .setBarcodeSid(barcodeSid)
                        .setSku2GroupSid(skuGoup)
                        .setQuantity(quantity)
                        .setPurchasePriceTax(purchasePriceTax)
                        .setMaterialName(materialName)
                        .setContractDate(contractDate)
                        .setFreeFlag((objects.get(5) == "" || objects.get(5) == null) ? null : ("是".equals(objects.get(5).toString()) ? "Y" : null))
                        .setRemark((objects.get(6) == "" || objects.get(6) == null) ? null : objects.get(6).toString());
//                        .setUnitBase(unitBase);

                purPurchaseOrderItems.add(purPurchaseOrderItem);
            }
            if (CollectionUtil.isNotEmpty(msgList)) {
                return AjaxResult.error("报错信息", msgList);
            }
            purPurchaseOrder.setPurPurchaseOrderItemList(purPurchaseOrderItems);
            try {
                purPurchaseOrder.setImportType(BusinessType.IMPORT.getValue());
                insertPurPurchaseOrder(purPurchaseOrder);
            } catch (CustomException e) {
                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                errMsgResponse.setMsg(e.getMessage());
                msgList.add(errMsgResponse);
            }
            if (CollectionUtil.isNotEmpty(msgList)) {
                return AjaxResult.error("报错信息", msgList);
            }
        } catch (BaseException e) {
            throw new BaseException(e.getDefaultMessage());
        }

        return AjaxResult.success(1);
    }

    /**
     * 商品采购退货订单 导入
     */
    @Override
    public int importDataGre(MultipartFile file) {
        Long basStorehouseSid = null;
        Long storehouseLocationSid = null;
        Long companySid = null;
        Long vendorSid = null;
        Long productSeasonSid = null;
        Long sku1Sid = null;
        Long sku2Sid = null;
        Long materialSid = null;
        Long barcodeSid = null;
        Long purchaseContractSid = null;
        String materialName = null;
        BigDecimal purchasePriceTax = null;
        String unitBase = null;
        BasMaterialBarcode basMaterialBarcode = null;
        try {
            File toFile = null;
            try {
                toFile = FileUtils.multipartFileToFile(file);
            } catch (Exception e) {
                e.getMessage();
                throw new BaseException("文件转换失败");
            }
            ExcelReader reader = cn.hutool.poi.excel.ExcelUtil.getReader(toFile);
            FileUtils.delteTempFile(toFile);
            List<List<Object>> readAll = reader.read();
            //采购订单-单据类型
            Map<String, String> purchaseDocumentMaps = conDocTypePurchaseOrderMapper.getList().stream().collect(Collectors.toMap(ConDocTypePurchaseOrder::getName, ConDocTypePurchaseOrder::getCode, (key1, key2) -> key2));
            //采购订单-业务类型
            Map<String, String> purchaseTypeMaps = conBuTypePurchaseOrderMapper.getList().stream().collect(Collectors.toMap(ConBuTypePurchaseOrder::getName, ConBuTypePurchaseOrder::getCode, (key1, key2) -> key2));
            // 租户配置
            SysDefaultSettingClient settingClient = settingClientMapper.selectOne(new QueryWrapper<SysDefaultSettingClient>().lambda()
                    .eq(SysDefaultSettingClient::getClientId, ApiThreadLocalUtil.get().getClientId()));
            //客供料 方式
            List<DictData> rawMaterialMode = sysDictDataService.selectDictData("s_raw_material_mode");
            Map<String, String> rawMaterialModeMaps = rawMaterialMode.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            //采购模式
            List<DictData> priceType = sysDictDataService.selectDictData("s_price_type");
            Map<String, String> priceTypeMaps = priceType.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            //销售渠道
            Map<String, String> conSaleChannelMaps = conSaleChannelMapper.getConSaleChannelList().stream().collect(Collectors.toMap(ConSaleChannel::getCode, ConSaleChannel::getName, (key1, key2) -> key2));
            //物料类型
            Map<String, String> materailTypeMaps = conMaterialTypeMapper.getConMaterialTypeList().stream().collect(Collectors.toMap(ConMaterialType::getName, ConMaterialType::getCode, (key1, key2) -> key2));
            //采购组织
            Map<String, String> purchaseOrgMaps = conPurchaseOrgMapper.getConPurchaseOrgList().stream().collect(Collectors.toMap(ConPurchaseOrg::getCode, ConPurchaseOrg::getName, (key1, key2) -> key2));
            //采购组
            Map<String, String> purchaseGroupMaps = conPurchaseGroupMapper.getList().stream().collect(Collectors.toMap(ConPurchaseGroup::getCode, ConPurchaseGroup::getName, (key1, key2) -> key2));
            ArrayList<PurPurchaseOrderItem> purPurchaseOrderItems = new ArrayList<>();
            PurPurchaseOrder purPurchaseOrder = new PurPurchaseOrder();
            for (int i = 0; i < readAll.size(); i++) {
                if (i < 2 || i == 3 || i == 4) {
                    //前两行跳过
                    continue;
                }
                if (i == 2) {
                    List<Object> objects = readAll.get(i);
                    copy(objects, readAll);
                    if (objects.get(0) == null || objects.get(0) == "") {
                        throw new BaseException("供应商编码，不能为空");
                    }
                    String bendorCode = objects.get(0).toString();
                    BasVendor basVendor = basVendorMapper.selectOne(new QueryWrapper<BasVendor>()
                            .lambda().eq(BasVendor::getVendorCode, bendorCode));
                    if (basVendor == null) {
                        throw new BaseException("供应商编码为" + bendorCode + "没有对应的供应商");
                    } else {
                        vendorSid = basVendor.getVendorSid();
                    }

                    if (objects.get(1) == null || objects.get(1) == "") {
                        throw new BaseException("单据类型名称，不能为空");
                    }
                    String valuePurchaseDocument = purchaseDocumentMaps.get(objects.get(1).toString());
                    if (valuePurchaseDocument == null) {
                        throw new BaseException("单据类型名称配置错误，请联系管理员");
                    }
                    if (objects.get(2) == null || objects.get(2) == "") {
                        throw new BaseException("业务类型名称，不能为空");
                    }
                    String valuePurchaseType = purchaseTypeMaps.get(objects.get(2).toString());
                    if (valuePurchaseType == null) {
                        throw new BaseException("业务类型名称配置错误，请联系管理员");
                    }
                    if (objects.get(3) == null || objects.get(3) == "") {
                        throw new BaseException("公司编码，不能为空");
                    }
                    String compamyCode = objects.get(3).toString();
                    BasCompany company = basCompanyMapper.selectOne(new QueryWrapper<BasCompany>()
                            .lambda().eq(BasCompany::getCompanyCode, compamyCode));
                    if (company == null) {
                        throw new BaseException("公司编码为" + compamyCode + "没有对应的公司");
                    } else {
                        companySid = company.getCompanySid();
                    }
                    if (objects.get(4) == null || objects.get(4) == "") {
                        throw new BaseException("产品季，不能为空");
                    }
                    String productSeasonName = objects.get(4).toString();
                    BasProductSeason productSeason = basProductSeasonMapper.selectOne(new QueryWrapper<BasProductSeason>()
                            .lambda().eq(BasProductSeason::getProductSeasonName, productSeasonName));
                    if (productSeason == null) {
                        throw new BaseException("产品季名称为" + productSeasonName + "没有对应的产品季");
                    } else {
                        productSeasonSid = productSeason.getProductSeasonSid();
                    }
                    if (objects.get(5) == null || objects.get(5) == "") {
                        throw new BaseException("销售员账号，不能为空");
                    }
                    if (objects.get(6) == null || objects.get(6) == "") {
                        throw new BaseException("单据日期，不能为空");
                    }
                    String documnet = objects.get(6).toString();
                    Date documnetDate = DateUtil.parse(documnet);
                    if (objects.get(7) == null || objects.get(7) == "") {
                        throw new BaseException("客供料方式，不能为空");
                    }
                    String valueRawMaterial = rawMaterialModeMaps.get(objects.get(7).toString());
                    if (valueRawMaterial == null) {
                        throw new BaseException("客供料方式配置错误，请联系管理员");
                    }
                    if (objects.get(8) == null || objects.get(8) == "") {
                        throw new BaseException("采购模式，不能为空");
                    }
                    String valuePriceType = priceTypeMaps.get(objects.get(8).toString());
                    if (valuePriceType == null) {
                        throw new BaseException("采购模式配置错误，请联系管理员");
                    }
                    /*
                    String paperPurchaseContractCode = null;
                    if (objects.get(9) != null && objects.get(9) != "") {
                        paperPurchaseContractCode = objects.get(9).toString();
                    }
                    */
                    if (objects.get(9) == null || objects.get(9) == "") {
                        throw new BaseException("采购合同号，不能为空");
                    }
                    String purPurchaseContractCode = objects.get(9).toString();
                    if (settingClient == null || !ConstantsOrder.CONTRACT_ENTER_MODE_SG.equals(settingClient.getPurchaseOrderContractEnterMode())) {
                        PurPurchaseContract purPurchase = purPurchaseContractMapper.selectOne(new QueryWrapper<PurPurchaseContract>()
                                .lambda().eq(PurPurchaseContract::getPurchaseContractCode, purPurchaseContractCode));
                        if (purPurchase == null) {
                            throw new BaseException("采购合同号为" + purPurchaseContractCode + "没有对应的采购合同号");
                        } else {
                            purchaseContractSid = purPurchase.getPurchaseContractSid();
                        }
                    }
                    if (objects.get(10) != null && objects.get(10) != "") {
                        String code = objects.get(10).toString();
                        String name = materailTypeMaps.get(code);
                        if (name == null) {
                            throw new BaseException("物料类型为" + code + "没有对应的物料类型");
                        }
                    }
                    if (objects.get(11) != null && objects.get(11) != "") {
                        BasStorehouse basStorehouse = basStorehouseMapper.selectOne(new QueryWrapper<BasStorehouse>().lambda()
                                .eq(BasStorehouse::getStorehouseCode, objects.get(11).toString())
                        );
                        if (basStorehouse == null) {
                            throw new BaseException("没有编码为" + objects.get(11).toString() + "的仓库");
                        } else {
                            basStorehouseSid = Long.valueOf(basStorehouse.getStorehouseSid());
                        }
                    }
                    if (objects.get(12) != null && objects.get(12) != "") {
                        BasStorehouseLocation basStorehouseLocation = basStorehouseLocationMapper.selectOne(new QueryWrapper<BasStorehouseLocation>().lambda()
                                .eq(BasStorehouseLocation::getStorehouseSid, basStorehouseSid)
                                .eq(BasStorehouseLocation::getLocationCode, objects.get(12).toString())
                        );
                        if (basStorehouseLocation == null) {
                            throw new BaseException("没有编码为" + objects.get(12).toString() + "的库位");
                        } else {
                            storehouseLocationSid = Long.valueOf(basStorehouseLocation.getStorehouseLocationSid());
                        }
                    }
                    if (objects.get(16) != null && objects.get(16) != "") {
                        String name = purchaseOrgMaps.get(objects.get(16).toString());
                        if (name == null) {
                            throw new BaseException("没有编码为" + objects.get(16).toString() + "的采购组织");
                        }
                    }
                    if (objects.get(17) != null && objects.get(17) != "") {
                        String name = purchaseGroupMaps.get(objects.get(17).toString());
                        if (name == null) {
                            throw new BaseException("没有编码为" + objects.get(17).toString() + "的采购组");
                        }
                    }
                    purPurchaseOrder
                            .setVendorSid(Long.valueOf(vendorSid))
                            .setDocumentType(valuePurchaseDocument)
                            .setBusinessType(valuePurchaseType)
                            .setCompanySid(companySid)
                            .setPurchaseMode(valuePriceType)
                            .setProductSeasonSid(productSeasonSid)
                            .setBuyer(objects.get(5).toString())
                            .setDocumentDate(documnetDate)
                            .setRawMaterialMode(valueRawMaterial)
                            .setPurchaseContractSid(purchaseContractSid)
                            .setPurchaseContractCode(purPurchaseContractCode)
                            .setMaterialType((objects.get(10) == "" || objects.get(10) == null) ? null : objects.get(10).toString())
                            .setStorehouseSid(basStorehouseSid)
                            .setStorehouseLocationSid(storehouseLocationSid)
                            .setConsignee((objects.get(13) == "" || objects.get(13) == null) ? null : objects.get(13).toString())
                            .setConsigneePhone((objects.get(14) == "" || objects.get(14) == null) ? null : objects.get(14).toString())
                            .setConsigneeAddr((objects.get(15) == "" || objects.get(15) == null) ? null : objects.get(15).toString())
                            .setPurchaseOrg((objects.get(16) == "" || objects.get(16) == null) ? null : objects.get(16).toString())
                            .setPurchaseGroup((objects.get(17) == "" || objects.get(17) == null) ? null : objects.get(17).toString())
                            .setRemark((objects.get(18) == "" || objects.get(18) == null) ? null : objects.get(18).toString())
                            .setHandleStatus(ConstantsEms.SAVA_STATUS)
                            .setMaterialCategory(ConstantsEms.MATERIAL_CATEGORY_SP);

                    continue;
                }
                List<Object> objects = readAll.get(i);
                copy(objects, readAll);
                if (objects.get(0) == null || objects.get(0) == "") {
                    throw new BaseException("商品编码不可为空");
                }
                BasMaterial basMaterial = basMaterialMapper.selectOne(new QueryWrapper<BasMaterial>().lambda()
                        .eq(BasMaterial::getMaterialCode, objects.get(0).toString())
                );
                if (basMaterial == null) {
                    throw new BaseException("没有编码为" + objects.get(0).toString() + "的商品");
                } else {
                    materialSid = basMaterial.getMaterialSid();
                    unitBase = basMaterial.getUnitBase();
                    materialName = basMaterial.getMaterialName();
                }
                if (objects.get(1) == null || objects.get(1) == "") {
                    throw new BaseException("SKU1编码不可为空");
                }
                BasSku basSku = basSkuMapper.selectOne(new QueryWrapper<BasSku>().lambda()
                        .eq(BasSku::getSkuCode, objects.get(1).toString())
                );
                if (basSku == null) {
                    throw new BaseException("没有编码为" + objects.get(1).toString() + "的sku1");
                } else {
                    sku1Sid = basSku.getSkuSid();
                }
                if (objects.get(2) == null || objects.get(2) == "") {
                    throw new BaseException("SKU2编码不可为空");
                }
                BasSku basSku2 = basSkuMapper.selectOne(new QueryWrapper<BasSku>().lambda()
                        .eq(BasSku::getSkuCode, objects.get(2).toString())
                );
                if (basSku2 == null) {
                    throw new BaseException("没有编码为" + objects.get(2).toString() + "的sku2");
                } else {
                    sku2Sid = basSku2.getSkuSid();
                }
                if (objects.get(3) == null || objects.get(3) == "") {
                    throw new BaseException("订单量 不可为空");
                }
                if (objects.get(4) == null || objects.get(4) == "") {
                    throw new BaseException("退货价(含税) 不可为空");
                }
                if (objects.get(5) == null || objects.get(5) == "") {
                    throw new BaseException("合同交期 不可为空");
                }
                if (sku1Sid != null && sku2Sid != null) {
                    basMaterialBarcode = basMaterialBarcodeMapper.selectOne(new QueryWrapper<BasMaterialBarcode>().lambda()
                            .eq(BasMaterialBarcode::getMaterialSid, materialSid)
                            .eq(BasMaterialBarcode::getSku1Sid, sku1Sid)
                            .eq(BasMaterialBarcode::getSku2Sid, sku2Sid)
                    );
                } else if (sku1Sid != null && sku2Sid == null) {
                    basMaterialBarcode = basMaterialBarcodeMapper.selectOne(new QueryWrapper<BasMaterialBarcode>().lambda()
                            .eq(BasMaterialBarcode::getMaterialSid, materialSid)
                            .eq(BasMaterialBarcode::getSku1Sid, sku1Sid)
                    );
                }
                if (basMaterialBarcode == null) {
                    throw new BaseException("不存在商品编码为" + objects.get(0) + "sku1编码为" + objects.get(1) + "" + "sku2编码为" + objects.get(2) + "的商品条码");
                } else {
                    barcodeSid = basMaterialBarcode.getBarcodeSid();
                }
                PurPurchasePrice purPurchase = new PurPurchasePrice();
                BeanCopyUtils.copyProperties(purPurchaseOrder, purPurchase);
                purPurchase.setSku1Sid(sku1Sid)
                        .setMaterialSid(materialSid);
                PurPurchasePriceItem purchasePrice = priceService.getPurchasePrice(purPurchase);
                if (purchasePrice != null) {
                    purchasePriceTax = purchasePrice.getPurchasePriceTax();
                }
                PurPurchaseOrderItem purPurchaseOrderItem = new PurPurchaseOrderItem();
                purPurchaseOrderItem.setSku1Sid(sku1Sid)
                        .setSku2Sid(sku2Sid)
                        .setMaterialSid(materialSid)
                        .setBarcodeSid(barcodeSid)
                        .setQuantity(BigDecimal.valueOf(Long.valueOf(objects.get(3).toString())))
                        .setPurchasePriceTax(BigDecimal.valueOf(Long.valueOf(objects.get(4).toString())))
                        .setMaterialName(materialName)
                        .setContractDate(DateUtil.parse(objects.get(5).toString()))
                        .setRemark((objects.get(6) == "" || objects.get(6) == null) ? null : objects.get(6).toString())
                        .setUnitBase(unitBase);
                purPurchaseOrderItems.add(purPurchaseOrderItem);
            }
            purPurchaseOrder.setPurPurchaseOrderItemList(purPurchaseOrderItems);
            purPurchaseOrder.setImportType(BusinessType.IMPORT.getValue());
            insertPurPurchaseOrder(purPurchaseOrder);
        } catch (BaseException e) {
            throw new BaseException(e.getDefaultMessage());
        }

        return 1;
    }

    /**
     * 供应商寄售结算单 导入
     */
    @Override
    public int importDataVe(MultipartFile file) {
        Long basStorehouseSid = null;
        Long storehouseLocationSid = null;
        Long companySid = null;
        Long vendorSid = null;
        Long productSeasonSid = null;
        Long sku1Sid = null;
        Long sku2Sid = null;
        Long materialSid = null;
        Long barcodeSid = null;
        Long purchaseContractSid = null;
        String materialName = null;
        BigDecimal purchasePriceTax = null;
        String unitBase = null;
        BasMaterialBarcode basMaterialBarcode = null;
        try {
            File toFile = null;
            try {
                toFile = FileUtils.multipartFileToFile(file);
            } catch (Exception e) {
                e.getMessage();
                throw new BaseException("文件转换失败");
            }
            ExcelReader reader = cn.hutool.poi.excel.ExcelUtil.getReader(toFile);
            FileUtils.delteTempFile(toFile);
            List<List<Object>> readAll = reader.read();
            //采购订单-单据类型
            Map<String, String> purchaseDocumentMaps = conDocTypePurchaseOrderMapper.getList().stream().collect(Collectors.toMap(ConDocTypePurchaseOrder::getName, ConDocTypePurchaseOrder::getCode, (key1, key2) -> key2));
            //采购订单-业务类型
            Map<String, String> purchaseTypeMaps = conBuTypePurchaseOrderMapper.getList().stream().collect(Collectors.toMap(ConBuTypePurchaseOrder::getName, ConBuTypePurchaseOrder::getCode, (key1, key2) -> key2));
            // 租户配置
            SysDefaultSettingClient settingClient = settingClientMapper.selectOne(new QueryWrapper<SysDefaultSettingClient>().lambda()
                    .eq(SysDefaultSettingClient::getClientId, ApiThreadLocalUtil.get().getClientId()));
            //客供料 方式
            List<DictData> rawMaterialMode = sysDictDataService.selectDictData("s_raw_material_mode");
            Map<String, String> rawMaterialModeMaps = rawMaterialMode.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            //采购模式
            List<DictData> priceType = sysDictDataService.selectDictData("s_price_type");
            Map<String, String> priceTypeMaps = priceType.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            //销售渠道
            Map<String, String> conSaleChannelMaps = conSaleChannelMapper.getConSaleChannelList().stream().collect(Collectors.toMap(ConSaleChannel::getCode, ConSaleChannel::getName, (key1, key2) -> key2));
            //物料类型
            Map<String, String> materailTypeMaps = conMaterialTypeMapper.getConMaterialTypeList().stream().collect(Collectors.toMap(ConMaterialType::getName, ConMaterialType::getCode, (key1, key2) -> key2));
            //采购组织
            Map<String, String> purchaseOrgMaps = conPurchaseOrgMapper.getConPurchaseOrgList().stream().collect(Collectors.toMap(ConPurchaseOrg::getCode, ConPurchaseOrg::getName, (key1, key2) -> key2));
            //采购组
            Map<String, String> purchaseGroupMaps = conPurchaseGroupMapper.getList().stream().collect(Collectors.toMap(ConPurchaseGroup::getCode, ConPurchaseGroup::getName, (key1, key2) -> key2));
            ArrayList<PurPurchaseOrderItem> purPurchaseOrderItems = new ArrayList<>();
            PurPurchaseOrder purPurchaseOrder = new PurPurchaseOrder();
            for (int i = 0; i < readAll.size(); i++) {
                if (i < 2 || i == 3 || i == 4) {
                    //前两行跳过
                    continue;
                }
                if (i == 2) {
                    List<Object> objects = readAll.get(i);
                    copy(objects, readAll);
                    if (objects.get(0) == null || objects.get(0) == "") {
                        throw new BaseException("供应商编码，不能为空");
                    }
                    String bendorCode = objects.get(0).toString();
                    BasVendor basVendor = basVendorMapper.selectOne(new QueryWrapper<BasVendor>()
                            .lambda().eq(BasVendor::getVendorCode, bendorCode));
                    if (basVendor == null) {
                        throw new BaseException("供应商编码为" + bendorCode + "没有对应的供应商");
                    } else {
                        vendorSid = basVendor.getVendorSid();
                    }

                    if (objects.get(1) == null || objects.get(1) == "") {
                        throw new BaseException("单据类型名称，不能为空");
                    }
                    String valuePurchaseDocument = purchaseDocumentMaps.get(objects.get(1).toString());
                    if (valuePurchaseDocument == null) {
                        throw new BaseException("单据类型名称配置错误，请联系管理员");
                    }
                    if (objects.get(2) == null || objects.get(2) == "") {
                        throw new BaseException("业务类型名称，不能为空");
                    }
                    String valuePurchaseType = purchaseTypeMaps.get(objects.get(2).toString());
                    if (valuePurchaseType == null) {
                        throw new BaseException("业务类型名称配置错误，请联系管理员");
                    }
                    if (objects.get(3) == null || objects.get(3) == "") {
                        throw new BaseException("公司编码，不能为空");
                    }
                    String compamyCode = objects.get(3).toString();
                    BasCompany company = basCompanyMapper.selectOne(new QueryWrapper<BasCompany>()
                            .lambda().eq(BasCompany::getCompanyCode, compamyCode));
                    if (company == null) {
                        throw new BaseException("公司编码为" + compamyCode + "没有对应的公司");
                    } else {
                        companySid = company.getCompanySid();
                    }
                    if (objects.get(4) == null || objects.get(4) == "") {
                        throw new BaseException("产品季，不能为空");
                    }
                    String productSeasonName = objects.get(4).toString();
                    BasProductSeason productSeason = basProductSeasonMapper.selectOne(new QueryWrapper<BasProductSeason>()
                            .lambda().eq(BasProductSeason::getProductSeasonName, productSeasonName));
                    if (productSeason == null) {
                        throw new BaseException("产品季名称为" + productSeasonName + "没有对应的产品季");
                    } else {
                        productSeasonSid = productSeason.getProductSeasonSid();
                    }
                    if (objects.get(5) == null || objects.get(5) == "") {
                        throw new BaseException("销售员账号，不能为空");
                    }
                    if (objects.get(6) == null || objects.get(6) == "") {
                        throw new BaseException("单据日期，不能为空");
                    }
                    String documnet = objects.get(6).toString();
                    Date documnetDate = DateUtil.parse(documnet);
                    if (objects.get(7) == null || objects.get(7) == "") {
                        throw new BaseException("客供料方式，不能为空");
                    }
                    String valueRawMaterial = rawMaterialModeMaps.get(objects.get(7).toString());
                    if (valueRawMaterial == null) {
                        throw new BaseException("客供料方式配置错误，请联系管理员");
                    }
                    if (objects.get(8) == null || objects.get(8) == "") {
                        throw new BaseException("采购模式，不能为空");
                    }
                    String valuePriceType = priceTypeMaps.get(objects.get(8).toString());
                    if (valuePriceType == null) {
                        throw new BaseException("采购模式配置错误，请联系管理员");
                    }
                    /*
                    String paperPurchaseContractCode = null;
                    if (objects.get(11) != null && objects.get(11) != "") {
                        paperPurchaseContractCode = objects.get(11).toString();
                    }
                    */
                    if (objects.get(11) == null || objects.get(11) == "") {
                        throw new BaseException("采购合同号，不能为空");
                    }
                    String purPurchaseContractCode = objects.get(11).toString();
                    if (settingClient == null || !ConstantsOrder.CONTRACT_ENTER_MODE_SG.equals(settingClient.getPurchaseOrderContractEnterMode())) {
                        PurPurchaseContract purPurchase = purPurchaseContractMapper.selectOne(new QueryWrapper<PurPurchaseContract>()
                                .lambda().eq(PurPurchaseContract::getPurchaseContractCode, purPurchaseContractCode));
                        if (purPurchase == null) {
                            throw new BaseException("采购合同号为" + purPurchaseContractCode + "没有对应的采购合同号");
                        } else {
                            purchaseContractSid = purPurchase.getPurchaseContractSid();
                        }
                    }
                    if (objects.get(12) != null && objects.get(12) != "") {
                        String code = objects.get(12).toString();
                        String name = materailTypeMaps.get(code);
                        if (name == null) {
                            throw new BaseException("物料类型为" + code + "没有对应的物料类型");
                        }
                    }
                    if (objects.get(9) == null || objects.get(9) == "") {
                        throw new BaseException("仓库，不能为空");
                    }
                    if (objects.get(9) != null && objects.get(9) != "") {
                        BasStorehouse basStorehouse = basStorehouseMapper.selectOne(new QueryWrapper<BasStorehouse>().lambda()
                                .eq(BasStorehouse::getStorehouseCode, objects.get(9).toString())
                        );
                        if (basStorehouse == null) {
                            throw new BaseException("没有编码为" + objects.get(9).toString() + "的仓库");
                        } else {
                            basStorehouseSid = Long.valueOf(basStorehouse.getStorehouseSid());
                        }
                    }
                    if (objects.get(10) == null || objects.get(10) == "") {
                        throw new BaseException("库位，不能为空");
                    }
                    if (objects.get(10) != null && objects.get(10) != "") {
                        BasStorehouseLocation basStorehouseLocation = basStorehouseLocationMapper.selectOne(new QueryWrapper<BasStorehouseLocation>().lambda()
                                .eq(BasStorehouseLocation::getStorehouseSid, basStorehouseSid)
                                .eq(BasStorehouseLocation::getLocationCode, objects.get(10).toString())
                        );
                        if (basStorehouseLocation == null) {
                            throw new BaseException("没有编码为" + objects.get(10).toString() + "的库位");
                        } else {
                            storehouseLocationSid = Long.valueOf(basStorehouseLocation.getStorehouseLocationSid());
                        }
                    }
                    if (objects.get(13) != null && objects.get(13) != "") {
                        String name = purchaseOrgMaps.get(objects.get(13).toString());
                        if (name == null) {
                            throw new BaseException("没有编码为" + objects.get(13).toString() + "的采购组织");
                        }
                    }
                    if (objects.get(14) != null && objects.get(14) != "") {
                        String name = purchaseGroupMaps.get(objects.get(14).toString());
                        if (name == null) {
                            throw new BaseException("没有编码为" + objects.get(14).toString() + "的采购组");
                        }
                    }
                    purPurchaseOrder
                            .setVendorSid(Long.valueOf(vendorSid))
                            .setDocumentType(valuePurchaseDocument)
                            .setBusinessType(valuePurchaseType)
                            .setCompanySid(companySid)
                            .setPurchaseMode(valuePriceType)
                            .setProductSeasonSid(productSeasonSid)
                            .setBuyer(objects.get(5).toString())
                            .setDocumentDate(documnetDate)
                            .setRawMaterialMode(valueRawMaterial)
                            .setPurchaseContractSid(purchaseContractSid)
                            .setPurchaseContractCode(purPurchaseContractCode)
                            .setMaterialType((objects.get(12) == "" || objects.get(12) == null) ? null : objects.get(12).toString())
                            .setStorehouseSid(basStorehouseSid)
                            .setStorehouseLocationSid(storehouseLocationSid)
                            .setPurchaseOrg((objects.get(13) == "" || objects.get(13) == null) ? null : objects.get(13).toString())
                            .setPurchaseGroup((objects.get(14) == "" || objects.get(14) == null) ? null : objects.get(14).toString())
                            .setRemark((objects.get(15) == "" || objects.get(15) == null) ? null : objects.get(15).toString())
                            .setHandleStatus(ConstantsEms.SAVA_STATUS)
                            .setSpecialBusCategory(ConstantsEms.VENDOR_SPECIAL_BUS_CATEGORY);

                    continue;
                }
                List<Object> objects = readAll.get(i);
                copy(objects, readAll);
                if (objects.get(0) == null || objects.get(0) == "") {
                    throw new BaseException("商品编码不可为空");
                }
                BasMaterial basMaterial = basMaterialMapper.selectOne(new QueryWrapper<BasMaterial>().lambda()
                        .eq(BasMaterial::getMaterialCode, objects.get(0).toString())
                );
                if (basMaterial == null) {
                    throw new BaseException("没有编码为" + objects.get(0).toString() + "的商品");
                } else {
                    materialSid = basMaterial.getMaterialSid();
                    unitBase = basMaterial.getUnitBase();
                    materialName = basMaterial.getMaterialName();
                }
                if (objects.get(1) == null || objects.get(1) == "") {
                    throw new BaseException("SKU1编码不可为空");
                }
                BasSku basSku = basSkuMapper.selectOne(new QueryWrapper<BasSku>().lambda()
                        .eq(BasSku::getSkuCode, objects.get(1).toString())
                );
                if (basSku == null) {
                    throw new BaseException("没有编码为" + objects.get(1).toString() + "的sku1");
                } else {
                    sku1Sid = basSku.getSkuSid();
                }
                if (objects.get(2) == null || objects.get(2) == "") {
                    throw new BaseException("SKU2编码不可为空");
                }
                BasSku basSku2 = basSkuMapper.selectOne(new QueryWrapper<BasSku>().lambda()
                        .eq(BasSku::getSkuCode, objects.get(2).toString())
                );
                if (basSku2 == null) {
                    throw new BaseException("没有编码为" + objects.get(2).toString() + "的sku2");
                } else {
                    sku2Sid = basSku2.getSkuSid();
                }
                if (objects.get(3) == null || objects.get(3) == "") {
                    throw new BaseException("结算量 不可为空");
                }
                /*
                if (objects.get(4) == null || objects.get(4) == "") {
                    throw new BaseException("采购价(含税) 不可为空");
                }
                 */
                if (sku1Sid != null && sku2Sid != null) {
                    basMaterialBarcode = basMaterialBarcodeMapper.selectOne(new QueryWrapper<BasMaterialBarcode>().lambda()
                            .eq(BasMaterialBarcode::getMaterialSid, materialSid)
                            .eq(BasMaterialBarcode::getSku1Sid, sku1Sid)
                            .eq(BasMaterialBarcode::getSku2Sid, sku2Sid)
                    );
                } else if (sku1Sid != null && sku2Sid == null) {
                    basMaterialBarcode = basMaterialBarcodeMapper.selectOne(new QueryWrapper<BasMaterialBarcode>().lambda()
                            .eq(BasMaterialBarcode::getMaterialSid, materialSid)
                            .eq(BasMaterialBarcode::getSku1Sid, sku1Sid)
                    );
                }
                if (basMaterialBarcode == null) {
                    throw new BaseException("不存在商品编码为" + objects.get(0) + "sku1编码为" + objects.get(1) + "" + "sku2编码为" + objects.get(2) + "的商品条码");
                } else {
                    barcodeSid = basMaterialBarcode.getBarcodeSid();
                }
                PurPurchasePrice purPurchase = new PurPurchasePrice();
                BeanCopyUtils.copyProperties(purPurchaseOrder, purPurchase);
                purPurchase.setSku1Sid(sku1Sid)
                        .setMaterialSid(materialSid);
                PurPurchasePriceItem purchasePrice = priceService.getPurchasePrice(purPurchase);
                if (purchasePrice != null) {
                    purchasePriceTax = purchasePrice.getPurchasePriceTax();
                }
                PurPurchaseOrderItem purPurchaseOrderItem = new PurPurchaseOrderItem();
                purPurchaseOrderItem.setSku1Sid(sku1Sid)
                        .setSku2Sid(sku2Sid)
                        .setMaterialSid(materialSid)
                        .setBarcodeSid(barcodeSid)
                        .setQuantity(BigDecimal.valueOf(Long.valueOf(objects.get(3).toString())))
                        .setPurchasePriceTax(BigDecimal.valueOf(Long.valueOf(objects.get(4).toString())))
                        .setMaterialName(materialName)
                        .setRemark((objects.get(5) == "" || objects.get(5) == null) ? null : objects.get(5).toString())
                        .setUnitBase(unitBase);
                purPurchaseOrderItems.add(purPurchaseOrderItem);
            }
            purPurchaseOrder.setPurPurchaseOrderItemList(purPurchaseOrderItems);
            purPurchaseOrder.setImportType(BusinessType.IMPORT.getValue());
            insertPurPurchaseOrder(purPurchaseOrder);
        } catch (BaseException e) {
            throw new BaseException(e.getDefaultMessage());
        }

        return 1;
    }

    //物料采购订单明细导出
    @Override
    public void export(HttpServletResponse response, Long[] sids) {
//        Long[] a ={1458708772802314241L};
//        sids=a;
        for (int m = 0; m < sids.length; m++) {
            try {
                XSSFWorkbook workbook = new XSSFWorkbook();
                Sheet sheet = workbook.createSheet("物料采购订单明细");
                PurPurchaseOrder purPurchaseOrder = selectPurPurchaseOrderById(sids[m]);
                List<PurPurchaseOrderItem> purPurchaseOrderItemList = purPurchaseOrder.getPurPurchaseOrderItemList();
                String isViewPrice = ApiThreadLocalUtil.get().getSysUser().getIsViewPrice();
                purPurchaseOrderItemList.forEach(li -> {
                    if (!ConstantsEms.YES.equals(isViewPrice)) {
                        li.setPurchasePriceTax(null)
                                .setPurchasePrice(null)
                                .setPriceTax(null)
                                .setPrice(null);

                    }
                });
                sheet.setDefaultColumnWidth(18);
                //甲供料方式
                List<DictData> raw = sysDictDataService.selectDictData("s_raw_material_mode");
                Map<String, String> rawMaps = raw.stream().collect(Collectors.toMap(DictData::getDictValue, DictData::getDictLabel, (key1, key2) -> key2));
                //采购模式
                List<DictData> mode = sysDictDataService.selectDictData("s_price_type");
                Map<String, String> modeMaps = mode.stream().collect(Collectors.toMap(DictData::getDictValue, DictData::getDictLabel, (key1, key2) -> key2));
                String[] titles = {"采购订单号", "供应商", "单据类型", "业务类型", "公司", "产品季", "采购员", "单据日期", "甲供料方式", "采购模式", "采购合同号", "下单批次", "物料类型",
                        "配送方式", "仓库", "库位", "收货人", "收货人联系电话", "收货地址", "采购组织", "采购组", "供方跟单员", "纸质下单合同号", "备注"};
                //第一行数据
                Row rowBOMHead = sheet.createRow(0);
                //第一行样式
                CellStyle cellStyle = ExcelStyleUtil.getStyle(workbook);
                //第一行数据
                ExcelStyleUtil.setCellStyleLime(cellStyle);
                ExcelStyleUtil.setBorderStyle(cellStyle);
                for (int i = 0; i < titles.length; i++) {
                    Cell cell = rowBOMHead.createCell(i);
                    cell.setCellValue(titles[i]);
                    cell.setCellStyle(cellStyle);
                }
                CellStyle defaultCellStyle = ExcelStyleUtil.getDefaultCellStyle(workbook);
                //第二行数据
                Row rowBOM = sheet.createRow(1);
                //采购订单号
                Cell cell0 = rowBOM.createCell(0);
                cell0.setCellValue(purPurchaseOrder.getPurchaseOrderCode());
                cell0.setCellStyle(defaultCellStyle);
                //供应商
                Cell cell1 = rowBOM.createCell(1);
                cell1.setCellValue(purPurchaseOrder.getVendorName());
                cell1.setCellStyle(defaultCellStyle);
                //单据类型
                Cell cell2 = rowBOM.createCell(2);
                cell2.setCellValue(purPurchaseOrder.getDocumentTypeName());
                cell2.setCellStyle(defaultCellStyle);
                //业务类型
                Cell cell3 = rowBOM.createCell(3);
                cell3.setCellValue(purPurchaseOrder.getBusinessTypeName());
                cell3.setCellStyle(defaultCellStyle);
                //公司
                Cell cell4 = rowBOM.createCell(4);
                cell4.setCellValue(purPurchaseOrder.getCompanyName());
                cell4.setCellStyle(defaultCellStyle);
                //产品季
                Cell cell5 = rowBOM.createCell(5);
                cell5.setCellValue(purPurchaseOrder.getProductSeasonName());
                cell5.setCellStyle(defaultCellStyle);
                //采购员
                Cell cell6 = rowBOM.createCell(6);
                cell6.setCellValue(purPurchaseOrder.getNickName());
                cell6.setCellStyle(defaultCellStyle);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                //单据日期
                Cell cell7 = rowBOM.createCell(7);
                cell7.setCellValue(sdf.format(purPurchaseOrder.getDocumentDate()));
                cell7.setCellStyle(defaultCellStyle);
                //甲供料
                Cell cell8 = rowBOM.createCell(8);
                cell8.setCellValue(purPurchaseOrder.getRawMaterialMode() == null ? null : rawMaps.get(purPurchaseOrder.getRawMaterialMode().toString()));
                cell8.setCellStyle(defaultCellStyle);
                //采购模式
                Cell cell9 = rowBOM.createCell(9);
                cell9.setCellValue(purPurchaseOrder.getPurchaseMode() == null ? null : modeMaps.get(purPurchaseOrder.getPurchaseMode().toString()));
                cell9.setCellStyle(defaultCellStyle);
                //采购合同号
                Cell cell10A = rowBOM.createCell(10);
                cell10A.setCellValue(purPurchaseOrder.getPurchaseContractCode());
                cell10A.setCellStyle(defaultCellStyle);
                //下单批次
                Cell cell11A = rowBOM.createCell(11);
                cell11A.setCellValue(purPurchaseOrder.getOrderBatchName());
                cell11A.setCellStyle(defaultCellStyle);
                //物料类型
                Cell cell12A = rowBOM.createCell(12);
                cell12A.setCellValue(purPurchaseOrder.getMaterialTypeName());
                cell12A.setCellStyle(defaultCellStyle);
                //配送方式
                Cell cell13A = rowBOM.createCell(13);
                cell13A.setCellValue(purPurchaseOrder.getShipmentTypeName());
                cell13A.setCellStyle(defaultCellStyle);
                //"仓库"
                Cell cell14A = rowBOM.createCell(14);
                cell14A.setCellValue(purPurchaseOrder.getStorehouseName());
                cell14A.setCellStyle(defaultCellStyle);
                //"库位"
                Cell cell15A = rowBOM.createCell(15);
                cell15A.setCellValue(purPurchaseOrder.getLocationName());
                cell15A.setCellStyle(defaultCellStyle);
                //"收货人"
                Cell cell16A = rowBOM.createCell(16);
                cell16A.setCellValue(purPurchaseOrder.getConsignee());
                cell16A.setCellStyle(defaultCellStyle);
                //"收货人联系电话"
                Cell cell17A = rowBOM.createCell(17);
                cell17A.setCellValue(purPurchaseOrder.getConsigneePhone());
                cell17A.setCellStyle(defaultCellStyle);
                //,"收货地址"
                Cell cell18A = rowBOM.createCell(18);
                cell18A.setCellValue(purPurchaseOrder.getConsigneeAddr());
                cell18A.setCellStyle(defaultCellStyle);
                //"采购组织",
                Cell cell19A = rowBOM.createCell(19);
                cell19A.setCellValue(purPurchaseOrder.getPurchaseGroupName());
                cell19A.setCellStyle(defaultCellStyle);
                // "采购组"
                Cell cell20A = rowBOM.createCell(20);
                cell20A.setCellValue(purPurchaseOrder.getPurchaseGroupName());
                cell20A.setCellStyle(defaultCellStyle);
                // "供方跟单员",
                Cell cell21A = rowBOM.createCell(21);
                cell21A.setCellValue(purPurchaseOrder.getVendorBusinessman());
                cell21A.setCellStyle(defaultCellStyle);
                // 纸质下单合同号
                Cell cell22A = rowBOM.createCell(22);
                cell22A.setCellValue(purPurchaseOrder.getPaperPurchaseContractCode());
                cell22A.setCellStyle(defaultCellStyle);
                // "备注"
                Cell cell23A = rowBOM.createCell(23);
                cell23A.setCellValue(purPurchaseOrder.getRemark());
                cell23A.setCellStyle(defaultCellStyle);
                //第三行数据
                Row rowBomItm = sheet.createRow(2);
                String[] titleItem = {"物料编码", "物料名称", "SKU1名称", "SKU2名称", "订单量", "基本计量单位", "合同交期", "采购价(含税)", "采购价(不含税)", "金额(含税)", "金额(不含税)", "税率", "款号", "款颜色", "款尺码", "款备注", "款颜色备注", "款尺码备注", "备注"};
                for (int i = 0; i < titleItem.length; i++) {
                    Cell cell = rowBomItm.createCell(i);
                    cell.setCellValue(titleItem[i]);
                    cell.setCellStyle(cellStyle);
                }
                //   数据部分
                for (int i = 0; i < purPurchaseOrderItemList.size(); i++) {
                    Row row = sheet.createRow(i + 3);
                    //物料编码
                    Cell cell01 = row.createCell(0);
                    cell01.setCellValue(purPurchaseOrderItemList.get(i).getMaterialCode());
                    cell01.setCellStyle(defaultCellStyle);
                    //物料名称
                    Cell cell02 = row.createCell(1);
                    cell02.setCellValue(purPurchaseOrderItemList.get(i).getMaterialName());
                    cell02.setCellStyle(defaultCellStyle);
                    //SKU1名称
                    Cell cell03 = row.createCell(2);
                    cell03.setCellValue(purPurchaseOrderItemList.get(i).getSku1Name());
                    cell03.setCellStyle(defaultCellStyle);
                    //SKU2名称
                    Cell cell05 = row.createCell(3);
                    cell05.setCellValue(purPurchaseOrderItemList.get(i).getSku2Name());
                    cell05.setCellStyle(defaultCellStyle);
                    //订单量
                    Cell cell06 = row.createCell(4);
                    cell06.setCellValue(purPurchaseOrderItemList.get(i).getQuantity() == null ? null : removeZero(purPurchaseOrderItemList.get(i).getQuantity().toString()));
                    cell06.setCellStyle(defaultCellStyle);
                    //基本计量单位
                    Cell cell07 = row.createCell(5);
                    cell07.setCellValue(purPurchaseOrderItemList.get(i).getUnitBaseName());
                    cell07.setCellStyle(defaultCellStyle);
                    //合同交期
                    Cell cell08 = row.createCell(6);
                    if (purPurchaseOrderItemList.get(i).getContractDate() != null) {
                        cell08.setCellValue(sdf.format(purPurchaseOrderItemList.get(i).getContractDate()));
                    }
                    cell08.setCellStyle(defaultCellStyle);
                    //采购价(含税)
                    Cell cell09 = row.createCell(7);
                    cell09.setCellValue(purPurchaseOrderItemList.get(i).getPurchasePriceTax() == null ? null : removeZero(purPurchaseOrderItemList.get(i).getPurchasePriceTax().toString()));
                    cell09.setCellStyle(defaultCellStyle);
                    //采购价(不含税)
                    Cell cell10 = row.createCell(8);
                    cell10.setCellValue(purPurchaseOrderItemList.get(i).getPurchasePriceTax() == null ? null : removeZero(purPurchaseOrderItemList.get(i).getPurchasePriceTax().divide(BigDecimal.ONE.add(purPurchaseOrderItemList.get(i).getTaxRate()), BigDecimal.ROUND_HALF_UP, 4).toString()));
                    cell10.setCellStyle(defaultCellStyle);
                    //金额(含税)
                    Cell cell11 = row.createCell(9);
                    cell11.setCellValue(purPurchaseOrderItemList.get(i).getPurchasePriceTax() == null ? null : removeZero(purPurchaseOrderItemList.get(i).getPurchasePriceTax().multiply(purPurchaseOrderItemList.get(i).getQuantity()).setScale(2, BigDecimal.ROUND_HALF_UP).toString()));
                    cell11.setCellStyle(defaultCellStyle);
                    //金额(不含税)
                    Cell cell12 = row.createCell(10);
                    cell12.setCellValue(purPurchaseOrderItemList.get(i).getPurchasePriceTax() == null ? null : removeZero(purPurchaseOrderItemList.get(i).getPurchasePriceTax().divide(BigDecimal.ONE.add(purPurchaseOrderItemList.get(i).getTaxRate()), BigDecimal.ROUND_HALF_UP, 6).multiply(purPurchaseOrderItemList.get(i).getQuantity()).setScale(2, BigDecimal.ROUND_HALF_UP).toString()));
                    cell12.setCellStyle(defaultCellStyle);
                    //税率
                    Cell cell13 = row.createCell(11);
                    cell13.setCellValue(purPurchaseOrderItemList.get(i).getTaxRate() == null ? null : removeZero(purPurchaseOrderItemList.get(i).getTaxRate().toString()));
                    cell13.setCellStyle(defaultCellStyle);
                    //款号
                    Cell cell14 = row.createCell(12);
                    cell14.setCellValue(purPurchaseOrderItemList.get(i).getProductCode());
                    cell14.setCellStyle(defaultCellStyle);
                    //款颜色
                    Cell cell15 = row.createCell(13);
                    cell15.setCellValue(purPurchaseOrderItemList.get(i).getProductName());
                    cell15.setCellStyle(defaultCellStyle);
                    //款尺码
                    Cell cell16 = row.createCell(14);
                    cell16.setCellValue(purPurchaseOrderItemList.get(i).getProductSku2Name());
                    cell16.setCellStyle(defaultCellStyle);
                    //款备注
                    Cell cell17 = row.createCell(15);
                    cell17.setCellValue(purPurchaseOrderItemList.get(i).getProductCodes());
                    cell17.setCellStyle(defaultCellStyle);
                    //款颜色备注
                    Cell cell18 = row.createCell(16);
                    cell18.setCellValue(purPurchaseOrderItemList.get(i).getProductSku1Names());
                    cell18.setCellStyle(defaultCellStyle);
                    //款尺码备注
                    Cell cell19 = row.createCell(17);
                    cell19.setCellValue(purPurchaseOrderItemList.get(i).getProductSku2Names());
                    cell19.setCellStyle(defaultCellStyle);
                    //备注
                    Cell cell20 = row.createCell(18);
                    cell20.setCellValue(purPurchaseOrderItemList.get(i).getRemark());
                    cell20.setCellStyle(defaultCellStyle);
                }
                response.setContentType("application/vnd.ms-excel");
                response.setCharacterEncoding("utf-8");
                workbook.write(response.getOutputStream());
            } catch (Exception e) {
                throw new CustomException("导出失败");
            }
        }
    }

    //商品采购订单明细导出
    @Override
    public void exportGood(HttpServletResponse response, Long[] sids) {
//        Long[] a ={1456548036411965441L};
//        sids=a;
        for (int m = 0; m < sids.length; m++) {
            try {
                XSSFWorkbook workbook = new XSSFWorkbook();
                Sheet sheet = workbook.createSheet("商品采购订单明细");
                PurPurchaseOrder purPurchaseOrder = selectPurPurchaseOrderById(sids[m]);
                List<PurPurchaseOrderItem> purPurchaseOrderItemList = purPurchaseOrder.getPurPurchaseOrderItemList();
                String isViewPrice = ApiThreadLocalUtil.get().getSysUser().getIsViewPrice();
                purPurchaseOrderItemList.forEach(li -> {
                    if (!ConstantsEms.YES.equals(isViewPrice)) {
                        li.setPurchasePriceTax(null)
                                .setPurchasePrice(null)
                                .setPriceTax(null)
                                .setPrice(null);

                    }
                });
                sheet.setDefaultColumnWidth(18);
                //甲供料方式
                List<DictData> raw = sysDictDataService.selectDictData("s_raw_material_mode");
                Map<String, String> rawMaps = raw.stream().collect(Collectors.toMap(DictData::getDictValue, DictData::getDictLabel, (key1, key2) -> key2));
                //采购模式
                List<DictData> mode = sysDictDataService.selectDictData("s_price_type");
                Map<String, String> modeMaps = mode.stream().collect(Collectors.toMap(DictData::getDictValue, DictData::getDictLabel, (key1, key2) -> key2));
                String[] titles = {"采购订单号", "供应商", "单据类型", "业务类型", "公司", "产品季", "采购员", "单据日期", "甲供料方式	", "采购模式", "采购合同号", "下单批次", "物料类型",
                        "配送方式", "仓库", "库位", "收货人", "收货人联系电话", "收货地址", "采购组织", "采购组", "供方跟单员", "纸质下单合同号", "备注"};
                //第一行数据
                Row rowBOMHead = sheet.createRow(0);
                //第一行样式
                CellStyle cellStyle = ExcelStyleUtil.getStyle(workbook);
                //第一行数据
                ExcelStyleUtil.setCellStyleLime(cellStyle);
                ExcelStyleUtil.setBorderStyle(cellStyle);
                for (int i = 0; i < titles.length; i++) {
                    Cell cell = rowBOMHead.createCell(i);
                    cell.setCellValue(titles[i]);
                    cell.setCellStyle(cellStyle);
                }
                CellStyle defaultCellStyle = ExcelStyleUtil.getDefaultCellStyle(workbook);
                //第二行数据
                Row rowBOM = sheet.createRow(1);
                //采购订单号
                Cell cell0 = rowBOM.createCell(0);
                cell0.setCellValue(purPurchaseOrder.getPurchaseOrderCode());
                cell0.setCellStyle(defaultCellStyle);
                //供应商
                Cell cell1 = rowBOM.createCell(1);
                cell1.setCellValue(purPurchaseOrder.getVendorName());
                cell1.setCellStyle(defaultCellStyle);
                //单据类型
                Cell cell2 = rowBOM.createCell(2);
                cell2.setCellValue(purPurchaseOrder.getDocumentTypeName());
                cell2.setCellStyle(defaultCellStyle);
                //业务类型
                Cell cell3 = rowBOM.createCell(3);
                cell3.setCellValue(purPurchaseOrder.getBusinessTypeName());
                cell3.setCellStyle(defaultCellStyle);
                //公司
                Cell cell4 = rowBOM.createCell(4);
                cell4.setCellValue(purPurchaseOrder.getCompanyName());
                cell4.setCellStyle(defaultCellStyle);
                //产品季
                Cell cell5 = rowBOM.createCell(5);
                cell5.setCellValue(purPurchaseOrder.getProductSeasonName());
                cell5.setCellStyle(defaultCellStyle);
                //采购员
                Cell cell6 = rowBOM.createCell(6);
                cell6.setCellValue(purPurchaseOrder.getNickName());
                cell6.setCellStyle(defaultCellStyle);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                //单据日期
                Cell cell7 = rowBOM.createCell(7);
                cell7.setCellValue(sdf.format(purPurchaseOrder.getDocumentDate()));
                cell7.setCellStyle(defaultCellStyle);
                //甲供料
                Cell cell8 = rowBOM.createCell(8);
                cell8.setCellValue(purPurchaseOrder.getRawMaterialMode() == null ? null : rawMaps.get(purPurchaseOrder.getRawMaterialMode().toString()));
                cell8.setCellStyle(defaultCellStyle);
                //采购模式
                Cell cell9 = rowBOM.createCell(9);
                cell9.setCellValue(purPurchaseOrder.getPurchaseMode() == null ? null : modeMaps.get(purPurchaseOrder.getPurchaseMode().toString()));
                cell9.setCellStyle(defaultCellStyle);
                //采购合同号
                Cell cell10A = rowBOM.createCell(10);
                cell10A.setCellValue(purPurchaseOrder.getPurchaseContractCode());
                cell10A.setCellStyle(defaultCellStyle);
                //下单批次
                Cell cell11A = rowBOM.createCell(11);
                cell11A.setCellValue(purPurchaseOrder.getOrderBatchName());
                cell11A.setCellStyle(defaultCellStyle);
                //物料类型
                Cell cell12A = rowBOM.createCell(12);
                cell12A.setCellValue(purPurchaseOrder.getMaterialTypeName());
                cell12A.setCellStyle(defaultCellStyle);
                //配送方式
                Cell cell13A = rowBOM.createCell(13);
                cell13A.setCellValue(purPurchaseOrder.getShipmentTypeName());
                cell13A.setCellStyle(defaultCellStyle);
                //"仓库"
                Cell cell14A = rowBOM.createCell(14);
                cell14A.setCellValue(purPurchaseOrder.getStorehouseName());
                cell14A.setCellStyle(defaultCellStyle);
                //"库位"
                Cell cell15A = rowBOM.createCell(15);
                cell15A.setCellValue(purPurchaseOrder.getLocationName());
                cell15A.setCellStyle(defaultCellStyle);
                //"收货人"
                Cell cell16A = rowBOM.createCell(16);
                cell16A.setCellValue(purPurchaseOrder.getConsignee());
                cell16A.setCellStyle(defaultCellStyle);
                //"收货人联系电话"
                Cell cell17A = rowBOM.createCell(17);
                cell17A.setCellValue(purPurchaseOrder.getConsigneePhone());
                cell17A.setCellStyle(defaultCellStyle);
                //,"收货地址"
                Cell cell18A = rowBOM.createCell(18);
                cell18A.setCellValue(purPurchaseOrder.getConsigneeAddr());
                cell18A.setCellStyle(defaultCellStyle);
                //"采购组织",
                Cell cell19A = rowBOM.createCell(19);
                cell19A.setCellValue(purPurchaseOrder.getPurchaseGroupName());
                cell19A.setCellStyle(defaultCellStyle);
                // "采购组"
                Cell cell20A = rowBOM.createCell(20);
                cell20A.setCellValue(purPurchaseOrder.getPurchaseGroupName());
                cell20A.setCellStyle(defaultCellStyle);
                // "供方跟单员",
                Cell cell21A = rowBOM.createCell(21);
                cell21A.setCellValue(purPurchaseOrder.getVendorBusinessman());
                cell21A.setCellStyle(defaultCellStyle);
                // 纸质下单合同号
                Cell cell22A = rowBOM.createCell(22);
                cell22A.setCellValue(purPurchaseOrder.getPaperPurchaseContractCode());
                cell22A.setCellStyle(defaultCellStyle);
                // "备注"
                Cell cell23A = rowBOM.createCell(23);
                cell23A.setCellValue(purPurchaseOrder.getRemark());
                cell23A.setCellStyle(defaultCellStyle);
                //第三行数据
                Row rowBomItm = sheet.createRow(2);
                String[] titleItem = {"物料编码", "物料名称", "SKU1名称", "SKU2名称", "订单量", "基本计量单位", "合同交期", "采购价(含税)", "采购价(不含税)", "金额(含税)", "金额(不含税)", "税率", "备注"};
                for (int i = 0; i < titleItem.length; i++) {
                    Cell cell = rowBomItm.createCell(i);
                    cell.setCellValue(titleItem[i]);
                    cell.setCellStyle(cellStyle);
                }
                //   数据部分
                for (int i = 0; i < purPurchaseOrderItemList.size(); i++) {
                    Row row = sheet.createRow(i + 3);
                    //物料编码
                    Cell cell01 = row.createCell(0);
                    cell01.setCellValue(purPurchaseOrderItemList.get(i).getMaterialCode());
                    cell01.setCellStyle(defaultCellStyle);
                    //物料名称
                    Cell cell02 = row.createCell(1);
                    cell02.setCellValue(purPurchaseOrderItemList.get(i).getMaterialName());
                    cell02.setCellStyle(defaultCellStyle);
                    //SKU1名称
                    Cell cell03 = row.createCell(2);
                    cell03.setCellValue(purPurchaseOrderItemList.get(i).getSku1Name());
                    cell03.setCellStyle(defaultCellStyle);
                    //SKU2名称
                    Cell cell05 = row.createCell(3);
                    cell05.setCellValue(purPurchaseOrderItemList.get(i).getSku2Name());
                    cell05.setCellStyle(defaultCellStyle);
                    //订单量
                    Cell cell06 = row.createCell(4);
                    cell06.setCellValue(purPurchaseOrderItemList.get(i).getQuantity() == null ? null : removeZero(purPurchaseOrderItemList.get(i).getQuantity().toString()));
                    cell06.setCellStyle(defaultCellStyle);
                    //基本计量单位
                    Cell cell07 = row.createCell(5);
                    cell07.setCellValue(purPurchaseOrderItemList.get(i).getUnitBaseName());
                    cell07.setCellStyle(defaultCellStyle);
                    //合同交期
                    Cell cell08 = row.createCell(6);
                    if (purPurchaseOrderItemList.get(i).getContractDate() != null) {
                        cell08.setCellValue(sdf.format(purPurchaseOrderItemList.get(i).getContractDate()));
                    }
                    cell08.setCellStyle(defaultCellStyle);
                    //采购价(含税)
                    Cell cell09 = row.createCell(7);
                    if (purPurchaseOrderItemList.get(i).getPurchasePriceTax() != null) {
                        removeZero(purPurchaseOrderItemList.get(i).getPurchasePriceTax().divide(BigDecimal.ONE.add(purPurchaseOrderItemList.get(i).getTaxRate()), BigDecimal.ROUND_HALF_UP, 4).toString());
                    }
                    cell09.setCellValue(purPurchaseOrderItemList.get(i).getPurchasePriceTax() == null ? null : removeZero(purPurchaseOrderItemList.get(i).getPurchasePriceTax().toString()));
                    cell09.setCellStyle(defaultCellStyle);
                    //采购价(不含税)
                    Cell cell10 = row.createCell(8);
                    cell10.setCellValue(purPurchaseOrderItemList.get(i).getPurchasePriceTax() == null ? null : removeZero(purPurchaseOrderItemList.get(i).getPurchasePriceTax().divide(BigDecimal.ONE.add(purPurchaseOrderItemList.get(i).getTaxRate()), BigDecimal.ROUND_HALF_UP, 4).toString()));
                    cell10.setCellStyle(defaultCellStyle);
                    //金额(含税)
                    Cell cell11 = row.createCell(9);
                    cell11.setCellValue(purPurchaseOrderItemList.get(i).getPurchasePriceTax() == null ? null : removeZero(purPurchaseOrderItemList.get(i).getPurchasePriceTax().multiply(purPurchaseOrderItemList.get(i).getQuantity()).divide(BigDecimal.ONE, BigDecimal.ROUND_HALF_UP, 2).setScale(2, BigDecimal.ROUND_HALF_UP).toString()));
                    cell11.setCellStyle(defaultCellStyle);
                    //金额(不含税)
                    Cell cell12 = row.createCell(10);
                    cell12.setCellValue(purPurchaseOrderItemList.get(i).getPurchasePriceTax() == null ? null : removeZero(purPurchaseOrderItemList.get(i).getPurchasePriceTax().divide(BigDecimal.ONE.add(purPurchaseOrderItemList.get(i).getTaxRate()), BigDecimal.ROUND_HALF_UP, 6).multiply(purPurchaseOrderItemList.get(i).getQuantity()).setScale(2, BigDecimal.ROUND_HALF_UP).toString()));
                    cell12.setCellStyle(defaultCellStyle);
                    //税率
                    Cell cell13 = row.createCell(11);
                    cell13.setCellValue(purPurchaseOrderItemList.get(i).getTaxRate() == null ? null : removeZero(purPurchaseOrderItemList.get(i).getTaxRate().toString()));
                    cell13.setCellStyle(defaultCellStyle);
                    //备注
                    Cell cell14 = row.createCell(12);
                    cell14.setCellValue(purPurchaseOrderItemList.get(i).getRemark());
                    cell14.setCellStyle(defaultCellStyle);
                }
                response.setContentType("application/vnd.ms-excel");
                response.setCharacterEncoding("utf-8");
                workbook.write(response.getOutputStream());
            } catch (Exception e) {
                throw new CustomException("导出失败");
            }
        }
    }

    //采购进度报表主
    @Override
    public List<PurchaseOrderProgressResponse> getProcessHead(PurchaseOrderProgressRequest request) {
        List<PurchaseOrderProgressResponse> list = purPurchaseOrderItemMapper.getProcessHead(request);
        return list;
    }

    //采购进度报表-明细
    @Override
    public List<PurchaseOrderProgressItemResponse> getProcessItem(PurchaseOrderProgressRequest request) {
        List<PurchaseOrderProgressItemResponse> list = purPurchaseOrderItemMapper.getProcessItem(request);
        return list;
    }

    /**
     * 采购入库进度跟踪报表
     *
     * @param purPurchaseOrder 采购订单
     * @return 采购订单集合
     */
    @Override
    public List<PurPurchaseOrderProcessTracking> selectPurPurchaseProcessTrackingList(PurPurchaseOrderProcessTracking purPurchaseOrder) {
        return purPurchaseOrderItemMapper.selectPurPurchaseProcessTrackingList(purPurchaseOrder);
    }

    /**
     * 商品采购订单合同导出
     */
    @Override
    public void exportHetongList(HttpServletResponse response, PurPurchaseOrder purPurchaseOrder) {
        try {
            //purPurchaseOrderMapper
            List<PurPurchaseOrder> purPurchaseOrderList = purPurchaseOrderMapper.selectPurPurchaseOrderList(purPurchaseOrder);
            // 遍历每个模块
            XSSFWorkbook workbook = new XSSFWorkbook();
            // 创建一个列表来存储导出的数据
            List<String> exportData = new ArrayList<>();
            int size = 0;
            int rowSize = 0;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            if (purPurchaseOrderList != null && !purPurchaseOrderList.isEmpty()) {
                PurPurchaseOrder record = purPurchaseOrderList.get(0);
                PurPurchaseOrder item = selectPurPurchaseOrderById(record.getPurchaseOrderSid());

                List<PurPurchaseOrderItem> allPurPurchaseOrderItemList = item.getPurPurchaseOrderItemList();
                //拿公司档案和供应商档案
                BasCompany basCompany = basCompanyService.selectBasCompanyById(item.getCompanySid());
                BasVendor basVendor = basVendorService.selectBasVendorBySid(item.getVendorSid());
                if (purPurchaseOrderList.size()>1) {
                    for (int i = 1;i < purPurchaseOrderList.size(); i++){
                        List<PurPurchaseOrderItem> itemList = selectPurPurchaseOrderById(purPurchaseOrderList.get(i).getPurchaseOrderSid())
                                .getPurPurchaseOrderItemList();
                        if (itemList != null) {
                            allPurPurchaseOrderItemList.addAll(itemList);
                        }
                    }
                }
                // 添加模块内容
                exportData.add(item.getCompanyName() +"商品采购订单合同");exportData.add("/n");
                exportData.add("供方：");exportData.add(item.getVendorName());
                exportData.add("合同编号：");exportData.add(item.getPurchaseContractCode());exportData.add("/n");
                exportData.add("地址：");exportData.add(basVendor.getBusinessAddr());exportData.add("/n");
                exportData.add("需方：");exportData.add(item.getCompanyName());
                exportData.add("合同签约地：厦门市湖里区");exportData.add("/n");
                exportData.add("地址：");exportData.add(basCompany.getOfficeAddr());exportData.add("/n");
                exportData.add("为了明确买卖双方在合作中的权力和义务，根据《中华人民共和国合同法》等相关法律法规规定，买卖双方本着长期合作、互惠互利的基础原则，经友好协商达成本采购框架协议;");
                exportData.add("/n");
                exportData.add("一、 产品价目表:");exportData.add("/n");
                exportData.add("产品名称");exportData.add("编码");exportData.add("颜色");exportData.add("规格");exportData.add("单位");
                exportData.add("单价");exportData.add("数量");exportData.add("金额");exportData.add("/n");
                rowSize = allPurPurchaseOrderItemList.size();
                BigDecimal sumMoneyAmount =  BigDecimal.ZERO;
                Date maxDate = null;
                for (PurPurchaseOrderItem order:allPurPurchaseOrderItemList) {
                    exportData.add(order.getMaterialName());
                    exportData.add(order.getMaterialCode());
                    exportData.add(order.getSku1Name());
                    exportData.add(order.getSku2Name());
                    exportData.add(order.getUnitPriceName());
                    exportData.add(Optional.ofNullable(order.getPurchasePriceTax()).map(Object::toString).orElse(""));
                    exportData.add(Optional.ofNullable(order.getQuantity()).map(Object::toString).orElse(""));
                    exportData.add(Optional.ofNullable(order.getPriceTax()).map(Object::toString).orElse(""));
                    exportData.add("/n");
                    // 总金额累加
                    if (order.getPriceTax() != null) {
                        sumMoneyAmount = sumMoneyAmount.add(order.getPriceTax());
                    }
                    // 获取订单日期
                    Date orderDate = order.getContractDate();

                    // 如果 jhDate 为空或者当前订单日期比 jhDate 大，则更新 jhDate
                    if (maxDate == null || orderDate.compareTo(maxDate) > 0) {
                        maxDate = orderDate;
                    }
                }
                exportData.add("金额：");exportData.add(sumMoneyAmount.toString());exportData.add("/n");
                exportData.add("单价含13%增值税税率及运费");exportData.add("/n");
                exportData.add("  在协议范围内，实际供货单价不得高于市场单价，同时供方有义务根据市场行情降低单价，并接受需方的监督。供方承诺向需方提供最优惠的具有竞争性的最低价格，在同一时期及相似的供货量情况下，如果供方以更低的价格供应其它需方时，则需方有权要求供方按照更低的价格最终结算");
                exportData.add("/n");
                exportData.add("二、 交货日期：");exportData.add(sdf.format(maxDate));exportData.add("/n");
                exportData.add("三、 交货方式及地点：  ");exportData.add(item.getConsigneeAddr());exportData.add("/n");
                exportData.add("四、 付款");exportData.add("/n");
                exportData.add("   4.1付款方式：银行转账 ");exportData.add("/n");
                exportData.add("   4.2增值税发票开票信息：以协议下买卖合同信息为准。供方以合同为单位单独开具增值税专用发票，发票上产品描述需要与订货单产品描述一致，如有变化，请另附说明，该说明须需方签字确认，否则供方须承担额外税费以及后续需方有关税务方面一切有关损失。");
                exportData.add("/n");
                exportData.add("   4.3 付款时间：收到发票后60天内付货款、可以用承兑汇票支付，期限6个月");exportData.add("/n");
                exportData.add("五、包装要求：供方必须按照需方要求包装袋外贴标签注明；款号、数量及物料名称，如未按照需方要求来包装，需方将拒收、如因此导致退货，需方将按第十条来处理.");exportData.add("/n");
                exportData.add("  6.1 超出：发货数量不得超过采购订单数量，超过部分不予结算");exportData.add("/n");
                exportData.add("  6.2 短少：发货数量不得短缺，因短缺造成的损失，按延期扣款、以及给工厂造成实际损失等两方面进行扣款");exportData.add("/n");
                exportData.add("七、质量要求、技术标准、供方对质量负责的条件及期限：按本合同第一、六、条的规定，若供方产品不符合约定的规格、数量、款号及不符合验收标准的，需方应在验收后 七 天内以书面通知供方，供方对需方提出的修复、重做、退货、降价或索赔的请求应予认可并在接通知后七日内解决。否则需方有权追究供方的违约责任和民事赔偿责任。");
                exportData.add("/n");
                exportData.add("九、其他约定：   产品必须不含偶氮等有毒物质，需符合欧盟环保标准。");exportData.add("/n");
                exportData.add("十、违约责任：若供方迟延供货，每迟延一日，应向需方支付订单金额的1%违约金，该款需方有权在货款中扣除。供方支付违约金后，应继续履行合同。");exportData.add("/n");
                exportData.add("十一、合同在履行中如发生争议，双方应协商解决，协商不成可向合同履行地厦门书湖里区人民法院起诉。");exportData.add("/n");
                exportData.add("十二、请收到【采购订单】2个工作日内盖章回传，否则订单作废");exportData.add("/n");
                exportData.add("供方：");exportData.add(item.getVendorName());exportData.add("");
                exportData.add("需方：");exportData.add(item.getCompanyName());exportData.add("/n");
                String vendorBankName = "";
                String vendorBankAccount = "";
                String companyBankName = "";
                String companyBankAccount = "";
                if (basVendor != null && basVendor.getBaseBankAccountList() != null && !basVendor.getBaseBankAccountList().isEmpty()) {
                    // 获取第一个银行账户
                    BasVendorBankAccount basVendorBankAccount = basVendor.getBaseBankAccountList().get(0);
                    // 获取银行名称和银行账户
                    vendorBankName = basVendorBankAccount.getBankName() + basVendorBankAccount.getBankBranchName();
                    vendorBankAccount = basVendorBankAccount.getBankAccount();
                }
                if (basCompany != null && basCompany.getBaseBankAccountList() != null && !basCompany.getBaseBankAccountList().isEmpty()) {
                    // 获取公司的基本户信息
                    BasCompanyBankAccount companyBaseBankAccount = basCompany.getBaseBankAccountList().get(0);
                    companyBankName = companyBaseBankAccount.getBankName() + companyBaseBankAccount.getBankBranchName();
                    companyBankAccount = companyBaseBankAccount.getBankAccount();
                }
                exportData.add("供方开户行：");exportData.add(vendorBankName);exportData.add("");
                exportData.add("需方开户行：");exportData.add(companyBankName);exportData.add("/n");
                exportData.add("账号：");exportData.add(vendorBankAccount);exportData.add("");
                exportData.add("账号：");exportData.add(companyBankAccount);exportData.add("/n");
                exportData.add("业务：");exportData.add("主管（签字）：");exportData.add("");
                exportData.add("采购：");exportData.add("主管（签字）：");exportData.add("/n");
                exportData.add("传真：");exportData.add(basVendor.getInvoiceTel() != null ? basVendor.getInvoiceTel() : "");
                exportData.add("");
                exportData.add("传真：");exportData.add(basCompany.getInvoiceTel() != null ? basCompany.getInvoiceTel() : "");
                exportData.add("/n");
                exportData.add("签约日期：");exportData.add("");exportData.add("");
                exportData.add("签约日期：");

            }
            // 导出到 Excel 文件
            size = exportData.size();
            printExcelHetongList(workbook, exportData, size,rowSize);
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-disposition", "attachment; filename=" + new String("导出资产卡片".getBytes("gbk"), "iso8859-1") + ".xlsx");
            workbook.write(response.getOutputStream());
        } catch (Exception e) {
            throw new CustomException("导出失败:"+e);
        }

    }
    /**
     * 商品采购订单合同导出成 excel
     */
    public void printExcelHetongList(XSSFWorkbook workbook, List<String> exportReport, int size,int rowSize) {
        // 绘制excel表格
        Sheet sheet = workbook.createSheet("商品采购订单合同");
        sheet.setDefaultColumnWidth(20);
        sheet.setColumnWidth(0, (int) (0.98 * 256)); // 第一列宽度

        // 创建边框样式
        CellStyle borderStyle = workbook.createCellStyle();
        borderStyle.setBorderBottom(BorderStyle.THIN);
        borderStyle.setBorderTop(BorderStyle.THIN);
        borderStyle.setBorderLeft(BorderStyle.THIN);
        borderStyle.setBorderRight(BorderStyle.THIN);
        borderStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        borderStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        borderStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        borderStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());


        // 创建自动换行样式
        CellStyle wrapStyle = workbook.createCellStyle();
        wrapStyle.setWrapText(true); // 设置自动换行

        for(int i=0; i<45 ;i++){
            if(i == 0 || i == 5 || i == 9+rowSize || i == 10+rowSize ||(i >= 14+rowSize && i <= 24 + rowSize) ){
                CellRangeAddress mergedRegion = new CellRangeAddress(i, i, 1, 8);
                sheet.addMergedRegion(mergedRegion);
                // 设置合并单元格的样式（应用自动换行）
                setCellStyleForRegion(sheet, mergedRegion, wrapStyle);

                // 获取或创建当前行
                Row currentRow = sheet.getRow(i);
                if (currentRow == null) {
                    currentRow = sheet.createRow(i);
                }

                // 自动调整行高
                currentRow.setHeight((short) -1);
            } else if (i == 8 + rowSize) {
                CellRangeAddress mergedRegion = new CellRangeAddress(i, i, 2, 8);
                sheet.addMergedRegion(mergedRegion);

                // 设置合并单元格的样式（应用自动换行）
                setCellStyleForRegion(sheet, mergedRegion, wrapStyle);

                // 获取或创建当前行
                Row currentRow = sheet.getRow(i);
                if (currentRow == null) {
                    currentRow = sheet.createRow(i);
                }

                // 自动调整行高
                currentRow.setHeight((short) -1);
            }
        }
        // 遍历数据，创建行和单元格
        int rowNum = 0;
        Row row = sheet.createRow(rowNum);
        int colNum = 1; // 从第二列开始填充
        for (int i = 0; i < exportReport.size(); i++) {
            CellStyle centerStyle = null;
            String data = exportReport.get(i);
            if(data == null){
                continue;
            }
            if(data.equals("/n")){
                //换行
                rowNum++; // 首次换行前先自增行数
                row = sheet.createRow(rowNum);
                colNum = 1;
                continue;
            }
            if(rowNum == 0){
                // 居中对齐
                centerStyle = workbook.createCellStyle();
                centerStyle.setAlignment(HorizontalAlignment.CENTER);
                centerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

                // 设置字体大小
                Font font = workbook.createFont();
                font.setFontHeightInPoints((short) 26);
                centerStyle.setFont(font);
            }
            // 检查当前行是否是第1行或第3行
            if ((rowNum == 1 || rowNum == 3) && colNum == 3) {
                colNum += 2; // 当前是第2列时，跳过两列
            }

            Cell cell = row.createCell(colNum++);
            cell.setCellStyle(centerStyle);
            cell.setCellValue(data.trim()); // 去除数据两侧的空格
            row.setHeightInPoints(26); // 设置行高为30个点
        }

        // 循环遍历第八行到第十五行的第二列到第八列的单元格，并为其应用边框样式
        for (int i = 7; i <= 10 + rowSize; i++) {
            Row currentRow = sheet.getRow(i);
            if (currentRow == null) {
                currentRow = sheet.createRow(i);
            }
            for (int col = 1; col <= 8; col++) {
                Cell cell = currentRow.getCell(col);
                if (cell == null) {
                    cell = currentRow.createCell(col);
                }
                // 创建单元格样式，并将边框样式设置为该样式
                CellStyle cellStyle = workbook.createCellStyle();
                cellStyle.cloneStyleFrom(borderStyle);
                cell.setCellStyle(cellStyle); // 应用边框样式
            }
        }
        for (int i = 1; i < 10; i++) {
            sheet.setColumnWidth(i, 26 * 256); // 设置每列宽度为30个字符宽度
        }
    }

    // 设置合并单元格的样式
    private void setCellStyleForRegion(Sheet sheet, CellRangeAddress region, CellStyle style) {
        for (int row = region.getFirstRow(); row <= region.getLastRow(); row++) {
            Row currentRow = sheet.getRow(row);
            if (currentRow == null) {
                currentRow = sheet.createRow(row);
            }
            for (int col = region.getFirstColumn(); col <= region.getLastColumn(); col++) {
                Cell cell = currentRow.getCell(col);
                if (cell == null) {
                    cell = currentRow.createCell(col);
                }
                // 应用样式
                cell.setCellStyle(style);
            }
        }
    }

    //填充-主表
    public void copy(List<Object> objects, List<List<Object>> readAll) {
        //获取第一行的列数
        int size = readAll.get(0).size();
        //当前行的列数
        int lineSize = objects.size();
        ArrayList<Object> all = new ArrayList<>();
        for (int i = lineSize; i < size; i++) {
            Object o = new Object();
            o = null;
            objects.add(o);
        }
    }

    //填充-明细表
    public void copyItem(List<Object> objects, List<List<Object>> readAll) {
        //获取第三行的列数
        int size = readAll.get(3).size();
        //当前行的列数
        int lineSize = objects.size();
        ArrayList<Object> all = new ArrayList<>();
        for (int i = lineSize; i < size; i++) {
            Object o = new Object();
            o = null;
            objects.add(o);
        }
    }

    public String removeZero(String s) {
        if (s != null && s.indexOf(".") > 0) {
            //正则表达
            s = s.replaceAll("0+?$", "");//去掉后面无用的零
            s = s.replaceAll("[.]$", "");//如小数点后面全是零则去掉小数点
        }
        return s;
    }

    public Boolean exit(String code) {
        List<String> codes = Arrays.asList("BLTZD");//备料通知单
        boolean isMatch = codes.stream().anyMatch(li -> li.equals(code));
        return isMatch;
    }
}
