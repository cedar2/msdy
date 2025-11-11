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

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 销售合同信息-支付方式对象 s_sal_sale_contract_pay_method
 *
 * @author chenkw
 * @date 2022-05-17
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_sal_sale_contract_pay_method")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SalSaleContractPayMethod extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-销售合同附件信息
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-销售合同附件信息")
    private Long contractPayMethodSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] contractPayMethodSidList;

    /**
     * 系统SID-销售合同
     */
    @Excel(name = "系统SID-销售合同")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-销售合同")
    private Long saleContractSid;

    /**
     * 款项类别（数据字典的键值或配置档案的编码）
     */
    @NotBlank(message = "款项类别不能为空")
    @ApiModelProperty(value = "款项类别（数据字典的键值或配置档案的编码）")
    private String accountCategory;

    @Excel(name = "款项类别")
    @TableField(exist = false)
    @ApiModelProperty(value = "款项类别（数据字典的键值或配置档案的编码）")
    private String accountCategoryName;

    @TableField(exist = false)
    @ApiModelProperty(value = "款项类别（多选）")
    private String[] accountCategoryList;

    /**
     * 支付方式（数据字典的键值或配置档案的编码）
     */
    @NotBlank(message = "支付方式不能为空")
    @ApiModelProperty(value = "支付方式（数据字典的键值或配置档案的编码）")
    private String payMethod;

    @Excel(name = "支付方式")
    @TableField(exist = false)
    @ApiModelProperty(value = "支付方式（数据字典的键值或配置档案的编码）")
    private String payMethodName;

    @TableField(exist = false)
    @ApiModelProperty(value = "支付方式（多选）")
    private String[] payMethodList;

    /**
     * 占比(存值，即：不含百分号，如20%，就存0.2)
     */
    @NotNull(message = "占比不能为空")
    @Digits(integer = 3, fraction = 2, message = "占比必须大于0且小于等于100%")
    @ApiModelProperty(value = "占比(存值，即：不含百分号，如20%，就存0.2)")
    private BigDecimal rate;

    /**
     * 账期(天)
     */
    @Excel(name = "账期(天)")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "账期(天)")
    private Long accountValidDays;

    /**
     * 账期类型编码（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "账期类型编码（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "账期类型编码（数据字典的键值或配置档案的编码）")
    private String zhangqiType;

    /**
     * 账期天类型编码（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "账期天类型", dictType = "s_day_type")
    @ApiModelProperty(value = "账期天类型编码（数据字典的键值或配置档案的编码）")
    private String dayType;

    /**
     * 排序
     */
    @Excel(name = "排序")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "排序")
    private Long itemSort;

    /**
     * 序号
     */
    @Excel(name = "序号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "序号")
    private Long sort;

    /**
     * 创建人账号（用户名称）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

	@Excel(name = "创建人")
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

	@Excel(name = "更新人")
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
    @Excel(name = "数据源系统（数据字典的键值或配置档案的编码）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

}
