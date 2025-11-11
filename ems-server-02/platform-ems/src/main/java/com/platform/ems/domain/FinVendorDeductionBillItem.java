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
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 供应商扣款单-明细对象 s_fin_vendor_deduction_bill_item
 *
 * @author linhongwei
 * @date 2021-05-31
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_fin_vendor_deduction_bill_item")
public class FinVendorDeductionBillItem extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-供应商扣款单明细
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商扣款单明细")
    private Long deductionBillItemSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] deductionBillItemSidList;

    /**
     * 系统SID-供应商扣款单
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商扣款单")
    private Long deductionBillSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "供应商扣款单号")
    @Excel(name = "供应商扣款单号")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long deductionBillCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "供应商")
    @Excel(name = "供应商名称")
    private String vendorName;

    @TableField(exist = false)
    @ApiModelProperty(value = "产品季")
    @Excel(name = "产品季名称")
    private String productSeasonName;

    @TableField(exist = false)
    /**    @Excel(name = "物料类型")  */
    @ApiModelProperty(value = "物料类型")
    private String materialTypeName;

    @Excel(name = "采购员")
    @TableField(exist = false)
    @ApiModelProperty(value = "采购员")
    private String buyerName;

    @TableField(exist = false)
    @ApiModelProperty(value = "核销状态")
    private String clearStatus;

    @Excel(name = "扣款类型")
    @TableField(exist = false)
    @ApiModelProperty(value = "扣款类型")
    private String deductionTypeName;

    @Excel(name = "扣款金额(含税)")
    @ApiModelProperty(value = "扣款金额(含税)")
    private BigDecimal currencyAmountTax;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司")
    @Excel(name = "公司名称")
    private String companyName;

    @TableField(exist = false)
    @ApiModelProperty(value = "单据日期")
    @Excel(name = "单据日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date documentDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态")
    @Excel(name = "处理状态", dictType = "s_handle_status")
    private String handleStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "币种")
    @Excel(name = "币种", dictType = "s_currency")
    private String currency;

    @TableField(exist = false)
    @ApiModelProperty(value = "币种单位")
    @Excel(name = "货币单位", dictType = "s_currency_unit")
    private String currencyUnit;

    /**    @Excel(name = "采购合同号")  */
    @ApiModelProperty(value = "采购合同号")
    private String purchaseContractCode;

    /**    @Excel(name = "采购订单号") */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购订单号")
    private Long purchaseOrderCode;

    /**    @Excel(name = "采购交货单") */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购交货单/销售发货单号")
    private Long deliveryNoteCode;

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    @Excel(name = "创建人")
    @TableField(exist = false)
    @ApiModelProperty(value = "创建人")
    private String creatorAccountName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /**
     * 扣款类型（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "扣款类型（数据字典的键值或配置档案的编码）")
    private String deductionType;

    /**
     * 行号
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "行号")
    private Long itemNum;

    @ApiModelProperty(value = "纸质合同号")
    private String paperContractCode;

    /**
     * 采购合同sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购合同sid")
    private Long purchaseContractSid;

    /**
     * 采购订单sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购订单sid")
    private Long purchaseOrderSid;

    /**
     * 采购交货单/销售发货单sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购交货单/销售发货单sid")
    private Long deliveryNoteSid;



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
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    @TableField(exist = false)
    @ApiModelProperty(value = "供应商sid")
    private String vendorSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "供应商编码")
    private String vendorCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "产品季sid")
    private String productSeasonSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "产品季编码")
    private String productSeasonCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料类型")
    private String materialType;

    @TableField(exist = false)
    @ApiModelProperty(value = "采购员")
    private String buyer;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司sid")
    private String companySid;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司编码")
    private String companyCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "供应商")
    private Long[] vendorSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司")
    private Long[] companySidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "产品季")
    private Long[] productSeasonSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料类型（配置档案 code 多选框）")
    private String[] materialTypeList;

    @TableField(exist = false)
    @ApiModelProperty(value = "核销状态（数据字典 多选框）")
    private String[] clearStatusList;

    @TableField(exist = false)
    @ApiModelProperty(value = "采购员（user_name 多选框）")
    private String[] buyerList;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态（数据字典的键值）(多选框)")
    private String[] handleStatusList;

    /**
     * 配置档案表 s_con_bu_type_vendor_deduction
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "业务类型")
    private String businessTypeName;

}
