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

import javax.validation.constraints.NotEmpty;

/**
 * 采购货源供方SKU编码对象 s_pur_material_sku_vencode
 *
 * @author linhongwei
 * @date 2021-03-29
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_pur_material_sku_vencode")
public class PurMaterialSkuVencode extends EmsBaseEntity {

    /**
     * 客户端口号
     */
    @Excel(name = "客户端口号")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /**
     * 系统ID-物料&商品SKU供方编码
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-物料&商品SKU供方编码")
    private Long materialVendorSkuSid;

    /**
     * 系统ID-货源清单信息
     */
    @Excel(name = "系统ID-货源清单信息")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-货源清单信息")
    private Long purchaseSourceSid;

    /**
     * SKU类型编码
     */
    @Excel(name = "SKU类型编码")
    @ApiModelProperty(value = "SKU类型编码")
    private String skuType;

    /**
     * 系统ID-SKU档案
     */
    @Excel(name = "系统ID-SKU档案")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-SKU档案")
    private Long skuSid;

    /**
     * 供方SKU编码
     */
    @Excel(name = "供方SKU编码")
    @ApiModelProperty(value = "供方SKU编码")
    private String vendorSkuCode;

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

    /**
     * SKU编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "SKU编码")
    private String skuCode;

    /**
     * SKU名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "SKU名称")
    private String skuName;


    private String remark;

    @TableField(exist = false)
    private String beginTime;

    @TableField(exist = false)
    private String endTime;


}
