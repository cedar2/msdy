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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 财务状况-供应商-应付对象 s_rep_finance_status_yingf
 *
 * @author chenkw
 * @date 2022-02-25
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_rep_finance_status_yingf")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RepFinanceStatusYingf extends EmsBaseEntity {

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
     * 产品季code
     */
    @Excel(name = "产品季code")
    @ApiModelProperty(value = "产品季code")
    private String productSeasonCode;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "产品季")
    private Long productSeasonSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "产品季")
    private String productSeasonName;

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
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司sid")
    private Long companySid;

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
    private String companyShortName;

    /**
     * 类型：采购订单、采购退货订单
     */
    @ApiModelProperty(value = "类型：采购订单、采购退货订单")
    private String statisticType;

    @TableField(exist = false)
    @Excel(name = "类型")
    @ApiModelProperty(value = "类型：销售订单、销售退货订单")
    private String statisticTypeName;

    /**
     * 应付金额
     */
    @Excel(name = "应付金额")
    @ApiModelProperty(value = "应付金额")
    private BigDecimal moneyAmountYingf;

    /**
     * 到期日
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "到期日", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "到期日")
    private Date dueDate;

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
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd hh:mm:ss")
    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建日期")
    private Date createDate;

}
