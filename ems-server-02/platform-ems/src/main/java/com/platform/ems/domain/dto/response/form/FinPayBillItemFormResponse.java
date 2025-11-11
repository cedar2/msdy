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
 * 付款申请单明细报表 FinPayBillItemFormResponse
 *
 * @author chenkaiwen
 * @date 2021-07-12
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FinPayBillItemFormResponse extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @Excel(name = "付款申请单号")
    @ApiModelProperty(value = "付款申请单号")
    private String payBillCode;

    @Excel(name = "单据类型")
    @ApiModelProperty(value = "单据类型")
    private String documentTypeName;

    @Excel(name = "业务类型")
    @ApiModelProperty(value = "业务类型")
    private String businessTypeName;

    @ApiModelProperty(value = "供应商")
    private String vendorName;

    @Excel(name = "供应商")
    @ApiModelProperty(value = "供应商")
    private String vendorShortName;

    @ApiModelProperty(value = "公司")
    private String companyName;

    @Excel(name = "公司")
    @ApiModelProperty(value = "供应商")
    private String companyShortName;

    @Excel(name = "金额(含税)", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "付款金额(元)")
    private BigDecimal currencyAmountTax;

    @Excel(name = "付款方式")
    @ApiModelProperty(value = "支付方式")
    private String paymentMethodName;

    @Excel(name = "款项类别")
    @ApiModelProperty(value = "款项类别名称")
    private String accountCategoryName;

    @Excel(name = "下单季")
    @ApiModelProperty(value = "产品季")
    private String productSeasonName;

    @Excel(name = "纸质合同号")
    @ApiModelProperty(value = "纸质合同号")
    private String paperContractCode;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    @Excel(name = "币种", dictType = "s_currency")
    @ApiModelProperty(value = "币种（数据字典）")
    private String currency;

    @Excel(name = "货币单位", dictType = "s_currency_unit")
    @ApiModelProperty(value = "币种单位（数据字典）")
    private String currencyUnit;

    @Excel(name = "支付完成状态", dictType = "s_payment_status")
    @ApiModelProperty(value = "付款状态（数据字典")
    private String paymentStatus;

    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value ="处理状态（数据字典）")
    private String handleStatus;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人")
    private String creatorAccountName;

    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @Excel(name = "更改人")
    @ApiModelProperty(value = "更改人昵称")
    private String updaterAccountName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更改日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    @ApiModelProperty(value = "采购组织")
    private String purchaseOrgName;

    @ApiModelProperty(value = "经办人")
    private String agentName;

    //        @Excel(name = "付款日期", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "付款日期")
    private Date paymentDate;

    //    @Excel(name = "财务流水类型")
    @ApiModelProperty(value = "财务流水类型")
    private String bookTypeName;

    //    @Excel(name = "财务流水号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "财务流水号（台帐流水号/财务账流水号）")
    private Long accountDocumentCode;

    //    @Excel(name = "财务流水来源类别")
    @ApiModelProperty(value = "财务流水来源类别")
    private String bookSourceCategoryName;

    //    @Excel(name = "财务凭证流水行号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "财务凭证流水行号")
    private Long accountItemCode;

    //    @Excel(name = "税率")
    @ApiModelProperty(value = "税率")
    private BigDecimal taxRate;


    //    @Excel(name = "行号")
    @ApiModelProperty(value = "行号")
    private Long itemNum;

    //    @Excel(name = "采购发票记录号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购发票记录号")
    private Long purchaseInvoiceCode;

    @ApiModelProperty(value = "数据源系统（数据字典）")
    private String dataSourceSys;
}
