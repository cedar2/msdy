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
import java.util.List;

/**
 * 供应商特殊库存（寄售/甲供料）对象 s_inv_ven_special_inventory
 *
 * @author linhongwei
 * @date 2021-06-01
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_inv_ven_special_inventory")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InvVenSpecialInventory extends EmsBaseEntity{

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-供应商特殊库存信息
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商特殊库存信息")
    private Long vendorSpecialStockSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] vendorSpecialStockSidList;
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
     * 系统SID-供应商档案
     */
    @Excel(name = "系统SID-供应商档案")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商档案")
    private Long vendorSid;

    /**
     * 基本计量单位（数据字典的键值）
     */
    @Excel(name = "基本计量单位（数据字典的键值）")
    @ApiModelProperty(value = "基本计量单位（数据字典的键值）")
    private String unitBase;

    /**
     * 系统SID-仓库档案
     */
    @Excel(name = "系统SID-仓库档案")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-仓库档案")
    private Long storehouseSid;

    /**
     * 系统SID-库位
     */
    @Excel(name = "系统SID-库位")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-库位")
    private Long storehouseLocationSid;

    /**
     * 特殊库存（数据字典的键值）
     */
    @Excel(name = "特殊库存（数据字典的键值）")
    @ApiModelProperty(value = "特殊库存（数据字典的键值）")
    private String specialStock;

    /**
     * 总库存量
     */
    @Excel(name = "总库存量")
    @ApiModelProperty(value = "总库存量")
    private BigDecimal totalQuantity;

    /**
     * 非限制库存量（非限制使用的库存）
     */
    @Excel(name = "非限制库存量（非限制使用的库存）")
    @ApiModelProperty(value = "非限制库存量（非限制使用的库存）")
    private BigDecimal unlimitedQuantity;

    /**
     * 在检库存量（处于质检的库存）
     */
    @Excel(name = "在检库存量（处于质检的库存）")
    @ApiModelProperty(value = "在检库存量（处于质检的库存）")
    private BigDecimal qualityTestedQuantity;

    /**
     * 冻结库存量（被冻结的库存）
     */
    @Excel(name = "冻结库存量（被冻结的库存）")
    @ApiModelProperty(value = "冻结库存量（被冻结的库存）")
    private BigDecimal freezedQuantity;

    /**
     * 首次更新库存时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "首次更新库存时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "首次更新库存时间")
    private Date firstUpdateStockDate;

    /**
     * 最近更新库存时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "最近更新库存时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "最近更新库存时间")
    private Date latestUpdateStockDate;

    /**
     * 最近盘点时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "最近盘点时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "最近盘点时间")
    private Date latestCountDate;

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
     * 确认人账号（用户名称）
     */
    @Excel(name = "确认人账号（用户名称）")
    @ApiModelProperty(value = "确认人账号（用户名称）")
    private String confirmerAccount;

    /**
     * 确认时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "确认时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 数据源系统（数据字典的键值）
     */
    @Excel(name = "数据源系统（数据字典的键值）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值）")
    private String dataSourceSys;

    @TableField(exist = false)
    @Excel(name = "仓库名称")
    @ApiModelProperty(value = "仓库名称")
    private String storehouseName;

    @TableField(exist = false)
    @Excel(name = "仓库编码")
    @ApiModelProperty(value = "仓库编码")
    private String storehouseCode;

    @TableField(exist = false)
    @Excel(name = "库位名称")
    @ApiModelProperty(value = "库位名称")
    private String locationName;

    @TableField(exist = false)
    @Excel(name = "库位编码")
    @ApiModelProperty(value = "库位编码")
    private String locationCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "库位编码")
    private String storehouseLocationCode;

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

    @TableField(exist = false)
    @Excel(name = "供应商名称")
    @ApiModelProperty(value = "供应商名称")
    private String vendorName;

    @TableField(exist = false)
    @Excel(name = "供应商编码")
    @ApiModelProperty(value = "供应商编码")
    private String vendorCode;

    @TableField(exist = false)
    @Excel(name = "客户sid")
    @ApiModelProperty(value = "客户sid")
    private String customerSid;

    @TableField(exist = false)
    @Excel(name = "客户名称")
    @ApiModelProperty(value = "客户名称")
    private String customerName;

    @Excel(name = "特殊库存名称")
    @ApiModelProperty(value = "特殊库存（数据字典的键值）")
    @TableField(exist = false)
    private String specialStockName;

    @Excel(name = "基本计量单位")
    @ApiModelProperty(value = "基本计量单位")
    @TableField(exist = false)
    private String unitBaseName;

    @ApiModelProperty(value = "查询：特殊库存")
    @TableField(exist = false)
    private List<String> specialStockList;

    @ApiModelProperty(value = "查询：供应商")
    @TableField(exist = false)
    private Long[] vendorSidList;

    @ApiModelProperty(value = "查询：客户")
    @TableField(exist = false)
    private Long[] customerSidList;

    @ApiModelProperty(value = "查询：库位")
    @TableField(exist = false)
    private Long[] storehouseLocationSidList;

    @ApiModelProperty(value = "查询：物料分类")
    @TableField(exist = false)
    private Long[] materialClassSidList;

    @ApiModelProperty(value = "查询：物料类型")
    @TableField(exist = false)
    private String[] materialTypeList;

    @ApiModelProperty(value = "查询：是否显示0库存")
    @TableField(exist = false)
    private String whether;

    @ApiModelProperty(value = "库存类型")
    @TableField(exist = false)
    private String type;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品条码")
    private String barcode;

    @Excel(name = "物料类型")
    @TableField(exist = false)
    @ApiModelProperty(value ="物料类型")
    private String materialTypeName;

    @TableField(exist = false)
    @ApiModelProperty(value = "可用库存")
    private BigDecimal ableQuantity;

    @TableField(exist = false)
    @ApiModelProperty(value = "预留库存")
    private BigDecimal obligateQuantity;

    @ApiModelProperty(value = "查询：仓库(多选)")
    @TableField(exist = false)
    private Long[] storehouseSidList;
}


