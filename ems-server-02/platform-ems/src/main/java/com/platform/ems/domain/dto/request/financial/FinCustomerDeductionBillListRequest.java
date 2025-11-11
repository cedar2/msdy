package com.platform.ems.domain.dto.request.financial;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;


/**
 * 客户扣款单列表请求实体 FinCustomerDeductionBillListRequest
 *
 * @author chenkaiwen
 * @date 2021-07-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel
public class FinCustomerDeductionBillListRequest extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "客户扣款单号（输入框）")
    private Long deductionBillCode;

    @ApiModelProperty(value = "系统SID-客户（下拉框 多选）")
    private Long[] customerSidList;

    @ApiModelProperty(value = "系统SID-公司档案（下拉框 多选）")
    private Long[] companySidList;

    @ApiModelProperty(value = "系统SID-产品季（下拉框 多选）")
    private Long[] productSeasonSidList;

    @ApiModelProperty(value = "销售员（下拉框 多选）")
    private String[] salePersonList;

    @ApiModelProperty(value = "业务类型编码code")
    private String[] businessTypeList;

    @ApiModelProperty(value = "创建人（输入框）")
    private String creatorAccount;

    @ApiModelProperty(value = "创建人（多选）")
    private String[] creatorAccountList;

    @ApiModelProperty(value = "处理状态（数据字典 单选）")
    private String handleStatus;

    @ApiModelProperty(value = "处理状态（数据字典 多选）")
    private String[] handleStatusList;

    @ApiModelProperty(value = "数据源系统（数据字典）")
    private String dataSourceSys;
}
