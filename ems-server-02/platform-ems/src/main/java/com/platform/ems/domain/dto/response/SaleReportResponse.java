package com.platform.ems.domain.dto.response;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class SaleReportResponse {

    /** 销售价信息编号 */
    @ApiModelProperty(value = "销售价信息编号")
    private String salePriceCode;

    @TableId
    @ApiModelProperty(value = "系统自增长ID-销售价明细")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long salePriceItemSid;

    @ApiModelProperty(value = "系统自增长ID-销售价")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long salePriceSid;

    @Excel(name = "物料/商品编码")
    @ApiModelProperty(value = "查询：物料编码")
    private String materialCode;

    @Excel(name = "物料/商品名称")
    @ApiModelProperty(value = "查询：物料名称")
    private String materialName;

    @Excel(name = "SKU1名称")
    @ApiModelProperty(value = "SKU1名称")
    private String sku1Name;

    @ApiModelProperty(value = "物料/商品类别")
    private String materialCategory;

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

    @ApiModelProperty(value = "客供料方式")
    @Excel(name = "客供料方式",dictType = "s_raw_material_mode")
    private String rawMaterialMode;

    @Excel(name = "销售模式",dictType = "s_price_type")
    @ApiModelProperty(value = "销售模式")
    private String saleMode;

    @ApiModelProperty(value = "销售价:客户名称")
    @Excel(name = "客户(销售价)")
    private String customerName;

    @ApiModelProperty(value = "档案:客户名称")
    private String customerNameMaterial;

    @ApiModelProperty(value = "客户简称")
    private String customerShortName;

    /**
     * 有效期（起）
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "有效期（起）", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "有效期（起）")
    @NotEmpty(message = "有效期（起）不能为空")
    private Date startDate;

    @ApiModelProperty(value = "查询:产品季")
    private String[] productSeasonSidList;

    @ApiModelProperty(value = "查询:产品季")
    private String productSeasonName;

    /**
     * 有效期（止）
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "有效期（至）", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "有效期（至）")
    @NotEmpty(message = "有效期（至）不能为空")
    private Date endDate;

    @Digits(integer=7,fraction = 3,message = "销售价(含税)整数位上限为7位，小数位上限为3位")
    @ApiModelProperty(value = "销售价(含税)")
    private BigDecimal salePriceTax;

    @Excel(name = "销售价(含税)")
    @Digits(integer=7,fraction = 3,message = "销售价(含税)整数位上限为7位，小数位上限为3位")
    @ApiModelProperty(value = "销售价(含税)")
    private String salePriceTaxS;

    /** 销售价(不含税) */
    @Digits(integer=7,fraction = 3,message = "销售价(不含税)整数位上限为7位，小数位上限为3位")
    @ApiModelProperty(value = "销售价(不含税)")
    private BigDecimal salePrice;

    @Excel(name = "销售价(不含税)")
    @Digits(integer=7,fraction = 3,message = "销售价(不含税)整数位上限为7位，小数位上限为3位")
    @ApiModelProperty(value = "销售价(不含税)")
    private String salePriceS;

    @Excel(name = "报价(含税)")
    @ApiModelProperty(value = "报价(含税)")
    private BigDecimal quotePriceTax;

    @Excel(name = "价格说明")
    @ApiModelProperty(value = "价格说明")
    private String priceRemark;

    @ApiModelProperty(value = "合同号编码")
    private String saleContractCode;

    private String saleContractName;

    @ApiModelProperty(value = "税率名称")
    @Excel(name = "税率")
    private BigDecimal taxRateName;

    @Excel(name = "基本计量单位")
    @ApiModelProperty(value = "基本计量单位名称")
    private String unitBaseName;

    @ApiModelProperty(value = "销售价单位名称")
    @Excel(name = "销售价格单位")
    private String unitPriceName;

    @ApiModelProperty(value = "单位换算比例")
    private BigDecimal unitConversionRate;

    @ApiModelProperty(value = "单位换算比例")
    @Excel(name = "单位换算比例")
    private String unitConversionRateS;

    /** 折扣类型编码 */
    @Excel(name = "折扣")
    @ApiModelProperty(value = "折扣类型编码")
    private String discountType;

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

    @ApiModelProperty(value = "递增销售价(含税)")
    private BigDecimal increPurPriceTax;

    /** 递增采购价(含税) */
    @Excel(name = "递增销售价((含税)")
    @ApiModelProperty(value = "递增销售价(含税)")
    private String increPurPriceTaxS;

    @ApiModelProperty(value = "递增销售价(((不含税)")
    private BigDecimal increPurPrice;

    @ApiModelProperty(value = "递增销售价(((不含税)")
    private BigDecimal increPrice;

    /** 递增采购价(不含税) */
    @Excel(name = "递增销售价(不含税)")
    @ApiModelProperty(value = "递增销售价(((不含税)")
    private String increPurPriceS;

    @ApiModelProperty(value = "递增销售价(((不含税)")
    private String increPriceS;

    @ApiModelProperty(value = "递减量")
    private BigDecimal decreQuantity;

    @Excel(name = "递减量")
    @ApiModelProperty(value = "递减量")
    private String decreQuantityS;

    @ApiModelProperty(value = "递减销售价((含税)")
    private BigDecimal decPurPriceTax;

    @Excel(name = "递减销售价(含税)")
    @ApiModelProperty(value = "递减销售价((含税)")
    private String decPurPriceTaxS;

    @ApiModelProperty(value = "递减销售价((不含税)")
    private BigDecimal decPurPrice;

    @ApiModelProperty(value = "递减销售价((不含税)")
    private BigDecimal decrePrice;

    @Excel(name = "递减销售价(不含税)")
    @ApiModelProperty(value = "递减销售价((不含税)")
    private String decPurPriceS;

    @ApiModelProperty(value = "递减销售价((不含税)")
    private String decrePriceS;

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
    //@Excel(name = "阶梯类型",dictType = "s_price_cascade_type")
    private String cascadeType;

    @ApiModelProperty(value = "是否递增减价")
    @Excel(name = "是否递增减价",dictType = "sys_yes_no")
    private String isRecursionPrice;

    @ApiModelProperty(value = "备注")
    @Excel(name = "备注")
    private String remark;

    @ApiModelProperty(value = "递增价(含税)")
    private BigDecimal increPriceTax;


    @ApiModelProperty(value = "递减价(含税)")
    private BigDecimal decrePriceTax;

    @ApiModelProperty(value = "递增减SKU类型")
    private String skuTypeRecursion;

    /** 处理状态 */
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;


    @ApiModelProperty(value = "主表处理状态")
    private String headHandleStatus;

    @ApiModelProperty(value = "创建者")
    @Excel(name = "创建人")
    private String creatorAccountName;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    private Date createDate;

    @ApiModelProperty(value = "创建人账号")
    @TableField(fill = FieldFill.INSERT)
    private String creatorAccount;

    /**************************查询参数***************************/
    @ApiModelProperty(value = "创建日期起")
    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    private String createDateStart;

    /** 创建日期至 */
    @ApiModelProperty(value = "创建日期至")
    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    private String createDateEnd;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询:客户编码")
    private String customerCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售价:客户")
    private Long[] customerSids;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "客户sid")
    private Long customerSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "sku1Sid")
    private Long sku1Sid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "物料商品的sid")
    private Long materialSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "档案:客户")
    private Long[] customerSidsMaterial;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：物料")
    private Long[] materialSids;

    @ApiModelProperty(value = "查询：公司档案")
    @TableField(exist = false)
    private Long[] companySids;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：销售组织")
    private String[] saleOrgs;

    @ApiModelProperty(value = "合同号sid")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long saleContractSid;

    /** 价格类型 */
    @ApiModelProperty(value = "查询：价格类型")
    @TableField(exist = false)
    private String[] priceTypes;

    @ApiModelProperty(value = "查询：处理状态")
    @TableField(exist = false)
    private String[] handleStatuses;

    @ApiModelProperty(value = "查询：销售渠道")
    @TableField(exist = false)
    private String[] saleChannels;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：销售模式")
    private String[] saleModes;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询:供料方式")
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

    @ApiModelProperty(value = "明细行sid")
    @TableField(exist = false)
    List<Long> itemSidList;

    /**
     * 提交日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "提交日期")
    @TableField(exist = false)
    private Date submitDate;

    @TableField(exist = false)
    private String updaterAccountName;

    @TableField(exist = false)
    private String confirmerAccountName;

    @Excel(name = "行号")
    @ApiModelProperty(value = "行号")
    private int  itemNum;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT)
    private Date updateDate;

    @ApiModelProperty(value = "物料类型名称（物料/商品/服务）")
    @TableField(exist = false)
    private String materialTypeName;

    @ApiModelProperty(value = "变更说明")
    @Excel(name = "变更说明")
    private String updateRemark;

    @ApiModelProperty(value = "物料类型名称（物料/商品/服务）")
    @TableField(exist = false)
    private String[] materialTypeList;

    @ApiModelProperty(value = "是否是最后一个节点")
    @TableField(exist = false)
    private String isFinallyNode;
}
