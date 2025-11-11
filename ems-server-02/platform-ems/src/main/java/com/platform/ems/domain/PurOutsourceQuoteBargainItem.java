package com.platform.ems.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.NotNull;

import lombok.experimental.Accessors;

/**
 * 加工询报议价单明细(询价/报价/核价/议价)对象 s_pur_outsource_request_quotation_item
 *
 * @author linhongwei
 * @date 2021-05-10
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = " s_pur_outsource_quote_bargain_item")
public class PurOutsourceQuoteBargainItem extends EmsBaseEntity implements Serializable {

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-加工询报议价单明细信息
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-加工询报议价单明细信息")
    private Long outsourceQuoteBargainItemSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-加工询报议价单明细信息(多选)")
    private Long[] outsourceQuoteBargainItemSidList;

    @JsonSerialize(using = ToStringSerializer.class)
    @NotNull(message = "加工项不能为空")
    @ApiModelProperty(value = "加工项（加工项的sid）")
    private Long processSid;

    @ApiModelProperty(value = "加工项（加工项的sid）")
    @TableField(exist = false)
    private String processName;

    /**
     * 系统SID-加工询报议价单号
     */
    @Excel(name = "系统SID-加工询报议价单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-加工询报议价单号")
    private Long outsourceQuoteBargainSid;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "加工报议价单号")
    private Long outsourceQuoteBargainCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-加工询价单")
    private Long outsourceInquirySid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-加工询价单明细")
    private Long outsourceInquiryItemSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "加工询价单编码")
    private Long outsourceInquiryCode;

    /**
     * 系统SID-物料档案（物料/商品/服务）
     */
    @Excel(name = "系统SID-物料档案（物料/商品/服务）")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料档案（物料/商品/服务）")
    private Long materialSid;

    /**
     * 系统SID-SKU1档案sid
     */
    @Excel(name = "系统SID-SKU1档案sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-SKU1档案sid")
    private Long sku1Sid;

    /**
     * 系统SID-SKU2档案sid
     */
    @Excel(name = "系统SID-SKU2档案sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-SKU2档案sid")
    private Long sku2Sid;

    /**
     * 系统SID-商品条码（物料&商品&服务）
     */
    @Excel(name = "系统SID-商品条码（物料&商品&服务）")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品条码（物料&商品&服务）")
    private Long barcodeSid;

    /** 系统SID-工价费用项sid（工价项） */
    @Excel(name = "系统SID-工价费用项sid（工价项）")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-工价费用项sid（工价项）")
    private Long laborTypeItemSid;

    /** 客方结算价(含税) */
    @Excel(name = "客方确认价(含税)")
    @ApiModelProperty(value = "客方确认价(含税)")
    private BigDecimal customerPriceTax;

    /** 客方结算价(不含税) */
    @Excel(name = "客方确认价(不含税)")
    @ApiModelProperty(value = "客方确认价(不含税)")
    private BigDecimal customerPrice;

    /**
     * 价格维度（数据字典的键值）
     */
    @Excel(name = "价格维度（数据字典的键值）")
    @ApiModelProperty(value = "价格维度（数据字典的键值）")
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
     * 币种（数据字典的键值）
     */
    @Excel(name = "币种（数据字典的键值）")
    @ApiModelProperty(value = "币种（数据字典的键值）")
    private String currency;

    /**
     * 货币单位（数据字典的键值）
     */
    @Excel(name = "货币单位（数据字典的键值）")
    @ApiModelProperty(value = "货币单位（数据字典的键值）")
    private String currencyUnit;

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

    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

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

    @ApiModelProperty(value = "物料名称")
    @TableField(exist = false)
    private String materialName;

    @ApiModelProperty(value = "物料编码")
    @TableField(exist = false)
    private String materialCode;

    @ApiModelProperty(value = "我司样衣号")
    @TableField(exist = false)
    private String sampleCodeSelf;

    @ApiModelProperty(value = "sku1编码")
    @TableField(exist = false)
    private String sku1Code;

    @ApiModelProperty(value = "sku1名称")
    @TableField(exist = false)
    private String sku1Name;

    @ApiModelProperty(value = "sku2编码")
    @TableField(exist = false)
    private String sku2Code;

    @ApiModelProperty(value = "sku2名称")
    @TableField(exist = false)
    private String sku2Name;

    @ApiModelProperty(value = "商品条码")
    @TableField(exist = false)
    private String barcode;

    @ApiModelProperty(value = "")
    @TableField(exist = false)
    private String laborTypItemName;

    @ApiModelProperty(value = "")
    @TableField(exist = false)
    private String laborTypeItemCode;

    @Excel(name = "基本计量单位")
    @ApiModelProperty(value = "基本计量单位")
    @TableField(exist = false)
    private String  unitBaseName;

    @Excel(name = "采购计量单位")
    @ApiModelProperty(value = "采购计量单位")
    @TableField(exist = false)
    private String  unitPriceName;

    @Excel(name = "行号")
    @ApiModelProperty(value = "行号")
    private int  itemNum;

    @TableField(exist = false)
    private String updaterAccountName;

    @TableField(exist = false)
    private String confirmerAccountName;

    @TableField(exist = false)
    private String creatorAccountName;

    @Excel(name = "工艺说明")
    @ApiModelProperty(value = "工艺说明")
    private String  processDesc;

    @Excel(name = "工艺图片")
    @ApiModelProperty(value = "工艺图片")
    private String  processPicture;

    @Excel(name = "当前所属阶段", dictType = "baoheyi_stage")
    @ApiModelProperty(value = "当前所属阶段（数据字典的键值或配置档案的编码）")
    private String currentStage;

    @TableField(exist = false)
    @ApiModelProperty(value = "建单所属阶段（数据字典的键值或配置档案的编码）")
    private String createdStage;

    @TableField(exist = false)
    @ApiModelProperty(value = "核价员（用户账号）")
    private String checker;

    @TableField(exist = false)
    @ApiModelProperty(value = "核价员（用户账号）")
    private String[] checkerList;

    @TableField(exist = false)
    @ApiModelProperty(value = "核价员（用户账号）")
    private String checkerName;

    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "有效期（起）")
    private Date startDate;

    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "有效期（止）")
    private Date endDate;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商档案sid")
    private Long vendorSid;

    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "核价日期")
    private Date dateCheck;

    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "议价日期")
    private Date dateConfirm;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态")
    private String[] handleStatusList;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询页面的所属阶段")
    private String stage;
}
