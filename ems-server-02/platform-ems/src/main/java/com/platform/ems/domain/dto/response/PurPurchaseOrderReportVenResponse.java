package com.platform.ems.domain.dto.response;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 供应商寄售结算导出响应实体
 *
 * @author yangqz
 * @date 2021-7-12
 */
@Data
@ApiModel
@Accessors(chain = true)
public class PurPurchaseOrderReportVenResponse {

    @Excel(name = "供应商寄售结算单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "供应商寄售结算单号")
    private Long purchaseOrderCode;

    @ApiModelProperty(value = "采购员名称")
    @Excel(name = "采购员名称")
    private String nickName;

    @ApiModelProperty(value = "供应商名称")
    @Excel(name = "供应商")
    private String vendorName;

    @Excel(name = "物料名称")
    @ApiModelProperty(value = "物料名称")
    private String materialName;

    @ApiModelProperty(value = "物料编码")
    @Excel(name = "物料编码")
    private String materialCode;

    @Excel(name = "sku1名称")
    @ApiModelProperty(value = "sku1名称")
    private String sku1Name;

    @Excel(name = "sku2名称")
    @ApiModelProperty(value = "sku2名称")
    private String sku2Name;

    @Excel(name = "交货状态",dictType = "s_delivery_status")
    @ApiModelProperty(value = "交货状态")
    private String deliveryStatus;

    @Excel(name = "结算量")
    @ApiModelProperty(value = "结算量")
    private BigDecimal quantity;

    @Excel(name = "基本计量单位")
    @ApiModelProperty(value = "计量单位名称")
    private String unitBaseName;

    @Excel(name = "采购单位")
    @ApiModelProperty(value = "采购单位")
    private String purchaseUnitName;

    @Excel(name = "单位换算比例")
    @ApiModelProperty(value = "单位换算比例")
    private BigDecimal unitConversionRate;

    @Excel(name = "免费")
    @ApiModelProperty(value = "免费")
    private String freeFlag;

    /**
     * 采购价(含税)
     */
    @Excel(name = "采购价(含税)")
    @ApiModelProperty(value = "采购价(含税)")
    private BigDecimal purchasePriceTax;

    /**
     * 采购价(不含税)
     */
    @Excel(name = "采购价(不含税)")
    @ApiModelProperty(value = "采购价(不含税)")
    private BigDecimal purchasePrice;

    /**
     * 采购价(含税)
     */
    @Excel(name = "金额(含税)")
    @ApiModelProperty(value = "金额(含税)")
    private BigDecimal priceTax;

    /**
     * 采购价(不含税)
     */
    @Excel(name = "金额(不含税)")
    @ApiModelProperty(value = "金额(不含税)")
    private BigDecimal price;

    @Excel(name = "税率")
    @ApiModelProperty(value = "税率")
    private BigDecimal taxRate;

    @ApiModelProperty(value = "仓库名称")
    @Excel(name = "仓库")
    private String storehouseName;

    @ApiModelProperty(value = "库位名称")
    @Excel(name = "库位")
    private String locationName;

    @Excel(name = "采购合同号")
    @ApiModelProperty(value = "采购合同号")
    private String purchaseContractCode;

    @Excel(name = "采购合同号(纸质合同)")
    @ApiModelProperty(value = "采购合同号(纸质合同)")
    private String paperPurchaseContractCode;

    @ApiModelProperty(value = "备注")
    @Excel(name = "备注")
    private String remark;

    @ApiModelProperty(value = "商品条码")
    @Excel(name = "商品条码")
    private String barcode;

    @Excel(name = "行号")
    @ApiModelProperty(value = "行号")
    private Integer itemNum;

    /** 创建人账号 */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccountName;

    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @Excel(name = "明细行状态",dictType ="s_order_item_status")
    @ApiModelProperty(value = "明细行状态")
    private String itemStatus;

}
