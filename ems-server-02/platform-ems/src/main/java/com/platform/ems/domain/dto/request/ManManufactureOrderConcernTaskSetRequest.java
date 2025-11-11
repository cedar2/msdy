package com.platform.ems.domain.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Digits;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 生产订单事项明细报表 ManManufactureOrderConcernTaskSetRequest
 *
 * @author chenkaiwen
 * @date 2022-08-11
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ManManufactureOrderConcernTaskSetRequest  {

    @ApiModelProperty(value = "查询页面设置信息的类型：计划信息JH/进度信息JD")
    private String setType;

    @ApiModelProperty(value = "sid数组")
    private Long[] manufactureOrderConcernTaskSidList;

    // === 计划信息 === //

    /**
     * 负责人sid(员工档案)
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "负责人sid(员工档案)")
    private Long handlerSid;

    @ApiModelProperty(value = "是否修改 Y or N 负责人sid(员工档案)")
    private String handlerSidIsUpd;

    /**
     * 负责人编号(员工档案)
     */
    @ApiModelProperty(value = "负责人编号(员工档案)")
    private String handlerCode;

    /**
     * 计划开始日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划开始日期")
    private Date planStartDate;

    @ApiModelProperty(value = "是否修改 Y or N 计划开始日期")
    private String planStartDateIsUpd;

    /**
     * 计划完成日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划完成日期")
    private Date planEndDate;

    @ApiModelProperty(value = "是否修改 Y or N 计划完成日期")
    private String planEndDateIsUpd;

    @Digits(integer = 8, fraction = 3, message = "计划完成量整数位上限为8位，小数位上限为3位")
    @ApiModelProperty(value = "计划完成量")
    private BigDecimal planQuantity;

    @ApiModelProperty(value = "是否修改 Y or N 计划完成量")
    private String planQuantityIsUpd;

    // === 进度信息 === //

    @ApiModelProperty(value = "是否修改 Y or N 实际开始日期")
    private String actualStartDateIsUpd;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "实际开始日期")
    private Date actualStartDate;

    @ApiModelProperty(value = "是否修改 Y or N 实际完成日期")
    private String actualEndDateIsUpd;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "实际完成日期")
    private Date actualEndDate;

    @ApiModelProperty(value = "是否修改 Y or N 实际完成量")
    private String actualQuantityIsUpd;

    @Digits(integer = 8, fraction = 3, message = "实际完成量整数位上限为8位，小数位上限为3位")
    @ApiModelProperty(value = "实际完成量）")
    private BigDecimal actualQuantity;

    @ApiModelProperty(value = "是否修改 Y or N 进度说明")
    private String handleCommentIsUpd;

    @ApiModelProperty(value = "进度说明")
    private String handleComment;

    // 2022-10-11 wp 新增字段
    // 字典数据：s_end_status
    @ApiModelProperty(value = "完成状态")
    private String endStatus;

    // 2022-10-11 wp 新增字段
    @ApiModelProperty(value = "是否修改 Y or N 完成状态")
    private String endStatusIsUpd;

    @ApiModelProperty(value = "图片路径是否修改")
    private String picturePathListIsUpd;

    @ApiModelProperty(value = "图片路径，可放多个链接，每个链接用”;“隔开")
    private String[] picturePathList;

    @ApiModelProperty(value = "视频路径是否修改")
    private String videoPathListIsUpd;

    @ApiModelProperty(value = "视频路径，可放多个链接，每个链接用”;“隔开")
    private String[] videoPathList;

}
