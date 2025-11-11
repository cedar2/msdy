package com.platform.ems.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 检测标准/项目/方法关联对象 s_con_check_standard_item_method
 *
 * @author qhq
 * @date 2021-11-01
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_con_check_standard_item_method")
public class ConCheckStandardItemMethod extends EmsBaseEntity {

	/**
	 * 租户ID
	 */
	@TableField(fill = FieldFill.INSERT)
	@ApiModelProperty(value = "租户ID")
	private String clientId;

	/**
	 * 系统SID-检测标准/项目/方法关联信息sid
	 */
	@TableId
	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty(value = "系统SID-检测标准/项目/方法关联信息sid")
	private Long checkStandardItemMethodSid;

	@ApiModelProperty(value = "sid数组")
	@TableField(exist = false)
	private Long[] checkStandardItemMethodSidList;
	/**
	 * 系统SID-检测标准/项目关联信息sid
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty(value = "系统SID-检测标准/项目关联信息sid")
	private Long checkStandardItemSid;
//------------------------------------------

	@Excel(name = "检测标准编码")
	@ApiModelProperty(value = "检测标准编码")
	@TableField(exist = false)
	private String checkStandardCode;

	@Excel(name = "检测标准名称")
	@TableField(exist = false)
	@ApiModelProperty(value = "检测标准名称")
	private String checkStandardName;

	@Excel(name = "检测项目编码")
	@TableField(exist = false)
	@ApiModelProperty(value = "检测项目编码")
	private String checkItemCode;

	@Excel(name = "检测项目名称")
	@TableField(exist = false)
	@ApiModelProperty(value = "检测项目名称")
	private String checkItemName;

	@Excel(name = "检测方法编码")
	@ApiModelProperty(value = "检测方法编码")
	private String checkMethodCode;

	@Excel(name = "检测方法名称")
	@TableField(exist = false)
	@ApiModelProperty(value = "检测方法名称")
	private String checkMethodName;

//------------------------------------------
	/**
	 * 检测方法sid
	 */
	//@Excel(name = "检测方法sid")
	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty(value = "检测方法sid")
	private Long checkMethodSid;

	/**
	 * 参考费用
	 */
	//@Excel(name = "参考费用")
	@ApiModelProperty(value = "参考费用")
	private BigDecimal checkCost;

	/**
	 * 货币(参考费用)（数据字典的键值或配置档案的编码）
	 */
	//@Excel(name = "货币(参考费用)（数据字典的键值或配置档案的编码）")
	@ApiModelProperty(value = "货币(参考费用)（数据字典的键值或配置档案的编码）")
	private String currency;

	/**
	 * 货币单位(参考费用)（数据字典的键值或配置档案的编码）
	 */
	//@Excel(name = "货币单位(参考费用)（数据字典的键值或配置档案的编码）")
	@ApiModelProperty(value = "货币单位(参考费用)（数据字典的键值或配置档案的编码）")
	private String currencyUnit;

	/**
	 * 标准值
	 */
	//@Excel(name = "标准值")
	@ApiModelProperty(value = "标准值")
	private String standardValue;

	/**
	 * 标准值类型（数据字典的键值或配置档案的编码）
	 */
	//@Excel(name = "标准值类型（数据字典的键值或配置档案的编码）")
	@ApiModelProperty(value = "标准值类型（数据字典的键值或配置档案的编码）")
	private String standardValueType;

	/**
	 * 区间符号(起)
	 */
	//@Excel(name = "区间符号(起)")
	@ApiModelProperty(value = "区间符号(起)")
	private String symbolStart;

	/**
	 * 起始值
	 */
	//@Excel(name = "起始值")
	@ApiModelProperty(value = "起始值")
	private BigDecimal valueStart;

	/**
	 * 区间符号(止)
	 */
	//@Excel(name = "区间符号(止)")
	@ApiModelProperty(value = "区间符号(止)")
	private String symbolEnd;

	/**
	 * 终止值
	 */
	//@Excel(name = "终止值")
	@ApiModelProperty(value = "终止值")
	private BigDecimal valueEnd;

	/**
	 * 序号
	 */
	//@Excel(name = "序号")
	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty(value = "序号")
	private Long sort;

	/**
	 * 创建人账号（用户账号）
	 */
	//@Excel(name = "创建人账号（用户账号）")
	@TableField(fill = FieldFill.INSERT)
	@ApiModelProperty(value = "创建人账号（用户账号）")
	private String creatorAccount;

	/**
	 * 创建时间
	 */
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
	//@Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
	@TableField(fill = FieldFill.INSERT)
	@ApiModelProperty(value = "创建时间")
	private Date createDate;

	/**
	 * 更新人账号（用户账号）
	 */
	//@Excel(name = "更新人账号（用户账号）")
	@TableField(fill = FieldFill.UPDATE)
	@ApiModelProperty(value = "更新人账号（用户账号）")
	private String updaterAccount;

	/**
	 * 更新时间
	 */
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
	//@Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
	@TableField(fill = FieldFill.UPDATE)
	@ApiModelProperty(value = "更新时间")
	private Date updateDate;

	/**
	 * 确认人账号（用户账号）
	 */
	//@Excel(name = "确认人账号（用户账号）")
	@ApiModelProperty(value = "确认人账号（用户账号）")
	private String confirmerAccount;

	/**
	 * 确认时间
	 */
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
	//@Excel(name = "确认时间", width = 30, dateFormat = "yyyy-MM-dd")
	@ApiModelProperty(value = "确认时间")
	private Date confirmDate;

	/**
	 * 数据源系统（数据字典的键值或配置档案的编码）
	 */
	//@Excel(name = "数据源系统（数据字典的键值或配置档案的编码）")
	@TableField(fill = FieldFill.INSERT)
	@ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
	private String dataSourceSys;




}
