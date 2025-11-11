package com.platform.ems.domain;

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

import lombok.experimental.Accessors;

/**
 * 生产关注事项对象 s_man_produce_concern_task
 *
 * @author zhuangyz
 * @date 2022-08-01
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_man_produce_concern_task")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ManProduceConcernTask extends EmsBaseEntity {

    /**
     * 租户ID
     */
    //@Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-生产关注事项
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-生产关注事项")
    private Long concernTaskSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] concernTaskSidList;
    /**
     * 生产关注事项编码
     */
    @Excel(name = "生产关注事项编码")
    @ApiModelProperty(value = "生产关注事项编码")
    private Long concernTaskCode;


    @ApiModelProperty(value = "查询：事项类型")
    @TableField(exist = false)
    private String[] concernTaskTypeList;

    /**
     * 生产关注事项名称
     */
    @Excel(name = "生产关注事项名称")
    @ApiModelProperty(value = "生产关注事项名称")
    private String concernTaskName;

    @Excel(name = "图片是否必传",dictType = "s_yesno_flag")
    @ApiModelProperty(value = "图片是否必传")
    private String isPictureUpload;

    @Excel(name = "附件是否必传",dictType = "s_yesno_flag")
    @ApiModelProperty(value = "附件是否必传")
    private String isAttachUpload;

    /**
     * 所属生产阶段（数据字典的键值或配置档案的编码）
     */

    @ApiModelProperty(value = "所属生产阶段")
    private String produceStage;

    @Excel(name = "所属生产阶段")
    @ApiModelProperty(value = "所属生产阶段")
    @TableField(exist = false)
    private String produceStageName;


    @ApiModelProperty(value = "所属生产阶段列表")
    @TableField(exist = false)
    private List<String> produceStageList;

    @Excel(name = "事项类型",dictType = "s_concern_task_type")
    @ApiModelProperty(value = "事项类型")
    private String concernTaskType;

    /**
     * 启用/停用状态（数据字典的键值或配置档案的编码）
     */
    @NotBlank(message = "确认状态不能为空")
    @Excel(name = "启用/停用", dictType = "s_valid_flag")
    @ApiModelProperty(value = "启用/停用状态（数据字典的键值或配置档案的编码）")
    private String status;

    /**
     * 处理状态（数据字典的键值或配置档案的编码）
     */
    @NotBlank(message = "启停状态不能为空")
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    @TableField(exist = false)
    private List<String> handleStatusList;

	@Excel(name = "备注")
	@ApiModelProperty(value ="备注")
	private String remark;

    /**
     * 创建人账号（用户账号）
     */
    //@Excel(name = "创建人")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;


	/**
	 * 创建人账号（用户账号）
	 */
	@Excel(name = "创建人")
	@TableField(exist = false)
	@ApiModelProperty(value = "创建人账号")
	private String creatorAccountName;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /**
     * 更新人账号（用户账号）
     */
    //@Excel(name = "更新人")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人")
    private String updaterAccount;

	/**
	 * 更新人账号（用户账号）
	 */
	@Excel(name = "更新人")
	@TableField(exist = false)
	@ApiModelProperty(value = "更新人")
	private String updaterAccountName;


	/**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更新日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 确认人账号（用户账号）
     */
    //@Excel(name = "确认人")
    @ApiModelProperty(value = "确认人账号（用户账号）")
    private String confirmerAccount;

	/**
	 * 确认人账号（用户账号）
	 */
	@Excel(name = "确认人")
	@ApiModelProperty(value = "确认人账号（用户账号）")
	@TableField(exist = false)
	private String confirmerAccountName;

    /**
     * 确认时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "确认日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    //@Excel(name = "数据源系统（数据字典的键值或配置档案的编码）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;


}
