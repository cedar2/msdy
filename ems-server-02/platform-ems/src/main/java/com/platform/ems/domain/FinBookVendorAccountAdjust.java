package com.platform.ems.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotEmpty;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;


/**
 * 财务流水账-供应商调账对象 s_fin_book_vendor_account_adjust
 *
 * @author qhq
 * @date 2021-06-02
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_fin_book_vendor_account_adjust")
public class FinBookVendorAccountAdjust extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-流水账（供应商调账）
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-流水账（供应商调账）")
    private Long bookAccountAdjustSid;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-流水账明细（供应商调账）")
    private Long bookAccountAdjustItemSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] bookAccountAdjustSidList;

    /**
     * 流水号（供应商调账）
     */
    @Excel(name = "供应商调账财务流水号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "流水号（供应商调账）")
    private Long bookAccountAdjustCode;

    @Excel(name = "供应商")
    @ApiModelProperty(value = "供應商name")
    @TableField(exist = false)
    private String vendorName;

    @TableField(exist = false)
    @ApiModelProperty(value = "供应商简称")
    private String vendorShortName;

    @Excel(name = "公司")
    @ApiModelProperty(value = "公司name")
    @TableField(exist = false)
    private String companyName;

    @Excel(name = "核销状态",dictType = "s_account_clear")
    @ApiModelProperty(value = "核销状态")
    @TableField(exist = false)
    private String clearStatus;

    @Excel(name = "调账金额")
    @ApiModelProperty(value = "调账金额")
    @TableField(exist = false)
    private BigDecimal currencyAmountTaxTz;

    @Excel(name = "待核销金额")
    @TableField(exist = false)
    @ApiModelProperty(value = "待核销金额（含税）")
    private BigDecimal currencyAmountTaxDhx;

    @Excel(name = "核销中金额")
    @ApiModelProperty(value = "核销中金额（含税）")
    @TableField(exist = false)
    private BigDecimal currencyAmountTaxHxz;

    @Excel(name = "已核销金额")
    @ApiModelProperty(value = "已核销金额（含税）")
    @TableField(exist = false)
    private BigDecimal currencyAmountTaxYhx;

    @Excel(name = "供应商调账单号")
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "供应商调账单号")
    private Long adjustBillCode;

    @ApiModelProperty(value = "关联单据号")
    @TableField(exist = false)
    private String referDocCode;

    @Excel(name = "产品季")
    @ApiModelProperty(value = "产品季名称")
    @TableField(exist = false)
    private String productSeasonName;

    @Excel(name = "单据日期", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "凭证日期/单据日期")
    private Date documentDate;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    @TableField(exist = false)
    private String remark;

    @Excel(name = "币种",dictType = "s_currency")
    @ApiModelProperty(value = "币种")
    private String currency;

    @Excel(name = "货币单位",dictType = "s_currency_unit")
    @ApiModelProperty(value = "货币单位")
    private String currencyUnit;

    @Excel(name = "创建人")
    @TableField(exist = false)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccountName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @Excel(name = "流水类型")
    @TableField(exist = false)
    @ApiModelProperty(value = "流水类型")
    private String bookTypeName;

    @Excel(name = "流水来源类别")
    @TableField(exist = false)
    @ApiModelProperty(value = "流水来源类别")
    private String bookSourceCategoryName;

    /**
     * 财务流水类型编码code
     */
    @ApiModelProperty(value = "财务流水类型编码code")
    private String bookType;

    /**
     * 财务流水来源类别编码code（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "财务流水来源类别编码code（数据字典的键值或配置档案的编码）")
    private String bookSourceCategory;

    /**
     * 系统SID-供应商
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商")
    private Long vendorSid;

    /**
     * 系统SID-公司档案
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-公司档案")
    private Long companySid;

    /**
     * 年份
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "年份")
    private int paymentYear;

    /**
     * 月份
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "月份")
    private int paymentMonth;

    /**
     * 公司品牌sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司品牌sid")
    private Long companyBrandSid;

    /**
     * 业务渠道（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "业务渠道（数据字典的键值或配置档案的编码）")
    private String businessChannel;

    /**
     * 处理状态（数据字典的键值或配置档案的编码）
     */
    @NotEmpty(message = "状态不能为空")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态（多选）")
    private String[] handleStatusList;

    /**
     * 创建人账号（用户名称）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;


    /**
     * 更新人账号（用户名称）
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 确认人账号（用户名称）
     */
    @ApiModelProperty(value = "确认人账号（用户名称）")
    private String confirmerAccount;

    /**
     * 确认时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    @ApiModelProperty(value = "明细表")
    @TableField(exist = false)
    private List<FinBookVendorAccountAdjustItem> itemList;

    @ApiModelProperty(value = "附件表")
    @TableField(exist = false)
    private List<FinBookVendorAccountAdjustAttachment> atmList;

    //=================报表参数======================

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "供应商调账单号")
    private Long referDocSid;

    @ApiModelProperty(value = "用来过滤")
    @TableField(exist = false)
    private String clearStatusNot;

    @ApiModelProperty(value = "用来过滤")
    @TableField(exist = false)
    private String handleStatusNot;

    @TableField(exist = false)
    @ApiModelProperty(value = "采购员")
    private String buyerName;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料类型")
    private String materialTypeName;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-产品季")
    private Long productSeasonSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "采购员（user_name）")
    private String buyer;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料类型（code）")
    private String materialType;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-供应商")
    private Long[] vendorSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-公司")
    private Long[] companySidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-产品季")
    private Long[] productSeasonSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-核销状态")
    private String[] clearStatusList;

    @TableField(exist = false)
    @ApiModelProperty(value = "行号")
    private Long itemNum;
}
