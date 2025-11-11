package com.platform.ems.domain.dto.response.export;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 生产周计划导出
 *
 * @author
 * @date
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ManWeekManufacturePlanExResponse {

    @Excel(name = "生产周计划编号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "生产周计划单号")
    private Long weekManufacturePlanCode;


    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "周计划日期(起)", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "周计划日期(起)")
    private Date dateStart;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "周计划日期(至)", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "周计划日期(至)")
    private Date dateEnd;

    @Excel(name = "工厂(工序)")
    @ApiModelProperty(value = "工厂名称")
    private String plantName;

    @TableField(exist = false)
    @Excel(name = "操作部门")
    @ApiModelProperty(value = "操作部门名称")
    private String departmentName;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;


    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人名称")
    private String creatorAccountName;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @Excel(name = "更改人")
    @ApiModelProperty(value = "更改人名称")
    private String updaterAccountName;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "更改日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    @Excel(name = "确认人")
    @ApiModelProperty(value = "确认人")
    private String confirmerAccountName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    @Excel(name = "确认日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date confirmDate;

}
