package com.platform.ems.domain;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.platform.common.annotation.Excel;

import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 员工所属部门岗位信息对象 s_bas_staff_depart_position
 *
 * @author qhq
 * @date 2021-03-18
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_bas_staff_depart_position")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasStaffDepartPosition extends EmsBaseEntity {

    /** 客户端口号 */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /** 系统ID-员工所属部门岗位信息 */
    @Excel(name = "系统ID-员工所属部门岗位信息")
    @ApiModelProperty(value = "系统ID-员工所属部门岗位信息")
    @TableId
    private String staffDepartmentPSid;

    /** 系统ID-组织架构节点信息 */
    @ApiModelProperty(value = "系统ID-组织架构节点信息")
    private String organizationNodeSid;

    /** 是否主属岗位 */
    @Excel(name = "是否主属岗位")
    @ApiModelProperty(value = "是否主属岗位")
    private String isDefaultPosition;

    /** 岗位名称 */
    @Excel(name = "岗位名称")
    @ApiModelProperty(value = "岗位名称")
    private String positionName;

    /** 岗位职责 */
    @Excel(name = "岗位职责")
    @ApiModelProperty(value = "岗位职责")
    private String positionDuty;

    /** 创建人账号 */
    @Excel(name = "创建人账号")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /** 更新人账号 */
    @Excel(name = "更新人账号")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccount;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /** 数据源系统 */
    @Excel(name = "数据源系统")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;

    /** 备注*/
    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    /** 系统ID-公司档案list */
    @TableField(exist = false)
    @ApiModelProperty(value = "系统ID-公司档案list")
    private Long[] companySidList;

    /** 处理状态list */
    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态list")
    private String[] handleStatusList;
}
