package com.platform.ems.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.BaseEntity;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 采购价信息明细对象 s_pur_purchase_price_item
 *
 * @author ChenPinzhen
 * @date 2021-02-04
 */
@ApiModel
@TableName("s_pur_purchase_price_item")
@Data
@Accessors(chain = true)
public class PurPurchasePriceItem extends EmsBaseEntity {
    private static final long serialVersionUID = 1L;

    /** 客户端口号 */
    @ApiModelProperty(value = "客户端口号")
    @TableField(fill = FieldFill.INSERT)
    private String clientId;

    /** 系统ID-物料采购价明细信息 */
    @Excel(name = "系统ID-物料采购价明细信息")
    @ApiModelProperty(value = "系统ID-物料采购价明细信息")
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    private Long purchasePriceItemSid;

    /** 系统ID-物料采购价信息 */
    @ApiModelProperty(value = "系统ID-物料采购价信息")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long purchasePriceSid;

    /** 有效期（起） */
    @NotBlank(message = "有效期起不能为空")
    @ApiModelProperty(value = "有效期（起）")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date startDate;

    /** 有效期（止） */
    @ApiModelProperty(value = "有效期（止）")
    @NotBlank(message = "有效期至不能为空")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date endDate;

    /** 阶梯类型 */
    @ApiModelProperty(value = "阶梯类型")
    private String cascadeType;

    /** 价格录入方式 */
    @Excel(name = "价格录入方式")
    @NotBlank(message = "价格录入方式不能为空")
    @ApiModelProperty(value = "价格录入方式")
    private String priceEnterMode;

    @ApiModelProperty(value = "是否递增减价")
    @NotBlank(message = "是否递增减价不能为空")
    private String isRecursionPrice;

    /** 递增减计量单位 */
    @Excel(name = "递增减计量单位")
    @ApiModelProperty(value = "递增减计量单位")
    private String unitRecursion;

    @TableField(exist = false)
    @ApiModelProperty(value = "递增减计量单位")
    private String unitRecursionName;

    @ApiModelProperty(value = "单位换算比例")
    @Excel(name = "单位换算比例")
    private BigDecimal unitConversionRate;

    /** 基准量 */
    @Excel(name = "基准量")
    @ApiModelProperty(value = "基准量")
    private BigDecimal referQuantity;

    @Excel(name = "基本计量单位")
    @ApiModelProperty(value = "基本计量单位")
    private String unitBase;

    /** 递增量 */
    @Excel(name = "递增量")
    @ApiModelProperty(value = "递增量")
    private BigDecimal increQuantity;

    /** 递减量 */
    @Excel(name = "递减量")
    @ApiModelProperty(value = "递减量")
    private BigDecimal decreQuantity;

    /** 价格最小起算量 */
    @Excel(name = "价格最小起算量")
    @ApiModelProperty(value = "价格最小起算量")
    private BigDecimal priceMinQuantity;

    /** 取整方式(递增减) */
    @Excel(name = "取整方式(递增减)")
    @ApiModelProperty(value = "取整方式(递增减)")
    private String roundingType ;

    /** 递增采购价(含税) */
    @Excel(name = "递增采购价(含税)")
    @Length(max = 10, message = "递增采购价(含税)长度不能超过10位字符")
    @ApiModelProperty(value = "递增采购价(含税)")
    private BigDecimal increPurPriceTax;

    /** 递增采购价(不含税) */
    @Excel(name = "递增采购价(不含税)")
    @ApiModelProperty(value = "递增采购价(不含税)")
    @Length(max = 10, message = "递增采购价(不含税)长度不能超过10位字符")
    private BigDecimal increPurPrice;

    /** 递减采购价(含税) */
    @Excel(name = "递减采购价(含税)")
    @ApiModelProperty(value = "递减采购价(含税)")
    @Length(max = 10, message = "递减采购价(含税)长度不能超过10位字符")
    private BigDecimal decPurPriceTax;

    /** 递减采购价(不含税) */
    @Excel(name = "递减采购价(不含税)")
    @ApiModelProperty(value = "递减采购价(不含税)")
    @Length(max = 10, message = "递减采购价(不含税)长度不能超过10位字符")
    private BigDecimal decPurPrice;

    /** 采购价(含税) */
    @Excel(name = "采购价(含税)")
    @Length(max = 15, message = "采购价(含税)长度不能超过15位字符")
    @ApiModelProperty(value = "采购价(含税)")
    private BigDecimal purchasePriceTax;

    /** 采购价(不含税) */
    @Excel(name = "采购价(不含税)")
    @Length(max = 15, message = "采购价(不含税)长度不能超过15位字符")
    @ApiModelProperty(value = "采购价(不含税)")
    private BigDecimal purchasePrice;

    @ApiModelProperty(value = "报价(含税)")
    private BigDecimal quotePriceTax;

    @ApiModelProperty(value = "核价(含税)")
    private BigDecimal checkPriceTax;

    @ApiModelProperty(value = "价格说明")
    private String priceRemark;

    /** 税率 */
    @Excel(name = "税率")
    @ApiModelProperty(value = "税率")
    @NotBlank(message = "税率不能为空")
    private BigDecimal taxRate;

    @ApiModelProperty(value = "税率名称")
    @TableField(exist = false)
    private BigDecimal taxRateName;

    @ApiModelProperty(value = "合同号/协议号sid")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long contractSid;

    @ApiModelProperty(value = "合同号/协议号")
    @Excel(name = "合同号/协议号")
    @TableField(exist = false)
    private String purchaseContractCode;

    @TableField(exist = false)
    @Excel(name = "合同号/协议号 名称")
    private String purchaseContractName;

    @TableField(exist = false)
    @ApiModelProperty(value = "基本计量单位名称")
    private String unitBaseName;


    @ApiModelProperty(value = "采购价单位名称")
    @TableField(exist = false)
    private String unitPriceName;

    /** 币种 */
    @Excel(name = "币种")
    @NotBlank(message = "币种不能为空")
    @ApiModelProperty(value = "币种")
    private String currency;

    /** 货币单位 */
    @Excel(name = "货币单位")
    @ApiModelProperty(value = "货币单位")
    @NotBlank(message = "货币单位不能为空")
    private String currencyUnit;

    /** 采购价格单位 */
    @Excel(name = "采购价格单位")
    @ApiModelProperty(value = "采购价格单位")
    private String unitPrice;

    /** 创建人账号 */
    @Excel(name = "创建人账号")
    @ApiModelProperty(value = "创建人账号")
    @TableField(fill = FieldFill.INSERT)
    private String creatorAccount;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createDate;

    /** 更新人账号 */
    @Excel(name = "更新人账号")
    @ApiModelProperty(value = "更新人账号")
    @TableField(fill = FieldFill.UPDATE)
    private String updaterAccount;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.UPDATE)
    private Date updateDate;

    /** 确认人账号 */
    @Excel(name = "确认人账号")
    @ApiModelProperty(value = "确认人账号")
    private String confirmerAccount;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "变更说明")
    private String updateRemark;

    /** 确认时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "确认时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /** 数据源系统 */
    @Excel(name = "数据源系统")
    @ApiModelProperty(value = "数据源系统")
    @TableField(fill = FieldFill.INSERT)
    private String dataSourceSys;

    /** 递增减SKU类型 */
    @Excel(name = "递增减SKU类型")
    @ApiModelProperty(value = "递增减SKU类型")
    private String skuTypeRecursion;

    /** 处理状态 */
    @Excel(name = "处理状态")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @Excel(name = "行号")
    @ApiModelProperty(value = "行号")
    private int  itemNum;

    @TableField(exist = false)
    private String importHandle;

    @TableField(exist = false)
    private String submitHandle;

    @TableField(exist = false)
    @ApiModelProperty(value = "价格维度")
    private String priceDimension;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料采购价信息单号")
    private Long purchasePriceCode;

    @ApiModelProperty(value = "是否走审批流程")
    @TableField (exist = false)
    private String isApproval;

    @ApiModelProperty(value = "系统税率")
    @TableField(exist = false)
    private BigDecimal systemTaxRate;
}
