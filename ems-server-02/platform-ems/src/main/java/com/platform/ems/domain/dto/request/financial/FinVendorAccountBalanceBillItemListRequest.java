package com.platform.ems.domain.dto.request.financial;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 供应商互抵单明细查询请求实体 FinVendorAccountBalanceBillItemListRequest
 *
 * @author chenkaiwen
 * @date 2021-07-26
 */
@Data
@Accessors(chain = true)
@ApiModel
public class FinVendorAccountBalanceBillItemListRequest extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @ApiModelProperty(value = "数据源系统（数据字典）")
    private String dataSourceSys;

    @ApiModelProperty(value = "财务流水类型编码code")
    private String bookType;

    @ApiModelProperty(value = "财务流水来源类别编码code")
    private String bookSourceCategory;

    @ApiModelProperty(value = "财务流水来源类别编码code")
    private String[] bookSourceCategoryList;

    @ApiModelProperty(value = "核销状态（数据字典 单选）")
    private String clearStatus;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "财务凭证流水号code（台帐流水号/财务账流水号）")
    private Long accountDocumentCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "供应商")
    private Long vendorSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司")
    private Long companySid;

}
