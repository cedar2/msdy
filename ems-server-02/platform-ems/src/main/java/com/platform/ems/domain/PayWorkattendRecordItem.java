package com.platform.ems.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 考勤信息-明细对象 s_pay_workattend_record_item
 *
 * @author linhongwei
 * @date 2021-09-14
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_pay_workattend_record_item")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PayWorkattendRecordItem extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-考勤明细信息
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-考勤明细信息")
    private Long recordItemSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] recordItemSidList;
    /**
     * 系统SID-考勤单
     */
    @Excel(name = "系统SID-考勤单")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-考勤单")
    private Long workattendRecordSid;

    /**
     * 系统SID-员工档案
     */
    @Excel(name = "系统SID-员工档案")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-员工档案")
    private Long staffSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "员工姓名")
    private String staffName;

    @ApiModelProperty(value = "员工编号")
    private String staffCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "岗位sid")
    private Long positionSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "岗位名称")
    private String positionName;

    /**
     * 应出勤天数
     */
    @NotNull(message = "明细行应出勤天数不能为空")
    @Digits(integer = 2,fraction = 1, message = "应出勤天数整数位上限为2位，小数位上限为1位")
    @Excel(name = "应出勤天数")
    @ApiModelProperty(value = "应出勤天数")
    private BigDecimal yingcq;

    /**
     * 带薪天数
     */
    @Digits(integer = 2,fraction = 1, message = "带薪天数整数位上限为2位，小数位上限为1位")
    @Excel(name = "带薪天数")
    @ApiModelProperty(value = "带薪天数")
    private BigDecimal daix;

    /**
     * 实出勤天数
     */
    @NotNull(message = "明细行实出勤天数不能为空")
    @Digits(integer = 2,fraction = 1, message = "实出勤天数整数位上限为2位，小数位上限为1位")
    @Excel(name = "实出勤天数")
    @ApiModelProperty(value = "实出勤天数")
    private BigDecimal shicq;

    /**
     * 日常白天加班时数
     */
    @Digits(integer = 2,fraction = 1, message = "日常白天加班时数整数位上限为2位，小数位上限为1位")
    @Excel(name = "日常白天加班时数")
    @ApiModelProperty(value = "日常白天加班时数")
    private BigDecimal rcbtjb;

    /**
     * 日常夜晚加班时数
     */
    @Digits(integer = 2,fraction = 1, message = "日常夜晚加班时数整数位上限为2位，小数位上限为1位")
    @Excel(name = "日常夜晚加班时数")
    @ApiModelProperty(value = "日常夜晚加班时数")
    private BigDecimal rcywjb;

    /**
     * 节假日白天加班时数
     */
    @Digits(integer = 2,fraction = 1, message = "节假日白天加班时数整数位上限为2位，小数位上限为1位")
    @Excel(name = "节假日白天加班时数")
    @ApiModelProperty(value = "节假日白天加班时数")
    private BigDecimal jrbtjb;

    /**
     * 节假日夜晚加班时数
     */
    @Digits(integer = 2,fraction = 1, message = "节假日夜晚加班时数整数位上限为2位，小数位上限为1位")
    @Excel(name = "节假日夜晚加班时数")
    @ApiModelProperty(value = "节假日夜晚加班时数")
    private BigDecimal jrywjb;

    /**
     * 请调休假天数
     */
    @Digits(integer = 2,fraction = 1, message = "请调休假天数整数位上限为2位，小数位上限为1位")
    @Excel(name = "请调休假天数")
    @ApiModelProperty(value = "请调休假天数")
    private BigDecimal tiaoxj;

    /**
     * 请年假天数
     */
    @Digits(integer = 2,fraction = 1, message = "请年假天数整数位上限为2位，小数位上限为1位")
    @Excel(name = "请年假天数")
    @ApiModelProperty(value = "请年假天数")
    private BigDecimal nianj;

    /**
     * 请无薪病假天数
     */
    @Digits(integer = 2,fraction = 1, message = "请无薪病假天数整数位上限为2位，小数位上限为1位")
    @Excel(name = "请无薪病假天数")
    @ApiModelProperty(value = "请无薪病假天数")
    private BigDecimal bingjWx;

    /**
     * 请带薪病假天数
     */
    @Digits(integer = 2,fraction = 1, message = "请带薪病假天数整数位上限为2位，小数位上限为1位")
    @Excel(name = "请带薪病假天数")
    @ApiModelProperty(value = "请带薪病假天数")
    private BigDecimal bingjDx;

    /**
     * 请带薪待料假天数
     */
    @Digits(integer = 2,fraction = 1, message = "请带薪待料假天数整数位上限为2位，小数位上限为1位")
    @Excel(name = "请带薪待料假天数")
    @ApiModelProperty(value = "请带薪待料假天数")
    private BigDecimal dailjDx;

    /**
     * 请无薪事假天数
     */
    @Digits(integer = 2,fraction = 1, message = "请无薪事假天数整数位上限为2位，小数位上限为1位")
    @Excel(name = "请无薪事假天数")
    @ApiModelProperty(value = "请无薪事假天数")
    private BigDecimal shijWx;

    /**
     * 请带薪事假天数
     */
    @Digits(integer = 2,fraction = 1, message = "请带薪事假天数整数位上限为2位，小数位上限为1位")
    @Excel(name = "请带薪事假天数")
    @ApiModelProperty(value = "请带薪事假天数")
    private BigDecimal shijDx;

    /**
     * 迟到次数
     */
//    @Pattern(regexp = "\\d{2}",message = "迟到次数必须为数字，并且位数不能超过2位数")
    @Excel(name = "迟到次数")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "迟到次数")
    private String chidCs;

    /**
     * 迟到分数
     */
//    @Pattern(regexp = "\\d{5}",message = "迟到分数必须为数字，并且位数不能超过5位数")
    @Excel(name = "迟到分数")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "迟到分数")
    private String chidFs;

    /**
     * 旷工天数
     */
    @Digits(integer = 2,fraction = 1, message = "旷工天数整数位上限为2位，小数位上限为1位")
    @Excel(name = "旷工天数")
    @ApiModelProperty(value = "旷工天数")
    private BigDecimal kuangg;

    /**
     * 请婚假天数
     */
    @Digits(integer = 2,fraction = 1, message = "请婚假天数整数位上限为2位，小数位上限为1位")
    @Excel(name = "请婚假天数")
    @ApiModelProperty(value = "请婚假天数")
    private BigDecimal hunj;

    /**
     * 请产假天数
     */
    @Digits(integer = 2,fraction = 1, message = "请产假天数整数位上限为2位，小数位上限为1位")
    @Excel(name = "请产假天数")
    @ApiModelProperty(value = "请产假天数")
    private BigDecimal chanj;

    /**
     * 请陪产假天数
     */
    @Digits(integer = 2,fraction = 1, message = "请陪产假天数整数位上限为2位，小数位上限为1位")
    @Excel(name = "请陪产假天数")
    @ApiModelProperty(value = "请陪产假天数")
    private BigDecimal peicj;

    /**
     * 请带薪差旅路途假天数
     */
    @Digits(integer = 2,fraction = 1, message = "请带薪差旅路途假天数整数位上限为2位，小数位上限为1位")
    @Excel(name = "请带薪差旅路途假天数")
    @ApiModelProperty(value = "请带薪差旅路途假天数")
    private BigDecimal chailjDx;

    /**
     * 请丧假天数
     */
    @Digits(integer = 2,fraction = 1, message = "请丧假天数整数位上限为2位，小数位上限为1位")
    @Excel(name = "请丧假天数")
    @ApiModelProperty(value = "请丧假天数")
    private BigDecimal shangj;

    /**
     * 创建人账号（用户名称）
     */
    @Excel(name = "创建人账号（用户名称）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /**
     * 更新人账号（用户名称）
     */
    @Excel(name = "更新人账号（用户名称）")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "数据源系统（数据字典的键值或配置档案的编码）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    @TableField(exist = false)
    @ApiModelProperty(value = "所属年月")
    private String yearmonth;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司sid")
    private Long companySid;

    @TableField(exist = false)
    @ApiModelProperty(value = "部门sid")
    private Long departmentSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "考勤单号")
    private String workattendRecordCode;

    @TableField(exist = false)
    private String handleStatus;

    /** 缺卡次数 */
    @Excel(name = "缺卡次数")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "缺卡次数")
    private Long quekCs;

    /** 早退次数 */
    @Excel(name = "早退次数")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "早退次数")
    private Long zaotCs;

    /** 平时加班时数 */
    @Excel(name = "平时加班时数")
    @ApiModelProperty(value = "平时加班时数")
    private BigDecimal pingsjb;

    /** 周末加班时数 */
    @Excel(name = "周末加班时数")
    @ApiModelProperty(value = "周末加班时数")
    private BigDecimal zhoumjb;

    /** 节假日加班时数 */
    @Excel(name = "节假日加班时数")
    @ApiModelProperty(value = "节假日加班时数")
    private BigDecimal jiejrjb;

    /** 深夜加班时数 */
    @Excel(name = "深夜加班时数")
    @ApiModelProperty(value = "深夜加班时数")
    private BigDecimal shenyjb;

    /** 其它加班时数 */
    @Excel(name = "其它加班时数")
    @ApiModelProperty(value = "其它加班时数")
    private BigDecimal qitjb;

    /** 晚餐补贴次数 */
    @Excel(name = "晚餐补贴次数")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "晚餐补贴次数")
    private Long wancbtCs;

    /** 其它假天数 */
    @Excel(name = "其它假天数")
    @ApiModelProperty(value = "其它假天数")
    private BigDecimal qitj;

    @Excel(name = "部门(主属)")
    @ApiModelProperty(value = "主属部门名称")
    @TableField(exist = false)
    private String departmentName;

    @TableField(exist = false)
    @Excel(name = "班组")
    @ApiModelProperty(value = "工作中心/班组名称")
    private String workCenterName;

    @Excel(name = "入职日期")
    @ApiModelProperty(value = "入职日期")
    @TableField(exist = false)
    private String checkInDate;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-工厂")
    @TableField(exist = false)
    private Long defaultPlantSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "主属公司sid")
    @TableField(exist = false)
    private Long defaultCompanySid;
}
