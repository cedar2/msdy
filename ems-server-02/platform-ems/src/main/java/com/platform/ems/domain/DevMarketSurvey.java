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

import lombok.experimental.Accessors;

/**
 * 市场调研对象 s_dev_market_survey
 *
 * @author chenkw
 * @date 2022-12-08
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_dev_market_survey")
public class DevMarketSurvey extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-市场调研
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-市场调研")
    private Long marketSurveySid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] marketSurveySidList;

    /**
     * 年度（数据字典的键值或配置档案的编码）
     */
    @NotBlank(message = "年份不能为空")
    @Excel(name = "年度", dictType = "s_year")
    @ApiModelProperty(value = "年度（数据字典的键值或配置档案的编码）")
    private String year;

    /**
     * 年度（多选）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "年度（多选）")
    private String[] yearList;

    /**
     * 产品季sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "产品季sid")
    private Long productSeasonSid;

    /**
     * 系统SID-产品季(多选)
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "产品季sid(多选)")
    private Long[] productSeasonSidList;

    /**
     * 产品季编码
     */
    @ApiModelProperty(value = "产品季编码")
    private String productSeasonCode;

	/**
	 * 产品季
	 */
	@TableField(exist = false)
	@ApiModelProperty(value = "产品季名称")
	private String productSeasonName;

	/**
     * 系统SID-公司档案
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-公司档案")
    private Long companySid;

    /**
     * 系统SID-公司档案(多选)
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-公司档案")
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
     * 公司名称
     */
    @TableField(exist = false)
    @Excel(name = "公司")
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
    @Excel(name = "品牌")
    @TableField(exist = false)
    @ApiModelProperty(value = "品牌名称")
    private String brandName;

    /**
     * 组别
     */
    @Excel(name = "组别", dictType = "s_product_group")
    @ApiModelProperty(value = "组别（数据字典的键值或配置档案的编码）")
    private String groupType;

    /**
     * 组别（多选）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "组别（多选）")
    private String[] groupTypeList;

    /**
     * 市场调研编码
     */
    @Excel(name = "市场调研编码")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "市场调研编码")
    private Long marketSurveyCode;

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
     * 备注
     */
    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

	/**
     * 启用/停用状态（数据字典的键值或配置档案的编码）
     */
    @NotBlank(message = "启停状态不能为空")
    @ApiModelProperty(value = "启用/停用状态（数据字典的键值或配置档案的编码）")
    private String status;

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
    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
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
    @Excel(name = "更改日期", width = 30, dateFormat = "yyyy-MM-dd")
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
    @Excel(name = "确认日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    /**
     * 附件对象
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "附件对象")
    private List<DevMarketSurveyAttach> attachmentList;

}
