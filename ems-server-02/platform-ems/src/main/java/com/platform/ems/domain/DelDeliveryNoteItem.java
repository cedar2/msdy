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
import com.platform.ems.domain.dto.response.SalSalesOrderItemTotalResponse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 交货单-明细对象 s_del_delivery_note_item
 *
 * @author linhongwei
 * @date 2021-04-21
 */
@Data
@Accessors(chain = true)
@ApiModel
    @TableName(value = "s_del_delivery_note_item")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DelDeliveryNoteItem extends EmsBaseEntity {

    /**
     * 客户端口号
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /**
     * 系统自增长ID-交货单明细
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-交货单明细")
    private Long deliveryNoteItemSid;

    /**
     * 系统自增长ID-交货单
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-交货单")
    private Long deliveryNoteSid;

    /**
     * 系统自增长ID-物料&商品&服务
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-物料&商品&服务")
    private Long materialSid;

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

    /**
     * 行号
     */
    @Excel(name = "行号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "行号")
    private Long itemNum;

    /**
     * 计量单位（数据字典的键值）
     */
    @ApiModelProperty(value = "计量单位（数据字典的键值）")
    private String unitBase;

    @TableField(exist = false)
    @ApiModelProperty(value = "价格单位")
    private String unitPriceName;

    /**
     * 计量单位名称
     */
    @TableField(exist = false)
    @Excel(name = "基本计量单位")
    @ApiModelProperty(value = "计量单位名称")
    private String unitBaseName;

    /**
     * 采购计量单位名称
     */
    @TableField(exist = false)
    @Excel(name = "采购计量单位")
    @ApiModelProperty(value = "采购计量单位名称")
    private String purchaseUnitBaseName;

    /**
     * 交货/发货量
     */
    @NotNull(message = "交货/发货量不能为空")
    @Excel(name = "交货/发货量")
    @ApiModelProperty(value = "交货/发货量")
    @Digits(integer = 8,fraction = 4, message = "交货/发货量整数位上限为8位，小数位上限为4位")
    private BigDecimal deliveryQuantity;

    /**
     * 出入库量
     */
    @Excel(name = "出入库量")
    @ApiModelProperty(value = "出入库量")
    private BigDecimal inOutStockQuantity;

    /**
     * 价格(不含税)
     */
    @ApiModelProperty(value = "价格(不含税)")
    private BigDecimal price;

    /**
     * 价格(含税)
     */
    @Excel(name = "采购价(含税)")
    @ApiModelProperty(value = "价格(含税)")
    private BigDecimal priceTax;

    /**
     * 订单明细价格(含税)
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "订单明细价格(含税)")
    private BigDecimal orderPriceTax;

    /**
     * 税率（存值，即：不含百分号，如20%，就存0.2）
     */
    @Excel(name = "税率")
    @ApiModelProperty(value = "税率（存值，即：不含百分号，如20%，就存0.2）")
    private BigDecimal taxRate;

    @TableField(exist = false)
    @ApiModelProperty(value = "订单明细税率")
    private BigDecimal orderTaxRate;

    @TableField(exist = false)
    @ApiModelProperty(value = "订单明细免费")
    private String orderFreeFlag;

    /**
     * 系统自增长ID-仓库
     */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-仓库")
    private Long storehouseSid;

    /**
     * 系统自增长ID-库位
     */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-库位")
    private Long storehouseLocationSid;

    /**
     * 价格计量单位（数据字典的键值）（停用）
     */
    @ApiModelProperty(value = "价格计量单位（数据字典的键值）（停用）")
    private String unitPrice;

    /**
     * 单位换算比例（价格单位/基本单位）（停用）
     */
    @Excel(name = "单位换算比例")
    @ApiModelProperty(value = "单位换算比例（价格单位/基本单位）")
    private BigDecimal unitConversionRate;

    /**
     * 系统SID-销售订单明细
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-销售订单明细")
    private Long salesOrderItemSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-销售订单明细")
    private Long[] salesOrderItemSidList;

    /**
     * 系统SID-采购订单明细
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-采购订单明细")
    private Long purchaseOrderItemSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-采购订单明细")
    private Long[] purchaseOrderItemSidList;

    /**
     * 创建人账号
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    @TableField(exist = false)
    @ApiModelProperty(value = "创建人账号List")
    private String[] creatorAccountList;

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
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccount;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 数据源系统
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统")
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

    @Excel(name = "物料/商品编码")
    @ApiModelProperty(value = "物料（商品/服务）编码")
    @TableField(exist = false)
    private String materialCode;

    @Excel(name = "物料/商品名称")
    @TableField(exist = false)
    @ApiModelProperty(value = "物料（商品/服务）名称")
    private String materialName;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料（商品/服务）类别")
    private String materialCategory;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料（商品/服务）类别")
    private String materialType;

    @TableField(exist = false)
    @ApiModelProperty(value = "我司样衣号")
    private String sampleCodeSelf;

    @TableField(exist = false)
    @ApiModelProperty(value = "快速编码")
    private String simpleCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "吊牌零售价（元）")
    private BigDecimal retailPrice;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料商品主图片路径")
    private String picturePath;

    @TableField(exist = false)
    @ApiModelProperty(value = "sku1编码")
    private String sku1Code;

    @TableField(exist = false)
    @ApiModelProperty(value = "sku2编码")
    private String sku2Code;

    @Excel(name = "sku1名称")
    @ApiModelProperty(value = "sku1名称")
    @TableField(exist = false)
    private String sku1Name;

    @Excel(name = "sku2名称")
    @ApiModelProperty(value = "sku2名称")
    @TableField(exist = false)
    private String sku2Name;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售量")
    private BigDecimal quantity;

    /**
     * 仓库编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "仓库编码")
    private String storehouseCode;

    /**
     * 仓库名称
     */
    @Excel(name = "仓库")
    @TableField(exist = false)
    @ApiModelProperty(value = "仓库名称")
    private String storehouseName;

    /**
     * 库位编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "库位编码")
    private String locationCode;

    /**
     * 库位名称
     */
    @Excel(name = "库位")
    @TableField(exist = false)
    @ApiModelProperty(value = "库位名称")
    private String locationName;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售价(含税)")
    private BigDecimal salePriceTax;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售价(不含税)")
    private BigDecimal salePrice;

    /**
     * 交货类型（数据字典的键值）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "交货类型（数据字典的键值）")
    private String deliveryType;

    @TableField(exist = false)
    @ApiModelProperty(value = "采购价(含税)")
    private BigDecimal purchasePriceTax;

    @TableField(exist = false)
    @ApiModelProperty(value = "采购价(不含税)")
    private BigDecimal purchasePrice;

    /**
     * 采购交货/销售发货单号
     */
    @Excel(name = "采购交货/销售发货单号")
    @TableField(exist = false)
    @ApiModelProperty(value = "采购交货/销售发货单号")
    private String deliveryNoteCode;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-供应商信息")
    private Long vendorSid;

    @TableField(exist = false)
    @Excel(name = "供应商")
    @ApiModelProperty(value = "供应商名称")
    private String vendorName;

    @TableField(exist = false)
    @ApiModelProperty(value = "供应商名称")
    private String vendorShortName;

    @TableField(exist = false)
    @ApiModelProperty(value = "供应商编码")
    private String vendorCode;

    /**
     * 系统自增长ID-采购订单
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-采购订单")
    private Long purchaseOrderSid;

    /**
     * 采购订单号
     */
    @Excel(name = "采购订单号")
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
     * 供方送货单号（供方交货单号）
     */
    @TableField(exist = false)
    @Excel(name = "供方货运单号")
    @ApiModelProperty(value = "供方送货单号（供方交货单号）")
    private String supplierDeliveryCode;

    /**
     * 采购员
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "采购员")
    private String buyer;

    @TableField(exist = false)
    @ApiModelProperty(value = "采购员数组")
    private String[] buyerList;

    /**
     * 采购员名称
     */
    @TableField(exist = false)
    @Excel(name = "采购员")
    private String nickName;

    @Excel(name = "商品条码")
    @TableField(exist = false)
    @ApiModelProperty(value = "商品条码")
    private String barcode;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品条码wms")
    private String barcode2;

    /**
     * 系统自增长ID-产品季档案
     */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-产品季档案")
    private Long productSeasonSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "产品季编码")
    private String productSeasonCode;

    @Excel(name = "产品季")
    @TableField(exist = false)
    @ApiModelProperty(value = "产品季名称")
    private String productSeasonName;

    /**
     * 单据类型（数据字典的键值）主表
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "单据类型（数据字典的键值）主表")
    private String documentType;

    /**
     * 单据类型名称主表
     */
    @TableField(exist = false)
    @Excel(name = "单据类型")
    @ApiModelProperty(value = "单据类型名称主表")
    private String documentTypeName;

    /**
     * 单据类型（数据字典的键值）明细关联订单
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "单据类型（数据字典的键值）明细关联订单")
    private String referDocumentType;

    /**
     * 单据类型名称明细关联订单
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "单据类型名称明细关联订单")
    private String referDocumentTypeName;

    /**
     * 业务类型（数据字典的键值）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "业务类型（数据字典的键值）")
    private String businessType;

    /**
     * 业务类型名称
     */
    @TableField(exist = false)
    @Excel(name = "业务类型")
    @ApiModelProperty(value = "业务类型名称")
    private String businessTypeName;

    /**
     * 货运单号
     */
    @Excel(name = "货运单号")
    @TableField(exist = false)
    @ApiModelProperty(value = "货运单号")
    private String carrierNoteCode;

    /**
     * 配送方式
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "配送方式")
    private String shipmentType;

    /**
     * 配送方式
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "配送方式")
    private String shipmentTypeName;

    /**
     * 收货方sid
     */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "收货方sid")
    private Long receiverOrg;

    /**
     * 收货方名称
     */
    @Excel(name = "收货方")
    @TableField(exist = false)
    @ApiModelProperty(value = "收货方名称")
    private String receiverOrgName;

    /**
     * 收货方编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "收货方编码")
    private String receiverOrgCode;

    /**
     * 采购组
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "采购组")
    private String purchaseGroup;

    /**
     * 采购组名称
     */
    @TableField(exist = false)
    @Excel(name = "采购组")
    @ApiModelProperty(value = "采购组名称")
    private String purchaseGroupName;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-公司档案")
    private Long companySid;


    /**
     * 公司代码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "公司代码")
    private String companyCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "启停状态")
    private String status;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    /**
     * 公司名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    /**
     * 公司名称
     */
    @TableField(exist = false)
    @Excel(name = "公司")
    @ApiModelProperty(value = "公司名称")
    private String companyShortName;

    /**
     * 单据类型编码list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "单据类型编码list")
    private String[] documentTypeList;

    /**
     * 业务类型编码list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "业务类型编码list")
    private String[] businessTypeList;

    /**
     * 系统自增长ID-供应商信息list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "系统自增长ID-供应商信息list")
    private Long[] vendorSidList;

    /**
     * 系统自增长ID-客户信息list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "系统自增长ID-客户信息list")
    private Long[] customerSidList;

    /**
     * 系统自增长ID-公司档案list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "系统自增长ID-公司档案list")
    private Long[] companySidList;

    /**
     * 系统ID-产品季档案list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "系统ID-产品季档案list")
    private Long[] productSeasonSidList;

    /**
     * 配送类型（数据字典的键值）list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "配送方式（数据字典的键值）list")
    private String[] shipmentTypeList;

    /**
     * 出入库状态（数据字典的键值）list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "出入库状态（数据字典的键值）list")
    private String[] inOutStockStatusList;

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
     * 处理状态list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态list")
    private String[] handleStatusList;


    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long customerSid;

    /**
     * 预计到货日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @TableField(exist = false)
    @ApiModelProperty(value = "预计到货日期")
    private Date expectedArrivalDate;

    /**
     * 预计到货日期从
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @TableField(exist = false)
    @ApiModelProperty(value = "预计到货日期从")
    private Date expectedArrivalBeginDate;

    /**
     * 预计到货日期至
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @TableField(exist = false)
    @ApiModelProperty(value = "预计到货日期至")
    private Date expectedArrivalEndDate;

    /**
     * 操作人（用户帐号）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "出入库操作人（用户帐号）")
    private String operator;

    /**
     * 操作人（用户帐号）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "出入库人")
    private String operatorName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @TableField(exist = false)
    @ApiModelProperty(value = "出入库日期")
    private Date accountDate;


    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @TableField(exist = false)
    @ApiModelProperty(value = "预计发货日期")
    private Date expectedShipDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询预计发货日期起始")
    private String expectedShipDateStart;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询预计发货日期结束")
    private String expectedShipDateEnd;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询出入库日期起始")
    private String accountDateStart;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询出入库日期结束")
    private String accountDateEnd;

    @TableField(exist = false)
    private String[] operatorList;


    @TableField(exist = false)
    @ApiModelProperty(value = "客户编码")
    private String customerCode;
    /**
     * 客户编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "出库金额")
    private BigDecimal accountPrice;

    @TableField(exist = false)
    @ApiModelProperty(value = "采购/销售渠道")
    private String businessChannel;

    @TableField(exist = false)
    @ApiModelProperty(value = "采购/销售渠道")
    private String businessChannelName;

    @TableField(exist = false)
    @ApiModelProperty(value = "采购/销售渠道数组")
    private String[]  businessChannelList;

    /**
     * 客户名称
     */
    @TableField(exist = false)
    @Excel(name = "客户")
    @ApiModelProperty(value = "客户名称")
    private String customerName;

    /**
     * 客户简称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "客户简称")
    private String customerShortName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value ="销售订单sid")
    private Long salesOrderSid;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value ="订单sid")
    private Long orderSid;


    /**
     * 销售订单号
     */
    @ApiModelProperty(value = "销售订单号")
    private String salesOrderCode;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售合同号")
    private Long saleContractSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售合同号")
    private String saleContractCode;

    /**
     * 销售员
     */
    @TableField(exist = false)
    @Excel(name = "销售员")
    @ApiModelProperty(value = "销售员")
    private String salePerson;

    @TableField(exist = false)
    @Excel(name = "销售员")
    @ApiModelProperty(value = "销售员")
    private String salePersonName;

    /**
     * 销售单位
     */
    @TableField(exist = false)
    @Excel(name = "销售单位")
    @ApiModelProperty(value = "销售单位")
    private String unitSale;

    /**
     * 销售组
     */
    @TableField(exist = false)
    @Excel(name = "销售组")
    @ApiModelProperty(value = "销售组")
    private String saleGroup;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售员数组")
    private String[] salePersonList;

    @TableField(exist = false)
    @ApiModelProperty(value ="采购员")
    private String buyerName;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value ="货运方sid")
    private Long carrier;

    @TableField(exist = false)
    @ApiModelProperty(value ="货运方code")
    private String carrierCode;

    @TableField(exist = false)
    @ApiModelProperty(value ="货运方")
    private String carrierName;

    @ApiModelProperty(value ="销售组织")
    @TableField(exist = false)
    private String saleGroupName;

    @ApiModelProperty(value ="销售订单行号")
    @TableField(exist = false)
    private String salesOrderItemNum;

    @ApiModelProperty(value ="采购订单行号")
    @TableField(exist = false)
    private String purchaseOrderItemNum;

    @ApiModelProperty(value ="创建人名称")
    @TableField(exist = false)
    private String creatorAccountName;

    @ApiModelProperty(value ="交货类型")
    @TableField(exist = false)
    private String deliveryCategory;

    @ApiModelProperty(value ="出入库状态")
    @TableField(exist = false)
    private String inOutStockStatus;

    @ApiModelProperty(value ="销售计量单位")
    @TableField(exist = false)
    private String saleUnitBaseName;

    @ApiModelProperty(value = "来源单据单号code")
    @TableField(exist = false)
    private String referDocCode;

    @ApiModelProperty(value = "来源单据sid")
    @TableField(exist = false)
    private Long referDocSid;

    @ApiModelProperty(value = "来源单据行sid")
    @TableField(exist = false)
    private Long referDocItemSid;

    @ApiModelProperty(value = "来源单据行号")
    @TableField(exist = false)
    private Long referDocItemNum;

    @ApiModelProperty(value = "来源类别")
    @TableField(exist = false)
    private String referDocCategory;

    @Excel(name = "免费")
    @ApiModelProperty(value = "免费")
    private String freeFlag;

    @TableField(exist = false)
    @ApiModelProperty(value = "尺码组sid")
    private Long sku2GroupSid;

    @TableField(exist = false)
    List<SalSalesOrderItemTotalResponse> itemlist;

    @Excel(name = "预留状态")
    @ApiModelProperty(value = "预留状态")
    private String reserveStatus;

    @TableField(exist = false)
    private List<Long> deliveryNoteItemSidList;

    @ApiModelProperty(value = "是否退货（数据字典的键值或配置档案的编码）")
    @TableField(exist = false)
    private String isReturnGoods;

    @TableField(exist = false)
    @ApiModelProperty(value = " 是否寄售结算（数据字典的键值或配置档案的编码）")
    private String isConsignmentSettle;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售模式")
    private String saleMode;

    @TableField(exist = false)
    @ApiModelProperty(value = "采购模式")
    private String purchaseMode;

    @TableField(exist = false)
    @ApiModelProperty(value = " 是否生成财务应收暂估流水")
    private String isFinanceBookYszg;

    @TableField(exist = false)
    @ApiModelProperty(value = "  是否生成财务应付暂估流水")
    private String isFinanceBookYfzg;

    @ApiModelProperty(value = "库存管理方式（数据字典的键值或配置档案的编码）")
    @TableField(exist = false)
    private String inventoryControlMode;

    @ApiModelProperty(value = "销售采购单据类型")
    @TableField(exist = false)
    private String saleAndPurchaseDocument;

    @TableField(exist = false)
    private String firstSort;

    @TableField(exist = false)
    private String secondSort;

    @TableField(exist = false)
    private String thirdSort;

    @TableField(exist = false)
    @ApiModelProperty(value = "交货单sids")
    private Long[] deliveryNoteSids;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料档案规格尺寸")
    private String specificationSize;

    @TableField(exist = false)
    @ApiModelProperty(value = "材质")
    private String materialComposition;

    @TableField(exist = false)
    @ApiModelProperty(value = "幅宽（厘米）")
    private String width;

    @TableField(exist = false)
    @ApiModelProperty(value = "克重")
    private String gramWeight;

    @TableField(exist = false)
    @ApiModelProperty(value = "sku1的序号")
    private BigDecimal sort1;

    @TableField(exist = false)
    @ApiModelProperty(value = "sku2的序号")
    private BigDecimal sort2;

    /**
     * 合同交期
     */
    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "合同交期")
    private Date contractDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "合同交期起")
    private String contractDateBegin;

    @TableField(exist = false)
    @ApiModelProperty(value = "合同交期止")
    private String contractDateEnd;
}
