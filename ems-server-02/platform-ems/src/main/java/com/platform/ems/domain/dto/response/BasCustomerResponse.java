package com.platform.ems.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.platform.common.annotation.Excel;
import com.platform.ems.domain.BasCustomer;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author Pinzhen Chen
 * @Date 2021/1/27 14:22
 */
@Data
public class BasCustomerResponse implements Serializable {

    /** 系统自增长ID-客户信息 */
    @Excel(name = "系统自增长ID-客户信息")
    private String customerSid;

    /** 客户编码 */
    private String customerCode;

    /** 客户名称 */
    @Excel(name = "客户名称")
    private String customerName;

    /** 客户简称 */
    @Excel(name = "客户简称")
    private String shortName;

    /** 客户组编码 */
    @Excel(name = "客户组编码")
    private String customerGroup;

    /** 所属行业编码 */
    @Excel(name = "所属行业编码")
    private String industryCode;

    /** 客户性质编码 */
    @Excel(name = "客户性质编码")
    private String customerProperty;

    /** 关联公司编码 */
    @Excel(name = "关联公司编码")
    private String companySid;

    /** 关联供应商编码 */
    @Excel(name = "关联供应商编码")
    private String vendorSid;

    /** 客户级别编码 */
    @Excel(name = "客户级别编码")
    private String customerLevel;

    /** 客户类别编码 */
    @Excel(name = "客户类别编码")
    private String customerCategory;

    /** 客户类型编码 */
    @Excel(name = "客户类型编码")
    private String customerType;

    /** 隶属上一级客户编码 */
    @Excel(name = "隶属上一级客户编码")
    private String superiorCustomerSid;

    /** 法人/负责人姓名 */
    @Excel(name = "法人/负责人姓名")
    private String ownerName;

    /** 统一信用代码证号/身份证号 */
    @Excel(name = "统一信用代码证号/身份证号")
    private String creditCode;

    /** 原始客户编码 */
    @Excel(name = "原始客户编码")
    private String originalCustomerSid;

    /** 营业执照-有效期从 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "营业执照-有效期从", width = 30, dateFormat = "yyyy-MM-dd")
    private Date creditStartDate;

    /** 营业执照-有效期至 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "营业执照-有效期至", width = 30, dateFormat = "yyyy-MM-dd")
    private Date creditEndDate;

    /** 所属国家编码 */
    @Excel(name = "所属国家编码")
    private String country;

    /** 所属省份编码 */
    @Excel(name = "所属省份编码")
    private String province;

    /** 所属城市编码 */
    @Excel(name = "所属城市编码")
    private String city;

    /** 注册日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "注册日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date registerDate;

    /** 注册资金（万元） */
    @Excel(name = "注册资金", readConverterExp = "万=元")
    private BigDecimal registerCapital;

    /** 注册资金（币种） */
    @Excel(name = "注册资金", readConverterExp = "币=种")
    private String registerCapitalCurrency;

    /** 网址 */
    @Excel(name = "网址")
    private String internetUrl;

    /** 纳税人识别号 */
    @Excel(name = "纳税人识别号")
    private String taxpayerIdentifyNumber;

    /** 开票电话 */
    @Excel(name = "开票电话")
    private String invoiceTel;

    /** 开票地址 */
    @Excel(name = "开票地址")
    private String invoiceAddr;

    /** 办公地址 */
    @Excel(name = "办公地址")
    private String officeAddr;

    /** 营业范围 */
    @Excel(name = "营业范围")
    private String businessScope;

    /** 图片路径 */
    @Excel(name = "图片路径")
    private String picturePath;

    /** 启用/停用状态 */
    @Excel(name = "启用/停用状态")
    private String status;

    /** 处理状态 */
    @Excel(name = "处理状态")
    private String handleStatus;

    /** 创建人账号 */
    @Excel(name = "创建人账号")
    private String creatorAccount;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date createDate;

    /** 更新人账号 */
    @Excel(name = "更新人账号")
    private String updaterAccount;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date updateDate;

    /** 确认人账号 */
    @Excel(name = "确认人账号")
    private String confirmerAccount;

    /** 确认时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "确认时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date confirmDate;

    /** 备注 */
    private String remark;
}
