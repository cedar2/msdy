package com.platform.ems.domain;

import java.math.BigDecimal;
import java.util.Date;

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

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * 报议价单明细(报价/核价/议价)对象 s_pur_quote_bargain_item
 *
 * @author linhongwei
 * @date 2021-04-26
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = " s_pur_quote_bargain_item")
public class PurQuoteBargainItem extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-报议价单明细信息
     */
    @TableId
    @ApiModelProperty(value = "系统SID-报议价单明细信息")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long quoteBargainItemSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-报议价单明细信息多选")
    private Long[] quoteBargainItemSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-报议价单信息多选")
    private Long[] quoteBargainSidList;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long materialSkuSid;

    /**
     * 系统SID-报议价单号
     */
    @Excel(name = "系统SID-报议价单号")
    @ApiModelProperty(value = "系统SID-报议价单号")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long quoteBargainSid;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-采购价")
    private Long purchasePriceSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-物料成本核算")
    @TableField(exist = false)
    private Long productCostSid;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "报议价单号")
    private Long quoteBargainCode;

    /**
     * 系统SID-物料档案（物料/商品/服务）
     */
    @Excel(name = "系统SID-物料档案（物料/商品/服务）")
    @ApiModelProperty(value = "系统SID-物料档案（物料/商品/服务）")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long materialSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-物料档案")
    private Long[] materialSidList;

    @Excel(name = "物料/商品 编码")
    @ApiModelProperty(value = "物料/商品 编码")
    @TableField(exist = false)
    private String materialCode;

    @Excel(name = "物料/商品 名称")
    @ApiModelProperty(value = "物料/商品 名称")
    @TableField(exist = false)
    private String materialName;

    @Excel(name = "商品条码Sid")
    @ApiModelProperty(value = "商品条码Sid")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long barcodeSid;

    @Excel(name = "商品条码")
    @ApiModelProperty(value = "商品条码")
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long barcode;

    /**
     * 系统SID-SKU1档案sid
     */
    @Excel(name = "系统SID-SKU1档案sid")
    @ApiModelProperty(value = "系统SID-SKU1档案sid")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long sku1Sid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-SKU1档案sid")
    private Long[] sku1SidList;

    @Excel(name = "sku1名称")
    @ApiModelProperty(value = "sku1名称")
    @TableField(exist = false)
    private String sku1Name;

    /**
     * 系统SID-SKU2档案sid
     */
    @Excel(name = "系统SID-SKU2档案sid")
    @ApiModelProperty(value = "系统SID-SKU2档案sid")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long sku2Sid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-SKU2档案sid")
    private Long[] sku2SidList;

    @Excel(name = "sku2名称")
    @ApiModelProperty(value = "sku2名称")
    @TableField(exist = false)
    private String sku2Name;

    /**
     * 价格维度（数据字典的键值）
     */
    @Excel(name = "价格维度（数据字典的键值）")
    @ApiModelProperty(value = "价格维度（数据字典的键值）")
    @NotBlank(message = "价格维度不能为空")
    private String priceDimension;

    /**
     * 阶梯类型（数据字典的键值）
     */
    @Excel(name = "阶梯类型（数据字典的键值）")
    @ApiModelProperty(value = "阶梯类型（数据字典的键值）")
    private String cascadeType;

    /**
     * 价格录入方式（数据字典的键值）
     */
    @Excel(name = "价格录入方式（数据字典的键值）")
    @ApiModelProperty(value = "价格录入方式（数据字典的键值）")
    private String priceEnterMode;

    /**
     * 报价(含税)
     */
    @Excel(name = "报价(含税)")
    @ApiModelProperty(value = "报价(含税)")
    private BigDecimal quotePriceTax;

    /**
     * 报价(不含税)
     */
    @Excel(name = "报价(不含税)")
    @ApiModelProperty(value = "报价(不含税)")
    private BigDecimal quotePrice;

    /**
     * 核定价(含税)
     */
    @Excel(name = "核定价(含税)")
    @ApiModelProperty(value = "核定价(含税)")
    private BigDecimal checkPriceTax;

    /**
     * 核定价(不含税)
     */
    @Excel(name = "核定价(不含税)")
    @ApiModelProperty(value = "核定价(不含税)")
    private BigDecimal checkPrice;

    /**
     * 确认价(含税)
     */
    @Excel(name = "确认价(含税)")
    @ApiModelProperty(value = "确认价(含税)")
    private BigDecimal confirmPriceTax;

    /**
     * 确认价(不含税)
     */
    @Excel(name = "确认价(不含税)")
    @ApiModelProperty(value = "确认价(不含税)")
    private BigDecimal confirmPrice;

    /**
     * 采购价(含税)
     */
    @Excel(name = "采购价(含税)")
    @ApiModelProperty(value = "采购价(含税)")
    private BigDecimal purchasePriceTax;

    /**
     * 采购价(不含税)
     */
    @Excel(name = "采购价(不含税)")
    @ApiModelProperty(value = "采购价(不含税)")
    private BigDecimal purchasePrice;

    /**
     * 税率（存值，即：不含百分号，如20%，就存0.2）
     */
    @Excel(name = "税率（存值，即：不含百分号，如20%，就存0.2）")
    @ApiModelProperty(value = "税率（存值，即：不含百分号，如20%，就存0.2）")
    private BigDecimal taxRate;

    /**
     * 基准量
     */
    @Excel(name = "基准量")
    @ApiModelProperty(value = "基准量")
    private BigDecimal referQuantity;

    /**
     * 递增量
     */
    @Excel(name = "递增量")
    @ApiModelProperty(value = "递增量")
    private BigDecimal increQuantity;

    /**
     * 递减量
     */
    @Excel(name = "递减量")
    @ApiModelProperty(value = "递减量")
    private BigDecimal decreQuantity;

    /**
     * 价格最小起算量
     */
    @Excel(name = "价格最小起算量")
    @ApiModelProperty(value = "价格最小起算量")
    private BigDecimal priceMinQuantity;

    /**
     * 取整方式(递增减)（数据字典的键值）
     */
    @Excel(name = "取整方式(递增减)（数据字典的键值）")
    @ApiModelProperty(value = "取整方式(递增减)（数据字典的键值）")
    private String roundingType;

    /**
     * 递增减计量单位（数据字典的键值）
     */
    @Excel(name = "递增减计量单位（数据字典的键值）")
    @ApiModelProperty(value = "递增减计量单位（数据字典的键值）")
    private String unitRecursion;

    /**
     * 递增报价(含税)
     */
    @Excel(name = "递增报价(含税)")
    @ApiModelProperty(value = "递增报价(含税)")
    private BigDecimal increQuoPriceTax;

    /**
     * 递增报价(不含税)
     */
    @Excel(name = "递增报价(不含税)")
    @ApiModelProperty(value = "递增报价(不含税)")
    private BigDecimal increQuoPrice;

    /**
     * 递增核定价(含税)
     */
    @Excel(name = "递增核定价(含税)")
    @ApiModelProperty(value = "递增核定价(含税)")
    private BigDecimal increChePriceTax;

    /**
     * 递增核定价(不含税)
     */
    @Excel(name = "递增核定价(不含税)")
    @ApiModelProperty(value = "递增核定价(不含税)")
    private BigDecimal increChePrice;

    /**
     * 递增确认价(含税)
     */
    @Excel(name = "递增确认价(含税)")
    @ApiModelProperty(value = "递增确认价(含税)")
    private BigDecimal increConfPriceTax;

    /**
     * 递增确认价(不含税)
     */
    @Excel(name = "递增确认价(不含税)")
    @ApiModelProperty(value = "递增确认价(不含税)")
    private BigDecimal increConfPrice;

    /**
     * 递增采购价(含税)
     */
    @Excel(name = "递增采购价(含税)")
    @ApiModelProperty(value = "递增采购价(含税)")
    private BigDecimal increPurPriceTax;

    /**
     * 递增采购价(不含税)
     */
    @Excel(name = "递增采购价(不含税)")
    @ApiModelProperty(value = "递增采购价(不含税)")
    private BigDecimal increPurPrice;

    /**
     * 递减报价(含税)
     */
    @Excel(name = "递减报价(含税)")
    @ApiModelProperty(value = "递减报价(含税)")
    private BigDecimal decreQuoPriceTax;

    /**
     * 递减报价(不含税)
     */
    @Excel(name = "递减报价(不含税)")
    @ApiModelProperty(value = "递减报价(不含税)")
    private BigDecimal decreQuoPrice;

    /**
     * 递减核定价(含税)
     */
    @Excel(name = "递减核定价(含税)")
    @ApiModelProperty(value = "递减核定价(含税)")
    private BigDecimal decreChePriceTax;

    /**
     * 递减核定价(不含税)
     */
    @Excel(name = "递减核定价(不含税)")
    @ApiModelProperty(value = "递减核定价(不含税)")
    private BigDecimal decreChePrice;

    /**
     * 递减确认价(含税)
     */
    @Excel(name = "递减确认价(含税)")
    @ApiModelProperty(value = "递减确认价(含税)")
    private BigDecimal decreConfPriceTax;

    /**
     * 递减确认价(不含税)
     */
    @Excel(name = "递减确认价(不含税)")
    @ApiModelProperty(value = "递减确认价(不含税)")
    private BigDecimal decreConfPrice;

    /**
     * 递减采购价(含税)
     */
    @Excel(name = "递减采购价(含税)")
    @ApiModelProperty(value = "递减采购价(含税)")
    private BigDecimal decrePurPriceTax;

    @ApiModelProperty(value = "是否递增减价")
    private String isRecursionPrice;

    /**
     * 递减采购价(不含税)
     */
    @Excel(name = "递减采购价(不含税)")
    @ApiModelProperty(value = "递减采购价(不含税)")
    private BigDecimal decrePurPrice;

    /**
     * 基本计量单位（数据字典的键值）
     */
    @Excel(name = "基本计量单位（数据字典的键值）")
    @ApiModelProperty(value = "基本计量单位（数据字典的键值）")
    private String unitBase;

    /**
     * 采购价计量单位（数据字典的键值）
     */
    @Excel(name = "采购价计量单位（数据字典的键值）")
    @ApiModelProperty(value = "采购价计量单位（数据字典的键值）")
    private String unitPrice;

    @TableField(exist = false)
    @ApiModelProperty(value = "基本计量单位名称")
    private String unitBaseName;

    @ApiModelProperty(value = "采购计量单位名称")
    @TableField(exist = false)
    private String unitPriceName;

    @Excel(name = "税率")
    @ApiModelProperty(value = "税率")
    @TableField(exist = false)
    private BigDecimal taxRateName;

    /**
     * 单位换算比例（采购价单位/基本单位）
     */
    @Excel(name = "单位换算比例（采购价单位/基本单位）")
    @ApiModelProperty(value = "单位换算比例（采购价单位/基本单位）")
    private BigDecimal unitConversionRate;

    /**
     * 报价更新人账号（用户名称）
     */
    @Excel(name = "报价更新人账号（用户名称）")
    @ApiModelProperty(value = "报价更新人账号（用户名称）")
    private String quoteUpdaterAccount;

    /**
     * 报价更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "报价更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "报价更新时间")
    private Date quoteUpdateDate;

    /**
     * 核定价更新人账号（用户名称）
     */
    @Excel(name = "核定价更新人账号（用户名称）")
    @ApiModelProperty(value = "核定价更新人账号（用户名称）")
    private String checkUpdaterAccount;

    /**
     * 核定价更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "核定价更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "核定价更新时间")
    private Date checkUpdateDate;

    /**
     * 确认价更新人账号（用户名称）
     */
    @Excel(name = "确认价更新人账号（用户名称）")
    @ApiModelProperty(value = "确认价更新人账号（用户名称）")
    private String confirmUpdaterAccount;

    /**
     * 确认价更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "确认价更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认价更新时间")
    private Date confirmUpdateDate;

    /**
     * 采购价更新人账号（用户名称）
     */
    @Excel(name = "采购价更新人账号（用户名称）")
    @ApiModelProperty(value = "采购价更新人账号（用户名称）")
    private String purchaseUpdaterAccount;

    /**
     * 采购价更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "采购价更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "采购价更新时间")
    private Date purchaseUpdateDate;

    /**
     * 备注(询价)
     */
    @Excel(name = "备注(询价)")
    @ApiModelProperty(value = "备注(询价)")
    private String remarkRequest;

    /**
     * 备注(报价)
     */
    @Excel(name = "备注(报价)")
    @ApiModelProperty(value = "备注(报价)")
    private String remarkQuote;

    /**
     * 备注(核价)
     */
    @Excel(name = "备注(核价)")
    @ApiModelProperty(value = "备注(核价)")
    private String remarkCheck;

    /**
     * 备注(确认价)
     */
    @Excel(name = "备注(确认价)")
    @ApiModelProperty(value = "备注(确认价)")
    private String remarkConfirm;

    /**
     * 备注(采购价)
     */
    @Excel(name = "备注(采购价)")
    @ApiModelProperty(value = "备注(采购价)")
    private String remarkPurchase;

    /**
     * 创建人账号（用户名称）
     */
    @Excel(name = "创建人账号（用户名称）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    @TableField(exist = false)
    @ApiModelProperty(value = "创建人账号（用户名称）")
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

    @Excel(name = "递增减SKU类型")
    @ApiModelProperty(value = "递增减SKU类型")
    private String skuTypeRecursion;

    @Excel(name = "处理状态（数据字典的键值）", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值）")
    private String handleStatus;

    @Excel(name = "行号")
    @ApiModelProperty(value = "行号")
    private int itemNum;

    @Excel(name = "当前所属阶段", dictType = "baoheyi_stage")
    @ApiModelProperty(value = "当前所属阶段（数据字典的键值或配置档案的编码）")
    private String currentStage;

    @TableField(exist = false)
    @ApiModelProperty(value = "主表建单的阶段（数据字典的键值或配置档案的编码）")
    private String createdStage;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料采购价格记录")
    private Long inquirySid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "物料采购价格记录编码")
    private Long inquiryCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料采购价格记录明细")
    private Long inquiryItemSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "核价员")
    private String checker;

    @TableField(exist = false)
    @ApiModelProperty(value = "核价员")
    private String checkerName;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商档案sid")
    private Long vendorSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "甲供料方式")
    private String rawMaterialMode;

    @TableField(exist = false)
    @ApiModelProperty(value = "采购模式（数据字典的键值）")
    private String purchaseMode;

    @TableField(exist = false)
    @ApiModelProperty(value = "币种（数据字典的键值）")
    private String currency;

    @TableField(exist = false)
    @ApiModelProperty(value = "货币单位（数据字典的键值）")
    private String currencyUnit;

    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "有效期（起）")
    private Date startDate;

    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "有效期（止）")
    private Date endDate;

    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "核价日期")
    private Date dateCheck;

    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "议价日期")
    private Date dateConfirm;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态（多选）")
    private String[] handleStatusList;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询页面的所属阶段")
    private String stage;

}
