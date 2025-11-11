package com.platform.ems.domain.dto.request.financial;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.ems.domain.FinVendorDeductionBillAttachment;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 供应商扣款单详情请求实体 FinVendorDeductionBillListRequest
 *
 * @author chenkaiwen
 * @date 2021-07-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel
public class FinVendorDeductionBillInfoRequest extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "供应商扣款单sid")
    private Long deductionBillSid;

    @ApiModelProperty(value = "sid数组")
    private Long[] deductionBillSidList;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "供应商扣款单号")
    private Long deductionBillCode;

    @NotNull(message = "供应商不能为空")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "供应商Sid")
    private Long vendorSid;

    @ApiModelProperty(value = "产品季Sid")
    private Long productSeasonSid;

    @NotNull(message = "公司不能为空")
    @ApiModelProperty(value = "公司Sid")
    private Long companySid;

    @ApiModelProperty(value = "公司品牌sid")
    private Long companyBrandSid;

    @ApiModelProperty(value = "采购员（user_name）")
    private String buyer;

    @ApiModelProperty(value = "采购组织（数据字典）")
    private String purchaseOrg;

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

    @ApiModelProperty(value = "启用/停用")
    private String status;

    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @ApiModelProperty(value = "创建人")
    private String creatorAccount;

    @ApiModelProperty(value = "数据源系统（数据字典）")
    private String dataSourceSys;

    @Valid
    @ApiModelProperty(value = "扣款明细")
    private List<FinVendorDeductionBillItemInfoRequest> itemList;

    @Valid
    @ApiModelProperty(value = "扣款附件")
    private List<FinVendorDeductionBillAttachment> attachmentList;
}
