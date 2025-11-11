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
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 实验室-联系方式信息对象 s_bas_laboratory_addr
 *
 * @author c
 * @date 2022-03-31
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_bas_laboratory_addr")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasLaboratoryAddr extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-实验室联系信息
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-实验室联系信息")
    private Long laboratoryContactSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] laboratoryContactSidList;
    /**
     * 实验室档案sid
     */
    @Excel(name = "实验室档案sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "实验室档案sid")
    private Long laboratorySid;

    /**
     * 联系信息编码
     */
    @Excel(name = "联系信息编码")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "联系信息编码")
    private Long contactInforCode;

    /**
     * 联系人姓名
     */
    @Excel(name = "联系人姓名")
    @ApiModelProperty(value = "联系人姓名")
    private String contacterName;

    /**
     * 联系人职务
     */
    @Excel(name = "联系人职务")
    @ApiModelProperty(value = "联系人职务")
    private String contacterPosition;

    /**
     * 移动电话
     */
    @Excel(name = "移动电话")
    @ApiModelProperty(value = "移动电话")
    private String contacterMobphone;

    /**
     * 固定电话
     */
    @Excel(name = "固定电话")
    @ApiModelProperty(value = "固定电话")
    private String contacterTelephone;

    /**
     * 传真
     */
    @Excel(name = "传真")
    @ApiModelProperty(value = "传真")
    private String contacterFax;

    /**
     * 电子邮箱
     */
    @Excel(name = "电子邮箱")
    @ApiModelProperty(value = "电子邮箱")
    private String contacterEmail;

    /**
     * 联系地址
     */
    @Excel(name = "联系地址")
    @ApiModelProperty(value = "联系地址")
    private String contacterAddress;

    /**
     * 所属业务部门
     */
    @Excel(name = "所属业务部门")
    @ApiModelProperty(value = "所属业务部门")
    private String contacterDepartment;

    /**
     * 联系人归属业务类型（数据字典的键值或配置档案的编码），可存储多个键值
     */
    @Excel(name = "联系人归属业务类型（数据字典的键值或配置档案的编码），可存储多个键值")
    @ApiModelProperty(value = "联系人归属业务类型（数据字典的键值或配置档案的编码），可存储多个键值")
    private String businessType;

    /**
     * 是否默认联系方式（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "是否默认联系方式（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "是否默认联系方式（数据字典的键值或配置档案的编码）")
    private String isDefault;

    /**
     * 创建人账号
     */
    @Excel(name = "创建人账号")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

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
    @Excel(name = "更新人账号")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccount;

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


    @TableField(exist = false)
    private String creatorAccountName;

    @TableField(exist = false)
    private String updaterAccountName;

    @TableField(exist = false)
    private String confirmerAccountName;

}
