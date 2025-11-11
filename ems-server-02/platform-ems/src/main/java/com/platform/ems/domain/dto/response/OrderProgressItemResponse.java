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

/**
 * 交期状况报表明细
 *
 * @author yangqz
 * @date
 */
@Data
@ApiModel
@Accessors(chain = true)
public class OrderProgressItemResponse {

    @ApiModelProperty(value = "下单季")
    @Excel(name = "下单季")
    private String  productSeasonName;

    @ApiModelProperty(value = "客户")
    @Excel(name = "客户")
    private String customerName;

    @ApiModelProperty(value = "供应商")
    private String vendorName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "合同交期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "合同交期")
    private Date contractDate;

    @ApiModelProperty(value = "商品编码")
    @Excel(name = "商品编码")
    private String materialCode;

    @ApiModelProperty(value = "商品名称")
    @Excel(name = "商品名称")
    private String materialName;

    @ApiModelProperty(value = "主图片路径")
    private String picturePath;

    @ApiModelProperty(value = "sku1名称")
    @Excel(name = "颜色")
    private String sku1Name;

    @ApiModelProperty(value = "sku2名称")
    @Excel(name = "尺码")
    private String sku2Name;

    @Excel(name = "已逾期数量")
    @ApiModelProperty(value = "已逾期数量")
    private BigDecimal yyqQuantity;

    @Excel(name = "已逾期金额（万）")
    @ApiModelProperty(value = "已逾期金额")
    private BigDecimal yyqPriceTax;

    @Excel(name = "即将到期数量")
    @ApiModelProperty(value = "即将到期数量")
    private BigDecimal jjdqQuantity;

    @Excel(name = "即将到期金额（万）")
    @ApiModelProperty(value = "即将到期金额")
    private BigDecimal jjdqPriceTax;


    @TableField(exist = false)
    private String firstSort;

    @TableField(exist = false)
    private String secondSort;

    @TableField(exist = false)
    private String thirdSort;
}
