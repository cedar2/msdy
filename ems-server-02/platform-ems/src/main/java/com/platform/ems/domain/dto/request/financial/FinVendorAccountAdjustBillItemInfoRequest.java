package com.platform.ems.domain.dto.request.financial;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 供应商调账单明细详情查询请求实体 FinVendorAccountAdjustBillItemInfoRequest
 *
 * @author chenkaiwen
 * @date 2021-07-12
 */
@Data
@Accessors(chain = true)
@ApiModel
public class FinVendorAccountAdjustBillItemInfoRequest extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "供应商调账单明细sid")
    private Long adjustBillItemSid;

    @NotBlank(message = "调账类型不能为空")
    @ApiModelProperty(value = "调账类型（配置档案）")
    private String adjustType;

    @NotNull(message = "调账金额不能为空")
    @Digits(integer = 11, fraction = 4, message = "调账金额整数位上限为11位，小数位上限为4位")
    @ApiModelProperty(value = "调账金额(含税)")
    private BigDecimal currencyAmountTax;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购合同sid")
    private Long purchaseContractSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购订单sid")
    private Long purchaseOrderSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购交货单sid")
    private Long deliveryNoteSid;

    @ApiModelProperty(value = "数据源系统（数据字典）")
    private String dataSourceSys;
}
