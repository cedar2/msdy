package com.platform.ems.domain;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;

import com.platform.ems.util.Phone;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;


/**
 * 报议价单主(报价/核价/议价)对象  s_pur_quote_bargain
 *
 * @author qhq
 * @date 2021-05-13
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = " s_pur_quote_bargain")
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class PurQuoteBargain extends EmsBaseEntity {
    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-报议价单
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-报议价单")
    private Long quoteBargainSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private List<Long> quoteBargainSidList;

    @ApiModelProperty(value = "明细的sid数组")
    @TableField(exist = false)
    private Long[] quoteBargainItemSidList;

    /**
     * 报议价单号
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "报议价单号")
    private Long quoteBargainCode;


    /**
     * 系统SID-供应商档案sid
     */
    @NotNull(message = "供应商不能为空")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商档案sid")
    private Long vendorSid;

    /**
     * 系统SID-供应商档案sid
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "查询：供应商")
    private Long[] vendorSids;

    /**
     * 系统SID-供应商档案sid
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "查询：供应商")
    private Long[] vendorSidList;

    /**
     * 公司编码（公司档案的sid）
     */
    @ApiModelProperty(value = "公司编码（公司档案的sid）")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long companySid;

    /**
     * 公司编码（公司档案的sid）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "查询：公司")
    private Long[] companySids;

    /**
     * 公司编码（公司档案的sid）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "查询：公司")
    private Long[] companySidList;

    /**
     * 系统SID-产品季档案
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-产品季档案")
    private Long productSeasonSid;

    /**
     * 系统SID-产品季档案
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "查询：产品季")
    private Long[] productSeasonSids;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：产品季")
    private Long[] productSeasonSidList;

    /**
     * 物料类别编码
     */
    @Excel(name = "物料类别编码", dictType = "s_material_category")
    @ApiModelProperty(value = "物料类别编码")
    private String materialCategory;

    /**
     * 有效期（起）
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "有效期（起）", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "有效期（起）")
    private Date startDate;

    /**
     * 有效期（止）
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "有效期（止）", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "有效期（止）")
    private Date endDate;

    /**
     * 询价日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "询价日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "询价日期")
    private Date dateRequest;

    /**
     * 报价计划截至日期
     */
    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "报价计划截至日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "报价计划截至日期")
    private Date quotepriceDeadline ;

    /**
     * 报价日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "报价日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "报价日期")
    private Date dateQuote;

    /**
     * 核价日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "核价日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "核价日期")
    private Date dateCheck;

    /**
     * 议价日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "议价日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "议价日期")
    private Date dateConfirm;

    /**
     * 采购员（用户名称）
     */
    @Excel(name = "采购员（用户名称）")
    @ApiModelProperty(value = "采购员（用户名称）")
    private String buyer;

    /**
     * 采购员名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "采购员名称")
    @Excel(name = "采购员名称")
    private String nickName;

    /**
     * 采购员电话
     */
    @Excel(name = "采购员电话")
    @ApiModelProperty(value = "采购员电话")
    @Phone
    private String buyerTelephone;

    /**
     * 采购员邮箱
     */
    @Excel(name = "采购员邮箱")
    @ApiModelProperty(value = "采购员邮箱")
    @Email
    private String buyerEmail;

    /**
     * 报价员（用户名称）
     */
    @Excel(name = "报价员（用户名称）")
    @ApiModelProperty(value = "报价员（用户名称）")
    private String quoter;

    /**
     * 报价员电话
     */
    @Excel(name = "报价员电话")
    @ApiModelProperty(value = "报价员电话")
    @Phone
    private String quoterTelephone;

    /**
     * 报价员邮箱
     */
    @Excel(name = "报价员邮箱")
    @ApiModelProperty(value = "报价员邮箱")
    @Email
    private String quoterEmail;

    @ApiModelProperty(value = "核价员（用户账号）")
    private String checker;

    @TableField(exist = false)
    @ApiModelProperty(value = "核价员（用户账号）")
    private String[] checkerList;

    @TableField(exist = false)
    @Excel(name = "核价员")
    @ApiModelProperty(value = "核价员（用户账号）")
    private String checkerName;

    @Excel(name = "甲供料方式（数据字典的键值）")
    @ApiModelProperty(value = "甲供料方式")
    @NotBlank(message = "甲供料方式不能为空")
    private String rawMaterialMode;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：甲供料方式")
    private String[] rawMaterialModes;

    @TableField(exist = false)
    @ApiModelProperty(value = "甲供料方式（数据字典的键值）")
    private String[] rawMaterialModeList;

    /**
     * 采购模式（数据字典的键值）
     */
    @Excel(name = "采购模式（数据字典的键值）")
    @ApiModelProperty(value = "采购模式（数据字典的键值）")
    @NotBlank(message = "采购模式不能为空")
    private String purchaseMode;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：采购模式")
    private String[] purchaseModes;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：采购模式")
    private String[] purchaseModeList;

    /**
     * 采购组织（数据字典的键值）
     */
    @Excel(name = "采购组织（数据字典的键值）")
    @ApiModelProperty(value = "采购组织（数据字典的键值）")
    private String purchaseOrg;

    /**
     * 采购组（数据字典的键值）
     */
    @Excel(name = "采购组（数据字典的键值）")
    @ApiModelProperty(value = "采购组（数据字典的键值）")
    private String purchaseGroup;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：采购组")
    private String[] purchaseGroups;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：采购组")
    private String[] purchaseGroupList;

    /**
     * 采购组名称
     */
    @TableField(exist = false)
    @Excel(name = "采购组名称")
    @ApiModelProperty(value = "采购组名称")
    private String purchaseGroupName;

    /**
     * 成本组织（数据字典的键值）
     */
    @Excel(name = "成本组织（数据字典的键值）")
    @ApiModelProperty(value = "成本组织（数据字典的键值）")
    private String costOrg;

    /**
     * 物料类型（数据字典的键值）
     */
    @Excel(name = "物料类型（数据字典的键值）")
    @ApiModelProperty(value = "物料类型（数据字典的键值）")
    private String materialType;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：物料类型")
    private String[] materialTypes;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：物料类型")
    private String[] materialTypeList;

    /**
     * 递增减SKU类型（数据字典的键值）
     */
    @Excel(name = "递增减SKU类型（数据字典的键值）")
    @ApiModelProperty(value = "递增减SKU类型（数据字典的键值）")
    private String skuTypeRecursion;

    /**
     * 币种（数据字典的键值）
     */
    @Excel(name = "币种（数据字典的键值）")
    @ApiModelProperty(value = "币种（数据字典的键值）")
    @NotBlank(message = "币种不能为空")
    private String currency;

    /**
     * 货币单位（数据字典的键值）
     */
    @Excel(name = "货币单位（数据字典的键值）")
    @ApiModelProperty(value = "货币单位（数据字典的键值）")
    @NotBlank(message = "货币单位不能为空")
    private String currencyUnit;

    @Excel(name = "建单所属阶段", dictType = "baoheyi_stage")
    @ApiModelProperty(value = "建单所属阶段（数据字典的键值或配置档案的编码）")
    private String createdStage;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料采购价格记录")
    private Long inquirySid;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "物料采购价格记录编码")
    private Long inquiryCode;

    /**
     * 备注(询价)
     */
    @Excel(name = "备注(询价)")
    @ApiModelProperty(value = "备注(询价)")
    private String remarkRequest;

    /**
     * 备注(报价)
     */
    @Excel(name = "备注(报价)")
    @ApiModelProperty(value = "备注(报价)")
    private String remarkQuote;

    /**
     * 备注(核价)
     */
    @Excel(name = "备注(核价)")
    @ApiModelProperty(value = "备注(核价)")
    private String remarkCheck;

    /**
     * 备注(确认价)
     */
    @Excel(name = "备注(确认价)")
    @ApiModelProperty(value = "备注(确认价)")
    private String remarkConfirm;

    /**
     * 备注(采购价)
     */
    @Excel(name = "备注(采购价)")
    @ApiModelProperty(value = "备注(采购价)")
    private String remarkPurchase;

    /**
     * 处理状态（数据字典的键值）
     */
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "处理状态（数据字典的键值）", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值）")
    private String handleStatus;

    /**
     * 处理状态（数据字典的键值）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态（数据字典的键值）")
    private String[] handleStatuses;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态（数据字典的键值）")
    private String[] handleStatuseList;

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
    private String companyName;

    @TableField(exist = false)
    private String productSeasonName;

    @TableField(exist = false)
    private String vendorName;

    @ApiModelProperty(value = "明细表")
    @TableField(exist = false)
    private List<PurQuoteBargainItem> purRequestQuotationItemList;

    @ApiModelProperty(value = "附件表")
    @TableField(exist = false)
    private List<PurQuoteBargainAttach> purRequestQuotationAttachmentList;

    @Excel(name = "更新人")
    @TableField(exist = false)
    private String updaterAccountName;

    @Excel(name = "创建人")
    @TableField(exist = false)
    private String creatorAccountName;

    @Excel(name = "确认人")
    @TableField(exist = false)
    private String confirmerAccountName;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：有效起")
    private String dateQuoteBeginTime;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：有效至")
    private String dateQuoteEndTime;

    @Excel(name = "价格维度（数据字典的键值）")
    @ApiModelProperty(value = "价格维度（数据字典的键值）")
    @TableField(exist = false)
    private String priceDimension;

    @TableField(exist = false)
    private String judgeSubmit;

    @TableField(exist = false)
    private String importHandle;

    @TableField(exist = false)
    @ApiModelProperty(value = "行号")
    private Long itemNum;
}
