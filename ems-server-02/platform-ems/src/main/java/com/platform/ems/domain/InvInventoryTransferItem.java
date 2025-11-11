package com.platform.ems.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 调拨单-明细对象 s_inv_inventory_transfer_item
 *
 * @author linhongwei
 * @date 2021-06-04
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_inv_inventory_transfer_item")
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class InvInventoryTransferItem extends EmsBaseEntity{

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-调拨单明细
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-调拨单明细")
    private Long inventoryTransferItemSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] inventoryTransferItemSidList;
    /**
     * 系统SID-调拨单
     */
    @Excel(name = "系统SID-调拨单")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-调拨单")
    private Long inventoryTransferSid;

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
     * 系统SID-商品条码（物料&商品&服务）
     */
    @Excel(name = "系统SID-商品条码（物料&商品&服务）")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品条码（物料&商品&服务）")
    private Long barcodeSid;

    /**
     * 基本计量单位（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "基本计量单位（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "基本计量单位（数据字典的键值或配置档案的编码）")
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

    @ApiModelProperty(value = "仓库编码")
    private String storehouseCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "仓库")
    private String storehouseName;

    @NotNull(message = "明细库位不能为空")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-库位")
    private Long storehouseLocationSid;

    @ApiModelProperty(value = "库位编码")
    private String storehouseLocationCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-库位")
    private String locationCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "库位")
    private String locationName;

    @NotNull(message = "明细目的仓库不能为空")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-仓库档案（目的仓库）")
    private Long destStorehouseSid;

    @ApiModelProperty(value = "目的仓库编码")
    private String destStorehouseCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "目的仓库")
    private String destStorehouseName;

    @NotNull(message = "明细目的库位不能为空")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-库位（目的库位）")
    private Long destStorehouseLocationSid;

    @ApiModelProperty(value = "目的库位编码")
    private String destStorehouseLocationCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "目的库位")
    private String destLocationCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "目的库位")
    private String destLocationName;

    /**
     * 原因类型（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "原因类型（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "原因类型（数据字典的键值或配置档案的编码）")
    private String reasonType;

    /**
     * 行号
     */
    @Excel(name = "行号")
    @JsonSerialize(using = ToStringSerializer.class)
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
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "数据源系统（数据字典的键值或配置档案的编码）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料名称")
    private String materialName;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料编码")
    private String materialCode;

    @TableField(exist = false)
    private String sku1Code;

    @TableField(exist = false)
    private String sku1Name;

    @TableField(exist = false)
    private String sku2Code;

    @TableField(exist = false)
    private String sku2Name;

    @ApiModelProperty(value = "基本计量单位")
    @TableField(exist = false)
    private String unitBaseName;

    @ApiModelProperty(value = "库存预留状态")
    private String reserveStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "启停状态")
    private String status;

    @ApiModelProperty(value = "创建人账号（用户名称）")
    @TableField(exist = false)
    private String creatorAccountName;

    @ApiModelProperty(value = "商品条码")
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long barcode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "调拨单号")
    @TableField(exist = false)
    private Long inventoryTransferCode;

    @Excel(name = "特殊库存（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "特殊库存（数据字典的键值或配置档案的编码）")
    @TableField(exist = false)
    private String specialStock;

    /**
     * 特殊库存供应商sid
     */
    @Excel(name = "特殊库存供应商sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "特殊库存供应商sid")
    @TableField(exist = false)
    private Long vendorSid;

    /**
     * 特殊库存客户sid
     */
    @Excel(name = "特殊库存客户sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "特殊库存客户sid")
    @TableField(exist = false)
    private Long customerSid;

    @ApiModelProperty(value = "所用商品信息")
    @TableField(exist = false)
    private List<InvInventoryTransferMaterialProduct> materialProductList;

    /** 商品编码备注(适用于物料对应的商品)，如合格证、洗唛；可以保存多个 */
    @Excel(name = "商品编码备注(适用于物料对应的商品)，如合格证、洗唛；可以保存多个")
    @ApiModelProperty(value = "商品编码备注(适用于物料对应的商品)，如合格证、洗唛；可以保存多个")
    private String productCodes;

    /** 商品sku1名称备注(适用于物料对应的商品)；可以保存多个 */
    @Excel(name = "商品sku1名称备注(适用于物料对应的商品)；可以保存多个")
    @ApiModelProperty(value = "商品sku1名称备注(适用于物料对应的商品)；可以保存多个")
    private String productSku1Names;

    /** 商品sku2名称备注(适用于物料对应的商品)；可以保存多个 */
    @Excel(name = "商品sku2名称备注(适用于物料对应的商品)；可以保存多个")
    @ApiModelProperty(value = "商品sku2名称备注(适用于物料对应的商品)；可以保存多个")
    private String productSku2Names;

    /** 商品销售订单号备注(适用于物料对应的商品的销售订单号)；可以保存多个 */
    @Excel(name = "商品销售订单号备注(适用于物料对应的商品的销售订单号)；可以保存多个")
    @ApiModelProperty(value = "商品销售订单号备注(适用于物料对应的商品的销售订单号)；可以保存多个")
    private String productSoCodes;

    /** 款数量备注(适用于物料对应的商品)；可以保存多个 */
    @Excel(name = "款数量备注(适用于物料对应的商品)；可以保存多个")
    @ApiModelProperty(value = "款数量备注(适用于物料对应的商品)；可以保存多个")
    private String productQuantityRemark;

    /** 商品需求方备注(适用于物料对应的商品的需求方)；可以保存多个；如：客户、供应商 */
    @Excel(name = "商品需求方备注(适用于物料对应的商品的需求方)；可以保存多个；如：客户、供应商")
    @ApiModelProperty(value = "商品需求方备注(适用于物料对应的商品的需求方)；可以保存多个；如：客户、供应商")
    private String productRequestPartys;

    /** 商品业务类型备注(适用于物料对应的商品的业务类型)；可以保存多个 */
    @Excel(name = "商品业务类型备注(适用于物料对应的商品的业务类型)；可以保存多个")
    @ApiModelProperty(value = "商品业务类型备注(适用于物料对应的商品的业务类型)；可以保存多个")
    private String productRequestBusType;

    @TableField(exist = false)
    private String firstSort;

    @TableField(exist = false)
    private String secondSort;

    @TableField(exist = false)
    private String thirdSort;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料档案规格尺寸")
    private String specificationSize;
}
