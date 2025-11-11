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
 * 工资单-明细对象 s_pay_salary_bill_item
 *
 * @author linhongwei
 * @date 2021-09-14
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_pay_salary_bill_item")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaySalaryBillItem extends EmsBaseEntity {

    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-工资单明细信息")
    private Long billItemSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] billItemSidList;

    @Excel(name = "系统SID-工资单")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-工资单")
    private Long salaryBillSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "工资单号")
    private String salaryBillCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "所属年月")
    private String yearmonth;

    @Excel(name = "系统SID-员工档案")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-员工档案")
    private Long staffSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-员工档案")
    private Long[] staffSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "员工姓名")
    private String staffName;

    @ApiModelProperty(value = "员工编号")
    private String staffCode;

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
    @ApiModelProperty(value = "入职日期")
    private String checkInDate;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司sid")
    private Long companySid;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司sid")
    private Long defaultCompanySid;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @ApiModelProperty(value = "公司简称")
    @TableField(exist = false)
    private String companyShortName;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "工厂sid")
    private Long plantSid;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "工厂sid")
    private Long defaultPlantSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "工厂名称")
    private String plantName;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-工作中心/班组")
    private Long workCenterSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "班组名称")
    private String workCenterName;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态")
    private String[] handleStatusList;

    @Excel(name = "应发工资(税前)")
    @Digits(integer = 6, fraction = 3, message = "应发工资(税前)整数位上限为6位，小数位上限为3位")
    @NotNull(message = "应发工资(税前)不能为空")
    @ApiModelProperty(value = "应发工资(税前)")
    private BigDecimal yingfPayroll;

    /**
     * 实发工资(税后)
     */
    @Excel(name = "实发工资(税后)")
    @Digits(integer = 6, fraction = 3, message = "实发工资(税后)整数位上限为6位，小数位上限为3位")
    @NotNull(message = "实发工资(税后)不能为空")
    @ApiModelProperty(value = "实发工资(税后)")
    private BigDecimal netPayroll;

    /**
     * 基本工资
     */
    @Digits(integer = 6, fraction = 3, message = "基本工资整数位上限为6位，小数位上限为3位")
    @NotNull(message = "基本工资不能为空")
    @Excel(name = "基本工资")
    @ApiModelProperty(value = "基本工资")
    private BigDecimal wageBase;

    /**
     * 责任工资
     */
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

    /**
     * 计件工资（系统自动生成）
     */
    @ApiModelProperty(value = "计件工资（系统自动生成）")
    private BigDecimal wagePieceSys;

    @Digits(integer = 6, fraction = 3, message = "计时工资整数位上限为6位，小数位上限为3位")
    @Excel(name = "计时工资")
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "计时工资")
    private BigDecimal wageJishi;

    @Digits(integer = 6, fraction = 3, message = "返修工资整数位上限为6位，小数位上限为3位")
    @Excel(name = "返修工资")
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "返修工资")
    private BigDecimal wageFanxiu;

    @ApiModelProperty(value = "返修工资（系统自动生成）")
    private BigDecimal wageFanxiuSys;

    @Digits(integer = 6, fraction = 3, message = "其它工资整数位上限为6位，小数位上限为3位")
    @Excel(name = "其它工资")
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "其它工资")
    private BigDecimal wageQit;

    @Digits(integer = 3, fraction = 1, message = "日常加班时间(时)整数位上限为3位，小数位上限为1位")
    @Excel(name = "日常加班时间(时)")
    @ApiModelProperty(value = "日常加班时间(时)")
    private BigDecimal timeRcjb;

    @Digits(integer = 6, fraction = 3, message = "日常加班工资整数位上限为6位，小数位上限为3位")
    @Excel(name = "日常加班工资")
    @ApiModelProperty(value = "日常加班工资")
    private BigDecimal wageRcjb;

    /**
     * 节假日加班工资
     */
    @Digits(integer = 6, fraction = 3, message = "节假日加班工资整数位上限为6位，小数位上限为3位")
    @Excel(name = "节假日加班工资")
    @ApiModelProperty(value = "节假日加班工资")
    private BigDecimal wageJrjb;

    @Digits(integer = 6, fraction = 3, message = "通宵加班工资整数位上限为6位，小数位上限为3位")
    @Excel(name = "通宵加班工资")
    @ApiModelProperty(value = "通宵加班工资")
    private BigDecimal wageTongxjb;

    @Digits(integer = 6, fraction = 3, message = "其它加班工资整数位上限为6位，小数位上限为3位")
    @Excel(name = "其它加班工资")
    @ApiModelProperty(value = "其它加班工资")
    private BigDecimal wageQitjb;

    @Digits(integer = 6, fraction = 3, message = "月绩效奖金整数位上限为6位，小数位上限为3位")
    @Excel(name = "月绩效奖金")
    @ApiModelProperty(value = "月绩效奖金")
    private BigDecimal bonusYuejx;

    @Digits(integer = 6, fraction = 3, message = "年度绩效奖金整数位上限为6位，小数位上限为3位")
    @Excel(name = "年度绩效奖金")
    @ApiModelProperty(value = "年度绩效奖金")
    private BigDecimal bonusNiandjx;

    @Digits(integer = 6, fraction = 3, message = "月满勤奖金整数位上限为6位，小数位上限为3位")
    @Excel(name = "月满勤奖金")
    @ApiModelProperty(value = "月满勤奖金")
    private BigDecimal bonusYuemq;

    @Digits(integer = 6, fraction = 3, message = "季度满勤奖金整数位上限为6位，小数位上限为3位")
    @Excel(name = "季度满勤奖金")
    @ApiModelProperty(value = "季度满勤奖金")
    private BigDecimal bonusJidmq;

    @Digits(integer = 6, fraction = 3, message = "年度满勤奖金整数位上限为6位，小数位上限为3位")
    @Excel(name = "年度满勤奖金")
    @ApiModelProperty(value = "年度满勤奖金")
    private BigDecimal bonusNiandmq;

    @Digits(integer = 6, fraction = 3, message = "机台保养奖金整数位上限为6位，小数位上限为3位")
    @Excel(name = "机台保养奖金")
    @ApiModelProperty(value = "机台保养奖金")
    private BigDecimal bonusJitby;

    @Digits(integer = 6, fraction = 3, message = "服从奖金整数位上限为6位，小数位上限为3位")
    @Excel(name = "服从奖金")
    @ApiModelProperty(value = "服从奖金")
    private BigDecimal bonusFuc;
    /**
     * 抽成奖金
     */
    @Digits(integer = 6, fraction = 3, message = "抽成奖金整数位上限为6位，小数位上限为3位")
    @Excel(name = "抽成奖金")
    @ApiModelProperty(value = "抽成奖金")
    private BigDecimal bonusChouc;

    @Digits(integer = 6, fraction = 3, message = "计件奖金整数位上限为6位，小数位上限为3位")
    @Excel(name = "计件奖金")
    @ApiModelProperty(value = "计件奖金")
    private BigDecimal bonusJij;

    @Digits(integer = 6, fraction = 3, message = "人才介绍奖金整数位上限为6位，小数位上限为3位")
    @Excel(name = "人才介绍奖金")
    @ApiModelProperty(value = "人才介绍奖金")
    private BigDecimal bonusRencjs;

    /**
     * 年终奖金
     */
    @Digits(integer = 6, fraction = 3, message = "年终奖金整数位上限为6位，小数位上限为3位")
    @Excel(name = "年终奖金")
    @ApiModelProperty(value = "年终奖金")
    private BigDecimal bonusYearend;

    /**
     * 其它奖金
     */
    @Digits(integer = 6, fraction = 3, message = "其它奖金整数位上限为6位，小数位上限为3位")
    @Excel(name = "其它奖金")
    @ApiModelProperty(value = "其它奖金")
    private BigDecimal bonusOther;

    /**
     * 岗位津贴
     */
    @Digits(integer = 6, fraction = 3, message = "岗位津贴整数位上限为6位，小数位上限为3位")
    @Excel(name = "岗位津贴")
    @ApiModelProperty(value = "岗位津贴")
    private BigDecimal allowanceGangw;

    /**
     * 交通补贴
     */
    @Digits(integer = 6, fraction = 3, message = "交通补贴整数位上限为6位，小数位上限为3位")
    @Excel(name = "交通补贴")
    @ApiModelProperty(value = "交通补贴")
    private BigDecimal allowanceJiaot;

    /**
     * 话费补贴
     */
    @Digits(integer = 6, fraction = 3, message = "话费补贴整数位上限为6位，小数位上限为3位")
    @Excel(name = "话费补贴")
    @ApiModelProperty(value = "话费补贴")
    private BigDecimal allowanceHauf;

    /**
     * 差旅补贴
     */
    @Digits(integer = 6, fraction = 3, message = "差旅补贴整数位上限为6位，小数位上限为3位")
    @Excel(name = "差旅补贴")
    @ApiModelProperty(value = "差旅补贴")
    private BigDecimal allowanceChail;

    /**
     * 住房补贴
     */
    @Digits(integer = 6, fraction = 3, message = "住房补贴整数位上限为6位，小数位上限为3位")
    @Excel(name = "住房补贴")
    @ApiModelProperty(value = "住房补贴")
    private BigDecimal allowanceZhuf;

    @Digits(integer = 6, fraction = 3, message = "稳岗补贴整数位上限为6位，小数位上限为3位")
    @Excel(name = "稳岗补贴")
    @ApiModelProperty(value = "稳岗补贴")
    private BigDecimal allowanceWeng;

    @Digits(integer = 6, fraction = 3, message = "特殊工种补贴整数位上限为6位，小数位上限为3位")
    @Excel(name = "特殊工种补贴")
    @ApiModelProperty(value = "特殊工种补贴")
    private BigDecimal allowanceTesgz;

    @Digits(integer = 6, fraction = 3, message = "餐具押金退回整数位上限为6位，小数位上限为3位")
    @Excel(name = "餐具押金退回")
    @ApiModelProperty(value = "餐具押金退回")
    private BigDecimal allowanceCanj;

    @Digits(integer = 6, fraction = 3, message = "计件保底补贴整数位上限为6位，小数位上限为3位")
    @Excel(name = "计件保底补贴")
    @ApiModelProperty(value = "计件保底补贴")
    private BigDecimal allowanceBaod;

    @Digits(integer = 6, fraction = 3, message = "其它计件补贴整数位上限为6位，小数位上限为3位")
    @Excel(name = "其它计件补贴")
    @ApiModelProperty(value = "其它计件补贴")
    private BigDecimal allowanceQitjj;

    /**
     * 办公补贴
     */
    @Digits(integer = 6, fraction = 3, message = "办公补贴整数位上限为6位，小数位上限为3位")
    @Excel(name = "办公补贴")
    @ApiModelProperty(value = "办公补贴")
    private BigDecimal allowanceBang;

    /**
     * 高温补贴
     */
    @Digits(integer = 6, fraction = 3, message = "高温补贴整数位上限为6位，小数位上限为3位")
    @Excel(name = "高温补贴")
    @ApiModelProperty(value = "高温补贴")
    private BigDecimal allowanceGaow;

    /**
     * 餐费补贴
     */
    @Digits(integer = 6, fraction = 3, message = "餐费补贴整数位上限为6位，小数位上限为3位")
    @Excel(name = "餐费补贴")
    @ApiModelProperty(value = "餐费补贴")
    private BigDecimal allowanceCanf;

    /**
     * 其它补贴
     */
    @Digits(integer = 6, fraction = 3, message = "其它补贴整数位上限为6位，小数位上限为3位")
    @Excel(name = "其它补贴")
    @ApiModelProperty(value = "其它补贴")
    private BigDecimal allowanceOther;

    /**
     * 迟到扣款
     */
    @Digits(integer = 6, fraction = 3, message = "迟到扣款整数位上限为6位，小数位上限为3位")
    @Excel(name = "迟到扣款")
    @ApiModelProperty(value = "迟到扣款")
    private BigDecimal deductChid;

    /**
     * 旷工扣款
     */
    @Digits(integer = 6, fraction = 3, message = "旷工扣款整数位上限为6位，小数位上限为3位")
    @Excel(name = "旷工扣款")
    @ApiModelProperty(value = "旷工扣款")
    private BigDecimal deductKuangg;

    /**
     * 责任扣款
     */
    @Digits(integer = 6, fraction = 3, message = "责任扣款整数位上限为6位，小数位上限为3位")
    @Excel(name = "责任扣款")
    @ApiModelProperty(value = "责任扣款")
    private BigDecimal deductDuty;

    /**
     * 餐费扣款
     */
    @Digits(integer = 6, fraction = 3, message = "餐费扣款整数位上限为6位，小数位上限为3位")
    @Excel(name = "餐费扣款")
    @ApiModelProperty(value = "餐费扣款")
    private BigDecimal deductCanf;

    /**
     * 水电费扣款
     */
    @Digits(integer = 6, fraction = 3, message = "水电费扣款整数位上限为6位，小数位上限为3位")
    @Excel(name = "水电费扣款")
    @ApiModelProperty(value = "水电费扣款")
    private BigDecimal deductShuid;

    @Digits(integer = 6, fraction = 3, message = "餐具押金扣款整数位上限为6位，小数位上限为3位")
    @Excel(name = "餐具押金扣款")
    @ApiModelProperty(value = "餐具押金扣款")
    private BigDecimal deductCanj;

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

    /**
     * 其它扣款
     */
    @Digits(integer = 6, fraction = 3, message = "其它扣款整数位上限为6位，小数位上限为3位")
    @Excel(name = "其它扣款")
    @ApiModelProperty(value = "其它扣款")
    private BigDecimal deductOther;

    /**
     * 代缴(医社保)
     */
    @Digits(integer = 6, fraction = 3, message = "代缴(医社保)整数位上限为6位，小数位上限为3位")
    @Excel(name = "代缴(医社保)")
    @ApiModelProperty(value = "代缴(医社保)")
    private BigDecimal feesYisb;

    /**
     * 代缴(公积金)
     */
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

    /**
     * 其它代缴
     */
    @Digits(integer = 6, fraction = 3, message = "其它代缴整数位上限为6位，小数位上限为3位")
    @Excel(name = "其它代缴")
    @ApiModelProperty(value = "其它代缴")
    private BigDecimal feesOther;

    @Excel(name = "工资成本分摊类型")
    @ApiModelProperty(value = "工资成本分摊类型（数据字典或者配置档案键值）")
    private String salaryCostAllocateType;

    @TableField(exist = false)
    @ApiModelProperty(value = "工资成本分摊类型（多选）")
    private String[] salaryCostAllocateTypeList;

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

}
