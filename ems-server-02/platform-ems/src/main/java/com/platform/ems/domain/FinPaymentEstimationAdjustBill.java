package com.platform.ems.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableId;

import com.platform.common.core.domain.EmsBaseEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

import lombok.experimental.Accessors;

/**
 * 应付暂估调价量单对象 s_fin_payment_estimation_adjust_bill
 *
 * @author chenkw
 * @date 2022-01-10
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_fin_payment_estimation_adjust_bill")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FinPaymentEstimationAdjustBill extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-应付暂估调价量单
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-应付暂估调价量单")
    private Long paymentEstimationAdjustBillSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] paymentEstimationAdjustBillSidList;

    /**
     * 应付暂估调价量单号
     */
    @Excel(name = "应付暂估调价量单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "应付暂估调价量单号")
    private Long paymentEstimationAdjustBillCode;

    /**
     * 系统SID-供应商
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商")
    private Long vendorSid;

    @ApiModelProperty(value = "供应商编码")
    private String vendorCode;

    @TableField(exist = false)
    @Excel(name = "供应商")
    @ApiModelProperty(value = "供应商名称")
    private String vendorName;

    /**
     * 系统SID-公司
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-公司")
    private Long companySid;

    @ApiModelProperty(value = "公司编码")
    private String companyCode;

    @TableField(exist = false)
    @Excel(name = "公司")
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    /**
     * 单据类型编码code
     */
    @ApiModelProperty(value = "单据类型编码code")
    private String documentType;

    @TableField(exist = false)
    @Excel(name = "单据类型")
    @ApiModelProperty(value = "单据类型编码code")
    private String documentTypeName;

    /**
     * 业务类型编码code
     */
    @ApiModelProperty(value = "业务类型编码code")
    private String businessType;

    @TableField(exist = false)
    @ApiModelProperty(value = "业务类型编码code")
    private String businessTypeName;

    /**
     * 单据日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "单据日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "单据日期")
    private Date documentDate;

    /**
     * 系统SID-产品季
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-产品季")
    private Long productSeasonSid;

    @TableField(exist = false)
    @Excel(name = "下单季")
    @ApiModelProperty(value = "产品季名称")
    private String productSeasonName;

    /**
     * 处理状态（数据字典的键值或配置档案的编码）
     */
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    @ApiModelProperty(value = "调价/调量说明")
    private String estimationAdjustRemark;

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

    /**
     * 创建人账号（用户名称）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    @TableField(exist = false)
    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人账号（用户昵称）")
    private String creatorAccountName;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /**
     * 更新人账号（用户名称）
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

    @TableField(exist = false)
    @Excel(name = "更新人")
    @ApiModelProperty(value = "更新人账号（用户昵称）")
    private String updaterAccountName;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 确认人账号（用户名称）
     */
    @ApiModelProperty(value = "确认人账号（用户名称）")
    private String confirmerAccount;

    @TableField(exist = false)
    @Excel(name = "确认人")
    @ApiModelProperty(value = "确认人账号（用户昵称）")
    private String confirmerAccountName;

    /**
     * 确认时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "确认时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    @TableField(exist = false)
    @ApiModelProperty(value = "价格(含税)（新）")
    private BigDecimal priceTaxNewTotal;

    @TableField(exist = false)
    @ApiModelProperty(value = "数量（新）")
    private BigDecimal quantityNewTotal;

    @TableField(exist = false)
    @ApiModelProperty(value = "应付暂估调价量单-明细对象")
    private List<FinPaymentEstimationAdjustBillItem> itemList;

    @TableField(exist = false)
    @ApiModelProperty(value = "应付暂估调价量单-附件对象")
    private List<FinPaymentEstimationAdjustBillAttach> attachmentList;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-供应商多选下拉框")
    private Long[] vendorSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-公司多选下拉框")
    private Long[] companySidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-产品季多选下拉框")
    private Long[] productSeasonSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态多选下拉框")
    private String[] handleStatusList;

    /**
     * 操作类型提交审批驳回
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "操作类型提交TJ审批SPTG驳回SPBH")
    private String operateType;
}
