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
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 生产订单-工序对象 s_man_manufacture_order_process
 *
 * @author qhq
 * @date 2021-04-13
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_man_manufacture_order_process")
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class ManManufactureOrderProcess extends EmsBaseEntity {
    /**
     * 客户端口号
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /**
     * 系统自增长ID-生产订单-工序
     */
    @TableId
    @ApiModelProperty(value = "系统自增长ID-生产订单-工序")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long manufactureOrderProcessSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统自增长ID-生产订单-工序(多选)")
    private Long[] manufactureOrderProcessSidList;

    /**
     * 系统自增长ID-生产订单
     */
    @ApiModelProperty(value = "系统自增长ID-生产订单")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long manufactureOrderSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统自增长ID-生产订单多选")
    private Long[] manufactureOrderSidList;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "生产订单工艺路线sid")
    private Long processRouteSid;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "计划完工日期(整单)", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划完工日期")
    @TableField(exist = false)
    private Date planEndDateHead;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-商品&物料&服务")
    private Long materialSid;

    @Excel(name = "商品编码")
    @ApiModelProperty(value = "商品编码")
    @TableField(exist = false)
    private String materialCode;

    /**
     * 物料（商品/服务）名称
     */
    @TableField(exist = false)
    @Excel(name = "商品名称")
    @ApiModelProperty(value = "物料（商品/服务）名称")
    private String materialName;

    @Excel(name = "排产批次号")
    @TableField(exist = false)
    @ApiModelProperty(value = "排产批次号")
    private Long paichanBatch;

    @TableField(exist = false)
    @Excel(name = "颜色")
    @ApiModelProperty(value = "sku1名称")
    private String sku1Name;

    @TableField(exist = false)
    @ApiModelProperty(value = "完工确认状态")
    private String[] completeStatusList;

    @Excel(name = "完工状态", dictType = "s_complete_status")
    @TableField(exist = false)
    @ApiModelProperty(value = "完工状态")
    private String completeStatus;

    @ApiModelProperty(value = "系统自增长ID-工厂")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long plantSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统自增长ID-工厂list")
    private Long[] plantSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "工厂编码(工序)")
    private String plantCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "工厂名称(工序)")
    private String plantName;

    @TableField(exist = false)
    @Excel(name = "工厂(工序)")
    @ApiModelProperty(value = "工厂简称(工序)")
    private String plantShortName;

    @ApiModelProperty(value = "系统自增长ID-工作中心/班组")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long workCenterSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统自增长ID-工作中心/班组list")
    private Long[] workCenterSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "工作中心/班组编码")
    private String workCenterCode;

    @Excel(name = "班组")
    @TableField(exist = false)
    @ApiModelProperty(value = "工作中心/班组名称")
    private String workCenterName;

    @NotNull(message = "工序不能为空")
    @ApiModelProperty(value = "系统自增长ID-工序")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long processSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统自增长ID-工序list")
    private Long[] processSidList;

    @Excel(name = "工序名称")
    @ApiModelProperty(value = "工序名称")
    private String processName;

    @ApiModelProperty(value = "责任人sid(员工档案)多选存值")
    private String directorSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "责任人sid(员工档案)多选")
    private String[] directorSidList;

    @ApiModelProperty(value = "责任人编码(员工档案)")
    private String directorCode;

    @TableField(exist = false)
    @Excel(name = "负责人")
    @ApiModelProperty(value = "责任人名称")
    private String directorName;

    @TableField(exist = false)
    @Excel(name = "待完成量(计划)", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "待完成量(计划)：计划产量-已完成量")
    private BigDecimal daiQuantity;

    @TableField(exist = false)
    @Excel(name = "待完成量(实裁)", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "待完成量(实裁)：实裁量 - 已完成量(工序)")
    private BigDecimal daiShicaiQuantity;

    @TableField(exist = false)
    @Excel(name = "已完成量(工序)", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "【报表中心】生产订单工序明细报表：已完成量(工序)")
    private BigDecimal totalCompleteQuantityProcess;

    @TableField(exist = false)
    @ApiModelProperty(value = "【报表中心】生产订单工序明细报表：已完成量(工序)")
    private BigDecimal totalCompleteQuantity;

    @Digits(integer = 8, fraction = 3, message = "计划产量整数位上限为8位，小数位上限为3位")
    @Excel(name = "计划产量(工序)")
    @ApiModelProperty(value = "产量（此工厂/此工作中心/班组/此道工序负责生产的产量）")
    private BigDecimal quantity;

    @Digits(integer = 8, fraction = 3, message = "已完成量整数位上限为8位，小数位上限为3位")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "已完工量")
    private Long currentCompleteQuantity;

    @Excel(name = "计划产量(整单)")
    @TableField(exist = false)
    @ApiModelProperty(value = "【报表中心】班组生产日报明细报表：计划产量(整单)")
    private String planOrderQuantity;

    @Excel(name = "实裁量")
    @TableField(exist = false)
    @ApiModelProperty(value = "实裁量")
    private BigDecimal isCaichuangQuantity;

    @TableField(exist = false)
    @ApiModelProperty(value = "溢出/短少量")
    private BigDecimal dvalue;

    @TableField(exist = false)
    @ApiModelProperty(value = "接收量（此工厂/此工作中心/班组/此道工序从上游接收的量汇总）")
    private BigDecimal jieshouQuantity;

    @TableField(exist = false)
    @ApiModelProperty(value = "头缸量")
    private BigDecimal touGangQuantity;

    @TableField(exist = false)
    @ApiModelProperty(value = "首批量")
    private BigDecimal shouPiQuantity;

    @TableField(exist = false)
    @ApiModelProperty(value = "计划产量（整单）")
    private BigDecimal planTotalQuantity;

    @TableField(exist = false)
    @ApiModelProperty(value = "未完成量")
    private BigDecimal notCompleteQuantity;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "计划开始日期(工序)", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划开始日期")
    private Date planStartDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "计划开始日期(起)")
    private String planStartDateBegin;

    @TableField(exist = false)
    @ApiModelProperty(value = "计划开始日期(止)")
    private String planStartDateEnd;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "计划完成日期(工序)", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划完成日期")
    private Date planEndDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "计划完成日期启")
    private String planEndDateBegin;

    @TableField(exist = false)
    @ApiModelProperty(value = "计划完成日期止")
    private String planEndDateEnd;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "初始计划完成日期")
    private Date initialPlanEndDate;

    @Excel(name = "完成状态(工序)", dictType = "s_end_status")
    @ApiModelProperty(value = "完成状态（工序）")
    private String endStatus;

    @ApiModelProperty(value = "完成状态（工序）")
    @TableField(exist = false)
    private String[] endStatusList;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-供应商信息")
    private Long vendorSid;

    @TableField(exist = false)
    @Excel(name = "外发加工商")
    @ApiModelProperty(value = "外发加工商(班组的供应商)")
    private String workVendorShortName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "实际开始日期")
    private Date actualStartDate;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "实际完成日期(工序)", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "实际完成日期")
    private Date actualEndDate;

    @Excel(name = "进度说明")
    @ApiModelProperty(value = "进度说明")
    private String comment;

    /** 是否做头缸（数据字典的键值或配置档案的编码） */
    // @Excel(name = "是否做头缸", dictType = "sys_yes_no")
    @ApiModelProperty(value = "是否做头缸（数据字典的键值或配置档案的编码）")
    private String isProduceTg;

    /** 计划完成日期(头缸) */
    // @Excel(name = "计划完成日期(头缸)", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划完成日期(头缸)")
    private Date planStartDateTg;

    /** 实际完成日期(头缸) */
    // @Excel(name = "实际完成日期(头缸)", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "实际完成日期(头缸)")
    private Date actualEndDateTg;

    /** 是否做首批（数据字典的键值或配置档案的编码） */
    // @Excel(name = "是否做首批", dictType = "sys_yes_no")
    @ApiModelProperty(value = "是否做首批（数据字典的键值或配置档案的编码）")
    private String isProduceSp;

    /** 计划完成日期(首批) */
    // @Excel(name = "计划完成日期(首批)", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划完成日期(首批)")
    private Date planStartDateSp;

    /** 实际完工日期(首批) */
    // @Excel(name = "实际完成日期(首批)", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "实际完成日期(首批)")
    private Date actualEndDateSp;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-工厂(整单)")
    private Long orderPlantSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统自增长ID-工厂list(整单)")
    private Long[] orderPlantSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "工厂编码(整单)")
    private String orderPlantCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "工厂名称(整单)")
    private String orderPlantName;

    @TableField(exist = false)
    @Excel(name = "工厂(整单)")
    @ApiModelProperty(value = "工厂简称(整单)")
    private String orderPlantShortName;

    @TableField(exist = false)
    @ApiModelProperty(value = "合同交期订单")
    private String contractDate;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    @Excel(name = "特殊工序标识", dictType = "s_special_flag")
    @ApiModelProperty(value = "特殊工序标识")
    private String specialFlag;

    @Excel(name = "工序编码")
    @ApiModelProperty(value = "工序编码")
    @TableField(exist = false)
    private String processCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "操作部门SID")
    private Long departmentSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "操作部门SID")
    private Long[] departmentSidList;

    @ApiModelProperty(value = "操作部门编码")
    private String departmentCode;

    @TableField(exist = false)
    @Excel(name = "操作部门")
    @ApiModelProperty(value = "操作部门名称")
    private String departmentName;

    @TableField(exist = false)
    private String[] departmentList;

    @Excel(name = "里程碑", dictType = "s_manufacture_milestone")
    @ApiModelProperty(value = "里程碑（数据字典的键值或配置档案的编码）")
    private String milestone;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "序号")
    private BigDecimal serialNumDecimal;

    @Excel(name = "工序序号")
    @ApiModelProperty(value = "序号")
    private String serialNum;

    public void setSerialNum(String serialNum) {
        DecimalFormat df = new DecimalFormat("####.###");
        try {
            serialNum = df.format(new BigDecimal(serialNum));
        }catch (Exception e) {
            serialNum = null;
        }
        this.serialNum = serialNum;
    }

    public String getSerialNum() {
        DecimalFormat df = new DecimalFormat("####.###");
        BigDecimal serial = null;
        try {
            serial = new BigDecimal(serialNum);
        } catch (Exception e) {
            return null;
        }
        return df.format(serial);
    }

    @TableField(exist = false)
    @ApiModelProperty(value = "工序的序号")
    private BigDecimal processSerialNum;

    @ApiModelProperty(value = "工序行号")
    private Long itemNum;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "工序批次号")
    private Long processBatchNum;

    @ApiModelProperty(value = "是否第一个工序（数据字典的键值或配置档案的编码）")
    private String isFirstProcess;

    @ApiModelProperty(value = "是否标志阶段完成的工序（数据字典的键值或配置档案的编码）")
    private String isStageComplete;

    @ApiModelProperty(value = "是否标志成品完工的工序（数据字典的键值或配置档案的编码）")
    private String isProduceComplete;

    @ApiModelProperty(value = "是否开始")
    private String startFlag;

    @ApiModelProperty(value = "是否完成")
    private String endFlag;

    @ApiModelProperty(value = "是否取消")
    private String cancelFlag;

    @ApiModelProperty(value = "工序生产方式（数据字典的键值或配置档案的编码），自产、外发")
    private String productionMode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "完成量校验参考工序sid")
    private Long quantityReferProcessSid;

    @ApiModelProperty(value = "完成量校验参考工序编码")
    private String quantityReferProcessCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "完成量校验参考工序名称")
    private String quantityReferProcessName;

    @ApiModelProperty(value = "参考工序所引用数量类型（数据字典的键值：s_quantity_type_refer_process）")
    private String quantityTypeReferProcess;

    @Excel(name = "到期提醒天数")
    @ApiModelProperty(value = "即将到期预警天数(生产订单-工序) ")
    private Integer toexpireDaysScddGx;

    @TableField(exist = false)
    @ApiModelProperty(value = "即将到期预警天数(生产订单-事项) ")
    private Integer toexpireDaysScdd;

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

    /**
     * 创建人账号
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    @TableField(exist = false)
    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccountName;


    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建日期")
    private Date createDate;

    @Excel(name = "生产订单号")
    @ApiModelProperty(value = "生产订单号")
    private String manufactureOrderCode;

    @Excel(name = "处理状态", dictType = "s_handle_status")
    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态list")
    private String[] handleStatusList;

    /**
     * 更新人账号
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccount;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 数据源系统
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;

    @TableField(exist = false)
    @ApiModelProperty(value = "预警：红色0绿色1黄橙2蓝色3")
    private String light;

    /**
     * 系统SID-销售订单
     */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-销售订单")
    private Long salesOrderSid;

    /**
     * 销售订单号
     */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售订单号")
    private String salesOrderCode;

    /**
     * 系统自增长ID-客户信息
     */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-客户信息")
    private Long customerSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "客户名称")
    private String customerName;

    @TableField(exist = false)
    @ApiModelProperty(value = "客户编码")
    private String customerCode;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-商品条码")
    private Long barcodeSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品条码")
    private String barcode;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-商品sku1")
    private Long sku1Sid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统自增长ID-商品sku1")
    private Long[] sku1SidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "sku1类型")
    private String sku1Type;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-商品sku2")
    private Long sku2Sid;

    @TableField(exist = false)
    @ApiModelProperty(value = "sku2名称")
    private String sku2Name;

    @TableField(exist = false)
    @ApiModelProperty(value = "sku2类型")
    private String sku2Type;

    /**
     * 生产类别编码list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "生产类别编码list")
    private String[] productionCategoryList;

    /**
     * 系统自增长ID-客户信息list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "系统自增长ID-客户信息list")
    private Long[] customerSidList;

    /**
     * 是否完成list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "是否完成list")
    private String[] endFlagList;

    /**
     * 计划完成日期从
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "计划完成日期从")
    private String planEndBeginDate;

    /**
     * 计划完成日期至
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "计划完成日期至")
    private String planEndEndDate;

    /**
     * 计量单位
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "基本计量单位")
    private String unitBase;

    /**
     * 计量单位名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "计量单位名称")
    private String unitBaseName;

    @TableField(exist = false)
    private String updaterAccountName;
    /**
     * 系统自增长ID-公司
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "系统自增长ID-公司")
    private Long companySid;

    @TableField(exist = false)
    @ApiModelProperty(value = "生产方式")
    private String[] productionModeList;

    @TableField(exist = false)
    private List<ManManufactureOrderProcess> datas;

    @TableField(exist = false)
    private Map<String, Integer> reportMap;

    @TableField(exist = false)
    @ApiModelProperty(value = "加工采购价明细信息")
    private PurOutsourcePurchasePriceItem purOutsourcePurchasePriceItem;

    @TableField(exist = false)
    @ApiModelProperty(value = "结算价格")
    private BigDecimal priceTax;

    @TableField(exist = false)
    @ApiModelProperty(value = "税率")
    private BigDecimal taxRate;

    @TableField(exist = false)
    @ApiModelProperty(value = "工序计划产量校验提示语")
    private String message;

    @TableField(exist = false)
    @ApiModelProperty(value = "完工量录入维度（数据字典的键值或配置档案的编码)")
    private String progressDimension;

    @TableField(exist = false)
    @ApiModelProperty(value = "周计划录入维度")
    private String enterDimension;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-生产订单sku_sid")
    private Long skuSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "生产订单sku")
    private String skuName;

    /** 物料&商品sku1编码 */
    @ApiModelProperty(value = "物料&商品sku1编码")
    @TableField(exist = false)
    private String sku1Code;

    /**
     * wp 未完成量(计划)：未完成量(计划) = 计划产量(工序) - 已完成量(工序)
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "未完成量(计划)")
    private BigDecimal planUnfinishedQuantity;

    /**
     * wp 未完成量(分配)：未完成量(分配) = 分配量 - 已完成量(工序)
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "未完成量(分配)")
    private BigDecimal shicaiUnfinishedQuantity;

    /**
     * 班组生产日报明细添加 的 分配量
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "分配量")
    private BigDecimal quantityFenpei;

}
