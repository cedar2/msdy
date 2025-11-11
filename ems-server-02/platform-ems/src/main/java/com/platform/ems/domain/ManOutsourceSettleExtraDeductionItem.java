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
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import lombok.experimental.Accessors;

/**
 * 外发加工费结算单-额外扣款明细对象 s_man_outsource_settle_extra_deduction_item
 *
 * @author admin
 * @date 2023-08-10
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_man_outsource_settle_extra_deduction_item")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ManOutsourceSettleExtraDeductionItem extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-外发加工费结算单附件信息
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-外发加工费结算单附件信息")
    private Long outsourceSettleExtraDeductionItemSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] outsourceSettleExtraDeductionItemSidList;

    /**
     * 系统SID-外发加工费结算单
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-外发加工费结算单")
    private Long manufactureOutsourceSettleSid;

    /**
     * 外发加工费结算单号
     */
    @Excel(name = "外发加工费结算单号")
    @ApiModelProperty(value = "外发加工费结算单号")
    private String manufactureOutsourceSettleCode;

    /**
     * 额外扣款说明
     */
    @Excel(name = "额外扣款说明")
    @ApiModelProperty(value = "额外扣款说明")
    private String extraDeductionInstruction;

    /**
     * 金额(含税)
     */
    @Excel(name = "金额(含税)")
    @ApiModelProperty(value = "金额(含税)")
    private BigDecimal extraDeductionTax;

    /**
     * 金额(不含税)
     */
    @Excel(name = "金额(不含税)")
    @ApiModelProperty(value = "金额(不含税)")
    private BigDecimal extraDeduction;

    /**
     * 创建人账号（用户名称）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

	@Excel(name = "创建人")
	@ApiModelProperty(value = "创建人昵称")
	@TableField(exist = false)
	private String creatorAccountName;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /**
     * 更新人账号（用户名称）
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

	@Excel(name = "更改人")
	@ApiModelProperty(value = "更改人昵称")
	@TableField(exist = false)
	private String updaterAccountName;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

}
