package com.platform.ems.domain;

import java.util.Date;

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
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 供应商注册-设备信息对象 s_bas_vendor_register_machine
 *
 * @author chenkw
 * @date 2022-02-21
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_bas_vendor_register_machine")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasVendorRegisterMachine extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-供应商注册设备信息
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商注册设备信息")
    private Long vendorRegisterMachineSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] vendorRegisterMachineSidList;

    /**
     * 系统SID-供应商注册基本信息
     */
    @Excel(name = "系统SID-供应商注册基本信息")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商注册基本信息")
    private Long vendorRegisterSid;

    /**
     * 设备名称
     */
    @NotBlank(message = "设备名称不能为空")
    @Length(max = 300, message = "设备名称最大只支持输入300位")
    @Excel(name = "设备名称")
    @ApiModelProperty(value = "设备名称")
    private String machineName;

    /**
     * 数量
     */
    @NotNull(message = "数量不能为空")
    @Digits(integer = 12, fraction = 0, message = "数量最大只支持输入12位")
    @Excel(name = "数量")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "数量")
    private Long quantity;

    /**
     * 计量单位（数据字典的键值或配置档案的编码）
     */
    @NotBlank(message = "基本单位不能为空")
    @Excel(name = "计量单位")
    @ApiModelProperty(value = "计量单位编码（配置档案的编码）")
    private String unitBase;

    @TableField(exist = false)
    @ApiModelProperty(value = "计量单位（配置档案的编码）")
    private String unitBaseName;

    /**
     * 检修周期
     */
    @Digits(integer = 3, fraction = 0, message = "检修周期最大只支持输入3位")
    @Excel(name = "检修周期")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "检修周期")
    private Long repairCycle;

    /**
     * 时间单位（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "时间单位", dictType = "s_time_unit")
    @ApiModelProperty(value = "时间单位（数据字典的键值）")
    private String timeUnit;

    /**
     * 品牌
     */
    @Length(max = 600, message = "品牌最大只支持输入600位")
    @Excel(name = "品牌")
    @ApiModelProperty(value = "品牌")
    private String brand;

    /**
     * 存放位置
     */
    @Length(max = 600, message = "存放位置最大只支持输入600位")
    @Excel(name = "存放位置")
    @ApiModelProperty(value = "存放位置")
    private String location;

    /**
     * 创建人账号（用户账号）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户账号）")
    private String creatorAccount;

    @Excel(name = "创建人")
    @TableField(exist = false)
    private String creatorAccountName;


    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd hh:mm:ss")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /**
     * 更新人账号（用户账号）
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户账号）")
    private String updaterAccount;

    @Excel(name = "更新人")
    @TableField(exist = false)
    private String updaterAccountName;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd hh:mm:ss")
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

}
