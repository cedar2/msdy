package com.platform.ems.domain;

import java.math.BigDecimal;
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
 * 供应商注册-主要客户信息对象 s_bas_vendor_register_customer
 *
 * @author chenkw
 * @date 2022-02-21
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_bas_vendor_register_customer")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasVendorRegisterCustomer extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-供应商注册主要客户信息
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商注册主要客户信息")
    private Long vendorRegisterCustomerSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] vendorRegisterCustomerSidList;

    /**
     * 系统SID-供应商注册基本信息
     */
    @Excel(name = "系统SID-供应商注册基本信息")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商注册基本信息")
    private Long vendorRegisterSid;

    /**
     * 供应商的客户名称
     */
    @NotBlank(message = "供应商的客户名称不能为空")
    @Length(max = 180,message = "供应商的客户名称最大只支持输入180位")
    @Excel(name = "供应商的客户名称")
    @ApiModelProperty(value = "供应商的客户名称")
    private String customerName;

    /**
     * 销售品类
     */
    @Length(max = 300,message = "销售品类最大只支持输入300位")
    @Excel(name = "销售品类")
    @ApiModelProperty(value = "销售品类")
    private String saleClass;

    /**
     * 业务占比（%）
     */
    @Digits(integer = 2, fraction = 3, message = "业务占比整数位上限为2位，小数位上限为3位")
    @Excel(name = "业务占比（%）")
    @ApiModelProperty(value = "业务占比（%）")
    private BigDecimal businessScale;

    /**
     * 合作方式
     */
    @Length(max = 180,message = "合作方式最大只支持输入180位")
    @Excel(name = "合作方式")
    @ApiModelProperty(value = "合作方式")
    private String cooperatMethod;

    /**
     * 付款方式
     */
    @Length(max = 180,message = "付款方式最大只支持输入180位")
    @Excel(name = "付款方式")
    @ApiModelProperty(value = "付款方式")
    private String paymentMethod;

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
