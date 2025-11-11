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
import com.platform.common.core.domain.BaseEntity;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;


import java.util.Date;


/**
 * 产品季档案对象 s_bas_product_season
 *
 * @author ruoyi
 * @date 2021-01-21
 */
@Data
@TableName("s_bas_product_season")
@ApiModel
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SBasProductSeason  extends EmsBaseEntity {

    @ApiModelProperty(value = "客户端口号")
    @Excel(name = "客户端口号")
    @TableField(fill = FieldFill.INSERT)
    private String clientId;

    /** 系统ID-产品季档案 */
    @Excel(name = "系统ID-产品季档案")
    @ApiModelProperty(value = "系统ID-产品季档案")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableId
    private Long productSeasonSid;

    /** 产品季编码 */
    @Excel(name = "产品季编码")
    @ApiModelProperty(value = "产品季编码")
    private String productSeasonCode;

    /** 产品季名称 */
    @Excel(name = "产品季名称")
    @ApiModelProperty(value = "产品季名称")
    private String productSeasonName;

    /** 年份（年份的编码） */
    @ApiModelProperty(value = "年=份的编码")
    @Excel(name = "年份", readConverterExp = "年=份的编码")
    private String year;

    /** 季度（季度的编码） */
    @Excel(name = "季度", readConverterExp = "季=度的编码")
    @ApiModelProperty(value = "季=度的编码")
    private String seasonCode;

    /** 公司（公司档案的sid） */
    @Excel(name = "公司", readConverterExp = "公=司档案的sid")
    @ApiModelProperty(value = "公司（公司档案的sid）")
    private String companySid;

    /** 产品季所属阶段编码 */
    @Excel(name = "产品季所属阶段编码")
    @ApiModelProperty(value = "产品季所属阶段编码")
    private String productSeasonStage;

    /** 备注 */
    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    /** 启用/停用状态 */
    @ApiModelProperty(value = "启用/停用状态")
    @Excel(name = "启用/停用状态")
    private String status;

    /** 处理状态 */
    @Excel(name = "处理状态")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    /** 创建人账号 */
    @Excel(name = "创建人账号")
    @ApiModelProperty(value = "创建人账号")
    @TableField(fill = FieldFill.INSERT)
    private String creatorAccount;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createDate;

    /** 更新人账号 */
    @Excel(name = "更新人账号")
    @ApiModelProperty(value = "更新人账号")
    @TableField(fill = FieldFill.UPDATE)
    private String updaterAccount;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.UPDATE)
    private Date updateDate;

    /** 确认人账号 */
    @Excel(name = "确认人账号")
    @ApiModelProperty(value = "确认人账号")
    private String confirmerAccount;

    /** 确认时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "确认时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /** 数据源系统 */
    @Excel(name = "数据源系统")
    @ApiModelProperty(value = "数据源系统")
    @TableField(fill = FieldFill.INSERT)
    private String dataSourceSys;


}
