package com.platform.ems.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;

import com.platform.ems.util.data.KeepTwoDecimalsSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;


/**
 * 客户业务台账-预收对象 s_fin_record_advance_receipt
 *
 * @author qhq
 * @date 2021-06-16
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_fin_record_advance_receipt")
public class FinRecordAdvanceReceipt extends EmsBaseEntity {

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-客户预收台账流水")
    private Long recordAdvanceReceiptSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] recordAdvanceReceiptSidList;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "台账流水号")
    private Long recordAdvanceReceiptCode;

    @Excel(name = "客户")
    @TableField(exist = false)
    @ApiModelProperty(value = "客户名称")
    private String customerName;

    @Excel(name = "销售合同号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售合同号")
    private String saleContractCode;

    @Excel(name = "销售订单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售订单号")
    private Long saleOrderCode;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售订单号")
    private Long salesOrderCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "业务类型")
    private String buTypeCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-流水来源类别")
    private String[] bookSourceCategoryList;

    @ApiModelProperty(value = "款项类别编码code")
    @TableField(exist = false)
    private String accountCategory;

    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    @Excel(name = "核销状态", dictType = "s_account_clear")
    @TableField(exist = false)
    @ApiModelProperty(value = "核销状态")
    private String clearStatus;

    @Excel(name = "应收金额（含税）")
    @TableField(exist = false)
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "应收金额（含税）")
    private BigDecimal currencyAmountTaxYings;

    @Excel(name = "待核销金额（含税）")
    @TableField(exist = false)
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "待核销金额（含税）")
    private BigDecimal currencyAmountTaxDhx;

    @Excel(name = "核销中金额（含税）")
    @TableField(exist = false)
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "核销中金额（含税）")
    private BigDecimal currencyAmountTaxHxz;

    @Excel(name = "已核销金额（含税）")
    @TableField(exist = false)
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "已核销金额（含税）")
    private BigDecimal currencyAmountTaxYhx;

    @Excel(name = "预收款比例（即：不含百分号，如20%，就存0.2）")
    @ApiModelProperty(value = "预收款比例（存值，即：不含百分号，如20%，就存0.2）")
    private BigDecimal advanceRate;

    @Excel(name = "合同金额（含税）")
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "合同的总金额（含税）")
    private BigDecimal currencyAmountTaxContract;

    @Excel(name = "订单金额（含税）")
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "订单的总金额（含税）")
    private BigDecimal currencyAmountTaxSo;

    @Excel(name = "到期日")
    @TableField(exist = false)
    @ApiModelProperty(value = "账期有效期（起）")
    private String accountValidDate;

    @Excel(name = "预收款结算方式")
    @TableField(exist = false)
    @ApiModelProperty(value = "预付款/预收款结算方式")
    private String settleModeName;

    @Excel(name = "销售员")
    @TableField(exist = false)
    @ApiModelProperty(value = "销售员")
    private String salePersonName;

    @Excel(name = "公司")
    @TableField(exist = false)
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司简称")
    private String companyShortName;

    @Excel(name = "产品季")
    @TableField(exist = false)
    @ApiModelProperty(value = "产品季")
    private String productSeasonName;

    @Excel(name = "年份")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "年份")
    private Integer paymentYear;

    @Excel(name = "客方合同号")
    @TableField(exist = false)
    @ApiModelProperty(value = "客方合同号")
    private String customerContractCode;

    @Excel(name = "物料类型")
    @TableField(exist = false)
    @ApiModelProperty(value = "物料类型")
    private String materialTypeName;

    @Excel(name = "税率")
    @TableField(exist = false)
    @ApiModelProperty(value = "税率")
    private String taxRate;

    @Excel(name = "币种", dictType = "s_currency")
    @ApiModelProperty(value = "币种")
    private String currency;

    @Excel(name = "货币单位", dictType = "s_currency_unit")
    @ApiModelProperty(value = "货币单位")
    private String currencyUnit;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    @Excel(name = "创建人")
    @TableField(exist = false)
    @ApiModelProperty(value = "创建人昵称")
    private String creatorAccountName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @Excel(name = "流水类型")
    @TableField(exist = false)
    @ApiModelProperty(value = "财务流水类型编码code")
    private String bookTypeName;

    @Excel(name = "流水来源类别")
    @TableField(exist = false)
    @ApiModelProperty(value = "财务流水来源类别编码code")
    private String bookSourceCategoryName;

    /**
     * 财务流水类型编码code
     */
    @ApiModelProperty(value = "财务流水类型编码code")
    private String bookType;

    /**
     * 财务流水来源类别编码code（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "财务流水来源类别编码code（数据字典的键值或配置档案的编码）")
    private String bookSourceCategory;

    /**
     * 系统SID-客户
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-客户")
    private Long customerSid;

    @TableField(exist = false)
    private Long[] customerSidList;

    /**
     * 系统SID-公司档案
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-公司档案")
    private Long companySid;

    /**
     * 凭证日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "凭证日期")
    private Date documentDate;

    /**
     * 产品季
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "产品季")
    private Long productSeasonSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "产品季")
    private String[] productSeasonSidList;

    @ApiModelProperty(value = "销售员")
    private String salePerson;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售员")
    private String[] salePersonList;

    /**
     * 物料类型
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "物料类型")
    private String materialType;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料类型")
    private String[] materialTypeList;

    /**
     * 月份
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "月份")
    private Integer paymentMonth;

    /**
     * 公司品牌sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司品牌sid")
    private Long companyBrandSid;

    /**
     * 业务渠道（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "业务渠道（数据字典的键值或配置档案的编码）")
    private String businessChannel;

    /**
     * 预收款结算方式（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "预收款结算方式（数据字典的键值或配置档案的编码）")
    private String settleMode;

    @TableField(exist = false)
    @ApiModelProperty(value = "预收款结算方式（数据字典的键值或配置档案的编码）")
    private String[] settleModeList;

    /**
     * 销售合同sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售合同sid")
    private Long saleContractSid;

    /**
     * 销售订单sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售订单sid")
    private Long saleOrderSid;

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

    @TableField(exist = false)
    private List<FinRecordAdvanceReceiptItem> itemList;

    @TableField(exist = false)
    private List<FinRecordAdvanceReceiptAttachment> atmList;

    @TableField(exist = false)
    @ApiModelProperty(value = "明细sid")
    private String recordAdvanceReceiptItemSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "核销状态")
    private String clearStatusNot;

    @TableField(exist = false)
    @ApiModelProperty(value = "核销状态")
    private String[] clearStatusList;

    @TableField(exist = false)
    @ApiModelProperty(value = "客户code")
    private String customerCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "客户name")
    private String customerShortName;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司code")
    private String companyCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司品牌code")
    private String brandCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司品牌name")
    private String brandName;

    @TableField(exist = false)
    @ApiModelProperty(value = "行号")
    private int itemNum;

}
