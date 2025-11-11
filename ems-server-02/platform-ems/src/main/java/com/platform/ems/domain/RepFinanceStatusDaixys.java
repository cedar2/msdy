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
 * 财务状况-客户-待销已收对象 s_rep_finance_status_daixys
 *
 * @author chenkw
 * @date 2022-02-25
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_rep_finance_status_daixys")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RepFinanceStatusDaixys extends EmsBaseEntity {

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
    @ApiModelProperty(value = "产品季code")
    private Long productSeasonSid;

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
     * 公司编码
     */
    @Excel(name = "公司编码")
    @ApiModelProperty(value = "公司编码")
    private String companyCode;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司")
    private Long companySid;

    /**
     * 公司简称
     */
    @Excel(name = "公司简称")
    @ApiModelProperty(value = "公司简称")
    private String companyShortName;

    /**
     * 类型：特殊收款
     */
    @Excel(name = "类型：特殊收款")
    @ApiModelProperty(value = "类型：特殊收款")
    private String statisticType;

    /**
     * 待销已收金额
     */
    @Excel(name = "待销已收金额")
    @ApiModelProperty(value = "待销已收金额")
    private BigDecimal moneyAmountDaixys;

    /**
     * 到期日
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "到期日", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "到期日")
    private Date dueDate;

    /**
     * 到期年份
     */
    @Excel(name = "到期年份")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "到期年份")
    private Long year;

    /**
     * 到期月份
     */
    @Excel(name = "到期月份")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "到期月份")
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
