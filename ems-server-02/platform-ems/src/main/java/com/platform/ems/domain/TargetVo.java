package com.platform.ems.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author :wance
 * @date : 2024/1/31 13:56
 */
@Data
public class TargetVo implements Serializable {

    @ApiModelProperty(value = "未完成数")
    private Integer unfinishedNum;

    @ApiModelProperty(value = "即将到期数")
    private Integer aboutToExpireNum;

    @ApiModelProperty(value = "已逾期数")
    private Integer overdueNum;

}
