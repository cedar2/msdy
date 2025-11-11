package com.platform.ems.domain.dto.response.form;

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
 * 应收暂估流水报表 FinBookReceiptEstimationFormResponse
 *
 * @author chenkaiwen
 * @date 2021-09-16
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FinBookReceiptEstimationFormResponse extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "流水号（应收暂估）")
    private Long bookPaymentEstimationCode;

    @ApiModelProperty(value = "财务流水类型")
    private String bookTypeName;

    @ApiModelProperty(value = "财务流水来源类别")
    private String bookSourceCategoryName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "库存凭证号")
    private Long referDocCode;

    @ApiModelProperty(value = "物料商品编码")
    private String materialCode;

    @ApiModelProperty(value = "物料商品名称")
    private String materialName;

    @ApiModelProperty(value = "计量单位")
    private String unitBaseName;

    @ApiModelProperty(value = "销售价单位")
    private String unitPriceName;

    @ApiModelProperty(value = "出入库量")
    private BigDecimal quantity;

    @ApiModelProperty(value = "销售价（含税）")
    private BigDecimal priceTax;

    @ApiModelProperty(value = "销售价（不含税）")
    private BigDecimal price;

    @ApiModelProperty(value = "出入库金额（含税）")
    private BigDecimal currencyAmountTax;

    @ApiModelProperty(value = "已核销金额（含税）")
    private BigDecimal currencyAmountTaxYhx;

    @ApiModelProperty(value = "待核销金额（含税）")
    private BigDecimal currencyAmountTaxDhx;

    @ApiModelProperty(value = "核销中金额（含税）")
    private BigDecimal currencyAmountTaxHxz;

    @ApiModelProperty(value = "核销状态（含税金额）")
    private String clearStatusMoney;

    @ApiModelProperty(value = "数量（出入库）已核销")
    private BigDecimal quantityYhx;

    @ApiModelProperty(value = "数量（出入库）核销中")
    private BigDecimal quantityHxz;

    @ApiModelProperty(value = "数量（出入库）待核销")
    private BigDecimal quantityDhx;

    @ApiModelProperty(value = "核销状态（数量）")
    private String clearStatusQuantity;

    @ApiModelProperty(value = "税率")
    private String taxRateName;

    @ApiModelProperty(value = "核销状态")
    private String clearStatus;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售订单号")
    private Long salesOrderCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售交货单号")
    private Long deliveryNoteCode;

    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @ApiModelProperty(value = "客户名称")
    private String customerName;

    @ApiModelProperty(value = "产品季名称")
    private String productSeasonName;

    @ApiModelProperty(value = "出入库人")
    private String storehouseOperatorName;

    @ApiModelProperty(value = "出入库日期")
    private Date accountDate;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "库存凭证号")
    private Long inventoryDocumentCode;

    @ApiModelProperty(value = "当前销售价（不含税）")
    private BigDecimal currentPrice;

    @ApiModelProperty(value = "当前销售价（含税）")
    private BigDecimal currentPriceTax;
}
