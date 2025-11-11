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
 * 销售订单明细导出响应实体
 *
 * @author yangqz
 * @date 2021-7-12
 */
@Data
@ApiModel
@Accessors(chain = true)
public class SalSaleOrderReportResponse {

    @Excel(name = "销售订单号")
    @ApiModelProperty(value = "销售订单号")
    private String salesOrderCode;

    @Excel(name = "客户")
    @ApiModelProperty(value = "客户名称")
    private String customerName;

    @ApiModelProperty(value = "销售员")
    @Excel(name = "销售员")
    private String nickName;

    @ApiModelProperty(value = "物料编码")
    @Excel(name = "商品/物料编码")
    private String materialCode;

    @Excel(name = "商品/物料名称")
    @ApiModelProperty(value = "物料名称")
    private String materialName;

    @Excel(name = "SKU1（颜色）")
    @ApiModelProperty(value = "sku1名称")
    private String sku1Name;

    @Excel(name = "SKU2（尺码）")
    @ApiModelProperty(value = "sku2名称")
    private String sku2Name;

    @Excel(name = "订单量")
    @ApiModelProperty(value = "订单量")
    private BigDecimal quantity;

    @ApiModelProperty(value = "是否做首缸")
    @Excel(name = "是否做首缸", dictType = "sys_yes_no")
    private String isMakeShougang;

    @Excel(name = "是否首批", dictType = "sys_yes_no")
    @ApiModelProperty(value = "是否做首批")
    private String  isMakeShoupi;

    @Excel(name = "出入库状态", dictType = "s_in_out_store_status")
    @ApiModelProperty(value = "出入库状态")
    private String inOutStockStatus;

    @Excel(name = "待出库量/待入库量")
    @ApiModelProperty(value = "待出入库量")
    private BigDecimal partQuantity;

    @Excel(name = "已出库量/已入库量")
    @ApiModelProperty(value = "已出入库量")
    private BigDecimal inQuantity;

    @Excel(name = "已发货未出库量")
    @ApiModelProperty(value = "已发货出入库量")
    private BigDecimal inWQuantity;

    @ApiModelProperty(value = "可用库存量")
    @Excel(name = "可用库存量(基本单位)")
    private BigDecimal  InvQuantity;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "合同交期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "合同交期")
    private Date contractDate;

    /**
     * 需求日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "需求日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "需求日期")
    private Date demandDate;

    /**
     * 最晚需求日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "最晚需求日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "最晚需求日期")
    private Date latestDemandDate;

    @Excel(name = "基本计量单位")
    @ApiModelProperty(value = "计量单位名称")
    private String unitBaseName;

    @Excel(name = "销售价单位")
    @ApiModelProperty(value = "销售单位")
    private String unitPriceName;

    @Excel(name = "单位换算比例")
    @ApiModelProperty(value = "单位换算比例")
    private BigDecimal unitConversionRate;

    @TableField(exist = false)
    @Excel(name = "折扣")
    @ApiModelProperty(value = "折扣")
    private String discountTypeName;

    @Excel(name = "免费",dictType = "sys_yes_no")
    @ApiModelProperty(value = "免费")
    private String freeFlag;

    /**
     * 销售价(含税)
     */
    @Excel(name = "销售价(含税)")
    @ApiModelProperty(value = "销售价(含税)")
    private BigDecimal salePriceTax;

    /**
     * 销售价(不含税)
     */
    @Excel(name = "销售价(不含税)")
    @ApiModelProperty(value = "销售价(不含税)")
    private BigDecimal salePrice;


    @Excel(name = "金额(含税)")
    @ApiModelProperty(value = "金额(含税)")
    private BigDecimal priceTax;


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

    @Excel(name = "销售合同/协议号")
    @ApiModelProperty(value = "合同名称")
    private String saleContractCode;

    @Excel(name = "下单季")
    @ApiModelProperty(value = "下单季名称")
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

    @Excel(name = "客供料方式",dictType ="s_raw_material_mode" )
    @ApiModelProperty(value = "供料方式")
    private String rawMaterialMode;

    @Excel(name = "销售模式",dictType ="s_price_type" )
    @ApiModelProperty(value = "销售模式")
    private String saleMode;

    @TableField(exist = false)
    @ApiModelProperty(value = "业务渠道/销售渠道名称")
    @Excel(name = "销售渠道")
    private String businessChannelName;

    @Excel(name = "纸质下单合同号")
    @ApiModelProperty(value = "销售合同号(纸质合同)")
    private String paperSaleContractCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售组织名称")
    @Excel(name = "销售部门")
    private String saleOrgName;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售组名称")
    @Excel(name = "销售组")
    private String saleGroupName;

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

    @Excel(name = "物料类型")
    @ApiModelProperty(value = "物料类型名称")
    private String materialTypeName;

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

    @Excel(name = "辅料_采购下单状态",dictType ="s_material_order_status")
    @ApiModelProperty(value = "辅料_采购下单状态")
    private String flCaigouxiadanStatus;

    @Excel(name = "面料_采购下单状态",dictType ="s_material_order_status")
    @ApiModelProperty(value = "面料_采购下单状态")
    private String mlCaigouxiadanStatus;


//    @ApiModelProperty(value = "需购状态(原材料)")
//    @Excel(name = "需购状态(原材料)",dictType = "s_material_require_status")
//    private String yclXugouStatus;
//
//    @ApiModelProperty(value = "备料状态(原材料)")
//    @Excel(name = "备料状态（原材料）",dictType ="s_material_preparation_status" )
//    private String yclBeiliaoStatus;
//
//    @ApiModelProperty(value = "下单状态（原材料）")
//    @Excel(name = "下单状态（原材料）",dictType ="s_material_order_status" )
//    private String yclCaigouxiadanStatus;

    @ApiModelProperty(value = "齐套状态（原材料）")
    @Excel(name = "齐套状态",dictType ="s_material_match_status" )
    private String yclQitaoStatus;
//
//    @ApiModelProperty(value = "原材料齐套说明")
//    @Excel(name = "原材料齐套说明" )
//    private String yclQitaoRemark;
//
//    @ApiModelProperty(value = "申请状态（原材料）")
//    @Excel(name = "申请状态（原材料）",dictType ="s_material_apply_status" )
//    private String kglShenqingStatus;
//
//    @ApiModelProperty(value = "到料状态（原材料）")
//    @Excel(name = "到料状态（原材料）",dictType ="s_material_receipt_status" )
//    private String kglDaoliaoStatus;

    @Excel(name = "逾期状态")
    private String overDueStatus;

    @Excel(name = "到期提醒天数")
    private Long toexpireDays;

}
