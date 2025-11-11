package com.platform.ems.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import lombok.experimental.Accessors;

/**
 * 商品尺寸-部位-尺码-尺寸（下装）对象 s_tec_material_pos_size_down
 *
 * @author linhongwei
 * @date 2021-04-27
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_tec_material_pos_size_down")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TecMaterialPosSizeDown  extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-商品部位尺码尺寸信息（下装）
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品部位尺码尺寸信息（下装）")
    private Long materialPosSizeSid;

    /**
     * 系统SID-商品部位信息（下装）
     */
    @Excel(name = "系统SID-商品部位信息（下装）")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品部位信息（下装）")
    private Long materialPosInforSid;

    /**
     * 系统SID-SKU档案（尺码）（下装）
     */
    @Excel(name = "系统SID-SKU档案（尺码）（下装）")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-SKU档案（尺码）（下装）")
    private Long skuSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-SKU档案（尺码）（下装）")
    private String skuCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-SKU档案（尺码）（下装）")
    private String skuName;

    /**
     * 具体尺码对应部位的尺寸值（下装）
     */
    @Excel(name = "具体尺码对应部位的尺寸值（下装）")
    @ApiModelProperty(value = "具体尺码对应部位的尺寸值（下装）")
//    @Digits(integer=5,fraction = 2,message = "尺寸值整数位上限为5位，小数位上限为2位")
    private String sizeValue;

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
     * 数据源系统（数据字典的键值）
     */
    @Excel(name = "数据源系统（数据字典的键值）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值）")
    private String dataSourceSys;


    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "创建日期开始时间")
    @TableField(exist = false)
    private String beginTime;

    @ApiModelProperty(value = "创建日期结束时间")
    @TableField(exist = false)
    private String endTime;

    @ApiModelProperty(value = "页数")
    @TableField(exist = false)
    private Integer pageNum;

    @ApiModelProperty(value = "每页个数")
    @TableField(exist = false)
    private Integer pageSize;


}
