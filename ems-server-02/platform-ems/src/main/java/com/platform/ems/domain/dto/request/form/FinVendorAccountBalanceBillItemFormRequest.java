package com.platform.ems.domain.dto.request.form;

import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 *  供应商互抵明细报表 FinVendorAccountBalanceBillItemFormRequest
 *
 * @author chenkaiwen
 * @date 2021-07-12
 */
@Data
@Accessors(chain = true)
@ApiModel
public class FinVendorAccountBalanceBillItemFormRequest extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @ApiModelProperty(value = "供应商互抵单号")
    private Long accountBalanceBillCode;

    @ApiModelProperty(value = "供应商")
    private Long[] vendorSidList;

    @ApiModelProperty(value = "公司")
    private Long[] companySidList;

    @ApiModelProperty(value = "产品季")
    private Long[] productSeasonSidList;

    @ApiModelProperty(value = "采购员（user_name 多选框）")
    private String[] buyerList;

    @ApiModelProperty(value = "物料类型（配置档案 code 多选框）")
    private String[] materialTypeList;

    @ApiModelProperty(value = "核销状态（数据字典 多选框）")
    private String[] clearStatusList;

    @ApiModelProperty(value = "创建人账号（输入框）")
    private String creatorAccount;

    @ApiModelProperty(value = "处理状态（数据字典 多选框)")
    private String[] handleStatusList;

    @ApiModelProperty(value = "数据源系统（数据字典）")
    private String dataSourceSys;
}
