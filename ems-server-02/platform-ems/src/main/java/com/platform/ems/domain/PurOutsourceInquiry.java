package com.platform.ems.domain;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableId;
import com.platform.common.core.domain.EmsBaseEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import lombok.experimental.Accessors;

/**
 * 加工询价单主对象 s_pur_outsource_inquiry
 *
 * @author chenkw
 * @date 2022-01-11
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_pur_outsource_inquiry")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PurOutsourceInquiry extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-加工询价单
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-加工询价单")
    private Long outsourceInquirySid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] outsourceInquirySidList;
    /**
     * 加工询价单号
     */
    @ApiModelProperty(value = "加工询价单号")
    private String outsourceInquiryCode;

    /**
     * 系统SID-供应商档案sid
     */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商档案sid")
    private Long vendorSid;

    @TableField(exist = false)
    @Excel(name = "供应商")
    @ApiModelProperty(value = "供应商名称")
    private String vendorName;

    @TableField(exist = false)
    @ApiModelProperty(value = "供应商简称")
    private String vendorShortName;

    /**
     * 公司档案sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司档案sid")
    private Long companySid;

    @TableField(exist = false)
    @Excel(name = "公司")
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    /**
     * 系统SID-产品季档案
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-产品季档案")
    private Long productSeasonSid;

    @TableField(exist = false)
    @Excel(name = "产品季")
    @ApiModelProperty(value = "产品季名称")
    private String productSeasonName;

    /**
     * 询价日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "询价日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "询价日期")
    private Date dateRequest;

    /**
     * 报价计划截至日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "报价计划截至日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "报价计划截至日期")
    private Date quotepriceDeadline;

    /**
     * 采购员（用户账号）
     */
    @ApiModelProperty(value = "采购员（用户账号）")
    private String buyer;

    @TableField(exist = false)
    @ApiModelProperty(value = "采购员（用户账号）（多选）")
    private String[] buyerList;

    @TableField(exist = false)
    @ApiModelProperty(value = "创建人（用户账号）（多选）")
    private String[] creatorAccountList;

    @TableField(exist = false)
    @Excel(name = "采购员")
    @ApiModelProperty(value = "采购员（用户昵称）")
    private String buyerName;

    /**
     * 采购员电话
     */
    @Excel(name = "采购员电话")
    @ApiModelProperty(value = "采购员电话")
    private String buyerTelephone;

    /**
     * 采购员邮箱
     */
    @Excel(name = "采购员邮箱")
    @ApiModelProperty(value = "采购员邮箱")
    private String buyerEmail;

    /**
     * 采购组织（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "采购组织（数据字典的键值或配置档案的编码）")
    private String purchaseOrg;

    @TableField(exist = false)
    @Excel(name = "采购组织")
    @ApiModelProperty(value = "采购组织（数据字典的键值）")
    private String purchaseOrgName;

    /**
     * 采购组（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "采购组（数据字典的键值或配置档案的编码）")
    private String purchaseGroup;

    @TableField(exist = false)
    @Excel(name = "采购组")
    @ApiModelProperty(value = "采购组（数据字典的键值或配置档案的编码）")
    private String purchaseGroupName;

    /**
     * 物料类型（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "物料类型（数据字典的键值或配置档案的编码）")
    private String materialType;

    @TableField(exist = false)
    @Excel(name = "物料类型")
    @ApiModelProperty(value = "物料类型（数据字典的键值或配置档案的编码）")
    private String materialTypeName;

    /**
     * 物料类别（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "物料类别（数据字典的键值或配置档案的编码）")
    private String materialCategory;

    @TableField(exist = false)
    @Excel(name = "物料类别")
    @ApiModelProperty(value = "物料类别（数据字典的键值或配置档案的编码）")
    private String materialCategoryName;

    /**
     * 币种（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "币种", dictType = "s_currency")
    @ApiModelProperty(value = "币种（数据字典的键值或配置档案的编码）")
    private String currency;

    /**
     * 货币单位（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "货币单位", dictType = "s_currency_unit")
    @ApiModelProperty(value = "货币单位（数据字典的键值或配置档案的编码）")
    private String currencyUnit;

    /**
     * 处理状态
     */
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人")
    private String creatorAccount;

    @TableField(exist = false)
    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人账号（用户昵称）")
    private String creatorAccountName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人")
    private String updaterAccount;

    @TableField(exist = false)
    @Excel(name = "更新人")
    @ApiModelProperty(value = "更新人账号（用户昵称）")
    private String updaterAccountName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    @ApiModelProperty(value = "确认人")
    private String confirmerAccount;

    @TableField(exist = false)
    @Excel(name = "确认人")
    @ApiModelProperty(value = "确认人账号（用户昵称）")
    private String confirmerAccountName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "确认时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 数据源系统（数据字典的键值）
     */
    @Excel(name = "数据源系统（数据字典的键值）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值）")
    private String dataSourceSys;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-供应商多选下拉框")
    private Long[] vendorSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-公司多选下拉框")
    private Long[] companySidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-产品季多选下拉框")
    private Long[] productSeasonSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态多选下拉框")
    private String[] handleStatusList;

    @TableField(exist = false)
    @ApiModelProperty(value = "采购组多选下拉框")
    private String[] purchaseGroupList;

    @TableField(exist = false)
    @ApiModelProperty(value = "采购组织多选下拉框")
    private String[] purchaseOrgList;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料类型多选下拉框")
    private String[] materialTypeList;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "加工报价单号")
    private Long outSourceQuoteBargainCode;

    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "加工报价日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "加工报价日期")
    private Date dateQuote;

    @TableField(exist = false)
    @ApiModelProperty(value = "是否已加工报价")
    private String isQuoteProcess;

    @Valid
    @TableField(exist = false)
    @ApiModelProperty(value = "加工询价单明细对象")
    private List<PurOutsourceInquiryItem> itemList;

    @TableField(exist = false)
    @ApiModelProperty(value = "加工询价单供应商明细对象")
    private List<PurOutsourceInquiryVendor> vendorList;

    @Valid
    @TableField(exist = false)
    @ApiModelProperty(value = "加工询价单附件对象")
    private List<PurOutsourceInquiryAttach> attachmentList;

}
