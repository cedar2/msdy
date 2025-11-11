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
 * 供应商业务台账-预付对象 s_fin_record_advance_payment
 *
 * @author qhq
 * @date 2021-05-29
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_fin_record_advance_payment")
public class FinRecordAdvancePayment extends EmsBaseEntity {

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商预付台账流水")
    private Long recordAdvancePaymentSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] recordAdvancePaymentSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "子表sid")
    private String recordAdvancePaymentItemSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "台账流水号")
    private Long recordAdvancePaymentCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "供应商编码")
    private Long vendorCode;

    @Excel(name = "供应商")
    @TableField(exist = false)
    @ApiModelProperty(value = "供应商名称")
    private String vendorName;

    @TableField(exist = false)
    @ApiModelProperty(value = "供应商简称")
    private String vendorShortName;

    @Excel(name = "采购合同号")
    @ApiModelProperty(value = "采购合同号")
    private String purchaseContractCode;

    @Excel(name = "采购订单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购订单号")
    private Long purchaseOrderCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "业务类型")
    private String buTypeCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-流水来源类别")
    private String[] bookSourceCategoryList;

    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    @TableField(exist = false)
    @Excel(name = "核销状态", dictType = "s_account_clear")
    @ApiModelProperty(value = "核销状态")
    private String clearStatus;

    @TableField(exist = false)
    @Excel(name = "预付款金额（含税）")
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "应付金额（含税）")
    private BigDecimal currencyAmountTaxYingf;

    @TableField(exist = false)
    @Excel(name = "待核销金额（含税）")
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "待核销金额（含税）")
    private BigDecimal currencyAmountTaxDhx;

    @TableField(exist = false)
    @Excel(name = "核销中金额（含税）")
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "核销中金额（含税）")
    private BigDecimal currencyAmountTaxHxz;

    @TableField(exist = false)
    @Excel(name = "已核销金额（含税）")
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "已核销金额（含税）")
    private BigDecimal currencyAmountTaxYhx;

    @Excel(name = "预付款比例（不含百分号，如20%，就存0.2）")
    @ApiModelProperty(value = "预付款比例（存值，即：不含百分号，如20%，就存0.2）")
    private BigDecimal advanceRate;

    @Excel(name = "合同金额（含税）")
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "合同的总金额（含税）")
    private BigDecimal currencyAmountTaxContract;

    @Excel(name = "订单金额（含税）")
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "订单的总金额（含税）")
    private BigDecimal currencyAmountTaxPo;

    @Excel(name = "到期日")
    @TableField(exist = false)
    @ApiModelProperty(value = "账期有效期（起）")
    private String accountValidDate;

    @Excel(name = "预付款结算方式")
    @TableField(exist = false)
    @ApiModelProperty(value = "预付款/预收款结算方式名称")
    private String settleModeName;

    @Excel(name = "采购员")
    @TableField(exist = false)
    private String buyerName;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司编码")
    private String companyCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @Excel(name = "公司简称")
    @TableField(exist = false)
    @ApiModelProperty(value = "公司简称")
    private String companyShortName;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司品牌编码")
    private String brandCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司品牌名称")
    private String brandName;

    @Excel(name = "产品季")
    @TableField(exist = false)
    @ApiModelProperty(value = "产品季")
    private String productSeasonName;

    @Excel(name = "年份")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "年份")
    private Integer paymentYear;

    @Excel(name = "供方合同号")
    @TableField(exist = false)
    @ApiModelProperty(value = "供方合同号")
    private String vendorContractCode;

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

    @TableField(exist = false)
    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人昵称")
    private String creatorAccountName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @TableField(exist = false)
    @Excel(name = "流水类型")
    @ApiModelProperty(value = "财务流水类型编码code")
    private String bookTypeName;

    @TableField(exist = false)
    @Excel(name = "流水来源类别")
    @ApiModelProperty(value = "财务流水来源类别编码code（数据字典的键值或配置档案的编码）")
    private String bookSourceCategoryName;

    @ApiModelProperty(value = "流水类型编码code")
    @TableField(exist = false)
    private String accountType;

    @ApiModelProperty(value = "款项类别编码code")
    @TableField(exist = false)
    private String accountCategory;

    @ApiModelProperty(value = "财务流水类型编码code")
    private String bookType;

    @ApiModelProperty(value = "财务流水来源类别编码code（数据字典的键值或配置档案的编码）")
    private String bookSourceCategory;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商")
    private Long vendorSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-供应商")
    private Long[] vendorSidList;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-公司档案")
    private Long companySid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "产品季")
    private Long productSeasonSid;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "凭证日期")
    private Date documentDate;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "月份")
    private Integer paymentMonth;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司品牌sid")
    private Long companyBrandSid;

    @ApiModelProperty(value = "销售渠道（数据字典的键值或配置档案的编码）")
    private String businessChannel;

    @ApiModelProperty(value = "预付款结算方式（数据字典的键值或配置档案的编码）")
    private String settleMode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购合同sid")
    private Long purchaseContractSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购订单sid")
    private Long purchaseOrderSid;

    @ApiModelProperty(value = "采购员账号")
    private String buyer;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料类型编码")
    private String materialType;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料类型，用于多选查询")
    private String[] materialTypeList;

    @TableField(exist = false)
    @ApiModelProperty(value = "采购员账号，用于多选查询")
    private String[] buyerList;

    @TableField(exist = false)
    @ApiModelProperty(value = "核销状态，用于多选查询")
    private String[] clearStatusList;

    @TableField(exist = false)
    @ApiModelProperty(value = "预付款结算方式，用于多选查询")
    private String[] settleModeList;

    @TableField(exist = false)
    @ApiModelProperty(value = "产品季Sid列表，用于多选查询")
    private Long[] productSeasonSidList;

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    @ApiModelProperty(value = "确认人账号（用户名称）")
    private String confirmerAccount;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    @TableField(exist = false)
    @ApiModelProperty(value = "核销状态，用于隐藏的核销状态过滤条件，如 核销状态 ！= clearStatusNot")
    private String clearStatusNot;

    @TableField(exist = false)
    @ApiModelProperty(value = "流水明细列表")
    private List<FinRecordAdvancePaymentItem> itemList;

    @TableField(exist = false)
    @ApiModelProperty(value = "附件清单列表")
    private List<FinRecordAdvancePaymentAttachment> atmList;

    @TableField(exist = false)
    private String processType;

    @TableField(exist = false)
    private String instanceId;

    @TableField(exist = false)
    @ApiModelProperty(value = "节点名称")
    private String node;

    @TableField(exist = false)
    @ApiModelProperty(value = "审批人")
    private String approval;

    @TableField(exist = false)
    @ApiModelProperty(value = "行号")
    private Long itemNum;

}
