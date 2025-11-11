package com.platform.ems.domain.dto.request.form;

import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 销售开票明细报表 FinSaleInvoiceItemFormRequest
 *
 * @author chenkaiwen
 * @date 2021-07-12
 */
@Data
@Accessors(chain = true)
@ApiModel
public class FinSaleInvoiceItemFormRequest extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @ApiModelProperty(value = "销售开票记录号")
    private Long saleInvoiceCode;

    @ApiModelProperty(value = "客户")
    private Long customerSid;

    @ApiModelProperty(value = "发票类型")
    private String invoiceType;

    @ApiModelProperty(value = "发票类别")
    private String invoiceCategory;

    @ApiModelProperty(value = "公司")
    private Long companySid;

    @ApiModelProperty(value = "产品季")
    private Long productSeasonSid;

    @ApiModelProperty(value = "物料类型（数据字典的键值）")
    private String materialType;

    @ApiModelProperty(value = "销售组织")
    private String saleOrg;

    @ApiModelProperty(value = "创建人账号（user_name）")
    private String creatorAccount;

    @ApiModelProperty(value = "发票签收/寄出状态（数据字典的键值）")
    private String sendFlag;

    @ApiModelProperty(value = "异常确认状态（数据字典的键值）")
    private String exceptionConfirmFlag;

    @ApiModelProperty(value = "处理状态（数据字典的键值）")
    private String handleStatus;

    @ApiModelProperty(value = "发票号")
    private String invoiceNum;

    @ApiModelProperty(value = "发票代码")
    private String inoviceCode;

    @ApiModelProperty(value = "数据源系统（数据字典的键值）")
    private String dataSourceSys;

    @ApiModelProperty(value = "系统SID-公司")
    private Long[] companySidList;

    @ApiModelProperty(value = "系统SID-客户")
    private Long[] customerSidList;

    @ApiModelProperty(value = "系统SID-产品季")
    private Long[] productSeasonSidList;

    @ApiModelProperty(value = "处理状态")
    private String[] handleStatusList;

    @ApiModelProperty(value = "发票类别")
    private String[] invoiceCategoryList;

    @ApiModelProperty(value = "发票类型")
    private String[] invoiceTypeList;

    @ApiModelProperty(value = "物料类型")
    private String[] materialTypeList;

    @ApiModelProperty(value = "销售组织")
    private String[] saleOrgList;
}
