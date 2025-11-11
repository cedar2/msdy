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
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 生产进度日报-明细对象 s_man_day_manufacture_progress_item
 *
 * @author linhongwei
 * @date 2021-06-09
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_man_day_manufacture_progress_item")
public class ManDayManufactureProgressItem extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-生产进度日报单明细
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-生产进度日报单明细")
    private Long dayManufactureProgressItemSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] dayManufactureProgressItemSidList;
    /**
     * 系统SID-生产进度日报单
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-生产进度日报单")
    private Long dayManufactureProgressSid;

    /**
     * 系统SID-生产订单
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-生产订单")
    private Long manufactureOrderSid;

    /**
     * 系统SID-生产订单-工序
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-生产订单-工序")
    private Long manufactureOrderProcessSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-生产订单-工序")
    private Long[] manufactureOrderProcessSidList;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "汇报日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(exist = false)
    @ApiModelProperty(value = "汇报日期")
    private Date documentDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "汇报日期开始")
    private String documentDateBeginTime;

    @TableField(exist = false)
    @ApiModelProperty(value = "汇报日期结束")
    private String documentDateEndTime;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "工厂sid")
    private Long plantSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "工厂sid")
    private Long[] plantSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "工厂编码")
    private String plantCode;

    @Excel(name = "工厂(工序)")
    @TableField(exist = false)
    @ApiModelProperty(value = "工厂名称")
    private String plantName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "工作中心/班组sid")
    private Long workCenterSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "工作中心/班组sid")
    private Long[] workCenterSidList;

    @ApiModelProperty(value = "工作中心/班组编码")
    private String workCenterCode;

    @Excel(name = "班组")
    @TableField(exist = false)
    @ApiModelProperty(value = "工作中心/班组名称")
    private String workCenterName;

    @TableField(exist = false)
    @Excel(name = "操作部门")
    @ApiModelProperty(value = "操作部门名称")
    private String departmentName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "操作部门")
    private Long departmentSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "操作部门")
    private Long[] departmentSidList;

    @ApiModelProperty(value = "操作部门编码")
    private String departmentCode;

    @Excel(name = "录入维度", dictType = "s_progress_dimension")
    @ApiModelProperty(value = "完工量录入维度（数据字典的键值或配置档案的编码)")
    private String progressDimension;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品&服务")
    private Long materialSid;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品&服务")
    private Long productSid;

    @Excel(name = "商品编码")
    @TableField(exist = false)
    @ApiModelProperty(value = "商品编码")
    private String materialCode;

    @Excel(name = "商品名称")
    @TableField(exist = false)
    @ApiModelProperty(value = "商品名称")
    private String materialName;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品条码（物料&商品&服务）")
    private Long barcodeSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品条码")
    private String barcode;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-物料&商品sku")
    private String sku1SidIsNull;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品sku")
    private Long sku1Sid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-物料&商品sku")
    private Long[] sku1SidList;

    @ApiModelProperty(value = "SKU类型（数据字典的键值或配置档案的编码）")
    private String sku1Type;

    @TableField(exist = false)
    @ApiModelProperty(value = "sku1编码")
    private String sku1Code;

    @Excel(name = "颜色")
    @TableField(exist = false)
    @ApiModelProperty(value = "sku1")
    private String sku1Name;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品sku")
    private Long sku2Sid;

    @TableField(exist = false)
    @ApiModelProperty(value = "sku2类型")
    private String sku2Type;

    @TableField(exist = false)
    @ApiModelProperty(value = "sku2编码")
    private String sku2Code;

    @TableField(exist = false)
    @ApiModelProperty(value = "sku2名称")
    private String sku2Name;

    @TableField(exist = false)
    @Excel(name = "排产批次号")
    @ApiModelProperty(value = "排产批次号")
    private Long paichanBatch;

    @Excel(name = "工序名称")
    @TableField(exist = false)
    @ApiModelProperty(value = "工序名称")
    private String processName;

    @Digits(integer = 8, fraction = 3, message = "当天完成量整数位上限为8位，小数位上限为3位")
    @NotNull(message = "当天完成量不能为空")
    @Excel(name = "当天完成量")
    @ApiModelProperty(value = "当天完成量/发料量/收料量")
    private BigDecimal quantity;

    @Digits(integer = 8, fraction = 3, message = "当天接收量(上一工序)整数位上限为8位，小数位上限为3位")
    // @Excel(name = "当天接收量(上一工序)")
    @ApiModelProperty(value = "当天接收量")
    private BigDecimal jieshouQuantity;

    @Digits(integer = 8, fraction = 3, message = "完成量(头缸)整数位上限为8位，小数位上限为3位")
    // @Excel(name = "完成量(头缸)")
    @ApiModelProperty(value = "完成量(头缸)")
    private BigDecimal quantityTg;

    @Digits(integer = 8, fraction = 3, message = "完成量(首批)整数位上限为8位，小数位上限为3位")
    // @Excel(name = "完成量(首批)")
    @ApiModelProperty(value = "完成量(首批)")
    private BigDecimal quantitySp;

    @Excel(name = "单件耗量")
    @Digits(integer = 6, fraction = 4, message = "单件耗量整数位上限为6位，小数位上限为4位")
    @ApiModelProperty(value = "单件耗量")
    private BigDecimal danjianhaoliang;

    @Excel(name = "出货数")
    @Digits(integer = 8, fraction = 3, message = "出货数整数位上限为8位，小数位上限为3位")
    @ApiModelProperty(value = "出货数")
    private BigDecimal chuhuoQuantity;

    @Excel(name = "箱数")
    @Digits(integer = 8, fraction = 3, message = "箱数整数位上限为8位，小数位上限为3位")
    @ApiModelProperty(value = "箱数")
    private BigDecimal xiangshu;

    @Excel(name = "计划产量(工序)")
    @TableField(exist = false)
    @ApiModelProperty(value = "计划产量")
    private BigDecimal platQuantity;

    @Excel(name = "已完成量(工序)")
    @TableField(exist = false)
    @ApiModelProperty(value = "【报表中心】班组生产日报明细报表：已完成量(工序)")
    private String totalCompleteQuantity;

    @Excel(name = "计划产量(整单)")
    @TableField(exist = false)
    @ApiModelProperty(value = "【报表中心】班组生产日报明细报表：计划产量(整单)")
    private String planOrderQuantity;

    @Excel(name = "实裁量")
    @TableField(exist = false)
    @ApiModelProperty(value = "【报表中心】班组生产日报明细报表：实裁量")
    private String isCaichuangQuantity;

    @Digits(integer = 8, fraction = 3, message = "当天外发料量整数位上限为8位，小数位上限为3位")
    @ApiModelProperty(value = "当天外发料量（针对外发加工工序）")
    private BigDecimal issueQuantity;

    @Digits(integer = 8, fraction = 3, message = "当天计划完成量整数位上限为8位，小数位上限为3位")
    @ApiModelProperty(value = "当天计划完成量")
    private BigDecimal planQuantity;

    @ApiModelProperty(value = "处理中次品量")
    private BigDecimal inDefectiveQuantity;

    @ApiModelProperty(value = "基本计量单位（数据字典的键值或配置档案的编码）")
    private String unitBase;

    @TableField(exist = false)
    @Excel(name = "计量单位")
    @ApiModelProperty(value = "基本计量单位名称")
    private String unitBaseName;

    @ApiModelProperty(value = "计薪完工类型（数据字典的键值或配置档案的编码）")
    private String completeType;

    @TableField(exist = false)
    @ApiModelProperty(value = "计薪完工类型（数据字典的键值或配置档案的编码）")
    private String jixinWangongType;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品工价类型（数据字典的键值或配置档案的编码）")
    private String productPriceType;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "计划完成日期(工序)", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(exist = false)
    @ApiModelProperty(value = "计划完成日期(工序)")
    private Date planEndDate;

    @Excel(name = "生产订单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "生产订单号")
    private Long manufactureOrderCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "完工量录入维度（数据字典的键值或配置档案的编码)")
    private String[] progressDimensionList;

    @Excel(name = "行号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "行号")
    private Long itemNum;

    @ApiModelProperty(value = "图片路径")
    private String picturePath;

    @TableField(exist = false)
    @ApiModelProperty(value = "图片路径，可放多个链接，每个链接用”;“隔开")
    private String[] picturePathList;

    @ApiModelProperty(value = "视频路径")
    private String videoPath;

    @TableField(exist = false)
    @ApiModelProperty(value = "视频路径，可放多个链接，每个链接用”;“隔开")
    private String[] videoPathList;

    @TableField(exist = false)
    @ApiModelProperty(value = "生产订单工序序号")
    private String progressSerialNum;

    @Excel(name = "工序行号")
    @TableField(exist = false)
    @ApiModelProperty(value = "工序行号")
    private String processItemNum;

    @Excel(name = "处理状态", dictType = "s_handle_status")
    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "是否第一个工序（数据字典的键值或配置档案的编码）")
    private String isFirstProcess;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态")
    private String[] handleStatusList;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    @TableField(exist = false)
    @ApiModelProperty(value = "汇报人")
    private String reporter;

    @TableField(exist = false)
    @ApiModelProperty(value = "汇报人list")
    private String[] reporterList;

    @Excel(name = "汇报人")
    @TableField(exist = false)
    @ApiModelProperty(value = "汇报人名称")
    private String reporterName;

    @TableField(exist = false)
    @Excel(name = "班组生产日报编号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "生产进度日报单号")
    private Long dayManufactureProgressCode;

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    @TableField(exist = false)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String[] creatorAccountList;

    @Excel(name = "创建人")
    @TableField(exist = false)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccountName;

    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-工序")
    private Long processSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统自增长ID-工序")
    private Long[] processSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "工序生产方式（数据字典的键值或配置档案的编码），自产、外发")
    private String productionMode;

    @TableField(exist = false)
    @ApiModelProperty(value = "已完成量")
    private BigDecimal currentCompleteQuantity;

    @TableField(exist = false)
    @ApiModelProperty(value = "工序批次号")
    private Long processBatchNum;

    @TableField(exist = false)
    @ApiModelProperty(value = "完成状态")
    private String endStatus;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-道序")
    private Long processStepSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "所属年月")
    private String yearmonth;

    @TableField(exist = false)
    @ApiModelProperty(value = "生产进度日报-完工明细对象")
    private List<ManDayManufactureProgressDetail> progressDetailList;

    /**
     * wp 未完成量(计划)：未完成量(计划) = 计划产量(工序) - 已完成量(工序)
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "未完成量(计划)")
    private BigDecimal planUnfinishedQuantity;

    /**
     * wp 未完成量(实裁)：未完成量(实裁) = 实裁量 - 已完成量(工序)
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "未完成量(实裁)")
    private BigDecimal shicaiUnfinishedQuantity;

    @TableField(exist = false)
    @ApiModelProperty(value = "分配量")
    private BigDecimal quantityFenpei;

    @TableField(exist = false)
    @ApiModelProperty(value = "生产订单工序的特殊工序标识,用来校验箱数字段")
    private String specialFlag;

    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "周计划日期起")
    private Date dateStart;

}
