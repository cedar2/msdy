package com.platform.ems.domain.dto.request;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.platform.common.annotation.Excel;
import com.platform.ems.domain.ManWeekManufacturePlan;
import com.platform.ems.domain.ManWeekManufacturePlanItem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;
@Data
@ApiModel
public class ManWeekManufacturePlanRequest {

    @ApiModelProperty(value = "生产周计划明细对象")
    private List<ManWeekManufacturePlanItem> itemList;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "周计划日期(起)", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(exist = false)
    @ApiModelProperty(value = "周计划日期(起)")
    private Date dateStart;
}
