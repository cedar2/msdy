package com.platform.ems.domain.dto.response.financial;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.ems.domain.FinPayBillAttachment;
import com.platform.ems.domain.FinReceivableBillAttachment;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 付款单详情返回实体 FinPayBillInfoResponse
 *
 * @author chenkaiwen
 * @date 2021-07-26
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FinPayBillInfoResponse extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-付款单")
    private Long payBillSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "付款单号")
    private Long payBillCode;

    @ApiModelProperty(value = "业务类型")
    private String businessType;

    @ApiModelProperty(value = "业务类型名称")
    private String businessTypeName;

    @ApiModelProperty(value = "款项类别")
    private String accountCategory;

    @ApiModelProperty(value = "款项类别名称")
    private String accountCategoryName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商")
    private Long vendorSid;

    @ApiModelProperty(value = "供应商名称")
    private String vendorName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-公司档案")
    private Long companySid;

    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-产品季档案")
    private Long productSeasonSid;

    @ApiModelProperty(value = "产品季名称")
    private String productSeasonName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "收付款方式组合sid")
    private Long accountsMethodGroup;

    @ApiModelProperty(value = "支付方式")
    private String paymentMethod;

    @ApiModelProperty(value = "支付方式名称")
    private String paymentMethodName;

    @ApiModelProperty(value = "本次付款金额小计(元)")
    private BigDecimal currencyAmountTax;

    @ApiModelProperty(value = "年（付款期间）")
    private Long paymentYear;

    @ApiModelProperty(value = "月（付款期间）")
    private Long paymentMonth;

    @ApiModelProperty(value = "日（付款期间）")
    private Long paymentDay;

    @ApiModelProperty(value = "紧急程度（数据字典的键值）")
    private String urgency;

    @ApiModelProperty(value = "经办人（用户名称）")
    private String agent;

    @ApiModelProperty(value = "经办人（用户昵称）")
    private String agentName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "单据日期")
    private Date documentDate;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "付款主体sid")
    private Long payCompanySid;

    @ApiModelProperty(value = "付款主体名称")
    private String payCompanyName;

    @ApiModelProperty(value = "银行账号")
    private String bankAccount;

    @ApiModelProperty(value = "银行名称")
    private String bankName;

    @ApiModelProperty(value = "币种（数据字典）")
    private String currency;

    @ApiModelProperty(value = "货币单位（数据字典）")
    private String currencyUnit;

    @ApiModelProperty(value = "付款状态（数据字典）")
    private String paymentStatus;

    @ApiModelProperty(value = "是否预付手工录入")
    private String isAdvanceManual;

    @ApiModelProperty(value = "处理状态（数据字典）")
    private String handleStatus;

    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    @ApiModelProperty(value = "创建人账号（用户昵称）")
    private String creatorAccountName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @ApiModelProperty(value = "付款单-明细对象")
    private List<FinPayBillItemInfoResponse> itemList;

    @ApiModelProperty(value = "付款单-附件对象")
    private List<FinPayBillAttachment> attachmentList;

    @ApiModelProperty(value = "数据源系统（数据字典）")
    private String dataSourceSys;
}
