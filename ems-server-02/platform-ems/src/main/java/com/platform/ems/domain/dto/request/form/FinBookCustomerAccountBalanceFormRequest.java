package com.platform.ems.domain.dto.request.form;

import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 客户账互抵流水报表 FinBookCustomerAccountBalanceFormRequest
 *
 * @author chenkaiwen
 * @date 2021-07-12
 */
@Data
@Accessors(chain = true)
@ApiModel
public class FinBookCustomerAccountBalanceFormRequest extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @ApiModelProperty(value = "客户账互抵单号 ")
    private Long accountBalanceBillCode;

    @ApiModelProperty(value = "系统SID-客户商")
    private Long customerSid;

    @ApiModelProperty(value = "系统SID-公司档案")
    private Long companySid;

    @ApiModelProperty(value = "系统SID-产品季")
    private Long productSeasonSid;

    @ApiModelProperty(value = "销售员（user_name）")
    private String salePerson;

    @ApiModelProperty(value = "关联的流水来源类别")
    private String referBookSourceCategory;

    @ApiModelProperty(value = "关联的流水类型")
    private String referBookType;

    @ApiModelProperty(value = "物料类型（code）")
    private String materialType;

    @ApiModelProperty(value = "处理状态（数据字典的键值）")
    private String handleStatus;

    @ApiModelProperty(value = "创建人账号（user_name）")
    private String creatorAccount;

    @ApiModelProperty(value = "数据源系统（数据字典的键值）")
    private String dataSourceSys;

    @ApiModelProperty(value = "系统SID-客户")
    private Long[] customerSidList;

    @ApiModelProperty(value = "系统SID-公司")
    private Long[] companySidList;

    @ApiModelProperty(value = "系统SID-产品季")
    private Long[] productSeasonSidList;

    @ApiModelProperty(value = "系统SID-流水类型")
    private String[] referBookTypeList;

    @ApiModelProperty(value = "系统SID-流水来源类别")
    private String[] referBookSourceCategoryList;

    @ApiModelProperty(value = "系统SID-销售员")
    private String[] salePersonList;
}
