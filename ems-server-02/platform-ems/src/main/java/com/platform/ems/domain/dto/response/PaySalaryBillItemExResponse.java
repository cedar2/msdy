package com.platform.ems.domain.dto.response;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.platform.common.annotation.Excel;
import com.platform.ems.util.data.KeepTwoDecimalsSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 工资单明细报表
 *
 * @author linhongwei
 * @date 2021-09-14
 */
@Data
@Accessors(chain = true)
@ApiModel
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaySalaryBillItemExResponse {

    @Excel(name = "所属年月")
    @ApiModelProperty(value = "所属年月")
    private String yearmonth;

    @Excel(name = "工号")
    @ApiModelProperty(value = "员工编号")
    private String staffCode;

    @Excel(name = "姓名")
    @ApiModelProperty(value = "员工姓名")
    private String staffName;

    @Excel(name = "公司")
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @Excel(name = "部门")
    @ApiModelProperty(value = "部门名称")
    private String departmentName;

    @Excel(name = "岗位")
    @ApiModelProperty(value = "岗位名称")
    private String positionName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "入职日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "入职日期")
    private Date checkInDate;

    @Excel(name = "工资成本分摊",dictType = "s_salary_cost_allocate_type")
    @ApiModelProperty(value = "工资成本分摊类型（数据字典或者配置档案键值）")
    private String salaryCostAllocateType;

    @Excel(name = "实发工资")
    @Digits(integer = 6, fraction = 3, message = "实发工资(税后)整数位上限为6位，小数位上限为3位")
    @ApiModelProperty(value = "实发工资(税后)")
    private BigDecimal netPayroll;

    @Excel(name = "应发工资")
    @Digits(integer = 6, fraction = 3, message = "应发工资(税前)整数位上限为6位，小数位上限为3位")
    @NotNull(message = "应发工资(税前)不能为空")
    @ApiModelProperty(value = "应发工资(税前)")
    private BigDecimal yingfPayroll;

    @Excel(name = "基本工资")
    @ApiModelProperty(value = "基本工资")
    private BigDecimal wageBase;

    @Digits(integer = 6, fraction = 3, message = "责任工资整数位上限为6位，小数位上限为3位")
    @Excel(name = "责任工资")
    @ApiModelProperty(value = "责任工资")
    private BigDecimal wageDuty;

    /**
     * 计件工资
     */
    @Digits(integer = 6, fraction = 3, message = "计件工资整数位上限为6位，小数位上限为3位")
    @Excel(name = "计件工资")
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "计件工资")
    private BigDecimal wagePiece;


    @Excel(name = "计时工资")
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "计时工资")
    private BigDecimal wageJishi;


    @Excel(name = "返修工资")
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "返修工资")
    private BigDecimal wageFanxiu;



    @Digits(integer = 6, fraction = 3, message = "其它工资整数位上限为6位，小数位上限为3位")
    @Excel(name = "其它工资")
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "其它工资")
    private BigDecimal wageQit;

    @Digits(integer = 3, fraction = 1, message = "日常加班时间(时)整数位上限为3位，小数位上限为1位")
    @Excel(name = "加班时间(时)")
    @ApiModelProperty(value = "日常加班时间(时)")
    private BigDecimal timeRcjb;

    @Digits(integer = 6, fraction = 3, message = "日常加班工资整数位上限为6位，小数位上限为3位")
    @Excel(name = "加班工资")
    @ApiModelProperty(value = "日常加班工资")
    private BigDecimal wageRcjb;

    @Excel(name = "通宵工资")
    @ApiModelProperty(value = "通宵加班工资")
    private BigDecimal wageTongxjb;

    @Excel(name = "其它加班工资")
    @ApiModelProperty(value = "其它加班工资")
    private BigDecimal wageQitjb;

    @Excel(name = "月绩效奖")
    @ApiModelProperty(value = "月绩效奖金")
    private BigDecimal bonusYuejx;


    @Excel(name = "月度满勤奖")
    @ApiModelProperty(value = "月满勤奖金")
    private BigDecimal bonusYuemq;

    @Digits(integer = 6, fraction = 3, message = "季度满勤奖金整数位上限为6位，小数位上限为3位")
    @Excel(name = "季度满勤奖")
    @ApiModelProperty(value = "季度满勤奖金")
    private BigDecimal bonusJidmq;

    @Digits(integer = 6, fraction = 3, message = "年度满勤奖金整数位上限为6位，小数位上限为3位")
    @Excel(name = "年度满勤奖")
    @ApiModelProperty(value = "年度满勤奖金")
    private BigDecimal bonusNiandmq;


    @Excel(name = "机台保养奖")
    @ApiModelProperty(value = "机台保养奖金")
    private BigDecimal bonusJitby;

    @Digits(integer = 6, fraction = 3, message = "服从奖金整数位上限为6位，小数位上限为3位")
    @Excel(name = "服从奖")
    @ApiModelProperty(value = "服从奖金")
    private BigDecimal bonusFuc;

    @Excel(name = "抽成奖")
    @ApiModelProperty(value = "抽成奖金")
    private BigDecimal bonusChouc;

    @Digits(integer = 6, fraction = 3, message = "计件奖金整数位上限为6位，小数位上限为3位")
    @Excel(name = "计件奖")
    @ApiModelProperty(value = "计件奖金")
    private BigDecimal bonusJij;


    @Excel(name = "人才介绍奖")
    @ApiModelProperty(value = "人才介绍奖金")
    private BigDecimal bonusRencjs;

    @Excel(name = "其它奖金")
    @ApiModelProperty(value = "其它奖金")
    private BigDecimal bonusOther;

    @Excel(name = "住房补贴")
    @ApiModelProperty(value = "住房补贴")
    private BigDecimal allowanceZhuf;

    @Excel(name = "餐费补贴")
    @ApiModelProperty(value = "餐费补贴")
    private BigDecimal allowanceCanf;

    @Excel(name = "话费补贴")
    @ApiModelProperty(value = "话费补贴")
    private BigDecimal allowanceHauf;

    @Excel(name = "交通补贴")
    @ApiModelProperty(value = "交通补贴")
    private BigDecimal allowanceJiaot;

    @Excel(name = "差旅补贴")
    @ApiModelProperty(value = "差旅补贴")
    private BigDecimal allowanceChail;

    @Excel(name = "岗位津贴")
    @ApiModelProperty(value = "岗位津贴")
    private BigDecimal allowanceGangw;


    @Excel(name = "稳岗补贴")
    @ApiModelProperty(value = "稳岗补贴")
    private BigDecimal allowanceWeng;

    @Excel(name = "特殊工种补贴")
    @ApiModelProperty(value = "特殊工种补贴")
    private BigDecimal allowanceTesgz;


    @Excel(name = "餐具补贴")
    @ApiModelProperty(value = "餐具补贴")
    private BigDecimal allowanceCanj;

    @Digits(integer = 6, fraction = 3, message = "计件保底补贴整数位上限为6位，小数位上限为3位")
    @Excel(name = "计件保底补贴")
    @ApiModelProperty(value = "计件保底补贴")
    private BigDecimal allowanceBaod;

    @Digits(integer = 6, fraction = 3, message = "其它计件补贴整数位上限为6位，小数位上限为3位")
    @Excel(name = "其它计件补贴")
    @ApiModelProperty(value = "其它计件补贴")
    private BigDecimal allowanceQitjj;

    @Excel(name = "其它补贴")
    @ApiModelProperty(value = "其它补贴")
    private BigDecimal allowanceOther;

    @Excel(name = "水电扣款")
    @ApiModelProperty(value = "水电费扣款")
    private BigDecimal deductShuid;

    @Excel(name = "迟到/缺卡扣款")
    @ApiModelProperty(value = "迟到扣款")
    private BigDecimal deductChid;

    @Excel(name = "餐费扣款")
    @ApiModelProperty(value = "餐费扣款")
    private BigDecimal deductCanf;

    @Digits(integer = 6, fraction = 3, message = "短少扣款整数位上限为6位，小数位上限为3位")
    @Excel(name = "短少扣款")
    @ApiModelProperty(value = "短少扣款")
    private BigDecimal deductDuans;

    @Digits(integer = 6, fraction = 3, message = "品质扣款整数位上限为6位，小数位上限为3位")
    @Excel(name = "品质扣款")
    @ApiModelProperty(value = "品质扣款")
    private BigDecimal deductPinz;

    @Digits(integer = 6, fraction = 3, message = "5S检查扣款整数位上限为6位，小数位上限为3位")
    @Excel(name = "5S检查扣款")
    @ApiModelProperty(value = "5S检查扣款")
    private BigDecimal deductFivecheck;


    @Excel(name = "其它扣款")
    @ApiModelProperty(value = "其它扣款")
    private BigDecimal deductOther;

    @Digits(integer = 6, fraction = 3, message = "代缴(公积金)整数位上限为6位，小数位上限为3位")
    @Excel(name = "代缴(公积金)")
    @ApiModelProperty(value = "代缴(公积金)")
    private BigDecimal feesGongjj;

    @Digits(integer = 6, fraction = 3, message = "代缴(医保)整数位上限为6位，小数位上限为3位")
    @Excel(name = "代缴(医保)")
    @ApiModelProperty(value = "代缴(医保)")
    private BigDecimal feesYib;

    @Digits(integer = 6, fraction = 3, message = "代缴(社保)整数位上限为6位，小数位上限为3位")
    @Excel(name = "代缴(社保)")
    @ApiModelProperty(value = "代缴(社保)")
    private BigDecimal feesSheb;

    /**
     * 代缴(个税)
     */
    @Digits(integer = 6, fraction = 3, message = "代缴(个税)整数位上限为6位，小数位上限为3位")
    @Excel(name = "代缴(个税)")
    @ApiModelProperty(value = "代缴(个税)")
    private BigDecimal feesIncometax;

    @Excel(name = "其它代缴")
    @ApiModelProperty(value = "其它代缴")
    private BigDecimal feesOther;

    @Excel(name = "备注")
    @ApiModelProperty(value ="备注")
    private String remark;

    @Excel(name = "工厂")
    @ApiModelProperty(value = "工厂名称")
    private String plantName;

    @Excel(name = "工资单号")
    @ApiModelProperty(value = "工资单号")
    private String salaryBillCode;

    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccountName;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

}
