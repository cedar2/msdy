package com.platform.ems.domain.dto.response.form;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.core.domain.EmsBaseEntity;
import com.platform.ems.util.data.KeepTwoDecimalsSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 收款流水报表 FinBookReceiptPaymentFormResponse
 *
 * @author chenkaiwen
 * @date 2021-07-12
 */
@Data
@Accessors(chain = true)
@ApiModel
public class FinBookReceiptPaymentFormResponse  extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "流水号（收款）")
    private Long bookReceiptPaymentSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "流水号（收款）")
    private Long bookReceiptPaymentCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "收款单号")
    private Long receivableBillCode;

    @ApiModelProperty(value = "客户名称")
    private String customerName;

    @TableField(exist = false)
    @ApiModelProperty(value = "客户简称")
    private String customerShortName;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司简称")
    private String companyShortName;

    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @ApiModelProperty(value = "流水类型")
    private String bookTypeName;

    @ApiModelProperty(value = "流水来源类别")
    private String bookSourceCategoryName;

    @ApiModelProperty(value = "流水类型")
    private String bookType;

    @ApiModelProperty(value = "流水来源类别")
    private String bookSourceCategory;

    @ApiModelProperty(value = "核销状态")
    private String clearStatus;

    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "收款金额（含税）")
    private BigDecimal currencyAmountTaxSk;

    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "已核销金额（含税）")
    private BigDecimal currencyAmountTaxYhx;

    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "待核销金额（含税）")
    private BigDecimal currencyAmountTaxDhx;

    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "核销中金额（含税）")
    private BigDecimal currencyAmountTaxHxz;

    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    @ApiModelProperty(value = "币种")
    private String currency;

    @ApiModelProperty(value = "货币单位")
    private String currencyUnit;

    @ApiModelProperty(value = "税率")
    private String taxRateName;

    @ApiModelProperty(value = "经办人")
    private String agentName;

    @ApiModelProperty(value = "支付方式")
    private String payMethodName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "收款日期")
    private Date receivableDate;

    @ApiModelProperty(value = "行号")
    private Long itemNum;

    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccountName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @ApiModelProperty(value = "产品季名称")
    private String productSeasonName;

    @ApiModelProperty(value = "是否已财务对账（数据字典的键值或配置档案的编码）")
    private String isFinanceVerify;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "关联单据号")
    private Long referDocSid;

    @ApiModelProperty(value = "关联单据号")
    private String referDocCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "产品季")
    private Long productSeasonSid;

}
