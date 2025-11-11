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
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.List;

/**
 * 工作中心/班组对象 s_man_work_center
 *
 * @author linhongwei
 * @date 2021-03-26
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_man_work_center")
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class ManWorkCenter extends EmsBaseEntity {

    /**
     * 客户端口号
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /**
     * 系统自增长ID-工作中心/班组
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-工作中心/班组")
    private Long workCenterSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] workCenterSidList;

    /**
     * 工作中心/班组编码
     */
    @Excel(name = "班组编码")
    @NotEmpty(message = "工作中心/班组编码不能为空")
    @Length(max = 8, message = "工作中心/班组编码不能超过8个字符")
    @ApiModelProperty(value = "工作中心/班组编码")
    private String workCenterCode;

    /**
     * 工作中心/班组名称
     */
    @Excel(name = "班组名称")
    @NotEmpty(message = "工作中心/班组名称不能为空")
    @Length(max = 100, message = "工作中心/班组名称不能超过100个字符")
    @ApiModelProperty(value = "工作中心/班组名称")
    private String workCenterName;

    /**
     * 系统自增长ID-工厂
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-工厂")
    private Long plantSid;

    @Excel(name = "所属工厂")
    @ApiModelProperty(value = "工厂名称")
    @TableField(exist = false)
    private String plantName;

    /**
     * 所属部门sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "所属部门sid")
    private Long departmentSid;

    /**
     * 部门名称
     */
    @TableField(exist = false)
    @Excel(name = "操作部门")
    @ApiModelProperty(value = "所属部门名称")
    private String departmentName;


    @ApiModelProperty(value = "班组类型")
    @Excel(name = "班组类型",dictType = "s_work_center_type")
    private String workCenterType;

    @ApiModelProperty(value = "供应商名称")
    @Excel(name = "供应商")
    @TableField(exist = false)
    private String vendorName;

    /**
     * 处理状态
     */
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @Excel(name = "启用/停用", dictType = "s_valid_flag")
    @ApiModelProperty(value = "状态")
    private String status;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-公司")
    private Long companySid;

    @ApiModelProperty(value = "系统自增长ID-公司")
    @TableField(exist = false)
    private String companyName;

    @ApiModelProperty(value = "系统SID-工序，支持多值保存，每个值用;隔开")
    private String processSids;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-工序")
    private String[] processSidList;

    @ApiModelProperty(value = "工序编码，支持多值保存，每个值用;隔开")
    private String processCodes;

    @TableField(exist = false)
    @ApiModelProperty(value = "工序名称，每个值用;隔开")
    private String processNames;

    /**
     * 负责人
     */
    @ApiModelProperty(value = "负责人")
    private String director;

    /**
     * 负责人
     */
    @TableField(exist = false)
    @Excel(name = "负责人")
    @ApiModelProperty(value = "负责人")
    private String directorName;

    /**
     * 用途
     */
    @ApiModelProperty(value = "用途")
    private String useableType;

    /**
     * 产量
     */
    @Excel(name = "产能/天")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "产量")
    private Long output;

    /**
     * 工作中心/班组类别
     */
    @Excel(name = "类别", dictType = "s_work_center_category")
    @ApiModelProperty(value = "工作中心/班组类别")
    private String workCenterCategory;

    /**
     * 地址
     */
    @Excel(name = "地址")
    @ApiModelProperty(value = "地址")
    private String address;

    @TableField(exist = false)
    @ApiModelProperty(value = "工作中心/班组类别")
    private String[] workCenterCategoryList;

    /**
     * 投料区域
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "投料区域")
    private Long depositArea;

    /**
     * 产量的时间单位（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "产量的时间单位（数据字典的键值或配置档案的编码）")
    private String timeUnit;

    /**
     * 产量的计量单位（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "产量的计量单位（数据字典的键值或配置档案的编码）")
    private String unit;

    /**
     * 联系电话
     */
    @ApiModelProperty(value = "联系电话")
    private String phone;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 创建人账号
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号")
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
     * 更新人账号
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccount;

    /**
     * 更新人名称
     */
    @TableField(exist = false)
    @Excel(name = "更改人")
    @ApiModelProperty(value = "更新人名称")
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

    @ApiModelProperty(value = "明细表")
    @TableField(exist = false)
    private List<ManWorkCenterProcess> listManWorkCenterProcess;

    @ApiModelProperty(value = "工作中心-成员")
    @TableField(exist = false)
    private List<ManWorkCenterMember> workCenterMemberList;

    @ApiModelProperty(value = "查询：工作中心/班组编码")
    @TableField(exist = false)
    private String workCenterCodes;

    @ApiModelProperty(value = "查询：类别")
    @TableField(exist = false)
    private String[] workCenterCategorys;

    @ApiModelProperty(value = "查询：工厂")
    @TableField(exist = false)
    private Long[] plantSidList;

    @TableField(exist = false)
    private Integer pageNum;

    @TableField(exist = false)
    private Integer pageSize;



    @ApiModelProperty(value = "操作部门（数据字典的键值或配置档案的编码）")
    private String departmentCode;


    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "供应商sid")
    private Long vendorSid;

    @ApiModelProperty(value = "供应商编码")
    private String vendorCode;

    @ApiModelProperty(value = "查询：创建人")
    @TableField(exist = false)
    private String[] creatorAccountList;

    @ApiModelProperty(value = "查询：处理状态")
    @TableField(exist = false)
    private String[] handleStatusList;

    @ApiModelProperty(value = "查询：供应商")
    @TableField(exist = false)
    private Long[] vendorSidList;

    @ApiModelProperty(value = "查询：操作部门")
    @TableField(exist = false)
    private String[] departmentList;

    @ApiModelProperty(value = "查询：班组类型")
    @TableField(exist = false)
    private String[] workCenterTypeList;

    @ApiModelProperty(value = "成员名单(隶属)")
    @TableField(exist = false)
    private List<BasStaff> StaffList;

}
