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
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

/**
 * 工序对象 s_man_process
 *
 * @author linhongwei
 * @date 2021-03-26
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_man_process")
public class ManProcess extends EmsBaseEntity {

    /**
     * 客户端口号
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /**
     * 系统自增长ID-工序
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-工序")
    private Long processSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] processSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "工序编码名称模糊搜索")
    private String processCodeName;

    /**
     * 工序编码
     */
    @Excel(name = "工序编码")
    @NotEmpty(message = "工序编码不能为空")
    @Length(max = 8, message = "工序编码不能超过8个字符")
    @ApiModelProperty(value = "工序编码")
    private String processCode;

    /**
     * 工序名称
     */
    @NotEmpty(message = "工序名称不能为空")
    @Excel(name = "工序名称")
    @Length(max = 100, message = "工序名称不能超过100个字符")
    @ApiModelProperty(value = "工序名称")
    private String processName;

    /**
     * 生产方式（数据字典的键值或配置档案的编码），自产、外发
     */
    @NotEmpty(message = "生产方式（默认）不能为空")
    @Excel(name = "生产方式（默认）", dictType = "s_production_mode")
    @ApiModelProperty(value = "生产方式（数据字典的键值或配置档案的编码），自产、外发")
    private String productionMode;

    @ApiModelProperty(value = "操作部门名称")
    @TableField(exist = false)
    @Excel(name = "操作部门（默认）")
    private String departmentName;

    @TableField(exist = false)
    @ApiModelProperty(value = "生产方式")
    private String[] productionModeList;

    /**
     * 所属生产阶段（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "所属生产阶段（数据字典的键值或配置档案的编码）")
    private String produceStage;

    @TableField(exist = false)
    @ApiModelProperty(value = "所属生产阶段")
    private String[] produceStageList;

    /**
     * 所属生产阶段名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "所属生产阶段名称")
    private String produceStageName;

    /**
     * 系统自增长ID-公司档案
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-公司档案")
    private Long companySid;

    /**
     * 吊挂系统对应工序（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "吊挂系统对应工序（数据字典的键值或配置档案的编码）")
    private String hangSysProcess;

    @Excel(name = "是否被道序引用", dictType = "sys_yes_no")
    @NotBlank(message = "请选择是否被道序引用")
    @ApiModelProperty(value = "是否被道序引用（数据字典的键值）")
    private String isProcessStepUsed;

    /**
     * 是否标志成品完工的工序（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "是否第一个工序", dictType = "sys_yes_no")
    @ApiModelProperty(value = "是否第一个工序（数据字典的键值或配置档案的编码）")
    private String isFirstProcess;

    /**
     * 是否录入明细完工量（数据字典的键值或配置档案的编码），用于生产进度日报
     */
    @Excel(name = "是否录入明细完工量", dictType = "sys_yes_no")
    @ApiModelProperty(value = "是否录入明细完工量（数据字典的键值或配置档案的编码）")
    private String isDetailQuantityEntry;

    @TableField(exist = false)
    @ApiModelProperty(value = "工序类别（数据字典的键值或配置档案的编码）")
    private String[] processCategoryList;

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

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态（数据字典的键值）")
    private String[] handleStatusList;


    /**
     * 是否标志成品完工的工序（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "是否标志成品完工的工序", dictType = "sys_yes_no")
    @ApiModelProperty(value = "是否标志成品完工的工序（数据字典的键值或配置档案的编码）")
    private String isProduceComplete;

    /**
     * 是否标志部门完成的工序（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "是否标志部门完成的工序", dictType = "sys_yes_no")
    @ApiModelProperty(value = "是否标志部门完成的工序（数据字典的键值或配置档案的编码）")
    private String isStageComplete;


    @ApiModelProperty(value = "完成量校验参考工序名称")
    @TableField(exist = false)
    @Excel(name = "完成量校验参考工序")
    private String quantityReferProcessName;


    @Excel(name = "参考工序所引用数量类型", dictType = "s_quantity_type_refer_process")
    @ApiModelProperty(value = "参考工序所引用数量类型（数据字典的键值或配置档案的编码）")
    private String quantityTypeReferProcess;
    /**
     * 创建人账号
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    @Excel(name = "特殊工序标识",dictType = "s_special_flag")
    @ApiModelProperty(value = "特殊工序标识（数据字典的键值或配置档案的编码）")
    private String specialFlag;

    @Excel(name = "工序类别", dictType = "s_process_category")
    @ApiModelProperty(value = "工序类别（数据字典的键值或配置档案的编码）")
    private String processCategory;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：特殊工序标识")
    private String[] specialFlagList;

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
     * 更新人账号
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号")
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
     * 确认人账号
     */
    @ApiModelProperty(value = "确认人账号")
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
     * 数据源系统
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司编码")
    private String companyCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "用户昵称")
    private String nickName;

    @ApiModelProperty(value = "操作部门（数据字典的键值或配置档案的编码）")
    private String department;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "操作部门sid")
    private Long departmentSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "操作部门（数据字典的键值或配置档案的编码）")
    private String departmentCode;

    @ApiModelProperty(value = "查询：操作部门")
    @TableField(exist = false)
    private String[] departmentList;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "完成量校验参考工序sid")
    private Long quantityReferProcessSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = " 完成量校验参考工序code")
    private String quantityReferProcessCode;

}
