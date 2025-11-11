package com.platform.ems.workflow.domain;

import com.platform.flowable.domain.vo.FormParameter;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 变更传参
 * @Author qhq
 * @create 2021/12/15 14:54
 */
@Data
public class Change {

	@ApiModelProperty(value ="变更申请人ID")
	private String startUserId;

	@ApiModelProperty(value = "操作用户ID")
	private String userId;

	@ApiModelProperty(value ="单据类型")
	private String formType;

	@ApiModelProperty(value ="单据参数List")
	private List<FormParameter> parameterList;

	@ApiModelProperty(value ="批语")
	private String comment;

}
