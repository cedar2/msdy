package com.platform.ems.domain.dto.response.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 生产进度跟踪报表（商品） 按库导出
 *
 * @author chenkw
 * @date 2023-02-22
 */
@Data
@Accessors(chain = true)
@ApiModel
public class ManManuOrderProductTrackingKu {

    @ApiModelProperty(value = "警示灯 0 红灯，-1 不显示")
    private String light;

    @Excel(name = "计划完工日期", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划完工日期")
    private Date planEndDate;

    @ApiModelProperty(value = "计划完工日期开始")
    private String planEndDateBegin;

    @ApiModelProperty(value = "计划完工日期结束")
    private String planEndDateEnd;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-工厂信息")
    private Long plantSid;

    @ApiModelProperty(value = "系统自增长ID-工厂信息")
    private Long[] plantSidList;

    @ApiModelProperty(value = "工厂名称")
    private String plantName;

    @Excel(name = "工厂")
    @ApiModelProperty(value = "工厂名称")
    private String plantShortName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-商品&物料&服务")
    private Long materialSid;

    @Excel(name = "商品编码(款号)")
    @ApiModelProperty(value = "物料/商品编码")
    private String materialCode;

    @Excel(name = "商品名称")
    @ApiModelProperty(value = "物料/商品名称")
    private String materialName;

    @Excel(name = "待完工量", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "待完工量(商品) = 已完工量 - 计划产量")
    private BigDecimal daiQuantity;

    @Excel(name = "已完工量", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "生产订单商品明细报表：已完工量(商品)")
    private BigDecimal completeSpQuantity;

    @Excel(name = "计划产量", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "计划产量/本次排产量")
    private BigDecimal quantity;

}
