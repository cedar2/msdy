package com.platform.ems.domain.dto.request.form;

import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 客户待收预收流水报表 FinRecordAdvanceReceiptFormRequest
 *
 * @author chenkaiwen
 * @date 2021-09-16
 */
@Data
@Accessors(chain = true)
@ApiModel
public class FinRecordAdvanceReceiptFormRequest extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @ApiModelProperty(value = "数据源系统（数据字典的键值）")
    private String dataSourceSys;

    @ApiModelProperty(value = "销售合同号")
    private String saleContractCode;

    @ApiModelProperty(value = "销售订单号")
    private Long saleOrderCode;

    @ApiModelProperty(value = "客方合同号")
    private String customerContractCode;

    @ApiModelProperty(value = "系统SID-客户预付台账流水")
    private Long recordAdvanceReceiptSid;

    @ApiModelProperty(value = "sid数组")
    private Long[] recordAdvanceReceiptSidList;

    @ApiModelProperty(value = "系统SID-客户")
    private Long customerSid;

    @ApiModelProperty(value = "系统SID-客户")
    private Long[] customerSidList;

    @ApiModelProperty(value = "产品季")
    private Long productSeasonSid;

    @ApiModelProperty(value = "产品季")
    private Long[] productSeasonSidList;

    @ApiModelProperty(value = "销售员")
    private String salePerson;

    @ApiModelProperty(value = "销售员")
    private String[] salePersonList;

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
