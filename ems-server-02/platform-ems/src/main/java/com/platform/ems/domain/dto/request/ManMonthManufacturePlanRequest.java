package com.platform.ems.domain.dto.request;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.ems.domain.ManMonthManufacturePlanItem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 生产月计划-添加明细
 *
 * @author linhongwei
 * @date 2022-08-08
 */
@Data
@Accessors(chain = true)
@ApiModel
public class ManMonthManufacturePlanRequest {

    @ApiModelProperty(value = "所勾选的明细行")
    List<ManMonthManufacturePlanItem> itemList;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "工艺路线sid")
    private Long processRouteSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "关注事项组sid")
    private Long concernTaskGroupSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "班组sid")
    private Long workCenterSid;
}
