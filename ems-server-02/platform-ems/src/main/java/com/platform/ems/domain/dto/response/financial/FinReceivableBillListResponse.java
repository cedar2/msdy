package com.platform.ems.domain.dto.response.financial;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 收款单列表返回实体 FinReceivableBillListResponse
 *
 * @author chenkaiwen
 * @date 2021-07-26
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FinReceivableBillListResponse extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "收款单")
    private Long receivableBillSid;

    @Excel(name = "收款单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "收款单号")
    private Long receivableBillCode;

    @Excel(name = "客户")
    @ApiModelProperty(value = "客户名称")
    private String customerName;

    @Excel(name = "公司")
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @Excel(name = "产品季")
    @ApiModelProperty(value = "产品季名称")
    private String productSeasonName;

    @Excel(name = "业务类型")
    @ApiModelProperty(value = "业务类型名称")
    private String businessTypeName;

    @Excel(name = "款项类别")
    @ApiModelProperty(value = "款项类别名称")
    private String accountCategoryName;

    @Excel(name = "经办人")
    @ApiModelProperty(value = "经办人（用户昵称）")
    private String agentName;

    @ApiModelProperty(value = "支付方式名称")
    private String payMethodName;

    @Excel(name = "收款主体")
    @ApiModelProperty(value = "收款主体名称")
    private String receivableCompanyName;

    @ApiModelProperty(value = "本次收款金额小计(元)")
    private BigDecimal currencyAmountTax;

    @Excel(name = "到账状态", dictType = "s_receipt_payment_status")
    @ApiModelProperty(value = "收款状态（数据字典）")
    private String receiptPaymentStatus;

    @ApiModelProperty(value = "销售组织")
    private String saleOrgName;

    @Excel(name = "单据日期", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "单据日期")
    private Date documentDate;

    @Excel(name = "是否已财务对账", dictType = "s_yesno_flag")
    @ApiModelProperty(value = "是否已财务对账（数据字典的键值）")
    private String isFinanceVerify;

    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典）")
    private String handleStatus;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    @Excel(name = "当前审批节点名称")
    @ApiModelProperty(value = "当前审批节点名称")
    @TableField(exist = false)
    private String approvalNode;

    @Excel(name = "当前审批人")
    @ApiModelProperty(value = "当前审批人")
    @TableField(exist = false)
    private String approvalUserName;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人账号（用户昵称）")
    private String creatorAccountName;

    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @Excel(name = "更新人")
    @ApiModelProperty(value = "更新人账号（用户昵称）")
    private String updaterAccountName;

    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    @ApiModelProperty(value = "数据源系统（数据字典的键值）")
    private String dataSourceSys;

}
