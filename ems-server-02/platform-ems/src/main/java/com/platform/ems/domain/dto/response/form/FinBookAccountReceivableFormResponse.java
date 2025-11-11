package com.platform.ems.domain.dto.response.form;

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

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 应收流水报表 FinBookAccountPayableFormResponse
 *
 * @author chenkaiwen
 * @date 2021-07-12
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FinBookAccountReceivableFormResponse extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-流水账（应付）")
    private Long bookAccountReceivableSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-流水账明细（应付）")
    private Long bookAccountReceivableItemSid;

    @Excel(name = "应收财务流水号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "流水号（应付）")
    private Long bookAccountReceivableCode;

    @Excel(name = "客户")
    @ApiModelProperty(value = "客户名称")
    private String customerName;

    @ApiModelProperty(value = "客户简称")
    private String customerShortName;

    @Excel(name = "公司")
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @ApiModelProperty(value = "公司简称")
    private String companyShortName;

    @Excel(name = "流水类型")
    @ApiModelProperty(value = "财务流水类型编码code")
    private String bookTypeName;

    @Excel(name = "流水来源类别")
    @ApiModelProperty(value = "财务流水来源类别编码code（数据字典的键值或配置档案的编码）")
    private String bookSourceCategoryName;

    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    @Excel(name = "核销状态", dictType = "s_account_clear")
    @ApiModelProperty(value = "核销状态")
    private String clearStatus;

    @Excel(name = "应收金额（含税）")
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "应收金额")
    private BigDecimal currencyAmountTaxYings;

    @Excel(name = "待核销金额（含税）")
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "待核销金额")
    private BigDecimal currencyAmountTaxDhx;

    @Excel(name = "核销中金额（含税）")
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "核销中金额")
    private BigDecimal currencyAmountTaxHxz;

    @Excel(name = "已核销金额（含税）")
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "已核销金额")
    private BigDecimal currencyAmountTaxYhx;

    @Excel(name = "税率")
    @ApiModelProperty(value = "税率")
    private BigDecimal taxRate;

    @Excel(name = "到期日", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "账期")
    private Date accountValidDate;

    @Excel(name = "销售发票记录号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售发票（应收）")
    private Long saleInvoiceCode;

    @Excel(name = "是否已财务对账", dictType = "s_yesno_flag")
    @ApiModelProperty(value = "是否已财务对账（数据字典的键值或配置档案的编码）\n")
    private String isFinanceVerify;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    @Excel(name = "行号")
    @ApiModelProperty(value = "行号")
    private Long itemNum;

    @Excel(name = "币种", dictType = "s_currency")
    @ApiModelProperty(value = "币种")
    private String currency;

    @Excel(name = "货币单位", dictType = "s_currency_unit")
    @ApiModelProperty(value = "货币单位")
    private String currencyUnit;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人账号（用户昵称）")
    private String creatorAccountName;

    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @Excel(name = "产品季")
    @ApiModelProperty(value = "产品季名称")
    private String productSeasonName;

    @ApiModelProperty(value = "财务流水类型编码code")
    private String bookType;

    @ApiModelProperty(value = "财务流水来源类别编码code（数据字典的键值或配置档案的编码）")
    private String bookSourceCategory;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "客户")
    private Long customerSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司")
    private Long companySid;

    @ApiModelProperty(value = "销售合同号")
    private String saleContractCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售合同号")
    private Long saleContractSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售订单号")
    private Long saleOrderCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售订单号")
    private Long saleOrderSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售发票号")
    private Long saleInvoiceSid;
}
