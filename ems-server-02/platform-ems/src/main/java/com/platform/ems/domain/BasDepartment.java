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

import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.List;


/**
 * 部门档案对象 s_bas_department
 *
 * @author qhq
 * @date 2021-04-09
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_bas_department")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasDepartment extends EmsBaseEntity {
    /**
     * 客户端口号
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /**
     * 系统ID-部门档案
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-部门档案")
    private Long departmentSid;

    public void setDepartmentCode(String departmentCode) {
        if (StrUtil.isNotBlank(departmentCode)) {
            departmentCode = departmentCode.replaceAll("\\s*", "");
        }
        this.departmentCode = departmentCode;
    }

    public void setDepartmentName(String departmentName) {
        if (StrUtil.isNotBlank(departmentName)) {
            departmentName = departmentName.trim();
        }
        this.departmentName = departmentName;
    }

    /**
     * 部门编码
     */
    @Excel(name = "部门编码")
    @ApiModelProperty(value = "部门编码")
    private String departmentCode;

    /**
     * 部门名称
     */
    @Excel(name = "部门名称")
    @ApiModelProperty(value = "部门名称")
    private String departmentName;

    /**
     * 公司名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @Excel(name = "公司")
    @TableField(exist = false)
    @ApiModelProperty(value = "公司简称")
    private String companyShortName;

    /**
     * 系统ID-公司档案
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-公司档案")
    private Long companySid;

    /**
     *
     */
    @Excel(name = "类别", dictType = "s_department_type")
    @ApiModelProperty(value = "类别编码")
    private String category;

    @Excel(name = "工资成本分摊类型" , dictType = "s_salary_cost_allocate_type")
    @ApiModelProperty(value = "工资成本分摊类型")
    private String salaryCostAllocateType;

    /**
     * 启用/停用状态
     */
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "启用/停用", dictType = "s_valid_flag")
    @ApiModelProperty(value = "启用/停用状态")
    private String status;

    @ApiModelProperty(value = "停用说明")
    private String disableRemark;

    /**
     * 处理状态
     */
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    /**
     * 部门主管
     */
    @Excel(name = "主管")
    @ApiModelProperty(value = "部门主管")
    private String supervisor;

    /**
     * 部门级别编码
     */
    @ApiModelProperty(value = "部门级别编码")
    private String level;

    /**
     * 部门助理
     */
    @Excel(name = "助理")
    @ApiModelProperty(value = "部门助理")
    private String departmentAid;


    /**
     * 上级主管领导
     */
    //@Excel(name = "上级主管领导")
    @ApiModelProperty(value = "上级主管领导")
    private String superiorLeader;


    @TableField(exist = false)
    @ApiModelProperty(value = "工资成本分摊列表")
    private List<String> salaryCostAllocateTypeList;

    /**
     * 上级分管领导
     */
    @ApiModelProperty(value = "上级分管领导")
    private String superiorSubLeader;

    /**
     * 固定电话
     */
    @Excel(name = "固定电话")
    @ApiModelProperty(value = "固定电话")
    private String telephone;

    /**
     * 传真
     */
    @ApiModelProperty(value = "传真")
    private String fax;

    /**
     * 电子邮箱
     */
    @ApiModelProperty(value = "电子邮箱")
    private String email;

    /**
     * 部门职能描述
     */
    @ApiModelProperty(value = "部门职能描述")
    private String departmentFunction;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 创建人账号
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    @Excel(name = "创建人")
    @TableField(exist = false)
    @ApiModelProperty(value = "创建人账号")
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

    @Excel(name = "更改人")
    @TableField(exist = false)
    @ApiModelProperty(value = "更新人账号")
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

    @TableField(exist = false)
    @Excel(name = "确认人")
    @ApiModelProperty(value = "确认人账号")
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

    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    private Integer serialNum;

    @ApiModelProperty(value = "创建日期开始时间")
    @TableField(exist = false)
    private String beginTime;

    @ApiModelProperty(value = "创建日期结束时间")
    @TableField(exist = false)
    private String endTime;

    @ApiModelProperty(value = "页数")
    @TableField(exist = false)
    private Integer pageNum;

    @ApiModelProperty(value = "每页个数")
    @TableField(exist = false)
    private Integer pageSize;

    /**
     * 系统ID-公司档案list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "系统ID-公司档案list")
    private Long[] companySidList;

    /**
     * 处理状态list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态list")
    private String[] handleStatusList;

    @ApiModelProperty(value = "sids用于批量操作")
    @TableField(exist = false)
    private List<Long> departmentSids;
}
