package com.platform.system.domain;


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
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.List;

/**
 * 通知公告对象 s_sys_notice
 *
 * @author linhongwei
 * @date 2021-06-30
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_sys_notice")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SysNotice extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 公告sid
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公告sid")
    private Long noticeSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] noticeSidList;
    /**
     * 公告编码
     */
    @Excel(name = "公告编码")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公告编码")
    private Long noticeCode;

    /**
     * 公告标题
     */
    @NotEmpty(message = "标题不能为空")
    @Length(max = 300, message = "标题不能超过300个字符")
    @Excel(name = "标题")
    @ApiModelProperty(value = "公告标题")
    private String noticeTitle;

    /**
     * 排序
     */
    @Excel(name = "排序")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "排序")
    private Long serialNum;

    /**
     * 公告类型（1通知 2公告）（数据字典的键值或配置档案的编码）
     */
    @NotEmpty(message = "类型不能为空")
    @Excel(name = "类型", dictType = "s_notice_type")
    @ApiModelProperty(value = "公告类型（1通知 2公告）（数据字典的键值或配置档案的编码）")
    private String noticeType;

    @TableField(exist = false)
    private String[] noticeTypeList;

    /**
     * 公告范围类型（数据字典的键值或配置档案的编码）；内部可见；供应商可见、客户可见、供应商/客户皆可见；全员可见
     */
    @Excel(name = "公告范围")
    @ApiModelProperty(value = "公告范围类型（数据字典的键值或配置档案的编码）；内部可见；供应商可见、客户可见、供应商/客户皆可见；全员可见")
    private String rangeType;

    /**
     * 公告内容
     */
    @Length(max = 600, message = "内容不能超过600个字符")
    @ApiModelProperty(value = "公告内容")
    private String noticeContent;

    /**
     * 公告有效期起
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "有效期(起)", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "公告有效期起")
    private Date dateStart;

    /**
     * 公告有效期止
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "有效期(至)", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "公告有效期止")
    private Date dateEnd;

    @TableField(exist = false)
    private Date today;

    /**
     * 处理状态（数据字典的键值或配置档案的编码）
     */
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    @TableField(exist = false)
    private String[] handleStatusList;

    /**
     * 启用/停用状态（数据字典的键值或配置档案的编码）
     */
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "启用/停用", dictType = "s_valid_flag")
    @ApiModelProperty(value = "启用/停用状态（数据字典的键值或配置档案的编码）")
    private String status;

    /**
     * 创建人账号（用户名称）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    /**
     * 创建人账号（用户名称）
     */
    @TableField(exist = false)
    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccountName;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /**
     * 更新人账号（用户名称）
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

    @TableField(exist = false)
    @ApiModelProperty(value = "更改人账号（用户名称）")
    private String updaterAccountName;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 确认/发布人账号（用户名称）
     */
    @ApiModelProperty(value = "确认/发布人账号（用户名称）")
    private String confirmerAccount;

    @TableField(exist = false)
    @Excel(name = "发布人")
    @ApiModelProperty(value = "确认/发布人账号（用户名称）")
    private String confirmerAccountName;

    /**
     * 确认/发布时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "发布日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认/发布时间")
    private Date confirmDate;

    /**
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    @TableField(exist = false)
    @ApiModelProperty(value ="附件列表")
    private List<SysNoticeAttach> attachmentList;

    /**
     * 用户id
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "用户id")
    private Long userId;

    /**
     * 待办条数
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "待办条数")
    private Integer dbTodoTasks;

    /**
     * 待批条数
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "待批条数")
    private Integer dpTodoTasks;

    /**
     * 预警条数
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "预警条数")
    private Integer toexpires;

    /**
     * 警示条数
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "警示条数")
    private Integer overdues;

    /**
     * 动态条数
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "动态条数")
    private Integer businessBcsts;

    /**
     * 公告条数
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "公告条数")
    private Integer notices;
}

