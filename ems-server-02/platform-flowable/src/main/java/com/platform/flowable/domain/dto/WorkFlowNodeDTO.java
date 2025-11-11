package com.platform.flowable.domain.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel
public class WorkFlowNodeDTO {

    private String nodeId;

    private String nodeName;
}
