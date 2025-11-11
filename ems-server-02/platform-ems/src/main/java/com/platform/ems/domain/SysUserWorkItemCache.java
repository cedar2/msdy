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
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 用户工作台提醒缓存对象 s_sys_user_work_item_cache
 *
 * @author platform
 * @date 2024-04-28
 */
@Data
@Accessors(chain = true)
@TableName(value = "s_sys_user_work_item_cache")
public class SysUserWorkItemCache extends EmsBaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-用户工作台提醒缓存信息
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-用户工作台提醒缓存信息")
    private Long userWorkItemCacheSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "sid数组")
    private Long[] userWorkItemCacheSidList;

    /**
     * 用户ID
     */
    @Excel(name = "用户ID")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @TableField(exist = false)
    @ApiModelProperty(value = "用户昵称")
    private String nickName;

    @TableField(exist = false)
    @ApiModelProperty(value = "企业微信openId")
    private String workWechatOpenid;

    @TableField(exist = false)
    @ApiModelProperty(value = "钉钉id")
    private String dingtalkOpenid;

    @TableField(exist = false)
    @ApiModelProperty(value = "飞书的openid")
    private String feishuOpenId;

    /**
     * 待办项数量
     */
    @Excel(name = "待办项数量")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "待办项数量")
    private Integer dbQuantity;

    /**
     * 待批项数量
     */
    @Excel(name = "待批项数量")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "待批项数量")
    private Integer dpQuantity;

    /**
     * 已逾期项数量
     */
    @Excel(name = "已逾期项数量")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "已逾期项数量")
    private Integer yyqQuantity;

    /**
     * 即将到期项数量
     */
    @Excel(name = "即将到期项数量")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "即将到期项数量")
    private Integer jjdqQuantity;

    /**
     * 创建人账号（用户账号）
     */
    @TableField(fill = FieldFill.INSERT)
    @Excel(name = "创建人账号")
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    /**
     * 创建人
     */
    @TableField(exist = false)
    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人昵称")
    private String creatorAccountName;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    public SysUserWorkItemCache() {

    }

    public SysUserWorkItemCache(String clientId, Long userId) {
        this.clientId = clientId;
        this.userId = userId;
        this.dbQuantity = 0;
        this.dpQuantity = 0;
        this.yyqQuantity = 0;
        this.jjdqQuantity = 0;
    }

}
