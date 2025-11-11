package com.platform.ems.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.platform.common.core.domain.EmsBaseEntity;
import com.platform.ems.domain.dto.response.SalSalesOrderItemTotalResponse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.experimental.Accessors;

/**
 * 销售订单-明细对象 s_sal_sales_order_item
 *
 * @author linhongwei
 * @date 2021-04-08
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_sal_sales_order_item")
public class SalSalesOrderItem  extends EmsBaseEntity {

    /**
     * 客户端口号
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /**
     * 系统自增长ID-销售订单明细
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-销售订单明细")
    @TableId
    private Long salesOrderItemSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统自增长ID-销售订单明细")
    private Long[] salesOrderItemSidList;

    /**
     * 系统自增长ID-销售订单
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-销售订单")
    private Long salesOrderSid;

    /**
     * 系统自增长ID-商品&物料&服务
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-商品&物料&服务")
    @NotNull(message = "物料sid不能为空")
    private Long materialSid;

    /**
     * 用来联查sku1Sid和sku2Sid
     */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-商品sku(用来联查sku1Sid和sku2Sid)")
    private Long skuSid;

    /**
     * 系统自增长ID-商品sku
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-商品sku")
    private Long sku1Sid;

    @TableField(exist = false)
    @ApiModelProperty(value = "sku1的序号")
    private BigDecimal sort1;

    @TableField(exist = false)
    @ApiModelProperty(value = "sku2的序号")
    private BigDecimal sort2;

    /**
     * 系统自增长ID-商品sku
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-商品sku")
    private Long sku2Sid;

    @TableField(exist = false)
    private String[] salePersonList;
    /**
     * 系统自增长ID-商品条码
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-商品条码")
    private Long barcodeSid;

    @TableField(exist = false)
    @Excel(name = "商品条码")
    @ApiModelProperty(value = "商品条码")
    private String barcode;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品条码")
    private String barcode2;

    /**
     * 基本计量单位
     */
    @ApiModelProperty(value = "基本计量单位")
    private String unitBase;

    /**
     * 销售单位
     */
    @ApiModelProperty(value = "销售单位")
    private String unitPrice;

    /**
     * 销售量
     */
    @Digits(integer = 8,fraction = 4, message = "订单量整数位上限为8位，小数位上限为4位")
    @Excel(name = "销售量")
    @ApiModelProperty(value = "销售量")
    @NotNull(message = "销售量不能为空")
    private BigDecimal quantity;

    /**
     * 税率
     */
    @Excel(name = "税率")
    @ApiModelProperty(value = "税率")
    private BigDecimal taxRate;

    @ApiModelProperty(value = "税率")
    @TableField(exist = false)
    private BigDecimal taxRateName;


    /** 单位换算比例（价格单位/基本单位） */
    @Digits(integer = 8,fraction = 4, message = "单位换算比例整数位上限为8位，小数位上限为4位")
    @Excel(name = "单位换算比例")
    @ApiModelProperty(value = "单位换算比例（价格单位/基本单位）")
    private BigDecimal unitConversionRate;

    /** 3、新增列：价格（元）
     若订单中有价格，就显示订单价格；
     若没有，进行如下操作：
     1）根据订单中的“编码+客供料方式+销售模式”在销售价档案中获取销售价的处理状态为“审批中/已确认/变更审批中”且有效期（至）>=当前日期的销售价数据
     若1）查找到多笔数据，则选择有效期（至）距离当前日期最近的价格 */
    @TableField(exist = false)
    @ApiModelProperty(value = "价格(元)")
    private BigDecimal nearSalePriceTax;

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
     * 折扣类型代码
     */
    @Excel(name = "折扣")
    @ApiModelProperty(value = "折扣类型代码")
    private String discountType;

    /**
     * 免费标识
     */
    @Excel(name = "免费")
    @ApiModelProperty(value = "免费标识")
    private String freeFlag;

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

    /**
     * 合同交期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "合同交期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "合同交期")
    private Date contractDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "合同交期开始")
    private String contractDateStart;

    @TableField(exist = false)
    @ApiModelProperty(value = "合同交期结束")
    private String contractDateEnd;

    // 移动端页签一致
    @ApiModelProperty(value = "合同交期起")
    @TableField(exist = false)
    private String contractDateBeginTime;

    @ApiModelProperty(value = "合同交期至")
    @TableField(exist = false)
    private String contractDateEndTime;

    /**
     * 系统自增长ID-仓库
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-仓库")
    private Long storehouseSid;

    /**
     * 系统自增长ID-库位
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-库位")
    private Long storehouseLocationSid;

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
     * 发货状态
     */
    @Excel(name = "发货状态", dictType = "s_delivery_status")
    @ApiModelProperty(value = "发货状态")
    private String deliveryStatus;

    @Excel(name = "出入库状态", dictType = "s_in_out_store_status")
    @ApiModelProperty(value = "出入库状态")
    private String inOutStockStatus;

    /**
     * 行号
     */
    @Excel(name = "行号")
    @ApiModelProperty(value = "行号")
    private Integer itemNum;

    @ApiModelProperty(value = "明细状态")
    private String itemStatus;

    /**
     * 创建人账号
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号")
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
     * 物料（商品/服务）名称
     */
    @Excel(name = "商品/物料名称")
    @ApiModelProperty(value = "物料（商品/服务）名称")
    private String materialName;

    /**
     * 数据源系统
     */
    @Excel(name = "数据源系统")
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

    @TableField(exist = false)
    @ApiModelProperty(value = "商品未排产提醒天数")
    private Integer wpcRemindDays;

    @Excel(name = "商品/物料编码")
    @ApiModelProperty(value = "物料编码")
    @TableField(exist = false)
    private String materialCode;

    @Excel(name = "sku1(颜色)")
    @ApiModelProperty(value = "sku1名称")
    @TableField(exist = false)
    private String sku1Name;

    @Excel(name = "sku1(尺码)")
    @ApiModelProperty(value = "sku2名称")
    @TableField(exist = false)
    private String sku2Name;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料商品主图片路径")
    private String picturePath;

    @TableField(exist = false)
    private String sku1Code;

    @TableField(exist = false)
    private String sku1Type;

    @TableField(exist = false)
    private String sku2Type;

    @TableField(exist = false)
    private String sku2Code;

    @ApiModelProperty(value = "仓库编码")
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
     * 销售订单号
     */
    @Excel(name = "销售订单号")
    @TableField(exist = false)
    @ApiModelProperty(value = "销售订单号")
    private String salesOrderCode;

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

    @TableField(exist = false)
    @ApiModelProperty(value = "销售员")
    private String salePersonId;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售员")
    private String salePersonName;

    /**
     * 销售合同号
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "销售合同号")
    private String saleContractCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售合同号(纸质合同)")
    private String paperSaleContractCode;

    /**
     * 销售合同号  精确查询
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "销售合同号精确查询")
    private String contractCode;

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
     * 销售部门
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "销售部门")
    private String saleDepartment;

    /**
     * 销售渠道
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "销售渠道")
    private String businessChannel;

    /**
     * 销售组
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "销售组")
    private String saleGroup;

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
     * 计量单位名称
     */
    @TableField(exist = false)
    @Excel(name = "基本计量单位")
    @ApiModelProperty(value = "计量单位名称")
    private String unitBaseName;

    /**
     * 销售单位名称
     */
    @TableField(exist = false)
    @Excel(name = "销售单位")
    @ApiModelProperty(value = "销售单位名称")
    private String unitPriceName;

    /**
     * 折扣类型名称
     */
    @TableField(exist = false)
    @Excel(name = "折扣类型名称")
    @ApiModelProperty(value = "折扣类型名称")
    private String discountTypeName;

    @TableField(exist = false)
    @Excel(name = "产品季")
    @ApiModelProperty(value = "产品季名称")
    private String productSeasonName;

    @TableField(exist = false)
    @ApiModelProperty(value = "产品季编码")
    private String productSeasonCode;

    /**
     * 单据类型名称
     */
    @TableField(exist = false)
    @Excel(name = "单据类型")
    @ApiModelProperty(value = "单据类型名称")
    private String documentTypeName;

    /**
     * 业务类型名称
     */
    @TableField(exist = false)
    @Excel(name = "业务类型")
    @ApiModelProperty(value = "业务类型名称")
    private String businessTypeName;

    /**
     * 物料类型名称
     */
    @TableField(exist = false)
    @Excel(name = "物料类型")
    @ApiModelProperty(value = "物料类型名称")
    private String materialTypeName;

    /**
     * 创建人名称
     */
    @TableField(exist = false)
    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人名称")
    private String creatorAccountName;

    /**
     * 特殊业务类别编码code，如：客户寄售结算
     */
    @TableField(exist = false)
    @Excel(name = "特殊业务类别编码code，如：客户寄售结算")
    @ApiModelProperty(value = "特殊业务类别编码code，如：客户寄售结算")
    private String specialBusCategory;

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

    /**
     * 发货状态list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "发货状态list")
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
     * 销售组list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "销售组list")
    private String[] saleGroupList;

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

    /** 供应商编码（默认） */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "供应商编码（默认）")
    private Long vendorSid;

    /** 供应商名称 */
    @Excel(name = "供应商名称")
    @TableField(exist = false)
    @ApiModelProperty(value = "供应商名称")
    private String vendorName;

    /** 供应商编码 */
    @TableField(exist = false)
    @ApiModelProperty(value = "供应商编码")
    private String vendorCode;

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

    @ApiModelProperty(value = "当前销售价-含税)")
    @TableField(exist = false)
    private BigDecimal returnPtin;

    @TableField(exist = false)
    @Excel(name = "公司")
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司简称")
    private String companyShortName;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司编码")
    private String companyCode;

    @ApiModelProperty(value = "初始销售价(不含税)")
    private BigDecimal initialSalePrice;

    @ApiModelProperty(value = "初始销售价(含税)")
    private BigDecimal initialSalePriceTax;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "初始合同交期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "初始合同交期")
    private Date initialContractDate;

    @ApiModelProperty(value = "初始销售量")
    private BigDecimal initialQuantity;

    @Digits(integer = 8,fraction = 4, message = "新订单量(变更中)整数位上限为8位，小数位上限为4位")
    @ApiModelProperty(value = "新销售量(变更中)")
    private BigDecimal newQuantity;

    @ApiModelProperty(value = "新销售价(不含税)(变更中)")
    private BigDecimal newSalePrice;

    @ApiModelProperty(value = "可用库存量")
    @TableField(exist = false)
    private BigDecimal  InvQuantity;

    @Excel(name = "待出入库量")
    @ApiModelProperty(value = "待出入库量")
    @TableField(exist = false)
    private BigDecimal partQuantity;

    @TableField(exist = false)
    @ApiModelProperty(value = "已发货量")
    private BigDecimal quantityYifh;

    @Excel(name = "已出入库量")
    @ApiModelProperty(value = "已出入库量")
    @TableField(exist = false)
    private BigDecimal inQuantity;

    @Excel(name = "已发货未出入库量")
    @ApiModelProperty(value = "已发货出入库量")
    @TableField(exist = false)
    private BigDecimal inWQuantity;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "启停状态")
    private String status;

    @ApiModelProperty(value = "新销售价(含税)(变更中)")
    private BigDecimal newSalePriceTax;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "新合同交期(变更中)", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "新合同交期(变更中)")
    private Date newContractDate;

    public BigDecimal getReturnPtin() {
        if(returnPtin==null){
            return new BigDecimal(0);
        }
        return returnPtin;
    }
    @ApiModelProperty(value = "来源单据单号code")
    private String referDocCode;

    @ApiModelProperty(value = "短交量（指取消掉的数）")
    private BigDecimal cancelQuantity;

    @ApiModelProperty(value = "来源单据sid")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long referDocSid;

    @ApiModelProperty(value = "来源单据行sid")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long referDocItemSid;

    @ApiModelProperty(value = "来源单据行号")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long referDocItemNum;

    @ApiModelProperty(value = "来源类别")
    private String referDocCategory;


    @TableField(exist = false)
    @ApiModelProperty(value = "销售发货类别（数据字典的键值或配置档案的编码）")
    private String  deliveryType;

    @TableField(exist = false)
    @ApiModelProperty(value = "价格是否为空")
    private String isNull;

    @Excel(name = "金额(含税)")
    @TableField(exist = false)
    @ApiModelProperty(value = "金额(含税)")
    private BigDecimal priceTax;


    @Excel(name = "金额(不含税)")
    @TableField(exist = false)
    @ApiModelProperty(value = "金额(不含税)")
    private BigDecimal price;

    @TableField(exist = false)
    @ApiModelProperty(value = "本次交货量")
    private BigDecimal deliveryQuantity;


    @TableField(exist = false)
    @ApiModelProperty(value = "查询：不包含的处理状态")
    private String[] notInHandleStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "客户简称")
    private String customerShortName;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：负责生产工厂")
    private String[] producePlantSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "负责生产工厂名称")
    private String producePlantName;

    @TableField(exist = false)
    @ApiModelProperty(value = "负责生产工厂简称")
    private String producePlantShortName;

    @TableField(exist = false)
    @ApiModelProperty(value = "是否排产（数据字典的键值或配置档案的编码）")
    private String isManufacture;

    @TableField(exist = false)
    @ApiModelProperty(value = "尺码组sid")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long sku2GroupSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "尺码组名称")
    private String sku2GroupName;

    @ApiModelProperty(value = "数量小计")
    @TableField(exist = false)
    private BigDecimal sumQuantity;

    @ApiModelProperty(value = "金额小计")
    @TableField(exist = false)
    private BigDecimal sumMoneyAmount;

    @ApiModelProperty(value = "维度")
    @TableField(exist = false)
    private String dimension;

    @ApiModelProperty(value = "商品类别")
    @TableField(exist = false)
    private String materialCategory;

    @TableField(exist = false)
    private String isNullContractDate;

    @TableField(exist = false)
    List<SalSalesOrderItemTotalResponse> itemlist;

    @ApiModelProperty(value = "发货计划明细")
    @TableField(exist = false)
    List<SalSalesOrderDeliveryPlan>  deliveryPlanList;

    @ApiModelProperty(value = "原材料_需购状态（数据字典的键值或配置档案的编码）")
    private String yclXugouStatus;

    @ApiModelProperty(value = "原材料_备料状态（数据字典的键值或配置档案的编码）")
    private String yclBeiliaoStatus;

    @ApiModelProperty(value = "原材料_采购下单状态（数据字典的键值或配置档案的编码）")
    private String yclCaigouxiadanStatus;

    @ApiModelProperty(value = "原材料_齐套状态（数据字典的键值或配置档案的编码）")
    private String yclQitaoStatus;

    @ApiModelProperty(value = "客供料_申请状态（数据字典的键值或配置档案的编码）")
    private String kglShenqingStatus;

    @ApiModelProperty(value = "客供料_到料状态（数据字典的键值或配置档案的编码）")
    private String kglDaoliaoStatus;

    @ApiModelProperty(value = "原材料_齐套说明")
    private String yclQitaoRemark;


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

    @ApiModelProperty(value = "采购订单号备注")
    private String productPoCodes;

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

    @ApiModelProperty(value = "款采购订单号")
    @TableField(exist = false)
    private String purchaseOrderCode;

    @ApiModelProperty(value = "款采购供应商")
    @TableField(exist = false)
    private String vendorShortName;

    @ApiModelProperty(value = "是否首杠")
    private String isMakeShougang;

    @ApiModelProperty(value = "是否做首批（数据字典的键值或配置档案的编码）")
    private String  isMakeShoupi;

    @ApiModelProperty(value = "款数量")
    private BigDecimal productQuantity;

    @TableField(exist = false)
    private String[] materialCategoryList;

    @TableField(exist = false)
    private String[] itemSidList;

    @ApiModelProperty(value = "是否退货（数据字典的键值或配置档案的编码）")
    @TableField(exist = false)
    private String isReturnGoods;

    @ApiModelProperty(value = " 是否寄售结算（数据字典的键值或配置档案的编码）")
    @TableField(exist = false)
    private String isConsignmentSettle;

    @TableField(exist = false)
    @ApiModelProperty(value = "是否生成财务待收预收流水（数据字典的键值或配置档案的编码）")
    private String isFinanceBookDsys;

    @TableField(exist = false)
    @ApiModelProperty(value = "是否生成财务流水（数据字典的键值或配置档案的编码）")
    private String isFinanceBookYszg;

    @TableField(exist = false)
    @ApiModelProperty(value = "库存管理方式（数据字典的键值或配置档案的编码）")
    private String inventoryControlMode;

    @ApiModelProperty(value = "来源类别")
    @TableField(exist = false)
    private String sourceCategory;

    @ApiModelProperty(value = "来源类别名称")
    @TableField(exist = false)
    private String sourceCategoryName;

    @ApiModelProperty(value = "是否跳过为零的待排产量")
    @TableField(exist = false)
    private String isSkipZero;

    @ApiModelProperty(value = "即将到期提醒天数")
    private Long toexpireDays;

    @TableField(exist = false)
    private String firstSort;

    @TableField(exist = false)
    private String secondSort;

    @TableField(exist = false)
    private String thirdSort;

    @ApiModelProperty(value = "辅料_采购下单状态")
    private String flCaigouxiadanStatus;

    @ApiModelProperty(value = "面料_采购下单状态")
    private String mlCaigouxiadanStatus;

    @TableField(exist = false)
    @Excel(name = "销售渠道")
    @ApiModelProperty(value = "业务渠道/销售渠道名称")
    private String businessChannelName;

    @TableField(exist = false)
    @Excel(name = "销售组织")
    @ApiModelProperty(value = "销售组织名称")
    private String saleOrgName;

    @Excel(name = "销售组")
    @TableField(exist = false)
    @ApiModelProperty(value = "销售组名称")
    private String saleGroupName;

    @ApiModelProperty(value = "待批销售价(含税)")
    @TableField(exist = false)
    private BigDecimal waitApprovalPrice;

    @ApiModelProperty(value = "待批税率")
    @TableField(exist = false)
    private BigDecimal waitApprovalTaxRate;

    @ApiModelProperty(value = "待批金额(含税)")
    @TableField(exist = false)
    private BigDecimal waitApprovalAmount;

    @Excel(name = "负责生产工厂sid(默认)")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "负责生产工厂sid(默认)")
    private Long producePlantSid;

    @Excel(name = "负责生产工厂code(默认)")
    @ApiModelProperty(value = "负责生产工厂code(默认)")
    private String producePlantCode;

    @ApiModelProperty(value = "初始税率")
    private BigDecimal initialTaxRate;

    @ApiModelProperty(value = "新税率")
    private BigDecimal newTaxRate;

    @ApiModelProperty(value = "系统税率")
    @TableField(exist = false)
    private BigDecimal systemTaxRate;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料档案规格尺寸")
    private String specificationSize;

    @ApiModelProperty(value = "合同特殊用途")
    @TableField(exist = false)
    private String contractPurpose;

    @TableField(exist = false)
    @ApiModelProperty(value = "订单量总")
    private BigDecimal sumQuantityDingd;

    @TableField(exist = false)
    @ApiModelProperty(value = "待出库")
    private BigDecimal sumQuantityDck;

    @TableField(exist = false)
    @ApiModelProperty(value = "已出库")
    private BigDecimal sumQuantityYck;

    @TableField(exist = false)
    @ApiModelProperty(value = "移动端未排产报表的排序")
    private String sort;
}
