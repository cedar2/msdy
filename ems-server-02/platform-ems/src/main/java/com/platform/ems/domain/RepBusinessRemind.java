package com.platform.ems.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 看板数据对象
 *
 * @author chenkw
 * @date 2022-04-26
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RepBusinessRemind {

    /***********************已逾期业务***********************/

    @ApiModelProperty(value = "已逾期业务-销售订单-订单数")
    private String yyqSalOrderDds;

    @ApiModelProperty(value = "已逾期业务-销售订单-款数")
    private String yyqSalOrderKs;

    @ApiModelProperty(value = "已逾期业务-销售订单-未发货量")
    private String yyqSalOrderWfhl;

    @ApiModelProperty(value = "已逾期业务-采购订单-订单数")
    private String yyqPurOrderDds;

    @ApiModelProperty(value = "已逾期业务-采购订单-款数")
    private String yyqPurOrderKs;

    @ApiModelProperty(value = "已逾期业务-采购订单-未交货量")
    private String yyqPurOrderWjhl;

    @ApiModelProperty(value = "已逾期业务-生产订单-订单数")
    private String yyqManOrderDds;

    @ApiModelProperty(value = "已逾期业务-生产订单-款数")
    private String yyqManOrderKs;

    @ApiModelProperty(value = "已逾期业务-生产订单-未完成量")
    private String yyqManOrderWwcl;

    /***********************即将到期业务***********************/

    @ApiModelProperty(value = "即将到期业务-销售订单-订单数")
    private String jjdqSalOrderDds;

    @ApiModelProperty(value = "即将到期业务-销售订单-款数")
    private String jjdqSalOrderKs;

    @ApiModelProperty(value = "即将到期业务-销售订单-待发货量")
    private String jjdqSalOrderDfhl;

    @ApiModelProperty(value = "即将到期业务-采购订单-订单数")
    private String jjdqPurOrderDds;

    @ApiModelProperty(value = "即将到期业务-采购订单-款数")
    private String jjdqPurOrderKs;

    @ApiModelProperty(value = "即将到期业务-采购订单-待交货量")
    private String jjdqPurOrderDjhl;

    @ApiModelProperty(value = "即将到期业务-生产订单-订单数")
    private String jjdqManOrderDds;

    @ApiModelProperty(value = "即将到期业务-生产订单-款数")
    private String jjdqManOrderKs;

    @ApiModelProperty(value = "即将到期业务-生产订单-待完成量")
    private String jjdqManOrderDwcl;

    /***********************财务状况***********************/

    @ApiModelProperty(value = "应收")
    private String typeYings;

    @ApiModelProperty(value = "待预收金额(万)")
    private String daiys;

    @ApiModelProperty(value = "应收金额(万)")
    private String yings;

    @ApiModelProperty(value = "待销已收金额(万)")
    private String daixys;

    @ApiModelProperty(value = "应付")
    private String typeYingf;

    @ApiModelProperty(value = "待预付金额(万)")
    private String daiyf;

    @ApiModelProperty(value = "应付金额(万)")
    private String yingf;

    @ApiModelProperty(value = "待销已付金额(万)")
    private String daixyf;

    @ApiModelProperty(value = "资金")
    private String fund;

    @ApiModelProperty(value = "存款账户(万)")
    private String fundTypeCk;

    @ApiModelProperty(value = "现金账户(万)")
    private String fundTypeXj;

    @ApiModelProperty(value = "汇票及其他账户(万)")
    private String fundTypeOther;

    @ApiModelProperty(value = "资产")
    private String asset;

    @ApiModelProperty(value = "房产资产(万)")
    private String assetTypeFc;

    @ApiModelProperty(value = "设备资产(万)")
    private String assetTypeSb;

    @ApiModelProperty(value = "其他资产(万)")
    private String assetTypeOther;


}
