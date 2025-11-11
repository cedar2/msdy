package com.platform.ems.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 外发加工发料单-明细对象 s_del_outsource_material_issue_note_item
 *
 * @author linhongwei
 * @date 2021-05-17
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_del_outsource_material_issue_note_item")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DelOutsourceMaterialIssueNoteItem extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-外发加工发料单明细
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-外发加工发料单明细")
    private Long issueNoteItemSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] issueNoteItemSidList;

    /**
     * 系统SID-外发加工发料单
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-外发加工发料单")
    private Long issueNoteSid;

    @Excel(name = "外发加工发料单号")
    @TableField(exist = false)
    @ApiModelProperty(value = "外发加工发料单单号")
    private Long issueNoteCode;

    /**
     * 工厂名称
     */
    @TableField(exist = false)
    @Excel(name = "工厂")
    @ApiModelProperty(value = "工厂名称")
    private String plantName;

    /**
     * 生产订单号
     */
    @Excel(name = "生产订单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "生产订单号")
    private Long manufactureOrderCode;

    /**
     * 供应商名称
     */
    @Excel(name = "加工商")
    @TableField(exist = false)
    @ApiModelProperty(value = "供应商名称")
    private String vendorName;

    /**
     * 货运单号
     */
    @Excel(name = "货运单号")
    @TableField(exist = false)
    @ApiModelProperty(value = "货运单号")
    private String carrierNoteCode;

    /**
     * 货运方名称
     */
    @Excel(name = "货运方")
    @TableField(exist = false)
    @ApiModelProperty(value = "货运方名称")
    private String carrierName;

    /**
     * 物料（商品/服务）编码
     */
    @Excel(name = "商品编码")
    @TableField(exist = false)
    @ApiModelProperty(value = "物料（商品/服务）编码")
    private String materialCode;

    /**
     * 物料（商品/服务）名称
     */
    @Excel(name = "商品名称")
    @TableField(exist = false)
    @ApiModelProperty(value = "物料（商品/服务）名称")
    private String materialName;

    /**
     * 计量单位名称
     */
    @TableField(exist = false)
    @Excel(name = "基本计量单位")
    @ApiModelProperty(value = "计量单位名称")
    private String unitBaseName;

    /**
     * 工序名称(加工项)
     */
    @TableField(exist = false)
    @Excel(name = "工序名称")
    @ApiModelProperty(value = "工序名称(加工项)")
    private String processName;

    /**
     * 生产批次号
     */
    @Excel(name = "生产批次")
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "生产批次号")
    private Long productionBatchNum;

    /**
     * 行项目类别名称
     */
    @TableField(exist = false)
    @Excel(name = "类别")
    @ApiModelProperty(value = "行项目类别名称")
    private String itemCategoryName;

    /**
     * 本次发料量
     */
    @NotNull(message = "本次发料量不能为空")
    @Digits(integer = 8, fraction = 3, message = "本次发料量整数位上限为8位，小数位上限为3位")
    @Excel(name = "本次发料量")
    @ApiModelProperty(value = "本次发料量")
    private BigDecimal quantity;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 行号
     */
    @Excel(name = "行号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "行号")
    private Long itemNum;

    @TableField(exist = false)
    @Excel(name = "工序序号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "序号")
    private Long serialNum;

    /**
     * 单据类型名称
     */
    @TableField(exist = false)
    @Excel(name = "单据类型")
    @ApiModelProperty(value = "单据类型名称")
    private String documentTypeName;

    /**
     * 业务类型名称
     */
    @TableField(exist = false)
    @Excel(name = "业务类型")
    @ApiModelProperty(value = "业务类型名称")
    private String businessTypeName;

    /**
     * 系统SID-物料&商品&服务
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品&服务")
    private Long materialSid;

    /**
     * 计量单位（数据字典的键值）
     */
    @ApiModelProperty(value = "计量单位（数据字典的键值）")
    private String unitBase;

    /**
     * 出库量
     */
    @ApiModelProperty(value = "出库量")
    private BigDecimal outStockQuantity;

    /**
     * 行项目类别（数据字典的键值或配置档案的编码）：常规、返修
     */
    @NotBlank(message = "类别不能为空")
    @ApiModelProperty(value = "行项目类别（数据字典的键值或配置档案的编码）：常规、返修")
    private String itemCategory;

    @TableField(exist = false)
    private String[] itemCategoryList;

    /**
     * 计划产量
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "计划产量")
    private BigDecimal planQuantity;

    /**
     * 计划完成日期
     */
    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划完成日期")
    private Date planEndDate;

    /**
     * 工序行号
     */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "工序行号")
    private Long processItemNum;

    /**
     * 系统SID-外发加工单
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-外发加工单")
    private Long outsourcePurchaseOrderSid;

    /**
     * 外发加工单号
     */
    @ApiModelProperty(value = "外发加工单号")
    private String outsourcePurchaseOrderCode;

    @TableField(exist = false)
    private String operatorName;

    @TableField(exist = false)
    private String issueStatus;

    @TableField(exist = false)
    private String[] issueStatusList;

    /**
     * 系统SID-外发加工单明细
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-外发加工单明细")
    private Long outsourcePurchaseOrderItemSid;

    /**
     * 系统SID-生产订单
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-生产订单")
    private Long manufactureOrderSid;

    /**
     * 系统自增长ID-生产订单-工序
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-生产订单-工序")
    private Long manufactureOrderProcessSid;


    /**
     * 创建人账号（用户名称）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

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
     * 更新人账号（用户名称）
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 数据源系统（数据字典的键值）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值）")
    private String dataSourceSys;

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
     * 系统自增长ID-工厂
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "系统自增长ID-工厂")
    private String plantSid;

    /**
     * 工厂编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "工厂编码")
    private String plantCode;

    /**
     * 预计发料日期
     */
    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "预计发料日期")
    private Date expectedIssueDate;

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
     * 物料基本计量单位
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "物料基本计量单位")
    private String materiaUnitBase;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料基本计量单位")
    private String materiaUnitBaseName;

    /**
     * 系统自增长ID-工序
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "系统自增长ID-工序")
    private String processSid;

    @TableField(exist = false)
    private String[] processSidList;

    /**
     * 工序编码(加工项)
     */
    @ApiModelProperty(value = "工序编码(加工项)")
    @TableField(exist = false)
    private String processCode;

    /**
     * 发料操作人（用户帐号）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "发料操作人（用户帐号）")
    private String operator;

    /**
     * 货运方（承运商）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "货运方（承运商）")
    private String carrier;

    /**
     * 单据日期
     */
    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "单据日期")
    private Date documentDate;

    /**
     * 单据类型编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "单据类型编码")
    private String documentType;

    /**
     * 业务类型编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "业务类型编码")
    private String businessType;

    /**
     * 处理状态
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    /**
     * 系统自增长ID-供应商（加工商）list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "系统自增长ID-供应商（加工商）list")
    private Long[] vendorSidList;

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
     * 系统自增长ID-工厂list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "系统自增长ID-工厂list")
    private Long[] plantSidList;

    /**
     * 系统ID-产品季档案list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "系统ID-产品季档案list")
    private Long[] productSeasonSidList;

    /**
     * 出入库状态（数据字典的键值）list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "出入库状态（数据字典的键值）list")
    private String[] inOutStockStatusList;

    /**
     * 处理状态list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态list")
    private String[] handleStatusList;


    /**
     * 预计发料日期从
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "预计发料日期从")
    private String expectedIssueBeginDate;

    /**
     * 预计发料日期至
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "预计发料日期至")
    private String expectedIssueEndDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "出入库操作人")
    private String storehouseOperator;
}
