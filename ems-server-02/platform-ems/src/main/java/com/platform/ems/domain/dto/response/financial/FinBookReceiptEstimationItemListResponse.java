package com.platform.ems.domain.dto.response.financial;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.core.domain.EmsBaseEntity;
import com.platform.ems.util.data.KeepFourDecimalsSerialize;
import com.platform.ems.util.data.KeepThreeDecimalsSerialize;
import com.platform.ems.util.data.KeepTwoDecimalsSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 应收暂估明细列表查询返回 FinBookReceiptEstimationItemListResponse
 *
 * @author chenkaiwen
 * @date 2021-07-12
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FinBookReceiptEstimationItemListResponse extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "财务流水号")
    private Long bookReceiptEstimationSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "财务流水号")
    private Long bookReceiptEstimationItemSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "财务流水号")
    private Long bookReceiptEstimationCode;

    @ApiModelProperty(value = "财务流水类型")
    private String bookType;

    @ApiModelProperty(value = "财务流水类型")
    private String bookTypeName;

    @ApiModelProperty(value = "财务流水来源类别")
    private String bookSourceCategory;

    @ApiModelProperty(value = "财务流水来源类别")
    private String bookSourceCategoryName;

    @ApiModelProperty(value = "系统SID-客户")
    private String customerName;

    @ApiModelProperty(value = "公司")
    private String companyName;

    @ApiModelProperty(value = "出入库数量")
    private BigDecimal quantity;

    @ApiModelProperty(value = "待开票量")
    private BigDecimal quantityLeft;

    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "待开票金额(含税)")
    private BigDecimal currencyAmountTaxLeft;

    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "销售金额/应开票金额(含税)")
    private BigDecimal currencyAmountTax;

    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "已核销金额（含税）")
    private BigDecimal currencyAmountTaxYhx;

    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "核销中金额（含税）")
    private BigDecimal currencyAmountTaxHxz;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售合同")
    private Long saleContractSid;

    @ApiModelProperty(value = "销售合同号")
    private String saleContractCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售订单")
    private Long salesOrderSid;

    @ApiModelProperty(value = "销售订单号")
    private String salesOrderCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售发货单")
    private Long deliveryNoteSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售发货单号")
    private Long deliveryNoteCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "物料/商品/服务sid")
    private Long materialSid;

    @ApiModelProperty(value = "物料/商品/服务编码")
    private String materialCode;

    @ApiModelProperty(value = "物料/商品/服务名称")
    private String materialName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品sku1")
    private Long sku1Sid;

    @ApiModelProperty(value = "系统SID-物料&商品sku1name")
    private String sku1Name;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品sku1")
    private Long sku2Sid;

    @ApiModelProperty(value = "系统SID-物料&商品sku2name")
    private String sku2Name;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品条码（物料&商品&服务）")
    private Long barcodeSid;

    @JsonSerialize(using = KeepThreeDecimalsSerialize.class)
    @ApiModelProperty(value = "销售价（含税）")
    private BigDecimal priceTax;

    @JsonSerialize(using = KeepFourDecimalsSerialize.class)
    @ApiModelProperty(value = "销售价（不含税）")
    private BigDecimal price;

    @JsonSerialize(using = KeepThreeDecimalsSerialize.class)
    @ApiModelProperty(value = "当前销售价（含税）")
    private BigDecimal currentPriceTax;

    @JsonSerialize(using = KeepFourDecimalsSerialize.class)
    @ApiModelProperty(value = "当前销售价（不含税）")
    private BigDecimal currentPrice;

    @ApiModelProperty(value = "核销状态")
    private String clearStatus;

    @ApiModelProperty(value = "核销状态（含税金额）")
    private String clearStatusMoney;

    @ApiModelProperty(value = "核销状态（数量）")
    private String clearStatusQuantity;

    @ApiModelProperty(value = "税率（存值，即：不含百分号，如20%，就存0.2）")
    private BigDecimal taxRate;

    @ApiModelProperty(value = "销售价单位")
    private BigDecimal unitPrice;

    @ApiModelProperty(value = "销售价单位")
    private String unitPriceName;

    @ApiModelProperty(value = "基本计量单位")
    private String unitBase;

    @ApiModelProperty(value = "基本计量单位")
    private String unitBaseName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "收付款方式组合")
    private Long accountsMethodGroup;

    @ApiModelProperty(value = "收付款方式组合")
    private String accountsMethodGroupName;

    @ApiModelProperty(value = "是否已业务对账（数据字典的键值或配置档案的编码）")
    private String isBusinessVerify;

    @ApiModelProperty(value = "业务对账所属期间（所属年月）")
    private String businessVerifyPeriod;

    @ApiModelProperty(value = "是否已财务对账（数据字典的键值或配置档案的编码）")
    private String isFinanceVerify;

    @ApiModelProperty(value = "数据源系统（数据字典的键值）")
    private String dataSourceSys;

}
