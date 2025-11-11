package com.platform.ems.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 销售扣款单-明细对象 s_fin_sale_deduction_item
 *
 * @author linhongwei
 * @date 2021-04-09
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_fin_sale_deduction_bill_item")
public class FinSaleDeductionBillItem extends EmsBaseEntity {

    /** 租户ID */
        @Excel(name = "租户ID")
        @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /** 系统SID-销售扣款单明细 */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-销售扣款单明细")
    private Long saleDeductionBillItemSid;

    /** 系统SID-销售扣款单 */
        @Excel(name = "系统SID-销售扣款单")
        @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-销售扣款单")
    private Long saleDeductionBillSid;

    /** 扣款类型（数据字典的键值） */
        @Excel(name = "扣款类型（数据字典的键值）")
        @ApiModelProperty(value = "扣款类型（数据字典的键值）")
    private String deductionType;

    /** 扣款金额(含税) */
        @Excel(name = "扣款金额(含税)")
        @ApiModelProperty(value = "扣款金额(含税)")
    private BigDecimal deductionAmountTax;

    /** 清账日期 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "清账日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "清账日期")
    private Date accountClearDate;

    /** 清账状态（数据字典的键值） */
        @Excel(name = "清账状态（数据字典的键值）")
        @ApiModelProperty(value = "清账状态（数据字典的键值）")
    private String accountClear;

    /** 行号 */
        @Excel(name = "行号")
        @ApiModelProperty(value = "行号")
    private Integer itemNum;

    /** 创建人账号（用户名称） */
        @Excel(name = "创建人账号（用户名称）")
        @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    /** 创建时间 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /** 更新人账号（用户名称） */
        @Excel(name = "更新人账号（用户名称）")
        @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

    /** 更新时间 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /** 数据源系统（数据字典的键值） */
        @Excel(name = "数据源系统（数据字典的键值）")
        @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值）")
    private String dataSourceSys;


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
