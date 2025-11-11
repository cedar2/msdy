package com.platform.ems.domain.dto.response.financial;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 供应商扣款单明细详情查询返回实体 FinVendorDeductionBillItemInfoResponse
 *
 * @author chenkaiwen
 * @date 2021-07-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FinVendorDeductionBillItemInfoResponse extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "供应商扣款单明细sid")
    private Long deductionBillItemSid;

    @ApiModelProperty(value = "扣款类型")
    private String deductionType;

    @ApiModelProperty(value = "扣款类型名称")
    private String deductionTypeName;

    @ApiModelProperty(value = "扣款金额(含税)")
    private BigDecimal currencyAmountTax;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购合同sid")
    private Long purchaseContractSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购合同号")
    private Long purchaseContractCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购订单sid")
    private Long purchaseOrderSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购订单号")
    private Long purchaseOrderCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购交货单sid")
    private Long deliveryNoteSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购交货单号")
    private Long deliveryNoteCode;

    @ApiModelProperty(value = "纸质合同号")
    private String paperContractCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "行号")
    private Long itemNum;

    @ApiModelProperty(value = "创建人账号（user_name）")
    private String creatorAccount;

    @ApiModelProperty(value = "创建人昵称")
    private String creatorAccountName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @ApiModelProperty(value = "数据源系统（数据字典）")
    private String dataSourceSys;
}
