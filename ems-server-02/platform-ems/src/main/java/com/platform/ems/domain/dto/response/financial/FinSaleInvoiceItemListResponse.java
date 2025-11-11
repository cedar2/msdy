package com.platform.ems.domain.dto.response.financial;

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
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 *  销售发票明细接收实体 FinsaleInvoiceItemListResponse
 *
 * @author chenkaiwen
 * @date 2021-07-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FinSaleInvoiceItemListResponse extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-流水账明细（应付暂估）")
    private Long bookReceiptEstimationItemSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-销售发票明细")
    private Long saleInvoiceItemSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品&服务")
    private Long materialSid;

    @ApiModelProperty(value = "系统SID-物料&商品&服务")
    private String materialCode;

    @ApiModelProperty(value = "开票货物或服务名称")
    private String materialName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品&服务sku1sid")
    private Long sku1Sid;

    @ApiModelProperty(value = "开票货物或服务名称sku1name")
    private String sku1Name;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品&服务sku2sid")
    private Long sku2Sid;

    @ApiModelProperty(value = "开票货物或服务名称sku2name")
    private String sku2Name;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品&服务")
    private Long barcodeSid;

    @ApiModelProperty(value = "销售计量单位（数据字典的键值）")
    private String unitPrice;

    @ApiModelProperty(value = "销售计量单位（数据字典的键值）")
    private String unitPriceName;

    @ApiModelProperty(value = "待开票量")
    private BigDecimal quantityLeft;

    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "待开票金额(含税)")
    private BigDecimal currencyAmountTaxLeft;

    @ApiModelProperty(value = "数量")
    private BigDecimal quantity;

    @ApiModelProperty(value = "销售价(不含税)")
    private BigDecimal price;

    @ApiModelProperty(value = "销售价(含税)")
    private BigDecimal priceTax;

    @JsonSerialize(using = KeepFourDecimalsSerialize.class)
    @ApiModelProperty(value = "本次开票金额(不含税)")
    private BigDecimal currencyAmount;

    @JsonSerialize(using = KeepFourDecimalsSerialize.class)
    @ApiModelProperty(value = "本次开票金额(含税)")
    private BigDecimal currencyAmountTax;

    @JsonSerialize(using = KeepFourDecimalsSerialize.class)
    @ApiModelProperty(value = "税额")
    private BigDecimal taxAmount;

    @ApiModelProperty(value = "基本计量单位（数据字典的键值）")
    private String unitBase;

    @ApiModelProperty(value = "基本计量单位")
    private String unitBaseName;

    @ApiModelProperty(value = "税率（存值，即：不含百分号，如20%，就存0.2）")
    private BigDecimal taxRate;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售合同号/协议sid")
    private Long saleContractSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售合同号")
    private String saleContractCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售订单sid")
    private Long salesOrderSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售订单号")
    private Long salesOrderCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-交货单sid")
    private Long deliveryNoteSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-交货单号")
    private Long deliveryNoteCode;

    @ApiModelProperty(value = "行号")
    private Long itemNum;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "财务凭证流水sid（台帐流水号/财务账流水号）")
    private Long accountDocumentSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "财务凭证流水明细sid（台帐流水号/财务账流水号）")
    private Long accountItemSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "财务凭证流水code（台帐流水号/财务账流水号）")
    private Long accountDocumentCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "财务凭证流水code（台帐流水号/财务账流水号）")
    private Long bookReceiptEstimationCode;

    @ApiModelProperty(value = "财务流水类型编码code")
    private String bookType;

    @ApiModelProperty(value = "财务流水类型")
    private String bookTypeName;

    @ApiModelProperty(value = "财务流水来源类别编码code（数据字典的键值或配置档案的编码）")
    private String bookSourceCategory;

    @ApiModelProperty(value = "财务流水来源类别")
    private String bookSourceCategoryName;

    @ApiModelProperty(value = "当前销售价（含税）")
    private BigDecimal currentPriceTax;

    @ApiModelProperty(value = "当前销售价（不含税）")
    private BigDecimal currentPrice;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "收付款方式组合")
    private Long accountsMethodGroup;

    @ApiModelProperty(value = "收付款方式组合")
    private String accountsMethodGroupName;

    @ApiModelProperty(value = "是否已业务对账（数据字典的键值或配置档案的编码）")
    private String isBusinessVerify;

    @ApiModelProperty(value = "业务对账所属期间（所属年月）")
    private String businessVerifyPeriod;

    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    @ApiModelProperty(value = "创建人账号（用户昵称）")
    private String creatorAccountName;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @ApiModelProperty(value = "更新人账号")
    private String updaterAccount;

    @ApiModelProperty(value = "更新人账号（用户昵称）")
    private String updaterAccountName;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    @ApiModelProperty(value = "数据源系统（数据字典的键值）")
    private String dataSourceSys;

}
