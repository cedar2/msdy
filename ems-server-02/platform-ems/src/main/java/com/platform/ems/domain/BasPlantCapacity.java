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
 * 工厂-富余产能明细对象 s_bas_plant_capacity
 *
 * @author linhongwei
 * @date 2021-03-27
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_bas_plant_capacity")
public class BasPlantCapacity extends EmsBaseEntity {

    /** 客户端口号 */
        @Excel(name = "客户端口号")
        @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /** 系统ID-工厂富余产能明细 */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-工厂富余产能明细")
    private Long plantOvercapacitySid;

    /** 系统ID-工厂生产线信息 */
        @Excel(name = "系统ID-工厂生产线信息")
        @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-工厂生产线信息")
    private Long productLineSid;

    /** 日期从 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "日期从", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "日期从")
    private Date startDate;

    /** 日期至 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "日期至", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "日期至")
    private Date endDate;

    /** 产能是否富余 */
        @Excel(name = "产能是否富余")
        @ApiModelProperty(value = "产能是否富余")
    private String isOvercapacity;

    /** 生产品类 */
        @Excel(name = "生产品类")
        @ApiModelProperty(value = "生产品类")
    private String productCategory;

    /** 富余产量 */
        @Excel(name = "富余产量")
        @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "富余产量")
    private Long spareOutput;

    /** 计量单位编码 */
        @Excel(name = "计量单位编码")
        @ApiModelProperty(value = "计量单位编码")
    private String unit;

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
