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

import javax.validation.constraints.NotBlank;

/**
 * 供应商注册-联系方式信息对象 s_bas_vendor_register_addr
 *
 * @author chenkw
 * @date 2022-02-21
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_bas_vendor_register_addr")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasVendorRegisterAddr extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-供应商注册联系信息sid
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商注册联系信息sid")
    private Long vendorRegisterContactSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] vendorRegisterContactSidList;

    /**
     * 系统SID-供应商注册基本信息
     */
    @Excel(name = "系统SID-供应商注册基本信息")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商注册基本信息")
    private Long vendorRegisterSid;

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
    @NotBlank(message = "联系人姓名不能为空")
    @Length(max = 120, message = "联系人姓名最大只支持输入120位")
    @Excel(name = "联系人姓名")
    @ApiModelProperty(value = "联系人姓名")
    private String contacterName;

    /**
     * 联系人职务
     */
    @Length(max = 60, message = "联系人职务最大只支持输入60位")
    @Excel(name = "联系人职务")
    @ApiModelProperty(value = "联系人职务")
    private String contacterPosition;

    /**
     * 移动电话
     */
    @NotBlank(message = "移动电话不能为空")
    @Length(max = 11, message = "移动电话最大只支持输入11位")
    @Excel(name = "移动电话")
    @ApiModelProperty(value = "移动电话")
    private String contacterMobphone;

    /**
     * 固定电话
     */
    @Length(max = 30, message = "固定电话最大只支持输入30位")
    @Excel(name = "固定电话")
    @ApiModelProperty(value = "固定电话")
    private String contacterTelephone;

    /**
     * 传真
     */
    @Length(max = 18, message = "传真最大只支持输入18位")
    @Excel(name = "传真")
    @ApiModelProperty(value = "传真")
    private String contacterFax;

    /**
     * 电子邮箱
     */
    @Length(max = 50, message = "电子邮箱最大只支持输入50位")
    @Excel(name = "电子邮箱")
    @ApiModelProperty(value = "电子邮箱")
    private String contacterEmail;

    /**
     * 联系地址
     */
    @Length(max = 300, message = "联系地址最大只支持输入300位")
    @Excel(name = "联系地址")
    @ApiModelProperty(value = "联系地址")
    private String contacterAddress;

    /**
     * 所属业务部门
     */
    @Length(max = 30, message = "所属业务部门最大只支持输入30位")
    @Excel(name = "所属业务部门")
    @ApiModelProperty(value = "所属业务部门")
    private String contacterDepartment;

    /**
     * 联系人归属业务类型（数据字典的键值或配置档案的编码），可存储多个键值
     */
    @Excel(name = "归属业务类型", dictType = "s_business_type_contact")
    @ApiModelProperty(value = "联系人归属业务类型（数据字典的键值），可存储多个键值")
    private String businessType;

    /**
     * 是否默认联系人（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "是否默认联系人", dictType = "s_yesno_flag")
    @ApiModelProperty(value = "是否默认联系人（数据字典的键值）")
    private String isDefault;

    @Length(max = 100, message = "联系方说明最大只支持输入100位")
    @ApiModelProperty(value = "联系方说明")
    private String contactPartyRemark;

    @ApiModelProperty(value = "备注")
    private String remark;

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
