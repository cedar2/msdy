package com.platform.ems.domain;

import java.util.Date;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableId;
import java.util.List;
import com.platform.common.core.domain.EmsBaseEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import javax.validation.constraints.NotEmpty;
import lombok.experimental.Accessors;

/**
 * 版型部位组档案对象 s_tec_model_position_group
 *
 * @author linhongwei
 * @date 2021-06-02
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_tec_model_position_group")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TecModelPositionGroup extends EmsBaseEntity{

    /** 租户ID */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /** 系统SID-版型部位组档案 */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-版型部位组档案")
    private Long groupSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long [] groupSidList;

    public void setGroupCode(String groupCode) {
        if (StrUtil.isNotBlank(groupCode)){
            groupCode = groupCode.replaceAll("\\s*", "");
        }
        this.groupCode = groupCode;
    }

    public void setGroupName(String groupName) {
        if (StrUtil.isNotBlank(groupName)){
            groupName = groupName.trim();
        }
        this.groupName = groupName;
    }

    /** 版型部位组编码（人工编码） */
    @Excel(name = "版型部位组编码")
    @ApiModelProperty(value = "版型部位组编码（人工编码）")
    private String groupCode;

    /** 版型部位组名称 */
    @Excel(name = "版型部位组名称")
    @ApiModelProperty(value = "版型部位组名称")
    private String groupName;

    /** 上下装/套装（数据字典的键值或配置档案的编码） */
    @Excel(name = "上下装/套装",dictType = "s_up_down_suit")
    @ApiModelProperty(value = "上下装/套装（数据字典的键值或配置档案的编码）")
    private String upDownSuit;

    @ApiModelProperty(value = "查询：上下装/套装")
    @TableField(exist = false)
    private String[] upDownSuitList;

    /** 客户档案sid */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "客户档案sid")
    private Long customerSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "客户档案sid")
    private Long[] customerSidList;

    /** 返回客户名称 */
    @Excel(name = "客户")
    @TableField(exist = false)
    private String customerName;

    @ApiModelProperty(value = "明细列表")
    @TableField(exist = false)
    private List<TecModelPositionGroupItem> itemList;

    /** 启用/停用状态（数据字典的键值或配置档案的编码） */
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "启用/停用",dictType = "s_valid_flag")
    @ApiModelProperty(value = "启用/停用状态（数据字典的键值或配置档案的编码）")
    private String status;

    @ApiModelProperty(value = "停用说明")
    private String disableRemark;

    /** 处理状态（数据字典的键值或配置档案的编码） */
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "处理状态",dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    @Excel(name = "备注")
    @ApiModelProperty(value ="备注")
    private String remark;

    @TableField(exist = false)
    private String[] handleStatusList;

    /** 创建人账号（用户名称） */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    @Excel(name = "创建人")
    @TableField(exist = false)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccountName;

    /** 创建时间 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /** 更新人账号（用户名称） */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

    @Excel(name = "更改人")
    @ApiModelProperty(value = "更新人昵称")
    @TableField(exist = false)
    private String updaterAccountName;

    /** 更新时间 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "更改日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /** 确认人账号（用户名称） */
    @ApiModelProperty(value = "确认人账号（用户名称）")
    private String confirmerAccount;

    @Excel(name = "确认人")
    @ApiModelProperty(value = "确认人昵称")
    @TableField(exist = false)
    private String confirmerAccountName;

    /** 确认时间 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "确认日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /** 数据源系统（数据字典的键值或配置档案的编码） */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

}
