package com.platform.ems.domain.dto.request.form;

import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 *  收款申请单明细报表 FinReceivableBillItemFormRequest
 *
 * @author chenkaiwen
 * @date 2021-07-12
 */
@Data
@Accessors(chain = true)
@ApiModel
public class FinReceivableBillItemFormRequest extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @ApiModelProperty(value = "收款单号")
    private String receivableBillCode;

    @ApiModelProperty(value = "客户")
    private Long[] customerSidList;

    @ApiModelProperty(value = "公司")
    private Long[] companySidList;

    @ApiModelProperty(value = "产品季")
    private Long[] productSeasonSidList;

    @ApiModelProperty(value = "收款方式（配置档案 code 多选框）")
    private String paymentMethod;

    @ApiModelProperty(value = "收款方式（配置档案 code 多选框）")
    private String[] paymentMethodList;

    @ApiModelProperty(value = "业务类型（配置档案 code 多选框）")
    private String[] businessTypeList;

    @ApiModelProperty(value = "单据类型（配置档案 code 多选框）")
    private String[] documentTypeList;

    @ApiModelProperty(value = "款项类别（配置档案 code 多选框）")
    private String[] accountCategoryList;

    @ApiModelProperty(value = "销售渠道")
    private String[] businessChannelList;

    @ApiModelProperty(value = "销售组织（配置档案 code 多选框）")
    private String[] saleOrgList;

    @ApiModelProperty(value = "经办人（user_name 多选框）")
    private String[] agentList;

    @ApiModelProperty(value ="收款状态（数据字典)")
    private String receiptPaymentStatus;

    @ApiModelProperty(value ="收款状态（数据字典 多选框)")
    private String[] receiptPaymentStatusList;

    @ApiModelProperty(value = "创建人")
    private String[] creatorAccountList;

    @ApiModelProperty(value = "处理状态（数据字典)")
    private String handleStatus;

    @ApiModelProperty(value = "处理状态（数据字典 多选框)")
    private String[] handleStatusList;

    @ApiModelProperty(value = "数据源系统（数据字典）")
    private String dataSourceSys;
}
