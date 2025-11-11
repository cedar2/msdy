package com.platform.ems.domain;

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

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.experimental.Accessors;

/**
 * 客户暂押款对象 s_fin_customer_funds_freeze_bill
 *
 * @author chenkw
 * @date 2021-09-22
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_fin_customer_funds_freeze_bill")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FinCustomerFundsFreezeBill extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-客户暂押款(释放)单
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-客户暂押款(释放)单")
    private Long fundsFreezeBillSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] fundsFreezeBillSidList;
    /**
     * 客户暂押款(释放)单号
     */

    @Excel(name = "客户暂押款(释放)单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "客户暂押款(释放)单号")
    private Long fundsFreezeBillCode;

    /**
     * 系统SID-客户
     */
    @NotNull(message = "客户不能为空！")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-客户")
    private Long customerSid;

    @ApiModelProperty(value = "客户")
    private String customerCode;

    /**
     * 系统SID-公司档案
     */
    @NotNull(message = "公司不能为空！")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-公司档案")
    private Long companySid;

    @ApiModelProperty(value = "公司")
    private String companyCode;

    @Excel(name = "单据类型")
    @TableField(exist = false)
    @ApiModelProperty(value = "单据类型")
    private String documentTypeName;

    @Excel(name = "客户")
    @TableField(exist = false)
    @ApiModelProperty(value = "客户")
    private String customerName;

    @Excel(name = "下单季")
    @TableField(exist = false)
    @ApiModelProperty(value = "产品季")
    private String productSeasonName;

    @Excel(name = "销售员")
    @TableField(exist = false)
    @ApiModelProperty(value = "销售员")
    private String salePersonName;

    /**
     * 解冻状态  20220419 改为释放状态
     */
    @Excel(name = "释放状态", dictType = "s_unfreeze_status")
    @ApiModelProperty(value = "解冻状态")
    private String unfreezeStatus;

    @Excel(name = "公司")
    @TableField(exist = false)
    @ApiModelProperty(value = "公司")
    private String companyName;

    /**
     * 单据日期
     */
    @NotNull(message = "请填写单据日期！")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "单据日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "单据日期")
    private Date documentDate;

    /**
     * 单据类型编码code
     */
    @NotBlank(message = "请先选择单据类型！")
    @ApiModelProperty(value = "单据类型编码code")
    private String documentType;

    /**
     * 业务类型编码code
     */
    @ApiModelProperty(value = "业务类型编码code")
    private String businessType;

    @TableField(exist = false)
    @ApiModelProperty(value = "业务类型编码code")
    private String businessTypeName;

    @TableField(exist = false)
    @ApiModelProperty(value = "业务类型编码code")
    private String[] businessTypeList;

    /**
     * 公司品牌sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司品牌sid")
    private Long companyBrandSid;

    /**
     * 业务渠道（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "业务渠道（数据字典的键值或配置档案的编码）")
    private String businessChannel;

    /**
     * 系统SID-产品季
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-产品季")
    private Long productSeasonSid;

    /**
     * 销售组织（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "销售组织（数据字典的键值或配置档案的编码）")
    private String saleOrg;

    /**
     * 销售员（用户名称）
     */
    @ApiModelProperty(value = "销售员（用户名称）")
    private String salePerson;

    /**
     * 物料类型（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "物料类型（数据字典的键值或配置档案的编码）")
    private String materialType;

    /**
     * 币种（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "币种（数据字典的键值或配置档案的编码）")
    private String currency;

    /**
     * 货币单位（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "货币单位（数据字典的键值或配置档案的编码）")
    private String currencyUnit;

    /**
     * 处理状态（数据字典的键值或配置档案的编码）
     */
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
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

    /**
     * 创建人账号（用户名称）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    @TableField(exist = false)
    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人")
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
    @ApiModelProperty(value = "修改人")
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
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "确认人账号（用户名称）")
    private String confirmerAccount;

    @TableField(exist = false)
    @ApiModelProperty(value = "确认人")
    private String confirmerAccountName;

    /**
     * 确认时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    @Valid
    @TableField(exist = false)
    @ApiModelProperty(value = "暂押款明细表")
    private List<FinCustomerFundsFreezeBillItem> itemList;

    @Valid
    @TableField(exist = false)
    @ApiModelProperty(value = "暂押款附件表")
    private List<FinCustomerFundsFreezeBillAttach> attachmentList;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-客户（下拉框 多选）")
    private Long[] customerSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-公司档案（下拉框 多选）")
    private Long[] companySidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-产品季（下拉框 多选）")
    private Long[] productSeasonSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售员（下拉框 多选）")
    private String[] salePersonList;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态（数据字典 多选）")
    private String[] handleStatusList;

    @TableField(exist = false)
    @ApiModelProperty(value = "解冻状态（下拉框 多选")
    private String[] unfreezeStatusList;

    /**
     * 操作类型提交审批驳回
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "操作类型提交TJ审批SPTG驳回SPBH")
    private String operateType;
}
