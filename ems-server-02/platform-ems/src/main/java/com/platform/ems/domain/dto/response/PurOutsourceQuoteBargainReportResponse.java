package com.platform.ems.domain.dto.response;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.ems.util.Phone;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Email;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 加工议价明细报表响应实体
 *
 * @author yangqz
 * @date 2021-7-12
 */
@Data
@ApiModel
@Accessors(chain = true)
public class PurOutsourceQuoteBargainReportResponse implements Serializable{

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "加工询报议价单号")
    private Long outsourceQuoteBargainCode;

    @Excel(name = "供应商")
    @ApiModelProperty(value ="供应商")
    private String vendorName;

    @Excel(name = "商品编码")
    @ApiModelProperty(value = "商品编码")
    private String materialCode;

    @Excel(name = "我司样衣号")
    @ApiModelProperty(value = "我司样衣号")
    private String sampleCodeSelf;

    @Excel(name = "商品名称")
    @ApiModelProperty(value = "商品名称")
    private String materialName;

    @Excel(name = "sku1名称")
    @ApiModelProperty(value = "sku1名称")
    private String sku1Name;

    @Excel(name = "加工询价单编码")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "加工询价单编码")
    private Long outsourceInquiryCode;

    @Excel(name = "报价计划截至日期", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "报价计划截至日期")
    private Date quotepriceDeadline;


    /** 议价日期 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "议价日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "议价日期")
    private Date dateConfirm;

    @ApiModelProperty(value ="公司编码")
    private String companyCode;

    @Excel(name = "公司名称")
    @ApiModelProperty(value ="公司名称")
    private String companyName;

    @Excel(name = "公司简称")
    @ApiModelProperty(value ="公司简称")
    private String companyShortName;

    /** 采购员（用户名称） */
    @Excel(name = "采购员（用户名称）")
    @ApiModelProperty(value = "采购员（用户名称）")
    private String buyer;


    @Excel(name = "采购员（用户名称）")
    @ApiModelProperty(value = "采购员（用户名称）")
    private String buyerName;

    @ApiModelProperty(value ="产品季名称")
    @TableField(exist = false)
    private String productSeasonName;

    @ApiModelProperty(value = "报价员（用户名称）")
    private String quoter;

    @Excel(name = "报价员电话")
    @ApiModelProperty(value = "报价员电话")
    private String quoterTelephone;

    @Excel(name = "报价员邮箱")
    @ApiModelProperty(value = "报价员邮箱")
    private String quoterEmail;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "报价日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "报价日期")
    private Date dateQuote;

    /**
     * 采购员名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "采购员名称")
    @Excel(name = "采购员名称")
    private String nickName;

    /** 采购员电话 */
    @Excel(name = "采购员电话")
    @ApiModelProperty(value = "采购员电话")
    @Phone
    private String buyerTelephone;

    /** 采购员邮箱 */
    @Excel(name = "采购员邮箱")
    @ApiModelProperty(value = "采购员邮箱")
    @Email
    private String buyerEmail;

    @Excel(name = "加工项")
    @ApiModelProperty(value = "加工项")
    private String processName;

    @ApiModelProperty(value = "采购组")
    private String purchaseGroupName;

    @Excel(name = "价格维度（数据字典的键值）")
    @ApiModelProperty(value = "价格维度（数据字典的键值）")
    private String priceDimension;

    @Excel(name = "报价(含税)")
    @ApiModelProperty(value = "报价(含税)")
    private BigDecimal quotePriceTax;

    @Excel(name = "核定价(含税)")
    @ApiModelProperty(value = "核定价(含税)")
    private BigDecimal checkPriceTax;

    @Excel(name = "采购价(含税)")
    @ApiModelProperty(value = "采购价(含税)")
    private BigDecimal purchasePriceTax;

    @Excel(name = "税率（存值，即：不含百分号，如20%，就存0.2）")
    @ApiModelProperty(value = "税率（存值，即：不含百分号，如20%，就存0.2）")
    private BigDecimal taxRate;

    @Excel(name = "基本计量单位（数据字典的键值）")
    @ApiModelProperty(value = "基本计量单位（数据字典的键值）")
    private String unitBase;

    @Excel(name = "采购价计量单位（数据字典的键值）")
    @ApiModelProperty(value = "采购价计量单位（数据字典的键值）")
    private String unitPrice;

    @Excel(name = "单位换算比例（采购价单位/基本单位）")
    @ApiModelProperty(value = "单位换算比例（采购价单位/基本单位）")
    private BigDecimal unitConversionRate;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "报价更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "报价更新时间")
    private Date quoteUpdateDate;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "核定价更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "核定价更新时间")
    private Date checkUpdateDate;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "采购价更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "采购价更新时间")
    private Date purchaseUpdateDate;

    @Excel(name = "价格录入方式（数据字典的键值）")
    @ApiModelProperty(value = "价格录入方式（数据字典的键值）")
    private String priceEnterMode;

    @Excel(name = "基本计量单位")
    @ApiModelProperty(value = "基本计量单位")
    private String  unitBaseName;

    @Excel(name = "采购计量单位")
    @ApiModelProperty(value = "采购计量单位")
    private String  unitPriceName;

    @Excel(name = "税率")
    @ApiModelProperty(value = "税率")
    private String  taxRateName;

    @Excel(name = "行号")
    @ApiModelProperty(value = "行号")
    private int itemNum;

    /** 当前审批节点名称 */
    @ApiModelProperty(value = "当前审批节点名称")
    @TableField(exist = false)
    private String approvalNode;

    /** 当前审批人ID */
    @ApiModelProperty(value = "当前审批人ID")
    @TableField(exist = false)
    private String approvalUserId;

    /** 当前审批人 */
    @ApiModelProperty(value = "当前审批人")
    @TableField(exist = false)
    private String approvalUserName;

    /** 提交人 */
    @ApiModelProperty(value = "提交人")
    @TableField(exist = false)
    private String submitUserName;

    /** 处理状态 */
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;
    /**
     * 提交日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "提交日期")
    @TableField(exist = false)
    private Date submitDate;

    @TableField(exist = false)
    private String updaterAccountName;

    @TableField(exist = false)
    private String confirmerAccountName;

    /**
     * 系统SID-加工询报议价单明细信息
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-加工询报议价单明细信息")
    private Long outsourceQuoteBargainItemSid;

    /**
     * 系统SID-加工询报议价单
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-加工询报议价单")
    private Long outsourceQuoteBargainSid;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;
    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

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

    @TableField(exist = false)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String  creatorAccountName;

    @ApiModelProperty(value = "询价备注")
    private String remarkRequest;

    @ApiModelProperty(value = "报价备注")
    private String remarkQuote;

    @ApiModelProperty(value = "核价备注")
    private String remarkCheck;

    @ApiModelProperty(value = "议价备注")
    private String remarkConfirm;

    @ApiModelProperty(value = "备注")
    private String remark;

    /** 客方结算价(含税) */
    @Excel(name = "客方确认价(含税)")
    @ApiModelProperty(value = "客方确认价(含税)")
    private BigDecimal customerPriceTax;

    @ApiModelProperty(value = "工艺说明")
    private String  processDesc;

    @ApiModelProperty(value = "工艺图片")
    private String  processPicture;

    @ApiModelProperty(value = "核价员")
    private String checkerName;

    @ApiModelProperty(value = "核价员（用户账号）")
    private String checker;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "核价日期")
    private Date dateCheck;

    @ApiModelProperty(value = "当前审批人ID（多选）")
    private String[] approvalUserIdList;

}
