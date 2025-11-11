package com.platform.ems.domain.dto.request.financial;

import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 应收暂估列表接收实体 FinBookReceiptEstimationItemListRequest
 *
 * @author chenkaiwen
 * @date 2021-07-26
 */
@Data
@Accessors(chain = true)
@ApiModel
public class FinBookReceiptEstimationItemListRequest extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @ApiModelProperty(value = "系统SID-客户")
    private Long customerSid;

    @ApiModelProperty(value = "系统SID-公司档案")
    private Long companySid;

    @ApiModelProperty(value = "财务流水类型code")
    private String bookType;

    @ApiModelProperty(value = "核销状态")
    private String clearStatus;

    @ApiModelProperty(value = "用来过滤红蓝票正反向金额")
    private String invoiceType;

    @ApiModelProperty(value = "销售合同号")
    private String saleContractCode;

    @ApiModelProperty(value = "销售订单号")
    private String salesOrderCode;

    @ApiModelProperty(value = "采购交货单/销售发货单号")
    private String deliveryNoteCode;

    @ApiModelProperty(value = "是否已业务对账（数据字典的键值或配置档案的编码）")
    private String isBusinessVerify;

    @ApiModelProperty(value = "是否已财务对账（数据字典的键值或配置档案的编码）")
    private String isFinanceVerify;

    @ApiModelProperty(value = "数据源系统（数据字典的键值）")
    private String dataSourceSys;
}
