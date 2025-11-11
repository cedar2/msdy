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

import java.math.BigDecimal;
import java.util.Date;

/**
 * 调班单-物料所用于商品信息对象 s_inv_inventory_transfer_material_product
 *
 * @author linhongwei
 * @date 2022-06-15
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_inv_inventory_transfer_material_product")
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class InvInventoryTransferMaterialProduct extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-调拨单物料所用于的商品信息（此表仅用于原材料对应的商品）
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-调拨单物料所用于的商品信息（此表仅用于原材料对应的商品）")
    private Long itemMaterialProductSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] itemMaterialProductSidList;
    /**
     * 系统SID-调拨单明细
     */
    @Excel(name = "系统SID-调拨单明细")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-调拨单明细")
    private Long inventoryTransferItemSid;

    /**
     * 系统SID-调拨单
     */
    @Excel(name = "系统SID-调拨单")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-调拨单")
    private Long inventoryTransferSid;

    /**
     * 系统SID-商品(物料对应的商品)
     */
    @Excel(name = "系统SID-商品(物料对应的商品)")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品(物料对应的商品)")
    private Long productSid;

    /**
     * 系统SID-商品sku1(物料对应的商品)
     */
    @Excel(name = "系统SID-商品sku1(物料对应的商品)")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品sku1(物料对应的商品)")
    private Long productSku1Sid;

    /**
     * 系统SID-商品sku2(物料对应的商品)
     */
    @Excel(name = "系统SID-商品sku2(物料对应的商品)")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品sku2(物料对应的商品)")
    private Long productSku2Sid;

    /**
     * 系统SID-商品条码(物料对应的商品)
     */
    @Excel(name = "系统SID-商品条码(物料对应的商品)")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品条码(物料对应的商品)")
    private Long productBarcodeSid;

    /**
     * 数量（物料）
     */
    @Excel(name = "数量（物料）")
    @ApiModelProperty(value = "数量（物料）")
    private BigDecimal quantityMaterial;

    /**
     * 数量（商品）
     */
    @Excel(name = "数量（商品）")
    @ApiModelProperty(value = "数量（商品）")
    private BigDecimal quantityProduct;

    /**
     * 基本计量单位_物料（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "基本计量单位_物料（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "基本计量单位_物料（数据字典的键值或配置档案的编码）")
    private String unitBaseMaterial;

    /**
     * 基本计量单位_商品（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "基本计量单位_商品（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "基本计量单位_商品（数据字典的键值或配置档案的编码）")
    private String unitBaseProduct;

    /**
     * 行号
     */
    @Excel(name = "行号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "行号")
    private Long itemNum;

    /**
     * 来源单据对象类别
     */
    @Excel(name = "来源单据对象类别")
    @ApiModelProperty(value = "来源单据对象类别")
    private String referDocCategory;

    /**
     * 来源单据sid
     */
    @Excel(name = "来源单据sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "来源单据sid")
    private Long referDocSid;

    /**
     * 来源单据单号code
     */
    @Excel(name = "来源单据单号code")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "来源单据单号code")
    private Long referDocCode;

    /**
     * 来源单据行sid
     */
    @Excel(name = "来源单据行sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "来源单据行sid")
    private Long referDocItemSid;

    /**
     * 来源单据行号
     */
    @Excel(name = "来源单据行号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "来源单据行号")
    private Long referDocItemNum;

    /**
     * 商品销售订单sid
     */
    @Excel(name = "商品销售订单sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "商品销售订单sid")
    private Long salesOrderSid;

    /**
     * 商品销售订单号code
     */
    @Excel(name = "商品销售订单号code")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "商品销售订单号code")
    private Long salesOrderCode;

    /**
     * 商品销售订单明细行sid
     */
    @Excel(name = "商品销售订单明细行sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "商品销售订单明细行sid")
    private Long salesOrderItemSid;

    /**
     * 商品销售订单明细行号
     */
    @Excel(name = "商品销售订单明细行号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "商品销售订单明细行号")
    private Long salesOrderItemNum;

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


    @TableField(exist = false)
    private String creatorAccountName;

    @TableField(exist = false)
    private String updaterAccountName;

    @ApiModelProperty(value = "来源单据对象类别")
    @TableField(exist = false)
    private String referDocCategoryName;

    @ApiModelProperty(value = "来源款号")
    @TableField(exist = false)
    private String productCode;

    @ApiModelProperty(value = "来源款名")
    @TableField(exist = false)
    private String productName;

    @ApiModelProperty(value = "来源款颜色")
    @TableField(exist = false)
    private String productSku1Name;

    @ApiModelProperty(value = "来源款尺码")
    @TableField(exist = false)
    private String productSku2Name;


}
