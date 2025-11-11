package com.platform.ems.domain.dto.request.financial;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.ems.domain.FinSaleInvoiceAttachment;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
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
 * 销售发票详情数据接收实体 FinsaleInvoiceInfoRequest
 *
 * @author chenkaiwen
 * @date 2021-07-26
 */
@Data
@Accessors(chain = true)
@ApiModel
public class FinSaleInvoiceInfoRequest extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售发票sid")
    private Long saleInvoiceSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售发票号")
    private Long saleInvoiceCode;

    /**
     * s_con_invoice_type
     */
    @NotBlank(message = "发票类型不能为空")
    @ApiModelProperty(value = "发票类型（配置档案）")
    private String invoiceType;

    @NotNull(message = "客户不能为空")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-客户")
    private Long customerSid;

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
    @ApiModelProperty(value = "销售合同号/协议号")
    private Long saleContractSid;

    /**
     * s_con_sale_org
     */
    @ApiModelProperty(value = "销售组织（配置档案）")
    private String saleOrg;

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

    /**
     * s_con_invoice_category
     */
    @ApiModelProperty(value = "发票类别（配置档案）")
    private String invoiceCategory;

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
    @ApiModelProperty(value = "销售发票-明细对象")
    private List<FinSaleInvoiceItemInfoRequest> itemList;

    @Valid
    @ApiModelProperty(value = "销售发票-折扣对象")
    private List<FinSaleInvoiceDiscountInfoRequest> discountList;

    @Valid
    @ApiModelProperty(value = "销售发票-附件对象")
    private List<FinSaleInvoiceAttachment> attachmentList;

    @ApiModelProperty(value = "数据源系统（数据字典的键值）")
    private String dataSourceSys;

}
