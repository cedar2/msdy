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
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 供应商调账单列表查询返回实体 FinVendorAccountAdjustBillListResponse
 *
 * @author chenkaiwen
 * @date 2021-07-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FinVendorAccountAdjustBillListResponse extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "客户调账单sid")
    private Long adjustBillSid;

    @Excel(name = "供应商调账单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "供应商调账单号")
    private Long adjustBillCode;

    @Excel(name = "供应商")
    @ApiModelProperty(value = "供应商")
    private String vendorName;

    @Excel(name = "公司")
    @ApiModelProperty(value = "公司")
    private String companyName;

    @Excel(name = "产品季")
    @ApiModelProperty(value = "产品季")
    private String productSeasonName;

    @Excel(name = "销售员")
    @ApiModelProperty(value = "销售员")
    private String buyerName;

    @Excel(name = "核销状态", dictType = "s_account_clear")
    @ApiModelProperty(value = "核销状态（数据字典）")
    private String clearStatus;

    @Excel(name = "单据日期", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "单据日期")
    private Date documentDate;

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
    @ApiModelProperty(value = "创建人")
    private String creatorAccountName;

    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @Excel(name = "更新人")
    @ApiModelProperty(value = "修改人")
    private String updaterAccountName;

    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "修改时间")
    private Date updateDate;

    @ApiModelProperty(value = "数据源系统（数据字典）")
    private String dataSourceSys;
}
