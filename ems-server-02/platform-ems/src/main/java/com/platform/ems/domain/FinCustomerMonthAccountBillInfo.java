package com.platform.ems.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.ems.util.data.KeepTwoDecimalsSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 客户月对账单对象 账单总览
 *
 * @author chenkw
 * @date 2021-09-22
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FinCustomerMonthAccountBillInfo {

    @Excel(name = "期初余额/上期余额金额")
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "月对账单-期初余额/上期余额金额")
    private BigDecimal yueQichu;

    @Excel(name = "本期到票")
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "月对账单-本期到票")
    private BigDecimal daopiaoBenqi;

    @Excel(name = "本期付款金额")
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "月对账单-本期付款金额")
    private BigDecimal fukuanBenqi;

    @Excel(name = "本期付款金额")
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "月对账单-本期收款金额")
    private BigDecimal shoukuanBenqi;

    @Excel(name = "本期销售抵扣金额")
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "月对账单-本期采购抵扣金额")
    private BigDecimal caigoudikouBenqi;

    @Excel(name = "本期扣款金额")
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "月对账单-本期扣款金额")
    private BigDecimal koukuanBenqi;

    @Excel(name = "本期调账金额")
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "月对账单-本期调账金额")
    private BigDecimal tiaozhangBenqi;

    @Excel(name = "期末余额/本期余额金额")
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "月对账单-期末余额/本期余额金额")
    private BigDecimal yueQimo;

    @Excel(name = "实际结欠余额金额")
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "月对账单-实际结欠余额金额")
    private BigDecimal yueShijijieqian;


    //=================================//


    @Excel(name = "应付暂估")
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "月对账单/台账-应付暂估")
    private BigDecimal yingfuzangu;

    @Excel(name = "应收暂估")
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "月对账单/台账-应收暂估")
    private BigDecimal yingshouzangu;

    @Excel(name = "押金金额")
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "月对账单/台账-押金金额")
    private BigDecimal yajin;

    @Excel(name = "暂押款金额")
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "月对账单/台账-暂押款金额")
    private BigDecimal zanyakuan;


    //=================================//


    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "台账-预收款金额")
    private BigDecimal yushoukuan;

    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "台账-应收款金额")
    private BigDecimal yingshoukuan;

    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "台账-特殊收款")
    private BigDecimal teshushoukuan;

    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "台账-扣款金额")
    private BigDecimal koukuan;

    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "台账-调账金额")
    private BigDecimal tiaozhang;


    //=================================//

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司sid")
    private Long companySid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "客户sid")
    private Long customerSid;

    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @ApiModelProperty(value = "公司编码")
    private String companyCode;

    @ApiModelProperty(value = "客户名称")
    private String customerName;

    @ApiModelProperty(value = "客户编码")
    private String customerCode;

    @ApiModelProperty(value = "公司简称")
    private String companyShortName;

    @ApiModelProperty(value = "客户简称")
    private String customerShortName;

    @ApiModelProperty(value = "单据类型")
    private String documentType;

    @ApiModelProperty(value = "唯一键")
    private String oneKey;
}
