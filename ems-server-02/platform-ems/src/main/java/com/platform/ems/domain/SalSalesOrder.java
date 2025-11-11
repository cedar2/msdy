package com.platform.ems.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Null;

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
import com.platform.ems.domain.dto.response.SalSalesOrderSku2GroupResponse;
import com.platform.ems.domain.dto.response.SalSalesOrderTotalResponse;
import com.platform.ems.util.Phone;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

/**
 * 销售订单对象 s_sal_sales_order
 *
 * @author linhongwei
 * @date 2021-04-08
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_sal_sales_order")
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class SalSalesOrder extends EmsBaseEntity {

    /**
     * 客户端口号
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /**
     * 系统自增长ID-销售订单
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-销售订单")
    @TableId
    private Long salesOrderSid;

    @TableField(exist = false)
    private Long[] salesOrderSidList;

    /**
     * 销售订单号
     */
    @Excel(name = "销售订单号")
    @ApiModelProperty(value = "销售订单号")
    private String salesOrderCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售订单号(多选)")
    private String[] salesOrderCodeList;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售部门名称")
    private String departmentName;

    @ApiModelProperty(value = "销售合同号(纸质合同)")
    private String paperSaleContractCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售合同号(纸质合同)是否为空")
    private String paperSaleContractCodeIsNull;

    @ApiModelProperty(value = "上传状态(纸质合同)（数据字典的键值或配置档案的编码）")
    private String uploadStatus;

    @ApiModelProperty(value = "销售模式")
    private String saleMode;

    private String currencyUnit;
    /**
     * 单据类型编码
     */
    @ApiModelProperty(value = "单据类型编码")
    private String documentType;

    /**
     * 单据类型名称
     */
    @TableField(exist = false)
    @Excel(name = "单据类型")
    @ApiModelProperty(value = "单据类型名称")
    private String documentTypeName;

    /**
     * 业务类型编码
     */
    @ApiModelProperty(value = "业务类型编码")
    private String businessType;

    /**
     * 业务类型名称
     */
    @TableField(exist = false)
    @Excel(name = "业务类型")
    @ApiModelProperty(value = "业务类型名称")
    private String businessTypeName;

    /**
     * 特殊业务类别编码code，如：客户寄售结算
     */
    @ApiModelProperty(value = "特殊业务类别编码code，如：客户寄售结算")
    private String specialBusCategory;

    /**
     * 系统自增长ID-客户信息
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-客户信息")
    private Long customerSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "仓库名称")
    private String storehouseName;

    @TableField(exist = false)
    @ApiModelProperty(value = "库位名称")
    private String locationName;
    /**
     * 系统自增长ID-公司档案
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-公司档案")
    private Long companySid;

    /**
     * 业务渠道/销售渠道（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "业务渠道/销售渠道（数据字典的键值或配置档案的编码）")
    @NotEmpty(message = "销售渠道不能为空")
    private String businessChannel;

    /**
     * 业务渠道/销售渠道名称
     */
    @TableField(exist = false)
    @Excel(name = "销售渠道")
    @ApiModelProperty(value = "业务渠道/销售渠道名称")
    private String businessChannelName;

    /**
     * 销售员
     */
    @ApiModelProperty(value = "销售员")
    private String salePerson;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售员用户表的id")
    private Long salePersonId;

    @TableField(exist = false)
    @ApiModelProperty(value = "租户默认设置销售财务对接人员")
    private String saleFinanceAccountId;

    /**
     * 单据日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "单据日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "单据日期")
    private Date documentDate;

    /**
     * 系统自增长ID-产品季档案
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-产品季档案")
    private Long productSeasonSid;

    @TableField(exist = false)
    @Excel(name = "预付款结算方式")
    @ApiModelProperty(value = "预付款结算方式")
    private String advanceSettleMode;

    /**
     * 销售类别
     */
    @ApiModelProperty(value = "销售类别")
    private String saleCategory;

    /**
     * 物料类型编码
     */
    @ApiModelProperty(value = "物料类型编码")
    private String materialType;

    @ApiModelProperty(value = "物料类别编码")
    private String materialCategory;

    /**
     * 物料类型名称
     */
    @TableField(exist = false)
    @Excel(name = "物料类型")
    @ApiModelProperty(value = "物料类型名称")
    private String materialTypeName;

    /**
     * 销售部门
     */
    @ApiModelProperty(value = "销售部门")
    private String saleDepartment;

    /**
     * 币种
     */
    @Excel(name = "币种", dictType = "s_currency")
    @ApiModelProperty(value = "币种")
    private String currency;

    /**
     * 收货人
     */
    @Length(max = 20, message = "收货人长度不能超过20个字符")
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

    @Length(max = 20, message = "收货人长度不能超过20个字符")
    @ApiModelProperty(value = "收货人")
    private String shipper;

    @Phone
    @Length(max = 20, message = "收货人联系电话长度不能超过20位")
    @ApiModelProperty(value = "收货人联系电话")
    private String shipperPhone;

    @ApiModelProperty(value = "收货地址")
    private String shipperAddr;

    @ApiModelProperty(value = "收货人地址-省(编码)(冗余)")
    private String shipperAddrProvinceCode;

    @ApiModelProperty(value = "收货人地址-省(名称)")
    private String shipperAddrProvince;

    @ApiModelProperty(value = "收货人地址-市(编码)(冗余)")
    private String shipperAddrCityCode;

    @ApiModelProperty(value = "收货人地址-市(名称)")
    private String shipperAddrCity;

    @ApiModelProperty(value = "收货人地址-区(编码)(冗余)")
    private String shipperAddrDistrictCode;

    @ApiModelProperty(value = "收货人地址-区(名称)")
    private String shipperAddrDistrict;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "货运方sid（承运商）")
    private Long carrier;

    @TableField(exist = false)
    @ApiModelProperty(value = "货运方名称（承运商）")
    private String carrierCode;

    @ApiModelProperty(value = "货运方名称（承运商）")
    private String carrierName;

    @ApiModelProperty(value = "货运单号")
    private String carrierNoteCode;

    @ApiModelProperty(value = "是否已推送外围系统(WMS)")
    private String isPushOtherSystem1;

    @ApiModelProperty(value = "外围系统推送结果(WMS)")
    private String pushResultOtherSystem1;

    @ApiModelProperty(value = "外围系统推送返回信息(WMS)")
    private String pushReturnMsgOtherSystem1;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "外围系统推送返回信息(WMS)")
    private Date pushTimeOtherSystem1;

    @ApiModelProperty(value = "是否已推送外围系统(极限云)")
    private String isPushOtherSystem2;

    @ApiModelProperty(value = "外围系统推送结果(极限云)")
    private String pushResultOtherSystem2;

    @ApiModelProperty(value = "外围系统推送返回信息(极限云)")
    private String pushReturnMsgOtherSystem2;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "外围系统推送返回信息(极限云)")
    private Date pushTimeOtherSystem2;

    @ApiModelProperty(value = "外围系统出入库单号(WMS)")
    private String otherSystemInOutStockOrder;

    @ApiModelProperty(value = "外围系统销售订单号(极限云)")
    private String otherSystemSaleOrder;

    /**
     * 销售组织编码
     */
    @ApiModelProperty(value = "销售组织编码")
    private String saleOrg;

    /**
     * 销售组织名称
     */
    @TableField(exist = false)
    @Excel(name = "销售组织")
    @ApiModelProperty(value = "销售组织名称")
    private String saleOrgName;

    /**
     * 销售组
     */
    @ApiModelProperty(value = "销售组")
    private String saleGroup;

    /**
     * 销售组名称
     */
    @TableField(exist = false)
    @Excel(name = "销售组")
    @ApiModelProperty(value = "销售组名称")
    private String saleGroupName;

    /**
     * 收付款方式组合
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "收付款方式组合")
    private Long accountsMethodGroup;

    /**
     * 系统自增长ID-销售合同
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-销售合同")
    private Long saleContractSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统自增长ID-销售合同")
    private List<Long> saleContractSidList;

    /**
     * 销售合同号
     */
    @ApiModelProperty(value = "销售合同号")
    private String saleContractCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "客供料方式（数据字典的键值）")
    private String rawMaterialModeContract;

    @TableField(exist = false)
    @ApiModelProperty(value = "客供料方式（数据字典的键值）")
    private String[] rawMaterialModeContractList;

    /**
     * 客方订单号
     */
    @ApiModelProperty(value = "客方订单号")
    private String customerOrderCode;

    /**
     * 销售订单合同(盖章版)
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "销售订单合同(盖章版)")
    private String saleOrderContractGzbName;

    /**
     * 销售订单合同(盖章版)
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "销售订单合同(盖章版)")
    private String saleOrderContractGzbPath;

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
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;


    @TableField(exist = false)
    @ApiModelProperty(value = "创建人账号数组")
    private String[] creatorAccountList;
    /**
     * 创建人账号
     */
    @TableField(exist = false)
    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccountName;

    @TableField(exist = false)
    @Excel(name = "更新人")
    @ApiModelProperty(value = "更新人")
    private String updaterAccountName;

    @TableField(exist = false)
    @Excel(name = "确认人")
    @ApiModelProperty(value = "确认人")
    private String confirmerAccountName;

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
     * 合同名称
     */
    @TableField(exist = false)
    @Excel(name = "合同名称")
    @ApiModelProperty(value = "合同名称")
    private String contractName;

    /**
     * 供料方式
     */
    @ApiModelProperty(value = "供料方式")
    private String rawMaterialMode;

    /**
     * 仓库Sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "仓库Sid")
    private Long storehouseSid;

    /**
     * 仓库库位Sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "仓库库位Sid")
    private Long storehouseLocationSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "外围系统仓库编码(WMS)")
    private String otherSystemStorehouseCode;

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

    /**
     * 销售订单sids
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "销售订单sids")
    private Long[] salesOrderSids;

    /**
     * 销售订单-明细对象
     */
    @Valid
    @TableField(exist = false)
    @ApiModelProperty(value = "销售订单-明细对象")
    private List<SalSalesOrderItem> salSalesOrderItemList;

    /**
     * 销售订单-附件对象
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "销售订单-附件对象")
    private List<SalSalesOrderAttachment> attachmentList;

    /**
     * 销售订单-合作伙伴对象
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "销售订单-合作伙伴对象")
    private List<SalSalesOrderPartner> salSalesOrderPartnerList;

    @ApiModelProperty(value = "发货状态")
    private String deliveryStatus;

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

    @TableField(exist = false)
    @Excel(name = "客户")
    @ApiModelProperty(value = "客户名称")
    private String customerName;

    @TableField(exist = false)
    @ApiModelProperty(value = "客户编码")
    private String customerCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "客户类型")
    private String customerType;

    @TableField(exist = false)
    @Excel(name = "产品季")
    @ApiModelProperty(value = "产品季名称")
    private String productSeasonName;

    @TableField(exist = false)
    @ApiModelProperty(value = "产品季编码")
    private String productSeasonCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "节点名称")
    private String node;

    @TableField(exist = false)
    @ApiModelProperty(value = "审批人")
    private String approval;

    /**
     * bom-明细对象
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "bom-明细对象")
    private List<TecBomItem> tecBomItemList;

    /**
     * 销售员名称
     */
    @TableField(exist = false)
    @Excel(name = "销售员")
    private String nickName;

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
     * 系统自增长ID-客户信息list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "系统自增长ID-客户信息list")
    private Long[] customerSidList;

    /**
     * 系统ID-公司档案list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "系统ID-公司档案list")
    private Long[] companySidList;

    /**
     * 系统ID-产品季档案list
     */
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
    @ApiModelProperty(value = "销售模式list")
    private String[] saleModeList;

    /**
     * 仓库list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "仓库list")
    private String[] storehouseSidList;

    /**
     * 销售渠道lsit
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "销售渠道list")
    private String[] businessChannelList;

    /**
     * 销售部门list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "销售部门list")
    private Long[] saleDepartmentList;

    /**
     * 销售组织编码list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "销售组织编码list")
    private String[] saleOrgList;

    /**
     * 销售组list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "销售组list")
    private String[] saleGroupList;

    /**
     * 处理状态list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态list")
    private String[] handleStatusList;

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

    @Excel(name = "客方跟单员")
    @ApiModelProperty(value = "客方跟单员")
    private String customerBusinessman;

    @ApiModelProperty(value = "客户简称")
    @TableField(exist = false)
    private String customerShortName;

    @Excel(name = "出入库状态", dictType = "s_in_out_store_status")
    @ApiModelProperty(value = "出入库状态")
    private String inOutStockStatus;

    @Excel(name = "出入库状态", dictType = "s_in_out_store_status")
    @ApiModelProperty(value = "出入库状态")
    @TableField(exist = false)
    private String[] inOutStockStatusList;

    @ApiModelProperty(value = "行号")
    @TableField(exist = false)
    private Long[] itemNumList;

    @ApiModelProperty(value = "是否退货（数据字典的键值或配置档案的编码）")
    private String isReturnGoods;

    @ApiModelProperty(value = "是否生成财务待收预收流水（数据字典的键值或配置档案的编码）")
    private String isFinanceBookDsys;

    @ApiModelProperty(value = "是否生成财务流水（数据字典的键值或配置档案的编码）")
    private String isFinanceBookYszg;

    @ApiModelProperty(value = "库存管理方式（数据字典的键值或配置档案的编码）")
    private String inventoryControlMode;

    @ApiModelProperty(value = "销售发货类别（数据字典的键值或配置档案的编码）")
    private String  deliveryType;

    @ApiModelProperty(value = "是否排产（数据字典的键值或配置档案的编码）")
    private String isManufacture;

    @ApiModelProperty(value = " 是否寄售结算（数据字典的键值或配置档案的编码）")
    private String isConsignmentSettle;

    @TableField(exist = false)
    @ApiModelProperty(value = "合同号是否为空（数据字典的键值或配置档案的编码）")
    private String contractIsNull;

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

    @TableField(exist = false)
    @ApiModelProperty(value = "明细汇总")
    private List<SalSalesOrderTotalResponse> itemTotalList;

    @TableField(exist = false)
    private List<SalSalesOrderSku2GroupResponse> Sku2GroupList;

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

    @ApiModelProperty(value = "委托人账号")
    private String trustorAccount;

    @ApiModelProperty(value = "委托人")
    @TableField(exist = false)
    private String trustorAccountName;

    @ApiModelProperty(value = "收货方说明")
    private String contactPartyRemark;

    @ApiModelProperty(value = "客户名称备注，用于一次性客户")
    private String customerNameRemark;

    @ApiModelProperty(value = "客户组编码")
    @TableField(exist = false)
    private String customerGroup;

    @ApiModelProperty(value = "是否无需审批")
    @TableField(exist = false)
    private String isNonApproval;

    @TableField(exist = false)
    @ApiModelProperty(value = "跳过校验")
    private String isSkipJudge;

    @TableField(exist = false)
    private BigDecimal salePrice;

    @TableField(exist = false)
    private BigDecimal salePriceTax;


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
    @JsonSerialize(using = ToStringSerializer.class)
    private Long salesOrderItemSid;

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

    @ApiModelProperty(value = "作废原因code")
    private String cancelType;

    @ApiModelProperty(value = "作废说明")
    private String cancelRemark;

    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "合同交期")
    private Date contractDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料/商品名称")
    private String materialName;

    @ApiModelProperty(value = "数据来源类别（数据字典的键值或配置档案的编码）")
    private String referDocCategory;

    @TableField(exist = false)
    @ApiModelProperty(value = "是否允许编辑价格（数据字典的键值或配置档案的编码）")
    private String isEditPrice;
}
