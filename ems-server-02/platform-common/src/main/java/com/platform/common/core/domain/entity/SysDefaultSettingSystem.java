package com.platform.common.core.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 系统默认设置_系统级对象 s_sys_default_setting_system
 *
 * @author chenkw
 * @date 2022-04-22
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_sys_default_setting_system")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SysDefaultSettingSystem extends BaseEntity {

    /**
     * 租户ID
     */
    @NotBlank(message = "租户ID不能为空")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @Excel(name = "长时间未操作限时(分钟)")
    @ApiModelProperty(value = "长时间未操作限时(分钟)")
    private Integer logonTimeout;

    /**
     * 即将到期预警天数(劳动合同)
     */
    @Excel(name = "即将到期预警天数(劳动合同)")
    @ApiModelProperty(value = "即将到期预警天数(劳动合同)")
    private BigDecimal toexpireDaysLdht;

    /**
     * 即将到期预警天数(试用期)
     */
    @Excel(name = "即将到期预警天数(试用期)")
    @ApiModelProperty(value = "即将到期预警天数(试用期)")
    private BigDecimal toexpireDaysSyq;

    /**
     * 即将到期预警天数(雇主责任险)
     */
    @Excel(name = "即将到期预警天数(雇主责任险) ")
    @ApiModelProperty(value = "即将到期预警天数(雇主责任险) ")
    private Integer toexpireDaysGzzrx;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private String[] clientIdList;

    /**
     * 部署方式（数据字典的键值或配置档案的编码）
     */
    @NotBlank(message = "部署方式不能为空")
    @Excel(name = "部署方式（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "部署方式（数据字典的键值或配置档案的编码）")
    private String deploymentMode;

    /**
     * 行业解决方案（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "行业解决方案（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "行业解决方案（数据字典的键值或配置档案的编码）")
    private String industrySolution;

    /**
     * 系统级对应的租户ID（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "系统级对应的租户ID（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "系统级对应的租户ID（数据字典的键值或配置档案的编码）")
    private String sysClientId;

    /**
     * 即将到期预警天数(采购合同)
     */
    @Excel(name = "即将到期预警天数(采购合同) ")
    @ApiModelProperty(value = "即将到期预警天数(采购合同) ")
    private BigDecimal toexpireDaysCght;

    /**
     * 即将到期预警天数(采购订单)
     */
    @Excel(name = "即将到期预警天数(采购订单) ")
    @ApiModelProperty(value = "即将到期预警天数(采购订单) ")
    private BigDecimal toexpireDaysCgdd;

    /**
     * 即将到期预警天数(销售合同)
     */
    @Excel(name = "即将到期预警天数(销售合同) ")
    @ApiModelProperty(value = "即将到期预警天数(销售合同) ")
    private BigDecimal toexpireDaysXsht;

    /**
     * 即将到期预警天数(销售订单)
     */
    @Excel(name = "即将到期预警天数(销售订单) ")
    @ApiModelProperty(value = "即将到期预警天数(销售订单) ")
    private BigDecimal toexpireDaysXsdd;

    /**
     * 即将到期预警天数(生产订单)
     */
    @Excel(name = "即将到期预警天数(生产订单) ")
    @ApiModelProperty(value = "即将到期预警天数(生产订单) ")
    private BigDecimal toexpireDaysScdd;

    /**
     * 即将到期预警天数(生产订单-工序)
     */
    @Excel(name = "即将到期预警天数(生产订单-工序) ")
    @ApiModelProperty(value = "即将到期预警天数(生产订单-工序) ")
    private BigDecimal toexpireDaysScddGx;

    /**
     * 即将到期预警天数(生产订单-商品)
     */
    @Excel(name = "即将到期预警天数(生产订单-商品) ")
    @ApiModelProperty(value = "即将到期预警天数(生产订单-商品) ")
    private BigDecimal toexpireDaysScddSp;

    /**
     * 即将到期预警天数(项目)
     */
    @Excel(name = "即将到期预警天数(项目)")
    @ApiModelProperty(value = "即将到期预警天数(项目)")
    private BigDecimal toexpireDaysProject;

    /**
     * 项目任务执行提醒天数
     */
    @Excel(name = "项目任务执行提醒天数")
    @ApiModelProperty(value = "项目任务执行提醒天数")
    private BigDecimal toexecuteNoticeDaysPrjTask;

    /**
     * 处理状态（数据字典的键值或配置档案的编码）
     */
    @NotBlank(message = "处理状态不能为空")
    @Excel(name = "处理状态（数据字典的键值或配置档案的编码）", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    /**
     * 创建人账号（用户账号）
     */
    @NotBlank(message = "创建人不能为空")
    @Excel(name = "创建人账号（用户账号）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户账号）")
    private String creatorAccount;

    @TableField(exist = false)
    @ApiModelProperty(value = "创建人（用户昵称）")
    private String creatorAccountName;

    /**
     * 创建时间
     */
    @NotNull(message = "创建日期不能为空")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /**
     * 更新人账号（用户账号）
     */
    @Excel(name = "更新人账号（用户账号）")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户账号）")
    private String updaterAccount;

    @ApiModelProperty(value = "更新人（用户昵称）")
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

    @ApiModelProperty(value = "即将到期预警天数(生产订单-事项)")
    private BigDecimal toexpireDaysScddSx;


}
