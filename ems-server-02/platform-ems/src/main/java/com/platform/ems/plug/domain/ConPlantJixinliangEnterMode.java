package com.platform.ems.plug.domain;

import java.util.Date;
import java.util.List;

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

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.experimental.Accessors;

/**
 * 工厂计薪量录入方式对象 s_con_plant_jixinliang_enter_mode
 *
 * @author zhuangyz
 * @date 2022-07-14
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_con_plant_jixinliang_enter_mode")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConPlantJixinliangEnterMode extends EmsBaseEntity {

    /**
     * 租户ID
     */
    //@Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-工厂计薪量录入方式信息sid
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-工厂计薪量录入方式信息sid")
    private Long sid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] sidList;
    /**
     * 工厂sid
     */

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "工厂sid")
	@NotNull(message = "工厂不能为空")
    private Long plantSid;

    @TableField(exist = false)
    private List<String> plantSidList;

	@Excel(name = "工厂")
	@TableField(exist = false)
    private String plantShortName;

    /**
     * 工厂编码code
     */
    //@Excel(name = "工厂编码code ")
    @ApiModelProperty(value = "工厂编码code ")
    private String plantCode;

    /**
     * 商品工价类型（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "商品工价类型" , dictType = "s_product_price_type")
    @ApiModelProperty(value = "商品工价类型（数据字典的键值或配置档案的编码）")
	@NotBlank(message = "商品工价类型不能为空")
    private String productPriceType;

    /**
     * 计薪完工类型（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "计薪完工类型" , dictType = "s_jixin_wangong_type")
    @ApiModelProperty(value = "计薪完工类型（数据字典的键值或配置档案的编码）")
	@NotBlank(message = "计薪完工类型不能为空")
    private String jixinWangongType;

    /**
     * 计薪量录入方式（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "计薪量录入方式" , dictType = "s_jixin_enter_mode")
    @ApiModelProperty(value = "计薪量录入方式")
	@NotBlank(message = "计薪量录入方式不能为空")
	private String enterMode;


    /**
     * 结算数录入顺序（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "结算数录入顺序" , dictType = "s_jiesuanshu_enter_sequence")
    @ApiModelProperty(value = "结算数录入顺序）")
	@NotBlank(message = "结算数录入顺序不能为空")
    private String jiesuanshuEnterSequence;

	/**
	 * 计薪量校验类型（数据字典的键值或配置档案的编码）
	 */
	@Excel(name = "计薪量校验类型" , dictType = "s_jixin_check_type")
	@ApiModelProperty(value = "校验类型")
	@NotBlank(message = "计薪量校验类型不能为空")
	private String checkType;

	@TableField(exist = false)
	private List<String> checkTypeList;

    /**
     * 排产批次号为空-提醒方式（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "排产批次号为空提醒方式" , dictType = "s_message_display_type")
    @ApiModelProperty(value = "排产批次号为空-提醒方式（数据字典的键值或配置档案的编码）")
	@NotBlank(message = "排产批次号为空提醒方式不能为空")
    private String paichanBatchNullRemindMode;

	@ApiModelProperty(value ="备注")
	@Excel(name = "备注")
	private String remark;

	/**
	 * 处理状态（数据字典的键值或配置档案的编码）
	 */
	@NotBlank(message = "状态不能为空")
	@Excel(name = "处理状态", dictType = "s_handle_status")
	@ApiModelProperty(value = "处理状态")
	private String handleStatus;

    /**
     * 序号
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "序号")
    private Long sort;

    /**
     * 启用/停用状态（数据字典的键值或配置档案的编码）
     */
    @NotBlank(message = "确认状态不能为空")
    //@Excel(name = "启用/停用状态", dictType = "s_valid_flag")
    @ApiModelProperty(value = "启用/停用状态（数据字典的键值或配置档案的编码）")
    private String status;

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


	@Excel(name = "确认人")
	@ApiModelProperty(value = "确认人昵称")
	@TableField(exist = false)
	private String confirmerAccountName;

	/**
	 * 确认时间
	 */
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
	@Excel(name = "确认时间", width = 30, dateFormat = "yyyy-MM-dd")
	@ApiModelProperty(value = "确认时间")
	private Date confirmDate;

	/**
     * 创建人账号（用户名称）
     */
    //@Excel(name = "创建人账号（用户名称）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;


    /**
     * 更新人账号（用户名称）
     */
    //@Excel(name = "更新人账号（用户名称）")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;



    /**
     * 确认人账号（用户名称）
     */
    //@Excel(name = "确认人账号（用户名称）")
    @ApiModelProperty(value = "确认人账号（用户名称）")
    private String confirmerAccount;



    /**
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    //@Excel(name = "数据源系统（数据字典的键值或配置档案的编码）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;



}
