package com.platform.system.domain.dto;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>流程定义<p>
 *
 * @author c
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("流程定义")
public class FlowProcDefDto implements Serializable {

    @ApiModelProperty("流程id")
    private String id;

    @Excel(name = "流程分类")
    @ApiModelProperty("流程分类")
    private String category;

    @Excel(name = "流程名称")
    @ApiModelProperty("流程名称")
    private String name;

    @ApiModelProperty("流程key")
    private String key;

    @ApiModelProperty("部署ID")
    private String deploymentId;

    @Excel(name = "流程版本")
    @ApiModelProperty("版本")
    private int version;

    @Excel(name = "状态", readConverterExp = "1=激活,2=中止")
    @ApiModelProperty("流程定义状态: 1:激活 , 2:中止")
    private int suspensionState;

    @Excel(name = "部署时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("部署时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date deploymentTime;

    String tenant_id;
}
