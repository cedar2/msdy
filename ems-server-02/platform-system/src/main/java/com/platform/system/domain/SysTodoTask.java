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
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * 待办事项列对象 s_sys_todo_task
 *
 * @author linhongwei
 * @date 2021-06-29
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_sys_todo_task")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document
public class SysTodoTask extends EmsBaseEntity {


    @Id
    @TableField(exist = false)
    private String id;

    /**
     * 租户ID
     */
    @Indexed
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 待办事项通知sid
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "待办事项通知sid")
    private Long todoTaskSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] todoTaskSidList;
    /**
     * 类别code（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "类别code（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "类别code（数据字典的键值或配置档案的编码）")
    private String taskCategory;

    @TableField(exist = false)
    @ApiModelProperty(value = "类别code")
    private String[] taskCategoryList;

    @ApiModelProperty(value = "业务类型")
    private String businessType;

    @TableField(exist = false)
    @ApiModelProperty(value = "业务类型")
    private String[] businessTypeList;

    /**
     * 标题
     */
    @Excel(name = "标题")
    @ApiModelProperty(value = "标题")
    private String title;

    /**
     * 数据库表名，如：s_pur_purchase_order
     */
    @Excel(name = "数据库表名，如：s_pur_purchase_order")
    @ApiModelProperty(value = "数据库表名，如：s_pur_purchase_order")
    private String tableName;

    @TableField(exist = false)
    @ApiModelProperty(value = "数据库表名，如：s_pur_purchase_order")
    private String[] tableNameList;

    /**
     * 单号/编码sid
     */
    @Excel(name = "单号/编码sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "单号/编码sid")
    private Long documentSid;

    /**
     * 菜单ID
     */
    @Excel(name = "菜单ID")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "菜单ID")
    private Long menuId;

    /**
     * 通知日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "通知日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "通知日期")
    private Date noticeDate;

    /**
     * 用户id
     */
    @Indexed
    @Excel(name = "用户id")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "用户id")
    private Long userId;

    @TableField(exist = false)
    @ApiModelProperty(value = "员工工作状况报表传Y表示后端不需要自动写入当前登录人查询条件")
    private String notAutoUser;

    @TableField(exist = false)
    @ApiModelProperty(value = "用户id")
    private Long[] userIdList;

    @TableField(exist = false)
    @ApiModelProperty(value = "用户（用户昵称）")
    private String nickName;

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
     * 确认人账号（用户名称）
     */
    @Excel(name = "确认人账号（用户名称）")
    @ApiModelProperty(value = "确认人账号（用户名称）")
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

    /**
     * 单号/编码 code
     */
    @Excel(name = "单号/编码 code")
    @ApiModelProperty(value = "单号/编码 code")
    private String documentCode;

    /**
     * 菜单路径
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "菜单路径")
    private String path;


    /**
     * 单据明细行sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "单据明细行sid")
    private Long documentItemSid;
}
