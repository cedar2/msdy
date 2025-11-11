package com.platform.ems.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;

/**
 * 客户发票台账表对象 s_fin_customer_invoice_record
 *
 * @author platform
 * @date 2024-03-12
 */
@Data
@Accessors(chain = true)
@TableName(value = "s_fin_customer_invoice_record")
public class FinCustomerInvoiceRecord extends EmsBaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 租户ID
     */
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-客户发票台账
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-客户发票台账")
    private Long customerInvoiceRecordSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "sid数组")
    private Long[] customerInvoiceRecordSidList;

    /**
     * 客户发票台账号
     */
    @Excel(name = "客户发票台账流水号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "客户发票台账号")
    private Long customerInvoiceRecordCode;

    /**
     * 发票类型（s_con_invoice_type）
     */
    @ApiModelProperty(value = "发票类型")
    private String invoiceType;

    @TableField(exist = false)
    @Excel(name = "发票类型")
    @ApiModelProperty(value = "发票类型")
    private String invoiceTypeName;

    @TableField(exist = false)
    @ApiModelProperty(value = "发票类型")
    private String[] invoiceTypeList;

    /**
     * 系统SID-客户
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-客户")
    private Long customerSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-客户")
    private Long[] customerSidList;

    /**
     * 客户编码
     */
    @ApiModelProperty(value = "客户编码")
    private String customerCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "客户")
    private String customerName;

    @TableField(exist = false)
    @Excel(name = "客户")
    @ApiModelProperty(value = "客户")
    private String customerShortName;

    /**
     * 发票号（纸质发票）
     */
    @Excel(name = "发票号")
    @ApiModelProperty(value = "发票号")
    private String invoiceNum;

    /**
     * 发票日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "发票日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "发票日期")
    private Date invoiceDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "发票日期 起")
    private String invoiceDateBegin;

    @TableField(exist = false)
    @ApiModelProperty(value = "发票日期 至")
    private String invoiceDateEnd;

    /**
     * 票面总金额(含税)
     */
    @Digits(integer = 13, fraction = 2, message = "票面金额(含税)整数位上限为13位，小数位上限为2位")
    @Excel(name = "票面金额(含税)", cellType = Excel.ColumnType.NUMERIC, scale = 2)
    @ApiModelProperty(value = "票面总金额(含税)")
    private BigDecimal totalCurrencyAmountTax;

    /**
     * 收款状态（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "收款状态", dictType = "s_shoukuan_status")
    @ApiModelProperty(value = "收款状态")
    private String shoukuanStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "收款状态")
    private String[] shoukuanStatusList;

    /**
     * 待收款金额
     */
    @TableField(exist = false)
    @Excel(name = "待收款金额", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "待收款金额")
    private BigDecimal currencyAmountTaxDai;

    /**
     * 收款中金额
     */
    @TableField(exist = false)
    @Excel(name = "收款中金额", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "收款中金额")
    private BigDecimal currencyAmountTaxZhong;

    /**
     * 已收款金额
     */
    @TableField(exist = false)
    @Excel(name = "已收款金额", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "已收款金额")
    private BigDecimal currencyAmountTaxYi;

    /**
     * 税率（存值，即：不含百分号，如20%，就存0.2）
     */
    @Excel(name = "税率")
    @ApiModelProperty(value = "税率")
    private BigDecimal taxRate;

    /**
     * 票面总税额
     */
    @Digits(integer = 13, fraction = 2, message = "票面税额整数位上限为13位，小数位上限为2位")
    @Excel(name = "票面税额", cellType = Excel.ColumnType.NUMERIC, scale = 2)
    @ApiModelProperty(value = "票面总税额")
    private BigDecimal totalTaxAmount;

    /**
     * 票面总金额(不含税)
     */
    @Digits(integer = 13, fraction = 2, message = "票面金额(不含税)整数位上限为13位，小数位上限为2位")
    @Excel(name = "票面金额(不含税)", cellType = Excel.ColumnType.NUMERIC, scale = 2)
    @ApiModelProperty(value = "票面总金额(不含税)")
    private BigDecimal totalCurrencyAmount;

    /**
     * 系统SID-公司
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-公司")
    private Long companySid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-公司")
    private Long[] companySidList;

    /**
     * 公司编码
     */
    @ApiModelProperty(value = "公司编码")
    private String companyCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司编码")
    private String companyName;

    @TableField(exist = false)
    @Excel(name = "公司")
    @ApiModelProperty(value = "公司编码")
    private String companyShortName;

    /**
     * 发票类别（s_con_invoice_category）
     */
    @ApiModelProperty(value = "发票类别")
    private String invoiceCategory;

    @TableField(exist = false)
    @ApiModelProperty(value = "发票类别")
    private String[] invoiceCategoryList;

    @TableField(exist = false)
    @Excel(name = "发票类别")
    @ApiModelProperty(value = "发票类别")
    private String invoiceCategoryName;

    /**
     * 发票寄出状态（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "发票寄出状态", dictType = "s_invoice_send_status")
    @ApiModelProperty(value = "发票寄出状态")
    private String sendFlag;

    @TableField(exist = false)
    @ApiModelProperty(value = "发票寄出状态")
    private String[] sendFlagList;

    /**
     * 发票代码（纸质发票）
     */
    @Excel(name = "发票代码")
    @ApiModelProperty(value = "发票代码")
    private String inoviceCode;

    /**
     * 纸质合同号
     */
    @Excel(name = "纸质合同号")
    @ApiModelProperty(value = "纸质合同号")
    private String paperContractCode;

    /**
     * 系统SID-产品季/下单季
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-产品季/下单季")
    private Long productSeasonSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-产品季/下单季")
    private Long[] productSeasonSidList;

    @TableField(exist = false)
    @Excel(name = "下单季")
    @ApiModelProperty(value = "系统SID-产品季/下单季")
    private String productSeasonName;

    /**
     * 物料类型（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "物料类型")
    private String materialType;

    /**
     * 物料类型（数据字典的键值或配置档案的编码）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "物料类型")
    private String[] materialTypeList;

    @TableField(exist = false)
    @Excel(name = "物料类型")
    @ApiModelProperty(value = "物料类型")
    private String materialTypeName;

    @Excel(name = "对账账期")
    @ApiModelProperty(value = "对账账期/业务对账所属期间（所属年月）")
    private String businessVerifyPeriod;

    /**
     * 总账日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "总账日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "总账日期")
    private Date generalLedgerDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "总账日期 起")
    private String generalLedgerDateBegin;

    @TableField(exist = false)
    @ApiModelProperty(value = "总账日期 止")
    private String generalLedgerDateEnd;

    /**
     * 参考发票台账sid（如此张发票是红冲发票；此字段保存红冲前的发票台账sid）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "参考发票台账sid")
    private Long referInvoiceRecordSid;

    /**
     * 参考发票台账号（如此张发票是红冲发票；此字段保存红冲前的发票台账号）
     */
    @Excel(name = "参考发票台账号(红冲前)")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "参考发票台账号")
    private Long referInvoiceRecordCode;

    /**
     * 币种（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "币种")
    private String currency;

    /**
     * 货币单位（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "货币单位")
    private String currencyUnit;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "最近被收款单引用日期")
    private Date newReceivableUseDate;

    /**
     * 作废说明
     */
    @ApiModelProperty(value = "作废说明")
    private String cancelRemark;

    /**
     * 红冲说明
     */
    @ApiModelProperty(value = "红冲说明")
    private String hongchongRemark;

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

    /**
     * 创建人账号（用户账号）
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
     * 更新人账号（用户账号）
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
     * 确认人账号（用户账号）
     */
    @ApiModelProperty(value = "确认人账号")
    private String confirmerAccount;

    /**
     * 确认人
     */
    @TableField(exist = false)
    @Excel(name = "确认人")
    @ApiModelProperty(value = "确认人昵称")
    private String confirmerAccountName;

    /**
     * 确认时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "确认日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;

    @TableField(exist = false)
    @ApiModelProperty(value = "附件清单")
    private List<FinCustomerInvoiceRecordAttachment> attachmentList;

    @TableField(exist = false)
    @ApiModelProperty(value = "导入")
    private String importType;
}
