package com.platform.ems.domain.dto.response.financial;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 *  销售发票查询列表返回实体 FinSaleInvoiceListResponse
 *
 * @author chenkaiwen
 * @date 2021-07-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel
public class FinSaleInvoiceListResponse extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-销售发票")
    private Long saleInvoiceSid;

    @ApiModelProperty(value = "sid数组")
    private Long[] saleInvoiceSidList;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售发票号")
    private Long saleInvoiceCode;

    @ApiModelProperty(value = "客户")
    private String customerName;

    @ApiModelProperty(value = "客户")
    private String customerShortName;

    @ApiModelProperty(value = "公司")
    private String companyName;

    @ApiModelProperty(value = "公司")
    private String companyShortName;

    @ApiModelProperty(value = "产品季")
    private String productSeasonName;

    @ApiModelProperty(value = "发票类型名称")
    private String invoiceTypeName;

    @ApiModelProperty(value = "发票类别名称")
    private String invoiceCategoryName;

    @ApiModelProperty(value = "发票签收状态")
    private String sendFlag;

    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @ApiModelProperty(value = "物料类型")
    private String materialTypeName;

    @ApiModelProperty(value = "销售组织")
    private String saleOrgName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "开票日期")
    private Date invoiceDate;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "总账日期")
    private Date generalLedgerDate;

    @ApiModelProperty(value = "发票号码")
    private String invoiceNum;

    @ApiModelProperty(value = "发票代码")
    private String invoiceCode;

    @ApiModelProperty(value = "是否已财务对账（数据字典的键值）")
    private String isFinanceVerify;

    @ApiModelProperty(value = "发票维度（配置档案）")
    private String invoiceDimension;

    @ApiModelProperty(value = "发票维度")
    private String invoiceDimensionName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售合同号/协议号")
    private Long saleContractSid;

    @ApiModelProperty(value = "币种（数据字典的键值）")
    private String currency;

    @ApiModelProperty(value = "货币单位（数据字典的键值）")
    private String currencyUnit;

    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccountName;

    /** 创建时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

    @ApiModelProperty(value = "确认人账号（用户名称）")
    private String confirmerAccountName;

    @ApiModelProperty(value = "确认人账号（用户名称）")
    private String confirmerAccount;

    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccountName;

    /** 更新时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    @ApiModelProperty(value = "数据源系统（数据字典的键值）")
    private String dataSourceSys;

}
