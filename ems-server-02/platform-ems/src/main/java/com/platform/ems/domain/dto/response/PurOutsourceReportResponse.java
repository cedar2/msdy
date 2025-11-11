package com.platform.ems.domain.dto.response;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class PurOutsourceReportResponse {

    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-加工采购价明细")
    private Long outsourcePurchasePriceItemSid;

    @ApiModelProperty(value = "加工采购价编码")
    private String outsourcePurchasePriceCode;

    @Excel(name = "系统SID-加工采购价")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-加工采购价")
    private Long outsourcePurchasePriceSid;

    @ApiModelProperty(value = "物料编码")
    @Excel(name = "商品编码")
    private String materialCode;

    @ApiModelProperty(value = "物料名称")
    @Excel(name = "商品名称")
    private String materialName;

    @ApiModelProperty(value = "商品客户Sid")
    private String materialCustomerSid;

    @ApiModelProperty(value = "商品客户")
    private String materialCustomerName;


    @ApiModelProperty(value = "sku1名称")
    @Excel(name = "sku1名称")
    private String sku1Name;

    /** 价格维度 */
    @ApiModelProperty(value = "价格维度")
    @Excel(name = "价格维度",dictType = "s_price_dimension")
    private String priceDimension;

    @Excel(name = "部位说明")
    @ApiModelProperty(value = "部位说明")
    private String positionDesc;

    @Excel(name = "加工项")
    @ApiModelProperty(value = "加工项")
    private String processName;

    @ApiModelProperty(value = "供应商名称")
    @Excel(name = "供应商")
    private String vendorName;

    /**
     * 有效期（起）
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "有效期（起）", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "有效期（起）")
    @NotEmpty(message = "有效期（起）不能为空")
    private Date startDate;

    /**
     * 有效期（止）
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "有效期（至）", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "有效期（至）")
    @NotEmpty(message = "有效期（至）不能为空")
    private Date endDate;

    /**
     * 采购价(含税)
     */
    @ApiModelProperty(value = "采购价(含税)")
    @Digits(integer=7,fraction = 3,message = "采购价(含税)整数位上限为7位，小数位上限为3位")
    private BigDecimal purchasePriceTax;

    @Excel(name = "采购价(含税)")
    @ApiModelProperty(value = "采购价(含税)")
    @Digits(integer=7,fraction = 3,message = "采购价(含税)整数位上限为7位，小数位上限为3位")
    private String purchasePriceTaxS;

    @ApiModelProperty(value = "采购价(不含税)")
    @Digits(integer=7,fraction = 3,message = "采购价(不含税)整数位上限为7位，小数位上限为3位")
    private BigDecimal purchasePrice;

    @Excel(name = "采购价(不含税)")
    @ApiModelProperty(value = "采购价(不含税)")
    @Digits(integer=7,fraction = 3,message = "采购价(不含税)整数位上限为7位，小数位上限为3位")
    private String purchasePriceS;

    @Excel(name = "内部核算价(含税)")
    @ApiModelProperty(value = "内部核算价(含税)")
    private BigDecimal innerCheckPriceTax;

    @Excel(name = "内部核算价(不含税)")
    @ApiModelProperty(value = "内部核算价(不含税)")
    private BigDecimal innerCheckPrice;

    @ApiModelProperty(value = "报价(含税)")
    private BigDecimal quotePriceTax;

    @ApiModelProperty(value = "价格说明")
    private String priceRemark;

    @ApiModelProperty(value = "税率名称")
    @Excel(name = "税率")
    private BigDecimal taxRateName;

    @Excel(name = "基本计量单位")
    @ApiModelProperty(value = "基本计量单位名称")
    private String unitBaseName;

    @ApiModelProperty(value = "采购价单位名称")
    @Excel(name = "采购价格单位")
    private String unitPriceName;

    @ApiModelProperty(value = "单位换算比例")
    private BigDecimal unitConversionRate;

    @ApiModelProperty(value = "单位换算比例")
    @Excel(name = "单位换算比例")
    private String unitConversionRateS;

    @Excel(name = "币种",dictType ="s_currency")
    @NotBlank(message = "币种不能为空")
    @ApiModelProperty(value = "币种")
    private String currency;

    @Excel(name = "货币单位",dictType ="s_currency_unit")
    @ApiModelProperty(value = "货币单位")
    private String currencyUnit;

    /** 价格录入方式 */
    @Excel(name = "价格录入方式",dictType = "s_price_enter_mode")
    @ApiModelProperty(value = "价格录入方式")
    private String priceEnterMode;

    @ApiModelProperty(value = "公司名称")
    @Excel(name = "公司")
    private String companyName;

    @ApiModelProperty(value = "备注")
    @Excel(name = "备注")
    private String remark;

    /** 处理状态 */
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @ApiModelProperty(value = "主表处理状态")
    private String headHandleStatus;

    @ApiModelProperty(value = "创建者")
    @Excel(name = "创建人")
    private String creatorAccountName;

    @ApiModelProperty(value = "商品季节SID")
    private String  materialProductSeasonSid;

    @ApiModelProperty(value = "商品季节")
    @Excel(name = "产品季(商品)")
    private String  materialProductSeasonName;

    @ApiModelProperty(value = "商品客户简称")
    @Excel(name = "客户(商品)")
    private String materialShortName;

    /** 创建时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    private Date createDate;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date updateDate;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "查询:加工项")
    @TableField(exist = false)
    private Long[] processSidList;

    @ApiModelProperty(value = "查询：供应商")
    @TableField(exist = false)
    private Long[] vendorSidList;

    @ApiModelProperty(value = "查询：公司")
    @TableField(exist = false)
    private Long[] companySidList;


    @TableField(exist = false)
    @ApiModelProperty(value = "查询：处理状态")
    private String[]  handleStatusList;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：商品季节")
    private Long[] materialProductSeasonSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：商品客户Sid")
    private Long[] materialCustomerSidList;


    /*
     * 创建人账号（用户名称）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    /**
     * 阶梯类型（数据字典的键值）
     */
    @ApiModelProperty(value = "阶梯类型（数据字典的键值）")
    private String cascadeType;


    @ApiModelProperty(value = "查询：创建时间起")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @TableField(exist = false)
    private Date beginTime;

    @ApiModelProperty(value = "查询：创建时间至")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @TableField(exist = false)
    private Date endTime;

    @ApiModelProperty(value ="每页个数")
    @TableField(exist = false)
    private Integer pageNum;

    @ApiModelProperty(value ="每页个数")
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

    @ApiModelProperty(value = "工艺说明")
    private String  processDesc;

    @ApiModelProperty(value = "工艺图片")
    private String  processPicture;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料商品主图片路径")
    private String picturePath;

}
