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
 * 采购订单-合作伙伴对象 s_pur_purchase_order_partner
 *
 * @author linhongwei
 * @date 2021-04-08
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_pur_purchase_order_partner")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PurPurchaseOrderPartner extends EmsBaseEntity {

    /**
     * 客户端口号
     */
    @Excel(name = "客户端口号")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /**
     * 系统自增长ID-采购订单-合作伙伴
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-采购订单-合作伙伴")
    private Long purchaseOrderPartnerSid;

    /**
     * 系统自增长ID-采购订单
     */
    @Excel(name = "系统自增长ID-采购订单")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-采购订单")
    private Long purchaseOrderSid;

    /**
     * 合作伙伴类型
     */
    @Excel(name = "合作伙伴类型")
    @ApiModelProperty(value = "合作伙伴类型")
    private String partnerType;

    /**
     * 合作伙伴名称
     */
    @Excel(name = "合作伙伴名称")
    @ApiModelProperty(value = "合作伙伴名称")
    private String partnerName;

    /**
     * 合作伙伴编码
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "合作伙伴编码")
    private Long partnerSid;

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


}
