package com.platform.ems.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

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

import com.platform.ems.domain.dto.request.OrderErrRequest;
import com.platform.ems.domain.dto.response.PurPurchaseOrderTotalResponse;
import com.platform.ems.domain.dto.response.SalSalesOrderSku2GroupResponse;
import com.platform.ems.domain.dto.response.SalSalesOrderTotalResponse;
import com.platform.ems.util.Phone;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

/**
 * 采购订单对象 s_pur_purchase_order
 *
 * @author linhongwei
 * @date 2021-04-08
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_pur_purchase_order")
public class PurPurchaseOrder extends EmsBaseEntity {

    /**
     * 客户端口号
     */
    @Excel(name = "客户端口号")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /**
     * 系统自增长ID-采购订单
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-采购订单")
    private Long purchaseOrderSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "采购订单号(多选)")
    private Long[] purchaseOrderSidList;
    /**
     * 采购订单号
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购订单号")
    private Long purchaseOrderCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "采购订单号(多选)")
    private Long[] purchaseOrderCodeList;

    /**
     * 单据类型编码
     */
    @Excel(name = "单据类型编码")
    @ApiModelProperty(value = "单据类型编码")
    private String documentType;

    /**
     * 单据类型名称
     */
    @TableField(exist = false)
    @Excel(name = "单据类型名称")
    @ApiModelProperty(value = "单据类型名称")
    private String documentTypeName;

    @Excel(name = "交货状态")
    @ApiModelProperty(value = "交货状态")
    private String deliveryStatus;

    @ApiModelProperty(value = "采购模式")
    private String purchaseMode;
    /**
     * 业务类型编码
     */
    @Excel(name = "业务类型编码")
    @ApiModelProperty(value = "业务类型编码")
    private String businessType;

    /**
     * 业务类型名称
     */
    @TableField(exist = false)
    @Excel(name = "业务类型")
    @ApiModelProperty(value = "业务类型名称")
    private String businessTypeName;

    /** 特殊业务类别编码code，如：供应商寄售结算 */
    @Excel(name = "特殊业务类别编码code，如：供应商寄售结算")
    @ApiModelProperty(value = "特殊业务类别编码code，如：供应商寄售结算")
    private String specialBusCategory;

    /**
     * 系统自增长ID-供应商信息
     */
    @Excel(name = "系统自增长ID-供应商信息")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-供应商信息")
    @NotNull(message = "供应商不能为空")
    private Long vendorSid;

    /**
     * 系统自增长ID-公司档案
     */
    @Excel(name = "系统自增长ID-公司档案")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-公司档案")
    @NotNull(message = "公司不能为空")
    private Long companySid;

    /** 业务渠道/销售渠道（数据字典的键值或配置档案的编码） */
    @Excel(name = "业务渠道/销售渠道（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "业务渠道/销售渠道（数据字典的键值或配置档案的编码）")
    private String businessChannel;

    /**
     * 采购员
     */
    @NotBlank(message = "采购员不能为空")
    @Excel(name = "采购员")
    @ApiModelProperty(value = "采购员")
    private String buyer;

    @TableField(exist = false)
    @ApiModelProperty(value = "采购员用户表的id")
    private Long buyerId;

    /**
     * 单据日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "单据日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "单据日期")
    @NotNull(message = "单据日期不能为空")
    private Date documentDate;

    /**
     * 系统自增长ID-产品季档案
     */
    @Excel(name = "系统自增长ID-产品季档案")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-产品季档案")
    private Long productSeasonSid;

    /**
     * 采购类别
     */
    @Excel(name = "采购类别")
    @ApiModelProperty(value = "采购类别")
    private String purchaseCategory;

    /**
     * 下单批次
     */
    @Excel(name = "下单批次")
    @ApiModelProperty(value = "下单批次")
    private String orderBatch;

    /**
     * 下单批次名称
     */
    @TableField(exist = false)
    @Excel(name = "下单批次名称")
    @ApiModelProperty(value = "下单批次名称")
    private String orderBatchName;

    /**
     * 物料类型编码
     */
    @Excel(name = "物料类型编码")
    @ApiModelProperty(value = "物料类型编码")
    private String materialType;

    @ApiModelProperty(value = "物料类别编码")
    private String materialCategory;

    /**
     * 物料类型名称
     */
    @TableField(exist = false)
    @Excel(name = "物料类型名称")
    @ApiModelProperty(value = "物料类型名称")
    private String materialTypeName;


    @ApiModelProperty(value = "查询：物料类型")
    @TableField(exist = false)
    private String[] materialTypeList;


    /**
     * 采购组织
     */
    @Excel(name = "采购组织")
    @ApiModelProperty(value = "采购组织")
    private String purchaseOrg;

    /**
     * 采购组织名称
     */
    @TableField(exist = false)
    @Excel(name = "采购组织名称")
    @ApiModelProperty(value = "采购组织名称")
    private String purchaseOrgName;

    /**
     * 采购组
     */
    @Excel(name = "采购组")
    @ApiModelProperty(value = "采购组")
    private String purchaseGroup;

    /**
     * 采购组名称
     */
    @TableField(exist = false)
    @Excel(name = "采购组名称")
    @ApiModelProperty(value = "采购组名称")
    private String purchaseGroupName;

    /**
     * 系统自增长ID-客户信息
     */
    @Excel(name = "系统自增长ID-客户信息")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-客户信息")
    private Long customerSid;

    /**
     * 配送类型
     */
    @Excel(name = "配送类型")
    @ApiModelProperty(value = "配送类型")
    private String shipmentType;

    /**
     * 配送方式名称
     */
    @TableField(exist = false)
    @Excel(name = "配送方式名称")
    @ApiModelProperty(value = "配送方式名称")
    private String shipmentTypeName;

    /**
     * 币种
     */
    @Excel(name = "币种")
    @ApiModelProperty(value = "币种")
    private String currency;

    @Excel(name = "货币单位")
    @ApiModelProperty(value = "货币单位")
    private String currencyUnit;

    /**
     * 收货人
     */
    @Length(max = 30, message = "收货人长度不能超过30个字符")
    @Excel(name = "收货人")
    @ApiModelProperty(value = "收货人")
    private String consignee;

    /**
     * 收货人联系电话
     */
    @Phone
    @Length(max = 20, message = "收货人联系电话长度不能超过20位")
    @Excel(name = "收货人联系电话")
    @ApiModelProperty(value = "收货人联系电话")
    private String consigneePhone;

    /**
     * 收货地址
     */
    @Excel(name = "收货地址")
    @ApiModelProperty(value = "收货地址")
    private String consigneeAddr;

    @ApiModelProperty(value = "收货人地址-省(编码)(冗余)")
    private String consigneeAddrProvinceCode;

    @ApiModelProperty(value = "收货人地址-省(名称)")
    private String consigneeAddrProvince;

    @ApiModelProperty(value = "收货人地址-市(编码)(冗余)")
    private String consigneeAddrCityCode;

    @ApiModelProperty(value = "收货人地址-市(名称)")
    private String consigneeAddrCity;

    @ApiModelProperty(value = "收货人地址-区(编码)(冗余)")
    private String consigneeAddrDistrictCode;

    @ApiModelProperty(value = "收货人地址-区(名称)")
    private String consigneeAddrDistrict;

    @ApiModelProperty(value = "外围系统出入库单号(WMS)")
    private String otherSystemInOutStockOrder;

    @ApiModelProperty(value = "是否已推送外围系统(WMS)")
    private String isPushOtherSystem;

    @ApiModelProperty(value = "外围系统推送结果(WMS)")
    private String pushResultOtherSystem;

    @ApiModelProperty(value = "外围系统推送返回信息(WMS)")
    private String pushReturnMsgOtherSystem;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "外围系统推送时间(WMS)")
    private Date pushTimeOtherSystem;

    /**
     * 系统自增长ID-采购合同
     */
    @Excel(name = "系统自增长ID-采购合同")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-采购合同")
    private Long purchaseContractSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统自增长ID-采购合同")
    private List<Long> purchaseContractSidList;

    /** 采购合同号 */
    @ApiModelProperty(value = "采购合同号")
    private String purchaseContractCode;

    @ApiModelProperty(value = "采购合同号(纸质合同)")
    private String paperPurchaseContractCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "采购合同号(纸质合同)是否为空")
    private String paperPurchaseContractCodeIsNull;

    @ApiModelProperty(value = "上传状态(纸质合同)（数据字典的键值或配置档案的编码）")
    private String uploadStatus;

    /**
     * 采购订单合同(盖章版)
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "采购订单合同(盖章版)")
    private String purchaseOrderContractGzbName;

    /**
     * 采购订单合同(盖章版)
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "采购订单合同(盖章版)")
    private String purchaseOrderContractGzbPath;

    @TableField(exist = false)
    @ApiModelProperty(value = "租户默认设置销售财务对接人员")
    private String purchaseFinanceAccountId;

    /**
     * 供方订单号
     */
    @Excel(name = "供方订单号")
    @ApiModelProperty(value = "供方订单号")
    private String vendorOrderCode;

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
    @Excel(name = "数据源系统")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;

    /**
     * 供料方式
     */
    @Excel(name = "供料方式")
    @ApiModelProperty(value = "供料方式")
    private String rawMaterialMode;


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
    @ApiModelProperty(value = "公司编码")
    private String companyCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @TableField(exist = false)
    @ApiModelProperty(value = "客户编码")
    private String customerCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "客户名称")
    private String customerName;

    @TableField(exist = false)
    @ApiModelProperty(value = "产品季编码")
    private String seasonCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "产品季名称")
    private String productSeasonName;

    @TableField(exist = false)
    @ApiModelProperty(value = "供应商名称")
    private String vendorName;

    @TableField(exist = false)
    @ApiModelProperty(value = "供应商编码")
    private String vendorCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "供应商类型")
    private String vendorType;

    /**
     * 采购订单sids
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "采购订单sids")
    private Long[] purchaseOrderSids;


    /**
     * 采购订单-明细对象
     */
    @Valid
    @TableField(exist = false)
    @ApiModelProperty(value = "采购订单-明细对象")
    private List<PurPurchaseOrderItem> purPurchaseOrderItemList;

    /**
     * 采购订单-附件对象
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "采购订单-附件对象")
    private List<PurPurchaseOrderAttachment> attachmentList;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品附件list")
    private List<BasMaterialAttachment> attachmentMaterialList;

    /**
     * 采购订单-合作伙伴对象
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "采购订单-合作伙伴对象")
    private List<PurPurchaseOrderPartner> purPurchaseOrderPartnerList;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "仓库sid")
    private Long storehouseSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "仓库名称")
    private String storehouseName;

    @TableField(exist = false)
    private String storehouseCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "库位sid")
    private Long storehouseLocationSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "外围系统仓库编码(WMS)")
    private String otherSystemStorehouseCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "库位名称")
    private String locationName;

    @TableField(exist = false)
    @ApiModelProperty(value = "库位编码")
    private String locationCode;

    @ApiModelProperty(value = "流程ID")
    private String instanceId;

    /** 流程状态 0：普通记录 1：待审批记录 2：审批结束记录*/
    @Excel(name = "流程状态")
    @ApiModelProperty(value = "流程状态")
    private String processType;

    /**
     * bom-明细对象
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "bom-明细对象")
    private List<TecBomItem> tecBomItemList;

    /** 单据类型编码list */
    @TableField(exist = false)
    @ApiModelProperty(value = "单据类型编码list")
    private String[] documentTypeList;

    /** 业务类型编码list */
    @TableField(exist = false)
    @ApiModelProperty(value = "业务类型编码list")
    private String[] businessTypeList;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-供应商信息list")
    private Long[] vendorSidList;

    /** 系统ID-公司档案list */
    @TableField(exist = false)
    @ApiModelProperty(value = "系统ID-公司档案list")
    private Long[] companySidList;

    /** 系统ID-产品季档案list */
    @TableField(exist = false)
    @ApiModelProperty(value = "系统ID-产品季档案list")
    private Long[] productSeasonSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "发货状态list")
    private String[] deliveryStatusList;

    /**
     * 供料方式list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "供料方式list")
    private String[] rawMaterialModeList;

    @TableField(exist = false)
    @ApiModelProperty(value = "甲供料方式（数据字典的键值）")
    private String rawMaterialModeContract;

    @TableField(exist = false)
    @ApiModelProperty(value = "甲供料方式（数据字典的键值）")
    private String[] rawMaterialModeContractList;

    @TableField(exist = false)
    @ApiModelProperty(value = "采购模式list")
    private String[] purchaseModeList;

    /**
     * 仓库list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "仓库list")
    private String[] storehouseSidList;

    /**
     * 下单批次list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "下单批次list")
    private String[] orderBatchList;

    /**
     * 配送类型list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "配送类型list")
    private String[] shipmentTypeList;

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

    /** 处理状态list */
    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态list")
    private String[] handleStatusList;

    @TableField(exist = false)
    @ApiModelProperty(value = "审批状态")
    private String[] processTypeList;

    /**
     * 合同名称
     */
    @TableField(exist = false)
    @Excel(name = "合同名称")
    @ApiModelProperty(value = "合同名称")
    private String contractName;

    @TableField(exist = false)
    @Excel(name = "预付款结算方式")
    @ApiModelProperty(value = "预付款结算方式")
    private String advanceSettleMode;
    /**
     * 采购员名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "采购员名称")
    @Excel(name = "采购员名称")
    private String nickName;

    @TableField(exist = false)
    private String creatorAccountName;

    @TableField(exist = false)
    private String updaterAccountName;

    @TableField(exist = false)
    private String confirmerAccountName;

    /** 当前审批节点名称 */
    @Excel(name = "当前审批节点名称")
    @ApiModelProperty(value = "当前审批节点名称")
    @TableField(exist = false)
    private String approvalNode;

    /** 当前审批人 */
    @Excel(name = "当前审批人")
    @ApiModelProperty(value = "当前审批人")
    @TableField(exist = false)
    private String approvalUserName;

    /** 提交人 */
    @Excel(name = "提交人")
    @ApiModelProperty(value = "提交人")
    @TableField(exist = false)
    private String submitUserName;

    /**
     * 提交日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "提交日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "提交日期")
    @TableField(exist = false)
    private Date submitDate;

    @Excel(name = " 供方跟单员")
    @ApiModelProperty(value = " 供方跟单员")
    private String vendorBusinessman;

    @ApiModelProperty(value = "供应商简称")
    @TableField(exist = false)
    private String vendorShortName;

    @Excel(name = "出入库状态", dictType = "s_in_out_store_status")
    @ApiModelProperty(value = "出入库状态")
    private String inOutStockStatus;


    @ApiModelProperty(value = "查询：出入库状态")
    @TableField(exist = false)
    private String[] inOutStockStatusList;

    @ApiModelProperty(value = "行号")
    @TableField(exist = false)
    private Long[] itemNumList;

    @ApiModelProperty(value = "是否退货（数据字典的键值或配置档案的编码）")
    private String isReturnGoods;

    /**
     * 是否生成财务待付预付流水（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "是否生成财务待付预付流水（数据字典的键值或配置档案的编码）")
    private String isFinanceBookDfyf;

    @ApiModelProperty(value = "是否生成财务流水（数据字典的键值或配置档案的编码）")
    private String isFinanceBookYfzg;

    @TableField(exist = false)
    @ApiModelProperty(value = "合同号是否为空（数据字典的键值或配置档案的编码）")
    private String contractIsNull;

    @Excel(name = "采购交货类别")
    @ApiModelProperty(value = "采购交货类别")
    private String  deliveryType;

    @ApiModelProperty(value = "库存管理方式（数据字典的键值或配置档案的编码）")
    private String inventoryControlMode;

    @ApiModelProperty(value = "预投/按需类型（数据字典的键值或配置档案的编码）")
    private String yutouAnxuType;

    @ApiModelProperty(value = " 是否寄售结算（数据字典的键值或配置档案的编码）")
    private String isConsignmentSettle;

    @TableField(exist = false)
    private String importHandle;

    @TableField(exist = false)
    @ApiModelProperty(value = "用来判断是否是导入操作")
    private String importType;

    @ApiModelProperty(value = "sid")
    @TableField(exist = false)
    List<Long> itemSidList;

    @ApiModelProperty(value = "总订单量")
    @TableField(exist = false)
    private BigDecimal sumQuantity;

    @ApiModelProperty(value = "总金额")
    @TableField(exist = false)
    private BigDecimal sumMoneyAmount;

    @ApiModelProperty(value = "总款数")
    @TableField(exist = false)
    private int sumQuantityCode;

    @ApiModelProperty(value = "签收状态")
    private String signInStatus;

    @ApiModelProperty(value = "合同特殊用途")
    @TableField(exist = false)
    private String contractPurpose;

    @ApiModelProperty(value = "查询：合同特殊用途")
    @TableField(exist = false)
    private String[] contractPurposeList;

    @TableField(exist = false)
    @ApiModelProperty(value = "合同处理状态")
    private String contractHandleStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "明细汇总")
    private List<PurPurchaseOrderTotalResponse> itemTotalList;

    @TableField(exist = false)
    private List<SalSalesOrderSku2GroupResponse> Sku2GroupList;

    @ApiModelProperty(value = "收货方说明")
    private String contactPartyRemark;

    @ApiModelProperty(value = "供应商名称备注，用于一次性供应商")
    private String vendorNameRemark ;

    @ApiModelProperty(value = "供应商组编码")
    @TableField(exist = false)
    private String vendorGroup;

    @ApiModelProperty(value = "是否无需审批")
    @TableField(exist = false)
    private String isNonApproval;

    @ApiModelProperty(value = "委托人账号")
    private String trustorAccount;

    @ApiModelProperty(value = "委托人")
    @TableField(exist = false)
    private String trustorAccountName;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司简称")
    private String companyShortName;

    @TableField(exist = false)
    @ApiModelProperty(value = "跳过校验")
    private String isSkipJudge;


    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long materialSid;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long sku1Sid;

    @TableField(exist = false)
    private BigDecimal taxRate;

    @TableField(exist = false)
    private BigDecimal unitConversionRate;

    @TableField(exist = false)
    private String unitBase;

    @TableField(exist = false)
    private String unitPrice;

    @TableField(exist = false)
    private BigDecimal purchasePriceTax;

    @TableField(exist = false)
    private BigDecimal purchasePrice;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long purchaseOrderItemSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "款备注")
    private String productCodes;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料/商品编码")
    private String materialCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "SKU1名称")
    private String sku1Name;

    @TableField(exist = false)
    @ApiModelProperty(value = "SKU2名称")
    private String sku2Name;

    @TableField(exist = false)
    @ApiModelProperty(value = "是否允许编辑价格（数据字典的键值或配置档案的编码）")
    private String isEditPrice;

    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "合同交期")
    private Date contractDate;
}
