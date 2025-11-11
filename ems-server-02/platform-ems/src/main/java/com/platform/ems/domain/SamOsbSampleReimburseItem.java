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
import java.util.List;


/**
 * 外采样报销单-明细对象 s_sam_osb_sample_reimburse_item
 *
 * @author qhq
 * @date 2021-12-28
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_sam_osb_sample_reimburse_item")
public class SamOsbSampleReimburseItem extends EmsBaseEntity {
	/**
	 * 租户ID
	 */
	@Excel(name = "租户ID")
	@TableField(fill = FieldFill.INSERT)
	@ApiModelProperty(value = "租户ID")
	private String clientId;

	/**
	 * 系统SID-外采样报销单明细
	 */
	@TableId
	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty(value = "系统SID-外采样报销单明细")
	private Long reimburseItemSid;
	@ApiModelProperty(value = "sid数组")
	@TableField(exist = false)
	private Long[] reimburseItemSidList;

	/**
	 * 外采样报销单sid
	 */
	@Excel(name = "外采样报销单sid")
	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty(value = "外采样报销单sid")
	private Long reimburseSid;

	/**
	 * 外采样sid
	 */
	@Excel(name = "外采样sid")
	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty(value = "外采样sid")
	private Long sampleSid;

	/**
	 * 外采样编码
	 */
	@Excel(name = "外采样编码")
	@ApiModelProperty(value = "外采样编码")
	private String sampleCode;

	/**
	 * 报销量
	 */
	@Excel(name = "报销量")
	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty(value = "报销量")
	private Long quantity;

	/**
	 * 采购价
	 */
	@Excel(name = "采购价")
	@ApiModelProperty(value = "采购价")
	private BigDecimal purchasePrice;

	/**
	 * 计量单位（数据字典的键值或配置档案的编码）
	 */
	@Excel(name = "计量单位（数据字典的键值或配置档案的编码）")
	@ApiModelProperty(value = "计量单位（数据字典的键值或配置档案的编码）")
	private String unitBase;

	/**
	 * 行号
	 */
	@Excel(name = "行号")
	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty(value = "行号")
	private Long itemNum;

	/**
	 * 创建人账号（用户账号）
	 */
	@Excel(name = "创建人账号（用户账号）")
	@TableField(fill = FieldFill.INSERT)
	@ApiModelProperty(value = "创建人账号（用户账号）")
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
	 * 更新人账号（用户账号）
	 */
	@Excel(name = "更新人账号（用户账号）")
	@TableField(fill = FieldFill.UPDATE)
	@ApiModelProperty(value = "更新人账号（用户账号）")
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
	 * 数据源系统（数据字典的键值或配置档案的编码）
	 */
	@Excel(name = "数据源系统（数据字典的键值或配置档案的编码）")
	@TableField(fill = FieldFill.INSERT)
	@ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
	private String dataSourceSys;

//    @TableField(exist = false)
//    BasMaterial material;

	@TableField(exist = false)
	private String materialName;

	@TableField(exist = false)
	private String osbSampleBuyer;

	@TableField(exist = false)
	private String osbSampleBuyerName;

	@TableField(exist = false)
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
	private Date purchaseDate;

	@TableField(exist = false)
	private Long purchaseCompany;

	@TableField(exist = false)
	private String purchaseCompanyName;

	@TableField(exist = false)
	private Long purchaseOrg;

	@TableField(exist = false)
	private String purchaseOrgName;

	@TableField(exist = false)
	private String purchaseFrom;

	@TableField(exist = false)
	private String osbSampleBrand;

	@TableField(exist = false)
	private String osbSampleCode;

	@TableField(exist = false)
	private String osbSampleColor;

	@TableField(exist = false)
	private String osbSampleSize;

	@TableField(exist = false)
	private String currency;

	@TableField(exist = false)
	private String currencyUnit;

	@TableField(exist = false)
	private Long purchaseQuantity;

	@TableField(exist = false)
	private String reimburseStatus;

	@TableField(exist = false)
	private String materialType;

	@TableField(exist = false)
	private String materialTypeName;

	@TableField(exist = false)
	private String materialClassSid;

	@TableField(exist = false)
	private String materialClassName;

	@TableField(exist = false)
	@JsonSerialize(using = ToStringSerializer.class)
	private Long productSeasonSid;

	@TableField(exist = false)
	private String productSeasonName;

	@TableField(exist = false)
	@ApiModelProperty(value = "外采样报销单号")
	private Long reimburseCode;

	@TableField(exist = false)
	private List<Long> materialSidList;

}
