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
 * 生产状况-待排产对象
 *
 * @author c
 * @date 2022-03-17
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_rep_manufacture_status_daipai")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RepManufactureStatusDaipai extends EmsBaseEntity {

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
     * 销售订单sid
     */
    @Excel(name = "销售订单sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售订单sid")
    private Long salesOrderSid;

    /**
     * 销售订单号
     */
    @Excel(name = "销售订单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售订单号")
    private Long salesOrderCode;

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
     * 产品季code
     */
    @Excel(name = "产品季code")
    @ApiModelProperty(value = "产品季code")
    private String productSeasonCode;

    /**
     * 客户sid
     */
    @Excel(name = "客户sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "客户sid")
    private Long customerSid;

    /**
     * 客户编码
     */
    @Excel(name = "客户编码")
    @ApiModelProperty(value = "客户编码")
    private String customerCode;

    /**
     * 客户简称
     */
    @Excel(name = "客户简称")
    @ApiModelProperty(value = "客户简称")
    private String customerShortName;

    /**
     * 销售订单行sid
     */
    @Excel(name = "销售订单行sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售订单行sid")
    private Long salesOrderItemSid;

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
     * 已排产量
     */
    @Excel(name = "已排产量")
    @ApiModelProperty(value = "已排产量")
    private BigDecimal quantityYipc;

    /**
     * 待排产量
     */
    @Excel(name = "待排产量")
    @ApiModelProperty(value = "待排产量")
    private BigDecimal quantityDaipc;

    /**
     * 创建日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建日期")
    private Date createDate;

    @ApiModelProperty(value = "处理状态")
    @TableField(exist = false)
    private String handleStatus;

}
