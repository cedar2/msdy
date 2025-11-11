package com.platform.ems.domain;

import com.baomidou.mybatisplus.annotation.*;
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
import java.util.List;

/**
 * 商品-上下装尺码对照对象 s_bas_material_bottoms
 *
 * @author linhongwei
 * @date 2021-03-14
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_bas_material_bottoms")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasMaterialBottoms extends EmsBaseEntity {

    /** 客户端口号 */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /** 系统ID-上下装尺寸对照表 */
    @ApiModelProperty(value = "系统ID-上下装尺寸对照表")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableId
    private Long bottomsSkuSid;

    /** 类型 */
    @ApiModelProperty(value = "类型")
    @Excel(name = "类型")
    private String bottomsType;

    /** 号型版型编码 */
    @ApiModelProperty(value = "号型版型编码")
    @Excel(name = "号型版型编码")
    private String sizePatternType;

    /** 尺码编码 */
    @ApiModelProperty(value = "尺码")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long skuSid;

    @ApiModelProperty(value = "尺码编码")
    @Excel(name = "尺码编码")
    @TableField(exist = false)
    private String skuCode;


    @TableField(exist = false)
    @ApiModelProperty(value = "尺码名称1")
    @Excel(name = "尺码名称1")
    private String skuName1;

    @TableField(exist = false)
    @ApiModelProperty(value = "尺码名称2")
    @Excel(name = "尺码名称2")
    private String skuName2;

    /** 套装下身尺码sid */
    @ApiModelProperty(value = "套装下身尺码sid")
    private Long sizeBottoms;

    @Excel(name = "套装下身尺码")
    @TableField(exist = false)
    @ApiModelProperty(value = "套装下身尺码")
    private String sizeBottomsName;

    @TableField(exist = false)
    @ApiModelProperty(value = "套装下身尺码编码")
    private String sizeBottomsCode;

    @Excel(name = "套装下身尺码2")
    @TableField(exist = false)
    @ApiModelProperty(value = "套装下身尺码2")
    private String sizeBottomsName2;

    /** 启用/停用状态 */
    @Excel(name = "启用/停用状态", dictType = "s_valid_flag")
    @ApiModelProperty(value = "启用/停用状态")
    private String status;

    /** 处理状态 */
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    /** 创建人账号 */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    @Excel(name = "创建人")
    @TableField(exist = false)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccountName;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;


    /** 更新人账号 */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccount;

    @Excel(name = "更新人")
    @TableField(exist = false)
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccountName;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /** 更新人账号 */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "确认人账号")
    private String confirmerAccount;

    @Excel(name = "确认人")
    @TableField(exist = false)
    @ApiModelProperty(value = "确认人账号")
    private String confirmerAccountName;

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

    /** 公司编码（公司档案的sid） */
    @Excel(name = "公司编码（公司档案的sid）")
    @ApiModelProperty(value = "公司编码（公司档案的sid）")
    private Long companySid;

    @TableField(exist = false)
    @Excel(name = "公司名称")
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @ApiModelProperty(value = "品牌")
    private Long companyBrandSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "品牌名称")
    @Excel(name = "品牌名称")
    private String companyBrandName;
                         /*********************查询参数****************************/
    @ApiModelProperty(value = "创建日期起")
    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    private String createDateStart;

    /** 创建日期至 */
    @ApiModelProperty(value = "创建日期至")
    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    private String createDateEnd;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询参数：尺码编码")
    private List<Long> skuSids;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询参数：尺码编码")
    private Long[] skuSides;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询参数：尺码名称")
    private String skuName;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询参数：类型")
    private List<String> bottomsTypes;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询参数：号型版型")
    private List<String> sizePatternTypes;

    @ApiModelProperty(value = "查询：处理状态")
    @TableField(exist = false)
    private String[] handleStatusList;

    @ApiModelProperty(value = "查询：启用/停用")
    @TableField(exist = false)
    private List<String> statuses;

    @TableField(exist = false)
    private Integer pageNum;

    @TableField(exist = false)
    private Integer pageSize;
}
