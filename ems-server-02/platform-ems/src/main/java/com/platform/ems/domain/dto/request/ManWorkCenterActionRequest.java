package com.platform.ems.domain.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ManWorkCenterActionRequest {
    /** 工作中心id */
    @ApiModelProperty(value = "工作中心id")
    private List<Long> workCenterSids;
    /** 处理状态 */
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;
    /** 启用/停用 */
    @ApiModelProperty(value = "启用/停用")
    private String status;
}
