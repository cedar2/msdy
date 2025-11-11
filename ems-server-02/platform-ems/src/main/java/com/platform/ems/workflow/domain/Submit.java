package com.platform.ems.workflow.domain;

import com.platform.flowable.domain.vo.FormParameter;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 提交参数
 * @Author qhq
 * @create 2021/12/6 10:39
 */
@Data
public class Submit {

	@ApiModelProperty(value = "用户ID")
	private String userId;

	@ApiModelProperty(value ="申请人ID")
	private String startUserId;

	@ApiModelProperty(value ="单据类型")
	private String formType;

	@ApiModelProperty(value ="单据参数List")
	private List<FormParameter> formParameters;

	@ApiModelProperty(value ="批语")
	private String comment;

	@ApiModelProperty(value ="提交类型（用于报核议价，判断是报价提交或是议价提交")
	private String submitType;

	@ApiModelProperty(value ="核价提交人id")
	private String hjSubmitUser;

	@ApiModelProperty(value ="议价提交人id")
	private String yjSubmitUser;

	@ApiModelProperty(value ="议价审批人id")
	private String yjApprovalUser;

}
