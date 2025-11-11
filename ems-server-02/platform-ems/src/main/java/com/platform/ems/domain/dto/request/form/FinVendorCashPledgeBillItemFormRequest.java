package com.platform.ems.domain.dto.request.form;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 供应商押金明细报表 FinVendorCashPledgeBillItemFormRequest
 *
 * @author chenkaiwen
 * @date 2021-11-11
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FinVendorCashPledgeBillItemFormRequest extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @ApiModelProperty(value = "供应商")
    private Long vendorSid;

    @ApiModelProperty(value = "公司")
    private Long companySid;

    @ApiModelProperty(value = "供应商")
    private Long[] vendorSidList;

    @ApiModelProperty(value = "公司")
    private Long[] companySidList;

    @ApiModelProperty(value = "单据类型（配置档案 code 多选框）")
    private String[] documentTypeList;

    @ApiModelProperty(value = "供应商押金(退回)单号")
    private Long cashPledgeBillCode;

    @ApiModelProperty(value = "退回状态")
    private String[] returnStatusList;

    @ApiModelProperty(value = "确认状态（多选）")
    private String[] handleStatusList;

    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;
}
