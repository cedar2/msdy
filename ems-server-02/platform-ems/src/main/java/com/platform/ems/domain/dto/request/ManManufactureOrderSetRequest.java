package com.platform.ems.domain.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 生产订单查询页面设置基本信息/头缸信息/首批信息 ManManufactureOrderSetRequest
 *
 * @author chenkaiwen
 * @date 2022-08-03
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ManManufactureOrderSetRequest {

    @ApiModelProperty(value = "查询页面设置信息的类型：基本JB/头缸TG/首批SP")
    private String setType;

    @ApiModelProperty(value = "sid数组")
    private Long[] manufactureOrderSidList;

    // === 基本信息 === //

    @ApiModelProperty(value = "优先级/紧急程度")
    private String producePriorityIsUpd;

    @ApiModelProperty(value = "优先级/紧急程度（数据字典的键值或配置档案的编码）")
    private String producePriority;

    @ApiModelProperty(value = "计划开始日期")
    private String planStartDateIsUpd;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划开始日期")
    private Date planStartDate;

    @ApiModelProperty(value = "计划完成日期")
    private String planEndDateIsUpd;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划完成日期")
    private Date planEndDate;

    @ApiModelProperty(value = "跟进人是否更改")
    private String genjinrenSidIsUpd;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "跟进人sid(员工档案)")
    private Long genjinrenSid;

    // === 头缸信息 === //

    @ApiModelProperty(value = "是否做头缸")
    private String isProduceTgIsUpd;

    @ApiModelProperty(value = "是否做头缸（数据字典的键值或配置档案的编码）")
    private String isProduceTg;

    @ApiModelProperty(value = "完工状态(头缸)")
    private String completeStatusTgIsUpd;

    @ApiModelProperty(value = "完工状态(头缸)（数据字典的键值或配置档案的编码）")
    private String completeStatusTg;

    @ApiModelProperty(value = "计划完工日期(头缸)")
    private String planStartDateTgIsUpd;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划完工日期(头缸)")
    private Date planStartDateTg;

    @ApiModelProperty(value = "实际完工日期(头缸)")
    private String actualEndDateTgIsUpd;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "实际完工日期(头缸)")
    private Date actualEndDateTg;

    @ApiModelProperty(value = "计划产量(头缸)")
    private String planQuantityTgIsUpd;

    @ApiModelProperty(value = "计划产量(头缸)")
    private Integer planQuantityTg;

    @ApiModelProperty(value = "实际产量(头缸)")
    private String actualQuantityTgIsUpd;

    @ApiModelProperty(value = "实际产量(头缸)")
    private Integer actualQuantityTg;

    // === 首批信息 === //

    @ApiModelProperty(value = "是否做首批")
    private String isProduceSpIsUpd;

    @ApiModelProperty(value = "是否做首批（数据字典的键值或配置档案的编码）")
    private String isProduceSp;

    @ApiModelProperty(value = "完工状态(首批)")
    private String completeStatusSpIsUpd;

    @ApiModelProperty(value = "完工状态(首批)（数据字典的键值或配置档案的编码）")
    private String completeStatusSp;

    @ApiModelProperty(value = "计划完工日期(首批)")
    private String planStartDateSpIsUpd;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划完工日期(首批)")
    private Date planStartDateSp;

    @ApiModelProperty(value = "实际完工日期(首批)")
    private String actualEndDateSpIsUpd;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "实际完工日期(头缸)")
    private Date actualEndDateSp;

    @ApiModelProperty(value = "计划产量(首批)")
    private String planQuantitySpIsUpd;

    @ApiModelProperty(value = "计划产量(首批)")
    private Integer planQuantitySp;

    @ApiModelProperty(value = "实际产量(首批)")
    private String actualQuantitySpIsUpd;

    @ApiModelProperty(value = "实际产量(首批)")
    private Integer actualQuantitySp;

}
