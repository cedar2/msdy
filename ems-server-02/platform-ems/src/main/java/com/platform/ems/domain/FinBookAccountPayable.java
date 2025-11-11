package com.platform.ems.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

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

import com.platform.ems.util.data.KeepTwoDecimalsSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;


/**
 * 财务流水账-应付对象 s_fin_book_account_payable
 *
 * @author qhq
 * @date 2021-06-03
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_fin_book_account_payable")
public class FinBookAccountPayable extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-流水账（应付）
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-流水账（应付）")
    private Long bookAccountPayableSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] bookAccountPayableSidList;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-流水账明细（应付）")
    @TableField(exist = false)
    private Long bookAccountPayableItemSid;

    /**
     * 流水号（应付）
     */
    @Excel(name = "流水号（应付）")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "流水号（应付）")
    private Long bookAccountPayableCode;

    /**
     * 财务流水类型编码code
     */
    @Excel(name = "财务流水类型编码code")
    @ApiModelProperty(value = "财务流水类型编码code")
    private String bookType;

    /**
     * 财务流水类型编码code
     */
    @Excel(name = "财务流水类型编码code")
    @ApiModelProperty(value = "财务流水类型编码code")
    @TableField(exist = false)
    private String bookTypeName;

    /**
     * 财务流水来源类别编码code（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "财务流水来源类别编码code（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "财务流水来源类别编码code（数据字典的键值或配置档案的编码）")
    private String bookSourceCategory;

    /**
     * 财务流水来源类别编码code（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "财务流水来源类别编码code（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "财务流水来源类别编码code（数据字典的键值或配置档案的编码）")
    @TableField(exist = false)
    private String bookSourceCategoryName;

    /**
     * 系统SID-供应商
     */
    @Excel(name = "系统SID-供应商")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商")
    private Long vendorSid;

    /**
     * 系统SID-公司档案
     */
    @Excel(name = "系统SID-公司档案")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-公司档案")
    private Long companySid;

    /**
     *（如此流水是红冲后发票的应付流水；此字段保存红冲前的发票的应付流水sid）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "参考应付流水")
    private Long referAccountPayableSid;

    /**
     * 月账单所属期间
     */
    @Excel(name = "月账单所属期间")
    @ApiModelProperty(value = "月账单所属期间")
    private String monthAccountPeriod;

    /**
     * 凭证日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "凭证日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "凭证日期")
    private Date documentDate;

    /**
     * 年份
     */
    @Excel(name = "年份")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "年份")
    private Long paymentYear;

    /**
     * 月份
     */
    @Excel(name = "月份")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "月份")
    private int paymentMonth;

    /**
     * 公司品牌sid
     */
    @Excel(name = "公司品牌sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司品牌sid")
    private Long companyBrandSid;

    /**
     * 业务渠道（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "业务渠道（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "业务渠道（数据字典的键值或配置档案的编码）")
    private String businessChannel;

    /**
     * 币种
     */
    @Excel(name = "币种")
    @ApiModelProperty(value = "币种")
    private String currency;

    /**
     * 货币单位
     */
    @Excel(name = "货币单位")
    @ApiModelProperty(value = "货币单位")
    private String currencyUnit;

    /**
     * 关联单据类别（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "关联单据类别（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "关联单据类别（数据字典的键值或配置档案的编码）")
    @TableField(exist = false)
    private String referDocCategory;

    /**
     * 关联单据号sid
     */
    @Excel(name = "关联单据号sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "关联单据号sid")
    @TableField(exist = false)
    private Long referDocSid;

    /**
     * 关联单据号
     */
    @Excel(name = "关联单据号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "关联单据号")
    @TableField(exist = false)
    private Long referDocCode;

    /**
     * 处理状态（数据字典的键值或配置档案的编码）
     */
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "处理状态（数据字典的键值或配置档案的编码）", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态（用来过滤）")
    private String handleStatusNot;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态（多选）")
    private String[] handleStatusList;

    /**
     * 创建人账号（用户名称）
     */
    @Excel(name = "创建人账号（用户名称）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    @Excel(name = "创建人账号（用户名称）")
    @TableField(exist = false)
    @ApiModelProperty(value = "创建人账号（昵称）")
    private String creatorAccountName;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /**
     * 更新人账号（用户名称）
     */
    @Excel(name = "更新人账号（用户名称）")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 确认人账号（用户名称）
     */
    @Excel(name = "确认人账号（用户名称）")
    @ApiModelProperty(value = "确认人账号（用户名称）")
    private String confirmerAccount;

    /**
     * 确认时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "确认时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "数据源系统（数据字典的键值或配置档案的编码）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

//======================报表返回用字段==========================

    @Excel(name = "行号")
    @TableField(exist = false)
    @ApiModelProperty(value = "行号")
    private Long itemNum;

    /**
     * 产品季名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "产品季sid")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long productSeasonSid;

    /**
     * 产品季名称
     */
    @Excel(name = "产品季名称")
    @TableField(exist = false)
    @ApiModelProperty(value = "产品季名称")
    private String productSeasonName;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料类型")
    private String materialTypeName;

    @TableField(exist = false)
    @ApiModelProperty(value = "供应商名称")
    private String vendorName;

    @TableField(exist = false)
    @ApiModelProperty(value = "供应商编码")
    private Long vendorCode;

    @TableField(exist = false)
    @Excel(name = "供应商简称")
    @ApiModelProperty(value = "供应商简称")
    private String vendorShortName;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司名称")
    private String companyCode;

    /**
     * 预警启用动作：距离天数
     */
    @TableField(exist = false)
    private Integer toDay;

    /**
     * 警示启用动作
     */
    @TableField(exist = false)
    private String overDue;

    /**
     * 警示启用动作:已逾期天数
     */
    @TableField(exist = false)
    private String overDays;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司简称")
    private String companyShortName;

    @TableField(exist = false)
    @ApiModelProperty(value = "核销状态")
    private String clearStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "核销状态过滤")
    private String clearStatusNot;

    @TableField(exist = false)
    @ApiModelProperty(value = "应付金额")
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    private BigDecimal currencyAmountTaxYingf;

    @TableField(exist = false)
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "已核销金额")
    private BigDecimal currencyAmountTaxYhx;

    @TableField(exist = false)
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "核销中金额")
    private BigDecimal currencyAmountTaxHxz;

    @TableField(exist = false)
    @Excel(name = "待核销金额")
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "待核销金额")
    private BigDecimal currencyAmountTaxDhx;

    /**
     * 采购合同号
     */
    @Excel(name = "采购合同号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购合同号")
    @TableField(exist = false)
    private String purchaseContractCode;

    /**
     * 采购订单号
     */
    @Excel(name = "采购订单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购订单号")
    @TableField(exist = false)
    private Long purchaseOrderCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售合同号")
    @TableField(exist = false)
    private Long purchaseContractSid;


    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售订单号")
    @TableField(exist = false)
    private Long purchaseOrderSid;

    /**
     * 发票类型（数据字典的键值）
     */
    @TableField(exist = false)
    @Excel(name = "发票类型（数据字典的键值）")
    @ApiModelProperty(value = "发票类型（数据字典的键值）")
    private String invoiceType;

    /**
     * 发票类型名称（数据字典的键值）
     */
    @Excel(name = "发票类型名称（数据字典的键值）")
    @ApiModelProperty(value = "发票类型名称（数据字典的键值）")
    @TableField(exist = false)
    private String invoiceTypeName;

    /**
     * 发票类别（数据字典的键值）
     */
    @TableField(exist = false)
    @Excel(name = "发票类别（数据字典的键值）")
    @ApiModelProperty(value = "发票类别（数据字典的键值）")
    private String invoiceCategory;

    /**
     * 发票类别（数据字典的键值）
     */
    @Excel(name = "发票类别（数据字典的键值）")
    @ApiModelProperty(value = "发票类别名称（数据字典的键值）")
    @TableField(exist = false)
    private String invoiceCategoryName;

    /**
     * 销售发票号
     */
    @Excel(name = "销售发票号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售发票号")
    @TableField(exist = false)
    private Long purchaseInvoiceCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售发票号")
    @TableField(exist = false)
    private Long purchaseInvoiceSid;

    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "账期")
    private Date accountValidDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "税率")
    private BigDecimal taxRate;

    @TableField(exist = false)
    @ApiModelProperty(value = "付款单的款项类别")
    private String accountCategory;

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
    @ApiModelProperty(value = "系统SID-流水类型")
    private String[] bookTypeList;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-流水来源类别")
    private String[] bookSourceCategoryList;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-核销状态")
    private String[] clearStatusList;

    @TableField(exist = false)
    @ApiModelProperty(value = "是否已财务对账（数据字典的键值或配置档案的编码）\n")
    private String isFinanceVerify;

}
