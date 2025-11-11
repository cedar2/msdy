package com.platform.ems.domain;

import java.math.BigDecimal;
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
 * 外发加工单-明细对象 s_pur_outsource_purchase_order_item
 *
 * @author linhongwei
 * @date 2021-05-17
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_pur_outsource_purchase_order_item")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PurOutsourcePurchaseOrderItem extends EmsBaseEntity{

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**delivery_status
     * 系统SID-外发加工单明细
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-外发加工单明细")
    private Long outsourcePurchaseOrderItemSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] outsourcePurchaseOrderItemSidList;
    /**
     * 系统SID-外发加工单
     */
    @Excel(name = "系统SID-外发加工单")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-外发加工单")
    private Long outsourcePurchaseOrderSid;

    /**
     * 系统SID-商品&物料&服务
     */
    @Excel(name = "系统SID-商品&物料&服务")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品&物料&服务")
    private Long materialSid;

    /**
     * 系统SID-生产订单
     */
    @Excel(name = "系统SID-生产订单")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-生产订单")
    private Long manufactureOrderSid;

    /**
     * 生产订单号
     */
    @Excel(name = "生产订单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "生产订单号")
    private Long manufactureOrderCode;

    /** 系统自增长ID-生产订单-工序 */
    @Excel(name = "系统自增长ID-生产订单-工序")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-生产订单-工序")
    private Long manufactureOrderProcessSid;

    /**
     * 加工量
     */
    @Excel(name = "加工量")
    @ApiModelProperty(value = "加工量")
    private BigDecimal quantity;

    /**
     * 基本计量单位（数据字典的键值）
     */
    @Excel(name = "基本计量单位（数据字典的键值）")
    @ApiModelProperty(value = "基本计量单位（数据字典的键值）")
    private String unitBase;

    /**
     * 采购价计量单位（数据字典的键值）
     */
    @Excel(name = "采购价计量单位（数据字典的键值）")
    @ApiModelProperty(value = "采购价计量单位（数据字典的键值）")
    private String purchaseUnit;

    /**
     * 单位换算比例（采购价单位/基本单位）
     */
    @Excel(name = "单位换算比例（采购价单位/基本单位）")
    @ApiModelProperty(value = "单位换算比例（采购价单位/基本单位）")
    private BigDecimal unitConversionRate;

    /**
     * 采购价(不含税)
     */
    @Excel(name = "采购价(不含税)")
    @ApiModelProperty(value = "采购价(不含税)")
    private BigDecimal purchasePrice;

    /**
     * 采购价(含税)
     */
    @Excel(name = "采购价(含税)")
    @ApiModelProperty(value = "采购价(含税)")
    private BigDecimal purchasePriceTax;

    /**
     * 税率（存值，即：不含百分号，如20%，就存0.2）
     */
    @Excel(name = "税率（存值，即：不含百分号，如20%，就存0.2）")
    @ApiModelProperty(value = "税率（存值，即：不含百分号，如20%，就存0.2）")
    private BigDecimal taxRate;

    /**
     * 是否免费（数据字典的键值）
     */
    @Excel(name = "是否免费（数据字典的键值）")
    @ApiModelProperty(value = "是否免费（数据字典的键值）")
    private String freeFlag;

    /**
     * 合同交期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "合同交期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "合同交期")
    private Date contractDate;

    /**
     * 需求日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "需求日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "需求日期")
    private Date demandDate;

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
     * 行号
     */
    @Excel(name = "行号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "行号")
    private Long itemNum;

    /**
     * 处理状态（数据字典的键值）（删除）
     */
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "处理状态（数据字典的键值）（删除）", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值）（删除）")
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
     * 物料（商品/服务）编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "物料（商品/服务）编码")
    private String materialCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "SKU1的code")
    private String sku1Code;

    @TableField(exist = false)
    @ApiModelProperty(value = "SKU1的name")
    private String sku1Name;

    @TableField(exist = false)
    @ApiModelProperty(value = "SKU2的code")
    private String sku2Code;

    @TableField(exist = false)
    @ApiModelProperty(value = "SKU2的name")
    private String sku2Name;

    /**
     * 工价费用项名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "工价费用项名称")
    private String itemName;

    /**
     * 工价项编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "工价项编码")
    private String itemCode;

    /**
     * 外发加工单号
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "外发加工单号")
    private String outsourcePurchaseOrderCode;

    /**
     * 系统SID-供应商信息(加工商)
     */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商信息(加工商)")
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
    @Excel(name = "供应商名称")
    @ApiModelProperty(value = "供应商名称")
    private String vendorName;

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

    /** 工序编码(加工项) */
    @ApiModelProperty(value = "工序编码(加工项)")
    @TableField(exist = false)
    private String processCode;

    @TableField(exist = false)
    @Excel(name = "工序名称")
    @ApiModelProperty(value = "工序名称")
    private String processName;

    /**
     * 单据类型编码
     */
    @TableField(exist = false)
    @Excel(name = "单据类型编码")
    @ApiModelProperty(value = "单据类型编码")
    private String documentType;

    /**
     * 业务类型编码
     */
    @TableField(exist = false)
    @Excel(name = "业务类型编码")
    @ApiModelProperty(value = "业务类型编码")
    private String businessType;

    /**
     * 下单员（用户名称）
     */
    @TableField(exist = false)
    @Excel(name = "下单员（用户名称）")
    @ApiModelProperty(value = "下单员（用户名称）")
    private String buyer;

    /** 销售订单号 */
    @TableField(exist = false)
    @ApiModelProperty(value = "销售订单号")
    private String salesOrderCode;

    /**
     * 系统SID-产品季档案
     */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-产品季档案")
    private Long productSeasonSid;

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

    /**
     * 单据日期
     */
    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "单据日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "单据日期")
    private Date documentDate;

    /** 系统自增长ID-采购合同 */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-采购合同")
    private Long purchaseContractSid;

    /** 采购合同号 */
    @TableField(exist = false)
    @ApiModelProperty(value = "采购合同号")
    private String purchaseContractCode;

    /** 单据类型编码list */
    @TableField(exist = false)
    @ApiModelProperty(value = "单据类型编码list")
    private String[] documentTypeList;

    /** 业务类型编码list */
    @TableField(exist = false)
    @ApiModelProperty(value = "业务类型编码list")
    private String[] businessTypeList;

    /**
     * 系统SID-供应商信息(加工商)list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-供应商信息(加工商)list")
    private Long[] vendorSidList;

    /** 系统自增长ID-工序list */
    @TableField(exist = false)
    @ApiModelProperty(value = "系统自增长ID-工序list")
    private Long[] processSidList;

    /** 系统自增长ID-工厂list */
    @TableField(exist = false)
    @ApiModelProperty(value = "系统自增长ID-工厂list")
    private Long[] plantSidList;

    /** 处理状态list */
    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态list")
    private String[] handleStatusList;

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
