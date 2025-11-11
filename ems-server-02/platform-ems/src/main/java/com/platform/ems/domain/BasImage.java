package com.platform.ems.domain;

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

import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;

/**
 * 图案档案对象 s_bas_image
 *
 * @author chenkw
 * @date 2022-12-14
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_bas_image")
public class BasImage extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-图案
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-图案")
    private Long imageSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] imageSidList;

    /**
     * 图案名称
     */
    @Excel(name = "图案名称")
    @ApiModelProperty(value = "图案名称")
    private String imageName;

    /**
     * 图案类型（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "图案类型", dictType = "s_image_type")
    @ApiModelProperty(value = "图案类型（数据字典的键值或配置档案的编码）")
    private String imageType;


    /**
     * 图案类型（多选）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "图案类型（数据字典的键值或配置档案的编码）")
    private String[] imageTypeList;

    /**
     * 图案说明
     */
    @ApiModelProperty(value = "图案说明")
    private String imageDescription;

    /**
     * 图片路径（主图）
     */
    @ApiModelProperty(value = "图片路径（主图）")
    private String picturePath;

    /**
     * 启用/停用状态（数据字典的键值或配置档案的编码）
     */
    @NotBlank(message = "启停状态不能为空")
    @Excel(name = "启用/停用", dictType = "s_valid_flag")
    @ApiModelProperty(value = "启用/停用状态（数据字典的键值或配置档案的编码）")
    private String status;

    /**
     * 处理状态（数据字典的键值或配置档案的编码）
     */
    @NotBlank(message = "处理状态不能为空")
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

	/**
	 * 处理状态（多选）
	 */
	@TableField(exist = false)
	@ApiModelProperty(value = "处理状态（多选）")
	private String[] handleStatusList;

    /**
     * 图案编码
     */
    @Excel(name = "图案编码")
    @ApiModelProperty(value = "图案编码")
    private String imageCode;

    /**
     * 备注
     */
    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 创建人账号（用户账号）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户账号）")
    private String creatorAccount;

	@Excel(name = "创建人")
	@ApiModelProperty(value = "创建人昵称")
	@TableField(exist = false)
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
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户账号）")
    private String updaterAccount;

	@Excel(name = "更改人")
	@ApiModelProperty(value = "更改人昵称")
	@TableField(exist = false)
	private String updaterAccountName;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更改日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 确认人账号（用户账号）
     */
    @ApiModelProperty(value = "确认人账号（用户账号）")
    private String confirmerAccount;

	@Excel(name = "确认人")
	@ApiModelProperty(value = "确认人昵称")
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
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

	/**
	 * 附件列表
	 */
	@TableField(exist = false)
	@ApiModelProperty(value = "附件列表")
	private List<BasImageAttach> attachmentList;

}
