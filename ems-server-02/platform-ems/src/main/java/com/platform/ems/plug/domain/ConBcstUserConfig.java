package com.platform.ems.plug.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 通知用户配置对象 s_con_bcst_user_config
 *
 * @author linhongwei
 * @date 2021-10-12
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_con_bcst_user_config")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConBcstUserConfig extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-通知用户配置信息sid
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-通知用户配置信息sid")
    private Long sid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] sidList;
    /**
     * 数据对象类别sid
     */
    @NotNull(message = "数据对象类别不能为空")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "数据对象类别sid")
    private Long dataobjectCategorySid;

    @TableField(exist = false)
    @ApiModelProperty(value = "数据对象类别list")
    private Long[] dataobjectCategorySidList;

    /**
     * 数据对象类别code
     */
    @ApiModelProperty(value = "数据对象类别code")
    private String dataobjectCategoryCode;

    @Excel(name = "数据对象类别")
    @TableField(exist = false)
    @ApiModelProperty(value = "数据对象类别名称")
    private String dataobjectCategoryName;

    /**
     * 通知类型（数据字典的键值或配置档案的编码）
     */
    @NotEmpty(message = "通知类型不能为空")
    @Excel(name = "通知类型", dictType = "s_bcst_type")
    @ApiModelProperty(value = "通知类型（数据字典的键值或配置档案的编码）")
    private String bcstType;

    @TableField(exist = false)
    @ApiModelProperty(value = "通知类型list")
    private String[] bcstTypeList;

    /**
     * 通知人账号（用户账号）
     */
    @NotEmpty(message = "通知人不能为空")
    @ApiModelProperty(value = "通知人账号（用户账号）")
    private String userAccount;

    @TableField(exist = false)
    @ApiModelProperty(value = "通知人账号list")
    private String[] userAccountList;

    @TableField(exist = false)
    @Excel(name = "通知人")
    @ApiModelProperty(value = "通知人名称")
    private String userAccountName;

    @Excel(name = "备注")
    @ApiModelProperty(value ="备注")
    private String remark;

    /**
     * 序号
     */
    @Excel(name = "序号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "序号")
    private Long sort;

    /**
     * 处理状态（数据字典的键值或配置档案的编码）
     */
    @NotEmpty(message = "处理状态不能为空")
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    /**
     * 创建人账号（用户账号）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户账号）")
    private String creatorAccount;

    @TableField(exist = false)
    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人账号（用户账号）")
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

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 确认人账号（用户账号）
     */
    @ApiModelProperty(value = "确认人账号（用户账号）")
    private String confirmerAccount;

    /**
     * 确认时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "用户id")
    private Long userId;

    @TableField(exist = false)
    @ApiModelProperty(value = "邮箱")
    private String email;


    /** 微信openId */
    @TableField(exist = false)
    @ApiModelProperty(value = "微信openId")
    private String wechatOpenid;

    /** 企业微信openId */
    @TableField(exist = false)
    @ApiModelProperty(value = "企业微信openId")
    private String workWechatOpenid;

    /** 钉钉id */
    @TableField(exist = false)
    @ApiModelProperty(value = "钉钉id")
    private String dingtalkOpenid;

    /** 微信公众号id */
    @TableField(exist = false)
    @ApiModelProperty(value = "微信公众号id")
    private String wxGzhOpenid;

    /** 微信小程序id */
    @TableField(exist = false)
    @ApiModelProperty(value = "微信小程序id")
    private String wxXcxOpenid;

}
