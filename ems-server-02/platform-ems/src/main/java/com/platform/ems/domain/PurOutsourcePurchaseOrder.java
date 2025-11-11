package com.platform.ems.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.core.domain.BaseEntity;
import com.platform.common.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableId;
import java.util.List;
import com.platform.common.core.domain.EmsBaseEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import com.platform.common.core.domain.document.UserOperLog;
import lombok.Data;
import javax.validation.constraints.NotEmpty;
import lombok.experimental.Accessors;

/**
 * 外发加工单对象 s_pur_outsource_purchase_order
 *
 * @author linhongwei
 * @date 2021-05-17
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_pur_outsource_purchase_order")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PurOutsourcePurchaseOrder extends EmsBaseEntity{

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-外发加工单
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-外发加工单")
    private Long outsourcePurchaseOrderSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] outsourcePurchaseOrderSidList;
    /**
     * 外发加工单号
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "外发加工单号")
    private Long outsourcePurchaseOrderCode;

    /**
     * 系统SID-供应商信息
     */
    @Excel(name = "系统SID-供应商信息")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商信息")
    private Long vendorSid;

    /**
     * 系统SID-公司档案
     */
    @Excel(name = "系统SID-公司档案")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-公司档案")
    private Long companySid;

    /**
     * 单据日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "单据日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "单据日期")
    private Date documentDate;

    /**
     * 下单员（用户名称）
     */
    @Excel(name = "下单员（用户名称）")
    @ApiModelProperty(value = "下单员（用户名称）")
    private String buyer;

    /**
     * 单据类型编码code
     */
    @Excel(name = "单据类型编码code")
    @ApiModelProperty(value = "单据类型编码code")
    private String documentType;

    /**
     * 业务类型编码code
     */
    @Excel(name = "业务类型编码code")
    @ApiModelProperty(value = "业务类型编码code")
    private String businessType;

    /**
     * 系统SID-产品季档案
     */
    @Excel(name = "系统SID-产品季档案")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-产品季档案")
    private Long productSeasonSid;

    /**
     * 下单批次sid
     */
    @Excel(name = "下单批次sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "下单批次sid")
    private Long orderBatch;

    /**
     * 物料类型（数据字典的键值）
     */
    @Excel(name = "物料类型（数据字典的键值）")
    @ApiModelProperty(value = "物料类型（数据字典的键值）")
    private String materialType;

    /**
     * 采购组织（数据字典的键值）
     */
    @Excel(name = "采购组织（数据字典的键值）")
    @ApiModelProperty(value = "采购组织（数据字典的键值）")
    private String purchaseOrg;

    /**
     * 采购组（数据字典的键值）
     */
    @Excel(name = "采购组（数据字典的键值）")
    @ApiModelProperty(value = "采购组（数据字典的键值）")
    private String purchaseGroup;

    /**
     * 系统SID-客户信息
     */
    @Excel(name = "系统SID-客户信息")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-客户信息")
    private Long customerSid;

    /**
     * 币种（数据字典的键值）
     */
    @Excel(name = "币种（数据字典的键值）")
    @ApiModelProperty(value = "币种（数据字典的键值）")
    private String currency;

    /**
     * 货币单位（数据字典的键值）
     */
    @Excel(name = "货币单位（数据字典的键值）")
    @ApiModelProperty(value = "货币单位（数据字典的键值）")
    private String currencyUnit;

    /**
     * 收货人
     */
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
     * 系统SID-采购合同
     */
    @Excel(name = "系统SID-采购合同")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-采购合同")
    private Long purchaseContractSid;

    /**
     * 供方订单号
     */
    @Excel(name = "供方订单号")
    @ApiModelProperty(value = "供方订单号")
    private String vendorOrderCode;

    /**
     * 交货状态（数据字典的键值）
     */
    @Excel(name = "交货状态（数据字典的键值）")
    @ApiModelProperty(value = "交货状态（数据字典的键值）")
    private String deliveryStatus;

    /**
     * 发料状态（数据字典的键值）
     */
    @Excel(name = "发料状态（数据字典的键值）")
    @ApiModelProperty(value = "发料状态（数据字典的键值）")
    private String materialIssueStatus;

    /**
     * 处理状态（数据字典的键值）
     */
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "处理状态（数据字典的键值）", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值）")
    private String handleStatus;

    /**
     * 创建人账号（用户名称）
     */
    @Excel(name = "创建人账号（用户名称）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

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
    @Excel(name = "更新人账号（用户名称）")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

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
    @Excel(name = "确认人账号（用户名称）")
    @ApiModelProperty(value = "确认人账号（用户名称）")
    private String confirmerAccount;

    /**
     * 确认时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "确认时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 数据源系统（数据字典的键值）
     */
    @Excel(name = "数据源系统（数据字典的键值）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值）")
    private String dataSourceSys;

    /**
     * 外发加工单sids
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "外发加工单sids")
    private List<Long> outsourcePurchaseOrderSids;

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
    @Excel(name = "供应商名称")
    @ApiModelProperty(value = "供应商名称")
    private String vendorName;

    /**
     * 供应商简称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "供应商简称")
    private String shortName;

    /**
     * 公司代码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "公司代码")
    private String companyCode;

    /**
     * 公司名称
     */
    @TableField(exist = false)
    @Excel(name = "公司名称")
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    /**
     * 产品季编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "产品季编码")
    private String productSeasonCode;

    /**
     * 产品季名称
     */
    @TableField(exist = false)
    @Excel(name = "产品季名称")
    @ApiModelProperty(value = "产品季名称")
    private String productSeasonName;

    /** 系统自增长ID-工厂 */
    @TableField(exist = false)
    @ApiModelProperty(value = "系统自增长ID-工厂")
    private String plantSid;

    /** 工厂编码*/
    @TableField(exist = false)
    @ApiModelProperty(value = "工厂编码")
    private String plantCode;

    /** 工厂名称*/
    @TableField(exist = false)
    @Excel(name = "工厂名称")
    @ApiModelProperty(value = "工厂名称")
    private String plantName;

    @TableField(exist = false)
    @ApiModelProperty(value = "采购合同号")
    private String purchaseContractCode;

    /**
     * 外发加工单-明细对象
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "外发加工单-明细对象")
    private List<PurOutsourcePurchaseOrderItem> purOutsourcePurchaseOrderItemList;

    /**
     * 外发加工单-附件对象
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "外发加工单-附件对象")
    private List<PurOutsourcePurchaseOrderAttachment> purOutsourcePurchaseOrderAttachmentList;

    /**
     * 系统SID-供应商信息
     */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商信息")
    private Long[] vendorSidList;

    /** 单据类型编码list */
    @TableField(exist = false)
    @ApiModelProperty(value = "单据类型编码list")
    private String[] documentTypeList;

    /** 业务类型编码list */
    @TableField(exist = false)
    @ApiModelProperty(value = "业务类型编码list")
    private String[] businessTypeList;

    /**
     * 物料（商品/服务）编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "物料（商品/服务）编码")
    private String materialCode;

    /**
     * 物料（商品/服务）名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "物料（商品/服务）名称")
    private String materialName;

    /**
     * 下单员名称
     */
    @TableField(exist = false)
    @Excel(name = "下单员名称")
    private String nickName;

    /** 系统自增长ID-工序（加工项） */
    @TableField(exist = false)
    @ApiModelProperty(value = "系统自增长ID-工序（加工项）")
    private Long[] processSidList;

    /**
     * 交货状态（数据字典的键值）list
     */
    @TableField(exist = false)
    @Excel(name = "交货状态（数据字典的键值）list")
    @ApiModelProperty(value = "交货状态（数据字典的键值）list")
    private String[] deliveryStatusList;

    /**
     * 发料状态（数据字典的键值）list
     */
    @TableField(exist = false)
    @Excel(name = "发料状态（数据字典的键值）list")
    @ApiModelProperty(value = "发料状态（数据字典的键值）list")
    private String[] materialIssueStatusList;

    /** 处理状态list */
    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态list")
    private String[] handleStatusList;

    /** 系统自增长ID-工厂list */
    @TableField(exist = false)
    @ApiModelProperty(value = "系统自增长ID-工厂list")
    private Long[] plantSidList;

    /**
     * 系统SID-产品季档案list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-产品季档案list")
    private Long[] productSeasonSidList;

    /** 合同交期从 */
    @TableField(exist = false)
    @ApiModelProperty(value = "合同交期从")
    private String contractBeginDate;

    /** 合同交期至 */
    @TableField(exist = false)
    @ApiModelProperty(value = "合同交期至")
    private String contractEndDate;

    /** 需求日期从 */
    @TableField(exist = false)
    @ApiModelProperty(value = "需求日期从")
    private String demandBeginDate;

    /** 需求日期至 */
    @TableField(exist = false)
    @ApiModelProperty(value = "需求日期至")
    private String demandEndDate;

}
