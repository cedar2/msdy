package com.platform.ems.domain.dto.request.financial;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


/**
 *  销售发票详情数据接收实体 FinSaleInvoiceItemInfoRequest
 *
 * @author chenkaiwen
 * @date 2021-07-26
 */
@Data
@Accessors(chain = true)
@ApiModel
public class FinSaleInvoiceItemInfoRequest extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-流水账（应付暂估）")
    private Long bookReceiptEstimationSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-流水账（应付暂估）")
    private Long bookReceiptEstimationCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-流水账明细（应付暂估）")
    private Long bookReceiptEstimationItemSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品&服务")
    private Long materialSid;

    @ApiModelProperty(value = "开票货物或服务编码")
    private String materialCode;

    @ApiModelProperty(value = "开票货物或服务名称")
    private String materialName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品&服务SKU1sid")
    private Long sku1Sid;

    @ApiModelProperty(value = "开票货物或服务名称SKU1name")
    private String sku1Name;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品&服务SKU2sid")
    private Long sku2Sid;

    @ApiModelProperty(value = "开票货物或服务名称SKU2name")
    private String sku2Name;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品&服务")
    private Long barcodeSid;

    @NotNull(message = "明细的开票数量不能为空")
    @ApiModelProperty(value = "数量")
    private BigDecimal quantity;

    @ApiModelProperty(value = "待开票数量")
    private BigDecimal quantityLeft;

    @ApiModelProperty(value = "销售价(不含税)")
    private BigDecimal price;

    @ApiModelProperty(value = "销售价(含税)")
    private BigDecimal priceTax;

    @ApiModelProperty(value = "本次开票金额(不含税)")
    private BigDecimal currencyAmount;

    @ApiModelProperty(value = "本次开票金额(含税)")
    private BigDecimal currencyAmountTax;

    @ApiModelProperty(value = "税额")
    private BigDecimal taxAmount;

    @ApiModelProperty(value = "基本计量单位（数据字典的键值）")
    private String unitBase;

    @ApiModelProperty(value = "销售价单位（数据字典的键值）")
    private String unitPrice;

    @ApiModelProperty(value = "税率")
    private BigDecimal taxRate;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "收付款方式组合")
    private Long accountsMethodGroup;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售合同号/协议sid")
    private Long saleContractSid;

    @ApiModelProperty(value = "销售合同号/协议sid")
    private String saleContractCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售订单sid")
    private Long salesOrderSid;

    @ApiModelProperty(value = "销售订单sid")
    private Long salesOrderCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-交货单sid")
    private Long deliveryNoteSid;

    @ApiModelProperty(value = "系统SID-交货单sid")
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

    @ApiModelProperty(value = "财务流水类型编码code")
    private String bookType;

    @ApiModelProperty(value = "财务流水来源类别编码code（数据字典的键值或配置档案的编码）")
    private String bookSourceCategory;

    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @ApiModelProperty(value = "数据源系统（数据字典的键值）")
    private String dataSourceSys;

    @ApiModelProperty(value = "子表")
    List<FinSaleInvoiceItemChildRequest> children;

    @ApiModelProperty(value = "唯一键")
    private String key;

}
