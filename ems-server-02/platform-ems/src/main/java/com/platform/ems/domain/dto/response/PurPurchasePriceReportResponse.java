package com.platform.ems.domain.dto.response;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class PurPurchasePriceReportResponse {

    @ApiModelProperty(value = "系统ID-物料采购价明细信息")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long purchasePriceItemSid;

    @TableField (exist = false)
    private List<Long> purchasePriceItemSidList;

    @ApiModelProperty(value = "是否走审批流程")
    @TableField (exist = false)
    private String isApproval;

    @ApiModelProperty(value = "采购类型编码（默认）")
    @TableField (exist = false)
    private String purchaseType;

    /** 系统ID-物料采购价信息 */
    @ApiModelProperty(value = "系统ID-物料采购价信息")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long purchasePriceSid;

    @Excel(name = "物料/商品 编码")
    @ApiModelProperty(value = "查询：物料编码")
    private String materialCode;

    @ApiModelProperty(value = "物料采购价信息编码")
    @Excel(name = "物料采购价信息编码")
    private String purchasePriceCode;

    @Length(max = 30, message = "供方编码长度不能超过30个字符")
    @ApiModelProperty(value = "供方编码（物料/商品/服务）")
    private String supplierProductCode;

    @ApiModelProperty(value = "物料档案规格尺寸")
    private String specificationSize;

    @ApiModelProperty(value = "物料类型名称（物料/商品/服务）")
    @TableField(exist = false)
    private String materialTypeName;

    @ApiModelProperty(value = "查询：物料类型")
    @TableField(exist = false)
    private String[] materialTypeList;

    @Excel(name = "物料/商品 名称")
    @ApiModelProperty(value = "查询：物料名称")
    private String materialName;

    @Excel(name = "SKU1名称")
    @ApiModelProperty(value = "SKU1类型")
    private String sku1Name;

    /**
     * 图片路径
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "物料商品主图片路径")
    private String picturePath;

    /** 价格维度 */
    @ApiModelProperty(value = "价格维度")
    @Excel(name = "价格维度",dictType = "s_price_dimension")
    private String priceDimension;

    @ApiModelProperty(value = "供料方式")
    @Excel(name = "甲供料方式",dictType = "s_raw_material_mode")
    private String rawMaterialMode;

    @Excel(name = "采购模式",dictType = "s_price_type")
    @ApiModelProperty(value = "采购模式")
    private String purchaseMode;

    @Excel(name = "供应商")
    @ApiModelProperty(value = "查询：供应商名称")
    private String vendorName;

    @ApiModelProperty(value = "供应商简称")
    private String vendorShortName;

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
    @Excel(name = "有效期（至）", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "有效期（至）")
    @NotEmpty(message = "有效期（至）不能为空")
    private Date endDate;

    @Length(max = 15, message = "采购价(含税)长度不能超过15位字符")
    @ApiModelProperty(value = "采购价(含税)")
    private BigDecimal purchasePriceTax;

    @Excel(name = "采购价(含税)")
    private String purchasePriceTaxS;

    @Excel(name = "采购价(不含税)")
    private String purchasePriceS;

    @Length(max = 15, message = "采购价(不含税)长度不能超过15位字符")
    @ApiModelProperty(value = "采购价(不含税)")
    private BigDecimal purchasePrice;

    @ApiModelProperty(value = "报价(含税)")
    private BigDecimal quotePriceTax;

    @ApiModelProperty(value = "核价(含税)")
    private BigDecimal checkPriceTax;

    @ApiModelProperty(value = "价格说明")
    private String priceRemark;

    @ApiModelProperty(value = "税率名称")
    @Excel(name = "税率")
    private BigDecimal taxRateName;

    @Excel(name = "基本计量单位")
    @ApiModelProperty(value = "基本计量单位名称")
    private String unitBaseName;

    @ApiModelProperty(value = "采购价单位名称")
    @Excel(name = "采购价格单位")
    private String unitPriceName;

    @ApiModelProperty(value = "单位换算比例")
    private BigDecimal unitConversionRate;

    @ApiModelProperty(value = "单位换算比例")
    @Excel(name = "单位换算比例")
    private String unitConversionRateS;

    @Excel(name = "币种",dictType ="s_currency")
    @NotBlank(message = "币种不能为空")
    @ApiModelProperty(value = "币种")
    private String currency;

    @Excel(name = "货币单位",dictType ="s_currency_unit")
    @ApiModelProperty(value = "货币单位")
    private String currencyUnit;

    @ApiModelProperty(value = "公司名称")
    @Excel(name = "公司")
    private String companyName;

    /** 递增减计量单位 */
    @Excel(name = "递增减计量单位")
    @ApiModelProperty(value = "递增减计量单位")
    private String unitRecursionName;;

    @ApiModelProperty(value = "基准量")
    private BigDecimal referQuantity;

    @Excel(name = "基准量")
    @ApiModelProperty(value = "基准量")
    private String referQuantityS;

    /** 价格最小起算量 */
    @ApiModelProperty(value = "价格最小起算量")
    private BigDecimal priceMinQuantity;

    @Excel(name = "价格最小起算量")
    @ApiModelProperty(value = "价格最小起算量")
    private String priceMinQuantityS;

    @ApiModelProperty(value = "递增量")
    private BigDecimal increQuantity;

    @Excel(name = "递增量")
    @ApiModelProperty(value = "递增量")
    private String increQuantityS;

    /** 递增采购价(含税) */
    @Length(max = 10, message = "递增采购价(含税)长度不能超过10位字符")
    @ApiModelProperty(value = "递增采购价(含税)")
    private BigDecimal increPurPriceTax;

    @Excel(name = "递增采购价(含税)")
    @Length(max = 10, message = "递增采购价(含税)长度不能超过10位字符")
    @ApiModelProperty(value = "递增采购价(含税)")
    private String increPurPriceTaxS;

    /** 递增采购价(不含税) */
    @ApiModelProperty(value = "递增采购价(不含税)")
    @Length(max = 10, message = "递增采购价(不含税)长度不能超过10位字符")
    private BigDecimal increPurPrice;

    @Excel(name = "递增采购价(不含税)")
    @ApiModelProperty(value = "递增采购价(不含税)")
    @Length(max = 10, message = "递增采购价(不含税)长度不能超过10位字符")
    private String increPurPriceS;

    /** 递减量 */
    @ApiModelProperty(value = "递减量")
    private BigDecimal decreQuantity;

    @Excel(name = "递减量")
    @ApiModelProperty(value = "递减量")
    private String decreQuantityS;

    /** 递减采购价(含税) */
    @ApiModelProperty(value = "递减采购价(含税)")
    @Length(max = 10, message = "递减采购价(含税)长度不能超过10位字符")
    private BigDecimal decPurPriceTax;

    @Excel(name = "递减采购价(含税)")
    @ApiModelProperty(value = "递减采购价(含税)")
    @Length(max = 10, message = "递减采购价(含税)长度不能超过10位字符")
    private String decPurPriceTaxS;

    /** 递减采购价(不含税) */
    @ApiModelProperty(value = "递减采购价(不含税)")
    @Length(max = 10, message = "递减采购价(不含税)长度不能超过10位字符")
    private BigDecimal decPurPrice;

    @Excel(name = "递减采购价(不含税)")
    @ApiModelProperty(value = "递减采购价(不含税)")
    @Length(max = 10, message = "递减采购价(不含税)长度不能超过10位字符")
    private String decPurPriceS;

    /** 取整方式(递增减) */
    @Excel(name = "取整方式(递增减)",dictType = "s_rounding_type")
    @ApiModelProperty(value = "取整方式(递增减)")
    private String roundingType ;

    /** 价格录入方式 */
    @Excel(name = "价格录入方式",dictType = "s_price_enter_mode")
    @ApiModelProperty(value = "价格录入方式")
    private String priceEnterMode;

    /** 阶梯类型 */
    @ApiModelProperty(value = "阶梯类型")
    @Excel(name = "阶梯类型",dictType = "s_price_cascade_type")
    private String cascadeType;

    @ApiModelProperty(value = "是否递增减价")
    @Excel(name = "是否递增减价",dictType = "sys_yes_no")
    private String isRecursionPrice;

    @ApiModelProperty(value = "递增减SKU类型")
    private String skuTypeRecursion;

    @ApiModelProperty(value = "备注")
    @Excel(name = "备注")
    private String remark;

    @ApiModelProperty(value = "变更说明")
    @Excel(name = "变更说明")
    private String updateRemark;

    /** 处理状态 */
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    /** 处理状态 */
    @Excel(name = "主表处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String headHandleStatus;

    @ApiModelProperty(value = "创建人")
    @Excel(name = "创建人")
    private String creatorAccountName;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    private Date createDate;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date updateDate;


    @ApiModelProperty(value = "创建人账号")
    @TableField(fill = FieldFill.INSERT)
    private String creatorAccount;

    /***************************查询参数***********************************/
    @ApiModelProperty(value = "创建日期起")
    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    private String createDateStart;

    @ApiModelProperty(value = "合同号/协议号sid")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long contractSid;

    /** 创建日期至 */
    @ApiModelProperty(value = "创建日期至")
    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    private String createDateEnd;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：供应商")
    private Long[] vendorSids;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：物料")
    private Long[] materialSids;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：公司")
    private Long[] companySids;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：价格类型")
    private String[] priceTypes;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：处理状态")
    private String[] handleStatuses;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：采购组织")
    private String[] purchaseOrgs;

    @ApiModelProperty(value = "创建人账号")
    @TableField(exist = false)
    private String[] creatorAccountList;

    @ApiModelProperty(value = "查询：采购模式")
    private String[] purchaseModes;

    @ApiModelProperty(value = "查询：供料方式")
    @TableField(exist = false)
    private String[] rawMaterialModes;

    @TableField(exist = false)
    private Integer pageNum;

    @TableField(exist = false)
    private Integer pageSize;

    /** 当前审批节点名称 */
    @ApiModelProperty(value = "当前审批节点名称")
    @TableField(exist = false)
    private String approvalNode;

    /** 当前审批人ID */
    @ApiModelProperty(value = "当前审批人ID")
    @TableField(exist = false)
    private String approvalUserId;

    /** 当前审批人 */
    @ApiModelProperty(value = "当前审批人")
    @TableField(exist = false)
    private String approvalUserName;

    /** 提交人 */
    @ApiModelProperty(value = "提交人")
    @TableField(exist = false)
    private String submitUserName;

    /**
     * 提交日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "提交日期")
    @TableField(exist = false)
    private Date submitDate;


    @ApiModelProperty(value = "查询:产品季")
    private String productSeasonName;

    @TableField(exist = false)
    private String updaterAccountName;

    @TableField(exist = false)
    private String confirmerAccountName;

    @ApiModelProperty(value = "是否是最后一个节点")
    @TableField(exist = false)
    private String isFinallyNode;

    @ApiModelProperty(value = "行号")
    private Integer  itemNum;
}
