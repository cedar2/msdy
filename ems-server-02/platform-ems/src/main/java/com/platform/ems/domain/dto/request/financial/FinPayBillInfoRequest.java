package com.platform.ems.domain.dto.request.financial;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.ems.domain.FinPayBillAttachment;
import com.platform.ems.domain.FinPayBillItem;
import com.platform.ems.domain.FinPayBillPayMethod;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 付款单详情请求实体 FinPayBillInfoRequest
 *
 * @author chenkaiwen
 * @date 2021-07-26
 */
@Data
@Accessors(chain = true)
@ApiModel
public class FinPayBillInfoRequest extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-付款单")
    private Long payBillSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "付款单号")
    private Long payBillCode;

    @NotBlank(message = "业务类型不能为空")
    @ApiModelProperty(value = "业务类型")
    private String businessType ;

    @NotBlank(message = "款项类别不能为空")
    @ApiModelProperty(value = "款项类别")
    private String accountCategory;

    @NotNull(message = "供应商不能为空")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商")
    private Long vendorSid;

    @NotNull(message = "公司不能为空")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-公司档案")
    private Long companySid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-产品季档案")
    private Long productSeasonSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "收付款方式组合sid")
    private Long accountsMethodGroup;

    @ApiModelProperty(value = "支付方式")
    private String paymentMethod;

    @ApiModelProperty(value = "本次付款金额小计(元)")
    private BigDecimal currencyAmountTax;

    @NotNull(message = "付款年份不能为空")
    @ApiModelProperty(value = "年（付款期间）")
    private Long paymentYear;

    @NotNull(message = "付款月份不能为空")
    @ApiModelProperty(value = "月（付款期间）")
    private Long paymentMonth;

    @NotNull(message = "付款日期不能为空")
    @ApiModelProperty(value = "日（付款期间）")
    private Long paymentDay;

    @NotBlank(message = "紧急程度不能为空")
    @ApiModelProperty(value = "紧急程度（数据字典的键值）")
    private String urgency;

    @NotBlank(message = "经办人不能为空")
    @ApiModelProperty(value = "经办人（用户名称）")
    private String agent;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "单据日期")
    private Date documentDate;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "付款主体sid")
    private Long payCompanySid;

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

    @ApiModelProperty(value = "是否已财务对账（数据字典的键值）")
    private String isFinanceVerify;

    @ApiModelProperty(value = "处理状态（数据字典）")
    private String handleStatus;

    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    @Valid
    @ApiModelProperty(value = "付款单-明细对象")
    private List<FinPayBillItemInfoRequest> itemList;

    @ApiModelProperty(value = "付款单-支付方式明细对象")
    private List<FinPayBillPayMethod> payMethodList;

    @Valid
    @ApiModelProperty(value = "付款单-附件对象")
    private List<FinPayBillAttachment> attachmentList;

    @ApiModelProperty(value = "数据源系统（数据字典）")
    private String dataSourceSys;
}
