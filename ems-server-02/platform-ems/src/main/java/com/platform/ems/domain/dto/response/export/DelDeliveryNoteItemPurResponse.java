package com.platform.ems.domain.dto.response.export;

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
 * 销售发货明细报表 导出
 *
 * @author yangqz
 * @date 2022-6-29
 */
@Data
@ApiModel
@Accessors(chain = true)
public class DelDeliveryNoteItemPurResponse {

    @Excel(name = "采购交货单号")
    @ApiModelProperty(value = "采购交货单号")
    private String deliveryNoteCode;

    @ApiModelProperty(value = "供应商名称")
    private String vendorName;

    @Excel(name = "供应商")
    @ApiModelProperty(value = "供应商名称")
    private String vendorShortName;

    @Excel(name = "采购订单号")
    @ApiModelProperty(value = "采购订单号")
    private String purchaseOrderCode;

    @Excel(name = "采购员")
    private String buyerName;

    /**
     * 合同交期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "合同交期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "合同交期")
    private Date contractDate;

    @Excel(name = "物料/商品编码")
    @ApiModelProperty(value = "物料（商品/服务）编码")
    private String materialCode;

    @Excel(name = "物料/商品名称")
    @ApiModelProperty(value = "物料（商品/服务）名称")
    private String materialName;

    @Excel(name = "sku1名称")
    @ApiModelProperty(value = "sku1名称")
    private String sku1Name;

    @Excel(name = "sku2名称")
    @ApiModelProperty(value = "sku2名称")
    private String sku2Name;

    @Excel(name = "商品条码")
    @ApiModelProperty(value = "商品条码")
    private String barcode;

    @Excel(name = "预留状态",dictType ="s_reserve_status")
    @ApiModelProperty(value = "预留状态")
    private String reserveStatus;

    @ApiModelProperty(value ="出入库状态")
    @Excel(name = "出入库状态",dictType ="s_in_out_store_status")
    private String inOutStockStatus;

    @Excel(name = "交货量")
    @ApiModelProperty(value = "交货/发货量")
    private BigDecimal deliveryQuantity;

    @Excel(name = "入库量")
    @ApiModelProperty(value = "出入库量")
    private BigDecimal inOutStockQuantity;

    @Excel(name = "基本单位")
    @ApiModelProperty(value = "基本单位")
    private String unitBaseName;

    @Excel(name = "采购价单位")
    @ApiModelProperty(value = "采购价单位")
    private String unitPriceName;

    @Excel(name = "单位换算比例")
    @ApiModelProperty(value = "单位换算比例（价格单位/基本单位）")
    private BigDecimal unitConversionRate;

    @Excel(name = "免费",dictType = "sys_yes_no")
    @ApiModelProperty(value = "免费")
    private String freeFlag;

    @Excel(name = "采购价(含税)")
    @ApiModelProperty(value = "价格(含税)")
    private BigDecimal purchasePriceTax;

    @Excel(name = "入库金额(含税)",scale = 2)
    @ApiModelProperty(value = "出库金额")
    private BigDecimal accountPrice;

    @Excel(name = "税率")
    @ApiModelProperty(value = "税率（存值，即：不含百分号，如20%，就存0.2）")
    private BigDecimal taxRate;

    @Excel(name = "仓库")
    @ApiModelProperty(value = "仓库名称")
    private String storehouseName;

    @Excel(name = "库位")
    @ApiModelProperty(value = "库位名称")
    private String locationName;


    @Excel(name = "下单季")
    @ApiModelProperty(value = "下单季")
    private String productSeasonName;

    @Excel(name = "单据类型")
    @ApiModelProperty(value = "单据类型名称")
    private String documentTypeName;

    @Excel(name = "业务类型")
    @ApiModelProperty(value = "业务类型名称")
    private String businessTypeName;

    @Excel(name = "供方送货单号")
    @ApiModelProperty(value = "供方送货单号（供方交货单号）")
    private String supplierDeliveryCode;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "预发货日期")
    private Date expectedShipDate;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "预计到货日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "预计到货日期")
    private Date expectedArrivalDate;

    @Excel(name = "货运单号")
    @TableField(exist = false)
    @ApiModelProperty(value = "货运单号")
    private String carrierNoteCode;

    @Excel(name = "货运方")
    @ApiModelProperty(value ="货运方")
    private String carrierName;

    @Excel(name = "配送方式")
    @ApiModelProperty(value = "配送方式")
    private String shipmentTypeName;

    @Excel(name = "收货方")
    @ApiModelProperty(value = "收货方名称")
    private String receiverOrgName;

    @Excel(name = "采购组")
    @ApiModelProperty(value = "采购组名称")
    private String purchaseGroupName;

    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @Excel(name = "公司")
    @ApiModelProperty(value = "公司名称")
    private String companyShortName;

    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @Excel(name = "行号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "行号")
    private Long itemNum;

    @ApiModelProperty(value ="采购订单行号")
    @Excel(name = "采购订单行号")
    private String purchaseOrderItemNum;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccountName;

    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建日期")
    private Date createDate;
}
