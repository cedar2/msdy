package com.platform.ems.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 报表中心生产进度报表 ManWorkOrderProgressFormConcern
 *
 * @author chenkaiwen
 * @date 2022-08-25
 */
@Data
@Accessors(chain = true)
@ApiModel
public class ManWorkOrderProgressFormConcern {

    @JsonIgnore
    @ApiModelProperty(value = "系统自增长ID-生产订单-关注事项")
    private Long manufactureOrderConcernTaskSid;

    @JsonIgnore
    @ApiModelProperty(value = "系统自增长ID-生产订单-关注事项(多选)")
    private Long[] manufactureOrderConcernTaskSidList;

    @JsonIgnore
    @ApiModelProperty(value = "系统自增长ID-生产订单")
    private Long manufactureOrderSid;

    @JsonIgnore
    @ApiModelProperty(value = "系统自增长ID-生产订单")
    private Long[] manufactureOrderSidList;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-关注事项")
    private Long concernTaskSid;

    @JsonIgnore
    @ApiModelProperty(value = "系统自增长ID-关注事项list")
    private Long[] concernTaskSidList;

    @JsonIgnore
    @ApiModelProperty(value = "事项编码")
    private String concernTaskCode;

    @ApiModelProperty(value = "事项名称")
    private String concernTaskName;

    @ApiModelProperty(value = "序号")
    private Long sort;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划完成日期")
    private Date planEndDate;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "实际完成日期")
    private Date actualEndDate;

    @ApiModelProperty(value = "完成状态")
    private String endStatus;

    @ApiModelProperty(value = "图片路径")
    private String picturePath;

    @ApiModelProperty(value = "图片路径，可放多个链接，每个链接用”;“隔开")
    private String[] picturePathList;

    @ApiModelProperty(value = "视频路径")
    private String videoPath;

    @ApiModelProperty(value = "视频路径，可放多个链接，每个链接用”;“隔开")
    private String[] videoPathList;

}
