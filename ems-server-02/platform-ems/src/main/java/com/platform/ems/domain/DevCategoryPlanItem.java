package com.platform.ems.domain;

import java.util.Date;
import java.util.List;

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
 * 品类规划-明细对象 s_dev_category_plan_item
 *
 * @author chenkw
 * @date 2022-12-09
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_dev_category_plan_item")
public class DevCategoryPlanItem extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-项目档案
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-项目档案")
    private Long categoryPlanItemSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] categoryPlanItemSidList;

    /**
     * 系统SID-品类规划
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-品类规划")
    private Long categoryPlanSid;

    /**
     * 品类规划编号
     */
    @Excel(name = "品类规划编号")
    @ApiModelProperty(value = "品类规划编号")
    private String categoryPlanCode;

    /**
     * 物料分类sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "物料分类sid")
    private Long materialClassSid;

    /**
     * 物料分类sid 多选
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "物料分类sid 多选")
    private Long[] materialClassSidList;

    /**
     * 物料分类编码
     */
    @Excel(name = "物料分类编码")
    @ApiModelProperty(value = "物料分类编码")
    private String materialClassCode;

    /**
     * 物料分类
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "物料分类")
    private String materialClassName;

    /**
     * 大类sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "大类sid")
    private Long bigClassSid;

    /**
     * 大类sid 多选
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "大类sid 多选")
    private Long[] bigClassSidList;

    /**
     * 大类code
     */
    @Excel(name = "大类code")
    @ApiModelProperty(value = "大类code")
    private String bigClassCode;

    /**
     * 大类
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "大类")
    private String bigClassName;

    /**
     * 中类sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "中类sid")
    private Long middleClassSid;

    /**
     * 中类sid 多选
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "中类sid 多选")
    private Long[] middleClassSidList;

    /**
     * 中类code
     */
    @Excel(name = "中类code")
    @ApiModelProperty(value = "中类code")
    private String middleClassCode;

    /**
     * 中类
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "中类")
    private String middleClassName;

    /**
     * 小类sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "小类sid")
    private Long smallClassSid;

    /**
     * 小类sid 多选
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "小类sid 多选")
    private Long[] smallClassSidList;

    /**
     * 小类code
     */
    @Excel(name = "小类code")
    @ApiModelProperty(value = "小类code")
    private String smallClassCode;

    /**
     * 小类
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "小类")
    private String smallClassName;

    /**
     * 规划款数量
     */
    @Excel(name = "规划款数量")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "规划款数量")
    private Long planQuantity;

    /**
     * 计划类型（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "计划类型")
    @ApiModelProperty(value = "计划类型（数据字典的键值或配置档案的编码）")
    private String planType;

    /**
     * 计划类型（多选）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "计划类型（多选）")
    private String[] planTypeList;

    /**
     * 款式（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "款式")
    @ApiModelProperty(value = "款式（数据字典的键值或配置档案的编码）")
    private String kuanType;

    /**
     * 系列（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "系列")
    @ApiModelProperty(value = "系列（数据字典的键值或配置档案的编码）")
    private String series;

    /**
     * 组别（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "组别", dictType = "s_product_group")
    @ApiModelProperty(value = "组别（数据字典的键值或配置档案的编码）")
    private String groupType;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "下一步负责人sid")
    private Long nextReceiverSid;

    @ApiModelProperty(value = "下一步负责人编码")
    private String nextReceiverCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "下一步负责人名称")
    private String nextReceiverName;

    /**
     * 规划说明
     */
    @ApiModelProperty(value = "规划说明")
    private String planRemark;

    /**
     * 品类规划行号
     */
    @ApiModelProperty(value = "品类规划行号")
    private Integer categoryPlanItemNum;

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

    /**
     * 年度 主表
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "年度")
    private String year;

    /**
     * 公司sid 主表
     */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司sid")
    private Long companySid;

    /**
     * 品牌编码 主表
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "品牌编码")
    private String brandCode;

    /**
     * 产品季 主表
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "产品季")
    private Long productSeasonSid;

    /**
     * 组别多选
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "组别多选")
    private String[] groupTypeList;

    /**
     * 导入标识： DR
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "导入标识： DR")
    private String importType;

    /**
     * 开发计划列表
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "开发计划列表")
    private List<DevDevelopPlan> developPlanList;

}
