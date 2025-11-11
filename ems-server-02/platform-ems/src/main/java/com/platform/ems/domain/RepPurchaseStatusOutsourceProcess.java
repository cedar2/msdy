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
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 采购状况-外发加工结算对象 s_rep_purchase_status_outsource_process
 *
 * @author linhongwei
 * @date 2022-02-25
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_rep_purchase_status_outsource_process")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RepPurchaseStatusOutsourceProcess {

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
     * 产品季code
     */
    @Excel(name = "产品季code")
    @ApiModelProperty(value = "产品季code")
    private String productSeasonCode;

    /**
     * 年份
     */
    @Excel(name = "年份")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "年份")
    private Long year;

    /**
     * 月份
     */
    @Excel(name = "月份")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "月份")
    private Long moth;

    /**
     * 供应商编码
     */
    @Excel(name = "供应商编码")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "供应商编码")
    private Long vendorCode;

    /**
     * 供应商简称
     */
    @Excel(name = "供应商简称")
    @ApiModelProperty(value = "供应商简称")
    private String vendorShortName;

    /**
     * 公司code
     */
    @Excel(name = "公司code")
    @ApiModelProperty(value = "公司code")
    private String companyCode;

    /**
     * 公司简称
     */
    @Excel(name = "公司简称")
    @ApiModelProperty(value = "公司简称")
    private String companyName;

    /**
     * 单据类型code
     */
    @Excel(name = "单据类型code")
    @ApiModelProperty(value = "单据类型code")
    private String documentTypeCode;

    /**
     * 单据类型名称
     */
    @Excel(name = "单据类型名称")
    @ApiModelProperty(value = "单据类型名称")
    private String documentTypeName;

    /**
     * 业务类型code
     */
    @Excel(name = "业务类型code")
    @ApiModelProperty(value = "业务类型code")
    private String businessTypeCode;

    /**
     * 业务类型名称
     */
    @Excel(name = "业务类型名称")
    @ApiModelProperty(value = "业务类型名称")
    private String businessTypeName;

    /**
     * 订单金额
     */
    @Excel(name = "订单金额")
    @ApiModelProperty(value = "订单金额")
    private BigDecimal moneyAmount;

    /**
     * 订单量
     */
    @Excel(name = "订单量")
    @ApiModelProperty(value = "订单量")
    private BigDecimal quantity;

    /**
     * 币种
     */
    @Excel(name = "币种")
    @ApiModelProperty(value = "币种")
    private String currency;

    /**
     * 货币单位
     */
    @Excel(name = "货币单位")
    @ApiModelProperty(value = "货币单位")
    private String currencyUnit;

    /**
     * 创建日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建日期")
    private Date createDate;

    @ApiModelProperty(value = "创建日期开始时间")
    @TableField(exist = false)
    private String beginTime;

    @ApiModelProperty(value = "创建日期结束时间")
    @TableField(exist = false)
    private String endTime;

    @ApiModelProperty(value = "每页个数")
    @TableField(exist = false)
    private Integer pageNum;

    @ApiModelProperty(value = "每页个数")
    @TableField(exist = false)
    private Integer pageSize;

}
