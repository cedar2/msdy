package com.platform.workflow.response;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

/**
 * 流程实例
 * @author qhq
 *
 */
@Getter
@Setter
@ApiModel("流程实例")
public class ProcessInstanceResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7665455010606107931L;
	
	private String processInstanceId;
	
	private String name;
	
	private String businessKey;
	
	private String processDefinitionId;
	
	private String startUserId;

}
