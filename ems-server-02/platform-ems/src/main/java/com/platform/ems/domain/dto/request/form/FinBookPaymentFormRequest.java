package com.platform.ems.domain.dto.request.form;

import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 付款流水报表 FinBookAccountPayableFormResponse
 *
 * @author chenkaiwen
 * @date 2021-07-12
 */
@Data
@Accessors(chain = true)
@ApiModel
public class FinBookPaymentFormRequest extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @ApiModelProperty(value = "付款单号")
    private Long payBillCode;

    @ApiModelProperty(value = "产品季sid")
    private Long productSeasonSid;

    @ApiModelProperty(value = "系统SID-供应商")
    private Long vendorSid;

    @ApiModelProperty(value = "系统SID-公司档案")
    private Long companySid;

    @ApiModelProperty(value = "业务类型")
    private String businessType;

    @ApiModelProperty(value = "流水类型")
    private String bookType;

    @ApiModelProperty(value = "流水来源类别")
    private String bookSourceCategory;

    @ApiModelProperty(value = "支付方式")
    private String paymentMethod;

    @ApiModelProperty(value = "单据类型")
    private String documentType;

    @ApiModelProperty(value = "经办人")
    private String agent;

    @ApiModelProperty(value = "采购组织")
    private String purchaseOrg;

    @ApiModelProperty(value = "采购订单号")
    private Long purchaseOrderCode;

    @ApiModelProperty(value = "核销状态")
    private String clearStatus;

    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @ApiModelProperty(value = "处理状态（多选）")
    private String[] handleStatusList;

    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    @ApiModelProperty(value = "系统SID-供应商")
    private Long[] vendorSidList;

    @ApiModelProperty(value = "系统SID-公司")
    private Long[] companySidList;

    @ApiModelProperty(value = "系统SID-产品季")
    private Long[] productSeasonSidList;

    @ApiModelProperty(value = "系统SID-流水类型")
    private String[] bookTypeList;

    @ApiModelProperty(value = "系统SID-流水来源类别")
    private String[] bookSourceCategoryList;

    @ApiModelProperty(value = "系统SID-核销状态")
    private String[] clearStatusList;

    @ApiModelProperty(value = "经办人（多选）")
    private String[] agentList;

    @ApiModelProperty(value = "支付方式（多选）")
    private String[] paymentMethodList;

    @ApiModelProperty(value = "创建人（多选）")
    private String[] creatorAccountList;

    @ApiModelProperty(value = "是否已财务对账（数据字典的键值或配置档案的编码）")
    private String isFinanceVerify;

}
