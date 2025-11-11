package com.platform.ems.domain;

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
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 客户对账单暂估明细表对象 s_sal_customer_month_account_bill_zangu
 *
 * @author xfzz
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_sal_customer_month_account_bill_zangu")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SalCustomerMonthAccountBillZangu extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-客户对账单暂估明细SID
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-客户对账单暂估明细SID")
    private Long customerMonthAccountBillZanguSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] customerMonthAccountBillZanguSidList;

    /**
     * 系统SID-客户对账单
     */
    @Excel(name = "系统SID-客户对账单")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-客户对账单")
    private Long customerMonthAccountBillSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] customerMonthAccountBillSidList;

    /**
     * 系统SID-流水账明细(应付暂估)
     */
    @Excel(name = "系统SID-流水账明细(应付暂估)")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-客户对账单")
    private Long bookReceiptEstimationItemSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] bookReceiptEstimationItemSidList;

    /**
     * 流水号(应付暂估)
     */
    @Excel(name = "流水号(应付暂估)")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "流水号(应付暂估)")
    private Long bookReceiptEstimationCode;

    /** 创建人账号（用户名称） */
    @Excel(name = "创建人账号（用户名称）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    @TableField(exist = false)
    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人")
    private String creatorAccountName;

    /** 创建时间 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;


    /**
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "数据源系统（数据字典的键值或配置档案的编码）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    @TableField(exist = false)
    @ApiModelProperty(value = "流水来源类别")
    private String bookSourceCategoryName;

    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "出入库日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "出入库日期")
    private Date accountDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料/商品编码")
    private String materialCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料/商品名称")
    private String materialName;

    @TableField(exist = false)
    @ApiModelProperty(value = "SKU1名称")
    private String sku1Name;

    @TableField(exist = false)
    @ApiModelProperty(value = "SKU2名称")
    private String sku2Name;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售价单位")
    private String unitPriceName;

    @TableField(exist = false)
    @ApiModelProperty(value = "出入库数量")
    private BigDecimal quantity;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售价(含税)")
    private BigDecimal priceTax;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售价(不含税)")
    private BigDecimal price;

    @TableField(exist = false)
    @ApiModelProperty(value = "金额(含税)")
    private BigDecimal currencyAmountTax;

    @TableField(exist = false)
    @ApiModelProperty(value = "税率")
    private BigDecimal taxRate;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售合同号")
    private String saleContractCode;

    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "合同交期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "合同交期")
    private Date contractDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "下单季")
    private String productSeasonName;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售模式")
    private String businessChannelName;

    @TableField(exist = false)
    @ApiModelProperty(value = "客户")
    private String customerName;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司")
    private String companyName;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售订单号")
    private String salesOrderCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "流水类型")
    private String bookTypeName;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售发货单号")
    private String deliveryNoteCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料类型")
    private String materialTypeName;

    @Excel(name = "库存凭证号")
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "库存凭证号")
    private Long inventoryDocumentCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "对账账期")
    private String businessVerifyPeriod;

    @TableField(exist = false)
    @ApiModelProperty(value = "出入库人")
    private String storehouseOperatorName;

    @ApiModelProperty(value = "规格")
    @TableField(exist = false)
    @Length(max = 180, message = "规格尺寸不能超过180个字符")
    private String specificationSize;

    @ApiModelProperty(value = "型号")
    @TableField(exist = false)
    @Length(max = 180, message = "型号不能超过180个字符")
    private String modelSize;

    @TableField(exist = false)
    @ApiModelProperty(value = "是否已业务对账（数据字典的键值）")
    private String isBusinessVerify;

    /**
     * 流水特征（数据字典的键值或配置档案的编码）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "流水特征（数据字典的键值或配置档案的编码）")
    private String bookFeature;

}
