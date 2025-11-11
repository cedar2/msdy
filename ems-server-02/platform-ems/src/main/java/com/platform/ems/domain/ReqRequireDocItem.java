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
import com.fasterxml.jackson.annotation.JsonInclude;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

import lombok.experimental.Accessors;

/**
 * 需求单明细对象 s_req_require_doc_item
 *
 * @author linhongwei
 * @date 2021-04-02
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_req_require_doc_item")
public class ReqRequireDocItem extends EmsBaseEntity {

    /**
     * 客户端口号
     */
    @Excel(name = "客户端口号")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /**
     * 系统自增长ID-需求单明细
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-需求单明细")
    private Long requireDocItemSid;

    /**
     * 系统自增长ID-需求单
     */
    @Excel(name = "系统自增长ID-需求单")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-需求单")
    private Long requireDocSid;

    /**
     * 系统自增长ID-商品&物料&服务
     */
    @Excel(name = "系统自增长ID-商品&物料&服务")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-商品&物料&服务")
    private Long materialSid;

    /**
     * 系统自增长ID-商品sku
     */
    @Excel(name = "系统自增长ID-商品sku")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-商品sku")
    private Long sku1Sid;

    /**
     * 系统自增长ID-商品sku
     */
    @Excel(name = "系统自增长ID-商品sku")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-商品sku")
    private Long sku2Sid;

    /**
     * 系统自增长ID-商品条码
     */
    @Excel(name = "系统自增长ID-商品条码")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-商品条码")
    private Long barcodeSid;

    /**
     * 需求量
     */
    @Excel(name = "需求量")
    @ApiModelProperty(value = "需求量")
    private BigDecimal quantity;

    /**
     * 需求日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "需求日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "需求日期")
    private Date demandDate;

    /**
     * 最晚需求日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "最晚需求日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "最晚需求日期")
    private Date latestDemandDate;

    /**
     * 基本计量单位
     */
    @Excel(name = "基本计量单位")
    @ApiModelProperty(value = "基本计量单位")
    private String unitBase;

    /**
     * 系统自增长ID-销售订单明细
     */
    @Excel(name = "系统自增长ID-销售订单明细")
    @ApiModelProperty(value = "系统自增长ID-销售订单明细")
    private String salesOrderItemSid;

    /**
     * 系统自增长ID-采购订单明细
     */
    @Excel(name = "系统自增长ID-采购订单明细")
    @ApiModelProperty(value = "系统自增长ID-采购订单明细")
    private String purchaseOrderItemSid;

    /**
     * 行号
     */
    @ApiModelProperty(value = "行号")
    private Integer itemNum;

    /**
     * 处理状态
     */
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

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

    /**
     * 物料（商品/服务）名称
     */
    @Excel(name = "物料（商品/服务）名称")
    @ApiModelProperty(value = "物料（商品/服务）名称")
    private String materialName;


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
    private String materialCode;

    /**
     * SKU编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "SKU1编码")
    private String sku1Code;

    /**
     * SKU名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "SKU1名称")
    private String sku1Name;

    /**
     * SKU编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "SKU2编码")
    private String sku2Code;

    /**
     * SKU名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "SKU2名称")
    private String sku2Name;

    /**
     * 商品条码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "商品条码")
    private Long barcode;

    /**
     * 系统自增长ID-销售订单
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-销售订单")
    private Long salesOrderSid;

    /**
     * 销售订单号
     */
    @ApiModelProperty(value = "销售订单号")
    private String salesOrderCode;

    /**
     * 销售订单行号
     */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售订单行号")
    private Long salesOrderItemNum;

    /**
     * 系统自增长ID-采购订单
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-采购订单")
    private Long purchaseOrderSid;

    /**
     * 采购订单号
     */
    @ApiModelProperty(value = "采购订单号")
    private String purchaseOrderCode;

    /**
     * 采购订单行号
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "采购订单行号")
    private Long purchaseOrderItemNum;

    /**
     * 系统自增长ID-生产订单
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-生产订单")
    private Long manufactureOrderSid;

    /**
     * 生产订单号
     */
    @ApiModelProperty(value = "生产订单号")
    private String manufactureOrderCode;
}
