package com.platform.ems.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableId;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;

import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


import lombok.experimental.Accessors;

/**
 * 服务采购验收单-明细对象 s_pur_service_acceptance_item
 *
 * @author linhongwei
 * @date 2021-04-07
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_pur_service_acceptance_item")
public class PurServiceAcceptanceItem extends EmsBaseEntity {

    /**
     * 客户端口号
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /**
     * 系统自增长ID-采购服务验收单明细
     */
    @TableId
    @Excel(name = "系统自增长ID-采购服务验收单明细")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-采购服务验收单明细")
    private Long serviceAcceptanceItemSid;

    /**
     * 系统自增长ID-采购服务验收单
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-采购服务验收单")
    private Long serviceAcceptanceSid;

    /**
     * 系统自增长ID-物料档案
     */
    @Excel(name = "系统自增长ID-物料档案")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-物料档案")
    private Long materialSid;

    /**
     * 系统自增长ID-商品条码
     */
    @Excel(name = "系统自增长ID-商品条码")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-商品条码")
    private Long barcodeSid;

    /**
     * 物料（商品/服务）名称
     */
    @Excel(name = "物料（商品/服务）名称")
    @ApiModelProperty(value = "物料（商品/服务）名称")
    private String materialName;

    /**
     * 系统自增长ID-采购订单明细
     */
    @Excel(name = "系统自增长ID-采购订单明细")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-采购订单明细")
    private Long purchaseOrderItemSid;

    /**
     * 行号
     */
    @ApiModelProperty(value = "行号")
    private Integer itemNum;

    /**
     * 计量单位
     */
    @Excel(name = "计量单位")
    @ApiModelProperty(value = "计量单位")
    private String unitBase;

    /**
     * 采购单位
     */
    @Excel(name = "采购单位")
    @ApiModelProperty(value = "采购单位")
    private String purchaseUnit;

    /**
     * 税率
     */
    @Excel(name = "税率")
    @ApiModelProperty(value = "税率")
    private BigDecimal taxRate;

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
     * 计划验收量
     */
    @Excel(name = "计划验收量")
    @TableField(exist = false)
    @ApiModelProperty(value = "计划验收量")
    private BigDecimal planQuantity;

    @Excel(name = "计划验收量")
    @ApiModelProperty(value = "计划验收量")
    private BigDecimal expectedQuantity;

    /**
     * 本次验收量
     */
    @Excel(name = "本次验收量")
    @ApiModelProperty(value = "本次验收量")
    private BigDecimal actualQuantity;

    /**
     * 创建人账号
     */
    @Excel(name = "创建人账号")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号")
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
     * 更新人账号
     */
    @Excel(name = "更新人账号")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccount;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;


    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "创建日期开始时间")
    @TableField(exist = false)
    private String beginTime;

    @ApiModelProperty(value = "创建日期结束时间")
    @TableField(exist = false)
    private String endTime;

    @ApiModelProperty(value = "页数")
    @TableField(exist = false)
    private Integer pageNum;

    @ApiModelProperty(value = "每页个数")
    @TableField(exist = false)
    private Integer pageSize;

    /**
     * 物料（商品/服务）编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "物料（商品/服务）编码")
    private Long materialCode;

    @TableField(exist = false)
    @Excel(name = "采购量")
    @ApiModelProperty(value = "采购量")
    private BigDecimal quantity;
}
