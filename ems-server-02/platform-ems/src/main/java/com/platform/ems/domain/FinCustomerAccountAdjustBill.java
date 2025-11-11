package com.platform.ems.domain;

import java.io.Serializable;
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
 * 客户调账单对象 s_fin_customer_account_adjust_bill
 *
 * @author qhq
 * @date 2021-05-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_fin_customer_account_adjust_bill")
public class FinCustomerAccountAdjustBill extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-客户调账单
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-客户调账单")
    private Long adjustBillSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] adjustBillSidList;

    /**
     * 客户调账单号
     */
    @Excel(name = "客户调账单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "客户调账单号")
    private Long adjustBillCode;

    /**
     * 系统SID-客户
     */
    @Excel(name = "系统SID-客户")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-客户")
    private Long customerSid;

    /**
     * 系统SID-公司档案
     */
    @Excel(name = "系统SID-公司档案")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-公司档案")
    private Long companySid;

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

    /**
     * 公司品牌sid
     */
    @Excel(name = "公司品牌sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司品牌sid")
    private Long companyBrandSid;

    /**
     * 业务渠道（数据字典的键值）
     */
    @Excel(name = "业务渠道（数据字典的键值）")
    @ApiModelProperty(value = "业务渠道（数据字典的键值）")
    private String businessChannel;

    /**
     * 系统SID-产品季
     */
    @Excel(name = "系统SID-产品季")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-产品季")
    private Long productSeasonSid;

    /**
     * 销售组织（数据字典的键值）
     */
    @Excel(name = "销售组织（数据字典的键值）")
    @ApiModelProperty(value = "销售组织（数据字典的键值）")
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
     * 物料类型（数据字典的键值）
     */
    @Excel(name = "物料类型（数据字典的键值）")
    @ApiModelProperty(value = "物料类型（数据字典的键值）")
    private String materialType;

    /**
     * 物料类型（数据字典的键值）
     */
    @Excel(name = "物料类型（数据字典的键值）")
    @ApiModelProperty(value = "物料类型（数据字典的键值）")
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
     * 处理状态（数据字典的键值）
     */
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "处理状态（数据字典的键值）", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值）")
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
     * 数据源系统（数据字典的键值）
     */
    @Excel(name = "数据源系统（数据字典的键值）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值）")
    private String dataSourceSys;

    @TableField(exist = false)
    @ApiModelProperty(value = "明细子表List")
    private List<FinCustomerAccountAdjustBillItem> itemList;

    @TableField(exist = false)
    @ApiModelProperty(value = "附件List")
    private List<FinCustomerAccountAdjustBillAttachment> attachmentList;

    @TableField(exist = false)
    private String customerName;

    @TableField(exist = false)
    private String companyName;

    @TableField(exist = false)
    private String brandName;

    @TableField(exist = false)
    private String productSeasonName;

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
    @ApiModelProperty(value = "核销状态（数据字典）")
    private String clearStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "创建人")
    private String creatorAccountName;

    @TableField(exist = false)
    @ApiModelProperty(value = "修改人")
    private String updaterAccountName;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售组织名称")
    private String saleOrgName;

    @TableField(exist = false)
    @ApiModelProperty(value = "单据类型名称")
    private String documentTypeName;

    @TableField(exist = false)
    @ApiModelProperty(value = "业务类型名称")
    private String businessTypeName;

    @TableField(exist = false)
    @ApiModelProperty(value = "业务渠道名称")
    private String businessChannelName;

    @TableField(exist = false)
    @ApiModelProperty(value = "创建人（多选）")
    private String[] creatorAccountList;

}
