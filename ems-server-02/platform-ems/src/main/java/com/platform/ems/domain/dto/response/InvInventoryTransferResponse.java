package com.platform.ems.domain.dto.response;

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
 * 调拨单明细报表响应实体
 *
 * @author yangqz
 * @date 2021-7-12
 */
@Data
@ApiModel
@Accessors(chain = true)
public class InvInventoryTransferResponse implements Serializable {

    @Excel(name = "出库状态",dictType = "s_in_out_store_status")
    @ApiModelProperty(value = "出入库状态")
    private String outStockStatus;

    @Excel(name = "入库状态",dictType = "s_in_out_store_status")
    @ApiModelProperty(value = "出入库状态")
    private String inStockStatus;

    @Excel(name = "调拨单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "调拨单号")
    private Long inventoryTransferCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "调拨单号")
    private Long inventoryTransferSid;

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

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-调拨单明细")
    private Long inventoryTransferItemSid;

    @Excel(name = "sku2名称")
    @ApiModelProperty(value = "sku2名称")
    private String sku2Name;

    @Excel(name = "调拨单量")
    @ApiModelProperty(value = "调拨单量")
    private BigDecimal quantity;

    /** 数量 */
    @Excel(name = "出库量")
    @ApiModelProperty(value = "出库量")
    private BigDecimal outQuantity;

    @Excel(name = "入库量")
    @ApiModelProperty(value = "入库量")
    private BigDecimal inQuantity;

    @Excel(name = "基本单位")
    @ApiModelProperty(value = "基本单位")
    private String unitBaseName;

    @Excel(name = "仓库")
    @ApiModelProperty(value = "仓库名称")
    private String storehouseName;

    @Excel(name = "库位")
    @ApiModelProperty(value = "库位名称")
    private String locationName;

    @Excel(name = "目的仓库")
    @ApiModelProperty(value = "目的仓库名称")
    private String destStorehouseName;

    @Excel(name = "目的库位")
    @ApiModelProperty(value = "目的库位名称")
    private String destLocationName;

    @Excel(name = "特殊库存")
    @ApiModelProperty(value = "特殊库存（数据字典的键值或配置档案的编码）")
    private String specialStockName;

    @Excel(name = "供应商")
    @ApiModelProperty(value = "供应商名称")
    private String vendorName;

    @Excel(name = "客户")
    @ApiModelProperty(value = "客户名称")
    private String customerName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "开单日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "开单日期")
    private Date documentDate;

    @Excel(name = "出库人")
    @ApiModelProperty(value = "出库人")
    private String outStorehouseOperatorName;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "出库日期 ", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "出库日期")
    private Date outAccountDate;

    @Excel(name = "入库人")
    @ApiModelProperty(value = "入库人")
    private String inStorehouseOperatorName;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "入库日期 ", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "入库日期")
    private Date inAccountDate;

    @Excel(name = "出库凭证编号")
    @ApiModelProperty(value = "出库凭证编号")
    private String inventoryDocumentCodeChk;

    @Excel(name = "入库凭证编号")
    @ApiModelProperty(value = "入库凭证编号")
    private String inventoryDocumentCodeRu;

    @Excel(name = "商品条码")
    @ApiModelProperty(value = "系统SID-商品条码")
    private Long barcode;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    @Excel(name = "处理状态",dictType ="s_handle_status" )
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "行号")
    @Excel(name = "行号")
    private Integer itemNum;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人")
    private String creatorAccountName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;


    @ApiModelProperty(value = "特殊库存（数据字典的键值或配置档案的编码）")
    @TableField(exist = false)
    private String specialStock;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "特殊库存供应商sid")
    @TableField(exist = false)
    private Long vendorSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "特殊库存客户sid")
    @TableField(exist = false)
    private Long customerSid;


    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-仓库档案")
    private Long storehouseSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-库位")
    private Long storehouseLocationSid;

    @Excel(name = "系统SID-商品条码（物料&商品&服务）")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品条码（物料&商品&服务）")
    private Long barcodeSid;

    @ApiModelProperty(value = "库存预留状态")
    private String reserveStatus;

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
