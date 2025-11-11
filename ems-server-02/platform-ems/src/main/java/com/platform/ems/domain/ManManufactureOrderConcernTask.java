package com.platform.ems.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableId;

import com.platform.common.core.domain.EmsBaseEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import lombok.experimental.Accessors;

import javax.validation.Valid;
import javax.validation.constraints.Digits;

/**
 * 生产订单-关注事项对象 s_man_manufacture_order_concern_task
 *
 * @author chenkw
 * @date 2022-08-02
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_man_manufacture_order_concern_task")
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class ManManufactureOrderConcernTask extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-生产订单关注事项
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-生产订单关注事项")
    private Long manufactureOrderConcernTaskSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "sid数组")
    private Long[] manufactureOrderConcernTaskSidList;

    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "计划完工日期(整单)", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划完工日期(整单)")
    private Date orderPlanEndDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "合同交期")
    private String contractDate;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "工厂(整单)")
    private Long plantSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "工厂(整单)")
    private Long[] plantSidList;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "工厂(整单)")
    private Long orderPlantSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "工厂名称(整单)")
    private String orderPlantName;

    @TableField(exist = false)
    @Excel(name = "工厂(整单)")
    @ApiModelProperty(value = "工厂(整单)(简称)")
    private String orderPlantShortName;

    @TableField(exist = false)
    @ApiModelProperty(value = "工厂(整单)(多选)")
    private Long[] orderPlantSidList;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "商品sid")
    private Long materialSid;

    @Excel(name = "商品编码")
    @TableField(exist = false)
    @ApiModelProperty(value = "商品编码")
    private String materialCode;

    @Excel(name = "商品名称")
    @TableField(exist = false)
    @ApiModelProperty(value = "商品名称")
    private String materialName;

    @Excel(name = "排产批次号")
    @TableField(exist = false)
    @ApiModelProperty(value = "排产批次号")
    private String paichanBatch;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-生产订单sku_sid")
    private Long skuSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "生产订单sku")
    private String skuName;

    @TableField(exist = false)
    @Excel(name = "颜色")
    @ApiModelProperty(value = "sku1名称")
    private String sku1Name;

    @Excel(name = "完工状态", dictType = "s_complete_status")
    @TableField(exist = false)
    @ApiModelProperty(value = "完工状态")
    private String completeStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "完工状态（多选）")
    private String[] completeStatusList;

    /**
     * 关注事项sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "关注事项sid")
    private Long concernTaskSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "关注事项sid")
    private Long[] concernTaskSidList;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-关注事项组")
    private Long concernTaskGroupSid;

    @Excel(name = "关注事项名称")
    @TableField(exist = false)
    @ApiModelProperty(value = "关注事项名称")
    private String concernTaskName;

    @TableField(exist = false)
    @ApiModelProperty(value = "图片是否必传")
    private String isPictureUpload;

    @TableField(exist = false)
    @ApiModelProperty(value = "附件是否必传")
    private String isAttachUpload;

    @TableField(exist = false)
    @ApiModelProperty(value = "序号")
    private Long sort;

    @TableField(exist = false)
    @ApiModelProperty(value = "关注事项类型")
    private String[] concernTaskTypeList;

    /**
     * 负责人sid(员工档案)
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "负责人sid(员工档案)")
    private Long handlerSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "负责人sid(员工档案)")
    private Long[] handlerSidList;

    /**
     * 负责人编号(员工档案)
     */
    @ApiModelProperty(value = "负责人编号(员工档案)")
    private String handlerCode;

    @Excel(name = "事项负责人")
    @TableField(exist = false)
    @ApiModelProperty(value = "负责人姓名(员工档案)")
    private String handlerName;

    /**
     * 完成状态
     */
    @Excel(name = "完成状态(事项)", dictType = "s_end_status")
    @ApiModelProperty(value = "完成状态")
    private String endStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "完成状态")
    private String[] endStatusList;

    @TableField(exist = false)
    @ApiModelProperty(value = "完成状态")
    private String[] endList;

    @TableField(exist = false)
    @ApiModelProperty(value = "所属生产阶段")
    private String produceStage;

    @TableField(exist = false)
    @Excel(name = "所属生产阶段")
    @ApiModelProperty(value = "所属生产阶段")
    private String produceStageName;

    @TableField(exist = false)
    @ApiModelProperty(value = "所属生产阶段（多选）")
    private String[] produceStageList;

    /**
     * 计划开始日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "计划开始日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划开始日期")
    private Date planStartDate;

    /**
     * 计划完成日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "计划完成日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划完成日期")
    private Date planEndDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "计划完成日期(起)")
    private String planEndDateBegin;

    @TableField(exist = false)
    @ApiModelProperty(value = "计划完成日期(止)")
    private String planEndDateEnd;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "初始计划完成日期")
    private Date initialPlanEndDate;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "实际开始日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "实际开始日期")
    private Date actualStartDate;

    /**
     * 实际完成日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "实际完成日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "实际完成日期")
    private Date actualEndDate;

    /**
     * 进展说明
     */
    @Excel(name = "进度说明")
    @ApiModelProperty(value = "进度说明")
    private String handleComment;

    @TableField(exist = false)
    @ApiModelProperty(value = "计划产量生产订单")
    private BigDecimal quantity;

    @Excel(name = "计划完成量", scale = 0)
    @Digits(integer = 8, fraction = 3, message = "计划完成量整数位上限为8位，小数位上限为3位")
    @ApiModelProperty(value = "计划完成量")
    private BigDecimal planQuantity;

    @Excel(name = "实际完成量", scale = 0)
    @Digits(integer = 8, fraction = 3, message = "实际完成量整数位上限为8位，小数位上限为3位")
    @ApiModelProperty(value = "实际完成量）")
    private BigDecimal actualQuantity;

    @Excel(name = "事项类型", dictType = "s_concern_task_type")
    @TableField(exist = false)
    @ApiModelProperty(value = "关注事项类型")
    private String concernTaskType;

    @Excel(name = "事项编码")
    @ApiModelProperty(value = "关注事项编码")
    private String concernTaskCode;

    @Excel(name = "到期提醒天数")
    @ApiModelProperty(value = "即将到期预警天数(生产订单-事项) ")
    private Integer toexpireDaysScddSx;

    @TableField(exist = false)
    @ApiModelProperty(value = "即将到期预警天数(生产订单-事项) ")
    private Integer toexpireDaysScdd;

    @Excel(name = "里程碑", dictType = "s_manufacture_milestone")
    @ApiModelProperty(value = "里程碑（数据字典的键值或配置档案的编码）")
    private String milestone;

    @Excel(name = "序号")
    @ApiModelProperty(value = "序号")
    private BigDecimal serialNum;

    @ApiModelProperty(value = "图片路径")
    private String picturePath;

    @TableField(exist = false)
    @ApiModelProperty(value = "图片路径，可放多个链接，每个链接用”;“隔开")
    private String[] picturePathList;

    @ApiModelProperty(value = "视频路径")
    private String videoPath;

    @TableField(exist = false)
    @ApiModelProperty(value = "视频路径，可放多个链接，每个链接用”;“隔开")
    private String[] videoPathList;

    /**
     * 创建人账号（用户账号）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户账号）")
    private String creatorAccount;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人昵称")
    @TableField(exist = false)
    private String creatorAccountName;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @Excel(name = "生产订单号")
    @ApiModelProperty(value = "生产订单号")
    private String manufactureOrderCode;

    @TableField(exist = false)
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态")
    private String[] handleStatusList;

    /**
     * 更新人账号（用户账号）
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户账号）")
    private String updaterAccount;

    @ApiModelProperty(value = "更改人昵称")
    @TableField(exist = false)
    private String updaterAccountName;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 系统SID-生产订单
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-生产订单")
    private Long manufactureOrderSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-生产订单")
    private Long[] manufactureOrderSidList;

    @ApiModelProperty(value = "生产订单号")
    @TableField(exist = false)
    private Set<Long> manufactureOrderCodeSet;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划投产日期")
    @TableField(exist = false)
    private Date planStartDateHead;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划完工日期")
    @TableField(exist = false)
    private Date planEndDateHead;

    /**
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    @TableField(exist = false)
    @ApiModelProperty(value = "实裁量")
    private BigDecimal isCaichuangQuantity;

    @TableField(exist = false)
    @ApiModelProperty(value = "预警：红色0绿色1黄橙2蓝色3灰色未开始4灰色暂搁5灰色取消6")
    private String light;

    @Valid
    @TableField(exist = false)
    @ApiModelProperty(value = "生产订单关注事项-附件")
    private List<ManManufactureOrderConcernTaskAttach> attachmentList;

}

