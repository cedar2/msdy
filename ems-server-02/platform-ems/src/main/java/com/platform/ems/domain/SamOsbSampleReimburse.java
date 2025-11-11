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

import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.List;


/**
 * 外采样报销单-主对象 s_sam_osb_sample_reimburse
 *
 * @author qhq
 * @date 2021-12-28
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_sam_osb_sample_reimburse")
public class SamOsbSampleReimburse extends EmsBaseEntity {
    /** 租户ID */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /** 系统SID-外采样报销单 */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-外采样报销单")
    private Long reimburseSid;
    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] reimburseSidList;

    /** 外采样报销单号 */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "外采样报销单号")
    @Excel(name="外采样报销单号",sort = 1)
    private Long reimburseCode;

    /** 单据日期 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "单据日期")
    private Date documentDate;

    /** 公司sid */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司sid")
    private Long companySid;

    @Excel(name = "公司",sort = 2)
    @TableField(exist = false)
    private String companyName;

    /** 报销部门sid */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "报销部门sid")
    private Long departmentSid;

    @TableField(exist = false)
    private List<Long> departmentSidList;

    @TableField(exist = false)
    @Excel(name = "报销部门",sort = 3)
    private String departmentName;

    /** 报销人（用户账号） */
    @ApiModelProperty(value = "报销人（用户账号）")
    private String reimburser;

    @TableField(exist = false)
    private List<String> reimburserList;

    @TableField(exist = false)
    @Excel(name = "报销人",sort = 4)
    private String reimburserName;

    @ApiModelProperty(value = "当前审批节点名称")
    @TableField(exist = false)
    @Excel(name="当前审批节点名称",sort = 6)
    private String approvalNode;

    @ApiModelProperty(value = "当前审批人")
    @TableField(exist = false)
    @Excel(name="当前审批人",sort = 7)
    private String approvalUserName;

    @TableField(exist = false)
    private String approvalUserId;

    /** 币种（数据字典的键值或配置档案的编码） */
    @ApiModelProperty(value = "币种（数据字典的键值或配置档案的编码）")
    private String currency;

    /** 货币单位（数据字典的键值或配置档案的编码） */
    @ApiModelProperty(value = "货币单位（数据字典的键值或配置档案的编码）")
    private String currencyUnit;

    /** 处理状态（数据字典的键值或配置档案的编码） */
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "处理状态", dictType = "s_handle_status" ,sort = 5)
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    @ApiModelProperty(value = "处理状态（数据字典 多选）")
    @TableField(exist = false)
    private String[] handleStatusList;

    @ApiModelProperty(value ="备注")
    @Excel(name="备注",sort = 8)
    private String remark;

    /** 创建人账号（用户账号） */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户账号）")
    private String creatorAccount;

    @TableField(exist = false)
    @Excel(name="创建人",sort = 9)
    private String creatorAccountName;

    /** 创建时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd",sort = 10)
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /** 更新人账号（用户账号） */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户账号）")
    private String updaterAccount;

    @TableField(exist = false)
    @Excel(name= "更改人",sort = 11)
    private String updaterAccountName;

    /** 更新时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更改日期", width = 30, dateFormat = "yyyy-MM-dd",sort = 12)
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /** 确认人账号（用户账号） */
    @ApiModelProperty(value = "确认人账号（用户账号）")
    private String confirmerAccount;

    @TableField(exist = false)
    private String confirmerAccountName;

    /** 确认时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /** 数据源系统（数据字典的键值或配置档案的编码） */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    @TableField(exist = false)
    private String hint;

    @TableField(exist = false)
    private String flag;

    @TableField(exist = false)
    @ApiModelProperty(value = "明细LIST")
    private List<SamOsbSampleReimburseItem> itemList;

    @TableField(exist = false)
    @ApiModelProperty(value = "附件LIST")
    private List<SamOsbSampleReimburseAttach> attachList;

}
