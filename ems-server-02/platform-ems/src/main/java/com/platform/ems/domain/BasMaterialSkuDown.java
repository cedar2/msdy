package com.platform.ems.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
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
 * 商品SKU羽绒充绒量对象 s_bas_material_sku_down
 *
 * @author linhongwei
 * @date 2021-03-20
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_bas_material_sku_down")
public class BasMaterialSkuDown extends EmsBaseEntity {

    /** 客户端口号 */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /** 系统ID-商品档案 */
    @Excel(name = "系统ID-商品档案")
    @TableId
    @ApiModelProperty(value = "系统ID-商品档案")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long materialSkuDownSid;

    /** 系统ID-商品档案 */
    @Excel(name = "系统ID-商品档案")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-商品档案")
    private Long materialSid;

    /** 系统ID-公司档案 */
    @Excel(name = "系统ID-公司档案")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-公司档案")
    private Long companySid;

    /** 系统ID-SKU档案 */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-SKU档案")
    private Long skuSid;

    /** SKU类型编码（尺码） */
    @ApiModelProperty(value = "SKU类型编码（尺码）")
    private String skuType;

    /** 克重 */
    @Excel(name = "克重")
    @ApiModelProperty(value = "克重")
    private String gramWeight;

    /** 备注 */
    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    /** 创建人账号 */
    @Excel(name = "创建人账号")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
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
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /** 数据源系统 */
    @Excel(name = "数据源系统")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;

    /** SKU编码 */
    @TableField(exist = false)
    @ApiModelProperty(value = "SKU编码")
    private String skuCode;

    /** SKU名称 */
    @TableField(exist = false)
    @ApiModelProperty(value = "SKU名称")
    private String skuName;


}
