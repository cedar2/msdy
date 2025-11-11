package com.platform.ems.domain.dto.response;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 收货单明细报表响应实体
 *
 * @author yangqz
 * @date 2021-7-12
 */
@Data
@ApiModel
@Accessors(chain = true)
public class InvReceiptNoteReportResponse implements Serializable {

    @ApiModelProperty(value = "出入库状态")
    @Excel(name = "入库状态",dictType = "s_in_out_store_status")
    private String inOutStockStatus;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-收货单号")
    @Excel(name = "收货单号")
    private Long noteCode;

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

    @Excel(name = "数量")
    @ApiModelProperty(value = "数量")
    private BigDecimal quantity;

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

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    @Excel(name = "处理状态",dictType ="s_handle_status" )
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;


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

    @ApiModelProperty(value = "销售订单号")
    @Excel(name = "销售订单号")
    private Long salesOrderCode;

    @ApiModelProperty(value = "款备注")
    @Excel(name = "款备注")
    private String productCodes;

    @ApiModelProperty(value = "款颜色备注")
    @Excel(name = "款颜色备注")
    private String productSku1Names;

    @ApiModelProperty(value = "款尺码备注")
    @Excel(name = "款尺码备注")
    private String productSku2Names;

    @ApiModelProperty(value = "款销售订单备注")
    @Excel(name = "款销售订单备注")
    private String productSoCodes;

    @Excel(name = "行号")
    @ApiModelProperty(value = "行号")
    private String itemNum;

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

    @ApiModelProperty(value = "系统SID-收货单明细")
    private Long noteItemSid;

}
