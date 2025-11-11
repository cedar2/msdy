package com.platform.ems.domain.dto.response;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 供应商档案
 *
 * @author chr
 * @date 2021-01-31
 */
@Data
@Accessors( chain = true)
public class VendorResponse {

    /** "系统ID-供应商档案" */
    private String vendorSid;

    /** "供应商编码" */
    private String vendorCode;

    /** "供应商名称" */
    private String vendorName;

    /** "供应商简称" */
    private String shortName;

    /** "供应商组编码" */
    private String vendorGroup;

    /** "所属行业编码" */
    private String industryCode;

    /** "供应商类别编码" */
    private String vendorCategory;

    /** "关联公司编码" */
    private String companySid;

    /** "关联客户编码" */
    private String customerSid;

    /** "供应商类型编码" */
    private String vendorType;

    /** "经营性质编码" */
    private String businessNature;

    /** "供应商性质编码" */
    private String vendorProperty;

    /** "主营产品" */
    private String coreProduct;

    /** "法人/负责人姓名" */
    private String ownerName;

    /** "统一信用代码证号/身份证号" */
    private String creditCode;

    /** "注册日期" */
    private String registerDate;

    /** "注册资金（万元）" */
    private String registerCapital;

    /** "注册资金（币种）" */
    private String registerCapitalCurrency;

    /** "所属国家编码" */
    private String country;

    /** "所属省份编码" */
    private String province;

    /** "所属城市编码" */
    private String city;

    /** "是否上市" */
    private String onMarket;

    /** "是否有检测实验室" */
    private String withTestlab;

    /** "是否快反战略合作供应商" */
    private String isAgileVendor;

    /** "技术等级编码" */
    private String technicalGrade;

    /** "管理等级编码" */
    private String managementClass;

    /** "固定资产规模（万元）" */
    private String fixedAssetValue;

    /** "固定资产规模（币种）" */
    private String fixedAssetValueCurrency;

    /** "设备资产（万元）" */
    private String equipmentAssetValue;

    /** "设备资产（币种）" */
    private String equipmentAssetCurrency;

    /** "图片路径" */
    private String picturePath;

    /** "网址" */
    private String internetUrl;

    /** "启用/停用状态" */
    private String status;

    /** "处理状态" */
    private String handleStatus;

    /** "创建人账号" */
    private String creatorAccount;

    /** "创建时间" */
    private String createDate;

    /** "更新人账号" */
    private String updaterAccount;

    /** "更新时间" */
    private String updateDate;

    /** "确认人账号" */
    private String confirmerAccount;

    /** "确认时间" */
    private String confirmDate;
}
