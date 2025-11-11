package com.platform.ems.domain.dto.request;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 生产产品缺陷登记对象-弹出框设置值请求实体
 *
 */
@Data
@ApiModel
public class ManProductDefectRequest {

    @ApiModelProperty(value = "type:传JJZT 解决状态, 传JJRQ 计划解决日期 ,传FZJJR 负责解决人,传 JJD 紧急度 ")
    private String type;

    @ApiModelProperty(value = "解决状态")
    private String resolveStatus;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "计划解决日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划解决日期")
    private Date planResolveDate;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "负责解决人sid（员工档案sid）")
    private Long defectResolverSid;

    @ApiModelProperty(value = "紧急度/优先级")
    private String defectPriority;

    @ApiModelProperty(value = "所选择的sid")
    @TableField(exist = false)
    private Long[] productDefectSidList;
}
