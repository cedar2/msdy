package com.platform.ems.domain;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableId;
import com.platform.common.core.domain.EmsBaseEntity;
import com.platform.ems.domain.dto.request.InvInventoryDocumentCodeRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import lombok.experimental.Accessors;

import javax.validation.Valid;

/**
 * 库存凭证对象 s_inv_inventory_document
 *
 * @author linhongwei
 * @date 2021-04-16
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_inv_inventory_document")
public class InvInventoryDocument extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @ApiModelProperty(value = "参考业务单号")
    private String referBusinessNote;

    /**
     * 系统SID-库存凭证信息
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-库存凭证信息")
    private Long inventoryDocumentSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "产品季sid")
    private Long productSeasonSid;

    /**
     * 系统SID-产品季档案 多选数组
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "产品季sid")
    private Long[] productSeasonSidList;

    /**
     * 产品季档案编码
     */
    @ApiModelProperty(value = "产品季/下单季档案编码（数据字典的键值或配置档案的编码）")
    private String productSeasonCode;

    /**
     * 产品季档案名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "产品季/下单季档案名称（数据字典的键值或配置档案的编码）")
    private String productSeasonName;

    /**
     * 其它出入库所属业务类型(数据字典的键值或配置档案的编码)
     */
    @ApiModelProperty(value = "其它出入库所属业务类型（数据字典的键值或配置档案的编码）")
    private String businessType;

    /**
     * 其它出入库所属业务类型名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "其它出入库所属业务类型（数据字典的键值或配置档案的编码）")
    private String businessTypeName;

    /**
     * 其它出入库所属业务类型(数据字典的键值或配置档案的编码)多选数组
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "其它出入库所属业务类型（数据字典的键值或配置档案的编码）")
    private String[] businessTypeList;

    @ApiModelProperty(value = "是否已推送外围系统(极限云)")
    private String isPushOtherSystem;

    @ApiModelProperty(value = "外围系统推送结果(极限云)")
    private String pushResultOtherSystem;

    @ApiModelProperty(value = "外围系统推送返回信息(极限云)")
    private String pushReturnMsgOtherSystem;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "外围系统推送时间(极限云)")
    private Date pushTimeOtherSystem;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] inventoryDocumentSidList;

    /**
     * 库存凭证号
     */
    @Excel(name = "库存凭证号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "库存凭证号")
    private Long inventoryDocumentCode;


    private String handleStatus;

    /**
     * 单据日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "单据日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "单据日期")
    private Date documentDate;

    /**
     * 年份（过账）
     */
    @Excel(name = "年份（过账）")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "年份（过账）")
    private Long year;

    /**
     * 月份（过账）
     */
    @Excel(name = "月份（过账）")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "月份（过账）")
    private Long month;

    @TableField(exist = false)
    @ApiModelProperty(value = "类型 1出库 2入库")
    private String type;

    /**
     * 出入库操作人（用户名称）
     */
    @Excel(name = "出入库操作人（用户名称）")
    @ApiModelProperty(value = "出入库操作人（用户名称）")
    private String storehouseOperator;

    /**
     * 系统SID-公司档案
     */
    @Excel(name = "系统SID-公司档案")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-公司档案")
    private Long companySid;

    /**
     * 关联单据类别（数据字典的键值或配置档案的编码），如：领退料单、调拨单、库存调整单、盘点单、采购交货单、销售发货单、采购订单、生产订单
     */
    @Excel(name = "关联单据类别（数据字典的键值或配置档案的编码），如：领退料单、调拨单、库存调整单、盘点单、采购交货单、销售发货单、采购订单、生产订单")
    @ApiModelProperty(value = "关联单据类别（数据字典的键值或配置档案的编码），如：领退料单、调拨单、库存调整单、盘点单、采购交货单、销售发货单、采购订单、生产订单")
    private String referDocCategory;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：单据类别")
    private String[] referDocCategoryList;

    /**
     * 作业类型sid
     */
    @Excel(name = "作业类型sid")
    @ApiModelProperty(value = "作业类型sid")
    private String movementType;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：作业类型")
    private String[] movementTypeList;

    @ApiModelProperty(value = "单据类型编码code")
    private String documentType;

    @ApiModelProperty(value = "冲销前的库存凭证sid")
    private Long preInventoryDocumentSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "作业类型名称")
    private String movementTypeName;

    @TableField(exist = false)
    @ApiModelProperty(value = "单据类别名称")
    private String referDocCategoryName;

    /**
     * 运输单号
     */
    @Excel(name = "运输单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "运输单号")
    private Long shippingOrderCode;

    /**
     * 供方送货单号（供方交货单号）
     */
    @Excel(name = "供方送货单号（供方交货单号）")
    @ApiModelProperty(value = "供方送货单号（供方交货单号）")
    private String supplierDeliveryCode;

    @TableField(exist = false)
    @Excel(name = "销售员/采购员名称")
    private String nickName;

    /**
     * 出入库日期（过账）
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "出入库日期（过账）", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "出入库日期（过账）")
    private Date accountDate;

    /**
     * 系统SID-仓库档案
     */
    @Excel(name = "系统SID-仓库档案")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-仓库档案")
    private Long storehouseSid;

    /**
     * 系统SID-仓库档案
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-仓库档案")
    private Long[] storehouseSidList;

    /**
     * 货运单号
     */
    @Excel(name = "货运单号")
    @ApiModelProperty(value = "货运单号")
    private String carrierNoteCode;

    /**
     * 系统SID-库位
     */
    @Excel(name = "系统SID-库位")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-库位")
    private Long storehouseLocationSid;

    /**
     * 货运方（承运商）
     */
    @Excel(name = "货运方（承运商）")
    @ApiModelProperty(value = "货运方（承运商）")
    private String carrier;

    /**
     * 货运方(承运商)多选数组
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "货运方（承运商）sid")
    private String[] carrierList;

    /**
     * 系统SID-仓库档案（目的仓库）
     */
    @Excel(name = "系统SID-仓库档案（目的仓库）")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-仓库档案（目的仓库）")
    private Long destStorehouseSid;

    /**
     * 交易类型（数据字典的键值）
     */
    @Excel(name = "交易类型（数据字典的键值）")
    @ApiModelProperty(value = "交易类型（数据字典的键值）")
    private String tradeType;

    /**
     * 库存类型（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "目标库位")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "目标库位")
    private Long destStorehouseLocationSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(exist = false)
    @ApiModelProperty(value = "查询:目标库位")
    private Long[] destStorehouseLocationSidList;

    @ApiModelProperty(value = "作业类型编码")
    @TableField(exist = false)
    private String movementTypeCode;

    /**
     * 库存类型（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "库存类型（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "库存类型（数据字典的键值或配置档案的编码）")
    private String stockType;

    /**
     * 特殊库存（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "特殊库存（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "特殊库存（数据字典的键值或配置档案的编码）")
    private String specialStock;

    /**
     * 其它出入库对应的业务标识(数据字典的键值或配置档案的编码)
     */
    @ApiModelProperty(value = "其它出入库对应的业务标识（数据字典的键值或配置档案的编码）")
    private String businessFlag;

    /**
     * 其它出入库对应的业务标识(数据字典的键值或配置档案的编码)多选数组
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "其它出入库对应的业务标识（数据字典的键值或配置档案的编码）")
    private String[] businessFlagList;

    /**
     * 特殊库存供应商sid
     */
    @Excel(name = "特殊库存供应商sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "特殊库存供应商sid/无采购订单或采购退货订单的供应商")
    private Long vendorSid;

    /**
     * 创建人账号（用户名称）
     */
    @Excel(name = "创建人账号（用户名称）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    @TableField(exist = false)
    @ApiModelProperty(value = "创建人名称")
    private String creatorAccountName;

    @ApiModelProperty(value = "创建人查询")
    @TableField(exist = false)
    private String[] creatorAccountList;

    @TableField(exist = false)
    @ApiModelProperty(value = "出入库操作人")
    private String storehouseOperatorName;

    /**
     * 特殊库存客户sid
     */
    @Excel(name = "特殊库存客户sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "特殊库存客户sid/无销售订单或销售退货订单的客户")
    private Long customerSid;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /**
     * 凭证类别编码，用于表示：入库、出库、移库、调拨、库存调整、盘点、领退料
     */
    @Excel(name = "凭证类别编码，用于表示：入库、出库、移库、调拨、库存调整、盘点、领退料")
    @ApiModelProperty(value = "凭证类别编码，用于表示：入库、出库、移库、调拨、库存调整、盘点、领退料")
    private String documentCategory;

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
     * 系统SID-关联业务单据
     */
    @Excel(name = "系统SID-关联业务单据")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-关联业务单据")
    private Long referDocumentSid;

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

    /**
     * 公司名称
     */
    @TableField(exist = false)
    @Excel(name = "公司名称")
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @TableField(exist = false)
    @Excel(name = "公司名称")
    @ApiModelProperty(value = "公司名称")
    private String companyShortName;

    /**
     * 库存凭证sids
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "库存凭证sids")
    private Long[] inventoryDocumentSids;


    /**
     * 库存凭证-明细对象
     */
    @Valid
    @TableField(exist = false)
    @ApiModelProperty(value = "库存凭证-明细对象")
    private List<InvInventoryDocumentItem> invInventoryDocumentItemList;

    @TableField(exist = false)
    @ApiModelProperty(value = "入库详情页面明细汇总页签的数据")
    private List<InvInventoryDocumentItem> itemCollect;

    @TableField(exist = false)
    @ApiModelProperty(value = "库存凭证-附件对象")
    private List<InvInventoryDocumentAttach> invInventoryDocumentAttacList;


    @ApiModelProperty(value = "查询：单据日期起")
    @TableField(exist = false)
    private String documentBeginTime;

    @ApiModelProperty(value = "查询：单据日期至")
    @TableField(exist = false)
    private String documentEndTime;

    @ApiModelProperty(value = "查询：处理状态")
    @TableField(exist = false)
    private String[] handleStatusList;

    @ApiModelProperty(value = "查询：出入库时间起")
    @TableField(exist = false)
    private String accountDateBeginTime;

    @ApiModelProperty(value = "查询：出入库时间至")
    @TableField(exist = false)
    private String accountDateEndTime;

    @TableField(exist = false)
    @Excel(name = "公司编码")
    @ApiModelProperty(value = "公司编码")
    private String companyCode;

    @TableField(exist = false)
    @Excel(name = "供应商名称")
    @ApiModelProperty(value = "供应商名称")
    private String vendorName;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：供应商")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long[] vendorSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：客户")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long[] customerSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：库位")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long[] storehouseLocationSidList;

    @TableField(exist = false)
    @Excel(name = "供应商编码")
    @ApiModelProperty(value = "供应商编码")
    private String vendorCode;

    @TableField(exist = false)
    @Excel(name = "货运方名称")
    @ApiModelProperty(value = "货运方名称")
    private String carrierName;

    @TableField(exist = false)
    @Excel(name = "货运方编码")
    @ApiModelProperty(value = "货运方编码")
    private String carrierCode;

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
    @Excel(name = "目标仓库名称")
    @ApiModelProperty(value = "目标仓库名称")
    private String destStorehouseName;

    @TableField(exist = false)
    @Excel(name = "目标仓库编码")
    @ApiModelProperty(value = "目标仓库编码")
    private String destStorehouseCode;

    @TableField(exist = false)
    @Excel(name = "目标库位名称")
    @ApiModelProperty(value = "目标库位名称")
    private String destLocationName;

    @TableField(exist = false)
    @Excel(name = "目标库位编码")
    @ApiModelProperty(value = "目标库位编码")
    private String destLocationCode;

    @TableField(exist = false)
    @Excel(name = "客户编码")
    @ApiModelProperty(value = "客户编码")
    private String customerCode;

    @TableField(exist = false)
    @Excel(name = "客户名称")
    @ApiModelProperty(value = "客户名称")
    private String customerName;

    @ApiModelProperty(value = "查询：关联业务单号")
    private String referDocumentCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：关联业务单据类型")
    private String category;

    @TableField(exist = false)
    @ApiModelProperty(value = "特殊移库")
    private String SpecialToCommon;

    @ApiModelProperty(value = "调拨单号")
    @TableField(exist = false)
    private Long inventoryTransferCode;

    @ApiModelProperty(value = "单据类型")
    @TableField(exist = false)
    private String documentTypeName;

    @ApiModelProperty(value = "调拨单sid")
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long inventoryTransferSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统自增长ID-采购合同")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long purchaseContractSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "交货单号")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long deliveryNoteCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售合同号")
    private String saleContractCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统自增长ID-销售合同")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long saleContractSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "特殊库存")
    private String specialStockName;

    @TableField(exist = false)
    @ApiModelProperty(value = "库存凭证类别")
    private String documentCategoryName;

    @TableField(exist = false)
    @Excel(name = "采购订单编号")
    @ApiModelProperty(value = "采购订单编号")
    private String purchaseOrderCode;

    @TableField(exist = false)
    @Excel(name = "生产订单编号")
    @ApiModelProperty(value = "生产订单编号")
    private String manufactureOrderCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "退货销售订单号")
    private String comebackSaleCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "采购交货单号")
    private String deliveryPurchaseCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "退料单号")
    private String materialsReturnedCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "领料单号")
    private String materialRequisitionCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售订单号")
    private String salesOrderCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售订单号")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long salesOrderSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "采购订单号")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long purchaseOrderSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "采购退货订单号")
    private String comebackPurchaseCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售发货单号")
    private String deliverySaleCode;

    @TableField(exist = false)
    private List<InvInventoryDocumentCodeRequest> ListCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "判断出入库状态")
    private Boolean inOutStatus = true;


    @TableField(exist = false)
    @ApiModelProperty(value = "判断否是为出入库")
    private Boolean inOutStatusNo = true;

    @TableField(exist = false)
    @ApiModelProperty(value = "判断销售发货单 出入库状态")
    private Boolean inOutStatusDelDelivery = true;

    @TableField(exist = false)
    @ApiModelProperty(value = "判断销售发货单 出入库状态是否为0")
    private Boolean inOutStatusDelDeliveryNo = false;

    @TableField(exist = false)
    @ApiModelProperty(value = "判断出入库状态")
    private Boolean inOutStatusAll;

    @TableField(exist = false)
    @ApiModelProperty(value = "交货单sid")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long deliveryNoteSid;

    /**
     * 当前审批节点名称
     */
    @Excel(name = "当前审批节点名称")
    @ApiModelProperty(value = "当前审批节点名称")
    @TableField(exist = false)
    private String approvalNode;

    /**
     * 当前审批人
     */
    @Excel(name = "当前审批人")
    @ApiModelProperty(value = "当前审批人")
    @TableField(exist = false)
    private String approvalUserName;

    /**
     * 提交人
     */
    @Excel(name = "提交人")
    @ApiModelProperty(value = "提交人")
    @TableField(exist = false)
    private String submitUserName;

    @TableField(exist = false)
    @ApiModelProperty(value = "出入库冲销")
    private String documentTypeInv;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "出入库冲销-上一个单据sid")
    private Long inventoryDocumentOldSid;
    /**
     * 提交日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "提交日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "提交日期")
    @TableField(exist = false)
    private Date submitDate;

    @ApiModelProperty(value = "来源")
    @TableField(exist = false)
    private String source;

    @ApiModelProperty(value = "销售采购单据类型")
    @TableField(exist = false)
    private String saleAndPurchaseDocument;

    /**
     * 交货类别（数据字典的键值或配置档案的编码），如：采购交货、销售发货
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "交货类别（数据字典的键值或配置档案的编码），如：采购交货、销售发货")
    private String deliveryCategory;

    @TableField(exist = false)
    @ApiModelProperty(value = " 是否生成财务应收暂估流水（数据字典的键值或配置档案的编码）")
    private String isFinanceBookYszg;

    @TableField(exist = false)
    @ApiModelProperty(value = "  是否生成财务应付暂估流水（数据字典的键值或配置档案的编码）")
    private String isFinanceBookYfzg;

    @TableField(exist = false)
    @ApiModelProperty(value = "是否退货（数据字典的键值或配置档案的编码）")
    private String isReturnGoods;

    @ApiModelProperty(value = "特殊校验")
    @TableField(exist = false)
    private String specialVtil;


    @TableField(exist = false)
    @ApiModelProperty(value = "库存初始化：为Y")
    private String initializeStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "二维码")
    private String qrCode;

    @ApiModelProperty(value = "用途说明")
    private String usageRemark;

    @ApiModelProperty(value = "货源说明")
    private String sourceRemark;

    @Excel(name = "是否允许仓库可编辑", dictType = "s_yesno_flag")
    @ApiModelProperty(value = "是否允许仓库可编辑")
    @TableField(exist = false)
    private String isStorehouseEdit;

    @Excel(name = "是否允许库位可编辑", dictType = "s_yesno_flag")
    @ApiModelProperty(value = "是否允许库位可编辑")
    @TableField(exist = false)
    private String isStorehouseLocationEdit;

    @ApiModelProperty(value = "领料人账号名")
    private String materialReceiver;

    @ApiModelProperty(value = "领料人名称")
    @TableField(exist = false)
    private String materialReceiverName;

    @ApiModelProperty(value = "数量对应计量单位")
    @TableField(exist = false)
    private String referUnitType;

    @ApiModelProperty(value = "供应商简称")
    @TableField(exist = false)
    private String vendorShortName;

    @ApiModelProperty(value = "客户简称")
    @TableField(exist = false)
    private String customerShortName;

    @ApiModelProperty(value = "logo路径")
    @TableField(exist = false)
    private String logoPicturePath;

    @ApiModelProperty(value = "销售出库类别（数据字典的键值或配置档案的编码）")
    @TableField(exist = false)
    private String chukuCategory;

    @ApiModelProperty(value = "到货日期")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date daohuoDate;

    /**
     * 移库方式(数据字典的键值)
     */
    @ApiModelProperty(value = "移库方式（数据字典的键值或配置档案的编码）")
    private String stockTransferMode;

    /**
     * 移库方式(数据字典的键值)多选数组
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "移库方式（数据字典的键值或配置档案的编码）")
    private String[] stockTransferModeList;

    /**
     * 入库状态(数据字典的键值)：用于标识两步法移库的出库凭证的入库状态
     */
    @ApiModelProperty(value = "入库状态，用于标识两步法移库的出库凭证的入库状态")
    private String inOutStockStatus;

    /**
     * 前期没理解款备注的字段，所以用itemRemark，
     * 当前这个字段只在mapper.xml文件中查询条件(前端传)有用过一个
     * 其实款备注字段在明细中是 productCodes  这个字段
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "款备注")
    private String itemRemark;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料/商品编码")
    private String materialCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "SKU1名称")
    private String sku1Name;

    @TableField(exist = false)
    @ApiModelProperty(value = "SKU2名称")
    private String sku2Name;

    /**
     * 系统SID-班组档案
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-班组档案")
    private Long workCenterSid;

    /**
     * 系统SID-班组档案多选数组
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-班组档案多选数组")
    private Long[] workCenterSidList;

    /**
     * 班组档案编码
     */
    @ApiModelProperty(value = "班组档案编码")
    private String workCenterCode;

    /**
     * 班组档案名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "班组档案名称")
    private String workCenterName;

    /**
     * 是否多订单
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "是否多订单")
    private String isMultiOrder;

}
