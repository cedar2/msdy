package com.platform.ems.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

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
 * 版型-部位信息对象 s_tec_model_pos_infor
 *
 * @author linhongwei
 * @date 2021-02-08
 */
@Data
@Accessors( chain = true)
@TableName("s_tec_model_pos_infor")
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TecModelPosInfor  extends EmsBaseEntity {
    private static final long serialVersionUID = 1L;

    /** 客户端口号 */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value ="客户端口号")
    private String clientId;

    /** 系统ID-版型部位信息 */
    @Excel(name = "系统ID-版型部位信息")
    @TableId
    @ApiModelProperty(value ="系统ID-版型部位信息")
    private String modelPositionInforSid;

    /** 系统ID-版型档案 */
    @ApiModelProperty(value ="系统ID-版型档案 ")
    private Long modelSid;

    /** 系统ID-版型部位档案 */
    @Excel(name = "系统ID-版型部位档案")
    @ApiModelProperty(value ="系统ID-版型部位档案 ")
    private String modelPositionSid;

    /** 公差（+） */
    @Excel(name = "公差+", readConverterExp = "±=")
    @ApiModelProperty(value ="公差 +")
    private BigDecimal deviation;

    @ApiModelProperty(value = "备注")
    private String remark;
    /** 公差（-） */
    @Excel(name = "公差-", readConverterExp = "±=")
    @ApiModelProperty(value ="公差 -")
    private BigDecimal deviationMinus;

    /** 序号 */
    @Excel(name = "序号")
    @ApiModelProperty(value ="序号")
    private Long serialNum;

    /** 计量单位编码 */
    @Excel(name = "计量单位编码")
    @ApiModelProperty(value ="计量单位编码")
    private String unit;

    /** 创建人账号 */
    @Excel(name = "创建人账号")
    @ApiModelProperty(value ="创建人账号")
    @TableField(fill = FieldFill.INSERT)
    private String creatorAccount;

    @Excel(name = "创建人")
    @ApiModelProperty(value ="创建人")
    @TableField(exist = false)
    private String creatorAccountName;

    /** 创建时间 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value ="创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createDate;

    /** 更新人账号 */
    @Excel(name = "更新人账号")
    @ApiModelProperty(value ="更新人账号")
    @TableField(fill = FieldFill.UPDATE)
    private String updaterAccount;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value ="更新时间")
    @TableField(fill = FieldFill.UPDATE)
    private Date updateDate;

    /** 数据源系统 */
    @Excel(name = "数据源系统")
    @ApiModelProperty(value ="数据源系统")
    @TableField(fill = FieldFill.INSERT)
    private String dataSourceSys;

    @TableField(exist = false)
    @ApiModelProperty(value ="版型部位编码")
    private String modelPositionCode;
    @TableField(exist = false)
    @ApiModelProperty(value ="版型部位名称")
    private String modelPositionName;
    @TableField(exist = false)
    @ApiModelProperty(value ="度量方法说明")
    private String measureDescription;

    @TableField(exist = false)
    @ApiModelProperty("尺寸详情列表")
    private List<TecModelPosSize> posSizeList;

}
