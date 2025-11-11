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
import com.platform.ems.device.api.CompanyFactoryInfo;
import com.platform.common.core.domain.EmsBaseEntity;
import com.platform.ems.domain.base.HandleStatusInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * 工位档案对象 s_man_workstation
 *
 * @author Straw
 * @date 2023-03-31
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_man_workstation")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ManWorkstation extends EmsBaseEntity implements HandleStatusInfo, CompanyFactoryInfo {

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    String clientId;

    /**
     * 工位档案SID
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "工位档案SID")
    Long workstationSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    Long[] workstationSidList;
    /**
     * 工位编码
     */
    @Excel(name = "工位编码")
    @ApiModelProperty(value = "工位编码")
    String workstationCode;

    /**
     * 工位名称
     */
    @Excel(name = "工位名称")
    @NotBlank(message = "工位名称不能为空")
    @ApiModelProperty(value = "工位名称")
    String workstationName;

    /**
     * 隶属工厂sid
     */
    @Excel(name = "隶属工厂sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "隶属工厂sid")
    Long plantSid;

    /**
     * 隶属工厂编码
     */
    @Excel(name = "隶属工厂编码")
    @ApiModelProperty(value = "隶属工厂编码")
    String plantCode;

    /**
     * 隶属工作中心(班组)sid
     */
    @Excel(name = "隶属工作中心(班组)sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "隶属工作中心(班组)sid")
    Long workCenterSid;

    /**
     * 隶属工作中心(班组)编码
     */
    @Excel(name = "隶属工作中心(班组)编码")
    @ApiModelProperty(value = "隶属工作中心(班组)编码")
    String workCenterCode;

    /**
     * 隶属操作部门sid
     */
    @Excel(name = "隶属操作部门sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "隶属操作部门sid")
    Long departmentSid;

    /**
     * 隶属操作部门编码
     */
    @Excel(name = "隶属操作部门编码")
    @ApiModelProperty(value = "隶属操作部门编码")
    String departmentCode;

    /**
     * 启用/停用状态（数据字典的键值或配置档案的编码）
     */
    @NotBlank(message = "确认状态不能为空")
    @Excel(name = "启用/停用状态（数据字典的键值或配置档案的编码）",
           dictType = "s_valid_flag")
    @ApiModelProperty(value = "启用/停用状态（数据字典的键值或配置档案的编码）")
    String status;

    /**
     * 处理状态（数据字典的键值或配置档案的编码）
     */
    @NotBlank(message = "启停状态不能为空")
    @Excel(name = "处理状态（数据字典的键值或配置档案的编码）",
           dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    String handleStatus;

    /**
     * 处理状态（多选）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    String[] handleStatusList;

    /**
     * 创建人账号（用户账号）
     */
    @Excel(name = "创建人账号（用户账号）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户账号）")
    String creatorAccount;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8",
                pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间",
           width = 30,
           dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    Date createDate;

    /**
     * 更新人账号（用户账号）
     */
    @Excel(name = "更新人账号（用户账号）")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户账号）")
    String updaterAccount;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8",
                pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间",
           width = 30,
           dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    Date updateDate;

    /**
     * 确认人账号（用户账号）
     */
    @Excel(name = "确认人账号（用户账号）")
    @ApiModelProperty(value = "确认人账号（用户账号）")
    String confirmerAccount;

    /**
     * 确认时间
     */
    @JsonFormat(timezone = "GMT+8",
                pattern = "yyyy-MM-dd")
    @Excel(name = "确认时间",
           width = 30,
           dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    Date confirmDate;

    /**
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "数据源系统（数据字典的键值或配置档案的编码）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    String dataSourceSys;


    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人昵称")
    @TableField(exist = false)
    String creatorAccountName;

    @Excel(name = "更改人")
    @ApiModelProperty(value = "更改人昵称")
    @TableField(exist = false)
    String updaterAccountName;

    @Excel(name = "确认人")
    @ApiModelProperty(value = "确认人昵称")
    @TableField(exist = false)
    String confirmerAccountName;

    @TableField(exist = false)
    @ApiModelProperty(value = "工厂简称")
    String shortName;

    @TableField(exist = false)
    @ApiModelProperty(value = "班组名称")
    String workCenterName;

    @TableField(exist = false)
    @ApiModelProperty(value = "操作部门名称")
    String departmentName;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司code")
    String companyCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司sid")
    Long companySid;

    /**
     * 隶属班组，隶属操作部门，隶属工厂要有多选的查询
     *
     * @return
     */

    @TableField(exist = false)
    @ApiModelProperty(value = "班组sid-多选")
    Long[] workCenterSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "操作部门sid-多选")
    Long[] departmentSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "工厂sid-多选")
    Long[] plantSidList;

    @Override
    public String getCompanyCode() {
        return companyCode;
    }

    @Override
    public String getName() {
        return workstationName;
    }
}
