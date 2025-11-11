package com.platform.ems.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.core.domain.BaseEntity;
import com.platform.common.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableId;

import java.util.List;

import com.platform.common.core.domain.EmsBaseEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import com.platform.common.core.domain.document.UserOperLog;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 用户收藏菜单对象 s_sys_user_favorite_menu
 *
 * @author linhongwei
 * @date 2021-06-29
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_sys_user_favorite_menu")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document
public class SysUserFavoriteMenu extends EmsBaseEntity {

    @Id
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
     * 用户收藏菜单sid
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "用户收藏菜单sid")
    private Long favoriteMenuSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] favoriteMenuSidList;
    /**
     * 用户（用户名称）
     */
    @Indexed
    @Excel(name = "用户（用户名称）")
    @ApiModelProperty(value = "用户（用户名称）")
    private Long userId;

    /**
     * 菜单ID
     */
    @Excel(name = "菜单ID")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "菜单ID")
    private Long menuId;

    /**
     * 菜单重命名称
     */
    @Excel(name = "菜单重命名称")
    @ApiModelProperty(value = "菜单重命名称")
    private String menuRename;

    /**
     * 排序
     */
    @Excel(name = "排序")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "排序")
    private Long serialNum;

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
     * 菜单路径
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "菜单路径")
    private String path;


}
