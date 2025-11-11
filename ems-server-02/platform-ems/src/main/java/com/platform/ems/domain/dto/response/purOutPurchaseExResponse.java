package com.platform.ems.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
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
 * 加工采购价导出响应实体
 *
 * @author yangqz
 * @date 2021-7-12
 */
@Data
@ApiModel
@Accessors(chain = true)
public class purOutPurchaseExResponse implements Serializable {

    @Excel(name = "加工采购价信息编号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "加工采购价编码")
    private String outsourcePurchasePriceCode;

    @Excel(name = "加工商")
    @ApiModelProperty(value = "查询：供应商名称")
    private String vendorName;

    @ApiModelProperty(value = "查询：物料编码")
    @Excel(name = "商品编码")
    private String materialCode;

    @ApiModelProperty(value = "查询：物料名称")
    @Excel(name = "商品名称")
    private String materialName;

    @Excel(name = "sku1名称")
    @ApiModelProperty(value = "sku1名称")
    private String sku1Name;

    @ApiModelProperty(value = "加工项")
    @Excel(name = "加工项")
    private String processName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "有效期（起）", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "有效期（起）")
    @NotEmpty(message = "有效期（起）不能为空")
    private Date startDate;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "有效期（至）", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "有效期（至）")
    @NotEmpty(message = "有效期（至）不能为空")
    private Date endDate;

    @Excel(name = "采购价(含税)")
    @ApiModelProperty(value = "采购价(含税)")
    private BigDecimal purchasePriceTax;

    @Excel(name = "内部核算价(含税)")
    @ApiModelProperty(value = "内部核算价(含税)")
    private BigDecimal innerCheckPriceTax;

    @ApiModelProperty(value = "内部核算价(不含税)")
    private BigDecimal innerCheckPrice;

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
    private String unitConversionRateS;

    @ApiModelProperty(value = "税率名称")
    @Excel(name = "税率")
    private BigDecimal taxRateName;

    @Excel(name = "价格维度",dictType = "s_price_dimension")
    @ApiModelProperty(value = "价格维度")
    private String priceDimension;

    @Excel(name = "部位说明")
    @ApiModelProperty(value = "部位说明")
    private String positionDesc;

    @Excel(name = "工艺说明")
    @ApiModelProperty(value = "工艺说明")
    private String  processDesc;

    @Excel(name = "采购组")
    @ApiModelProperty(value = "采购组织（数据字典的键值）")
    private String purchaseOrg;

    @ApiModelProperty(value = "当前审批节点名称")
    @Excel(name = "当前审批节点")
    private String approvalNode;

    /** 当前审批人 */
    @ApiModelProperty(value = "当前审批人")
    @Excel(name = "当前审批人")
    private String approvalUserName;

    @Excel(name = "处理状态(明细)",dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @Excel(name = "备注")
    @ApiModelProperty(value ="备注")
    private String remark;

    @Excel(name = "价格说明")
    @ApiModelProperty(value = "价格说明")
    private String priceRemark;

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

    @Excel(name = "处理状态(主表)",dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String headHandleStatus;

    @ApiModelProperty(value = "商品季节")
    @Excel(name = "产品季(商品)")
    private String  materialProductSeasonName;

    @ApiModelProperty(value = "商品客户简称")
    @Excel(name = "客户(商品)")
    private String materialShortName;
}
