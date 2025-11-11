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
 * 特殊库存报表响应实体
 *
 * @author yangqz
 * @date 2021-7-12
 */
@Data
@ApiModel
@Accessors(chain = true)
public class InvInventorySpecialResponse implements Serializable {

    @Excel(name = "仓库")
    @ApiModelProperty(value = "仓库名称")
    private String storehouseName;

    @Excel(name = "库位")
    @ApiModelProperty(value = "库位名称")
    private String locationName;

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

    @Excel(name = "特殊库存类型")
    @ApiModelProperty(value = "特殊库存（数据字典的键值）")
    private String specialStockName;

    @Excel(name = "库存量")
    @ApiModelProperty(value = "非限制库存量（非限制使用的库存）")
    private BigDecimal unlimitedQuantity;

    @Excel(name = "预留库存量")
    @ApiModelProperty(value = "预留库存")
    private BigDecimal obligateQuantity;

    @Excel(name = "可用库存量")
    @ApiModelProperty(value = "可用库存")
    private BigDecimal ableQuantity;

    @Excel(name = "客户")
    @ApiModelProperty(value = "客户名称")
    private String customerName;

    @Excel(name = "供应商")
    @ApiModelProperty(value = "供应商名称")
    private String vendorName;


    @Excel(name = "物料类型")
    @ApiModelProperty(value ="物料类型")
    private String materialTypeName;

    @Excel(name = "仓库编码")
    @ApiModelProperty(value = "仓库编码")
    private String storehouseCode;

    @Excel(name = "库位编码")
    @ApiModelProperty(value = "库位编码")
    private String locationCode;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "首次更新日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "首次更新库存时间")
    private Date firstUpdateStockDate;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "最近更新日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "最近更新库存时间")
    private Date latestUpdateStockDate;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "最近盘点日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "最近盘点时间")
    private Date latestCountDate;


}
