package com.platform.ems.domain;

import java.util.Date;
import java.util.List;

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
 * 工厂-生产线信息对象 s_bas_plant_prod_line
 *
 * @author linhongwei
 * @date 2021-03-27
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_bas_plant_prod_line")
public class BasPlantProdLine extends EmsBaseEntity {

    /** 客户端口号 */
        @Excel(name = "客户端口号")
        @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /** 系统ID-工厂生产线信息 */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-工厂生产线信息")
    private Long productLineSid;

    /** 系统ID-工厂档案 */
        @Excel(name = "系统ID-工厂档案")
        @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-工厂档案")
    private Long plantSid;

    /** 产线名称 */
        @Excel(name = "产线名称")
        @ApiModelProperty(value = "产线名称")
    private String productLineName;

    /** 生产品类 */
        @Excel(name = "生产品类")
        @ApiModelProperty(value = "生产品类")
    private String productCategory;

    /** 产线工人数 */
        @Excel(name = "产线工人数")
        @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "产线工人数")
    private Long workNumber;

    /** 预计日均产量 */
        @Excel(name = "预计日均产量")
        @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "预计日均产量")
    private Long perdayOutput;

    /** 产线联系人 */
        @Excel(name = "产线联系人")
        @ApiModelProperty(value = "产线联系人")
    private String name;

    /** 联系人职务 */
        @Excel(name = "联系人职务")
        @ApiModelProperty(value = "联系人职务")
    private String position;

    /** 联系电话 */
        @Excel(name = "联系电话")
        @ApiModelProperty(value = "联系电话")
    private String phone;

    /** 传真 */
        @Excel(name = "传真")
        @ApiModelProperty(value = "传真")
    private String fax;

    /** 电子邮箱 */
        @Excel(name = "电子邮箱")
        @ApiModelProperty(value = "电子邮箱")
    private String email;

    /** 产线地址 */
        @Excel(name = "产线地址")
        @ApiModelProperty(value = "产线地址")
    private String address;

    /** 富余产能说明 */
        @Excel(name = "富余产能说明")
        @ApiModelProperty(value = "富余产能说明")
    private String surplusCapacityD;

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

    /** 对外共享产能信息标识（数据字典的键值） */
        @Excel(name = "对外共享产能信息标识（数据字典的键值）")
        @ApiModelProperty(value = "对外共享产能信息标识（数据字典的键值）")
    private String shareFlag;


    private String remark;

    @TableField(exist = false)
    private String beginTime;

    @TableField(exist = false)
    private String endTime;

    /**
     * 工厂-富余产能明细对象
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "工厂-富余产能明细对象")
    private List<BasPlantCapacity> basPlantCapacityList;
}
