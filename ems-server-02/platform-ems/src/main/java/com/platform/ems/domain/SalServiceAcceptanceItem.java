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
 * 服务销售验收单-明细对象 s_sal_service_acceptance_item
 *
 * @author linhongwei
 * @date 2021-04-06
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_sal_service_acceptance_item")
public class SalServiceAcceptanceItem extends EmsBaseEntity {

    /**
     * 客户端口号
     */
    @Excel(name = "客户端口号")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /**
     * 系统自增长ID-销售服务验收单明细
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-销售服务验收单明细")
    private Long serviceAcceptanceItemSid;

    /**
     * 系统自增长ID-销售服务验收单
     */
    @Excel(name = "系统自增长ID-销售服务验收单")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-销售服务验收单")
    private Long serviceAcceptanceSid;

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
     * 计划验收量
     */
    @Excel(name = "计划验收量")
    @ApiModelProperty(value = "计划验收量")
    private BigDecimal planQuantity;

    /**
     * 行号
     */
    @Excel(name = "行号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "行号")
    private Long itemNum;

    /**
     * 系统自增长ID-销售订单明细
     */
    @Excel(name = "系统自增长ID-销售订单明细")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-销售订单明细")
    private Long salesOrderItemSid;

    /**
     * 实际验收量
     */
    @Excel(name = "实际验收量")
    @ApiModelProperty(value = "实际验收量")
    private BigDecimal actualQuantity;

    /**
     * 税率
     */
    @Excel(name = "税率")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "税率")
    private Long taxRate;

    /**
     * 销售价(不含税)
     */
    @Excel(name = "销售价(不含税)")
    @ApiModelProperty(value = "销售价(不含税)")
    private BigDecimal salePrice;

    /**
     * 销售价(含税)
     */
    @Excel(name = "销售价(含税)")
    @ApiModelProperty(value = "销售价(含税)")
    private BigDecimal salePriceTax;

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

    @ApiModelProperty(value = "物料（商品/服务）编码")
    @TableField(exist = false)
    private Long materialCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料（商品/服务）名称")
    private String materialName;

    /**
     * 计量单位（数据字典的键值）
     */
    @TableField(exist = false)
    @Excel(name = "计量单位（数据字典的键值）")
    @ApiModelProperty(value = "计量单位（数据字典的键值）")
    private String unitBase;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售量")
    private BigDecimal quantity;


}
