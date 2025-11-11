package com.platform.ems.domain.dto.request;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 生产关注事组明细报表-请求实体
 *
 */
@Data
@Accessors(chain = true)
@ApiModel
public class ManProduceConcernTaskGroupRequest {

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

    @Excel(name = "工厂")
    @ApiModelProperty(value = "工厂")
    private String[] plantSidList;

    @TableField(exist = false)
    @Excel(name = "处理状态")
    private List<String> handleStatusList;

    @ApiModelProperty(value = "启停")
    private String status;

    @ApiModelProperty(value = "创建人")
    private String[] creatorAccountList;

    @ApiModelProperty(value ="创建日期开始时间")
    @TableField(exist = false)
    private String beginTime;

    @ApiModelProperty(value ="创建日期结束时间")
    @TableField(exist = false)
    private String endTime;

    @ApiModelProperty(value ="每页个数")
    @TableField(exist = false)
    private Integer pageNum;

    @ApiModelProperty(value ="每页个数")
    @TableField(exist = false)
    private Integer pageSize;

    @ApiModelProperty(value ="分页起始页")
    @TableField(exist = false)
    private Integer pageBegin;
}
