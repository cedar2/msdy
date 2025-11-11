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

import java.math.BigDecimal;
import java.util.Date;

/**
 * 已逾期/即将到期-采购订单对象 s_rep_business_remind_po
 *
 * @author linhongwei
 * @date 2022-02-24
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_rep_business_remind_po")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RepBusinessRemindPo extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 数据记录sid
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "数据记录sid")
    private Long dataRecordSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] dataRecordSidList;
    /**
     * 预警类型：已逾期、即将到期
     */
    @Excel(name = "预警类型：已逾期、即将到期")
    @ApiModelProperty(value = "预警类型：已逾期、即将到期")
    private String remindType;

    /**
     * 采购订单sid
     */
    @Excel(name = "采购订单sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购订单sid")
    private Long purchaseOrderSid;

    /**
     * 采购订单号
     */
    @Excel(name = "采购订单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购订单号")
    private Long purchaseOrderCode;

    /**
     * 公司编码
     */
    @Excel(name = "公司编码")
    @ApiModelProperty(value = "公司编码")
    private String companyCode;

    /**
     * 公司简称
     */
    @Excel(name = "公司简称")
    @ApiModelProperty(value = "公司简称")
    private String companyName;

    /**
     * 供应商sid
     */
    @Excel(name = "供应商sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "供应商sid")
    private Long vendorSid;

    /**
     * 供应商编码
     */
    @Excel(name = "供应商编码")
    @ApiModelProperty(value = "供应商编码")
    private String vendorCode;

    /**
     * 供应商简称
     */
    @Excel(name = "供应商简称")
    @ApiModelProperty(value = "供应商简称")
    private String vendorShortName;

    @TableField(exist = false)
    @ApiModelProperty(value = "档案默认的供应商简称")
    private String defaultVendorShortName;

    @TableField(exist = false)
    @ApiModelProperty(value = "基本计量单位")
    private String unitBaseName;

    @TableField(exist = false)
    @ApiModelProperty(value = "采购价单位")
    private String unitPriceName;

    /**
     * 采购订单行sid
     */
    @Excel(name = "采购订单行sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购订单行sid")
    private Long purchaseOrderItemSid;

    /**
     * 物料/商品sid
     */
    @Excel(name = "物料/商品sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "物料/商品sid")
    private Long materialSid;

    /**
     * 物料/商品编码
     */
    @Excel(name = "物料/商品编码")
    @ApiModelProperty(value = "物料/商品编码")
    private String materialCode;

    /**
     * SKU1 sid
     */
    @Excel(name = "SKU1 sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "SKU1 sid")
    private Long sku1Sid;

    /**
     * SKU1 code
     */
    @Excel(name = "SKU1 code")
    @ApiModelProperty(value = "SKU1 code")
    private String sku1Code;

    /**
     * SKU2 sid
     */
    @Excel(name = "SKU2 sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "SKU2 sid")
    private Long sku2Sid;

    /**
     * SKU2 code
     */
    @Excel(name = "SKU2 code")
    @ApiModelProperty(value = "SKU2 code")
    private String sku2Code;

    /**
     * 合同交期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "合同交期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "合同交期")
    private Date contractDate;

    /**
     * 订单量
     */
    @Excel(name = "订单量")
    @ApiModelProperty(value = "订单量")
    private BigDecimal quantityDingd;

    /**
     * 已交货量
     */
    @Excel(name = "已交货量")
    @ApiModelProperty(value = "已交货量")
    private BigDecimal quantityYijh;

    /**
     * 未交货量
     */
    @Excel(name = "未交货量")
    @ApiModelProperty(value = "未交货量")
    private BigDecimal quantityWeijh;

    /**
     * 创建日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建日期")
    private Date createDate;


    @ApiModelProperty(value = "订单量总")
    @TableField(exist = false)
    private BigDecimal sumQuantityDingd;

    @ApiModelProperty(value = "待入库")
    @TableField(exist = false)
    private BigDecimal sumQuantityDrk;

    @ApiModelProperty(value = "已入库")
    @TableField(exist = false)
    private BigDecimal sumQuantityYrk;

    @ApiModelProperty(value = "合同交期起")
    @TableField(exist = false)
    private String contractDateBeginTime;

    @ApiModelProperty(value = "合同交期至")
    @TableField(exist = false)
    private String contractDateEndTime;

    @ApiModelProperty(value = "行号")
    @TableField(exist = false)
    private String itemNum;

    @ApiModelProperty(value = "商品sku条码")
    @TableField(exist = false)
    private String barcode;

    @ApiModelProperty(value = "采购模式")
    @TableField(exist = false)
    private String purchaseMode;

    @ApiModelProperty(value = "下单季")
    @TableField(exist = false)
    private String productSeasonName;

    @ApiModelProperty(value = "单据类型")
    @TableField(exist = false)
    private String documentTypeName;

    @ApiModelProperty(value = "业务类型")
    @TableField(exist = false)
    private String businessTypeName;

    @ApiModelProperty(value = "销售渠道")
    @TableField(exist = false)
    private String businessChannelName;

    @ApiModelProperty(value = "查询字段：合同交期")
    @TableField(exist = false)
    private Date contractDateQuery;

    @ApiModelProperty(value = "商品名称")
    @TableField(exist = false)
    private String materialName;

    @ApiModelProperty(value = "sku1名称")
    @TableField(exist = false)
    private String sku1Name;

    @ApiModelProperty(value = "sku2名称")
    @TableField(exist = false)
    private String sku2Name;

    @TableField(exist = false)
    @ApiModelProperty(value = "sku1的序号")
    private BigDecimal sort1;

    @TableField(exist = false)
    @ApiModelProperty(value = "sku2的序号")
    private BigDecimal sort2;

    @TableField(exist = false)
    @ApiModelProperty(value = "采购合同号")
    private String purchaseContractCode;

    @ApiModelProperty(value = "物料类型")
    @TableField(exist = false)
    private String materialTypeName;

    @ApiModelProperty(value = "采购员")
    @TableField(exist = false)
    private String buyerName;

    @ApiModelProperty(value = "查询：采购员")
    @TableField(exist = false)
    private String[] buyerList;

    @ApiModelProperty(value = "查询：物料类型")
    @TableField(exist = false)
    private String[] materialTypeList;

    @TableField(exist = false)
    private String firstSort;

    @TableField(exist = false)
    private String secondSort;

    @TableField(exist = false)
    private String thirdSort;

    @ApiModelProperty(value = "即将到期提醒天数")
    @TableField(exist = false)
    private Long toexpireDays;

}
