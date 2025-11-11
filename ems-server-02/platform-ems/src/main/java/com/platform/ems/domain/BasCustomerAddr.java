package com.platform.ems.domain;

import java.util.Date;

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
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

/**
 * 客户-联系方式信息对象 s_bas_customer_addr
 *
 * @author ChenPinzhen
 * @date 2021-01-27
 */
@Data
@Accessors( chain = true)
@TableName(value = "s_bas_customer_addr")
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasCustomerAddr extends EmsBaseEntity {

    /** 客户端口号 */
    @ApiModelProperty(value = "客户端口号")
    @TableField(fill = FieldFill.INSERT)
    private String clientId;

    /** 系统ID-客户联系信息 */
    @Excel(name = "系统ID-客户联系信息")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-客户联系信息")
    @TableId
    private Long customerContactSid;

    /** 系统ID-客户档案 */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-客户档案")
    private Long customerSid;

    /** 联系信息编码 */
    @ApiModelProperty(value = "联系信息编码")
    private String contactInforCode;

    /** 联系人姓名 */
    @Excel(name = "联系人姓名")
    @Length(max = 120, message = "联系方式中的联系人姓名不能超过120位")
    @NotBlank(message = "联系方式中的联系人姓名不能为空！")
    @ApiModelProperty(value = "联系人姓名")
    private String contacterName;

    /** 联系人职务 */
    @Excel(name = "联系人职务")
    @Length(max = 60, message = "联系方式中的联系人职务不能超过60位")
    @ApiModelProperty(value = "联系人职务")
    private String contacterPosition;

    /** 移动电话 */
    @Excel(name = "移动电话")
    @Length(max = 11, message = "联系方式中的移动电话不能超过11位")
    @NotBlank(message = "联系方式中的移动电话不能为空！")
    @ApiModelProperty(value = "移动电话")
    private String contacterMobphone;

    /** 固定电话 */
    @Excel(name = "固定电话")
    @Length(max = 30, message = "联系方式中的固定电话不能超过30位")
    @ApiModelProperty(value = "固定电话")
    private String contacterTelephone;

    /** 传真 */
    @Excel(name = "传真")
    @Length(max = 18, message = "联系方式中的传真不能超过18位")
    @ApiModelProperty(value = "传真")
    private String contacterFax;

    /** 电子邮箱 */
    @Excel(name = "电子邮箱")
    @Length(max = 50, message = "联系方式中的电子邮箱不能超过50位")
    @ApiModelProperty(value = "电子邮箱")
    private String contacterEmail;

    /** 联系地址 */
    @Excel(name = "联系地址")
    @Length(max = 300, message = "联系方式中的联系地址不能超过300位")
    @ApiModelProperty(value = "联系地址")
    private String contacterAddress;

    /** 所属业务部门 */
    @Excel(name = "所属业务部门")
    @Length(max = 30, message = "联系方式中的所属业务部门不能超过30位")
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

    @Length(max = 100, message = "联系方说明最大只支持输入100位")
    @ApiModelProperty(value = "联系方说明")
    private String contactPartyRemark;

    /** 创建人账号 */
    @ApiModelProperty(value = "创建人账号")
    @TableField(fill = FieldFill.INSERT)
    private String creatorAccount;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人账号")
    @TableField(exist = false)
    private String creatorAccountName;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    @ApiModelProperty(value = "创建时间")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    private Date createDate;

    /** 更新人账号 */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccount;

    @Excel(name = "更新人")
    @ApiModelProperty(value = "更新人账号")
    @TableField(exist = false)
    private String updaterAccountName;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /** 数据源系统 */
    @Excel(name = "数据源系统")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;

    @TableField(exist = false)
    @ApiModelProperty(value = "客户编码")
    private String customerCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "客户名称")
    private String customerName;

}
