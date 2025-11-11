package com.platform.ems.domain.dto.response.financial;

import com.baomidou.mybatisplus.annotation.TableField;
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

/**
 * 收款单明细详情返回实体 FinReceivableBillItemInfoResponse
 *
 * @author chenkaiwen
 * @date 2021-07-26
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FinReceivableBillItemInfoResponse extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-收款单明细")
    private Long receivableBillItemSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-流水账（应收）")
    private Long bookAccountReceivableSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-流水账明细（应收）")
    private Long bookAccountReceivableItemSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "流水号（应收）")
    private Long bookAccountReceivableCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-流水账（预收）")
    private Long recordAdvanceReceiptSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-流水账明细（预收）")
    private Long recordAdvanceReceiptCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "流水号（预收）")
    private Long recordAdvanceReceiptItemSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "财务凭证流水sid（台帐流水号/财务账流水号）")
    private Long accountDocumentSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "财务凭证流水code（台帐流水号/财务账流水号）")
    private Long accountDocumentCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "财务凭证流水行sid")
    private Long accountItemSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "财务凭证流水行号")
    private Long accountItemCode;

    @ApiModelProperty(value = "财务流水类型")
    private String bookType;

    @ApiModelProperty(value = "财务流水类型")
    private String bookTypeName;

    @ApiModelProperty(value = "财务流水来源类别")
    private String bookSourceCategory;

    @ApiModelProperty(value = "财务流水来源类别")
    private String bookSourceCategoryName;

    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "本次申请金额（含税）/本次收款金额（含税）")
    private BigDecimal currencyAmountTax;

    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "应收金额（含税）")
    private BigDecimal currencyAmountTaxYings;

    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "已核销金额（含税）")
    private BigDecimal currencyAmountTaxYhx;

    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "核销中金额（含税）")
    private BigDecimal currencyAmountTaxHxz;

    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "待核销（含税）待收")
    private BigDecimal currencyAmountTaxDhx;

    @ApiModelProperty(value = "税率（存值，即：不含百分号，如20%，就存0.2）")
    private BigDecimal taxRate;

    @ApiModelProperty(value = "税率")
    private String taxRateName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-销售发票")
    private Long saleInvoiceSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售发票记录号")
    private Long saleInvoiceCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售订单sid")
    private Long salesOrderSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售订单号")
    private Long salesOrderCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-销售合同")
    private Long saleContractSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售合同号")
    private String saleContractCode;

    @ApiModelProperty(value = "数据源系统（数据字典）")
    private String dataSourceSys;

}
