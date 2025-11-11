package com.platform.ems.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

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
 * 库存凭证-明细对象 s_inv_inventory_document_item
 *
 * @author linhongwei
 * @date 2021-04-16
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_inv_inventory_document_item")
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class InvInventoryDocumentItem extends EmsBaseEntity {

    /** 租户ID */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /** 系统SID-库存凭证明细 */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-库存凭证明细")
    private Long inventoryDocumentItemSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long [] inventoryDocumentItemSidList;
    /** 系统SID-库存凭证 */
    @Excel(name = "系统SID-库存凭证")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-库存凭证")
    private Long inventoryDocumentSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-关联业务单")
    private Long referDocumentSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-关联业务单")
    private String referDocumentCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-关联业务单行")
    private Long referDocumentItemSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-关联业务单行")
    private Long[] referDocumentItemSidList;

    @ApiModelProperty(value = "来源单据类别")
    private String referDocCategory;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-关联业务单行")
    private Integer referDocumentItemNum;

    /** 系统SID-物料&商品&服务 */
    @Excel(name = "系统SID-物料&商品&服务")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品&服务")
    private Long materialSid;

    /** 系统SID-物料&商品sku1 */
    @Excel(name = "系统SID-物料&商品sku1")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品sku1")
    private Long sku1Sid;

    /** 系统SID-物料&商品sku2 */
    @Excel(name = "系统SID-物料&商品sku2")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品sku2")
    private Long sku2Sid;

    /** 系统SID-物料&商品条码 */
    @Excel(name = "系统SID-物料&商品条码")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品条码")
    private Long barcodeSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-物料&商品条码")
    private String barcode;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-物料&商品条码")
    private String barcode2;

    /** 基本计量单位（数据字典的键值） */
    @Excel(name = "基本计量单位（数据字典的键值）")
    @ApiModelProperty(value = "基本计量单位（数据字典的键值）")
    private String unitBase;

    /** 数量 */
    @Excel(name = "数量")
    @ApiModelProperty(value = "数量")
    private BigDecimal quantity;

    /** 价格（移动平均价）-作业前 */
    @Excel(name = "价格（移动平均价）-作业前")
    @ApiModelProperty(value = "价格（移动平均价）-作业前")
    private BigDecimal price;

    /** 系统SID-仓库档案 */
    @NotNull(message = "明细的仓库不能为空")
    @Excel(name = "系统SID-仓库档案")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-仓库档案")
    private Long storehouseSid;

    /** 系统SID-库位 */
    @NotNull(message = "明细的库位不能为空")
    @Excel(name = "系统SID-库位")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-库位")
    private Long storehouseLocationSid;

    /** 系统SID-仓库档案（目的仓库） */
    @Excel(name = "系统SID-仓库档案（目的仓库）")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-仓库档案（目的仓库）")
    private Long destStorehouseSid;

    /** 系统SID-库位（目的库位） */
    @Excel(name = "系统SID-库位（目的库位）")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-库位（目的库位）")
    private Long destStorehouseLocationSid;


    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "被串商品条码")
    @TableField(exist = false)
    private Long destBarcodeSid;

    /** 原因类型（数据字典的键值） */
    @Excel(name = "原因类型（数据字典的键值）")
    @ApiModelProperty(value = "原因类型（数据字典的键值）")
    private String reasonType;


    /** 库位总库存金额（作业前） */
    @Excel(name = "库位总库存金额（作业前）")
    @ApiModelProperty(value = "库位总库存金额（作业前）")
    private BigDecimal currencyAmountBefore;

    /** 库位总库存金额（作业后） */
    @Excel(name = "库位总库存金额（作业后）")
    @ApiModelProperty(value = "库位总库存金额（作业后）")
    private BigDecimal currencyAmountAfter;

    /** 交易类型（数据字典的键值） */
    @Excel(name = "交易类型（数据字典的键值）")
    @ApiModelProperty(value = "交易类型（数据字典的键值）")
    private String tradeType;

    /** 价格计量单位（数据字典的键值）采购订单/销售订单 */
    @Excel(name = "价格计量单位（数据字典的键值）采购订单/销售订单")
    @ApiModelProperty(value = "价格计量单位（数据字典的键值）采购订单/销售订单")
    private String unitPrice;

    /** 价格计量单位对应的出入库数量（采购订单/销售订单） */
    @Excel(name = "价格计量单位对应的出入库数量（采购订单/销售订单）")
    @ApiModelProperty(value = "价格计量单位对应的出入库数量（采购订单/销售订单）")
    private BigDecimal priceQuantity;

    /** 单位换算比例（价格单位/基本单位） */
    @Excel(name = "单位换算比例（价格单位/基本单位）")
    @ApiModelProperty(value = "单位换算比例（价格单位/基本单位）")
    private BigDecimal unitConversionRate;

    /** 库位总库存量（作业前） */
    @Excel(name = "库位总库存量（作业前）")
    @ApiModelProperty(value = "库位总库存量（作业前）")
    private BigDecimal totalQuantityBefore;

    /** 库位价格或加权平均价（作业前） */
    @Excel(name = "库位价格或加权平均价（作业前）")
    @ApiModelProperty(value = "库位价格或加权平均价（作业前）")
    private BigDecimal priceBefore;

    /** 库位总库存量（作业后） */
    @Excel(name = "库位总库存量（作业后）")
    @ApiModelProperty(value = "库位总库存量（作业后）")
    private BigDecimal totalQuantityAfter;

    /** 库位价格或加权平均价（作业后） */
    @Excel(name = "库位价格或加权平均价（作业后）")
    @ApiModelProperty(value = "库位价格或加权平均价（作业后）")
    private BigDecimal priceAfter;

    /** 行号 */
    @Excel(name = "行号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "行号")
    private Integer itemNum;

    /** 创建人账号（用户名称） */
    @Excel(name = "创建人账号（用户名称）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    /** 创建时间 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /** 更新人账号（用户名称） */
    @Excel(name = "更新人账号（用户名称）")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

    /** 更新时间 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /** 数据源系统（数据字典的键值） */
    @Excel(name = "数据源系统（数据字典的键值）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值）")
    private String dataSourceSys;

    @TableField(exist = false)
    @ApiModelProperty(value ="物料名称")
    private String materialName;

    @TableField(exist = false)
    @ApiModelProperty(value ="物料编码")
    private String materialCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "我司样衣号")
    private String sampleCodeSelf;

    @TableField(exist = false)
    @ApiModelProperty(value ="物料类型")
    private String materialType;

    @TableField(exist = false)
    @ApiModelProperty(value ="物料类型")
    private String materialTypeName;

    @TableField(exist = false)
    private String sku1Code;

    @TableField(exist = false)
    private String sku1Name;

    @TableField(exist = false)
    private String sku2Code;

    @TableField(exist = false)
    private String sku2Name;

    @TableField(exist = false)
    @ApiModelProperty(value = "sku1的序号")
    private BigDecimal sort1;

    @TableField(exist = false)
    @ApiModelProperty(value = "sku2的序号")
    private BigDecimal sort2;

    @TableField(exist = false)
    @ApiModelProperty(value ="仓库编码")
    private String storehouseCode;

    @TableField(exist = false)
    @ApiModelProperty(value ="仓库名称")
    private String storehouseName;

    @TableField(exist = false)
    @ApiModelProperty(value ="库位编码")
    private String locationCode;

    @TableField(exist = false)
    @ApiModelProperty(value ="库位名称")
    private String locationName;

    @TableField(exist = false)
    @ApiModelProperty(value ="目标仓库编码")
    private String destStorehouseCode;

    @TableField(exist = false)
    @ApiModelProperty(value ="目标仓库名称")
    private String destStorehouseName;

    @TableField(exist = false)
    @ApiModelProperty(value ="目标库位编码")
    private String destLocationCode;

    @TableField(exist = false)
    @ApiModelProperty(value ="目标库位名称")
    private String destLocationName;

    @TableField(exist = false)
    @ApiModelProperty(value ="库存信息")
    private InvInventoryLocation location;

    @TableField(exist = false)
    @ApiModelProperty(value ="供应商特殊库存（寄售/甲供料）对象")
    private InvVenSpecialInventory  invVenSpecialInventory;

    @TableField(exist = false)
    @ApiModelProperty(value ="客户特殊库存（寄售/客供料）对象")
    private InvCusSpecialInventory  invCusSpecialInventory;

    @TableField(exist = false)
    @ApiModelProperty(value ="采购订单行号")
    private String purchaseOrderItemNum;

    /**
     * 系统自增长ID-采购订单
     */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-采购订单")
    private Long purchaseOrderSid;

    /**
     * 采购订单号
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "采购订单号")
    private String purchaseOrderCode;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购合同号")
    private Long purchaseContractSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "采购合同号")
    private String purchaseContractCode;

    /**
     * 系统自增长ID-销售订单
     */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-销售订单")
    private Long salesOrderSid;

    /**
     * 销售订单号
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "销售订单号")
    private String salesOrderCode;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售合同号")
    private Long saleContractSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售合同号")
    private String saleContractCode;

    @TableField(exist = false)
    @ApiModelProperty(value ="生产工序行号")
    private String manufactureOrderProcessNum;

    @TableField(exist = false)
    @ApiModelProperty(value = "调拨单明细sid")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long inventoryTransferItemSid;

    @ApiModelProperty(value = "明细报表-库存凭证号")
    @TableField(exist = false)
    private Long inventoryDocumentCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "明细报表-供应商名称")
    private String vendorName;

    @TableField(exist = false)
    @ApiModelProperty(value = "明细报表-客户名称")
    private String customerName;

    @TableField(exist = false)
    @ApiModelProperty(value = "明细报表-作业类型名称")
    private String movementTypeName;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @TableField(exist = false)
    @ApiModelProperty(value = "明细报表-出入库日期（过账）")
    private Date accountDate;

    @TableField(exist = false)
    private List<Long> ids;

    @TableField(exist = false)
    @ApiModelProperty(value = "是否生成应收暂估")
    private String isFinanceBookYszg;

    @TableField(exist = false)
    @ApiModelProperty(value = "是否生成应付暂估")
    private String isFinanceBookYfzg;

    @ApiModelProperty(value = "税率")
    private BigDecimal taxRate;

    @TableField(exist = false)
    @ApiModelProperty(value = "关联采购订单/销售订单的税率")
    private BigDecimal orderTaxRate;

    @TableField(exist = false)
    @ApiModelProperty(value = "关联采购订单/销售订单的是否免费")
    private String orderFreeFlag;

    @ApiModelProperty(value = "销售价/采购价（含税）")
    @TableField(exist = false)
    private BigDecimal priceTax;

    @ApiModelProperty(value = "价格（不含税）")
    @TableField(exist = false)
    private BigDecimal ExcludingPriceTax;

    @ApiModelProperty(value = "应收应付报表价格")
    @TableField(exist = false)
    private BigDecimal invPriceTax;

    @ApiModelProperty(value = "应收应付报表价格")
    @TableField(exist = false)
    private BigDecimal invPrice;

    @ApiModelProperty(value = "物料&商品sku1(被串款)")
    @TableField(exist = false)
    private Long  destSku1Sid;

    @ApiModelProperty(value = "物料&商品sku2(被串款)")
    @TableField(exist = false)
    private Long  destSku2Sid;

    @ApiModelProperty(value = "基本计量单位名称")
    @TableField(exist = false)
    private String unitBaseName;

    @ApiModelProperty(value = "价格单位名称")
    @TableField(exist = false)
    private String unitPriceName;

    @ApiModelProperty(value = "创建人账号（用户名称）")
    @TableField(exist = false)
    private String creatorAccountName;

    @ApiModelProperty(value = "预付款付款方式")
    @TableField(exist = false)
    private String advanceSettleMode;

    @ApiModelProperty(value = "业务单量")
    @TableField(exist = false)
    private BigDecimal businessQuantity;

    @ApiModelProperty(value = "库存价")
    @TableField(exist = false)
    private BigDecimal locationPrice;


    @Excel(name = "免费")
    @ApiModelProperty(value = "免费")
    private String freeFlag;

    @ApiModelProperty(value = "盘点结果标识")
    private String stockCountResult;

    @ApiModelProperty(value = "出入库状态")
    @TableField(exist = false)
    private String inOutStockStatus;

    @ApiModelProperty(value = "启停状态")
    @TableField(exist = false)
    private String status;

    @ApiModelProperty(value = "商品条码重复后应扣减的库存量")
    @TableField(exist = false)
    private BigDecimal locationQuantity;

    @TableField(exist = false)
    @ApiModelProperty(value = "款号")
    private String productCode;

    @ApiModelProperty(value = "样品出库用途")
    private String sampleUsage;

    @TableField(exist = false)
    @ApiModelProperty(value = "样品出库用途")
    private String sampleUsageName;

    @ApiModelProperty(value = "用途说明")
    private String  usageRemark;

    @ApiModelProperty(value = "货源说明")
    private String  sourceRemark;

    @ApiModelProperty(value = "商品编码备注(适用于物料对应的商品)，如合格证、洗唛；可以保存多个")
    private String productCodes;

    @ApiModelProperty(value = "商品sku1名称备注(适用于物料对应的商品)；可以保存多个")
    private String productSku1Names;

    @ApiModelProperty(value = "商品sku2名称备注(适用于物料对应的商品)；可以保存多个")
    private String productSku2Names;

    @ApiModelProperty(value = "款数量备注(适用于物料对应的商品)；可以保存多个")
    private String productQuantityRemark;

    @TableField(exist = false)
    private String firstSort;

    @TableField(exist = false)
    private String secondSort;

    @TableField(exist = false)
    private String thirdSort;

    @TableField(exist = false)
    @ApiModelProperty(value = "库存凭证sids")
    private Long[] inventoryDocumentSids;

    @ApiModelProperty(value = "排产批次号")
    private  Integer paichanBatch;

    @TableField(exist = false)
    @ApiModelProperty(value = "其它出入库对应的业务标识（数据字典的键值或配置档案的编码）")
    private String businessFlag;

    @TableField(exist = false)
    @ApiModelProperty(value = "凭证类别编码，用于表示：入库、出库、移库、调拨、库存调整、盘点、领退料")
    private String documentCategory;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "特殊库存供应商sid/无采购订单或采购退货订单的供应商")
    private Long vendorSid;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "特殊库存客户sid/无销售订单或销售退货订单的客户")
    private Long customerSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "库存量")
    private BigDecimal unlimitedQuantity;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料档案规格尺寸")
    private String specificationSize;

    @TableField(exist = false)
    @ApiModelProperty(value = "快速编码")
    private String simpleCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "季节")
    private String seasonName;

    @TableField(exist = false)
    @ApiModelProperty(value = "成分")
    private String composition;

    @TableField(exist = false)
    @ApiModelProperty(value = "版型名称")
    private String modelName;

    @TableField(exist = false)
    @ApiModelProperty(value = "吊牌零售价（元）")
    private BigDecimal retailPrice;

    @TableField(exist = false)
    @ApiModelProperty(value = "作业类型code")
    private String movementType;

    @TableField(exist = false)
    @ApiModelProperty(value = "特殊库存（数据字典的键值或配置档案的编码）")
    private String specialStock;

    @TableField(exist = false)
    @ApiModelProperty(value = "特殊库存")
    private String specialStockName;

    @TableField(exist = false)
    @ApiModelProperty(value = "关联单据类别（数据字典的键值或配置档案的编码），如：领退料单、调拨单、库存调整单、盘点单、采购交货单、销售发货单、采购订单、生产订单")
    private String referDocCategoryName;

    @TableField(exist = false)
    @ApiModelProperty(value = "供应商简称")
    private String vendorShortName;

    @TableField(exist = false)
    @ApiModelProperty(value = "客户简称")
    private String customerShortName;

    @TableField(exist = false)
    @ApiModelProperty(value = "多作业类型出库 获取库存量时前端用来存所选中行的下标数据")
    private String depId;

    @TableField(exist = false)
    @ApiModelProperty(value = "货架编码多值用分号隔开")
    private String goodsShelfCodes;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料分类")
    private Long materialClassSid;

    /**
     * 单据类型编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "单据类型编码")
    private String documentType;

    /**
     * 单据类型编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "单据类型编码")
    private String documentTypeName;

    /**
     * 处理状态
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "冲销前的明细行sid")
    private Long preInventoryDocumentItemSid;

}
