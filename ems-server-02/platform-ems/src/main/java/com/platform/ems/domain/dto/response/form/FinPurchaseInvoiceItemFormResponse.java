package com.platform.ems.domain.dto.response.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.core.domain.EmsBaseEntity;
import com.platform.ems.util.data.KeepFourDecimalsSerialize;
import com.platform.ems.util.data.KeepTwoDecimalsSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 采购发票明细报表 FinPurchaseInvoiceItemFormResponse
 *
 * @author chenkaiwen
 * @date 2021-07-12
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FinPurchaseInvoiceItemFormResponse extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购发票号")
    private Long purchaseInvoiceCode;

    @ApiModelProperty(value = "供应商名称")
    private String vendorName;

    @ApiModelProperty(value = "发票类型")
    private String invoiceTypeName;

    @ApiModelProperty(value = "发票类别")
    private String invoiceCategoryName;

    @ApiModelProperty(value = "财务流水来源类别")
    private String bookSourceCategoryName;

    @ApiModelProperty(value = "物料类型（数据字典的键值）")
    private String materialTypeName;

    @ApiModelProperty(value = "物料&商品&服务编码")
    private String materialCode;

    @ApiModelProperty(value = "物料&商品&服务名称")
    private String materialName;

    @ApiModelProperty(value = "本次开票数量")
    private BigDecimal quantity;

    @JsonSerialize(using = KeepFourDecimalsSerialize.class)
    @ApiModelProperty(value = "价格(不含税)")
    private BigDecimal price;

    @JsonSerialize(using = KeepFourDecimalsSerialize.class)
    @ApiModelProperty(value = "价格(含税)")
    private BigDecimal priceTax;

    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "本次开票金额(含税)")
    private BigDecimal currencyAmountTax;

    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "税额")
    private BigDecimal taxAmount;

    @ApiModelProperty(value = "税率")
    private BigDecimal taxRate;

    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "本次抵折扣金额(含税)")
    private BigDecimal currencyAmountTaxDiscount;

    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @ApiModelProperty(value = "产品季名称")
    private String productSeasonName;

    @ApiModelProperty(value = "采购组织（数据字典的键值）")
    private String purchaseOrgName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "开票日期")
    private Date invoiceDate;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "总账日期")
    private Date generalLedgerDate;

    @ApiModelProperty(value = "纸质发票号")
    private String invoiceNum;

    @ApiModelProperty(value = "发票代码")
    private String invoiceCode;

    @ApiModelProperty(value = "币种（数据字典的键值）")
    private String currency;

    @ApiModelProperty(value = "货币单位（数据字典的键值）")
    private String currencyUnit;

    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccountName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @ApiModelProperty(value = "数据源系统（数据字典的键值）")
    private String dataSourceSys;

}
