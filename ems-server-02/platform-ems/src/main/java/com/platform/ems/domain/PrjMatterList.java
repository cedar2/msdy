package com.platform.ems.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * 事项清单对象 s_prj_matter_list
 *
 * @author platform
 * @date 2023-11-20
 */
@Data
@Accessors(chain = true)
@TableName(value = "s_prj_matter_list")
public class PrjMatterList extends EmsBaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-事项清单
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-事项清单")
    private Long matterListSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "sid数组")
    private Long[] matterListSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "项目类型数组")
    private String[] projectTypeList;

    /**
     * 预警灯
     */
    @TableField(exist = false)
    @Excel(name = "预警", dictType = "s_early_warning")
    @ApiModelProperty(value = "预警灯")
    private String light;

    /**
     * 事项/任务名称
     */
    @Excel(name = "事项名称")
    @ApiModelProperty(value = "事项/任务名称")
    private String matterName;

    /**
     * 项目编号
     */
    @Excel(name = "项目编号")
    @ApiModelProperty(value = "项目编号")
    private String projectCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @Excel(name = "门店编码")
    @ApiModelProperty(value = "门店编码")
    private String storeCode;

    @TableField(exist = false)
    @Excel(name = "门店名称")
    @ApiModelProperty(value = "门店名称")
    private String storeName;

    /**
     * 事项状态（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "事项状态", dictType = "s_project_task_status")
    @ApiModelProperty(value = "事项状态")
    private String matterStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "事项状态")
    private String[] matterStatusList;

    /**
     * 事项主管（用户账号）
     */
    @ApiModelProperty(value = "事项主管")
    private String matterManager;

    @TableField(exist = false)
    @ApiModelProperty(value = "事项主管是否为空")
    private String matterManagerIsNull;

    @TableField(exist = false)
    @ApiModelProperty(value = "事项主管")
    private String[] matterManagerList;

    @TableField(exist = false)
    @Excel(name = "事项主管")
    @ApiModelProperty(value = "事项主管")
    private String matterManagerName;

    @TableField(exist = false)
    @ApiModelProperty(value = "事项主管")
    private Long matterManagerId;

    /**
     * 岗位类型(事项处理人)（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "岗位类型(事项处理人)", dictType = "s_position_type")
    @ApiModelProperty(value = "岗位类型(事项处理人)")
    private String positionTypeMatterHandler;

    @TableField(exist = false)
    @ApiModelProperty(value = "岗位类型(事项处理人)")
    private String[] positionTypeMatterHandlerList;

    /**
     * 事项处理人是否为空
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "事项处理人是否为空")
    private String matterHandlerIsNull;

    /**
     * 事项处理人（用户账号）
     */
    @ApiModelProperty(value = "事项处理人")
    private String matterHandler;

    @TableField(exist = false)
    @ApiModelProperty(value = "事项处理人")
    private Long matterHandlerId;

    @TableField(exist = false)
    @ApiModelProperty(value = "事项处理人")
    private String[] matterHandlerList;

    @TableField(exist = false)
    @Excel(name = "事项处理人")
    @ApiModelProperty(value = "事项处理人")
    private String matterHandlerName;

    /**
     * 告知人(事项)（用户账号），如包含多个，则用英文分号";"隔开
     */
    @ApiModelProperty(value = "告知人(事项)")
    private String personNoticeMatter;

    @TableField(exist = false)
    @Excel(name = "告知人(事项)")
    @ApiModelProperty(value = "告知人(事项)")
    private String personNoticeMatterName;

    @TableField(exist = false)
    @ApiModelProperty(value = "告知人(事项)")
    private String[] personNoticeMatterList;

    /**
     * 计划开始日期(事项)
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "计划开始日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划开始日期(事项)")
    private Date planStartDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "是否修改计划开始日期")
    private String planStartDateIsUpdate;

    @TableField(exist = false)
    @ApiModelProperty(value = "计划开始日期(事项)起")
    private String planStartDateBegin;

    @TableField(exist = false)
    @ApiModelProperty(value = "计划开始日期(事项)至")
    private String planStartDateEnd;

    /**
     * 计划完成日期(事项)
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "计划完成日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划完成日期(事项)")
    private Date planEndDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "是否修改计划完成日期")
    private String planEndDateIsUpdate;

    @TableField(exist = false)
    @ApiModelProperty(value = "计划完成日期(事项)起")
    private String planEndDateBegin;

    @TableField(exist = false)
    @ApiModelProperty(value = "计划完成日期(事项)至")
    private String planEndDateEnd;

    /**
     * 实际完成日期(事项)
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "实际完成日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "实际完成日期(事项)")
    private Date actualEndDate;

    /**
     * 待办提醒天数(事项)
     */
    @Excel(name = "待办提醒天数")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "待办提醒天数(事项)")
    private Integer todoDaysMatter;

    /**
     * 即将到期提醒天数(事项)
     */
    @Excel(name = "即将到期提醒天数")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "即将到期提醒天数(事项)")
    private Integer toexpireDaysMatter;

    /**
     * 进度说明
     */
    @Excel(name = "进度说明")
    @ApiModelProperty(value = "进度说明")
    private String progressDescription;

    /**
     * 优先级(事项)（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "优先级", dictType = "s_urgency_type")
    @ApiModelProperty(value = "优先级(事项)")
    private String priorityMatter;

    /**
     * 关联业务单据类别
     */
    @ApiModelProperty(value = "关联业务单据类别")
    private String referDocCategory;

    @TableField(exist = false)
    @Excel(name = "关联业务单据类别")
    @ApiModelProperty(value = "关联业务单据类别")
    private String referDocCategoryName;

    /**
     * 系统SID-关联业务单
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-关联业务单")
    private Long referDocumentSid;

    /**
     * 关联业务单号
     */
    @Excel(name = "关联业务单号")
    @ApiModelProperty(value = "关联业务单号")
    private String referDocumentCode;

    /**
     * 事项所属阶段（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "事项所属阶段", dictType = "s_task_phase")
    @ApiModelProperty(value = "事项所属阶段")
    private String matterPhase;

    /**
     * 事项所属业务（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "事项所属业务", dictType = "s_task_business")
    @ApiModelProperty(value = "事项所属业务")
    private String matterBusiness;

    /**
     * 图片是否必传（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "图片是否必传", dictType = "s_yes_no_flag")
    @ApiModelProperty(value = "图片是否必传")
    private String isPictureUpload;

    @ApiModelProperty(value = "图片路径")
    private String picturePath;

    @TableField(exist = false)
    @ApiModelProperty(value = "图片路径数组")
    private String[] picturePathList;

    @ApiModelProperty(value = "视频路径")
    private String videoPath;

    @TableField(exist = false)
    @ApiModelProperty(value = "视频路径数组")
    private String[] videoPathList;

    /**
     * 系统SID-项目档案
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-项目档案")
    private Long projectSid;

    @TableField(exist = false)
    @Excel(name = "项目类型", dictType = "s_project_type")
    @ApiModelProperty(value = "项目类型")
    private String projectType;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-门店")
    private Long storeSid;

    /**
     * 前置事项，如包含多个，则用英文分号";"隔开
     */
    @ApiModelProperty(value = "前置事项，如包含多个，则用英文分号隔开")
    private String preMatter;

    @TableField(exist = false)
    @ApiModelProperty(value = "前置事项，如包含多个，则用英文分号隔开")
    private String[] preMatterList;

    /**
     * 关注人（用户账号），如包含多个，则用英文分号";"隔开
     */
    @ApiModelProperty(value = "关注人")
    private String personAttent;

    @TableField(exist = false)
    @ApiModelProperty(value = "关注人")
    private String personAttentName;

    @TableField(exist = false)
    @ApiModelProperty(value = "关注人")
    private String[] personAttentList;

    /**
     * 创建人账号（用户账号）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    /**
     * 创建人
     */
    @TableField(exist = false)
    @Excel(name = "创建人账号")
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

    /**
     * 更新人账号（用户账号）
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccount;

    /**
     * 更改人
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "更改人昵称")
    private String updaterAccountName;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.UPDATE)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;

    /**
     * 附件对象
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "附件对象")
    private List<PrjMatterListAttach> attachmentList;

    @TableField(exist = false)
    @ApiModelProperty(value = "数据库表名-待办，如：s_pur_purchase_order")
    private String tableName;

    @TableField(exist = false)
    @ApiModelProperty(value = "类别code-待办")
    private String taskCategory;

    @TableField(exist = false)
    @ApiModelProperty(value = "业务类型-待办")
    private String businessType;

    @TableField(exist = false)
    @ApiModelProperty(value = "用户账号-待办")
    private String userName;

    @TableField(exist = false)
    @ApiModelProperty(value = "标题-待办任务用")
    private String title;

}
