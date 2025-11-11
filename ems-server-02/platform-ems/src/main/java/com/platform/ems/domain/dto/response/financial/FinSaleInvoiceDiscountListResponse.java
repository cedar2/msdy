package com.platform.ems.domain.dto.response.financial;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.core.domain.EmsBaseEntity;
import com.platform.ems.util.data.KeepTwoDecimalsSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 *  销售发票折扣明细返回实体 FinSaleInvoiceDiscountListResponse
 *
 * @author chenkaiwen
 * @date 2021-07-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FinSaleInvoiceDiscountListResponse extends EmsBaseEntity implements Serializable {

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

    @ApiModelProperty(value = "财务流水类型")
    private String bookTypeName;

    @ApiModelProperty(value = "财务流水来源类别编码（配置档案）")
    private String bookSourceCategory;

    @ApiModelProperty(value = "财务流水来源类别")
    private String bookSourceCategoryName;

    @ApiModelProperty(value = "核销状态")
    private String clearStatus;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "财务凭证流水行sid")
    private Long accountItemSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "财务凭证流水行code")
    private Long accountItemCode;

    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "本次抵折扣金额(含税)")
    private BigDecimal currencyAmountTax;

    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "本次抵折扣金额(不含税)")
    private BigDecimal currencyAmount;

    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "本次抵折扣税额")
    private BigDecimal taxAmount;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-客户")
    private Long customerSid;

    @ApiModelProperty(value = "客户")
    private String customerName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-公司档案")
    private Long companySid;

    @ApiModelProperty(value = "公司")
    private String companyName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-产品季档案")
    private Long productSeasonSid;

    @ApiModelProperty(value = "产品季")
    private String productSeasonName;

    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "应抵扣金额（扣款金额/调账金额..）")
    private BigDecimal currencyAmountTaxYingD;

    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "待抵扣金额（应抵-已核销-核销中）")
    private BigDecimal currencyAmountTaxDaiD;

    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "已核销金额（含税）")
    private BigDecimal currencyAmountTaxYhx;

    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "核销中金额（含税）")
    private BigDecimal currencyAmountTaxHxz;

    @ApiModelProperty(value = "行号")
    private int itemNum;

    @ApiModelProperty(value = "数据源系统（数据字典的键值）")
    private String dataSourceSys;
}
