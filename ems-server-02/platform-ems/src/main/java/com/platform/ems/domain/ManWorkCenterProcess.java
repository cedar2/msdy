package com.platform.ems.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 工作中心-工序对象 s_man_work_center_process
 *
 * @author linhongwei
 * @date 2021-03-26
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_man_work_center_process")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ManWorkCenterProcess extends EmsBaseEntity {

    /**
     * 客户端口号
     */
    @Excel(name = "客户端口号")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /**
     * 系统自增长ID-工作中心-工序
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-工作中心-工序")
    private Long workCenterProcessSid;

    /**
     * 系统自增长ID-工作中心
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @Excel(name = "系统自增长ID-工作中心")
    @ApiModelProperty(value = "系统自增长ID-工作中心")
    private Long workCenterSid;

    /**
     * 系统自增长ID-工序
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @Excel(name = "系统自增长ID-工序")
    @ApiModelProperty(value = "系统自增长ID-工序")
    private Long processSid;

    @Excel(name = "工序名称")
    @ApiModelProperty(value = "工序名称")
    @TableField(exist = false)
    private String processName;

    /**
     * 序列号
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @Excel(name = "序列号")
    @ApiModelProperty(value = "序列号")
    private Long sort;

    /**
     * 创建人账号
     */
    @Excel(name = "创建人账号")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /**
     * 更新人账号
     */
    @Excel(name = "更新人账号")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccount;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 数据源系统
     */
    @Excel(name = "数据源系统")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;

    @ApiModelProperty(value = "备注")
    private String remark;

    @TableField(exist = false)
    private Integer pageNum;

    @TableField(exist = false)
    private Integer pageSize;

}
