package com.platform.ems.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotEmpty;

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

import com.platform.ems.util.data.KeepTwoDecimalsSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 财务流水账-应付暂估对象 s_fin_book_payment_estimation
 *
 * @author qhq
 * @date 2021-05-31
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_fin_book_payment_estimation")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FinBookPaymentEstimation extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-流水账（应付暂估）
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-流水账（应付暂估）")
    private Long bookPaymentEstimationSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "sid数组")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long[] bookPaymentEstimationSidList;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-流水账（应付暂估）")
    private Long bookPaymentEstimationItemSid;

    /**
     * 流水特征（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "流水特征（数据字典的键值或配置档案的编码）")
    private String bookFeature;

    /**
     * 财务流水类型编码code
     */
    @ApiModelProperty(value = "财务流水类型编码code")
    private String bookType;

    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "出入库日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "出入库日期")
    private Date accountDate;

    @TableField(exist = false)
    @ApiModelProperty(value ="出入库日期(起)")
    private String accountDateBegin;

    @TableField(exist = false)
    @ApiModelProperty(value ="出入库日期(止)")
    private String accountDateEnd;

    @Excel(name = "物料/商品编码")
    @ApiModelProperty(value = "物料商品编码")
    @TableField(exist = false)
    private String materialCode;

    @Excel(name = "物料/商品名称")
    @ApiModelProperty(value = "物料名称")
    @TableField(exist = false)
    private String materialName;

    @Excel(name = "SKU1名称")
    @TableField(exist = false)
    @ApiModelProperty(value = "物料sku1名称")
    private String sku1Name;

    @Excel(name = "SKU2名称")
    @TableField(exist = false)
    @ApiModelProperty(value = "物料sku2名称")
    private String sku2Name;

    @TableField(exist = false)
    @ApiModelProperty(value = "基本计量单位")
    private String unitBaseName;

    @Excel(name = "采购价单位")
    @TableField(exist = false)
    @ApiModelProperty(value = "采购单位")
    private String unitPriceName;

    @Excel(name = "订单量")
    @TableField(exist = false)
    @ApiModelProperty(value = "订单量")
    private BigDecimal orderQuantity;

    @Excel(name = "出入库数量")
    @TableField(exist = false)
    @ApiModelProperty(value = "出入库量")
    private BigDecimal quantity;

    @Excel(name = "采购价（含税）")
    @TableField(exist = false)
    @ApiModelProperty(value = "采购价（含税）")
    private BigDecimal priceTax;

    @Excel(name = "采购价（不含税）")
    @TableField(exist = false)
    @ApiModelProperty(value = "采购价（不含税）")
    private BigDecimal price;

    @Excel(name = "金额（含税）")
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @TableField(exist = false)
    @ApiModelProperty(value = "金额（含税）")
    private BigDecimal currencyAmountTax;

    @Excel(name = "核销状态",dictType = "s_account_clear")
    @TableField(exist = false)
    @ApiModelProperty(value = "核销状态")
    private String clearStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "核销状态（含税金额）")
    private String clearStatusMoney;

    @TableField(exist = false)
    @ApiModelProperty(value = "核销状态")
    private String[] clearStatusList;

    @Excel(name = "待核销金额")
    @TableField(exist = false)
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "待核销金额（含税）")
    private BigDecimal currencyAmountTaxDhx;

    @Excel(name = "核销中金额")
    @TableField(exist = false)
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "核销中金额（含税）")
    private BigDecimal currencyAmountTaxHxz;

    @Excel(name = "已核销金额")
    @TableField(exist = false)
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "已核销金额（含税）")
    private BigDecimal currencyAmountTaxYhx;


    @TableField(exist = false)
    @ApiModelProperty(value = "核销状态（数量）")
    private String clearStatusQuantity;

    @Excel(name = "待核销数量")
    @ApiModelProperty(value = "数量（出入库）待核销")
    @TableField(exist = false)
    private BigDecimal quantityDhx;

    @Excel(name = "核销中数量")
    @ApiModelProperty(value = "数量（出入库）核销中")
    @TableField(exist = false)
    private BigDecimal quantityHxz;

    @Excel(name = "已核销数量")
    @ApiModelProperty(value = "数量（出入库）已核销")
    @TableField(exist = false)
    private BigDecimal quantityYhx;

    @Excel(name = "已核销金额（含税）付款")
    @ApiModelProperty(value = "已核销金额（含税）付款")
    @TableField(exist = false)
    private BigDecimal currencyAmountTaxFkYhx;

    @Excel(name = "核销中金额（含税）付款")
    @ApiModelProperty(value = "核销中金额（含税）付款")
    @TableField(exist = false)
    private BigDecimal currencyAmountTaxFkHxz;

    @Excel(name = "已核销金额（含税）开票")
    @ApiModelProperty(value = "已核销金额（含税）开票")
    @TableField(exist = false)
    private BigDecimal currencyAmountTaxKpYhx;

    @Excel(name = "核销中金额（含税）开票")
    @ApiModelProperty(value = "核销中金额（含税）开票")
    @TableField(exist = false)
    private BigDecimal currencyAmountTaxKpHxz;

    @Excel(name = "税率")
    @TableField(exist = false)
    @ApiModelProperty(value = "税率")
    private String taxRateName;

    /**
     * 采购合同号
     */
    @TableField(exist = false)
    @Excel(name = "采购合同号")
    @ApiModelProperty(value = "采购合同号")
    private String purchaseContractCode;

    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "合同交期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "合同交期")
    private Date contractDate;

    @Excel(name = "下单季")
    @TableField(exist = false)
    @ApiModelProperty(value = "产品季")
    private String productSeasonName;

    @TableField(exist = false)
    @Excel(name = "采购模式", dictType = "s_price_type")
    @ApiModelProperty(value = "采购模式")
    private String purchaseMode;

    @Excel(name = "供应商")
    @ApiModelProperty(value = "供应商名称")
    @TableField(exist = false)
    private String vendorName;

    @ApiModelProperty(value = "供应商简称")
    @TableField(exist = false)
    private String vendorShortName;

    /**
     * 采购订单号
     */
    @Excel(name = "采购订单号")
    @ApiModelProperty(value = "采购订单号")
    @TableField(exist = false)
    private String purchaseOrderCode;

    /**
     * 流水号（应付暂估）
     */
    @Excel(name = "应付暂估流水号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "流水号（应付暂估）")
    private Long bookPaymentEstimationCode;

    /**
     * 财务流水类型编码code
     */
    @Excel(name = "流水类型")
    @ApiModelProperty(value = "财务流水类型编码code")
    @TableField(exist = false)
    private String bookTypeName;

    /**
     * 财务流水来源类别编码code（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "财务流水来源类别编码code（数据字典的键值或配置档案的编码）")
    private String bookSourceCategory;

    /**
     * 财务流水来源类别编码code（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "流水来源类别")
    @ApiModelProperty(value = "财务流水来源类别编码code（数据字典的键值或配置档案的编码）")
    @TableField(exist = false)
    private String bookSourceCategoryName;

    /**
     * 处理状态（数据字典的键值或配置档案的编码）
     */
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态（用来过滤）")
    private String handleStatusNot;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态（多选）")
    private String[] handleStatusList;

    /**
     * 采购交货单号
     */
    @Excel(name = "采购交货单号")
    @ApiModelProperty(value = "采购交货单号")
    @TableField(exist = false)
    private String deliveryNoteCode;

    @Excel(name = "公司")
    @ApiModelProperty(value = "公司名称")
    @TableField(exist = false)
    private String companyName;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司简称")
    private String companyShortName;

    @TableField(exist = false)
    @Excel(name = "物料类型")
    @ApiModelProperty(value = "物料类型")
    private String materialTypeName;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料类型")
    private String[] materialTypeList;

    @Excel(name = "库存凭证号")
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "库存凭证号")
    private Long inventoryDocumentCode;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "库存凭证号明细sid")
    private Long inventoryDocumentItemSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "库存凭证号明细sid")
    private Long[] inventoryDocumentItemSidList;

    @Excel(name = "是否已业务对账", dictType = "s_yesno_flag")
    @TableField(exist = false)
    @ApiModelProperty(value = "是否已业务对账（数据字典的键值或配置档案的编码）")
    private String isBusinessVerify;

    /**
     * 创建时间
     */
    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @Excel(name = "对账账期")
    @TableField(exist = false)
    @ApiModelProperty(value = "业务对账所属期间（所属年月）")
    private String businessVerifyPeriod;

    @TableField(exist = false)
    @ApiModelProperty(value = "是否已财务对账（数据字典的键值或配置档案的编码）")
    private String isFinanceVerify;

    @Excel(name = "出入库人")
    @TableField(exist = false)
    @ApiModelProperty(value = "出入库人")
    private String storehouseOperatorName;

    @ApiModelProperty(value = "币种")
    private String currency;

    /**
     * 货币单位
     */
    @ApiModelProperty(value = "货币单位")
    private String currencyUnit;

    /**
     * 系统SID-供应商
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商")
    private Long vendorSid;

    @ApiModelProperty(value = "供应商Code")
    private String vendorCode;

    /**
     * 系统SID-公司档案
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-公司档案")
    private Long companySid;

    @ApiModelProperty(value = "公司Code")
    private String companyCode;

    @ApiModelProperty(value = "是否退货（数据字典的键值或配置档案的编码）")
    private String isTuihuo;

    /**
     * 公司品牌sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司品牌sid")
    private Long companyBrandSid;

    @ApiModelProperty(value = "公司品牌Code")
    @TableField(exist = false)
    private String brandCode;

    @ApiModelProperty(value = "公司品牌名称")
    @TableField(exist = false)
    private String brandName;

    @ApiModelProperty(value = "物料sid")
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long materialSid;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品条码（物料&商品&服务）")
    private Long barcodeSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "物料sku1Sid")
    @TableField(exist = false)
    private Long sku1Sid;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品sku2")
    private Long sku2Sid;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "产品季")
    private Long productSeasonSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购订单SID")
    @TableField(exist = false)
    private Long purchaseOrderSid;


    /**
     * 销售渠道（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "销售渠道（数据字典的键值或配置档案的编码）")
    private String businessChannel;

    @TableField(exist = false)
    @ApiModelProperty(value = "出入库人")
    private String storehouseOperator;

    @TableField(exist = false)
    @ApiModelProperty(value = "账期有效期（起）")
    private String accountValidDate;



    @TableField(exist = false)
    @ApiModelProperty(value = "核销状态")
    private String clearStatusNot;

    /**
     * 当前采购价（不含税）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "当前采购价（不含税）")
    private BigDecimal currentPrice;

    /**
     * 当前采购价（含税）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "当前采购价（含税）")
    private BigDecimal currentPriceTax;

    @TableField(exist = false)
    @ApiModelProperty(value = "采购员")
    private String buyer;

    @TableField(exist = false)
    @ApiModelProperty(value = "采购组织")
    private String purchaseOrg;

    @TableField(exist = false)
    @ApiModelProperty(value = "甲供料方式")
    private String rawMaterialMode;

    @TableField(exist = false)
    @ApiModelProperty(value = "业务类型")
    private String businessType;

    @TableField(exist = false)
    @ApiModelProperty(value = "单据类型")
    private String documentType;

    /**
     * 凭证日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "凭证日期")
    private Date documentDate;

    /**
     * 年份
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "年份")
    private Long paymentYear;

    /**
     * 月份
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "月份")
    private Long paymentMonth;

    @TableField(exist = false)
    @ApiModelProperty(value = "基本计量单位")
    private String unitBase;

    @TableField(exist = false)
    @ApiModelProperty(value = "采购单位")
    private String unitPrice;

    @TableField(exist = false)
    @ApiModelProperty(value = "税率")
    private BigDecimal taxRate;

    /**
     * 创建人账号（用户名称）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    /**
     * 更新人账号（用户名称）
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 确认人账号（用户名称）
     */
    @ApiModelProperty(value = "确认人账号（用户名称）")
    private String confirmerAccount;

    /**
     * 确认时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    @TableField(exist = false)
    private List<FinBookPaymentEstimationItem> itemList;

    @TableField(exist = false)
    private List<FinBookPaymentEstimationAttachment> atmList;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-供应商")
    private Long[] vendorSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-公司")
    private Long[] companySidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-产品季")
    private Long[] productSeasonSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-流水类型")
    private String[] bookTypeList;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-流水来源类别")
    private String[] bookSourceCategoryList;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-核销状态")
    private String[] clearStatusMoneyList;

    @TableField(exist = false)
    private String processType;

    @TableField(exist = false)
    private String instanceId;

    @TableField(exist = false)
    @ApiModelProperty(value = "节点名称")
    private String node;

    @TableField(exist = false)
    @ApiModelProperty(value = "审批人")
    private String approval;

    @TableField(exist = false)
    @ApiModelProperty(value = "行号")
    private Long itemNum;

}
