package com.platform.ems.domain.dto.response;

import com.baomidou.mybatisplus.annotation.TableField;
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
 * 采购价导出响应实体
 *
 * @author yangqz
 * @date 2021-7-12
 */
@Data
@ApiModel
@Accessors(chain = true)
public class purPurchaseExResponse implements Serializable {

    @ApiModelProperty(value = "物料采购价信息编码")
    @Excel(name = "采购价信息编号")
    private String purchasePriceCode;

    @Excel(name = "供应商")
    @ApiModelProperty(value = "查询：供应商名称")
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

    @Excel(name = "规格尺寸")
    @ApiModelProperty(value = "物料档案规格尺寸")
    private String specificationSize;

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

    @Excel(name = "报价(含税)")
    @ApiModelProperty(value = "报价(含税)")
    private BigDecimal quotePriceTax;

    @Excel(name = "核价(含税)")
    @ApiModelProperty(value = "核价(含税)")
    private BigDecimal checkPriceTax;

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
    private BigDecimal taxRateName;

    @Excel(name = "价格维度", dictType = "s_price_dimension")
    @ApiModelProperty(value = "价格维度")
    private String priceDimension;

    @ApiModelProperty(value = "是否递增减价")
    @Excel(name = "是否递增减价", dictType = "sys_yes_no")
    private String isRecursionPrice;

    @Excel(name = "递增减SKU类型", dictType = "s_sku_type")
    @ApiModelProperty(value = "递增减SKU类型")
    private String skuTypeRecursion;

    @Excel(name = "取整方式(递增减)", dictType = "s_rounding_type")
    @ApiModelProperty(value = "取整方式(递增减)")
    private String roundingType;

    @Excel(name = "基准量")
    @ApiModelProperty(value = "基准量")
    private BigDecimal referQuantity;

    @Excel(name = "递增量")
    @ApiModelProperty(value = "递增量")
    private BigDecimal increQuantity;

    @Excel(name = "递增价(含税)")
    @ApiModelProperty(value = "递增采购价(含税)")
    private BigDecimal increPurPriceTax;

    @ApiModelProperty(value = "递减量")
    @Excel(name = "递减量")
    private BigDecimal decreQuantity;

    @ApiModelProperty(value = "递减采购价(含税)")
    @Excel(name = "递减价(含税)")
    private BigDecimal decPurPriceTax;

    @Excel(name = "递增减计量单位")
    @ApiModelProperty(value = "递增减计量单位")
    private String unitRecursionName;
    ;

    @ApiModelProperty(value = "价格最小起算量")
    @Excel(name = "价格最小起算量")
    private BigDecimal priceMinQuantity;

    @Excel(name = "处理状态(明细)", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @ApiModelProperty(value = "变更说明")
    @Excel(name = "变更说明")
    private String updateRemark;

    @ApiModelProperty(value = "备注")
    @Excel(name = "备注")
    private String remark;

    @Excel(name = "价格说明")
    @ApiModelProperty(value = "价格说明")
    private String priceRemark;

    @ApiModelProperty(value = "当前审批节点名称")
    @Excel(name = "当前审批节点")
    private String approvalNode;

    /**
     * 当前审批人
     */
    @ApiModelProperty(value = "当前审批人")
    @Excel(name = "当前审批人")
    private String approvalUserName;

    @Excel(name = "甲供料方式", dictType = "s_raw_material_mode")
    @ApiModelProperty(value = "甲供料方式")
    private String rawMaterialMode;

    @Excel(name = "采购模式", dictType = "s_price_type")
    @ApiModelProperty(value = "采购模式")
    private String purchaseMode;

    @ApiModelProperty(value = "采购组织编码")
    @Excel(name = "采购组织", dictType = "s_purchase_org")
    private String purchaseOrg;

    @ApiModelProperty(value = "物料类型名称（物料/商品/服务）")
    @Excel(name = "物料类型")
    private String materialTypeName;

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

    @Excel(name = "处理状态(主表)", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String headHandleStatus;

}
