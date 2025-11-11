package com.platform.ems.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * 申请单对象 s_req_purchase_require
 *
 * @author linhongwei
 * @date 2021-04-06
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_req_purchase_require")
public class ReqPurchaseRequire extends EmsBaseEntity {

    /**
     * 客户端口号
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /**
     * 系统ID-申请单
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-申请单")
    private Long purchaseRequireSid;

    /**
     * 申请单sids
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "申请单sids")
    private Long[] purchaseRequireSidList;

    /**
     * 申请单号
     */
    @Excel(name = "采购申请单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "申请单号")
    private String purchaseRequireCode;

    /**
     * 单据类型
     */
    @ApiModelProperty(value = "单据类型")
    private String documentType;

    /**
     * 单据类型名称
     */
    @TableField(exist = false)
    @Excel(name = "单据类型")
    @ApiModelProperty(value = "单据类型名称")
    private String documentTypeName;

    /**
     * 业务类型
     */
    @ApiModelProperty(value = "业务类型")
    private String businessType;

    /**
     * 业务类型名称
     */
    @TableField(exist = false)
    @Excel(name = "业务类型")
    @ApiModelProperty(value = "业务类型名称")
    private String businessTypeName;

    @Excel(name = "商品/物料类别", dictType = "s_material_category")
    @ApiModelProperty(value = "物料/商品类别")
    private String materialCategory;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料/商品类别多选")
    private String[] materialCategoryList;

    /**
     * 系统ID-公司档案
     */
    @NotNull(message = "公司不能为空")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-公司档案")
    private Long companySid;

    /**
     * 公司代码
     */
    @ApiModelProperty(value = "公司代码")
    private String companyCode;

    /**
     * 公司名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    /**
     * 公司名称
     */
    @TableField(exist = false)
    @Excel(name = "公司")
    @ApiModelProperty(value = "公司简称")
    private String companyShortName;

    /**
     * 公司品牌sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司品牌sid")
    private Long companyBrandSid;

    /**
     * 申请部门
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "申请部门")
    private Long requireDepartmentSid;

    @ApiModelProperty(value = "申请部门编码")
    private String requireDepartmentCode;

    /**
     * 部门名称
     */
    @TableField(exist = false)
    @Excel(name = "申请部门")
    @ApiModelProperty(value = "部门名称")
    private String requireDepartmentName;

    /**
     * 系统ID-产品季档案
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-产品季档案")
    private Long productSeasonSid;

    /**
     * 产品季编码
     */
    @ApiModelProperty(value = "产品季编码")
    private String productSeasonCode;

    /**
     * 产品季名称
     */
    @TableField(exist = false)
    @Excel(name = "需求季")
    @ApiModelProperty(value = "产品季名称")
    private String productSeasonName;

    /**
     * 单据日期
     */
    @NotNull(message = "单据日期不能为空")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "单据日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "单据日期")
    private Date documentDate;

    /**
     * 收货人
     */
    @Length(max = 30, message = "收货人长度不能超过30个字符")
    @Excel(name = "收货人")
    @ApiModelProperty(value = "收货人")
    private String consignee;

    /**
     * 收货人联系电话
     */
    @Excel(name = "收货人联系电话")
    @ApiModelProperty(value = "收货人联系电话")
    private String consigneePhone;

    /**
     * 收货地址
     */
    @Excel(name = "收货地址")
    @ApiModelProperty(value = "收货地址")
    private String consigneeAddr;

    /**
     * 处理状态
     */
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    @TableField(exist = false)
    @ApiModelProperty(value = "备注")
    private String importType;

    /**
     * 申请方类型（数据字典的键值）客户/供应商
     */
    @ApiModelProperty(value = "申请方类型（数据字典的键值）客户/供应商")
    private String requireOrgType;

    /**
     * 业务渠道/销售渠道（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "业务渠道/销售渠道（数据字典的键值或配置档案的编码）")
    private String businessChannel;

    /**
     * 申请方
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "申请方")
    private Long requireOrg;

    /**
     * 客户sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "客户sid")
    private Long customerSid;

    /**
     * 客户编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "客户编码")
    private String customerCode;

    /**
     * 客户名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "客户名称")
    private String customerName;

    /**
     * 客户名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "客户名称")
    private String customerShortName;

    /**
     * 供应商sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "供应商sid")
    private Long vendorSid;

    /**
     * 供应商编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "供应商编码")
    private String vendorCode;

    /**
     * 供应商名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "供应商名称")
    private String vendorName;

    /**
     * 供应商简称称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "供应商简称")
    private String vendorShortName;

    /**
     * 供货模式（数据字典的键值）
     */
    @ApiModelProperty(value = "供货模式（数据字典的键值）")
    private String supplyType;

    /**
     * 甲供料方式（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "甲供料方式（数据字典的键值或配置档案的编码）")
    private String rawMaterialMode;

    @TableField(exist = false)
    @ApiModelProperty(value = "甲供料方式list")
    private String[] rawMaterialModeList;

    /**
     * 执行状态（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "执行状态（数据字典的键值或配置档案的编码）")
    private String executeStatus;

    /**
     * 数据来源类别（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "数据来源类别（数据字典的键值或配置档案的编码）")
    private String referDocCategory;

    /**
     * 当前审批节点名称
     */
    @ApiModelProperty(value = "当前审批节点名称")
    @TableField(exist = false)
    private String approvalNode;

    @ApiModelProperty(value = "审批节点list")
    @TableField(exist = false)
    private String[] approvalNodeList;

    /**
     * 当前审批人
     */
    @ApiModelProperty(value = "当前审批人")
    @TableField(exist = false)
    private String approvalUserName;

    /**
     * 创建人账号
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    /**
     * 创建人名称
     */
    @TableField(exist = false)
    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人名称")
    private String creatorAccountName;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /**
     * 更新人账号
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccount;

    /**
     * 更新人账号
     */
    @TableField(exist = false)
    @Excel(name = "更改人")
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccountName;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更改日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 确认人账号
     */
    @ApiModelProperty(value = "确认人账号")
    private String confirmerAccount;

    /**
     * 确认人账号
     */
    @TableField(exist = false)
    @Excel(name = "确认人")
    @ApiModelProperty(value = "确认人账号")
    private String confirmerAccountName;

    /**
     * 确认时间
     */
    @Excel(name = "确认日期", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认日期")
    private Date confirmDate;

    /**
     * 数据源系统
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;

    @ApiModelProperty(value = "单据日期开始时间")
    @TableField(exist = false)
    private String documentBeginTime;

    @ApiModelProperty(value = "单据日期结束时间")
    @TableField(exist = false)
    private String documentEndTime;

    /**
     * 单据类型编码list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "单据类型编码list")
    private String[] documentTypeList;

    /**
     * 业务类型编码list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "业务类型编码list")
    private String[] businessTypeList;

    /**
     * 申请方list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "申请方list")
    private Long[] requireOrgList;

    /**
     * 系统ID-公司档案list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "系统ID-公司档案list")
    private Long[] companySidList;

    /**
     * 申请部门list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "申请部门list")
    private Long[] requireDepartmentSidList;

    /**
     * 系统ID-产品季档案list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "系统ID-产品季档案list")
    private Long[] productSeasonSidList;

    /**
     * 供货模式（数据字典的键值）list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "供货模式（数据字典的键值）list")
    private String[] supplyTypeList;

    /**
     * 处理状态list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态list")
    private String[] handleStatusList;

    @TableField(exist = false)
    @ApiModelProperty(value = "最大行号")
    private Integer maxItemNum;

    @TableField(exist = false)
    @ApiModelProperty(value = "作废说明")
    private String cancelRemark;

    /**
     * 申请单明细对象
     */
    @Valid
    @TableField(exist = false)
    @ApiModelProperty(value = "申请单明细对象")
    private List<ReqPurchaseRequireItem> reqPurchaseRequireItemList;

    /**
     * 申请单附件对象
     */
    @Valid
    @TableField(exist = false)
    @ApiModelProperty(value = "申请单附件对象")
    private List<ReqPurchaseRequireAttachment> attachmentList;

    /**
     * 是否跳过明细申请量”小于“已转采购量”的校验
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "是否跳过明细申请量”小于“已转采购量”的校验")
    private boolean jumpJudgeQuantity;
}
