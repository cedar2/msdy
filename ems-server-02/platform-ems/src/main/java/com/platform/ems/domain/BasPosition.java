package com.platform.ems.domain;

import java.util.Date;
import java.util.List;

import cn.hutool.core.util.StrUtil;
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

/**
 * 岗位对象 s_bas_position
 *
 * @author qhq
 * @date 2021-03-18
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_bas_position")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasPosition extends EmsBaseEntity {

    /** 客户端口号 */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /** 系统ID-岗位信息
 */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-岗位信息 ")
    private Long positionSid;

    public void setPositionCode(String positionCode) {
        if (StrUtil.isNotBlank(positionCode)){
            positionCode = positionCode.replaceAll("\\s*", "");
        }
        this.positionCode = positionCode;
    }

    public void setPositionName(String positionName) {
        if (StrUtil.isNotBlank(positionName)){
            positionName = positionName.trim();
        }
        this.positionName = positionName;
    }

    /** 岗位编码 */
    @Excel(name = "岗位编码")
    @ApiModelProperty(value = "岗位编码")
    private String positionCode;

    /** 岗位名称 */
    @Excel(name = "岗位名称")
    @ApiModelProperty(value = "岗位名称")
    private String positionName;

    /** 所属公司编码（公司档案的sid） */
    @ApiModelProperty(value = "所属公司编码（公司档案的sid）")
    private String companySid;

    @TableField(exist = false)
    @ApiModelProperty(value = "所属公司名称")
    private String companyName;

    @Excel(name = "公司")
    @TableField(exist = false)
    @ApiModelProperty(value = "所属公司简称")
    private String companyShortName;

    /** 启用/停用状态 */
    @Excel(name = "启用/停用", dictType = "s_valid_flag")
    @ApiModelProperty(value = "启用/停用状态")
    private String status;

    /** 处理状态 */
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @ApiModelProperty(value = "停用说明")
    private String disableRemark;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    /** 岗位职责 */
    @ApiModelProperty(value = "岗位职责")
    private String positionDuty;

    /** 创建人账号 */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    @Excel(name = "创建人")
    @TableField(exist = false)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccountName;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /** 更新人账号 */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccount;

    @Excel(name = "更改人")
    @TableField(exist = false)
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccountName;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "更改日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /** 确认人账号 */
    @ApiModelProperty(value = "确认人账号")
    private String confirmerAccount;

    @Excel(name = "确认人")
    @TableField(exist = false)
    @ApiModelProperty(value = "确认人账号")
    private String confirmerAccountName;

    /** 确认时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "确认日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /** 数据源系统 */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;

    @TableField(exist = false)
    @ApiModelProperty(value = "sid集合，用于批量操作")
    private List<Long> positionSids;

    @ApiModelProperty(value = "排序")
    private int serialNum;

    @TableField(exist = false)
    private Integer pageNum;

    @TableField(exist = false)
    private Integer pageSize;

    /** 系统ID-公司档案list */
    @TableField(exist = false)
    @ApiModelProperty(value = "系统ID-公司档案list")
    private Long[] companySidList;

    /** 处理状态list */
    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态list")
    private String[] handleStatusList;
}
