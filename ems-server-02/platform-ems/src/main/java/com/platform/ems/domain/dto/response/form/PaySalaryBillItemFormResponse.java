package com.platform.ems.domain.dto.response.form;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
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
import java.math.BigDecimal;
import java.util.Date;

/**
 * 工资单明细报表 PaySalaryBillItemFormResponse
 *
 * @author chenkaiwen
 * @date 2022-08-02
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaySalaryBillItemFormResponse extends EmsBaseEntity {

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-工资单明细信息")
    private Long billItemSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "sid数组")
    private Long[] billItemSidList;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-工资单")
    private Long salaryBillSid;

    @TableField(exist = false)
    @Excel(name = "所属年月")
    @ApiModelProperty(value = "所属年月")
    private String yearmonth;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-员工档案")
    private Long staffSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-员工档案")
    private Long[] staffSidList;

    @Excel(name = "工号")
    @ApiModelProperty(value = "员工编号")
    private String staffCode;

    @TableField(exist = false)
    @Excel(name = "姓名")
    @ApiModelProperty(value = "员工姓名")
    private String staffName;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司sid")
    private Long companySid;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司sid（多选）")
    private Long[] companySidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司编码")
    private String companyCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @TableField(exist = false)
    @Excel(name = "公司")
    @ApiModelProperty(value = "公司名称")
    private String companyShortName;

    @TableField(exist = false)
    @Excel(name = "入职日期")
    @ApiModelProperty(value = "入职日期")
    private String checkInDate;

    @TableField(exist = false)
    @Excel(name = "工资成本分摊" , dictType = "s_salary_cost_allocate_type")
    @ApiModelProperty(value = "工资成本分摊类型")
    private String salaryCostAllocateType;

    @Digits(integer = 6, fraction = 2, message = "实发工资(税后)整数位上限为6位，小数位上限为2位")
    @NotNull(message = "实发工资(税后)不能为空")
    @Excel(name = "实发工资")
    @ApiModelProperty(value = "实发工资(税后)")
    private BigDecimal netPayroll;

    @Digits(integer = 6, fraction = 2, message = "应发工资(税前)整数位上限为6位，小数位上限为2位")
    @NotNull(message = "应发工资(税前)不能为空")
    @Excel(name = "应发工资")
    @ApiModelProperty(value = "应发工资(税前)")
    private BigDecimal yingfPayroll;

    @Digits(integer = 6, fraction = 2, message = "基本工资整数位上限为6位，小数位上限为2位")
    @NotNull(message = "基本工资不能为空")
    @Excel(name = "基本工资")
    @ApiModelProperty(value = "基本工资")
    private BigDecimal wageBase;

    @Digits(integer = 6, fraction = 2, message = "责任工资整数位上限为6位，小数位上限为2位")
    @Excel(name = "责任工资")
    @ApiModelProperty(value = "责任工资")
    private BigDecimal wageDuty;

    @Digits(integer = 6, fraction = 2, message = "计件工资整数位上限为6位，小数位上限为2位")
    @Excel(name = "计件工资")
    @ApiModelProperty(value = "计件工资")
    private BigDecimal wagePiece;

    @Digits(integer = 6, fraction = 2, message = "返修工资整数位上限为6位，小数位上限为2位")
    @Excel(name = "返修工资")
    @ApiModelProperty(value = "返修工资")
    private BigDecimal wageFanxiu;

    @ApiModelProperty(value = "返修工资（系统自动生成）")
    private BigDecimal wageFanxiuSys;

    @Digits(integer = 6, fraction = 2, message = "其它工资整数位上限为6位，小数位上限为2位")
    @Excel(name = "其它工资")
    @ApiModelProperty(value = "其它工资")
    private BigDecimal wageQit;

    @Excel(name = "加班时间(时)")
    @ApiModelProperty(value = "日常加班时间(时)")
    private BigDecimal timeRcjb;

    @Digits(integer = 6, fraction = 2, message = "日常加班工资整数位上限为6位，小数位上限为2位")
    @Excel(name = "加班工资")
    @ApiModelProperty(value = "日常加班工资")
    private BigDecimal wageRcjb;

    @Digits(integer = 6, fraction = 2, message = "节假日加班工资整数位上限为6位，小数位上限为2位")
    @ApiModelProperty(value = "节假日加班工资")
    private BigDecimal wageJrjb;

    @Digits(integer = 6, fraction = 2, message = "通宵工资整数位上限为6位，小数位上限为2位")
    @Excel(name = "通宵工资")
    @ApiModelProperty(value = "通宵加班工资")
    private BigDecimal wageTongxjb;

    @Digits(integer = 6, fraction = 2, message = "其它加班工资整数位上限为6位，小数位上限为2位")
    @Excel(name = "其它加班工资")
    @ApiModelProperty(value = "其它加班工资")
    private BigDecimal wageQitjb;

    @Digits(integer = 6, fraction = 2, message = "月绩效奖金整数位上限为6位，小数位上限为2位")
    @Excel(name = "月绩效奖金")
    @ApiModelProperty(value = "月绩效奖金")
    private BigDecimal bonusYuejx;

    @Digits(integer = 6, fraction = 2, message = "年度绩效奖金整数位上限为6位，小数位上限为2位")
    @ApiModelProperty(value = "年度绩效奖金")
    private BigDecimal bonusNiandjx;

    @Digits(integer = 6, fraction = 2, message = "月满勤奖金整数位上限为6位，小数位上限为2位")
    @Excel(name = "月满勤奖金")
    @ApiModelProperty(value = "月满勤奖金")
    private BigDecimal bonusYuemq;

    @Digits(integer = 6, fraction = 2, message = "季度满勤奖金整数位上限为6位，小数位上限为2位")
    @Excel(name = "季度满勤奖金")
    @ApiModelProperty(value = "季度满勤奖金")
    private BigDecimal bonusJidmq;

    @Digits(integer = 6, fraction = 2, message = "年度满勤奖金整数位上限为6位，小数位上限为2位")
    @Excel(name = "年度满勤奖金")
    @ApiModelProperty(value = "年度满勤奖金")
    private BigDecimal bonusNiandmq;

    @Digits(integer = 6, fraction = 2, message = "机台保养奖金整数位上限为6位，小数位上限为2位")
    @Excel(name = "机台保养奖金")
    @ApiModelProperty(value = "机台保养奖金")
    private BigDecimal bonusJitby;

    @Digits(integer = 6, fraction = 2, message = "服从奖金整数位上限为6位，小数位上限为2位")
    @Excel(name = "服从奖金")
    @ApiModelProperty(value = "服从奖金")
    private BigDecimal bonusFuc;

    @Digits(integer = 6, fraction = 2, message = "抽成奖金整数位上限为6位，小数位上限为2位")
    @Excel(name = "抽成奖金")
    @ApiModelProperty(value = "抽成奖金")
    private BigDecimal bonusChouc;

    @Digits(integer = 6, fraction = 2, message = "计件奖金整数位上限为6位，小数位上限为2位")
    @Excel(name = "计件奖金")
    @ApiModelProperty(value = "计件奖金")
    private BigDecimal bonusJij;

    @Digits(integer = 6, fraction = 2, message = "人才介绍奖金整数位上限为6位，小数位上限为2位")
    @Excel(name = "人才介绍奖金")
    @ApiModelProperty(value = "人才介绍奖金")
    private BigDecimal bonusRencjs;

    @Digits(integer = 6, fraction = 2, message = "年终奖金整数位上限为6位，小数位上限为2位")
    @ApiModelProperty(value = "年终奖金")
    private BigDecimal bonusYearend;

    @Digits(integer = 6, fraction = 2, message = "其它奖金整数位上限为6位，小数位上限为2位")
    @Excel(name = "其它奖金")
    @ApiModelProperty(value = "其它奖金")
    private BigDecimal bonusOther;

    @Digits(integer = 6, fraction = 2, message = "住房补贴整数位上限为6位，小数位上限为2位")
    @Excel(name = "住房补贴")
    @ApiModelProperty(value = "住房补贴")
    private BigDecimal allowanceZhuf;

    @Digits(integer = 6, fraction = 2, message = "餐费补贴整数位上限为6位，小数位上限为2位")
    @Excel(name = "餐费补贴")
    @ApiModelProperty(value = "餐费补贴")
    private BigDecimal allowanceCanf;

    @Digits(integer = 6, fraction = 2, message = "话费补贴整数位上限为6位，小数位上限为2位")
    @Excel(name = "话费补贴")
    @ApiModelProperty(value = "话费补贴")
    private BigDecimal allowanceHauf;

    @Digits(integer = 6, fraction = 2, message = "交通补贴整数位上限为6位，小数位上限为2位")
    @Excel(name = "交通补贴")
    @ApiModelProperty(value = "交通补贴")
    private BigDecimal allowanceJiaot;

    @Digits(integer = 6, fraction = 2, message = "差旅补贴整数位上限为6位，小数位上限为2位")
    @Excel(name = "差旅补贴")
    @ApiModelProperty(value = "差旅补贴")
    private BigDecimal allowanceChail;

    @Digits(integer = 6, fraction = 2, message = "岗位津贴整数位上限为6位，小数位上限为2位")
    @Excel(name = "岗位津贴")
    @ApiModelProperty(value = "岗位津贴")
    private BigDecimal allowanceGangw;

    @Digits(integer = 6, fraction = 2, message = "稳岗补贴整数位上限为6位，小数位上限为2位")
    @Excel(name = "稳岗补贴")
    @ApiModelProperty(value = "稳岗补贴")
    private BigDecimal allowanceWeng;

    @Digits(integer = 6, fraction = 2, message = "特殊工种补贴整数位上限为6位，小数位上限为2位")
    @Excel(name = "特殊工种补贴")
    @ApiModelProperty(value = "特殊工种补贴")
    private BigDecimal allowanceTesgz;

    @Digits(integer = 6, fraction = 2, message = "餐具补贴整数位上限为6位，小数位上限为2位")
    @Excel(name = "餐具补贴")
    @ApiModelProperty(value = "餐具押金退回")
    private BigDecimal allowanceCanj;

    @Digits(integer = 6, fraction = 2, message = "计件保底补贴整数位上限为6位，小数位上限为2位")
    @Excel(name = "计件保底补贴")
    @ApiModelProperty(value = "计件保底补贴")
    private BigDecimal allowanceBaod;

    @Digits(integer = 6, fraction = 2, message = "其它计件补贴整数位上限为6位，小数位上限为2位")
    @Excel(name = "其它计件补贴")
    @ApiModelProperty(value = "其它计件补贴")
    private BigDecimal allowanceQitjj;

    @Digits(integer = 6, fraction = 2, message = "其它补贴整数位上限为6位，小数位上限为2位")
    @Excel(name = "其它补贴")
    @ApiModelProperty(value = "其它补贴")
    private BigDecimal allowanceOther;

    @Digits(integer = 6, fraction = 2, message = "办公补贴整数位上限为6位，小数位上限为2位")
    @ApiModelProperty(value = "办公补贴")
    private BigDecimal allowanceBang;

    @Digits(integer = 6, fraction = 2, message = "高温补贴整数位上限为6位，小数位上限为2位")
    @ApiModelProperty(value = "高温补贴")
    private BigDecimal allowanceGaow;

    @Digits(integer = 6, fraction = 2, message = "水电费扣款整数位上限为6位，小数位上限为2位")
    @Excel(name = "水电费扣款")
    @ApiModelProperty(value = "水电费扣款")
    private BigDecimal deductShuid;

    @Digits(integer = 6, fraction = 2, message = "旷工扣款整数位上限为6位，小数位上限为2位")
    @ApiModelProperty(value = "旷工扣款")
    private BigDecimal deductKuangg;

    @Digits(integer = 6, fraction = 2, message = "迟到扣款整数位上限为6位，小数位上限为2位")
    @Excel(name = "迟到/缺卡扣款")
    @ApiModelProperty(value = "迟到扣款")
    private BigDecimal deductChid;

    @Digits(integer = 6, fraction = 2, message = "餐具扣款整数位上限为6位，小数位上限为2位")
    @Excel(name = "餐具扣款")
    @ApiModelProperty(value = "餐具押金扣款")
    private BigDecimal deductCanj;

    @Digits(integer = 6, fraction = 2, message = "短少扣款整数位上限为6位，小数位上限为2位")
    @Excel(name = "短少扣款")
    @ApiModelProperty(value = "短少扣款")
    private BigDecimal deductDuans;

    @Digits(integer = 6, fraction = 2, message = "品质扣款奖金整数位上限为6位，小数位上限为2位")
    @Excel(name = "品质扣款")
    @ApiModelProperty(value = "品质扣款")
    private BigDecimal deductPinz;

    @Digits(integer = 6, fraction = 2, message = "5S检查扣款整数位上限为6位，小数位上限为2位")
    @Excel(name = "5S检查扣款")
    @ApiModelProperty(value = "5S检查扣款")
    private BigDecimal deductFivecheck;

    @Digits(integer = 6, fraction = 2, message = "其它扣款整数位上限为6位，小数位上限为2位")
    @Excel(name = "其它扣款")
    @ApiModelProperty(value = "其它扣款")
    private BigDecimal deductOther;

    @Digits(integer = 6, fraction = 2, message = "责任扣款整数位上限为6位，小数位上限为2位")
    @ApiModelProperty(value = "责任扣款")
    private BigDecimal deductDuty;

    @Digits(integer = 6, fraction = 2, message = "餐费扣款整数位上限为6位，小数位上限为2位")
    @ApiModelProperty(value = "餐费扣款")
    private BigDecimal deductCanf;

    @Digits(integer = 6, fraction = 2, message = "代缴(公积金)整数位上限为6位，小数位上限为2位")
    @Excel(name = "代缴(公积金)")
    @ApiModelProperty(value = "代缴(公积金)")
    private BigDecimal feesGongjj;

    @Digits(integer = 6, fraction = 2, message = "代缴(社保)整数位上限为6位，小数位上限为2位")
    @Excel(name = "代缴(社保)")
    @ApiModelProperty(value = "代缴(社保)")
    private BigDecimal feesSheb;

    @Digits(integer = 6, fraction = 2, message = "代缴(医保)整数位上限为6位，小数位上限为2位")
    @Excel(name = "代缴(医保)")
    @ApiModelProperty(value = "代缴(医保)")
    private BigDecimal feesYib;

    @Digits(integer = 6, fraction = 2, message = "代缴(个税)整数位上限为6位，小数位上限为2位")
    @Excel(name = "代缴(个税)")
    @ApiModelProperty(value = "代缴(个税)")
    private BigDecimal feesIncometax;

    @Digits(integer = 6, fraction = 2, message = "其它代缴整数位上限为6位，小数位上限为2位")
    @Excel(name = "其它代缴")
    @ApiModelProperty(value = "其它代缴")
    private BigDecimal feesOther;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "工厂sid")
    private Long plantSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "工厂sid（多选）")
    private Long[] plantSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "工厂编码")
    private String plantCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "工厂名称")
    private String plantName;

    @TableField(exist = false)
    @Excel(name = "工厂")
    @ApiModelProperty(value = "工厂简称")
    private String plantShortName;

    @TableField(exist = false)
    @Excel(name = "工资单号")
    @ApiModelProperty(value = "工资单号")
    private String salaryBillCode;

    @TableField(exist = false)
    @Excel(name = "处理状态" , dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态（多选）")
    private String[] handleStatusList;

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    @TableField(exist = false)
    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccountName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @Excel(name = "更新人账号（用户名称）")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

    @TableField(exist = false)
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccountName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "部门sid")
    private Long departmentSid;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "部门sid")
    private Long defaultDepartmentSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "部门名称")
    private String departmentName;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "岗位sid")
    private Long positionSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "岗位名称")
    private String positionName;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-工作中心/班组")
    private Long workCenterSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "班组名称")
    private String workCenterName;

}
