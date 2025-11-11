package com.platform.ems.plug.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.core.domain.BaseEntity;
import com.platform.common.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableId;

import java.util.List;

import com.platform.common.core.domain.EmsBaseEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import com.platform.common.core.domain.document.UserOperLog;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

import lombok.experimental.Accessors;

/**
 * 收付款方式组合-支付方式对象 s_con_account_method_group_method
 *
 * @author linhongwei
 * @date 2021-06-10
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_con_account_method_group_method")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConAccountMethodGroupMethod extends EmsBaseEntity {

    @ApiModelProperty(value = "创建人账号（用户名称）")
    @TableField(exist = false)
    private String creatorAccountName;

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-收付款方式组合-支付方式
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-收付款方式组合-支付方式")
    private Long sid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] sidList;
    /**
     * 系统SID-收付款方式组合sid
     */
    @Excel(name = "系统SID-收付款方式组合sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-收付款方式组合sid")
    private Long accountMethodGroupSid;

    /**
     * 款项类别编码（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "款项类别编码（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "款项类别编码 1 预付款 3 中期款  5尾款")
    private String accountCategory;

    /**
     * 支付方式编码（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "支付方式编码（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "支付方式编码（数据字典的键值或配置档案的编码）")
    private String paymentMethod;

    /**
     * 占比（存值，即：不含百分号，如20%，就存0.2）
     */
    @Excel(name = "占比（存值，即：不含百分号，如20%，就存0.2）")
    @ApiModelProperty(value = "占比（存值，即：不含百分号，如20%，就存0.2）")
    private Double rate;

    /**
     * 账期(天)
     */
    @Excel(name = "账期(天)")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "账期(天)")
    private Long accountValidDays;

    /**
     * 账期天类型编码（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "账期天类型编码（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "账期天类型编码（数据字典的键值或配置档案的编码）")
    private String dayType;

    /**
     * 排序
     */
    @Excel(name = "排序")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "排序")
    private Long itemSort;

    /**
     * 创建人账号（用户名称）
     */
    @Excel(name = "创建人账号（用户名称）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
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
     * 更新人账号（用户名称）
     */
    @Excel(name = "更新人账号（用户名称）")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
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
     * 确认人账号（用户名称）
     */
    @Excel(name = "确认人账号（用户名称）")
    @ApiModelProperty(value = "确认人账号（用户名称）")
    private String confirmerAccount;

    /**
     * 确认时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "确认时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "数据源系统（数据字典的键值或配置档案的编码）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    /**
     * 序号
     */
    @Excel(name = "序号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "序号")
    private Long sort;

}
