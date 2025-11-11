package com.platform.ems.domain.dto.response.financial;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.ems.domain.FinVendorDeductionBillAttachment;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 供应商扣款单详情查询返回实体 FinVendorDeductionBillInfoResponse
 *
 * @author chenkaiwen
 * @date 2021-07-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FinVendorDeductionBillInfoResponse extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "供应商扣款单sid")
    private Long deductionBillSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "供应商扣款单号")
    private Long deductionBillCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "供应商")
    private Long vendorSid;

    @ApiModelProperty(value = "供应商")
    private String vendorCode;

    @ApiModelProperty(value = "供应商名称")
    private String vendorName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司")
    private Long companySid;

    @ApiModelProperty(value = "公司")
    private String companyCode;

    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "产品季")
    private Long productSeasonSid;

    @ApiModelProperty(value = "产品季名称")
    private String productSeasonName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "单据日期")
    private Date documentDate;

    @ApiModelProperty(value = "采购员")
    private String buyer;

    @ApiModelProperty(value = "采购员昵称")
    private String buyerName;

    @ApiModelProperty(value = "采购组织")
    private String purchaseOrg;

    @ApiModelProperty(value = "采购组织名称")
    private String purchaseOrgName;

    @ApiModelProperty(value = "单据类型")
    private String documentType;

    @ApiModelProperty(value = "单据类型名称")
    private String documentTypeName;

    @ApiModelProperty(value = "物料类型")
    private String materialType;

    @ApiModelProperty(value = "物料类型名称")
    private String materialTypeName;

    @ApiModelProperty(value = "业务类型")
    private String businessType;

    @ApiModelProperty(value = "业务类型名称")
    private String businessTypeName;

    @ApiModelProperty(value = "业务渠道")
    private String businessChannel;

    @ApiModelProperty(value = "业务渠道名称")
    private String businessChannelName;

    @ApiModelProperty(value = "币种（数据字典）")
    private String currency;

    @ApiModelProperty(value = "货币单位（数据字典）")
    private String currencyUnit;

    @ApiModelProperty(value = "处理状态（数据字典）")
    private String handleStatus;

    @ApiModelProperty(value = "创建人")
    private String creatorAccount;

    @ApiModelProperty(value = "创建人昵称")
    private String creatorAccountName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @ApiModelProperty(value = "数据源系统（数据字典）")
    private String dataSourceSys;

    @ApiModelProperty(value = "扣款明细")
    private List<FinVendorDeductionBillItemInfoResponse> itemList;

    @ApiModelProperty(value = "扣款明细")
    private List<FinVendorDeductionBillAttachment> attachmentList;

    public List<FinVendorDeductionBillItemInfoResponse> getItemList() {
        if (this.itemList == null){
            this.itemList = new ArrayList<>();
        }
        return this.itemList;
    }

}
