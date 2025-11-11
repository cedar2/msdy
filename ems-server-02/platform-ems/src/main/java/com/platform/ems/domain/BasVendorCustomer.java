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

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;

/**
 * 供应商的主要客户信息对象 s_bas_vendor_customer
 *
 * @author chenkw
 * @date 2022-01-05
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_bas_vendor_customer")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasVendorCustomer extends EmsBaseEntity {

    /**
     * 客户端口号
     */
    @Excel(name = "客户端口号")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /**
     * 系统ID-供应商的主要客户信息
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-供应商的主要客户信息")
    private Long vendorCustomerSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] vendorCustomerSidList;
    /**
     * 系统ID-供应商档案
     */
    @Excel(name = "系统ID-供应商档案")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-供应商档案")
    private Long vendorSid;

    /**
     * 客户名称
     */
    @NotBlank(message = "主要客户名称不能为空")
    @Excel(name = "客户名称")
    @ApiModelProperty(value = "客户名称")
    private String customerName;

    /**
     * 销售品类
     */
    @NotBlank(message = "主要客户的销售品类不能为空")
    @Excel(name = "销售品类")
    @ApiModelProperty(value = "销售品类")
    private String saleClass;

    /**
     * 业务占比
     */
    @Digits(integer=8,fraction = 2,message = "主要客户业务占比的整数位上限为8位，小数位上限为2位")
    @Excel(name = "业务占比")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "业务占比")
    private Long businessScale;

    /**
     * 合作方式
     */
    @Excel(name = "合作方式")
    @ApiModelProperty(value = "合作方式")
    private String cooperatMethod;

    /**
     * 付款方式
     */
    @Excel(name = "付款方式")
    @ApiModelProperty(value = "付款方式")
    private String paymentMethod;

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
