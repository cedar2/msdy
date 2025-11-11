package com.platform.ems.domain.dto.response.financial;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.core.domain.EmsBaseEntity;
import com.platform.ems.util.data.KeepTwoDecimalsSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 供应商互抵单明细详情返回实体 FinVendorAccountBalanceBillItemInfoResponse
 *
 * @author chenkaiwen
 * @date 2021-07-26
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FinVendorAccountBalanceBillItemInfoResponse extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @ApiModelProperty(value = "数据源系统（数据字典）")
    private String dataSourceSys;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商账互抵单明细")
    private Long accountBalanceBillItemSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "财务凭证流水sid（台帐流水号/财务账流水号）")
    private Long accountDocumentSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "财务凭证流水code（台帐流水号/财务账流水号） 关联单据号")
    private Long accountDocumentCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "财务凭证流水行sid")
    private Long accountItemSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "财务凭证流水行号 关联单据行号")
    private Long accountItemCode;

    @ApiModelProperty(value = "财务流水类型编码code")
    private String bookType;

    @ApiModelProperty(value = "财务流水类型编码code")
    private String bookTypeName;

    @ApiModelProperty(value = "财务流水来源类别编码code")
    private String bookSourceCategory;

    @ApiModelProperty(value = "财务流水来源类别编码code")
    private String bookSourceCategoryName;

    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "本次互抵金额(含税)（元）")
    private BigDecimal currencyAmountTax;

    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "应互抵金额（含税）")
    private BigDecimal currencyAmountTaxYhd;

    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "待核销金额（含税）")
    private BigDecimal currencyAmountTaxDhx;

    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "已核销金额（含税）")
    private BigDecimal currencyAmountTaxYhx;

    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "核销中金额（含税）")
    private BigDecimal currencyAmountTaxHxz;

    @ApiModelProperty(value = "供应商")
    private Long vendorSid;

    @ApiModelProperty(value = "供应商")
    private String vendorName;


}
