package com.platform.workflow.response;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PorcessDefinitionResponse implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty("流程定义ID")
    private String processDefinitionId;

    @ApiModelProperty("流程定义名称")
    private String processDefinitionName;

    @ApiModelProperty("部署ID")
    private String deployId;

}
