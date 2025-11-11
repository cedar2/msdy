package com.platform.ems.domain.dto.response;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class MaterialBottomsListResponse {

    /** 系统ID-上下装尺寸对照表 */
    @Excel(name = "系统ID-上下装尺寸对照表")
    @ApiModelProperty(value = "系统ID-上下装尺寸对照表")
    private Long bottomsSkuSid;

    /** 类型 */
    @ApiModelProperty(value = "类型")
    private String bottomsType;

    /** 号型版型编码 */
    @ApiModelProperty(value = "号型版型编码")
    private String sizePatternType;

    /** 尺码编码 */
    @ApiModelProperty(value = "尺码编码")
    private Long skuSid;

    /** 套装下身尺码 */
    @Excel(name = "套装下身尺码")
    @ApiModelProperty(value = "套装下身尺码")
    private String sizeBottoms;

    /** 启用/停用状态 */
    @Excel(name = "启用/停用状态")
    @ApiModelProperty(value = "启用/停用状态")
    private String status;

    /** 处理状态 */
    @Excel(name = "处理状态")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

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


    /** 公司编码（公司档案的sid） */
    @Excel(name = "公司编码（公司档案的sid）")
    @ApiModelProperty(value = "公司编码（公司档案的sid）")
    private Long companySid;
}
