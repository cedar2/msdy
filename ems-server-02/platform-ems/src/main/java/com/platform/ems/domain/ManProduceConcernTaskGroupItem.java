package com.platform.ems.domain;

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
 * 生产关注事项组-明细对象 s_man_produce_concern_task_group_item
 *
 * @author zhuangyz
 * @date 2022-08-02
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_man_produce_concern_task_group_item")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ManProduceConcernTaskGroupItem extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-生产关注事项组明细
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-生产关注事项组明细")
    private Long concernTaskGroupItemSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] concernTaskGroupItemSidList;
    /**
     * 系统SID-生产关注事项组
     */
    @Excel(name = "系统SID-生产关注事项组")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-生产关注事项组")
    private Long concernTaskGroupSid;



    /**
     * 事项负责人sid（员工档案）
     */
    @Excel(name = "事项负责人sid（员工档案）")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "事项负责人sid（员工档案）")
    private Long handlerSid;

    /**
     * 事项负责人编码（员工档案）
     */
    @Excel(name = "事项负责人编码（员工档案）")
    @ApiModelProperty(value = "事项负责人编码（员工档案）")
    private String handlerCode;

    @ApiModelProperty(value = "里程碑（数据字典的键值或配置档案的编码）")
    private String milestone;

    @Excel(name = "图片是否必传",dictType = "s_yesno_flag")
    @ApiModelProperty(value = "图片是否必传")
    private String isPictureUpload;

    @Excel(name = "附件是否必传",dictType = "s_yesno_flag")
    @ApiModelProperty(value = "附件是否必传")
    private String isAttachUpload;

    /**
     * 序号
     */
    @Excel(name = "序号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "序号")
    private Long sort;

    /**
     * 创建人账号（用户账号）
     */
    @Excel(name = "创建人账号（用户账号）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户账号）")
    private String creatorAccount;

    /**
     * 更新人账号（用户账号）
     */
    @Excel(name = "更新人账号（用户账号）")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户账号）")
    private String updaterAccount;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    @TableField(exist = false)
    @Excel(name = "更改人")
    private String updaterAccountName;

    /**
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "数据源系统（数据字典的键值或配置档案的编码）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;


    @Excel(name = "确认人")
    @ApiModelProperty(value = "确认人昵称")
    @TableField(exist = false)
    private String confirmerAccountName;

	/**
	 * 生产关注事项sid
	 */
	@Excel(name = "生产关注事项sid")
	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty(value = "生产关注事项sid")
	private Long concernTaskSid;

    /**
     * 生产关注事项组编码
     */
    @Excel(name = "生产关注事项组编码")
    @ApiModelProperty(value = "生产关注事项组编码")
    private Long concernTaskCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "事项类型")
    private String concernTaskType;

    @TableField(exist = false)
	private String concernTaskName;

    @TableField(exist = false)
    private String produceStageName;

    @TableField(exist = false)
    private String handlerName;

    @TableField(exist = false)
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

}
