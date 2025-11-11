package com.platform.ems.domain;

import java.math.BigDecimal;
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

import javax.validation.constraints.NotEmpty;

import lombok.experimental.Accessors;

/**
 * 供应商月对账单对象 s_fin_vendor_month_account_bill
 *
 * @author chenkw
 * @date 2021-09-22
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_fin_vendor_month_account_bill")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FinVendorMonthAccountBill extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-供应商月对账单
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商月对账单")
    private Long vendorMonthAccountBillSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] vendorMonthAccountBillSidList;

    /**
     * 供应商月对账单号
     */
    @Excel(name = "月对账单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "供应商月对账单号")
    private Long vendorMonthAccountBillCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "变更所属账期的单据sid")
    private List<Long> sidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "变更所属账期的单据类型")
    private String formType;

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


    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-供应商（下拉框 多选）")
    private Long[] vendorSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "供应商")
    private Long vendorCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "供应商")
    private String vendorName;

    @Excel(name = "供应商")
    @TableField(exist = false)
    @ApiModelProperty(value = "供应商简称")
    private String vendorShortName;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-公司档案（下拉框 多选）")
    private Long[] companySidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司")
    private String companyCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司")
    private String companyName;

    @Excel(name = "公司简称")
    @TableField(exist = false)
    @ApiModelProperty(value = "公司简称")
    private String companyShortName;

    /**
     * 所属年月
     */
    @Excel(name = "所属年月")
    @ApiModelProperty(value = "所属年月")
    private String yearMonths;

    /**
     * 期初余额/上期余额金额
     */
    @Excel(name = "上期余额")
    @ApiModelProperty(value = "期初余额/上期余额金额")
    private BigDecimal yueQichu;

    @TableField(exist = false)
    @ApiModelProperty(value = "台账-预付款")
    private BigDecimal yufukuan;

    @TableField(exist = false)
    @ApiModelProperty(value = "应付款")
    private BigDecimal yingfukuan;

    /**
     * 本期到票
     */
    @Excel(name = "本期到票")
    @ApiModelProperty(value = "本期到票")
    private BigDecimal daopiaoBenqi;

    /**
     * 本期付款金额
     */
    @Excel(name = "本期付款")
    @ApiModelProperty(value = "本期付款金额")
    private BigDecimal fukuanBenqi;

    /**
     * 本期付款金额
     */
    @Excel(name = "本期收款")
    @ApiModelProperty(value = "本期收款金额")
    private BigDecimal shoukuanBenqi;

    /**
     * 本期销售抵扣金额
     */
    @Excel(name = "本期销售抵扣")
    @ApiModelProperty(value = "本期销售抵扣金额")
    private BigDecimal xiaoshoudikouBenqi;

    /**
     * 本期扣款金额
     */
    @Excel(name = "本期扣款")
    @ApiModelProperty(value = "本期扣款金额")
    private BigDecimal koukuanBenqi;

    /**
     * 本期调账金额
     */
    @Excel(name = "本期调账")
    @ApiModelProperty(value = "本期调账金额")
    private BigDecimal tiaozhangBenqi;

    /**
     * 期末余额/本期余额金额
     */
    @Excel(name = "本期余额")
    @ApiModelProperty(value = "期末余额/本期余额金额")
    private BigDecimal yueQimo;

    /**
     * 押金金额
     */
    @Excel(name = "押金")
    @ApiModelProperty(value = "押金金额")
    private BigDecimal yajin;

    /**
     * 暂押款金额
     */
    @Excel(name = "暂押款")
    @ApiModelProperty(value = "暂押款金额")
    private BigDecimal zanyakuan;

    /**
     * 实际结欠余额金额
     */
    @Excel(name = "实际结欠余额")
    @ApiModelProperty(value = "实际结欠余额金额")
    private BigDecimal yueShijijieqian;

    /**
     * 应付暂估
     */
    @Excel(name = "应付暂估")
    @ApiModelProperty(value = "应付暂估")
    private BigDecimal yingfuzangu;

    /**
     * 应收暂估
     */
    @Excel(name = "应收暂估")
    @ApiModelProperty(value = "应收暂估")
    private BigDecimal yingshouzangu;

    /**
     * 货币（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "货币（数据字典的键值或配置档案的编码）")
    private String currency;

    /**
     * 货币单位（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "货币单位（数据字典的键值或配置档案的编码）")
    private String currencyUnit;

    /**
     * 处理状态（数据字典的键值或配置档案的编码）
     */
    @NotEmpty(message = "处理状态")
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态（多选）")
    private String[] handleStatusList;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态（用来过滤）")
    private String handleStatusNot;

    @TableField(exist = false)
    @ApiModelProperty(value = "是否已财务对账（数据字典的键值）")
    private String isFinanceVerify;

    /** 创建人账号（用户名称） */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    @TableField(exist = false)
    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人")
    private String creatorAccountName;

    /** 创建时间 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /** 更新人账号（用户名称） */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

    @TableField(exist = false)
    @Excel(name = "更新人")
    @ApiModelProperty(value = "修改人")
    private String updaterAccountName;

    /** 更新时间 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /** 确认人账号（用户名称） */
    @ApiModelProperty(value = "确认人账号（用户名称）")
    private String confirmerAccount;

    @TableField(exist = false)
    @Excel(name = "确认人")
    @ApiModelProperty(value = "确认人")
    private String confirmerAccountName;

    /** 确认时间 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "确认时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    @TableField(exist = false)
    @ApiModelProperty(value = "月对账单附件表")
    private List<FinVendorMonthAccountBillAttach> attachmentList;

    @TableField(exist = false)
    @ApiModelProperty(value = "核销状态")
    private String clearStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "金额核销状态")
    private String clearStatusMoney;

    @TableField(exist = false)
    @ApiModelProperty(value = "流水来源类别")
    private String bookSourceCategory;

    @TableField(exist = false)
    @ApiModelProperty(value = "退回状态")
    private String[] returnStatusList;

    @TableField(exist = false)
    @ApiModelProperty(value = "单据类型")
    private String documentType;

    @TableField(exist = false)
    @ApiModelProperty(value = "支付状态")
    private String paymentStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "单据类型")
    private String[] documentTypeList;

    @TableField(exist = false)
    @ApiModelProperty(value = "解冻状态")
    private String[] unfreezeStatusList;

    @TableField(exist = false)
    @ApiModelProperty(value = "应付暂估")
    private List<FinBookPaymentEstimation> bookPaymentEstimationList;

    @TableField(exist = false)
    @ApiModelProperty(value = "预付款")
    private List<FinRecordAdvancePayment> recordAdvancePaymentList;

    @TableField(exist = false)
    @ApiModelProperty(value = "本期到票")
    private List<FinPurchaseInvoice> invoiceList;

    @TableField(exist = false)
    @ApiModelProperty(value = "应付款")
    private List<FinBookAccountPayable> payableList;

    @TableField(exist = false)
    @ApiModelProperty(value = "本期付款")
    private List<FinPayBill> payBillList;

    @TableField(exist = false)
    @ApiModelProperty(value = "本期付款流水")
    private List<FinBookPayment> bookPaymentList;

    @TableField(exist = false)
    @ApiModelProperty(value = "本期扣款-付款单明细/发票折扣中的扣款")
    private List<FinVendorMonthAccountBillKkInfo> deductionList;

    @TableField(exist = false)
    @ApiModelProperty(value = "本期调账-付款单明细/发票折扣中的调账")
    private List<FinVendorMonthAccountBillTzInfo> adjustList;

    @TableField(exist = false)
    @ApiModelProperty(value = "特殊付款流水")
    private List<FinBookPayment> bookTspaymentList;

    @TableField(exist = false)
    @ApiModelProperty(value = "扣款流水")
    private List<FinBookVendorDeduction> bookDeductionList;

    @TableField(exist = false)
    @ApiModelProperty(value = "调账流水")
    private List<FinBookVendorAccountAdjust> bookAdjustList;

    @TableField(exist = false)
    @ApiModelProperty(value = "押金支付")
    private List<FinVendorCashPledgeBillItem> cashPledgeListZf;

    @TableField(exist = false)
    @ApiModelProperty(value = "押金收取")
    private List<FinVendorCashPledgeBillItem> cashPledgeListSq;

    @TableField(exist = false)
    @ApiModelProperty(value = "暂押款")
    private List<FinVendorFundsFreezeBillItem> fundsFreezeList;

    @TableField(exist = false)
    @ApiModelProperty(value = "账单总览")
    private List<FinVendorMonthAccountBillInfo> info;

}
