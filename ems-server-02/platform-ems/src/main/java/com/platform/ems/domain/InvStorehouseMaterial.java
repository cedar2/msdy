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
 * 仓库物料信息对象 s_inv_storehouse_material
 *
 * @author linhongwei
 * @date 2022-02-12
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_inv_storehouse_material")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InvStorehouseMaterial extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-仓库库位库存信息
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-仓库库位库存信息")
    private Long storehouseMaterialSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] storehouseMaterialSidList;
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
     * 系统SID-仓库档案
     */
    @Excel(name = "系统SID-仓库档案")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-仓库档案")
    private Long storehouseSid;

    /**
     * 库存价
     */
    @Excel(name = "库存价")
    @ApiModelProperty(value = "库存价")
    private BigDecimal price;

    /**
     * 库存价（加权平均价）
     */
    @Excel(name = "库存价（加权平均价）")
    @ApiModelProperty(value = "库存价（加权平均价）")
    private BigDecimal priceAverage;

    /**
     * 库存价（标准统一价）
     */
    @Excel(name = "库存价（标准统一价）")
    @ApiModelProperty(value = "库存价（标准统一价）")
    private BigDecimal priceUniform;

    @ApiModelProperty(value = "使用频率标识（数据字典的键值或配置档案的编码）")
    private String usageFrequencyFlag;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "最近一次采购入库日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "最近一次采购入库日期")
    private Date latestPurchaseEntryDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "最近一次采购入库日期:是否更改：Y是N否")
    private String latestPurchaseEntryDateUpd;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "最近一次生产入库日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "最近一次生产入库日期")
    private Date latestManufactEntryDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "最近一次生产入库日期:是否更改：Y是N否")
    private String latestManufactEntryDateUpd;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "最近一次调拨入库日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "最近一次调拨入库日期")
    private Date latestTransferEntryDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "最近一次调拨入库日期:是否更改：Y是N否")
    private String latestTransferEntryDateUpd;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "最近一次入库日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "最近一次入库日期")
    private Date latestStockEntryDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "最近一次入库日期:是否更改：Y是N否")
    private String latestStockEntryDateUpd;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "最近一次销售出库日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "最近一次销售出库日期")
    private Date latestSaleOutDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "最近一次销售出库日期:是否更改：Y是N否")
    private String latestSaleOutDateUpd;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "最近一次调拨出库日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "最近一次调拨出库日期")
    private Date latestTransferOutDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "最近一次调拨出库日期:是否更改：Y是N否")
    private String latestTransferOutDateUpd;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "最近一次领料出库日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "最近一次领料出库日期")
    private Date latestRequisitionOutDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "最近一次领料出库日期:是否更改：Y是N否")
    private String latestRequisitionOutDateUpd;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "最近一次出库日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "最近一次出库日期")
    private Date latestStockOutDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "最近一次出库日期:是否更改：Y是N否")
    private String latestStockOutDateUpd;

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
     * 确认人账号（用户账号）
     */
    @Excel(name = "确认人账号（用户账号）")
    @ApiModelProperty(value = "确认人账号（用户账号）")
    private String confirmerAccount;

    /**
     * 确认时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "确认时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

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

    @TableField(exist = false)
    private String confirmerAccountName;

}
