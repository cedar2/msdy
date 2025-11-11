package com.platform.ems.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.platform.common.annotation.Excel;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author Pinzhen Chen
 * @Date 2021/2/4 10:54
 */
@Data
public class PurPurchasePriceResponse implements Serializable {
    /** 系统ID-物料采购价信息 */
    @NotBlank(message = "系统ID-物料采购价信息不能为空")
    private String purchasePriceInforSid;

    /** 系统ID-供应商档案 */
    @Excel(name = "系统ID-供应商档案")
    @NotBlank(message = "系统ID-供应商档案不能为空")
    private String vendorSid;

    /** 系统ID-物料档案 */
    @Excel(name = "系统ID-物料档案")
    @NotBlank(message = "系统ID-物料档案不能为空")
    private String materialSid;

    /** 系统ID-公司档案 */
    @Excel(name = "系统ID-公司档案")
    @NotBlank(message = "系统ID-公司档案不能为空")
    private String companySid;

    /** 采购组织编码 */
    @Excel(name = "采购组织编码")
    @NotBlank(message = "采购组织编码不能为空")
    private String purchaseOrg;

    /** 价格类型 */
    @Excel(name = "价格类型")
    @NotBlank(message = "价格类型不能为空")
    private String priceType;

    /** 价格类别 */
    @Excel(name = "价格类别")
    @NotBlank(message = "价格类别不能为空")
    private String priceCategory;

    /** 税率 */
    @Excel(name = "税率")
    @NotNull(message = "税率不能为空")
    private BigDecimal taxRate;

    /** 价格维度 */
    @Excel(name = "价格维度")
    @NotBlank(message = "价格维度不能为空")
    private String priceDimension;

    /** 系统ID-SKU1档案 */
    @Excel(name = "系统ID-SKU1档案")
    private String sku1Sid;

    /** 系统ID-SKU2档案 */
    @Excel(name = "系统ID-SKU2档案")
    private String sku2Sid;

    /** 基本计量单位 */
    @Excel(name = "基本计量单位")
    @NotBlank(message = "基本计量单位不能为空")
    private String baseUnit;

    /** 递增减SKU类型 */
    @Excel(name = "递增减SKU类型")
    private String skuTypeRecursion;

    /** 成本类型 */
    @Excel(name = "成本类型")
    private String costType;

    /** 启用/停用状态 */
    @Excel(name = "启用/停用状态")
    @NotBlank(message = "启用/停用状态不能为空")
    private String status;

    /** 处理状态 */
    @Excel(name = "处理状态")
    @NotBlank(message = "处理状态不能为空")
    private String handleStatus;

    /** 创建人账号 */
    @Excel(name = "创建人账号")
    @NotBlank(message = "创建人账号不能为空")
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
    @Excel(name = "备注")
    private String remark;

}
