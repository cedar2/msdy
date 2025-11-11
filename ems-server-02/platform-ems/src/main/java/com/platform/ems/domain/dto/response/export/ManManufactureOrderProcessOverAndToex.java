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
 * 已逾期/即将到期生产订单工序导出
 */
@Data
@ApiModel
@Accessors(chain = true)
public class ManManufactureOrderProcessOverAndToex {

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "计划完成日期(工序)", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划完成日期")
    private Date planEndDate;

    @Excel(name = "商品编码(款号)")
    @ApiModelProperty(value = "商品编码")
    private String materialCode;

    @Excel(name = "商品名称")
    @ApiModelProperty(value = "物料（商品/服务）名称")
    private String materialName;

    @Excel(name = "工序")
    @ApiModelProperty(value = "工序名称")
    private String processName;

    @Excel(name = "完成状态(工序)", dictType = "s_end_status")
    @ApiModelProperty(value = "完成状态（工序）")
    private String endStatus;

    @Excel(name = "负责人")
    @ApiModelProperty(value = "责任人名称")
    private String directorName;

    @Excel(name = "待完成量(计划)", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "待完成量(计划)：计划产量-已完成量")
    private BigDecimal daiQuantity;

    @Excel(name = "待完成量(实裁)", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "待完成量(实裁)：实裁量 - 已完成量(工序)")
    private BigDecimal daiShicaiQuantity;

    @Excel(name = "已完成量(工序)", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "【报表中心】生产订单工序明细报表：已完成量(工序)")
    private BigDecimal totalCompleteQuantity;

    @Excel(name = "计划产量(工序)", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "产量（此工厂/此工作中心/班组/此道工序负责生产的产量）")
    private BigDecimal quantity;

    @Excel(name = "实裁量", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "实裁量")
    private BigDecimal isCaichuangQuantity;

    @Excel(name = "工厂(工序)")
    @ApiModelProperty(value = "工厂简称(工序)")
    private String plantShortName;

    @Excel(name = "班组")
    @ApiModelProperty(value = "工作中心/班组名称")
    private String workCenterName;

    @Excel(name = "排产批次号")
    @ApiModelProperty(value = "排产批次号")
    private Long paichanBatch;

    @Excel(name = "颜色")
    @ApiModelProperty(value = "sku1名称")
    private String skuName;

    @Excel(name = "生产订单号")
    @ApiModelProperty(value = "生产订单号")
    private String manufactureOrderCode;

}
