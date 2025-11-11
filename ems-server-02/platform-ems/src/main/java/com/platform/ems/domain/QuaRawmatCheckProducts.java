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

import java.util.Date;

/**
 * 面辅料检测单-款明细对象 s_qua_rawmat_check_products
 *
 * @author linhongwei
 * @date 2022-04-11
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_qua_rawmat_check_products")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuaRawmatCheckProducts extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-面辅料检测单-款明细
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-面辅料检测单-款明细")
    private Long rawmatCheckProductsSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] rawmatCheckProductsSidList;
    /**
     * 系统SID-面辅料检测单
     */
    @Excel(name = "系统SID-面辅料检测单")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-面辅料检测单")
    private Long rawmatCheckSid;

    /**
     * 款号sid
     */
    @Excel(name = "款号sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "款号sid")
    private Long productSid;

    /**
     * 款号
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "款号")
    private String materialCode;

    /**
     * 款名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "款名称")
    private String materialName;

    /**
     * 样衣号
     */
    @Excel(name = "样衣号")
    @ApiModelProperty(value = "样衣号")
    private String sampleCode;

    /**
     * 颜色sid
     */
    @Excel(name = "颜色sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "颜色sid")
    private Long sku1Sid;

    @TableField(exist = false)
    @ApiModelProperty(value = "颜色名称")
    private String sku1Name;

    /**
     * 参考成分
     */
    @Excel(name = "参考成分")
    @ApiModelProperty(value = "参考成分")
    private String composition;

    /**
     * 创建人账号（用户账号）
     */
    @Excel(name = "创建人账号（用户账号）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户账号）")
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
     * 更新人账号（用户账号）
     */
    @Excel(name = "更新人账号（用户账号）")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户账号）")
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
    private String creatorAccountName;

    @TableField(exist = false)
    private String updaterAccountName;

    @TableField(exist = false)
    private String confirmerAccountName;

}
