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
import com.platform.ems.domain.dto.response.SalSalesOrderSku2GroupResponse;
import com.platform.ems.domain.dto.response.SalSalesOrderTotalResponse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 交货单对象 s_del_delivery_note
 *
 * @author linhongwei
 * @date 2021-04-21
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_del_delivery_note")
public class DelDeliveryNote extends EmsBaseEntity {

    /**
     * 客户端口号
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /**
     * 系统自增长ID-交货单
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-交货单")
    private Long deliveryNoteSid;

    /**
     * 交货单号
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "交货单号")
    private Long deliveryNoteCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-公司档案")
    private Long companySid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-仓库档案")
    private Long storehouseSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-库位")
    private Long storehouseLocationSid;

    /**
     * 单据类型（数据字典的键值）
     */
    @ApiModelProperty(value = "单据类型（数据字典的键值）")
    private String documentType;

    /**
     * 单据类型名称
     */
    @TableField(exist = false)
    @Excel(name = "单据类型")
    @ApiModelProperty(value = "单据类型名称")
    private String documentTypeName;

    /**
     * 业务类型（数据字典的键值）
     */
    @ApiModelProperty(value = "业务类型（数据字典的键值）")
    private String businessType;

    /**
     * 业务类型名称
     */
    @TableField(exist = false)
    @Excel(name = "业务类型")
    @ApiModelProperty(value = "业务类型名称")
    private String businessTypeName;

    @ApiModelProperty(value = "采购交货类别")
    @TableField(exist = false)
    private String deliveryType;

    /**
     * 交货类别（数据字典的键值或配置档案的编码），如：采购交货、销售发货
     */
    @ApiModelProperty(value = "交货类别（数据字典的键值或配置档案的编码），如：采购交货、销售发货")
    private String deliveryCategory;

    /**
     * 单据日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "单据日期", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss:sss")
    @ApiModelProperty(value = "单据日期")
    private Date documentDate;

    /**
     * 收货方sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "收货方sid")
    private Long receiverOrg;

    @TableField(exist = false)
    private String receiverOrgName;

    @TableField(exist = false)
    private String receiverOrgCode;

    /**
     * 收货方（供应商sid）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "收货方（供应商sid）")
    private Long receiverVendorSid;


    /**
     * 收货方（客户sid）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "收货方（客户sid）")
    private Long receiverCustomerSid;


    /**
     * 收货方（门店sid）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "收货方（门店sid）")
    private Long receiverShopSid;

    /**
     * 收货方类型（数据字典的键值）客户/供应商
     */
    @ApiModelProperty(value = "收货方类型（数据字典的键值）客户/供应商")
    private String receiverOrgType;

    /**
     * 收货人
     */
    @Length(max = 30, message = "收货人长度不能超过30位字符")
    @Excel(name = "收货人")
    @ApiModelProperty(value = "收货人")
    private String consignee;

    /**
     * 收货人联系电话
     */
    @Excel(name = "收货人联系电话")
    @ApiModelProperty(value = "收货人联系电话")
    private String consigneePhone;

    /**
     * 收货地址
     */
    @Excel(name = "收货地址")
    @ApiModelProperty(value = "收货地址")
    private String consigneeAddr;

    /**
     * 配送类型（数据字典的键值）
     */
    @ApiModelProperty(value = "配送类型（数据字典的键值）")
    private String shipmentType;

    /**
     * 配送方式名称
     */
    @TableField(exist = false)
    @Excel(name = "配送方式")
    @ApiModelProperty(value = "配送方式名称")
    private String shipmentTypeName;

    /**
     * 货运单号
     */
    @Length(max = 20, message = "货运单号长度不能超过20位字符")
    @Excel(name = "货运单号")
    @ApiModelProperty(value = "货运单号")
    private String carrierNoteCode;

    /**
     * 预计到货日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "预计到货日期")
    private Date expectedArrivalDate;

    /**
     * 装运点（数据字典的键值）
     */
    @ApiModelProperty(value = "装运点（数据字典的键值）")
    private String shippingPoint;

    /**
     * 信用类型（数据字典的键值）
     */
    @Excel(name = "信用类型")
    @ApiModelProperty(value = "信用类型（数据字典的键值）")
    private String creditType;

    /**
     * 预发货日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "预计发货日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "预发货日期")
    private Date expectedShipDate;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "发货日期(发货方)")
    private Date shipDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "发货日期(发货方)开始时间")
    private String shipDateBegin;

    @TableField(exist = false)
    @ApiModelProperty(value = "发货日期(发货方)结束时间")
    private String shipDateEnd;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "到货日期(收货方)")
    private Date arrivalDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "到货日期(收货方)开始时间")
    private String arrivalDateBegin;

    @TableField(exist = false)
    @ApiModelProperty(value = "到货日期(收货方)结束时间")
    private String arrivalDateEnd;

    /**
     * 出入库状态（数据字典的键值）
     */
    @Excel(name = "出入库状态", dictType = "s_in_out_store_status")
    @ApiModelProperty(value = "出入库状态（数据字典的键值）")
    private String inOutStockStatus;

    /**
     * 操作人（用户帐号）
     */
    @ApiModelProperty(value = "操作人（用户帐号）")
    private String operator;

    /**
     * 系统SID-销售订单（仅单订单时，存值）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-销售订单（仅单订单时，存值）")
    private Long salesOrderSid;

    /**
     * 系统SID-采购订单（仅单订单时，存值）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-采购订单（仅单订单时，存值）")
    private Long purchaseOrderSid;

    /**
     * 货运方（承运商）
     */
    @ApiModelProperty(value = "货运方（承运商）")
    private String carrier;

    /**
     * 货运方名称
     */
    @TableField(exist = false)
    @Excel(name = "货运方")
    @ApiModelProperty(value = "货运方名称")
    private String carrierName;

    /**
     * 货运方编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "货运方编码")
    private String carrierCode;

    @TableField(exist = false)
    private String salePersonName;

    @TableField(exist = false)
    private String creatorAccountName;

    @TableField(exist = false)
    private String updaterAccountName;

    @TableField(exist = false)
    private String confirmerAccountName;

    /**
     * 供方送货单号（供方交货单号）
     */
    @ApiModelProperty(value = "供方送货单号（供方交货单号）")
    private String supplierDeliveryCode;

    /**
     * 采购订单号code（仅单订单时，存值）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购订单号code（仅单订单时，存值）")
    private Long purchaseOrderCode;

    /**
     * 销售订单号code（仅单订单时，存值）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售订单号code（仅单订单时，存值）")
    private Long salesOrderCode;

    /**
     * 处理状态
     */
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    /**
     * 创建人账号
     */
    @Excel(name = "创建人账号")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss:sss")
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
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss:sss")
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
    @Excel(name = "确认时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss:sss")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 数据源系统
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;

    /**
     * 运输单号（停用）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "运输单号（停用）")
    private Long shippingOrderCode;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "创建日期开始时间")
    @TableField(exist = false)
    private String beginTime;

    @ApiModelProperty(value = "创建日期结束时间")
    @TableField(exist = false)
    private String endTime;

    @ApiModelProperty(value = "预发货日期开始时间")
    @TableField(exist = false)
    private String expectedShipDateBeginTime;

    @ApiModelProperty(value = "预发货日期结束时间")
    @TableField(exist = false)
    private String expectedShipDateEndTime;

    /**
     * 预计到货日期从
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "预计到货日期从")
    private String expectedArrivalBeginDate;

    /**
     * 预计到货日期至
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "预计到货日期至")
    private String expectedArrivalEndDate;

    @ApiModelProperty(value = "页数")
    @TableField(exist = false)
    private Integer pageNum;

    @ApiModelProperty(value = "每页个数")
    @TableField(exist = false)
    private Integer pageSize;

    /**
     * 交货单sids
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "交货单sids")
    private Long[] deliveryNoteSids;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "客户sid")
    private Long customerSid;

    /**
     * 客户编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "客户编码")
    private String customerCode;

    /**
     * 客户名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "客户名称")
    private String customerName;

    @TableField(exist = false)
    @Excel(name = "客户")
    @ApiModelProperty(value = "客户简称")
    private String customerShortName;

    /**
     * 公司代码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "公司代码")
    private String companyCode;

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
    @ApiModelProperty(value = "公司简称/名称")
    private String companyShortName;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售渠道")
    private String businessChannel;

    /**
     * 业务渠道/销售渠道名称
     */
    @TableField(exist = false)
    @Excel(name = "销售渠道")
    @ApiModelProperty(value = "业务渠道/销售渠道名称")
    private String businessChannelName;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售员")
    private String salePerson;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-产品季档案")
    private Long productSeasonSid;

    /**
     * 产品季编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "产品季编码")
    private String productSeasonCode;

    /**
     * 产品季名称
     */
    @TableField(exist = false)
    @Excel(name = "产品季")
    @ApiModelProperty(value = "产品季名称")
    private String productSeasonName;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统自增长ID-产品季档案")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long shipmentsProductSeasonSid;

    /**
     * 交货单产品季编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "交货单产品季编码")
    private String shipmentsProductSeasonCode;

    /**
     * 交货单产品季名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "交货单产品季名称")
    private String shipmentsProductSeasonName;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售组织编码")
    private String saleOrg;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料（商品/服务）编码")
    private String materialCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售部门")
    private String saleDepartment;

    /**
     * 部门编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "部门编码")
    private String departmentCode;

    /**
     * 部门名称
     */
    @TableField(exist = false)
    @Excel(name = "销售部门")
    @ApiModelProperty(value = "部门名称")
    private String departmentName;

    /**
     * 仓库编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "仓库编码")
    private String storehouseCode;

    /**
     * 仓库名称
     */
    @TableField(exist = false)
    @Excel(name = "仓库")
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
    @TableField(exist = false)
    @Excel(name = "库位")
    @ApiModelProperty(value = "库位名称")
    private String locationName;

    @TableField(exist = false)
    @ApiModelProperty(value = "外围系统仓库编码(WMS)")
    private String otherSystemStorehouseCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "供应商sid")
    private Long vendorSid;

    /**
     * 供应商编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "供应商编码")
    private String vendorCode;

    /**
     * 供应商名称
     */
    @TableField(exist = false)
    @Excel(name = "供应商")
    @ApiModelProperty(value = "供应商名称")
    private String vendorName;

    @TableField(exist = false)
    @ApiModelProperty(value = "供应商简称")
    private String vendorShortName;

    @TableField(exist = false)
    @ApiModelProperty(value = "采购组")
    private String purchaseOrg;

    @TableField(exist = false)
    @ApiModelProperty(value = "采购组织")
    private String purchaseOrgName;

    @TableField(exist = false)
    @ApiModelProperty(value = "采购员")
    private String buyer;

    @TableField(exist = false)
    @ApiModelProperty(value = "采购员")
    private String buyerName;

    /**
     * 销售员/采购员名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "销售员/采购员名称")
    @Excel(name = "销售员/采购员")
    private String nickName;

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

    @Excel(name = "采购销售业务标识")
    @ApiModelProperty(value = "采购销售业务标识")
    private String businessFlag;

    @ApiModelProperty(value = "销售模式/采购模式（数据字典的键值或配置档案的编码）")
    private String salePurchaseMode;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售模式/采购模式（多选）")
    private String[] salePurchaseModeList;

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

    @TableField(exist = false)
    @ApiModelProperty(value = "系统自增长ID-客户信息list")
    private Long[] customerSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统自增长ID-供应商信息list")
    private Long[] vendorSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统自增长ID-公司档案list")
    private Long[] companySidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售渠道list")
    private String[] businessChannelList;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售组织编码list")
    private String[] saleOrgList;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售部门list")
    private String[] saleDepartmentList;

    @TableField(exist = false)
    @ApiModelProperty(value = "采购组织list")
    private String[] purchaseOrgList;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统自增长ID-产品季档案list")
    private Long[] productSeasonSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "采购组")
    private String[] purchaseGroupList;

    /**
     * 出入库状态（数据字典的键值）list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "出入库状态（数据字典的键值）list")
    private String[] inOutStockStatusList;

    /**
     * 处理状态list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态list")
    private String[] handleStatusList;

    /**
     * 配送类型list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "配送类型list")
    private String[] shipmentTypeList;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-仓库档案")
    private Long[] storehouseSidList;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-库位")
    private Long[] storehouseLocationSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：采购员")
    private String[] buyerList;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：销售员")
    private String[] salePersonList;

    /**
     * 交货单-明细对象
     */
    @Valid
    @TableField(exist = false)
    @ApiModelProperty(value = "交货单-明细对象")
    private List<DelDeliveryNoteItem> delDeliveryNoteItemList;

    /**
     * 交货单-附件对象
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "交货单-附件对象")
    private List<DelDeliveryNoteAttachment> attachmentList;

    /**
     * 交货单-合作伙伴对象
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "交货单-合作伙伴对象")
    private List<DelDeliveryNotePartner> delDeliveryNotePartnerList;

    @ApiModelProperty(value = "出入库操作人（用户名称）")
    @TableField(exist = false)
    private String storehouseOperator;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "出入库日期（过账）", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(exist = false)
    @ApiModelProperty(value = "出入库日期（过账）")
    private Date accountDate;

    @ApiModelProperty(value = "是否多订单（数据字典的键值或配置档案的编码）")
    private String isMultipleOrders;

    @TableField(exist = false)
    @ApiModelProperty(value = "是否多订单（多选）")
    private String[] isMultipleOrdersList;

    @ApiModelProperty(value = "直发后续业务是否已处理（数据字典的键值或配置档案的编码）")
    private String isDirectTransportFollowup;

    @ApiModelProperty(value = "直发对应后续业务类型（数据字典的键值或配置档案的编码）")
    private String followupBusinessType;

    @ApiModelProperty(value = "是否退货（数据字典的键值或配置档案的编码）")
    private String isReturnGoods;

    @ApiModelProperty(value = " 是否寄售结算（数据字典的键值或配置档案的编码）")
    private String isConsignmentSettle;

    @ApiModelProperty(value = "库存管理方式（数据字典的键值或配置档案的编码）")
    private String inventoryControlMode;

    @ApiModelProperty(value = "销售采购单据类型")
    @TableField(exist = false)
    private String saleAndPurchaseDocument;

    @ApiModelProperty(value = "是否为虚拟库位")
    @TableField(exist = false)
    private String isVirtual;

    /**
     * 收货人地址-省(编码)(冗余)
     */
    @ApiModelProperty(value = "收货人地址-省(编码)(冗余)")
    private String consigneeAddrProvinceCode;

    /**
     * 收货人地址-省(名称)
     */
    @ApiModelProperty(value = "收货人地址-省(名称)")
    private String consigneeAddrProvince;

    /**
     * 收货人地址-市(编码)(冗余)
     */
    @ApiModelProperty(value = "收货人地址-市(编码)(冗余)")
    private String consigneeAddrCityCode;

    /**
     * 收货人地址-市(名称)
     */
    @ApiModelProperty(value = "收货人地址-市(名称)")
    private String consigneeAddrCity;

    /**
     * 收货人地址-区(编码)(冗余)
     */
    @ApiModelProperty(value = "收货人地址-区(编码)(冗余)")
    private String consigneeAddrDistrictCode;

    /**
     * 收货人地址-区(名称)
     */
    @ApiModelProperty(value = "收货人地址-区(名称)")
    private String consigneeAddrDistrict;

    /**
     * 发货人
     */
    @ApiModelProperty(value = "发货人")
    private String shipper;

    /**
     * 发货人联系电话
     */
    @ApiModelProperty(value = "发货人联系电话")
    private String shipperPhone;

    /**
     * 发货人地址-省(编码)(冗余)
     */
    @ApiModelProperty(value = "发货人地址-省(编码)(冗余)")
    private String shipperAddrProvinceCode;

    /**
     * 发货人地址-省(名称)
     */
    @ApiModelProperty(value = "发货人地址-省(名称)")
    private String shipperAddrProvince;

    /**
     * 发货人地址-市(编码)(冗余)
     */
    @ApiModelProperty(value = "发货人地址-市(编码)(冗余)")
    private String shipperAddrCityCode;

    /**
     * 发货人地址-市(名称)
     */
    @ApiModelProperty(value = "发货人地址-市(名称)")
    private String shipperAddrCity;

    /**
     * 发货人地址-区(编码)(冗余)
     */
    @ApiModelProperty(value = "发货人地址-区(编码)(冗余)")
    private String shipperAddrDistrictCode;

    /**
     * 发货人地址-区(名称)
     */
    @ApiModelProperty(value = "发货人地址-区(名称)")
    private String shipperAddrDistrict;

    /**
     * 发货地址
     */
    @ApiModelProperty(value = "发货地址")
    private String shipperAddr;

    /**
     * 外围系统出入库单号(WMS)
     */
    @ApiModelProperty(value = "外围系统出入库单号(WMS)")
    private String otherSystemInOutStockOrder;

    @ApiModelProperty(value = "外围系统销售发货单号(极限云)")
    private String otherSystemDeliveryNoteCode;

    @ApiModelProperty(value = "是否已推送外围系统(WMS)")
    private String isPushOtherSystem1;

    @ApiModelProperty(value = "外围系统推送结果(WMS)")
    private String pushResultOtherSystem1;

    @ApiModelProperty(value = "外围系统推送返回信息(WMS)")
    private String pushReturnMsgOtherSystem1;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "外围系统推送时间(WMS)")
    private Date pushTimeOtherSystem1;

    @ApiModelProperty(value = "是否已推送外围系统(极限云)")
    private String isPushOtherSystem2;

    @ApiModelProperty(value = "外围系统推送结果(极限云)")
    private String pushResultOtherSystem2;

    @ApiModelProperty(value = "外围系统推送返回信息(极限云)")
    private String pushReturnMsgOtherSystem2;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "外围系统推送时间(极限云)")
    private Date pushTimeOtherSystem2;

    @ApiModelProperty(value = " 是否生成财务应收暂估流水（数据字典的键值或配置档案的编码）")
    private String isFinanceBookYszg;

    @ApiModelProperty(value = "  是否生成财务应付暂估流水（数据字典的键值或配置档案的编码）")
    private String isFinanceBookYfzg;

    @ApiModelProperty(value = "总订单量")
    @TableField(exist = false)
    private BigDecimal sumQuantity;

    @TableField(exist = false)
    @ApiModelProperty(value = "创建人账号List")
    private String[] creatorAccountList;

    @TableField(exist = false)
    @ApiModelProperty(value = "明细汇总")
    private List<SalSalesOrderTotalResponse> itemTotalList;

    @TableField(exist = false)
    private List<SalSalesOrderSku2GroupResponse> Sku2GroupList;

    @ApiModelProperty(value = "联系方说明")
    private String contactPartyRemark;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售出库类别")
    private String chukuCategory;

    @TableField(exist = false)
    @ApiModelProperty(value = "二维码/条形码")
    private String qrCode;
}
