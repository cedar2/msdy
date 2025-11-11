package com.platform.ems.domain;

import java.math.BigDecimal;
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

import com.platform.ems.util.data.KeepTwoDecimalsSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;


/**
 * 财务流水账-收款对象 s_fin_book_receipt_payment
 *
 * @author qhq
 * @date 2021-06-09
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_fin_book_receipt_payment")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FinBookReceiptPayment extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-流水账（收款）
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-流水账（收款）")
    private Long bookReceiptPaymentSid;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-流水账（收款）")
    private Long bookReceiptPaymentItemSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] bookReceiptPaymentSidList;

    /**
     * 流水号（收款）
     */
    @Excel(name = "收款财务流水号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "流水号（收款）")
    private Long bookReceiptPaymentCode;

    @ApiModelProperty(value = "客户name")
    @TableField(exist = false)
    private String customerName;

    @Excel(name = "客户")
    @TableField(exist = false)
    @ApiModelProperty(value = "客户简称")
    private String customerShortName;

    @Excel(name = "公司")
    @TableField(exist = false)
    @ApiModelProperty(value = "公司简称")
    private String companyShortName;

    @ApiModelProperty(value = "公司name")
    @TableField(exist = false)
    private String companyName;

    /**
     * 财务流水类型编码code
     */
    @TableField(exist = false)
    @Excel(name = "流水类型")
    @ApiModelProperty(value = "财务流水类型编码code")
    private String bookTypeName;

    /**
     * 财务流水来源类别编码code（数据字典的键值或配置档案的编码）
     */
    @TableField(exist = false)
    @Excel(name = "流水来源类别")
    @ApiModelProperty(value = "财务流水来源类别编码code（数据字典的键值或配置档案的编码）")
    private String bookSourceCategoryName;

    @Excel(name = "流水来源类别", dictType = "s_account_clear")
    @ApiModelProperty(value = "核销状态")
    @TableField(exist = false)
    private String clearStatus;

    @Excel(name = "收款金额（含税）")
    @ApiModelProperty(value = "收款金额（含税）")
    @TableField(exist = false)
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    private BigDecimal currencyAmountTaxSk;

    @Excel(name = "待核销金额（含税）")
    @ApiModelProperty(value = "待核销金额（含税）")
    @TableField(exist = false)
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    private BigDecimal currencyAmountTaxDhx;

    @Excel(name = "核销中金额（含税）")
    @ApiModelProperty(value = "核销中金额（含税）")
    @TableField(exist = false)
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    private BigDecimal currencyAmountTaxHxz;

    @Excel(name = "已核销金额（含税）")
    @ApiModelProperty(value = "已核销金额（含税）")
    @TableField(exist = false)
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    private BigDecimal currencyAmountTaxYhx;

    @Excel(name = "收款单号")
    @TableField(exist = false)
    @ApiModelProperty(value = "收款单号")
    private Long receivableBillCode;

    @Excel(name = "经办人")
    @TableField(exist = false)
    @ApiModelProperty(value = "经办人")
    private String agentName;

    @ApiModelProperty(value = "支付方式")
    @TableField(exist = false)
    private String payMethod;

    @Excel(name = "收款日期", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "收款日期")
    @TableField(exist = false)
    private Date receivableDate;

    @TableField(exist = false)
    @Excel(name = "是否已财务对账", dictType = "s_yesno_flag")
    @ApiModelProperty(value = "是否已财务对账（数据字典的键值或配置档案的编码）")
    private String isFinanceVerify;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    @Excel(name = "行号")
    @ApiModelProperty(value = "行号")
    @TableField(exist = false)
    private Long itemNum;

    @Excel(name = "币种", dictType = "s_currency")
    @ApiModelProperty(value = "币种")
    private String currency;

    @Excel(name = "货币单位", dictType = "s_currency_unit")
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

    @Excel(name = "产品季")
    @ApiModelProperty(value = "订单产品季名称")
    @TableField(exist = false)
    private String productSeasonName;

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
     * 凭证日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "凭证日期")
    private Date documentDate;

    /**
     * 年份
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "年份")
    private Long paymentYear;

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
    private String referDocCategory;

    /**
     * 关联单据号sid
     */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "关联单据号sid")
    private Long referDocSid;

    /**
     * 处理状态（数据字典的键值或配置档案的编码）
     */
    @NotEmpty(message = "状态不能为空")
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

    @ApiModelProperty(value = "明细表LIST")
    @TableField(exist = false)
    private List<FinBookReceiptPaymentItem> itemList;

    @ApiModelProperty(value = "附件表LIST")
    @TableField(exist = false)
    private List<FinBookReceiptPaymentAttachment> atmList;

    //======================报表返回用字段==========================
    @ApiModelProperty(value = "关联单据号")
    @TableField(exist = false)
    private String referDocCode;

    @ApiModelProperty(value = "核销状态用来过滤")
    @TableField(exist = false)
    private String clearStatusNot;

    @ApiModelProperty(value = "税率")
    @TableField(exist = false)
    private String taxRate;

    @ApiModelProperty(value = "经办人")
    @TableField(exist = false)
    private String agent;

    @ApiModelProperty(value = "业务类型")
    @TableField(exist = false)
    private String businessType;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "产品季sid")
    private Long productSeasonSid;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售部门")
    private Long saleDepartment;

    @TableField(exist = false)
    @ApiModelProperty(value = "单据类型")
    private String documentType;

    @TableField(exist = false)
    @ApiModelProperty(value = "收款状态")
    private String receiptPaymentStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "税率")
    private String taxRateName;

    @TableField(exist = false)
    @ApiModelProperty(value = "支付方式")
    private String payMethodName;

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
    @ApiModelProperty(value = "系统SID-流水类型")
    private String[] bookTypeList;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-流水来源类别")
    private String[] bookSourceCategoryList;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-核销状态")
    private String[] clearStatusList;

    @TableField(exist = false)
    @ApiModelProperty(value = "经办人（多选）")
    private String[] agentList;

    @TableField(exist = false)
    @ApiModelProperty(value = "支付方式（多选）")
    private String[] payMethodList;

    @TableField(exist = false)
    @ApiModelProperty(value = "创建人（多选）")
    private String[] creatorAccountList;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售组织")
    private String saleOrgName;

    @TableField(exist = false)
    @ApiModelProperty(value = "业务类型（数据字典的键值或配置档案的编码）")
    private String businessTypeName;

    @TableField(exist = false)
    @ApiModelProperty(value = "款项类别名称")
    private String accountCategoryName;

    /**
     * 月账单所属期间
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "月账单所属期间")
    private String monthAccountPeriod;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "收款主体sid")
    private Long receivableCompanySid;

    @TableField(exist = false)
    @ApiModelProperty(value = "收款主体名称")
    private String receivableCompanyName;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售部门（数据字典的键值或配置档案的编码）")
    private String saleDepartmentName;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料类型")
    private String materialTypeName;
}
