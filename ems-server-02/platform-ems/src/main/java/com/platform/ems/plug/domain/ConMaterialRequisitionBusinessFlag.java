package com.platform.ems.plug.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * 业务标识_领退料对象 s_con_material_requisition_business_flag
 *
 * @author platform
 * @date 2024-11-10
 */
@Data
@Accessors(chain = true)
@TableName(value = "s_con_material_requisition_business_flag")
public class ConMaterialRequisitionBusinessFlag extends EmsBaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 租户ID
     */

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-业务标识(领退料)sid
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-业务标识(领退料)sid")
    private Long sid;

    @TableField(exist = false)
    @ApiModelProperty(value = "sid数组")
    private Long[] sidList;

    /**
     * 业务标识(领退料)编码
     */
    @Excel(name = "业务标识编码")
    @ApiModelProperty(value = "业务标识(领退料)编码")
    private String code;

    /**
     * 业务标识(领退料)名称
     */
    @Excel(name = "业务标识名称")
    @ApiModelProperty(value = "业务标识(领退料)名称")
    private String name;

    /**
     * 是否超耗
     */
    @Excel(name = "是否超耗", dictType = "s_yesno_flag")
    @ApiModelProperty(value = "是否超耗")
    private String isChaohao;

    /**
     * 启用/停用状态
     */
    @NotBlank(message = "启停状态不能为空")
    @Excel(name = "启用/停用状态", dictType = "s_valid_flag")
    @ApiModelProperty(value = "启用/停用状态")
    private String status;

    /**
     * 处理状态
     */
    @NotBlank(message = "处理状态不能为空")
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    /**
     * 创建人账号(用户账号)
     */
    @TableField(fill = FieldFill.INSERT)
//    @Excel(name = "创建人账号(用户账号)")
    @ApiModelProperty(value = "创建人账号(用户账号)")
    private String creatorAccount;

    /**
     * 创建人
     */
    @TableField(exist = false)
    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人昵称")
    private String creatorAccountName;

    /**
     * 创建人数组
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "创建人数组（用于查询）")
    private String[] creatorAccountList;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /**
     * 更新人账号(用户账号)
     */
    @TableField(fill = FieldFill.UPDATE)
//    @Excel(name = "更新人账号(用户账号)")
    @ApiModelProperty(value = "更新人账号(用户账号)")
    private String updaterAccount;

    /**
     * 更改人
     */
    @TableField(exist = false)
//    @Excel(name = "更改人")
    @ApiModelProperty(value = "更改人昵称")
    private String updaterAccountName;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.UPDATE)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
//    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 确认人账号(用户账号)
     */
//    @Excel(name = "确认人账号(用户账号)")
    @ApiModelProperty(value = "确认人账号(用户账号)")
    private String confirmerAccount;

    /**
     * 确认人
     */
    @TableField(exist = false)
//    @Excel(name = "确认人")
    @ApiModelProperty(value = "确认人昵称")
    private String confirmerAccountName;

    /**
     * 确认时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
//    @Excel(name = "确认时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 数据源系统(数据字典的键值或配置档案的编码)
     */
    @TableField(fill = FieldFill.INSERT)
//    @Excel(name = "数据源系统(数据字典的键值或配置档案的编码)")
    @ApiModelProperty(value = "数据源系统(数据字典的键值或配置档案的编码)")
    private String dataSourceSys;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

}
