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
 * 客户互抵单列表请求实体 FinCustomerAccountBalanceBillListRequest
 *
 * @author chenkaiwen
 * @date 2021-07-26
 */
@Data
@Accessors(chain = true)
@ApiModel
public class FinCustomerAccountBalanceBillListRequest extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @ApiModelProperty(value = "数据源系统（数据字典）")
    private String dataSourceSys;

    @ApiModelProperty(value = "sid数组")
    private Long[] accountBalanceBillSidList;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "客户账互抵单号")
    private Long accountBalanceBillCode;

    @ApiModelProperty(value = "系统SID-客户（单选）")
    private Long customerSid;

    @ApiModelProperty(value = "系统SID-客户")
    private Long[] customerSidList;

    @ApiModelProperty(value = "系统SID-公司档案（单选）")
    private Long companySid;

    @ApiModelProperty(value = "系统SID-公司档案（单选）")
    private Long[] companySidList;

    @ApiModelProperty(value = "系统SID-产品季（单选）")
    private Long productSeasonSid;

    @ApiModelProperty(value = "系统SID-产品季")
    private Long[] productSeasonSidList;

    @ApiModelProperty(value = "销售员（单选）")
    private String salePerson;

    @ApiModelProperty(value = "销售员")
    private String[] salePersonList;

    @ApiModelProperty(value = "创建人账号（用户名称 单选）")
    private String creatorAccount;

    @ApiModelProperty(value = "创建人账号（用户名称 多选）")
    private String[] creatorAccountList;

    @ApiModelProperty(value = "处理状态（单选）")
    private String handleStatus;

    @ApiModelProperty(value = "处理状态（数据字典 多选）")
    private String[] handleStatusList;
}
