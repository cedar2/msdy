package com.platform.ems.domain.dto.response.form;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.ems.util.data.KeepTwoDecimalsSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 合同统计报表 SalSaleContractFormResponse
 *
 * @author chenkaiwen
 * @date 2022-06-06
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SalSaleContractFormResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @Excel(name = "年份", dictType = "s_year")
    @ApiModelProperty(value = "年份（数据字典的键值）")
    private Integer year;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "客户SID")
    private Long customerSid;

    @ApiModelProperty(value = "客户名称")
    private String customerName;

    @Excel(name = "客户")
    @ApiModelProperty(value = "客户简称")
    private String customerShortName;

    @ApiModelProperty(value = "合同类型（数据字典的键值）")
    @Excel(name = "合同类型", dictType = "s_contract_type")
    private String contractType;

    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @ApiModelProperty(value = "公司简称")
    private String companyShortName;

    @ApiModelProperty(value = "产品季名称")
    private String productSeasonName;

    @ApiModelProperty(value = "销售模式（数据字典）")
    private String saleMode;

    @ApiModelProperty(value = "销售渠道名称")
    private String businessChannelName;

    @ApiModelProperty(value = "客供料方式（数据字典的键值）")
    private String rawMaterialMode;

    @Excel(name = "合同数")
    @ApiModelProperty(value = "合同数")
    private BigDecimal numContract;

    @Excel(name = "合同总金额(元)", scale = 2)
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "合同总金额")
    private BigDecimal sumAmountTaxContract;

    @Excel(name = "订单数")
    @ApiModelProperty(value = "订单数")
    private BigDecimal numOrder;

    @ApiModelProperty(value = "数据源系统（数据字典的键值）")
    private String dataSourceSys;
}
