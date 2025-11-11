package com.platform.ems.domain.dto.response;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 生产周计划-明细对象 s_man_week_manufacture_plan_item
 *
 * @author hjj
 * @date 2021-07-16
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ManWeekManufacturePlanItemResponse extends EmsBaseEntity implements Serializable {

    /**
     * 系统SID-生产周计划单明细
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-生产周计划单明细")
    private Long weekManufacturePlanItemSid;

    /**
     * 系统SID-生产周计划单
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-生产周计划单")
    private Long weekManufacturePlanSid;

    /**
     * 工厂名称
     */
    @ApiModelProperty(value = "工厂名称")
    private String plantName;

    /**
     * 工作中心名称
     */
    @ApiModelProperty(value = "工作中心名称")
    private String workCenterName;

    /**
     * 周计划日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "周计划日期")
    private Date documentDate;

    /**
     * 生产订单号
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "生产订单号")
    private Long manufactureOrderCode;

    /**
     * 商品编码
     */
    @ApiModelProperty(value = "商品编码")
    private String materialCode;

    /**
     * 商品名称
     */
    @ApiModelProperty(value = "商品名称")
    private String materialName;

    /**
     * 工序名称
     */
    @ApiModelProperty(value = "工序名称")
    private String processName;

    /**
     * 生产批次号
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "生产批次号")
    private Long productionBatchNum;

    /**
     * 本周计划完成量
     */
    @ApiModelProperty(value = "本周计划完成量")
    private BigDecimal planQuantity;

    /**
     * 本周计划处理次品量
     */
    @ApiModelProperty(value = "本周计划处理次品量")
    private BigDecimal planDefectiveQuantity;

    /**
     * 计划产量
     */
    @ApiModelProperty(value = "计划产量")
    private BigDecimal quantity;

    /**
     * 已完成量
     */
    @ApiModelProperty(value = "已完成量")
    private BigDecimal currentCompleteQuantity;

    /**
     * 基本计量单位
     */
    @ApiModelProperty(value = "基本计量单位")
    private String unitBaseName;

    /**
     * 计划完成日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划完成日期")
    private Date planEndDate;

    /**
     * 完成状态
     */
    @ApiModelProperty(value = "完成状态")
    private String endStatus;

    /**
     * 行号
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "行号")
    private Long itemNum;

    /** 工序序号 */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "工序序号")
    private Long serialNum;

    /**
     * 创建人账号（用户名称）
     */
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccountName;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @TableField(exist = false)
    @ApiModelProperty(value = "周计划日期(起)")
    private Date dateStart;

    /**
     * 周计划日期(至)
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @TableField(exist = false)
    @ApiModelProperty(value = "周计划日期(至)")
    private Date dateEnd;

}
