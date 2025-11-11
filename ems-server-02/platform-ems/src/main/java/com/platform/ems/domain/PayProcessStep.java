package com.platform.ems.domain;

import cn.hutool.core.util.StrUtil;
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

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 通用道序对象 s_pay_process_step
 *
 * @author linhongwei
 * @date 2021-09-07
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_pay_process_step")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PayProcessStep extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-通用道序
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-通用道序")
    private Long processStepSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] processStepSidList;
    /**
     * 通用道序编码
     */
//    @Length(max = 20, message = "通用道序编码不能超过20个字符")
//    @NotEmpty(message = "通用道序编码不能为空")
    @Excel(name = "道序编码")
    @ApiModelProperty(value = "道序编码")
    private String processStepCode;

    public void setProcessStepCode(String processStepCode) {
        if (StrUtil.isNotBlank(processStepCode)) {
            processStepCode = processStepCode.trim();
        }
        this.processStepCode = processStepCode;
    }

    public void setProcessStepName(String processStepName) {
        if (StrUtil.isNotBlank(processStepName)) {
            processStepName = processStepName.trim();
        }
        this.processStepName = processStepName;
    }

    /**
     * 通用道序名称
     */
    @Length(max = 300, message = "道序名称不能超过300个字符")
    @NotEmpty(message = "道序名称不能为空")
    @Excel(name = "道序名称")
    @ApiModelProperty(value = "通用道序名称")
    private String processStepName;

    /**
     * 标准工价(元)
     */
    @Digits(integer = 5, fraction = 3, message = "标准工价(元)整数位上限为5位，小数位上限为3位")
    @Excel(name = "标准工价(元)")
    @ApiModelProperty(value = "标准工价(元)")
    private BigDecimal standardPrice;

    /**
     * 货币单位（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "货币单位（数据字典的键值或配置档案的编码）")
    private String currencyUnit;

    /**
     * 所属生产工序sid
     */
    @NotNull(message = "所属生产工序不能为空")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "所属生产工序sid")
    private Long processSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] processSidList;

    /**
     * 工序编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "工序编码")
    private String processCode;

    /**
     * 工序名称
     */
    @TableField(exist = false)
    @Excel(name = "所属生产工序")
    @ApiModelProperty(value = "工序名称")
    private String processName;

    /**
     * 作业单位（数据字典的键值或配置档案的编码）
     */
    @NotEmpty(message = "作业计量单位不能为空")
    @ApiModelProperty(value = "作业计量单位（数据字典的键值或配置档案的编码）")
    private String taskUnit;

    @TableField(exist = false)
    @Excel(name = "作业计量单位")
    @ApiModelProperty(value = "作业计量单位名称")
    private String taskUnitName;

    /**
     * 货币（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "币种", dictType = "s_currency")
    @ApiModelProperty(value = "货币（数据字典的键值或配置档案的编码）")
    private String currency;

    /**
     * 类别（数据字典的键值或配置档案的编码）
     */
    @NotEmpty(message = "道序类别不能为空")
    @Excel(name = "道序类别", dictType = "s_process_step_category")
    @ApiModelProperty(value = "道序类别（数据字典的键值或配置档案的编码）")
    private String stepCategory;

    /**
     * 启用/停用状态（数据字典的键值或配置档案的编码）
     */
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "启用/停用", dictType = "s_valid_flag")
    @ApiModelProperty(value = "启用/停用状态（数据字典的键值或配置档案的编码）")
    private String status;

    @ApiModelProperty(value = "停用说明")
    private String disableRemark;

    /**
     * 处理状态（数据字典的键值或配置档案的编码）
     */
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态list")
    private String[] handleStatusList;

    /**
     * 创建人账号（用户名称）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    /**
     * 创建人名称
     */
    @TableField(exist = false)
    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人名称")
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

    /**
     * 更改人名称
     */
    @TableField(exist = false)
    @Excel(name = "更改人")
    @ApiModelProperty(value = "更改人名称")
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

    /**
     * 确认人名称
     */
    @TableField(exist = false)
    @Excel(name = "确认人")
    @ApiModelProperty(value = "确认人名称")
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

    @TableField(exist = false)
    private String msg;

    @TableField(exist = false)
    private List<Long> processStepSids;
}
