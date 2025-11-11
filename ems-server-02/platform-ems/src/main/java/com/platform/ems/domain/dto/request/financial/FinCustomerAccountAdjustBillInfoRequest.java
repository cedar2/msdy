package com.platform.ems.domain.dto.request.financial;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.ems.domain.FinCustomerAccountAdjustBillAttachment;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 客户调账单详情请求实体 FinCustomerAccountAdjustBillInfoRequest
 *
 * @author chenkaiwen
 * @date 2021-07-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel
public class FinCustomerAccountAdjustBillInfoRequest extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "客户调账单sid")
    private Long adjustBillSid;

    @ApiModelProperty(value = "sid数组")
    private Long[] adjustBillSidList;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "客户调账单号")
    private Long adjustBillCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "客户Sid")
    private Long customerSid;

    @ApiModelProperty(value = "产品季Sid")
    private Long productSeasonSid;

    @ApiModelProperty(value = "公司Sid")
    private Long companySid;

    @ApiModelProperty(value = "公司品牌sid")
    private Long companyBrandSid;

    @ApiModelProperty(value = "销售员（user_name）")
    private String salePerson;

    @ApiModelProperty(value = "销售组织（数据字典=）")
    private String saleOrg;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "单据日期")
    private Date documentDate;

    @ApiModelProperty(value = "单据类型")
    private String documentType;

    @ApiModelProperty(value = "物料类型")
    private String materialType;

    @ApiModelProperty(value = "业务类型")
    private String businessType;

    @ApiModelProperty(value = "业务渠道")
    private String businessChannel;

    @ApiModelProperty(value = "币种（数据字典）")
    private String currency;

    @ApiModelProperty(value = "货币单位（数据字典）")
    private String currencyUnit;

    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @ApiModelProperty(value = "创建人")
    private String creatorAccount;

    @ApiModelProperty(value = "数据源系统（数据字典）")
    private String dataSourceSys;

    @Valid
    @ApiModelProperty(value = "调账明细")
    private List<FinCustomerAccountAdjustBillItemInfoRequest> itemList;

    @Valid
    @ApiModelProperty(value = "附件List")
    private List<FinCustomerAccountAdjustBillAttachment> attachmentList;
}
