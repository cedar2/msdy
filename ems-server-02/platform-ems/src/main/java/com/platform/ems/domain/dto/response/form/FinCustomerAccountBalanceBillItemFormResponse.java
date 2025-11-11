package com.platform.ems.domain.dto.response.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 客户互抵明细报表 FinCustomerAccountBalanceBillItemFormResponse
 *
 * @author chenkaiwen
 * @date 2021-07-12
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FinCustomerAccountBalanceBillItemFormResponse extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @Excel(name = "客户互抵单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "客户互抵单号")
    private Long accountBalanceBillCode;

    @Excel(name = "客户")
    @ApiModelProperty(value = "客户")
    private String customerName;

    @Excel(name = "公司")
    @ApiModelProperty(value = "公司")
    private String companyName;

    @Excel(name = "产品季")
    @ApiModelProperty(value = "产品季")
    private String productSeasonName;

    /**   @Excel(name = "物料类型")   */
    @ApiModelProperty(value = "物料类型")
    private String materialTypeName;

    @Excel(name = "销售员")
    @ApiModelProperty(value = "销售员")
    private String salePersonName;

    @Excel(name = "单据日期", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "单据日期")
    private Date documentDate;

    @Excel(name = "互抵金额(含税)")
    @ApiModelProperty(value = "互抵金额(含税)")
    private BigDecimal currencyAmountTax;

    @Excel(name = "行号")
    @ApiModelProperty(value = "行号")
    private int itemNum;

    @Excel(name = "关联流水类别")
    @ApiModelProperty(value = "关联流水类别")
    private String bookSourceCategoryName;

    @Excel(name = "关联流水类别")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "关联流水类别（台帐流水号/财务账流水号）")
    private Long accountDocumentCode;

    @Excel(name = "关联流水行号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "关联流水行号")
    private Long accountItemCode;

    @Excel(name = "币种", dictType = "s_currency")
    @ApiModelProperty(value = "币种（数据字典的键值）")
    private String currency;

    @Excel(name = "货币单位", dictType = "s_currency_unit")
    @ApiModelProperty(value = "货币单位（数据字典的键值）")
    private String currencyUnit;

    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值）")
    private String handleStatus;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccountName;

    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @ApiModelProperty(value = "数据源系统（数据字典的键值）")
    private String dataSourceSys;

}
