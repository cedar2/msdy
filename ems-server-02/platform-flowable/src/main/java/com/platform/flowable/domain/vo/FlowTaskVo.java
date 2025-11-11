package com.platform.flowable.domain.vo;

import java.util.List;
import java.util.Map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>流程任务<p>
 *
 * @author c
 */
@Data
@ApiModel("工作流任务相关--请求参数")
public class FlowTaskVo {

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

	@ApiModelProperty("单据CODE")
	private String formCode;

	@ApiModelProperty("任务Id")
	private String taskId;

	@ApiModelProperty("流程定义Id")
	private String definitionId;

	@ApiModelProperty("在给定日期之后")
	private String startAfter;

	@ApiModelProperty("在给定日期之前")
	private String startedBefore;

    @ApiModelProperty("用户Id")
    private String userId;

    @ApiModelProperty("流程实例Id")
    private String instanceId;

    @ApiModelProperty("节点")
    private String targetKey;

    @ApiModelProperty("流程变量信息")
    private Map<String, Object> values;

    @ApiModelProperty("审批人")
    private List<String> assignee;

    @ApiModelProperty("候选人")
    private List<String> candidateUsers;

    @ApiModelProperty("审批组")
    private List<String> candidateGroups;

	@ApiModelProperty("单据IDList")
	private List<String> formIdList;

	@ApiModelProperty("ErpCode")
	private String erpCode;

	@ApiModelProperty("来源类别")
	private String dataObject;

	@ApiModelProperty("任务名称")
	private String taskName;

	private List<FormParameter> parameterList;

}
