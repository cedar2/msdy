package com.platform.ems.domain.dto.response;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
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
 * 在途库存报表响应实体
 *
 * @author yangqz
 * @date 2021-7-12
 */
@Data
@ApiModel
@Accessors(chain = true)
public class InvIntransitReportResponse implements Serializable {

    @Excel(name = "物料/商品编码")
    @ApiModelProperty(value ="物料编码")
    private String materialCode;

    @Excel(name = "物料/商品名称")
    @ApiModelProperty(value ="物料名称")
    private String materialName;

    @Excel(name = "sku1名称")
    @ApiModelProperty(value ="sku1名称")
    private String sku1Name;

    @Excel(name = "sku2名称")
    @ApiModelProperty(value ="sku2名称")
    private String sku2Name;

    @Excel(name = "基本计量单位")
    @ApiModelProperty(value = "基本计量单位")
    private String unitBaseName;

    @Excel(name = "仓库")
    @ApiModelProperty(value = "仓库名称")
    private String storehouseName;

    @Excel(name = "库位")
    @ApiModelProperty(value = "库位名称")
    private String locationName;

    @Excel(name = "在途量")
    @ApiModelProperty(value = "在途量")
    private BigDecimal unlimitedQuantity;

    @Excel(name = "调拨单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "调拨单号")
    private Long inventoryTransferCode;

    /** 库存凭证号 */
    @Excel(name = "库存凭证号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "库存凭证号")
    private Long inventoryDocumentCode;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "出库时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "出库时间")
    private Date outStockDate;

    @Excel(name = "调拨单行号")
    @ApiModelProperty(value = "调拨单行号")
    private Long itemNum;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-库存凭证行号")
    @Excel(name = "库存凭证行号")
    private Long inventoryDocumentItemNum;
}
