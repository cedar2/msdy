package com.platform.ems.domain.dto.response.export;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 已逾期/即将到期生产订单导出
 */
@Data
@ApiModel
@Accessors(chain = true)
public class ManManufactureOrderOverAndToex {

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "计划完工日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划完工日期")
    private Date planEndDate;

    @Excel(name = "工厂")
    @ApiModelProperty(value = "工厂名称")
    private String plantShortName;

    @Excel(name = "跟进人")
    @ApiModelProperty(value = "跟进人")
    private String genjinrenName;

    @Excel(name = "商品编码(款号)")
    @ApiModelProperty(value = "商品编码")
    private String materialCode;

    @Excel(name = "商品名称")
    @ApiModelProperty(value = "物料（商品/服务）名称")
    private String materialName;

    @Excel(name = "排产批次号")
    @ApiModelProperty(value = "排产批次号")
    private String paichanBatch;

    @Excel(name = "颜色")
    @ApiModelProperty(value = "sku名称")
    private String skuName;

    @Excel(name = "待完工量", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "待完工量：计划产量-已完工量")
    private BigDecimal daiQuantity;

    @Excel(name = "已完工量", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "报表的已完工量")
    private BigDecimal totalCompleteQuantity;

    @Excel(name = "计划产量", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "计划产量")
    private BigDecimal quantity;

    @Excel(name = "实裁量", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "实裁量")
    private BigDecimal isCaichuangQuantity;

    @Excel(name = "生产订单号")
    @ApiModelProperty(value = "生产订单号")
    private String manufactureOrderCode;

}
