package com.platform.ems.domain.dto.response;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 生产产品缺陷登记对象-导出响应实体
 *
 */
@Data
@Accessors(chain = true)
@ApiModel
public class ManProductDefectResponse {

    @Excel(name = "商品编码")
    @ApiModelProperty(value = "商品编码")
    private String productCode;

    @ApiModelProperty(value = "商品名称")
    @Excel(name = "商品名称")
    private String productName;

    @Excel(name = "排产批次号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "排产批次号")
    private Long paichanBatch;

    @Excel(name = "问题简述")
    @ApiModelProperty(value = "问题简述")
    private String defectShortDescription;

    @Excel(name = "解决状态",dictType = "s_resolve_status")
    @ApiModelProperty(value = "解决状态")
    private String resolveStatus;

    @Excel(name = "负责解决人")
    @TableField(exist = false)
    private String defectResolverName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "计划解决日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划解决日期")
    private Date planResolveDate;

    @Excel(name = "紧急度" , dictType = "s_urgency_type")
    @ApiModelProperty(value = "紧急度")
    private String defectPriority;

    @Excel(name = "提报人")
    @TableField(exist = false)
    private String defectReporterName;


    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "提报日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "提报日期")
    private Date defectReportDate;


    @Excel(name = "问题分类")
    @TableField(exist = false)
    private Long defectClassName;

    @Excel(name = "工厂")
    @TableField(exist = false)
    private String plantShortName;

    @Excel(name = "操作部门")
    @TableField(exist = false)
    private String departmentName;

    @TableField(exist = false)
    @Excel(name = "班组")
    private String workCenterName;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人昵称")
    @TableField(exist = false)
    private String creatorAccountName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @Excel(name = "更改人")
    @ApiModelProperty(value = "更改人昵称")
    @TableField(exist = false)
    private String updaterAccountName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更改日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "更改日期")
    private Date updateDate;


    @Excel(name = "确认人")
    @ApiModelProperty(value = "确认人昵称")
    @TableField(exist = false)
    private String confirmerAccountName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "确认日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "确认日期")
    private String confirmDate;

    /**
     * 生产订单号
     */
    @Excel(name = "生产订单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "生产订单号")
    private Long manufactureOrderCode;

    /**
     * 生产产品缺陷编号
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @Excel(name = "生产产品缺陷登记编号")
    @ApiModelProperty(value = "生产产品缺陷编号")
    private Long productDefectCode;
}
