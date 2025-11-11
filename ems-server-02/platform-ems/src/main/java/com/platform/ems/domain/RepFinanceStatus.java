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
 * 财务状况对象 s_rep_finance_status
 *
 * @author chenkw
 * @date 2022-02-25
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_rep_finance_status")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RepFinanceStatus extends EmsBaseEntity {

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
     * 类型：资金、应收、应付、资产
     */
    @Excel(name = "类型：资金、应收、应付、资产")
    @ApiModelProperty(value = "类型：资金、应收、应付、资产")
    private String statisticType;

    /**
     * 字段名
     */
    @Excel(name = "字段名")
    @ApiModelProperty(value = "字段名")
    private String fieldName;

    /**
     * 字段值
     */
    @Excel(name = "字段值")
    @ApiModelProperty(value = "字段值")
    private BigDecimal fieldValue;

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
