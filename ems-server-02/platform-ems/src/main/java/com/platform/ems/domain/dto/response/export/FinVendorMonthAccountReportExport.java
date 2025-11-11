package com.platform.ems.domain.dto.response.export;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;
import com.platform.ems.util.data.KeepTwoDecimalsSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 供应商台账导出 FinVendorMonthAccountReportExport
 *
 * @author chenkaiwen
 * @date 2022-04-12
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FinVendorMonthAccountReportExport extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Excel(name = "公司简称")
    @ApiModelProperty(value = "公司简称")
    private String companyShortName;

    @Excel(name = "供应商编码")
    @ApiModelProperty(value = "供应商编码")
    private Long vendorCode;

    @Excel(name = "供应商简称")
    @ApiModelProperty(value = "供应商简称")
    private String vendorShortName;

    @Excel(name = "应付暂估",scale = 2)
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "月对账单/台账-应付暂估")
    private BigDecimal yingfuzangu;

    @Excel(name = "应付款",scale = 2)
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "台账-应付款金额")
    private BigDecimal yingfukuan;

    @Excel(name = "待核销扣款",scale = 2)
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "台账-扣款金额")
    private BigDecimal koukuan;

    @Excel(name = "待核销调账",scale = 2)
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "台账-调账金额")
    private BigDecimal tiaozhang;

    @Excel(name = "待核销预付款",scale = 2)
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "台账-预付款金额")
    private BigDecimal yufukuan;

    @Excel(name = "待核销特殊付款",scale = 2)
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "台账-特殊付款")
    private BigDecimal teshufukuan;

    @Excel(name = "押金",scale = 2)
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "月对账单/台账-押金金额")
    private BigDecimal yajin;

    @Excel(name = "暂押款",scale = 2)
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "月对账单/台账-暂押款金额")
    private BigDecimal zanyakuan;

    @Excel(name = "应收款",scale = 2)
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "月对账单/台账-应收款金额")
    private BigDecimal yingshoukuan;

    @Excel(name = "应收暂估",scale = 2)
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "月对账单/台账-应收暂估")
    private BigDecimal yingshouzangu;
}
