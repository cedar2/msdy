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
 * 商品SKU实测成分对象 s_bas_material_sku_component
 *
 * @author linhongwei
 * @date 2021-03-20
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_bas_material_sku_component")
public class BasMaterialSkuComponent extends EmsBaseEntity {

    /** 客户端口号 */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /** 系统ID-商品SKU实测成分信息 */
    @TableId
    @Excel(name = "系统ID-商品SKU实测成分信息")
    @ApiModelProperty(value = "系统ID-商品SKU实测成分信息")
    private String materialSkuComponentSid;

    /** 系统ID-商品档案 */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-商品档案")
    private Long materialSid;

    /** 系统ID-公司档案 */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-公司档案")
    private Long companySid;

    /** 系统ID-SKU档案 */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-SKU档案")
    private Long skuSid;

    /** SKU类型编码（颜色） */
    @ApiModelProperty(value = "SKU类型编码（颜色）")
    private String skuType;

    /** 实测成分 */
    @Excel(name = "实测成分")
    @ApiModelProperty(value = "实测成分")
    private String actualComponent;

    /** 下装实测成分 */
    @Excel(name = "下装实测成分")
    @ApiModelProperty(value = "下装实测成分")
    private String bottomsActualComponent;

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

    /** 实测成分（面料） */
    @Excel(name = "实测成分（面料）")
    @ApiModelProperty(value = "实测成分（面料）")
    private String componentOutFabric;

    /** 实测成分（里料） */
    @Excel(name = "实测成分（里料）")
    @ApiModelProperty(value = "实测成分（里料）")
    private String componentInFabric;

    /** 实测成分（填充物） */
    @Excel(name = "实测成分（填充物）")
    @ApiModelProperty(value = "实测成分（填充物）")
    private String componentPadding;

    /** 下装实测成分（面料） */
    @Excel(name = "下装实测成分（面料）")
    @ApiModelProperty(value = "下装实测成分（面料）")
    private String bottomsComponentOutFabric;

    /** 实测成分（里料） */
    @Excel(name = "实测成分（里料）")
    @ApiModelProperty(value = "实测成分（里料）")
    private String bottomsComponentInFabric;

    /** 下装实测成分（填充物） */
    @Excel(name = "下装实测成分（填充物）")
    @ApiModelProperty(value = "下装实测成分（填充物）")
    private String bottomsComponentPadding;

}
