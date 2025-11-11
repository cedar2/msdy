package com.platform.ems.domain.dto.response.export;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 已逾期/即将到期生产订单事项导出
 */
@Data
@ApiModel
@Accessors(chain = true)
public class ManManufactureOrderConcernOverAndToex {

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "计划完成日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划完成日期")
    private Date planEndDate;

    @Excel(name = "事项名称")
    @ApiModelProperty(value = "关注事项名称")
    private String concernTaskName;

    @Excel(name = "完成状态(事项)", dictType = "s_end_status")
    @ApiModelProperty(value = "完成状态")
    private String endStatus;

    @Excel(name = "负责人")
    @ApiModelProperty(value = "负责人姓名(员工档案)")
    private String handlerName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "计划开始日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划开始日期")
    private Date planStartDate;

    @Excel(name = "商品编码(款号)")
    @ApiModelProperty(value = "商品编码")
    private String materialCode;

    @Excel(name = "商品名称")
    @ApiModelProperty(value = "商品名称")
    private String materialName;

    @Excel(name = "工厂")
    @ApiModelProperty(value = "工厂(整单)(简称)")
    private String orderPlantShortName;

    @Excel(name = "排产批次号")
    @ApiModelProperty(value = "排产批次号")
    private String paichanBatch;

    @Excel(name = "颜色")
    @ApiModelProperty(value = "sku1名称")
    private String skuName;

    @Excel(name = "生产订单号")
    @ApiModelProperty(value = "生产订单号")
    private String manufactureOrderCode;


}

