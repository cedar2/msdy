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

import javax.validation.constraints.Digits;

/**
 * 商品实际成本台账表对象 s_cos_record_product_actual_cost
 *
 * @author chenkw
 * @date 2023-04-27
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_cos_record_product_actual_cost")
public class CosRecordProductActualCost extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-商品实际成本台账
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品实际成本台账")
    private Long recordCostSid;

	@TableField(exist = false)
    @ApiModelProperty(value = "sid数组")
    private Long[] recordCostSidList;

    /**
     * 成本台账记录号
     */
	@Excel(name = "成本台账编码")
	@JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "成本台账记录号")
    private Long recordCostCode;

	/**
	 * 工厂SID
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty(value = "工厂SID")
	private Long plantSid;

    /**
     * 工厂SID 多选
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "工厂SID 多选")
    private Long[] plantSidList;

	/**
	 * 工厂编码
	 */
	@ApiModelProperty(value = "工厂编码")
	private String plantCode;

	/**
	 * 工厂名称
	 */
	@TableField(exist = false)
	@ApiModelProperty(value = "工厂名称")
	private String plantName;

	/**
	 * 工厂简称
	 */
	@TableField(exist = false)
	@Excel(name = "工厂")
	@ApiModelProperty(value = "工厂简称")
	private String plantShortName;

    /**
     * 商品编码/款号sid
     */
	@JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "商品编码/款号sid")
    private Long materialSid;

    /**
     * 商品编码/款号
     */
    @Excel(name = "商品编码(款号)")
    @ApiModelProperty(value = "商品编码/款号")
    private String materialCode;

    /**
     * 商品(款)名称
     */
    @TableField(exist = false)
    @Excel(name = "商品(款)名称")
    @ApiModelProperty(value = "商品(款)名称")
    private String materialName;

    /**
     * 系统SID-SKU1档案
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-SKU1档案")
    private Long sku1Sid;

    /**
     * SKU1属性编码
     */
    @ApiModelProperty(value = "SKU1属性编码")
    private String sku1Code;

    /**
     * SKU1属性名称
     */
    @Excel(name = "SKU1属性")
    @ApiModelProperty(value = "SKU1属性名称")
    private String sku1Name;

    /**
     * SKU1属性类型
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "SKU1属性类型")
    private String sku1Type;

    /**
     * 系统SID-SKU2档案
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-SKU2档案")
    private Long sku2Sid;

    /**
     * SKU2属性编码
     */
    @ApiModelProperty(value = "SKU2属性编码")
    private String sku2Code;

    /**
     * SKU2属性名称
     */
    @Excel(name = "SKU2属性")
    @ApiModelProperty(value = "SKU2属性名称")
    private String sku2Name;

    /**
     * SKU2属性类型
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "SKU2属性类型")
    private String sku2Type;

    /**
     * 成本(元)
     */
    @TableField(exist = false)
    @Excel(name = "成本(元)", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "成本(元)")
    private BigDecimal totalCost;

    /**
     * 材料费(元)
     */
    @Digits(integer = 8, fraction = 2, message = "材料费(元)整数位不能超过8位，小数位不能超过2位")
    @Excel(name = "材料费(元)", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "材料费(元)")
    private BigDecimal materialPrice;

    /**
     * 工价(元)
     */
    @Digits(integer = 8, fraction = 2, message = "工价(元)整数位不能超过8位，小数位不能超过2位")
    @Excel(name = "工价(元)", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "工价(元)")
    private BigDecimal price;

    /**
     * 特殊工艺外发加工费(元)
     */
    @Digits(integer = 8, fraction = 2, message = "特殊工艺外发加工费(元)整数位不能超过8位，小数位不能超过2位")
    @Excel(name = "特殊工艺外发加工费(元)", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "特殊工艺外发加工费(元)")
    private BigDecimal specialCraftPrice;

    /**
     * 其它费(元)
     */
    @Digits(integer = 8, fraction = 2, message = "其它费(元)整数位不能超过8位，小数位不能超过2位")
    @Excel(name = "其它费(元)", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "其它费(元)")
    private BigDecimal otherPrice;

    /**
     * 基本计量单位编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "基本计量单位编码")
    private String unitBase;

    /**
     * 基本计量单位
     */
    @TableField(exist = false)
    @Excel(name = "基本计量单位")
    @ApiModelProperty(value = "基本计量单位")
    private String unitBaseName;

    /**
     * 销售价(元)
     */
    @Digits(integer = 8, fraction = 2, message = "销售价(元)整数位不能超过8位，小数位不能超过2位")
    @Excel(name = "销售价(元)", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "销售价(元)")
    private BigDecimal salePrice;

    /**
     * 货币
     */
    @ApiModelProperty(value = "货币")
    private String currency;

    /**
     * 货币单位
     */
    @ApiModelProperty(value = "货币单位")
    private String currencyUnit;

    /**
     * 成本核算日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "成本核算日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "成本核算日期")
    private Date productCostDate;

    /**
     * 说明
     */
    @Excel(name = "说明")
    @ApiModelProperty(value = "说明")
    private String remark;

    /**
     * 处理状态（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    /**
     * 处理状态（多选）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态（多选）")
    private String[] handleStatusList;

    /**
     * 创建人账号（用户账号）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户账号）")
    private String creatorAccount;

    /**
     * 创建日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建日期")
    private Date createDate;

    /**
     * 创建人昵称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "创建人昵称")
    private String creatorAccountName;

    /**
     * 更改人账号（用户账号）
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更改人账号（用户账号）")
    private String updaterAccount;

    /**
     * 更改人昵称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "更改人昵称")
    private String updaterAccountName;

    /**
     * 更新日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新日期")
    private Date updateDate;

    /**
     * 确认人账号（用户账号）
     */
    @ApiModelProperty(value = "确认人账号（用户账号）")
    private String confirmerAccount;

    /**
     * 确认人昵称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "确认人昵称")
    private String confirmerAccountName;

    /**
     * 确认日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认日期")
    private Date confirmDate;

    /**
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    /**
     * 导入时默认BusinessType.IMPORT.getValue()
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "导入时默认BusinessType.IMPORT.getValue()")
    private String importType;

}
