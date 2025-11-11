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
import javax.validation.constraints.NotEmpty;
import lombok.experimental.Accessors;

/**
 * 数据对象类别编码规则对象 s_con_dataobject_code_rule
 *
 * @author chenkw
 * @date 2021-11-25
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_con_dataobject_code_rule")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConDataobjectCodeRule extends EmsBaseEntity{

    /** 租户ID */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /** 系统SID-数据对象类别sid */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-数据对象类别sid")
    private Long sid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long [] sidList;
    /** 系统SID-数据对象类别sid */
    @Excel(name = "系统SID-数据对象类别sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-数据对象类别sid")
    private Long dataobjectCategorySid;

    @TableField(exist = false)
    @ApiModelProperty(value = "数据对象类别编码")
    private String dataobjectCategoryCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "数据对象类别名称")
    private String dataobjectCategoryName;

    /** 编码方式（数据字典的键值或配置档案的编码） */
    @Excel(name = "编码方式（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "编码方式（数据字典的键值或配置档案的编码）")
    private String codeMode;

    /** 前缀 */
    @Excel(name = "前缀")
    @ApiModelProperty(value = "前缀")
    private String prefix;

    /** 流水号(起)  */
    @Excel(name = "流水号(起) ")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "流水号(起) ")
    private Long serialNumberFrom;

    /** 流水号(至)  */
    @Excel(name = "流水号(至) ")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "流水号(至) ")
    private Long serialNumberTo;

    /** 当前流水号 */
    @Excel(name = "当前流水号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "当前流水号")
    private Long serialNumberCurrent;

    /** 业务类别 */
    @Excel(name = "业务类别")
    @ApiModelProperty(value = "业务类别")
    private String businessCategory;

    /** 业务类别说明 */
    @Excel(name = "业务类别说明")
    @ApiModelProperty(value = "业务类别说明")
    private String businessCategoryDesc;

    /** 启用/停用状态（数据字典的键值或配置档案的编码） */
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "启用/停用状态（数据字典的键值或配置档案的编码）",dictType = "s_valid_flag")
    @ApiModelProperty(value = "启用/停用状态（数据字典的键值或配置档案的编码）")
    private String status;

    /** 处理状态（数据字典的键值或配置档案的编码） */
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "处理状态（数据字典的键值或配置档案的编码）",dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    /** 创建人账号（用户名称） */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    @TableField(exist = false)
    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人")
    private String creatorAccountName;

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

    @TableField(exist = false)
    @Excel(name = "更新人")
    @ApiModelProperty(value = "更新人")
    private String updaterAccountName;

    /** 更新时间 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /** 确认人账号（用户名称） */
    @Excel(name = "确认人账号（用户名称）")
    @ApiModelProperty(value = "确认人账号（用户名称）")
    private String confirmerAccount;

    @TableField(exist = false)
    @Excel(name = "确认人")
    @ApiModelProperty(value = "确认人")
    private String confirmerAccountName;

    /** 确认时间 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "确认时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /** 数据源系统（数据字典的键值或配置档案的编码） */
    @Excel(name = "数据源系统（数据字典的键值或配置档案的编码）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    @TableField(exist = false)
    @ApiModelProperty(value = "新增的数量")
    private Long number;
}
