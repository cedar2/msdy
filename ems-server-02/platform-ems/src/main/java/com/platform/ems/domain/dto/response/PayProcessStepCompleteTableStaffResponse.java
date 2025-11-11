package com.platform.ems.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.ems.domain.PayProcessStepCompleteItem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 计薪量申报-按款显示的返回实体 - 每行的员工
 *
 * @author chenkw
 * @date 2022-07-28
 */
@Data
@Accessors(chain = true)
@ApiModel
public class PayProcessStepCompleteTableStaffResponse {

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "员工账号sid")
    private Long workerSid;

    @ApiModelProperty(value = "员工号")
    private String workerCode;

    @ApiModelProperty(value = "员工姓名")
    private String workerName;

    @ApiModelProperty(value = "每行的员工对应的商品道序")
    private List<PayProcessStepCompleteTableQuantityResponse> quantityList;

    @ApiModelProperty(value = "计薪量申报-明细对象")
    private List<PayProcessStepCompleteItem> payProcessStepCompleteItemList;

}
