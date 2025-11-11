package com.platform.ems.domain.dto.response.form;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
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
 * 供应商调账流水报表 FinBookVendorAccountAdjustFormResponse
 *
 * @author chenkaiwen
 * @date 2021-07-12
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FinBookVendorAccountAdjustFormResponse extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "流水号（供应商调账）")
    private Long bookAccountAdjustSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "流水号（供应商调账）")
    private Long bookAccountAdjustItemSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "流水号（供应商调账）")
    private Long bookAccountAdjustCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "客户调账单号")
    private Long adjustBillCode;

    @ApiModelProperty(value = "流水来源类别")
    private String bookSourceCategoryName;

    @ApiModelProperty(value = "流水类型")
    private String bookTypeName;

    @ApiModelProperty(value = "流水来源类别")
    private String bookSourceCategory;

    @ApiModelProperty(value = "流水类型")
    private String bookType;

    @ApiModelProperty(value = "系统SID-供应商")
    private String vendorName;

    @ApiModelProperty(value = "供应商简称")
    private String vendorShortName;

    @ApiModelProperty(value = "系统SID-公司档案")
    private String companyName;

    @ApiModelProperty(value = "系统SID-产品季")
    private String productSeasonName;

    @ApiModelProperty(value = "采购员")
    private String buyerName;

    @ApiModelProperty(value = "物料类型")
    private String materialTypeName;

    @ApiModelProperty(value = "核销状态")
    private String clearStatus;

    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "调账金额")
    private BigDecimal currencyAmountTaxTz;

    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "待调账金额（含税）")
    private BigDecimal currencyAmountTaxDhx;

    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "已核销金额（含税）")
    private BigDecimal currencyAmountTaxYhx;

    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "核销中金额（含税）")
    private BigDecimal currencyAmountTaxHxz;

    @TableField(exist = false)
    @ApiModelProperty(value = "行号")
    private Long itemNum;

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
