package com.platform.flowable.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 查询流程相关请求体
 * @author qhq
 */
@Data
@ApiModel("工作流流程相关--请求参数")
public class FlowProcessRequest {

		@ApiModelProperty("任务意见")
	    private String comment;

		@ApiModelProperty("1:同意 2:拒绝")
		private String result;

		@ApiModelProperty("审批类型:1为用户审批 2为无需审批")
	    private String type;

		@ApiModelProperty("一般传单据sid，bom传对应materialSid")
		private String businessKey;

		@ApiModelProperty("单据ID")
		private Long formId;

		@ApiModelProperty("单据类型")
		private String formType;

		@ApiModelProperty("是否为结束流程 true  false  all查所有")
		private String finished;

		@ApiModelProperty("操作用户ID")
		private String userId;

		private Integer pageNum;

		private Integer pageSize;
}
