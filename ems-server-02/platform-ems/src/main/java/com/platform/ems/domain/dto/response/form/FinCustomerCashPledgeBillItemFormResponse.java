package com.platform.ems.domain.dto.response.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;
import com.platform.ems.util.data.KeepTwoDecimalsSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 客户押金明细报表 FinCustomerCashPledgeBillItemFormResponse
 *
 * @author chenkaiwen
 * @date 2021-11-11
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FinCustomerCashPledgeBillItemFormResponse extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-客户押金(退回)单明细")
    private Long cashPledgeBillItemSid;

    @Excel(name = "客户押金(退回)单号")
    @ApiModelProperty(value = "客户押金(退回)单号")
    private Long cashPledgeBillCode;

    @Excel(name = "单据类型")
    @ApiModelProperty(value = "单据类型")
    private String documentTypeName;

    @Excel(name = "押金类型")
    @ApiModelProperty(value = "押金类型")
    private String pledgeTypeName;

    @Excel(name = "公司")
    @ApiModelProperty(value = "公司")
    private String companyName;

    @ApiModelProperty(value = "客户简称")
    private String customerShortName;

    @ApiModelProperty(value = "公司简称")
    private String companyShortName;

    @Excel(name = "客户")
    @ApiModelProperty(value = "客户")
    private String customerName;

    @Excel(name = "下单季")
    @ApiModelProperty(value = "产品季")
    private String productSeasonName;

    @Excel(name = "销售员")
    @ApiModelProperty(value = "销售员")
    private String salePersonName;

    @Excel(name = "关联业务单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "退回单对应的押金单号")
    private Long preCashPledgeBillCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "引用的行号")
    private Long preItemNum;

    @Excel(name = "退回状态",dictType = "s_return_status")
    @ApiModelProperty(value = "退回状态")
    private String returnStatus;

    @Excel(name = "押金金额/本次退回金额")
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "押金金额/本次退回金额")
    private BigDecimal currencyAmount;

    @Excel(name = "已退回金额")
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "已退回金额")
    private BigDecimal currencyAmountYth;

    @Excel(name = "退回中金额")
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "退回中金额")
    private BigDecimal currencyAmountThz;

    @Excel(name = "待退回金额")
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "待退回金额")
    private BigDecimal currencyAmountDth;

    @Excel(name = "单据日期", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "单据日期")
    private Date documentDate;

    @Excel(name = "处理状态",dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人")
    private String creatorAccountName;

    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @ApiModelProperty(value = "数据源系统（数据字典的键值）")
    private String dataSourceSys;
}
