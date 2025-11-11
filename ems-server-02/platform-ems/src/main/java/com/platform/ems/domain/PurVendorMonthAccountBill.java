package com.platform.ems.domain;

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

import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 供应商对账单对象 s_pur_vendor_month_account_bill
 *
 * @author chenkw
 * @date 2021-09-22
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_pur_vendor_month_account_bill")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PurVendorMonthAccountBill extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-供应商对账单
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商对账单")
    private Long vendorMonthAccountBillSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] vendorMonthAccountBillSidList;
    /**
     * 供应商对账单sids
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "供应商对账单sids")
    private Long[] vendorMonthAccountBillSids;

    /**
     * 供应商对账单号
     */
    @Excel(name = "供应商对账单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "供应商对账单号")
    private Long vendorMonthAccountBillCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "变更所属账期的单据sid")
    private List<Long> sidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "变更所属账期的单据类型")
    private String formType;


    /**
     * 处理状态（数据字典的键值或配置档案的编码）
     */
    @NotEmpty(message = "处理状态")
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    /**
     * 系统SID-供应商
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商")
    private Long vendorSid;

    /**
     * 供应商编码
     */
    @ApiModelProperty(value = "供应商编码")
    private Long vendorCode;

    /**
     * 系统SID-公司档案
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-公司档案")
    private Long companySid;

    /**
     * 公司编码
     */
    @ApiModelProperty(value = "公司编码")
    private String companyCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-供应商（下拉框 多选）")
    private Long[] vendorSidList;

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
    private String companyName;

    @Excel(name = "公司简称")
    @TableField(exist = false)
    @ApiModelProperty(value = "公司简称")
    private String companyShortName;

    /**
     * 对账月
     */
    @Excel(name = "对账月")
    @ApiModelProperty(value = "对账月")
    private String yearMonths;

    @TableField(exist = false)
    @ApiModelProperty(value ="创建日期开始时间")
    private String beginTime;

    @TableField(exist = false)
    @ApiModelProperty(value ="创建日期结束时间")
    private String endTime;

    /**
     * 物料类型
     */
    @ApiModelProperty(value = "物料类型（数据字典的键值或配置档案的编码）")
    private String materialType;

    /**
     * 物料类型
     */
    @Excel(name = "物料类型")
    @TableField(exist = false)
    @ApiModelProperty(value = "物料类型（数据字典的键值或配置档案的编码）")
    private String materialTypeName;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料类型")
    private String[] materialTypeList;

    /**
     * 期初余额/上期余额金额
     */
    @Excel(name = "期初余额")
    @ApiModelProperty(value = "期初余额")
    private BigDecimal initialBalance;

    @TableField(exist = false)
    @ApiModelProperty(value = "台账-预付款")
    private BigDecimal yufukuan;

    @TableField(exist = false)
    @ApiModelProperty(value = "应付款")
    private BigDecimal yingfukuan;

    /**
     * 本期到货
     */
    @Excel(name = "本期到货")
    @ApiModelProperty(value = "本期到货")
    private BigDecimal daohuo;

    /**
     * 本期到票
     */
    @Excel(name = "本期到票")
    @ApiModelProperty(value = "本期到票")
    private BigDecimal daopiao;

    /**
     * 本期付款金额
     */
    @Excel(name = "本期付款")
    @ApiModelProperty(value = "本期付款金额")
    private BigDecimal fukuan;

    /**
     * 本期付款金额
     */
    @ApiModelProperty(value = "本期收款金额")
    private BigDecimal shoukuan;

    /**
     * 本期销售抵扣金额
     */
    @ApiModelProperty(value = "本期销售抵扣金额")
    private BigDecimal xiaoshoudikou;

    /**
     * 本期扣款金额
     */
    @ApiModelProperty(value = "本期扣款金额")
    private BigDecimal koukuan;

    /**
     * 本期调账金额
     */
    @Excel(name = "本期调账")
    @ApiModelProperty(value = "本期调账金额")
    private BigDecimal tiaozhang;

    /**
     * 期末余额/本期余额金额
     */
    @Excel(name = "期末余额")
    @ApiModelProperty(value = "期末余额/本期余额金额")
    private BigDecimal endingBalance;

    /**
     * 押金金额
     */
    @ApiModelProperty(value = "押金金额")
    private BigDecimal yajin;

    /**
     * 暂押款金额
     */
    @ApiModelProperty(value = "暂押款金额")
    private BigDecimal zanyakuan;

    /**
     * 实际结欠余额金额
     */
    @ApiModelProperty(value = "实际结欠余额金额")
    private BigDecimal yueShijijieqian;

    /**
     * 应付暂估
     */
    @ApiModelProperty(value = "应付暂估")
    private BigDecimal yingfuzangu;

    /**
     * 应收暂估
     */
    @ApiModelProperty(value = "应收暂估")
    private BigDecimal yingshouzangu;

    /**
     * 货币（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "币种", dictType = "s_currency")
    @ApiModelProperty(value = "货币（数据字典的键值或配置档案的编码）")
    private String currency;

    /**
     * 货币单位（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "货币单位", dictType = "s_currency_unit")
    @ApiModelProperty(value = "货币单位（数据字典的键值或配置档案的编码）")
    private String currencyUnit;



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

    /** 创建人账号（用户名称）列表 */
    @TableField(exist = false)
    @ApiModelProperty(value = "创建人账号（用户名称）列表")
    private String[] creatorAccountList;

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
    @ApiModelProperty(value = "供应商对账单附件表")
    private List<PurVendorMonthAccountBillAttach> attachmentList;

    @TableField(exist = false)
    @ApiModelProperty(value = "供应商对账单明细表")
    private List<PurVendorMonthAccountBillZangu> purVendorMonthAccountBillZanguList;

    @TableField(exist = false)
    @ApiModelProperty(value = "核销状态")
    private String clearStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "提交")
    private String tjzt;

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
    private List<PurVendorMonthAccountBillKkInfo> deductionList;

    @TableField(exist = false)
    @ApiModelProperty(value = "本期调账-付款单明细/发票折扣中的调账")
    private List<PurVendorMonthAccountBillTzInfo> adjustList;

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
    private List<PurVendorMonthAccountBillInfo> info;

}
