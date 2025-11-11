package com.platform.ems.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * 报表中心生产管理生产月进度 ManDayProgressMonthForm
 *
 * @author chenkaiwen
 * @date 2022-08-24
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ManDayProgressMonthFormData {

    @ApiModelProperty(value = "日期列表")
    List<String> dayList;

    @ApiModelProperty(value = "表单数据")
    List<ManDayProgressMonthForm> formList;

    @ApiModelProperty(value = "完成量累计(区间前)累计")
    private BigDecimal totalCompleteQuantityBeforeSum;

    @ApiModelProperty(value = "完成量累计(区间前+区间内)累计")
    private BigDecimal totalCompleteQuantityAddSum;

    @ApiModelProperty(value = "完成量累计(区间内)累计")
    private BigDecimal totalCompleteQuantityInSum;

    @ApiModelProperty(value = "已完成总量累计")
    private BigDecimal totalCompleteQuantitySum;

    @ApiModelProperty(value = "当前计划完成总量累计")
    private BigDecimal currentTotalPlanQuantitySum;

    @ApiModelProperty(value = "未完成量累计")
    private BigDecimal weiCompleteQuantitySum;

    /**
     * 表单数据 的 总记录数
     */
    private long total;

}
