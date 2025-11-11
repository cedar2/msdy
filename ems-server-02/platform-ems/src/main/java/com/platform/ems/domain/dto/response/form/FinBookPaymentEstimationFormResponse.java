package com.platform.ems.domain.dto.response.form;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;
import com.platform.ems.util.data.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 应付暂估流水报表 FinBookPaymentEstimationFormResponse
 *
 * @author chenkaiwen
 * @date 2021-07-12
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FinBookPaymentEstimationFormResponse extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-流水账（应付暂估）")
    private Long bookPaymentEstimationSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "流水号（应付暂估）")
    private Long bookPaymentEstimationCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-流水账（应付暂估）")
    private Long bookPaymentEstimationItemSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "库存凭证号明细sid")
    private Long inventoryDocumentItemSid;

    @ApiModelProperty(value = "财务流水类型")
    private String bookTypeName;

    @ApiModelProperty(value = "财务流水来源类别")
    private String bookSourceCategoryName;

    @ApiModelProperty(value = "处理状态（数据字典的键值）")
    private String handleStatus;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "库存凭证号")
    private Long referDocCode;

    @ApiModelProperty(value = "物料商品编码")
    private String materialCode;

    @ApiModelProperty(value = "物料商品名称")
    private String materialName;

    @ApiModelProperty(value = "SKU1名称")
    private String sku1Name;

    @ApiModelProperty(value = "SKU2名称")
    private String sku2Name;

    @ApiModelProperty(value = "采购计量单位")
    private String unitPriceName;

    @ApiModelProperty(value = "出入库量")
    private BigDecimal quantity;

    @ApiModelProperty(value = "订单量")
    private BigDecimal orderQuantity;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "合同交期")
    private Date contractDate;

    @JsonSerialize(using = KeepFiveDecimalsSerialize.class)
    @ApiModelProperty(value = "采购价（含税）")
    private BigDecimal priceTax;

    @JsonSerialize(using = KeepSixDecimalsSerialize.class)
    @ApiModelProperty(value = "采购价（不含税）")
    private BigDecimal price;

    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "出入库金额（含税）")
    private BigDecimal currencyAmountTax;

    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "已核销金额（含税）")
    private BigDecimal currencyAmountTaxYhx;

    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "待核销金额（含税）")
    private BigDecimal currencyAmountTaxDhx;

    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
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

    @ApiModelProperty(value = "已核销金额（含税）付款")
    @TableField(exist = false)
    private BigDecimal currencyAmountTaxFkYhx;

    @ApiModelProperty(value = "核销中金额（含税）付款")
    @TableField(exist = false)
    private BigDecimal currencyAmountTaxFkHxz;

    @ApiModelProperty(value = "已核销金额（含税）开票")
    @TableField(exist = false)
    private BigDecimal currencyAmountTaxKpYhx;

    @ApiModelProperty(value = "核销中金额（含税）开票")
    @TableField(exist = false)
    private BigDecimal currencyAmountTaxKpHxz;

    @ApiModelProperty(value = "税率")
    private String taxRateName;

    @ApiModelProperty(value = "核销状态")
    private String clearStatus;

    @ApiModelProperty(value = "采购订单号")
    private String purchaseOrderCode;

    @ApiModelProperty(value = "采购交货单号")
    private String deliveryNoteCode;

    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @ApiModelProperty(value = "公司简称")
    private String companyShortName;

    @ApiModelProperty(value = "供应商名称")
    private String vendorName;

    @ApiModelProperty(value = "供应商简称")
    @TableField(exist = false)
    private String vendorShortName;

    @ApiModelProperty(value = "产品季名称")
    private String productSeasonName;

    @ApiModelProperty(value = "出入库人")
    private String storehouseOperatorName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "出入库日期")
    private Date accountDate;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建日期")
    private Date createDate;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "库存凭证号")
    private Long inventoryDocumentCode;

    @ApiModelProperty(value = "是否已业务对账（数据字典的键值或配置档案的编码）")
    private String isBusinessVerify;

    @ApiModelProperty(value = "业务对账所属期间（所属年月）")
    private String businessVerifyPeriod;

    @ApiModelProperty(value = "是否已财务对账（数据字典的键值或配置档案的编码）")
    private String isFinanceVerify;

    @ApiModelProperty(value = "当前采购价（不含税）")
    private BigDecimal currentPrice;

    @ApiModelProperty(value = "当前采购价（含税）")
    private BigDecimal currentPriceTax;

    @ApiModelProperty(value = "采购合同号")
    private String purchaseContractCode;

    @ApiModelProperty(value = "采购模式")
    private String purchaseMode;

    @ApiModelProperty(value = "采购模式")
    private String materialTypeName;

}
