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
 * 收款申请单明细报表 FinReceivableBillItemFormResponse
 *
 * @author chenkaiwen
 * @date 2021-07-12
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FinReceivableBillItemFormResponse extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @Excel(name = "收款单号")
    @ApiModelProperty(value = "收款申请单号")
    private String receivableBillCode;

    @Excel(name = "单据类型")
    @ApiModelProperty(value = "单据类型")
    private String documentTypeName;

    @Excel(name = "业务类型")
    @ApiModelProperty(value = "业务类型")
    private String businessTypeName;

    @ApiModelProperty(value = "客户")
    private String customerName;

    @Excel(name = "客户")
    @ApiModelProperty(value = "客户")
    private String customerShortName;

    @ApiModelProperty(value = "公司")
    private String companyName;

    @Excel(name = "公司")
    @ApiModelProperty(value = "公司")
    private String companyShortName;

    @Excel(name = "金额(含税)", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "收款金额(元)")
    private BigDecimal currencyAmountTax;

    @Excel(name = "收款方式")
    @ApiModelProperty(value = "收款方式")
    private String paymentMethodName;

    @Excel(name = "款项类别")
    @ApiModelProperty(value = "款项类别名称")
    private String accountCategoryName;

    @Excel(name = "销售渠道")
    @ApiModelProperty(value = "销售渠道")
    private String businessChannelName;

    @Excel(name = "下单季")
    @ApiModelProperty(value = "产品季")
    private String productSeasonName;

    @Excel(name = "纸质合同号")
    @ApiModelProperty(value = "纸质合同号")
    private String paperContractCode;

    @ApiModelProperty(value = "销售组织")
    private String saleOrgName;

    @ApiModelProperty(value = "销售部门")
    private String saleDepartmentName;

    @ApiModelProperty(value = "经办人")
    private String agentName;

    //    @Excel(name = "收款日期", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "收款日期")
    private Date receivableDate;

    @ApiModelProperty(value = "财务流水类型")
    private String bookTypeName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "财务流水号（台帐流水号/财务账流水号）")
    private Long accountDocumentCode;

    @ApiModelProperty(value = "财务流水来源类别")
    private String bookSourceCategoryName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "财务凭证流水行号")
    private Long accountItemCode;

    @ApiModelProperty(value = "税率")
    private BigDecimal taxRate;

    @ApiModelProperty(value = "行号")
    private Long itemNum;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售发票记录号")
    private Long saleInvoiceCode;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    @Excel(name = "币种", dictType = "s_currency")
    @ApiModelProperty(value = "币种（数据字典）")
    private String currency;

    @Excel(name = "货币单位", dictType = "s_currency_unit")
    @ApiModelProperty(value = "币种单位（数据字典）")
    private String currencyUnit;

    @Excel(name = "到账状态", dictType = "s_receipt_payment_status")
    @ApiModelProperty(value = "收款状态（数据字典")
    private String receiptPaymentStatus;

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

    @ApiModelProperty(value = "数据源系统（数据字典）")
    private String dataSourceSys;
}
