package com.platform.ems.domain.dto.response.financial;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.ems.domain.FinSaleInvoiceAttachment;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


/**
 *  销售发票详情数据返回实体 FinsaleInvoiceInfoResponse
 *
 * @author chenkaiwen
 * @date 2021-07-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FinSaleInvoiceInfoResponse extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售发票sid")
    private Long saleInvoiceSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售发票号")
    private Long saleInvoiceCode;

    /** s_con_invoice_type */
    @ApiModelProperty(value = "发票类型（配置档案）")
    private String invoiceType;

    @ApiModelProperty(value = "发票类型")
    private String invoiceTypeName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-客户")
    private Long customerSid;

    @ApiModelProperty(value = "客户")
    private String customerName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-公司")
    private Long companySid;

    @ApiModelProperty(value = "公司")
    private String companyName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "开票日期")
    private Date invoiceDate;

    @ApiModelProperty(value = "物料类型（配置档案）")
    private String materialType;

    @ApiModelProperty(value = "物料类型")
    private String materialTypeName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-产品季")
    private Long productSeasonSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售合同号/协议号")
    private Long saleContractSid;

    /** s_con_sale_org  */
    @ApiModelProperty(value = "销售组织（配置档案）")
    private String saleOrg;

    @ApiModelProperty(value = "销售组织")
    private String saleOrgName;

    @ApiModelProperty(value = "开发票维度（配置档案）")
    private String invoiceDimension;

    @ApiModelProperty(value = "开发票维度")
    private String invoiceDimensionName;

    @ApiModelProperty(value = "纸质发票号")
    private String invoiceNum;

    @ApiModelProperty(value = "发票代码")
    private String invoiceCode;

    @ApiModelProperty(value = "税率（存值，即：不含百分号，如20%，就存0.2）")
    private BigDecimal taxRate;

    @ApiModelProperty(value = "税率")
    private BigDecimal taxRateName;

    /** s_con_invoice_category */
    @ApiModelProperty(value = "发票类别（配置档案）")
    private String invoiceCategory;

    @ApiModelProperty(value = "发票类别")
    private String invoiceCategoryName;

    @ApiModelProperty(value = "异常确认状态（数据字典的键值）")
    private String exceptionConfirmFlag;

    @ApiModelProperty(value = "发票寄出状态（数据字典的键值）")
    private String sendFlag;

    @ApiModelProperty(value = "票面总金额(含税)")
    private BigDecimal totalCurrencyAmountTax;

    @ApiModelProperty(value = "票面总金额(不含税)")
    private BigDecimal totalCurrencyAmount;

    @ApiModelProperty(value = "票面总税额")
    private BigDecimal totalTaxAmount;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "总账日期")
    private Date generalLedgerDate;

    @ApiModelProperty(value = "币种（数据字典的）")
    private String currency;

    @ApiModelProperty(value = "货币单位（数据字典）")
    private String currencyUnit;

    @ApiModelProperty(value = "处理状态（数据字典）")
    private String handleStatus;

    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    @ApiModelProperty(value = "创建人账号（用户昵称）")
    private String creatorAccountName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @ApiModelProperty(value = "更新人账号")
    private String updaterAccount;

    @ApiModelProperty(value = "更新人账号（用户昵称）")
    private String updaterAccountName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    @ApiModelProperty(value = "销售发票-明细对象")
    private List<FinSaleInvoiceItemListResponse> itemList;

    @ApiModelProperty(value = "销售发票-折扣对象")
    private List<FinSaleInvoiceDiscountListResponse> discountList;

    @ApiModelProperty(value = "销售发票-附件对象")
    private List<FinSaleInvoiceAttachment> attachmentList;

    @ApiModelProperty(value = "数据源系统（数据字典的键值）")
    private String dataSourceSys;
}
