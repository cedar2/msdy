package com.platform.ems.domain.dto.request.form;

import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 采购发票明细报表 FinPurchaseInvoiceItemFormRequest
 *
 * @author chenkaiwen
 * @date 2021-07-12
 */
@Data
@Accessors(chain = true)
@ApiModel
public class FinPurchaseInvoiceItemFormRequest extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @ApiModelProperty(value = "采购发票记录号")
    private Long purchaseInvoiceCode;

    @ApiModelProperty(value = "供应商")
    private Long vendorSid;

    @ApiModelProperty(value = "发票类型")
    private String invoiceTypeName;

    @ApiModelProperty(value = "发票类别")
    private String invoiceCategoryName;

    @ApiModelProperty(value = "公司")
    private Long companySid;

    @ApiModelProperty(value = "产品季")
    private Long productSeasonSid;

    @ApiModelProperty(value = "物料类型（数据字典的键值）")
    private String materialType;

    @ApiModelProperty(value = "采购组织")
    private String purchaseOrg;

    @ApiModelProperty(value = "创建人账号（user_name）")
    private String creatorAccount;

    @ApiModelProperty(value = "发票签收/寄出状态（数据字典的键值）")
    private String signFlag;

    @ApiModelProperty(value = "异常确认状态（数据字典的键值）")
    private String exceptionConfirmFlag;

    @ApiModelProperty(value = "处理状态（数据字典的键值）")
    private String handleStatus;

    @ApiModelProperty(value = "发票号")
    private String invoiceNum;

    @ApiModelProperty(value = "发票代码")
    private String invoiceCode;

    @ApiModelProperty(value = "数据源系统（数据字典的键值）")
    private String dataSourceSys;

    @ApiModelProperty(value = "系统SID-公司")
    private Long[] companySidList;

    @ApiModelProperty(value = "系统SID-供应商")
    private Long[] vendorSidList;

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

    @ApiModelProperty(value = "采购组织")
    private String[] purchaseOrgList;
}
