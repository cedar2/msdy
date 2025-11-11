package com.platform.ems.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableId;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 商品合格证洗唛自定义字段对象 s_bas_material_certificate_field
 *
 * @author linhongwei
 * @date 2021-03-31
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_bas_material_certificate_field")
public class BasMaterialCertificateField extends EmsBaseEntity {

    /**
     * 客户端口号
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /**
     * 系统ID-合格证洗唛字段自定义
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-合格证洗唛字段自定义")
    private Long materialCertificateFieldSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统ID-合格证洗唛字段自定义")
    private Long[] materialCertificateFieldSidList;

    /**
     * 字段名
     */
    @Excel(name = "字段名")
    @ApiModelProperty(value = "字段名")
    private String fieldName;

    /**
     * 字段描述
     */
    @Excel(name = "字段注释")
    @ApiModelProperty(value = "字段描述")
    private String fieldDesc;

    /**
     * 字段类型
     */
    @Excel(name = "字段类型", dictType = "s_table_field_type")
    @ApiModelProperty(value = "字段类型")
    private String fieldType;

    @TableField(exist = false)
    @ApiModelProperty(value = "字段类型 多选")
    private String[] fieldTypeList;

    /**
     * 字段字符长度
     */
    @Excel(name = "字段字符长度")
    @ApiModelProperty(value = "字段字符长度")
    private Integer fieldTextLength;

    /**
     * 小数位数
     */
    @Excel(name = "小数位数")
    @ApiModelProperty(value = "小数位数")
    private Integer fieldDecimalLength;

    /**
     * 是否可为空（数据字典的键值）
     */
    @Excel(name = "是否可为空", dictType = "s_yesno_flag")
    @ApiModelProperty(value = "是否可为空（数据字典的键值）")
    private String isNullFlag;

    /**
     * 公司编码（公司档案的sid）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司编码（公司档案的sid）")
    private Long companySid;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司编码（公司档案的sid）")
    private String companyName;

    /**
     * 是否打印（数据字典的键值）
     */
    @Excel(name = "是否打印", dictType = "s_yesno_flag")
    @ApiModelProperty(value = "是否打印（数据字典的键值）")
    private String isPrint;

    /**
     * 启用/停用状态
     */
    @Excel(name = "启用/停用", dictType = "s_valid_flag")
    @ApiModelProperty(value = "启用/停用状态")
    private String status;

    /**
     * 处理状态
     */
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态")
    private String[] handleStatusList;

    /**
     * 创建人账号
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    @TableField(exist = false)
    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人账号")
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
     * 更新人账号
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccount;

    @TableField(exist = false)
    @Excel(name = "更新人")
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccountName;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更新日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 确认人账号
     */
    @ApiModelProperty(value = "确认人账号")
    private String confirmerAccount;

    @TableField(exist = false)
    @Excel(name = "确认人")
    @ApiModelProperty(value = "确认人账号")
    private String confirmerAccountName;

    /**
     * 确认时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "确认日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 数据源系统
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;


}
