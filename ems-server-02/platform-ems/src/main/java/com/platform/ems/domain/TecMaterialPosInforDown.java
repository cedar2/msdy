package com.platform.ems.domain;

import java.math.BigDecimal;
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

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import lombok.experimental.Accessors;

/**
 * 商品尺寸-部位（套装的下装）对象 s_tec_material_pos_infor_down
 *
 * @author linhongwei
 * @date 2021-04-27
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_tec_material_pos_infor_down")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TecMaterialPosInforDown  extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-商品部位信息（套装的下装）
     */
    @Excel(name = "系统SID-商品部位信息（套装的下装）")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品部位信息（套装的下装）")
    @TableId
    private Long materialPosInforSid;

    /**
     * 系统SID-商品尺寸表信息
     */
    @Excel(name = "系统SID-商品尺寸表信息")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品尺寸表信息")
    private Long materialSizeSid;

    /**
     * 系统SID-版型部位档案（套装的下装）
     */
    @Excel(name = "系统SID-版型部位档案（套装的下装）")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-版型部位档案（套装的下装）")
    private Long modelPositionSid;

    /**
     * 版型部位名称（套装的下装）
     */
    @Excel(name = "版型部位名称（套装的下装）")
    @ApiModelProperty(value = "版型部位名称（套装的下装）")
    private String modelPositionName;

    @TableField(exist = false)
    @Excel(name = "版型部位名称（套装的下装）")
    @ApiModelProperty(value = "版型部位名称（套装的下装）")
    private String modelPositionCode;

    /**
     * 度量方法说明（套装的下装）
     */
    @Excel(name = "度量方法说明（套装的下装）")
    @ApiModelProperty(value = "度量方法说明（套装的下装）")
    private String measureDescription;

    /**
     * 公差（+）
     */
    @Excel(name = "公差（+）")
    @ApiModelProperty(value = "公差（+）")
    private BigDecimal deviation;

    /**
     * 公差（-）
     */
    @Excel(name = "公差（-）")
    @ApiModelProperty(value = "公差（-）")
    private BigDecimal deviationMinus;

    /**
     * 序号
     */
    @Excel(name = "序号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "序号")
    private Long serialNum;

    /**
     * 计量单位（数据字典的键值）
     */
    @Excel(name = "计量单位（数据字典的键值）")
    @ApiModelProperty(value = "计量单位（数据字典的键值）")
    private String unit;

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

    @TableField(exist = false)
    @ApiModelProperty(value ="部位尺寸列表")
    @Valid
    private List<TecMaterialPosSizeDown> posSizeDownList;

}
