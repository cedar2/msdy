package com.platform.ems.domain;

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
import lombok.experimental.Accessors;


/**
 * 财务流水账-客户账互抵对象 s_fin_book_customer_account_balance
 *
 * @author qhq
 * @date 2021-06-11
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_fin_book_customer_account_balance")
public class FinBookCustomerAccountBalance extends EmsBaseEntity {

    /** 租户ID */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /** 系统SID-流水账（客户账互抵） */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-流水账（客户账互抵）")
    private Long bookAccountBalanceSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] bookAccountBalanceSidList;

    @Excel(name = "客户账互抵财务流水号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "流水号（客户账互抵）")
    private Long bookAccountBalanceCode;

    @Excel(name = "客户")
    @TableField(exist = false)
    @ApiModelProperty(value = "客户名")
    private String customerName;

    @Excel(name = "公司")
    @TableField(exist = false)
    @ApiModelProperty(value = "公司名")
    private String companyName;

    @Excel(name = "处理状态", dictType = "s_handle_status")
    @NotEmpty(message = "状态不能为空")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    @Excel(name = "互抵金额")
    @TableField(exist = false)
    @ApiModelProperty(value = "hd金额")
    private BigDecimal currencyAmountTaxHd;

    @Excel(name = "客户账互抵单号")
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "客户账互抵单号 ")
    private Long accountBalanceBillCode;

    @Excel(name = "关联流水号")
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "关联流水号")
    private Long referAccountDocumentCode;

    @Excel(name = "关联流水类型")
    @TableField(exist = false)
    @ApiModelProperty(value = "关联流水类型（配置档案的名称）")
    private String referBookTypeName;

    @Excel(name = "关联流水来源类别")
    @TableField(exist = false)
    @ApiModelProperty(value = "关联流水来源类别（配置档案的名称）")
    private String referBookSourceCategoryName;

    @TableField(exist = false)
    @Excel(name = "销售员")
    @ApiModelProperty(value = "销售员")
    private String salePersonName;

    @TableField(exist = false)
    @Excel(name = "产品季")
    @ApiModelProperty(value = "系统SID-产品季")
    private String productSeasonName;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料类型")
    private String materialTypeName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "单据日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "凭证日期")
    private Date documentDate;

    @TableField(exist = false)
    @Excel(name = "行号")
    @ApiModelProperty(value = "行号")
    private int itemNum;

    @TableField(exist = false)
    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    @Excel(name = "币种", dictType = "s_currency")
    @ApiModelProperty(value = "币种")
    private String currency;

    @Excel(name = "货币单位", dictType = "s_currency_unit")
    @ApiModelProperty(value = "货币单位")
    private String currencyUnit;

    @TableField(exist = false)
    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人昵称")
    private String creatorAccountName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;


    /** 系统SID-客户 */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-客户")
    private Long customerSid;

    /** 系统SID-公司档案 */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-公司档案")
    private Long companySid;

    /** 年份 */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "年份")
    private int paymentYear;

    /** 月份 */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "月份")
    private int paymentMonth;

    /** 公司品牌sid */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司品牌sid")
    private Long companyBrandSid;

    /** 业务渠道（数据字典的键值或配置档案的编码） */
    @ApiModelProperty(value = "业务渠道（数据字典的键值或配置档案的编码）")
    private String businessChannel;

    @ApiModelProperty(value = "流水类型")
    private String bookType;

    @ApiModelProperty(value = "流水来源类别")
    private String bookSourceCategory;

    @TableField(exist = false)
    @ApiModelProperty(value = "流水类型")
    private String bookTypeName;

    @TableField(exist = false)
    @ApiModelProperty(value = "流水来源类别")
    private String bookSourceCategoryName;

    /** 创建人账号（用户名称） */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    /** 更新人账号（用户名称） */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

    /** 更新时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /** 确认人账号（用户名称） */
    @ApiModelProperty(value = "确认人账号（用户名称）")
    private String confirmerAccount;

    /** 确认时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /** 数据源系统（数据字典的键值或配置档案的编码） */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    @TableField(exist = false)
    @ApiModelProperty(value = "明细表LIST")
    private List<FinBookCustomerAccountBalanceItem> itemList;

    @TableField(exist = false)
    @ApiModelProperty(value = "附件表LIST")
    private List<FinBookCustomerAccountBalanceAttachment> atmList;

    //=================报表参数======================

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "关联单号")
    private Long referDocSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "客户调账单号")
    private Long referDocCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "核销状态")
    private String clearStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "类型")
    private String referDocCategory;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商")
    private String vendorName;

    /** 关联单据行号 */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "关联单据行号")
    private Long referDocItemCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "关联单据类别（配置档案的名称）")
    private String referDocCategoryName;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-产品季")
    private Long productSeasonSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售员（user_name）")
    private String salePerson;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料类型（code）")
    private String materialType;

    @TableField(exist = false)
    @ApiModelProperty(value = "关联流水类型（配置档案的名称）")
    private String referBookType;

    @TableField(exist = false)
    @ApiModelProperty(value = "关联流水来源类别（配置档案的名称）")
    private String referBookSourceCategory;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-客户")
    private Long[] customerSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-公司")
    private Long[] companySidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-产品季")
    private Long[] productSeasonSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-流水类型")
    private String[] referBookTypeList;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-流水来源类别")
    private String[] referBookSourceCategoryList;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-销售员员")
    private String[] salePersonList;

}
