package com.platform.ems.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 员工完成量汇总的查看详情的员工行中的完成量返回实体
 *
 * @author chenkw
 * @date 2022-11-15
 */
@Data
@Accessors(chain = true)
@ApiModel
public class StaffCompleteSummaryTableQuantity {

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "商品道序sid(商品道序明细表中的sid)")
    private Long processStepItemSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "商品道序明细序号")
    private BigDecimal sort;

    @ApiModelProperty(value = "完成量(当天)")
    private BigDecimal completeQuantity;
}
