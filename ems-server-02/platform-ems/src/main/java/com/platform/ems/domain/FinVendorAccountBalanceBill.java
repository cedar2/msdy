package com.platform.ems.domain;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotEmpty;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;


/**
 * 供应商账互抵单对象 s_fin_vendor_account_balance_bill
 *
 * @author qhq
 * @date 2021-06-07
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_fin_vendor_account_balance_bill")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FinVendorAccountBalanceBill extends EmsBaseEntity {
    /** 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /** 系统SID-供应商账互抵单 */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商账互抵单")
    private Long accountBalanceBillSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] accountBalanceBillSidList;

    /** 供应商账互抵单号  */
    @Excel(name = "供应商账互抵单号 ")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "供应商账互抵单号 ")
    private Long accountBalanceBillCode;

    /** 系统SID-供应商 */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商")
    private Long vendorSid;

    /** 系统SID-公司档案 */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-公司档案")
    private Long companySid;

    @Excel(name = "供应商名称 ")
    @TableField(exist = false)
    @ApiModelProperty(value = "供应商名称")
    private String vendorName;

    @Excel(name = "公司名称 ")
    @TableField(exist = false)
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @Excel(name = "产品季 ")
    @TableField(exist = false)
    @ApiModelProperty(value = "产品季")
    private String productSeasonName;

    @Excel(name = "采购员 ")
    @TableField(exist = false)
    @ApiModelProperty(value = "采购员")
    private String buyerName;

    /** 单据日期 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "单据日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "单据日期")
    private Date documentDate;

    /** 处理状态（数据字典的键值或配置档案的编码） */
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    @Excel(name = "备注")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String remark;

    @Excel(name = "当前审批节点名称")
    @ApiModelProperty(value = "当前审批节点名称")
    @TableField(exist = false)
    private String approvalNode;

    @Excel(name = "当前审批人")
    @ApiModelProperty(value = "当前审批人")
    @TableField(exist = false)
    private String approvalUserName;

    /** 单据类型编码code */
    @ApiModelProperty(value = "单据类型编码code")
    private String documentType;

    /** 业务类型编码code */
    @ApiModelProperty(value = "业务类型编码code")
    private String businessType;

    /**
     * 月账单所属期间
     */
    @ApiModelProperty(value = "月账单所属期间")
    private String monthAccountPeriod;

    /** 公司品牌sid */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司品牌sid")
    private Long companyBrandSid;

    /** 业务渠道（数据字典的键值或配置档案的编码） */
    @ApiModelProperty(value = "业务渠道（数据字典的键值或配置档案的编码）")
    private String businessChannel;

    /** 系统SID-产品季 */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-产品季")
    private Long productSeasonSid;

    /** 采购组织（数据字典的键值或配置档案的编码） */
    @ApiModelProperty(value = "采购组织（数据字典的键值或配置档案的编码）")
    private String purchaseOrg;

    /** 采购员 */
    @ApiModelProperty(value = "采购员")
    private String buyer;

    /** 物料类型（数据字典的键值或配置档案的编码） */
    @ApiModelProperty(value = "物料类型（数据字典的键值或配置档案的编码）")
    private String materialType;

    @ApiModelProperty(value = "币种（数据字典的键值或配置档案的编码）")
    private String currency;

    /** 货币单位（数据字典的键值或配置档案的编码） */
    @ApiModelProperty(value = "货币单位（数据字典的键值或配置档案的编码）")
    private String currencyUnit;

    /** 创建人账号（用户名称） */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    @Excel(name = "创建人")
    @TableField(exist = false)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccountName;

    /** 创建时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /** 更新人账号（用户名称） */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

    @Excel(name = "更新人")
    @TableField(exist = false)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String updaterAccountName;

    /** 更新时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /** 确认人账号（用户名称） */
    @ApiModelProperty(value = "确认人账号（用户名称）")
    private String confirmerAccount;

    /** 确认时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /** 数据源系统（数据字典的键值或配置档案的编码） */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    /** 明细 */
    @TableField(exist = false)
    @ApiModelProperty(value = "明细列表")
    private List<FinVendorAccountBalanceBillItem> itemList;

    /** 附件 */
    @TableField(exist = false)
    @ApiModelProperty(value = "附件清单")
    private List<FinVendorAccountBalanceBillAttachment> attachmentList;

    @TableField(exist = false)
    @ApiModelProperty(value = "品牌名称")
    private String brandName;

    @TableField(exist = false)
    private String nickName;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-供应商")
    private Long[] vendorSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-公司")
    private Long[] companySidList;

    @TableField(exist = false)
    private Long[] productSeasonSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "采购员")
    private String[] buyerList;

    @TableField(exist = false)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String[] creatorAccountList;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态（数据字典）")
    private String[] handleStatusList;

}
