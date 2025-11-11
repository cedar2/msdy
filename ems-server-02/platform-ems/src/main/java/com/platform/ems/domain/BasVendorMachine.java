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
 * 供应商的设备信息对象 s_bas_vendor_machine
 *
 * @author chenkw
 * @date 2022-01-06
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_bas_vendor_machine")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasVendorMachine extends EmsBaseEntity {

    /**
     * 客户端口号
     */
    @Excel(name = "客户端口号")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /**
     * 系统SID-供应商的设备信息
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商的设备信息")
    private Long vendorMachineSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] vendorMachineSidList;
    /**
     * 系统ID-供应商档案
     */
    @Excel(name = "系统ID-供应商档案")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-供应商档案")
    private Long vendorSid;

    /**
     * 设备名称
     */
    @Excel(name = "设备名称")
    @NotBlank(message = "设备名称不能为空")
    @ApiModelProperty(value = "设备名称")
    private String machineName;

    /**
     * 数量
     */
    @Excel(name = "数量")
    @NotNull(message = "数量不能为空")
    @Digits(integer = 3, fraction = 0, message = "数量长度不能超过3位")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "数量")
    private Long quantity;

    /**
     * 计量单位（数据字典的键值或配置档案的编码）
     */
    @NotBlank(message = "计量单位不能为空")
    @ApiModelProperty(value = "计量单位（数据字典的键值或配置档案的编码）")
    private String unitBase;

    @Excel(name = "计量单位")
    @TableField(exist = false)
    @ApiModelProperty(value = "计量单位名称")
    private String unitBaseName;

    /**
     * 检修周期
     */
    @Excel(name = "检修周期")
    @Digits(integer = 3, fraction = 0, message = "检修周期长度不能超过3位")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "检修周期")
    private Long repairCycle;

    /**
     * 时间单位（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "时间单位（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "时间单位（数据字典的键值或配置档案的编码）")
    private String timeUnit;

    /**
     * 品牌
     */
    @Excel(name = "品牌")
    @ApiModelProperty(value = "品牌")
    private String brand;

    /**
     * 存放位置
     */
    @Excel(name = "存放位置")
    @ApiModelProperty(value = "存放位置")
    private String location;

    /**
     * 创建人账号
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    @TableField(exist = false)
    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人账号")
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
     * 更新人账号
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccount;

    @TableField(exist = false)
    @Excel(name = "更新人")
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccountName;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 数据源系统
     */
    @Excel(name = "数据源系统")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;


}
