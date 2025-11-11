package com.platform.ems.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 生产日进度报表 查看详情 行转列 ManDayProgressDetailTableQuantity
 * 每行对应某个日期的 完成量
 *
 * @author chenkaiwen
 * @date 2022-11-07
 */
@Data
@Accessors(chain = true)
@ApiModel
public class ManDayProgressDetailTableQuantity {

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @ApiModelProperty(value = "所属月日")
    private String monthday;

    @ApiModelProperty(value = "完成量")
    private BigDecimal quantity;

    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;
}
