package com.platform.ems.domain.dto.request.financial;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.ems.domain.FinPurchaseInvoiceAttachment;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 *  采购发票详情数据接收实体 FinPurchaseInvoiceInfoRequest
 *
 * @author chenkaiwen
 * @date 2021-07-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel
public class FinPurchaseInvoiceInfoRequest extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购发票sid")
    private Long purchaseInvoiceSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购发票号")
    private Long purchaseInvoiceCode;

    /** s_con_invoice_type */
    @NotBlank(message = "发票类型不能为空")
    @ApiModelProperty(value = "发票类型（配置档案）")
    private String invoiceType;

    @NotNull(message = "供应商不能为空")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商")
    private Long vendorSid;

    @NotNull(message = "公司不能为空")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-公司")
    private Long companySid;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "开票日期")
    private Date invoiceDate;

    @ApiModelProperty(value = "物料类型")
    private String materialType;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-产品季")
    private Long productSeasonSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购合同号/协议号")
    private Long purchaseContractSid;

    /** s_con_purchase_org  */
    @ApiModelProperty(value = "采购组织（配置档案）")
    private String purchaseOrg;

    @ApiModelProperty(value = "开发票维度（数据字典的键值）")
    private String invoiceDimension;

    @Length(max = 20, message = "发票号长度不能大于20")
    @ApiModelProperty(value = "纸质发票号")
    private String invoiceNum;

    @Length(max = 20, message = "发票代码长度不能大于20")
    @ApiModelProperty(value = "发票代码")
    private String invoiceCode;

    @NotNull(message = "税率不能为空")
    @ApiModelProperty(value = "税率（存值，即：不含百分号，如20%，就存0.2）")
    private BigDecimal taxRate;

    /** s_con_invoice_category */
    @ApiModelProperty(value = "发票类别（配置档案）")
    private String invoiceCategory;

    @ApiModelProperty(value = "异常确认状态（数据字典的键值）")
    private String exceptionConfirmFlag;

    @ApiModelProperty(value = "发票签收状态（数据字典的键值）")
    private String signFlag;

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

    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    @Valid
    @ApiModelProperty(value = "采购发票-明细对象")
    private List<FinPurchaseInvoiceItemInfoRequest> itemList;

    @Valid
    @ApiModelProperty(value = "采购发票-折扣对象")
    private List<FinPurchaseInvoiceDiscountInfoRequest> discountList;

    @Valid
    @ApiModelProperty(value = "采购发票-附件对象")
    private List<FinPurchaseInvoiceAttachment> attachmentList;

    @ApiModelProperty(value = "数据源系统（数据字典的键值）")
    private String dataSourceSys;

}
