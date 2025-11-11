package com.platform.common.core.domain.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import lombok.experimental.Accessors;

/**
 * 组织架构信息对象 s_sys_org
 *
 * @author c
 * @date 2021-09-17
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_sys_org")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SysOrg extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 租户ID */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /** 系统SID-组织架构节点信息 */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-组织架构节点信息")
    private Long nodeSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long [] nodeSidList;

    /** 节点编码 */
    @Excel(name = "节点编码")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "节点编码")
    private Long nodeCode;

    /** 节点名称 */
    @Excel(name = "节点名称")
    @ApiModelProperty(value = "节点名称")
    private String nodeName;

    /** 节点名称简称 */
    @Excel(name = "节点名称简称")
    @ApiModelProperty(value = "节点名称简称")
    private String nodeShortName;

    /** 节点类型（数据字典的键值或配置档案的编码），如：公司、部门、员工 */
    @Excel(name = "节点类型（数据字典的键值或配置档案的编码），如：公司、部门、员工")
    @ApiModelProperty(value = "节点类型（数据字典的键值或配置档案的编码），如：公司、部门、员工")
    private String nodeType;

    @TableField(exist = false)
    @ApiModelProperty(value = "节点类型（数据字典的键值或配置档案的编码），如：公司、部门、员工")
    private String parentNodeType;

    @TableField(exist = false)
    @ApiModelProperty(value = "上一级节点名称")
    private String parentNodeName;

    /** 节点层级 */
    @Excel(name = "节点层级")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "节点层级")
    private Long level;

    /** 上一级节点ID */
    @Excel(name = "上一级节点ID")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "上一级节点ID")
    private Long parentNodeSid;

    /** 是否主属部门（数据字典的键值或配置档案的编码） */
    @Excel(name = "是否主属部门（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "是否主属部门（数据字典的键值或配置档案的编码）")
    private String isDefaultDepartment;

    /** 上级领导（用户名称） */
    @Excel(name = "上级领导（用户名称）")
    @ApiModelProperty(value = "上级领导（用户名称）")
    private String superiorLeaders;

    /** 排序 */
    @Excel(name = "排序")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "排序")
    private Long serialNum;

    /** 主属岗位sid */
    @Excel(name = "主属岗位sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "主属岗位sid")
    private Long defaultPosition;

    /** 是否有下一级节点（数据字典的键值或配置档案的编码） */
    @Excel(name = "是否有下一级节点（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "是否有下一级节点（数据字典的键值或配置档案的编码）")
    private String hasNextNode;

    /** 创建人账号（用户名称） */
    @Excel(name = "创建人账号（用户名称）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    /** 创建时间 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /** 更新人账号（用户名称） */
    @Excel(name = "更新人账号（用户名称）")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

    /** 更新时间 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /** 确认人账号（用户名称） */
    @Excel(name = "确认人账号（用户名称）")
    @ApiModelProperty(value = "确认人账号（用户名称）")
    private String confirmerAccount;

    /** 确认时间 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "确认时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /** 数据源系统（数据字典的键值或配置档案的编码） */
    @Excel(name = "数据源系统（数据字典的键值或配置档案的编码）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    /** 系统SID-员工档案 */
    @Excel(name = "系统SID-员工档案")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-员工档案")
    private Long staffSid;

    /** 系统SID-部门档案 */
    @Excel(name = "系统SID-部门档案")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-部门档案")
    private Long departmentSid;

    /** 系统SID-公司档案 */
    @Excel(name = "系统SID-公司档案")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-公司档案")
    private Long companySid;

    /** 附属岗位sid */
    @Excel(name = "附属岗位sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "附属岗位sid")
    private Long pertainPosition;

    @TableField(exist = false)
    @ApiModelProperty(value ="子集列表")
    private List<SysOrg> children = new ArrayList<>();
}
