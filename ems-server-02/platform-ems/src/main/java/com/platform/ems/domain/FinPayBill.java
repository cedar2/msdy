package com.platform.ems.domain;

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

import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 付款单对象 s_fin_pay_bill
 *
 * @author linhongwei
 * @date 2021-04-21
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_fin_pay_bill")
public class FinPayBill extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 租户ID
     */
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-付款单
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-付款单")
    private Long payBillSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "sid数组")
    private Long[] payBillSidList;

    /**
     * 付款单号
     */
    @Excel(name = "付款单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "付款单号")
    private Long payBillCode;

    /**
     * 单据日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "单据日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "单据日期")
    private Date documentDate;

    @TableField(exist = false)
    @Excel(name = "公司")
    @ApiModelProperty(value = "公司简称")
    private String companyShortName;

    /**
     * 业务类型编码code
     */
    @ApiModelProperty(value = "业务类型编码code")
    private String businessType;

    @TableField(exist = false)
    @ApiModelProperty(value = "业务类型编码code")
    private String[] businessTypeList;

    @TableField(exist = false)
    @Excel(name = "业务类型")
    @ApiModelProperty(value = "业务类型编码code")
    private String businessTypeName;

    /**
     * 单据类型编码code
     */
    @ApiModelProperty(value = "单据类型编码code")
    private String documentType;

    /**
     * 单据类型编码
     */
    @TableField(exist = false)
    @Excel(name = "单据类型")
    @ApiModelProperty(value = "单据类型")
    private String documentTypeName;

    @TableField(exist = false)
    @ApiModelProperty(value = "单据类型编码code")
    private String[] documentTypeList;

    /**
     * 系统SID-供应商
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商")
    private Long vendorSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-供应商")
    private Long[] vendorSidList;

    /**
     * 供应商编码
     */
    @ApiModelProperty(value = "供应商编码")
    private String vendorCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "供应商名称")
    private String vendorName;

    @TableField(exist = false)
    @Excel(name = "供应商")
    @ApiModelProperty(value = "供应商简称")
    private String vendorShortName;

    /**
     * 系统SID-公司档案
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-公司档案")
    private Long companySid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-公司档案")
    private Long[] companySidList;

    /**
     * 公司编码
     */
    @ApiModelProperty(value = "公司编码")
    private String companyCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @TableField(exist = false)
    @ApiModelProperty(value = "单据日期 起")
    private String documentDateBegin;

    @TableField(exist = false)
    @ApiModelProperty(value = "单据日期 止")
    private String documentDateEnd;

    /**
     * 待付货款金额(元)
     * 》若系统默认设置的“付款核销模式“为”应付暂估“，默认显示所有”核销状态“为”未核销“或”部分核销“，”是否退货“为”否“，
     * 且客户与公司一致的应付暂估流水的未核销金额(未核销金额=出入库金额-已核销金额-核销中金额)的和
     * 》若系统默认设置的“付款核销模式“为”应付“，暂不实现
     * 》若系统默认设置的“付款核销模式“为”应付“，显示为空
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "待付货款金额(元)")
    private BigDecimal daiFuHuoKuan;

    /**
     * 待核销已预付款金额(元)
     * 默认显示所有”核销状态“为”未核销“或”部分核销“，”流水来源类别“为”预付款“，
     * 且客户与公司一致的付款流水的未核销金额(未核销金额=付款金额-核销中金额-已核销金额)的和
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "待核销已预付款金额(元)")
    private BigDecimal daiXiaoYiYuFuKuan;

    /**
     * 待核销扣款金额(元)
     * 默认显示所有”核销状态“为”未核销“或”部分核销“，
     * 且客户与公司一致的客户扣款流水的未核销金额(未核销金额=扣款金额-核销中金额-已核销金额)的和
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "待核销扣款金额(元)")
    private BigDecimal daiXiaoKouKuan;

    /**
     * 待核销退货扣款金额(元)
     * 若系统默认设置的“付款核销模式“为”应付暂估“，默认显示所有”核销状态“为”未核销“或”部分核销“，”是否退货“为”是“，
     * 且客户与公司一致的应付暂估流水的未核销金额(未核销金额=出入库金额-已核销金额-核销中金额)的和的绝对值
     * 注意点：这里得到的流水的未核销金额为负数，显示时要转化为绝对值
     * 》若系统默认设置的“付款核销模式“为”应付“，暂不实现
     * 》若系统默认设置的“付款核销模式“为”应付“，显示为空
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "待核销退货扣款金额(元)")
    private BigDecimal daiXiaoTuiHuoKouKuan;

    @TableField(exist = false)
    @ApiModelProperty(value = "本次核销扣款(元)")
    private BigDecimal heXiaoKouKuan;

    @TableField(exist = false)
    @ApiModelProperty(value = "本次核销客供料扣款")
    private BigDecimal heXiaoJiagongliao;

    @TableField(exist = false)
    @ApiModelProperty(value = "本次核销退货扣款")
    private BigDecimal heXiaoTuihuo;

    @TableField(exist = false)
    @ApiModelProperty(value = "本次核销已预付款")
    private BigDecimal heXiaoYiyufu;

    /**
     * 本次核销总货款=本次实付金额+本次核销扣款+本次核销客供料扣款金额+本次核销退货扣款+本次核销已预付款
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "本次核销总货款")
    private BigDecimal heXiaoHuokuan;

    /**
     * 本次付款金额小计(含税)
     */
    @Digits(integer = 8, fraction = 4, message = "本次实付小计(含税)整数位上限为8位，小数位上限为4位")
    @Excel(name = "本次实付金额小计(含税)")
    @ApiModelProperty(value = "本次付款金额小计(含税)")
    private BigDecimal currencyAmountTax;

    /**
     * 币种（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "币种", dictType = "s_currency")
    @ApiModelProperty(value = "币种")
    private String currency;

    /**
     * 货币单位（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "货币单位", dictType = "s_currency_unit")
    @ApiModelProperty(value = "货币单位")
    private String currencyUnit;

    /**
     * 计划付款日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "计划付款日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划付款日期")
    private Date planPaymentDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "计划付款日期起")
    private String planPaymentDateBegin;

    @TableField(exist = false)
    @ApiModelProperty(value = "计划付款日期至")
    private String planPaymentDateEnd;

    /**
     * 付款状态（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "支付状态", dictType = "s_payment_status")
    @ApiModelProperty(value = "支付状态")
    private String paymentStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "支付状态")
    private String[] paymentStatusList;

    /**
     * 付款日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "支付日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "支付日期")
    private Date paymentDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "支付日期 起")
    private String paymentDateBegin;

    @TableField(exist = false)
    @ApiModelProperty(value = "支付日期 止")
    private String paymentDateEnd;

    @TableField(exist = false)
    @Excel(name = "付款方式")
    @ApiModelProperty(value = "付款方式")
    private String paymentMethodNames;

    /**
     * 系统SID-产品季档案
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-产品季档案")
    private Long productSeasonSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-产品季档案")
    private Long[] productSeasonSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "产品季")
    private String productSeasonName;

    /**
     * 银行名称
     */
    @Excel(name = "银行名称")
    @ApiModelProperty(value = "银行名称")
    private String bankName;

    /**
     * 银行账号
     */
    @Excel(name = "银行账号")
    @ApiModelProperty(value = "银行账号")
    private String bankAccount;

    /**
     * 银行所在城市
     */
    @ApiModelProperty(value = "银行所在城市")
    private String bankCity;

    /**
     * 付款主体sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "付款主体sid")
    private Long payCompanySid;

    @Excel(name = "是否有票", dictType = "s_yesno_flag")
    @ApiModelProperty(value = "是否有票")
    private String isYoupiao;

    @TableField(exist = false)
    @Excel(name = "是否到票提醒", dictType = "s_yesno_flag")
    @ApiModelProperty(value = "是否到票提醒")
    private String isRemindDaopiao;

    /**
     * 备注
     */
    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 紧急程度（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "紧急程度", dictType = "s_urgency_type")
    @ApiModelProperty(value = "紧急程度")
    private String urgency;

    @TableField(exist = false)
    @ApiModelProperty(value = "紧急程度")
    private String[] urgencyList;

    /**
     * 经办人（用户名称）
     */
    @ApiModelProperty(value = "经办人")
    private String agent;

    @TableField(exist = false)
    @ApiModelProperty(value = "经办人")
    private Long agentId;

    /**
     * 经办人（用户名称）
     */
    @TableField(exist = false)
    @Excel(name = "经办人")
    @ApiModelProperty(value = "经办人")
    private String agentName;

    /**
     * 处理状态（数据字典的键值或配置档案的编码）
     */
    @NotBlank(message = "处理状态不能为空")
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态")
    private String[] handleStatusList;

    @Excel(name = "当前审批节点")
    @TableField(exist = false)
    @ApiModelProperty(value = "当前审批节点名称")
    private String approvalNode;

    @Excel(name = "当前审批人")
    @TableField(exist = false)
    @ApiModelProperty(value = "当前审批人")
    private String approvalUserName;

    /**
     * 款项类别编码code（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "款项类别编码code")
    private String accountCategory;

    /**
     * 付款维度（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "付款维度")
    private String payDimension;

    /**
     * 公司品牌sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司品牌sid")
    private Long companyBrandSid;

    /**
     * 业务渠道（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "业务渠道")
    private String businessChannel;

    /**
     * 年份（付款期间）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "年份")
    private Long paymentYear;

    /**
     * 月份（付款期间）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "月份")
    private Long paymentMonth;

    /**
     * 日（付款期间）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "日")
    private Long paymentDay;

    /**
     * 月账单所属期间
     */
    @ApiModelProperty(value = "月账单所属期间")
    private String monthAccountPeriod;

    /**
     * 归属部门sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "归属部门sid")
    private Long departmentSid;

    /**
     * 采购组织（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "采购组织")
    private String purchaseOrg;

    /**
     * 付付款方式组合sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "付付款方式组合sid")
    private Long accountsMethodGroup;

    /**
     * 支付方式（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "支付方式")
    private String payMethod;

    @TableField(exist = false)
    @ApiModelProperty(value = "支付方式")
    private String[] payMethodList;

    /**
     * 是否预付手工录入
     */
    @ApiModelProperty(value = "是否预付手工录入")
    private String isAdvanceManual;

    /**
     * 是否已财务对账（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "是否已财务对账")
    private String isFinanceVerify;

    /**
     * 创建人账号（用户名称）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    /**
     * 创建人
     */
    @TableField(exist = false)
    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人昵称")
    private String creatorAccountName;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /**
     * 更新人账号（用户名称）
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccount;

    /**
     * 更改人
     */
    @TableField(exist = false)
    @Excel(name = "更改人")
    @ApiModelProperty(value = "更改人昵称")
    private String updaterAccountName;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.UPDATE)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更改日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 确认人账号（用户名称）
     */
    @ApiModelProperty(value = "确认人账号")
    private String confirmerAccount;

    /**
     * 确认人
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "确认人昵称")
    private String confirmerAccountName;

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
    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;

    /**
     * 付款单-明细对象list
     */
    @Valid
    @TableField(exist = false)
    @ApiModelProperty(value = "付款单-明细对象")
    private List<FinPayBillItem> itemList;

    /**
     * 付款单-附件对象list
     */
    @Valid
    @TableField(exist = false)
    @ApiModelProperty(value = "付款单-附件对象")
    private List<FinPayBillAttachment> attachmentList;

    @Valid
    @TableField(exist = false)
    @ApiModelProperty(value = "本次核销扣款明细列表")
    private List<FinPayBillItemKoukuan> koukuanList;

    @Valid
    @TableField(exist = false)
    @ApiModelProperty(value = "本次核销甲供料扣款明细列表")
    private List<FinPayBillItemKoukuanJiagongliao> jiagongliaoList;

    @Valid
    @TableField(exist = false)
    @ApiModelProperty(value = "本次核销退货扣款明细列表")
    private List<FinPayBillItemKoukuanTuihuo> tuihuoList;

    @Valid
    @TableField(exist = false)
    @ApiModelProperty(value = "本次核销货款明细列表")
    private List<FinPayBillItemHuokuan> huokuanList;

    @Valid
    @TableField(exist = false)
    @ApiModelProperty(value = "本次核销已预付款明细列表")
    private List<FinPayBillItemYufu> yufuList;

    @Valid
    @TableField(exist = false)
    @ApiModelProperty(value = "发票台账明细列表")
    private List<FinPayBillItemInvoice> invoiceList;

    @Valid
    @TableField(exist = false)
    @ApiModelProperty(value = "外发加工费结算单列表")
    private List<FinPayBillItemOutsourceSettle> outsourceSettleList;

    /**
     * 操作类型提交审批驳回
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "操作类型提交TJ审批SPTG驳回SPBH")
    private String operateType;

}

