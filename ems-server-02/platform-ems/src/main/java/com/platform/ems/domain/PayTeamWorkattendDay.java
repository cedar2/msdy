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

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * 班组日出勤信息对象 s_pay_team_workattend_day
 *
 * @author linhongwei
 * @date 2022-07-27
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_pay_team_workattend_day")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PayTeamWorkattendDay extends EmsBaseEntity {

    /**confirmer_account_name
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-班组日出勤信息
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-班组日出勤信息")
    private Long teamWorkattendDaySid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "班组日出勤编号")
    private Long teamWorkattendDayCode;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] teamWorkattendDaySidList;
    /**
     * 日期(出勤)
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "日期(出勤)", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "日期(出勤)")
    private Date workattendDate;

    @ApiModelProperty(value ="日期(出勤)开始时间")
    @TableField(exist = false)
    private String workattendBeginTime;

    @ApiModelProperty(value ="日期(出勤)结束时间")
    @TableField(exist = false)
    private String workattendEndTime;

    /**
     * 工厂sid
     */
    @Excel(name = "工厂sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "工厂sid")
    private Long plantSid;

    /**
     * 工厂编码code
     */
    @Excel(name = "工厂编码code")
    @ApiModelProperty(value = "工厂编码code")
    private String plantCode;

    @ApiModelProperty(value = "工厂名称")
    @TableField(exist = false)
    private String plantName;

    /**
     * 班组sid
     */
    @Excel(name = "班组sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "班组sid")
    private Long workCenterSid;

    @ApiModelProperty(value = "班组名称")
    @TableField(exist = false)
    private String workCenterName;

    /**
     * 班组编码code
     */
    @Excel(name = "班组编码code")
    @ApiModelProperty(value = "班组编码code")
    private String workCenterCode;

    /**
     * 操作部门（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "操作部门（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "操作部门（数据字典的键值或配置档案的编码）")
    private String department;


    @ApiModelProperty(value = "班组名称")
    @TableField(exist = false)
    private String departmentName;

    /**
     * 工作班次（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "工作班次（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "工作班次（数据字典的键值或配置档案的编码）")
    private String workShift;

    /**
     * 应出勤(人数)
     */
    @Excel(name = "应出勤(人数)")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "应出勤(人数)")
    private Long yingcq;

    /**
     * 实出勤(人数)
     */
    @Excel(name = "实出勤(人数)")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "实出勤(人数)")
    private Long shicq;

    /**
     * 请假(人数)
     */
    @Excel(name = "请假(人数)")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "请假(人数)")
    private Long qingj;

    /**
     * 待料(人数)
     */
    @Excel(name = "待料(人数)")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "待料(人数)")
    private Long dail;

    /**
     * 旷工(人数)
     */
    @Excel(name = "旷工(人数)")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "旷工(人数)")
    private Long kuangg;

    /**
     * 缺卡(人数)
     */
    @Excel(name = "缺卡(人数)")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "缺卡(人数)")
    private Long quek;

    /**
     * 迟到(人数)
     */
    @Excel(name = "迟到(人数)")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "迟到(人数)")
    private Long chid;

    /**
     * 早退(人数)
     */
    @Excel(name = "早退(人数)")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "早退(人数)")
    private Long zaot;

    /**
     * 处理状态（数据字典的键值或配置档案的编码）
     */
    @NotBlank(message = "启停状态不能为空")
    @Excel(name = "处理状态（数据字典的键值或配置档案的编码）", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    /**
     * 创建人账号（用户账号）
     */
    @Excel(name = "创建人账号（用户账号）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户账号）")
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
     * 更新人账号（用户账号）
     */
    @Excel(name = "更新人账号（用户账号）")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户账号）")
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
     * 确认人账号（用户账号）
     */
    @Excel(name = "确认人账号（用户账号）")
    @ApiModelProperty(value = "确认人账号（用户账号）")
    private String confirmerAccount;

    /**
     * 确认时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "确认时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "数据源系统（数据字典的键值或配置档案的编码）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;


    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人昵称")
    @TableField(exist = false)
    private String creatorAccountName;

    @Excel(name = "更改人")
    @ApiModelProperty(value = "更改人昵称")
    @TableField(exist = false)
    private String updaterAccountName;

    @Excel(name = "确认人")
    @ApiModelProperty(value = "确认人昵称")
    @TableField(exist = false)
    private String confirmerAccountName;


    @TableField(exist = false)
    @ApiModelProperty(value = "工厂sid")
    private Long[] plantSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "工作中心/班组sid")
    private Long[] workCenterSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "部门查询")
    private String[] departmentList;

    @TableField(exist = false)
    @ApiModelProperty(value = "工作班次")
    private String[] workShiftList;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态")
    private String[] handleStatusList;

    @TableField(exist = false)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String[] creatorAccountList;

}
