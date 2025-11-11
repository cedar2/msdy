package com.platform.ems.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

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
 * 计薪量申报-明细对象 s_pay_process_step_complete_item
 *
 * @author linhongwei
 * @date 2021-09-08
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_pay_process_step_complete_item")
public class PayProcessStepCompleteItem extends EmsBaseEntity {

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-计薪量申报明细信息")
    private Long stepCompleteItemSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] stepCompleteItemSidList;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-计薪量申报单")
    private Long stepCompleteSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "计薪量申报单号")
    private Long stepCompleteCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "员工账号sid")
    private Long workerSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "员工账号sid")
    private Long[] workerSidList;

    @Excel(name = "员工号")
    @ApiModelProperty(value = "员工号")
    private String workerCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "员工姓名")
    private String workerName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "生产订单sid")
    private Long manufactureOrderSid;

    @Excel(name = "生产订单号")
    @ApiModelProperty(value = "生产订单号")
    private String manufactureOrderCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "班组sid")
    private Long workCenterSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "商品sid")
    private Long productSid;

    @Excel(name = "商品编码")
    @ApiModelProperty(value = "商品编码")
    private String productCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品名称")
    private String productName;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品名称")
    private String materialName;

    @Excel(name = "商品道序sid(商品道序明细表中的sid)")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "商品道序sid(商品道序明细表中的sid)")
    private Long processStepItemSid;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "道序sid(商品道序表中的sid)")
    private Long processStepSid;

    @Excel(name = "商品道序编码")
    @ApiModelProperty(value = "商品道序编码")
    private String processStepCode;

    @Excel(name = "商品道序名称")
    @ApiModelProperty(value = "商品道序名称")
    private String processStepName;

    @Digits(integer = 8, fraction = 3, message = "计薪量整数位上限为8位，小数位上限为3位")
    @NotNull(message = "计薪量不能为空")
    @Excel(name = "计薪量")
    @ApiModelProperty(value = "当天计薪量")
    private BigDecimal completeQuantity;

    @ApiModelProperty(value = "当天计薪量（系统自动生成）")
    private BigDecimal completeQuantitySys;

    @TableField(exist = false)
    @Excel(name = "计薪完工类型")
    @ApiModelProperty(value = "计薪完工类型（数据字典的键值或配置档案的编码）")
    private String jixinWangongType;

    @TableField(exist = false)
    @ApiModelProperty(value = "计薪完工类型(多选)")
    private String[] jixinWangongTypeList;

    @Digits(integer = 5, fraction = 4, message = "道序工价整数位上限为5位，小数位上限为4位")
    @ApiModelProperty(value = "道序工价(元)")
    private BigDecimal price;

    @Digits(integer = 2, fraction = 3, message = "倍率(道序)整数位上限为2位，小数位上限为3位")
    @ApiModelProperty(value = "倍率(道序)")
    private BigDecimal priceRate;

    @Digits(integer = 2, fraction = 3, message = "完工调价倍率整数位上限为2位，小数位上限为3位")
    @ApiModelProperty(value = "完工调价倍率(道序)")
    private BigDecimal wangongPriceRate;

    @Excel(name = "排产批次号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "排产批次号")
    private Long paichanBatch;

    @TableField(exist = false)
    @ApiModelProperty(value = "排产批次号是否精确查询")
    private String isPaichanPre;

    @TableField(exist = false)
    @ApiModelProperty(value = "录入方式(数据字典)")
    private String enterMode;

    @Excel(name = "行号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "行号")
    private Long itemNum;

    @Digits(integer = 4, fraction = 2, message = "序号整数位上限为4位，小数位上限为2位")
    @Excel(name = "序号(商品道序)")
    @ApiModelProperty(value = "序号(商品道序)")
    private BigDecimal sort;

    @Excel(name = "创建人账号（用户名称）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @Excel(name = "更新人账号（用户名称）")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    @Excel(name = "数据源系统（数据字典的键值或配置档案的编码）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "所属生产工序sid")
    private Long processSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "所属生产工序编码")
    private String processCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "所属生产工序名称")
    private String processName;

    @TableField(exist = false)
    @ApiModelProperty(value = "已计薪量")
    private BigDecimal cumulativeQuantity;

    @TableField(exist = false)
    @ApiModelProperty(value = "工序计划产量")
    private BigDecimal processQuantity;

    @TableField(exist = false)
    @ApiModelProperty(value = "所属年月")
    private String yearmonth;

    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "申报日期")
    private Date reportDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "道序类别（数据字典的键值或配置档案的编码）")
    private String stepCategory;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品工价类型（数据字典的键值或配置档案的编码）")
    private String productPriceType;

    /*
     * 计薪量申报*道序工价*道序倍率（保留2位小数）
     */
    @TableField(exist = false)
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "金额(元)")
    private BigDecimal money;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "完成量校验参考工序sid")
    private Long quantityReferProcessSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "完成量校验参考工序编码")
    private String quantityReferProcessCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "完成量校验参考工序名称")
    private String quantityReferProcessName;

    @TableField(exist = false)
    @ApiModelProperty(value = "参考工序所引用数量类型（数据字典的键值：s_quantity_type_refer_process）")
    private String quantityTypeReferProcess;

    @TableField(exist = false)
    @ApiModelProperty(value = "参考工序校验量")
    private String quantityReferProcess;

    @TableField(exist = false)
    @ApiModelProperty(value = "操作部门（数据字典的键值或配置档案的编码）")
    private String department;

    @TableField(exist = false)
    @ApiModelProperty(value = "操作部门（数据字典的键值或配置档案的编码）")
    private String departmentName;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "工厂")
    private Long plantSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "工厂编码")
    private String plantCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "工厂名称")
    private String plantName;

    @TableField(exist = false)
    @ApiModelProperty(value = "工厂简称")
    private String plantShortName;

    @TableField(exist = false)
    @ApiModelProperty(value = "实裁量")
    private BigDecimal shicaiQuantity;

}
