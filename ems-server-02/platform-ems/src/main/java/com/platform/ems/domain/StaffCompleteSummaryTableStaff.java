package com.platform.ems.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 员工完成量汇总的查看详情的员工行返回实体
 *
 * @author chenkw
 * @date 2022-11-15
 */
@Data
@Accessors(chain = true)
@ApiModel
public class StaffCompleteSummaryTableStaff {

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "员工账号sid")
    private Long workerSid;

    @ApiModelProperty(value = "员工号")
    private String workerCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "员工")
    private String workerName;

    @ApiModelProperty(value = "商品道序明细对应的完成量")
    private List<StaffCompleteSummaryTableQuantity> quantityList;

}
