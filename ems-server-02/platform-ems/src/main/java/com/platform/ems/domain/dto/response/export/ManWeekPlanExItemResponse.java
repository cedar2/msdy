package com.platform.ems.domain.dto.response.export;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 生产周计划 明细报表导出
 *
 * @author
 * @date
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ManWeekPlanExItemResponse {

    @Excel(name = "周计划日期", width = 30)
    @ApiModelProperty(value = "周计划日期")
    private String dateWeek;

    @Excel(name = "工厂(工序)")
    @ApiModelProperty(value = "工厂名称")
    private String plantName;

    @Excel(name = "生产阶段")
    @ApiModelProperty(value = "生产阶段")
    private String produceStageName;

    @Excel(name = "班组")
    @TableField(exist = false)
    @ApiModelProperty(value = "班组名称")
    private String workCenterName;

    @Excel(name = "商品编码(款号)")
    @TableField(exist = false)
    @ApiModelProperty(value = "商品编码")
    private String materialCode;

    @Excel(name = "排产批次号")
    @ApiModelProperty(value = "排产批次号")
    private Long paichanBatch;
}
