package com.platform.ems.domain.dto.request.financial;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 客户扣款单明细详情查询请求实体 FinCustomerDeductionBillItemInfoRequest
 *
 * @author chenkaiwen
 * @date 2021-07-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel
public class FinCustomerDeductionBillItemInfoRequest extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "客户扣款单明细sid")
    private Long deductionBillItemSid;

    @NotBlank(message = "扣款类型不能为空")
    @ApiModelProperty(value = "扣款类型（配置档案）")
    private String deductionType;

    @NotNull(message = "扣款金额不能为空")
    @Digits(integer = 11, fraction = 4, message = "扣款金额整数位上限为11位，小数位上限为4位")
    @ApiModelProperty(value = "扣款金额(含税)")
    private BigDecimal currencyAmountTax;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售合同sid")
    private Long saleContractSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售订单sid")
    private Long salesOrderSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售交货单sid")
    private Long deliveryNoteSid;

    @ApiModelProperty(value = "纸质合同号")
    private String paperContractCode;

    @ApiModelProperty(value = "数据源系统（数据字典）")
    private String dataSourceSys;

}
