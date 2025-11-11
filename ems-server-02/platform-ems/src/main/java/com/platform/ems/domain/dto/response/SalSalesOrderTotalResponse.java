package com.platform.ems.domain.dto.response;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 销售订单明细汇总
 *
 * @author yangqz
 * @date 2021-7-12
 */
@Data
@ApiModel
@Accessors(chain = true)
public class SalSalesOrderTotalResponse {

    @ApiModelProperty(value = "物料档案编码")
    @Excel(name = "商品编码")
    private String materialCode ;

    @ApiModelProperty(value = "商品名称")
    @Excel(name = "商品名称")
    private String materialName;

    @Excel(name = "sku1(颜色)")
    @ApiModelProperty(value = "sku1名称")
    @TableField(exist = false)
    private String sku1Name;

    @Excel(name = "销售价(含税)")
    @ApiModelProperty(value = "销售价(含税)")
    private BigDecimal salePriceTax;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "合同交期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "合同交期")
    private Date contractDate;

    @ApiModelProperty(value = "数量小计")
    @TableField(exist = false)
    private BigDecimal sumQuantity;

    @ApiModelProperty(value = "金额小计")
    @TableField(exist = false)
    private BigDecimal sumMoneyAmount;

    @ApiModelProperty(value = "价格单位名称")
    private String unitPriceName;

    @ApiModelProperty(value = "计量单位名称")
    private String unitBaseName;

    @TableField(exist = false)
    @ApiModelProperty(value = "尺码组名称")
    private String sku2GroupName;

    @ApiModelProperty(value = "sku1的序号")
    private BigDecimal sort1;

    @ApiModelProperty(value = "sku2的序号")
    private BigDecimal sort2;

    @ApiModelProperty(value = "单位换算比例（价格单位/基本单位）")
    private BigDecimal unitConversionRate;

    @ApiModelProperty(value = "sku2汇总")
    List<SalSalesOrderTotalSku2Response> sku2TotalList;
}
