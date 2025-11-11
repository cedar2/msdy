package com.platform.ems.domain.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 工艺路线状态请求信息
 *
 * @author yangqize
 * @date 2021-03-08
 */
@Data
public class ManProcessRouteActionRequest {
    /** 工艺路线主sid */
    @ApiModelProperty(value = "工艺路线主sid")
    private List<Long> processRouteSids;
    /** 处理状态 */
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;
    /** 启用/停用 */
    @ApiModelProperty(value = "启用/停用")
    private String status;

}
