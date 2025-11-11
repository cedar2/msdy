package com.platform.ems.plug.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableId;

import com.platform.common.core.domain.EmsBaseEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.NotBlank;

import lombok.experimental.Accessors;

/**
 * 生产操作部门对象 s_con_manufacture_department
 *
 * @author zhuangyz
 * @date 2022-07-25
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_con_manufacture_department")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConManufactureDepartment extends EmsBaseEntity {

    /**
     * 租户ID
     */
    //@Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-生产操作部门sid
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-生产操作部门sid")
    private Long sid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] sidList;
    /**
     * 生产操作部门编码
     */
    @Excel(name = "生产操作部门编码")
    @ApiModelProperty(value = "生产操作部门编码")
	@NotBlank(message = "生产操作部门编码不能为空")
    private String code;

    /**
     * 生产操作部门名称
     */
    @Excel(name = "生产操作部门名称")
    @ApiModelProperty(value = "生产操作部门名称")
	@NotBlank(message = "生产操作部门名称不能为空")
    private String name;

    @Excel(name = "类型(操作部门)",dictType = "s_man_department_type")
    @ApiModelProperty(value = "操作部门类型")
    private String departmentType;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "主管sid(员工档案)")
    private Long managerSid;

    @ApiModelProperty(value = "主管编码(员工档案)")
    private String managerCode;

    @TableField(exist = false)
    @Excel(name = "部门主管")
    @ApiModelProperty(value = "主管编码(员工档案)")
    private String managerName;

    @TableField(exist = false)
    @ApiModelProperty(value = "主管sid(员工档案)")
    private Long[] managerSidList;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 启用/停用状态（数据字典的键值或配置档案的编码）
     */
    @NotBlank(message = "确认状态不能为空")
    @Excel(name = "启用/停用", dictType = "s_valid_flag")
    @ApiModelProperty(value = "启用/停用状态（数据字典的键值或配置档案的编码）")
    private String status;

    /**
     * 序号
     */
    //@Excel(name = "序号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "序号")
    private Long sort;

    /**
     * 处理状态（数据字典的键值或配置档案的编码）
     */
    @NotBlank(message = "启停状态不能为空")
    //@Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "操作部门类型")
    private String[] departmentTypeList;

    /**
     * 创建人账号（用户账号）
     */
    //@Excel(name = "创建人账号（用户账号）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户账号）")
    private String creatorAccount;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人昵称")
    @TableField(exist = false)
    private String creatorAccountName;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /**
     * 更新人账号（用户账号）
     */
    //@Excel(name = "更新人账号（用户账号）")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户账号）")
    private String updaterAccount;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    //@Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 确认人账号（用户账号）
     */
    //@Excel(name = "确认人账号（用户账号）")
    @ApiModelProperty(value = "确认人账号（用户账号）")
    private String confirmerAccount;

    /**
     * 确认时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    //@Excel(name = "确认时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    //@Excel(name = "数据源系统")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    //@Excel(name = "更改人")
    @ApiModelProperty(value = "更改人昵称")
    @TableField(exist = false)
    private String updaterAccountName;

    //@Excel(name = "确认人")
    @ApiModelProperty(value = "确认人昵称")
    @TableField(exist = false)
    private String confirmerAccountName;

}
