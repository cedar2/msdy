package com.platform.ems.domain.dto.response;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * 生产关注事组明细报表-响应实体
 *
 */
@Data
@Accessors(chain = true)
@ApiModel
public class ManProduceConcernTaskGroupResponse {

    @Excel(name = "工厂")
    @TableField(exist = false)
    private String plantName;


    @Excel(name = "关注事项组编码")
    @ApiModelProperty(value = "生产关注事项组编码")
    private String concernTaskGroupCode;


    @Excel(name = "关注事项组名称")
    @ApiModelProperty(value = "关注事项组名称")
    private String concernTaskGroupName;


    @Excel(name = "关注事项编码")
    @ApiModelProperty(value = "关注事项编码")
    private Long concernTaskCode;

    @Excel(name = "关注事项名称")
    @ApiModelProperty(value = "关注事项名称")
    private String concernTaskName;

    @Excel(name = "所属生产阶段")
    @ApiModelProperty(value = "所属生产阶段")
    @TableField(exist = false)
    private String produceStageName;

    @TableField(exist = false)
    @Excel(name = "事项负责人")
    @ApiModelProperty(value = "事项负责人")
    private String handlerName;

    @Excel(name = "里程碑", dictType = "s_manufacture_milestone")
    @ApiModelProperty(value = "里程碑（数据字典的键值或配置档案的编码）")
    private String milestone;

    @Excel(name = "图片是否必传",dictType = "s_yesno_flag")
    @ApiModelProperty(value = "图片是否必传")
    private String isPictureUpload;

    @Excel(name = "附件是否必传",dictType = "s_yesno_flag")
    @ApiModelProperty(value = "附件是否必传")
    private String isAttachUpload;

    @Excel(name = "序号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "序号")
    private Long sort;


    @ApiModelProperty(value ="备注")
    private String remark;


    @Excel(name = "启用/停用（事项）", dictType = "s_valid_flag")
    @ApiModelProperty(value = "状态（数据字典的键值或配置档案的编码）")
    private String status;


    @Excel(name = "处理状态（事项组）", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人昵称")
    @TableField(exist = false)
    private String creatorAccountName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;


}
