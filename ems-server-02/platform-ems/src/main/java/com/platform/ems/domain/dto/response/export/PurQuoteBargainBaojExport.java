package com.platform.ems.domain.dto.response.export;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 采购报价单查询导出返回类
 *
 * @author chenkw
 * @date 2023-06-06
 */
@Data
public class PurQuoteBargainBaojExport {

    @Excel(name = "采购报价单号")
    @ApiModelProperty(value = "采购报价单号")
    private String quoteBargainCode;

    @Excel(name = "供应商")
    @ApiModelProperty(value = "供应商")
    private String vendorName;

    @Excel(name = "物料/商品编码")
    @ApiModelProperty(value = "物料/商品编码")
    private String materialCode;

    @Excel(name = "物料/商品名称")
    @ApiModelProperty(value = "物料/商品名称")
    private String materialName;

    @Excel(name = "SKU1名称")
    @ApiModelProperty(value = "SKU1类型")
    private String sku1Name;

    @Excel(name = "供方编码")
    @ApiModelProperty(value = "供方编码（物料/商品/服务）")
    private String supplierProductCode;

    @Excel(name = "有效期(起)", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "有效期（起）")
    private Date startDate;

    @Excel(name = "有效期(至)", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "有效期（至）")
    private Date endDate;

    @Excel(name = "报价(含税)")
    @ApiModelProperty(value = "报价(含税)")
    private BigDecimal quotePriceTax;

    @Excel(name = "采购价单位")
    @ApiModelProperty(value = "采购价单位名称")
    private String unitPriceName;

    @Excel(name = "基本计量单位")
    @ApiModelProperty(value = "基本计量单位名称")
    private String unitBaseName;

    @Excel(name = "单位换算比例")
    @ApiModelProperty(value = "单位换算比例")
    private BigDecimal unitConversionRate;

    @Excel(name = "税率")
    @ApiModelProperty(value = "税率名称")
    private BigDecimal taxRate;

    @Excel(name = "价格维度", dictType = "s_price_dimension")
    @ApiModelProperty(value = "价格维度")
    private String priceDimension;

    @Excel(name = "是否递增减价", dictType = "sys_yes_no")
    @ApiModelProperty(value = "是否递增减价")
    private String isRecursionPrice;

    @Excel(name = "基准量", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "基准量")
    private BigDecimal referQuantity;

    @Excel(name = "递增量", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "递增量")
    private BigDecimal increQuantity;

    @Excel(name = "递增报价(含税)", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "递增报价(含税)")
    private BigDecimal increQuoPriceTax;

    @Excel(name = "递减量", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "递减量")
    private BigDecimal decreQuantity;

    @Excel(name = "递减报价(含税)", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "递减报价(含税)")
    private BigDecimal decreQuoPriceTax;

    @Excel(name = "价格最小起算量", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "价格最小起算量")
    private BigDecimal priceMinQuantity;

    @Excel(name = "公司")
    @ApiModelProperty(value = "公司")
    private String companyName;

    @Excel(name = "下单季")
    @ApiModelProperty(value = "下单季")
    private String productSeasonName;

    @Excel(name = "采购员")
    @ApiModelProperty(value = "采购员")
    private String buyerName;

    @Excel(name = "报价员")
    @ApiModelProperty(value = "报价员")
    private String quoter;

    @Excel(name = "报价日期", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "报价日期")
    private Date dateQuote;

    @Excel(name = "采购组")
    @ApiModelProperty(value = "采购组名称")
    private String purchaseGroupName;

    @Excel(name = "物料类型")
    @ApiModelProperty(value = "物料类型")
    private String materialTypeName;

    @Excel(name = "采购模式", dictType = "s_price_type")
    @ApiModelProperty(value = "采购模式（数据字典的键值）")
    private String purchaseMode;

    @Excel(name = "报价员电话")
    @ApiModelProperty(value = "报价员电话")
    private String quoterTelephone;

    @Excel(name = "报价员邮箱")
    @ApiModelProperty(value = "报价员邮箱")
    private String quoterEmail;

    @Excel(name = "询价单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "询价单号")
    private Long inquiryCode;

    @Excel(name = "报价计划截至日期", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "报价计划截至日期")
    private Date quotepriceDeadline;

    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @Excel(name = "当前审批节点")
    @ApiModelProperty(value = "当前审批节点名称")
    private String approvalNode;

    @Excel(name = "当前审批人")
    @ApiModelProperty(value = "当前审批人")
    private String approvalUserName;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remarkQuote;

    @Excel(name = "行号")
    @ApiModelProperty(value = "行号")
    private Long itemNum;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人")
    private String creatorAccountName;

    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @Excel(name = "更改人")
    @ApiModelProperty(value = "更改人")
    private String updaterAccountName;

    @Excel(name = "更改日期", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "更改时间")
    private Date updateDate;

}
