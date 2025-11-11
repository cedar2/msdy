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
 * 财务流水账-付款对象 s_fin_book_payment
 *
 * @author qhq
 * @date 2021-06-07
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_fin_book_payment")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FinBookPayment extends EmsBaseEntity {
    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-流水账（付款）
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-流水账（付款）")
    private Long bookPaymentSid;

    /**
     * 系统SID-流水账（付款）
     */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-流水账（付款）")
    private Long bookPaymentItemSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] bookPaymentSidList;

    /**
     * 流水号（付款）
     */
    @Excel(name = "付款财务流水号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "流水号（付款）")
    private Long bookPaymentCode;

    @ApiModelProperty(value = "供应商name")
    @TableField(exist = false)
    private String vendorName;

    @Excel(name = "供应商")
    @ApiModelProperty(value = "供应商简称")
    @TableField(exist = false)
    private String vendorShortName;

    @ApiModelProperty(value = "公司name")
    @TableField(exist = false)
    private String companyName;

    @Excel(name = "公司")
    @ApiModelProperty(value = "公司简称")
    @TableField(exist = false)
    private String companyShortName;

    @Excel(name = "流水类型")
    @TableField(exist = false)
    @ApiModelProperty(value = "财务流水类型编码code")
    private String bookTypeName;

    @Excel(name = "流水来源类别")
    @TableField(exist = false)
    @ApiModelProperty(value = "财务流水类型编码code")
    private String bookSourceCategoryName;

    @Excel(name = "核销状态", dictType = "s_account_status")
    @ApiModelProperty(value = "核销状态")
    @TableField(exist = false)
    private String clearStatus;

    @Excel(name = "付款金额（含税）")
    @ApiModelProperty(value = "付款金额（含税）")
    @TableField(exist = false)
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    private BigDecimal currencyAmountTaxFk;

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

    @Excel(name = "付款单号")
    @ApiModelProperty(value = "付款单号")
    @TableField(exist = false)
    private Long payBillCode;

    @Excel(name = "经办人")
    @ApiModelProperty(value = "经办人")
    @TableField(exist = false)
    private String agentName;

    @TableField(exist = false)
    @ApiModelProperty(value = "支付方式")
    private String paymentMethodName;

    @ApiModelProperty(value = "支付方式")
    @TableField(exist = false)
    private String paymentMethod;

    @Excel(name = "付款日期", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "付款日期")
    @TableField(exist = false)
    private Date paymentDate;

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
    @TableField(exist = false)
    @ApiModelProperty(value = "产品季名称")
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
     * 系统SID-供应商
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商")
    private Long vendorSid;

    @ApiModelProperty(value = "供应商编码")
    private String vendorCode;

    /**
     * 系统SID-公司档案
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-公司档案")
    private Long companySid;

    @ApiModelProperty(value = "公司编码")
    private String companyCode;

    /**
     * 月账单所属期间
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "月账单所属期间")
    private String monthAccountPeriod;

    @TableField(exist = false)
    @ApiModelProperty(value = "日（付款期间）")
    private Long paymentDay;

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
    private Long paymentMonth;

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

    @TableField(exist = false)
    @ApiModelProperty(value = "业务类型（数据字典的键值或配置档案的编码）")
    private String businessType;

    @TableField(exist = false)
    @ApiModelProperty(value = "业务类型（数据字典的键值或配置档案的编码）")
    private String businessTypeName;

    @TableField(exist = false)
    @ApiModelProperty(value = "款项类别名称")
    private String accountCategoryName;

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

    @ApiModelProperty(value = "明细表")
    @TableField(exist = false)
    private List<FinBookPaymentItem> itemList;

    @ApiModelProperty(value = "附件表")
    @TableField(exist = false)
    private List<FinBookPaymentAttachment> atmList;

    //======================报表返回用字段==========================
    @ApiModelProperty(value = "关联单据号")
    @TableField(exist = false)
    private String referDocCode;

    @ApiModelProperty(value = "核销状态用来过滤")
    @TableField(exist = false)
    private String clearStatusNot;

    @ApiModelProperty(value = "税率")
    @TableField(exist = false)
    private BigDecimal taxRate;

    @ApiModelProperty(value = "经办人")
    @TableField(exist = false)
    private String agent;

    @TableField(exist = false)
    private String purchaseOrg;

    @TableField(exist = false)
    private String documentType;

    @TableField(exist = false)
    @ApiModelProperty(value = "采购组织")
    private String purchaseOrgName;

    @ApiModelProperty(value = "订单产品季")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(exist = false)
    private Long productSeasonSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料类型")
    private String materialTypeName;

    @ApiModelProperty(value = "付款状态")
    @TableField(exist = false)
    private String paymentStatus;

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
    @ApiModelProperty(value = "经办人（多选）")
    private String[] agentList;

    @TableField(exist = false)
    @ApiModelProperty(value = "支付方式（多选）")
    private String[] paymentMethodList;

    @TableField(exist = false)
    @ApiModelProperty(value = "创建人（多选）")
    private String[] creatorAccountList;

}
