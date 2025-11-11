package com.platform.ems.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 报表中心生产管理生产月进度每一行的日期 ManDayProgressMonthFormDay
 *
 * @author chenkaiwen
 * @date 2022-08-24
 */
@Data
@Accessors(chain = true)
@ApiModel
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class ManDayProgressMonthFormDay {

    @JsonIgnore
    @ApiModelProperty(value = "系统SID-生产订单-工序")
    private Long manufactureOrderProcessSid;

    @JsonIgnore
    @ApiModelProperty(value = "系统SID-班组生产日报的班组")
    private Long workCenterSid;

    @JsonIgnore
    @ApiModelProperty(value = "系统SID-颜色")
    private Long sku1Sid;

    @JsonIgnore
    @ApiModelProperty(value = "系统SID-生产订单-工序")
    private Long[] manufactureOrderProcessSidList;

    @JsonIgnore
    @ApiModelProperty(value = "所属年月")
    private String yearmonthday;

    @JsonIgnore
    @ApiModelProperty(value = "生产日进度报表——日期起")
    private String yearmonthdayBegin;

    @JsonIgnore
    @ApiModelProperty(value = "生产日进度报表——日期至")
    private String yearmonthdayEnd;

    @ApiModelProperty(value = "所属月日")
    private String monthday;

    @ApiModelProperty(value = "每日完成量")
    private BigDecimal dayCompleteQuantity;

    @JsonIgnore
    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;

    @JsonIgnore
    @ApiModelProperty(value ="数据权限过滤参数")
    private Map<String, Object> params;

    public Map<String, Object> getParams() {
        if (params == null) {
            params = new HashMap<>();
        }
        return params;
    }

}
