package com.platform.ems.domain.dto.response;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 生产月计划=生产关注事项
 *
 */
@Data
@Accessors(chain = true)
@ApiModel
public class ManProduceConcernTaskResponse {

    @ApiModelProperty(value = "生产关注事项名称")
    private String concernTaskName;

    @Excel(name = "进展状态", dictType = "s_end_status")
    @ApiModelProperty(value = "进展状态")
    private String endStatus;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "生产订单号")
    private Long manufactureOrderCode;
}
