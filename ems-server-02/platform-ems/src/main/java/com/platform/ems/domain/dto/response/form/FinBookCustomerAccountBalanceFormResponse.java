package com.platform.ems.domain.dto.response.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
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
 * 客户互抵流水报表 FinBookCustomerAccountBalanceFormResponse
 *
 * @author chenkaiwen
 * @date 2021-07-12
 */
@Data
@Accessors(chain = true)
@ApiModel
public class FinBookCustomerAccountBalanceFormResponse extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "供应商账互抵财务流水号 ")
    private Long bookAccountBalanceCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "供应商账互抵单号 ")
    private Long accountBalanceBillCode;

    @ApiModelProperty(value = "系统SID-供应商")
    private String customerName;

    @ApiModelProperty(value = "系统SID-公司档案")
    private String companyName;

    @ApiModelProperty(value = "系统SID-产品季")
    private String productSeasonName;

    @ApiModelProperty(value = "销售员")
    private String salePersonName;

    @ApiModelProperty(value = "物料类型")
    private String materialTypeName;

    @ApiModelProperty(value = "行号")
    private int itemNum;

    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "本次互抵金额（含税）")
    private BigDecimal currencyAmountTaxHd;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "关联流水号")
    private Long referAccountDocumentCode;

    @ApiModelProperty(value = "流水类型")
    private String bookTypeName;

    @ApiModelProperty(value = "流水来源类别")
    private String bookSourceCategoryName;

    @ApiModelProperty(value = "关联流水类型（配置档案的名称）")
    private String referBookTypeName;

    @ApiModelProperty(value = "关联流水来源类别（配置档案的名称）")
    private String referBookSourceCategoryName;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "单据日期")
    private Date documentDate;

    @ApiModelProperty(value = "币种")
    private String currency;

    @ApiModelProperty(value = "货币单位")
    private String currencyUnit;

    @ApiModelProperty(value = "处理状态（数据字典的键值）")
    private String handleStatus;

    @ApiModelProperty(value = "创建人昵称")
    private String creatorAccountName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @ApiModelProperty(value = "数据源系统（数据字典的键值）")
    private String dataSourceSys;

}
