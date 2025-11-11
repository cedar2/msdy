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

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.List;

/**
 * 生产进度日报对象 s_man_day_manufacture_progress
 *
 * @author linhongwei
 * @date 2021-06-09
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_man_day_manufacture_progress")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ManDayManufactureProgress extends EmsBaseEntity {

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-生产进度日报单")
    private Long dayManufactureProgressSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] dayManufactureProgressSidList;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "汇报日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "单据日期/汇报日期")
    private Date documentDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "汇报日期开始")
    private String documentDateStart;

    @TableField(exist = false)
    @ApiModelProperty(value = "汇报日期结束")
    private String documentDateEnd;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "工厂sid")
    private Long plantSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "工厂数组")
    private Long[] plantSidList;

    @ApiModelProperty(value = "工厂编码")
    private String plantCode;

    @TableField(exist = false)
    @Excel(name = "工厂(工序)")
    @ApiModelProperty(value = "工厂名称")
    private String plantName;

    @TableField(exist = false)
    @ApiModelProperty(value = "工厂简称")
    private String shortName;

    @TableField(exist = false)
    @ApiModelProperty(value = "工厂简称")
    private String plantShortName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "操作部门")
    private Long departmentSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "操作部门")
    private Long[] departmentSidList;

    @ApiModelProperty(value = "操作部门编码")
    private String departmentCode;

    @Excel(name = "操作部门")
    @TableField(exist = false)
    @ApiModelProperty(value = "操作部门名称")
    private String departmentName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "工作中心/班组sid")
    private Long workCenterSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "工作中心/班组数组")
    private Long[] workCenterSidList;

    @ApiModelProperty(value = "工作中心/班组编码")
    private String workCenterCode;

    @TableField(exist = false)
    @Excel(name = "班组")
    @ApiModelProperty(value = "工作中心/班组名称")
    private String workCenterName;

    /**
     * s_ progress_enter_mode   键值：GC（按工厂）、 CZBM（按操作部门）、BZ（按班组）
     */
    @Excel(name = "录入方式", dictType = "s_progress_enter_mode")
    @ApiModelProperty(value = "录入方式（数据字典的键值或配置档案的编码）")
    private String enterMode;

    @TableField(exist = false)
    @ApiModelProperty(value = "录入方式（数据字典的键值或配置档案的编码）")
    private String[] enterModeList;

    @ApiModelProperty(value = "汇报人账号（用户名称）")
    private String reporter;

    @TableField(exist = false)
    @ApiModelProperty(value = "汇报人数组")
    private String[] reporterList;

    @TableField(exist = false)
    @Excel(name = "汇报人")
    @ApiModelProperty(value = "汇报人名称")
    private String reporterName;

    @TableField(exist = false)
    @ApiModelProperty(value = "操作部门")
    private String department;

    @ApiModelProperty(value = "所属年月")
    private String yearmonth;

    @ApiModelProperty(value = "单据类型编码code")
    private String documentType;

    @ApiModelProperty(value = "业务类型编码code")
    private String businessType;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-公司档案")
    private Long companySid;

    @Excel(name = "备注")
    @ApiModelProperty(value ="备注")
    private String remark;

    @Excel(name = "班组生产日报编号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "生产进度日报单号")
    private Long dayManufactureProgressCode;

    @NotEmpty(message = "处理状态不能为空")
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    @TableField(exist = false)
    private String[] handleStatusList;

    @Excel(name = "当前审批节点")
    @ApiModelProperty(value = "当前审批节点名称")
    @TableField(exist = false)
    private String approvalNode;

    @Excel(name = "当前审批人")
    @ApiModelProperty(value = "当前审批人")
    @TableField(exist = false)
    private String approvalUserName;

    @ApiModelProperty(value = "提交人")
    @TableField(exist = false)
    private String submitUserName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "提交日期")
    @TableField(exist = false)
    private Date submitDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "当前审批人id")
    private String approvalUserId;

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    @TableField(exist = false)
    @ApiModelProperty(value = "创建人数组")
    private String[] creatorAccountList;

    @TableField(exist = false)
    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人名称")
    private String creatorAccountName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

    @TableField(exist = false)
    @Excel(name = "更改人")
    @ApiModelProperty(value = "更新人名称")
    private String updaterAccountName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更改日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    @ApiModelProperty(value = "确认人账号（用户名称）")
    private String confirmerAccount;

    @TableField(exist = false)
    @ApiModelProperty(value = "确认人名称")
    private String confirmerAccountName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    @TableField(exist = false)
    private String nickName;

    @TableField(exist = false)
    @ApiModelProperty(value = "最大行号")
    private Long maxItemNum;

    // ======班组日出勤信息对象=========//

    @TableField(exist = false)
    @ApiModelProperty(value = "工作班次（数据字典的键值或配置档案的编码）")
    private String workShift;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "班组总人数/应出勤(人数)")
    private Long yingcq;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "出勤(人数)/实出勤(人数)")
    private Long shicq;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "请假(人数)")
    private Long qingj;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "待料(人数)")
    private Long dail;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "旷工(人数)")
    private Long kuangg;

    @TableField(exist = false)
    @ApiModelProperty(value = "出勤备注")
    private String workattendRemark;

    /**
     * 生产进度日报sids
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "生产进度日报sids")
    private List<Long> dayManufactureProgressSids;

    /**
     * 生产进度日报-明细对象
     */
    @Valid
    @TableField(exist = false)
    @ApiModelProperty(value = "生产进度日报-明细对象")
    private List<ManDayManufactureProgressItem> dayManufactureProgressItemList;

    @TableField(exist = false)
    @ApiModelProperty(value = "是否跳过 当天完成量/发料量/收料量、当天接收量(上一工序)”至少要有一个值大于0 校验")
    private String continueAtleast;

    /**
     * 生产进度日报-附件对象
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "生产进度日报-附件对象")
    private List<ManDayManufactureProgressAttach> attachmentList;

    /**
     * 生产进度日报-款生产进度对象对象
     */
    @Valid
    @TableField(exist = false)
    @ApiModelProperty(value = "生产进度日报-款生产进度对象对象")
    private List<ManDayManufactureKuanProgress> kuanProcessList;

}
