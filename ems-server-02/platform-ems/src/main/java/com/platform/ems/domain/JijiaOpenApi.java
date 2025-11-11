package com.platform.ems.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 积加测试实体
 *
 * @author chenkw
 * @date 2023-03-02
 */
@Data
@Accessors(chain = true)
@ApiModel
public class JijiaOpenApi {

    @ApiModelProperty(value = "SKU")
    private String[] skuList;

    @ApiModelProperty(value = "单据状态")
    private String[] invoicesStatusList;

    @ApiModelProperty(value = "sku")
    private String sku;

    @ApiModelProperty(value = "产品名称")
    private String skuName;

    @ApiModelProperty(value = "MSKU")
    private String msku;

    @ApiModelProperty(value = "MSKUS")
    private String[] mskus;

    @ApiModelProperty(value = "店铺站点名称")
    private String arrivalMarketName;

    @ApiModelProperty(value = "商品列表的店铺站点名称")
    private String marketName;

    @ApiModelProperty(value = "采购订单编号")
    private String code;

    @ApiModelProperty(value = "采购数量")
    private String orderQuantity;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "采购订单创建时间")
    private Date createdAt;

    @ApiModelProperty(value = "采购订单创建人")
    private String creator;

    @ApiModelProperty(value = "目的仓名称")
    private String arrivalWarehouseName;

    @ApiModelProperty(value = "交货仓名称")
    private String deliveryWarehouseName;

    @ApiModelProperty(value = "页码")
    private Integer page;

    @ApiModelProperty(value = "当页数据条数")
    private Integer pagesize;

    @ApiModelProperty(value = "SKU")
    private String[] skus;

    @ApiModelProperty(value = "类型：固定传值 Receipts：接收")
    private String[] eventTypes;

    @ApiModelProperty(value = "仓库名称")
    private String warehouseName;

    @ApiModelProperty(value = "货件ID")
    private String referenceId;

    @ApiModelProperty(value = "收货数量")
    private String quantity;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "收货日期")
    private Date reportDate;

    /**
     * "model": {
     *         "beginReportDate": "2022-12-19",
     *         "endReportDate": "2023-01-18"
     *     },
     */
    @ApiModelProperty(value = "收货通知接口必填")
    private Object model;

    /**
     * "sort": "addDate",
     */
    @ApiModelProperty(value = "商品信息列表接口必填 排序字段")
    private String sort;

    /**
     * "order": "ascend"
     */
    @ApiModelProperty(value = "商品信息列表接口必填 排序类型")
    private String order;

}
