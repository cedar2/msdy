package com.platform.ems.domain.dto.response.export;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.ems.domain.dto.response.form.PaySalaryWageFormResponse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * 计件工资明细报表打印
 * PaySalaryWageExportReport
 *
 * @author chenkaiwen
 * @date 2022-06-16
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaySalaryWageExportReport {

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "员工SID")
    private Long staffSid;

    @ApiModelProperty(value = "员工SID")
    private Long[] staffSidList;

    @Excel(name = "员工编码")
    @ApiModelProperty(value = "员工编码")
    private String staffCode;

    @Excel(name = "员工名称")
    @ApiModelProperty(value = "员工名称")
    private String staffName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "班组")
    private Long workCenterSid;

    @ApiModelProperty(value = "班组")
    private Long[] workCenterSidList;

    @Excel(name = "班组")
    @ApiModelProperty(value = "班组")
    private String workCenterName;

    @ApiModelProperty(value = "员工和班组的sid拼接")
    private String staffAndWorkSid;

    @ApiModelProperty(value = "总金额")
    private BigDecimal money;

    @ApiModelProperty(value = "数量")
    private BigDecimal quantity;

    @ApiModelProperty(value = "数量")
    private String quantityToString;

    @ApiModelProperty(value ="页数")
    private Integer pageNum;

    @ApiModelProperty(value ="每页个数")
    private Integer pageSize;

    @ApiModelProperty(value ="计件工资明细报表")
    private List<PaySalaryWageFormResponse> formItem;

    @ApiModelProperty(value = "数据源系统（数据字典的键值）")
    private String dataSourceSys;

}
