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
 * 客户业务台账-明细-预收对象 s_fin_record_advance_receipt_item
 *
 * @author linhongwei
 * @date 2021-06-16
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_fin_record_advance_receipt_item")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FinRecordAdvanceReceiptItem extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-客户预收台账流水明细
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-客户预收台账流水明细")
    private Long recordAdvanceReceiptItemSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] recordAdvanceReceiptItemSidList;

    /**
     * 系统SID-客户预收台账流水
     */
    @Excel(name = "系统SID-客户预收台账流水")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-客户预收台账流水")
    private Long recordAdvanceReceiptSid;

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
     * 已收金额（含税）
     */
    @Excel(name = "已核销金额（含税）")
    @ApiModelProperty(value = "已核销金额（含税）")
    private BigDecimal currencyAmountTaxYhx;

    /**
     * 申请中未收金额（含税）
     */
    @Excel(name = "核销中金额（含税）")
    @ApiModelProperty(value = "核销中金额（含税）")
    private BigDecimal currencyAmountTaxHxz;

    /**
     * 待收金额（含税）
     */
    @Excel(name = "待收金额（含税）")
    @TableField(exist = false)
    @ApiModelProperty(value = "待核销金额（含税）")
    private BigDecimal currencyAmountTaxDhx;

    /**
     * 税率（存值，即：不含百分号，如20%，就存0.2）
     */
    @Excel(name = "税率（存值，即：不含百分号，如20%，就存0.2）")
    @ApiModelProperty(value = "税率（存值，即：不含百分号，如20%，就存0.2）")
    private BigDecimal taxRate;

    /**
     * 收款状态
     */
    @Excel(name = "核销状态")
    @ApiModelProperty(value = "核销状态")
    private String clearStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "核销状态(多选)")
    private String[] clearStatusList;

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

    /**
     * 账期有效期（起）
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "账期有效期（起）", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "账期有效期（起）")
    private Date accountValidDate;

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
