package com.platform.ems.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 员工完成量汇总的查看详情的返回实体
 *
 * @author chenkw
 * @date 2022-11-15
 */
@Data
@Accessors(chain = true)
@ApiModel
public class StaffCompleteSummaryTable {

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "工厂sid")
    private Long plantSid;

    @ApiModelProperty(value = "工厂编码")
    private String plantCode;

    @ApiModelProperty(value = "工厂名称")
    private String plantName;

    @ApiModelProperty(value = "工厂名称")
    private String plantShortName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "工作中心/班组sid")
    private Long workCenterSid;

    @ApiModelProperty(value = "工作中心/班组编码")
    private String workCenterCode;

    @ApiModelProperty(value = "工作中心/班组名称")
    private String workCenterName;

    @ApiModelProperty(value = "操作部门（数据字典的键值或配置档案的编码）")
    private String department;

    @ApiModelProperty(value = "操作部门（数据字典的键值或配置档案的编码）")
    private String departmentName;

    @ApiModelProperty(value = "完成日期起")
    private String completeDateBegin;

    @ApiModelProperty(value = "完成日期至")
    private String completeDateEnd;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "所选明细行的商品sid")
    private Long productSid;

    @ApiModelProperty(value = "所选明细行的商品编码")
    private String productCode;

    @ApiModelProperty(value = "所选明细行的商品名称")
    private String productName;

    @ApiModelProperty(value = "所选明细行的排产批次号")
    private Integer paichanBatch;

    @ApiModelProperty(value = "排产批次号是否精确查询")
    private String isPaichanPre;

    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    @ApiModelProperty(value = "道序序号行")
    private List<StaffCompleteSummaryTableProcess> processList;

    @ApiModelProperty(value = "员工行")
    private List<StaffCompleteSummaryTableStaff> staffList;


}
