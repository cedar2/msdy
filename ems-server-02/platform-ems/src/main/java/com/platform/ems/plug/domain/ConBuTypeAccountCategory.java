package com.platform.ems.plug.domain;

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

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import lombok.experimental.Accessors;

/**
 * 业务类型对应款项类别对象 s_con_bu_type_account_category
 *
 * @author chenkw
 * @date 2022-06-22
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_con_bu_type_account_category")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConBuTypeAccountCategory extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-业务类型对应的款项类别sid
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-业务类型对应的款项类别sid")
    private Long sid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] sidList;

    /**
     * 收付款类型编码
     */
    @NotBlank(message = "请选择收付款类型")
    @Excel(name = "收付款类型", dictType = "s_shoufukuan_type")
    @ApiModelProperty(value = "收付款类型编码")
    private String finShoufukuanTypeCode;

    /**
     * 业务类型编码
     */
    @NotBlank(message = "请选择业务类型")
    @ApiModelProperty(value = "业务类型编码")
    private String buTypeCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "业务类型(多选)")
    private String[] buTypeCodeList;

    @TableField(exist = false)
    @Excel(name = "业务类型")
    @ApiModelProperty(value = "业务类型名称")
    private String buTypeName;

    /** 款项类别编码 */
    @NotBlank(message = "款项类别")
    @ApiModelProperty(value = "款项类别编码")
    private String accountCategoryCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "款项类别(多选)")
    private String[] accountCategoryCodeList;

    @TableField(exist = false)
    @Excel(name = "款项类别")
    @ApiModelProperty(value = "款项类别名称")
    private String accountCategoryName;

    @ApiModelProperty(value = "流水类型编码")
    private String bookTypeCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "流水类型(多选)")
    private String[] bookTypeCodeList;

    @TableField(exist = false)
    @Excel(name = "流水类型")
    @ApiModelProperty(value = "流水类型名称")
    private String bookTypeName;

    @ApiModelProperty(value = "流水来源类别编码")
    private String bookSourceCategory;

    @TableField(exist = false)
    @ApiModelProperty(value = "流水来源类别")
    private String[] bookSourceCategoryList;

    @TableField(exist = false)
    @Excel(name = "流水来源类别")
    @ApiModelProperty(value = "流水来源类别名称")
    private String bookSourceCategoryName;

    /**
     * 序号
     */
    @ApiModelProperty(value = "序号")
    private Long sort;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态（数据字典的键值）")
    private String status;

    /**
     * 处理状态（数据字典的键值）
     */
    @NotEmpty(message = "处理状态不能为空")
    @ApiModelProperty(value = "处理状态（数据字典的键值）")
    private String handleStatus;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

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

    @TableField(exist = false)
    private String updaterAccountName;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 确认人账号（用户名称）
     */
    @ApiModelProperty(value = "确认人账号（用户名称）")
    private String confirmerAccount;

    @TableField(exist = false)
    private String confirmerAccountName;

    /**
     * 确认时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 数据源系统（数据字典的键值）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值）")
    private String dataSourceSys;


}
