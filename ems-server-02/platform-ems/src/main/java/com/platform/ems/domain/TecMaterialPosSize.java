package com.platform.ems.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
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
 * 商品尺寸-部位-尺码-尺寸对象 s_tec_material_pos_size
 *
 * @author olive
 * @date 2021-02-21
 */
@Data
@Accessors( chain = true)
@TableName("s_tec_material_pos_size")
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TecMaterialPosSize extends EmsBaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 客户端口号
     */
    @ApiModelProperty(value ="租户id")
    @TableField(fill = FieldFill.INSERT)
    private String clientId;

    @ApiModelProperty(value = "备注")
    private String remark;
    /**
     * 系统ID-商品部位尺码尺寸信息
     */
    @Excel(name = "系统ID-商品部位尺码尺寸信息")
    @TableId
    @ApiModelProperty(value ="部位尺寸id")
    private String materialPosSizeSid;

    /**
     * 系统ID-商品部位信息
     */
    @ApiModelProperty(value ="部位id")
    private String materialPosInforSid;

    /**
     * 系统ID-SKU档案（尺码）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value ="sku档案id")
    private Long skuSid;

    /**
     * 具体尺码对应部位的尺寸值
     */
    @Excel(name = "具体尺码对应部位的尺寸值")
    @ApiModelProperty(value ="尺寸值")
//    @Digits(integer=5,fraction = 2,message = "尺寸值整数位上限为5位，小数位上限为2位")
    private String sizeValue;


    /**
     * 创建人账号
     */
    @Excel(name = "创建人账号")
    @ApiModelProperty(value ="创建人账号")
    @TableField(fill = FieldFill.INSERT)
    private String creatorAccount;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value ="创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createDate;

    /**
     * 更新人账号
     */
    @Excel(name = "更新人账号")
    @ApiModelProperty(value ="更新人账号")
    @TableField(fill = FieldFill.UPDATE)
    private String updaterAccount;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value ="更新时间")
    @TableField(fill = FieldFill.UPDATE)
    private Date updateDate;

    /**
     * 数据源系统
     */
    @Excel(name = "数据源系统")
    @ApiModelProperty(value ="数据源系统")
    private String dataSourceSys;

    @TableField(exist = false)
    @ApiModelProperty(value ="sku编码")
    private String skuCode;

    @TableField(exist = false)
    @ApiModelProperty(value ="sku名称")
    private String skuName;


}
