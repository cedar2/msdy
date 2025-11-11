package com.platform.ems.workflow.domain;

import com.platform.flowable.domain.vo.FormParameter;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 变更审批/驳回参数
 * @Author qhq
 * @create 2021/12/16 15:21
 */
@Data
public class ChangeApproval {

	@ApiModelProperty(value = "用户ID")
	private String userId;

	@ApiModelProperty(value ="单据类型")
	private String formType;

	@ApiModelProperty(value ="单据参数List")
	private List<FormParameter> formParameters;

	@ApiModelProperty(value ="批语")
	private String comment;

}
