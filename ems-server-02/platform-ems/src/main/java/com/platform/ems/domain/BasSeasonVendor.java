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
 * 季度供应商对象 s_bas_season_vendor
 *
 * @author chenkw
 * @date 2023-04-13
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_bas_season_vendor")
public class BasSeasonVendor extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

	/**
	 * 系统SID-季度供应商
	 */
	@TableId
	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty(value = "系统SID-季度供应商")
	private Long seasonVendorSid;

	@ApiModelProperty(value = "sid数组")
	@TableField(exist = false)
	private Long[] seasonVendorSidList;

	/**
	 * 季度供应商信息记录编号
	 */
	@Excel(name = "信息记录编号")
	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty(value = "季度供应商信息记录编号")
	private Long seasonVendorCode;

	/**
	 * 公司SID
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty(value = "公司SID")
	private Long companySid;

	@TableField(exist = false)
	@ApiModelProperty(value = "公司SID多选")
	private Long[] companySidList;

	/**
	 * 公司编码
	 */
	@ApiModelProperty(value = "公司编码")
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
	@TableField(exist = false)
	@Excel(name = "公司")
	@ApiModelProperty(value = "公司简称")
	private String companyShortName;

	/**
	 * 公司品牌sid
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty(value = "公司品牌sid")
	private Long companyBrandSid;

	@TableField(exist = false)
	@ApiModelProperty(value = "公司品牌sid多选")
	private Long[] companyBrandSidList;

	/**
	 * 公司品牌code
	 */
	@ApiModelProperty(value = "公司品牌code")
	private String companyBrandCode;

	/**
	 * 公司品牌
	 */
	@TableField(exist = false)
	@Excel(name = "品牌")
	@ApiModelProperty(value = "公司品牌")
	private String companyBrandName;

	/**
     * 系统SID-产品季档案
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-产品季档案")
    private Long productSeasonSid;

	@TableField(exist = false)
	@ApiModelProperty(value = "系统SID-产品季档案多选")
	private Long[] productSeasonSidList;

    /**
     * 产品季编码（人工编码）
     */
    @ApiModelProperty(value = "产品季编码（人工编码）")
    private String productSeasonCode;

	/**
	 * 产品季
	 */
	@TableField(exist = false)
	@Excel(name = "产品季")
	@ApiModelProperty(value = "产品季名称")
	private String productSeasonName;

    /**
     * 供应商档案SID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "供应商档案SID")
    private Long vendorSid;

	@TableField(exist = false)
	@ApiModelProperty(value = "供应商档案SID多选")
	private Long[] vendorSidList;

	/**
	 * 供应商名称
	 */
	@TableField(exist = false)
	@ApiModelProperty(value = "供应商名称")
	private String vendorName;

	/**
	 * 供应商简称
	 */
	@TableField(exist = false)
	@Excel(name = "供应商")
	@ApiModelProperty(value = "供应商简称")
	private String vendorShortName;

    /**
     * 是否快反供应商（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "是否快反供应商", dictType = "sys_yes_no")
    @ApiModelProperty(value = "是否快反供应商（数据字典的键值或配置档案的编码）")
    private String isKuaifanVendor;

	/**
	 * 供应商编码
	 */
	@Excel(name = "供应商编码")
	@ApiModelProperty(value = "供应商编码")
	private String vendorCode;

	@Excel(name = "备注")
	@ApiModelProperty(value = "备注")
	private String remark;

    /**
     * 启用/停用状态（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "启用/停用", dictType = "s_valid_flag")
    @ApiModelProperty(value = "启用/停用状态（数据字典的键值或配置档案的编码）")
    private String status;

    /**
     * 处理状态（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

	@TableField(exist = false)
	@ApiModelProperty(value = "处理状态（多选）")
	private String[] handleStatusList;

    /**
     * 创建人账号（用户名称）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

	@TableField(exist = false)
	@ApiModelProperty(value = "创建人账号（用户名称）多选")
	private String[] creatorAccountList;

	@TableField(exist = false)
	@Excel(name = "创建人")
	@ApiModelProperty(value = "创建人昵称")
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
     * 更新人账号（用户名称）
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

	@TableField(exist = false)
	@Excel(name = "更改人")
	@ApiModelProperty(value = "更改人昵称")
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
     * 确认人账号（用户名称）
     */
    @ApiModelProperty(value = "确认人账号（用户名称）")
    private String confirmerAccount;

	@TableField(exist = false)
	@Excel(name = "确认人")
	@ApiModelProperty(value = "确认人昵称")
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

}
