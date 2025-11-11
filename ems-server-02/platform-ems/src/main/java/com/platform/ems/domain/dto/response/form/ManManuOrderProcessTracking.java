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
import java.text.DecimalFormat;
import java.util.Date;

/**
 * 生产进度跟踪报表（工序）
 *
 * @author chenkw
 * @date 2023-02-22
 */
@Data
@Accessors(chain = true)
@ApiModel
public class ManManuOrderProcessTracking extends EmsBaseEntity {

    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    //    @Excel(name = "预警", readConverterExp = "0=已逾期")
    @ApiModelProperty(value = "警示灯 0 红灯，-1 不显示")
    private String light;

    @ApiModelProperty(value = "即将到期预警天数(生产订单-工序) ")
    private Integer toexpireDaysScddGx;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划开始日期")
    private Date planStartDate;

    @ApiModelProperty(value = "特殊工序标识")
    private String specialFlag;

    @ApiModelProperty(value = "是否第一个工序（数据字典的键值或配置档案的编码）")
    private String isFirstProcess;

    @ApiModelProperty(value = "是否标志阶段完成的工序（数据字典的键值或配置档案的编码）")
    private String isStageComplete;

    @ApiModelProperty(value = "是否标志成品完工的工序（数据字典的键值或配置档案的编码）")
    private String isProduceComplete;

    @ApiModelProperty(value = "序号")
    private String serialNum;

    public void setSerialNum(String serialNum) {
        DecimalFormat df = new DecimalFormat("####.###");
        try {
            serialNum = df.format(new BigDecimal(serialNum));
        }catch (Exception e) {
            serialNum = null;
        }
        this.serialNum = serialNum;
    }

    public String getSerialNum() {
        DecimalFormat df = new DecimalFormat("####.###");
        BigDecimal serial = null;
        try {
            serial = new BigDecimal(serialNum);
        } catch (Exception e) {
            return null;
        }
        return df.format(serial);
    }

    @ApiModelProperty(value = "进度说明")
    private String comment;

    @ApiModelProperty(value = "计划产量（整单）")
    private BigDecimal planTotalQuantity;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "计划完成日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划完成日期")
    private Date planEndDate;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划完工日期")
    private Date planEndDateHead;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "实际开始日期")
    private Date actualStartDate;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "实际完成日期")
    private Date actualEndDate;

    @Excel(name = "商品编码(款号)")
    @ApiModelProperty(value = "物料/商品编码")
    private String materialCode;

    @Excel(name = "商品名称")
    @ApiModelProperty(value = "物料/商品名称")
    private String materialName;

    @ApiModelProperty(value = "系统自增长ID-sku")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long skuSid;

    @ApiModelProperty(value = "sku编码生产订单")
    private String skuCode;

    @ApiModelProperty(value = "sku名称生产订单")
    private String skuName;

    @Excel(name = "工序")
    @ApiModelProperty(value = "工序名称")
    private String processName;

    @ApiModelProperty(value = "合同交期订单")
    private String contractDate;

    @Excel(name = "完成状态(工序)", dictType = "s_end_status")
    @ApiModelProperty(value = "完成状态（工序）")
    private String endStatus;

    @ApiModelProperty(value = "完成状态（工序）")
    private String[] endStatusList;

    @Excel(name = "负责人")
    @ApiModelProperty(value = "责任人名称")
    private String directorName;

    @Excel(name = "待完成量(计划)", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "待完成量(计划)：计划产量-已完成量")
    private BigDecimal daiQuantity;

    @Excel(name = "待完成量(实裁)", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "待完成量(实裁)：实裁量 - 已完成量(工序)")
    private BigDecimal daiShicaiQuantity;

    @Excel(name = "已完成量(工序)", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "【报表中心】生产订单工序明细报表：已完成量(工序)")
    private BigDecimal totalCompleteQuantity;

    @Excel(name = "计划产量(工序)", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "产量（此工厂/此工作中心/班组/此道工序负责生产的产量）")
    private BigDecimal quantity;

    @Excel(name = "实裁量", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "实裁量")
    private BigDecimal isCaichuangQuantity;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-工厂list")
    private Long plantSid;

    @ApiModelProperty(value = "系统自增长ID-工厂list")
    private Long[] plantSidList;

    @ApiModelProperty(value = "工厂编码(工序)")
    private String plantCode;

    @ApiModelProperty(value = "工厂(工序)")
    private String plantName;

    @Excel(name = "工厂(工序)")
    @ApiModelProperty(value = "工厂简称(工序)")
    private String plantShortName;

    @Excel(name = "排产批次号")
    @ApiModelProperty(value = "排产批次号")
    private Long paichanBatch;

    @ApiModelProperty(value = "系统自增长ID-生产订单")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long manufactureOrderSid;

    @ApiModelProperty(value = "系统自增长ID-生产订单")
    private Long[] manufactureOrderSidList;

    @Excel(name = "生产订单号")
    @ApiModelProperty(value = "生产订单号")
    private String manufactureOrderCode;

    @ApiModelProperty(value = "系统自增长ID-生产订单工序")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long manufactureOrderProcessSid;

    @ApiModelProperty(value = "系统自增长ID-生产订单工序")
    private Long[] manufactureOrderProcessSidList;

    @ApiModelProperty(value = "系统自增长ID-工厂(整单)")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long orderPlantSid;

    @ApiModelProperty(value = "系统自增长ID-工厂(整单)")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long[] orderPlantSidList;

    @ApiModelProperty(value = "工厂编码(整单)")
    private String orderPlantCode;

    @ApiModelProperty(value = "工厂简称(整单)")
    private String orderPlantName;

    @ApiModelProperty(value = "工厂简称(整单)")
    private String orderPlantShortName;

    @ApiModelProperty(value = "系统自增长ID-工序")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long processSid;

    @ApiModelProperty(value = "系统自增长ID-工序list")
    private Long[] processSidList;

    @ApiModelProperty(value = "责任人sid(员工档案)")
    private String directorSid;

    @ApiModelProperty(value = "责任人sid(员工档案)")
    private String[] directorSidList;

    @ApiModelProperty(value = "责任人编码(员工档案)")
    private String directorCode;

    @ApiModelProperty(value = "计划完成日期启")
    private String planEndDateBegin;

    @ApiModelProperty(value = "计划完成日期止")
    private String planEndDateEnd;

    @ApiModelProperty(value = "完工状态（订单）")
    private String completeStatus;

    @ApiModelProperty(value = "完工状态（订单）")
    private String[] completeStatusList;

    @ApiModelProperty(value = "图片路径")
    private String picturePath;

    @ApiModelProperty(value = "图片路径，可放多个链接，每个链接用”;“隔开")
    private String[] picturePathList;

    @ApiModelProperty(value = "视频路径")
    private String videoPath;

    @ApiModelProperty(value = "视频路径，可放多个链接，每个链接用”;“隔开")
    private String[] videoPathList;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "操作部门SID")
    private Long departmentSid;

    @ApiModelProperty(value = "操作部门编码")
    private String departmentCode;

    @ApiModelProperty(value = "操作部门名称")
    private String departmentName;

    @ApiModelProperty(value = "里程碑（数据字典的键值或配置档案的编码）")
    private String milestone;

    @ApiModelProperty(value = "系统自增长ID-工作中心/班组")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long workCenterSid;

    @ApiModelProperty(value = "系统自增长ID-工作中心/班组list")
    private Long[] workCenterSidList;

    @ApiModelProperty(value = "工作中心/班组编码")
    private String workCenterCode;

    //    @Excel(name = "班组")
    @ApiModelProperty(value = "工作中心/班组名称")
    private String workCenterName;
}
