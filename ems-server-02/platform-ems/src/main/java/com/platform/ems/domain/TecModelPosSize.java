package com.platform.ems.domain;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 版型-部位-尺码-尺寸对象 s_tec_model_pos_size
 *
 * @author linhongwei
 * @date 2021-02-08
 */
@ApiModel
@Data
@TableName(value = "s_tec_model_pos_size")
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TecModelPosSize extends EmsBaseEntity {

    private static final long serialVersionUID = 1L;

    @TableField(exist = false)
    private String firstSort;

    @TableField(exist = false)
    private String secondSort;

    @TableField(exist = false)
    private String thirdSort;

    /**
     * 客户端口号
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /**
     * 系统ID-版型部位尺码尺寸信息
     */
    @Excel(name = "系统ID-版型部位尺码尺寸信息")
    @TableId
    @ApiModelProperty(value = "系统ID-版型部位尺码尺寸信息")
    private String modelPositionSizeSid;

    @ApiModelProperty(value = "备注")
    private String remark;
    /**
     * 系统ID-版型部位信息
     */
    @ApiModelProperty(value = "系统ID-版型部位信息")
    private String modelPositionInforSid;

    /**
     * 系统ID-SKU档案（尺码）
     */
    @Excel(name = "系统ID-SKU档案", readConverterExp = "尺=码")
    @ApiModelProperty(value = "系统ID-SKU档案（尺码）")
    private String skuSid;

    /**
     * 具体尺码对应部位的尺寸值
     */
    @ApiModelProperty(value = "具体尺码对应部位的尺寸值")
    @Excel(name = "具体尺码对应部位的尺寸值")
//    @Digits(integer=5,fraction = 2,message = "尺寸值整数位上限为5位，小数位上限为2位")
    private String sizeValue;

    /**
     * 创建人账号
     */
    @Excel(name = "创建人账号")
    @ApiModelProperty(value = "创建人账号")
    @TableField(fill = FieldFill.INSERT)
    private String creatorAccount;

    @TableField(exist = false)
    private String skuCode;

    @TableField(exist = false)
    private String skuName;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createDate;

    /**
     * 更新人账号
     */
    @Excel(name = "更新人账号")
    @ApiModelProperty(value = "更新人账号")
    @TableField(fill = FieldFill.UPDATE)
    private String updaterAccount;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.UPDATE)
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date updateDate;

    /**
     * 数据源系统
     */
    @Excel(name = "数据源系统")
    @ApiModelProperty(value = "数据源系统")
    @TableField(fill = FieldFill.INSERT)
    private String dataSourceSys;

}
