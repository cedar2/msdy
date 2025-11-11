package com.platform.ems.domain.dto.response.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 扣款单明细报表 FinVendorDeductionBillItemFormResponse
 *
 * @author chenkaiwen
 * @date 2021-07-12
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FinVendorDeductionBillItemFormResponse extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @ApiModelProperty(value = "供应商扣款单号")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long deductionBillCode;

    @ApiModelProperty(value = "供应商")
    private String vendorName;

    @ApiModelProperty(value = "公司")
    private String companyName;

    @ApiModelProperty(value = "产品季")
    private String productSeasonName;

    @ApiModelProperty(value = "物料类型")
    private String materialTypeName;

    @ApiModelProperty(value = "核销状态（数据字典）")
    private String clearStatus;

    @ApiModelProperty(value = "采购员")
    private String buyerName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "单据日期")
    private Date documentDate;

    @ApiModelProperty(value = "扣款类型")
    private String deductionTypeName;

    @ApiModelProperty(value = "扣款金额(含税)")
    private BigDecimal currencyAmountTax;

    @ApiModelProperty(value = "币种（数据字典的键值）")
    private String currency;

    @ApiModelProperty(value = "货币单位（数据字典的键值）")
    private String currencyUnit;

    @ApiModelProperty(value = "采购合同号")
    private String purchaseContractCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购订单号")
    private Long purchaseOrderCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购交货单/销售发货单号")
    private Long deliveryNoteCode;

    @ApiModelProperty(value = "创建人")
    private String creatorAccountName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @ApiModelProperty(value = "处理状态（数据字典的键值）")
    private String handleStatus;

    @ApiModelProperty(value = "数据源系统（数据字典的键值）")
    private String dataSourceSys;
}
