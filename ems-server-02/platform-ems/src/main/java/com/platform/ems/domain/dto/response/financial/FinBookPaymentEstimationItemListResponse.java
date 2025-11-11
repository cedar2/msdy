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
 * 应付暂估明细列表查询返回 FinBookPaymentEstimationItemListResponse
 *
 * @author chenkaiwen
 * @date 2021-07-12
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FinBookPaymentEstimationItemListResponse extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "财务流水号")
    private Long bookPaymentEstimationSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "财务流水号")
    private Long bookPaymentEstimationItemSid;

    @ApiModelProperty(value = "财务流水号")
    private Long bookPaymentEstimationCode;

    @ApiModelProperty(value = "财务流水类型")
    private String bookType;

    @ApiModelProperty(value = "财务流水来源类别")
    private String bookSourceCategory;

    @ApiModelProperty(value = "财务流水类型")
    private String bookTypeName;

    @ApiModelProperty(value = "财务流水来源类别")
    private String bookSourceCategoryName;

    @ApiModelProperty(value = "供应商")
    private String vendorName;

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

    @ApiModelProperty(value = "核销状态")
    private String clearStatus;

    @ApiModelProperty(value = "核销状态（含税金额）")
    private String clearStatusMoney;

    @ApiModelProperty(value = "核销状态（数量）")
    private String clearStatusQuantity;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购合同")
    private Long purchaseContractSid;

    @ApiModelProperty(value = "采购合同号")
    private String purchaseContractCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购订单")
    private Long purchaseOrderSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购订单号")
    private Long purchaseOrderCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购发货单")
    private Long deliveryNoteSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购发货单号")
    private Long deliveryNoteCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "物料/商品/服务编码")
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
    @ApiModelProperty(value = "系统SID-物料&商品sku2")
    private Long sku2Sid;

    @ApiModelProperty(value = "系统SID-物料&商品sku2name")
    private String sku2Name;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品条码（物料&商品&服务）")
    private Long barcodeSid;

    @ApiModelProperty(value = "采购价（含税）")
    private BigDecimal priceTax;

    @ApiModelProperty(value = "采购价（不含税）")
    private BigDecimal price;

    @ApiModelProperty(value = "当前采购价（含税）")
    private BigDecimal currentPriceTax;

    @ApiModelProperty(value = "当前采购价（不含税）")
    private BigDecimal currentPrice;

    @ApiModelProperty(value = "采购计量单位")
    private String unitPrice;

    @ApiModelProperty(value = "采购计量单位")
    private String unitPriceName;

    @ApiModelProperty(value = "税率（存值，即：不含百分号，如20%，就存0.2）")
    private BigDecimal taxRate;

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
