package com.platform.ems.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotEmpty;

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
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 客户扣款单对象 s_fin_customer_deduction_bill
 *
 * @author qhq
 * @date 2021-06-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_fin_customer_deduction_bill")
public class FinCustomerDeductionBill extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-客户扣款单
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-客户扣款单")
    private Long deductionBillSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] deductionBillSidList;

    /**
     * 客户扣款单号
     */
    @Excel(name = "客户扣款单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "客户扣款单号")
    private Long deductionBillCode;

    /**
     * 系统SID-客户
     */
    @Excel(name = "系统SID-客户")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-客户")
    private Long customerSid;

    @ApiModelProperty(value = "客户")
    private String customerCode;

    /**
     * 系统SID-客户
     */
    @Excel(name = "系统SID-客户")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-客户")
    @TableField(exist = false)
    private String customerName;

    /**
     * 系统SID-公司档案
     */
    @Excel(name = "系统SID-公司档案")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-公司档案")
    private Long companySid;

    @ApiModelProperty(value = "公司")
    private String companyCode;

    /**
     * 系统SID-公司档案
     */
    @Excel(name = "系统SID-公司档案")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-公司档案")
    @TableField(exist = false)
    private String companyName;

    /**
     * 单据日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "单据日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "单据日期")
    private Date documentDate;

    /**
     * 单据类型编码code
     */
    @Excel(name = "单据类型编码code")
    @ApiModelProperty(value = "单据类型编码code")
    private String documentType;

    /**
     * 业务类型编码code
     */
    @Excel(name = "业务类型编码code")
    @ApiModelProperty(value = "业务类型编码code")
    private String businessType;

    @TableField(exist = false)
    @ApiModelProperty(value = "业务类型编码code")
    private String businessTypeName;

    /**
     * 公司品牌sid
     */
    @Excel(name = "公司品牌sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司品牌sid")
    private Long companyBrandSid;

    /**
     * 业务渠道（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "业务渠道（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "业务渠道（数据字典的键值或配置档案的编码）")
    private String businessChannel;

    /**
     * 系统SID-产品季
     */
    @Excel(name = "系统SID-产品季")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-产品季")
    private Long productSeasonSid;

    /**
     * 系统SID-产品季
     */
    @Excel(name = "系统SID-产品季")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-产品季")
    @TableField(exist = false)
    private String productSeasonName;

    /**
     * 销售组织（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "销售组织（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "销售组织（数据字典的键值或配置档案的编码）")
    private String saleOrg;

    /**
     * 销售员（用户名称）
     */
    @Excel(name = "销售员（用户名称）")
    @ApiModelProperty(value = "销售员（用户名称）")
    private String salePerson;

    /**
     * 销售员（用户名称）
     */
    @Excel(name = "销售员（用户名称）")
    @ApiModelProperty(value = "销售员（用户名称）")
    @TableField(exist = false)
    private String salePersonName;

    /**
     * 物料类型（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "物料类型（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "物料类型（数据字典的键值或配置档案的编码）")
    private String materialType;

    /**
     * 物料类型（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "物料类型（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "物料类型（数据字典的键值或配置档案的编码）")
    @TableField(exist = false)
    private String materialTypeName;

    /**
     * 币种（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "币种（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "币种（数据字典的键值或配置档案的编码）")
    private String currency;

    /**
     * 货币单位（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "货币单位（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "货币单位（数据字典的键值或配置档案的编码）")
    private String currencyUnit;

    /**
     * 处理状态（数据字典的键值或配置档案的编码）
     */
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "处理状态（数据字典的键值或配置档案的编码）", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

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
     * 确认人账号（用户名称）
     */
    @Excel(name = "确认人账号（用户名称）")
    @ApiModelProperty(value = "确认人账号（用户名称）")
    private String confirmerAccount;

    /**
     * 确认时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "确认时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "数据源系统（数据字典的键值或配置档案的编码）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    /**
     * 流程状态
     */
    @Excel(name = "流程状态")
    @ApiModelProperty(value = "流程状态")
    private String processType;

    /**
     * 流程id
     */
    @Excel(name = "流程id")
    @ApiModelProperty(value = "流程id")
    private String instanceId;

    @ApiModelProperty(value = "明细list")
    @TableField(exist = false)
    private List<FinCustomerDeductionBillItem> itemList;

    @ApiModelProperty(value = "附件list")
    @TableField(exist = false)
    private List<FinCustomerDeductionBillAttachment> attachmentList;

    @TableField(exist = false)
    @ApiModelProperty(value = "核销状态（数据字典）")
    private String clearStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "创建人")
    private String creatorAccountName;

    @TableField(exist = false)
    @ApiModelProperty(value = "修改人")
    private String updaterAccountName;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-客户商（下拉框 多选）")
    private Long[] customerSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-公司档案（下拉框 多选）")
    private Long[] companySidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-产品季（下拉框 多选）")
    private Long[] productSeasonSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售员（下拉框 多选）")
    private String[] salePersonList;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态（数据字典 多选）")
    private String[] handleStatusList;

    @TableField(exist = false)
    @ApiModelProperty(value = "创建人（多选）")
    private String[] creatorAccountList;

    @TableField(exist = false)
    @ApiModelProperty(value = "业务类型编码code")
    private String[] businessTypeList;

    /**
     * 明细合
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "扣款金额(含税)")
    private BigDecimal currencyAmountTaxTotal;

    /**
     * 操作类型提交审批驳回
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "操作类型提交TJ审批SPTG驳回SPBH")
    private String operateType;
}
