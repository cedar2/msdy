package com.platform.ems.domain.dto.response.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 生产进度跟踪报表（事项）
 *
 * @author chenkw
 * @date 2023-02-22
 */
@Data
@Accessors(chain = true)
@ApiModel
public class ManManuOrderConcernTracking extends EmsBaseEntity {

    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    //    @Excel(name = "预警", readConverterExp = "0=已逾期")
    @ApiModelProperty(value = "警示灯 0 红灯，-1 不显示")
    private String light;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "计划完成日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划完成日期")
    private Date planEndDate;

    @ApiModelProperty(value = "计划完成日期(起)")
    private String planEndDateBegin;

    @ApiModelProperty(value = "计划完成日期(止)")
    private String planEndDateEnd;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "实际开始日期")
    private Date actualStartDate;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "实际完成日期")
    private Date actualEndDate;

    @Excel(name = "事项名称")
    @ApiModelProperty(value = "关注事项名称")
    private String concernTaskName;

    @Excel(name = "完成状态(事项)", dictType = "s_end_status")
    @ApiModelProperty(value = "负责人姓名(员工档案)")
    private String endStatus;

    @ApiModelProperty(value = "完成状态")
    private String[] endStatusList;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "负责人sid(员工档案)")
    private Long handlerSid;

    @ApiModelProperty(value = "负责人sid(员工档案)")
    private Long[] handlerSidList;

    @ApiModelProperty(value = "负责人编号(员工档案)")
    private String handlerCode;

    @Excel(name = "负责人")
    @ApiModelProperty(value = "负责人姓名(员工档案)")
    private String handlerName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "计划开始日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划开始日期")
    private Date planStartDate;

    @ApiModelProperty(value = "合同交期")
    private String contractDate;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "物料/商品sid")
    private Long materialSid;

    @Excel(name = "商品编码(款号)")
    @ApiModelProperty(value = "物料/商品编码")
    private String materialCode;

    @Excel(name = "商品名称")
    @ApiModelProperty(value = "物料/商品名称")
    private String materialName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "SKUsid")
    private Long skuSid;

    @ApiModelProperty(value = "SKU编码")
    private String skuCode;

    @ApiModelProperty(value = "SKU名称")
    private String skuName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划完工日期")
    private Date orderPlanEndDate;

    @ApiModelProperty(value = "图片路径")
    private String picturePath;

    @ApiModelProperty(value = "图片路径，可放多个链接，每个链接用”;“隔开")
    private String[] picturePathList;

    @ApiModelProperty(value = "视频路径")
    private String videoPath;

    @ApiModelProperty(value = "视频路径，可放多个链接，每个链接用”;“隔开")
    private String[] videoPathList;

    @ApiModelProperty(value = "即将到期预警天数(生产订单-事项) ")
    private Integer toexpireDaysScddSx;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "工厂(整单)")
    private Long plantSid;

    @ApiModelProperty(value = "工厂(整单)")
    private Long[] plantSidList;

    @ApiModelProperty(value = "工厂名称(整单)")
    private String plantName;

    @Excel(name = "工厂")
    @ApiModelProperty(value = "工厂(整单)(简称)")
    private String plantShortName;

    @Excel(name = "排产批次号")
    @ApiModelProperty(value = "排产批次号")
    private String paichanBatch;

    @Excel(name = "生产订单号")
    @ApiModelProperty(value = "生产订单号")
    private String manufactureOrderCode;

    @ApiModelProperty(value = "实裁量")
    private BigDecimal isCaichuangQuantity;

    @ApiModelProperty(value = "计划产量生产订单")
    private BigDecimal quantity;

    @ApiModelProperty(value = "计划完成量")
    private BigDecimal planQuantity;

    @ApiModelProperty(value = "实际完成量）")
    private BigDecimal actualQuantity;

    @ApiModelProperty(value = "进度说明")
    private String handleComment;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "生产订单关注事项sid")
    private String manufactureOrderConcernTaskSid;

    @ApiModelProperty(value = "生产订单关注事项sid")
    private Long[] manufactureOrderConcernTaskSidList;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "生产订单sid")
    private Long manufactureOrderSid;

    @ApiModelProperty(value = "生产订单sid")
    private Long[] manufactureOrderSidList;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "关注事项sid")
    private Long concernTaskSid;

    @ApiModelProperty(value = "关注事项sid")
    private Long[] concernTaskSidList;

    @ApiModelProperty(value = "完工状态")
    private String completeStatus;

    @ApiModelProperty(value = "完工状态（多选）")
    private String[] completeStatusList;

    @ApiModelProperty(value = "关注事项类型")
    private String concernTaskType;

    @ApiModelProperty(value = "所属生产阶段")
    private String produceStage;

    @ApiModelProperty(value = "所属生产阶段")
    private String produceStageName;

}
