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
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.List;

/**
 * 收付款方式组合对象 s_con_account_method_group
 *
 * @author linhongwei
 * @date 2021-05-19
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_con_account_method_group")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConAccountMethodGroup extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-收付款方式组合sid
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-收付款方式组合sid")
    private Long sid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] sidList;
    /**
     * 收付款方式组合编码
     */
    @Length(max = 8, message = "收付款方式组合编码不能超过8个字符")
    @NotEmpty(message = "收付款方式组合编码不能为空")
    @Excel(name = "收付款方式组合编码")
    @ApiModelProperty(value = "收付款方式组合编码")
    private String code;

    /**
     * 收付款方式组合名称
     */
    @Length(max = 300, message = "收付款方式组合名称不能超过300个字符")
    @NotEmpty(message = "收付款方式组合名称不能为空")
    @Excel(name = "收付款方式组合名称")
    @ApiModelProperty(value = "收付款方式组合名称")
    private String name;

    /**
     * 收付款类型编码（数据字典的键值或配置档案的编码）
     */
    @NotEmpty(message = "收付款类型不能为空")
    @Excel(name = "收付款类型", dictType = "s_shoufukuan_type")
    @ApiModelProperty(value = "收付款类型编码（数据字典的键值或配置档案的编码）")
    private String shoufukuanType;

    /**
     * 预付款比例（存值，即：不含百分号，如20%，就存0.2）
     */
    @NotEmpty(message = "预付款比例（%）不能为空")
    @Excel(name = "预付款比例(%)")
    @ApiModelProperty(value = "预付款比例（存值，即：不含百分号，如20%，就存0.2）")
    private String advanceRate;

    /**
     * 预付款账期(天)
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "预付款账期(天)")
    private Long yfAccountValidDays;

    /**
     * 中期款比例（存值，即：不含百分号，如20%，就存0.2）
     */
    @NotEmpty(message = "中期款比例（%）不能为空")
    @Excel(name = "中期款比例(%)")
    @ApiModelProperty(value = "中期款比例（存值，即：不含百分号，如20%，就存0.2）")
    private String middleRate;

    /**
     * 中期款账期(天)
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "中期款账期(天)")
    private Long zqAccountValidDays;

    /**
     * 尾款比例（存值，即：不含百分号，如20%，就存0.2）
     */
    @NotEmpty(message = "尾款比例（%）不能为空")
    @Excel(name = "尾款比例(%)")
    @ApiModelProperty(value = "尾款比例（存值，即：不含百分号，如20%，就存0.2）")
    private String remainRate;

    /**
     * 尾款账期(天)
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "尾款账期(天)")
    private Long wqAccountValidDays;

    /**
     * 账期天类型编码（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "账期天类型", dictType = "s_day_type")
    @ApiModelProperty(value = "账期天类型编码（数据字典的键值或配置档案的编码）")
    private String dayType;

    /**
     * 启用/停用状态（数据字典的键值）
     */
    @NotEmpty(message = "启停状态不能为空")
    @Excel(name = "启用/停用", dictType = "s_valid_flag")
    @ApiModelProperty(value = "启用/停用状态（数据字典的键值）")
    private String status;

    /**
     * 处理状态（数据字典的键值）
     */
    @NotEmpty(message = "处理状态不能为空")
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值）")
    private String handleStatus;

    /**
     * 预收付款说明
     */
    @Excel(name = "预收付款说明")
    @ApiModelProperty(value = "预收付款说明")
    private String yushoufukuanRemark;

    /**
     * 中期款说明
     */
    @Excel(name = "中期款说明")
    @ApiModelProperty(value = "中期款说明")
    private String zhongqikuanRemark;

    /**
     * 尾款说明
     */
    @Excel(name = "尾款说明")
    @ApiModelProperty(value = "尾款说明")
    private String weikuanRemark;

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
    @ApiModelProperty(value = "创建人账号（用户名称）")
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
     * 更新人账号（用户名称）
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

    @Excel(name = "更改人")
    @ApiModelProperty(value = "更新人账号（用户名称）")
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
     * 确认人账号（用户名称）
     */
    @ApiModelProperty(value = "确认人账号（用户名称）")
    private String confirmerAccount;

    @Excel(name = "确认人")
    @ApiModelProperty(value = "确认人账号（用户名称）")
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
     * 数据源系统（数据字典的键值）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值）")
    private String dataSourceSys;

    /**
     * 是否允许租户自定义
     */
    @ApiModelProperty(value = "是否允许租户自定义")
    private String isClientDefine;

    /**
     * 序号
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "序号")
    private Long sort;

    @TableField(exist = false)
    @ApiModelProperty(value = "支付方式列表")
    private List<ConAccountMethodGroupMethod> methodList;


}
