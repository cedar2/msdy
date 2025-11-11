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

import javax.validation.constraints.NotNull;

/**
 * 库存调整单-明细对象 s_inv_inventory_adjust_item
 *
 * @author linhongwei
 * @date 2021-04-19
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_inv_inventory_adjust_item")
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class InvInventoryAdjustItem extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-库存调整单明细
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-库存调整单明细")
    private Long inventoryAdjustItemSid;

    /**
     * 系统SID-库存调整单
     */
    @Excel(name = "系统SID-库存调整单")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-库存调整单")
    private Long inventoryAdjustSid;

    /**
     * 系统SID-物料&商品&服务
     */
    @Excel(name = "系统SID-物料&商品&服务")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品&服务")
    private Long materialSid;

    /**
     * 系统SID-物料&商品sku1
     */
    @Excel(name = "系统SID-物料&商品sku1")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品sku1")
    private Long sku1Sid;

    /**
     * 系统SID-物料&商品sku2
     */
    @Excel(name = "系统SID-物料&商品sku2")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品sku2")
    private Long sku2Sid;


    @Excel(name = "加权平均价")
    @ApiModelProperty(value = "加权平均价")
    private BigDecimal price;

    @Excel(name = "金额")
    @ApiModelProperty(value = "金额")
    private BigDecimal currencyAmount;

    /**
     * 系统SID-物料&商品条码
     */
    @Excel(name = "系统SID-物料&商品条码")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品条码")
    private Long barcodeSid;

    /**
     * 基本计量单位（数据字典的键值）
     */
    @Excel(name = "基本计量单位（数据字典的键值）")
    @ApiModelProperty(value = "基本计量单位（数据字典的键值）")
    private String unitBase;

    /**
     * 数量
     */
    @Excel(name = "数量")
    @ApiModelProperty(value = "数量")
    private BigDecimal quantity;

    /** 系统SID-仓库档案 */
    @NotNull(message = "明细仓库不能为空")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-仓库档案")
    private Long storehouseSid;

    @ApiModelProperty(value = "系统SID-仓库档案")
    private String storehouseCode;

    /** 系统SID-库位 */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-库位")
    private Long storehouseLocationSid;

    @ApiModelProperty(value = "系统SID-库位")
    private String storehouseLocationCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-仓库档案（目的仓库）")
    private String destStorehouseCode;

    /**
     * 原因类型（数据字典的键值）
     */
    @Excel(name = "原因类型（数据字典的键值）")
    @ApiModelProperty(value = "原因类型（数据字典的键值）")
    private String reasonType;
    /**
     * 作业类型sid
     */
    @Excel(name = "作业类型sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "作业类型sid")
    private Long movementType;

    /**
     * 行号
     */
    @Excel(name = "行号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "行号")
    private Integer itemNum;

    @ApiModelProperty(value = "被串款")
    private Long  destMaterialSid;

    @ApiModelProperty(value = "物料&商品sku1(被串款)")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long  destSku1Sid;

    @ApiModelProperty(value = "物料&商品sku2(被串款)")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long  destSku2Sid;

    @ApiModelProperty(value = "物料&商品sku1(被串款)")
    @TableField(exist = false)
    private String  destSku1Name;

    @ApiModelProperty(value = "物料&商品sku2(被串款)")
    @TableField(exist = false)
    private String  destSku2Name;

    @ApiModelProperty(value = "物料&商品sku2(被串款)")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long  destBarcodeSid;
    /**
     * 创建人账号（用户名称）
     */
    @Excel(name = "创建人账号（用户名称）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @ApiModelProperty(value = "商品编码")
    @TableField(exist = false)
    private String materialCode;

    @ApiModelProperty(value = "商品名称")
    @TableField(exist = false)
    private String materialName;

    @TableField(exist = false)
    private String sku1Name;

    @TableField(exist = false)
    private String sku1Code;

    @TableField(exist = false)
    private String sku2Name;


    @TableField(exist = false)
    private String sku2Code;
    /**
     * 更新人账号（用户名称）
     */
    @Excel(name = "更新人账号（用户名称）")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
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
     * 数据源系统（数据字典的键值）
     */
    @Excel(name = "数据源系统（数据字典的键值）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值）")
    private String dataSourceSys;


    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "创建日期开始时间")
    @TableField(exist = false)
    private String beginTime;

    @ApiModelProperty(value = "创建日期结束时间")
    @TableField(exist = false)
    private String endTime;

    @ApiModelProperty(value = "页数")
    @TableField(exist = false)
    private Integer pageNum;

    @ApiModelProperty(value = "每页个数")
    @TableField(exist = false)
    private Integer pageSize;

    @ApiModelProperty(value = "仓库名称")
    @TableField(exist = false)
    private String storehouseName;

    @ApiModelProperty(value = "库位编码")
    @TableField(exist = false)
    private String locationCode;

    @ApiModelProperty(value = "库位名称")
    @TableField(exist = false)
    private String locationName;

    @ApiModelProperty(value = "目标仓库名称")
    @TableField(exist = false)
    private String destStorehouseName;

    @ApiModelProperty(value = "目标库位编码")
    @TableField(exist = false)
    private String destLocationCode;

    @ApiModelProperty(value = "目标库位名称")
    @TableField(exist = false)
    private String destLocationName;

    @ApiModelProperty(value = "基本计量单位名称")
    @TableField(exist = false)
    private String unitBaseName;

    @ApiModelProperty(value = "创建人账号（用户名称）")
    @TableField(exist = false)
    private String creatorAccountName;

    @ApiModelProperty(value = "商品条码")
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long barcode;

    @TableField(exist = false)
    private String firstSort;

    @TableField(exist = false)
    private String secondSort;

    @TableField(exist = false)
    private String thirdSort;

    @ApiModelProperty(value = "标识")
    @TableField(exist = false)
    private String flag;
}
