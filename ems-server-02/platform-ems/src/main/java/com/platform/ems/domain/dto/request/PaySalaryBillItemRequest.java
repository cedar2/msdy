package com.platform.ems.domain.dto.request;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 工资明细报表
 *
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaySalaryBillItemRequest {
    @TableField(exist = false)
    private Long[] plantSidList;

    @TableField(exist = false)
    private Long[] companySidList;

    @ApiModelProperty(value = "员工姓名")
    private String staffName;

    @ApiModelProperty(value = "员工编号")
    private String staffCode;

    @TableField(exist = false)
    private String[] handleStatusList;

    @ApiModelProperty(value = "所属年月")
    private String yearmonth;

    @ApiModelProperty(value = "工资单号")
    private String salaryBillCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String[] creatorAccountList;

    @TableField(exist = false)
    @ApiModelProperty(value ="创建日期开始时间")
    private String beginTime;

    @TableField(exist = false)
    @ApiModelProperty(value ="创建日期结束时间")
    private String endTime;

    @TableField(exist = false)
    @ApiModelProperty(value ="每页个数")
    private Integer pageNum;

    @TableField(exist = false)
    @ApiModelProperty(value ="每页个数")
    private Integer pageSize;
}
