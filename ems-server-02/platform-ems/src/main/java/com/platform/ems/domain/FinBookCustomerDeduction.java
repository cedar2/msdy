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
 * 财务流水账-客户扣款对象 s_fin_book_customer_deduction
 *
 * @author qhq
 * @date 2021-06-08
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_fin_book_customer_deduction")
public class FinBookCustomerDeduction extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-流水账（客户扣款）
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-流水账（客户扣款）")
    private Long bookDeductionSid;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-流水账（供应商扣款）")
    private Long bookDeductionItemSid;


    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] bookDeductionSidList;

    /**
     * 流水号（客户扣款）
     */
    @Excel(name = "客户扣款财务流水号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "流水号（供应商扣款）")
    private Long bookDeductionCode;

    @Excel(name = "客户")
    @ApiModelProperty(value = "客户名称")
    @TableField(exist = false)
    private String customerName;

    @TableField(exist = false)
    @ApiModelProperty(value = "客户简称")
    private String customerShortName;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司简称")
    private String companyShortName;

    @Excel(name = "公司")
    @ApiModelProperty(value = "公司名称")
    @TableField(exist = false)
    private String companyName;

    @Excel(name = "核销状态",dictType = "s_account_clear")
    @ApiModelProperty(value = "核销状态")
    @TableField(exist = false)
    private String clearStatus;

    @Excel(name = "扣款金额")
    @ApiModelProperty(value = "扣款金额")
    @TableField(exist = false)
    private BigDecimal currencyAmountTaxKk;

    @Excel(name = "待核销金额")
    @TableField(exist = false)
    @ApiModelProperty(value = "待核销金额（含税）")
    private BigDecimal currencyAmountTaxDhx;

    @ApiModelProperty(value = "核销中金额（含税）")
    @TableField(exist = false)
    private BigDecimal currencyAmountTaxHxz;

    @Excel(name = "已核销金额")
    @ApiModelProperty(value = "已核销金额（含税）")
    @TableField(exist = false)
    private BigDecimal currencyAmountTaxYhx;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "客户扣款单号")
    private Long deductionBillCode;

    @Excel(name = "客户扣款单号")
    @TableField(exist = false)
    @ApiModelProperty(value = "客户扣款单号")
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
     * 系统SID-客户
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-客户")
    private Long customerSid;

    @ApiModelProperty(value = "客户编码")
    private String customerCode;

    /**
     * 系统SID-公司档案
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-公司档案")
    private Long companySid;

    @ApiModelProperty(value = "公司编码")
    private String companyCode;

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
     * 关联单据类别（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "关联单据类别（数据字典的键值或配置档案的编码）")
    @TableField(exist = false)
    private String referDocCategory;

    /**
     * 关联单据号sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "关联单据号sid")
    @TableField(exist = false)
    private Long referDocSid;

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

    @ApiModelProperty(value = "明细list")
    @TableField(exist = false)
    private List<FinBookCustomerDeductionItem> itemList;

    @ApiModelProperty(value = "附件list")
    @TableField(exist = false)
    private List<FinBookCustomerDeductionAttachment> atmList;

    @ApiModelProperty(value = "用来过滤")
    @TableField(exist = false)
    private String clearStatusNot;

    @ApiModelProperty(value = "用来过滤")
    @TableField(exist = false)
    private String handleStatusNot;

    @ApiModelProperty(value = "供應商name")
    @TableField(exist = false)
    private String vendorName;

    @ApiModelProperty(value = "销售员")
    @TableField(exist = false)
    private String salePersonName;

    @ApiModelProperty(value = "物料类型名称")
    @TableField(exist = false)
    private String materialTypeName;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-产品季档案")
    private Long productSeasonSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售员")
    private String salePerson;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料类型（数据字典的键值或配置档案的编码）")
    private String materialType;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-客户")
    private Long[] customerSidList;

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
