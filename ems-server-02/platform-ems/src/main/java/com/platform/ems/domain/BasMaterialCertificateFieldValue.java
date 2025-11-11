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
 * 商品合格证洗唛自定义字段-值对象 s_bas_material_certificate_field_value
 *
 * @author linhongwei
 * @date 2021-03-20
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_bas_material_certificate_field_value")
public class BasMaterialCertificateFieldValue extends EmsBaseEntity {

    /** 客户端口号 */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /** 系统ID-合格证洗唛字段自定义 */
    @TableId
    @Excel(name = "系统ID-合格证洗唛字段自定义")
    @ApiModelProperty(value = "系统ID-合格证洗唛字段自定义")
    private Long materialCertificateFieldValueSid;

    /** 系统ID-合格证洗唛字段自定义 */
    @Excel(name = "系统ID-合格证洗唛字段自定义")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-合格证洗唛字段自定义")
    private Long materialCertificateFieldSid;

    /** 系统ID-商品档案 */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-商品档案")
    private Long materialSid;

    /** 系统ID-公司档案 */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-公司档案")
    private Long companySid;

    /** 字段名 */
    @ApiModelProperty(value = "字段名")
    private String fieldName;

    /** 字段值 */
    @Excel(name = "字段值")
    @ApiModelProperty(value = "字段值")
    private String fieldValue;

    /** 字段值 */
    @Excel(name = "是否可为空（数据字典的键值）")
    @ApiModelProperty(value = "是否可为空（数据字典的键值）")
    private String isNullFlag;

    /** 是否打印（数据字典的键值） */
    @ApiModelProperty(value = "是否打印（数据字典的键值）")
    private String isPrint;

    /** 字段描述 */
    @Excel(name = "字段描述")
    @ApiModelProperty(value = "字段描述")
    private String fieldDesc;

    /** 字段类型 */
    @Excel(name = "字段类型")
    @ApiModelProperty(value = "字段类型")
    private String fieldType;

    /** 字段字符长度 */
    @Excel(name = "字段字符长度")
    @ApiModelProperty(value = "字段字符长度")
    private String fieldTextLength;

    /** 小数位数 */
    @Excel(name = "小数位数")
    @ApiModelProperty(value = "小数位数")
    private String fieldDecimalLength;

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

}
