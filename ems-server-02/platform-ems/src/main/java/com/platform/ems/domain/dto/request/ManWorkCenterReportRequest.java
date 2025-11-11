package com.platform.ems.domain.dto.request;

import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author c
 */
@Data
@ApiModel
public class ManWorkCenterReportRequest extends EmsBaseEntity {

    @ApiModelProperty(value ="工作中心sid")
    private String workCenterSid;

    @ApiModelProperty(value ="工厂sid")
    private String plantSid;


}
