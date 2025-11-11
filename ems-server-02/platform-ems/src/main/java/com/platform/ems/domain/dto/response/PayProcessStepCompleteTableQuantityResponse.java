package com.platform.ems.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 计薪量申报-按款显示的返回实体 - 每行的员工 的 对应道序序号的计薪量
 *
 * @author chenkw
 * @date 2022-07-28
 */
@Data
@Accessors(chain = true)
@ApiModel
public class PayProcessStepCompleteTableQuantityResponse {

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "员工账号sid")
    private Long workerSid;

    @ApiModelProperty(value = "员工号")
    private String workerCode;

    @ApiModelProperty(value = "员工姓名")
    private String workerName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "道序sid")
    private Long processStepSid;

    @ApiModelProperty(value = "道序编码")
    private String processStepCode;

    @ApiModelProperty(value = "道序名称")
    private String processStepName;

    @ApiModelProperty(value = "序号")
    private BigDecimal sort;

    @ApiModelProperty(value = "当天计薪量")
    private BigDecimal completeQuantity;

}
