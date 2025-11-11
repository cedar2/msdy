package com.platform.ems.domain.dto.response;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.ems.util.Phone;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class PurQuoteBargainReportResponse {

    /** 询报议价单号 */
    @ApiModelProperty(value = "询报议价单号")
    private String quoteBargainCode;

    /** 系统SID-询报议价单明细信息 */
    @TableId
    @ApiModelProperty(value = "系统SID-询报议价单明细信息")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long quoteBargainItemSid;

    /** 有效期（起） */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "有效期（起）", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "有效期（起）")
    private Date startDate;

    /** 有效期（止） */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "有效期（止）", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "有效期（止）")
    private Date endDate;

    /** 系统SID-询报议价单号 */
    @Excel(name = "系统SID-询报议价单号")
    @ApiModelProperty(value = "系统SID-询报议价单号")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long quoteBargainSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料采购价格记录")
    private Long inquirySid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "物料采购价格记录编码")
    private Long inquiryCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料采购价格记录明细")
    private Long inquiryItemSid;

    /** 系统SID-供应商档案sid */
    @NotNull(message = "供应商不能为空")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商档案sid")
    private Long vendorSid;

    /** 系统SID-供应商档案sid */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商档案sid")
    private Long[] vendorSids;

    /** 公司编码（公司档案的sid） */
    @NotNull(message = "公司不能为空")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司编码（公司档案的sid）")
    private Long companySid;

    /** 公司编码（公司档案的sid） */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司编码（公司档案的sid）")
    private Long[] companySids;

    /** 系统SID-产品季档案 */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-产品季档案")
    private Long productSeasonSid;

    /** 系统SID-产品季档案 */
    @ApiModelProperty(value = "系统SID-产品季档案")
    private Long[] productSeasonSids;

    /** 物料类别编码 */
    @Excel(name = "物料类别编码", dictType = "s_material_category")
    @ApiModelProperty(value = "物料类别编码")
    private String materialCategory;

    /** 采购员（用户名称） */
    @Excel(name = "采购员（用户名称）")
    @ApiModelProperty(value = "采购员（用户名称）")
    @NotBlank(message = "采购员不能为空")
    private String buyer;

    @TableField(exist = false)
    private String buyerName;

    /**
     * 采购员名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "采购员名称")
    @Excel(name = "采购员名称")
    private String nickName;


    /** 甲供料方式（数据字典的键值） */
    @Excel(name = "甲供料方式（数据字典的键值）")
    @ApiModelProperty(value = "甲供料方式（数据字典的键值）")
    @NotBlank(message = "甲供料方式不能为空")
    private String rawMaterialMode;

    /** 甲供料方式（数据字典的键值） */
    @TableField(exist = false)
    @ApiModelProperty(value = "甲供料方式（数据字典的键值）")
    private String[] rawMaterialModes;

    /** 采购模式（数据字典的键值） */
    @Excel(name = "采购模式（数据字典的键值）")
    @ApiModelProperty(value = "采购模式（数据字典的键值）")
    @NotBlank(message = "采购模式不能为空")
    private String purchaseMode;

    /** 采购模式（数据字典的键值） */
    @TableField(exist = false)
    @ApiModelProperty(value = "采购模式（数据字典的键值）")
    private String[] purchaseModes;


    /** 递增减SKU类型（数据字典的键值） */
    @Excel(name = "递增减SKU类型（数据字典的键值）")
    @ApiModelProperty(value = "递增减SKU类型（数据字典的键值）")
    private String skuTypeRecursion;

    /** 是否询价（数据字典的键值） */
    @Excel(name = "是否询价（数据字典的键值）")
    @ApiModelProperty(value = "是否询价（数据字典的键值）")
    private String isRequestProcess;

    /** 处理状态（数据字典的键值） */
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "处理状态（数据字典的键值）", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值）")
    private String handleStatus;

    /** 处理状态（数据字典的键值） */
    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态（数据字典的键值）")
    private String[] handleStatuses;

    /** 创建人账号（用户名称） */
    @Excel(name = "创建人账号（用户名称）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    @TableField(exist = false)
    private String companyName;

    @TableField(exist = false)
    private String productSeasonName;

    @TableField(exist = false)
    private String vendorName;


    @Excel(name = "物料/商品 编码")
    @ApiModelProperty(value = "物料/商品 编码")
    @TableField(exist = false)
    private String materialCode;

    @Excel(name = "物料/商品 名称")
    @ApiModelProperty(value = "物料/商品 名称")
    @TableField(exist = false)
    private String materialName;

    /** 系统SID-SKU1档案sid */
    @Excel(name = "系统SID-SKU1档案sid")
    @ApiModelProperty(value = "系统SID-SKU1档案sid")
    private Long sku1Sid;

    @Excel(name = "sku1名称")
    @ApiModelProperty(value = "sku1名称")
    @TableField(exist = false)
    private String sku1Name;

    /** 价格维度（数据字典的键值） */
    @Excel(name = "价格维度（数据字典的键值）")
    @ApiModelProperty(value = "价格维度（数据字典的键值）")
    @NotBlank(message = "价格维度不能为空")
    private String priceDimension;

    /** 阶梯类型（数据字典的键值） */
    @Excel(name = "阶梯类型（数据字典的键值）")
    @ApiModelProperty(value = "阶梯类型（数据字典的键值）")
    private String cascadeType;

    /** 价格录入方式（数据字典的键值） */
    @Excel(name = "价格录入方式（数据字典的键值）")
    @ApiModelProperty(value = "价格录入方式（数据字典的键值）")
    private String priceEnterMode;

    /** 报价(含税) */
    @Excel(name = "报价(含税)")
    @ApiModelProperty(value = "报价(含税)")
    private BigDecimal quotePriceTax;

    /** 核定价(含税) */
    @Excel(name = "核定价(含税)")
    @ApiModelProperty(value = "核定价(含税)")
    private BigDecimal checkPriceTax;

    /** 确认价(含税) */
    @Excel(name = "确认价(含税)")
    @ApiModelProperty(value = "确认价(含税)")
    private BigDecimal confirmPriceTax;

    /** 采购价(含税) */
    @Excel(name = "采购价(含税)")
    @ApiModelProperty(value = "采购价(含税)")
    private BigDecimal purchasePriceTax;


    /** 税率（存值，即：不含百分号，如20%，就存0.2） */
    @Excel(name = "税率（存值，即：不含百分号，如20%，就存0.2）")
    @ApiModelProperty(value = "税率（存值，即：不含百分号，如20%，就存0.2）")
    private BigDecimal taxRate;

    /** 基准量 */
    @Excel(name = "基准量")
    @ApiModelProperty(value = "基准量")
    private BigDecimal referQuantity   ;

    /** 递增量 */
    @Excel(name = "递增量")
    @ApiModelProperty(value = "递增量")
    private BigDecimal increQuantity;

    /** 递减量 */
    @Excel(name = "递减量")
    @ApiModelProperty(value = "递减量")
    private BigDecimal decreQuantity;

    /** 价格最小起算量 */
    @Excel(name = "价格最小起算量")
    @ApiModelProperty(value = "价格最小起算量")
    private BigDecimal priceMinQuantity;

    /** 取整方式(递增减)（数据字典的键值） */
    @Excel(name = "取整方式(递增减)（数据字典的键值）")
    @ApiModelProperty(value = "取整方式(递增减)（数据字典的键值）")
    private String roundingType;

    @ApiModelProperty(value = "物料类型")
    private String materialTypeName;

    /** 递增减计量单位（数据字典的键值） */
    @Excel(name = "递增减计量单位（数据字典的键值）")
    @ApiModelProperty(value = "递增减计量单位（数据字典的键值）")
    private String unitRecursion;

    /** 递增报价(含税) */
    @Excel(name = "递增报价(含税)")
    @ApiModelProperty(value = "递增报价(含税)")
    private BigDecimal increQuoPriceTax;


    /** 递增核定价(含税) */
    @Excel(name = "递增核定价(含税)")
    @ApiModelProperty(value = "递增核定价(含税)")
    private BigDecimal increChePriceTax;

    /** 递增确认价(含税) */
    @Excel(name = "递增确认价(含税)")
    @ApiModelProperty(value = "递增确认价(含税)")
    private BigDecimal increConfPriceTax;

    @Excel(name = "采购组名称")
    @ApiModelProperty(value = "采购组名称")
    private String purchaseGroupName;

    /** 递增采购价(含税) */
    @Excel(name = "递增采购价(含税)")
    @ApiModelProperty(value = "递增采购价(含税)")
    private BigDecimal increPurPriceTax;

    /** 递减报价(含税) */
    @Excel(name = "递减报价(含税)")
    @ApiModelProperty(value = "递减报价(含税)")
    private BigDecimal decreQuoPriceTax;

    /** 递减核定价(含税) */
    @Excel(name = "递减核定价(含税)")
    @ApiModelProperty(value = "递减核定价(含税)")
    private BigDecimal decreChePriceTax;

    /** 递减确认价(含税) */
    @Excel(name = "递减确认价(含税)")
    @ApiModelProperty(value = "递减确认价(含税)")
    private BigDecimal decreConfPriceTax;

    /** 递减采购价(含税) */
    @Excel(name = "递减采购价(含税)")
    @ApiModelProperty(value = "递减采购价(含税)")
    private BigDecimal decrePurPriceTax;

    @ApiModelProperty(value = "是否递增减价")
    private String isRecursionPrice;

    /** 基本计量单位（数据字典的键值） */
    @Excel(name = "基本计量单位（数据字典的键值）")
    @ApiModelProperty(value = "基本计量单位（数据字典的键值）")
    private String unitBase;

    /** 采购价计量单位（数据字典的键值） */
    @Excel(name = "采购价计量单位（数据字典的键值）")
    @ApiModelProperty(value = "采购价计量单位（数据字典的键值）")
    private String unitPrice;

    /** 单位换算比例（采购价单位/基本单位） */
    @Excel(name = "单位换算比例（采购价单位/基本单位）")
    @ApiModelProperty(value = "单位换算比例（采购价单位/基本单位）")
    private BigDecimal unitConversionRate;

    /** 报价更新人账号（用户名称） */
    @Excel(name = "报价更新人账号（用户名称）")
    @ApiModelProperty(value = "报价更新人账号（用户名称）")
    private String quoteUpdaterAccount;

    /** 报价更新时间 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "报价更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "报价更新时间")
    private Date quoteUpdateDate;

    @Excel(name = "报价更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "查询：报价更新时间起")
    private String quoteUpdateDateBeginTime;

    @Excel(name = "报价更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "查询：报价更新时间至")
    private String quoteUpdateDateEndTime;

    /** 核定价更新人账号（用户名称） */
    @Excel(name = "核定价更新人账号（用户名称）")
    @ApiModelProperty(value = "核定价更新人账号（用户名称）")
    private String checkUpdaterAccount;

    @Excel(name = "核定价更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "查询：核定价更新时间起")
    private String checkUpdateDateBeginTime;

    @Excel(name = "核定价更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "查询：核定价更新时间至")
    private String checkUpdateDateEndTime;


    /** 核定价更新时间 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "核定价更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "核定价更新时间")
    private Date checkUpdateDate;

    /** 确认价更新人账号（用户名称） */
    @Excel(name = "确认价更新人账号（用户名称）")
    @ApiModelProperty(value = "确认价更新人账号（用户名称）")
    private String confirmUpdaterAccount;

    /** 确认价更新时间 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "确认价更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认价更新时间")
    private Date confirmUpdateDate;

    @Excel(name = "确认价更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "查询：确认价更新时间起")
    private String confirmUpdateBeginTime;

    @Excel(name = "确认价更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "查询：确认价更新时间至")
    private String confirmUpdateEndTime;

    /** 采购价更新人账号（用户名称） */
    @Excel(name = "采购价更新人账号（用户名称）")
    @ApiModelProperty(value = "采购价更新人账号（用户名称）")
    private String purchaseUpdaterAccount;

    /** 采购价更新时间 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "采购价更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "采购价更新时间")
    private Date purchaseUpdateDate;


    @TableField(exist = false)
    @ApiModelProperty(value = "基本计量单位名称")
    private String unitBaseName;

    @ApiModelProperty(value = "采购计量单位名称")
    @TableField(exist = false)
    private String unitPriceName;

    @Excel(name = "税率")
    @ApiModelProperty(value = "税率")
    @TableField(exist = false)
    private BigDecimal taxRateName;

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

    @ApiModelProperty(value = "创建日期开始时间")
    @TableField(exist = false)
    private String beginTime;

    @ApiModelProperty(value = "创建日期结束时间")
    @TableField(exist = false)
    private String endTime;

    @ApiModelProperty(value = "页数")
    @TableField(exist = false)
    private Integer pageNum;

    @ApiModelProperty(value = "每页个数")
    @TableField(exist = false)
    private Integer pageSize;

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

    @ApiModelProperty(value = "明细行sid")
    @TableField(exist = false)
    List<Long> itemSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：处理状态")
    private String[] handleStatusList;

    @TableField(exist = false)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String  creatorAccountName;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    @ApiModelProperty(value = "供方编码（物料/商品/服务）")
    private String supplierProductCode;

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

    @Excel(name = "当前所属阶段", dictType = "s_baoheyi_stage")
    @ApiModelProperty(value = "当前所属阶段（单选）")
    private String currentStage;

    @ApiModelProperty(value = "阶段（多选）")
    private String[] stageList;

    @ApiModelProperty(value = "当前所属阶段（多选）")
    private String[] currentStageList;

    @ApiModelProperty(value = "建单所属阶段（单选）")
    private String createdStage;

    @ApiModelProperty(value = "建单所属阶段（多选）")
    private String[] createdStageList;

    @ApiModelProperty(value = "查询页面的阶段")
    private String stage;

    @ApiModelProperty(value = "查询：供应商")
    private Long[] vendorSidList;

    @ApiModelProperty(value = "查询：公司")
    private Long[] companySidList;

    @ApiModelProperty(value = "查询：产品季")
    private Long[] productSeasonSidList;

    @ApiModelProperty(value = "查询：采购模式")
    private String[] purchaseModeList;

    @ApiModelProperty(value = "查询：采购组")
    private String[] purchaseGroupList;

    @ApiModelProperty(value = "查询：物料类型")
    private String[] materialTypeList;

    @ApiModelProperty(value = "处理状态（数据字典的键值）")
    private String[] handleStatuseList;

    @ApiModelProperty(value = "甲供料方式（数据字典的键值）")
    private String[] rawMaterialModeList;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "询价日期")
    private Date dateRequest;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "报价日期")
    private Date dateQuote;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "核价日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "核价日期")
    private Date dateCheck;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "议价日期")
    private Date dateConfirm;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "报价计划截至日期")
    private Date quotepriceDeadline ;

    @ApiModelProperty(value = "报价员（用户名称）")
    private String quoter;

    @ApiModelProperty(value = "报价员电话")
    private String quoterTelephone;

    @ApiModelProperty(value = "报价员邮箱")
    private String quoterEmail;

    @ApiModelProperty(value = "核价员（用户账号）")
    private String checker;

    @ApiModelProperty(value = "核价员（用户账号）")
    private String[] checkerList;

    @ApiModelProperty(value = "核价员（用户账号）")
    private String checkerName;

    @ApiModelProperty(value = "采购员（用户账号）")
    private String[] buyerList;

    @ApiModelProperty(value = "行号")
    private Long itemNum;

    @ApiModelProperty(value = "当前审批人ID（多选）")
    private String[] approvalUserIdList;
}
