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
 * 采购到货台账明细报表
 *
 * @author yangqz
 * @date 2022-6-30
 */
@Data
@ApiModel
@Accessors(chain = true)
public class InvRecordGoodsArrivalResponse {

    @Excel(name = "采购到货台账编号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购到货台账编号")
    private Long goodsArrivalCode;

    @ApiModelProperty(value = "供应商")
    @Excel(name = "供应商")
    private String vendorName;

    @ApiModelProperty(value = "查询：采购订单号")
    @Excel(name = "采购订单号")
    private String purchaseOrderCode;

    @ApiModelProperty(value = "物料编码")
    @Excel(name = "物料编码")
    private String materialCode;

    @ApiModelProperty(value = "物料名称")
    @Excel(name = "物料名称")
    private String materialName;

    @Excel(name = "sku1名称")
    @ApiModelProperty(value = "sku1名称")
    private String sku1Name;

    @Excel(name = "sku2名称")
    @ApiModelProperty(value = "sku2名称")
    private String sku2Name;

    @Excel(name = "到货量")
    @ApiModelProperty(value = "到货量")
    private BigDecimal arrivalQuantity;

    @ApiModelProperty(value = "采购价单位")
    @Excel(name = "采购价单位")
    private String unitPriceName;

    @ApiModelProperty(value = "基本计量单位")
    @Excel(name = "基本计量单位")
    private String unitBaseName;

    @Excel(name = "单位换算比例")
    @ApiModelProperty(value = "单位换算比例")
    private BigDecimal unitConversionRate;

    @Excel(name = "行号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "行号")
    private Integer itemNum;


    @Excel(name = "检测状态",dictType = "s_check_status")
    @ApiModelProperty(value = "检测状态（数据字典的键值或配置档案的编码）")
    private String checkStatus;

    @Excel(name = "检测单号")
    @ApiModelProperty(value = "检测单号")
    private String checkDocumentCode;

    @Excel(name = "检测结果",dictType = "s_detect_result")
    @ApiModelProperty(value = "检测结果（数据字典的键值或配置档案的编码）")
    private String checkResult;

    @Excel(name = "供应商退货的联系状态",dictType = "s_vendor_contact_status")
    @ApiModelProperty(value = "供应商退货的联系状态（数据字典的键值或配置档案的编码）")
    private String vendorContactStatus;


    @Excel(name = "供应商退货的取货状态",dictType = "s_vendor_pickup_status")
    @ApiModelProperty(value = "供应商退货的取货状态（数据字典的键值或配置档案的编码）")
    private String vendorPickupStatus;

    @Excel(name = "商品条码")
    @ApiModelProperty(value = "商品条码")
    private String barcode;

    @Excel(name = "采购员")
    @ApiModelProperty(value = "采购员")
    private String buyerName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "到货日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "到货日期")
    private Date arrivalDate;


    @Excel(name = "供方送货单号")
    @ApiModelProperty(value = "供方送货单号")
    private String supplierDeliveryCode;

    @Excel(name = "货运单号")
    @ApiModelProperty(value = "货运单号")
    private String carrierNoteCode;

    @Excel(name = "货运方")
    @ApiModelProperty(value = "货运方名称（承运商）")
    private String carrierName;

    @Excel(name = "公司")
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @Excel(name = "下单季")
    @ApiModelProperty(value = "下单季")
    private String productSeasonName;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人")
    private String creatorAccountName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建日期")
    private Date createDate;
}
