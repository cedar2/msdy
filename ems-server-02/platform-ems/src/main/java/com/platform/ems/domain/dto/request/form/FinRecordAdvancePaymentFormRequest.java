package com.platform.ems.domain.dto.request.form;

import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 供应商待付预付流水报表 FinRecordAdvancePaymentFormRequest
 *
 * @author chenkaiwen
 * @date 2021-09-16
 */
@Data
@Accessors(chain = true)
@ApiModel
public class FinRecordAdvancePaymentFormRequest extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @ApiModelProperty(value = "数据源系统（数据字典的键值）")
    private String dataSourceSys;

    @ApiModelProperty(value = "采购合同号")
    private String purchaseContractCode;

    @ApiModelProperty(value = "采购订单号")
    private Long purchaseOrderCode;

    @ApiModelProperty(value = "供方合同号")
    private String vendorContractCode;

    @ApiModelProperty(value = "系统SID-供应商预付台账流水")
    private Long recordAdvancePaymentSid;

    @ApiModelProperty(value = "sid数组")
    private Long[] recordAdvancePaymentSidList;

    @ApiModelProperty(value = "系统SID-供应商")
    private Long vendorSid;

    @ApiModelProperty(value = "系统SID-供应商")
    private Long[] vendorSidList;

    @ApiModelProperty(value = "产品季")
    private Long productSeasonSid;

    @ApiModelProperty(value = "产品季")
    private Long[] productSeasonSidList;

    @ApiModelProperty(value = "采购员")
    private String buyer;

    @ApiModelProperty(value = "采购员")
    private String[] buyerList;

    @ApiModelProperty(value = "预付款结算方式（数据字典的键值或配置档案的编码）")
    private String settleMode;

    @ApiModelProperty(value = "预付款结算方式（数据字典的键值或配置档案的编码）")
    private String[] settleModeList;

    @ApiModelProperty(value = "物料类型")
    private String materialType;

    @ApiModelProperty(value = "物料类型")
    private String[] materialTypeList;

    @ApiModelProperty(value = "核销状态")
    private String clearStatus;

    @ApiModelProperty(value = "核销状态")
    private String[] clearStatusList;

    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String[] creatorAccountList;
}
