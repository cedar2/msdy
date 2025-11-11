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

import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.List;

/**
 * 检测标准对象 s_con_check_standard
 *
 * @author qhq
 * @date 2021-11-01
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_con_check_standard")
public class ConCheckStandard extends EmsBaseEntity {

	/**
	 * 租户ID
	 */
	@TableField(fill = FieldFill.INSERT)
	@ApiModelProperty(value = "租户ID")
	private String clientId;

	/**
	 * 系统SID-检测标准sid
	 */
	@TableId
	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty(value = "系统SID-检测标准sid")
	private Long sid;

	@ApiModelProperty(value = "sid数组")
	@TableField(exist = false)
	private Long[] sidList;
	/**
	 * 检测标准编码
	 */
	@Excel(name = "检测标准编码",sort = 1)
	@ApiModelProperty(value = "检测标准编码")
	private String code;

	/**
	 * 检测标准名称
	 */
	@Excel(name = "检测标准名称",sort = 2)
	@ApiModelProperty(value = "检测标准名称")
	private String name;

	@Excel(name = "备注",sort = 3)
	@ApiModelProperty(value ="备注")
	private String remark;

	/**
	 * 序号
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty(value = "序号")
	private Long sort;

	/**
	 * 启用/停用状态（数据字典的键值或配置档案的编码）
	 */
	@NotEmpty(message = "状态不能为空")
	@Excel(name = "启用/停用", dictType = "s_valid_flag",sort = 5)
	@ApiModelProperty(value = "启用/停用状态（数据字典的键值或配置档案的编码）")
	private String status;

	/**
	 * 处理状态（数据字典的键值或配置档案的编码）
	 */
	@NotEmpty(message = "状态不能为空")
	@Excel(name = "处理状态", dictType = "s_handle_status",sort = 4)
	@ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
	private String handleStatus;

	/**
	 * 创建人账号（用户账号）
	 */
	@TableField(fill = FieldFill.INSERT)
	@ApiModelProperty(value = "创建人账号（用户账号）")
	private String creatorAccount;

	/**
	 * 创建时间
	 */
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
	@Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd",sort = 7)
	@TableField(fill = FieldFill.INSERT)
	@ApiModelProperty(value = "创建时间")
	private Date createDate;

	/**
	 * 更新人账号（用户账号）
	 */
	@TableField(fill = FieldFill.UPDATE)
	@ApiModelProperty(value = "更新人账号（用户账号）")
	private String updaterAccount;

	/**
	 * 更新时间
	 */
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
	@TableField(fill = FieldFill.UPDATE)
	@ApiModelProperty(value = "更新时间")
	private Date updateDate;

	/**
	 * 确认人账号（用户账号）
	 */
	@ApiModelProperty(value = "确认人账号（用户账号）")
	private String confirmerAccount;

	/**
	 * 确认时间
	 */
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
	@ApiModelProperty(value = "确认时间")
	private Date confirmDate;

	/**
	 * 数据源系统（数据字典的键值或配置档案的编码）
	 */
	@TableField(fill = FieldFill.INSERT)
	@ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
	private String dataSourceSys;

	@TableField(exist = false)
    @Excel(name = "创建人",sort = 6)
	private String creatorAccountName;

	@TableField(exist = false)
	private String updaterAccountName;

	@TableField(exist = false)
	private String confirmerAccountName;

	@TableField(exist = false)
	private List<ConCheckStandardItem> standardItemList;

}
