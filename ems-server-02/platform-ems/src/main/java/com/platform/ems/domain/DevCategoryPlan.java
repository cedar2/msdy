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

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.experimental.Accessors;

/**
 * 品类规划对象 s_dev_category_plan
 *
 * @author chenkw
 * @date 2022-12-09
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_dev_category_plan")
public class DevCategoryPlan extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-品类规划
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-品类规划")
    private Long categoryPlanSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] categoryPlanSidList;

    /**
     * 品类规划编号
     */
    @NotBlank(message = "品类规划编号不能为空")
    @Excel(name = "品类规划编号")
    @ApiModelProperty(value = "品类规划编号")
    private String categoryPlanCode;

    /**
     * 年度（数据字典的键值或配置档案的编码）
     */
    @NotBlank(message = "年度不能为空")
    @Excel(name = "年度")
    @ApiModelProperty(value = "年度（数据字典的键值或配置档案的编码）")
    private String year;

    /**
     * 年度（多选）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "年度（数据字典的键值或配置档案的编码）")
    private String[] yearList;

    /**
     * 公司sid
     */
    @NotNull(message = "公司不能为空")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司sid")
    private Long companySid;

    /**
     * 公司sid (多选)
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "公司sid")
    private Long[] companySidList;

    /**
     * 公司代码
     */
    @ApiModelProperty(value = "公司代码")
    private String companyCode;

    /**
     * 公司名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    /**
     * 公司简称
     */
    @Excel(name = "公司")
    @TableField(exist = false)
    @ApiModelProperty(value = "公司简称")
    private String companyShortName;

    /**
     * 品牌编码
     */
    @ApiModelProperty(value = "品牌编码")
    private String brandCode;

    /**
     * 品牌编码 （多选）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "品牌编码 （多选）")
    private String[] brandCodeList;

    /**
     * 品牌名称
     */
    @TableField(exist = false)
    @Excel(name = "品牌")
    @ApiModelProperty(value = "品牌名称")
    private String brandName;

    /**
     * 组别
     */
    @ApiModelProperty(value = "组别（数据字典的键值或配置档案的编码）")
    private String groupType;

    /**
     * 组别（多选）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "组别（多选）")
    private String[] groupTypeList;

    /**
     * 作废说明
     */
    @ApiModelProperty(value = "作废说明")
    private String cancelRemark;

    /**
     * 产品季sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "产品季sid")
    private Long productSeasonSid;

    /**
     * 产品季sid（多选）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "产品季sid（多选）")
    private Long[] productSeasonSidList;

    /**
     * 产品季编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "产品季编码")
    private String productSeasonCode;

    /**
     * 产品季
     */
    @TableField(exist = false)
    @Excel(name = "产品季")
    @ApiModelProperty(value = "产品季")
    private String productSeasonName;

    /**
     * 处理状态（数据字典的键值或配置档案的编码）
     */
    @NotBlank(message = "处理状态不能为空")
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    /**
     * 处理状态（多选）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String[] handleStatusList;

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
     * 确认人账号（用户账号）
     */
    @ApiModelProperty(value = "确认人账号（用户账号）")
    private String confirmerAccount;

    @Excel(name = "确认人")
    @ApiModelProperty(value = "确认人昵称")
    @TableField(exist = false)
    private String confirmerAccountName;

    /**
     * 确认时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "确认时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    /**
     * 明细对象
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "明细对象")
    private List<DevCategoryPlanItem> categoryPlanItemList;

    /**
     * 附件对象
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "附件对象")
    private List<DevCategoryPlanAttach> attachmentList;

    /**
     * 导入标识： DR
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "导入标识： DR")
    private String importType;

    /**
     * 跳过校验
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "跳过校验")
    private boolean jumpJudge;
}
