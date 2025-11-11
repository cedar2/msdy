package com.platform.ems.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 销售价信息-明细对象 s_sal_sale_price_item
 *
 * @author linhongwei
 * @date 2021-03-05
 */
@Data
@TableName("s_sal_sale_price_item")
@ApiModel
@Accessors(chain = true)
public class SalSalePriceItem  extends EmsBaseEntity {
    private static final long serialVersionUID = 1L;

    /** 客户端口号 */
    @Excel(name = "客户端口号")
    @ApiModelProperty(value = "客户端口号")
    @TableField(fill = FieldFill.INSERT)
    private String clientId;

    /** 系统自增长ID-销售价明细 */
    @TableId
    @ApiModelProperty(value = "系统自增长ID-销售价明细")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long salePriceItemSid;

    /** 系统自增长ID-销售价 */
    @Excel(name = "系统自增长ID-销售价")
    @ApiModelProperty(value = "系统自增长ID-销售价")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long salePriceSid;

    /** 有效期从 */

    @Excel(name = "有效期从", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "有效期从")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date startDate;

    /** 有效期至 */
    @Excel(name = "有效期至", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "有效期至")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date endDate;

    /** 阶梯类型 */
    @Excel(name = "阶梯类型")
    @ApiModelProperty(value = "阶梯类型")
    private String cascadeType;

    /** 价格录入方式 */
    @Excel(name = "价格录入方式")
    @NotBlank(message = "价格录入方式不能为空")
    @ApiModelProperty(value = "价格录入方式")
    private String priceEnterMode;

    @Excel(name = "合同号sid")
    @ApiModelProperty(value = "合同号sid")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long saleContractSid;

    @Excel(name = "合同号编码")
    @ApiModelProperty(value = "合同号编码")
    @TableField(exist = false)
    private String saleContractCode;

    @TableField(exist = false)
    private String saleContractName;

    @Excel(name = "递增减SKU类型")
    @ApiModelProperty(value = "递增减SKU类型")
    private String skuTypeRecursion;

    @ApiModelProperty(value = "递增减计量单位")
    @TableField(exist = false)
    private String unitRecursionName;;

    /** 销售价(含税) */
    @Excel(name = "销售价(含税)")
    @Digits(integer=7,fraction = 3,message = "销售价(含税)整数位上限为7位，小数位上限为3位")
    @ApiModelProperty(value = "销售价(含税)")
    private BigDecimal salePriceTax;

    /** 销售价(不含税) */
    @Excel(name = "销售价(不含税)")
    @Digits(integer=7,fraction = 3,message = "销售价(不含税)整数位上限为7位，小数位上限为3位")
    @ApiModelProperty(value = "销售价(不含税)")
    private BigDecimal salePrice;

    @Excel(name = "报价(含税)")
    @ApiModelProperty(value = "报价(含税)")
    private BigDecimal quotePriceTax;

    @Excel(name = "价格说明")
    @ApiModelProperty(value = "价格说明")
    private String priceRemark;

    @NotBlank(message = "是否递增减价不能为空")
    @ApiModelProperty(value = "是否递增减价")
    private String isRecursionPrice;

    /** 递增减计量单位 */
    @Excel(name = "递增减计量单位")
    @ApiModelProperty(value = "递增减计量单位")
    private String unitRecursion;

    /** 基准量 */
    @Excel(name = "基准量")
    @ApiModelProperty(value = "基准量")
    @Digits(integer=7,fraction = 3,message = "基准量整数位上限为7位，小数位上限为3位")
    private BigDecimal referQuantity;

    @Excel(name = "单位换算比例")
    @ApiModelProperty(value = "单位换算比例")
    private BigDecimal  unitConversionRate;
    /** 递增量 */
    @Excel(name = "递增量")
    @ApiModelProperty(value = "递增量")
    @Digits(integer=7,fraction = 3,message = "递增量整数位上限为7位，小数位上限为3位")
    private BigDecimal increQuantity;

    /** 递减量 */
    @Excel(name = "递减量")
    @ApiModelProperty(value = "递减量")
    @Digits(integer=7,fraction = 3,message = "递减量整数位上限为7位，小数位上限为3位")
    private BigDecimal decreQuantity;

    /** 价格最小起算量 */
    @Excel(name = "价格最小起算量")
    @ApiModelProperty(value = "价格最小起算量")
    private BigDecimal priceMinQuantity;

    /** 递增价(含税) */
    @Digits(integer=7,fraction = 3,message = "递增价(含税)整数位上限为7位，小数位上限为3位")
    @Excel(name = "递增价(含税)")
    @ApiModelProperty(value = "递增价(含税)")
    private BigDecimal increPriceTax;

    /** 递增价(不含税) */
    @Excel(name = "递增价(不含税)")
    @Digits(integer=7,fraction = 3,message = "递增价(不含税)整数位上限为7位，小数位上限为3位")
    @ApiModelProperty(value = "递增价(不含税)")
    private BigDecimal increPrice;

    /** 递减价(含税) */
    @Excel(name = "递减价(含税)")
    @Digits(integer=7,fraction = 3,message = "递减价(含税)整数位上限为7位，小数位上限为3位")
    @ApiModelProperty(value = "递减价(含税)")
    private BigDecimal decrePriceTax;

    /** 递减价(不含税) */
    @Excel(name = "递减价(不含税)")
    @Digits(integer=7,fraction = 3,message = "递减价(不含税)整数位上限为7位，小数位上限为3位")
    @ApiModelProperty(value = "递减价(不含税)")
    private BigDecimal decrePrice;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "变更说明")
    private String updateRemark;

    /** 取整方式(递增减) */
    @Excel(name = "取整方式(递增减)")
    @ApiModelProperty(value = "取整方式(递增减)")
    private String roundingType;

    /** 税率 */
    @Excel(name = "税率")
    @NotBlank(message = "税率不能为空")
    @ApiModelProperty(value = "税率")
    private BigDecimal taxRate;

    @ApiModelProperty(value = "税率名称")
    @TableField(exist = false)
    private BigDecimal taxRateName;

    /** 币种 */
    @Excel(name = "币种")
    @NotBlank(message = "币种不能为空")
    @ApiModelProperty(value = "币种")
    private String currency;

    /** 货币单位 */
    @Excel(name = "货币单位")
    @ApiModelProperty(value = "货币单位")
    private String currencyUnit;

    /** 折扣类型编码 */
    @Excel(name = "折扣类型编码")
    @ApiModelProperty(value = "折扣类型编码")
    private String discountType;

    /** 基本计量单位 */
    @Excel(name = "基本计量单位")
    @NotBlank(message = "基本计量单位不能为空")
    @ApiModelProperty(value = "基本计量单位")
    private String unitBase;

    @TableField(exist = false)
    @ApiModelProperty(value = "基本计量单位名称")
    private String unitBaseName;

    /** 销售价单位 */
    @Excel(name = "销售价单位")
    @NotBlank(message = "销售价单位不能为空")
    @ApiModelProperty(value = "销售价单位")
    private String unitPrice;

    @ApiModelProperty(value = "销售价单位名称")
    @TableField(exist = false)
    private String unitPriceName;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createDate;

    @Excel(name = "创建人账号")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    @TableField(exist = false)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccountName;
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

    /** 数据源系统 */
    @Excel(name = "数据源系统")
    @ApiModelProperty(value = "数据源系统")
    @TableField(fill = FieldFill.INSERT)
    private String dataSourceSys;

    @Excel(name = "处理状态")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @Excel(name = "行号")
    @ApiModelProperty(value = "行号")
    private Integer  itemNum;

    @TableField(exist = false)
    private String importHandle;

    @TableField(exist = false)
    private String submitHandle;

    @ApiModelProperty(value = "系统税率")
    @TableField(exist = false)
    private BigDecimal systemTaxRate;
}
