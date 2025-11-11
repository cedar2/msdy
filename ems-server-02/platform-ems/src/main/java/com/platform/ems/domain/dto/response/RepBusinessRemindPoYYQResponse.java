package com.platform.ems.domain.dto.response;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
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
 * 已逾期/即将到期-采购订单对象
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_rep_business_remind_so")
public class RepBusinessRemindPoYYQResponse {


    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "合同交期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "合同交期")
    private Date contractDate;

    @Excel(name = "物料/商品编码")
    @ApiModelProperty(value = "物料/商品编码")
    private String materialCode;

    @ApiModelProperty(value = "商品名称")
    @TableField(exist = false)
    @Excel(name = "物料/商品名称")
    private String materialName;

    @ApiModelProperty(value = "颜色")
    @TableField(exist = false)
    @Excel(name = "SKU1名称")
    private String sku1Name;

    @ApiModelProperty(value = "尺码")
    @TableField(exist = false)
    @Excel(name = "SKU2名称")
    private String sku2Name;

    @Excel(name = "供应商")
    @ApiModelProperty(value = "供应商")
    private String vendorShortName;


    @Excel(name = "采购订单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购订单号")
    private Long purchaseOrderCode;

    /**
     * 订单量
     */
    @Excel(name = "订单量")
    @ApiModelProperty(value = "订单量")
    private BigDecimal quantityDingd;


    @ApiModelProperty(value = "待入库")
    @TableField(exist = false)
    @Excel(name = "待入库量")
    private BigDecimal sumQuantityDrk;

    @ApiModelProperty(value = "已入库")
    @TableField(exist = false)
    @Excel(name = "已入库量")
    private BigDecimal sumQuantityYrk;

    @ApiModelProperty(value = "下单季")
    @TableField(exist = false)
    @Excel(name = "下单季")
    private String productSeasonName;

    @ApiModelProperty(value = "单据类型")
    @Excel(name = "单据类型")
    private String documentTypeName;

    @ApiModelProperty(value = "业务类型")
    @Excel(name = "业务类型")
    private String businessTypeName;

    @Excel(name = "公司")
    @ApiModelProperty(value = "公司简称")
    private String companyName;

    @TableField(exist = false)
    @Excel(name = "采购合同号")
    @ApiModelProperty(value = "采购合同号")
    private String purchaseContractCode;

    @ApiModelProperty(value = "采购模式")
    @Excel(name = "采购模式",dictType = "s_price_type")
    private String purchaseMode;


    @ApiModelProperty(value = "行号")
    @TableField(exist = false)
    @Excel(name = "行号")
    private String itemNum;

    @ApiModelProperty(value = "即将到期提醒天数")
    @TableField(exist = false)
    private Long toexpireDays;

    @ApiModelProperty(value = "商品sku条码")
    @TableField(exist = false)
    @Excel(name = "商品sku条码")
    private String barcode;

    @ApiModelProperty(value = "物料类型")
    @Excel(name = "物料类型")
    private String materialTypeName;

    @ApiModelProperty(value = "采购员")
    @Excel(name = "采购员")
    private String buyerName;
}
