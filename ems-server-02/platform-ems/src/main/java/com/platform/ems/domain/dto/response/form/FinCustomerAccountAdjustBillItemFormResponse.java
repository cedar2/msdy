package com.platform.ems.domain.dto.response.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 客户调账单明细报表 FinCustomerAccountAdjustBillItemFormResponse
 *
 * @author chenkaiwen
 * @date 2021-07-12
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FinCustomerAccountAdjustBillItemFormResponse extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @Excel(name = "客户调账单号")
    @ApiModelProperty(value = "客户调账单号")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long adjustBillCode;

    @Excel(name = "客户")
    @ApiModelProperty(value = "客户")
    private String customerName;

    @Excel(name = "公司")
    @ApiModelProperty(value = "公司")
    private String companyName;

    @Excel(name = "产品季")
    @ApiModelProperty(value = "产品季")
    private String productSeasonName;

    /**    @Excel(name = "物料类型") */
    @ApiModelProperty(value = "物料类型")
    private String materialTypeName;

    @Excel(name = "核销状态", dictType = "s_account_clear")
    @ApiModelProperty(value = "核销状态（数据字典）")
    private String clearStatus;

    @Excel(name = "销售员")
    @ApiModelProperty(value = "销售员")
    private String salePersonName;

    @Excel(name = "单据日期", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "单据日期")
    private Date documentDate;

    @Excel(name = "调账类型")
    @ApiModelProperty(value = "调账类型")
    private String adjustTypeName;

    @Excel(name = "调账金额(含税)")
    @ApiModelProperty(value = "调账金额(含税)")
    private BigDecimal currencyAmountTax;

    @Excel(name = "币种", dictType = "s_currency")
    @ApiModelProperty(value = "币种（数据字典的键值）")
    private String currency;

    @Excel(name = "货币单位", dictType = "s_currency_unit")
    @ApiModelProperty(value = "货币单位（数据字典的键值）")
    private String currencyUnit;

    /**    @Excel(name = "销售合同号") */
    @ApiModelProperty(value = "销售合同号")
    private String saleContractCode;

    /**    @Excel(name = "销售订单号") */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售订单号")
    private Long salesOrderCode;

    /**    @Excel(name = "销售交货单") */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售交货单/销售发货单号")
    private Long deliveryNoteCode;

    /**    @Excel(name = "销售发票记录号") */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售发票记录号")
    private Long saleInvoiceCode;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人")
    private String creatorAccountName;

    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值）")
    private String handleStatus;

    @ApiModelProperty(value = "数据源系统（数据字典的键值）")
    private String dataSourceSys;
}
