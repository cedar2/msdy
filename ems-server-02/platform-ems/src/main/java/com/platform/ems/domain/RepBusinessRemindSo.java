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
 * 已逾期/即将到期-销售订单对象 s_rep_business_remind_so
 *
 * @author linhongwei
 * @date 2022-02-24
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_rep_business_remind_so")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RepBusinessRemindSo extends EmsBaseEntity {


    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "合同交期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "合同交期")
    private Date contractDate;

    @Excel(name = " 商品编码(款号)")
    @ApiModelProperty(value = "物料/商品编码")
    private String materialCode;

    @ApiModelProperty(value = "商品名称")
    @TableField(exist = false)
    @Excel(name = "商品名称")
    private String materialName;

    @ApiModelProperty(value = "颜色")
    @TableField(exist = false)
    @Excel(name = "颜色")
    private String sku1Name;

    @ApiModelProperty(value = "尺码")
    @TableField(exist = false)
    @Excel(name = "尺码")
    private String sku2Name;

    @TableField(exist = false)
    @ApiModelProperty(value = "sku1的序号")
    private BigDecimal sort1;

    @TableField(exist = false)
    @ApiModelProperty(value = "sku2的序号")
    private BigDecimal sort2;

    @Excel(name = "客户")
    @ApiModelProperty(value = "客户简称")
    private String customerShortName;


    @Excel(name = "销售订单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售订单号")
    private Long salesOrderCode;

    /**
     * 订单量
     */
    @Excel(name = "订单量")
    @ApiModelProperty(value = "订单量")
    private BigDecimal quantityDingd;


    @ApiModelProperty(value = "待出库")
    @TableField(exist = false)
    @Excel(name = "待出库量")
    private BigDecimal sumQuantityDck;

    @ApiModelProperty(value = "已出库")
    @TableField(exist = false)
    @Excel(name = "已出库量")
    private BigDecimal sumQuantityYck;



    @ApiModelProperty(value = "下单季")
    @TableField(exist = false)
    @Excel(name = "下单季")
    private String productSeasonName;

    @ApiModelProperty(value = "单据类型")
    @Excel(name = "单据类型")
    private String documentTypeName;

    @ApiModelProperty(value = "业务类型")
    @Excel(name = "业务类型")
    private String businessTypeName;

    @Excel(name = "公司")
    @ApiModelProperty(value = "公司简称")
    private String companyName;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售合同号")
    @Excel(name = "销售合同号")
    private String saleContractCode;

    @ApiModelProperty(value = "销售模式")
    @TableField(exist = false)
    @Excel(name = "销售模式",dictType = "s_price_type")
    private String saleMode;

    @ApiModelProperty(value = "销售渠道")
    @TableField(exist = false)
    @Excel(name = "销售渠道")
    private String businessChannelName;

    @ApiModelProperty(value = "行号")
    @TableField(exist = false)
    @Excel(name = "行号")
    private String itemNum;

    @ApiModelProperty(value = "商品sku条码")
    @TableField(exist = false)
    @Excel(name = "商品sku条码")
    private String barcode;


    @ApiModelProperty(value = "销售员")
    @TableField(exist = false)
    @Excel(name = "销售员")
    private String salePersonName;


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

    @ApiModelProperty(value = "预警类型：已逾期、即将到期")
    private String remindType;


    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售订单sid")
    private Long salesOrderSid;



    @ApiModelProperty(value = "公司编码")
    private String companyCode;


    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "客户sid")
    private Long customerSid;

    @ApiModelProperty(value = "客户编码")
    private String customerCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "档案默认的客户客户简称")
    private String defaultCustomerShortName;

    @TableField(exist = false)
    @ApiModelProperty(value = "基本计量单位")
    private String unitBaseName;

    @TableField(exist = false)
    @ApiModelProperty(value = "采购价单位")
    private String unitPriceName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售订单行sid")
    private Long salesOrderItemSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "物料/商品sid")
    private Long materialSid;



    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "SKU1 sid")
    private Long sku1Sid;


    @ApiModelProperty(value = "SKU1 code")
    private String sku1Code;


    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "SKU2 sid")
    private Long sku2Sid;


    @ApiModelProperty(value = "SKU2 code")
    private String sku2Code;




    @ApiModelProperty(value = "已发货量")
    private BigDecimal quantityYifh;


    @ApiModelProperty(value = "未发货量")
    private BigDecimal quantityWeifh;

    /**
     * 创建日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建日期")
    private Date createDate;


    @ApiModelProperty(value = "订单量总")
    @TableField(exist = false)
    private BigDecimal sumQuantityDingd;

    @ApiModelProperty(value = "合同交期起")
    @TableField(exist = false)
    private String contractDateBeginTime;

    @ApiModelProperty(value = "合同交期至")
    @TableField(exist = false)
    private String contractDateEndTime;


    @ApiModelProperty(value = "查询字段：合同交期")
    @TableField(exist = false)
    private Date contractDateQuery;

    @ApiModelProperty(value = "查询：销售员")
    @TableField(exist = false)
    private String[] salePersonList;

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
