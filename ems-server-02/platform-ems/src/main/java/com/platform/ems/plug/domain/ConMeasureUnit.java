package com.platform.ems.plug.domain;

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
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.Date;

/**
 * 计量单位对象 s_con_measure_unit
 *
 * @author linhongwei
 * @date 2021-05-20
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_con_measure_unit")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConMeasureUnit extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-计量单位sid
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-计量单位sid")
    private Long sid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] sidList;
    /**
     * 计量单位编码
     */
    @NotBlank(message = "计量单位编码不能为空")
    @Length(max = 8, message = "计量单位编码不能超过8个字符")
    @Excel(name = "计量单位编码")
    @ApiModelProperty(value = "计量单位编码")
    private String code;

    /**
     * 计量单位名称
     */
    @NotBlank(message = "计量单位名称不能为空")
    @Length(max = 300, message = "计量单位名称不能超过300个字符")
    @Excel(name = "计量单位名称")
    @ApiModelProperty(value = "计量单位名称")
    private String name;

    /**
     * 是否整数型计量单位
     */
    @Excel(name = "整数型计量单位", dictType = "s_yesno_flag")
    @ApiModelProperty(value = "是否整数型计量单位")
    private String isInteger;

    /** 取整方式（数据字典的键值或配置档案的编码） */
    @Excel(name = "BOM用量取整方式", dictType = "s_rounding_type")
    @ApiModelProperty(value = "取整方式（数据字典的键值或配置档案的编码）")
    private String roundingType;


    /** 允许小数位数 */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "允许小数位数")
    private Long decimalNum;

    /** 计量单位类别（数据字典的键值或配置档案的编码） */
    @ApiModelProperty(value = "计量单位类别（数据字典的键值或配置档案的编码）")
    private String category;

    @Excel(name = "备注")
    @ApiModelProperty(value ="备注")
    private String remark;

    /**
     * 启用/停用状态（数据字典的键值）
     */
    @NotEmpty(message = "启停状态不能为空")
    @Excel(name = "启用/停用", dictType = "s_valid_flag")
    @ApiModelProperty(value = "启用/停用状态（数据字典的键值）")
    private String status;

    /**
     * 处理状态（数据字典的键值）
     */
    @NotEmpty(message = "处理状态不能为空")
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值）")
    private String handleStatus;

    /**
     * 创建人账号（用户名称）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人账号（用户名称）")
    @TableField(exist = false)
    private String creatorAccountName;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /**
     * 更新人账号（用户名称）
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 确认人账号（用户名称）
     */
    @ApiModelProperty(value = "确认人账号（用户名称）")
    private String confirmerAccount;

    /**
     * 确认时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 数据源系统（数据字典的键值）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值）")
    private String dataSourceSys;

    /**
     * 是否允许租户自定义
     */
    @ApiModelProperty(value = "是否允许租户自定义")
    private String isClientDefine;

    /**
     * 序号
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "序号")
    private Long sort;


    @ApiModelProperty(value = "查询：取整方式")
    @TableField(exist = false)
    private String[]  roundingTypeList;
}
