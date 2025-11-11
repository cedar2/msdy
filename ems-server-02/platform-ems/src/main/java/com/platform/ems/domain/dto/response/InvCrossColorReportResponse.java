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
 * 串色串码明细报表响应实体
 *
 * @author yangqz
 * @date 2021-7-13
 */
@Data
@ApiModel
@Accessors(chain = true)
public class InvCrossColorReportResponse implements Serializable {
    @Excel(name = "库存凭证编号")
    @ApiModelProperty(value = "库存凭证编号")
    private String inventoryDocumentCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "库存调整sid")
    private String inventoryAdjustSid;

    @Excel(name = "作业类型")
    @ApiModelProperty(value = "作业类型")
    private String movementTypeName;

    @Excel(name = "特殊库存")
    @ApiModelProperty(value = "特殊库存")
    private String specialStockName;

    @Excel(name = "商品编码")
    @ApiModelProperty(value = "商品编码")
    private String materialCode;

    @Excel(name = "商品名称")
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

    @Excel(name = "被串商品/物料名称")
    @ApiModelProperty(value = "被串商品名称")
    private String destMaterialName;

    @ApiModelProperty(value = "被串SKU1名称")
    @TableField(exist = false)
    private String  destSku1Name;

    @ApiModelProperty(value = "被串SKU2名称")
    @TableField(exist = false)
    private String  destSku2Name;

    @Excel(name = "仓库名称")
    @ApiModelProperty(value = "仓库名称")
    private String storehouseName;

    @Excel(name = "库位名称")
    @ApiModelProperty(value = "库位名称")
    private String locationName;

    @Excel(name = "供应商名称")
    @ApiModelProperty(value = "供应商名称")
    private String vendorName;

    @Excel(name = "客户名称")
    @ApiModelProperty(value = "客户名称")
    private String customerName;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "开单日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "开单日期")
    private Date documentDate;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "过账日期 ", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "过账日期")
    private Date accountDate;

    @Excel(name = "基本计量单位名称")
    @ApiModelProperty(value = "基本计量单位名称")
    private String unitBaseName;

    @Excel(name = "系统SID-商品条码")
    @ApiModelProperty(value = "系统SID-商品条码")
    private Long barcode;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    @Excel(name = "行号")
    @ApiModelProperty(value = "行号")
    private String itemNum;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人")
    private String creatorAccountName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;
}
