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
 * 客户月对账单对象 s_fin_customer_month_account_bill
 *
 * @author chenkw
 * @date 2021-09-22
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_fin_customer_month_account_bill")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FinCustomerMonthAccountBill extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-客户月对账单
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-客户月对账单")
    private Long customerMonthAccountBillSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] customerMonthAccountBillSidList;
    /**
     * 客户月对账单号
     */
    @Excel(name = "月对账单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "客户月对账单号")
    private Long customerMonthAccountBillCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "变更所属账期的单据sid")
    private List<Long> sidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "变更所属账期的单据类型")
    private String formType;

    /**
     * 系统SID-客户
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-客户")
    private Long customerSid;

    /**
     * 系统SID-公司档案
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-公司档案")
    private Long companySid;


    @TableField(exist = false)
    @ApiModelProperty(value = "客户")
    private String customerName;

    @Excel(name = "客户简称")
    @TableField(exist = false)
    @ApiModelProperty(value = "客户简称")
    private String customerShortName;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司")
    private String companyName;

    @Excel(name = "公司简称")
    @TableField(exist = false)
    @ApiModelProperty(value = "公司简称")
    private String companyShortName;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司")
    private String companyCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "客户")
    private String customerCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-客户（下拉框 多选）")
    private Long[] customerSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-公司档案（下拉框 多选）")
    private Long[] companySidList;

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
     * 本期收款金额
     */
    @Excel(name = "本期收款")
    @ApiModelProperty(value = "本期收款金额")
    private BigDecimal shoukuanBenqi;

    /**
     * 本期采购抵扣金额
     */
    @Excel(name = "本期采购抵扣")
    @ApiModelProperty(value = "本期采购抵扣金额")
    private BigDecimal caigoudikouBenqi;

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
    @NotEmpty(message = "处理状态不能为空")
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（）")
    private String handleStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态（多选）")
    private String[] handleStatusList;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态（用来过滤）")
    private String handleStatusNot;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    @TableField(exist = false)
    @ApiModelProperty(value = "是否已财务对账（数据字典的键值）")
    private String isFinanceVerify;

    @TableField(exist = false)
    @ApiModelProperty(value = "到账状态")
    private String receiptPaymentStatus;

    /**
     * 创建人账号（用户名称）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    @TableField(exist = false)
    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人")
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
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

    @TableField(exist = false)
    @Excel(name = "更新人")
    @ApiModelProperty(value = "修改人")
    private String updaterAccountName;

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
    @ApiModelProperty(value = "确认人账号（用户名称）")
    private String confirmerAccount;

    @TableField(exist = false)
    @Excel(name = "确认人")
    @ApiModelProperty(value = "确认人")
    private String confirmerAccountName;

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
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    @TableField(exist = false)
    @ApiModelProperty(value = "月对账单附件表")
    private List<FinCustomerMonthAccountBillAttach> attachmentList;

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
    @ApiModelProperty(value = "单据类型")
    private String[] documentTypeList;

    @TableField(exist = false)
    @ApiModelProperty(value = "解冻状态")
    private String[] unfreezeStatusList;

    @TableField(exist = false)
    @ApiModelProperty(value = "核销状态")
    private String clearStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "金额核销状态")
    private String clearStatusMoney;

    @TableField(exist = false)
    @ApiModelProperty(value = "应收暂估")
    private List<FinBookReceiptEstimation> bookReceiptEstimationList;

    @TableField(exist = false)
    @ApiModelProperty(value = "本期到票")
    private List<FinSaleInvoice> invoiceList;

    @TableField(exist = false)
    @ApiModelProperty(value = "本期收款 - 流水")
    private List<FinBookReceiptPayment> bookReceiptPaymentList;

    @TableField(exist = false)
    @ApiModelProperty(value = "本期收款单")
    private List<FinReceivableBill> receivableBillList;

    @TableField(exist = false)
    @ApiModelProperty(value = "本期扣款")
    private List<FinCustomerMonthAccountBillKkInfo> deductionList;

    @TableField(exist = false)
    @ApiModelProperty(value = "本期调账")
    private List<FinCustomerMonthAccountBillTzInfo> adjustList;

    @TableField(exist = false)
    @ApiModelProperty(value = "押金")
    private List<FinCustomerCashPledgeBillItem> cashPledgeListZf;

    @TableField(exist = false)
    @ApiModelProperty(value = "押金")
    private List<FinCustomerCashPledgeBillItem> cashPledgeListSq;

    @TableField(exist = false)
    @ApiModelProperty(value = "暂押款")
    private List<FinCustomerFundsFreezeBillItem> fundsFreezeList;

    @TableField(exist = false)
    @ApiModelProperty(value = "账单总览")
    private List<FinCustomerMonthAccountBillInfo> info;
}
