package com.platform.ems.domain.dto.request.form;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Straw
 * @date 2022/12/20
 */
@SuppressWarnings("unused")
@Data
public class PrjTaskTemplateFormRequest {

    /**
     * 租户ID
     */
    @ApiModelProperty(value = "租户ID")
    String clientId;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-项目任务模板")
    private Long taskTemplateSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-项目任务模板-任务明细")
    private Long taskTemplateItemSid;

    @ApiModelProperty(value = "系统SID-项目任务模板-任务明细")
    private Long[] taskTemplateItemSidList;

    /**
     * 项目任务模板名称
     */
    @ApiModelProperty(value = "项目任务模板名称")
    String taskTemplateName;

    /**
     * 任务节点名称
     */
    @ApiModelProperty(value = "任务节点名称")
    String taskName;

    /**
     * 所属任务阶段（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "所属任务阶段（数据字典的键值或配置档案的编码）")
    String taskPhase;

    /**
     * 所属任务阶段（多选）
     */
    @ApiModelProperty(value = "所属任务阶段（多选）")
    String[] taskPhaseList;

    /**
     * 关联业务单据单号
     */
    @ApiModelProperty(value = "关联业务单据单号")
    String relateBusinessFormCode;

    /**
     * 关联业务单据单号（多选）
     */
    @ApiModelProperty(value = "关联业务单据单号（多选）")
    String[] relateBusinessFormCodeList;


    /**
     * 项目类型（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "项目类型（数据字典的键值或配置档案的编码）")
    String projectType;

    /**
     * 项目类型（多选）
     */
    @ApiModelProperty(value = "项目类型（多选）")
    String[] projectTypeList;

    /**
     * 处理状态（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    String handleStatus;

    /**
     * 处理状态（多选）
     */
    @ApiModelProperty(value = "处理状态（多选）")
    String[] handleStatusList;

    /**
     * 启用/停用状态（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "启用/停用状态（数据字典的键值或配置档案的编码）")
    String status;

    @ApiModelProperty(value = "处理人（用户账号）")
    private String handlerTask;

    @ApiModelProperty(value = "处理人（是否为空）")
    private String handlerTaskIsNull;

    @ApiModelProperty(value = "处理人（用户账号多选）")
    private String[] handlerTaskList;

    @ApiModelProperty(value ="每页个数")
    private Integer pageNum;

    @ApiModelProperty(value ="每页个数")
    private Integer pageSize;

    @ApiModelProperty(value ="分页起始数")
    private Integer pageBegin;

}
