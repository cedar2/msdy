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
 * 供应商的产能信息对象 s_bas_vendor_productivity
 *
 * @author chenkw
 * @date 2022-01-06
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_bas_vendor_productivity")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasVendorProductivity extends EmsBaseEntity {

    /**
     * 客户端口号
     */
    @Excel(name = "客户端口号")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /**
     * 系统SID-供应商的产能信息
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商的产能信息")
    private Long vendorProductivitySid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] vendorProductivitySidList;
    /**
     * 系统ID-供应商档案
     */
    @Excel(name = "系统ID-供应商档案")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-供应商档案")
    private Long vendorSid;

    /**
     * 班组名称
     */
    @Excel(name = "班组名称")
    @NotBlank(message = "班组名称不能为空")
    @ApiModelProperty(value = "班组名称")
    private String teamName;

    /**
     * 生产品类
     */
    @Excel(name = "生产品类")
    @ApiModelProperty(value = "生产品类")
    private String productCategory;

    /**
     * 班组工人数
     */
    @Excel(name = "班组工人数")
    @Digits(integer = 5, fraction = 0, message = "班组工人数长度不能超过5位")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "班组工人数")
    private Long workerNumber;

    /**
     * 预计日均产量
     */
    @Excel(name = "预计日均产量")
    @Digits(integer = 5, fraction = 0, message = "预计日均产量长度不能超过5位")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "预计日均产量")
    private Long perdayOutput;

    /**
     * 班组联系人
     */
    @Excel(name = "班组联系人")
    @ApiModelProperty(value = "班组联系人")
    private String name;

    /**
     * 班组联系人职务
     */
    @Excel(name = "班组联系人职务")
    @ApiModelProperty(value = "班组联系人职务")
    private String position;

    /**
     * 电话
     */
    @Excel(name = "电话")
    @Length(max = 60, message = "电话不能超过60位")
    @ApiModelProperty(value = "电话")
    private String phone;

    /**
     * Email
     */
    @Excel(name = "Email")
    @Length(max = 60, message = "Email不能超过60位")
    @ApiModelProperty(value = "Email")
    private String email;

    /**
     * 富余产能说明
     */
    @Excel(name = "富余产能说明")
    @ApiModelProperty(value = "富余产能说明")
    private String surplusCapacityD;

    /**
     * 班组地址
     */
    @Excel(name = "班组地址")
    @ApiModelProperty(value = "班组地址")
    private String address;

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
