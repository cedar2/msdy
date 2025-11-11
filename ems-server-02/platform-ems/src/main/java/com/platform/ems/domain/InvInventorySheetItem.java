package com.platform.ems.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.core.domain.BaseEntity;
import com.platform.common.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.experimental.Accessors;

/**
 * 盘点单-明细对象 s_inv_inventory_sheet_item
 *
 * @author linhongwei
 * @date 2021-04-20
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_inv_inventory_sheet_item")
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class InvInventorySheetItem extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-盘点单明细
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-盘点单明细")
    private Long inventorySheetItemSid;

    /**
     * 系统SID-盘点单
     */
    @Excel(name = "系统SID-盘点单")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-盘点单")
    private Long inventorySheetSid;

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

    /**
     * 系统SID-物料&商品条码
     */
    @Excel(name = "系统SID-物料&商品条码")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品条码")
    private Long barcodeSid;

    /**
     * 库存类型（数据字典的键值）
     */
    @Excel(name = "库存类型（数据字典的键值）")
    @ApiModelProperty(value = "库存类型（数据字典的键值）")
    private String stockType;

    /** 系统SID-仓库档案 */
    @NotNull(message = "明细仓库不能为空")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-仓库档案")
    private Long storehouseSid;

    @ApiModelProperty(value = "系统SID-仓库档案")
    private String storehouseCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-仓库档案")
    private String storehouseName;

    /** 系统SID-库位 */
    @NotNull(message = "明细库位不能为空")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-库位")
    private Long storehouseLocationSid;

    @ApiModelProperty(value = "系统SID-库位")
    private String storehouseLocationCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-库位")
    private String locationCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-库位")
    private String storehouseLocationName;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-库位")
    private String locationName;

    /**
     * 库存量
     */
    @Excel(name = "库存量")
    @ApiModelProperty(value = "库存量")
    private BigDecimal stockQuantity;

    @TableField(exist = false)
    @ApiModelProperty(value = "非限制库存量（非限制使用的库存）")
    private BigDecimal unlimitedQuantity;

    /**
     * 实盘量
     */
    @Excel(name = "实盘量")
    @ApiModelProperty(value = "实盘量")
    private BigDecimal countQuantity;

    /**
     * 基本计量单位（数据字典的键值）
     */
    @Excel(name = "基本计量单位（数据字典的键值）")
    @ApiModelProperty(value = "基本计量单位（数据字典的键值）")
    private String unitBase;

    /**
     * 价格（移动平均价）
     */
    @Excel(name = "价格（移动平均价）")
    @ApiModelProperty(value = "价格（移动平均价）")
    private BigDecimal price;

    /**
     * 批次号
     */
    @Excel(name = "批次号")
    @ApiModelProperty(value = "批次号")
    private String batchNum;

    /**
     * 原因类型（数据字典的键值）
     */
    @Excel(name = "原因类型（数据字典的键值）")
    @ApiModelProperty(value = "原因类型（数据字典的键值）")
    private String reasonType;

    /**
     * 行号
     */
    @Excel(name = "行号")
    @ApiModelProperty(value = "行号")
    private Integer itemNum;

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


    @ApiModelProperty(value = "盘点结果标识")
    private String stockCountResult;


    @ApiModelProperty(value = "页数")
    @TableField(exist = false)
    private Integer pageNum;

    @ApiModelProperty(value = "每页个数")
    @TableField(exist = false)
    private Integer pageSize;

    @TableField(exist = false)
    @ApiModelProperty(value ="物料名称")
    private String materialName;

    @TableField(exist = false)
    @ApiModelProperty(value ="物料编码")
    private String materialCode;

    @TableField(exist = false)
    private String sku1Code;

    @TableField(exist = false)
    private String sku1Name;

    @TableField(exist = false)
    private String sku2Code;

    @TableField(exist = false)
    private String sku2Name;

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
    @ApiModelProperty(value = "商品条码")
    private String barcode2;

    @TableField(exist = false)
    @ApiModelProperty(value = "使用频率标识（数据字典的键值或配置档案的编码）")
    private String usageFrequencyFlag;

    @TableField(exist = false)
    @ApiModelProperty(value = "使用频率标识（数据字典的键值或配置档案的编码）")
    private String usageFrequencyFlagName;

    @TableField(exist = false)
    private String firstSort;

    @TableField(exist = false)
    private String secondSort;

    @TableField(exist = false)
    private String thirdSort;

}
