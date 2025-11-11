package com.platform.ems.domain.dto.response;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
/**
 * 外部接口 物料采购订单主表
 *
 * @author yangqz
 * @date 2022-2-21
 */
@Data
@ApiModel
@Accessors(chain = true)
public class PurPurchaseOrderOutResponse implements Serializable {

    @ApiModelProperty(value = "租户 ID")
    private String clientId;

    @ApiModelProperty(value = "租户名称")
    private String clientName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购订单 sid")
    private Long purchaseOrderSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购订单号")
    private Long purchaseOrderCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "供应商sid")
    private Long vendorSid;

    @ApiModelProperty(value = "供应商名称")
    private String vendorName;

    @ApiModelProperty(value = "供应商编码")
    private String vendorCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司sid")
    private Long companySid;

    @ApiModelProperty(value = "公司编码")
    private String companyCode;

    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "单据日期")
    private Date documentDate;

    @Excel(name = "单据类型编码")
    @ApiModelProperty(value = "单据类型编码")
    private String documentType;

    @Excel(name = "单据类型")
    @ApiModelProperty(value = "单据类型名称")
    private String documentTypeName;

    @Excel(name = "业务类型编码")
    @ApiModelProperty(value = "业务类型编码")
    private String businessType;

    @Excel(name = "业务类型")
    @ApiModelProperty(value = "业务类型名称")
    private String businessTypeName;

    @Excel(name = "下单类型")
    @ApiModelProperty(value = "下单类型")
    private String orderType;

    @Excel(name = "操作类型")
    @ApiModelProperty(value = "操作类型")
    private String operateType;

    @ApiModelProperty(value = "采购订单明细信息")
    List<PurPurchaseOrderItemOutResponse> itemList;

    @ApiModelProperty(value = "采购订单交货计划明细信息")
    List<PurPurchaseOrderDePlanOutResponse> deliveryPlanList;
}
