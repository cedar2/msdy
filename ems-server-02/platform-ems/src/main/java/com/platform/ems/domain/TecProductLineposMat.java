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

import javax.validation.Valid;
import javax.validation.constraints.Digits;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 商品线部位-线料对象 s_tec_product_linepos_mat
 *
 * @author linhongwei
 * @date 2021-10-21
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_tec_product_linepos_mat")
public class TecProductLineposMat extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-商品线部位-线料
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品线部位-线料")
    private Long lineposMatSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] lineposMatSidList;

    /**
     * 系统SID-商品线信息
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品线信息")
    private Long productLineSid;

    /**
     * 系统SID-商品档案
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品档案")
    private Long productSid;

    /**
     * 系统SID-线部位档案
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-线部位档案")
    private Long linePositionSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "线部位sids")
    private List<Long> linePositionSids;

    /**
     * 系统SID-线物料档案
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-线物料档案")
    private Long materialSid;

    /**
     * 用量
     */
    @Digits(integer = 3,fraction = 1,message = "用量整数位上限为3位，小数位上限为1位")
    @Excel(name = "用量")
    @ApiModelProperty(value = "用量")
    private BigDecimal quantity;

    /**
     * 线部位类别（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "线部位类别（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "线部位类别（数据字典的键值或配置档案的编码）")
    private String linePositionCategory;

    /**
     * 用量计量单位（数据字典的键值或配置档案的编码）
     */
//    @NotEmpty(message = "BOM用量单位不能为空")
    @Excel(name = "用量计量单位（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "用量计量单位（数据字典的键值或配置档案的编码）")
    private String quantityUnit;

    /**
     * 线部位名称
     */
    @Excel(name = "线部位名称")
    @ApiModelProperty(value = "线部位名称")
    private String linePositionName;

    /**
     * 序号
     */
    @Excel(name = "序号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "序号")
    private Long serialNum;

    /**
     * 度量方法说明
     */
    @Excel(name = "度量方法说明")
    @ApiModelProperty(value = "度量方法说明")
    private String measureDescription;

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
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "数据源系统（数据字典的键值或配置档案的编码）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    @TableField(exist = false)
    @ApiModelProperty(value = "线部位编码")
    private String linePositionCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "用量计量单位名称")
    private String quantityUnitName;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料编码")
    private String materialCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料名称")
    private String materialName;

    @TableField(exist = false)
    @ApiModelProperty(value = "基本计量单位名称")
    private String unitBaseName;

    @Valid
    @TableField(exist = false)
    @ApiModelProperty(value = "商品线部位-款色线色")
    private List<TecProductLineposMatColor> tecProductLineposMatColorList;
}
