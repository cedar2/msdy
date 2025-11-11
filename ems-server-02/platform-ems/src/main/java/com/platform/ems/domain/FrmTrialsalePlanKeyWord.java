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
 * 新品试销计划单-关键词分析对象 s_frm_trialsale_plan_key_word
 *
 * @author chenkw
 * @date 2022-12-16
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_frm_trialsale_plan_key_word")
public class FrmTrialsalePlanKeyWord extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-新品试销计划单关键词分析
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-新品试销计划单关键词分析")
    private Long trialsalePlanKeyWordSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] trialsalePlanKeyWordSidList;

    /**
     * 系统SID-新品试销计划单
     */
    @Excel(name = "系统SID-新品试销计划单")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-新品试销计划单")
    private Long newproductTrialsalePlanSid;

    /**
     * 核心词
     */
    @Excel(name = "核心词")
    @ApiModelProperty(value = "核心词")
    private String coreWord;

    /**
     * 搜索量（核心词）
     */
    @Excel(name = "搜索量（核心词）")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "搜索量（核心词）")
    private Long searchNumCoreWord;

    /**
     * 长尾词
     */
    @Excel(name = "长尾词")
    @ApiModelProperty(value = "长尾词")
    private String longtailWord;

    /**
     * 搜索量（长尾词）
     */
    @Excel(name = "搜索量（长尾词）")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "搜索量（长尾词）")
    private Long searchNumLongtailWord;

    /**
     * 否定词
     */
    @Excel(name = "否定词")
    @ApiModelProperty(value = "否定词")
    private String negativeWord;

    /**
     * 创建人账号（用户账号）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户账号）")
    private String creatorAccount;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人昵称")
    @TableField(exist = false)
    private String creatorAccountName;

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
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户账号）")
    private String updaterAccount;

    @Excel(name = "更改人")
    @ApiModelProperty(value = "更改人昵称")
    @TableField(exist = false)
    private String updaterAccountName;

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
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

}
