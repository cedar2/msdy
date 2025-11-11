package com.platform.ems.domain.dto.request.financial;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 *  采购发票查询列表接收实体 FinPurchaseInvoiceListRequest
 *
 * @author chenkaiwen
 * @date 2021-07-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel
public class FinPurchaseInvoiceListRequest extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购发票sid")
    private Long purchaseInvoiceSid;

    @ApiModelProperty(value = "采购发票sid")
    private Long[] purchaseInvoiceSids;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购发票号")
    private Long purchaseInvoiceCode;

    @ApiModelProperty(value = "系统SID-供应商")
    private Long[] vendorSidList;

    @ApiModelProperty(value = "系统SID-公司")
    private Long[] companySidList;

    @ApiModelProperty(value = "系统SID-产品季")
    private Long[] productSeasonSidList;

    /** 配置档案 s_con_invoice_type */
    @ApiModelProperty(value = "发票类型（配置档案）")
    private String[] invoiceTypeList;

    /** 配置档案 s_con_invoice_category */
    @ApiModelProperty(value = "发票类别（配置档案）")
    private String[] invoiceCategoryList;

    /** 配置档案 s_con_material_type */
    @ApiModelProperty(value = "物料类型（配置档案）")
    private String[] materialTypeList;

    /** 配置档案 s_con_purchase_org */
    @ApiModelProperty(value = "采购组织（配置档案）")
    private String[] purchaseOrgList;

    /** 配置档案 s_con_invoice_dimension */
    @ApiModelProperty(value = "开票维度（配置档案）")
    private String[] invoiceDimensionList;

    @ApiModelProperty(value = "发票签收状态（数据字典的键值）")
    private String[] signFlagList;

    @ApiModelProperty(value = "异常确认状态（数据字典的键值）")
    private String[] exceptionConfirmFlagList;

    @ApiModelProperty(value = "处理状态（数据字典的键值）")
    private String[] handleStatusList;

    @ApiModelProperty(value = "发票号")
    private String invoiceNum;

    @ApiModelProperty(value = "发票代码")
    private String invoiceCode;

    @ApiModelProperty(value = "是否已财务对账（数据字典的键值）")
    private String isFinanceVerify;

    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    @ApiModelProperty(value = "数据源系统（数据字典的键值）")
    private String dataSourceSys;
}
