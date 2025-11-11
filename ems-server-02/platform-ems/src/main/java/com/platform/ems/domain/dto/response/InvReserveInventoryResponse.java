package com.platform.ems.domain.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 预留库存对象明细报表
 *
 * @author yangqz
 * @date 2021-12-1
 */
@Data
@ApiModel
@Accessors(chain = true)
public class InvReserveInventoryResponse {

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-预留库存信息")
    private Long reserveStockSid;

    @Excel(name = "仓库")
    @ApiModelProperty(value = "仓库名称")
    private String storehouseName;

    @Excel(name = "库位")
    @ApiModelProperty(value = "库位名称")
    private String locationName;

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

    @Excel(name = "物料类型")
    @ApiModelProperty(value = "物料类型")
    private String materialTypeName;

    @Excel(name = "基本单位")
    @ApiModelProperty(value = "基本计量单位名称")
    private String unitBaseName;


    @Excel(name = "预留量")
    @ApiModelProperty(value = "预留库存量")
    private BigDecimal quantity;

    @Excel(name = "关联业务单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "关联业务单号")
    private Long businessOrderCode;

    @Excel(name = "关联业务单行号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "关联业务单行号")
    private Long businessOrderItemNum;

    @Excel(name = "商品条码")
    @ApiModelProperty(value = "商品条码")
    private String barcode;

    @Excel(name = "特殊库存")
    @ApiModelProperty(value = "特殊库存（数据字典的键值或配置档案的编码）")
    private String specialStockName;

    @Excel(name = "供应商")
    @ApiModelProperty(value = "供应商名称")
    private String vendorName;

    @Excel(name = "客户")
    @ApiModelProperty(value = "客户名称")
    private String customerName;

}
