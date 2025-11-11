package com.platform.ems.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 采购议价单导出响应实体
 *
 * @author yangqz
 * @date 2021-7-12
 */
@Data
@ApiModel
@Accessors(chain = true)
public class PurQuoteBargainExportResponse implements Serializable {

    @Excel(name = "采购议价单号")
    @ApiModelProperty(value = "询报议价单号")
    private String quoteBargainCode;

    @Excel(name = "供应商")
    private String vendorName;

    @ApiModelProperty(value = "查询：物料编码")
    @Excel(name = "物料/商品编码")
    private String materialCode;

    @ApiModelProperty(value = "查询：物料名称")
    @Excel(name = "物料/商品名称")
    private String materialName;

    @Excel(name = "SKU1名称")
    @ApiModelProperty(value = "SKU1类型")
    private String sku1Name;

    @Excel(name = "供方编码")
    @ApiModelProperty(value = "供方编码（物料/商品/服务）")
    private String supplierProductCode;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "有效期(起)", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "有效期（起）")
    @NotEmpty(message = "有效期（起）不能为空")
    private Date startDate;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "有效期(至)", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "有效期（至）")
    @NotEmpty(message = "有效期（至）不能为空")
    private Date endDate;

    @Excel(name = "采购价(含税)")
    @ApiModelProperty(value = "采购价(含税)")
    private BigDecimal purchasePriceTax;

    @Excel(name = "核定价(含税)")
    @ApiModelProperty(value = "核定价(含税)")
    private BigDecimal checkPriceTax;

    @Excel(name = "报价(含税)")
    @ApiModelProperty(value = "报价(含税)")
    private BigDecimal quotePriceTax;

    @ApiModelProperty(value = "采购价单位名称")
    @Excel(name = "采购价单位")
    private String unitPriceName;

    @Excel(name = "基本计量单位")
    @ApiModelProperty(value = "基本计量单位名称")
    private String unitBaseName;

    @Excel(name = "单位换算比例")
    @ApiModelProperty(value = "单位换算比例")
    private BigDecimal unitConversionRate;

    @ApiModelProperty(value = "税率名称")
    @Excel(name = "税率")
    private BigDecimal taxRate;

    @Excel(name = "价格维度",dictType = "s_price_dimension")
    @ApiModelProperty(value = "价格维度")
    private String priceDimension;

    @ApiModelProperty(value = "是否递增减价")
    @Excel(name = "是否递增减价",dictType = "sys_yes_no")
    private String isRecursionPrice;

    @Excel(name = "基准量")
    @ApiModelProperty(value = "基准量")
    private BigDecimal referQuantity;

    @Excel(name = "递增量")
    @ApiModelProperty(value = "递增量")
    private BigDecimal increQuantity;

    @Excel(name = "递增采购价(含税)")
    @ApiModelProperty(value = "递增采购价(含税)")
    private BigDecimal increPurPriceTax;

    @Excel(name = "递增核定价(含税)")
    @ApiModelProperty(value = "递增核定价(含税)")
    private BigDecimal increChePriceTax;

    @Excel(name = "递增报价(含税)")
    @ApiModelProperty(value = "递增报价(含税)")
    private BigDecimal increQuoPriceTax;

    @ApiModelProperty(value = "递减量")
    @Excel(name = "递减量")
    private BigDecimal decreQuantity;

    @ApiModelProperty(value = "递减采购价(含税)")
    @Excel(name = "递减采购价(含税)")
    private BigDecimal decrePurPriceTax;

    @Excel(name = "递减核定价(含税)")
    @ApiModelProperty(value = "递减核定价(含税)")
    private BigDecimal decreChePriceTax;

    @Excel(name = "递减报价(含税)")
    @ApiModelProperty(value = "递减报价(含税)")
    private BigDecimal decreQuoPriceTax;

    @ApiModelProperty(value = "价格最小起算量")
    @Excel(name = "价格最小起算量")
    private BigDecimal priceMinQuantity;

    @Excel(name = "产品季")
    @ApiModelProperty(value = "产品季名称")
    private String productSeasonName;

    @Excel(name = "采购员")
    private String buyerName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "议价日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "议价日期")
    private Date dateConfirm;

    @Excel(name = "核价日期", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "核价日期")
    private Date dateCheck;

    @Excel(name = "报价日期", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "报价日期")
    private Date dateQuote;

    @Excel(name = "采购组")
    @ApiModelProperty(value = "采购组名称")
    private String purchaseGroupName;

    @ApiModelProperty(value = "物料类型名称（物料/商品/服务）")
    @Excel(name = "物料类型")
    private String materialTypeName;

    @Excel(name = "甲供料方式",dictType = "s_raw_material_mode")
    @ApiModelProperty(value = "客供料方式")
    private String rawMaterialMode;

    @Excel(name = "采购模式",dictType = "s_price_type")
    @ApiModelProperty(value = "采购模式（数据字典的键值）")
    private String purchaseMode;

    @Excel(name = "采购员电话")
    @ApiModelProperty(value = "采购员电话")
    private String buyerTelephone;

    @Excel(name = "采购员邮箱")
    @ApiModelProperty(value = "采购员邮箱")
    private String buyerEmail;

    @Excel(name = "报价员")
    @ApiModelProperty(value = "报价员")
    private String quoter;

    @Excel(name = "报价员电话")
    @ApiModelProperty(value = "报价员电话")
    private String quoterTelephone;

    @Excel(name = "报价员邮箱")
    @ApiModelProperty(value = "报价员邮箱")
    private String quoterEmail;

    @ApiModelProperty(value = "当前审批节点名称")
    @Excel(name = "当前审批节点")
    private String approvalNode;

    /** 当前审批人 */
    @ApiModelProperty(value = "当前审批人")
    @Excel(name = "当前审批人")
    private String approvalUserName;

    @Excel(name = "处理状态",dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccountName;

    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @Excel(name = "更改人")
    @ApiModelProperty(value = "更新人")
    private String updaterAccountName;

    @Excel(name = "更改日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;
}
