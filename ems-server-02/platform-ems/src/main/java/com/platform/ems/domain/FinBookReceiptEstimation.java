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
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;

import com.platform.ems.util.data.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 财务流水账-应收暂估对象 s_fin_book_receipt_estimation
 *
 * @author qhq
 * @date 2021-06-08
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_fin_book_receipt_estimation")
public class FinBookReceiptEstimation extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-流水账（应收暂估）
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-流水账（应收暂估）")
    private Long bookReceiptEstimationSid;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-流水账（应收暂估）")
    private Long bookReceiptEstimationItemSid;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "sid数组")
    private Long[] bookReceiptEstimationSidList;

    /**
     * 流水类型编码code
     */
    @ApiModelProperty(value = "流水类型编码code")
    @TableField(exist = false)
    private String accountType;

    /**
     * 款项类别编码code
     */
    @ApiModelProperty(value = "款项类别编码code")
    @TableField(exist = false)
    private String accountCategory;

    /**
     * 流水特征（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "流水特征（数据字典的键值或配置档案的编码）")
    private String bookFeature;

    @Excel(name = "出入库日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "出入库日期")
    private Date accountDate;

    @TableField(exist = false)
    @ApiModelProperty(value ="出入库日期(起)")
    private String accountDateBegin;

    @TableField(exist = false)
    @ApiModelProperty(value ="出入库日期(止)")
    private String accountDateEnd;

    @Excel(name = "物料/商品编码")
    @ApiModelProperty(value = "物料code")
    @TableField(exist = false)
    private String materialCode;

    @Excel(name = "物料/商品名称")
    @ApiModelProperty(value = "物料name")
    @TableField(exist = false)
    private String materialName;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品sku1")
    private Long sku1Sid;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品sku2")
    private Long sku2Sid;

    @Excel(name = "SKU1名称")
    @ApiModelProperty(value = "SKU1名称")
    @TableField(exist = false)
    private String sku1Name;

    @Excel(name = "SKU2名称")
    @ApiModelProperty(value = "SKU2名称")
    @TableField(exist = false)
    private String sku2Name;

    @TableField(exist = false)
    @ApiModelProperty(value = "计量单位")
    private String unitBaseName;

    @Excel(name = "销售价单位")
    @TableField(exist = false)
    @ApiModelProperty(value = "销售价单位")
    private String unitPriceName;

    @Excel(name = "订单量")
    @ApiModelProperty(value = "数量（出入库）")
    @TableField(exist = false)
    private BigDecimal orderQuantity;

    @Excel(name = "出入库数量")
    @ApiModelProperty(value = "数量（出入库）")
    @TableField(exist = false)
    private BigDecimal quantity;

    @Excel(name = "销售价（含税）")
    @JsonSerialize(using = KeepFiveDecimalsSerialize.class)
    @ApiModelProperty(value = "销售价（含税）")
    @TableField(exist = false)
    private BigDecimal priceTax;

    @Excel(name = "销售价（不含税）")
    @JsonSerialize(using = KeepSixDecimalsSerialize.class)
    @ApiModelProperty(value = "销售价（不含税）")
    @TableField(exist = false)
    private BigDecimal price;

    @Excel(name = "金额（含税）")
    @ApiModelProperty(value = " 销售金额/出入库金额（含税）")
    @TableField(exist = false)
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    private BigDecimal currencyAmountTax;

    @Excel(name = "核销状态",dictType = "s_account_clear")
    @ApiModelProperty(value = "核销状态")
    @TableField(exist = false)
    private String clearStatus;

    @ApiModelProperty(value = "金额核销状态")
    @TableField(exist = false)
    private String clearStatusMoney;

    @Excel(name = "待核销金额")
    @ApiModelProperty(value = " 待核销金额（含税）")
    @TableField(exist = false)
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    private BigDecimal currencyAmountTaxDhx;

    @Excel(name = "核销中金额")
    @ApiModelProperty(value = "核销中金额（含税）")
    @TableField(exist = false)
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    private BigDecimal currencyAmountTaxHxz;

    @Excel(name = "已核销金额")
    @ApiModelProperty(value = "已核销金额（含税）")
    @TableField(exist = false)
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    private BigDecimal currencyAmountTaxYhx;

    @ApiModelProperty(value = "核销状态（数量）")
    @TableField(exist = false)
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

    @Excel(name = "已核销金额（含税）收款")
    @ApiModelProperty(value = "已核销金额（含税）收款")
    @TableField(exist = false)
    private BigDecimal currencyAmountTaxSkYhx;

    @Excel(name = "核销中金额（含税）收款")
    @ApiModelProperty(value = "核销中金额（含税）收款")
    @TableField(exist = false)
    private BigDecimal currencyAmountTaxSkHxz;

    @Excel(name = "已核销金额（含税）到票")
    @ApiModelProperty(value = "已核销金额（含税）到票")
    @TableField(exist = false)
    private BigDecimal currencyAmountTaxDpYhx;

    @Excel(name = "核销中金额（含税）到票")
    @ApiModelProperty(value = "核销中金额（含税）到票")
    @TableField(exist = false)
    private BigDecimal currencyAmountTaxDpHxz;

    @Excel(name = "税率")
    @ApiModelProperty(value = "税率")
    @TableField(exist = false)
    private BigDecimal taxRate;

    @ApiModelProperty(value = "税率")
    @TableField(exist = false)
    private String taxRateName;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售合同sid")
    private Long saleContractSid;

    @TableField(exist = false)
    @Excel(name = "销售合同号")
    @ApiModelProperty(value = "销售合同code")
    private String saleContractCode;

    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "合同交期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "合同交期")
    private Date contractDate;

    @Excel(name = "下单季")
    @TableField(exist = false)
    @ApiModelProperty(value = "产品季")
    private String productSeasonName;

    /**
     * 业务渠道/销售渠道名称
     */
    @TableField(exist = false)
    @Excel(name = "销售渠道")
    @ApiModelProperty(value = "业务渠道/销售渠道名称")
    private String businessChannelName;

    @Excel(name = "客户")
    @ApiModelProperty(value = "客户名称")
    @TableField(exist = false)
    private String customerName;

    @TableField(exist = false)
    @ApiModelProperty(value = "客户简称")
    private String customerShortName;

    @Excel(name = "销售订单号")
    @ApiModelProperty(value = "销售订单code")
    @TableField(exist = false)
    private String salesOrderCode;

    /**
     * 流水号（应收暂估）
     */
    @Excel(name = "应收暂估流水号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "流水号（应收暂估）")
    private Long bookReceiptEstimationCode;

    /**
     * 财务流水类型编码code
     */
    @ApiModelProperty(value = "财务流水类型编码code")
    private String bookType;

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
    @NotEmpty(message = "处理状态不能为空")
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态（用来过滤）")
    private String handleStatusNot;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态（多选）")
    private String[] handleStatusList;

    @Excel(name = "销售发货单号")
    @ApiModelProperty(value = "销售发货单号code")
    @TableField(exist = false)
    private String deliveryNoteCode;

    @Excel(name = "公司")
    @ApiModelProperty(value = "公司name")
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

    @Excel(name = "是否已财务对账", dictType = "s_yesno_flag")
    @TableField(exist = false)
    @ApiModelProperty(value = "是否已财务对账（数据字典的键值或配置档案的编码）")
    private String isFinanceVerify;

    @Excel(name = "出入库人")
    @TableField(exist = false)
    @ApiModelProperty(value = "出入库人")
    private String storehouseOperatorName;

    /**
     * 系统SID-客户
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-客户")
    private Long customerSid;

    @ApiModelProperty(value = "客户")
    private String customerCode;

    /**
     * 系统SID-公司档案
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-公司档案")
    private Long companySid;

    @ApiModelProperty(value = "公司")
    private String companyCode;

    @ApiModelProperty(value = "是否退货（数据字典的键值或配置档案的编码）")
    private String isTuihuo;

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

    /**
     * 公司品牌sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司品牌sid")
    private Long companyBrandSid;

    /**
     * 销售渠道（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "销售渠道（数据字典的键值或配置档案的编码）")
    @TableField(exist = false)
    private String saleChannel;

    /**
     * 销售渠道（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "销售渠道（数据字典的键值或配置档案的编码）")
    private String businessChannel;

    /**
     * 币种
     */
    @ApiModelProperty(value = "币种")
    private String currency;

    /**
     * 货币单位
     */
    @ApiModelProperty(value = "货币单位")
    private String currencyUnit;

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

    @ApiModelProperty(value = "明细表LIST")
    @TableField(exist = false)
    private List<FinBookReceiptEstimationItem> itemList;

    @ApiModelProperty(value = "附件表LIST")
    @TableField(exist = false)
    private List<FinBookReceiptEstimationAttachment> atmList;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "物料sid")
    @TableField(exist = false)
    private Long materialSid;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品条码（物料&商品&服务）")
    private Long barcodeSid;

    @ApiModelProperty(value = "计量单位")
    @TableField(exist = false)
    private String unitBase;

    @ApiModelProperty(value = "销售价单位")
    @TableField(exist = false)
    private String unitPrice;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售订单sid")
    @TableField(exist = false)
    private Long salesOrderSid;


    @ApiModelProperty(value = "核销状态")
    @TableField(exist = false)
    private String[] clearStatusList;

    @TableField(exist = false)
    @ApiModelProperty(value = "核销状态")
    private String clearStatusNot;

    @ApiModelProperty(value = "账期有效期（起）")
    @TableField(exist = false)
    private String accountValidDate;

    @ApiModelProperty(value = "公司品牌code")
    @TableField(exist = false)
    private String brandCode;

    @ApiModelProperty(value = "公司品牌name")
    @TableField(exist = false)
    private String brandName;

    @TableField(exist = false)
    @ApiModelProperty(value = "出入库人")
    private String storehouseOperator;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "产品季")
    private Long productSeasonSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售组织")
    private String saleOrg;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售员")
    private String salePerson;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-客户")
    private Long[] customerSidList;

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
    @ApiModelProperty(value = "行号")
    private Long itemNum;

}
