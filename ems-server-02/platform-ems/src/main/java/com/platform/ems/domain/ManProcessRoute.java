package com.platform.ems.domain;

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

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.List;

/**
 * 工艺路线对象 s_man_process_route
 *
 * @author linhongwei
 * @date 2021-03-26
 */
@Data
@Accessors(chain = true)
@ApiModel
//@JsonInclude(JsonInclude.Include.NON_NULL)
@TableName(value = "s_man_process_route")
public class ManProcessRoute extends EmsBaseEntity {

    /**
     * 客户端口号
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /**
     * 系统自增长ID-工艺路线
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-工艺路线")
    private Long processRouteSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] processRouteSidList;

    /**
     * 工艺路线编码
     */
    @Excel(name = "工艺路线编码")
    @NotEmpty(message = "工艺路线编码不能为空")
    @ApiModelProperty(value = "工艺路线编码")
    @Length(max = 8, message = "工艺路线编码不能超过8个字符")
    private String processRouteCode;

    /**
     * 工艺路线名称
     */
    @Excel(name = "工艺路线名称")
    @NotEmpty(message = "工艺路线名称不能为空")
    @Length(max = 100, message = "工艺路线名称不能超过100个字符")
    @ApiModelProperty(value = "工艺路线名称")
    private String processRouteName;

    @Excel(name = "客户")
    @TableField(exist = false)
    @ApiModelProperty(value = "客户")
    private String customerName;

    @TableField(exist = false)
    @Excel(name = "工厂")
    @ApiModelProperty(value = "工厂编码")
    private String plantShortName;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @NotEmpty(message = "状态不能为空")
    @Excel(name = "启用/停用", dictType = "s_valid_flag")
    @ApiModelProperty(value = "启用/停用状态（数据字典的键值）")
    private String status;

    /**
     * 工艺路线类别
     */
    @Excel(name = "类别", dictType = "s_process_route_category")
    @ApiModelProperty(value = "工艺路线类别")
    private String processRouteCategory;

    @TableField(exist = false)
    @ApiModelProperty(value = "工艺路线类别")
    private String[] processRouteCategoryList;

    /**
     * 生效日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "生效日期")
    private Date effectiveDate;

    /**
     * 用途
     */
    @ApiModelProperty(value = "用途")
    private String useableType;

    /**
     * 系统自增长ID-公司档案
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-公司档案")
    private Long companySid;


    /**
     * 处理状态
     */
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态")
    private String[] handleStatusList;

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

    @Excel(name = "更改人")
    @TableField(exist = false)
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
     * 确认人账号
     */
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

    @TableField(exist = false)
    private Integer pageNum;

    @TableField(exist = false)
    private Integer pageSize;

    @TableField(exist = false)
    @ApiModelProperty(value = "明细表")
    List<ManProcessRouteItem> listManProcessRouteItem;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询:类别")
    private String[] processRouteCategorys;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询:编码")
    private String processRouteCodes;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询:用途")
    private String[] useableTypes;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-客户信息")
    private Long customerSid;

    @ApiModelProperty(value = "客户编码")
    private String customerCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询:客户")
    private String[] customerSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询:创建人")
    private String[] creatorAccountList;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询:公司")
    private String[] companySidList;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-工厂")
    private Long plantSid;

    @ApiModelProperty(value = "工厂编码")
    private String plantCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-工厂")
    private Long[] plantSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "工厂编码")
    private String plantName;

}
