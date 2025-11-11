package com.platform.flowable.response;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

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

	private String processDefinitionKey;

	private List<String> approvalNodes;

	private List<String> approvalIds;

	private String startUserId;

	private String status;

	private Date startTime;

	private Date endTime;

}
