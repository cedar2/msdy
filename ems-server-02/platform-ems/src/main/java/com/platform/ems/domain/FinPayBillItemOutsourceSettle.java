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
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 付款单-外发加工费结算单明细对象 s_fin_pay_bill_item_outsource_settle
 *
 * @author platform
 * @date 2024-05-22
 */
@Data
@Accessors(chain = true)
@TableName(value = "s_fin_pay_bill_item_outsource_settle")
public class FinPayBillItemOutsourceSettle extends EmsBaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-付款单外发加工费结算单明细
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-付款单外发加工费结算单明细")
    private Long payBillItemOutsourceSettleSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "sid数组")
    private Long[] payBillItemOutsourceSettleSidList;

    /**
     * 系统SID-付款单
     */
    @Excel(name = "系统SID-付款单")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-付款单")
    private Long payBillSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-付款单")
    private Long[] payBillSidList;

    /**
     * 付款单号
     */
    @Excel(name = "付款单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "付款单号")
    private Long payBillCode;

    /**
     * 系统SID-外发加工费结算单
     */
    @Excel(name = "系统SID-外发加工费结算单")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-外发加工费结算单")
    private Long manufactureOutsourceSettleSid;

    /**
     * 外发加工费结算单号
     */
    @Excel(name = "外发加工费结算单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "外发加工费结算单号")
    private Long manufactureOutsourceSettleCode;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商信息（外发加工商）")
    private Long vendorSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "供应商编码")
    private String vendorCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "供应商名称")
    private String vendorName;

    @TableField(exist = false)
    @ApiModelProperty(value = "供应商简称")
    private String vendorShortName;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-公司信息")
    private Long companySid;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司编码")
    private String companyCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司简称")
    private String companyShortName;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-工厂信息")
    private Long plantSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "工厂编码")
    private String plantCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "工厂名称")
    private String plantName;

    @TableField(exist = false)
    @ApiModelProperty(value = "工厂简称")
    private String plantShortName;

    @TableField(exist = false)
    @ApiModelProperty(value = "单据类型编码code")
    private String documentType;

    @TableField(exist = false)
    @ApiModelProperty(value = "单据类型")
    private String documentTypeName;

    @TableField(exist = false)
    @ApiModelProperty(value = "业务类型编码code")
    private String businessType;

    @TableField(exist = false)
    @ApiModelProperty(value = "业务类型")
    private String businessTypeName;

    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "单据日期")
    private Date documentDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "结算金额小计(含税)")
    private BigDecimal totalSettleAmountTax;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-采购合同")
    private Long purchaseContractSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "采购合同号")
    private String purchaseContractCode;

    /**
     * 本次申请金额(含税)
     */
    @Excel(name = "本次申请金额(含税)")
    @ApiModelProperty(value = "本次申请金额(含税)")
    private BigDecimal currencyAmountTax;

    @TableField(exist = false)
    @ApiModelProperty(value = "本次申请金额(含税)(支付中)")
    private BigDecimal currencyAmountTaxZfz;

    @TableField(exist = false)
    @ApiModelProperty(value = "本次申请金额(含税)(已支付)")
    private BigDecimal currencyAmountTaxYzf;

    @TableField(exist = false)
    @ApiModelProperty(value = "本次申请金额(含税)(未支付)")
    private BigDecimal currencyAmountTaxWzf;

    @TableField(exist = false)
    @ApiModelProperty(value = "结算单的备注")
    private String outsourceSettleRemark;

    /**
     * 创建人账号（用户账号）
     */
    @TableField(fill = FieldFill.INSERT)
    @Excel(name = "创建人账号")
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    /**
     * 创建人
     */
    @TableField(exist = false)
    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人昵称")
    private String creatorAccountName;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /**
     * 更新人账号（用户账号）
     */
    @TableField(fill = FieldFill.UPDATE)
    @Excel(name = "更新人账号")
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccount;

    /**
     * 更改人
     */
    @TableField(exist = false)
    @Excel(name = "更改人")
    @ApiModelProperty(value = "更改人昵称")
    private String updaterAccountName;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.UPDATE)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @TableField(fill = FieldFill.INSERT)
    @Excel(name = "数据源系统")
    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;

}
