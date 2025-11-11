package com.platform.ems.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FunFundAccountExport {

    /**
     * 类型（数据字典的键值或配置档案的编码）
     */
    @NotBlank(message = "类型不能为空")
    @Excel(name = "类型",dictType = "s_fund_account_type")
    @ApiModelProperty(value = "类型（数据字典的键值或配置档案的编码）")
    private String accountType;

    @Excel(name = "公司")
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    /**
     * 账户金额
     */
    @Digits(integer = 10, fraction = 5, message = "账户金额整数位上限为10位，小数位上限为5位")
    @Excel(name = "金额(万)", scale = 2)
    @ApiModelProperty(value = "账户金额")
    private BigDecimal currencyAmount;


}
