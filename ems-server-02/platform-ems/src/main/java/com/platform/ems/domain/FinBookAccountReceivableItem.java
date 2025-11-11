package com.platform.ems.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

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

/**
 * 财务流水账-明细-应收对象 s_fin_book_account_receivable_item
 *
 * @author linhongwei
 * @date 2021-06-11
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_fin_book_account_receivable_item")
public class FinBookAccountReceivableItem extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-流水账明细（应收）
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-流水账明细（应收）")
    private Long bookAccountReceivableItemSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] bookAccountReceivableItemSidList;
    /**
     * 系统SID-流水账（应收）
     */
    @Excel(name = "系统SID-流水账（应收）")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-流水账（应收）")
    private Long bookAccountReceivableSid;

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
     * 行号
     */
    @Excel(name = "行号")
    @ApiModelProperty(value = "行号")
    private Long itemNum;

    /**
     * 应收金额（含税）
     */
    @Excel(name = "应收金额（含税）")
    @ApiModelProperty(value = "应收金额（含税）")
    private BigDecimal currencyAmountTaxYings;

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

    @Excel(name = "核销中金额（含税）")
    @TableField(exist = false)
    @ApiModelProperty(value = "核销中金额（含税）")
    private BigDecimal currencyAmountTaxDhx;

    /**
     * 销售合同sid
     */
    @Excel(name = "销售合同sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售合同sid")
    private Long saleContractSid;

    /**
     * 销售合同号
     */
    @Excel(name = "销售合同号")
    @ApiModelProperty(value = "销售合同号")
    private String saleContractCode;

    /**
     * 销售订单sid
     */
    @Excel(name = "销售订单sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售订单sid")
    private Long saleOrderSid;

    /**
     * 销售订单号
     */
    @Excel(name = "销售订单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售订单号")
    private Long saleOrderCode;

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
    @ApiModelProperty(value = "核销状态(多选)")
    private String[] clearStatusList;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态(多选)")
    private String[] handleStatusList;

    /**
     * 账期(天)
     */
    @Excel(name = "账期(天)")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "账期(天)")
    private Long accountValidDays;

    /**
     * 账期天类型编码（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "账期天类型编码（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "账期天类型编码（数据字典的键值或配置档案的编码）")
    private String dayType;

    @Excel(name = "账期有效期（起）")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "账期有效期（起）")
    private Date accountValidDate;

    @ApiModelProperty(value = "是否已财务对账（数据字典的键值或配置档案的编码）\n")
    private String isFinanceVerify;

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

    @TableField(exist= false)
    @ApiModelProperty(value = "关联单据号")
    private Long bookAccountPayableCode;

    @TableField(exist= false)
    @ApiModelProperty(value = "财务流水来源类别编码code（数据字典的键值或配置档案的编码）")
    private String bookSourceCategory;
}
