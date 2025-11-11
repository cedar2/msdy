package com.platform.ems.domain;

import java.math.BigDecimal;
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
 * 新品试销计划单-类目分析对象 s_frm_trialsale_plan_category_analysis
 *
 * @author chenkw
 * @date 2022-12-16
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_frm_trialsale_plan_category_analysis")
public class FrmTrialsalePlanCategoryAnalysis extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-新品试销计划单类目分析
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-新品试销计划单类目分析")
    private Long trialsalePlanCategoryAnalysisSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] trialsalePlanCategoryAnalysisSidList;

    /**
     * 系统SID-新品试销计划单
     */
    @Excel(name = "系统SID-新品试销计划单")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-新品试销计划单")
    private Long newproductTrialsalePlanSid;

    /**
     * 排名
     */
    @Excel(name = "排名")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "排名")
    private Long rankNum;

    /**
     * 品牌
     */
    @Excel(name = "品牌")
    @ApiModelProperty(value = "品牌")
    private String brand;

    /**
     * 客单价（元）
     */
    @Excel(name = "客单价（元）")
    @ApiModelProperty(value = "客单价（元）")
    private BigDecimal salePriceAverage;

    /**
     * 销量
     */
    @Excel(name = "销量")
    @ApiModelProperty(value = "销量")
    private BigDecimal saleQuantity;

    /**
     * 核心卖点
     */
    @Excel(name = "核心卖点")
    @ApiModelProperty(value = "核心卖点")
    private String coreSalePoint;

    /**
     * 痛点
     */
    @Excel(name = "痛点")
    @ApiModelProperty(value = "痛点")
    private String sorePoint;

    /**
     * 总结
     */
    @Excel(name = "总结")
    @ApiModelProperty(value = "总结")
    private String summary;

    /**
     * 币种
     */
    @Excel(name = "币种")
    @ApiModelProperty(value = "币种")
    private String currency;

    /**
     * 货币单位
     */
    @Excel(name = "货币单位")
    @ApiModelProperty(value = "货币单位")
    private String currencyUnit;

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
