package com.platform.ems.domain.dto.response;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 考勤信息-明细报表
 *
 * @author linhongwei
 * @date 2021-09-14
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PayWorkattendRecordItemResponse {

    @Excel(name = "所属年月")
    @ApiModelProperty(value = "所属年月")
    private String yearmonth;

    @ApiModelProperty(value = "员工编号")
    @Excel(name = "工号")
    private String staffCode;

    @Excel(name = "姓名")
    @ApiModelProperty(value = "员工姓名")
    private String staffName;

    @Excel(name = "公司")
    @ApiModelProperty(value = "公司sid")
    private String companyName;

    @Excel(name = "部门")
    @ApiModelProperty(value = "主属部门名称")
    @TableField(exist = false)
    private String departmentName;

    @TableField(exist = false)
    @ApiModelProperty(value = "岗位名称")
    @Excel(name = "岗位")
    private String positionName;

    @TableField(exist = false)
    @Excel(name = "班组")
    @ApiModelProperty(value = "工作中心/班组名称")
    private String workCenterName;

    @Excel(name = "入职日期")
    @ApiModelProperty(value = "入职日期")
    @TableField(exist = false)
    private String checkInDate;

    @Excel(name = "应出勤（天）")
    private BigDecimal yingcq;

    @Excel(name = "实出勤（天）")
    @ApiModelProperty(value = "实出勤天数")
    private BigDecimal shicq;

    @Excel(name = "旷工（天）")
    @ApiModelProperty(value = "旷工天数")
    private BigDecimal kuangg;

    @Excel(name = "缺卡（次）")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "缺卡次数")
    private Long quekCs;


    @Excel(name = "迟到（次）")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "迟到次数")
    private String chidCs;

    @Excel(name = "早退（次）")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "早退次数")
    private Long zaotCs;


    @Excel(name = "平时加班（时）")
    @ApiModelProperty(value = "平时加班时数")
    private BigDecimal pingsjb;

    @Excel(name = "周末加班（时）")
    @ApiModelProperty(value = "周末加班时数")
    private BigDecimal zhoumjb;

    @Excel(name = "节假日加班（时）")
    @ApiModelProperty(value = "节假日加班时数")
    private BigDecimal jiejrjb;

    @Excel(name = "深夜加班（时）")
    @ApiModelProperty(value = "深夜加班时数")
    private BigDecimal shenyjb;

    @Excel(name = "其它加班（时）")
    @ApiModelProperty(value = "其它加班时数")
    private BigDecimal qitjb;

    @Excel(name = "事假（天）")
    @ApiModelProperty(value = "请无薪事假天数")
    private BigDecimal shijWx;

    @Excel(name = "调休（天）")
    @ApiModelProperty(value = "请调休假天数")
    private BigDecimal tiaoxj;

    @Excel(name = "年假（天）")
    @ApiModelProperty(value = "请年假天数")
    private BigDecimal nianj;

    @Excel(name = "病假（天）")
    @ApiModelProperty(value = "病假（天）")
    private BigDecimal bingjDx;

    @Digits(integer = 2,fraction = 1, message = "请婚假天数整数位上限为2位，小数位上限为1位")
    @Excel(name = "婚假（天）")
    @ApiModelProperty(value = "请婚假天数")
    private BigDecimal hunj;

    @Digits(integer = 2,fraction = 1, message = "请产假天数整数位上限为2位，小数位上限为1位")
    @Excel(name = "产假（天）")
    @ApiModelProperty(value = "请产假天数")
    private BigDecimal chanj;

    @Digits(integer = 2,fraction = 1, message = "请丧假天数整数位上限为2位，小数位上限为1位")
    @Excel(name = "丧假（天）")
    @ApiModelProperty(value = "请丧假天数")
    private BigDecimal shangj;

    @Excel(name = "待料（天）")
    @ApiModelProperty(value = "请带薪待料假天数")
    private BigDecimal dailjDx;

    @Excel(name = "其它假（天）")
    @ApiModelProperty(value = "其它假天数")
    private BigDecimal qitj;

    @Excel(name = "晚餐补贴（次）")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "晚餐补贴次数")
    private Long wancbtCs;

    @Excel(name = "备注")
    @ApiModelProperty(value ="备注")
    private String remark;

    @Excel(name = "工厂")
    @ApiModelProperty(value = "工厂名称")
    private String plantName;

    @Excel(name = "考勤单号")
    @ApiModelProperty(value = "考勤单号")
    private String workattendRecordCode;

    @Excel(name = "处理状态",dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccountName;

    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;
}
