package com.platform.ems.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableId;

import com.platform.common.core.domain.EmsBaseEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.NotNull;

import lombok.experimental.Accessors;

/**
 * 加工询价单明细对象 s_pur_outsource_inquiry_item
 *
 * @author chenkw
 * @date 2022-01-11
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_pur_outsource_inquiry_item")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PurOutsourceInquiryItem extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-加工询价单明细信息
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-加工询价单明细信息")
    private Long outsourceInquiryItemSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] outsourceInquiryItemSidList;
    /**
     * 系统SID-加工询价单号
     */
    @Excel(name = "系统SID-加工询价单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-加工询价单号")
    private Long outsourceInquirySid;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "加工询价单号")
    private Long outsourceInquiryCode;

    /**
     * 系统SID-工序
     */
    @NotNull(message = "工序加工项不能为空")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-工序")
    private Long processSid;

    @Excel(name = "系统SID-工序")
    @TableField(exist = false)
    @ApiModelProperty(value = "工序")
    private String processName;

    /**
     * 系统SID-物料档案（物料/商品/服务）
     */
    @NotNull(message = "商品档案不能是空的")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料档案（物料/商品/服务）")
    private Long materialSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料档案编码")
    private String materialCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料档案名称")
    private String materialName;

    /**
     * 系统SID-SKU1档案
     */
    @Excel(name = "系统SID-SKU1档案")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-SKU1档案")
    private Long sku1Sid;

    /**
     * 系统SID-SKU2档案
     */
    @Excel(name = "系统SID-SKU2档案")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-SKU2档案")
    private Long sku2Sid;

    @TableField(exist = false)
    @ApiModelProperty(value = "SKU1档案编码")
    private String sku1Code;

    @TableField(exist = false)
    @ApiModelProperty(value = "SKU1档案名称")
    private String sku1Name;

    @TableField(exist = false)
    @ApiModelProperty(value = "SKU1档案类型")
    private String sku1Type;

    @TableField(exist = false)
    @ApiModelProperty(value = "SKU2档案编码")
    private String sku2Code;

    @TableField(exist = false)
    @ApiModelProperty(value = "SKU2档案名称")
    private String sku2Name;

    @TableField(exist = false)
    @ApiModelProperty(value = "SKU2档案类型")
    private String sku2Type;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "物料/商品明细的sid")
    private Long materialSkuSid;

    /**
     * 系统SID-商品条码（物料&商品&服务）
     */
    @Excel(name = "系统SID-商品条码（物料&商品&服务）")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品条码（物料&商品&服务）")
    private Long barcodeSid;

    @Excel(name = "商品条码")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "商品条码编码")
    private Long barcodeCode;

    @Excel(name = "价格维度", dictType = "s_price_dimension")
    @ApiModelProperty(value = "价格维度（数据字典的键值）")
    private String priceDimension;

    /**
     * 价格录入方式（数据字典的键值）
     */
    @Excel(name = "价格录入方式", dictType = "s_price_enter_mode")
    @ApiModelProperty(value = "价格录入方式（数据字典的键值）")
    private String priceEnterMode;

    /**
     * 基本计量单位（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "基本计量单位（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "基本计量单位（数据字典的键值或配置档案的编码）")
    private String unitBase;

    @TableField(exist = false)
    @Excel(name = "基本计量单位")
    @ApiModelProperty(value = "基本计量单位")
    private String unitBaseName;

    /**
     * 行号
     */
    @Excel(name = "行号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "行号")
    private Long itemNum;

    /**
     * 工艺说明
     */
    @Excel(name = "工艺说明")
    @ApiModelProperty(value = "工艺说明")
    private String processDesc;

    /**
     * 工艺图片
     */
    @Excel(name = "工艺图片")
    @ApiModelProperty(value = "工艺图片")
    private String processPicture;

    /**
     * 主表的处理状态（数据字典的键值或配置档案的编码）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    /**
     * 创建人账号（用户名称）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    @TableField(exist = false)
    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人账号（用户昵称）")
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
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

    @TableField(exist = false)
    @Excel(name = "更新人")
    @ApiModelProperty(value = "更新人账号（用户昵称）")
    private String updaterAccountName;

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
    @ApiModelProperty(value = "确认人账号（用户名称）")
    private String confirmerAccount;

    @TableField(exist = false)
    @Excel(name = "确认人")
    @ApiModelProperty(value = "确认人账号（用户昵称）")
    private String confirmerAccountName;

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


    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-工序 多选")
    private Long[] processSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-供应商多选下拉框")
    private Long[] vendorSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "采购组多选下拉框")
    private String[] purchaseGroupList;

    @TableField(exist = false)
    @ApiModelProperty(value = "采购组织多选下拉框")
    private String[] purchaseOrgList;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态多选下拉框")
    private String[] handleStatusList;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品类型多选下拉框")
    private String[] materialTypeList;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-产品季多选下拉框")
    private Long[] productSeasonSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-公司多选下拉框")
    private Long[] companySidList;

    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "报价计划截至日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "报价计划截至日期")
    private Date quotepriceDeadline;

    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "报价计划截至日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "报价计划截至日期-起")
    private Date quotepriceDeadlineBegin;

    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "报价计划截至日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "报价计划截至日期-止")
    private Date quotepriceDeadlineEnd;

    @TableField(exist = false)
    @Excel(name = "供应商")
    @ApiModelProperty(value = "供应商名称")
    private String vendorName;

    @TableField(exist = false)
    @Excel(name = "公司")
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @TableField(exist = false)
    @Excel(name = "产品季")
    @ApiModelProperty(value = "产品季名称")
    private String productSeasonName;

    @TableField(exist = false)
    @Excel(name = "采购员")
    @ApiModelProperty(value = "采购员（用户昵称）")
    private String buyerName;

    @TableField(exist = false)
    @Excel(name = "采购员电话")
    @ApiModelProperty(value = "采购员电话")
    private String buyerTelephone;

    @TableField(exist = false)
    @Excel(name = "采购员邮箱")
    @ApiModelProperty(value = "采购员邮箱")
    private String buyerEmail;

    @TableField(exist = false)
    @Excel(name = "采购组")
    @ApiModelProperty(value = "采购组（数据字典的键值或配置档案的编码）")
    private String purchaseGroupName;

    @TableField(exist = false)
    @Excel(name = "采购组织")
    @ApiModelProperty(value = "采购组织（数据字典的键值或配置档案的编码）")
    private String purchaseOrgName;

    @TableField(exist = false)
    @Excel(name = "币种", dictType = "s_currency")
    @ApiModelProperty(value = "币种（数据字典的键值或配置档案的编码）")
    private String currency;

    @TableField(exist = false)
    @Excel(name = "货币单位", dictType = "s_currency_unit")
    @ApiModelProperty(value = "货币单位（数据字典的键值或配置档案的编码）")
    private String currencyUnit;

    @TableField(exist = false)
    @Excel(name = "询价单备注")
    @ApiModelProperty(value = "询价单备注")
    private String inquiryRemark;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "报价单号")
    private Long quoteBargainCode;

    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "报价日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "报价日期")
    private Date dateQuote;

    @TableField(exist = false)
    @Excel(name = "是否报价", dictType = "s_yesno_flag")
    @ApiModelProperty(value = "是否报价（数据字典的键值）")
    private String isQuoteProcess;
}
