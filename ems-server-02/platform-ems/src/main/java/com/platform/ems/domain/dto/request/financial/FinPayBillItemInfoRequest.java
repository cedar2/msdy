package com.platform.ems.domain.dto.request.financial;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 付款单明细详情请求实体 FinPayBillItemInfoRequest
 *
 * @author chenkaiwen
 * @date 2021-07-26
 */
@Data
@Accessors(chain = true)
@ApiModel
public class FinPayBillItemInfoRequest extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-付款单明细")
    private Long payBillItemSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-流水账（应付）")
    private Long bookAccountPayableSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-流水账明细（应付）")
    private Long bookAccountPayableItemSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "流水号（应付）")
    private Long bookAccountPayableCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-流水账（预付）")
    private Long recordAdvancePaymentSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-流水账明细（预付）")
    private Long recordAdvancePaymentCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "流水号（预付）")
    private Long recordAdvancePaymentItemSid;

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

    @ApiModelProperty(value = "财务流水来源类别")
    private String bookSourceCategory;

    @NotNull(message = "本次申请金额不能为空")
    @Digits(integer = 11,fraction = 4, message = "本次申请金额整数位上限为11位，小数位上限为4位")
    @ApiModelProperty(value = "本次申请金额（含税）/本次付款金额（含税）")
    private BigDecimal currencyAmountTax;

    @ApiModelProperty(value = "本次申请金额（含税）/本次付款金额（含税）")
    private BigDecimal currencyAmountTaxYingf;

    @ApiModelProperty(value = "税率（存值，即：不含百分号，如20%，就存0.2）")
    private BigDecimal taxRate;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-采购发票")
    private Long purchaseInvoiceSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购发票记录号")
    private Long purchaseInvoiceCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购订单sid")
    private Long purchaseOrderSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购订单号")
    private Long purchaseOrderCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-采购合同")
    private Long purchaseContractSid;

    @ApiModelProperty(value = "采购合同号")
    private String purchaseContractCode;

    @ApiModelProperty(value = "数据源系统（数据字典）")
    private String dataSourceSys;

}
