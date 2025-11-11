package com.platform.flowable.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author qhq
 * @create 2021/12/7 17:28
 */
@Data
public class FormParameter {

    @ApiModelProperty(value ="单据ID")
    private String formId;

    @ApiModelProperty(value ="单据编码")
    private String formCode;

    @ApiModelProperty(value ="ERP编码")
    private String erpCode;

    @ApiModelProperty(value ="父级单据ID")
    private String parentId;

    Long userId;

    String processInstanceId;

    @ApiModelProperty(value = "是否走审批流程")
    private String isApproval;
}

