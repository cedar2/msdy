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

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 发货单明细报表响应实体
 *
 * @author yangqz
 * @date 2021-7-12
 */
@Data
@ApiModel
@Accessors(chain = true)
public class InvIssueNoteReportResponse implements Serializable {

    @Excel(name = "出库状态",dictType = "s_in_out_store_status")
    private String inOutStockStatus;

    @ApiModelProperty(value = "系统SID-发货单号")
    @Excel(name = "发货单号")
    private String noteCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-发货单号")
    private Long noteSid;

    @Excel(name = "作业类型")
    @ApiModelProperty(value = "作业类型名称")
    private String movementTypeName;

    @Excel(name = "商品/物料编码")
    @ApiModelProperty(value = "商品编码")
    private String materialCode;

    @Excel(name = "商品/物料名称")
    @ApiModelProperty(value = "商品名称")
    private String materialName;

    @Excel(name = "sku1名称")
    @ApiModelProperty(value = "sku1名称")
    private String sku1Name;

    @Excel(name = "sku2名称")
    @ApiModelProperty(value = "sku2名称")
    private String sku2Name;

    /** 数量 */
    @Excel(name = "数量")
    @ApiModelProperty(value = "数量")
    private BigDecimal quantity;

    @Excel(name = "已出库量")
    @ApiModelProperty(value = "已出库量")
    private BigDecimal invQuantity;

    @Excel(name = "基本单位")
    @ApiModelProperty(value = "基本计量单位名称")
    private String unitBaseName;

    @Excel(name = "仓库")
    @ApiModelProperty(value = "仓库名称")
    private String storehouseName;

    @Excel(name = "库位")
    @ApiModelProperty(value = "库位名称")
    private String locationName;

    @Excel(name = "特殊库存")
    @ApiModelProperty(value = "特殊库存（数据字典的键值或配置档案的编码）")
    private String specialStockName;

    @Excel(name = "供应商")
    @ApiModelProperty(value = "供应商名称")
    private String vendorName;

    @Excel(name = "客户")
    @ApiModelProperty(value = "客户名称")
    private String customerName;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "开单日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "开单日期")
    private Date documentDate;

    @Excel(name = "商品条码")
    @ApiModelProperty(value = "系统SID-商品条码")
    private Long barcode;

    @ApiModelProperty(value = "款号名称")
    @TableField(exist = false)
    private String productName;

    @ApiModelProperty(value = "款号sid")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long productSid;

    @ApiModelProperty(value = "商品条码sid(适用于物料对应的商品)；只能保存单个")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long productBarcodeSid;

    @ApiModelProperty(value = "款号code")
    @Excel(name = "款号")
    private String productCode;

    @ApiModelProperty(value = "款颜色")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long productSku1Sid;

    @TableField(exist = false)
    @ApiModelProperty(value = "款颜色")
    @Excel(name = "款颜色")
    private String productSku1Name;

    @ApiModelProperty(value = "款颜色code")
    private String productSku1Code;

    @ApiModelProperty(value = "款尺码")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long productSku2Sid;

    @TableField(exist = false)
    @ApiModelProperty(value = "款尺码")
    @Excel(name = "款尺码")
    private String productSku2Name;

    @ApiModelProperty(value = "款数量")
    @Excel(name = "款数量")
    private BigDecimal productQuantity;

    @ApiModelProperty(value = "款尺码")
    private String productSku2Code;

    @ApiModelProperty(value = "款采购订单号")
    @Excel(name = "款采购订单号")
    private String purchaseOrderCode;

    @ApiModelProperty(value = "款备注")
    @Excel(name = "款备注")
    private String productCodes;

    @ApiModelProperty(value = "款颜色备注")
    @Excel(name = "款颜色备注")
    private String productSku1Names;

    @ApiModelProperty(value = "款尺码备注")
    @Excel(name = "款尺码备注")
    private String productSku2Names;

    @ApiModelProperty(value = "采购订单号备注")
    @Excel(name = "采购订单号备注")
    private String productPoCodes;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    @Excel(name = "处理状态",dictType ="s_handle_status" )
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @Excel(name = "行号")
    @ApiModelProperty(value = "行号")
    private Integer itemNum;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人")
    private String creatorAccountName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
   // @Excel(name = "出入库日期 ", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "出入库日期")
    private Date accountDate;

    @ApiModelProperty(value = "库存预留状态")
    private String reserveStatus;

    @ApiModelProperty(value = "供应商sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(exist = false)
    private Long vendorSid;

    @ApiModelProperty(value = "客户sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(exist = false)
    private Long customerSid;


    @ApiModelProperty(value = "特殊库存（数据字典的键值或配置档案的编码）")
    @TableField(exist = false)
    private String specialStock;

    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-发货单明细")
    private Long noteItemSid;


    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-仓库档案")
    private Long storehouseSid;


    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-库位")
    private Long storehouseLocationSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品条码")
    private Long barcodeSid;


    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品&服务")
    private Long materialSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品sku1")
    private Long sku1Sid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品sku2")
    private Long sku2Sid;
}
