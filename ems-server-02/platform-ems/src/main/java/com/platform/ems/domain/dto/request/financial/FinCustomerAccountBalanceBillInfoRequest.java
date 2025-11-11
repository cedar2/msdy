package com.platform.ems.domain.dto.request.financial;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.ems.domain.FinCustomerAccountBalanceBillAttachment;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 客户互抵单详情请求实体 FinCustomerAccountBalanceBillInfoRequest
 *
 * @author chenkaiwen
 * @date 2021-07-26
 */
@Data
@Accessors(chain = true)
@ApiModel
public class FinCustomerAccountBalanceBillInfoRequest extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @ApiModelProperty(value = "数据源系统（数据字典）")
    private String dataSourceSys;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-客户账互抵单")
    private Long accountBalanceBillSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "客户账互抵单号 ")
    private Long accountBalanceBillCode;

    @NotNull(message = "客户不能为空")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-客户")
    private Long customerSid;

    @NotNull(message = "公司不能为空")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-公司档案")
    private Long companySid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司品牌sid")
    private Long companyBrandSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-产品季")
    private Long productSeasonSid;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "单据日期")
    private Date documentDate;

    @ApiModelProperty(value = "单据类型编码code")
    private String documentType;

    @ApiModelProperty(value = "业务类型编码code")
    private String businessType;

    @ApiModelProperty(value = "业务渠道编码code")
    private String businessChannel;

    @ApiModelProperty(value = "销售组织编码code")
    private String saleOrg;

    @ApiModelProperty(value = "销售员")
    private String salePerson;

    @ApiModelProperty(value = "物料类型编码code")
    private String materialType;

    @ApiModelProperty(value = "币种（数据字典）")
    private String currency;

    @ApiModelProperty(value = "货币单位（数据字典）")
    private String currencyUnit;

    @ApiModelProperty(value = "处理状态（数据字典）")
    private String handleStatus;

    @Valid
    @ApiModelProperty(value = "明细表")
    private List<FinCustomerAccountBalanceBillItemInfoRequest> itemList;

    @Valid
    @ApiModelProperty(value = "附件清单")
    private List<FinCustomerAccountBalanceBillAttachment> attachmentList;
}
