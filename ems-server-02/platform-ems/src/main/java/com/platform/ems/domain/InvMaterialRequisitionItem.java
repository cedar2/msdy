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

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 领退料单-明细对象 s_inv_material_requisition_item
 *
 * @author linhongwei
 * @date 2021-04-08
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_inv_material_requisition_item")
public class InvMaterialRequisitionItem extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-领退料单明细
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-领退料单明细")
    private Long materialRequisitionItemSid;

    /**
     * 系统SID-领退料单
     */
    @Excel(name = "系统SID-领退料单")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-领退料单")
    private Long materialRequisitionSid;

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

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-仓库")
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
    private String storehouseLocationName;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-库位")
    private String locationName;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-库位")
    private String locationCode;

    /**
     * 需求日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "需求日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "需求日期")
    private Date demandDate;

    /**
     * 行号
     */
    @Excel(name = "行号")
    @ApiModelProperty(value = "行号")
    private Integer itemNum;

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

    @ApiModelProperty(value = "出入库状态")
    private String inOutStockStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "启停状态")
    private String status;

    @TableField(exist = false)
    @ApiModelProperty(value = "基本计量单位")
    private String unitBaseName;

    @TableField(exist = false)
    @ApiModelProperty(value = "创建人名称")
    private String creatorAccountName;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品条码")
    private String barcode;

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


    @ApiModelProperty(value = "款号名称")
    @TableField(exist = false)
    private String productName;

    @ApiModelProperty(value = "款号sid")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long productSid;

    @ApiModelProperty(value = "商品条码sid(适用于物料对应的商品)；只能保存单个")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long productBarcodeSid;

    @ApiModelProperty(value = "款号code")
    private String productCode;

    @ApiModelProperty(value = "款颜色")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long productSku1Sid;

    @TableField(exist = false)
    @ApiModelProperty(value = "款颜色")
    private String productSku1Name;

    @ApiModelProperty(value = "款颜色code")
    private String productSku1Code;

    @ApiModelProperty(value = "款尺码")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long productSku2Sid;

    @TableField(exist = false)
    @ApiModelProperty(value = "款尺码")
    private String productSku2Name;

    @ApiModelProperty(value = "款尺码")
    private String productSku2Code;

    @ApiModelProperty(value = "款备注")
    private String productCodes;

    @ApiModelProperty(value = "款颜色备注")
    private String productSku1Names;

    @ApiModelProperty(value = "款尺码备注")
    private String productSku2Names;

    @ApiModelProperty(value = "款数量")
    private BigDecimal productQuantity;

    @ApiModelProperty(value = "库存预留状态")
    private String reserveStatus;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "领退料单号")
    private Long materialRequisitionCode;

    @ApiModelProperty(value = "供应商sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(exist = false)
    private Long vendorSid;

    @ApiModelProperty(value = "客户sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(exist = false)
    private Long customerSid;

    @Excel(name = "特殊库存（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "特殊库存（数据字典的键值或配置档案的编码）")
    @TableField(exist = false)
    private String specialStock;

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
