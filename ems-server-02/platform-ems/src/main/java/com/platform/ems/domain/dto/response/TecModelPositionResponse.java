package com.platform.ems.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 版型部位档案对象 s_tec_model_position
 *
 * @author ChenPinzhen
 * @date 2021-01-25
 ")*/
@Data
@ApiModel
public class TecModelPositionResponse implements Serializable {

    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    @ApiModelProperty(value = "系统ID-版型部位档案")
    private String modelPositionSid;

    @ApiModelProperty(value = "版型部位编码")
    @Excel(name = "版型部位编码")
    private String modelPositionCode;

    @ApiModelProperty(value = "版型部位名称")
    @Excel(name = "版型部位名称")
    private String modelPositionName;

    @ApiModelProperty(value = "版型部位类型编码")
    @Excel(name = "版型部位类型编码")
    private String modelPositionType;

    @ApiModelProperty(value = "度量方法说明")
    @Excel(name = "度量方法说明")
    private String measureDescription;

    @ApiModelProperty(value = "图片路径")
    @Excel(name = "图片路径")
    private String picturePath;

    @ApiModelProperty(value = "启用/停用状态")
    @Excel(name = "启用/停用状态")
    private String status;

    @ApiModelProperty(value = "处理状态")
    @Excel(name = "处理状态")
    private String handleStatus;

    @ApiModelProperty(value = "备注")
    @Excel(name = "备注")
    private String remark;

    @ApiModelProperty(value = "创建人账号")
    @Excel(name = "创建人账号")
    private String creatorAccount;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date createDate;

    @ApiModelProperty(value = "更新人账号")
    @Excel(name = "更新人账号")
    private String updaterAccount;

    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date updateDate;

    @ApiModelProperty(value = "确认人账号")
    @Excel(name = "确认人账号")
    private String confirmerAccount;

    @ApiModelProperty(value = "确认时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "确认时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date confirmDate;

    @ApiModelProperty(value = "数据源系 ")
    @Excel(name = "数据源系统")
    private String dataSourceSys;
}
