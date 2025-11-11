package com.platform.ems.domain.dto.request.financial;

import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 供应商调账单列表请求实体 FinVendorAccountAdjustBillListRequest
 *
 * @author chenkaiwen
 * @date 2021-07-26
 */
@Data
@Accessors(chain = true)
@ApiModel
public class FinVendorAccountAdjustBillListRequest extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @ApiModelProperty(value = "供应商调账单号（输入框）")
    private Long adjustBillCode;

    @ApiModelProperty(value = "系统SID-供应商（下拉框 多选）")
    private Long[] vendorSidList;

    @ApiModelProperty(value = "系统SID-公司档案（下拉框 多选）")
    private Long[] companySidList;

    @ApiModelProperty(value = "系统SID-产品季（下拉框 多选）")
    private Long[] productSeasonSidList;

    @ApiModelProperty(value = "采购员（下拉框 多选）")
    private String[] buyerList;

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
