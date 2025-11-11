package com.platform.ems.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.platform.common.core.domain.entity.SysUser;
import com.platform.common.core.domain.model.DictData;
import com.platform.common.exception.base.BaseException;
import com.platform.common.utils.file.FileUtils;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.*;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.domain.dto.response.CommonErrMsgResponse;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.*;
import com.platform.ems.plug.domain.*;
import com.platform.ems.plug.mapper.*;
import com.platform.ems.service.IBasMaterialImportService;
import com.platform.ems.service.IBasMaterialService;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.ems.util.JudgeFormat;
import com.platform.system.mapper.SystemUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 物料&商品&服务档案导入Service接口
 *
 * @author chenkw
 * @date 2022-01-20
 */
@Service
@SuppressWarnings("all")
@Slf4j
public class BasMaterialServiceImportImpl implements IBasMaterialImportService {

    @Autowired
    private BasMaterialMapper basMaterialMapper;
    @Autowired
    private IBasMaterialService basMaterialService;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    @Autowired
    private ConMaterialTypeMapper conMaterialTypeMapper;
    @Autowired
    private ConMeasureUnitMapper conMeasureUnitMapper;
    @Autowired
    private ConPurchaseTypeMapper conPurchaseTypeMapper;
    @Autowired
    private BasVendorMapper basVendorMapper;
    @Autowired
    private BasCompanyMapper basCompanyMapper;
    @Autowired
    private BasCompanyBrandMapper basCompanyBrandMapper;
    @Autowired
    private BasCustomerMapper basCustomerMapper;
    @Autowired
    private BasCustomerBrandMapper basCustomerBrandMapper;
    @Autowired
    private BasProductSeasonMapper basProductSeasonMapper;
    @Autowired
    private ConProductTechniqueTypeMapper conProductTechniqueTypeMapper;
    @Autowired
    private  TecModelMapper tecModelMapper;
    @Autowired
    private BasSkuMapper basSkuMapper;
    @Autowired
    private BasSkuGroupMapper basSkuGroupMapper;
    @Autowired
    private BasSkuGroupItemMapper basSkuGroupItemMapper;
    @Autowired
    private BasMaterialSaleStationMapper basMaterialSaleStationMapper;
    @Autowired
    private ConSaleStationMapper conSaleStationMapper;
    @Autowired
    private SystemUserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int importDataSecond(List<BasMaterial> basMaterialList){
        if(CollectionUtils.isNotEmpty(basMaterialList)){
            basMaterialList.forEach(item->{
                basMaterialService.insertBasMaterial(item);
            });
        }
        return basMaterialList.size();
    }

    /**
     * 物料-常规物料
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public HashMap<String, Object> importDataCg(MultipartFile file) {
        // 返回体
        HashMap<String, Object> res = new HashMap<>();
        // 列表数据
        List<BasMaterial> basMaterialList = new ArrayList<>();
        // 错误信息
        List<CommonErrMsgResponse> errMsgList = new ArrayList<>();
        List<CommonErrMsgResponse> warMsgList = new ArrayList<>();
        CommonErrMsgResponse errMsg = null;
        CommonErrMsgResponse warMsg = null;
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

            // 物料类型
            List<ConMaterialType> conMaterialTypeList = conMaterialTypeMapper.getConMaterialTypeList();
            conMaterialTypeList = conMaterialTypeList.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String,String> materialTypeMaps = conMaterialTypeList.stream().collect(Collectors.toMap(ConMaterialType::getName, ConMaterialType::getCode,(key1, key2)->key2));
            // 计量单位
            List<ConMeasureUnit> conMeasureUnitList = conMeasureUnitMapper.getConMeasureUnitList();
            conMeasureUnitList = conMeasureUnitList.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String,String> measureUnitMaps = conMeasureUnitList.stream().collect(Collectors.toMap(ConMeasureUnit::getName, ConMeasureUnit::getCode,(key1, key2)->key2));
            // 是否sku物料
            List<DictData> isSkuList = sysDictDataService.selectDictData("sys_yes_no");
            isSkuList = isSkuList.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String,String> isSkuMaps = isSkuList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue,(key1, key2)->key2));
            // sku维度
            List<DictData> skuList = sysDictDataService.selectDictData("s_sku_dimension");
            skuList = skuList.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String,String> skuMaps = skuList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue,(key1, key2)->key2));
            // sku类型
            List<DictData> skuTypeList = sysDictDataService.selectDictData("s_sku_type");
            skuTypeList = skuTypeList.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String,String> skuTypeMaps = skuTypeList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue,(key1, key2)->key2));
            // 拉链标识
            List<DictData> zipperList = sysDictDataService.selectDictData("s_zipper_flag");
            zipperList = zipperList.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String,String> zipperMaps = zipperList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue,(key1, key2)->key2));

            // excel表里面编码和名称的缓存
            HashMap<String, String> codeMap = new HashMap<>();
            HashMap<String, String> nameMap = new HashMap<>();
            HashMap<String, String> vendorMap = new HashMap<>();

            // 采购类型 根据用户所在租户的租户类型判断
            String clientType = ApiThreadLocalUtil.get().getSysUser().getClientType();

            // 租户字段配置
            com.platform.common.core.domain.entity.SysDefaultSettingClient client = ApiThreadLocalUtil.get().getSysUser().getClient();

            for (int i = 0; i < readAll.size(); i++) {
                if (i < 2) {
                    //前两行跳过
                    continue;
                }
                int num = i + 1;
                List<Object> objects = readAll.get(i);
                copy(objects, readAll);
                /*
                 * 物料编码
                 */
                String materialCode = objects.get(0) == null || objects.get(0) == "" ? null : objects.get(0).toString();
                if (materialCode == null) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("物料编码不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    if (materialCode.contains(" ")) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("物料编码数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        if (materialCode.length() > 20) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("物料编码长度不能大于20位，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                    // 判断是否与表格内的编码重复
                    if (codeMap.get(materialCode) == null) {
                        codeMap.put(materialCode, String.valueOf(num));
                        // 如果表格内没重复则判断与数据库之间是否存在重复
                        List<BasMaterial> materialList = basMaterialMapper.selectList(new QueryWrapper<BasMaterial>().lambda()
                                .eq(BasMaterial::getMaterialCode, materialCode));
                        if (CollectionUtil.isNotEmpty(materialList)) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("物料编码已存在，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    } else {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("物料编码表格内重复，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * 物料名称
                 */
                String materialName = objects.get(1) == null || objects.get(1) == "" ? null : objects.get(1).toString();
                if (materialName == null) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("物料名称不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    if (materialName.length() > 300) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("物料名称长度不能大于300位，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * 物料类型
                 */
                String materialTypeName = objects.get(2) == null || objects.get(2) == "" ? null : objects.get(2).toString();
                String materialType = null;
                if (materialTypeName == null) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("物料类型不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    try {
                        ConMaterialType conMaterialType = conMaterialTypeMapper.selectOne(new QueryWrapper<ConMaterialType>().lambda()
                                .eq(ConMaterialType::getName,materialTypeName).eq(ConMaterialType::getHandleStatus, ConstantsEms.CHECK_STATUS)
                                .eq(ConMaterialType::getStatus, ConstantsEms.ENABLE_STATUS));
                        if (conMaterialType == null) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("物料类型填写错误，导入失败！");
                            errMsgList.add(errMsg);
                        } else {
                            materialType = conMaterialType.getCode();
                        }
                    } catch (Exception e) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(materialTypeName + "物料类型配置存在重复，请先检查该物料类型，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * 基本计量单位
                 */
                String unitBaseName = objects.get(3) == null || objects.get(3) == "" ? null : objects.get(3).toString();
                String unitBase = null;
                if (unitBaseName == null) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("基本计量单位不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    try {
                        ConMeasureUnit measureUnit = conMeasureUnitMapper.selectOne(new QueryWrapper<ConMeasureUnit>().lambda()
                                .eq(ConMeasureUnit::getName,unitBaseName).eq(ConMeasureUnit::getHandleStatus, ConstantsEms.CHECK_STATUS)
                                .eq(ConMeasureUnit::getStatus, ConstantsEms.ENABLE_STATUS));
                        if (measureUnit == null) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("基本计量单位填写错误，导入失败！");
                            errMsgList.add(errMsg);
                        } else {
                            unitBase = measureUnit.getCode();
                        }
                    } catch (Exception e) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(unitBaseName + "基本计量单位配置存在重复，请先检查该基本计量单位，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * BOM用量计量单位
                 */
                String unitQuantityName = objects.get(4) == null || objects.get(4) == "" ? null : objects.get(4).toString();
                String unitQuantity = null;
                if (StrUtil.isBlank(unitQuantityName) && !ConstantsEms.NO.equals(client.getIsRequiredUnitQuantityMaterial())) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("BOM用量计量单位不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else if (StrUtil.isNotBlank(unitQuantityName)) {
                    try {
                        ConMeasureUnit measureUnit = conMeasureUnitMapper.selectOne(new QueryWrapper<ConMeasureUnit>().lambda()
                                .eq(ConMeasureUnit::getName,unitQuantityName).eq(ConMeasureUnit::getHandleStatus, ConstantsEms.CHECK_STATUS)
                                .eq(ConMeasureUnit::getStatus, ConstantsEms.ENABLE_STATUS));
                        if (measureUnit == null) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("BOM用量计量单位填写错误，导入失败！");
                            errMsgList.add(errMsg);
                        } else {
                            unitQuantity = measureUnit.getCode();
                            if (StrUtil.isNotBlank(unitBase) && !unitBase.equals(unitQuantity)) {
                                warMsg = new CommonErrMsgResponse();
                                warMsg.setItemNum(num);
                                warMsg.setMsg("基本单位与BOM用量单位不同，确定是否继续?");
                                warMsgList.add(warMsg);
                            }
                        }
                    } catch (Exception e) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(unitQuantityName + "BOM用量计量单位配置存在重复，请先检查该BOM用量计量单位，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * 报价单位
                 */
                String unitPriceName = objects.get(5) == null || objects.get(5) == "" ? null : objects.get(5).toString();
                String unitPrice = null;
                if (StrUtil.isBlank(unitPriceName) && !ConstantsEms.NO.equals(client.getIsRequiredQuotePriceTaxMaterial())) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("报价单位不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else if (StrUtil.isNotBlank(unitPriceName)) {
                    try {
                        ConMeasureUnit measureUnit = conMeasureUnitMapper.selectOne(new QueryWrapper<ConMeasureUnit>().lambda()
                                .eq(ConMeasureUnit::getName,unitPriceName).eq(ConMeasureUnit::getHandleStatus, ConstantsEms.CHECK_STATUS)
                                .eq(ConMeasureUnit::getStatus, ConstantsEms.ENABLE_STATUS));
                        if (measureUnit == null) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("报价单位填写错误，导入失败！");
                            errMsgList.add(errMsg);
                        } else {
                            unitPrice = measureUnit.getCode();
                        }
                    } catch (Exception e){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(unitPriceName + "报价单位配置存在重复，请先检查该报价单位，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * 单位换算比例(基本计量单位/BOM用量)
                 */
                String unitConversionRate_s = objects.get(6) == null || objects.get(6) == "" ? null:objects.get(6).toString();
                BigDecimal unitConversionRate = null;
                if (StrUtil.isBlank(unitConversionRate_s) && !ConstantsEms.NO.equals(client.getIsRequiredUnitQuantityMaterial())) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("单位换算比例(基本计量单位/BOM用量)不可为空，导入失败！");
                    errMsgList.add(errMsg);
                } else if (StrUtil.isNotBlank(unitConversionRate_s)) {
                    if (!JudgeFormat.isValidDouble(unitConversionRate_s,4,4)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("单位换算比例(基本计量单位/BOM用量)数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        unitConversionRate = new BigDecimal(unitConversionRate_s);
                        if (unitBase != null && unitQuantity != null && unitBase.equals(unitQuantity) && !"1".equals(unitConversionRate_s)) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("单位换算比例(基本计量单位/BOM用量)必须等于1，导入失败！");
                            errMsgList.add(errMsg);
                        } else {
                            if (unitConversionRate != null && BigDecimal.ZERO.compareTo(unitConversionRate) >= 0) {
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("单位换算比例(基本计量单位/BOM用量)不能小于等于0，导入失败！");
                                errMsgList.add(errMsg);
                            }
                        }
                    }
                }
                /*
                 * 单位换算比例(报价单位/基本计量用量)
                 */
                String unitConversionRatePrice_s = objects.get(7) == null || objects.get(7) == "" ? null : objects.get(7).toString();
                BigDecimal unitConversionRatePrice = null;
                if (StrUtil.isBlank(unitConversionRatePrice_s) && !ConstantsEms.NO.equals(client.getIsRequiredQuotePriceTaxMaterial())) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("单位换算比例(报价单位/基本计量用量)不可为空，导入失败！");
                    errMsgList.add(errMsg);
                } else if (StrUtil.isNotBlank(unitConversionRatePrice_s)) {
                    if (!JudgeFormat.isValidDouble(unitConversionRatePrice_s,4,4)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("单位换算比例(报价单位/基本计量用量)数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        unitConversionRatePrice = new BigDecimal(unitConversionRatePrice_s);
                        if (unitBase != null && unitPrice != null && unitBase.equals(unitPrice) && !"1".equals(unitConversionRatePrice_s)) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("单位换算比例(报价单位/基本计量用量)必须等于1，导入失败！");
                            errMsgList.add(errMsg);
                        }else {
                            if (unitConversionRatePrice != null && BigDecimal.ZERO.compareTo(unitConversionRatePrice) >= 0) {
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("单位换算比例(报价单位/基本计量用量)不能小于等于0，导入失败！");
                                errMsgList.add(errMsg);
                            }
                        }
                    }
                }
                /*
                 * 采购类型
                 */
                String purchaseTypeName = objects.get(8) == null || objects.get(8) == "" ? null : objects.get(8).toString();
                String purchaseType = null;
                if (purchaseTypeName == null) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("采购类型不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    try {
                        QueryWrapper<ConPurchaseType> queryWrapper = new QueryWrapper<>();
                        queryWrapper.lambda().eq(ConPurchaseType::getName,purchaseTypeName)
                                .eq(ConPurchaseType::getHandleStatus, ConstantsEms.CHECK_STATUS).eq(ConPurchaseType::getStatus, ConstantsEms.ENABLE_STATUS);
                        if (StrUtil.isNotBlank(clientType) && !ConstantsEms.CLIENT_TYPE_GMYT.equals(clientType)) {
                            queryWrapper.lambda().eq(ConPurchaseType::getClientType, clientType);
                        }
                        ConPurchaseType conPurchaseType = conPurchaseTypeMapper.selectOne(queryWrapper);
                        if (conPurchaseType == null) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("采购类型配置错误，导入失败！");
                            errMsgList.add(errMsg);
                        } else {
                            purchaseType = conPurchaseType.getCode();
                        }
                    } catch (Exception e) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(purchaseTypeName + "采购类型配置存在重复，请先检查该计价计量单位，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * 是否sku物料
                 */
                String isSkuMaterialName = objects.get(9) == null || objects.get(9) == "" ? null:objects.get(9).toString();
                String isSkuMaterial = null;
                if (isSkuMaterialName == null) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("是否SKU物料不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    if (StrUtil.isBlank(isSkuMaps.get(isSkuMaterialName))) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("是否SKU物料配置错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        isSkuMaterial = isSkuMaps.get(isSkuMaterialName);
                    }
                }
                /*
                 * sku维度
                 */
                String skuDimensionName = objects.get(10) == null || objects.get(10) == "" ? null : objects.get(10).toString();
                Integer skuDimension = null;
                if (skuDimensionName != null) {
                    if (ConstantsEms.NO.equals(isSkuMaterial)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("是否SKU物料为否时，SKU维度数必须为空，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        if (StrUtil.isNotBlank(skuMaps.get(skuDimensionName))) {
                            skuDimensionName = skuMaps.get(skuDimensionName);
                            try {
                                skuDimension= Integer.parseInt(skuDimensionName);
                            } catch (Exception e) {
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("SKU维度配置错误，请联系管理员，导入失败！");
                                errMsgList.add(errMsg);
                            }
                        } else {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("SKU维度配置错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                } else {
                    if (ConstantsEms.YES.equals(isSkuMaterial)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("是否SKU物料为是时，SKU维度数必填，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * sku1类型
                 */
                String sku1TypeName = objects.get(11) == null || objects.get(11) == "" ? null : objects.get(11).toString();
                String sku1Type = null;
                if (sku1TypeName != null) {
                    if (ConstantsEms.NO.equals(isSkuMaterial)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("是否SKU物料为否时，SKU1类型必须为空，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        if (StrUtil.isBlank(skuTypeMaps.get(sku1TypeName))) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("SKU1类型配置错误，导入失败！");
                            errMsgList.add(errMsg);
                        } else {
                            sku1Type= skuTypeMaps.get(sku1TypeName);
                        }
                    }
                } else {
                    if ("1".equals(skuDimensionName)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("SKU维度为一维时，SKU1类型不允许为空，导入失败！");
                        errMsgList.add(errMsg);
                    } else if ("2".equals(skuDimensionName)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("SKU维度为二维时，SKU1类型不允许为空，导入失败！");
                        errMsgList.add(errMsg);
                    } else {}
                }
                /*
                 * sku2类型
                 */
                String sku2TypeName = objects.get(12) == null || objects.get(12) == "" ? null : objects.get(12).toString();
                String sku2Type = null;
                // 如果sku2类型有填
                if (sku2TypeName != null) {
                    // 如果不是sku物料的话就提示不能填sku2类型
                    if (ConstantsEms.NO.equals(isSkuMaterial)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("是否SKU物料为否时，SKU2类型必须为空，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        // 如果是sku物料但是sku维度是1维的，填写了sku2类型就要提示sku2类型不能填
                        if ("1".equals(skuDimensionName)) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("SKU维度为一维时，SKU2类型必须为空，导入失败！");
                            errMsgList.add(errMsg);
                        }   else {
                            // 如果是sku物料且sku维度是二维的，但是找不到sku2类型的数据字典则提示配置错误
                            if (StrUtil.isBlank(skuTypeMaps.get(sku2TypeName))) {
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("SKU2类型配置错误，导入失败！");
                                errMsgList.add(errMsg);
                            } else {
                                sku2Type= skuTypeMaps.get(sku2TypeName);
                                // 如果是sku物料且sku维度是二维的，且找得到sku2类型的数据字典，但是sku1类型和sku2类型一样的话就提示不能一样
                                if (sku1TypeName != null && sku2TypeName.equals(sku1TypeName)){
                                    errMsg = new CommonErrMsgResponse();
                                    errMsg.setItemNum(num);
                                    errMsg.setMsg("SKU1类型与SKU2类型不能一样，导入失败！");
                                    errMsgList.add(errMsg);
                                }
                                // 如果是sku物料且sku维度是二维的，且找得到sku2类型的数据字典，但是sku2类型是颜色，则提示颜色类型只能是sku1
                                if (ConstantsEms.SKUTYP_YS.equals(sku2Type)){
                                    errMsg = new CommonErrMsgResponse();
                                    errMsg.setItemNum(num);
                                    errMsg.setMsg("颜色应为SKU1类型，导入失败！");
                                    errMsgList.add(errMsg);
                                }
                            }
                        }
                    }
                } else {
                    // 如果sku维度是2维且sku2类型没有填写，则提示sku2类型不能为空
                    if ("2".equals(skuDimensionName)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("SKU维度为二维时,SKU2类型不允许为空，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * 成分
                 */
                String composition = objects.get(13) == "" || objects.get(13) == null ? null : objects.get(13).toString();
                if (composition != null && composition.length() > 180) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("成分长度不能大于180位，导入失败！");
                    errMsgList.add(errMsg);
                }
                /*
                 * 规格尺寸
                 */
                String specificationSize = objects.get(14)==null || objects.get(14) == "" ? null : objects.get(14).toString();
                if (specificationSize != null && specificationSize.length() > 180) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("规格尺寸长度不能大于180位，导入失败！");
                    errMsgList.add(errMsg);
                }
                /*
                 * 型号
                 */
                String modelSize = objects.get(15)==null || objects.get(15) == "" ? null : objects.get(15).toString();
                if (modelSize != null && modelSize.length() > 180) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("型号长度不能大于180位，导入失败！");
                    errMsgList.add(errMsg);
                }
                /*
                 * 材质
                 */
                String materialComposition = objects.get(16) == null || objects.get(16) == "" ? null : objects.get(16).toString();
                if (materialComposition != null && materialComposition.length() > 180) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("材质长度不能大于180位，导入失败！");
                    errMsgList.add(errMsg);
                }
                /*
                 * 供应商简称
                 */
                String vendorShortName = objects.get(17) == null || objects.get(17) == "" ? null : objects.get(17).toString();
                Long vendorSid = null;
                if (StrUtil.isBlank(vendorShortName) && !ConstantsEms.NO.equals(client.getIsRequiredVendorMaterial())) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("供应商简称不可为空，导入失败！");
                    errMsgList.add(errMsg);
                } else if (StrUtil.isNotBlank(vendorShortName)) {
                    try {
                        BasVendor basVendor = basVendorMapper.selectOne(new QueryWrapper<BasVendor>()
                                .lambda().eq(BasVendor::getShortName, vendorShortName));
                        if (basVendor==null) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg(vendorShortName + "对应的供应商不存在，导入失败！");
                            errMsgList.add(errMsg);
                        } else {
                            if (!ConstantsEms.ENABLE_STATUS.equals(basVendor.getStatus()) || !ConstantsEms.CHECK_STATUS.equals(basVendor.getHandleStatus())) {
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg(vendorShortName + "对应的供应商必须是确认且已启用的状态，导入失败！");
                                errMsgList.add(errMsg);
                            }
                            vendorSid = basVendor.getVendorSid();
                        }
                    } catch (Exception e) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(vendorShortName + "供应商档案存在重复，请先检查该供应商，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * 供方编码
                 */
                String supplierProductCode = objects.get(18) == null || objects.get(18) == "" ? null  : objects.get(18).toString();
                if (StrUtil.isBlank(supplierProductCode) && !ConstantsEms.NO.equals(client.getIsRequiredVendorMaterial())) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("供方编码不可为空，导入失败！");
                    errMsgList.add(errMsg);
                } else if (StrUtil.isNotBlank(supplierProductCode)) {
                    if (supplierProductCode.length() > 30){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("供方编码长度不能大于30位，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                //"物料编码：" + codes + " 在系统中已存在相同“供应商/供方编码”的物料档案，确定是否继续?"
                if (vendorSid != null && supplierProductCode != null) {
                    //表格中
                    if (vendorMap.get(vendorSid.toString() + supplierProductCode)==null) {
                        vendorMap.put(vendorSid.toString() + supplierProductCode,String.valueOf(num));
                        // 去系统
                        List<BasMaterial> list = basMaterialMapper.selectList(new QueryWrapper<BasMaterial>().lambda().eq(BasMaterial::getVendorSid, vendorSid)
                                .eq(BasMaterial::getSupplierProductCode, supplierProductCode));
                        if (CollectionUtils.isNotEmpty(list)){
                            warMsg = new CommonErrMsgResponse();
                            warMsg.setItemNum(num);
                            warMsg.setMsg("系统中已存在相同“供应商/供方编码”的物料档案，确定是否继续?");
                            warMsgList.add(warMsg);
                        }
                    }else{
                        warMsg = new CommonErrMsgResponse();
                        warMsg.setItemNum(num);
                        warMsg.setMsg("表格中已存在相同“供应商/供方编码”的物料档案，确定是否继续?");
                        warMsgList.add(warMsg);
                    }
                }
                /*
                 * 客户简称
                 */
                String customerShortName = objects.get(19) == null || objects.get(19) == "" ? null : objects.get(19).toString();
                Long customerSid = null;
                if (customerShortName != null) {
                    try {
                        BasCustomer basCustomer = basCustomerMapper.selectOne(new QueryWrapper<BasCustomer>()
                                .lambda().eq(BasCustomer::getShortName, customerShortName));
                        if (basCustomer == null) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg(customerShortName + "对应的客户不存在，导入失败！");
                            errMsgList.add(errMsg);
                        } else {
                            if (!ConstantsEms.ENABLE_STATUS.equals(basCustomer.getStatus()) || !ConstantsEms.CHECK_STATUS.equals(basCustomer.getHandleStatus())) {
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg(customerShortName + "对应的客户必须是确认且已启用的状态，导入失败！");
                                errMsgList.add(errMsg);
                            }
                            customerSid = basCustomer.getCustomerSid();
                        }
                    } catch (Exception e) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(customerShortName + "客户档案存在重复，请先检查该客户，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * 报价(元/人民币)
                 */
                String quotePriceTax_s = objects.get(20) == null || objects.get(20) == "" ? null : objects.get(20).toString();
                BigDecimal quotePriceTax = null;
                if (StrUtil.isBlank(quotePriceTax_s) && !ConstantsEms.NO.equals(client.getIsRequiredQuotePriceTaxMaterial())) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("报价(元/人民币)不可为空，导入失败！");
                    errMsgList.add(errMsg);
                }
                else if (StrUtil.isNotBlank(quotePriceTax_s)) {
                    if (!JudgeFormat.isValidDouble(quotePriceTax_s,11,4)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("报价(元/人民币)数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        quotePriceTax = new BigDecimal(quotePriceTax_s);
                        if (quotePriceTax != null && BigDecimal.ZERO.compareTo(quotePriceTax) > 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("报价(元/人民币)不能小于0，导入失败！");
                            errMsgList.add(errMsg);
                        }
                        quotePriceTax = quotePriceTax.divide(BigDecimal.ONE,5,BigDecimal.ROUND_HALF_UP);
                    }
                }
                BasMaterial basMaterial = new BasMaterial();
                List<BasMaterialSku> basMaterialSkuList = new ArrayList<>();
                /*
                 * SKU1名称
                 */
                String sku1Names = objects.get(21) == null || objects.get(21) == "" ? null : objects.get(21).toString();
                if (sku1Names != null) {
                    if (ConstantsEms.NO.equals(isSkuMaterial)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("非SKU物料，SKU1名称应为空，导入失败！");
                        errMsgList.add(errMsg);
                    } else if (ConstantsEms.YES.equals(isSkuMaterial)) {
                        if (sku1TypeName == null) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("SKU1类型为空时，SKU1名称应为空，导入失败！");
                            errMsgList.add(errMsg);
                        } else {
                            String[] skus = sku1Names.split(";|；");
                            Set<String> staffsSet = new HashSet<>(Arrays.asList(skus));
                            for (String s : staffsSet) {
                                BasSku basSku = null;
                                try {
                                    basSku = basSkuMapper.selectOne(new QueryWrapper<BasSku>().lambda().eq(BasSku::getSkuType, sku1Type == null ? sku1TypeName : sku1Type)
                                            .eq(BasSku::getSkuName, s));
                                    if (basSku == null){
                                        errMsg = new CommonErrMsgResponse();
                                        errMsg.setItemNum(num);
                                        errMsg.setMsg("找不到SKU类型为" + sku1TypeName + "的 " + s + " 档案，导入失败！");
                                        errMsgList.add(errMsg);
                                    } else {
                                        if (!(ConstantsEms.CHECK_STATUS.equals(basSku.getHandleStatus()) && ConstantsEms.ENABLE_STATUS.equals(basSku.getStatus()))) {
                                            errMsg = new CommonErrMsgResponse();
                                            errMsg.setItemNum(num);
                                            errMsg.setMsg("SKU类型为" + sku1TypeName + "的 " + s +" 档案必须是确认且已启用，导入失败！");
                                            errMsgList.add(errMsg);
                                        } else {
                                            BasMaterialSku materialSku = new BasMaterialSku();
                                            materialSku.setSkuSid(basSku.getSkuSid()).setSkuType(basSku.getSkuType());
                                            materialSku.setStatus(ConstantsEms.ENABLE_STATUS).setCreateDate(new Date()).setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
                                            basMaterialSkuList.add(materialSku);
                                        }
                                    }
                                } catch (Exception e) {
                                    errMsg = new CommonErrMsgResponse();
                                    errMsg.setItemNum(num);
                                    errMsg.setMsg("SKU类型为" + sku1TypeName + "的 " + s +" 档案存在重复，请先检查该" + sku1TypeName + "，导入失败！");
                                    errMsgList.add(errMsg);
                                }
                            }
                        }
                    }
                }
                /*
                 * SKU2名称
                 */
                String sku2Names = objects.get(22) == null || objects.get(22) == "" ? null : objects.get(22).toString();
                if (sku2Names != null) {
                    if (ConstantsEms.NO.equals(isSkuMaterial)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("非SKU物料，SKU2名称应为空，导入失败！");
                        errMsgList.add(errMsg);
                    } else if (ConstantsEms.YES.equals(isSkuMaterial)) {
                        if ("1".equals(skuDimensionName)){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("SKU维度为一维时，SKU2名称应为空，导入失败！");
                            errMsgList.add(errMsg);
                        } else if ("2".equals(skuDimensionName) && sku2TypeName != null) {
                            String[] skus = sku2Names.split(";|；");
                            Set<String> staffsSet = new HashSet<>(Arrays.asList(skus));
                            for (String s : staffsSet) {
                                BasSku basSku = null;
                                try {
                                    basSku = basSkuMapper.selectOne(new QueryWrapper<BasSku>().lambda().eq(BasSku::getSkuType,sku2Type == null ? sku2TypeName : sku2Type)
                                            .eq(BasSku::getSkuName, s));
                                    if (basSku == null) {
                                        errMsg = new CommonErrMsgResponse();
                                        errMsg.setItemNum(num);
                                        errMsg.setMsg("找不到SKU类型为" + sku2TypeName + "的 " + s +" 档案，导入失败！");
                                        errMsgList.add(errMsg);
                                    } else {
                                        if (!(ConstantsEms.CHECK_STATUS.equals(basSku.getHandleStatus()) && ConstantsEms.ENABLE_STATUS.equals(basSku.getStatus()))) {
                                            errMsg = new CommonErrMsgResponse();
                                            errMsg.setItemNum(num);
                                            errMsg.setMsg("SKU类型为" + sku2TypeName + "的 " + s + " 档案必须是确认且已启用的状态，导入失败！");
                                            errMsgList.add(errMsg);
                                        } else {
                                            BasMaterialSku materialSku = new BasMaterialSku();
                                            materialSku.setSkuSid(basSku.getSkuSid()).setSkuType(basSku.getSkuType());
                                            materialSku.setStatus(ConstantsEms.ENABLE_STATUS).setCreateDate(new Date()).setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
                                            basMaterialSkuList.add(materialSku);
                                        }
                                    }
                                } catch (Exception e) {
                                    errMsg = new CommonErrMsgResponse();
                                    errMsg.setItemNum(num);
                                    errMsg.setMsg("SKU类型为" + sku2TypeName + "的 " + s + " 档案存在重复，请先检查该" + sku2TypeName + "，导入失败！");
                                    errMsgList.add(errMsg);
                                }
                            }
                        }
                    }
                }
                /*
                 * 备注
                 */
                String remark = objects.get(23) == null || objects.get(23) == "" ? null : objects.get(23).toString();
                if (remark != null && remark.length() > 600) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("备注长度不能大于600位，导入失败！");
                    errMsgList.add(errMsg);
                }
                if (CollectionUtil.isEmpty(errMsgList)) {
                    basMaterial.setBasMaterialSkuList(basMaterialSkuList);
                    basMaterial.setMaterialName(materialName);
                    basMaterial.setMaterialCode(materialCode);
                    basMaterial.setSupplierProductCode(supplierProductCode);
                    basMaterial.setMaterialType(ConstantsEms.MATERIAL_F)
                            .setUnitBase(unitBase).setUnitQuantity(unitQuantity).setInventoryPriceMethod(ConstantsEms.INVENTORY_PRICE_METHOD_JQPJJ)
                            .setUnitPrice(unitPrice).setUnitConversionRatePrice(unitConversionRatePrice).setUnitConversionRate(unitConversionRate)
                            .setPurchaseType(purchaseType).setIndustryField(ConstantsEms.INDUSTRY_FIELD_XIEF)
                            .setIsSkuMaterial(isSkuMaterial).setSkuDimension(skuDimension).setSku1Type(sku1Type).setSku2Type(sku2Type)
                            .setStatus(ConstantsEms.ENABLE_STATUS).setHandleStatus(ConstantsEms.SAVA_STATUS)
                            .setComposition(composition).setSpecificationSize(specificationSize)
                            .setModelSize(modelSize).setMaterialComposition(materialComposition)
                            .setVendorSid(vendorSid).setCustomerSid(customerSid).setQuotePriceTax(quotePriceTax)
                            .setRemark(remark).setMaterialCategory(ConstantsEms.MATERIAL_CATEGORY_WL)
                            .setImportType(BusinessType.IMPORT.getValue());
                    basMaterialList.add(basMaterial);
                }
            }
        } catch (BaseException e) {
            throw new BaseException(e.getDefaultMessage());
        }
        //检查有没有报错
        if (CollectionUtil.isNotEmpty(errMsgList)) {
            res.put("errList", errMsgList);
            res.put("warn", null);
            res.put("tableData", null);
            return res;
        } else if (CollectionUtil.isNotEmpty(warMsgList)) {
            res.put("errList", warMsgList);
            res.put("warn", true);
            res.put("tableData", basMaterialList);
            return res;
        } else {
            if (CollectionUtils.isNotEmpty(basMaterialList)) {
                basMaterialList.forEach(item -> {
                    basMaterialService.insertBasMaterial(item);
                });
            }
            res.put("success", basMaterialList.size());
            return res;
        }
    }

    /**
     * 物料-辅料
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public HashMap<String, Object> importData(MultipartFile file){
        HashMap<String, Object> res = new HashMap<>();
        String vendorJudge = null;
        List<BasMaterial> basMaterialList=new ArrayList<>();
        //错误信息
        List<CommonErrMsgResponse> errMsgList = new ArrayList<>();
        List<CommonErrMsgResponse> warMsgList = new ArrayList<>();
        CommonErrMsgResponse errMsg = null;
        CommonErrMsgResponse warMsg = null;
        try{
            File toFile=null;
            try {
                toFile   = FileUtils.multipartFileToFile(file);
            }catch (Exception e){
                e.getMessage();
                throw new BaseException("文件转换失败");
            }
            ExcelReader reader = cn.hutool.poi.excel.ExcelUtil.getReader(toFile);
            FileUtils.delteTempFile(toFile);
            List<List<Object>> readAll = reader.read();
            // 物料类型
            List<ConMaterialType> conMaterialTypeList = conMaterialTypeMapper.getConMaterialTypeList();
            conMaterialTypeList = conMaterialTypeList.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String,String> materialTypeMaps=conMaterialTypeList.stream().collect(Collectors.toMap(ConMaterialType::getName, ConMaterialType::getCode,(key1, key2)->key2));
            // 计量单位
            List<ConMeasureUnit> conMeasureUnitList = conMeasureUnitMapper.getConMeasureUnitList();
            conMeasureUnitList = conMeasureUnitList.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String,String> measureUnitMaps=conMeasureUnitList.stream().collect(Collectors.toMap(ConMeasureUnit::getName, ConMeasureUnit::getCode,(key1, key2)->key2));
            // 是否sku物料
            List<DictData> isSkuList=sysDictDataService.selectDictData("sys_yes_no");
            isSkuList = isSkuList.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String,String> isSkuMaps=isSkuList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue,(key1, key2)->key2));
            // sku维度
            List<DictData> skuList=sysDictDataService.selectDictData("s_sku_dimension");
            skuList = skuList.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String,String> skuMaps=skuList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue,(key1, key2)->key2));
            // sku类型
            List<DictData> skuTypeList=sysDictDataService.selectDictData("s_sku_type");
            skuTypeList = skuTypeList.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String,String> skuTypeMaps=skuTypeList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue,(key1, key2)->key2));
            // 拉链标识
            List<DictData> zipperList=sysDictDataService.selectDictData("s_zipper_flag");
            zipperList = zipperList.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String,String> zipperMaps=zipperList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue,(key1, key2)->key2));
            // 采购类型 根据用户所在租户的租户类型判断
            String clientType = ApiThreadLocalUtil.get().getSysUser().getClientType();
            //excel表里面编码和名称的缓存
            HashMap<String, String> codeMap = new HashMap<>();
            HashMap<String, String> nameMap = new HashMap<>();
            HashMap<String, String> vendorMap = new HashMap<>();
            com.platform.common.core.domain.entity.SysDefaultSettingClient client = ApiThreadLocalUtil.get().getSysUser().getClient();
            for (int i = 0; i < readAll.size(); i++) {
                if(i<2){
                    //前两行跳过
                    continue;
                }
                int num=i+1;
                List<Object> objects=readAll.get(i);
                copy( objects, readAll);
                /*
                 * 物料编码
                 */
                String materialCode = objects.get(0)==null||objects.get(0)==""?null:objects.get(0).toString();
                if(materialCode == null){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("物料编码不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    if (materialCode.contains(" ")){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("物料编码数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        if (materialCode.length() > 20){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("物料编码长度不能大于20位，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                    // 判断是否与表格内的编码重复
                    if (codeMap.get(materialCode) == null){
                        codeMap.put(materialCode,String.valueOf(num));
                        // 如果表格内没重复则判断与数据库之间是否存在重复
                        List<BasMaterial> materialList = basMaterialMapper.selectList(new QueryWrapper<BasMaterial>().lambda()
                                .eq(BasMaterial::getMaterialCode,materialCode));
                        if (CollectionUtil.isNotEmpty(materialList)){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("物料编码重复，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }else {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("物料编码重复，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * 物料名称
                 */
                String materialName = objects.get(1)==null||objects.get(1)==""?null:objects.get(1).toString();
                if(materialName == null){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("物料名称不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    if (materialName.length() > 300){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("物料名称长度不能大于300位，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * 物料类型
                 */
                String materialTypeName = objects.get(2)==null||objects.get(2)==""?null:objects.get(2).toString();
                String materialType = null;
                if(materialTypeName == null){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("物料类型不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    try {
                        ConMaterialType conMaterialType = conMaterialTypeMapper.selectOne(new QueryWrapper<ConMaterialType>().lambda()
                                .eq(ConMaterialType::getName,materialTypeName));
                        if(conMaterialType == null){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("物料类型配置错误，导入失败！");
                            errMsgList.add(errMsg);
                        }else {
                            if (!ConstantsEms.MATERIAL_F.equals(conMaterialType.getCode())){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("对应的物料类型必须是辅料，导入失败！");
                                errMsgList.add(errMsg);
                            }
                            materialType = conMaterialType.getCode();
                        }
                    }catch (Exception e){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(materialTypeName +"物料类型档案存在重复，请先检查该物料类型，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * 基本计量单位
                 */
                String unitBaseName = objects.get(3)==null||objects.get(3)==""?null:objects.get(3).toString();
                String unitBase = null;
                if(unitBaseName == null){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("基本计量单位不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    try {
                        ConMeasureUnit measureUnit = conMeasureUnitMapper.selectOne(new QueryWrapper<ConMeasureUnit>().lambda()
                                .eq(ConMeasureUnit::getName,unitBaseName));
                        if(measureUnit == null){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("基本计量单位配置错误，导入失败！");
                            errMsgList.add(errMsg);
                        }else {
                            if (!ConstantsEms.CHECK_STATUS.equals(measureUnit.getHandleStatus()) || !ConstantsEms.ENABLE_STATUS.equals(measureUnit.getStatus())){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("对应的基本计量单位必须是确认且已启用的状态，导入失败！");
                                errMsgList.add(errMsg);
                            }
                            unitBase = measureUnit.getCode();
                        }
                    }catch (Exception e){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(unitBaseName +"基本计量单位档案存在重复，请先检查该基本计量单位，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * BOM用量计量单位
                 */
                String unitQuantityName = objects.get(4)==null||objects.get(4)==""?null:objects.get(4).toString();
                String unitQuantity = null;
                if(StrUtil.isBlank(unitQuantityName) && !ConstantsEms.NO.equals(client.getIsRequiredUnitQuantityMaterial())) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("BOM用量计量单位不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else if (StrUtil.isNotBlank(unitQuantityName)) {
                    try {
                        ConMeasureUnit measureUnit = conMeasureUnitMapper.selectOne(new QueryWrapper<ConMeasureUnit>().lambda()
                                .eq(ConMeasureUnit::getName,unitQuantityName));
                        if(measureUnit == null){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("BOM用量计量单位配置错误，导入失败！");
                            errMsgList.add(errMsg);
                        }else {
                            if (!ConstantsEms.CHECK_STATUS.equals(measureUnit.getHandleStatus()) || !ConstantsEms.ENABLE_STATUS.equals(measureUnit.getStatus())){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("对应的BOM用量计量单位必须是确认且已启用的状态，导入失败！");
                                errMsgList.add(errMsg);
                            }
                            unitQuantity = measureUnit.getCode();
                            if (StrUtil.isNotBlank(unitBase) && !unitBase.equals(unitQuantity)) {
                                warMsg = new CommonErrMsgResponse();
                                warMsg.setItemNum(num);
                                warMsg.setMsg("基本单位与BOM用量单位不同，确定是否继续?");
                                warMsgList.add(warMsg);
                            }
                        }
                    }catch (Exception e){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(unitQuantityName +"BOM用量计量单位档案存在重复，请先检查该BOM用量计量单位，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * 报价单位
                 */
                String unitPriceName = objects.get(5)==null||objects.get(5)==""?null:objects.get(5).toString();
                String unitPrice = null;
                if(StrUtil.isBlank(unitPriceName) && !ConstantsEms.NO.equals(client.getIsRequiredQuotePriceTaxMaterial())) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("报价单位不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else if (StrUtil.isNotBlank(unitPriceName)) {
                    try {
                        ConMeasureUnit measureUnit = conMeasureUnitMapper.selectOne(new QueryWrapper<ConMeasureUnit>().lambda()
                                .eq(ConMeasureUnit::getName,unitPriceName));
                        if(measureUnit == null){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("报价单位配置错误，导入失败！");
                            errMsgList.add(errMsg);
                        }else {
                            if (!ConstantsEms.CHECK_STATUS.equals(measureUnit.getHandleStatus()) || !ConstantsEms.ENABLE_STATUS.equals(measureUnit.getStatus())){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("对应的报价单位必须是确认且已启用的状态，导入失败！");
                                errMsgList.add(errMsg);
                            }
                            unitPrice = measureUnit.getCode();
                        }
                    }catch (Exception e){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(unitPriceName +"报价单位档案存在重复，请先检查该报价单位，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * 单位换算比例(基本计量单位/BOM用量)
                 */
                String unitConversionRate_s = objects.get(6)==null||objects.get(6)==""?null:objects.get(6).toString();
                BigDecimal unitConversionRate = null;
                if (StrUtil.isBlank(unitConversionRate_s) && !ConstantsEms.NO.equals(client.getIsRequiredUnitQuantityMaterial())) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("单位换算比例(基本计量单位/BOM用量)不可为空，导入失败！");
                    errMsgList.add(errMsg);
                } else if (StrUtil.isNotBlank(unitConversionRate_s)) {
                    if (!JudgeFormat.isValidDouble(unitConversionRate_s,4,4)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("单位换算比例(基本计量单位/BOM用量)数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        unitConversionRate = new BigDecimal(unitConversionRate_s);
                        if (unitBase != null && unitQuantity != null && unitBase.equals(unitQuantity) && !"1".equals(unitConversionRate_s)){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("单位换算比例(基本计量单位/BOM用量)必须等于1，导入失败！");
                            errMsgList.add(errMsg);
                        }else {
                            if (unitConversionRate != null && BigDecimal.ZERO.compareTo(unitConversionRate) >= 0) {
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("单位换算比例(基本计量单位/BOM用量)不能小于等于0，导入失败！");
                                errMsgList.add(errMsg);
                            }
                        }
                    }
                }
                /*
                 * 单位换算比例(报价单位/基本计量用量)
                 */
                String unitConversionRatePrice_s = objects.get(7)==null||objects.get(7)==""?null:objects.get(7).toString();
                BigDecimal unitConversionRatePrice = null;
                if (StrUtil.isBlank(unitConversionRatePrice_s) && !ConstantsEms.NO.equals(client.getIsRequiredQuotePriceTaxMaterial())) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("单位换算比例(报价单位/基本计量用量)不可为空，导入失败！");
                    errMsgList.add(errMsg);
                } else if (StrUtil.isNotBlank(unitConversionRatePrice_s)) {
                    if (!JudgeFormat.isValidDouble(unitConversionRatePrice_s,4,4)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("单位换算比例(报价单位/基本计量用量)数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        unitConversionRatePrice = new BigDecimal(unitConversionRatePrice_s);
                        if (unitBase != null && unitPrice != null && unitBase.equals(unitPrice) && !"1".equals(unitConversionRatePrice_s)){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("单位换算比例(报价单位/基本计量用量)必须等于1，导入失败！");
                            errMsgList.add(errMsg);
                        }else {
                            if (unitConversionRatePrice != null && BigDecimal.ZERO.compareTo(unitConversionRatePrice) >= 0) {
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("单位换算比例(报价单位/基本计量用量)不能小于等于0，导入失败！");
                                errMsgList.add(errMsg);
                            }
                        }
                    }
                }
                /*
                 * 采购类型
                 */
                String purchaseTypeName = objects.get(8)==null||objects.get(8)==""?null:objects.get(8).toString();
                String purchaseType = null;
                if(purchaseTypeName == null){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("采购类型不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    try {
                        QueryWrapper<ConPurchaseType> queryWrapper = new QueryWrapper<>();
                        queryWrapper.lambda().eq(ConPurchaseType::getName,purchaseTypeName);
                        if (StrUtil.isNotBlank(clientType) && !ConstantsEms.CLIENT_TYPE_GMYT.equals(clientType)){
                            queryWrapper.lambda().eq(ConPurchaseType::getClientType,clientType);
                        }
                        ConPurchaseType conPurchaseType = conPurchaseTypeMapper.selectOne(queryWrapper);
                        if(conPurchaseType == null){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("采购类型配置错误，导入失败！");
                            errMsgList.add(errMsg);
                        }else {
                            if (!ConstantsEms.CHECK_STATUS.equals(conPurchaseType.getHandleStatus()) || !ConstantsEms.ENABLE_STATUS.equals(conPurchaseType.getStatus())){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("对应的采购类型必须是确认且已启用的状态，导入失败！");
                                errMsgList.add(errMsg);
                            }
                            purchaseType = conPurchaseType.getCode();
                        }
                    }catch (Exception e){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(purchaseTypeName +"采购类型档案存在重复，请先检查该计价计量单位，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * 是否sku物料
                 */
                String isSkuMaterialName = objects.get(9)==null||objects.get(9)==""?null:objects.get(9).toString();
                String isSkuMaterial = null;
                if(isSkuMaterialName == null){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("是否SKU物料不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    if(StrUtil.isBlank(isSkuMaps.get(isSkuMaterialName))){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("是否SKU物料配置错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        isSkuMaterial=isSkuMaps.get(isSkuMaterialName);
                    }
                }
                /*
                 * sku维度
                 */
                String skuDimensionName = objects.get(10)==null||objects.get(10)==""?null:objects.get(10).toString();
                Integer skuDimension = null;
                if (skuDimensionName != null){
                    if (ConstantsEms.NO.equals(isSkuMaterial)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("是否SKU物料为否时，SKU维度数必须为空，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        if (StrUtil.isNotBlank(skuMaps.get(skuDimensionName))){
                            skuDimensionName = skuMaps.get(skuDimensionName);
                            try {
                                skuDimension= Integer.parseInt(skuDimensionName);
                            }catch (Exception e){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("SKU维度配置错误，请联系管理员，导入失败！");
                                errMsgList.add(errMsg);
                            }
                        }else {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("SKU维度配置错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                } else {
                    if (ConstantsEms.YES.equals(isSkuMaterial)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("是否SKU物料为是时，SKU维度数必填，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * sku1类型
                 */
                String sku1TypeName = objects.get(11)==null||objects.get(11)==""?null:objects.get(11).toString();
                String sku1Type = null;
                if(sku1TypeName != null) {
                    if (ConstantsEms.NO.equals(isSkuMaterial)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("是否SKU物料为否时，SKU1类型必须为空，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        if(StrUtil.isBlank(skuTypeMaps.get(sku1TypeName))){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("SKU1类型配置错误，导入失败！");
                            errMsgList.add(errMsg);
                        }else {
                            sku1Type= skuTypeMaps.get(sku1TypeName);
                        }
                    }
                }else {
                    if ("1".equals(skuDimensionName)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("SKU维度为一维时，SKU1类型不允许为空，导入失败！");
                        errMsgList.add(errMsg);
                    }else if ("2".equals(skuDimensionName)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("SKU维度为二维时，SKU1类型不允许为空，导入失败！");
                        errMsgList.add(errMsg);
                    }else {}
                }
                /*
                 * sku2类型
                 */
                String sku2TypeName = objects.get(12)==null||objects.get(12)==""?null:objects.get(12).toString();
                String sku2Type = null;
                //如果sku2类型有填
                if(sku2TypeName != null) {
                    // 如果不是sku物料的话就提示不能填sku2类型
                    if (ConstantsEms.NO.equals(isSkuMaterial)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("是否SKU物料为否时，SKU2类型必须为空，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        // 如果是sku物料但是sku维度是1维的，填写了sku2类型就要提示sku2类型不能填
                        if ("1".equals(skuDimensionName)) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("SKU维度为一维时，SKU2类型必须为空，导入失败！");
                            errMsgList.add(errMsg);
                        }else {
                            // 如果是sku物料且sku维度是二维的，但是找不到sku2类型的数据字典则提示配置错误
                            if(StrUtil.isBlank(skuTypeMaps.get(sku2TypeName))){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("SKU2类型配置错误，导入失败！");
                                errMsgList.add(errMsg);
                            }else {
                                sku2Type= skuTypeMaps.get(sku2TypeName);
                                // 如果是sku物料且sku维度是二维的，且找得到sku2类型的数据字典，但是sku1类型和sku2类型一样的话就提示不能一样
                                if (sku1TypeName != null && sku2TypeName.equals(sku1TypeName)){
                                    errMsg = new CommonErrMsgResponse();
                                    errMsg.setItemNum(num);
                                    errMsg.setMsg("SKU1类型与SKU2类型不能一样，导入失败！");
                                    errMsgList.add(errMsg);
                                }
                                // 如果是sku物料且sku维度是二维的，且找得到sku2类型的数据字典，但是sku2类型是颜色，则提示颜色类型只能是sku1
                                if (ConstantsEms.SKUTYP_YS.equals(sku2Type)){
                                    errMsg = new CommonErrMsgResponse();
                                    errMsg.setItemNum(num);
                                    errMsg.setMsg("颜色应为SKU1类型，导入失败！");
                                    errMsgList.add(errMsg);
                                }
                            }
                        }
                    }
                }else {
                    // 如果sku维度是2维且sku2类型没有填写，则提示sku2类型不能为空
                    if ("2".equals(skuDimensionName)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("SKU维度为二维时,SKU2类型不允许为空，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * 成分
                 */
                String composition = objects.get(13)==""||objects.get(13)==null?null:objects.get(13).toString();
                if (composition != null && composition.length() > 180){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("成分长度不能大于180位，导入失败！");
                    errMsgList.add(errMsg);
                }
                /*
                 * 密度
                 */
                String density = objects.get(14)==null||objects.get(14)==""?null:objects.get(14).toString();
                if (density != null && density.length() > 180){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("密度长度不能大于180位，导入失败！");
                    errMsgList.add(errMsg);
                }
                /*
                 * 规格尺寸
                 */
                String specificationSize = objects.get(15)==null||objects.get(15)==""?null:objects.get(15).toString();
                if (specificationSize != null && specificationSize.length() > 180){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("规格尺寸长度不能大于180位，导入失败！");
                    errMsgList.add(errMsg);
                }
                /*
                 * 型号
                 */
                String modelSize = objects.get(16)==null||objects.get(16)==""?null:objects.get(16).toString();
                if (modelSize != null && modelSize.length() > 180){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("型号长度不能大于180位，导入失败！");
                    errMsgList.add(errMsg);
                }
                /*
                 * 材质
                 */
                String materialComposition = objects.get(17)==null||objects.get(17)==""?null:objects.get(17).toString();
                if (materialComposition != null && materialComposition.length() > 180){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("材质长度不能大于180位，导入失败！");
                    errMsgList.add(errMsg);
                }
                /*
                 * 口型
                 */
                String zipperMonth = objects.get(18)==null||objects.get(18)==""?null:objects.get(18).toString();
                if (zipperMonth != null && zipperMonth.length() > 180){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("口型长度不能大于180位，导入失败！");
                    errMsgList.add(errMsg);
                }
                /*
                 * 号型
                 */
                String zipperSize = objects.get(19)==null||objects.get(19)==""?null:objects.get(19).toString();
                if (zipperSize != null && zipperSize.length() > 180){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("号型长度不能大于180位，导入失败！");
                    errMsgList.add(errMsg);
                }
                /*
                 * 工艺说明
                 */
                String processDesc = objects.get(20)==null||objects.get(20)==""?null:objects.get(20).toString();
                if (processDesc != null && processDesc.length() > 600){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("工艺说明长度不能大于600位，导入失败！");
                    errMsgList.add(errMsg);
                }
                /*
                 * 供应商简称
                 */
                String vendorShortName = objects.get(21)==null||objects.get(21)==""?null:objects.get(21).toString();
                Long vendorSid = null;
                if (StrUtil.isBlank(vendorShortName) && !ConstantsEms.NO.equals(client.getIsRequiredVendorMaterial())) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("供应商简称不可为空，导入失败！");
                    errMsgList.add(errMsg);
                } else if (StrUtil.isNotBlank(vendorShortName)) {
                    try {
                        BasVendor basVendor = basVendorMapper.selectOne(new QueryWrapper<BasVendor>()
                                .lambda().eq(BasVendor::getShortName, vendorShortName));
                        if(basVendor==null){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("对应的供应商简称不存在，导入失败！");
                            errMsgList.add(errMsg);
                        }else{
                            if (!ConstantsEms.ENABLE_STATUS.equals(basVendor.getStatus()) || !ConstantsEms.CHECK_STATUS.equals(basVendor.getHandleStatus())){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("对应的供应商简称必须是确认且已启用的状态，导入失败！");
                                errMsgList.add(errMsg);
                            }
                            vendorSid = basVendor.getVendorSid();
                        }
                    }catch (Exception e){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(vendorShortName +"供应商档案存在重复，请先检查该供应商，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * 供方编码
                 */
                String supplierProductCode = objects.get(22)==null||objects.get(22)==""?null:objects.get(22).toString();
                if (StrUtil.isBlank(supplierProductCode) && !ConstantsEms.NO.equals(client.getIsRequiredVendorMaterial())) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("供方编码不可为空，导入失败！");
                    errMsgList.add(errMsg);
                } else if (StrUtil.isNotBlank(supplierProductCode)) {
                    if (supplierProductCode.length() > 30){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("供方编码长度不能大于30位，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                //"物料编码：" + codes + " 在系统中已存在相同“供应商/供方编码”的物料档案，确定是否继续?"
                if (vendorSid != null && supplierProductCode != null){
                    //表格中
                    if (vendorMap.get(vendorSid.toString()+supplierProductCode)==null){
                        vendorMap.put(vendorSid.toString()+supplierProductCode,String.valueOf(num));
                        // 去系统
                        List<BasMaterial> list = basMaterialMapper.selectList(new QueryWrapper<BasMaterial>().lambda().eq(BasMaterial::getVendorSid,vendorSid)
                                .eq(BasMaterial::getSupplierProductCode,supplierProductCode));
                        if (CollectionUtils.isNotEmpty(list)){
                            warMsg = new CommonErrMsgResponse();
                            warMsg.setItemNum(num);
                            warMsg.setMsg("系统中已存在相同“供应商/供方编码”的物料档案，确定是否继续?");
                            warMsgList.add(warMsg);
                            vendorJudge = "Y";
                        }
                    }else{
                        warMsg = new CommonErrMsgResponse();
                        warMsg.setItemNum(num);
                        warMsg.setMsg("表格中已存在相同“供应商/供方编码”的物料档案，确定是否继续?");
                        warMsgList.add(warMsg);
                        vendorJudge = "Y";
                    }
                }
                /*
                 * 客户简称
                 */
                String customerShortName = objects.get(23)==null||objects.get(23)==""?null:objects.get(23).toString();
                Long customerSid = null;
                if (customerShortName != null){
                    try {
                        BasCustomer basCustomer = basCustomerMapper.selectOne(new QueryWrapper<BasCustomer>()
                                .lambda().eq(BasCustomer::getShortName, customerShortName));
                        if(basCustomer==null){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("对应的客户简称不存在，导入失败！");
                            errMsgList.add(errMsg);
                        }else{
                            if (!ConstantsEms.ENABLE_STATUS.equals(basCustomer.getStatus()) || !ConstantsEms.CHECK_STATUS.equals(basCustomer.getHandleStatus())){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("对应的客户简称必须是确认且已启用的状态，导入失败！");
                                errMsgList.add(errMsg);
                            }
                            customerSid = basCustomer.getCustomerSid();
                        }
                    }catch (Exception e){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(customerShortName +"客户档案存在重复，请先检查该客户，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * 报价(元/人民币)
                 */
                String quotePriceTax_s = objects.get(24)==null||objects.get(24)==""?null:objects.get(24).toString();
                BigDecimal quotePriceTax = null;
                if (StrUtil.isBlank(quotePriceTax_s) && !ConstantsEms.NO.equals(client.getIsRequiredQuotePriceTaxMaterial())) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("报价(元/人民币)不可为空，导入失败！");
                    errMsgList.add(errMsg);
                }
                else if (StrUtil.isNotBlank(quotePriceTax_s)) {
                    if (!JudgeFormat.isValidDouble(quotePriceTax_s,11,4)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("报价(元/人民币)数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        quotePriceTax = new BigDecimal(quotePriceTax_s);
                        if (quotePriceTax != null && BigDecimal.ZERO.compareTo(quotePriceTax) > 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("报价(元/人民币)不能小于0，导入失败！");
                            errMsgList.add(errMsg);
                        }
                        quotePriceTax=quotePriceTax.divide(BigDecimal.ONE,5,BigDecimal.ROUND_HALF_UP);
                    }
                }
                BasMaterial basMaterial = new BasMaterial();
                List<BasMaterialSku> basMaterialSkuList = new ArrayList<>();
                /*
                 * SKU1名称
                 */
                String sku1Names = objects.get(25)==null||objects.get(25)==""?null:objects.get(25).toString();
                if (sku1Names != null){
                    if (ConstantsEms.NO.equals(isSkuMaterial)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("非SKU物料，SKU1名称应为空，导入失败！");
                        errMsgList.add(errMsg);
                    } else if(ConstantsEms.YES.equals(isSkuMaterial)) {
                        if (sku1TypeName == null){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("SKU1类型为空时，SKU1名称应为空，导入失败！");
                            errMsgList.add(errMsg);
                        } else {
                            String[] skus = sku1Names.split(";|；");
                            Set<String> staffsSet = new HashSet<>(Arrays.asList(skus));
                            for (String s : staffsSet) {
                                BasSku basSku = null;
                                try {
                                    basSku = basSkuMapper.selectOne(new QueryWrapper<BasSku>().lambda().eq(BasSku::getSkuType,sku1Type==null?sku1TypeName:sku1Type)
                                            .eq(BasSku::getSkuName, s));
                                    if (basSku == null){
                                        errMsg = new CommonErrMsgResponse();
                                        errMsg.setItemNum(num);
                                        errMsg.setMsg("找不到SKU类型为" + sku1TypeName + "的 " + s +" 档案，导入失败！");
                                        errMsgList.add(errMsg);
                                    } else {
                                        if (!(ConstantsEms.CHECK_STATUS.equals(basSku.getHandleStatus()) && ConstantsEms.ENABLE_STATUS.equals(basSku.getStatus()))){
                                            errMsg = new CommonErrMsgResponse();
                                            errMsg.setItemNum(num);
                                            errMsg.setMsg("SKU类型为" + sku1TypeName + "的 " + s +" 档案必须是确认且已启用，导入失败！");
                                            errMsgList.add(errMsg);
                                        } else {
                                            BasMaterialSku materialSku = new BasMaterialSku();
                                            materialSku.setSkuSid(basSku.getSkuSid()).setSkuType(basSku.getSkuType());
                                            materialSku.setStatus(ConstantsEms.ENABLE_STATUS).setCreateDate(new Date()).setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
                                            basMaterialSkuList.add(materialSku);
                                        }
                                    }
                                } catch (Exception e){
                                    errMsg = new CommonErrMsgResponse();
                                    errMsg.setItemNum(num);
                                    errMsg.setMsg("SKU类型为" + sku1TypeName + "的 " + s +" 档案存在重复，请先检查该"+ sku1TypeName + "，导入失败！");
                                    errMsgList.add(errMsg);
                                }
                            }
                        }
                    }
                }
                /*
                 * SKU2名称
                 */
                String sku2Names = objects.get(26)==null||objects.get(26)==""?null:objects.get(26).toString();
                if (sku2Names != null){
                    if (ConstantsEms.NO.equals(isSkuMaterial)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("非SKU物料，SKU2名称应为空，导入失败！");
                        errMsgList.add(errMsg);
                    } else if(ConstantsEms.YES.equals(isSkuMaterial)) {
                        if ("1".equals(skuDimensionName)){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("SKU维度为一维时，SKU2名称应为空，导入失败！");
                            errMsgList.add(errMsg);
                        } else if ("2".equals(skuDimensionName) && sku2TypeName != null){
                            String[] skus = sku2Names.split(";|；");
                            Set<String> staffsSet = new HashSet<>(Arrays.asList(skus));
                            for (String s : staffsSet) {
                                BasSku basSku = null;
                                try {
                                    basSku = basSkuMapper.selectOne(new QueryWrapper<BasSku>().lambda().eq(BasSku::getSkuType,sku2Type==null?sku2TypeName:sku2Type)
                                            .eq(BasSku::getSkuName, s));
                                    if (basSku == null){
                                        errMsg = new CommonErrMsgResponse();
                                        errMsg.setItemNum(num);
                                        errMsg.setMsg("找不到SKU类型为" + sku2TypeName + "的 " + s +" 档案，导入失败！");
                                        errMsgList.add(errMsg);
                                    } else {
                                        if (!(ConstantsEms.CHECK_STATUS.equals(basSku.getHandleStatus()) && ConstantsEms.ENABLE_STATUS.equals(basSku.getStatus()))){
                                            errMsg = new CommonErrMsgResponse();
                                            errMsg.setItemNum(num);
                                            errMsg.setMsg("SKU类型为" + sku2TypeName + "的 " + s +" 档案必须是确认且已启用的状态，导入失败！");
                                            errMsgList.add(errMsg);
                                        } else {
                                            BasMaterialSku materialSku = new BasMaterialSku();
                                            materialSku.setSkuSid(basSku.getSkuSid()).setSkuType(basSku.getSkuType());
                                            materialSku.setStatus(ConstantsEms.ENABLE_STATUS).setCreateDate(new Date()).setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
                                            basMaterialSkuList.add(materialSku);
                                        }
                                    }
                                } catch (Exception e){
                                    errMsg = new CommonErrMsgResponse();
                                    errMsg.setItemNum(num);
                                    errMsg.setMsg("SKU类型为" + sku2TypeName + "的 " + s +" 档案存在重复，请先检查该"+ sku2TypeName + "，导入失败！");
                                    errMsgList.add(errMsg);
                                }
                            }
                        }
                    }
                }
                /*
                 * 拉链标识
                 */
                String zipperFlagName = objects.get(27)==null||objects.get(27)==""?null:objects.get(27).toString();
                String zipperFlag = null;
                if (zipperFlagName != null){
                    if(StrUtil.isBlank(zipperMaps.get(zipperFlagName))){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("拉链标识配置错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        zipperFlag=zipperMaps.get(zipperFlagName);
                    }
                }
                /*
                 * 备注
                 */
                String remark = objects.get(28)==null||objects.get(28)==""?null:objects.get(28).toString();
                if (remark != null && remark.length() > 600){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("备注长度不能大于600位，导入失败！");
                    errMsgList.add(errMsg);
                }
                if (CollectionUtil.isEmpty(errMsgList)){
                    basMaterial.setBasMaterialSkuList(basMaterialSkuList);
                    basMaterial.setMaterialName(materialName);
                    basMaterial.setMaterialCode(materialCode);
                    basMaterial.setSupplierProductCode(supplierProductCode);
                    basMaterial.setMaterialType(ConstantsEms.MATERIAL_F).setZipperFlag(zipperFlag)
                            .setUnitBase(unitBase).setUnitQuantity(unitQuantity).setInventoryPriceMethod(ConstantsEms.INVENTORY_PRICE_METHOD_JQPJJ)
                            .setUnitPrice(unitPrice).setUnitConversionRatePrice(unitConversionRatePrice).setUnitConversionRate(unitConversionRate)
                            .setPurchaseType(purchaseType).setIndustryField(ConstantsEms.INDUSTRY_FIELD_XIEF)
                            .setIsSkuMaterial(isSkuMaterial).setSkuDimension(skuDimension).setSku1Type(sku1Type).setSku2Type(sku2Type)
                            .setStatus(ConstantsEms.ENABLE_STATUS).setHandleStatus(ConstantsEms.SAVA_STATUS)
                            .setComposition(composition).setDensity(density).setSpecificationSize(specificationSize)
                            .setModelSize(modelSize).setMaterialComposition(materialComposition)
                            .setZipperMonth(zipperMonth).setZipperSize(zipperSize).setProcessDesc(processDesc)
                            .setVendorSid(vendorSid).setCustomerSid(customerSid).setQuotePriceTax(quotePriceTax)
                            .setRemark(remark).setMaterialCategory(ConstantsEms.MATERIAL_CATEGORY_WL)
                            .setImportType(BusinessType.IMPORT.getValue());
                    basMaterialList.add(basMaterial);
                }
            }
        }catch (BaseException e){
            throw new BaseException(e.getDefaultMessage());
        }
        //检查有没有报错
        if (CollectionUtil.isNotEmpty(errMsgList)){
            res.put("errList",errMsgList);
            res.put("warn",null);
            res.put("tableData",null);
            return res;
        }else {
            if (CollectionUtil.isNotEmpty(warMsgList)){
                res.put("errList",warMsgList);
                res.put("warn",true);
                res.put("tableData",basMaterialList);
                return res;
            }
        }
        if(CollectionUtils.isNotEmpty(basMaterialList)){
            basMaterialList.forEach(item->{
                basMaterialService.insertBasMaterial(item);
            });
        }
        res.put("success",basMaterialList.size());
        return res;
    }

    /**
     * 物料-面料
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public HashMap<String, Object> importDataM(MultipartFile file){
        HashMap<String, Object> res = new HashMap<>();
        String vendorJudge = null;
        List<BasMaterial> basMaterialList=new ArrayList<>();
        //错误信息
        CommonErrMsgResponse errMsg = null;
        CommonErrMsgResponse warMsg = null;
        List<CommonErrMsgResponse> errMsgList = new ArrayList<>();
        List<CommonErrMsgResponse> warMsgList = new ArrayList<>();
        try{
            File toFile=null;
            try {
                toFile   = FileUtils.multipartFileToFile(file);
            }catch (Exception e){
                e.getMessage();
                throw new BaseException("文件转换失败");
            }
            ExcelReader reader = cn.hutool.poi.excel.ExcelUtil.getReader(toFile);
            FileUtils.delteTempFile(toFile);
            List<List<Object>> readAll = reader.read();
            // 物料类型
            List<ConMaterialType> conMaterialTypeList = conMaterialTypeMapper.getConMaterialTypeList();
            conMaterialTypeList = conMaterialTypeList.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String,String> materialTypeMaps=conMaterialTypeList.stream().collect(Collectors.toMap(ConMaterialType::getName, ConMaterialType::getCode,(key1, key2)->key2));
            // 计量单位
            List<ConMeasureUnit> conMeasureUnitList = conMeasureUnitMapper.getConMeasureUnitList();
            conMeasureUnitList = conMeasureUnitList.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String,String> measureUnitMaps=conMeasureUnitList.stream().collect(Collectors.toMap(ConMeasureUnit::getName, ConMeasureUnit::getCode,(key1, key2)->key2));
            // 是否sku物料
            List<DictData> isSkuList=sysDictDataService.selectDictData("sys_yes_no");
            isSkuList = isSkuList.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String,String> isSkuMaps=isSkuList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue,(key1, key2)->key2));
            // sku维度
            List<DictData> skuList=sysDictDataService.selectDictData("s_sku_dimension");
            skuList = skuList.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String,String> skuMaps=skuList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue,(key1, key2)->key2));
            // sku类型
            List<DictData> skuTypeList=sysDictDataService.selectDictData("s_sku_type");
            skuTypeList = skuTypeList.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String,String> skuTypeMaps=skuTypeList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue,(key1, key2)->key2));
            // 采购类型 根据用户所在租户的租户类型判断
            String clientType = ApiThreadLocalUtil.get().getSysUser().getClientType();
            //excel表里面编码和名称的缓存
            HashMap<String, String> codeMap = new HashMap<>();
            HashMap<String, String> nameMap = new HashMap<>();
            HashMap<String, String> vendorMap = new HashMap<>();
            com.platform.common.core.domain.entity.SysDefaultSettingClient client = ApiThreadLocalUtil.get().getSysUser().getClient();
            for (int i = 0; i < readAll.size(); i++) {
                if(i<2){
                    //前两行跳过
                    continue;
                }
                int num=i+1;
                List<Object> objects=readAll.get(i);
                copy(objects, readAll);
                /*
                 * 物料编码
                 */
                String materialCode = objects.get(0)==null||objects.get(0)==""?null:objects.get(0).toString();
                if(materialCode == null){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("物料编码不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    if (materialCode.contains(" ")){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("物料编码数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        if (materialCode.length() > 20){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("物料编码长度不能大于20位，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                    // 判断是否与表格内的编码重复
                    if (codeMap.get(materialCode) == null){
                        codeMap.put(materialCode,String.valueOf(num));
                        // 如果表格内没重复则判断与数据库之间是否存在重复
                        List<BasMaterial> materialList = basMaterialMapper.selectList(new QueryWrapper<BasMaterial>().lambda()
                                .eq(BasMaterial::getMaterialCode,materialCode));
                        if (CollectionUtil.isNotEmpty(materialList)){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("物料编码重复，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }else {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("物料编码重复，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * 物料名称
                 */
                String materialName = objects.get(1)==null||objects.get(1)==""?null:objects.get(1).toString();
                if(materialName == null){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("物料名称不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    if (materialName.length() > 300){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("物料名称长度不能大于300位，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * 物料类型
                 */
                String materialTypeName = objects.get(2)==null||objects.get(2)==""?null:objects.get(2).toString();
                String materialType = null;
                if(materialTypeName == null){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("物料类型不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    try {
                        ConMaterialType conMaterialType = conMaterialTypeMapper.selectOne(new QueryWrapper<ConMaterialType>().lambda()
                                .eq(ConMaterialType::getName,materialTypeName));
                        if(conMaterialType == null){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("物料类型配置错误，导入失败！");
                            errMsgList.add(errMsg);
                        }else {
                            if (!ConstantsEms.MATERIAL_M.equals(conMaterialType.getCode())){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("对应的物料类型必须是面料，导入失败！");
                                errMsgList.add(errMsg);
                            }
                            materialType = conMaterialType.getCode();
                        }
                    }catch (Exception e){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(materialTypeName +"物料类型档案存在重复，请先检查该物料类型，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * 基本计量单位
                 */
                String unitBaseName = objects.get(3)==null||objects.get(3)==""?null:objects.get(3).toString();
                String unitBase = null;
                if(unitBaseName == null){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("基本计量单位不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    try {
                        ConMeasureUnit measureUnit = conMeasureUnitMapper.selectOne(new QueryWrapper<ConMeasureUnit>().lambda()
                                .eq(ConMeasureUnit::getName,unitBaseName));
                        if(measureUnit == null){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("基本计量单位配置错误，导入失败！");
                            errMsgList.add(errMsg);
                        }else {
                            if (!ConstantsEms.CHECK_STATUS.equals(measureUnit.getHandleStatus()) || !ConstantsEms.ENABLE_STATUS.equals(measureUnit.getStatus())){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("对应的基本计量单位必须是确认且已启用的状态，导入失败！");
                                errMsgList.add(errMsg);
                            }
                            unitBase = measureUnit.getCode();
                        }
                    }catch (Exception e){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(unitBaseName +"基本计量单位档案存在重复，请先检查该基本计量单位，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * BOM用量计量单位
                 */
                String unitQuantityName = objects.get(4)==null||objects.get(4)==""?null:objects.get(4).toString();
                String unitQuantity = null;
                if(StrUtil.isBlank(unitQuantityName) && !ConstantsEms.NO.equals(client.getIsRequiredUnitQuantityMaterial())) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("BOM用量计量单位不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else if (StrUtil.isNotBlank(unitQuantityName)) {
                    try {
                        ConMeasureUnit measureUnit = conMeasureUnitMapper.selectOne(new QueryWrapper<ConMeasureUnit>().lambda()
                                .eq(ConMeasureUnit::getName,unitQuantityName));
                        if(measureUnit == null){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("BOM用量计量单位配置错误，导入失败！");
                            errMsgList.add(errMsg);
                        }else {
                            if (!ConstantsEms.CHECK_STATUS.equals(measureUnit.getHandleStatus()) || !ConstantsEms.ENABLE_STATUS.equals(measureUnit.getStatus())){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("对应的BOM用量计量单位必须是确认且已启用的状态，导入失败！");
                                errMsgList.add(errMsg);
                            }
                            unitQuantity = measureUnit.getCode();
                            if (StrUtil.isNotBlank(unitBase) && !unitBase.equals(unitQuantity)) {
                                warMsg = new CommonErrMsgResponse();
                                warMsg.setItemNum(num);
                                warMsg.setMsg("基本单位与BOM用量单位不同，确定是否继续?");
                                warMsgList.add(warMsg);
                            }
                        }
                    }catch (Exception e){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(unitQuantityName +"BOM用量计量单位档案存在重复，请先检查该BOM用量计量单位，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * 报价单位
                 */
                String unitPriceName = objects.get(5)==null||objects.get(5)==""?null:objects.get(5).toString();
                String unitPrice = null;
                if(StrUtil.isBlank(unitPriceName) && !ConstantsEms.NO.equals(client.getIsRequiredQuotePriceTaxMaterial())) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("报价单位不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else if (StrUtil.isNotBlank(unitPriceName)) {
                    try {
                        ConMeasureUnit measureUnit = conMeasureUnitMapper.selectOne(new QueryWrapper<ConMeasureUnit>().lambda()
                                .eq(ConMeasureUnit::getName,unitPriceName));
                        if(measureUnit == null){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("报价单位配置错误，导入失败！");
                            errMsgList.add(errMsg);
                        }else {
                            if (!ConstantsEms.CHECK_STATUS.equals(measureUnit.getHandleStatus()) || !ConstantsEms.ENABLE_STATUS.equals(measureUnit.getStatus())){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("对应的报价单位必须是确认且已启用的状态，导入失败！");
                                errMsgList.add(errMsg);
                            }
                            unitPrice = measureUnit.getCode();
                        }
                    }catch (Exception e){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(unitPriceName +"报价单位档案存在重复，请先检查该报价单位，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * 单位换算比例(基本计量单位/BOM用量)
                 */
                String unitConversionRate_s = objects.get(6)==null||objects.get(6)==""?null:objects.get(6).toString();
                BigDecimal unitConversionRate = null;
                if (StrUtil.isBlank(unitConversionRate_s) && !ConstantsEms.NO.equals(client.getIsRequiredUnitQuantityMaterial())) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("单位换算比例(基本计量单位/BOM用量)不可为空，导入失败！");
                    errMsgList.add(errMsg);
                } else if (StrUtil.isNotBlank(unitConversionRate_s)) {
                    if (!JudgeFormat.isValidDouble(unitConversionRate_s,4,4)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("单位换算比例(基本计量单位/BOM用量)数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        unitConversionRate = new BigDecimal(unitConversionRate_s);
                        if (unitBase != null && unitQuantity != null && unitBase.equals(unitQuantity) && !"1".equals(unitConversionRate_s)){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("单位换算比例(基本计量单位/BOM用量)必须等于1，导入失败！");
                            errMsgList.add(errMsg);
                        }else {
                            if (unitConversionRate != null && BigDecimal.ZERO.compareTo(unitConversionRate) >= 0) {
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("单位换算比例(基本计量单位/BOM用量)不能小于等于0，导入失败！");
                                errMsgList.add(errMsg);
                            }
                        }
                    }
                }
                /*
                 * 单位换算比例(报价单位/基本计量用量)
                 */
                String unitConversionRatePrice_s = objects.get(7)==null||objects.get(7)==""?null:objects.get(7).toString();
                BigDecimal unitConversionRatePrice = null;
                if (StrUtil.isBlank(unitConversionRatePrice_s) && !ConstantsEms.NO.equals(client.getIsRequiredQuotePriceTaxMaterial())) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("单位换算比例(报价单位/基本计量用量)不可为空，导入失败！");
                    errMsgList.add(errMsg);
                } else if (StrUtil.isNotBlank(unitConversionRatePrice_s)) {
                    if (!JudgeFormat.isValidDouble(unitConversionRatePrice_s,4,4)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("单位换算比例(报价单位/基本计量用量)数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        unitConversionRatePrice = new BigDecimal(unitConversionRatePrice_s);
                        if (unitBase != null && unitPrice != null && unitBase.equals(unitPrice) && !"1".equals(unitConversionRatePrice_s)){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("单位换算比例(报价单位/基本计量用量)必须等于1，导入失败！");
                            errMsgList.add(errMsg);
                        }else {
                            if (unitConversionRatePrice != null && BigDecimal.ZERO.compareTo(unitConversionRatePrice) >= 0) {
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("单位换算比例(报价单位/基本计量用量)不能小于等于0，导入失败！");
                                errMsgList.add(errMsg);
                            }
                        }
                    }
                }
                /*
                 * 采购类型
                 */
                String purchaseTypeName = objects.get(8)==null||objects.get(8)==""?null:objects.get(8).toString();
                String purchaseType = null;
                if(purchaseTypeName == null){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("采购类型不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    try {
                        QueryWrapper<ConPurchaseType> queryWrapper = new QueryWrapper<>();
                        queryWrapper.lambda().eq(ConPurchaseType::getName,purchaseTypeName);
                        if (StrUtil.isNotBlank(clientType) && !ConstantsEms.CLIENT_TYPE_GMYT.equals(clientType)){
                            queryWrapper.lambda().eq(ConPurchaseType::getClientType,clientType);
                        }
                        ConPurchaseType conPurchaseType = conPurchaseTypeMapper.selectOne(queryWrapper);
                        if(conPurchaseType == null){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("采购类型配置错误，导入失败！");
                            errMsgList.add(errMsg);
                        }else {
                            if (!ConstantsEms.CHECK_STATUS.equals(conPurchaseType.getHandleStatus()) || !ConstantsEms.ENABLE_STATUS.equals(conPurchaseType.getStatus())){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("对应的采购类型必须是确认且已启用的状态，导入失败！");
                                errMsgList.add(errMsg);
                            }
                            purchaseType = conPurchaseType.getCode();
                        }
                    }catch (Exception e){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(purchaseTypeName +"采购类型档案存在重复，请先检查该计价计量单位，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * 是否sku物料
                 */
                String isSkuMaterialName = objects.get(9)==null||objects.get(9)==""?null:objects.get(9).toString();
                String isSkuMaterial = null;
                if(isSkuMaterialName == null){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("是否SKU物料不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    if(StrUtil.isBlank(isSkuMaps.get(isSkuMaterialName))){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("是否SKU物料配置错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        isSkuMaterial=isSkuMaps.get(isSkuMaterialName);
                    }
                }
                /*
                 * sku维度
                 */
                String skuDimensionName = objects.get(10)==null||objects.get(10)==""?null:objects.get(10).toString();
                Integer skuDimension = null;
                if (skuDimensionName != null){
                    if (ConstantsEms.NO.equals(isSkuMaterial)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("是否SKU物料为否时，SKU维度数必须为空，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        if (StrUtil.isNotBlank(skuMaps.get(skuDimensionName))){
                            skuDimensionName = skuMaps.get(skuDimensionName);
                            if (!"1".equals(skuDimensionName)){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("是否SKU物料为是时，SKU维度数必填且必须是一维，导入失败！");
                                errMsgList.add(errMsg);
                            }
                            try {
                                skuDimension= Integer.parseInt(skuDimensionName);
                            }catch (Exception e){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("SKU维度配置错误，请联系管理员，导入失败！");
                                errMsgList.add(errMsg);
                            }
                        }else {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("SKU维度配置错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                } else {
                    if (ConstantsEms.YES.equals(isSkuMaterial)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("是否SKU物料为是时，SKU维度数必填且必须是一维，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * sku1类型
                 */
                String sku1TypeName = objects.get(11)==null||objects.get(11)==""?null:objects.get(11).toString();
                String sku1Type = null;
                if(sku1TypeName != null) {
                    if (ConstantsEms.NO.equals(isSkuMaterial)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("是否SKU物料为否时，SKU1类型必须为空，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        if(StrUtil.isBlank(skuTypeMaps.get(sku1TypeName))){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("SKU1类型配置错误，导入失败！");
                            errMsgList.add(errMsg);
                        }else {
                            sku1Type= skuTypeMaps.get(sku1TypeName);
                            if (!ConstantsEms.SKUTYP_YS.equals(sku1Type)){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("SKU1类型必须是颜色，导入失败！");
                                errMsgList.add(errMsg);
                            }
                        }
                    }
                }else {
                    if ("1".equals(skuDimensionName)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("SKU维度为一维时，SKU1类型不允许为空，导入失败！");
                        errMsgList.add(errMsg);
                    } else if ("2".equals(skuDimensionName)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("SKU维度为二维时，SKU1类型不允许为空，导入失败！");
                        errMsgList.add(errMsg);
                    }else {}
                }
                /*
                 * 幅宽（厘米）
                 */
                String width = objects.get(12)==""||objects.get(12)==null?null:objects.get(12).toString();
                if (width != null && width.length() > 180){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("幅宽（厘米）长度不能大于180位，导入失败！");
                    errMsgList.add(errMsg);
                }
                /*
                 * 克重
                 */
                String gramWeight = objects.get(13)==""||objects.get(13)==null?null:objects.get(13).toString();
                if (gramWeight != null && gramWeight.length() > 180){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("克重长度不能大于180位，导入失败！");
                    errMsgList.add(errMsg);
                }
                /*
                 * 成分
                 */
                String composition = objects.get(14)==""||objects.get(14)==null?null:objects.get(14).toString();
                if (composition != null && composition.length() > 180){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("成分长度不能大于180位，导入失败！");
                    errMsgList.add(errMsg);
                }
                /*
                 * 纱支
                 */
                String yarnCount = objects.get(15)==""||objects.get(15)==null?null:objects.get(15).toString();
                if (yarnCount == null){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("纱支不允许为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    if (yarnCount.length() > 180){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("纱支长度不能大于180位，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * 密度
                 */
                String density = objects.get(16)==""||objects.get(16)==null?null:objects.get(16).toString();
                if (density == null){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("密度不允许为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    if (density.length() > 180){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("密度长度不能大于180位，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * 规格尺寸
                 */
                String specificationSize = objects.get(17)==null||objects.get(17)==""?null:objects.get(17).toString();
                if (specificationSize != null && specificationSize.length() > 180){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("规格尺寸长度不能大于180位，导入失败！");
                    errMsgList.add(errMsg);
                }
                /*
                 * 型号
                 */
                String modelSize = objects.get(18)==null||objects.get(18)==""?null:objects.get(18).toString();
                if (modelSize != null && modelSize.length() > 180){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("型号长度不能大于180位，导入失败！");
                    errMsgList.add(errMsg);
                }
                /*
                 * 材质
                 */
                String materialComposition = objects.get(19)==null||objects.get(19)==""?null:objects.get(19).toString();
                if (materialComposition != null && materialComposition.length() > 180){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("材质长度不能大于180位，导入失败！");
                    errMsgList.add(errMsg);
                }
                /*
                 * 工艺说明
                 */
                String processDesc = objects.get(20)==null||objects.get(20)==""?null:objects.get(20).toString();
                if (processDesc != null && processDesc.length() > 600){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("工艺说明长度不能大于600位，导入失败！");
                    errMsgList.add(errMsg);
                }
                /*
                 * 供应商简称
                 */
                String vendorShortName = objects.get(21)==null||objects.get(21)==""?null:objects.get(21).toString();
                Long vendorSid = null;
                if (StrUtil.isBlank(vendorShortName) && !ConstantsEms.NO.equals(client.getIsRequiredVendorMaterial())) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("供应商简称不可为空，导入失败！");
                    errMsgList.add(errMsg);
                } else if (StrUtil.isNotBlank(vendorShortName)) {
                    try {
                        BasVendor basVendor = basVendorMapper.selectOne(new QueryWrapper<BasVendor>()
                                .lambda().eq(BasVendor::getShortName, vendorShortName));
                        if(basVendor==null){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("对应的供应商简称不存在，导入失败！");
                            errMsgList.add(errMsg);
                        }else{
                            if (!ConstantsEms.ENABLE_STATUS.equals(basVendor.getStatus()) || !ConstantsEms.CHECK_STATUS.equals(basVendor.getHandleStatus())){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("对应的供应商简称必须是确认且已启用的状态，导入失败！");
                                errMsgList.add(errMsg);
                            }
                            vendorSid = basVendor.getVendorSid();
                        }
                    }catch (Exception e){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(vendorShortName +"供应商档案存在重复，请先检查该供应商，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * 供方编码
                 */
                String supplierProductCode = objects.get(22)==null||objects.get(22)==""?null:objects.get(22).toString();
                if (StrUtil.isBlank(supplierProductCode) && !ConstantsEms.NO.equals(client.getIsRequiredVendorMaterial())) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("供方编码不可为空，导入失败！");
                    errMsgList.add(errMsg);
                } else if (StrUtil.isNotBlank(supplierProductCode)) {
                    if (supplierProductCode.length() > 30){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("供方编码长度不能大于30位，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                //"物料编码：" + codes + " 在系统中已存在相同“供应商/供方编码”的物料档案，确定是否继续?"
                if (vendorSid != null && supplierProductCode != null){
                    //表格中
                    if (vendorMap.get(vendorSid.toString()+supplierProductCode)==null){
                        vendorMap.put(vendorSid.toString()+supplierProductCode,String.valueOf(num));
                        // 去系统
                        List<BasMaterial> list = basMaterialMapper.selectList(new QueryWrapper<BasMaterial>().lambda().eq(BasMaterial::getVendorSid,vendorSid)
                                .eq(BasMaterial::getSupplierProductCode,supplierProductCode));
                        if (CollectionUtils.isNotEmpty(list)){
                            warMsg = new CommonErrMsgResponse();
                            warMsg.setItemNum(num);
                            warMsg.setMsg("系统中已存在相同“供应商/供方编码”的物料档案，确定是否继续?");
                            warMsgList.add(warMsg);
                            vendorJudge = "Y";
                        }
                    }else{
                        warMsg = new CommonErrMsgResponse();
                        warMsg.setItemNum(num);
                        warMsg.setMsg("表格中已存在相同“供应商/供方编码”的物料档案，确定是否继续?");
                        warMsgList.add(warMsg);
                        vendorJudge = "Y";
                    }
                }
                /*
                 * 客户简称
                 */
                String customerShortName = objects.get(23)==null||objects.get(23)==""?null:objects.get(23).toString();
                Long customerSid = null;
                if (customerShortName != null){
                    try {
                        BasCustomer basCustomer = basCustomerMapper.selectOne(new QueryWrapper<BasCustomer>()
                                .lambda().eq(BasCustomer::getShortName, customerShortName));
                        if(basCustomer==null){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("对应的客户简称不存在，导入失败！");
                            errMsgList.add(errMsg);
                        }else{
                            if (!ConstantsEms.ENABLE_STATUS.equals(basCustomer.getStatus()) || !ConstantsEms.CHECK_STATUS.equals(basCustomer.getHandleStatus())){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("对应的客户简称必须是确认且已启用的状态，导入失败！");
                                errMsgList.add(errMsg);
                            }
                            customerSid = basCustomer.getCustomerSid();
                        }
                    }catch (Exception e){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(customerShortName +"客户档案存在重复，请先检查该客户，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * 报价(元/人民币)
                 */
                String quotePriceTax_s = objects.get(24)==null||objects.get(24)==""?null:objects.get(24).toString();
                BigDecimal quotePriceTax = null;
                if (StrUtil.isBlank(quotePriceTax_s) && !ConstantsEms.NO.equals(client.getIsRequiredQuotePriceTaxMaterial())) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("报价(元/人民币)不可为空，导入失败！");
                    errMsgList.add(errMsg);
                }
                else if (StrUtil.isNotBlank(quotePriceTax_s)) {
                    if (!JudgeFormat.isValidDouble(quotePriceTax_s,11,4)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("报价(元/人民币)数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        quotePriceTax = new BigDecimal(quotePriceTax_s);
                        if (quotePriceTax != null && BigDecimal.ZERO.compareTo(quotePriceTax) > 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("报价(元/人民币)不能小于0，导入失败！");
                            errMsgList.add(errMsg);
                        }
                        quotePriceTax=quotePriceTax.divide(BigDecimal.ONE,5,BigDecimal.ROUND_HALF_UP);
                    }
                }
                BasMaterial basMaterial = new BasMaterial();
                List<BasMaterialSku> basMaterialSkuList = new ArrayList<>();
                /*
                 * SKU1名称
                 */
                String sku1Names = objects.get(25)==null||objects.get(25)==""?null:objects.get(25).toString();
                if (sku1Names != null){
                    if (ConstantsEms.NO.equals(isSkuMaterial)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("非SKU物料，SKU1名称应为空，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        if (sku1TypeName == null){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("SKU1类型为空时，SKU1名称应为空，导入失败！");
                            errMsgList.add(errMsg);
                        } else {
                            String[] skus = sku1Names.split(";|；");
                            Set<String> staffsSet = new HashSet<>(Arrays.asList(skus));
                            for (String s : staffsSet) {
                                BasSku basSku = null;
                                try {
                                    basSku = basSkuMapper.selectOne(new QueryWrapper<BasSku>().lambda().eq(BasSku::getSkuType,sku1Type==null?sku1TypeName:sku1Type)
                                            .eq(BasSku::getSkuName, s));
                                    if (basSku == null){
                                        errMsg = new CommonErrMsgResponse();
                                        errMsg.setItemNum(num);
                                        errMsg.setMsg("找不到SKU类型为" + sku1TypeName + "的 " + s +" 档案，导入失败！");
                                        errMsgList.add(errMsg);
                                    } else {
                                        if (!(ConstantsEms.CHECK_STATUS.equals(basSku.getHandleStatus()) && ConstantsEms.ENABLE_STATUS.equals(basSku.getStatus()))){
                                            errMsg = new CommonErrMsgResponse();
                                            errMsg.setItemNum(num);
                                            errMsg.setMsg("SKU类型为" + sku1TypeName + "的 " + s +" 档案必须是确认且已启用的状态，导入失败！");
                                            errMsgList.add(errMsg);
                                        } else {
                                            BasMaterialSku materialSku = new BasMaterialSku();
                                            materialSku.setSkuSid(basSku.getSkuSid()).setSkuType(basSku.getSkuType());
                                            materialSku.setStatus(ConstantsEms.ENABLE_STATUS).setCreateDate(new Date()).setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
                                            basMaterialSkuList.add(materialSku);
                                        }
                                    }
                                } catch (Exception e){
                                    errMsg = new CommonErrMsgResponse();
                                    errMsg.setItemNum(num);
                                    errMsg.setMsg("SKU类型为" + sku1TypeName + "的 " + s +" 档案存在重复，请先检查该"+ sku1TypeName + "，导入失败！");
                                    errMsgList.add(errMsg);
                                }
                            }
                        }
                    }
                }
                /*
                 * 备注
                 */
                String remark = objects.get(26)==null||objects.get(26)==""?null:objects.get(26).toString();
                if (remark != null && remark.length() > 600){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("备注长度不能大于600位，导入失败！");
                    errMsgList.add(errMsg);
                }
                if (CollectionUtil.isEmpty(errMsgList)){
                    basMaterial.setBasMaterialSkuList(basMaterialSkuList);
                    basMaterial.setMaterialName(materialName);
                    basMaterial.setMaterialCode(materialCode);
                    basMaterial.setSupplierProductCode(supplierProductCode);
                    basMaterial.setMaterialType(ConstantsEms.MATERIAL_M).setInventoryPriceMethod(ConstantsEms.INVENTORY_PRICE_METHOD_JQPJJ)
                            .setUnitBase(unitBase).setUnitQuantity(unitQuantity).setUnitPrice(unitPrice)
                            .setUnitConversionRatePrice(unitConversionRatePrice)
                            .setUnitConversionRate(unitConversionRate).setPurchaseType(purchaseType)
                            .setIsSkuMaterial(isSkuMaterial).setIndustryField(ConstantsEms.INDUSTRY_FIELD_XIEF)
                            .setStatus(ConstantsEms.ENABLE_STATUS).setHandleStatus(ConstantsEms.SAVA_STATUS)
                            .setSkuDimension(skuDimension).setSku1Type(sku1Type)
                            .setWidth(width).setGramWeight(gramWeight).setComposition(composition)
                            .setYarnCount(yarnCount).setDensity(density).setSpecificationSize(specificationSize)
                            .setModelSize(modelSize).setMaterialComposition(materialComposition)
                            .setProcessDesc(processDesc).setVendorSid(vendorSid).setCustomerSid(customerSid)
                            .setQuotePriceTax(quotePriceTax).setRemark(remark).setMaterialCategory(ConstantsEms.MATERIAL_CATEGORY_WL)
                            .setImportType(BusinessType.IMPORT.getValue());
                    basMaterialList.add(basMaterial);
                }
            }
        }catch (BaseException e){
            throw new BaseException(e.getDefaultMessage());
        }
        //检查有没有报错
        if (CollectionUtil.isNotEmpty(errMsgList)){
            res.put("errList",errMsgList);
            res.put("warn",null);
            res.put("tableData",null);
            return res;
        }else {
            if (CollectionUtil.isNotEmpty(warMsgList)){
                res.put("errList",warMsgList);
                res.put("warn",true);
                res.put("tableData",basMaterialList);
                return res;
            }
        }
        if(CollectionUtils.isNotEmpty(basMaterialList)){
            basMaterialList.forEach(item->{
                basMaterialService.insertBasMaterial(item);
            });
        }
        res.put("success",basMaterialList.size());
        return res;
    }

    /**
     * 商品
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public HashMap<String, Object> importDataG(MultipartFile file){
        HashMap<String, Object> res = new HashMap<>();
        List<BasMaterial> basMaterialList=new ArrayList<>();
        // 我司样衣号的提示信息
        String sampleCodeSelfMsg = "";
        //错误信息
        List<CommonErrMsgResponse> errMsgList = new ArrayList<>();
        CommonErrMsgResponse errMsg = null;
        //继续导入
        List<CommonErrMsgResponse> warnMsgList = new ArrayList<>();
        CommonErrMsgResponse warnMsg = null;
        String industryField = ApiThreadLocalUtil.get().getSysUser().getIndustryField();
        try{
            File toFile=null;
            try {
                toFile   = FileUtils.multipartFileToFile(file);
            }catch (Exception e){
                e.getMessage();
                throw new BaseException("文件转换失败");
            }
            ExcelReader reader = cn.hutool.poi.excel.ExcelUtil.getReader(toFile);
            FileUtils.delteTempFile(toFile);
            List<List<Object>> readAll = reader.read();
            //是否sku
            List<DictData> isSkuList=sysDictDataService.selectDictData("sys_yes_no");
            isSkuList = isSkuList.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String,String> isSkuMaps=isSkuList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue,(key1, key2)->key2));
            //sku维度
            List<DictData> skuList=sysDictDataService.selectDictData("s_sku_dimension");
            skuList = skuList.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String,String> skuMaps=skuList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue,(key1, key2)->key2));
            //sku类型
            List<DictData> skuTypeList=sysDictDataService.selectDictData("s_sku_type");
            skuTypeList = skuTypeList.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String,String> skuTypeMaps=skuTypeList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue,(key1, key2)->key2));
            //上下装
            List<DictData> upDownList=sysDictDataService.selectDictData("s_up_down_suit");
            upDownList = upDownList.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> upDownListMaps = upDownList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            //甲供料
            List<DictData> rawMaterialList=sysDictDataService.selectDictData("s_raw_material_mode");
            rawMaterialList = rawMaterialList.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> rawMaterialMaps = rawMaterialList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            //男女装
            List<DictData> suitGenderList=sysDictDataService.selectDictData("s_suit_gender");
            suitGenderList = suitGenderList.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> suitGenderMaps = suitGenderList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            //季节
            List<DictData> seasonList=sysDictDataService.selectDictData("s_season");
            seasonList = seasonList.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> seasonMaps = seasonList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            //excel表里面编码和名称的缓存
            HashMap<String, String> codeMap = new HashMap<>();
            HashMap<String, String> sampleCodeSelfMap = new HashMap<>();
            for (int i = 0; i < readAll.size(); i++) {
                int num=i+1;
                if(i<2){
                    //前两行跳过
                    continue;
                }
                List<Object> objects=readAll.get(i);
                copy(objects, readAll);
                /*
                 * 商品编码
                 */
                String materialCode = objects.get(0)==null||objects.get(0)==""?null:objects.get(0).toString();
                if(materialCode == null){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("商品编码不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    // 不支持汉字
                    boolean numberOrE = JudgeFormat.isNumberOrE(materialCode);
                    if(!numberOrE){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("商品编码数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                    // 判断是否与表格内的编码重复
                    if (codeMap.get(materialCode) == null){
                        codeMap.put(materialCode,String.valueOf(num));
                        List<BasMaterial> materialList = basMaterialMapper.selectList(new QueryWrapper<BasMaterial>().lambda()
                                .eq(BasMaterial::getMaterialCode,materialCode));
                        if (CollectionUtil.isNotEmpty(materialList)){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("商品编码重复，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }else {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("商品编码重复，导入失败！");
                        errMsgList.add(errMsg);
                    }
                    if (materialCode.length() > 20){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("商品编码长度不能大于20位，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * 商品名称
                 */
                String materialName = objects.get(1)==null||objects.get(1)==""?null:objects.get(1).toString();
                if(materialName == null){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("商品名称不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    if (materialName.length() > 300){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("商品名称长度不能大于300位，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * 商品类型
                 */
                String materialTypeName = objects.get(2)==null||objects.get(2)==""?null:objects.get(2).toString();
                String materialType = null;
                if(materialTypeName == null){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("商品类型不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    try {
                        ConMaterialType conMaterialType = conMaterialTypeMapper.selectOne(new QueryWrapper<ConMaterialType>().lambda()
                                .eq(ConMaterialType::getName,materialTypeName));
                        if(conMaterialType == null){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("商品类型配置错误，导入失败！");
                            errMsgList.add(errMsg);
                        }else {
                            if (!ConstantsEms.CHECK_STATUS.equals(conMaterialType.getHandleStatus()) || !ConstantsEms.ENABLE_STATUS.equals(conMaterialType.getStatus())){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("对应的商品类型必须是确认且已启用的状态，导入失败！");
                                errMsgList.add(errMsg);
                            }
                            materialType = conMaterialType.getCode();
                        }
                    }catch (Exception e){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(materialTypeName +"商品类型档案存在重复，请先检查该商品类型，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * 基本计量单位
                 */
                String unitBaseName = objects.get(3)==null||objects.get(3)==""?null:objects.get(3).toString();
                String unitBase = null;
                if(unitBaseName == null){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("基本计量单位不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    try {
                        ConMeasureUnit measureUnit = conMeasureUnitMapper.selectOne(new QueryWrapper<ConMeasureUnit>().lambda()
                                .eq(ConMeasureUnit::getName,unitBaseName));
                        if(measureUnit == null){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("基本计量单位配置错误，导入失败！");
                            errMsgList.add(errMsg);
                        }else {
                            if (!ConstantsEms.CHECK_STATUS.equals(measureUnit.getHandleStatus()) || !ConstantsEms.ENABLE_STATUS.equals(measureUnit.getStatus())){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("对应的基本计量单位必须是确认且已启用的状态，导入失败！");
                                errMsgList.add(errMsg);
                            }
                            unitBase = measureUnit.getCode();
                        }
                    }catch (Exception e){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(unitBaseName +"基本计量单位档案存在重复，请先检查该基本计量单位，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * 产品季
                 */
                String productSeasonName = objects.get(4)==null||objects.get(4)==""?null:objects.get(4).toString();
                Long productSeasonSid = null;
                if(productSeasonName == null) {
                    if (!ConstantsEms.NO.equals(ApiThreadLocalUtil.get().getSysUser().getClient().getIsRequiredProductSeasonProduct())) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("产品季不能为空，导入失败！");
                        errMsgList.add(errMsg);
                    }
                } else if(StrUtil.isNotBlank(productSeasonName)) {
                    try {
                        BasProductSeason basProductSeason = basProductSeasonMapper.selectOne(new QueryWrapper<BasProductSeason>().lambda()
                                .eq(BasProductSeason::getProductSeasonName,productSeasonName));
                        if (basProductSeason == null){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("产品季"+ productSeasonName+ "不存在，导入失败！");
                            errMsgList.add(errMsg);
                        }else {
                            if (!ConstantsEms.CHECK_STATUS.equals(basProductSeason.getHandleStatus()) || !ConstantsEms.ENABLE_STATUS.equals(basProductSeason.getStatus())){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("对应的产品季必须是确认且已启用的状态，导入失败！");
                                errMsgList.add(errMsg);
                            }
                            productSeasonSid = basProductSeason.getProductSeasonSid();
                        }
                    }catch (Exception e){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(productSeasonName +"产品季档案存在重复，请先检查该产品季，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * 生产工艺类型
                 */
                String productTechniqueName = objects.get(5)==null||objects.get(5)==""?null:objects.get(5).toString();
                String productTechnique = null;
                if(productTechniqueName == null){
                    if (!ConstantsEms.NO.equals(ApiThreadLocalUtil.get().getSysUser().getClient().getIsRequiredProductTechniqueTypeProduct())) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("生产工艺类型不能为空，导入失败！");
                        errMsgList.add(errMsg);
                    }
                } else if (StrUtil.isNotBlank(productTechniqueName)) {
                    try {
                        ConProductTechniqueType conProductTechniqueType = conProductTechniqueTypeMapper.selectOne(new QueryWrapper<ConProductTechniqueType>()
                                .lambda().eq(ConProductTechniqueType::getName,productTechniqueName));
                        if(conProductTechniqueType == null){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("生产工艺类型配置错误，导入失败！");
                            errMsgList.add(errMsg);
                        }else {
                            if (!ConstantsEms.CHECK_STATUS.equals(conProductTechniqueType.getHandleStatus()) || !ConstantsEms.ENABLE_STATUS.equals(conProductTechniqueType.getStatus())){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("对应的生产工艺类型必须是确认且已启用的状态，导入失败！");
                                errMsgList.add(errMsg);
                            }
                            productTechnique = conProductTechniqueType.getCode();
                        }
                    }catch (Exception e){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(productTechniqueName +"生产工艺类型档案存在重复，请先检查该生产工艺类型，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * 是否SKU商品
                 */
                String isSkuMaterialName = objects.get(6)==null||objects.get(6)==""?null:objects.get(6).toString();
                String isSkuMaterial = null;
                if(isSkuMaterialName == null){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("是否SKU商品不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    isSkuMaterial=isSkuMaps.get(isSkuMaterialName);
                    if(StrUtil.isBlank(isSkuMaterial)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("是否SKU商品配置错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                    if (!ConstantsEms.YES.equals(isSkuMaterial)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("是否SKU商品只能为“是”，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * SKU维度
                 */
                String skuDimensionName = objects.get(7)==null||objects.get(7)==""?null:objects.get(7).toString();
                Integer skuDimension = null;
                if(skuDimensionName == null){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("SKU维度不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    skuDimensionName = skuMaps.get(skuDimensionName);
                    if (StrUtil.isNotBlank(skuDimensionName)){
                        try {
                            skuDimension= Integer.parseInt(skuDimensionName);
                        }catch (Exception e){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("SKU维度配置错误，请联系管理员，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }else {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("SKU维度配置错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                    if (!"2".equals(skuDimensionName)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("SKU维度只能为“二维”，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * SKU1类型
                 */
                String sku1TypeName = objects.get(8)==null||objects.get(8)==""?null:objects.get(8).toString();
                String sku1Type = null;
                if(sku1TypeName == null){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("SKU1类型不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    sku1Type= skuTypeMaps.get(sku1TypeName);
                    if(StrUtil.isBlank(sku1Type)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("SKU1类型配置错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                    if (!ConstantsEms.SKUTYP_YS.equals(sku1Type)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("SKU1类型只能是“颜色”，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * SKU2类型
                 */
                String sku2TypeName = objects.get(9)==null||objects.get(9)==""?null:objects.get(9).toString();
                String sku2Type = null;
                if(sku2TypeName == null){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("SKU2类型不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    sku2Type= skuTypeMaps.get(sku2TypeName);
                    if(StrUtil.isBlank(sku2Type)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("SKU2类型配置错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                    if (!ConstantsEms.SKUTYP_CM.equals(sku2Type)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("SKU2类型只能是“尺码”，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * 上下装/套装
                 */
                String upDownSuitName = objects.get(10)==null||objects.get(10)==""?null:objects.get(10).toString();
                String upDownSuit = null;
                if(upDownSuitName == null){
                    if (!ConstantsEms.NO.equals(ApiThreadLocalUtil.get().getSysUser().getClient().getIsRequiredUpDownSuitProduct())) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("上下装/套装不能为空，导入失败！");
                        errMsgList.add(errMsg);
                    }
                } else if (StrUtil.isNotBlank(upDownSuitName)){
                    upDownSuit = upDownListMaps.get(upDownSuitName);
                    if(StrUtil.isBlank(upDownSuit)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("上下装/套装配置错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * 版型编码
                 */
                String modelCode = objects.get(11)==null||objects.get(11)==""?null:objects.get(11).toString();
                Long tecModelSid=null;
                // 版型的尺码组sid
                Long sku2GroupSidModel = null;
                // 版型尺码组中的尺码明细列表
                List<BasMaterialSku> basMaterialSkuList = new ArrayList<>();
                if(modelCode == null){
                    if (!ConstantsEms.NO.equals(ApiThreadLocalUtil.get().getSysUser().getClient().getIsRequiredModelProduct())) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("版型编码不能为空，导入失败！");
                        errMsgList.add(errMsg);
                    }
                } else if (StrUtil.isNotBlank(modelCode)){
                    try {
                        TecModel tecModel = tecModelMapper.selectOne(new QueryWrapper<TecModel>().lambda()
                                .eq(TecModel::getModelCode, modelCode));
                        if(tecModel==null){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("版型"+modelCode+"不存在，导入失败！");
                            errMsgList.add(errMsg);
                        }else{
                            if(!tecModel.getStatus().equals(ConstantsEms.ENABLE_STATUS)||!tecModel.getHandleStatus().equals(ConstantsEms.CHECK_STATUS)){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("对应的版型必须是确认且已启用的状态，导入失败！");
                                errMsgList.add(errMsg);
                            }
                            tecModelSid = tecModel.getModelSid();
                            sku2GroupSidModel = tecModel.getSkuGroupSid();
                        }
                    }catch (Exception e){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(modelCode +"版型档案存在重复，请先检查该版型，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * 我司样衣号
                 */
                String sampleCodeSelf =objects.get(12)==null||objects.get(12)==""?null:objects.get(12).toString();
                if (sampleCodeSelf != null){
                    // 判断是否与表格内的我司样衣号重复
                    if (sampleCodeSelfMap.get(sampleCodeSelf) == null){
                        sampleCodeSelfMap.put(sampleCodeSelf,String.valueOf(num));
                        List<BasMaterial> materialList = basMaterialMapper.selectList(new QueryWrapper<BasMaterial>().lambda()
                                .eq(BasMaterial::getSampleCodeSelf,sampleCodeSelf));
                        if (CollectionUtil.isNotEmpty(materialList)){
                            sampleCodeSelfMsg = sampleCodeSelfMsg + materialName + ";";
                        }
                    }else {
                        sampleCodeSelfMsg = sampleCodeSelfMsg + materialName + ";";
                    }
                    if (sampleCodeSelf.length() > 30){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("我司样衣号长度不能大于30位，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * 客户简称
                 */
                String customerShortName = objects.get(13)==null||objects.get(13) == ""?null:objects.get(13).toString();
                String customerName = null;
                Long customerSid=null;
                if(customerShortName != null){
                    try {
                        BasCustomer basCustomer = basCustomerMapper.selectOne(new QueryWrapper<BasCustomer>()
                                .lambda().eq(BasCustomer::getShortName, customerShortName));
                        if(basCustomer==null){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("客户"+ customerShortName+ "不存在，导入失败！");
                            errMsgList.add(errMsg);
                        }else{
                            if(!basCustomer.getStatus().equals(ConstantsEms.ENABLE_STATUS)||!basCustomer.getHandleStatus().equals(ConstantsEms.CHECK_STATUS)){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("对应的客户必须是确认且已启用的状态，导入失败！");
                                errMsgList.add(errMsg);
                            }
                            customerSid=basCustomer.getCustomerSid();
                        }
                    }catch (Exception e){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(customerShortName +"客户档案存在重复，请先检查该客户，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * 客户品牌名称
                 */
                String customerBrandName = objects.get(14)==null||objects.get(14) == ""?null:objects.get(14).toString();
                Long customerBrandSid = null;
                if(customerBrandName != null){
                    if(customerShortName==null){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("客户品牌名称存在时，客户简称为必填，导入失败！");
                        errMsgList.add(errMsg);
                    }else{
                        try {
                            BasCustomerBrand brand = basCustomerBrandMapper.selectOne(new QueryWrapper<BasCustomerBrand>().lambda()
                                    .eq(BasCustomerBrand::getBrandName,customerBrandName)
                                    .eq(BasCustomerBrand::getCustomerSid,customerSid));
                            if (brand == null){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("客户"+customerShortName + "下不存在名称为"+ customerBrandName+ "的客户品牌，导入失败！");
                                errMsgList.add(errMsg);
                            }else {
                                if (!ConstantsEms.ENABLE_STATUS.equals(brand.getStatus())){
                                    errMsg = new CommonErrMsgResponse();
                                    errMsg.setItemNum(num);
                                    errMsg.setMsg("对应的客户品牌名称必须是已启用的状态，导入失败！");
                                    errMsgList.add(errMsg);
                                }
                                customerBrandSid = brand.getCustomerBrandSid();
                            }
                        }catch (Exception e){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("客户" + customerShortName +"下的"+customerBrandName+"存在重复，请先检查该客户品牌，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 客方样衣号
                 */
                String sampleCodeCustomer = objects.get(15)==""||objects.get(15)==null?null:objects.get(15).toString();
                if (sampleCodeCustomer != null && sampleCodeCustomer.length() > 30){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("客方样衣号长度不能大于30位，导入失败！");
                    errMsgList.add(errMsg);
                }
                /*
                 * 客方商品编码(款号)
                 */
                String customerProductCode=objects.get(16)==null||objects.get(16) == ""?null:objects.get(16).toString();
                if (customerProductCode != null && customerProductCode.length() > 30){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("客方商品编码(款号)长度不能大于30位，导入失败！");
                    errMsgList.add(errMsg);
                }
                /*
                 * 甲供料/客供料方式
                 */
                String rawMaterialName = objects.get(17)==""||objects.get(17)==null?null:objects.get(17).toString();
                String rawMaterial = null;
                if (rawMaterialName != null){
                    rawMaterial = rawMaterialMaps.get(rawMaterialName);
                    if(StrUtil.isBlank(rawMaterial)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("甲供料方式配置错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * 尺码组名称
                 */
                String sku2GroupName = objects.get(20)==""||objects.get(20)==null?null:objects.get(20).toString();
                Long sku2GroupSid = null;
                String sku2GroupCode = null;
                if (StrUtil.isNotBlank(sku2GroupName)) {
                    BasSkuGroup skuGroup = basSkuGroupMapper.selectOne(new QueryWrapper<BasSkuGroup>()
                            .lambda().eq(BasSkuGroup::getSkuGroupName, sku2GroupName)
                            .eq(BasSkuGroup::getSkuType, ConstantsEms.SKUTYP_CM));
                    if (skuGroup == null) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("尺码组"+ sku2GroupName+ "不存在，导入失败！");
                        errMsgList.add(errMsg);
                    }
                    else {
                        sku2GroupSid = skuGroup.getSkuGroupSid();
                        sku2GroupCode = skuGroup.getSkuGroupCode();
                    }
                }
                else {
                    if (!ConstantsEms.NO.equals(ApiThreadLocalUtil.get().getSysUser().getClient().getIsRequiredSku2GroupProduct())) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("尺码组不能为空，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                // 自动带出尺码组或者版型的尺码
                Long cmGroupSid = null;
                cmGroupSid = sku2GroupSid == null ? sku2GroupSidModel : sku2GroupSid;
                List<BasMaterialSku> basMaterialCmSkuList = new ArrayList<>();
                if (cmGroupSid != null) {
                    List<BasSkuGroupItem> groupItems = basSkuGroupItemMapper.selectBasSkuGroupItemList(new BasSkuGroupItem()
                            .setSkuGroupSid(cmGroupSid).setSkuStatus(ConstantsEms.ENABLE_STATUS));
                    if (CollectionUtil.isNotEmpty(groupItems)) {
                        groupItems.forEach(item->{
                            BasMaterialSku materialSku = new BasMaterialSku();
                            materialSku.setSkuSid(Long.parseLong(item.getSkuSid())).setSkuType(item.getSkuType());
                            materialSku.setStatus(ConstantsEms.ENABLE_STATUS).setCreateDate(new Date())
                                    .setSort(item.getSort())
                                    .setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
                            basMaterialSkuList.add(materialSku);
                            basMaterialCmSkuList.add(materialSku);
                        });
                    }
                }
                /*
                 * SKU1名称
                 */
                String sku1Names = objects.get(18)==""||objects.get(18)==null?null:objects.get(18).toString();
                if(sku1Names!=null){
                    if (ConstantsEms.NO.equals(isSkuMaterial)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("非SKU物料，SKU1名称应为空，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        if (sku1TypeName == null){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("SKU1类型为空时，SKU1名称应为空，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                    String[] skus = sku1Names.split(";|；");
                    //字符串拆分数组后利用set去重复
                    Set<String> staffsSet = new HashSet<>(Arrays.asList(skus));
                    for (String s : staffsSet) {
                        BasSku basSku = basSkuMapper.selectOne(new QueryWrapper<BasSku>().lambda().eq(BasSku::getSkuType,sku1Type)
                                .eq(BasSku::getSkuName, s));
                        if (basSku == null){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("找不到SKU类型为" + sku1TypeName + "的 " + s +" 档案，导入失败！");
                            errMsgList.add(errMsg);
                        } else {
                            if (!(ConstantsEms.CHECK_STATUS.equals(basSku.getHandleStatus()) && ConstantsEms.ENABLE_STATUS.equals(basSku.getStatus()))){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("SKU类型为" + sku1TypeName + "的 " + s +" 档案必须是确认且已启用的状态，导入失败！");
                                errMsgList.add(errMsg);
                            } else {
                                //存进商品sku明细列表
                                BasMaterialSku materialSku = new BasMaterialSku();
                                materialSku.setSkuSid(basSku.getSkuSid()).setSkuType(basSku.getSkuType());
                                materialSku.setStatus(ConstantsEms.ENABLE_STATUS).setCreateDate(new Date()).setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
                                basMaterialSkuList.add(materialSku);
                            }
                        }
                    }
                }
                /*
                 * SKU2名称
                 */
                String sku2Names = objects.get(19)==""||objects.get(19)==null?null:objects.get(19).toString();
                if(sku2Names!=null){
                    if (ConstantsEms.NO.equals(isSkuMaterial)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("非SKU物料，SKU2名称应为空，导入失败！");
                        errMsgList.add(errMsg);
                    }
                    else {
                        if (sku2TypeName == null){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("SKU2类型为空时，SKU2名称应为空，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                    String[] skus = sku2Names.split(";|；");
                    Set<String> staffsSet = new HashSet<>(Arrays.asList(skus));
                    for (String s : staffsSet) {
                        BasSku basSku = basSkuMapper.selectOne(new QueryWrapper<BasSku>().lambda().eq(BasSku::getSkuType,sku2Type)
                                .eq(BasSku::getSkuName, s));
                        if (basSku == null){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("找不到SKU类型为" + sku2TypeName + "的 " + s +" 档案，导入失败！");
                            errMsgList.add(errMsg);
                        } else {
                            if (!(ConstantsEms.CHECK_STATUS.equals(basSku.getHandleStatus()) && ConstantsEms.ENABLE_STATUS.equals(basSku.getStatus()))){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("SKU类型为" + sku2TypeName + "的 " + s +" 档案必须是确认且已启用的状态，导入失败！");
                                errMsgList.add(errMsg);
                            } else {
                                // 因为默认 sku2 是 尺码 所以只在这里进行判断而 sku1Name没有判断
                                if (ConstantsEms.SKUTYP_CM.equals(sku2Type) && CollectionUtil.isNotEmpty(basMaterialCmSkuList)) {
                                    boolean flag = basMaterialCmSkuList.stream().anyMatch(o->o.getSkuSid().equals(basSku.getSkuSid()));
                                    if (!flag) {
                                        warnMsg = new CommonErrMsgResponse();
                                        warnMsg.setItemNum(num);
                                        if (sku2GroupSid != null) {
                                            warnMsg.setMsg("尺码组" + sku2GroupName + "中没有尺码" + s + "，是否继续导入！");
                                        }
                                        else if (sku2GroupSidModel != null) {
                                            warnMsg.setMsg("版型" + modelCode + "的尺码组中没有尺码" + s + "，是否继续导入！");
                                        }
                                        warnMsgList.add(warnMsg);
                                    }
                                }
                                //存进商品sku明细列表
                                BasMaterialSku materialSku = new BasMaterialSku();
                                materialSku.setSkuSid(basSku.getSkuSid()).setSkuType(basSku.getSkuType());
                                materialSku.setStatus(ConstantsEms.ENABLE_STATUS).setCreateDate(new Date()).setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
                                basMaterialSkuList.add(materialSku);
                            }
                        }
                    }
                }
                //款色去重 （主要目的跟版型中的尺码明细去重复）
                Set<BasMaterialSku> set = new TreeSet<>(new Comparator<BasMaterialSku>() {
                    @Override
                    public int compare(BasMaterialSku o1, BasMaterialSku o2) {
                        return o1.getSkuSid().compareTo(o2.getSkuSid());
                    }
                });
                set.addAll(basMaterialSkuList);
                //得到去重后的sku明细
                List<BasMaterialSku> materialSkuList = new ArrayList<>(set);
                /*
                 * 吊牌零售价(元)
                 */
                String retailPrice_s = objects.get(21)==""||objects.get(21)==null?null:objects.get(21).toString();
                BigDecimal retailPrice = null;
                if (retailPrice_s != null){
                    if (!JudgeFormat.isValidDouble(retailPrice_s,9,4)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("吊牌零售价(元)数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        retailPrice = new BigDecimal(retailPrice_s);
                        if (retailPrice != null && BigDecimal.ZERO.compareTo(retailPrice) > 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("吊牌零售价(元)不能小于0，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 目标成本(元)
                 */
                String costTargetTax_s = objects.get(22)==""||objects.get(22)==null?null:objects.get(22).toString();
                BigDecimal costTargetTax = null;
                if (costTargetTax_s != null){
                    if (!JudgeFormat.isValidDouble(costTargetTax_s,10,5)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("目标成本(元)数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        costTargetTax = new BigDecimal(costTargetTax_s);
                        if (costTargetTax != null && BigDecimal.ZERO.compareTo(costTargetTax) > 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("目标成本(元)不能小于0，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 快速编码
                 */
                String simpleCode = objects.get(23)==""||objects.get(23)==null?null:objects.get(23).toString();
                if (simpleCode != null && simpleCode.length() > 20){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("快速编码长度不能大于20位，导入失败！");
                    errMsgList.add(errMsg);
                }
                /*
                 * 设计师账号
                 */
                String designer =objects.get(24)==""||objects.get(24)==null?null:objects.get(24).toString();
                if (designer != null){
                    try {
                        SysUser user = userMapper.selectOne(new QueryWrapper<SysUser>().lambda().eq(SysUser::getUserName,designer));
                        if (user == null){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("对应的设计师账号不存在，导入失败！");
                            errMsgList.add(errMsg);
                        }else {
                            if (!ConstantsEms.SYS_COMMON_STATUS_Y.equals(user.getStatus())){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("对应的设计师账号必须是已启用的状态，导入失败！");
                                errMsgList.add(errMsg);
                            }
                        }
                    }catch (Exception e){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("设计师账号" + designer +"存在重复，请先检查该设计师账号，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * 男女装
                 */
                String maleFemale = objects.get(25)==""||objects.get(25)==null?null:objects.get(25).toString();
                if (maleFemale != null){
                    maleFemale = suitGenderMaps.get(maleFemale);
                    if(StrUtil.isBlank(maleFemale)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("男女装配置错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * 卖点说明
                 */
                String sellPointDesc = objects.get(26)==""||objects.get(26)==null?null:objects.get(26).toString();
                if (sellPointDesc != null && sellPointDesc.length() > 1800){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("卖点说明长度不能大于1800位，导入失败！");
                    errMsgList.add(errMsg);
                }
                /*
                 * 成分说明
                 */
                String composition = objects.get(27)==""||objects.get(27)==null?null:objects.get(27).toString();
                if (composition != null && composition.length() > 180){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("成分说明长度不能大于180位，导入失败！");
                    errMsgList.add(errMsg);
                }
                /*
                 * 特殊工艺说明
                 */
                String specialCraft = objects.get(28)==""||objects.get(28)==null?null:objects.get(28).toString();
                if (specialCraft != null && specialCraft.length() > 600){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("特殊工艺说明长度不能大于600位，导入失败！");
                    errMsgList.add(errMsg);
                }
                /*
                 * 季节
                 */
                String season = objects.get(29)==""||objects.get(29)==null?null:objects.get(29).toString();
                if (season != null){
                    season = seasonMaps.get(season);
                    if(StrUtil.isBlank(season)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("季节配置错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * 备注
                 */
                String remark = objects.get(30)==""||objects.get(30)==null?null:objects.get(30).toString();
                if (remark != null && remark.length() > 600){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("备注长度不能大于600位，导入失败！");
                    errMsgList.add(errMsg);
                }
                BasMaterial basMaterial = new BasMaterial();
                basMaterial.setMaterialName(materialName);
                basMaterial.setMaterialCode(materialCode);
                if (sku2GroupSid == null && sku2GroupSidModel != null) {
                    basMaterial.setSku2GroupSid(sku2GroupSidModel);
                }
                else {
                    basMaterial.setSku2GroupSid(sku2GroupSid);
                }
                basMaterial.setMaterialType(materialType)
                        .setProductSeasonSid(productSeasonSid).setInventoryPriceMethod(ConstantsEms.INVENTORY_PRICE_METHOD_JQPJJ)
                        .setProductTechniqueType(productTechnique)
                        .setIsSkuMaterial(isSkuMaterial)
                        .setStatus(ConstantsEms.ENABLE_STATUS).setHandleStatus(ConstantsEms.SAVA_STATUS)
                        .setSkuDimension(skuDimension).setSku1Type(sku1Type).setSku2Type(sku2Type)
                        .setUpDownSuit(upDownSuit).setModelSid(tecModelSid)
                        .setSampleCodeSelf(sampleCodeSelf).setSampleCodeCustomer(sampleCodeCustomer)
                        .setCustomerProductCode(customerProductCode)
                        .setCustomerSid(customerSid).setCustomerBrandSid(customerBrandSid)
                        .setUnitBase(unitBase).setRawMaterialMode(rawMaterial)
                        .setRetailPrice(retailPrice).setCostTargetTax(costTargetTax)
                        .setSimpleCode(simpleCode).setDesignerAccount(designer)
                        .setMaleFemaleFlag(maleFemale).setSeason(season)
                        .setSellPointDesc(sellPointDesc).setComposition(composition)
                        .setSpecialCraft(specialCraft).setRemark(remark)
                        .setMaterialCategory(ConstantsEms.MATERIAL_CATEGORY_SP)
                        .setImportType(BusinessType.IMPORT.getValue());
                // 写入sku明细
                basMaterial.setBasMaterialSkuList(materialSkuList);
                basMaterialList.add(basMaterial);
            }
        }catch (BaseException e){
            throw new BaseException(e.getDefaultMessage());
        }
        //检查有没有报错

        //检查有没有报错
        if (CollectionUtil.isNotEmpty(errMsgList)){
            res.put("errList",errMsgList);
            res.put("warn",null);
            res.put("tableData",null);
            return res;
        }else {
            if (CollectionUtil.isNotEmpty(warnMsgList)){
                res.put("errList",warnMsgList);
                res.put("warn",true);
                res.put("tableData",basMaterialList);
                return res;
            }
        }
        if(CollectionUtils.isNotEmpty(basMaterialList)){
            basMaterialList.forEach(item->{
                basMaterialService.insertBasMaterial(item);
            });
        }
        //我司样衣号的提示
        if (StrUtil.isNotBlank(sampleCodeSelfMsg)){
            if (sampleCodeSelfMsg.endsWith(";")) {
                sampleCodeSelfMsg = sampleCodeSelfMsg.substring(0,sampleCodeSelfMsg.length() - 1);
            }
            sampleCodeSelfMsg = "导入成功，" + sampleCodeSelfMsg + "的我司样衣号已存在";
            res.put("success",basMaterialList.size());
            res.put("message",sampleCodeSelfMsg);
            return res;
        }
        res.put("success",basMaterialList.size());
        return res;
    }

    /**
     * 普通商品
     *
     * @author chenkw
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public HashMap<String, Object> importDataProduct(MultipartFile file) {
        HashMap<String, Object> res = new HashMap<>();
        List<BasMaterial> basMaterialList=new ArrayList<>();
        // 我司样衣号的提示信息
        String sampleCodeSelfMsg = "";
        //错误信息
        List<CommonErrMsgResponse> errMsgList = new ArrayList<>();
        CommonErrMsgResponse errMsg = null;
        //继续导入
        List<CommonErrMsgResponse> warnMsgList = new ArrayList<>();
        CommonErrMsgResponse warnMsg = null;
        String industryField = ApiThreadLocalUtil.get().getSysUser().getIndustryField();
        try{
            File toFile=null;
            try {
                toFile   = FileUtils.multipartFileToFile(file);
            }catch (Exception e){
                e.getMessage();
                throw new BaseException("文件转换失败");
            }
            ExcelReader reader = cn.hutool.poi.excel.ExcelUtil.getReader(toFile);
            FileUtils.delteTempFile(toFile);
            List<List<Object>> readAll = reader.read();
            //是否sku
            List<DictData> isSkuList=sysDictDataService.selectDictData("sys_yes_no");
            isSkuList = isSkuList.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String,String> isSkuMaps=isSkuList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue,(key1, key2)->key2));
            //sku维度
            List<DictData> skuList=sysDictDataService.selectDictData("s_sku_dimension");
            skuList = skuList.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String,String> skuMaps=skuList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue,(key1, key2)->key2));
            //sku类型
            List<DictData> skuTypeList=sysDictDataService.selectDictData("s_sku_type");
            skuTypeList = skuTypeList.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String,String> skuTypeMaps=skuTypeList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue,(key1, key2)->key2));
            //excel表里面编码和名称的缓存
            HashMap<String, String> codeMap = new HashMap<>();
            HashMap<String, String> sampleCodeSelfMap = new HashMap<>();
            for (int i = 0; i < readAll.size(); i++) {
                int num=i+1;
                if(i<2){
                    //前两行跳过
                    continue;
                }
                List<Object> objects=readAll.get(i);
                copy(objects, readAll);
                /*
                 * 商品编码
                 */
                String materialCode = objects.get(0)==null||objects.get(0)==""?null:objects.get(0).toString();
                if(StrUtil.isBlank(materialCode)){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("商品编码不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    // 不支持汉字
                    boolean numberOrE = JudgeFormat.isNumberOrE(materialCode);
                    if(!numberOrE){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("商品编码数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                    // 判断是否与表格内的编码重复
                    if (codeMap.get(materialCode) == null){
                        codeMap.put(materialCode,String.valueOf(num));
                        List<BasMaterial> materialList = basMaterialMapper.selectList(new QueryWrapper<BasMaterial>().lambda()
                                .eq(BasMaterial::getMaterialCode,materialCode));
                        if (CollectionUtil.isNotEmpty(materialList)){
                            continue;
                        }
                    }else {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("商品编码表格内重复，导入失败！");
                        errMsgList.add(errMsg);
                    }
                    if (materialCode.length() > 20){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("商品编码长度不能大于20位，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * 商品名称
                 */
                String materialName = objects.get(1)==null||objects.get(1)==""?null:objects.get(1).toString();
                if(StrUtil.isBlank(materialName)){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("商品名称不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    if (materialName.length() > 300){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("商品名称长度不能大于300位，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * 商品类型
                 */
                String materialTypeName = objects.get(2)==null||objects.get(2)==""?null:objects.get(2).toString();
                String materialType = null;
                if(StrUtil.isNotBlank(materialTypeName)){
                    try {
                        ConMaterialType conMaterialType = conMaterialTypeMapper.selectOne(new QueryWrapper<ConMaterialType>().lambda()
                                .eq(ConMaterialType::getName,materialTypeName).eq(ConMaterialType::getHandleStatus, ConstantsEms.CHECK_STATUS)
                                .eq(ConMaterialType::getStatus, ConstantsEms.ENABLE_STATUS).eq(ConMaterialType::getMaterialCategory, ConstantsEms.MATERIAL_CATEGORY_SP));
                        if(conMaterialType == null){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("商品类型填写错误，导入失败！");
                            errMsgList.add(errMsg);
                        } else {
                            materialType = conMaterialType.getCode();
                        }
                    }catch (Exception e){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(materialTypeName +"商品类型档案存在重复，请先检查该商品类型，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * 基本计量单位
                 */
                String unitBaseName = objects.get(3)==null||objects.get(3)==""?null:objects.get(3).toString();
                String unitBase = null;
                if(unitBaseName == null){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("基本计量单位不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    try {
                        ConMeasureUnit measureUnit = conMeasureUnitMapper.selectOne(new QueryWrapper<ConMeasureUnit>().lambda()
                                .eq(ConMeasureUnit::getName,unitBaseName).eq(ConMeasureUnit::getHandleStatus, ConstantsEms.CHECK_STATUS)
                                .eq(ConMeasureUnit::getStatus, ConstantsEms.ENABLE_STATUS));
                        if(measureUnit == null){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("基本计量单位填写错误，导入失败！");
                            errMsgList.add(errMsg);
                        }else {
                            unitBase = measureUnit.getCode();
                        }
                    }catch (Exception e){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(unitBaseName +"基本计量单位档案存在重复，请先检查该基本计量单位，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * 是否SKU商品
                 */
                String isSkuMaterialName = objects.get(4)==null||objects.get(4)==""?null:objects.get(4).toString();
                String isSkuMaterial = null;
                if(StrUtil.isBlank(isSkuMaterialName)){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("是否SKU商品不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    isSkuMaterial=isSkuMaps.get(isSkuMaterialName);
                    if(StrUtil.isBlank(isSkuMaterial)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("是否SKU商品填写错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * SKU维度数
                 */
                String skuDimensionName = objects.get(5)==null||objects.get(5)==""?null:objects.get(5).toString();
                String skuDe = null;
                Integer skuDimension = null;
                if(ConstantsEms.YES.equals(isSkuMaterial) && StrUtil.isBlank(skuDimensionName)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("SKU维度数不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else if (ConstantsEms.YES.equals(isSkuMaterial) && StrUtil.isNotBlank(skuDimensionName)) {
                    skuDe = skuMaps.get(skuDimensionName);
                    if (StrUtil.isNotBlank(skuDe)) {
                        try {
                            skuDimension= Integer.parseInt(skuDe);
                        }catch (Exception e){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("SKU维度数配置错误，请联系管理员，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    } else {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("SKU维度数填写错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                } else if (ConstantsEms.NO.equals(isSkuMaterial) && StrUtil.isNotBlank(skuDimensionName)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("是否SKU商品为否时，SKU维度必须为空，导入失败！");
                    errMsgList.add(errMsg);
                }
                /*
                 * SKU1类型
                 */
                String sku1TypeName = objects.get(6)==null||objects.get(6)==""?null:objects.get(6).toString();
                String sku1Type = null;
                if (ConstantsEms.YES.equals(isSkuMaterial) && StrUtil.isBlank(sku1TypeName)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("SKU1类型不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else if (ConstantsEms.YES.equals(isSkuMaterial) && StrUtil.isNotBlank(sku1TypeName)) {
                    sku1Type= skuTypeMaps.get(sku1TypeName);
                    if(StrUtil.isBlank(sku1Type)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("SKU1类型填写错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                } else if (ConstantsEms.NO.equals(isSkuMaterial) && StrUtil.isNotBlank(sku1TypeName)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("是否SKU商品为否时，SKU1类型必须为空，导入失败！");
                    errMsgList.add(errMsg);
                }
                /*
                 * SKU2类型
                 */
                String sku2TypeName = objects.get(7)==null||objects.get(7)==""?null:objects.get(7).toString();
                String sku2Type = null;
                if (ConstantsEms.YES.equals(isSkuMaterial) && skuDimension != null && skuDimension == 2 && StrUtil.isBlank(sku2TypeName)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("SKU2类型不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }  else if (ConstantsEms.YES.equals(isSkuMaterial) && skuDimension != null && skuDimension != 2 && StrUtil.isNotBlank(sku2TypeName)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("SKU维度数不是二维时，SKU2类型必须为空，导入失败！");
                    errMsgList.add(errMsg);
                }  else if (ConstantsEms.YES.equals(isSkuMaterial) && skuDimension != null && skuDimension == 2 && StrUtil.isNotBlank(sku2TypeName)) {
                    sku2Type= skuTypeMaps.get(sku2TypeName);
                    if(StrUtil.isBlank(sku2Type)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("SKU2类型填写错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                    else if (ConstantsEms.SKUTYP_YS.equals(sku2Type)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("“颜色”只能是SKU1类型，导入失败！");
                        errMsgList.add(errMsg);
                    }
                    else if (sku2Type.equals(sku1Type)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("SKU2类型与SKU1类型必须不同，导入失败！");
                        errMsgList.add(errMsg);
                    }
                } else if (ConstantsEms.NO.equals(isSkuMaterial) && StrUtil.isNotBlank(sku2TypeName)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("是否SKU商品为否时，SKU2类型必须为空，导入失败！");
                    errMsgList.add(errMsg);
                }
                /*
                 * 供应商简称
                 */
                String vendorShortName = objects.get(8) == null || objects.get(8) == "" ? null : objects.get(8).toString();
                Long vendorSid = null;
                if (StrUtil.isNotBlank(vendorShortName)) {
                    try {
                        BasVendor basVendor = basVendorMapper.selectOne(new QueryWrapper<BasVendor>()
                                .lambda().eq(BasVendor::getShortName, vendorShortName));
                        if (basVendor==null) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg(vendorShortName + "对应的供应商不存在，导入失败！");
                            errMsgList.add(errMsg);
                        } else {
                            if (!ConstantsEms.ENABLE_STATUS.equals(basVendor.getStatus()) || !ConstantsEms.CHECK_STATUS.equals(basVendor.getHandleStatus())) {
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg(vendorShortName + "对应的供应商未确认或已停用，导入失败！");
                                errMsgList.add(errMsg);
                            }
                            vendorSid = basVendor.getVendorSid();
                        }
                    } catch (Exception e) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(vendorShortName + "供应商档案存在重复，请先检查该供应商，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * 供方编码
                 */
                String supplierProductCode = objects.get(9) == null || objects.get(9) == "" ? null  : objects.get(9).toString();
                if (StrUtil.isNotBlank(supplierProductCode)) {
                    if (supplierProductCode.length() > 30){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("供方编码长度不能大于30位，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * 规格
                 */
                String specificationSize = objects.get(10)==null || objects.get(10) == "" ? null : objects.get(10).toString();
                if (specificationSize != null && specificationSize.length() > 180) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("规格长度不能大于180位，导入失败！");
                    errMsgList.add(errMsg);
                }
                /*
                 * 型号
                 */
                String modelSize = objects.get(11)==null || objects.get(11) == "" ? null : objects.get(11).toString();
                if (modelSize != null && modelSize.length() > 180) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("型号长度不能大于180位，导入失败！");
                    errMsgList.add(errMsg);
                }
                /*
                 * 材质
                 */
                String materialComposition = objects.get(12) == null || objects.get(12) == "" ? null : objects.get(12).toString();
                if (materialComposition != null && materialComposition.length() > 180) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("材质长度不能大于180位，导入失败！");
                    errMsgList.add(errMsg);
                }
                /*
                 * 成分
                 */
                String composition = objects.get(13) == "" || objects.get(13) == null ? null : objects.get(13).toString();
                if (composition != null && composition.length() > 180) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("成分长度不能大于180位，导入失败！");
                    errMsgList.add(errMsg);
                }
                /*
                 * 成本价(元)
                 */
                String priceCostTax_s = objects.get(14)==""||objects.get(14)==null?null:objects.get(14).toString();
                BigDecimal priceCostTax = null;
                if (priceCostTax_s != null){
                    if (!JudgeFormat.isValidDouble(priceCostTax_s,10,5)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("成本价(元)数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        priceCostTax = new BigDecimal(priceCostTax_s);
                        if (priceCostTax != null && BigDecimal.ZERO.compareTo(priceCostTax) > 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("成本价(元)不能小于0，导入失败！");
                            errMsgList.add(errMsg);
                        }
                        else {
                            priceCostTax = priceCostTax.divide(BigDecimal.ONE,2,BigDecimal.ROUND_HALF_UP);
                        }
                    }
                }
                /*
                 * 公司简称
                 */
                String companyShortName = objects.get(15) == null || objects.get(15) == "" ? null : objects.get(15).toString();
                Long companySid = null;
                if (StrUtil.isNotBlank(companyShortName)) {
                    try {
                        BasCompany basCompany = basCompanyMapper.selectOne(new QueryWrapper<BasCompany>()
                                .lambda().eq(BasCompany::getShortName, companyShortName));
                        if (basCompany==null) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg(companyShortName + "对应的公司不存在，导入失败！");
                            errMsgList.add(errMsg);
                        } else {
                            if (!ConstantsEms.ENABLE_STATUS.equals(basCompany.getStatus()) || !ConstantsEms.CHECK_STATUS.equals(basCompany.getHandleStatus())) {
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg(companyShortName + "对应的公司未确认或已停用，导入失败！");
                                errMsgList.add(errMsg);
                            }
                            companySid = basCompany.getCompanySid();
                        }
                    } catch (Exception e) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(companyShortName + "公司档案存在重复，请先检查该公司，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * 品牌
                 */
                String companyBrandName = objects.get(16)==null||objects.get(16) == ""?null:objects.get(16).toString();
                Long companyBrandSid = null;
                if(companyBrandName != null){
                    if(companyShortName==null){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("品牌存在时，公司简称为必填，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        try {
                            BasCompanyBrand brand = basCompanyBrandMapper.selectOne(new QueryWrapper<BasCompanyBrand>().lambda()
                                    .eq(BasCompanyBrand::getBrandName, companyBrandName)
                                    .eq(BasCompanyBrand::getCompanySid, companySid));
                            if (brand == null) {
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("公司"+companyShortName + "下不存在名称为"+ companyBrandName+ "的品牌，导入失败！");
                                errMsgList.add(errMsg);
                            } else {
                                if (!ConstantsEms.ENABLE_STATUS.equals(brand.getStatus())){
                                    errMsg = new CommonErrMsgResponse();
                                    errMsg.setItemNum(num);
                                    errMsg.setMsg("对应的品牌名称必须是已启用的状态，导入失败！");
                                    errMsgList.add(errMsg);
                                }
                                companyBrandSid = brand.getCompanyBrandSid();
                            }
                        }catch (Exception e){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("公司" + companyShortName +"下的"+companyBrandName+"存在重复，请先检查该品牌，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 客户简称
                 */
                String customerShortName = objects.get(17)==null||objects.get(17) == ""?null:objects.get(17).toString();
                String customerName = null;
                Long customerSid=null;
                if(customerShortName != null){
                    try {
                        BasCustomer basCustomer = basCustomerMapper.selectOne(new QueryWrapper<BasCustomer>()
                                .lambda().eq(BasCustomer::getShortName, customerShortName));
                        if(basCustomer==null){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("客户"+ customerShortName+ "不存在，导入失败！");
                            errMsgList.add(errMsg);
                        }else{
                            if(!basCustomer.getStatus().equals(ConstantsEms.ENABLE_STATUS)||!basCustomer.getHandleStatus().equals(ConstantsEms.CHECK_STATUS)){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg(customerShortName + "对应的客户未确认或已停用，导入失败！");
                                errMsgList.add(errMsg);
                            }
                            customerSid=basCustomer.getCustomerSid();
                        }
                    }catch (Exception e){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(customerShortName +"客户档案存在重复，请先检查该客户，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * 客方品牌
                 */
                String customerBrandName = objects.get(18)==null||objects.get(18) == ""?null:objects.get(18).toString();
                Long customerBrandSid = null;
                if(customerBrandName != null){
                    if(customerShortName==null){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("客方品牌存在时，客户简称为必填，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        try {
                            BasCustomerBrand brand = basCustomerBrandMapper.selectOne(new QueryWrapper<BasCustomerBrand>().lambda()
                                    .eq(BasCustomerBrand::getBrandName,customerBrandName)
                                    .eq(BasCustomerBrand::getCustomerSid,customerSid));
                            if (brand == null) {
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("客户"+customerShortName + "下不存在名称为"+ customerBrandName+ "的客户品牌，导入失败！");
                                errMsgList.add(errMsg);
                            } else {
                                if (!ConstantsEms.ENABLE_STATUS.equals(brand.getStatus())){
                                    errMsg = new CommonErrMsgResponse();
                                    errMsg.setItemNum(num);
                                    errMsg.setMsg("对应的客方品牌名称必须是已启用的状态，导入失败！");
                                    errMsgList.add(errMsg);
                                }
                                customerBrandSid = brand.getCustomerBrandSid();
                            }
                        }catch (Exception e){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("客户" + customerShortName +"下的"+customerBrandName+"存在重复，请先检查该客方品牌，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                // 尺码明细列表
                List<BasMaterialSku> basMaterialSkuList = new ArrayList<>();
                /*
                 * SKU1名称
                 */
                String sku1Names = objects.get(19)==""||objects.get(19)==null?null:objects.get(19).toString();
                if(StrUtil.isNotBlank(sku1Type) && StrUtil.isNotBlank(sku1Names)){
                    if (ConstantsEms.NO.equals(isSkuMaterial)) {
                        warnMsg = new CommonErrMsgResponse();
                        warnMsg.setItemNum(num);
                        warnMsg.setMsg("非SKU商品，SKU1名称应为空，是否继续导入！");
                        warnMsgList.add(warnMsg);
                    } else {
                        if (StrUtil.isBlank(sku1TypeName)){
                            warnMsg = new CommonErrMsgResponse();
                            warnMsg.setItemNum(num);
                            warnMsg.setMsg("SKU1类型为空时，SKU1名称应为空，是否继续导入！");
                            warnMsgList.add(warnMsg);
                        }
                        else {
                            String[] skus = sku1Names.split(";|；");
                            //字符串拆分数组后利用set去重复
                            Set<String> staffsSet = new HashSet<>(Arrays.asList(skus));
                            for (String s : staffsSet) {
                                BasSku basSku = basSkuMapper.selectOne(new QueryWrapper<BasSku>().lambda().eq(BasSku::getSkuType,sku1Type)
                                        .eq(BasSku::getSkuName, s));
                                if (basSku == null){
                                    errMsg = new CommonErrMsgResponse();
                                    errMsg.setItemNum(num);
                                    errMsg.setMsg("找不到SKU类型为" + sku1TypeName + "的 " + s +" 档案，导入失败！");
                                    errMsgList.add(errMsg);
                                } else {
                                    if (!(ConstantsEms.CHECK_STATUS.equals(basSku.getHandleStatus()) && ConstantsEms.ENABLE_STATUS.equals(basSku.getStatus()))){
                                        errMsg = new CommonErrMsgResponse();
                                        errMsg.setItemNum(num);
                                        errMsg.setMsg("SKU类型为" + sku1TypeName + "的 " + s +" 档案未确认或已停用，导入失败！");
                                        errMsgList.add(errMsg);
                                    } else {
                                        //存进商品sku明细列表
                                        BasMaterialSku materialSku = new BasMaterialSku();
                                        materialSku.setSkuSid(basSku.getSkuSid()).setSkuType(basSku.getSkuType());
                                        materialSku.setStatus(ConstantsEms.ENABLE_STATUS).setCreateDate(new Date()).setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
                                        basMaterialSkuList.add(materialSku);
                                    }
                                }
                            }
                        }
                    }
                }
                /*
                 * SKU2名称
                 */
                String sku2Names = objects.get(20)==""||objects.get(20)==null?null:objects.get(20).toString();
                if(StrUtil.isNotBlank(sku2Type) && StrUtil.isNotBlank(sku2Names)){
                    if (ConstantsEms.NO.equals(isSkuMaterial)) {
                        warnMsg = new CommonErrMsgResponse();
                        warnMsg.setItemNum(num);
                        warnMsg.setMsg("非SKU商品，SKU2名称应为空，是否继续导入！");
                        warnMsgList.add(warnMsg);
                    }
                    else {
                        if (StrUtil.isBlank(sku2TypeName)){
                            warnMsg = new CommonErrMsgResponse();
                            warnMsg.setItemNum(num);
                            warnMsg.setMsg("SKU2类型为空时，SKU2名称应为空，是否继续导入！");
                            warnMsgList.add(warnMsg);
                        }
                        else {
                            String[] skus = sku2Names.split(";|；");
                            Set<String> staffsSet = new HashSet<>(Arrays.asList(skus));
                            for (String s : staffsSet) {
                                BasSku basSku = basSkuMapper.selectOne(new QueryWrapper<BasSku>().lambda().eq(BasSku::getSkuType,sku2Type)
                                        .eq(BasSku::getSkuName, s));
                                if (basSku == null){
                                    errMsg = new CommonErrMsgResponse();
                                    errMsg.setItemNum(num);
                                    errMsg.setMsg("找不到SKU类型为" + sku2TypeName + "的 " + s +" 档案，导入失败！");
                                    errMsgList.add(errMsg);
                                } else {
                                    if (!(ConstantsEms.CHECK_STATUS.equals(basSku.getHandleStatus()) && ConstantsEms.ENABLE_STATUS.equals(basSku.getStatus()))){
                                        errMsg = new CommonErrMsgResponse();
                                        errMsg.setItemNum(num);
                                        errMsg.setMsg("SKU类型为" + sku2TypeName + "的 " + s +" 档案未确认或已停用，导入失败！");
                                        errMsgList.add(errMsg);
                                    } else {
                                        //存进商品sku明细列表
                                        BasMaterialSku materialSku = new BasMaterialSku();
                                        materialSku.setSkuSid(basSku.getSkuSid()).setSkuType(basSku.getSkuType());
                                        materialSku.setStatus(ConstantsEms.ENABLE_STATUS).setCreateDate(new Date()).setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
                                        basMaterialSkuList.add(materialSku);
                                    }
                                }
                            }
                        }
                    }
                }
                //款色去重 （主要目的跟版型中的尺码明细去重复）
                Set<BasMaterialSku> set = new TreeSet<>(new Comparator<BasMaterialSku>() {
                    @Override
                    public int compare(BasMaterialSku o1, BasMaterialSku o2) {
                        return o1.getSkuSid().compareTo(o2.getSkuSid());
                    }
                });
                set.addAll(basMaterialSkuList);
                //得到去重后的sku明细
                List<BasMaterialSku> materialSkuList = new ArrayList<>(set);
                /*
                 * 备注
                 */
                String remark = objects.get(21)==""||objects.get(21)==null?null:objects.get(21).toString();
                if (remark != null && remark.length() > 600){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("备注长度不能大于600位，导入失败！");
                    errMsgList.add(errMsg);
                }
                BasMaterial basMaterial = new BasMaterial();
                basMaterial.setMaterialName(materialName);
                basMaterial.setMaterialCode(materialCode);
                basMaterial.setSupplierProductCode(supplierProductCode);
                basMaterial.setMaterialType(materialType)
                        .setInventoryPriceMethod(ConstantsEms.INVENTORY_PRICE_METHOD_JQPJJ)
                        .setIsSkuMaterial(isSkuMaterial)
                        .setVendorSid(vendorSid).setSpecificationSize(specificationSize)
                        .setModelSize(modelSize).setMaterialComposition(materialComposition)
                        .setComposition(composition).setRemark(remark)
                        .setCompanySid(companySid).setCompanyBrandSid(companyBrandSid)
                        .setStatus(ConstantsEms.ENABLE_STATUS).setHandleStatus(ConstantsEms.SAVA_STATUS)
                        .setSkuDimension(skuDimension).setSku1Type(sku1Type).setSku2Type(sku2Type)
                        .setCustomerSid(customerSid).setCustomerBrandSid(customerBrandSid)
                        .setUnitBase(unitBase).setPriceCostTax(priceCostTax)
                        .setMaterialCategory(ConstantsEms.MATERIAL_CATEGORY_SP)
                        .setImportType(BusinessType.IMPORT.getValue());
                // 写入sku明细
                basMaterial.setBasMaterialSkuList(materialSkuList);
                basMaterialList.add(basMaterial);
            }
        }catch (BaseException e){
            throw new BaseException(e.getDefaultMessage());
        }
        //检查有没有报错

        //检查有没有报错
        if (CollectionUtil.isNotEmpty(errMsgList)){
            res.put("errList",errMsgList);
            res.put("warn",null);
            res.put("tableData",null);
            return res;
        }else {
            if (CollectionUtil.isNotEmpty(warnMsgList)){
                res.put("errList",warnMsgList);
                res.put("warn",true);
                res.put("tableData",basMaterialList);
                return res;
            }
        }
        if(CollectionUtils.isNotEmpty(basMaterialList)){
            basMaterialList.forEach(item->{
                basMaterialService.insertBasMaterial(item);
            });
        }
        //我司样衣号的提示
        if (StrUtil.isNotBlank(sampleCodeSelfMsg)){
            if (sampleCodeSelfMsg.endsWith(";")) {
                sampleCodeSelfMsg = sampleCodeSelfMsg.substring(0,sampleCodeSelfMsg.length() - 1);
            }
            sampleCodeSelfMsg = "导入成功，" + sampleCodeSelfMsg + "的我司样衣号已存在";
            res.put("success",basMaterialList.size());
            res.put("message",sampleCodeSelfMsg);
            return res;
        }
        res.put("success",basMaterialList.size());
        return res;
    }

    /**
     * 更新数据导入运营状态
     * @param file 文件
     * @return 返回
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public EmsResultEntity importUpdateSaleStation(MultipartFile file) {
        int num = 0;
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
            // 运营状态
            List<DictData> operateStatusDict = sysDictDataService.selectDictData("s_sale_station_operate_status");
            operateStatusDict = operateStatusDict.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> operateStatusMaps = operateStatusDict.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            // 错误信息
            CommonErrMsgResponse errMsg = null;
            List<CommonErrMsgResponse> errMsgList = new ArrayList<>();
            // 更新数据
            List<BasMaterialSaleStation> updateStationList = new ArrayList<>();
            // 新增数据
            List<BasMaterialSaleStation> addStationList = new ArrayList<>();
            // 循环文件
            for (int i = 0; i < readAll.size(); i++) {
                if (i < 2) {
                    //前两行跳过
                    continue;
                }
                List<Object> objects = readAll.get(i);
                //填充总列数
                copy(objects, readAll);
                num = i + 1;

                // 行对应数据
                BasMaterialSaleStation item = new BasMaterialSaleStation();

                /**
                 * 商品编码 必填
                 */
                String materialCode = objects.get(0) == null || objects.get(0) == "" ? null : objects.get(0).toString();
                Long materialSid = null;
                if (StrUtil.isBlank(materialCode)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("商品编码不可为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    try {
                        BasMaterial material = basMaterialMapper.selectOne(new QueryWrapper<BasMaterial>()
                                .lambda().eq(BasMaterial::getMaterialCode, materialCode));
                        if (material == null) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("找不到" + materialCode + "对应的商品，导入失败！");
                            errMsgList.add(errMsg);
                        }
                        else {
                            materialSid = material.getMaterialSid();
                        }
                    } catch (TooManyResultsException e) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(materialCode + "商品编码存在重复，请先检查该商品，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /**
                 * 销售站点名称 必填
                 */
                String saleStationName = objects.get(1) == null || objects.get(1) == "" ? null : objects.get(1).toString();
                Long saleStationSid = null;
                Long saleStationCode = null;
                if (StrUtil.isBlank(saleStationName)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("销售站点/网店不可为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    try {
                        ConSaleStation saleStation = conSaleStationMapper.selectOne(new QueryWrapper<ConSaleStation>().lambda()
                                .eq(ConSaleStation::getName, saleStationName));
                        if (saleStation == null) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("找不到" + saleStationName + "对应的销售站点/网店，导入失败！");
                            errMsgList.add(errMsg);
                        }
                        else {
                            if (!ConstantsEms.CHECK_STATUS.equals(saleStation.getHandleStatus()) || !ConstantsEms.ENABLE_STATUS.equals(saleStation.getStatus())) {
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("对应的销售站点/网店必须是确认且已启用的状态，导入失败！");
                                errMsgList.add(errMsg);
                            }
                            saleStationSid = saleStation.getSid();
                            saleStationCode = saleStation.getCode();
                        }
                    } catch (TooManyResultsException e) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(saleStationName + "销售站点/网店存在重复，请先检查该销售站点，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                // 判断是否存在，不存在则考虑新增, flag = false 不存在， flag = true 存在
                boolean flag = false;
                if (materialSid != null && saleStationSid != null) {
                    item.setMaterialSid(materialSid).setMaterialCode(materialCode)
                            .setSaleStationSid(saleStationSid).setSaleStationCode(saleStationCode);
                    try {
                        BasMaterialSaleStation exist = basMaterialSaleStationMapper.selectOne(
                                new QueryWrapper<BasMaterialSaleStation>().lambda()
                                        .eq(BasMaterialSaleStation::getMaterialSid, materialSid)
                                        .eq(BasMaterialSaleStation::getSaleStationSid, saleStationSid));
                        // 不存在，要新建
                        if (exist == null) {
                            addStationList.add(item);
                        }
                        else {
                            // 已存在，要更新
                            BeanUtil.copyProperties(exist, item);
                            updateStationList.add(item);
                            flag = true;
                        }
                    } catch (TooManyResultsException e) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("商品编码(" + materialCode + ")和销售站点/网店("+ saleStationName +")组合系统中存在重复，请先检查该数据，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /**
                 * 运营状态 选填
                 */
                String operateStatus_s = objects.get(2) == null || objects.get(2) == "" ? null : objects.get(2).toString();
                String operateStatus = null;
                if (StrUtil.isBlank(operateStatus_s)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("运营状态不可为空，导入失败！");
                    errMsgList.add(errMsg);
                }
                else {
                    // 通过数据字典标签获取数据字典的值
                    operateStatus = operateStatusMaps.get(operateStatus_s);
                    if (StrUtil.isBlank(operateStatus)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("运营状态填写错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                    item.setOperateStatus(operateStatus);
                }

                /**
                 * 备注 选填
                 */
                item.setRemark(objects.get(3) == null || objects.get(3) == "" ? null : objects.get(3).toString());

            }
            if (CollectionUtil.isNotEmpty(errMsgList)) {
                return EmsResultEntity.error(errMsgList);
            }
            else {
                if (CollectionUtil.isNotEmpty(addStationList)) {
                    basMaterialSaleStationMapper.inserts(addStationList);
                }
                if (CollectionUtil.isNotEmpty(updateStationList)) {
                    basMaterialSaleStationMapper.updatesAllById(updateStationList);
                }
            }
        } catch (BaseException e) {
            throw new BaseException(e.getDefaultMessage());
        }
        return EmsResultEntity.success(num-2);
    }

    //填充
    public void copy(List<Object> objects,List<List<Object>> readAll){
        //获取第一行的列数
        int size = readAll.get(0).size();
        //当前行的列数
        int lineSize = objects.size();
        ArrayList<Object> all = new ArrayList<>();
        for (int i=lineSize;i<size;i++){
            Object o = new Object();
            o=null;
            objects.add(o);
        }
    }
}
