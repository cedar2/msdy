package com.platform.ems.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FastByteArrayOutputStream;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.mail.MailUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.poi.excel.ExcelReader;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.platform.common.core.domain.entity.ConMaterialClass;
import com.platform.common.core.domain.entity.SysClient;
import com.platform.common.core.domain.entity.SysDefaultSettingClient;
import com.platform.common.core.domain.entity.SysUser;
import com.platform.common.core.domain.model.DictData;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CustomException;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.utils.StringUtils;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.utils.file.FileUtils;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.core.redis.RedisCache;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.security.utils.dingtalk.DdPushUtil;
import com.platform.common.security.utils.dingtalk.DingtalkConstants;
import com.platform.common.security.utils.wx.QiYePushUtil;
import com.platform.common.security.utils.wx.WxConstants;
import com.platform.ems.config.MinioConfig;
import com.platform.ems.constant.AutoIdField;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsFinance;
import com.platform.ems.domain.*;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.domain.dto.request.BasSaleOrderRequest;
import com.platform.ems.domain.dto.request.MaterialAddRequest;
import com.platform.ems.domain.dto.request.material.BasMaterialSkuRequest;
import com.platform.ems.domain.dto.response.*;
import com.platform.ems.domain.dto.response.form.BasMaterialSaleStationCategoryForm;
import com.platform.ems.enums.BusinessType;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.enums.Status;
import com.platform.ems.mapper.*;
import com.platform.ems.plug.domain.*;
import com.platform.ems.plug.mapper.*;
import com.platform.ems.service.*;
import com.platform.ems.util.*;
import com.platform.ems.util.data.ComUtil;
import com.platform.system.domain.SysBusinessBcst;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.*;
import com.platform.system.service.ISysDictDataService;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;

/**
 * 物料&商品&服务档案Service业务层处理
 *
 * @author linhongwei
 * @date 2021-03-12
 */
@Service
@SuppressWarnings("all")
@Slf4j
public class BasMaterialServiceImpl extends ServiceImpl<BasMaterialMapper, BasMaterial> implements IBasMaterialService {

    /*
    对于编码：
    不管是什么物料类别，都不能重复
    对于名称：
    物料类别为服务、外采样、物料，在表中的名称都不能重复
    物料类别为样品、商品，在表中的名称可以重复，但不能和其它三个物料类别重复
     */
    @Autowired
    private BasMaterialMapper basMaterialMapper;
    @Autowired
    private BasMaterialAttachmentMapper basMaterialAttachmentMapper;
    @Autowired
    private BasMaterialSaleStationMapper basMaterialSaleStationMapper;
    @Autowired
    private BasMaterialSkuMapper basMaterialSkuMapper;
    @Autowired
    private ConMaterialClassMapper conMaterialClassMapper;
    @Autowired
    private ConMaterialTypeMapper conMaterialTypeMapper;
    @Autowired
    private ConMeasureUnitMapper conMeasureUnitMapper;
    @Autowired
    private BasSkuMapper basSkuMapper;
    @Autowired
    private TecModelMapper tecModelMapper;
    @Autowired
    private BasPlantMapper basPlantMapper;
    @Autowired
    private BasSkuGroupMapper basSkuGroupMapper;
    @Autowired
    private BasSkuGroupItemMapper basSkuGroupItemMapper;
    @Autowired
    private BasProductSeasonMapper basProductSeasonMapper;
    @Autowired
    private BasMaterialBarcodeMapper basMaterialBarcodeMapper;
    @Autowired
    private BasMaterialCertificateMapper basMaterialCertificateMapper;
    @Autowired
    private IConBarcodeRangeConfigService barcodeRangeConfigService;
    @Autowired
    private TecBomHeadMapper tecBomHeadMapper;
    @Autowired
    private TecProductLineMapper tecProductLineMapper;
    @Autowired
    private TecBomItemMapper tecBomItemMapper;
    @Autowired
    private CosProductCostMapper cosProductCostMapper;
    @Autowired
    private RedisCache redisService;
    @Autowired
    private BasCustomerMapper basCustomerMapper;
    @Autowired
    private PayProductProcessStepMapper payProductProcessStepMapper;
    @Autowired
    private ConSaleStationMapper conSaleStationMapper;
    @Autowired
    private SysBusinessBcstMapper businessBcstMapper;
    @Autowired
    private ConTaxRateMapper conTaxRateMapper;
    @Autowired
    private ConBcstUserConfigMapper conBcstUserConfigMapper;
    @Autowired
    private SysUserMapper userMapper;
    @Autowired
    private ISysDictDataService sysDictDataService;
    @Autowired
    private SysClientMapper sysClientMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private IPurPriceInforService purPriceInforService;
    @Autowired
    RedissonClient redissonClient;
    @Autowired
    private MinioConfig minioConfig;
    @Autowired
    private MinioClient client;

    private static final String LOCK_KEY = "MATERIAL_STOCK";

    private static final String TITLE = "物料/商品/服务档案";

    private static final String DATAOBJECT = "Material";

    @Autowired
    private InvInventoryLocationMapper invInventoryLocationMapper;
    @Autowired
    private SalSalesOrderItemMapper salSalesOrderItemMapper;
    @Autowired
    private PurPurchaseOrderItemMapper purPurchaseOrderItemMapper;
    @Autowired
    private ISalSalesOrderService salSalesOrderService;

    @Autowired
    private SysDefaultSettingClientMapper sysDefaultSettingClientMapper;

    private static String KEY = "";

    private static final String[] BOM_STATUS = {HandleStatus.SAVE.getCode(), HandleStatus.RETURNED.getCode(), HandleStatus.SUBMIT.getCode()};

    private static final String[] ORDER_STATUS = {HandleStatus.SAVE.getCode(), HandleStatus.CONFIRMED.getCode(), HandleStatus.RETURNED.getCode(), HandleStatus.SUBMIT.getCode(), HandleStatus.CHANGEAPPROVAL.getCode()};

    /**
     * 查询物料&商品&服务档案
     *
     * @param materialSid 物料&商品&服务档案ID
     * @return 物料&商品&服务档案
     */
    @Override
    public BasMaterial selectBasMaterialById(Long materialSid) {
        BasMaterial basMaterial = basMaterialMapper.selectBasMaterialById(materialSid);
        if (basMaterial == null) {
            return null;
        }
        getPictuerPath(basMaterial);
        //物料&商品-附件对象
        BasMaterialAttachment basMaterialAttachment = new BasMaterialAttachment();
        basMaterialAttachment.setMaterialSid(materialSid);
        List<BasMaterialAttachment> basMaterialAttachmentList = basMaterialAttachmentMapper.selectBasMaterialAttachmentList(basMaterialAttachment);

        //商品销售站点对象
        basMaterial.setSaleStationList(new ArrayList<>());
        BasMaterialSaleStation saleStation = new BasMaterialSaleStation();
        saleStation.setMaterialSid(materialSid);
        List<BasMaterialSaleStation> basMaterialSaleStationList = basMaterialSaleStationMapper
                .selectBasMaterialSaleStationList(saleStation);
        if (CollectionUtil.isNotEmpty(basMaterialSaleStationList)) {
            // 排序
            basMaterialSaleStationList = basMaterialSaleStationList
                    .stream().sorted(Comparator.comparing(BasMaterialSaleStation::getSaleStationCode))
                    .collect(Collectors.toList());
            basMaterial.setSaleStationList(basMaterialSaleStationList);
        }

        //物料&商品-SKU明细对象
        BasMaterialSku basMaterialSku = new BasMaterialSku();
        basMaterialSku.setMaterialSid(materialSid);
        List<BasMaterialSku> basMaterialSkus = basMaterialSkuMapper.selectBasMaterialSkuListByNameSort(basMaterialSku);
        if (CollectionUtil.isNotEmpty(basMaterialSkus)) {
            // 如果无序号再排序
            List<BasMaterialSku> haveSort = basMaterialSkus.stream().filter(item -> item.getSort() != null).collect(Collectors.toList());
            basMaterialSkus = basMaterialSkus.stream().filter(item -> item.getSort() == null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(basMaterialSkus)) {
                List<BasMaterialSku> cm = basMaterialSkus.stream().filter(item -> item.getSkuType().equals(ConstantsEms.SKUTYP_CM)).collect(Collectors.toList());
                List<BasMaterialSku> ys = basMaterialSkus.stream().filter(item -> item.getSkuType().equals(ConstantsEms.SKUTYP_YS)).collect(Collectors.toList());
                basMaterialSkus = basMaterialSkus.stream().filter(item -> !item.getSkuType().equals(ConstantsEms.SKUTYP_CM) && !item.getSkuType().equals(ConstantsEms.SKUTYP_YS)).collect(Collectors.toList());
                //对除了颜色的sku按中文排序
                if (CollectionUtil.isNotEmpty(ys)) {
                    Comparator<Object> com = Collator.getInstance(java.util.Locale.CHINA);
                    Collections.sort(ys, new Comparator<BasMaterialSku>() {
                        @Override
                        public int compare(BasMaterialSku info1, BasMaterialSku info2) {
                            Comparator<Object> com = Collator.getInstance(java.util.Locale.CHINA);
                            return com.compare(info1.getSkuName(), info2.getSkuName());
                        }
                    });
                }
                //对尺码排序
                if (CollectionUtil.isNotEmpty(cm)) {
                    cm.forEach(li -> {
                        String skuName = li.getSkuName();
                        String[] nameSplit = skuName.split("/");
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
                    });
                    List<BasMaterialSku> allList = new ArrayList<>();
                    List<BasMaterialSku> allThirdList = new ArrayList<>();
                    List<BasMaterialSku> sortThird = cm.stream().filter(li -> li.getThirdSort() != null).collect(Collectors.toList());
                    List<BasMaterialSku> sortThirdNull = cm.stream().filter(li -> li.getThirdSort() == null).collect(Collectors.toList());
                    sortThird = sortThird.stream().sorted(Comparator.comparing(li -> li.getThirdSort())).collect(Collectors.toList());
                    allThirdList.addAll(sortThird);
                    allThirdList.addAll(sortThirdNull);
                    List<BasMaterialSku> sort = allThirdList.stream().filter(li -> li.getSecondSort() != null).collect(Collectors.toList());
                    sort = sort.stream().sorted(Comparator.comparing(li -> Integer.valueOf(li.getSecondSort()))).collect(Collectors.toList());
                    List<BasMaterialSku> sortNull = allThirdList.stream().filter(li -> li.getSecondSort() == null).collect(Collectors.toList());
                    allList.addAll(sort);
                    allList.addAll(sortNull);
                    cm = allList.stream().sorted(Comparator.comparing(item -> item.getFirstSort())
                    ).collect(Collectors.toList());
                }
                basMaterialSkus.addAll(ys);
                basMaterialSkus.addAll(cm);
            }
            else {
                basMaterialSkus = new ArrayList<>();
            }
            if (CollectionUtil.isNotEmpty(haveSort)) {
                haveSort = haveSort.stream().sorted(Comparator.comparing(BasMaterialSku::getSort)).collect(Collectors.toList());
                basMaterialSkus.addAll(haveSort);
            }
        }
        basMaterial.setBasMaterialSkuList(basMaterialSkus);
        basMaterial.setAttachmentList(basMaterialAttachmentList);
        MongodbUtil.find(basMaterial);
        return basMaterial;
    }

    /**
     * 查询物料&商品&服务档案 详细档案
     */
    @Override
    public BasMaterialPicture selectBasMaterialPicture(Long materialSid) {
        BasMaterialPicture materialPicture = basMaterialMapper.selectBasMaterialPicture(materialSid);
        if (materialPicture != null) {
            materialPicture.setPicturePathSecondList(ComUtil.strToArr(materialPicture.getPicturePathSecond()));
        }
        return materialPicture;
    }

    /**
     * 商品停用校验
     */
    @Override
    public List<BasMaterialDisabledResponse> judgeDisable(List<BasMaterial> list) {
        List<BasMaterialDisabledResponse> msgList = new ArrayList<>();
        List<DictData> categoryDict = sysDictDataService.selectDictData("s_material_category"); //
        Map<String, String> categoryMaps = categoryDict.stream().collect(Collectors.toMap(DictData::getDictValue, DictData::getDictLabel, (key1, key2) -> key2));

        return msgList;
    }

    /**
     * 查询物料&商品&服务档案列表
     *
     * @param basMaterial 物料&商品&服务档案
     * @return 物料&商品&服务档案
     */
    @Override
    public List<BasMaterial> selectBasMaterialList(BasMaterial basMaterial) {
        List<BasMaterial> basMaterials = basMaterialMapper.selectBasMaterialList(basMaterial);
        ConTaxRate taxRate = conTaxRateMapper.selectOne(new QueryWrapper<ConTaxRate>().lambda().eq(ConTaxRate::getIsDefault, "Y"));
        basMaterials.forEach(item -> {
            if (taxRate != null) {
                item.setTaxRate(taxRate.getTaxRateValue().toString());
            }
        });
        return basMaterials;
    }

    /**
     * 下拉框接口
     */
    @Override
    public List<BasMaterialDropDown> selectMaterialList(BasMaterial basMaterial) {
        return basMaterialMapper.selectMaterialList(basMaterial);
    }

    @Override
    public String checkCode(BasMaterial basMaterial) {
        String code = null;
        QueryWrapper<BasMaterial> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(BasMaterial::getMaterialCode, basMaterial.getMaterialCode());
        if (basMaterial.getMaterialSid() != null) {
            queryWrapper.lambda().ne(BasMaterial::getMaterialSid, basMaterial.getMaterialSid());
        }
        List<BasMaterial> list = basMaterialMapper.selectList(queryWrapper);
        if (CollectionUtil.isNotEmpty(list)) {
            throw new BaseException("编码已存在");
        }
        return code;
    }

    @Override
    public AjaxResult checkVendor(BasMaterial basMaterial) {
        //如果是查询页面批量确认走这里
        if (basMaterial.getMaterialSidList() != null && basMaterial.getMaterialSidList().length > 0) {
            // 报错
            EmsResultEntity resultEntity = confirmCheck(basMaterial);
            if (EmsResultEntity.ERROR_TAG.equals(resultEntity.getTag())) {
                return AjaxResult.success(resultEntity);
            }
            if ("XIEF".equals(basMaterial.getExportType())) {
                return AjaxResult.success(true);
            }
            // 提示
            String codes = "";
            for (Long sid : basMaterial.getMaterialSidList()) {
                BasMaterial one = basMaterialMapper.selectOne(new QueryWrapper<BasMaterial>().lambda().eq(BasMaterial::getMaterialSid, sid));
                if (one == null || one.getVendorSid() == null || StrUtil.isBlank(one.getSupplierProductCode())) {
                    continue;
                }
                List<BasMaterial> list = basMaterialMapper.selectList(new QueryWrapper<BasMaterial>()
                        .lambda().eq(BasMaterial::getVendorSid, one.getVendorSid()).eq(BasMaterial::getSupplierProductCode, one.getSupplierProductCode()));
                if (CollectionUtils.isNotEmpty(list) && list.size() >= 2) {
                    codes = codes + one.getMaterialCode() + ",";
                }
            }
            if (StrUtil.isNotBlank(codes)) {
                codes = codes.substring(0, codes.lastIndexOf(","));
                return AjaxResult.success("物料编码：" + codes + " 在系统中已存在相同“供应商/供方编码”的物料档案，确定是否继续?", false);
            }
        }
        //如果是新建编辑变更页面走这里
        if (basMaterial.getVendorSid() != null && StrUtil.isNotBlank(basMaterial.getSupplierProductCode())) {
            List<BasMaterial> list = basMaterialMapper.selectList(new QueryWrapper<BasMaterial>().lambda().eq(BasMaterial::getVendorSid, basMaterial.getVendorSid())
                    .eq(BasMaterial::getSupplierProductCode, basMaterial.getSupplierProductCode()));
            if (CollectionUtils.isNotEmpty(list)) {
                //如果是编辑变更物料
                if (basMaterial.getMaterialSid() != null) {
                    for (BasMaterial material : list) {
                        if (!material.getMaterialSid().equals(basMaterial.getMaterialSid())) {
                            return AjaxResult.success(false);
                        }
                    }
                }
                //如果是新建物料
                else {
                    return AjaxResult.success(false);
                }
            }
        }
        return AjaxResult.success(true);
    }

    @Override
    public void checkSelfCode(BasMaterial basMaterial) {
        //新建 编辑 变更
        if (ArrayUtil.isEmpty(basMaterial.getMaterialSidList())) {
            // 样品
            if (ConstantsEms.MATERIAL_CATEGORY_YP.equals(basMaterial.getMaterialCategory())
                    && CollectionUtil.isNotEmpty(basMaterial.getBasMaterialSkuList()) && basMaterial.getSku2GroupSid() != null) {
                List<BasMaterialSku> cmList = basMaterial.getBasMaterialSkuList().stream().filter(o -> ConstantsEms.SKUTYP_CM.equals(o.getSkuType())).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(cmList)) {
                    List<Long> skuSidList = cmList.stream().map(BasMaterialSku::getSkuSid).collect(Collectors.toList());
                    List<BasSkuGroupItem> skuGroupItemList = basSkuGroupItemMapper.selectList(new QueryWrapper<BasSkuGroupItem>().lambda()
                            .eq(BasSkuGroupItem::getSkuGroupSid, basMaterial.getSku2GroupSid()).in(BasSkuGroupItem::getSkuSid, skuSidList));
                    if (CollectionUtil.isEmpty(skuGroupItemList) || skuGroupItemList.size() != skuSidList.size()) {
                        throw new CustomException("存在尺码明细不属于尺码组，是否继续操作？");
                    }
                }
            }
            //商品才走
            if (!ConstantsEms.MATERIAL_CATEGORY_SP.equals(basMaterial.getMaterialCategory())) {
                return;
            }
            if (StrUtil.isNotBlank(basMaterial.getSampleCodeSelf())) {
                QueryWrapper<BasMaterial> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(BasMaterial::getSampleCodeSelf, basMaterial.getSampleCodeSelf());
                //不是新建的时候 不要和自身校验重复
                if (basMaterial.getMaterialSid() != null) {
                    queryWrapper.lambda().ne(BasMaterial::getMaterialSid, basMaterial.getMaterialSid());
                }
                List<BasMaterial> list = basMaterialMapper.selectList(queryWrapper);
                if (CollectionUtil.isNotEmpty(list)) {
                    throw new CustomException("我司样衣号已存在，是否确认执行操作？");
                }
            }
        }
        //查询页面批量点 确认
        else {
            List<BasMaterial> materialList = basMaterialMapper.selectList(new QueryWrapper<BasMaterial>().lambda()
                    .in(BasMaterial::getMaterialSid, basMaterial.getMaterialSidList()));
            if (CollectionUtil.isNotEmpty(materialList)) {
                String codes = "";
                for (BasMaterial material : materialList) {
                    if (StrUtil.isNotBlank(material.getSampleCodeSelf())) {
                        List<BasMaterial> list = basMaterialMapper.selectList(new QueryWrapper<BasMaterial>().lambda()
                                .eq(BasMaterial::getSampleCodeSelf, material.getSampleCodeSelf())
                                .ne(BasMaterial::getMaterialSid, material.getMaterialSid()));
                        if (CollectionUtil.isNotEmpty(list)) {
                            codes = codes + material.getMaterialCode() + ";";
                        }
                    }
                }
                if (StrUtil.isNotBlank(codes)) {
                    if (codes.endsWith(";")) {
                        codes = codes.substring(0, codes.length() - 1);
                    }
                    codes = codes + "的我司样衣号已存在，是否确认执行操作？";
                    throw new CustomException(codes);
                }
            }
        }
    }

    /**
     * 点击“新增行”按钮时，判断此商品的BOM处理状态是否是“审批中”，如是，则提示错误信息：此商品BOM正在审批中，不允许加色，请先将此商品的BOM驳回。
     * 点击“启用/停用”时，判断此商品的BOM处理状态是否是“审批中”，如是，则提示错误信息：此商品BOM正在审批中，不允许启用/停用颜色，请先将此商品的BOM驳回。
     *
     * @param materialSid 物料&商品&服务档案
     * @return
     */
    @Override
    public void checkBomApproval(Long materialSid) {
        List<TecBomHead> list = tecBomHeadMapper.selectList(new QueryWrapper<TecBomHead>().lambda().eq(TecBomHead::getMaterialSid, materialSid)
                .eq(TecBomHead::getStatus, ConstantsEms.ENABLE_STATUS)
                .in(TecBomHead::getHandleStatus, new String[]{HandleStatus.SUBMIT.getCode(), HandleStatus.CHANGEAPPROVAL.getCode()}));
        if (CollectionUtil.isNotEmpty(list)) {
            throw new BaseException("该商品存在bom状态为审批中和变更审批中");
        }
    }

    @Override
    public BasMaterialBarcode getBarcodeLabelInfo(Long barcode) {
        if (barcode == null) {
            return null;
        }
        BasMaterialBarcode basMaterialBarcode = basMaterialBarcodeMapper.selectBasMaterialBarcodeByCode(barcode);
        SysClient sysClient = sysClientMapper.selectOne(new QueryWrapper<SysClient>()
                .lambda().eq(SysClient::getClientId, ApiThreadLocalUtil.get().getClientId()));
        if (StrUtil.isNotBlank(sysClient.getLogoPicturePath())){
            GetObjectResponse object = null;
            String path = sysClient.getLogoPicturePath();
            String str1 = path.substring(0, path.indexOf("/" + minioConfig.getBucketName()));
            String str2 = path.substring(str1.length()+9);
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(str2).build();
            try {
                object= client.getObject(args);
                FastByteArrayOutputStream fos = new FastByteArrayOutputStream();
                BufferedImage image = ImageIO.read(object);
                BufferedImage images = new BufferedImage(55, 55, TYPE_INT_RGB);
                Graphics graphics = images.createGraphics();
                graphics.drawImage(image,0,0,55,55,null);
                ImageIO.write(images, "png", fos);
                //将Logo转成要在前端显示需要转成Base64
                basMaterialBarcode.setLogoPicturePath(Base64.getEncoder().encodeToString(fos.toByteArray()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 生成二维码并指定宽高
        BufferedImage generate = QrCodeUtil.generate(basMaterialBarcode.getBarcode().toString(), 80, 80);
        // 转换流信息写出
        FastByteArrayOutputStream os = new FastByteArrayOutputStream();
        try {
            ImageIO.write(generate, "jpg", os);
            //如果二维码要在前端显示需要转成Base64
            String qrcode = Base64.getEncoder().encodeToString(os.toByteArray());
            basMaterialBarcode.setQrCode(qrcode);
        } catch (IOException e) {
            throw new BaseException(e.getMessage());
        }
        return basMaterialBarcode;
    }

    @Override
    public BasMaterial getLabelInfo(Long basMaterialSid) {
        if (basMaterialSid == null) {
            return null;
        }
        BasMaterial material = basMaterialMapper.selectBasMaterialById(basMaterialSid);
        if (ConstantsEms.MATERIAL_CATEGORY_WCY.equals(material.getMaterialCategory())) {
            SysClient sysClient = sysClientMapper.selectOne(new QueryWrapper<SysClient>()
                    .lambda().eq(SysClient::getClientId, ApiThreadLocalUtil.get().getClientId()));
            if (StrUtil.isNotBlank(sysClient.getLogoPicturePath())){
                GetObjectResponse object = null;
                String path = sysClient.getLogoPicturePath();
                String str1 = path.substring(0, path.indexOf("/" + minioConfig.getBucketName()));
                String str2 = path.substring(str1.length()+9);
                GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(str2).build();
                try {
                    object= client.getObject(args);
                    FastByteArrayOutputStream fos = new FastByteArrayOutputStream();
                    BufferedImage image = ImageIO.read(object);
/*                    Thumbnails.of(image)
                            .size(55, 55)
                            .outputFormat("png").toOutputStream(fos);
                    *//*
                        <dependency>
                            <groupId>net.coobird</groupId>
                            <artifactId>thumbnailator</artifactId>
                            <version>0.4.14</version>
                        </dependency>
                     *//*
                     */
                    BufferedImage images = new BufferedImage(55, 55, TYPE_INT_RGB);
                    Graphics graphics = images.createGraphics();
                    graphics.drawImage(image,0,0,55,55,null);
                    ImageIO.write(images, "png", fos);
                    //将Logo转成要在前端显示需要转成Base64
                    material.setLogoPicturePath(Base64.getEncoder().encodeToString(fos.toByteArray()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
//        //附带logo,设置纠错等级，去白边，.set可自定义属性
//        String logoPath = ClassUtils.getDefaultClassLoader().getResource("static").getPath() + "/emslogo1.png";
//        BufferedImage generate = QrCodeUtil.generate(material.getMaterialCode(),
//                QrConfig.create().setImg(logoPath).setErrorCorrection(ErrorCorrectionLevel.H).setMargin(0).setWidth(50).setHeight(50));
        // 生成二维码并指定宽高
        BufferedImage generate = QrCodeUtil.generate(material.getMaterialCode().toString(), 80, 80);
        // 转换流信息写出
        FastByteArrayOutputStream os = new FastByteArrayOutputStream();
        try {
            ImageIO.write(generate, "jpg", os);
            //如果二维码要在前端显示需要转成Base64
            String qrcode = Base64.getEncoder().encodeToString(os.toByteArray());
            material.setQrCode(qrcode);
        } catch (IOException e) {
            throw new BaseException(e.getMessage());
        }
        return material;
    }

    @Override
    public List<BasMaterialBarcode> selectBasMaterialBarcodeList(BasMaterialBarcode basMaterialBarcode) {
        return basMaterialBarcodeMapper.selectBasMaterialBarcodeList(basMaterialBarcode);
    }

    @Override
    public List<BasMaterialBarcode> getBasMaterialBarcodeList(BasMaterialBarcode basMaterialBarcode) {
        return basMaterialBarcodeMapper.getBasMaterialBarcodeList(basMaterialBarcode);
    }

    @Override
    public List<BasMaterial> selectBasMaterialSkuList(BasMaterial basMaterial) {
        return basMaterialBarcodeMapper.getBasMaterialSkuList(basMaterial);
    }

    //变更时，对bom操作
    public void changeBom(BasMaterial basMaterial) {
        Long materialSid = basMaterial.getMaterialSid();
        List<TecBomHead> tecBomHeads = tecBomHeadMapper.selectList(new QueryWrapper<TecBomHead>().lambda()
                .eq(TecBomHead::getMaterialSid, materialSid));
        //判断是否存在对应的款
        if (CollectionUtils.isNotEmpty(tecBomHeads)) {
            List<BasMaterialSku> basMaterialSkuList = basMaterial.getBasMaterialSkuList();
            basMaterialSkuList = basMaterialSkuList.stream().filter(item -> item.getSkuType().equals(ConstantsEms.SKUTYP_YS)).collect(Collectors.toList());
            TecBomHead head = new TecBomHead();
            BeanCopyUtils.copyProperties(tecBomHeads.get(0), head);
            basMaterialSkuList.forEach(li -> {
                List<TecBomHead> list = tecBomHeads.stream().filter(bom -> bom.getSku1Sid().toString().equals(li.getSkuSid().toString())).collect(Collectors.toList());
                //bom存在对应的sku1
                if (CollectionUtils.isNotEmpty(list)) {
                    TecBomHead tecBomHead = list.get(0);
                    tecBomHead.setStatus(li.getStatus());
                    tecBomHeadMapper.updateById(tecBomHead);
                } else {
                    head.setSku1Sid(li.getSkuSid());
                    List<TecBomItem> tecBomItems = tecBomItemMapper.selectList(new QueryWrapper<TecBomItem>().lambda()
                            .eq(TecBomItem::getBomSid, head.getBomSid()));
                    head.setBomSid(null);
                    head.setCreatorAccount(null);
                    head.setStatus(li.getStatus());
                    tecBomHeadMapper.insert(head);
                    //初始化颜色
                    tecBomItems.forEach(item -> {
                        item.setBomSid(head.getBomSid());
                        item.setCreateDate(null);
                        item.setCreatorAccount(null);
                        item.setBomItemSid(null);
                        item.setBomMaterialSku1Sid(null);
                    });
                    tecBomItems.forEach(tecbom -> {
                        tecBomItemMapper.insert(tecbom);
                    });
                }
            });
        }
    }

    /**
     * 获取自动编码的编码
     *
     * @param basSku
     */
    private void getCode(BasMaterial basMaterial) {
        Map<String, String> map = CodeRuleUtil.allocation(DATAOBJECT, basMaterial.getMaterialCategory());
        if (map == null || StrUtil.isBlank(map.get(AutoIdField.code))) {
            throw new CustomException("编码不能为空");
        } else {
            basMaterial.setMaterialCode(map.get(AutoIdField.code));
            KEY = map.get(AutoIdField.key_name);
            Map<String, Object> params = new HashMap<>();
            params.put("material_code", basMaterial.getMaterialCode());
            List<BasMaterial> basMaterial1 = basMaterialMapper.selectByMap(params);
            if (basMaterial1.size() > 0) {
                getCode(basMaterial);
            }
        }
    }

    /**
     * 新增物料&商品&服务档案
     *
     * @param basMaterial 物料&商品&服务档案
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasMaterial(BasMaterial basMaterial) {
        RLock lock = redissonClient.getLock(LOCK_KEY);
        if (lock.isLocked()) {
            throw new CustomException("请勿重复操作");
        }
        lock.lock(15L, TimeUnit.SECONDS);
        try {
            Map<String, Object> params = new HashMap<>();
            List<BasMaterial> checkMaterial = new ArrayList<>();
            if (StrUtil.isBlank(basMaterial.getMaterialCode()) && !ConstantsEms.MATERIAL_CATEGORY_YP.equals(basMaterial.getMaterialCategory())) {
                getCode(basMaterial);
            }
            if (StrUtil.isNotBlank(basMaterial.getMaterialCode())) {
                params.put("material_code", basMaterial.getMaterialCode());
                checkMaterial = basMaterialMapper.selectByMap(params);
                if (checkMaterial.size() > 0) {
                    if (ConstantsEms.MATERIAL_CATEGORY_WL.equals(basMaterial.getMaterialCategory())) {
                        throw new CustomException("物料编码已存在,请查看");
                    }
                    else if (ConstantsEms.MATERIAL_CATEGORY_SP.equals(basMaterial.getMaterialCategory())
                            || ConstantsEms.MATERIAL_CATEGORY_YP.equals(basMaterial.getMaterialCategory())) {
                        throw new CustomException("商品编码(款号)已存在,请查看");
                    }
                    throw new CustomException("编码已存在,请查看");
                }
                params.clear();
            }
            if (
                    ConstantsEms.MATERIAL_CATEGORY_FW.equals(basMaterial.getMaterialCategory()) ||
                            ConstantsEms.MATERIAL_CATEGORY_WCY.equals(basMaterial.getMaterialCategory())) {
                params.put("material_name", basMaterial.getMaterialName());
                checkMaterial = basMaterialMapper.selectByMap(params);
                if (checkMaterial.size() > 0) {
                    throw new CustomException("名称已存在,请查看");
                }
            } else if (ConstantsEms.MATERIAL_CATEGORY_WL.equals(basMaterial.getMaterialCategory()) ||
                    ConstantsEms.MATERIAL_CATEGORY_SP.equals(basMaterial.getMaterialCategory()) ||
                    ConstantsEms.MATERIAL_CATEGORY_YP.equals(basMaterial.getMaterialCategory())) {
                params.put("material_name", basMaterial.getMaterialName());
                checkMaterial = basMaterialMapper.selectList(new QueryWrapper<BasMaterial>().lambda()
                        .eq(BasMaterial::getMaterialName, basMaterial.getMaterialName())
                        .in(BasMaterial::getMaterialCategory, new String[]{ConstantsEms.MATERIAL_CATEGORY_FW, ConstantsEms.MATERIAL_CATEGORY_WCY}));
                if (checkMaterial.size() > 0) {
                    if (ConstantsEms.MATERIAL_CATEGORY_WL.equals(basMaterial.getMaterialCategory())) {
                        throw new CustomException("物料名称已存在,请查看");
                    }
                    else if (ConstantsEms.MATERIAL_CATEGORY_SP.equals(basMaterial.getMaterialCategory())) {
                        throw new CustomException("商品名称已存在,请查看");
                    }
                    else if (ConstantsEms.MATERIAL_CATEGORY_YP.equals(basMaterial.getMaterialCategory())) {
                        throw new CustomException("样衣名称已存在,请查看");
                    }
                    throw new CustomException("名称已存在,请查看");
                }
            } else {
            }
            if (ConstantsEms.MATERIAL_CATEGORY_YP.equals(basMaterial.getMaterialCategory()) && StrUtil.isNotBlank(basMaterial.getSampleCodeSelf())) {
                checkMaterial = basMaterialMapper.selectList(new QueryWrapper<BasMaterial>().lambda().eq(BasMaterial::getSampleCodeSelf, basMaterial.getSampleCodeSelf()));
                if (checkMaterial.size() > 0) {
                    throw new CustomException("我司样衣号已存在,请查看");
                }
            }
            judge(basMaterial);
            if (ConstantsEms.CHECK_STATUS.equals(basMaterial.getHandleStatus()) && CollectionUtils.isNotEmpty(basMaterial.getBasMaterialSkuList())) {
                checkMaterialSku(basMaterial);
            }
            this.setConfirmInfo(basMaterial);
            String name = "";
            name = checkSkuStatus(basMaterial.getBasMaterialSkuList());
            if (StrUtil.isNotBlank(name)) {
                throw new CustomException("SKU档案" + name + "已停用，不能启用！");
            }
            basMaterial.setMaterialSid(IdWorker.getId());
            //物料&商品-附件对象
            List<BasMaterialAttachment> basMaterialAttachmentList = basMaterial.getAttachmentList();
            //工艺单是否上传和是否完成的默认值
            basMaterial.setIsHasUploadedZhizaodan(ConstantsEms.NO).setIsUploadZhizaodan(ConstantsEms.YES);
            if (CollectionUtils.isNotEmpty(basMaterialAttachmentList)) {
                addBasMaterialAttachment(basMaterial);
                basMaterialAttachmentList.forEach(item -> {
                    item.setMaterialAttachmentSid(null);
                });
            }

            // 商品销售站点对象
            List<BasMaterialSaleStation> basMaterialSaleStationList = basMaterial.getSaleStationList();
            if (CollectionUtil.isNotEmpty(basMaterialSaleStationList)) {
                // 校验重复
                Map<String, List<BasMaterialSaleStation>> map = basMaterialSaleStationList.stream()
                        .collect(Collectors.groupingBy(e -> String.valueOf(e.getSaleStationSid())));
                if (map.size() < basMaterialSaleStationList.size()) {
                    throw new CustomException("网店运营信息存在重复的销售站点/网店编码");
                }
                basMaterialSaleStationList.forEach(item->{
                    item.setMaterialSid(basMaterial.getMaterialSid());
                    ConSaleStation station = conSaleStationMapper.selectById(item.getSaleStationSid());
                    if (station != null) {
                        item.setSaleStationCode(station.getCode());
                    }
                });
                basMaterialSaleStationMapper.inserts(basMaterialSaleStationList);
            }

            if (ConstantsEms.MATERIAL_CATEGORY_SP.equals(basMaterial.getMaterialCategory())) {
                basMaterial.setIsCreateBom(ConstantsEms.YES).setIsCreateProductcost(ConstantsEms.YES);
                basMaterial.setIsCreateProductLine(ConstantsEms.YES);
            } else {
                basMaterial.setIsCreateBom(ConstantsEms.NO).setIsCreateProductcost(ConstantsEms.NO);
                basMaterial.setIsCreateProductLine(ConstantsEms.NO);
            }
            if (ConstantsEms.MATERIAL_CATEGORY_YP.equals(basMaterial.getMaterialCategory())) {
                basMaterial.setIsCreateFromSample(ConstantsEms.YES);
                basMaterial.setIsCreateBom(ConstantsEms.YES).setIsCreateProductcost(ConstantsEms.YES);
            } else {
                basMaterial.setIsCreateFromSample(ConstantsEms.NO);
            }
            if (ConstantsEms.MATERIAL_CATEGORY_WCY.equals(basMaterial.getMaterialCategory())) {
                basMaterial.setReimburseStatus(ConstantsEms.REIMBURSE_STATUS_WBX);
            }
            basMaterial.setIsHasCreatedBom(ConstantsEms.NO).setIsHasCreatedProductcost(ConstantsEms.NO).setIsHasCreatedProductLine(ConstantsEms.NO);
            setPictuerPath(basMaterial);
            //按老板要求在这里再校验一次编码
            List<BasMaterial> codeCheck = basMaterialMapper.selectList(new QueryWrapper<BasMaterial>().lambda()
                    .eq(BasMaterial::getMaterialCode, basMaterial.getMaterialCode()));
            if (CollectionUtil.isNotEmpty(codeCheck)) {
                throw new CustomException("编码已存在,请查看");
            }
            setProducePlantCode(basMaterial);
            setMaterialClassSids(basMaterial);
            SysDefaultSettingClient sysDefaultSettingClient = sysDefaultSettingClientMapper.selectOne(new QueryWrapper<SysDefaultSettingClient>()
                    .lambda().eq(SysDefaultSettingClient::getClientId,ApiThreadLocalUtil.get().getSysUser().getClientId()));
            if(sysDefaultSettingClient.getWpcRemindDays() != null){
                basMaterial.setWpcRemindDays(sysDefaultSettingClient.getWpcRemindDays());
            }else{
                basMaterial.setWpcRemindDays(30);
            }
            basMaterialMapper.insert(basMaterial);
            //物料&商品-SKU明细对象
            List<BasMaterialSku> basMaterialSkuList = basMaterial.getBasMaterialSkuList();
            if (CollectionUtils.isNotEmpty(basMaterialSkuList)) {
                addBasMaterialSku(basMaterial, basMaterialSkuList);
            }
            //新增、编辑确认时生成商品条码
            createBarcodeMethod(basMaterial);
            //价格记录表
            if (ConstantsEms.MATERIAL_CATEGORY_WL.equals(basMaterial.getMaterialCategory()) && ConstantsEms.CHECK_STATUS.equals(basMaterial.getHandleStatus())) {
                if (basMaterial.getVendorSid() != null && basMaterial.getQuotePriceTax() != null) {
                    insertPriceInfo(basMaterial);
                }
            }
            //工艺单动态通知
            this.sent(basMaterial);
            //待办通知
            SysTodoTask sysTodoTask = new SysTodoTask();
            if (ConstantsEms.SAVA_STATUS.equals(basMaterial.getHandleStatus())) {
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName("s_bas_material")
                        .setDocumentSid(basMaterial.getMaterialSid());
                sysTodoTask.setTitle("")
                        .setDocumentCode(String.valueOf(basMaterial.getMaterialCode()))
                        .setNoticeDate(new Date())
                        .setUserId(ApiThreadLocalUtil.get().getUserid());
                if (ConstantsEms.MATERIAL_CATEGORY_WL.equals(basMaterial.getMaterialCategory())) {
                    sysTodoTask.setTitle("物料档案: " + basMaterial.getMaterialCode() + " 当前是保存状态，请及时处理！");
                }
                if (ConstantsEms.MATERIAL_CATEGORY_SP.equals(basMaterial.getMaterialCategory())) {
                    sysTodoTask.setTitle("商品档案: " + basMaterial.getMaterialCode() + " 当前是保存状态，请及时处理！");
                }
                if (ConstantsEms.MATERIAL_CATEGORY_FW.equals(basMaterial.getMaterialCategory())) {
                    sysTodoTask.setTitle("服务档案: " + basMaterial.getMaterialCode() + " 当前是保存状态，请及时处理！");
                }
                if (ConstantsEms.MATERIAL_CATEGORY_YP.equals(basMaterial.getMaterialCategory())) {
                    sysTodoTask.setTitle("样品档案: " + basMaterial.getSampleCodeSelf() + " 当前是保存状态，请及时处理！");
                }
                if (ConstantsEms.MATERIAL_CATEGORY_WCY.equals(basMaterial.getMaterialCategory())) {
                    sysTodoTask.setTitle("外采样档案: " + basMaterial.getMaterialCode() + " 当前是保存状态，请及时处理！");
                }
                sysTodoTaskMapper.insert(sysTodoTask);
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbDeal.insert(basMaterial.getMaterialSid(), basMaterial.getHandleStatus(), null, TITLE, null, basMaterial.getImportType());
        } catch (BaseException e) {
            throw new BaseException(e.getMessage());
        } finally {
            redisService.deleteObject(KEY);
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return 1;
    }

    /**
     * 设置 物料分类的大类小类
     *
     * @param basMaterial 物料&商品&服务档案
     * @return 结果
     */
    public void setMaterialClassSids(BasMaterial basMaterial) {
        basMaterial.setBigClassSid(null).setBigClassCode(null)
                .setMiddleClassSid(null).setMiddleClassCode(null)
                .setSmallClassSid(null).setSmallClassCode(null);
        if (basMaterial.getMaterialClassSid() != null) {
            List<ConMaterialClass> parents = conMaterialClassMapper.selectConMaterialClassParentsListySon(basMaterial.getMaterialClassSid());
            if (CollectionUtil.isNotEmpty(parents)) {
                parents.forEach(item->{
                    if (item.getLevel() != null) {
                        if (item.getLevel() == 1) {
                            basMaterial.setBigClassSid(item.getMaterialClassSid())
                                    .setBigClassCode(item.getNodeCode());
                        }
                        if (item.getLevel() == 2) {
                            basMaterial.setMiddleClassSid(item.getMaterialClassSid())
                                    .setMiddleClassCode(item.getNodeCode());
                        }
                        if (item.getLevel() == 3) {
                            basMaterial.setSmallClassSid(item.getMaterialClassSid())
                                    .setSmallClassCode(item.getNodeCode());
                        }
                    }
                });
            }
        }
    }

    /**
     * 设置 负责生产工厂code(默认)
     *
     * @param basMaterial 物料&商品&服务档案
     * @return 结果
     */
    public void setProducePlantCode(BasMaterial basMaterial) {
        if (basMaterial.getProducePlantSid() == null) {
            return;
        }
        BasPlant plant = basPlantMapper.selectById(basMaterial.getProducePlantSid());
        if (plant != null) {
            basMaterial.setProducePlantCode(plant.getPlantCode());
        }
    }

    //新建或者修改时如果有多个图片就将多个图片拼接起来
    public void setPictuerPath(BasMaterial basMaterial) {
        if (basMaterial.getPicturePathList() != null && basMaterial.getPicturePathList().length > 0) {
            String picturePathSecond = StringUtils.join(basMaterial.getPicturePathList(), ";");
            basMaterial.setPicturePathSecond(picturePathSecond);
        } else {
            basMaterial.setPicturePathSecond(null);
        }
    }

    //取详情时将图片路径分割出来存入数组
    public void getPictuerPath(BasMaterial basMaterial) {
        if (StrUtil.isNotBlank(basMaterial.getPicturePathSecond())) {
            String[] picturePathList = basMaterial.getPicturePathSecond().split(";");
            basMaterial.setPicturePathList(picturePathList);
        }
    }

    public void judge(BasMaterial basMaterial) {
        String materialType = basMaterial.getMaterialType();
        if (ConstantsEms.MATERIAL_CATEGORY_WL.equals(basMaterial.getMaterialCategory())) {
            if (basMaterial.getVendorSid() == null && !ConstantsEms.NO.equals(ApiThreadLocalUtil.get().getSysUser().getClient().getIsRequiredVendorMaterial())
                    && ConstantsEms.CHECK_STATUS.equals(basMaterial.getHandleStatus())) {
                throw new CustomException("供应商不允许为空");
            }
            if (StrUtil.isBlank(basMaterial.getSupplierProductCode()) && !ConstantsEms.NO.equals(ApiThreadLocalUtil.get().getSysUser().getClient().getIsRequiredVendorMaterial())
                    && ConstantsEms.CHECK_STATUS.equals(basMaterial.getHandleStatus())) {
                throw new CustomException("供方编码不允许为空");
            }
            if (basMaterial.getQuotePriceTax() == null && !ConstantsEms.NO.equals(ApiThreadLocalUtil.get().getSysUser().getClient().getIsRequiredQuotePriceTaxMaterial())
                    && ConstantsEms.CHECK_STATUS.equals(basMaterial.getHandleStatus())){
                throw new CustomException("物料报价不允许为空");
            }
            else if (basMaterial.getQuotePriceTax() != null) {
                if (BigDecimal.ZERO.compareTo(basMaterial.getQuotePriceTax()) > 0){
                    throw new CustomException("物料报价不允许小于0");
                }
            }
        }
        if (ConstantsEms.MATERIAL_CATEGORY_WL.equals(basMaterial.getMaterialCategory())
                || ConstantsEms.MATERIAL_CATEGORY_SP.equals(basMaterial.getMaterialCategory())
                || ConstantsEms.MATERIAL_CATEGORY_YP.equals(basMaterial.getMaterialCategory())) {
            if (ConstantsEms.INVENTORY_PRICE_METHOD_GDJ.equals(basMaterial.getInventoryPriceMethod())) {
                if (basMaterial.getInventoryStandardPrice() == null) {
                    throw new CustomException("当“库存价核算方式”为“固定价”时，“固定价”不能为空");
                } else {
                    basMaterial.setCurrency(ConstantsFinance.CURRENCY_CNY).setCurrencyUnit(ConstantsFinance.CURRENCY_UNIT_YUAN);
                }
            }
            if (basMaterial.getInventoryPriceMethod() == null) {
                basMaterial.setInventoryPriceMethod(ConstantsEms.INVENTORY_PRICE_METHOD_JQPJJ);
            }
        }

    }

    /**
     * 插入价格记录表
     */
    private void insertPriceInfo(BasMaterial basMaterial) {
        //价格记录信息主表
        PurPriceInfor priceInfor = new PurPriceInfor();
        priceInfor.setMaterialSid(basMaterial.getMaterialSid())
                .setVendorSid(basMaterial.getVendorSid()).setCompanySid(basMaterial.getCompanySid())
                .setMaterialCategory(basMaterial.getMaterialCategory()).setPriceDimension(ConstantsEms.PRICE_K)
                .setRawMaterialMode(ConstantsEms.RAW_MATERIAL_MODE_WU).setPurchaseMode(ConstantsEms.DOCUMNET_TYPE_ZG);
        //价格记录信息明细表
        PurPriceInforItem priceInforItem = new PurPriceInforItem();
        if (basMaterial.getTaxRate() == null) {
            try {
                ConTaxRate rate = conTaxRateMapper.selectOne(new QueryWrapper<ConTaxRate>().lambda()
                        .eq(ConTaxRate::getIsDefault, ConstantsEms.YES));
                if (rate != null) {
                    priceInforItem.setTaxRate(rate.getTaxRateValue().toString());
                }
            } catch (Exception e) {
                throw new CustomException("系统默认税率配置错误，只允许存在一个默认税率");
            }
        } else {
            try {
                ConTaxRate rate = conTaxRateMapper.selectOne(new QueryWrapper<ConTaxRate>().lambda()
                        .eq(ConTaxRate::getTaxRateCode, basMaterial.getTaxRate()));
                if (rate != null) {
                    priceInforItem.setTaxRate(rate.getTaxRateValue().toString());
                }
            } catch (Exception e) {
                throw new CustomException("系统税率配置错误，税率编码不允许重复");
            }
        }
        priceInforItem.setQuotePriceTax(basMaterial.getQuotePriceTax()).setUnitPrice(basMaterial.getUnitPrice())
                .setUnitConversionRate(basMaterial.getUnitConversionRatePrice())
                .setPriceEnterMode(ConstantsEms.PRICE_INTER_MODER_TAX);
        BigDecimal tax = priceInforItem.getQuotePriceTax().divide(BigDecimal.ONE.add(priceInforItem.getTaxRate() == null ?
                BigDecimal.ZERO : new BigDecimal(priceInforItem.getTaxRate())), 6, BigDecimal.ROUND_HALF_UP);
        priceInforItem.setIsRecursionPrice(ConstantsEms.YES);
        priceInforItem.setQuotePrice(tax).setQuoteUpdateDate(new Date()).setQuoteUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        priceInforItem.setCurrency(ConstantsFinance.CURRENCY_CNY).setCurrencyUnit(ConstantsFinance.CURRENCY_UNIT_YUAN)
                .setUnitBase(basMaterial.getUnitBase());
        purPriceInforService.updatePriceInfor(priceInfor, priceInforItem);
    }

    /**
     * 新增、编辑确认时生成商品条码
     */
    private void createBarcodeMethod(BasMaterial basMaterial) {
        if (HandleStatus.CONFIRMED.getCode().equals(basMaterial.getHandleStatus())) {
            List<Long> sidList = new ArrayList<>();
            sidList.add(basMaterial.getMaterialSid());
            insertBarcode(sidList);
        }
    }

    private void updateBarcodeMethod(BasMaterial basMaterial) {
        BasMaterial result = new BasMaterial().setMaterialSid(basMaterial.getMaterialSid());
        List<BasMaterial> materialSkuList = basMaterialSkuMapper.getBasMaterialSkuList(result);
        //查询原本的商品条码
        List<BasMaterialBarcode> oldBarcodeList = basMaterialBarcodeMapper.selectList(new QueryWrapper<BasMaterialBarcode>().lambda().eq(BasMaterialBarcode::getMaterialSid, basMaterial.getMaterialSid()));
        Map<String, BasMaterialBarcode> map = new HashMap<>();
        oldBarcodeList.forEach(o -> {
            String key = "";
            if (o.getSku1Sid() != null) {
                key += o.getSku1Sid().toString() + ":";
            }
            if (o.getSku2Sid() != null) {
                key += o.getSku2Sid() != null ? o.getSku2Sid().toString() : "";
            }
            map.put(key, o);
        });
        List<BasMaterial> addList = new ArrayList<>();
        List<Long> barcodeSidsEnable = new ArrayList<>();
        List<Long> barcodeSidsDisenable = new ArrayList<>();
        for (int i = 0; i < materialSkuList.size(); i++) {
            String key = "";
            if (materialSkuList.get(i).getSku1Sid() != null) {
                key += materialSkuList.get(i).getSku1Sid().toString() + ":";
            }
            if (materialSkuList.get(i).getSku2Sid() != null) {
                key += materialSkuList.get(i).getSku2Sid() != null ? materialSkuList.get(i).getSku2Sid().toString() : "";
            }
            if (ObjectUtil.isEmpty(map.get(key))) {
                addList.add(materialSkuList.get(i));
            }
            if (ObjectUtil.isNotEmpty(map.get(key))) {
                BasMaterialBarcode basMaterialBarcode = map.get(key);
                //如果原来商品条码是启用 要改成停用的
                if (ConstantsEms.ENABLE_STATUS.equals(basMaterialBarcode.getStatus())) {
                    //现在是要停用，则加入停用队列
                    if (materialSkuList.get(i).getSku2Status() != null && !ConstantsEms.ENABLE_STATUS.equals(materialSkuList.get(i).getSku2Status())) {
                        barcodeSidsDisenable.add(basMaterialBarcode.getBarcodeSid());
                    }
                    if (materialSkuList.get(i).getSku1Status() != null && !ConstantsEms.ENABLE_STATUS.equals(materialSkuList.get(i).getSku1Status())) {
                        barcodeSidsDisenable.add(basMaterialBarcode.getBarcodeSid());
                    }
                    if (!ConstantsEms.ENABLE_STATUS.equals(basMaterial.getStatus())) {
                        barcodeSidsDisenable.add(basMaterialBarcode.getBarcodeSid());
                    }
                }
                //如果原来商品条码是停用 要改成启用的
                else {
                    if (materialSkuList.get(i).getSku2Status() != null && ConstantsEms.ENABLE_STATUS.equals(basMaterial.getStatus())
                            && ConstantsEms.ENABLE_STATUS.equals(materialSkuList.get(i).getSku1Status())
                            && ConstantsEms.ENABLE_STATUS.equals(materialSkuList.get(i).getSku2Status())) {
                        //现在是要启用用，则加入启用队列
                        barcodeSidsEnable.add(basMaterialBarcode.getBarcodeSid());
                    }
                    if (materialSkuList.get(i).getSku2Status() == null && ConstantsEms.ENABLE_STATUS.equals(basMaterial.getStatus())
                            && ConstantsEms.ENABLE_STATUS.equals(materialSkuList.get(i).getSku1Status())) {
                        //现在是要启用用，则加入启用队列
                        barcodeSidsEnable.add(basMaterialBarcode.getBarcodeSid());
                    }
                    if (materialSkuList.get(i).getSku1Status() == null && ConstantsEms.ENABLE_STATUS.equals(basMaterial.getStatus())) {
                        //现在是要启用用，则加入启用队列
                        barcodeSidsEnable.add(basMaterialBarcode.getBarcodeSid());
                    }
                }
            }
        }
        //更新商品条码为停用
        if (CollectionUtils.isNotEmpty(barcodeSidsDisenable)) {
            basMaterialBarcodeMapper.update(null, new UpdateWrapper<BasMaterialBarcode>().lambda()
                    .set(BasMaterialBarcode::getStatus, ConstantsEms.DISENABLE_STATUS)
                    .in(BasMaterialBarcode::getBarcodeSid, barcodeSidsDisenable));
        }
        //更新商品条码为启用
        if (CollectionUtils.isNotEmpty(barcodeSidsEnable)) {
            basMaterialBarcodeMapper.update(null, new UpdateWrapper<BasMaterialBarcode>().lambda()
                    .set(BasMaterialBarcode::getStatus, ConstantsEms.ENABLE_STATUS)
                    .in(BasMaterialBarcode::getBarcodeSid, barcodeSidsEnable));
        }
        if (CollUtil.isNotEmpty(addList)) {
            createBarcode(addList);
        }
    }

    /**
     * 修改物料&商品&服务档案
     *
     * @param basMaterial 物料&商品&服务档案
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasMaterial(BasMaterial basMaterial) {
        RLock lock = redissonClient.getLock(LOCK_KEY);
        lock.lock(10L, TimeUnit.SECONDS);
        // 变更时记录部分字段变更说明
        boolean warn = false;
        try {
            BasMaterial material = basMaterialMapper.selectBasMaterialById(basMaterial.getMaterialSid());
            Map<String, Object> queryParams = new HashMap<>();
            List<BasMaterial> queryResult = new ArrayList<>();
            if (StrUtil.isNotBlank(basMaterial.getMaterialCode())) {
                if (StrUtil.isBlank(material.getMaterialCode()) || !material.getMaterialCode().equals(basMaterial.getMaterialCode())) {
                    queryParams.put("material_code", basMaterial.getMaterialCode());
                    queryResult = basMaterialMapper.selectByMap(queryParams);
                    if (queryResult.size() > 0) {
                        for (BasMaterial o : queryResult) {
                            if (!o.getMaterialSid().equals(basMaterial.getMaterialSid())) {
                                if (ConstantsEms.MATERIAL_CATEGORY_WL.equals(basMaterial.getMaterialCategory())) {
                                    throw new CustomException("物料编码重复,请查看");
                                }
                                else if (ConstantsEms.MATERIAL_CATEGORY_SP.equals(basMaterial.getMaterialCategory())
                                        || ConstantsEms.MATERIAL_CATEGORY_YP.equals(basMaterial.getMaterialCategory())) {
                                    throw new CustomException("商品编码(款号)重复,请查看");
                                }
                                throw new CustomException("编码重复,请查看");
                            }
                        }
                    }
                    queryParams.clear();
                }
            }
            if (!material.getMaterialName().equals(basMaterial.getMaterialName())) {
                if (
                        ConstantsEms.MATERIAL_CATEGORY_FW.equals(basMaterial.getMaterialCategory()) ||
                                ConstantsEms.MATERIAL_CATEGORY_WCY.equals(basMaterial.getMaterialCategory())) {
                    queryParams.put("material_name", basMaterial.getMaterialName());
                    queryResult = basMaterialMapper.selectByMap(queryParams);
                    if (queryResult.size() > 0) {
                        for (BasMaterial o : queryResult) {
                            if (!o.getMaterialSid().equals(basMaterial.getMaterialSid())) {
                                throw new CustomException("名称已存在,请查看");
                            }
                        }
                    }
                } else if (ConstantsEms.MATERIAL_CATEGORY_WL.equals(basMaterial.getMaterialCategory()) ||
                        ConstantsEms.MATERIAL_CATEGORY_SP.equals(basMaterial.getMaterialCategory()) ||
                        ConstantsEms.MATERIAL_CATEGORY_YP.equals(basMaterial.getMaterialCategory())) {
                    queryParams.put("material_name", basMaterial.getMaterialName());
                    queryResult = basMaterialMapper.selectList(new QueryWrapper<BasMaterial>().lambda()
                            .eq(BasMaterial::getMaterialName, basMaterial.getMaterialName())
                            .in(BasMaterial::getMaterialCategory, new String[]{ConstantsEms.MATERIAL_CATEGORY_FW, ConstantsEms.MATERIAL_CATEGORY_WCY}));
                    if (queryResult.size() > 0) {
                        for (BasMaterial o : queryResult) {
                            if (!o.getMaterialSid().equals(basMaterial.getMaterialSid())) {
                                if (ConstantsEms.MATERIAL_CATEGORY_WL.equals(basMaterial.getMaterialCategory())) {
                                    throw new CustomException("物料名称已存在,请查看");
                                }
                                else if (ConstantsEms.MATERIAL_CATEGORY_SP.equals(basMaterial.getMaterialCategory())) {
                                    throw new CustomException("商品名称已存在,请查看");
                                }
                                else if (ConstantsEms.MATERIAL_CATEGORY_YP.equals(basMaterial.getMaterialCategory())) {
                                    throw new CustomException("样衣名称已存在,请查看");
                                }
                                throw new CustomException("名称已存在,请查看");
                            }
                        }
                    }
                } else {
                }
            }
            if (ConstantsEms.MATERIAL_CATEGORY_YP.equals(basMaterial.getMaterialCategory()) &&
                    StrUtil.isNotBlank(basMaterial.getSampleCodeSelf()) && !basMaterial.getSampleCodeSelf().equals(material.getSampleCodeSelf())) {
                queryResult = basMaterialMapper.selectList(new QueryWrapper<BasMaterial>().lambda().eq(BasMaterial::getSampleCodeSelf, basMaterial.getSampleCodeSelf()));
                if (queryResult.size() > 0) {
                    throw new CustomException("我司样衣号已存在,请查看");
                }
            }
            judge(basMaterial);
            this.setConfirmInfo(basMaterial);
            String name = "";
            name = checkSkuStatus(basMaterial.getBasMaterialSkuList());
            if (StrUtil.isNotBlank(name)) {
                throw new CustomException("SKU档案" + name + "已停用，不能启用！");
            }
            if (ConstantsEms.CHECK_STATUS.equals(basMaterial.getHandleStatus()) && CollectionUtils.isNotEmpty(basMaterial.getBasMaterialSkuList())) {
                checkMaterialSku(basMaterial);
            }
            //工艺单动态通知
            this.sent(basMaterial);
            //物料&商品-附件对象
            addBasMaterialAttachment(basMaterial);
            // 商品销售站点对象
            List<BasMaterialSaleStation> basMaterialSaleStationList = basMaterial.getSaleStationList();
            basMaterialSaleStationMapper.delete(new QueryWrapper<BasMaterialSaleStation>()
                    .lambda().eq(BasMaterialSaleStation::getMaterialSid, basMaterial.getMaterialSid()));
            if (CollectionUtil.isNotEmpty(basMaterialSaleStationList)) {
                basMaterialSaleStationList.forEach(item->{
                    item.setMaterialSid(basMaterial.getMaterialSid());
                });
                basMaterialSaleStationMapper.inserts(basMaterialSaleStationList);
            }
            setPictuerPath(basMaterial);
            if (material.getProducePlantSid() == null || !material.getProducePlantSid().equals(basMaterial.getProducePlantSid())) {
                setProducePlantCode(basMaterial);
            }
            if (material.getMaterialClassSid() == null || !material.getMaterialClassSid().equals(basMaterial.getMaterialClassSid())) {
                setMaterialClassSids(basMaterial);
            }
            basMaterialMapper.updateAllById(basMaterial);
            //物料&商品-SKU明细对象
            List<BasMaterialSku> basMaterialSkuList = basMaterial.getBasMaterialSkuList();
            List<BasMaterialSku> oldItemList = basMaterialSkuMapper.selectList(new QueryWrapper<BasMaterialSku>().lambda()
                    .eq(BasMaterialSku::getMaterialSid, basMaterial.getMaterialSid()));
            Map<Long, BasMaterialSku> materialSkuMap = oldItemList.stream().collect(Collectors.toMap(BasMaterialSku::getMaterialSkuSid, Function.identity()));
            if (CollectionUtils.isNotEmpty(basMaterialSkuList)) {
                if (CollectionUtil.isNotEmpty(materialSkuMap)) {
                    basMaterialSkuList.forEach(o -> {
                        if (o.getMaterialSkuSid() != null) {
                            List<OperMsg> msgList = new ArrayList<>();
                            msgList = BeanUtils.eq(materialSkuMap.get(o.getMaterialSkuSid()), o);
                            if (msgList.size() > 0) {
                                o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                                o.setUpdateDate(new Date());
                            }
                        }
                    });
                }
                addBasMaterialSku(basMaterial, basMaterialSkuList);
            }
            //如果删除了全部的物料sku 则删除。
            else {
                basMaterialSkuMapper.delete(
                        new UpdateWrapper<BasMaterialSku>()
                                .lambda()
                                .eq(BasMaterialSku::getMaterialSid, basMaterial.getMaterialSid())
                );
            }
            //确认状态后删除待办
            if (!ConstantsEms.SAVA_STATUS.equals(basMaterial.getHandleStatus())) {
                sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                        .eq(SysTodoTask::getDocumentSid, basMaterial.getMaterialSid()));
            }
            createBarcodeMethod(basMaterial);
            //价格记录表
            if (ConstantsEms.MATERIAL_CATEGORY_WL.equals(basMaterial.getMaterialCategory()) && ConstantsEms.CHECK_STATUS.equals(basMaterial.getHandleStatus())) {
                if (basMaterial.getVendorSid() != null && basMaterial.getQuotePriceTax() != null) {
                    insertPriceInfo(basMaterial);
                }
            }
            //操作日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(material, basMaterial);
            // 变更时记录部分字段变更说明
            String remark = "";
            if (ConstantsEms.CHECK_STATUS.equals(material.getHandleStatus())) {
                // 物料的 面料辅料 或者 商品 或者 样衣
                if (ConstantsEms.MATERIAL_CATEGORY_WL.equals(material.getMaterialCategory())
                        || ConstantsEms.MATERIAL_CATEGORY_YP.equals(material.getMaterialCategory())
                        || ConstantsEms.MATERIAL_CATEGORY_SP.equals(material.getMaterialCategory())) {
                    // 物料类型 商品类型 样衣类型
                    if ((material.getMaterialType() != null && !material.getMaterialType().equals(basMaterial.getMaterialType()))
                            || (StrUtil.isBlank(material.getMaterialType()) && StrUtil.isNotBlank(basMaterial.getMaterialType()))) {
                        List<ConMaterialType> typeList = conMaterialTypeMapper.selectList(new QueryWrapper<>());
                        Map<String, String> typeMaps = typeList.stream().collect(Collectors.toMap(ConMaterialType::getCode, ConMaterialType::getName, (key1, key2) -> key2));
                        String oldType = material.getMaterialType() == null ? "" : typeMaps.get(material.getMaterialType());
                        String newType = basMaterial.getMaterialType() == null ? "" : typeMaps.get(basMaterial.getMaterialType());
                        if (ConstantsEms.MATERIAL_CATEGORY_WL.equals(material.getMaterialCategory())) {
                            remark = remark + "物料类型字段变更，更新前：" + oldType + "，更新后：" + newType + "\n";
                        }
                        else if (ConstantsEms.MATERIAL_CATEGORY_SP.equals(material.getMaterialCategory())) {
                            remark = remark + "商品类型字段变更，更新前：" + oldType + "，更新后：" + newType + "\n";
                        }
                        else if (ConstantsEms.MATERIAL_CATEGORY_YP.equals(material.getMaterialCategory())) {
                            remark = remark + "样衣类型字段变更，更新前：" + oldType + "，更新后：" + newType + "\n";
                        }
                    }
                    // 上下装
                    if ((material.getUpDownSuit() != null && !material.getUpDownSuit().equals(basMaterial.getUpDownSuit()))
                            || (material.getUpDownSuit() == null && basMaterial.getUpDownSuit() != null)) {
                        List<DictData> upDownSuitList = sysDictDataService.selectDictData("s_up_down_suit");
                        Map<String, String> upDownSuitMaps = upDownSuitList.stream().collect(Collectors.toMap(DictData::getDictValue, DictData::getDictLabel, (key1, key2) -> key2));
                        String oldData = material.getUpDownSuit() == null ? "" : upDownSuitMaps.get(material.getUpDownSuit());
                        String newData = basMaterial.getUpDownSuit() == null ? "" : upDownSuitMaps.get(basMaterial.getUpDownSuit());
                        remark = remark + "上下装字段变更，更新前：" + oldData + "，更新后：" + newData + "\n";
                    }
                    // 基本计量单位
                    if ((material.getUnitBase() != null && !material.getUnitBase().equals(basMaterial.getUnitBase()))
                            || (StrUtil.isBlank(material.getUnitBase()) && StrUtil.isNotBlank(basMaterial.getUnitBase()))) {
                        List<ConMeasureUnit> measureUnitList = conMeasureUnitMapper.selectList(new QueryWrapper<>());
                        Map<String, String> measureUnitMaps = measureUnitList.stream().collect(Collectors.toMap(ConMeasureUnit::getCode, ConMeasureUnit::getName, (key1, key2) -> key2));
                        String oldData = material.getUnitBase() == null ? "" : measureUnitMaps.get(material.getUnitBase());
                        String newData = basMaterial.getUnitBase() == null ? "" : measureUnitMaps.get(basMaterial.getUnitBase());
                        remark = remark + "基本计量单位字段变更，更新前：" + oldData + "，更新后：" + newData + "\n";
                    }
                    // 是否SKU物料
                    if ((material.getIsSkuMaterial() != null && !material.getIsSkuMaterial().equals(basMaterial.getIsSkuMaterial()))
                            || (StrUtil.isBlank(material.getIsSkuMaterial()) && StrUtil.isNotBlank(basMaterial.getIsSkuMaterial()))) {
                        List<DictData> isSkuList = sysDictDataService.selectDictData("s_yesno_flag");
                        Map<String, String> isSkuMaps = isSkuList.stream().collect(Collectors.toMap(DictData::getDictValue, DictData::getDictLabel, (key1, key2) -> key2));
                        String oldData = material.getIsSkuMaterial() == null ? "" : isSkuMaps.get(material.getIsSkuMaterial());
                        String newData = basMaterial.getIsSkuMaterial() == null ? "" : isSkuMaps.get(basMaterial.getIsSkuMaterial());
                        if (ConstantsEms.MATERIAL_CATEGORY_WL.equals(material.getMaterialCategory())) {
                            remark = remark + "是否SKU物料字段变更，更新前：" + oldData + "，更新后：" + newData + "\n";
                        }
                        else if (ConstantsEms.MATERIAL_CATEGORY_SP.equals(material.getMaterialCategory())) {
                            remark = remark + "是否SKU商品字段变更，更新前：" + oldData + "，更新后：" + newData + "\n";
                        }
                        else if (ConstantsEms.MATERIAL_CATEGORY_YP.equals(material.getMaterialCategory())) {
                            remark = remark + "是否SKU样衣字段变更，更新前：" + oldData + "，更新后：" + newData + "\n";
                        }
                        warn = true;
                    }
                    // SKU维度数
                    if ((material.getSkuDimension() != null && !material.getSkuDimension().equals(basMaterial.getSkuDimension()))
                            || (material.getSkuDimension() == null && basMaterial.getSkuDimension() != null)) {
                        List<DictData> skuDimensionList = sysDictDataService.selectDictData("s_sku_dimension");
                        Map<String, String> skuDimensionMaps = skuDimensionList.stream().collect(Collectors.toMap(DictData::getDictValue, DictData::getDictLabel, (key1, key2) -> key2));
                        String oldData = material.getSkuDimension() == null ? "" : skuDimensionMaps.get(material.getSkuDimension().toString());
                        String newData = basMaterial.getSkuDimension() == null ? "" : skuDimensionMaps.get(basMaterial.getSkuDimension().toString());
                        remark = remark + "SKU维度数字段变更，更新前：" + oldData + "，更新后：" + newData + "\n";
                        warn = true;
                    }
                    // SKU1属性类型
                    if ((material.getSku1Type() != null && !material.getSku1Type().equals(basMaterial.getSku1Type()))
                            || (material.getSku1Type() == null && basMaterial.getSku1Type() != null)) {
                        List<DictData> sku1TypeList = sysDictDataService.selectDictData("s_sku_type");
                        Map<String, String> sku1TypeMaps = sku1TypeList.stream().collect(Collectors.toMap(DictData::getDictValue, DictData::getDictLabel, (key1, key2) -> key2));
                        String oldData = material.getSku1Type() == null ? "" : sku1TypeMaps.get(material.getSku1Type());
                        String newData = basMaterial.getSku1Type() == null ? "" : sku1TypeMaps.get(basMaterial.getSku1Type());
                        remark = remark + "SKU1属性类型字段变更，更新前：" + oldData + "，更新后：" + newData + "\n";
                        warn = true;
                    }
                    // SKU2属性类型
                    if ((material.getSku2Type() != null && !material.getSku2Type().equals(basMaterial.getSku2Type()))
                            || (material.getSku2Type() == null && basMaterial.getSku2Type() != null)) {
                        List<DictData> sku2TypeList = sysDictDataService.selectDictData("s_sku_type");
                        Map<String, String> sku2TypeMaps = sku2TypeList.stream().collect(Collectors.toMap(DictData::getDictValue, DictData::getDictLabel, (key1, key2) -> key2));
                        String oldData = material.getSku2Type() == null ? "" : sku2TypeMaps.get(material.getSku2Type());
                        String newData = basMaterial.getSku2Type() == null ? "" : sku2TypeMaps.get(basMaterial.getSku2Type());
                        remark = remark + "SKU2属性类型字段变更，更新前：" + oldData + "，更新后：" + newData + "\n";
                        warn = true;
                    }
                    // 我司样衣号
                    if ((material.getSampleCodeSelf() != null && !material.getSampleCodeSelf().equals(basMaterial.getSampleCodeSelf()))
                            || (StrUtil.isBlank(material.getSampleCodeSelf()) && StrUtil.isNotBlank(basMaterial.getSampleCodeSelf()))) {
                        String oldData = material.getSampleCodeSelf() == null ? "" : material.getSampleCodeSelf();
                        String newData = basMaterial.getSampleCodeSelf() == null ? "" : basMaterial.getSampleCodeSelf();
                        remark = remark + "我司样衣号字段变更，更新前：" + oldData + "，更新后：" + newData + "\n";
                    }
                }
            }
            MongodbDeal.update(basMaterial.getMaterialSid(), material.getHandleStatus(), basMaterial.getHandleStatus(), msgList, TITLE, remark);
        } catch (BaseException e) {
            throw new BaseException(e.getMessage());
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        if (warn) {
            return 100;
        }
        return 1;
    }

    /**
     * 物料&商品-附件对象
     */
    @Transactional(rollbackFor = Exception.class)
    private void addBasMaterialAttachment(BasMaterial basMaterial) {
        basMaterialAttachmentMapper.delete(
                new UpdateWrapper<BasMaterialAttachment>()
                        .lambda()
                        .eq(BasMaterialAttachment::getMaterialSid, basMaterial.getMaterialSid())
        );
        if (CollectionUtils.isNotEmpty(basMaterial.getAttachmentList())) {
            int i = 0;
            for (BasMaterialAttachment item : basMaterial.getAttachmentList()) {
                if (ConstantsEms.FILE_TYPE_SPEC.equals(item.getFileType())) {
                    i += 1;
                    if (i > 1) {
                        throw new BaseException("一个款只能上传一个工艺单附件！");
                    }
                    if (item.getMaterialAttachmentSid() == null || basMaterial.getZhizaodanUploadDate() == null) {
                        basMaterial.setZhizaodanUploadDate(new Date());
                    }
                    basMaterial.setIsHasUploadedZhizaodan(ConstantsEms.YES);
                }
                item.setMaterialSid(basMaterial.getMaterialSid());
            }
            if (i == 0) {
                basMaterial.setIsHasUploadedZhizaodan(ConstantsEms.NO).setZhizaodanUploadDate(null);
            }
            basMaterialAttachmentMapper.inserts(basMaterial.getAttachmentList());
        } else {
            basMaterial.setIsHasUploadedZhizaodan(ConstantsEms.NO).setZhizaodanUploadDate(null);
        }
    }

    /**
     * 物料&商品-SKU明细对象
     */
    private void addBasMaterialSku(BasMaterial basMaterial, List<BasMaterialSku> basMaterialSkuList) {
        basMaterialSkuMapper.delete(
                new UpdateWrapper<BasMaterialSku>()
                        .lambda()
                        .eq(BasMaterialSku::getMaterialSid, basMaterial.getMaterialSid())
        );
        int max = 0, i = 1;
        //存放当前数据库中的所有存在的行号
        List<Integer> itemList = new ArrayList<>();
        basMaterialSkuList.forEach(item -> {
            if (item.getItemNum() != 0) {
                itemList.add(item.getItemNum());
            }
            if (item.getItemNum() == 0 && item.getMaterialSkuSid() != null) {
                int tempMax = 0;
                if (CollectionUtils.isNotEmpty(itemList)) {
                    tempMax = Collections.max(itemList);
                }
                item.setItemNum(tempMax + 1);
                itemList.add(item.getItemNum());
            }
        });
        if (CollectionUtils.isNotEmpty(itemList)) {
            //获取最大行号
            max = Collections.max(itemList);
            i = max + 1;
        }
        for (BasMaterialSku item : basMaterialSkuList) {
            item.setMaterialSid(basMaterial.getMaterialSid());
            //为新增的明细从最大行号后开始赋值
            if (item.getMaterialSkuSid() == null) {
                item.setItemNum(i++);
            }
        }
        basMaterialSkuMapper.inserts(basMaterialSkuList);
    }

    /**
     * 设置确认信息
     */
    private void setConfirmInfo(BasMaterial o) {
        if (o == null) {
            return;
        }
        if (HandleStatus.CONFIRMED.getCode().equals(o.getHandleStatus())) {
            o.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            o.setConfirmDate(new Date());
        }
    }

    /**
     * 创建bom时 校验
     */
    @Override
    public BasMaterial judgeBomCreate(Long materialSid) {
        BasMaterial basMaterial = basMaterialMapper.selectBasMaterialById(materialSid);
        if (!ConstantsEms.SAVA_STATUS.equals(basMaterial.getStatus())) {
            throw new BaseException("该商品已停用，请检查！");
        }
        if (!ConstantsEms.CHECK_STATUS.equals(basMaterial.getHandleStatus())) {
            throw new BaseException("该商品非确认状态，请检查！");
        }
        //BOM
        String sku1Type = basMaterial.getSku1Type();
        if (sku1Type == null) {
            throw new BaseException("创建bom的商品必须要有颜色类型");
        }
        List<TecBomHead> tecBomHeads = tecBomHeadMapper.selectList(new QueryWrapper<TecBomHead>().lambda()
                .eq(TecBomHead::getMaterialSid, materialSid)
        );
        if (CollectionUtil.isNotEmpty(tecBomHeads)) {
            basMaterial.setExit(true);
        }
        //物料&商品-SKU明细对象
        BasMaterialSku basMaterialSku = new BasMaterialSku();
        basMaterialSku.setMaterialSid(basMaterial.getMaterialSid());
        List<BasMaterialSku> basMaterialSkuList = basMaterialSkuMapper.selectBasMaterialSkuList(basMaterialSku);
        basMaterialSkuList = basMaterialSkuList.stream().filter(li -> li.getStatus().equals(ConstantsEms.ENABLE_STATUS)).collect(Collectors.toList());
        basMaterialSkuList = basMaterialSkuList.stream().sorted(Comparator.comparing(BasMaterialSku::getItemNum)).collect(Collectors.toList());
        basMaterial.setBasMaterialSkuList(basMaterialSkuList);
        return basMaterial;
    }

    /**
     * 批量删除物料&商品&服务档案
     * *
     *
     * @param materialSids 需要删除的物料&商品&服务档案ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasMaterialByIds(Long[] materialSids) {
        if (ArrayUtil.isEmpty(materialSids)) {
            throw new BaseException("请选择行");
        }
        int count = basMaterialMapper.selectCount(new QueryWrapper<BasMaterial>().lambda().in(BasMaterial::getMaterialSid, materialSids).eq(BasMaterial::getHandleStatus, ConstantsEms.SAVA_STATUS));
        if (count != materialSids.length) {
            throw new BaseException("仅保存状态才允许删除");
        }
        //删除物料&商品&服务档案
        basMaterialMapper.deleteBasMaterialByIds(materialSids);
        //删除物料&商品-附件
        basMaterialAttachmentMapper.deleteBasMaterialAttachmentByIds(materialSids);
        // 商品销售站点对象
        basMaterialSaleStationMapper.delete(new QueryWrapper<BasMaterialSaleStation>()
                .lambda().in(BasMaterialSaleStation::getMaterialSid, materialSids));
        //删除物料&商品-SKU明细
        basMaterialSkuMapper.deleteBasMaterialSkuByIds(materialSids);
        //删除商品条码
        basMaterialBarcodeMapper.delete(new QueryWrapper<BasMaterialBarcode>().lambda().in(BasMaterialBarcode::getMaterialSid, materialSids));
        //删除待办
        sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                .in(SysTodoTask::getDocumentSid, materialSids));
        for (int i = 0; i < materialSids.length; i++) {
            Long id = materialSids[i];
            //插入日志
            MongodbUtil.insertUserLog(Long.valueOf(id), com.platform.common.log.enums.BusinessType.DELETE.getValue(), null, TITLE);
        }
        return materialSids.length;
    }

    /**
     * 输入物料/商品/服务编码，查询物料&商品&服务档案
     *
     * @param materialCode 物料&商品&服务档案编码
     * @return 物料&商品&服务档案
     */
    @Override
    public BasMaterial selectBasMaterialByCode(String materialCode, String businessType) {
        BasMaterial basMaterial = basMaterialMapper.selectBasMaterialByCode(materialCode);
        if (basMaterial == null) {
            throw new BaseException("输入的商品编码不存在，请检查！");
        }
        if (!ConstantsEms.SAVA_STATUS.equals(basMaterial.getStatus())) {
            throw new BaseException("输入的商品编码（款号）已停用，请检查！");
        }
        if (!ConstantsEms.CHECK_STATUS.equals(basMaterial.getHandleStatus())) {
            throw new BaseException("输入的商品编码非确认状态，请检查！");
        }
        //合格证
        if (BusinessType.MATERIALCERTIFICATE.getCode().equals(businessType)) {
            if (!basMaterial.getHandleStatus().equals(ConstantsEms.CHECK_STATUS)) {
                throw new BaseException("输入的商品编码不存在，请检查！");
            }
            if (!basMaterial.getStatus().equals(ConstantsEms.ENABLE_STATUS)) {
                throw new BaseException("输入的商品编码已停用，请检查！");
            }
            BasMaterialCertificate basMaterialCertificate = basMaterialCertificateMapper.selectBasMaterialCertificateByMaterialSid(basMaterial.getMaterialSid());
            if (basMaterialCertificate != null) {
                throw new BaseException("该商品已建立合格证洗唛信息");
            }
        }
        //BOM
        else if (BusinessType.BOM.getCode().equals(businessType)) {
            String materialCategory = basMaterial.getMaterialCategory();
            if (!ConstantsEms.MATERIAL_CATEGORY_SP.equals(materialCategory)) {
                throw new BaseException("录入的编码非商品档案，请检查！");
            }
            String sku1Type = basMaterial.getSku1Type();
            if (sku1Type == null) {
                throw new BaseException("创建bom的商品必须要有颜色类型");
            }
            List<TecBomHead> tecBomHead = tecBomHeadMapper.selectTecBomHeadByMaterialSid(basMaterial.getMaterialSid());
            if (CollectionUtils.isNotEmpty(tecBomHead)) {
                //是否存在对应的bom
                TecBomHead head = tecBomHead.get(0);
                basMaterial.setHandleStatus(head.getHandleStatus());
                basMaterial.setExit(true);
            }
        }
        if (BusinessType.PRODUCT_PROCESS_STEP.getCode().equals(businessType)) {
            List<PayProductProcessStep> processStepList = payProductProcessStepMapper
                    .selectList(new QueryWrapper<PayProductProcessStep>().lambda()
                            .eq(PayProductProcessStep::getProductCode, basMaterial.getMaterialCode()));

            if (CollectionUtil.isNotEmpty(processStepList)) {
                basMaterial.setExit(true);
            }
        }
        //物料&商品-SKU明细对象
        BasMaterialSku basMaterialSku = new BasMaterialSku();
        basMaterialSku.setMaterialSid(basMaterial.getMaterialSid());
        List<BasMaterialSku> basMaterialSkuList = basMaterialSkuMapper.selectBasMaterialSkuList(basMaterialSku);
        basMaterialSkuList = basMaterialSkuList.stream().filter(li -> li.getStatus().equals(ConstantsEms.ENABLE_STATUS)).collect(Collectors.toList());
        basMaterialSkuList = basMaterialSkuList.stream().sorted(Comparator.comparing(BasMaterialSku::getItemNum)).collect(Collectors.toList());
        basMaterial.setBasMaterialSkuList(basMaterialSkuList);
        return basMaterial;
    }

    /**
     * 查询商品档案下的sku列表
     *
     * @param materialSid 商品档案sid
     * @param skuType     sku类型（null时查全部）
     */
    @Override
    public List<BasMaterialSku> getBasMaterialSku(BasMaterialSkuRequest request) {
        BasMaterialSku basMaterialSku = new BasMaterialSku();
        BeanUtil.copyProperties(request, basMaterialSku);
        List<BasMaterialSku> basMaterialSkuList = basMaterialSkuMapper.selectBasMaterialSkuList(basMaterialSku);
        return basMaterialSkuList;
    }

    /**
     * 变更前调用的校验  已启用尺码是否存在于尺码组  忽略并继续
     * @param basMaterial
     */
    @Override
    public EmsResultEntity changeVerify(BasMaterial basMaterial) {
        EmsResultEntity result = new EmsResultEntity(null, "操作成功");
        String skuName = "";
        if (basMaterial.getSku2GroupSid() != null) {
            List<BasMaterialSku> itemList = basMaterial.getBasMaterialSkuList();
            //得到明细中启用状态的尺码明细的skuSid
            List<Long> skuSidList = itemList.stream().filter(o -> ConstantsEms.ENABLE_STATUS.equals(o.getStatus()) && ConstantsEms.SKUTYP_CM.equals(o.getSkuType())).map(o -> o.getSkuSid()).collect(Collectors.toList());
            for (Long skuSid : skuSidList) {
                //判断这个sku有没有在物料商品的尺码组下
                BasSkuGroupItem groupItem = basSkuGroupItemMapper.selectOne(new QueryWrapper<BasSkuGroupItem>()
                        .lambda().eq(BasSkuGroupItem::getSkuGroupSid, basMaterial.getSku2GroupSid()).eq(BasSkuGroupItem::getSkuSid, skuSid));
                if (groupItem == null) {
                    BasSku sku = basSkuMapper.selectById(skuSid);
                    skuName = skuName + sku.getSkuName() + ";";
                }
            }
            if (StrUtil.isNotBlank(skuName)) {
                if (skuName.endsWith(";")) {
                    skuName = skuName.substring(0, skuName.length() - 1);
                }
                BasSkuGroup skuGroup = basSkuGroupMapper.selectById(basMaterial.getSku2GroupSid());
                skuName = "存在尺码" + skuName + "不属于尺码组" + skuGroup.getSkuGroupName() + "，是否进行确认操作？";
                return EmsResultEntity.warning(null, skuName);
            }
        }
        return result;
    }

    /**
     * 新建编辑时校验已启用尺码是否存在于尺码组 强控
     * @param basMaterial
     */
    public void checkMaterialSku(BasMaterial basMaterial) {
        String skuName = "";
        if (basMaterial.getSku2GroupSid() != null
                && !(ConstantsEms.MATERIAL_CATEGORY_SP.equals(basMaterial.getMaterialCategory())
                || ConstantsEms.MATERIAL_CATEGORY_YP.equals(basMaterial.getMaterialCategory()))) {
            List<BasMaterialSku> itemList = basMaterial.getBasMaterialSkuList();
            //得到明细中启用状态的尺码明细的skuSid
            List<Long> skuSidList = itemList.stream().filter(o -> ConstantsEms.ENABLE_STATUS.equals(o.getStatus()) && ConstantsEms.SKUTYP_CM.equals(o.getSkuType())).map(o -> o.getSkuSid()).collect(Collectors.toList());
            for (Long skuSid : skuSidList) {
                //判断这个sku有没有在物料商品的尺码组下
                BasSkuGroupItem groupItem = basSkuGroupItemMapper.selectOne(new QueryWrapper<BasSkuGroupItem>()
                        .lambda().eq(BasSkuGroupItem::getSkuGroupSid, basMaterial.getSku2GroupSid()).eq(BasSkuGroupItem::getSkuSid, skuSid));
                if (groupItem == null) {
                    BasSku sku = basSkuMapper.selectById(skuSid);
                    skuName = skuName + sku.getSkuName() + ";";
                }
            }
            if (StrUtil.isNotBlank(skuName)) {
                if (skuName.endsWith(";")) {
                    skuName = skuName.substring(0, skuName.length() - 1);
                }
                BasSkuGroup skuGroup = basSkuGroupMapper.selectById(basMaterial.getSku2GroupSid());
                skuName = "尺码组" + skuGroup.getSkuGroupName() + "中不存在尺码" + skuName + "，无法确认！";
            }
        }
        if (StrUtil.isNotBlank(skuName)) {
            throw new CustomException(skuName);
        }
    }

    /**
     * 样品档案查询页面确认前校验
     */
    @Override
    public EmsResultEntity confirmCheck(BasMaterial basMaterial) {
        Long[] materialSidList = basMaterial.getMaterialSidList();
        if (ArrayUtil.isEmpty(materialSidList)) {
            throw new BaseException("请选择行");
        }
        List<BasMaterial> materialList = basMaterialMapper.selectList(new QueryWrapper<BasMaterial>().lambda().in(BasMaterial::getMaterialSid, materialSidList));
        if (CollectionUtil.isNotEmpty(materialList)) {
            //sku类型
            List<DictData> skuTypeList=sysDictDataService.selectDictData("s_sku_type");
            skuTypeList = skuTypeList.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String,String> skuTypeMaps=skuTypeList.stream().collect(Collectors.toMap(DictData::getDictValue, DictData::getDictLabel,(key1, key2)->key2));
            // 租户字段配置
            SysDefaultSettingClient client = ApiThreadLocalUtil.get().getSysUser().getClient();
            // 报错信息
            List<CommonErrMsgResponse> msgList = new ArrayList<>();
            // 提示信息
            List<CommonErrMsgResponse> warnList = new ArrayList<>();
            materialList.forEach(item->{
                if (ConstantsEms.MATERIAL_CATEGORY_WL.equals(item.getMaterialCategory())) {
                    if (StrUtil.isBlank(item.getMaterialType())) {
                        CommonErrMsgResponse errMsg = new CommonErrMsgResponse();
                        errMsg.setMsg(item.getMaterialCode() + "的物料类型为空，无法确认!");
                        msgList.add(errMsg);
                    }
                    if (item.getMaterialClassSid() == null) {
                        CommonErrMsgResponse errMsg = new CommonErrMsgResponse();
                        errMsg.setMsg(item.getMaterialCode() + "的物料分类为空，无法确认!");
                        msgList.add(errMsg);
                    }
                    if (StrUtil.isBlank(item.getPurchaseType())) {
                        CommonErrMsgResponse errMsg = new CommonErrMsgResponse();
                        errMsg.setMsg(item.getMaterialCode() + "的采购类型为空，无法确认!");
                        msgList.add(errMsg);
                    }
                    if (ConstantsEms.MATERIAL_M.equals(item.getMaterialType()) && StrUtil.isBlank(item.getYarnCount())) {
                        CommonErrMsgResponse errMsg = new CommonErrMsgResponse();
                        errMsg.setMsg(item.getMaterialCode() + "的纱支为空，无法确认!");
                        msgList.add(errMsg);
                    }
                    if (ConstantsEms.MATERIAL_M.equals(item.getMaterialType()) && StrUtil.isBlank(item.getDensity())) {
                        CommonErrMsgResponse errMsg = new CommonErrMsgResponse();
                        errMsg.setMsg(item.getMaterialCode() + "的密度为空，无法确认!");
                        msgList.add(errMsg);
                    }
                    if (item.getVendorSid() == null && !ConstantsEms.NO.equals(client.getIsRequiredVendorMaterial())) {
                        CommonErrMsgResponse errMsg = new CommonErrMsgResponse();
                        errMsg.setMsg(item.getMaterialCode() + "的供应商为空，无法确认!");
                        msgList.add(errMsg);
                    }
                    if (StrUtil.isBlank(item.getSupplierProductCode()) && !ConstantsEms.NO.equals(client.getIsRequiredVendorMaterial())) {
                        CommonErrMsgResponse errMsg = new CommonErrMsgResponse();
                        errMsg.setMsg(item.getMaterialCode() + "的供方编码为空，无法确认!");
                        msgList.add(errMsg);
                    }
                    if (StrUtil.isBlank(item.getUnitQuantity()) && !ConstantsEms.NO.equals(client.getIsRequiredUnitQuantityMaterial())) {
                        CommonErrMsgResponse errMsg = new CommonErrMsgResponse();
                        errMsg.setMsg(item.getMaterialCode() + "的用量计量单位(BOM)为空，无法确认!");
                        msgList.add(errMsg);
                    }
                    if (item.getUnitConversionRate() == null && !ConstantsEms.NO.equals(client.getIsRequiredUnitQuantityMaterial())) {
                        CommonErrMsgResponse errMsg = new CommonErrMsgResponse();
                        errMsg.setMsg(item.getMaterialCode() + "的单位换算比例(基本计量单位/BOM用量单位)为空，无法确认!");
                        msgList.add(errMsg);
                    }
                    if (item.getQuotePriceTax() == null && !ConstantsEms.NO.equals(client.getIsRequiredQuotePriceTaxMaterial())) {
                        CommonErrMsgResponse errMsg = new CommonErrMsgResponse();
                        errMsg.setMsg(item.getMaterialCode() + "的报价为空，无法确认!");
                        msgList.add(errMsg);
                    }
                    if (StrUtil.isBlank(item.getUnitPrice()) && !ConstantsEms.NO.equals(client.getIsRequiredQuotePriceTaxMaterial())) {
                        CommonErrMsgResponse errMsg = new CommonErrMsgResponse();
                        errMsg.setMsg(item.getMaterialCode() + "的报价单位为空，无法确认!");
                        msgList.add(errMsg);
                    }
                    if (item.getUnitConversionRatePrice() == null && !ConstantsEms.NO.equals(client.getIsRequiredQuotePriceTaxMaterial())) {
                        CommonErrMsgResponse errMsg = new CommonErrMsgResponse();
                        errMsg.setMsg(item.getMaterialCode() + "的单位换算比例(报价单位/基本单位)为空，无法确认!");
                        msgList.add(errMsg);
                    }
                }
                if (ConstantsEms.MATERIAL_CATEGORY_YP.equals(item.getMaterialCategory())) {
                    if (StrUtil.isBlank(item.getMaterialType())) {
                        CommonErrMsgResponse errMsg = new CommonErrMsgResponse();
                        errMsg.setMsg("我司样衣号" + item.getSampleCodeSelf() + "的样衣类型不能为空!");
                        msgList.add(errMsg);
                    }
                    if (!ConstantsEms.NO.equals(client.getIsRequiredProductSeasonProduct())
                            && item.getProductSeasonSid() == null) {
                        CommonErrMsgResponse errMsg = new CommonErrMsgResponse();
                        errMsg.setMsg("我司样衣号" + item.getSampleCodeSelf() + "的产品季不能为空!");
                        msgList.add(errMsg);
                    }
                    if (!ConstantsEms.NO.equals(client.getIsRequiredProductTechniqueTypeProduct())
                            && StrUtil.isBlank(item.getProductTechniqueType())) {
                        CommonErrMsgResponse errMsg = new CommonErrMsgResponse();
                        errMsg.setMsg("我司样衣号" + item.getSampleCodeSelf() + "的生产工艺类型不能为空!");
                        msgList.add(errMsg);
                    }
                    if (!ConstantsEms.NO.equals(client.getIsRequiredUpDownSuitProduct())
                            && StrUtil.isBlank(item.getUpDownSuit())) {
                        CommonErrMsgResponse errMsg = new CommonErrMsgResponse();
                        errMsg.setMsg("我司样衣号" + item.getSampleCodeSelf() + "的上下装不能为空!");
                        msgList.add(errMsg);
                    }
                    if (!ConstantsEms.NO.equals(client.getIsRequiredModelProduct())
                            && item.getModelSid() == null) {
                        CommonErrMsgResponse errMsg = new CommonErrMsgResponse();
                        errMsg.setMsg("我司样衣号" + item.getSampleCodeSelf() + "的版型不能为空!");
                        msgList.add(errMsg);
                    }
                    if (!ConstantsEms.NO.equals(client.getIsRequiredSku2GroupProduct())
                            && item.getSku2GroupSid() == null && ConstantsEms.SKUTYP_CM.equals(item.getSku2Type())) {
                        CommonErrMsgResponse errMsg = new CommonErrMsgResponse();
                        errMsg.setMsg("我司样衣号" + item.getSampleCodeSelf() + "的尺码组不能为空!");
                        msgList.add(errMsg);
                    }

                    List<BasMaterialSku> materialSkuList = basMaterialSkuMapper.selectList(new QueryWrapper<BasMaterialSku>()
                            .lambda().eq(BasMaterialSku::getMaterialSid, item.getMaterialSid()));
                    item.setBasMaterialSkuList(materialSkuList);
                    if (CollectionUtil.isNotEmpty(item.getBasMaterialSkuList()) && item.getSku2GroupSid() != null) {
                        List<BasMaterialSku> cmList = item.getBasMaterialSkuList().stream().filter(o -> ConstantsEms.SKUTYP_CM.equals(o.getSkuType())).collect(Collectors.toList());
                        if (CollectionUtil.isNotEmpty(cmList)) {
                            List<Long> skuSidList = cmList.stream().map(BasMaterialSku::getSkuSid).collect(Collectors.toList());
                            List<BasSkuGroupItem> skuGroupItemList = basSkuGroupItemMapper.selectList(new QueryWrapper<BasSkuGroupItem>().lambda()
                                    .eq(BasSkuGroupItem::getSkuGroupSid, item.getSku2GroupSid()).in(BasSkuGroupItem::getSkuSid, skuSidList));
                            if (CollectionUtil.isEmpty(skuGroupItemList) || skuGroupItemList.size() != skuSidList.size()) {
                                CommonErrMsgResponse warnMsg = new CommonErrMsgResponse();
                                warnMsg.setMsg("我司样衣号" + item.getSampleCodeSelf() + "存在尺码明细不属于尺码组，是否继续操作？");
                                warnList.add(warnMsg);
                            }
                        }
                    }

                }
                if (ConstantsEms.MATERIAL_CATEGORY_SP.equals(item.getMaterialCategory()) && "XIEF".equals(basMaterial.getExportType())) {
                    if (StrUtil.isBlank(item.getMaterialType())) {
                        CommonErrMsgResponse errMsg = new CommonErrMsgResponse();
                        errMsg.setMsg(item.getMaterialCode() + "的商品类型为空，无法确认!");
                        msgList.add(errMsg);
                    }
                    if (item.getMaterialClassSid() == null) {
                        CommonErrMsgResponse errMsg = new CommonErrMsgResponse();
                        errMsg.setMsg(item.getMaterialCode() + "的商品分类为空，无法确认!");
                        msgList.add(errMsg);
                    }
                    if (!ConstantsEms.NO.equals(client.getIsRequiredProductSeasonProduct())
                            && item.getProductSeasonSid() == null) {
                        CommonErrMsgResponse errMsg = new CommonErrMsgResponse();
                        errMsg.setMsg(item.getMaterialCode() + "的产品季不能为空!");
                        msgList.add(errMsg);
                    }
                    if (!ConstantsEms.NO.equals(client.getIsRequiredProductTechniqueTypeProduct())
                            && StrUtil.isBlank(item.getProductTechniqueType())) {
                        CommonErrMsgResponse errMsg = new CommonErrMsgResponse();
                        errMsg.setMsg(item.getMaterialCode() + "的生产工艺类型不能为空!");
                        msgList.add(errMsg);
                    }
                    if (!ConstantsEms.NO.equals(client.getIsRequiredUpDownSuitProduct())
                            && StrUtil.isBlank(item.getUpDownSuit())) {
                        CommonErrMsgResponse errMsg = new CommonErrMsgResponse();
                        errMsg.setMsg(item.getMaterialCode() + "的上下装不能为空!");
                        msgList.add(errMsg);
                    }
                    if (!ConstantsEms.NO.equals(client.getIsRequiredModelProduct())
                            && item.getModelSid() == null) {
                        CommonErrMsgResponse errMsg = new CommonErrMsgResponse();
                        errMsg.setMsg(item.getMaterialCode() + "的版型不能为空!");
                        msgList.add(errMsg);
                    }
                    if (!ConstantsEms.NO.equals(client.getIsRequiredSku2GroupProduct())
                            && item.getSku2GroupSid() == null && ConstantsEms.SKUTYP_CM.equals(item.getSku2Type())) {
                        CommonErrMsgResponse errMsg = new CommonErrMsgResponse();
                        errMsg.setMsg(item.getMaterialCode() + "的尺码组不能为空!");
                        msgList.add(errMsg);
                    }
                    if (item.getSku1Type() != null) {
                        List<BasMaterialSku> sku1List = basMaterialSkuMapper.selectList(new QueryWrapper<BasMaterialSku>().lambda()
                                .eq(BasMaterialSku::getSkuType, item.getSku1Type()).eq(BasMaterialSku::getMaterialSid, item.getMaterialSid()));
                        if (CollectionUtil.isEmpty(sku1List)) {
                            CommonErrMsgResponse errMsg = new CommonErrMsgResponse();
                            errMsg.setMsg(item.getMaterialCode() + "的" + skuTypeMaps.get(item.getSku1Type()) + "明细不能为空，无法确认!");
                            msgList.add(errMsg);
                        }
                    }
                    if (item.getSku2Type() != null) {
                        List<BasMaterialSku> sku2List = basMaterialSkuMapper.selectList(new QueryWrapper<BasMaterialSku>().lambda()
                                .eq(BasMaterialSku::getSkuType, item.getSku2Type()).eq(BasMaterialSku::getMaterialSid, item.getMaterialSid()));
                        if (CollectionUtil.isEmpty(sku2List)) {
                            CommonErrMsgResponse errMsg = new CommonErrMsgResponse();
                            errMsg.setMsg(item.getMaterialCode() + "的" + skuTypeMaps.get(item.getSku2Type()) + "明细不能为空，无法确认!");
                            msgList.add(errMsg);
                        }
                    }
                }
                if (ConstantsEms.MATERIAL_CATEGORY_SP.equals(item.getMaterialCategory()) && !"XIEF".equals(basMaterial.getExportType())) {
                    if (StrUtil.isBlank(item.getMaterialType())) {
                        CommonErrMsgResponse errMsg = new CommonErrMsgResponse();
                        errMsg.setMsg(item.getMaterialCode() + "的商品类型为空，无法确认!");
                        msgList.add(errMsg);
                    }
                    if (item.getMaterialClassSid() == null) {
                        CommonErrMsgResponse errMsg = new CommonErrMsgResponse();
                        errMsg.setMsg(item.getMaterialCode() + "的商品分类为空，无法确认!");
                        msgList.add(errMsg);
                    }
                    if (item.getVendorSid() == null && !ConstantsEms.NO.equals(client.getIsRequiredVendorMaterial())) {
                        CommonErrMsgResponse errMsg = new CommonErrMsgResponse();
                        errMsg.setMsg(item.getMaterialCode() + "的供应商为空，无法确认!");
                        msgList.add(errMsg);
                    }
                    if (StrUtil.isBlank(item.getSupplierProductCode()) && !ConstantsEms.NO.equals(client.getIsRequiredVendorMaterial())) {
                        CommonErrMsgResponse errMsg = new CommonErrMsgResponse();
                        errMsg.setMsg(item.getMaterialCode() + "的供方编码为空，无法确认!");
                        msgList.add(errMsg);
                    }
                    if (item.getSku1Type() != null) {
                        List<BasMaterialSku> sku1List = basMaterialSkuMapper.selectList(new QueryWrapper<BasMaterialSku>().lambda()
                                .eq(BasMaterialSku::getSkuType, item.getSku1Type()).eq(BasMaterialSku::getMaterialSid, item.getMaterialSid()));
                        if (CollectionUtil.isEmpty(sku1List)) {
                            CommonErrMsgResponse errMsg = new CommonErrMsgResponse();
                            errMsg.setMsg(item.getMaterialCode() + "的" + skuTypeMaps.get(item.getSku1Type()) + "明细不能为空，无法确认!");
                            msgList.add(errMsg);
                        }
                    }
                    if (item.getSku2Type() != null) {
                        List<BasMaterialSku> sku2List = basMaterialSkuMapper.selectList(new QueryWrapper<BasMaterialSku>().lambda()
                                .eq(BasMaterialSku::getSkuType, item.getSku2Type()).eq(BasMaterialSku::getMaterialSid, item.getMaterialSid()));
                        if (CollectionUtil.isEmpty(sku2List)) {
                            CommonErrMsgResponse errMsg = new CommonErrMsgResponse();
                            errMsg.setMsg(item.getMaterialCode() + "的" + skuTypeMaps.get(item.getSku2Type()) + "明细不能为空，无法确认!");
                            msgList.add(errMsg);
                        }
                    }
                }
            });
            if (CollectionUtil.isNotEmpty(msgList)) {
                return EmsResultEntity.error(msgList);
            }
            else if (CollectionUtil.isNotEmpty(warnList)) {
                return EmsResultEntity.warning(warnList);
            }
        }
        return EmsResultEntity.success();
    }

    /**
     * 物料&商品&服务档案确认
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int confirm(BasMaterial basMaterial) {
        //物料&商品&服务sids
        Long[] materialSidList = basMaterial.getMaterialSidList();
        if (ArrayUtil.isEmpty(materialSidList)) {
            throw new BaseException("请选择行");
        }
        BasMaterial params = new BasMaterial();
        int count = basMaterialMapper.selectCount(new QueryWrapper<BasMaterial>().lambda().in(BasMaterial::getMaterialSid, materialSidList).eq(BasMaterial::getHandleStatus, HandleStatus.SAVE.getCode()));
        if (count != materialSidList.length) {
            throw new BaseException("仅保存状态才允许确认");
        }
        List<BasMaterial> materialList = basMaterialMapper.selectList(new QueryWrapper<BasMaterial>().lambda().in(BasMaterial::getMaterialSid, materialSidList));
        // 物料类别
        List<DictData> categoryList = sysDictDataService.selectDictData("s_material_category");
        Map<String, String> categoryMaps = categoryList.stream().collect(Collectors.toMap(DictData::getDictValue, DictData::getDictLabel, (key1, key2) -> key2));
        String skuName = "";
        for (BasMaterial item : materialList) {
            if (item.getIsSkuMaterial().equals("Y")) {
                String name = "";
                List<BasMaterialSku> itemList = basMaterialSkuMapper.selectList(new QueryWrapper<BasMaterialSku>().lambda().eq(BasMaterialSku::getMaterialSid, item.getMaterialSid()));
                if (item.getSku1Type() != null) {
                    List<BasMaterialSku> item1List = itemList.stream().filter(o -> o.getSkuType().equals(item.getSku1Type())).collect(Collectors.toList());
                    if (CollectionUtils.isEmpty(item1List)) {
                        throw new BaseException("存在SKU明细为空（如：颜色、长度）的" + categoryMaps.get(item.getMaterialCategory()) + "档案，无法执行确认操作，请核实！");
                    } else {
                        name = checkSkuStatus(item1List);
                    }
                }
                if (item.getSku2Type() != null) {
                    List<BasMaterialSku> item2List = itemList.stream().filter(o -> o.getSkuType().equals(item.getSku2Type())).collect(Collectors.toList());
                    if (CollectionUtils.isEmpty(item2List)) {
                        throw new BaseException("存在SKU明细为空（如：颜色、长度）的" + categoryMaps.get(item.getMaterialCategory()) + "档案，无法执行确认操作，请核实！");
                    } else {
                        name = name + checkSkuStatus(item2List);
                    }
                }
                if (StrUtil.isNotBlank(name)) {
                    throw new BaseException("SKU档案" + name + "已停用，不能启用！");
                }
                // 尺码组存在
                if (item.getSku2GroupSid() != null &&
                        !(ConstantsEms.MATERIAL_CATEGORY_SP.equals(item.getMaterialCategory())
                                || ConstantsEms.MATERIAL_CATEGORY_YP.equals(item.getMaterialCategory()))) {
                    //得到明细中启用状态的尺码明细的skuSid
                    List<Long> skuSidList = itemList.stream().filter(o -> ConstantsEms.ENABLE_STATUS.equals(o.getStatus()) && ConstantsEms.SKUTYP_CM.equals(o.getSkuType())).map(o -> o.getSkuSid()).collect(Collectors.toList());
                    for (Long skuSid : skuSidList) {
                        //判断这个sku有没有在物料商品的尺码组下
                        BasSkuGroupItem groupItem = basSkuGroupItemMapper.selectOne(new QueryWrapper<BasSkuGroupItem>()
                                .lambda().eq(BasSkuGroupItem::getSkuGroupSid, item.getSku2GroupSid()).eq(BasSkuGroupItem::getSkuSid, skuSid));
                        if (groupItem == null) {
                            BasSku sku = basSkuMapper.selectById(skuSid);
                            skuName = skuName + sku.getSkuName() + ";";
                        }
                    }
                    if (StrUtil.isNotBlank(skuName)) {
                        if (skuName.endsWith(";")) {
                            skuName = skuName.substring(0, skuName.length() - 1);
                        }
                        BasSkuGroup skuGroup = basSkuGroupMapper.selectById(item.getSku2GroupSid());
                        skuName = item.getMaterialName() + "的尺码组" + skuGroup.getSkuGroupName() + "中不存在尺码" + skuName + "，无法确认！";
                    }
                }
            }
            BasMaterial materialNEW = basMaterialMapper.selectById(item.getMaterialSid());
            judge(materialNEW);
            //价格记录表
            if (ConstantsEms.MATERIAL_CATEGORY_WL.equals(materialNEW.getMaterialCategory()) && ConstantsEms.CHECK_STATUS.equals(basMaterial.getHandleStatus())) {
                if (materialNEW.getVendorSid() != null && materialNEW.getQuotePriceTax() != null) {
                    insertPriceInfo(materialNEW);
                }
            }
        }
        if (StrUtil.isNotBlank(skuName)) {
            throw new BaseException(skuName);
        }
        //确认状态后删除待办
        if (!ConstantsEms.SAVA_STATUS.equals(basMaterial.getHandleStatus())) {
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .in(SysTodoTask::getDocumentSid, materialSidList));
        }
        int row = 0;
        String msg = "";
        String BARCODE_LOCK_KEY = "material_add_lock:" + ApiThreadLocalUtil.get().getClientId();
        String random = UUID.randomUUID().toString();
        try {
            //尝试获取商品条码锁
            if (redisService.lock(BARCODE_LOCK_KEY, random)) {
                basMaterial.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
                basMaterial.setConfirmDate(new Date());
                row = basMaterialMapper.confirm(basMaterial);
                if (row == materialSidList.length && ConstantsEms.CHECK_STATUS.equals(basMaterial.getHandleStatus())) {
                    //更新商品条码表
                    for (Long materalSid : materialSidList) {
                        List<BasMaterialBarcode> barcode = basMaterialBarcodeMapper.selectList(new QueryWrapper<BasMaterialBarcode>().lambda().eq(BasMaterialBarcode::getMaterialSid, materalSid));
                        if (CollectionUtils.isEmpty(barcode)) {
                            List<BasMaterial> materialSkuList = basMaterialSkuMapper.getBasMaterialSkuList(new BasMaterial().setMaterialSid(materalSid));
                            createBarcode(materialSkuList);
                        }
                        //插入日志
                        MongodbDeal.check(materalSid, basMaterial.getHandleStatus(), null, TITLE, null);
                    }
                } else {
                    throw new BaseException("确认异常,请联系管理员");
                }
            } else {
                throw new BaseException("系统繁忙,请稍后再试");
            }
        } catch (Exception e) {
            if (StrUtil.isEmpty(msg)) {
                msg = e.getMessage();
            }
            throw new BaseException(msg);
        } finally {
            redisService.unlock(BARCODE_LOCK_KEY, random);
        }
        return row;
    }

    /**
     * 生成商品条码 如果没有就新建，有就修改商品条码的启停状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBarcode(List<Long> materialSids) {
        List<BasMaterial> list = new ArrayList<>();
        materialSids.forEach(materialSid -> {
            List<BasMaterial> materialSkuList = basMaterialSkuMapper.getBasMaterialSkuList(new BasMaterial().setMaterialSid(materialSid));
            materialSkuList.forEach(item -> {
                if (!(ConstantsEms.CHECK_STATUS.equals(item.getHandleStatus()) && ConstantsEms.ENABLE_STATUS.equals(item.getStatus()))) {
                    throw new BaseException("生成商品条码需要满足确认状态同时是已启用状态！");
                }
                BasMaterialBarcode basMaterialBarcode = new BasMaterialBarcode();
                String status = item.getStatus();
                if (item.getSku2Sid() != null) {
                    basMaterialBarcode.setSku2Sid(item.getSku2Sid());
                    basMaterialBarcode.setSku2Code(item.getSku2Code());
                    basMaterialBarcode.setSku2Type(item.getSku2Type());
                    if (ConstantsEms.ENABLE_STATUS.equals(item.getSku1Status())) {
                        status = item.getSku2Status();
                    }
                }
                if (item.getSku1Sid() != null) {
                    basMaterialBarcode.setSku1Sid(item.getSku1Sid());
                    basMaterialBarcode.setSku1Code(item.getSku1Code());
                    basMaterialBarcode.setSku1Type(item.getSku1Type());
                    if (ConstantsEms.DISENABLE_STATUS.equals(item.getSku1Status())) {
                        status = item.getSku1Status();
                    }
                }
                basMaterialBarcode.setMaterialSid(item.getMaterialSid());
                basMaterialBarcode = basMaterialBarcodeMapper.selectBasMaterialBarcode(basMaterialBarcode);
                //如果当前没有商品条码
                if (basMaterialBarcode == null) {
                    list.add(item);
                }
                //如果已经存在商品条码
                else {
                    basMaterialBarcode.setStatus(status);
                    basMaterialBarcodeMapper.updateById(basMaterialBarcode);
                }
            });
        });
        if (CollectionUtils.isNotEmpty(list)) {
            createBarcode(list);
        }
        return materialSids.size();
    }

    /*
     * 对新增的款色（款 / 款SKU） 生成商品条码
     *
     * */
    public void createBarcode(List<BasMaterial> materialList) {
        List<BasMaterialBarcode> barcodeList = new ArrayList<>();
        for (int i = 0; i < materialList.size(); i++) {
            Long nextCode = barcodeRangeConfigService.nextId();
            BasMaterialBarcode barcode = new BasMaterialBarcode();
            barcode.setBarcode(nextCode.toString());
            barcode.setMaterialSid(Long.valueOf(materialList.get(i).getMaterialSid()));
            barcode.setSku1Sid(materialList.get(i).getSku1Sid());
            barcode.setSku1Code(materialList.get(i).getSku1Code());
            barcode.setSku1Type(materialList.get(i).getSku1Type());
            barcode.setSku2Code(materialList.get(i).getSku2Code());
            barcode.setSku2Type(materialList.get(i).getSku2Type());
            barcode.setSku2Sid(materialList.get(i).getSku2Sid());

            String matrialCode = materialList.get(i).getMaterialCode() == null ? "" : materialList.get(i).getMaterialCode();
            String sku1Code = materialList.get(i).getSku1Code() == null ? "" : materialList.get(i).getSku1Code();
            String sku2Code = materialList.get(i).getSku2Code() == null ? "" : materialList.get(i).getSku2Code();
            barcode.setBarcode2(matrialCode + sku1Code + sku2Code);

            if (!ConstantsEms.ENABLE_STATUS.equals(materialList.get(i).getStatus())) {
                barcode.setStatus(ConstantsEms.DISENABLE_STATUS);
            } else {
                barcode.setStatus(ConstantsEms.ENABLE_STATUS);
                if (StrUtil.isNotEmpty(materialList.get(i).getSku1Status()) && ConstantsEms.DISENABLE_STATUS.equals(materialList.get(i).getSku1Status())) {
                    barcode.setStatus(ConstantsEms.DISENABLE_STATUS);
                }
                if (StrUtil.isNotEmpty(materialList.get(i).getSku1Status()) && ConstantsEms.ENABLE_STATUS.equals(materialList.get(i).getSku1Status())) {
                    if (StrUtil.isNotEmpty(materialList.get(i).getSku2Status()) && ConstantsEms.DISENABLE_STATUS.equals(materialList.get(i).getSku2Status())) {
                        barcode.setStatus(ConstantsEms.DISENABLE_STATUS);
                    }
                }
            }
            barcodeList.add(barcode);
        }
        basMaterialBarcodeMapper.inserts(barcodeList);
    }

    /**
     * 物料&商品&服务档案变更
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String change(BasMaterial basMaterial) {
        RLock lock = redissonClient.getLock(LOCK_KEY);
        lock.lock(10L, TimeUnit.SECONDS);
        Map<Long, BasMaterialSku> materialSkuSidMap = new HashMap<>();
        String msg = "";
        boolean warn = false;
        try {
            Long materialSid = basMaterial.getMaterialSid();
            //旧数据
            BasMaterial material = basMaterialMapper.selectBasMaterialById(materialSid);
            //验证是否确认状态
            if (!HandleStatus.CONFIRMED.getCode().equals(material.getHandleStatus())) {
                throw new BaseException("仅确认状态才允许变更");
            }
            Map<String, Object> queryParams = new HashMap<>();
            List<BasMaterial> queryResult = new ArrayList<>();
            if (StrUtil.isNotBlank(basMaterial.getMaterialCode())) {
                if (StrUtil.isBlank(material.getMaterialCode()) || !material.getMaterialCode().equals(basMaterial.getMaterialCode())) {
                    queryParams.put("material_code", basMaterial.getMaterialCode());
                    queryResult = basMaterialMapper.selectByMap(queryParams);
                    if (queryResult.size() > 0) {
                        for (BasMaterial o : queryResult) {
                            if (!o.getMaterialSid().equals(basMaterial.getMaterialSid())) {
                                if (ConstantsEms.MATERIAL_CATEGORY_WL.equals(basMaterial.getMaterialCategory())) {
                                    throw new CustomException("物料编码重复,请查看");
                                }
                                else if (ConstantsEms.MATERIAL_CATEGORY_SP.equals(basMaterial.getMaterialCategory())
                                        || ConstantsEms.MATERIAL_CATEGORY_YP.equals(basMaterial.getMaterialCategory())) {
                                    throw new CustomException("商品编码(款号)重复,请查看");
                                }
                                throw new CustomException("编码重复,请查看");
                            }
                        }
                    }
                    queryParams.clear();
                }
            }
            if (!material.getMaterialName().equals(basMaterial.getMaterialName())) {
                if (
                        ConstantsEms.MATERIAL_CATEGORY_FW.equals(basMaterial.getMaterialCategory()) ||
                                ConstantsEms.MATERIAL_CATEGORY_WCY.equals(basMaterial.getMaterialCategory())) {
                    queryParams.put("material_name", basMaterial.getMaterialName());
                    queryResult = basMaterialMapper.selectByMap(queryParams);
                    if (queryResult.size() > 0) {
                        for (BasMaterial o : queryResult) {
                            if (!o.getMaterialSid().equals(basMaterial.getMaterialSid())) {
                                throw new CustomException("名称已存在,请查看");
                            }
                        }
                    }
                } else if (ConstantsEms.MATERIAL_CATEGORY_WL.equals(basMaterial.getMaterialCategory()) ||
                        ConstantsEms.MATERIAL_CATEGORY_SP.equals(basMaterial.getMaterialCategory()) ||
                        ConstantsEms.MATERIAL_CATEGORY_YP.equals(basMaterial.getMaterialCategory())) {
                    queryParams.put("material_name", basMaterial.getMaterialName());
                    queryResult = basMaterialMapper.selectList(new QueryWrapper<BasMaterial>().lambda()
                            .eq(BasMaterial::getMaterialName, basMaterial.getMaterialName())
                            .in(BasMaterial::getMaterialCategory, new String[]{ConstantsEms.MATERIAL_CATEGORY_FW, ConstantsEms.MATERIAL_CATEGORY_WCY}));
                    if (queryResult.size() > 0) {
                        for (BasMaterial o : queryResult) {
                            if (!o.getMaterialSid().equals(basMaterial.getMaterialSid())) {
                                if (ConstantsEms.MATERIAL_CATEGORY_WL.equals(basMaterial.getMaterialCategory())) {
                                    throw new CustomException("物料名称已存在,请查看");
                                }
                                else if (ConstantsEms.MATERIAL_CATEGORY_SP.equals(basMaterial.getMaterialCategory())) {
                                    throw new CustomException("商品名称已存在,请查看");
                                }
                                else if (ConstantsEms.MATERIAL_CATEGORY_YP.equals(basMaterial.getMaterialCategory())) {
                                    throw new CustomException("样衣名称已存在,请查看");
                                }
                                throw new CustomException("名称已存在,请查看");
                            }
                        }
                    }
                } else {
                }
            }
            if (ConstantsEms.MATERIAL_CATEGORY_YP.equals(basMaterial.getMaterialCategory()) &&
                    StrUtil.isNotBlank(basMaterial.getSampleCodeSelf()) && !basMaterial.getSampleCodeSelf().equals(material.getSampleCodeSelf())) {
                queryResult = basMaterialMapper.selectList(new QueryWrapper<BasMaterial>().lambda().eq(BasMaterial::getSampleCodeSelf, basMaterial.getSampleCodeSelf()));
                if (queryResult.size() > 0) {
                    for (BasMaterial o : queryResult) {
                        if (!o.getMaterialSid().equals(basMaterial.getMaterialSid())) {
                            throw new CustomException("我司样衣号已存在,请查看");
                        }
                    }
                }
            }
            judge(basMaterial);
            String name = "";
            name = checkSkuStatus(basMaterial.getBasMaterialSkuList());
            if (StrUtil.isNotBlank(name)) {
                throw new CustomException("SKU档案" + name + "已停用，不能启用！");
            }
            this.setConfirmInfo(basMaterial);
            //工艺单动态通知
            this.sent(basMaterial);
            //物料&商品-附件对象
            addBasMaterialAttachment(basMaterial);
            // 商品销售站点对象
            List<BasMaterialSaleStation> basMaterialSaleStationList = basMaterial.getSaleStationList();
            basMaterialSaleStationMapper.delete(new QueryWrapper<BasMaterialSaleStation>()
                    .lambda().eq(BasMaterialSaleStation::getMaterialSid, basMaterial.getMaterialSid()));
            if (CollectionUtil.isNotEmpty(basMaterialSaleStationList)) {
                basMaterialSaleStationList.forEach(item->{
                    item.setMaterialSid(basMaterial.getMaterialSid());
                });
                basMaterialSaleStationMapper.inserts(basMaterialSaleStationList);
            }
            setPictuerPath(basMaterial);
            if (material.getProducePlantSid() == null || !material.getProducePlantSid().equals(basMaterial.getProducePlantSid())) {
                setProducePlantCode(basMaterial);
            }
            if (material.getMaterialClassSid() == null || !material.getMaterialClassSid().equals(basMaterial.getMaterialClassSid())) {
                setMaterialClassSids(basMaterial);
            }
            basMaterialMapper.updateAllById(basMaterial);
            //物料&商品-SKU明细对象
            List<BasMaterialSku> basMaterialSkuList = basMaterial.getBasMaterialSkuList();
            List<BasMaterialSku> oldItemList = basMaterialSkuMapper.selectList(new QueryWrapper<BasMaterialSku>().lambda()
                    .eq(BasMaterialSku::getMaterialSid, basMaterial.getMaterialSid()));
            materialSkuSidMap = oldItemList.stream().collect(Collectors.
                    toMap(BasMaterialSku::getMaterialSkuSid, Function.identity()));
            Map<Long, BasMaterialSku> materialSkuMap = oldItemList.stream().collect(Collectors.toMap(BasMaterialSku::getMaterialSkuSid, Function.identity()));
            if (CollectionUtils.isNotEmpty(basMaterialSkuList)) {
                if (CollectionUtil.isNotEmpty(materialSkuMap)) {
                    basMaterialSkuList.forEach(o -> {
                        if (o.getMaterialSkuSid() != null) {
                            List<OperMsg> msgList = new ArrayList<>();
                            msgList = BeanUtils.eq(materialSkuMap.get(o.getMaterialSkuSid()), o);
                            if (msgList.size() > 0) {
                                o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                                o.setUpdateDate(new Date());
                            }
                        }
                    });
                }
                addBasMaterialSku(basMaterial, basMaterialSkuList);
            }
            updateBarcodeMethod(basMaterial);
            //bom颜色操作
            if (ConstantsEms.MATERIAL_CATEGORY_SP.equals(basMaterial.getMaterialCategory()) ||
                    ConstantsEms.MATERIAL_CATEGORY_YP.equals(basMaterial.getMaterialCategory())) {
                changeBom(basMaterial);
            }
            //价格记录表
            if (ConstantsEms.MATERIAL_CATEGORY_WL.equals(basMaterial.getMaterialCategory()) && ConstantsEms.CHECK_STATUS.equals(basMaterial.getHandleStatus())) {
                if (basMaterial.getVendorSid() != null && basMaterial.getQuotePriceTax() != null) {
                    insertPriceInfo(basMaterial);
                }
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(material, basMaterial);
            String remark = "";
            // 控制变更操作才记录
            if (ConstantsEms.CHECK_STATUS.equals(material.getHandleStatus())) {
                // 物料的 面料辅料 或者 商品 或者 样衣
                if (ConstantsEms.MATERIAL_CATEGORY_WL.equals(material.getMaterialCategory())
                        || ConstantsEms.MATERIAL_CATEGORY_YP.equals(material.getMaterialCategory())
                        || ConstantsEms.MATERIAL_CATEGORY_SP.equals(material.getMaterialCategory())) {
                    // 物料类型 商品类型 样衣类型
                    if ((material.getMaterialType() != null && !material.getMaterialType().equals(basMaterial.getMaterialType()))
                            || (StrUtil.isBlank(material.getMaterialType()) && StrUtil.isNotBlank(basMaterial.getMaterialType()))) {
                        List<ConMaterialType> typeList = conMaterialTypeMapper.selectList(new QueryWrapper<>());
                        Map<String, String> typeMaps = typeList.stream().collect(Collectors.toMap(ConMaterialType::getCode, ConMaterialType::getName, (key1, key2) -> key2));
                        String oldType = material.getMaterialType() == null ? "" : typeMaps.get(material.getMaterialType());
                        String newType = basMaterial.getMaterialType() == null ? "" : typeMaps.get(basMaterial.getMaterialType());
                        if (ConstantsEms.MATERIAL_CATEGORY_WL.equals(material.getMaterialCategory())) {
                            remark = remark + "物料类型字段变更，更新前：" + oldType + "，更新后：" + newType + "\n";
                        }
                        else if (ConstantsEms.MATERIAL_CATEGORY_SP.equals(material.getMaterialCategory())) {
                            remark = remark + "商品类型字段变更，更新前：" + oldType + "，更新后：" + newType + "\n";
                        }
                        else if (ConstantsEms.MATERIAL_CATEGORY_YP.equals(material.getMaterialCategory())) {
                            remark = remark + "样衣类型字段变更，更新前：" + oldType + "，更新后：" + newType + "\n";
                        }
                    }
                    // 上下装
                    if ((material.getUpDownSuit() != null && !material.getUpDownSuit().equals(basMaterial.getUpDownSuit()))
                            || (material.getUpDownSuit() == null && basMaterial.getUpDownSuit() != null)) {
                        List<DictData> upDownSuitList = sysDictDataService.selectDictData("s_up_down_suit");
                        Map<String, String> upDownSuitMaps = upDownSuitList.stream().collect(Collectors.toMap(DictData::getDictValue, DictData::getDictLabel, (key1, key2) -> key2));
                        String oldData = material.getUpDownSuit() == null ? "" : upDownSuitMaps.get(material.getUpDownSuit());
                        String newData = basMaterial.getUpDownSuit() == null ? "" : upDownSuitMaps.get(basMaterial.getUpDownSuit());
                        remark = remark + "上下装字段变更，更新前：" + oldData + "，更新后：" + newData + "\n";
                    }
                    // 基本计量单位
                    if ((material.getUnitBase() != null && !material.getUnitBase().equals(basMaterial.getUnitBase()))
                            || (StrUtil.isBlank(material.getUnitBase()) && StrUtil.isNotBlank(basMaterial.getUnitBase()))) {
                        List<ConMeasureUnit> measureUnitList = conMeasureUnitMapper.selectList(new QueryWrapper<>());
                        Map<String, String> measureUnitMaps = measureUnitList.stream().collect(Collectors.toMap(ConMeasureUnit::getCode, ConMeasureUnit::getName, (key1, key2) -> key2));
                        String oldData = material.getUnitBase() == null ? "" : measureUnitMaps.get(material.getUnitBase());
                        String newData = basMaterial.getUnitBase() == null ? "" : measureUnitMaps.get(basMaterial.getUnitBase());
                        remark = remark + "基本计量单位字段变更，更新前：" + oldData + "，更新后：" + newData + "\n";
                    }
                    // 是否SKU物料
                    if ((material.getIsSkuMaterial() != null && !material.getIsSkuMaterial().equals(basMaterial.getIsSkuMaterial()))
                            || (StrUtil.isBlank(material.getIsSkuMaterial()) && StrUtil.isNotBlank(basMaterial.getIsSkuMaterial()))) {
                        List<DictData> isSkuList = sysDictDataService.selectDictData("s_yesno_flag");
                        Map<String, String> isSkuMaps = isSkuList.stream().collect(Collectors.toMap(DictData::getDictValue, DictData::getDictLabel, (key1, key2) -> key2));
                        String oldData = material.getIsSkuMaterial() == null ? "" : isSkuMaps.get(material.getIsSkuMaterial());
                        String newData = basMaterial.getIsSkuMaterial() == null ? "" : isSkuMaps.get(basMaterial.getIsSkuMaterial());
                        if (ConstantsEms.MATERIAL_CATEGORY_WL.equals(material.getMaterialCategory())) {
                            remark = remark + "是否SKU物料字段变更，更新前：" + oldData + "，更新后：" + newData + "\n";
                        }
                        else if (ConstantsEms.MATERIAL_CATEGORY_SP.equals(material.getMaterialCategory())) {
                            remark = remark + "是否SKU商品字段变更，更新前：" + oldData + "，更新后：" + newData + "\n";
                        }
                        else if (ConstantsEms.MATERIAL_CATEGORY_YP.equals(material.getMaterialCategory())) {
                            remark = remark + "是否SKU样衣字段变更，更新前：" + oldData + "，更新后：" + newData + "\n";
                        }
                        warn = true;
                    }
                    // SKU维度数
                    if ((material.getSkuDimension() != null && !material.getSkuDimension().equals(basMaterial.getSkuDimension()))
                            || (material.getSkuDimension() == null && basMaterial.getSkuDimension() != null)) {
                        List<DictData> skuDimensionList = sysDictDataService.selectDictData("s_sku_dimension");
                        Map<String, String> skuDimensionMaps = skuDimensionList.stream().collect(Collectors.toMap(DictData::getDictValue, DictData::getDictLabel, (key1, key2) -> key2));
                        String oldData = material.getSkuDimension() == null ? "" : skuDimensionMaps.get(material.getSkuDimension().toString());
                        String newData = basMaterial.getSkuDimension() == null ? "" : skuDimensionMaps.get(basMaterial.getSkuDimension().toString());
                        remark = remark + "SKU维度数字段变更，更新前：" + oldData + "，更新后：" + newData + "\n";
                        warn = true;
                    }
                    // SKU1属性类型
                    if ((material.getSku1Type() != null && !material.getSku1Type().equals(basMaterial.getSku1Type()))
                            || (material.getSku1Type() == null && basMaterial.getSku1Type() != null)) {
                        List<DictData> sku1TypeList = sysDictDataService.selectDictData("s_sku_type");
                        Map<String, String> sku1TypeMaps = sku1TypeList.stream().collect(Collectors.toMap(DictData::getDictValue, DictData::getDictLabel, (key1, key2) -> key2));
                        String oldData = material.getSku1Type() == null ? "" : sku1TypeMaps.get(material.getSku1Type());
                        String newData = basMaterial.getSku1Type() == null ? "" : sku1TypeMaps.get(basMaterial.getSku1Type());
                        remark = remark + "SKU1属性类型字段变更，更新前：" + oldData + "，更新后：" + newData + "\n";
                        warn = true;
                    }
                    // SKU2属性类型
                    if ((material.getSku2Type() != null && !material.getSku2Type().equals(basMaterial.getSku2Type()))
                            || (material.getSku2Type() == null && basMaterial.getSku2Type() != null)) {
                        List<DictData> sku2TypeList = sysDictDataService.selectDictData("s_sku_type");
                        Map<String, String> sku2TypeMaps = sku2TypeList.stream().collect(Collectors.toMap(DictData::getDictValue, DictData::getDictLabel, (key1, key2) -> key2));
                        String oldData = material.getSku2Type() == null ? "" : sku2TypeMaps.get(material.getSku2Type());
                        String newData = basMaterial.getSku2Type() == null ? "" : sku2TypeMaps.get(basMaterial.getSku2Type());
                        remark = remark + "SKU2属性类型字段变更，更新前：" + oldData + "，更新后：" + newData + "\n";
                        warn = true;
                    }
                }
                // 版型
                if ((material.getModelSid() != null && !material.getModelSid().equals(basMaterial.getModelSid()))
                        || (material.getModelSid() == null && basMaterial.getModelSid() != null)) {
                    List<TecModel> modelList = tecModelMapper.selectList(new QueryWrapper<TecModel>().lambda()
                            .eq(TecModel::getModelSid, material.getModelSid()).or().eq(TecModel::getModelSid, basMaterial.getModelSid()));
                    Map<Long, String> modelMaps = modelList.stream().collect(Collectors.toMap(TecModel::getModelSid, TecModel::getModelName, (key1, key2) -> key2));
                    String oldData = material.getModelSid() == null ? "" : modelMaps.get(material.getModelSid());
                    String newData = basMaterial.getModelSid() == null ? "" : modelMaps.get(basMaterial.getModelSid());
                    remark = remark + "版型字段变更，更新前：" + oldData + "，更新后：" + newData + "\n";
                }
                // 尺码组
                if ((material.getSku2GroupSid() != null && !material.getSku2GroupSid().equals(basMaterial.getSku2GroupSid()))
                        || (material.getSku2GroupSid() == null && basMaterial.getSku2GroupSid() != null)) {
                    List<BasSkuGroup> skuGroupList = basSkuGroupMapper.selectList(new QueryWrapper<BasSkuGroup>().lambda()
                            .eq(BasSkuGroup::getSkuGroupSid, material.getSku2GroupSid()).or().eq(BasSkuGroup::getSkuGroupSid, basMaterial.getSku2GroupSid()));
                    Map<Long, String> skuGroupMaps = skuGroupList.stream().collect(Collectors.toMap(BasSkuGroup::getSkuGroupSid, BasSkuGroup::getSkuGroupName, (key1, key2) -> key2));
                    String oldData = material.getSku2GroupSid() == null ? "" : skuGroupMaps.get(material.getSku2GroupSid());
                    String newData = basMaterial.getSku2GroupSid() == null ? "" : skuGroupMaps.get(basMaterial.getSku2GroupSid());
                    remark = remark + "尺码组字段变更，更新前：" + oldData + "，更新后：" + newData + "\n";
                }
                // 我司样衣号
                if ((material.getSampleCodeSelf() != null && !material.getSampleCodeSelf().equals(basMaterial.getSampleCodeSelf()))
                        || (StrUtil.isBlank(material.getSampleCodeSelf()) && StrUtil.isNotBlank(basMaterial.getSampleCodeSelf()))) {
                    String oldData = material.getSampleCodeSelf() == null ? "" : material.getSampleCodeSelf();
                    String newData = basMaterial.getSampleCodeSelf() == null ? "" : basMaterial.getSampleCodeSelf();
                    remark = remark + "我司样衣号字段变更，更新前：" + oldData + "，更新后：" + newData + "\n";
                }
            }
            MongodbUtil.insertUserLog(basMaterial.getMaterialSid(), com.platform.common.log.enums.BusinessType.CHANGE.getValue(), msgList, TITLE, remark);
        } catch (BaseException e) {
            throw new BaseException(e.getMessage());
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        if (CollectionUtil.isNotEmpty(basMaterial.getBasMaterialSkuList())) {
            if (CollectionUtil.isNotEmpty(basMaterial.getBasMaterialSkuList().stream().filter(o -> o.getMaterialSkuSid() == null).collect(Collectors.toList()))) {
                msg = "请及时更新此商品新增颜色的BOM信息！\n";
            }
            if (warn) {
                msg = msg + "请按需更新后续相关数据 或 停用相应的商品SKU条码！";
            }
        }
        if (CollectionUtil.isNotEmpty(basMaterial.getBasMaterialSkuList())) {
            Map<Long, BasMaterialSku> materialSkuSidMap2 = basMaterial.getBasMaterialSkuList().stream().filter(o -> o.getMaterialSkuSid() != null).collect(Collectors.
                    toMap(BasMaterialSku::getMaterialSkuSid, Function.identity()));
            for (Long key : materialSkuSidMap2.keySet()) {
                if (materialSkuSidMap.get(key) != null) {
                    if (materialSkuSidMap.get(key).getStatus().equals(ConstantsEms.DISENABLE_STATUS) &&
                            materialSkuSidMap2.get(key).getStatus().equals(ConstantsEms.ENABLE_STATUS)) {
                        if (warn) {
                            return "请及时更新此商品新启用颜色的BOM信息！\n请按需更新后续相关数据 或 停用相应的商品SKU条码！";
                        }
                        else {
                            return "请及时更新此商品新启用颜色的BOM信息！";
                        }
                    }
                }
            }
        }
        return msg;
    }

    /**
     * 商品条码 查询页面   批量启用/停用商品条码
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int barcodeStatus(BasMaterialBarcode basMaterialBarcode) {
        if (basMaterialBarcode.getBarcodeSidList() == null) {
            throw new BaseException("请选择行！");
        }
        if (basMaterialBarcode.getStatus() == null) {
            throw new BaseException("参数缺失！");
        }
        // 商品条码sids
        Long[] barcodeSidList = basMaterialBarcode.getBarcodeSidList();
        List<BasMaterialBarcode> basMaterialBarcodeList = basMaterialBarcodeMapper
                .selectBasMaterialBarcodeList(new BasMaterialBarcode().setBarcodeSidList(barcodeSidList));
        basMaterialBarcodeList.forEach(item -> {
            basMaterialBarcodeMapper.updateById(new BasMaterialBarcode().setBarcodeSid(item.getBarcodeSid())
                    .setStatus(basMaterialBarcode.getStatus()));
            // 商品条码插入日志
            String businessType = basMaterialBarcode.getStatus().equals(ConstantsEms.ENABLE_STATUS)
                    ? com.platform.common.log.enums.BusinessType.ENABLE.getValue()
                    : com.platform.common.log.enums.BusinessType.DISENABLE.getValue();
            MongodbUtil.insertUserLog(Long.valueOf(item.getBarcodeSid()), businessType, null, TITLE);
            // 物料商品样品档案插入日志
            String status = basMaterialBarcode.getStatus().equals(ConstantsEms.ENABLE_STATUS)
                    ? "启用" : "停用";
            status = status + "商品条码" + item.getBarcode();
            MongodbUtil.insertUserLog(Long.valueOf(item.getMaterialSid()), com.platform.common.log.enums.BusinessType.QITA.getValue(),
                    null, TITLE, status);
        });
        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeIsLine(List<Long> materialSids) {
        List<BasMaterial> materialList = basMaterialMapper.selectList(new QueryWrapper<BasMaterial>().lambda().in(BasMaterial::getMaterialSid, materialSids));
        //允许的物料
        List<BasMaterial> yes = materialList.stream().filter(s -> s.getIsCreateProductLine().equals(ConstantsEms.YES)).collect(Collectors.toList());
        List<Long> yesSidList = new ArrayList<>();
        yes.forEach(item -> {
            yesSidList.add(item.getMaterialSid());
        });
        //允许改为不允许要校验
        if (CollectionUtils.isNotEmpty(yesSidList)) {
            List<TecProductLine> lineList = tecProductLineMapper.selectList(new QueryWrapper<TecProductLine>().lambda().in(TecProductLine::getProductLineSid, yesSidList));
            if (CollectionUtils.isNotEmpty(lineList)) {
                throw new BaseException("存在已建商品线用量的商品，不能改为“否”状态");
            }
        }
        //不允许的物料
        List<BasMaterial> no = materialList.stream().filter(s -> s.getIsCreateProductLine().equals(ConstantsEms.NO)).collect(Collectors.toList());
        List<Long> noSidList = new ArrayList<>();
        no.forEach(item -> {
            noSidList.add(item.getMaterialSid());
        });
        //不允许改为允许
        if (CollectionUtils.isNotEmpty(noSidList)) {
            LambdaUpdateWrapper<BasMaterial> noUpdateWrapper = new LambdaUpdateWrapper<>();
            noUpdateWrapper.in(BasMaterial::getMaterialSid, noSidList).set(BasMaterial::getIsCreateProductLine, ConstantsEms.YES);
            basMaterialMapper.update(null, noUpdateWrapper);
        }
        //允许改为不允许
        if (CollectionUtils.isNotEmpty(yesSidList)) {
            LambdaUpdateWrapper<BasMaterial> yesUpdateWrapper = new LambdaUpdateWrapper<>();
            yesUpdateWrapper.in(BasMaterial::getMaterialSid, yesSidList).set(BasMaterial::getIsCreateProductLine, ConstantsEms.NO);
            basMaterialMapper.update(null, yesUpdateWrapper);
        }
        return materialSids.size();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeIsBom(List<Long> materialSids) {
        List<BasMaterial> materialList = basMaterialMapper.selectList(new QueryWrapper<BasMaterial>().lambda().in(BasMaterial::getMaterialSid, materialSids));
        //允许的物料
        List<BasMaterial> yes = materialList.stream().filter(s -> s.getIsCreateBom().equals(ConstantsEms.YES)).collect(Collectors.toList());
        List<Long> yesSidList = new ArrayList<>();
        yes.forEach(item -> {
            yesSidList.add(item.getMaterialSid());
        });
        //允许改为不允许要校验
        if (CollectionUtils.isNotEmpty(yesSidList)) {
            List<TecBomHead> bomHeadList = tecBomHeadMapper.selectList(new QueryWrapper<TecBomHead>().lambda().in(TecBomHead::getMaterialSid, yesSidList));
            if (CollectionUtils.isNotEmpty(bomHeadList)) {
                throw new BaseException("存在已建BOM的商品，不能改为“否”状态");
            }
        }
        //不允许的物料
        List<BasMaterial> no = materialList.stream().filter(s -> s.getIsCreateBom().equals(ConstantsEms.NO)).collect(Collectors.toList());
        List<Long> noSidList = new ArrayList<>();
        no.forEach(item -> {
            noSidList.add(item.getMaterialSid());
        });
        //不允许改为允许
        if (CollectionUtils.isNotEmpty(noSidList)) {
            LambdaUpdateWrapper<BasMaterial> noUpdateWrapper = new LambdaUpdateWrapper<>();
            noUpdateWrapper.in(BasMaterial::getMaterialSid, noSidList).set(BasMaterial::getIsCreateBom, ConstantsEms.YES);
            basMaterialMapper.update(null, noUpdateWrapper);
        }
        //允许改为不允许
        if (CollectionUtils.isNotEmpty(yesSidList)) {
            LambdaUpdateWrapper<BasMaterial> yesUpdateWrapper = new LambdaUpdateWrapper<>();
            yesUpdateWrapper.in(BasMaterial::getMaterialSid, yesSidList).set(BasMaterial::getIsCreateBom, ConstantsEms.NO);
            basMaterialMapper.update(null, yesUpdateWrapper);
        }
        return materialSids.size();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeIsCost(List<Long> materialSids) {
        List<BasMaterial> materialList = basMaterialMapper.selectList(new QueryWrapper<BasMaterial>().lambda().in(BasMaterial::getMaterialSid, materialSids));
        //允许的物料
        List<BasMaterial> yes = materialList.stream().filter(s -> s.getIsCreateProductcost().equals(ConstantsEms.YES)).collect(Collectors.toList());
        List<Long> yesSidList = new ArrayList<>();
        yes.forEach(item -> {
            yesSidList.add(item.getMaterialSid());
        });
        //允许改为不允许要校验
        if (CollectionUtils.isNotEmpty(yesSidList)) {
            List<CosProductCost> cosProductCostList = cosProductCostMapper.selectList(new QueryWrapper<CosProductCost>().lambda().in(CosProductCost::getMaterialSid, yesSidList));
            if (CollectionUtils.isNotEmpty(cosProductCostList)) {
                throw new BaseException("存在已建产前成本核算的商品，不能改为“否”状态");
            }
        }
        //不允许的物料
        List<BasMaterial> no = materialList.stream().filter(s -> s.getIsCreateProductcost().equals(ConstantsEms.NO)).collect(Collectors.toList());
        List<Long> noSidList = new ArrayList<>();
        no.forEach(item -> {
            noSidList.add(item.getMaterialSid());
        });
        //不允许改为允许
        if (CollectionUtils.isNotEmpty(noSidList)) {
            LambdaUpdateWrapper<BasMaterial> noUpdateWrapper = new LambdaUpdateWrapper<>();
            noUpdateWrapper.in(BasMaterial::getMaterialSid, noSidList).set(BasMaterial::getIsCreateProductcost, ConstantsEms.YES);
            basMaterialMapper.update(null, noUpdateWrapper);
        }
        //允许改为不允许
        if (CollectionUtils.isNotEmpty(yesSidList)) {
            LambdaUpdateWrapper<BasMaterial> yesUpdateWrapper = new LambdaUpdateWrapper<>();
            yesUpdateWrapper.in(BasMaterial::getMaterialSid, yesSidList).set(BasMaterial::getIsCreateProductcost, ConstantsEms.NO);
            basMaterialMapper.update(null, yesUpdateWrapper);
        }
        return materialSids.size();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeIsUploadGyd(List<Long> materialSids) {
        List<BasMaterial> materialList = basMaterialMapper.selectList(new QueryWrapper<BasMaterial>().lambda().in(BasMaterial::getMaterialSid, materialSids));
        //允许的物料
        List<BasMaterial> yes = materialList.stream().filter(s -> s.getIsUploadZhizaodan().equals(ConstantsEms.YES)).collect(Collectors.toList());
        List<Long> yesSidList = new ArrayList<>();
        yes.forEach(item -> {
            yesSidList.add(item.getMaterialSid());
        });
        //不允许的物料
        List<BasMaterial> no = materialList.stream().filter(s -> s.getIsUploadZhizaodan().equals(ConstantsEms.NO)).collect(Collectors.toList());
        List<Long> noSidList = new ArrayList<>();
        no.forEach(item -> {
            noSidList.add(item.getMaterialSid());
        });
        //不允许改为允许
        if (CollectionUtils.isNotEmpty(noSidList)) {
            LambdaUpdateWrapper<BasMaterial> noUpdateWrapper = new LambdaUpdateWrapper<>();
            noUpdateWrapper.in(BasMaterial::getMaterialSid, noSidList).set(BasMaterial::getIsUploadZhizaodan, ConstantsEms.YES);
            basMaterialMapper.update(null, noUpdateWrapper);
        }
        //允许改为不允许
        if (CollectionUtils.isNotEmpty(yesSidList)) {
            LambdaUpdateWrapper<BasMaterial> yesUpdateWrapper = new LambdaUpdateWrapper<>();
            yesUpdateWrapper.in(BasMaterial::getMaterialSid, yesSidList).set(BasMaterial::getIsUploadZhizaodan, ConstantsEms.NO);
            basMaterialMapper.update(null, yesUpdateWrapper);
        }
        return materialSids.size();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeCategory(BasMaterial basMaterial) {
        int row = 0;
        if (ArrayUtil.isEmpty(basMaterial.getMaterialSidList())) {
            return row;
        }
        if (StrUtil.isBlank(basMaterial.getMaterialCategory())) {
            throw new BaseException("参数丢失");
        }
        if (ConstantsEms.MATERIAL_CATEGORY_SP.equals(basMaterial.getMaterialCategory())) {
            Long sid = basMaterial.getMaterialSidList()[0];
            BasMaterial material = basMaterialMapper.selectById(sid);
            if (!ConstantsEms.CHECK_STATUS.equals(material.getHandleStatus()) && ConstantsEms.MATERIAL_CATEGORY_YP.equals(material.getMaterialCategory())) {
                throw new BaseException("样品 " + material.getSampleCodeSelf() + " 未确认，不能进行此操作");
            }
            //构造sql
            LambdaUpdateWrapper<BasMaterial> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(BasMaterial::getMaterialSid, sid).eq(BasMaterial::getHandleStatus, ConstantsEms.CHECK_STATUS);
            updateWrapper.set(BasMaterial::getMaterialCategory, basMaterial.getMaterialCategory());
            if (StrUtil.isBlank(material.getMaterialCode()) && ConstantsEms.MATERIAL_CATEGORY_YP.equals(material.getMaterialCategory())) {
                //前端传编码
                if (StrUtil.isNotBlank(basMaterial.getMaterialCode())) {
                    List<BasMaterial> list = basMaterialMapper.selectList(new QueryWrapper<BasMaterial>().lambda()
                            .eq(BasMaterial::getMaterialCode, basMaterial.getMaterialCode()));
                    if (CollectionUtil.isNotEmpty(list)) {
                        throw new BaseException("编码已存在,请查看");
                    }
                    updateWrapper.set(BasMaterial::getMaterialCode, basMaterial.getMaterialCode());
                    row = basMaterialMapper.update(null, updateWrapper);
                    if (row != 0) {
                        List<SysUser> userList = userMapper.selectSysUserRoleList(new SysUser().setClientId(material.getClientId())
                                .setRoleName("技术员"));
                        if (CollectionUtil.isNotEmpty(userList)) {
                            List<SysBusinessBcst> bcstList = new ArrayList<>();
                            userList.forEach(item->{
                                SysBusinessBcst bcst = new SysBusinessBcst();
                                bcst.setTitle("商品编码" + basMaterial.getMaterialCode() + "，已由样品档案转为商品档案，请知悉！")
                                        .setDocumentSid(sid).setDocumentCode(basMaterial.getMaterialCode()).setMenuId(Long.parseLong("2336"))
                                        .setNoticeDate(new Date()).setUserId(item.getUserId());
                                bcstList.add(bcst);
                            });
                            businessBcstMapper.inserts(bcstList);
                        }
                        MongodbUtil.insertUserLog(sid, com.platform.common.log.enums.BusinessType.CHANGE.getValue(), null, TITLE, "样品转为商品");
                    }
                } else {
                    throw new BaseException("样品 " + material.getSampleCodeSelf() + " 的商品编码不能为空，转为商品档案失败");
                }
            } else {
                row = basMaterialMapper.update(null, updateWrapper);
            }
        } else {
        }
        return row;
    }


    /**
     * 批量启用/停用物料&商品&服务档案
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int status(BasMaterial basMaterial) {
        //物料&商品&服务档案sids
        Long[] materialSidList = basMaterial.getMaterialSidList();
        basMaterialMapper.confirm(basMaterial);
        //启用
        if (Status.ENABLE.getCode().equals(basMaterial.getStatus())) {
            BasMaterial params = new BasMaterial();
            int count = basMaterialMapper.selectCount(new QueryWrapper<BasMaterial>().lambda().in(BasMaterial::getMaterialSid, materialSidList)
                    .eq(BasMaterial::getHandleStatus, HandleStatus.CONFIRMED.getCode()));
            if (count != materialSidList.length) {
                throw new BaseException("仅确认状态才允许启用");
            }
            List<Long> sidList = new ArrayList<>();
            for (int i = 0; i < materialSidList.length; i++) {
                sidList.add(materialSidList[i]);
            }
            // 更新商品条码
            insertBarcode(sidList);
        } else {
            //更新商品条码为停用
            basMaterialBarcodeMapper.update(null, new UpdateWrapper<BasMaterialBarcode>().lambda()
                    .set(BasMaterialBarcode::getStatus, ConstantsEms.DISENABLE_STATUS)
                    .in(BasMaterialBarcode::getMaterialSid, materialSidList));
        }
        String disableRemark = "";
        if (StrUtil.isNotBlank(basMaterial.getDisableRemark())) {
            disableRemark = basMaterial.getDisableRemark();
        }
        for (int i = 0; i < basMaterial.getMaterialSidList().length; i++) {
            Long id = basMaterial.getMaterialSidList()[i];
            //插入日志
            MongodbDeal.status(id, basMaterial.getStatus(), null, TITLE, disableRemark);
        }
        return 1;
    }

    @Override
    public int setWpcRemindDays(BasMaterial basMaterial) {
        int row = 0;
        if(basMaterial.getMaterialSidList().length>0){
            row = basMaterialMapper.update(null, new UpdateWrapper<BasMaterial>().lambda()
                    .set(BasMaterial::getWpcRemindDays, basMaterial.getWpcRemindDays())
                    .in(BasMaterial::getMaterialSid, basMaterial.getMaterialSidList()));
        }
        return row;
    }

    /*
     * 获取sku明细中停用的sku档案名称
     *
     * */
    private String checkSkuStatus(List<BasMaterialSku> request) {
        String msg = "";
        if (CollectionUtil.isNotEmpty(request)) {
            request = request.stream().filter(o -> ConstantsEms.ENABLE_STATUS.equals(o.getStatus())).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(request)) {
                List<Long> skuSidList = request.stream().map(BasMaterialSku::getSkuSid).collect(Collectors.toList());
                List<BasSku> basSkuList = basSkuMapper.selectList(new QueryWrapper<BasSku>().lambda()
                        .in(BasSku::getSkuSid, skuSidList)
                        .eq(BasSku::getStatus, ConstantsEms.DISENABLE_STATUS));
                if (CollectionUtil.isNotEmpty(basSkuList)) {
                    msg = "[";
                    for (BasSku basSku : basSkuList) {
                        msg = msg + basSku.getSkuName() + ",";
                    }
                    msg = msg.substring(0, msg.length() - 1);
                    msg = msg + "]";
                }
            }
        }
        return msg;
    }

    /*
     * 工艺单上传提醒 （邮箱+动态通知）
     *
     * */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int sent(BasMaterial basMaterial) {
        String mailList = "";
        //得到用户Id
        List<Long> userIdList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(basMaterial.getAttachmentList())) {
            //在集合中查询出第一个工艺单的附件
            Optional<BasMaterialAttachment> optional = basMaterial.getAttachmentList().stream().filter(
                    item -> ConstantsEms.FILE_TYPE_SPEC.equals(item.getFileType())).findFirst();
            BasMaterialAttachment attachment = optional.isPresent() ? optional.get() : null;
            //如果工艺单不等于空 且 这是新的工艺单（没有sid）
            if (attachment != null && attachment.getMaterialAttachmentSid() == null) {
                //获取通知配置表中工艺单需要通知的用户
                List<ConBcstUserConfig> conBcstUserConfigList = conBcstUserConfigMapper.selectConBcstUserConfigList(
                        new ConBcstUserConfig()
                                .setBcstType(ConstantsEms.BCST_TYPE_SPEC)
                                .setDataobjectCategoryCode(ConstantsEms.BCST_OBJECT_MATERIAL));
                if (CollectionUtils.isNotEmpty(conBcstUserConfigList)) {
                    if (basMaterial.getCustomerSid() != null) {
                        BasCustomer customer = basCustomerMapper.selectBasCustomerById(basMaterial.getCustomerSid());
                        basMaterial.setCustomerShortName(customer.getShortName());
                    }
                    //拼接邮箱和id
                    for (ConBcstUserConfig email : conBcstUserConfigList) {
                        if (StrUtil.isNotBlank(email.getEmail())) {
                            mailList = mailList + email.getEmail() + ";";
                        }
                        userIdList.add(email.getUserId());
                    }
                    if (CollectionUtils.isNotEmpty(userIdList)) {
                        addBusinessBcst(basMaterial, attachment, userIdList);
                    }
                    if (mailList != "") {
                        mailSend(basMaterial, attachment, mailList);
                    }
                    sentOther(basMaterial, conBcstUserConfigList);
                }
            }
        }
        return userIdList.size();
    }

    /*
     * 业务动态列对象写入新的工艺单上传动态
     *
     * */
    public void addBusinessBcst(BasMaterial basMaterial, BasMaterialAttachment attachment, List<Long> userIdList) {
        if (CollectionUtils.isNotEmpty(userIdList)) {
            Long documentSid = basMaterial.getMaterialSid();
            String documentCode = basMaterial.getMaterialCode();
            String title = "款号：" + documentCode + " 的工艺单附件已更新";
            if (basMaterial.getCustomerShortName() != null) {
                title = basMaterial.getCustomerShortName() + " 款号：" + documentCode + " 的工艺单附件已更新";
            }
            if (attachment != null && attachment.getMaterialAttachmentSid() == null) {
                List<SysBusinessBcst> businessBcstList = new ArrayList<>();
                String finalTitle = title;
                userIdList.forEach(id -> {
                    SysBusinessBcst item = new SysBusinessBcst();
                    item.setUserId(id).setDocumentSid(documentSid).setDocumentCode(documentCode)
                            .setTitle(finalTitle).setNoticeDate(new Date());
                    businessBcstList.add(item);
                });
                businessBcstMapper.inserts(businessBcstList);
            }
        }
    }

    /*
     * 工艺单更新完成通知：企业微信，钉钉，公众号
     *
     * */
    public void sentOther(BasMaterial material, List<ConBcstUserConfig> conBcstUserConfigList) {
        if (CollectionUtil.isEmpty(conBcstUserConfigList)) {
            return;
        }
        BasProductSeason basProductSeason = basProductSeasonMapper.selectById(material.getProductSeasonSid());
        //得到用户企业微信Id
        String workWechatOpenidList = "";
        //得到用户钉钉Id
        String dingtalkOpenidList = "";
        //拼接工艺单openid和企微openid
        for (ConBcstUserConfig config : conBcstUserConfigList) {
            workWechatOpenidList = workWechatOpenidList + config.getDingtalkOpenid() + ",";
            dingtalkOpenidList = dingtalkOpenidList + config.getDingtalkOpenid() + ",";
        }
        workWechatOpenidList = workWechatOpenidList.substring(0, workWechatOpenidList.length() - 1);
        dingtalkOpenidList = dingtalkOpenidList.substring(0, dingtalkOpenidList.length() - 1);
        //工艺单更新完成通知-钉钉
        try {
            SysClient client = new SysClient();
            client = sysClientMapper.selectSysClientById(ApiThreadLocalUtil.get().getClientId());
            SysUser user = new SysUser();
            //工艺单更新完成通知-钉钉
            if (StrUtil.isNotBlank(client.getDingtalkAppkey()) && StrUtil.isNotBlank(client.getDingtalkAppsecret())) {
                String title = "工艺单更新通知";
                JSONObject textJson = new JSONObject();
                textJson.put("msgtype", DingtalkConstants.MSG_TYPE_OA);
                JSONObject oaJson = new JSONObject();
                oaJson.put("message_url", "");
                JSONObject oaJson1 = new JSONObject();
                oaJson1.put("bgcolor", "FF0097FF");
                oaJson1.put("text", "");
                oaJson.put("head", oaJson1);
                JSONObject oaJson2 = new JSONObject();
                oaJson2.put("title", title);
                JSONObject oaJson3 = new JSONObject();
                oaJson3.put("key", "款号：");
                oaJson3.put("value", material.getMaterialCode());
                JSONObject oaJson4 = new JSONObject();
                oaJson4.put("key", "款名称：");
                oaJson4.put("value", material.getMaterialName());
                JSONObject oaJson5 = new JSONObject();
                oaJson5.put("key", "产品季：");
                oaJson5.put("value", basProductSeason == null ? "" : basProductSeason.getProductSeasonName());
                JSONObject oaJson6 = new JSONObject();
                oaJson6.put("key", "上传人：");
                oaJson6.put("value", ApiThreadLocalUtil.get().getSysUser().getNickName());
                JSONObject oaJson7 = new JSONObject();
                oaJson7.put("key", "上传时间：");
                oaJson7.put("value", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
                JSONObject oaJson8 = new JSONObject();
                oaJson8.put("key", "备注：");
                oaJson8.put("value", "请按最新工艺单组织排产，谢谢！");
                List<JSONObject> list = new ArrayList<>();
                list.add(oaJson3);
                list.add(oaJson4);
                list.add(oaJson5);
                list.add(oaJson6);
                list.add(oaJson7);
                list.add(oaJson8);
                oaJson2.put("form", list);
                oaJson.put("body", oaJson2);
                textJson.put("oa", oaJson);
                //
                user.setDingtalkAppkey(client.getDingtalkAppkey());
                user.setDingtalkAppsecret(client.getDingtalkAppsecret());
                for (ConBcstUserConfig config : conBcstUserConfigList) {
                    if (StrUtil.isNotBlank(config.getDingtalkOpenid())) {
                        user.setTouser(config.getDingtalkOpenid());
                        DdPushUtil.SendDdMsgOA(user, DingtalkConstants.SCM_AGENT_ID, textJson);
                    }
                }
            }
            if (StrUtil.isNotBlank(client.getWorkWechatAppkey()) && StrUtil.isNotBlank(client.getWorkWechatAppsecret())) {
                //企微
                String productSeasonName = basProductSeason == null ? "" : basProductSeason.getProductSeasonName();
                String sampleCodeSelf = material.getSampleCodeSelf() == null ? "" : material.getSampleCodeSelf();
                String markdowntext = "<font color=\"warning\">工艺单更新通知</font> \n" +
                        "款号：<font color=\"info\">" + material.getMaterialCode() + "</font> \n" +
                        "款名称：<font color=\"info\">" + material.getMaterialName() + "</font> \n" +
                        "产品季：<font color=\"info\">" + productSeasonName + "</font> \n" +
                        "上传人：<font color=\"info\">" + ApiThreadLocalUtil.get().getSysUser().getNickName() + "</font> \n" +
                        "上传时间：<font color=\"info\">" + DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss") + "</font> \n" +
                        "备注：<font color=\"info\">" + "请按最新工艺单组织排产，谢谢！" + "</font>";
                user.setWorkWechatAppkey(client.getWorkWechatAppkey());
                user.setWorkWechatAppsecret(client.getWorkWechatAppsecret());
                for (ConBcstUserConfig config : conBcstUserConfigList) {
                    if (StrUtil.isNotBlank(config.getWorkWechatOpenid())) {
                        user.setTouser(config.getWorkWechatOpenid());
                        QiYePushUtil.SendQyMsgMarkdown(user, WxConstants.SCM_AGENT_ID, markdowntext);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * 邮件发送新的工艺单上传提醒
     *
     * */
    public void mailSend(BasMaterial basMaterial, BasMaterialAttachment attachment, String mailList) {
        String remark = "";
        if (attachment != null && mailList != "") {
            if (attachment.getRemark() != null) {
                remark = attachment.getRemark();
            }
            SysUser user = userMapper.selectById(ApiThreadLocalUtil.get().getSysUser().getUserId());
            if (user == null) {
                user = new SysUser();
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
            String date = simpleDateFormat.format(new Date());
            if (basMaterial.getCustomerShortName() == null) {
                basMaterial.setCustomerShortName("");
            }
            String mailtext =
                    "<font color=\"gray\">款号：</font><font color=\"black\">" + basMaterial.getMaterialCode() + "</font>  <br />" +
                            "<font color=\"gray\">附件更新人：</font><font color=\"black\">" + user.getNickName() + "</font>  <br />" +
                            "<font color=\"gray\">附件更新日期：</font><font color=\"black\">" + date + "</font>  <br />" +
                            "<font color=\"gray\">备注：</font><font color=\"black\">" + remark + "</font>  <br />" +
                            "<font color=\"black\">具体附件请到SCM系统中查询！</font>";
            try {
                if (attachment.getFilePath() != null) {
                    MailUtil.send(mailList, null, mailList,
                            "【SCM工艺单更新通知】 " + basMaterial.getCustomerShortName() + " 款号：" + basMaterial.getMaterialCode() + " 的工艺单附件已更新，请查阅", mailtext, true);
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.info("邮件发送失败");
            }
        }
    }

    @Override
    public BasMaterial copyBasMaterialById(Long materialSid) {
        BasMaterial basMaterial = basMaterialMapper.selectBasMaterialById(materialSid);
        if (basMaterial == null) {
            return null;
        }
        //物料&商品-SKU明细对象
        BasMaterialSku basMaterialSku = new BasMaterialSku();
        basMaterialSku.setMaterialSid(materialSid);
        List<BasMaterialSku> basMaterialSkuList = basMaterialSkuMapper.selectBasMaterialSkuList(basMaterialSku);
        basMaterialSkuList.forEach(item -> {
            item.setMaterialSid(null).setMaterialSkuSid(null).setHandleStatus(null)
                    .setCreatorAccount(null).setCreateDate(null).setCreatorAccountName(null)
                    .setUpdaterAccount(null).setUpdateDate(null);
        });
        basMaterial.setBasMaterialSkuList(basMaterialSkuList);
        // 商品销售站点对象
        List<BasMaterialSaleStation> basMaterialSaleStationList = basMaterialSaleStationMapper
                .selectBasMaterialSaleStationList(new BasMaterialSaleStation()
                        .setMaterialSid(materialSid));
        if (CollectionUtil.isNotEmpty(basMaterialSaleStationList)) {
            basMaterialSaleStationList.forEach(item->{
                item.setMaterialSid(null).setMaterialSaleStationSid(null)
                        .setCreateDate(null).setCreatorAccount(null)
                        .setUpdateDate(null).setUpdaterAccount(null);
            });
            basMaterial.setSaleStationList(basMaterialSaleStationList);
        }
        basMaterial.setMaterialSid(null).setHandleStatus(null)
                .setCreatorAccount(null).setCreateDate(null).setCreatorAccountName(null)
                .setUpdaterAccount(null).setUpdateDate(null)
                .setConfirmerAccount(null).setConfirmDate(null);
        basMaterial.setMaterialCode(null);
        return basMaterial;
    }

    /**
     * 设置快反款
     * @param basMaterial
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setKuaiFan(BasMaterial basMaterial) {
        if (basMaterial.getMaterialSidList().length == 0) {
            throw new BaseException("请选择行！");
        }
        LambdaUpdateWrapper<BasMaterial> updateWrapper = new LambdaUpdateWrapper<>();
        int row = 0;
        if (StrUtil.isBlank(basMaterial.getIsKuaifankuan())) {
            basMaterial.setIsKuaifankuan(null);
        }
        //是否快反款
        updateWrapper.in(BasMaterial::getMaterialSid, basMaterial.getMaterialSidList()).set(BasMaterial::getIsKuaifankuan, basMaterial.getIsKuaifankuan());
        row = basMaterialMapper.update(null, updateWrapper);
        return row;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setOperator(BasMaterial basMaterial) {
        if (basMaterial.getMaterialSidList().length == 0) {
            throw new BaseException("请选择行！");
        }
        LambdaUpdateWrapper<BasMaterial> updateWrapper = new LambdaUpdateWrapper<>();
        int row = 0;
        if (StrUtil.isBlank(basMaterial.getBuOperator())) {
            basMaterial.setBuOperator(null);
        }
        //我方跟单员
        updateWrapper.in(BasMaterial::getMaterialSid, basMaterial.getMaterialSidList()).set(BasMaterial::getBuOperator, basMaterial.getBuOperator());
        row = basMaterialMapper.update(null, updateWrapper);
        return row;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setOperatorVendor(BasMaterial basMaterial) {
        if (basMaterial.getMaterialSidList().length == 0) {
            throw new BaseException("请选择行！");
        }
        LambdaUpdateWrapper<BasMaterial> updateWrapper = new LambdaUpdateWrapper<>();
        int row = 0;
        //供方业务员
        if (StrUtil.isBlank(basMaterial.getBuOperatorVendor())) {
            basMaterial.setBuOperatorVendor(null);
        }
        updateWrapper.in(BasMaterial::getMaterialSid, basMaterial.getMaterialSidList()).set(BasMaterial::getBuOperatorVendor, basMaterial.getBuOperatorVendor());
        row = basMaterialMapper.update(null, updateWrapper);
        return row;
    }

    /**
     * 设置商品编码(款号)
     * @param basMaterial
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setMaterialCode(BasMaterial basMaterial) {
        if (basMaterial.getMaterialSidList() == null || basMaterial.getMaterialSidList().length == 0) {
            throw new BaseException("请选择行！");
        }
        basMaterial.setMaterialSid(basMaterial.getMaterialSidList()[0]);
        if (StrUtil.isBlank(basMaterial.getMaterialCode())) {
            throw new BaseException("商品编码不能为空！");
        }
        else {
            List<BasMaterial> materialList = basMaterialMapper.selectList(new QueryWrapper<BasMaterial>()
                    .lambda().eq(BasMaterial::getMaterialCode, basMaterial.getMaterialCode())
                    .ne(BasMaterial::getMaterialSid, basMaterial.getMaterialSid()));
            if (CollectionUtil.isNotEmpty(materialList)) {
                throw new BaseException("商品编码已存在！");
            }
        }
        // 得到旧的数据
        BasMaterial material = basMaterialMapper.selectById(basMaterial.getMaterialSid());
        LambdaUpdateWrapper<BasMaterial> updateWrapper = new LambdaUpdateWrapper<>();
        int row = 0;
        //商品编码
        if (StrUtil.isBlank(basMaterial.getMaterialCode())) {
            basMaterial.setMaterialCode(null);
        }
        updateWrapper.eq(BasMaterial::getMaterialSid, basMaterial.getMaterialSid()).set(BasMaterial::getMaterialCode, basMaterial.getMaterialCode());
        row = basMaterialMapper.update(null, updateWrapper);
        // 记录操作日志
        if (!basMaterial.getMaterialCode().equals(material.getMaterialCode())) {
            BasMaterial nowData = new BasMaterial();
            BeanUtil.copyProperties(material, nowData);
            nowData.setMaterialCode(basMaterial.getMaterialCode());
            List<OperMsg> msgList;
            msgList = BeanUtils.eq(material, nowData);
            String oldCode = material.getMaterialCode() == null ? "" : material.getMaterialCode();
            String newCode = nowData.getMaterialCode() == null ? "" : nowData.getMaterialCode();
            String remark = "商品编码变更，变更前：" + oldCode + "；变更后：" + newCode;
            MongodbUtil.insertUserLog(basMaterial.getMaterialSid(), com.platform.common.log.enums.BusinessType.QITA.getValue(), msgList, TITLE, remark);
            row = 1;
        }
        return row;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setProducePlant(BasMaterial basMaterial) {
        if (basMaterial.getMaterialSidList().length == 0) {
            throw new BaseException("请选择行！");
        }
        LambdaUpdateWrapper<BasMaterial> updateWrapper = new LambdaUpdateWrapper<>();
        int row = 0;
        //供方业务员
        if (basMaterial.getProducePlantSid() != null) {
            setProducePlantCode(basMaterial);
        }
        else {
            basMaterial.setProducePlantSid(null);
            basMaterial.setProducePlantCode(null);
        }
        updateWrapper.in(BasMaterial::getMaterialSid, basMaterial.getMaterialSidList())
                .set(BasMaterial::getProducePlantSid, basMaterial.getProducePlantSid())
                .set(BasMaterial::getProducePlantCode, basMaterial.getProducePlantCode());
        row = basMaterialMapper.update(null, updateWrapper);
        return row;
    }

    /*
     * 按款添加明细 查询
     *
     * */
    @Override
    public MaterialAddResponse addBodyItem(MaterialAddRequest request){
        String code = request.getCode();
        List<OrderItemFunResponse> orderItemList = request.getOrderItemList();
        MaterialAddResponse data = new MaterialAddResponse();
        List<OrderItemFunResponse> includeList= includeList = orderItemList.stream().filter(li -> li.getMaterialCode().equals(code)).collect(Collectors.toList());
        List<BasMaterialSku> list = basMaterialSkuMapper.getSkuByCode(code);
        List<BasMaterialSku> listYs = list.stream().filter(li -> ConstantsEms.SKUTYP_YS.equals(li.getSkuType())).collect(Collectors.toList());
        if(CollectionUtil.isEmpty(listYs)){
            throw new CustomException("该商品没有对应的商品条码，操作失败");
        }
        List<BasMaterialSku> listCm = list.stream().filter(li -> !ConstantsEms.SKUTYP_YS.equals(li.getSkuType())).collect(Collectors.toList());
        if(CollectionUtil.isEmpty(listCm)){
            throw new CustomException("该商品只有一种sku类型，操作失败");
        }
        List<String> headList = listCm.stream().map(li -> li.getSkuName()).collect(Collectors.toList());
        Long materialSid = list.get(0).getMaterialSid();
        List<MaterialAddSkuResponse> sku1List = new ArrayList<>();
        listYs.forEach(li->{
            MaterialAddSkuResponse materialAddSkuResponse = new MaterialAddSkuResponse();
            materialAddSkuResponse.setSkuName(li.getSkuName());
            materialAddSkuResponse.setSkuSid(li.getSkuSid());
            sku1List.add(materialAddSkuResponse);
        });
        //明细中存在对应的款
        if(CollectionUtil.isNotEmpty(includeList)){
            String dateStr = "9999-12-31";
            Date time = DateUtil.parse(dateStr);
            includeList.parallelStream().forEach(li->{
                if(li.getContractDate()==null){
                    li.setContractDate(time);
                }
            });
            List<MaterialAddItemResponse> listSkuItem = new ArrayList<MaterialAddItemResponse>();
            //通过sKu1name 分组
            Map<String, List<OrderItemFunResponse>> hashMap = includeList.stream().collect(Collectors.groupingBy(li -> li.getSku1Name()+";"+li.getSku1Sid()+";"+li.getContractDate()));
            List<String> headSort = new ArrayList<>();
            hashMap.keySet().stream().forEach(li->{
                List<MaterialAddSkuResponse> sku2List = new ArrayList<>();
                MaterialAddSkuResponse sku1Item = new MaterialAddSkuResponse();
                List<OrderItemFunResponse> purPurchaseOrderItems = hashMap.get(li);
                String[] arr = li.split(";");
                MaterialAddItemResponse item = new MaterialAddItemResponse();
                item.setSkuName(arr[0])
                        .setSkuSid(Long.valueOf(arr[1]));
                long time1 = time.getTime();
                long time2 = DateUtil.parse(arr[2]).getTime();
                if(time1!=time2){
                    item.setContractDate(DateUtil.parse(arr[2]));
                }
                Map<Long, List<OrderItemFunResponse>> map = purPurchaseOrderItems.stream().collect(Collectors.groupingBy(m -> m.getSku2Sid()));
                //对应尺码的数量
                listCm.parallelStream().forEach(cm->{
                    MaterialAddSkuResponse materialAddSkuResponse = new MaterialAddSkuResponse();
                    materialAddSkuResponse.setSkuName(cm.getSkuName());
                    materialAddSkuResponse.setSkuSid(cm.getSkuSid());
                    List<OrderItemFunResponse> items = map.get(cm.getSkuSid());
                    BigDecimal sum=null;
                    if(CollectionUtil.isNotEmpty(items)){
                        sum = items.stream().filter(m->m.getQuantity()!=null).map(it -> it.getQuantity()).reduce(BigDecimal.ZERO, BigDecimal::add);
                    }
                    materialAddSkuResponse.setQuantity(sum);
                    sku2List.add(materialAddSkuResponse);
                });
                item.setMaterialSid(materialSid);
                item.setListSku1(sku1List);
                List<MaterialAddSkuResponse> sortSku2 = sortSku2Item(sku2List);
                if(CollectionUtil.isEmpty(headSort)){
                    List<String> items = sortSku2.stream().map(h -> h.getSkuName()).collect(Collectors.toList());
                    headSort.addAll(items);
                }
                item.setListSku2(sortSku2);
                listSkuItem.add(item);
            });
            data.setItemList(listSkuItem);
            data.setHeadList(headSort);
        }else{
            List<MaterialAddItemResponse> listSkuItem = new ArrayList<MaterialAddItemResponse>();
            List<MaterialAddSkuResponse> sku2List = new ArrayList<>();
            MaterialAddItemResponse item = new MaterialAddItemResponse();
            item.setMaterialSid(materialSid);
            listCm.parallelStream().forEach(cm->{
                MaterialAddSkuResponse materialAddSkuResponse = new MaterialAddSkuResponse();
                materialAddSkuResponse.setSkuName(cm.getSkuName());
                materialAddSkuResponse.setSkuSid(cm.getSkuSid());
                sku2List.add(materialAddSkuResponse);
            });
            List<MaterialAddSkuResponse> sortSku2 = sortSku2Item(sku2List);
            item.setListSku2(sortSku2);
            List<String> headSort = sortSku2.stream().map(h -> h.getSkuName()).collect(Collectors.toList());
            item.setListSku1(sku1List);
            listSkuItem.add(item);
            data.setItemList(listSkuItem);
            data.setHeadList(headSort);
        }
        return data;
    }

    public List<MaterialAddSkuResponse> sortSku2Item(List<MaterialAddSkuResponse> itemList){
        if(CollectionUtil.isNotEmpty(itemList)){
            List<MaterialAddSkuResponse> skuExit = itemList.stream().filter(li -> li.getSkuName() != null).collect(Collectors.toList());
            if(CollectionUtil.isNotEmpty(skuExit)){
                //对尺码排序
                if (CollectionUtil.isNotEmpty(skuExit)) {
                    skuExit.forEach(li -> {
                        String skuName = li.getSkuName();
                        String[] nameSplit = skuName.split("/");
                        if (nameSplit.length == 1) {
                            li.setFirstSort(nameSplit[0].replaceAll("[a-zA-Z]", ""));
                        } else {
                            String[] name2split = nameSplit[1].split("\\(");
                            if (name2split.length == 2) {
                                li.setSecondSort(name2split[0].replaceAll("[a-zA-Z]", ""));

                                li.setThirdSort(name2split[1].replaceAll("[a-zA-Z]", ""));
                            } else {
                                li.setSecondSort(nameSplit[1].replaceAll("[a-zA-Z]", ""));
                            }
                            li.setFirstSort(nameSplit[0].replaceAll("[a-zA-Z]", ""));
                        }
                        if(!JudgeFormat.isValidDouble(li.getFirstSort())){
                            li.setFirstSort("10000");
                        }
                    });
                    List<MaterialAddSkuResponse> allList = new ArrayList<>();
                    List<MaterialAddSkuResponse> allThirdList = new ArrayList<>();
                    List<MaterialAddSkuResponse> sortThird = skuExit.stream().filter(li -> li.getThirdSort() != null).collect(Collectors.toList());
                    List<MaterialAddSkuResponse> sortThirdNull = skuExit.stream().filter(li -> li.getThirdSort() == null).collect(Collectors.toList());
                    sortThird = sortThird.stream().sorted(Comparator.comparing(li -> li.getThirdSort())).collect(Collectors.toList());
                    allThirdList.addAll(sortThird);
                    allThirdList.addAll(sortThirdNull);
                    List<MaterialAddSkuResponse> sort = allThirdList.stream().filter(li -> li.getSecondSort() != null).collect(Collectors.toList());
                    sort = sort.stream().sorted(Comparator.comparing(li -> Integer.valueOf(li.getSecondSort()))).collect(Collectors.toList());
                    List<MaterialAddSkuResponse> sortNull = allThirdList.stream().filter(li -> li.getSecondSort() == null).collect(Collectors.toList());
                    allList.addAll(sort);
                    allList.addAll(sortNull);
                    skuExit = allList.stream().sorted(Comparator.comparing(item -> Double.valueOf(item.getFirstSort()))
                    ).collect(Collectors.toList());
                }
            }
            List<MaterialAddSkuResponse> skuExitNo = itemList.stream().filter(li -> li.getSkuName() == null).collect(Collectors.toList());
            ArrayList<MaterialAddSkuResponse> itemArrayListAll = new ArrayList<>();
            itemArrayListAll.addAll(skuExit);
            itemArrayListAll.addAll(skuExitNo);
            return itemArrayListAll;
        }
        return new ArrayList<>();
    }

    public List<OrderItemFunResponse> sortItem(List<OrderItemFunResponse> salSalesOrderItemList){
        if(CollectionUtil.isNotEmpty(salSalesOrderItemList)){
            List<OrderItemFunResponse> skuExit = salSalesOrderItemList.stream().filter(li -> li.getSku2Name() != null).collect(Collectors.toList());
            if(CollectionUtil.isNotEmpty(skuExit)){
                //对尺码排序
                if (CollectionUtil.isNotEmpty(skuExit)) {
                    skuExit.forEach(li -> {
                        String skuName = li.getSku2Name();
                        String[] nameSplit = skuName.split("/");
                        if (nameSplit.length == 1) {
                            li.setFirstSort(nameSplit[0].replaceAll("[a-zA-Z]", ""));
                        } else {
                            String[] name2split = nameSplit[1].split("\\(");
                            if (name2split.length == 2) {
                                li.setSecondSort(name2split[0].replaceAll("[a-zA-Z]", ""));

                                li.setThirdSort(name2split[1].replaceAll("[a-zA-Z]", ""));
                            } else {
                                li.setSecondSort(nameSplit[1].replaceAll("[a-zA-Z]", ""));
                            }
                            li.setFirstSort(nameSplit[0].replaceAll("[a-zA-Z]", ""));
                        }
                        if(!JudgeFormat.isValidDouble(li.getFirstSort())){
                            li.setFirstSort("10000");
                        }
                    });
                    List<OrderItemFunResponse> allList = new ArrayList<>();
                    List<OrderItemFunResponse> allThirdList = new ArrayList<>();
                    List<OrderItemFunResponse> sortThird = skuExit.stream().filter(li -> li.getThirdSort() != null).collect(Collectors.toList());
                    List<OrderItemFunResponse> sortThirdNull = skuExit.stream().filter(li -> li.getThirdSort() == null).collect(Collectors.toList());
                    sortThird = sortThird.stream().sorted(Comparator.comparing(li -> li.getThirdSort())).collect(Collectors.toList());
                    allThirdList.addAll(sortThird);
                    allThirdList.addAll(sortThirdNull);
                    List<OrderItemFunResponse> sort = allThirdList.stream().filter(li -> li.getSecondSort() != null).collect(Collectors.toList());
                    sort = sort.stream().sorted(Comparator.comparing(li -> Integer.valueOf(li.getSecondSort()))).collect(Collectors.toList());
                    List<OrderItemFunResponse> sortNull = allThirdList.stream().filter(li -> li.getSecondSort() == null).collect(Collectors.toList());
                    allList.addAll(sort);
                    allList.addAll(sortNull);
                    skuExit = allList.stream().sorted(Comparator.comparing(item -> Double.valueOf(item.getFirstSort()))
                    ).collect(Collectors.toList());
                }
            }
            List<OrderItemFunResponse> skuExitNo = salSalesOrderItemList.stream().filter(li -> li.getSku2Name() == null).collect(Collectors.toList());
            ArrayList<OrderItemFunResponse> itemArrayListAll = new ArrayList<>();
            itemArrayListAll.addAll(skuExit);
            itemArrayListAll.addAll(skuExitNo);
            salSalesOrderItemList=itemArrayListAll.stream().sorted(Comparator.comparing(OrderItemFunResponse::getMaterialCode)
                    .thenComparing(OrderItemFunResponse::getSku1Name)
            ).collect(Collectors.toList());

            return salSalesOrderItemList;
        }
        return new ArrayList<>();
    }

    @Override
    public List<BasMaterialBarcode> sortBarcode(List<BasMaterialBarcode> salSalesOrderItemList){
        if(CollectionUtil.isNotEmpty(salSalesOrderItemList)){
            List<BasMaterialBarcode> skuExit = salSalesOrderItemList.stream().filter(li -> li.getSku2Name() != null).collect(Collectors.toList());
            if(CollectionUtil.isNotEmpty(skuExit)){
                //对尺码排序
                if (CollectionUtil.isNotEmpty(skuExit)) {
                    skuExit.forEach(li -> {
                        String skuName = li.getSku2Name();
                        String[] nameSplit = skuName.split("/");
                        if (nameSplit.length == 1) {
                            li.setFirstSort(nameSplit[0].replaceAll("[a-zA-Z]", ""));
                        } else {
                            String[] name2split = nameSplit[1].split("\\(");
                            if (name2split.length == 2) {
                                li.setSecondSort(name2split[0].replaceAll("[a-zA-Z]", ""));

                                li.setThirdSort(name2split[1].replaceAll("[a-zA-Z]", ""));
                            } else {
                                li.setSecondSort(nameSplit[1].replaceAll("[a-zA-Z]", ""));
                            }
                            li.setFirstSort(nameSplit[0].replaceAll("[a-zA-Z]", ""));
                        }
                        if(!JudgeFormat.isValidDouble(li.getFirstSort())){
                            li.setFirstSort("10000");
                        }
                    });
                    List<BasMaterialBarcode> allList = new ArrayList<>();
                    List<BasMaterialBarcode> allThirdList = new ArrayList<>();
                    List<BasMaterialBarcode> sortThird = skuExit.stream().filter(li -> li.getThirdSort() != null).collect(Collectors.toList());
                    List<BasMaterialBarcode> sortThirdNull = skuExit.stream().filter(li -> li.getThirdSort() == null).collect(Collectors.toList());
                    sortThird = sortThird.stream().sorted(Comparator.comparing(li -> li.getThirdSort())).collect(Collectors.toList());
                    allThirdList.addAll(sortThird);
                    allThirdList.addAll(sortThirdNull);
                    List<BasMaterialBarcode> sort = allThirdList.stream().filter(li -> li.getSecondSort() != null).collect(Collectors.toList());
                    sort = sort.stream().sorted(Comparator.comparing(li -> Integer.valueOf(li.getSecondSort()))).collect(Collectors.toList());
                    List<BasMaterialBarcode> sortNull = allThirdList.stream().filter(li -> li.getSecondSort() == null).collect(Collectors.toList());
                    allList.addAll(sort);
                    allList.addAll(sortNull);
                    skuExit = allList.stream().sorted(Comparator.comparing(item -> Double.valueOf(item.getFirstSort()))
                    ).collect(Collectors.toList());
                }
            }
            List<BasMaterialBarcode> skuExitNo = salSalesOrderItemList.stream().filter(li -> li.getSku2Name() == null).collect(Collectors.toList());
            ArrayList<BasMaterialBarcode> itemArrayListAll = new ArrayList<>();
            itemArrayListAll.addAll(skuExit);
            itemArrayListAll.addAll(skuExitNo);
            salSalesOrderItemList=itemArrayListAll.stream().sorted(Comparator.comparing(BasMaterialBarcode::getMaterialCode, Comparator.nullsLast(String::compareTo))
                    .thenComparing(BasMaterialBarcode::getSku1Name, Comparator.nullsLast(String::compareTo))
            ).collect(Collectors.toList());

            return salSalesOrderItemList;
        }
        return new ArrayList<>();
    }

    /*
     * 按款添加明细 转换成对应的明细信息
     *
     * */
    @Override
    public List<OrderItemFunResponse> getItem(MaterialAddResponse data){
        BasSaleOrderRequest request = new BasSaleOrderRequest();
        BeanCopyUtils.copyProperties(data, request);
        List<MaterialAddItemResponse> itemList = data.getItemList();
        List<OrderItemFunResponse> orderItemList = data.getOrderItemList();
        String code = data.getCode();
        HashMap<Long, BigDecimal> map = new HashMap<>();
        List<Long> barcodes = new ArrayList<>();
        List<String> dataList = new ArrayList<>();
        Long materialSid = itemList.get(0).getMaterialSid();
        List<BasMaterialBarcode> basMaterialBarcodes = basMaterialBarcodeMapper.selectList(new QueryWrapper<BasMaterialBarcode>().lambda()
                .eq(BasMaterialBarcode::getMaterialSid, materialSid)
        );
        HashMap<String, String> judgeRepate = new HashMap<>();
        itemList.forEach(li -> {
            List<MaterialAddSkuResponse> skuList = li.getListSku2();
            List<MaterialAddSkuResponse> quantityList = skuList.stream().filter(sku -> sku.getQuantity() != null).collect(Collectors.toList());
            if(CollectionUtil.isEmpty(quantityList)){
                throw new CustomException("每个明细行至少存在一个数量值");
            }
            String key=li.getSkuSid()+""+li.getContractDate();
            if(judgeRepate.get(key)!=null){
                throw new CustomException("此合同交期与颜色组合已存在，请检查");
            }else{
                judgeRepate.put(key,key);
            }
            skuList.forEach(sku -> {
                Date contractDate = li.getContractDate();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd   HH:mm:ss");
                String date = "-1";
                if (contractDate != null) {
                    date = sdf.format(contractDate);
                }
                if (sku.getQuantity() != null) {
                    List<BasMaterialBarcode> list = basMaterialBarcodes.parallelStream().filter(m ->
                            m.getSku1Sid().toString().equals(li.getSkuSid().toString())
                                    && m.getSku2Sid().toString().equals(sku.getSkuSid().toString())).collect(Collectors.toList());
                    if(CollectionUtil.isNotEmpty(list)){
                        BasMaterialBarcode basMaterialBarcode = list.get(0);
                        barcodes.add(basMaterialBarcode.getBarcodeSid());
                        dataList.add(sku.getQuantity() + ";" + date);
                        map.put(basMaterialBarcode.getBarcodeSid(), sku.getQuantity());
                    }
                }
            });
        });
        if(barcodes.size()==0){
            throw new CustomException("每个明细行至少存在一个数量值");
        }
        List<OrderItemFunResponse> orderItems = new ArrayList<>();
        List<OrderItemFunResponse> includeList = null;
        List<OrderItemFunResponse> NotCludeList = null;
        if (CollectionUtil.isNotEmpty(orderItemList)) {
            includeList = orderItemList.stream().filter(li -> li.getMaterialCode().equals(code)).collect(Collectors.toList());
            NotCludeList = orderItemList.stream().filter(li -> !li.getMaterialCode().equals(code)).collect(Collectors.toList());
        }
        int legth = barcodes.size();
        Long[] barcodeArr = barcodes.stream().toArray(size -> new Long[legth]);
        request.setMaterialBarcodeSidList(barcodeArr);
        List<BasMaterial> list = salSalesOrderService.getMaterialInfo(request);
        for (int i = 0; i < list.size(); i++) {
            String item = dataList.get(i);
            String[] arrData = item.split(";");
            BigDecimal quantiy = new BigDecimal(arrData[0]);
            String date = arrData[1];
            if (!"-1".equals(date)) {
                Date contractDate = DateUtil.parse(date);
                list.get(i).setContractDate(contractDate);
            }
            list.get(i).setQuantity(quantiy);
        }
        List<OrderItemFunResponse> items = new ArrayList<>();
        list.parallelStream().forEach(li -> {
            OrderItemFunResponse item = new OrderItemFunResponse();
            li.setCreateDate(null)
                    .setRemark(null)
                    .setCreatorAccount(null)
                    .setCreatorAccountName(null);
            SalSalePriceItem detail = li.getSalePriceDetail();
            BeanCopyUtils.copyProperties(li,item);
            if(detail!=null){
                item.setSalePrice(detail.getSalePrice())
                        .setSalePriceTax(detail.getSalePriceTax())
                        .setUnitBase(detail.getUnitBase())
                        .setTaxRate(detail.getTaxRate())
                        .setUnitBaseName(detail.getUnitBaseName())
                        .setUnitConversionRate(detail.getUnitConversionRate())
                        .setUnitPrice(detail.getUnitPrice())
                        .setUnitPriceName(detail.getUnitPriceName());
            }
            PurPurchasePriceItem purDetail = li.getPurchasePriceDetail();
            if(purDetail!=null){
                item.setSalePrice(purDetail.getPurchasePrice())
                        .setSalePriceTax(purDetail.getPurchasePriceTax())
                        .setUnitBase(purDetail.getUnitBase())
                        .setUnitBaseName(purDetail.getUnitBaseName())
                        .setTaxRate(purDetail.getTaxRate())
                        .setUnitConversionRate(purDetail.getUnitConversionRate())
                        .setUnitPrice(purDetail.getUnitPrice())
                        .setUnitPriceName(purDetail.getUnitPriceName());
            }
            items.add(item);
        });
        orderItems.addAll(items);
        //添加款在原有的表单已存在
        if (CollectionUtil.isNotEmpty(includeList)) {
            orderItems.addAll(NotCludeList);
        } else {
            orderItems.addAll(orderItemList);
        }
        orderItems= sortItem(orderItems);
        return  orderItems;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setOperatorCustomer(BasMaterial basMaterial) {
        if (basMaterial.getMaterialSidList().length == 0) {
            throw new BaseException("请选择行！");
        }
        LambdaUpdateWrapper<BasMaterial> updateWrapper = new LambdaUpdateWrapper<>();
        int row = 0;
        //客方业务员
        if (StrUtil.isBlank(basMaterial.getBuOperatorCustomer())) {
            basMaterial.setBuOperatorCustomer(null);
        }
        updateWrapper.in(BasMaterial::getMaterialSid, basMaterial.getMaterialSidList()).set(BasMaterial::getBuOperatorCustomer, basMaterial.getBuOperatorCustomer());
        row = basMaterialMapper.update(null, updateWrapper);
        return row;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setMaterialClass(BasMaterial basMaterial) {
        if (basMaterial.getMaterialSidList().length == 0) {
            throw new BaseException("请选择行！");
        }
        LambdaUpdateWrapper<BasMaterial> updateWrapper = new LambdaUpdateWrapper<>();
        int row = 0;
        //物料分类
        updateWrapper.in(BasMaterial::getMaterialSid, basMaterial.getMaterialSidList())
                .set(BasMaterial::getMaterialClassSid, basMaterial.getMaterialClassSid());
        // 大类中类小类
        updateWrapper.set(BasMaterial::getBigClassSid, null);
        updateWrapper.set(BasMaterial::getBigClassCode, null);
        updateWrapper.set(BasMaterial::getMiddleClassSid, null);
        updateWrapper.set(BasMaterial::getMiddleClassCode, null);
        updateWrapper.set(BasMaterial::getSmallClassSid, null);
        updateWrapper.set(BasMaterial::getSmallClassCode, null);
        // 大类中类小类
        if (basMaterial.getMaterialClassSid() != null) {
            List<ConMaterialClass> parents = conMaterialClassMapper.selectConMaterialClassParentsListySon(basMaterial.getMaterialClassSid());
            if (CollectionUtil.isNotEmpty(parents)) {
                parents.forEach(item->{
                    if (item.getLevel() != null) {
                        if (item.getLevel() == 1) {
                            updateWrapper.set(BasMaterial::getBigClassSid, item.getMaterialClassSid());
                            updateWrapper.set(BasMaterial::getBigClassCode, item.getNodeCode());
                        }
                        if (item.getLevel() == 2) {
                            updateWrapper.set(BasMaterial::getMiddleClassSid, item.getMaterialClassSid());
                            updateWrapper.set(BasMaterial::getMiddleClassCode, item.getNodeCode());
                        }
                        if (item.getLevel() == 3) {
                            updateWrapper.set(BasMaterial::getSmallClassSid, item.getMaterialClassSid());
                            updateWrapper.set(BasMaterial::getSmallClassCode, item.getNodeCode());
                        }
                    }
                });
            }
        }
        row = basMaterialMapper.update(null, updateWrapper);
        return row;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setInventoryMethod(BasMaterial basMaterial) {
        if (basMaterial.getMaterialSidList().length == 0) {
            throw new BaseException("请选择行！");
        }
        LambdaUpdateWrapper<BasMaterial> updateWrapper = new LambdaUpdateWrapper<>();
        int row = 0;
        //库存价核算方法
        updateWrapper.in(BasMaterial::getMaterialSid, basMaterial.getMaterialSidList())
                .set(BasMaterial::getInventoryPriceMethod, basMaterial.getInventoryPriceMethod());
        if (ConstantsEms.INVENTORY_PRICE_METHOD_GDJ.equals(basMaterial.getInventoryPriceMethod())) {
            if (basMaterial.getInventoryStandardPrice() == null) {
                throw new CustomException("当“库存价核算方式”为“固定价”时，“固定价”不能为空");
            } else {
                updateWrapper.set(BasMaterial::getInventoryStandardPrice, basMaterial.getInventoryStandardPrice())
                        .set(BasMaterial::getCurrency, ConstantsFinance.CURRENCY_CNY)
                        .set(BasMaterial::getCurrencyUnit, ConstantsFinance.CURRENCY_UNIT_YUAN);
            }
        }
        if (basMaterial.getInventoryStandardPrice() != null) {
            updateWrapper.set(BasMaterial::getInventoryStandardPrice, basMaterial.getInventoryStandardPrice())
                    .set(BasMaterial::getCurrency, ConstantsFinance.CURRENCY_CNY)
                    .set(BasMaterial::getCurrencyUnit, ConstantsFinance.CURRENCY_UNIT_YUAN);
        }
        row = basMaterialMapper.update(null, updateWrapper);
        return row;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setPicture(BasMaterial basMaterial) {
        int row = 0;
        if (basMaterial.getMaterialSid() == null) {
            throw new BaseException("请选择行！");
        }
        LambdaUpdateWrapper<BasMaterial> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(BasMaterial::getMaterialSid, basMaterial.getMaterialSid());
        if (StrUtil.isBlank(basMaterial.getPicturePath())) {
            basMaterial.setPicturePath(null);
        }
        updateWrapper.set(BasMaterial::getPicturePath, basMaterial.getPicturePath());
        row = basMaterialMapper.update(null, updateWrapper);
        return row;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setPictures(BasMaterial basMaterial) {
        int row = 0;
        if (basMaterial.getMaterialSid() == null) {
            throw new BaseException("参数缺失，请联系管理员！");
        }
        LambdaUpdateWrapper<BasMaterial> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(BasMaterial::getMaterialSid, basMaterial.getMaterialSid());
        if (basMaterial.getPicturePathList() != null && basMaterial.getPicturePathList().length > 0) {
            setPictuerPath(basMaterial);
        } else {
            basMaterial.setPicturePathSecond(null);
        }
        updateWrapper.set(BasMaterial::getPicturePathSecond, basMaterial.getPicturePathSecond());
        if (StrUtil.isBlank(basMaterial.getPicturePath())) {
            basMaterial.setPicturePath(null);
        }
        updateWrapper.set(BasMaterial::getPicturePath, basMaterial.getPicturePath());
        row = basMaterialMapper.update(null, updateWrapper);
        // 短视频
        basMaterialAttachmentMapper.delete(new QueryWrapper<BasMaterialAttachment>().lambda()
                .eq(BasMaterialAttachment::getMaterialSid, basMaterial.getMaterialSid())
                .eq(BasMaterialAttachment::getFileType, "SP"));
        if (CollectionUtil.isNotEmpty(basMaterial.getAttachmentList())) {
            List<BasMaterialAttachment> attachmentList = basMaterial.getAttachmentList().stream().filter(o ->
                    "SP".equals(o.getFileType())).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(attachmentList)) {
                attachmentList.forEach(item->{
                    item.setMaterialSid(basMaterial.getMaterialSid());
                });
                basMaterialAttachmentMapper.inserts(attachmentList);
            }
        }
        return row;
    }

    /**
     * 报表中心类目明细报表
     *
     * @param request BasMaterialSaleStationCategoryForm
     * @return 报表中心类目明细报表
     */
    @Override
    public List<BasMaterialSaleStationCategoryForm> selectBasMaterialSaleStationCategoryFormList(BasMaterialSaleStationCategoryForm request) {
        request.setClientId(ApiThreadLocalUtil.get().getClientId());
        List<BasMaterialSaleStationCategoryForm> reponse =  basMaterialSaleStationMapper.selectMaterialSaleStationCategoryForm(request);
        return reponse;
    }

    /**
     * 报表中心类目明细报表 查看详情
     *
     * @param request BasMaterialSaleStation
     * @return BasMaterialSaleStation
     */
    @Override
    public List<BasMaterialSaleStation> selectBasMaterialSaleStationList(BasMaterialSaleStation request) {
        return basMaterialSaleStationMapper.selectBasMaterialSaleStationList(request);
    }

}
