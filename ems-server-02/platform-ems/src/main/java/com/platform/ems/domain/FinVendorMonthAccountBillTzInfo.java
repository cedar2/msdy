package com.platform.ems.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;
import com.platform.ems.util.data.KeepTwoDecimalsSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 月对账单用 调账单
 *
 * @author chenkw
 * @date 2021-11-05
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FinVendorMonthAccountBillTzInfo extends EmsBaseEntity {

    @Excel(name = "业务单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "业务单号")
    private Long billCode;

    @ApiModelProperty(value = "关联业务单号：发票/收付款/互抵单")
    private String referBillCode;

    @ApiModelProperty(value = "财务流水类型")
    private String bookTypeName;

    @ApiModelProperty(value = "财务流水来源类别")
    private String bookSourceCategoryName;

    @ApiModelProperty(value = "供应商")
    private String vendorName;

    @ApiModelProperty(value = "公司")
    private String companyName;

    @ApiModelProperty(value = "产品季")
    private String productSeasonName;

    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "本次扣款金额(含税)")
    private BigDecimal currencyAmountTax;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "单据日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "单据日期")
    private Date documentDate;

    @Excel(name = "月账单所属期间")
    @ApiModelProperty(value = "月账单所属期间")
    private String monthAccountPeriod;

    @Excel(name = "行号")
    @ApiModelProperty(value = "行号")
    private int itemNum;

    @Excel(name = "币种")
    @ApiModelProperty(value = "币种")
    private String currency;

    @Excel(name = "币种单位")
    @ApiModelProperty(value = "币种单位")
    private String currencyUnit;

    @Excel(name = "创建人账号（用户名称）")
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccountName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-付款单明细")
    private Long payBillItemSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-付款单")
    private Long payBillSid;

    @ApiModelProperty(value = "系统SID-付款单")
    private Long payBillCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-采购发票")
    private Long purchaseInvoiceDiscountSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-采购发票")
    private Long purchaseInvoiceSid;

    @Excel(name = "系统SID-采购发票")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-采购发票")
    private Long purchaseInvoiceCode;

    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @Excel(name = "财务凭证流水sid（台帐流水号/财务账流水号）")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "财务凭证流水sid（台帐流水号/财务账流水号）")
    private Long accountDocumentSid;

    @Excel(name = "财务凭证流水code（台帐流水号/财务账流水号）")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "财务凭证流水code（台帐流水号/财务账流水号）")
    private Long accountDocumentCode;

    @Excel(name = "财务凭证流水行sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "财务凭证流水行sid")
    private Long accountItemSid;

    @Excel(name = "财务凭证流水行code")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "财务凭证流水行code")
    private Long accountItemCode;

    @ApiModelProperty(value = "财务流水类型编码code")
    private String bookType;

    @ApiModelProperty(value = "财务流水来源类别编码code（数据字典的键值或配置档案的编码）")
    private String bookSourceCategory;

    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    @ApiModelProperty(value = "系统SID-供应商")
    private Long vendorSid;

    @ApiModelProperty(value = "系统SID-公司")
    private Long companySid;

    @ApiModelProperty(value = "系统SID-产品季")
    private Long productSeasonSid;

    @ApiModelProperty(value = "数据源系统（数据字典的键值）")
    private String dataSourceSys;

}
