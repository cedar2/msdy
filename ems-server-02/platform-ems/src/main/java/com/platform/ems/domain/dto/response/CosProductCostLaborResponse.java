package com.platform.ems.domain.dto.response;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 *

 * 商品成本工价明细报表
 */
@Data
@Accessors( chain = true)
public class CosProductCostLaborResponse {

    @ApiModelProperty(value = "类型")
    @Excel(name = "类型")
    private String laborTypeName;


    @Excel(name = "工价/费用项")
    @ApiModelProperty(value = "工价/费用项")
    private String laborTypeItemName;

    @Excel(name = "风险系数(%)")
    @ApiModelProperty(value = "风险系数(%)")
    private BigDecimal confirmFengxianRate;

    @Excel(name = "服务费率(%)")
    @ApiModelProperty(value = "服务费率(%)")
    private BigDecimal confirmFuwuRate;

    @Excel(name = "税率")
    @ApiModelProperty(value = "税率")
    private BigDecimal taxRate;


    @Excel(name = "成本价")
    @ApiModelProperty(value = "成本价")
    private BigDecimal innerPriceTax;

    @Excel(name = "报价")
    @ApiModelProperty(value = "报价(含税)")
    private BigDecimal quotePriceTax;

    @Excel(name = "核价")
    @ApiModelProperty(value = "核价")
    private BigDecimal checkPriceTax;

    @Excel(name = "确认价")
    @ApiModelProperty(value = "确认价")
    private BigDecimal confirmPriceTax;

    @Excel(name = "报价备注")
    @ApiModelProperty(value = "报价备注")
    private String remarkQuote;

    @Excel(name = "确认价备注")
    @ApiModelProperty(value = "确认价备注")
    private String remarkConfirm;

    @ApiModelProperty(value = "备注")
    @Excel(name = "备注")
    private String remark;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人名称")
    private String creatorAccountName;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @Excel(name = "更改人")
    @ApiModelProperty(value = "更改人名称")
    private String updaterAccountName;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "更改日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;


}
