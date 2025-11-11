package com.platform.ems.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableId;
import com.platform.common.core.domain.EmsBaseEntity;
import com.platform.ems.util.Phone;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.experimental.Accessors;

/**
 * 加工询报议价单主(询价/报价/核价/议价)对象 s_pur_outsource_request_quotation
 *
 * @author linhongwei
 * @date 2021-05-10
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_pur_outsource_quote_bargain")
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class PurOutsourceQuoteBargain extends EmsBaseEntity implements Serializable {

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-加工询报议价单
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-加工询报议价单")
    private Long outsourceQuoteBargainSid;

    @TableField(exist = false)
    private Long[] outsourceQuoteBargainSidList;

    /**
     * 加工询报议价单号
     */
    @Excel(name = "加工询报议价单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "加工询报议价单号")
    private Long outsourceQuoteBargainCode;

    /**
     * 系统SID-供应商档案sid
     */
    @NotNull(message = "供应商不能为空")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商档案sid")
    private Long vendorSid;

    /**
     * 公司编码（公司档案的sid）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司编码（公司档案的sid）")
    private Long companySid;

    /**
     * 系统SID-产品季档案
     */
    @Excel(name = "系统SID-产品季档案")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-产品季档案")
    private Long productSeasonSid;

    /** 物料类别编码 */
    @Excel(name = "物料类别编码", dictType = "s_material_category")
    @ApiModelProperty(value = "物料类别编码")
    private String materialCategory;

    /**
     * 询价日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "询价日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "询价日期")
    private Date dateRequest;

    @TableField(exist = false)
    @Excel(name = "报价计划截至日期", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "报价计划截至日期")
    private Date quotepriceDeadline;

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

    /**
     * 物料类型（数据字典的键值）
     */
    @Excel(name = "物料类型（数据字典的键值）")
    @ApiModelProperty(value = "物料类型（数据字典的键值）")
    private String materialType;

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


    @ApiModelProperty(value ="附件列表")
    @TableField(exist = false)
    private List<PurOutsourceQuoteBargainAttach> attachmentList;

    @ApiModelProperty(value ="明细列表")
    @TableField(exist = false)
    private List<PurOutsourceQuoteBargainItem> itemList;

    @ApiModelProperty(value ="供应商名称")
    @TableField(exist = false)
    private String vendorName;

    @ApiModelProperty(value ="供应商编码")
    @TableField(exist = false)
    private String vendorCode;

    @ApiModelProperty(value ="公司名称")
    @TableField(exist = false)
    private String companyName;

    @ApiModelProperty(value ="公司编码")
    @TableField(exist = false)
    private String companyCode;

    @ApiModelProperty(value ="产品季名称")
    @TableField(exist = false)
    private String productSeasonName;

    @ApiModelProperty(value ="产品季编码")
    @TableField(exist = false)
    private String productSeasonCode;

    @TableField(exist = false)
    @ApiModelProperty(value ="查询：供应商")
    private Long[] vendorSidList;

    @TableField(exist = false)
    @ApiModelProperty(value ="查询：公司")
    private Long[] companySidList;

    @TableField(exist = false)
    @ApiModelProperty(value ="查询：产品季")
    private Long[] productSeasonSidList;

    @ApiModelProperty(value = "查询：采购组")
    @TableField(exist = false)
    private String[] purchaseGroupList;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：物料类型")
    private String[] materialTypeList;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：处理状态")
    private String[] handleStatusList;

    @TableField(exist = false)
    @ApiModelProperty(value = "供方编码（物料/商品/服务）")
    private String supplierProductCode;

    @ApiModelProperty(value = "创建人账号（用户名称）")
    @TableField(exist = false)
    private String creatorAccountName;

    @ApiModelProperty(value = "更新人账号（用户名称）")
    @TableField(exist = false)
    private String updaterAccountName;

    @ApiModelProperty(value = "确认人账号（用户名称）")
    @TableField(exist = false)
    private String confirmerAccountName;

    @ApiModelProperty(value = "采购员")
    @TableField(exist = false)
    private String buyerName;

    @Excel(name = "基本计量单位")
    @ApiModelProperty(value = "基本计量单位")
    @TableField(exist = false)
    private String  unitBaseName;

    @Excel(name = "采购计量单位")
    @ApiModelProperty(value = "采购计量单位")
    @TableField(exist = false)
    private String  unitPriceName;

    /** 当前审批节点名称 */
    @Excel(name = "当前审批节点名称")
    @ApiModelProperty(value = "当前审批节点名称")
    @TableField(exist = false)
    private String approvalNode;

    /** 当前审批人 */
    @Excel(name = "当前审批人")
    @ApiModelProperty(value = "当前审批人")
    @TableField(exist = false)
    private String approvalUserName;

    /** 提交人 */
    @Excel(name = "提交人")
    @ApiModelProperty(value = "提交人")
    @TableField(exist = false)
    private String submitUserName;

    /**
     * 提交日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "提交日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "提交日期")
    @TableField(exist = false)
    private Date submitDate;

    @Excel(name = "价格维度（数据字典的键值）")
    @ApiModelProperty(value = "价格维度（数据字典的键值）")
    @TableField(exist = false)
    private String priceDimension;

    @TableField(exist = false)
    private String judgeSubmit;

    @TableField(exist = false)
    private String importHandle;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-加工询价单")
    private Long outsourceInquirySid;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "加工询价单编码")
    private Long outsourceInquiryCode;

    @TableField(exist = false)
    @Excel(name = "当前所属阶段", dictType = "s_baoheyi_stage")
    @ApiModelProperty(value = "当前所属阶段（单选）")
    private String currentStage;

    @TableField(exist = false)
    @ApiModelProperty(value = "当前所属阶段（多选）")
    private String[] currentStageList;

    @ApiModelProperty(value = "建单所属阶段（单选）")
    private String createdStage;

    @TableField(exist = false)
    @ApiModelProperty(value = "建单所属阶段（多选）")
    private String[] createdStageList;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询页面的所属阶段")
    private String stage;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-加工采购价")
    private Long outsourcePurchasePriceSid;

}
