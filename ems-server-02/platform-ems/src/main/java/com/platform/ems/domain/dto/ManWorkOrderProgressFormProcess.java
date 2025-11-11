package com.platform.ems.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 报表中心班组生产进度报表 ManWorkOrderProgressFormData
 *
 * @author chenkaiwen
 * @date 2022-08-25
 */
@Data
@Accessors(chain = true)
@ApiModel
public class ManWorkOrderProgressFormProcess {

    @JsonIgnore
    @ApiModelProperty(value = "系统自增长ID-生产订单-工序")
    private Long manufactureOrderProcessSid;

    @JsonIgnore
    @ApiModelProperty(value = "系统自增长ID-生产订单-工序(多选)")
    private Long[] manufactureOrderProcessSidList;

    @JsonIgnore
    @ApiModelProperty(value = "系统自增长ID-生产订单")
    private Long manufactureOrderSid;

    @JsonIgnore
    @ApiModelProperty(value = "系统自增长ID-生产订单")
    private Long[] manufactureOrderSidList;

    @JsonIgnore
    @ApiModelProperty(value = "系统自增长ID-工作中心/班组")
    private Long workCenterSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-工序")
    private Long processSid;

    @JsonIgnore
    @ApiModelProperty(value = "系统自增长ID-工序list")
    private Long[] processSidList;

    @JsonIgnore
    @ApiModelProperty(value = "工序编码")
    private String processCode;

    @ApiModelProperty(value = "工序名称")
    private String processName;

    @ApiModelProperty(value = "序号")
    private BigDecimal serialNum;

    @ApiModelProperty(value = "序号")
    private BigDecimal serialNumDecimal;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划完成日期")
    private Date planEndDate;

    @ApiModelProperty(value = "分配量")
    private BigDecimal quantity;

    @ApiModelProperty(value = "未完成量")
    private BigDecimal notCompleteQuantity;

    @ApiModelProperty(value = "已完成量")
    private BigDecimal totalCompleteQuantity;

    @ApiModelProperty(value = "图片路径")
    private String picturePath;

    @ApiModelProperty(value = "图片路径，可放多个链接，每个链接用”;“隔开")
    private String[] picturePathList;

    @ApiModelProperty(value = "视频路径")
    private String videoPath;

    @ApiModelProperty(value = "视频路径，可放多个链接，每个链接用”;“隔开")
    private String[] videoPathList;


}
