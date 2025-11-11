package com.platform.ems.domain.dto.response;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 物料采购订单明细导出响应实体
 *
 * @author yangqz
 * @date 2021-7-12
 */
@Data
@ApiModel
@Accessors(chain = true)
public class PurPurchaseOrderReportResponse {

    @Excel(name = "采购订单号")
    @ApiModelProperty(value = "采购订单号")
    private String purchaseOrderCode;

    @ApiModelProperty(value = "采购员名称")
    @Excel(name = "采购员")
    private String buyerName;

    @ApiModelProperty(value = "供应商名称")
    @Excel(name = "供应商")
    private String vendorName;

    @ApiModelProperty(value = "物料编码")
    @Excel(name = "商品/物料编码")
    private String materialCode;

    @Excel(name = "商品/物料名称")
    @ApiModelProperty(value = "物料名称")
    private String materialName;

    @Excel(name = "sku1名称")
    @ApiModelProperty(value = "sku1名称")
    private String sku1Name;

    @Excel(name = "sku2名称")
    @ApiModelProperty(value = "sku2名称")
    private String sku2Name;

    @Excel(name = "出入库状态", dictType = "s_in_out_store_status")
    @ApiModelProperty(value = "出入库状态")
    private String inOutStockStatus;

    @Excel(name = "采购量")
    @ApiModelProperty(value = "采购量")
    private BigDecimal quantity;

    @Excel(name = "待出库量/待入库量")
    @ApiModelProperty(value = "待出入库量")
    private BigDecimal partQuantity;

    @Excel(name = "已出库量/已入库量")
    @ApiModelProperty(value = "已出入库量")
    private BigDecimal inQuantity;

    @Excel(name = "已交货未入库量")
    @ApiModelProperty(value = "已发货未入库量")
    private BigDecimal inWQuantity;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "合同交期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "合同交期")
    private Date contractDate;

    @Excel(name = "基本计量单位")
    @ApiModelProperty(value = "计量单位名称")
    private String unitBaseName;

    @Excel(name = "采购价单位")
    @ApiModelProperty(value = "采购单位")
    private String unitPriceName;

    @Excel(name = "单位换算比例")
    @ApiModelProperty(value = "单位换算比例")
    private BigDecimal unitConversionRate;

    @Excel(name = "免费",dictType = "sys_yes_no")
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

    @ApiModelProperty(value = "款备注")
    @Excel(name = "款备注")
    private String productCodes;

    @Excel(name = "款合同交期", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = " 款合同交期")
    private Date productContractDate;

    @Excel(name = "款下单日期", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = " 款下单日期")
    private Date kuanXiadanDate;

    @ApiModelProperty(value = "仓库名称")
    @Excel(name = "仓库")
    private String storehouseName;

    @ApiModelProperty(value = "库位名称")
    @Excel(name = "库位")
    private String locationName;

    @Excel(name = "采购合同/协议号")
    @ApiModelProperty(value = "采购合同号")
    private String purchaseContractCode;

    @Excel(name = "下单季")
    @ApiModelProperty(value = "下单季")
    private String productSeasonName;

    @Excel(name = "单据类型")
    @ApiModelProperty(value = "单据类型名称")
    private String documentTypeName;

    @Excel(name = "业务类型")
    @ApiModelProperty(value = "业务类型名称")
    private String businessTypeName;

    @Excel(name = "公司")
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @Excel(name = "甲供料方式",dictType ="s_raw_material_mode" )
    @ApiModelProperty(value = "供料方式")
    private String rawMaterialMode;

    @Excel(name = "采购模式",dictType ="s_price_type" )
    @ApiModelProperty(value = "采购模式")
    private String purchaseMode;

    @TableField(exist = false)
    @Excel(name = "物料类型" )
    @ApiModelProperty(value = "物料类型")
    private String materialTypeName;

    @Excel(name = " 纸质下单合同号")
    @ApiModelProperty(value = "采购合同号(纸质合同)")
    private String paperPurchaseContractCode;

    @ApiModelProperty(value = "采购类型名称（默认）")
    private String purchaseTypeName;

    @Excel(name = "采购组")
    @ApiModelProperty(value = "采购组名称")
    private String purchaseGroupName;

    @Excel(name = "采购组织")
    @ApiModelProperty(value = "采购组织名称")
    private String purchaseOrgName;

    @Excel(name = "配送方式")
    @ApiModelProperty(value = "配送方式名称")
    private String shipmentTypeName;

    @ApiModelProperty(value = "备注")
    @Excel(name = "备注")
    private String remark;

    @ApiModelProperty(value = "商品条码")
    @Excel(name = "商品条码")
    private String barcode;

    @Excel(name = "行号")
    @ApiModelProperty(value = "行号")
    private Integer itemNum;

    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    /** 创建人账号 */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccountName;

    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @Excel(name = "明细行状态",dictType ="s_order_item_status")
    @ApiModelProperty(value = "明细行状态")
    private String itemStatus;

    @ApiModelProperty(value = "需购状态（面辅料）")
    @Excel(name = "需购状态（面辅料）", dictType = "s_material_require_status")
    private String yclXugouStatus;

    @ApiModelProperty(value = "备料状态（面辅料）")
    @Excel(name = "备料状态（面辅料）", dictType = "s_material_preparation_status")
    private String yclBeiliaoStatus;

    @ApiModelProperty(value = "下单状态（面辅料）")
    @Excel(name = "下单状态（面辅料）", dictType = "s_material_order_status")
    private String yclCaigouxiadanStatus;

    @ApiModelProperty(value = "齐套状态（面辅料）")
    @Excel(name = "齐套状态（面辅料）", dictType = "s_material_match_status")
    private String yclQitaoStatus;

    @ApiModelProperty(value = "面辅料齐套说明")
    @Excel(name = "面辅料齐套说明")
    private String yclQitaoRemark;

    @ApiModelProperty(value = "甲供料状态")
    @Excel(name = "甲供料状态", dictType = "s_raw_material_status")
    private String jglGongliaoStatus;

    @ApiModelProperty(value = "款颜色备注")
    @Excel(name = "款颜色备注")
    private String productSku1Names;

    @ApiModelProperty(value = "款尺码备注")
    @Excel(name = "款尺码备注")
    private String productSku2Names;

    @ApiModelProperty(value = " 款数量备注(适用于物料对应的商品)；可以保存多个")
    @Excel(name = "款数量备注")
    private String productQuantityRemark;

    @Excel(name = "款销售订单备注")
    private String productSoCodes;

    @Excel(name = "款需求方备注")
    @ApiModelProperty(value = " 商品需求方备注(适用于物料对应的商品的需求方)；可以保存多个；如：客户、供应商")
    private String productRequestPartys;

    @Excel(name = "款业务类型备注")
    @ApiModelProperty(value = " 商品业务类型备注(适用于物料对应的商品的业务类型)；可以保存多个")
    private String productRequestBusType;

    @Excel(name = "款采购订单号备注")
    private String productPoCodes;

    @Excel(name = "到期提醒天数")
    @ApiModelProperty(value = "即将到期提醒天数")
    private Long toexpireDays;
}
