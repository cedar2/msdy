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
import java.util.List;

/**
 * 工作中心-成员对象 s_man_work_center_member
 *
 * @author c
 * @date 2022-03-21
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_man_work_center_member")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ManWorkCenterMember extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-工作中心-成员
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-工作中心-成员")
    private Long workCenterMemberSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] workCenterMemberSidList;
    /**
     * 系统SID-工作中心
     */
    @Excel(name = "系统SID-工作中心")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-工作中心")
    private Long workCenterSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "工作中心/班组名称")
    private String workCenterName;

    /**
     * 成员员工号
     */
    @Excel(name = "成员员工号")
    @ApiModelProperty(value = "成员员工号")
    private String memberCode;

    /**
     * 成员员工sid
     */
    @Excel(name = "成员员工sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "成员员工sid")
    private Long memberSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "员工档案在离职状态")
    private String isOnJob;

    @TableField(exist = false)
    @ApiModelProperty(value = "员工档案处理状态")
    private String handleStatus;

    /**
     * 成员姓名
     */
    @Excel(name = "成员姓名")
    @ApiModelProperty(value = "成员姓名")
    private String memberName;

    /**
     * 成员岗位sid
     */
    @Excel(name = "成员岗位sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "成员岗位sid")
    private Long memberPosition;

    @ApiModelProperty(value = "岗位名称")
    @TableField(exist = false)
    private String positionName;

    @ApiModelProperty(value = "系统账号")
    @TableField(exist = false)
    private String systemAccount;

    /**
     * 成员联系电话
     */
    @Excel(name = "成员联系电话")
    @ApiModelProperty(value = "成员联系电话")
    private String phone;

    /**
     * 序号
     */
    @Excel(name = "序号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "序号")
    private Long sort;

    /**
     * 创建人账号（用户名称）
     */
    @Excel(name = "创建人账号（用户名称）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
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
     * 更新人账号（用户名称）
     */
    @Excel(name = "更新人账号（用户名称）")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
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
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "数据源系统（数据字典的键值或配置档案的编码）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;


    @TableField(exist = false)
    private String creatorAccountName;

    @TableField(exist = false)
    private String updaterAccountName;

    @TableField(exist = false)
    private String confirmerAccountName;

    @TableField(exist = false)
    private List<Long> staffSidList;

}
