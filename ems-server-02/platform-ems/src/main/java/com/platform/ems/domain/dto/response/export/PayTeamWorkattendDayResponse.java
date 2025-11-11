package com.platform.ems.domain.dto.response.export;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * 班组日出勤信息对象
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PayTeamWorkattendDayResponse {


    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "日期(出勤)")
    private Date workattendDate;

    @ApiModelProperty(value = "工厂名称")
    @TableField(exist = false)
    @Excel(name = "工厂")
    private String plantName;

    @ApiModelProperty(value = "操作部门")
    @TableField(exist = false)
    @Excel(name = "操作部门")
    private String departmentName;

    @ApiModelProperty(value = "班组名称")
    @TableField(exist = false)
    @Excel(name = "班组")
    private String workCenterName;

    @Excel(name = "工作班次",dictType = "s_work_shift")
    @ApiModelProperty(value = "工作班次（数据字典的键值或配置档案的编码）")
    private String workShift;

    /**
     * 应出勤(人数)
     */
    @Excel(name = "应出勤(人数)")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "应出勤(人数)")
    private Long yingcq;

    /**
     * 实出勤(人数)
     */
    @Excel(name = "实出勤(人数)")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "实出勤(人数)")
    private Long shicq;

    /**
     * 请假(人数)
     */
    @Excel(name = "请假(人数)")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "请假(人数)")
    private Long qingj;

    /**
     * 待料(人数)
     */
    @Excel(name = "待料(人数)")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "待料(人数)")
    private Long dail;

    /**
     * 旷工(人数)
     */
    @Excel(name = "旷工(人数)")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "旷工(人数)")
    private Long kuangg;

    /**
     * 缺卡(人数)
     */
    @Excel(name = "缺卡(人数)")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "缺卡(人数)")
    private Long quek;

    /**
     * 迟到(人数)
     */
    @Excel(name = "迟到(人数)")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "迟到(人数)")
    private Long chid;

    /**
     * 早退(人数)
     */
    @Excel(name = "早退(人数)")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "早退(人数)")
    private Long zaot;

    /**
     * 处理状态（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;


    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    /** 创建人账号 */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccountName;

    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @Excel(name = "更改人")
    @ApiModelProperty(value = "更新人")
    private String updaterAccountName;

    @Excel(name = "更新日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    @Excel(name = "确认人")
    @ApiModelProperty(value = "确认人")
    private String confirmerAccountName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "确认日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "班组日出勤编号")
    @Excel(name = "班组日出勤编号")
    private Long teamWorkattendDayCode;
}
