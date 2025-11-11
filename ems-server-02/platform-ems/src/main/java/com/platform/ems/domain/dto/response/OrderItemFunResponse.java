package com.platform.ems.domain.dto.response;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.ems.domain.PurPurchaseOrderDeliveryPlan;
import com.platform.ems.domain.PurPurchaseOrderMaterialProduct;
import com.platform.ems.domain.SalSalesOrderDeliveryPlan;
import com.platform.ems.domain.TecBomItem;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 按款添加 返回实体
 */
@Data
@Accessors( chain = true)
public class OrderItemFunResponse {

    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /**
     * 系统自增长ID-采购订单明细
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-采购订单明细")
    @TableId
    private Long purchaseOrderItemSid;

    /**
     * 系统自增长ID-采购订单
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-采购订单")
    private Long purchaseOrderSid;

    /**
     * 系统自增长ID-商品&物料&服务
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-商品&物料&服务")
    @NotNull(message = "商品sid不能为空")
    private Long materialSid;

    /**
     * 用来联查sku1Sid和sku2Sid
     */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-商品sku(用来联查sku1Sid和sku2Sid)")
    private Long skuSid;

    /**
     * 系统自增长ID-商品sku1
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-商品sku1")
    private Long sku1Sid;

    /**
     * 系统自增长ID-商品sku2
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-商品sku2")
    private Long sku2Sid;

    /**
     * 系统自增长ID-商品条码
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-商品条码")
    private Long barcodeSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品条码")
    private String barcode;
    /**
     * 采购量
     */
    @Excel(name = "采购量")
    @ApiModelProperty(value = "采购量")
    private BigDecimal quantity;

    /**
     * 行号
     */
    @Excel(name = "行号")
    @ApiModelProperty(value = "行号")
    private Integer itemNum;

    /**
     * 基本计量单位
     */
    @ApiModelProperty(value = "基本计量单位")
    private String unitBase;

    /**
     * 计量单位名称
     */
    @TableField(exist = false)
    @Excel(name = "基本计量单位")
    @ApiModelProperty(value = "计量单位名称")
    private String unitBaseName;

    /**
     * 采购单位
     */
    @ApiModelProperty(value = "采购单位")
    private String unitPrice;

    /**
     * 采购单位
     */
    @TableField(exist = false)
    @Excel(name = "采购单位")
    @ApiModelProperty(value = "采购单位")
    private String unitPriceName;

    /**
     * 采购价(不含税)
     */
    @Excel(name = "采购价(不含税)")
    @ApiModelProperty(value = "采购价(不含税)")
    private BigDecimal purchasePrice;

    /**
     * 采购价(含税)
     */
    @Excel(name = "采购价(含税)")
    @ApiModelProperty(value = "采购价(含税)")
    private BigDecimal purchasePriceTax;

    /**
     * 税率
     */
    @Excel(name = "税率")
    @ApiModelProperty(value = "税率")
    private BigDecimal taxRate;

    /**
     * 折扣类型代码
     */
    @ApiModelProperty(value = "折扣类型代码")
    private String discountType;

    /**
     * 货币单位
     */
    @Excel(name = "货币单位")
    @ApiModelProperty(value = "货币单位")
    private String currency;

    /**
     * 免费
     */
    @Excel(name = "免费")
    @ApiModelProperty(value = "免费")
    private String freeFlag;


    @Excel(name = "单位换算比例")
    @ApiModelProperty(value = "单位换算比例")
    private BigDecimal unitConversionRate;

    /**
     * 合同交期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "合同交期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "合同交期")
    private Date contractDate;

    /**
     * 仓库
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "仓库")
    private Long storehouseSid;

    /**
     * 库位
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "库位")
    private Long storehouseLocationSid;

    @TableField(exist = false)
    @ApiModelProperty(value ="当前销售价")
    private BigDecimal returnPtin;

    public BigDecimal getReturnPtin() {
        if(returnPtin==null){
            return new BigDecimal(0);
        }
        return returnPtin;
    }

    /**
     * 交货状态
     */
    @Excel(name = "交货状态")
    @ApiModelProperty(value = "交货状态")
    private String deliveryStatus;

    /** 来源单据类别 */
    @Excel(name = "来源单据类别")
    @ApiModelProperty(value = "来源单据类别")
    private String referDocCategory;

    /** 来源单据sid */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "来源单据sid")
    private Long referDocSid;

    /** 来源单据单号code */
    @Excel(name = "来源单据单号code")
    @ApiModelProperty(value = "来源单据单号code")
    private String referDocCode;

    /** 来源单据行sid */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "来源单据行sid")
    private Long referDocItemSid;

    /** 来源单据行号 */
    @Excel(name = "来源单据行号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "来源单据行号")
    private Long referDocItemNum;

    /**
     * 系统自增长ID-申购单
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-申购单")
    private Long purchaseRequireSid;

    /**
     * 系统自增长ID-申购单明细
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-申购单明细")
    private Long purchaseRequireItemSid;

    /**
     * 创建人账号
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    /**
     * 创建人账号
     */
    @Excel(name = "创建人")
    @TableField(exist = false)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccountName;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /**
     * 更新人账号
     */
    @Excel(name = "更新人账号")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号")
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
     * 确认人账号
     */
    @Excel(name = "确认人账号")
    @ApiModelProperty(value = "确认人账号")
    private String confirmerAccount;

    /**
     * 确认时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "确认时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 数据源系统
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;

    /**
     * 采购员名称
     */
    @TableField(exist = false)
    @Excel(name = "采购员")
    private String buyerName;

    /** 特殊业务类别编码code，如：供应商寄售结算 */
    @TableField(exist = false)
    @ApiModelProperty(value = "特殊业务类别编码code，如：供应商寄售结算")
    private String specialBusCategory;

    /**
     * 物料（商品/服务）名称
     */
    @Excel(name = "物料/商品/服务名称")
    @ApiModelProperty(value = "物料（商品/服务）名称")
    private String materialName;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private String[] purchaseOrderItemSidList;


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

    @ApiModelProperty(value = "物料编码")
    @TableField(exist = false)
    private String materialCode;

    @Excel(name = "sku1名称")
    @ApiModelProperty(value = "sku1名称")
    @TableField(exist = false)
    private String sku1Name;

    @Excel(name = "sku1名称")
    @ApiModelProperty(value = "sku1名称")
    @TableField(exist = false)
    private String sku1Code;

    @TableField(exist = false)
    private String sku2Code;

    @Excel(name = "sku2名称")
    @ApiModelProperty(value = "sku2名称")
    @TableField(exist = false)
    private String sku2Name;

    @ApiModelProperty(value = "仓库名编码")
    @TableField(exist = false)
    private String storehouseCode;

    @Excel(name = "仓库")
    @ApiModelProperty(value = "仓库名称")
    @TableField(exist = false)
    private String storehouseName;

    @ApiModelProperty(value = "库位编码")
    @TableField(exist = false)
    private String locationCode;

    @Excel(name = "库位")
    @ApiModelProperty(value = "库位名称")
    @TableField(exist = false)
    private String locationName;

    /**
     * 采购订单号
     */
    @Excel(name = "采购订单号")
    @TableField(exist = false)
    @ApiModelProperty(value = "采购订单号")
    private String purchaseOrderCode;

    /**
     * 系统自增长ID-供应商信息
     */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-供应商信息")
    private Long vendorSid;

    @TableField(exist = false)
    @Excel(name = "供应商")
    @ApiModelProperty(value = "供应商名称")
    private String vendorName;

    @TableField(exist = false)
    @ApiModelProperty(value = "供应商编码")
    private String vendorCode;

    /**
     * 单据类型编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "单据类型编码")
    private String documentType;

    /**
     * 业务类型编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "业务类型编码")
    private String businessType;

    /**
     * 采购员
     */
    @TableField(exist = false)
    @Excel(name = "采购员")
    @ApiModelProperty(value = "采购员")
    private String buyer;


    @TableField(exist = false)
    @ApiModelProperty(value = "采购员")
    private String[] buyerList;
    /**
     * 采购合同号
     */
    @Excel(name = "采购合同/协议号")
    @TableField(exist = false)
    @ApiModelProperty(value = "采购合同号")
    private String purchaseContractCode;

    /**
     * 系统自增长ID-产品季档案
     */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-产品季档案")
    private Long productSeasonSid;

    /** 物料类型编码（物料/商品/服务） */
    @TableField(exist = false)
    @ApiModelProperty(value = "物料类型编码（物料/商品/服务）")
    private String materialType;

    /**
     * 采购组织
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "采购组织")
    private String purchaseOrg;

    /**
     * 采购组
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "采购组")
    private String purchaseGroup;

    /**
     * 配送类型
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "配送类型")
    private String shipmentType;

    /**
     * 系统自增长ID-公司档案
     */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-公司档案")
    private Long companySid;

    /**
     * 供料方式
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "供料方式")
    private String rawMaterialMode;

    /**
     * 采购模式
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "采购模式")
    private String purchaseMode;

    /**
     * 合同交期从
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "合同交期从")
    private String contractBeginDate;

    /**
     * 合同交期至
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "合同交期至")
    private String contractEndDate;

    /** 销售订单sid */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售订单sid")
    private Long salesOrderSid;

    /** 销售订单号code */
    @Excel(name = "销售订单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售订单号code")
    private Long salesOrderCode;

    /** 销售订单明细行sid */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售订单明细行sid")
    private Long salesOrderItemSid;

    /** 销售订单明细行号 */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售订单明细行号")
    private Long salesOrderItemNum;

    /**
     * 系统自增长ID-供应商信息list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "系统自增长ID-供应商信息list")
    private Long[] vendorSidList;

    /** 单据类型编码list */
    @TableField(exist = false)
    @ApiModelProperty(value = "单据类型编码list")
    private String[] documentTypeList;

    /** 业务类型编码list */
    @TableField(exist = false)
    @ApiModelProperty(value = "业务类型编码list")
    private String[] businessTypeList;

    /** 处理状态list */
    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态list")
    private String[] handleStatusList;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    /**
     * 交货状态list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "交货状态list")
    private String[] deliveryStatusList;

    /** 系统ID-产品季档案list */
    @TableField(exist = false)
    @ApiModelProperty(value = "系统ID-产品季档案list")
    private Long[] productSeasonSidList;

    /** 物料类型list */
    @TableField(exist = false)
    @ApiModelProperty(value = "物料类型list")
    private String[] materialTypeList;

    /**
     * 采购组织list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "采购组织list")
    private String[] purchaseOrgList;

    /**
     * 采购组list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "采购组list")
    private String[] purchaseGroupList;

    /**
     * 系统自增长ID-仓库list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "系统自增长ID-仓库list")
    private Long[] storehouseSidList;

    /**
     * 系统自增长ID-库位list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "系统自增长ID-库位list")
    private Long[] storehouseLocationSidList;

    /**
     * 配送类型list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "配送类型list")
    private String[] shipmentTypeList;

    /**
     * 系统自增长ID-公司档案list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "系统自增长ID-公司档案list")
    private Long[] companySidList;

    /**
     * 供料方式list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "供料方式list")
    private String[] rawMaterialModeList;

    /**
     * 采购模式list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "采购模式list")
    private String[] purchaseModeList;

    @TableField(exist = false)
    @ApiModelProperty(value = "产品季名称")
    private String productSeasonName;

    @TableField(exist = false)
    @Excel(name = "单据类型名称")
    @ApiModelProperty(value = "单据类型名称")
    private String documentTypeName;

    @TableField(exist = false)
    @Excel(name = "业务类型")
    @ApiModelProperty(value = "业务类型名称")
    private String businessTypeName;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @ApiModelProperty(value = "款号sid")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long productSid;

    @ApiModelProperty(value = "款号名称")
    @TableField(exist = false)
    private String productName;

    @ApiModelProperty(value = "款号code")
    private String productCode;

    @ApiModelProperty(value = "款颜色")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long productSku1Sid;

    @TableField(exist = false)
    @ApiModelProperty(value = "款颜色")
    private String productSku1Name;

    @ApiModelProperty(value = "款尺码")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long productSku2Sid;

    @TableField(exist = false)
    @ApiModelProperty(value = "款尺码")
    private String productSku2Name;

    @ApiModelProperty(value = "款备注")
    private String productCodes;

    @ApiModelProperty(value = "款颜色备注")
    private String productSku1Names;

    @ApiModelProperty(value = "款尺码备注")
    private String productSku2Names;

    @Excel(name = "出入库状态", dictType = "s_in_out_store_status")
    @ApiModelProperty(value = "出入库状态")
    private String inOutStockStatus;

    @ApiModelProperty(value = "新采购量(变更中)")
    private BigDecimal newQuantity;

    @ApiModelProperty(value = "新采购价(不含税)(变更中)")
    private BigDecimal newPurchasePrice;

    @ApiModelProperty(value = "新采购价(含税)(变更中)")
    private BigDecimal newPurchasePriceTax;

    @ApiModelProperty(value = "初始采购价(不含税)")
    private BigDecimal initialPurchasePrice;

    @ApiModelProperty(value = "初始采购价(含税)")
    private BigDecimal initialPurchasePriceTax;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "初始合同交期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "初始合同交期")
    private Date initialContractDate;

    @ApiModelProperty(value = "初始销售量")
    private BigDecimal initialQuantity;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "新合同交期(变更中)", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "新合同交期(变更中)")
    private Date newContractDate;

    @ApiModelProperty(value = "可用库存量")
    @TableField(exist = false)
    private BigDecimal  InvQuantity;

    @Excel(name = "待出入库量")
    @ApiModelProperty(value = "待出入库量")
    @TableField(exist = false)
    private BigDecimal partQuantity;

    @Excel(name = "已出入库量")
    @ApiModelProperty(value = "已出入库量")
    @TableField(exist = false)
    private BigDecimal inQuantity;

    @Excel(name = "已发货未出入库量")
    @ApiModelProperty(value = "已发货出入库量")
    @TableField(exist = false)
    private BigDecimal inWQuantity;


    /** 生产订单sid（如是原材料采购，此处存放对应的成品生产订单：按库生产） */
    @Excel(name = "生产订单sid（如是原材料采购，此处存放对应的成品生产订单：按库生产）")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "生产订单sid（如是原材料采购，此处存放对应的成品生产订单：按库生产）")
    private Long manufactureOrderSid;

    /** 生产订单号code */
    @Excel(name = "生产订单号code")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "生产订单号code")
    private Long manufactureOrderCode;

    /** 生产订单商品明细行sid */
    @Excel(name = "生产订单商品明细行sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "生产订单商品明细行sid")
    private Long manufactureOrderProductSid;

    /** 生产订单商品明细行号 */
    @Excel(name = "生产订单商品明细行号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "生产订单商品明细行号")
    private Long manufactureOrderProductNum;

    /** 采购订单sid（如是原材料采购，此处存放对应的成品采购订单） */
    @Excel(name = "采购订单sid（如是原材料采购，此处存放对应的成品采购订单）")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购订单sid（如是原材料采购，此处存放对应的成品采购订单）")
    private Long referPurchaseOrderSid;

    /** 采购订单明细行sid */
    @Excel(name = "采购订单明细行sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购订单明细行sid")
    private Long referPurchaseOrderItemSid;

    /** 采购订单号code（如是原材料采购，此处存放对应的成品采购订单） */
    @Excel(name = "采购订单号code（如是原材料采购，此处存放对应的成品采购订单）")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购订单号code（如是原材料采购，此处存放对应的成品采购订单）")
    private Long referPurchaseOrderCode;

    /** 采购订单明细行号 */
    @Excel(name = "采购订单明细行号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购订单明细行号")
    private Long referPurchaseOrderItemNum;

    /** 商品sku1 code(适用于物料对应的商品)；只能保存单个 */
    @Excel(name = "商品sku1 code(适用于物料对应的商品)；只能保存单个")
    @ApiModelProperty(value = "商品sku1 code(适用于物料对应的商品)；只能保存单个")
    private String productSku1Code;

    /** 商品sku2 code(适用于物料对应的商品)；只能保存单个 */
    @Excel(name = "商品sku2 code(适用于物料对应的商品)；只能保存单个")
    @ApiModelProperty(value = "商品sku2 code(适用于物料对应的商品)；只能保存单个")
    private String productSku2Code;

    @ApiModelProperty(value = "款销售客户简称")
    @TableField(exist = false)
    private String customerShortName;

    @ApiModelProperty(value = "款数量")
    private BigDecimal productQuantity;

    @ApiModelProperty(value = "款销售业务类型")
    @TableField(exist = false)
    private String saleBusinessTypeName;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售发货类别（数据字典的键值或配置档案的编码）")
    private String  deliveryType;


    @TableField(exist = false)
    @ApiModelProperty(value = "价格是否为空")
    private String isNull;

    @TableField(exist = false)
    @ApiModelProperty(value = "本次交货量")
    private BigDecimal deliveryQuantity;

    @Excel(name = "采购类型（默认）")
    @ApiModelProperty(value = "采购类型名称（默认）")
    @TableField(exist = false)
    private String purchaseTypeName;

    /**
     * 采购价(含税)
     */
    @Excel(name = "金额(含税)")
    @TableField(exist = false)
    @ApiModelProperty(value = "金额(含税)")
    private BigDecimal priceTax;

    @ApiModelProperty(value = "短交量（指取消掉的数）")
    private BigDecimal cancelQuantity;


    private String productPoCodes;
    private String productSoCodes;
    private String productMoCodes;

    /**
     * 采购价(不含税)
     */
    @Excel(name = "金额(不含税)")
    @TableField(exist = false)
    @ApiModelProperty(value = "金额(不含税)")
    private BigDecimal price;

    @TableField(exist = false)
    @ApiModelProperty(value = "供应商简称")
    private String vendorShortName;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料类别")
    private String materialCategory;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：不包含的处理状态")
    private String[] notInHandleStatus;


    @ApiModelProperty(value = "原材料_需购状态（数据字典的键值或配置档案的编码）")
    private String yclXugouStatus;

    @ApiModelProperty(value = "原材料_备料状态（数据字典的键值或配置档案的编码）")
    private String yclBeiliaoStatus;

    @ApiModelProperty(value = "原材料_采购下单状态（数据字典的键值或配置档案的编码）")
    private String yclCaigouxiadanStatus;

    @ApiModelProperty(value = "原材料_齐套状态（数据字典的键值或配置档案的编码）")
    private String yclQitaoStatus;

    @ApiModelProperty(value = "甲供料_供料状态（数据字典的键值或配置档案的编码）")
    private String jglGongliaoStatus;

    @ApiModelProperty(value = "原材料_齐套说明")
    private String yclQitaoRemark;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "商品条码sid(适用于物料对应的商品)；只能保存单个")
    private Long productBarcodeSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "需求单sid")
    private Long requireDocSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "需求单code")
    private Long requireDocCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "需求单code")
    private Long requireDocItemSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "需求单code")
    private Long requireDocItemNum;

    @ApiModelProperty(value = "业务渠道/销售渠道（数据字典的键值或配置档案的编码）")
    @TableField(exist = false)
    private String businessChannel;

    @TableField(exist = false)
    @Excel(name = "销售渠道")
    @ApiModelProperty(value = "业务渠道/销售渠道名称")
    private String businessChannelName;

    @TableField(exist = false)
    @ApiModelProperty(value = "尺码组sid")
    private Long sku2GroupSid;

    @ApiModelProperty(value = "尺码组名称")
    @TableField(exist = false)
    private String sku2GroupName;

    @TableField(exist = false)
    private String[] materialCategoryList;

    @ApiModelProperty(value = "是否退货（数据字典的键值或配置档案的编码）")
    @TableField(exist = false)
    private String isReturnGoods;

    @ApiModelProperty(value = " 是否寄售结算（数据字典的键值或配置档案的编码）")
    @TableField(exist = false)
    private String isConsignmentSettle;

    @TableField(exist = false)
    private List<PurPurchaseOrderMaterialProduct> materialProductList;

    @ApiModelProperty(value = " 款数量备注(适用于物料对应的商品)；可以保存多个")
    private String productQuantityRemark;

    @ApiModelProperty(value = " 商品需求方备注(适用于物料对应的商品的需求方)；可以保存多个；如：客户、供应商")
    private String productRequestPartys;

    @ApiModelProperty(value = " 商品业务类型备注(适用于物料对应的商品的业务类型)；可以保存多个")
    private String productRequestBusType;

    @TableField(exist = false)
    private String[] itemSidList;

    @ApiModelProperty(value = "来源类别")
    @TableField(exist = false)
    private String sourceCategory;

    @ApiModelProperty(value = "来源类别名称")
    @TableField(exist = false)
    private String sourceCategoryName;

    @ApiModelProperty(value = "即将到期提醒天数")
    private Long toexpireDays;






    @TableField(exist = false)
    private String[] salePersonList;



    /**
     * 销售价(不含税)
     */
    @Excel(name = "销售价(不含税)")
    @ApiModelProperty(value = "销售价(不含税)")
    private BigDecimal salePrice;

    /**
     * 销售价(含税)
     */
    @Excel(name = "销售价(含税)")
    @ApiModelProperty(value = "销售价(含税)")
    private BigDecimal salePriceTax;


    /**
     * 需求日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "需求日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "需求日期")
    private Date demandDate;

    /**
     * 最晚需求日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "最晚需求日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "最晚需求日期")
    private Date latestDemandDate;



    @TableField(exist = false)
    @ApiModelProperty(value = "合同交期开始")
    private String contractDateStart;

    @TableField(exist = false)
    @ApiModelProperty(value = "合同交期结束")
    private String contractDateEnd;


    /**
     * 系统自增长ID-销售合同（备用）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-销售合同（备用）")
    private Long saleContractSid;

    /**
     * 系统自增长ID-销售合同明细（备用）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-销售合同明细（备用）")
    private Long saleContractItemSid;


    /**
     * 系统自增长ID-客户信息
     */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-客户信息")
    private Long customerSid;

    @Excel(name = "客户")
    @TableField(exist = false)
    @ApiModelProperty(value = "客户名称")
    private String customerName;

    @TableField(exist = false)
    @ApiModelProperty(value = "客户编码")
    private String customerCode;



    /**
     * 销售员名称
     */
    @TableField(exist = false)
    @Excel(name = "销售员")
    private String nickName;

    /**
     * 销售员
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "销售员")
    private String salePerson;

    /**
     * 销售合同号
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "销售合同号")
    private String saleContractCode;



    /**
     * 销售部门
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "销售部门")
    private String saleDepartment;


    /**
     * 销售组
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "销售组")
    private String saleGroup;

    /**
     * 销售模式
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "销售模式")
    private String saleMode;


    /**
     * 需求日期从
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "需求日期从")
    private String demandBeginDate;

    /**
     * 需求日期至
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "需求日期至")
    private String demandEndDate;

    /**
     * 最晚需求日期从
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "最晚需求日期从")
    private String latestDemandBeginDate;

    /**
     * 最晚需求日期至
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "最晚需求日期至")
    private String latestDemandEndDate;

    /**
     * 折扣类型名称
     */
    @TableField(exist = false)
    @Excel(name = "折扣类型名称")
    @ApiModelProperty(value = "折扣类型名称")
    private String discountTypeName;
    @TableField(exist = false)
    @ApiModelProperty(value = "产品季编码")
    private String productSeasonCode;



    /**
     * 物料类型名称
     */
    @TableField(exist = false)
    @Excel(name = "物料类型")
    @ApiModelProperty(value = "物料类型名称")
    private String materialTypeName;



    /**
     * 合同名称
     */
    @TableField(exist = false)
    @Excel(name = "销售合同/协议号")
    @ApiModelProperty(value = "合同名称")
    private String contractName;


    @TableField(exist = false)
    @ApiModelProperty(value = "已排产量")
    private BigDecimal alreadyQuantity;

    @TableField(exist = false)
    @ApiModelProperty(value = "待排产量")
    private BigDecimal notQuantity;

    @TableField(exist = false)
    @ApiModelProperty(value = "排产状态")
    private String quantityStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "排产状态")
    private String[] quantityStatusList;

    @TableField(exist = false)
    @ApiModelProperty(value = "已生产完工量")
    private BigDecimal completeQuantity;
    /**
     * 系统自增长ID-客户信息list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "系统自增长ID-客户信息list")
    private Long[] customerSidList;

    /**
     * 销售部门list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "销售部门list")
    private String[] saleDepartmentList;

    /**
     * 销售渠道list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "销售渠道list")
    private String[] businessChannelList;


    /**
     * 销售组list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "销售组list")
    private String[] saleGroupList;


    /**
     * 销售模式list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "销售模式list")
    private String[] saleModeList;

    /**
     * 需求量
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "需求量")
    private BigDecimal requireQuantity;

    /** 采购类型编码（默认） */
    @TableField(exist = false)
    @Excel(name = "采购类型编码（默认）", dictType = "s_purchase_type")
    @ApiModelProperty(value = "采购类型编码（默认）")
    private String purchaseType;

    /** 供方编码（物料/商品/服务） */
    @TableField(exist = false)
    @Excel(name = "供方编码（物料/商品/服务）")
    @ApiModelProperty(value = "供方编码（物料/商品/服务）")
    private String supplierProductCode;

    /** 物料（商品/服务）分类编码 */
    @TableField(exist = false)
    @ApiModelProperty(value = "物料（商品/服务）分类编码")
    private Long materialClassSid;

    /** 物料分类名称 */
    @TableField(exist = false)
    @Excel(name = "物料分类名称")
    @ApiModelProperty(value = "物料分类名称")
    private String nodeName;

    @TableField(exist = false)
    @ApiModelProperty(value = "BOM明细列表")
    private List<TecBomItem> tecBomItemList;



    @TableField(exist = false)
    @ApiModelProperty(value = "公司编码")
    private String companyCode;

    @ApiModelProperty(value = "初始销售价(不含税)")
    private BigDecimal initialSalePrice;

    @ApiModelProperty(value = "初始销售价(含税)")
    private BigDecimal initialSalePriceTax;



    @ApiModelProperty(value = "新销售价(不含税)(变更中)")
    private BigDecimal newSalePrice;



    @ApiModelProperty(value = "新销售价(含税)(变更中)")
    private BigDecimal newSalePriceTax;






    @TableField(exist = false)
    @ApiModelProperty(value = "是否排产（数据字典的键值或配置档案的编码）")
    private String isManufacture;


    @ApiModelProperty(value = "数量小计")
    @TableField(exist = false)
    private BigDecimal sumQuantity;

    @ApiModelProperty(value = "金额小计")
    @TableField(exist = false)
    private BigDecimal sumMoneyAmount;

    @ApiModelProperty(value = "维度")
    @TableField(exist = false)
    private String dimension;

    @TableField(exist = false)
    private String isNullContractDate;

    @TableField(exist = false)
    List<SalSalesOrderItemTotalResponse> itemlist;

    @ApiModelProperty(value = "发货计划明细")
    @TableField(exist = false)
    List<SalSalesOrderDeliveryPlan>  deliveryPlanList;

    @TableField(exist = false)
    @ApiModelProperty(value = "交货计划")
    List<PurPurchaseOrderDeliveryPlan> purDeliveryPlanList;

    @ApiModelProperty(value = "客供料_申请状态（数据字典的键值或配置档案的编码）")
    private String kglShenqingStatus;

    @ApiModelProperty(value = "客供料_到料状态（数据字典的键值或配置档案的编码）")
    private String kglDaoliaoStatus;

    @ApiModelProperty(value = "是否首杠")
    private String isMakeShougang;

    @ApiModelProperty(value = "是否做首批（数据字典的键值或配置档案的编码）")
    private String  isMakeShoupi;

    @TableField(exist = false)
    private String firstSort;

    @TableField(exist = false)
    private String secondSort;

    @TableField(exist = false)
    private String thirdSort;
}
