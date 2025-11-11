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
 * SKU组明细对象 s_bas_sku_group_item
 *
 * @author linhongwei
 * @date 2021-03-22
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_bas_sku_group_item")
public class BasSkuGroupItem extends EmsBaseEntity {

	/**
	 * 客户端口号
	 */
	@TableField(fill = FieldFill.INSERT)
	@ApiModelProperty(value = "客户端口号")
	private String clientId;

	/**
	 * 系统ID-SKU组明细
	 */
	@Excel(name = "系统ID-SKU组明细")
	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty(value = "系统ID-SKU组明细")
	@TableId
	private Long skuGroupItemSid;

	@TableField(exist = false)
	private Long[] skuGroupItemSidList;

	/**
	 * 系统ID-SKU组档案
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty(value = "系统ID-SKU组档案")
	private Long skuGroupSid;

	/**
	 * 系统ID-SKU档案
	 */
	@Excel(name = "系统ID-SKU档案")
	@ApiModelProperty(value = "系统ID-SKU档案")
	private String skuSid;

	/**
	 * 序号
	 */
	@Excel(name = "序号")
	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty(value = "序号")
	private BigDecimal sort;

	/**
	 * 创建人账号
	 */
	@Excel(name = "创建人账号")
	@TableField(fill = FieldFill.INSERT)
	@ApiModelProperty(value = "创建人账号")
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
	 * 更新人账号
	 */
	@Excel(name = "更新人账号")
	@TableField(fill = FieldFill.UPDATE)
	@ApiModelProperty(value = "更新人账号")
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
	 * 数据源系统
	 */
	@Excel(name = "数据源系统")
	@TableField(fill = FieldFill.INSERT)
	@ApiModelProperty(value = "数据源系统")
	private String dataSourceSys;

	@ApiModelProperty(value = "sku档案详情")
	@TableField(exist = false)
	private BasSku skuDetail;

	@ApiModelProperty(value = "SKU组编码")
	@TableField(exist = false)
	private String skuGroupCode;

	@ApiModelProperty(value = "SKU组名称")
	@TableField(exist = false)
	private String skuGroupName;

	@ApiModelProperty(value = "SKU编码")
	@TableField(exist = false)
	private String skuCode;

	@ApiModelProperty(value = "SKU名称")
	@TableField(exist = false)
	private String skuName;

	@ApiModelProperty(value = "上下装套装")
	@TableField(exist = false)
	private String upDownSuit;

	@ApiModelProperty(value = "上下装套装")
	@TableField(exist = false)
	private String[] upDownSuitList;

	@ApiModelProperty(value = "处理状态")
	@TableField(exist = false)
	private String handleStatus;

	@ApiModelProperty(value = "处理状态")
	@TableField(exist = false)
	private String[] handleStatusList;

	@ApiModelProperty(value = "启停状态")
	@TableField(exist = false)
	private String status;

	@ApiModelProperty(value = "启停状态")
	@TableField(exist = false)
	private String[] statusList;

	@ApiModelProperty(value = "客户")
	@TableField(exist = false)
	private String customerName;

	@ApiModelProperty(value = "客户")
	@TableField(exist = false)
	private String customerSid;

	@ApiModelProperty(value = "客户")
	@TableField(exist = false)
	private String[] customerSidList;

	@ApiModelProperty(value = "sku名称2")
	@TableField(exist = false)
	private String skuName2;

	@ApiModelProperty(value = "sku名称3")
	@TableField(exist = false)
	private String skuName3;

	@ApiModelProperty(value = "sku名称4")
	@TableField(exist = false)
	private String skuName4;

	@ApiModelProperty(value = "sku名称5")
	@TableField(exist = false)
	private String skuName5;

	@ApiModelProperty(value = "sku类型")
	@TableField(exist = false)
	private String skuType;

	@ApiModelProperty(value = "sku类型")
	@TableField(exist = false)
	private String[] skuTypeList;

	@ApiModelProperty(value = "sku数值")
	@TableField(exist = false)
	private String skuNumeralValue;

	@TableField(exist = false)
	@ApiModelProperty(value = "创建人账号")
	private String creatorAccountName;

	@TableField(exist = false)
	@ApiModelProperty(value = "更新人账号")
	private String updaterAccountName;

	@TableField(exist = false)
	@ApiModelProperty(value = "确认人账号")
	private String confirmerAccountName;

	@TableField(exist = false)
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
	@ApiModelProperty(value = "确认人日期")
	private String confirmDate;

	private String remark;

	@TableField(exist = false)
	private String beginTime;
	@TableField(exist = false)
	private String endTime;
	@TableField(exist = false)
	private String skuStatus;


	@TableField(exist = false)
	private String firstSort;

	@TableField(exist = false)
	private String secondSort;

	@TableField(exist = false)
	private String thirdSort;

}
