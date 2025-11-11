package com.platform.ems.domain.dto.request.form;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 应收暂估流水报表 FinBookReceiptEstimationFormRequest
 *
 * @author chenkaiwen
 * @date 2021-09-16
 */
@Data
@Accessors(chain = true)
@ApiModel
public class FinBookReceiptEstimationFormRequest extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @ApiModelProperty(value = "数据源系统（数据字典的键值）")
    private String dataSourceSys;

    @ApiModelProperty(value = "销售发货单号")
    private Long deliveryNoteCode;

    @ApiModelProperty(value = "系统SID-客户")
    private Long customerSid;

    @ApiModelProperty(value = "系统SID-公司档案")
    private Long companySid;

    @ApiModelProperty(value = "流水类型")
    private String bookType;

    @ApiModelProperty(value = "流水来源类别")
    private String bookSourceCategory;

    @ApiModelProperty(value = "业务类型")
    private String businessType;

    @ApiModelProperty(value = "单据类型")
    private String documentType;

    @ApiModelProperty(value = "产品季")
    private Long productSeasonSid;

    @ApiModelProperty(value = "销售员")
    private String salePerson;

    @ApiModelProperty(value = "销售组织")
    private String saleOrg;

    @ApiModelProperty(value = "核销状态")
    private String clearStatus;

    @ApiModelProperty(value = "核销状态（含税金额）")
    private String clearStatusMoney;

    @ApiModelProperty(value = "核销状态（数量）")
    private String clearStatusQuantity;

    @ApiModelProperty(value = "销售订单号")
    private Long saleOrderCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售合同号")
    private String saleContractCode;

    @ApiModelProperty(value = "系统SID-客户")
    private Long[] customerSidList;

    @ApiModelProperty(value = "系统SID-公司")
    private Long[] companySidList;

    @ApiModelProperty(value = "系统SID-产品季")
    private Long[] productSeasonSidList;

    @ApiModelProperty(value = "系统SID-流水类型")
    private String[] bookTypeList;

    @ApiModelProperty(value = "系统SID-流水来源类别")
    private String[] bookSourceCategoryList;

    @ApiModelProperty(value = "系统SID-核销状态")
    private String[] clearStatusMoneyList;
}
