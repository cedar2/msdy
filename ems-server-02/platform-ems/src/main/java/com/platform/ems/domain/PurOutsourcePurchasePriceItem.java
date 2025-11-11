package com.platform.ems.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.core.domain.BaseEntity;
import com.platform.common.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableId;

import java.util.List;

import com.platform.common.core.domain.EmsBaseEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import com.platform.common.core.domain.document.UserOperLog;
import lombok.Data;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;

import lombok.experimental.Accessors;

/**
 * 加工采购价明细对象 s_pur_outsource_purchase_price_item
 *
 * @author linhongwei
 * @date 2021-05-12
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_pur_outsource_purchase_price_item")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PurOutsourcePurchasePriceItem extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-加工采购价明细
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-加工采购价明细")
    private Long outsourcePurchasePriceItemSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] outsourcePurchasePriceItemSidList;
    /**
     * 系统SID-加工采购价
     */
    @Excel(name = "系统SID-加工采购价")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-加工采购价")
    private Long outsourcePurchasePriceSid;

    /**
     * 有效期（起）
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "有效期（起）", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "有效期（起）")
    @NotEmpty(message = "有效期（起）不能为空")
    private Date startDate;

    /**
     * 有效期（止）
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "有效期（止）", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "有效期（止）")
    @NotEmpty(message = "有效期（止）不能为空")
    private Date endDate;

    /**
     * 价格录入方式（数据字典的键值）
     */
    @Excel(name = "价格录入方式（数据字典的键值）")
    @ApiModelProperty(value = "价格录入方式（数据字典的键值）")
    @NotEmpty(message = "价格录入方式不能为空")
    private String priceEnterMode;

    /**
     * 阶梯类型（数据字典的键值）
     */
    @Excel(name = "阶梯类型（数据字典的键值）")
    @ApiModelProperty(value = "阶梯类型（数据字典的键值）")
    private String cascadeType;

    /**
     * 采购价(含税)
     */
    @Excel(name = "采购价(含税)")
    @ApiModelProperty(value = "采购价(含税)")
    @Digits(integer=7,fraction = 3,message = "采购价(含税)整数位上限为7位，小数位上限为3位")
    private BigDecimal purchasePriceTax;

    /**
     * 采购价(不含税)
     */
    @Excel(name = "采购价(不含税)")
    @ApiModelProperty(value = "采购价(不含税)")
    @Digits(integer=7,fraction = 3,message = "采购价(不含税)整数位上限为7位，小数位上限为3位")
    private BigDecimal purchasePrice;

    @ApiModelProperty(value = "内部核算价(含税)")
    @Digits(integer = 10, fraction = 5, message = "内部核算价(含税)整数位上限为10位，小数位上限为5位")
    private BigDecimal innerCheckPriceTax;

    @ApiModelProperty(value = "内部核算价(不含税)")
    @Digits(integer = 9, fraction = 6, message = "内部核算价(含税)整数位上限为9位，小数位上限为6位")
    private BigDecimal innerCheckPrice;

    @ApiModelProperty(value = "报价(含税)")
    private BigDecimal quotePriceTax;

    @ApiModelProperty(value = "价格说明")
    private String priceRemark;

    /**
     * 税率（存值，即：不含百分号，如20%，就存0.2）
     */
    @Excel(name = "税率（存值，即：不含百分号，如20%，就存0.2）")
    @ApiModelProperty(value = "税率（存值，即：不含百分号，如20%，就存0.2）")
    @NotEmpty(message = "税率不能为空")
    private BigDecimal taxRate;

    /**
     * 币种（数据字典的键值）
     */
    @Excel(name = "币种（数据字典的键值）")
    @ApiModelProperty(value = "币种（数据字典的键值）")
    @NotEmpty(message = "币种不能为空")
    private String currency;

    /**
     * 货币单位（数据字典的键值）
     */
    @Excel(name = "货币单位（数据字典的键值）")
    @ApiModelProperty(value = "货币单位（数据字典的键值）")
    @NotEmpty(message = "货币单位不能为空")
    private String currencyUnit;

    /**
     * 基本计量单位（数据字典的键值）
     */
    @Excel(name = "基本计量单位（数据字典的键值）")
    @ApiModelProperty(value = "基本计量单位（数据字典的键值）")
    @NotEmpty(message = "基本计量单位不能为空")
    private String unitBase;

    /**
     * 采购价计量单位（数据字典的键值）
     */
    @Excel(name = "采购价计量单位（数据字典的键值）")
    @ApiModelProperty(value = "采购价计量单位（数据字典的键值）")
    private String unitPrice;

    @Excel(name = "基本计量单位")
    @ApiModelProperty(value = "基本计量单位")
    @TableField(exist = false)
    private String unitBaseName;

    @Excel(name = "采购价计量单位")
    @ApiModelProperty(value = "采购价计量单位")
    @TableField(exist = false)
    private String unitPriceName;

    @Excel(name = "税率")
    @ApiModelProperty(value = "税率")
    @TableField(exist = false)
    private String taxRateName;

    /**
     * 单位换算比例（采购价单位/基本单位）
     */
    @Excel(name = "单位换算比例（采购价单位/基本单位）")
    @ApiModelProperty(value = "单位换算比例（采购价单位/基本单位）")
    private BigDecimal unitConversionRate;

    /**
     * 合同号/协议号sid
     */
    @Excel(name = "合同号/协议号sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "合同号/协议号sid")
    private Long contractCode;

    /**
     * 创建人账号（用户名称）
     */
    @Excel(name = "创建人账号（用户名称）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    @TableField(exist = false)
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


    @ApiModelProperty(value = "确认人账号（用户名称）")
    @TableField(exist = false)
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

    @ApiModelProperty(value = "加工项（加工项的sid）")
    @TableField(exist = false)
    private String processName;

    @TableField(exist = false)
    @ApiModelProperty(value = "加工项（加工项的sid）")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long processSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "加工项（加工项的sid）")
    private Long[] processSidList;

    /** 处理状态 */
    @Excel(name = "处理状态")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @Excel(name = "行号")
    @ApiModelProperty(value = "行号")
    private int  itemNum;

    @TableField(exist = false)
    private List<Long>  itemSidList;

    @Excel(name = "部位说明")
    @ApiModelProperty(value = "部位说明")
    private String  positionDesc;

    @Excel(name = "工艺说明")
    @ApiModelProperty(value = "工艺说明")
    private String  processDesc;

    @Excel(name = "工艺图片")
    @ApiModelProperty(value = "工艺图片")
    private String  processPicture;

    @TableField(exist = false)
    private String importHandle;

    @TableField(exist = false)
    private String submitHandle;

    @TableField(exist = false)
    @ApiModelProperty(value = "价格维度")
    private String priceDimension;

    @TableField(exist = false)
    @ApiModelProperty(value = "加工采购价信息单号")
    private Long outsourcePurchasePriceCode;

    /**
     * 系统SID-供应商档案sid
     */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商档案sid")
    private Long vendorSid;

    /**
     * 系统SID-物料商品sid
     */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料商品sid")
    private Long materialSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料商品sid（多选）")
    private Long[] materialSidList;

    /**
     * 系统SID-SKU1档案sid
     */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-SKU1档案sid")
    private Long sku1Sid;

    @TableField(exist = false)
    @ApiModelProperty(value = "区间内的日期")
    private String nowDate;

}
