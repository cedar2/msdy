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

/**
 * 供应商注册-产能信息对象 s_bas_vendor_register_productivity
 *
 * @author chenkw
 * @date 2022-02-21
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_bas_vendor_register_productivity")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasVendorRegisterProductivity extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-供应商注册产能信息
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商注册产能信息")
    private Long vendorRegisterProductivitySid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] vendorRegisterProductivitySidList;

    /**
     * 系统SID-供应商注册基本信息
     */
    @Excel(name = "系统SID-供应商注册基本信息")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商注册基本信息")
    private Long vendorRegisterSid;

    /**
     * 班组名称
     */
    @NotBlank(message = "班组名称不能为空")
    @Length(max = 300,message = "班组名称最大只支持输入300位")
    @Excel(name = "班组名称")
    @ApiModelProperty(value = "班组名称")
    private String teamName;

    /**
     * 生产品类
     */
    @Length(max = 300,message = "生产品类最大只支持输入300位")
    @Excel(name = "生产品类")
    @ApiModelProperty(value = "生产品类")
    private String productCategory;

    /**
     * 班组工人数
     */
    @Digits(integer = 5, fraction = 0, message = "班组工人数最大只支持输入5位")
    @Excel(name = "班组工人数")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "班组工人数")
    private Long workerNumber;

    /**
     * 预计日均产量
     */
    @Digits(integer = 5, fraction = 0, message = "预计日均产量最大只支持输入5位")
    @Excel(name = "预计日均产量")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "预计日均产量")
    private Long perdayOutput;

    /**
     * 班组联系人
     */
    @Length(max = 300,message = "班组联系人最大只支持输入300位")
    @Excel(name = "班组联系人")
    @ApiModelProperty(value = "班组联系人")
    private String name;

    /**
     * 班组联系人职务
     */
    @Length(max = 60,message = "班组联系人最大只支持输入60位")
    @Excel(name = "班组联系人职务")
    @ApiModelProperty(value = "班组联系人职务")
    private String position;

    /**
     * 电话
     */
    @Length(max = 60,message = "电话最大只支持输入60位")
    @Excel(name = "电话")
    @ApiModelProperty(value = "电话")
    private String phone;

    /**
     * Email
     */
    @Length(max = 60,message = "Email最大只支持输入60位")
    @Excel(name = "Email")
    @ApiModelProperty(value = "Email")
    private String email;

    /**
     * 富余产能说明
     */
    @Length(max = 300,message = "富余产能说明最大只支持输入300位")
    @Excel(name = "富余产能说明")
    @ApiModelProperty(value = "富余产能说明")
    private String surplusCapacityD;

    /**
     * 班组地址
     */
    @Length(max = 300,message = "班组地址最大只支持输入300位")
    @Excel(name = "班组地址")
    @ApiModelProperty(value = "班组地址")
    private String address;

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
