package com.platform.ems.domain;

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
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import lombok.experimental.Accessors;

/**
 * 财务流水账-明细-付款对象 s_fin_book_payment_item
 *
 * @author linhongwei
 * @date 2021-06-07
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_fin_book_payment_item")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FinBookPaymentItem extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-流水账明细（付款）
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-流水账明细（付款）")
    private Long bookPaymentItemSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] bookPaymentItemSidList;
    /**
     * 系统SID-流水账（付款）
     */
    @Excel(name = "系统SID-流水账（付款）")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-流水账（付款）")
    private Long bookPaymentSid;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-流水账（付款）")
    private Long bookPaymentCode;

    /**
     * 行号
     */
    @Excel(name = "行号")
    @ApiModelProperty(value = "行号")
    private Long itemNum;

    /**
     * 付款金额（含税）
     */
    @Excel(name = "待付款金额（含税）")
    @TableField(exist = false)
    @ApiModelProperty(value = "待核销金额（含税）/待付款")
    private BigDecimal currencyAmountTaxDhx;

    /**
     * 付款金额（含税）
     */
    @Excel(name = "付款金额（含税）")
    @ApiModelProperty(value = "付款金额（含税）")
    private BigDecimal currencyAmountTaxFk;

    /**
     * 已核销金额（含税）
     */
    @Excel(name = "已核销金额（含税）")
    @ApiModelProperty(value = "已核销金额（含税）")
    private BigDecimal currencyAmountTaxYhx;

    /**
     * 核销中金额（含税）
     */
    @Excel(name = "核销中金额（含税）")
    @ApiModelProperty(value = "核销中金额（含税）")
    private BigDecimal currencyAmountTaxHxz;

    /**
     * 关联单据类别（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "关联单据类别（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "关联单据类别（数据字典的键值或配置档案的编码）")
    private String referDocCategory;

    /**
     * 关联单据sid
     */
    @Excel(name = "关联单据sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "关联单据sid")
    private Long referDocSid;

    /**
     * 关联单据号
     */
    @Excel(name = "关联单据号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "关联单据号")
    private Long referDocCode;

    /**
     * 关联单据行sid（冗余）
     */
    @Excel(name = "关联单据行sid（冗余）")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "关联单据行sid（冗余）")
    private Long referDocItemSid;

    /**
     * 关联单据行号（冗余）
     */
    @Excel(name = "关联单据行号（冗余）")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "关联单据行号（冗余）")
    private Long referDocItemCode;

    /**
     * 税率（存值，即：不含百分号，如20%，就存0.2）
     */
    @Excel(name = "税率（存值，即：不含百分号，如20%，就存0.2）")
    @ApiModelProperty(value = "税率（存值，即：不含百分号，如20%，就存0.2）")
    private BigDecimal taxRate;

    /**
     * 核销状态
     */
    @Excel(name = "核销状态")
    @ApiModelProperty(value = "核销状态")
    private String clearStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "核销状态")
    private String[] clearStatusList;

    @TableField(exist = false)
    @ApiModelProperty(value = "财务流水来源类别编码code（数据字典的键值或配置档案的编码）")
    private String bookSourceCategory;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-公司档案")
    private Long companySid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-供应商档案")
    private Long vendorSid;

    /**
     * 账期有效期（起）
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "账期有效期（起）", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "账期有效期（起）")
    private Date accountValidDate;

    @ApiModelProperty(value = "是否已财务对账（数据字典的键值或配置档案的编码）\n")
    private String isFinanceVerify;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "最近被付款单引用日期")
    private Date newPaymentUseDate;

    /**
     * 创建人账号（用户名称）
     */
    @Excel(name = "创建人账号（用户名称）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

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
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "数据源系统（数据字典的键值或配置档案的编码）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;


}
