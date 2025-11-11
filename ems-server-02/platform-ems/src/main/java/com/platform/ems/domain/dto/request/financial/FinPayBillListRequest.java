package com.platform.ems.domain.dto.request.financial;

import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 付款单列表请求实体 FinPayBillListRequest
 *
 * @author chenkaiwen
 * @date 2021-07-26
 */
@Data
@Accessors(chain = true)
@ApiModel
public class FinPayBillListRequest extends EmsBaseEntity implements Serializable{

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @ApiModelProperty(value = "系统SID-付款单")
    private Long[] payBillSidList;

    @ApiModelProperty(value = "付款单号")
    private Long payBillCode;

    @ApiModelProperty(value = "系统SID-供应商（单选）")
    private Long vendorSid;

    @ApiModelProperty(value = "系统SID-供应商")
    private Long[] vendorSidList;

    @ApiModelProperty(value = "系统SID-公司档案（单选）")
    private Long companySid;

    @ApiModelProperty(value = "系统SID-公司档案")
    private Long[] companySidList;

    @ApiModelProperty(value = "系统SID-产品季档案（单选）")
    private Long productSeasonSid;

    @ApiModelProperty(value = "系统SID-产品季档案")
    private Long[] productSeasonSidList;

    @ApiModelProperty(value = "款项类别（单选）")
    private String accountCategory;

    @ApiModelProperty(value = "款项类别")
    private String[] accountCategoryList;

    @ApiModelProperty(value = "业务类型（单选）")
    private String businessType;

    @ApiModelProperty(value = "业务类型")
    private String[] businessTypeList;

    @ApiModelProperty(value = "支付方式（单选）")
    private String paymentMethod;

    @ApiModelProperty(value = "支付方式")
    private String[] paymentMethodList;

    @ApiModelProperty(value = "付款状态（数据字典的键值或配置档案的编码）（单选）")
    private String paymentStatus;

    @ApiModelProperty(value = "付款状态（数据字典的键值或配置档案的编码）")
    private String[] paymentStatusList;

    @ApiModelProperty(value = "采购组织（数据字典的键值）（单选）")
    private String purchaseOrg;

    @ApiModelProperty(value = "采购组织（数据字典的键值）")
    private String[] purchaseOrgList;

    @ApiModelProperty(value = "经办人（用户名称）（单选）")
    private String[] agent;

    @ApiModelProperty(value = "经办人（用户名称）")
    private String[] agentList;

    @ApiModelProperty(value = "是否已财务对账（数据字典的键值）")
    private String isFinanceVerify;

    @ApiModelProperty(value = "创建人（输入框）")
    private String creatorAccount;

    @ApiModelProperty(value = "创建人账号（多选）")
    private String[] creatorAccountList;

    @ApiModelProperty(value = "处理状态（单选）")
    private String handleStatus;

    @ApiModelProperty(value = "处理状态（多选）")
    private String[] handleStatusList;

    @ApiModelProperty(value = "数据源系统（数据字典的键值）")
    private String dataSourceSys;
}
