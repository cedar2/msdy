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
import java.math.BigDecimal;
import java.util.Date;

/**
 * 生产完工确认单-明细对象 s_man_manufacture_complete_note_item
 *
 * @author linhongwei
 * @date 2021-06-09
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_man_manufacture_complete_note_item")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ManManufactureCompleteNoteItem extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-生产完工确认单明细
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-生产完工确认单明细")
    private Long manufactureCompleteNoteItemSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] manufactureCompleteNoteItemSidList;
    /**
     * 系统SID-生产完工确认单
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-生产完工确认单")
    private Long manufactureCompleteNoteSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "生产订单产品明细sid")
    private Long manufactureOrderProductSid;
    /**
     * 系统SID-商品sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品sid")
    private Long materialSid;

    /**
     * 系统SID-商品sku1
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品sku1")
    private Long sku1Sid;

    /**
     * 系统SID-商品sku2
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品sku2")
    private Long sku2Sid;

    /**
     * 系统SID-商品条码（物料&商品&服务）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品条码（物料&商品&服务）")
    private Long barcodeSid;

    /**
     * 商品名称
     */
    @Excel(name = "商品名称")
    @ApiModelProperty(value = "商品名称")
    private String materialName;

    /**
     * 计量单位（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "计量单位（数据字典的键值或配置档案的编码）")
    private String unitBase;

    /**
     * 本次完工量
     */
    @Digits(integer = 8, fraction = 3, message = "本次完工量整数位上限为8位，小数位上限为3位")
    @ApiModelProperty(value = "本次完工量")
    private BigDecimal completeQuantity;

    /**
     * 行号
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "行号")
    private Long itemNum;

    /**
     * 创建人账号（用户名称）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
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
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    @TableField(exist = false)
    @ApiModelProperty(value = "工厂sid")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long plantSid;

    @Excel(name = "生产完工确认单号")
    @TableField(exist = false)
    @ApiModelProperty(value = "生产完工确认单号")
    private String manufactureCompleteNoteCode;

    @Excel(name = "工厂名称")
    @TableField(exist = false)
    @ApiModelProperty(value = "工厂名称")
    private String plantName;

    @Excel(name = "工厂编码")
    @TableField(exist = false)
    @ApiModelProperty(value = "工厂编码")
    private String plantCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "生产订单sid")
    private String manufactureOrderSid;

    @Excel(name = "生产订单号")
    @TableField(exist = false)
    @ApiModelProperty(value = "生产订单号")
    private String manufactureOrderCode;

    @Excel(name = "单据日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(exist = false)
    @ApiModelProperty(value = "单据日期")
    private String documentDate;

    @Excel(name = "商品编码")
    @TableField(exist = false)
    @ApiModelProperty(value = "商品编码")
    private String materialCode;

    @Excel(name = "sku1编码")
    @TableField(exist = false)
    @ApiModelProperty(value = "sku1编码")
    private String sku1Code;

    @Excel(name = "sku1名称")
    @TableField(exist = false)
    @ApiModelProperty(value = "")
    private String sku1Name;

    @Excel(name = "sku2编码")
    @TableField(exist = false)
    @ApiModelProperty(value = "")
    private String sku2Code;

    @Excel(name = "sku2名称")
    @TableField(exist = false)
    @ApiModelProperty(value = "")
    private String sku2Name;

    @Excel(name = "商品条码")
    @TableField(exist = false)
    @ApiModelProperty(value = "商品条码")
    private String barcode;

    @Excel(name = "合同交期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(exist = false)
    @ApiModelProperty(value = "合同交期")
    private String contractDate;

    @Excel(name = "需求日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(exist = false)
    @ApiModelProperty(value = "需求日期")
    private String demandDate;

    @Excel(name = "最晚需求日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(exist = false)
    @ApiModelProperty(value = "最晚需求日期")
    private String latestDemandDate;

    @Excel(name = "基本计量单位")
    @TableField(exist = false)
    @ApiModelProperty(value = "基本计量单位")
    private String saleUnitBase;

    @Excel(name = "销售单位")
    @TableField(exist = false)
    @ApiModelProperty(value = "销售单位")
    private String unitSale;

    @Excel(name = "销售员")
    @TableField(exist = false)
    @ApiModelProperty(value = "销售员")
    private String salePerson;

    @Excel(name = "销售订单号")
    @TableField(exist = false)
    @ApiModelProperty(value = "销售订单行号")
    private String saleItemNum;

    @Excel(name = "已完工量")
    @TableField(exist = false)
    @ApiModelProperty(value = "已完工量")
    private String orderCompleteQuantity;

    @Excel(name = "计划产量")
    @TableField(exist = false)
    @ApiModelProperty(value = "计划产量")
    private String quantity;

    @Excel(name = "计划开始日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(exist = false)
    @ApiModelProperty(value = "计划开始日期")
    private String planStartDate;

    @Excel(name = "计划完成日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(exist = false)
    @ApiModelProperty(value = "计划完成日期")
    private String planEndDate;

    @Excel(name = "处理状态", dictType = "s_handle_status")
    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @Excel(name = "客户名称")
    @TableField(exist = false)
    @ApiModelProperty(value = "客户名称")
    private String customerName;


    @Excel(name = "客户编码")
    @TableField(exist = false)
    @ApiModelProperty(value = "客户编码")
    private String customerCode;

    @Excel(name = "客户sid")
    @TableField(exist = false)
    @ApiModelProperty(value = "客户sid")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long customerSid;


    @TableField(exist = false)
    @ApiModelProperty(value = "销售订单sid")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long salesOrderSid;


    @TableField(exist = false)
    @ApiModelProperty(value = "销售订单号")
    private String salesOrderCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售员名称")
    private String salePersonName;

    @TableField(exist = false)
    @ApiModelProperty(value = "计量单位名称")
    private String unitBaseName;

    @TableField(exist = false)
    @ApiModelProperty(value = "基本计量单位名称")
    private String completeUnitBaseName;
}
