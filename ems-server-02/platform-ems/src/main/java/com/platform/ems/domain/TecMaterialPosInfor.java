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

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 商品尺寸-部位对象 s_tec_material_pos_infor
 *
 * @author olive
 * @date 2021-02-21
 */
@Data
@TableName("s_tec_material_pos_infor")
@ApiModel
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TecMaterialPosInfor  extends EmsBaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 客户端口号
     */
    @ApiModelProperty(value ="客户端口号")
    @TableField(fill = FieldFill.INSERT)
    private String clientId;

    /**
     * 系统ID-商品部位信息
     */
    @ApiModelProperty(value ="商品尺寸部位id")
    @Excel(name = "系统ID-商品部位信息")
    @TableId
    private String materialPosInforSid;

    @ApiModelProperty(value ="版型部位code")
    @TableField(exist = false)
    private String modelPositionCode;

    @ApiModelProperty(value ="版型部位名称")
    @TableField(exist = false)
    private String modelPositionName;

    @ApiModelProperty(value ="度量方法说明")
    @TableField(exist = false)
    private String measureDescription;

    /**
     * 系统ID-商品尺寸表信息
     */
    @ApiModelProperty(value ="尺寸表id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long materialSizeSid;

    @ApiModelProperty(value ="备注")
    private String remark;
    /**
     * 系统ID-商品部位档案（同版型部位）
     */
    @ApiModelProperty(value ="版型部位id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long modelPositionSid;

    /**
     * 公差（±）
     */
    @Excel(name = "公差", readConverterExp = "±=")
    @ApiModelProperty(value ="公差")
    @NotNull(message = "公差不能为空")
    private BigDecimal deviation;

    /** 公差（-） */
    @Excel(name = "公差-", readConverterExp = "±=")
    @ApiModelProperty(value ="公差 -")
    private BigDecimal deviationMinus;

    /**
     * 序号
     */
    @Excel(name = "序号")
    @ApiModelProperty(value ="序号")
    private Long serialNum;

    /**
     * 计量单位编码
     */
    @Excel(name = "计量单位编码")
    @ApiModelProperty(value ="计量单位编码")
    private String unit;



    /**
     * 创建人账号
     */
    @Excel(name = "创建人账号")
    @ApiModelProperty(value ="创建人账号")
    @TableField(fill = FieldFill.INSERT)
    private String creatorAccount;

    @Excel(name = "创建人")
    @ApiModelProperty(value ="创建人")
    @TableField(exist = false)
    private String creatorAccountName;

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
    @ApiModelProperty(value ="更新时间")
    @TableField(fill = FieldFill.UPDATE)
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date updateDate;

    /**
     * 数据源系统
     */
    @Excel(name = "数据源系统")
    @ApiModelProperty(value ="数据源系统")
    @TableField(fill = FieldFill.INSERT)
    private String dataSourceSys;

    @TableField(exist = false)
    @ApiModelProperty(value ="部位尺寸列表")
    @Valid
    private List<TecMaterialPosSize> posSizeList;


}
