package com.platform.ems.domain.dto.response.export;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 已逾期/即将到期-销售订单对象 s_rep_business_remind_so
 *
 * @author linhongwei
 * @date 2022-02-24
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_rep_business_remind_so")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RepBusinessRemindJJDQSo {


    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "合同交期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "合同交期")
    private Date contractDate;

    @Excel(name = "商品编码")
    @ApiModelProperty(value = "物料/商品编码")
    private String materialCode;

    @ApiModelProperty(value = "商品名称")
    @TableField(exist = false)
    @Excel(name = "商品名称")
    private String materialName;

    @ApiModelProperty(value = "颜色")
    @TableField(exist = false)
    @Excel(name = "颜色")
    private String sku1Name;

    @ApiModelProperty(value = "尺码")
    @TableField(exist = false)
    @Excel(name = "尺码")
    private String sku2Name;

    @Excel(name = "客户")
    @ApiModelProperty(value = "客户简称")
    private String customerShortName;


    @Excel(name = "销售订单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售订单号")
    private Long salesOrderCode;

    /**
     * 订单量
     */
    @Excel(name = "订单量")
    @ApiModelProperty(value = "订单量")
    private BigDecimal quantityDingd;


    @ApiModelProperty(value = "待出库")
    @TableField(exist = false)
    @Excel(name = "待出库量")
    private BigDecimal sumQuantityDck;

    @ApiModelProperty(value = "已出库")
    @TableField(exist = false)
    @Excel(name = "已出库量")
    private BigDecimal sumQuantityYck;



    @ApiModelProperty(value = "下单季")
    @TableField(exist = false)
    @Excel(name = "下单季")
    private String productSeasonName;

    @ApiModelProperty(value = "单据类型")
    @Excel(name = "单据类型")
    private String documentTypeName;

    @ApiModelProperty(value = "业务类型")
    @Excel(name = "业务类型")
    private String businessTypeName;

    @Excel(name = "公司")
    @ApiModelProperty(value = "公司简称")
    private String companyName;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售合同号")
    @Excel(name = "销售合同号")
    private String saleContractCode;

    @ApiModelProperty(value = "销售模式")
    @TableField(exist = false)
    @Excel(name = "销售模式",dictType = "s_price_type")
    private String saleMode;

    @ApiModelProperty(value = "销售渠道")
    @TableField(exist = false)
    @Excel(name = "销售渠道")
    private String businessChannelName;

    @ApiModelProperty(value = "行号")
    @TableField(exist = false)
    @Excel(name = "行号")
    private String itemNum;

    @ApiModelProperty(value = "即将到期提醒天数")
    @TableField(exist = false)
    @Excel(name = "到期提醒天数")
    private Long toexpireDays;

    @ApiModelProperty(value = "商品sku条码")
    @TableField(exist = false)
    @Excel(name = "商品sku条码")
    private String barcode;


    @ApiModelProperty(value = "销售员")
    @TableField(exist = false)
    @Excel(name = "销售员")
    private String salePersonName;
}
