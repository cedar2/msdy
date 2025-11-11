package com.platform.ems.domain.dto.request.financial;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 *  销售发票新增折扣接收实体 FinSaleInvoiceDiscountInfoRequest
 *
 * @author chenkaiwen
 * @date 2021-07-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel
public class FinSaleInvoiceDiscountInfoRequest extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-销售发票折扣")
    private Long saleInvoiceDiscountSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "财务凭证流水sid（台帐流水号/财务账流水号）")
    private Long accountDocumentSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "财务凭证流水code（台帐流水号/财务账流水号）")
    private Long accountDocumentCode;

    @ApiModelProperty(value = "财务流水类型编码code")
    private String bookType;

    @ApiModelProperty(value = "财务流水来源类别编码code（配置档案）")
    private String bookSourceCategory;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "财务凭证流水行sid")
    private Long accountItemSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "财务凭证流水行code")
    private Long accountItemCode;

    @NotNull(message = "折扣金额不能为空")
    @ApiModelProperty(value = "本次抵折扣金额(含税)")
    private BigDecimal currencyAmountTax;

    @ApiModelProperty(value = "本次抵折扣金额(含税)")
    private BigDecimal currencyAmountTaxYingD;

    @ApiModelProperty(value = "本次抵折扣金额(不含税)")
    private BigDecimal currencyAmount;

    @ApiModelProperty(value = "本次抵折扣税额")
    private BigDecimal taxAmount;

    @ApiModelProperty(value = "行号")
    private int itemNum;

    @ApiModelProperty(value = "数据源系统（数据字典的键值）")
    private String dataSourceSys;
}
