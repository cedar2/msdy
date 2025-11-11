package com.platform.ems.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 员工完成量汇总的查看详情的商品道序序号行返回实体
 *
 * @author chenkw
 * @date 2022-11-15
 */
@Data
@Accessors(chain = true)
@ApiModel
public class StaffCompleteSummaryTableProcess {

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品道序明细信息")
    private Long processStepItemSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品道序")
    private Long productProcessStepSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "道序sid")
    private Long processStepSid;

    @ApiModelProperty(value = "道序编码")
    private String processStepCode;

    @ApiModelProperty(value = "道序名称")
    private String processStepName;

    @ApiModelProperty(value = "序号")
    private BigDecimal sort;

}
