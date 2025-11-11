package com.platform.ems.domain.dto.request;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 外采样报销单报表查询类
 *
 * @Author qhq
 * @create 2022/1/18 14:03
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel
@Accessors(chain = true)
public class SamOsbSampleReimburseReportRequert extends EmsBaseEntity implements Serializable {

	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty(value = "明细sid")
	private Long reimburseItemSid;

	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty(value = "sid")
	private Long reimburseSid;

	/**
	 * 外采样报销单号
	 */
	@Excel(name = "报销单号")
	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty(value = "外采样报销单号")
	private Long reimburseCode;

	/**
	 * 公司sid
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty(value = "公司sid")
	private Long companySid;

	@Excel(name = "公司")
	@TableField(exist = false)
	private String companyName;

	/**
	 * 报销部门sid
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty(value = "报销部门sid")
	private Long departmentSid;

	@Excel(name = "报销部门")
	@TableField(exist = false)
	private String departmentName;

	@ApiModelProperty(name = "报销部门(多选)")
	@TableField(exist = false)
	private Long[] departmentSidList;

	/**
	 * 报销人（用户账号）
	 */
	@ApiModelProperty(value = "报销人（用户账号）")
	private String reimburser;

	@TableField(exist = false)
	@ApiModelProperty(value = "报销人（用户账号）(多选)")
	private String[] reimburserList;

	@Excel(name = "报销人")
	@TableField(exist = false)
	private String reimburserName;

	/**
	 * 外采样编码
	 */
	@Excel(name = "外采样编码")
	@ApiModelProperty(value = "外采样编码")
	private String sampleCode;

	/**
	 * 外采样编码
	 */
	@Excel(name = "外采样名称")
	@ApiModelProperty(value = "外采样名称")
	private String sampleName;

	/**
	 * 创建人账号（用户账号）
	 */
	@TableField(fill = FieldFill.INSERT)
	@ApiModelProperty(value = "创建人账号（用户账号）")
	private String creatorAccount;

	/**
	 * 报销量
	 */
	@Excel(name = "报销量")
	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty(value = "报销量")
	private Long quantity;

	@Excel(name = "计量单位")
	@TableField(exist = false)
	private String unitBaseName;

	/**
	 * 采购价
	 */
	@Excel(name = "采购价")
	@ApiModelProperty(value = "采购价")
	private BigDecimal purchasePrice;

	@Excel(name = "报销金额")
	private BigDecimal money;

	/**
	 * 处理状态（数据字典的键值或配置档案的编码）
	 */
	@NotEmpty(message = "状态不能为空")
	@Excel(name = "处理状态", dictType = "s_handle_status")
	@ApiModelProperty(value = "处理状态")
	private String handleStatus;

	@TableField(exist = false)
	@ApiModelProperty(value = "处理状态(多选)")
	private String[] handleStatusList;

	@Excel(name = "备注")
	@ApiModelProperty(value = "备注")
	private String remark;

	@Excel(name = "创建人")
	@TableField(exist = false)
	private String creatorAccountName;

	/**
	 * 单据日期
	 */
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
	@Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
	@ApiModelProperty(value = "单据日期")
	private Date documentDate;

}
