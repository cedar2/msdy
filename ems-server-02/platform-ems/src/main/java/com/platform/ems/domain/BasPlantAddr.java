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
import com.fasterxml.jackson.annotation.JsonInclude;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 工厂-联系方式信息对象 s_bas_plant_addr
 *
 * @author linhongwei
 * @date 2021-03-27
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_bas_plant_addr")
public class BasPlantAddr extends EmsBaseEntity {

    /** 客户端口号 */
        @Excel(name = "客户端口号")
        @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /** 系统ID-工厂联系信息 */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-工厂联系信息")
    private Long plantContactSid;

    /** 系统ID-工厂档案 */
        @Excel(name = "系统ID-工厂档案")
        @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-工厂档案")
    private Long plantSid;

    /** 联系信息编码 */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "联系信息编码")
    private Long contactInforCode;

    /** 联系人姓名 */
        @Excel(name = "联系人姓名")
        @ApiModelProperty(value = "联系人姓名")
    private String contacterName;

    /** 联系人职务 */
        @Excel(name = "联系人职务")
        @ApiModelProperty(value = "联系人职务")
    private String contacterPosition;

    /** 移动电话 */
        @Excel(name = "移动电话")
        @ApiModelProperty(value = "移动电话")
    private String contacterMobphone;

    /** 固定电话 */
        @Excel(name = "固定电话")
        @ApiModelProperty(value = "固定电话")
    private String contacterTelephone;

    /** 传真 */
        @Excel(name = "传真")
        @ApiModelProperty(value = "传真")
    private String contacterFax;

    /** 电子邮箱 */
        @Excel(name = "电子邮箱")
        @ApiModelProperty(value = "电子邮箱")
    private String contacterEmail;

    /** 联系地址 */
        @Excel(name = "联系地址")
        @ApiModelProperty(value = "联系地址")
    private String contacterAddress;

    /** 所属业务部门 */
        @Excel(name = "所属业务部门")
        @ApiModelProperty(value = "所属业务部门")
    private String contacterDepartment;

    /** 联系人归属业务类型编码 */
        @Excel(name = "联系人归属业务类型编码")
        @ApiModelProperty(value = "联系人归属业务类型编码")
    private String businessType;

    /** 默认联系方式 */
        @Excel(name = "默认联系方式")
        @ApiModelProperty(value = "默认联系方式")
    private String isDefault;

    /** 创建人账号 */
        @Excel(name = "创建人账号")
        @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    /** 创建时间 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
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
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /** 数据源系统 */
        @Excel(name = "数据源系统")
        @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;


    private String remark;

    @TableField(exist = false)
    private String beginTime;

    @TableField(exist = false)
    private String endTime;


}
