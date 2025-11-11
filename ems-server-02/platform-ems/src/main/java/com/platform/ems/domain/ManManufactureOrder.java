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

import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


/**
 * 生产订单对象 s_man_manufacture_order
 *
 * @author qhq
 * @date 2021-04-22
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_man_manufacture_order")
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class ManManufactureOrder extends EmsBaseEntity {

    /**
     * 客户端口号
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] manufactureOrderSidList;

    /**
     * 系统自增长ID-生产订单
     */
    @TableId
    @ApiModelProperty(value = "系统自增长ID-生产订单")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long manufactureOrderSid;

    /**
     * 生产订单号
     */
    @Excel(name = "生产订单号")
    @ApiModelProperty(value = "生产订单号")
    private String manufactureOrderCode;

    @NotNull(message = "商品编码不能为空")
    @ApiModelProperty(value = "系统自增长ID-商品&物料&服务")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long materialSid;

    @Excel(name = "商品编码")
    @ApiModelProperty(value = "商品编码")
    private String materialCode;

    @Excel(name = "商品名称")
    @ApiModelProperty(value = "物料（商品/服务）名称")
    private String materialName;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料商品主图片路径")
    private String picturePath;

    @TableField(exist = false)
    @ApiModelProperty(value = "快速编码")
    private String simpleCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "物料SKU档案")
    private Long skuSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料SKU档案")
    private Long[] skuSidList;

    @ApiModelProperty(value = "sku编码")
    private String skuCode;

    @ApiModelProperty(value = "sku类型")
    private String skuType;

    @Excel(name = "排产批次号")
    @ApiModelProperty(value = "排产批次号")
    private String paichanBatch;

    @Excel(name = "颜色")
    @TableField(exist = false)
    @ApiModelProperty(value = "sku名称")
    private String skuName;

    @TableField(exist = false)
    @ApiModelProperty(value = "客户简称")
    private String customerShortName;

    @ApiModelProperty(value = "物料类别（数据字典的键值或配置档案的编码）")
    private String materialCategory;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料类型编码（物料/商品/服务）")
    private String materialType;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料类型名称（物料/商品/服务）")
    private String materialTypeName;

    @NotNull(message = "工厂不能为空")
    @ApiModelProperty(value = "系统自增长ID-工厂")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long plantSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "生产月计划添加明细弹出窗")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long plantSids;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统自增长ID-工厂list")
    private Long[] plantSidList;

    @ApiModelProperty(value = "工厂编码")
    private String plantCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "工厂名称")
    private String plantName;

    @TableField(exist = false)
    @Excel(name = "工厂")
    @ApiModelProperty(value = "工厂名称")
    private String plantShortName;

    /**
     * 产量
     */
    @NotNull(message = "计划产量不能为空")
    @Excel(name = "计划产量")
    @ApiModelProperty(value = "计划产量")
    private BigDecimal quantity;

    @Excel(name = "紧急度", dictType = "s_urgency_type")
    @ApiModelProperty(value = "优先级/紧急程度（数据字典的键值或配置档案的编码）")
    private String producePriority;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "跟进人sid(员工档案)")
    private Long genjinrenSid;

    @ApiModelProperty(value = "跟进人编号(员工档案)")
    private String genjinrenCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "跟进人")
    private String genjinrenName;

    @TableField(exist = false)
    @Excel(name = "跟进人")
    @ApiModelProperty(value = "跟进人")
    private String genjinrenNameCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "跟进人sid(员工档案)")
    private Long[] genjinrenSidList;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "计划投产日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划投产日期")
    private Date planStartDate;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "计划完工日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划完工日期")
    private Date planEndDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "计划完工日期启")
    private String planEndDateBegin;

    @TableField(exist = false)
    @ApiModelProperty(value = "计划完工日期止")
    private String planEndDateEnd;

    @Excel(name = "合同交期")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "合同交期")
    private String contractDate;

    @TableField(exist = false)
    @Excel(name = "计量单位")
    @ApiModelProperty(value = "计量单位名称")
    private String unitBaseName;

    @NotBlank(message = "单据类型不能为空")
    @ApiModelProperty(value = "单据类型编码")
    private String documentType;

    @TableField(exist = false)
    @ApiModelProperty(value = "单据类型编码list")
    private String[] documentTypeList;

    @ApiModelProperty(value = "业务类型")
    private String businessType;

    @TableField(exist = false)
    @ApiModelProperty(value = "业务类型")
    private String[] businessTypeList;

    @TableField(exist = false)
    @ApiModelProperty(value = "业务类型编码")
    private String businessTypeName;

    /**
     * 完工状态（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "完工状态", dictType = "s_complete_status")
    @ApiModelProperty(value = "完工状态（数据字典的键值或配置档案的编码）")
    private String completeStatus;

    @TableField(exist = false)
    @Excel(name = "单据类型")
    @ApiModelProperty(value = "单据类型编码")
    private String documentTypeName;

    @TableField(exist = false)
    @ApiModelProperty(value = "完工状态")
    private String[] completeStatusList;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "工艺路线sid")
    private Long processRouteSid;

    @ApiModelProperty(value = "工艺路线编码")
    private String processRouteCode;

    // @Excel(name = "工艺路线")
    @TableField(exist = false)
    @ApiModelProperty(value = "工艺路线名称")
    private String processRouteName;

    @Excel(name = "录入维度", dictType = "s_progress_dimension")
    @ApiModelProperty(value = "录入维度")
    private String enterDimension;

    @TableField(exist = false)
    @ApiModelProperty(value = "生产进度报表需要控制全部第一道工序不分工厂")
    private String totalShicai;

    @Excel(name = "是否做头缸", dictType = "s_yesno_flag")
    @ApiModelProperty(value = "是否做头缸（数据字典的键值或配置档案的编码）")
    private String isProduceTg;

    @TableField(exist = false)
    @ApiModelProperty(value = "是否做头缸（数据字典的键值或配置档案的编码）")
    private String[] isProduceTgList;

    @TableField(exist = false)
    // @Excel(name = "计划完成量(头缸)")
    @ApiModelProperty(value = "计划产量(头缸)")
    private BigDecimal planQuantityTg;

    @TableField(exist = false)
    // @Excel(name = "计划完成日期(头缸)", width = 30 , dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划完工日期(头缸)")
    private Date planEndDateTg;

    @TableField(exist = false)
    @ApiModelProperty(value = "计划完工日期(头缸)(开始)")
    private String planEndDateTgBegin;

    @TableField(exist = false)
    @ApiModelProperty(value = "计划完工日期(头缸)(结束)")
    private String planEndDateTgEnd;

    @TableField(exist = false)
    // @Excel(name = "完成状态(头缸)", dictType = "s_end_status")
    @ApiModelProperty(value = "完工状态(头缸)（数据字典的键值或配置档案的编码）")
    private String completeStatusTg;

    @TableField(exist = false)
    @ApiModelProperty(value = "完工状态(头缸)（数据字典的键值或配置档案的编码）")
    private String[] completeStatusTgList;

    @TableField(exist = false)
    //  @Excel(name = "实际完成日期(头缸)", width = 30 , dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "实际完工日期(头缸)")
    private Date actualEndDateTg;

    @TableField(exist = false)
    @ApiModelProperty(value = "实际产量(头缸)")
    private BigDecimal actualQuantityTg;

    @Excel(name = "是否做首批", dictType = "s_yesno_flag")
    @ApiModelProperty(value = "是否做首批（数据字典的键值或配置档案的编码）")
    private String isProduceSp;

    @TableField(exist = false)
    @ApiModelProperty(value = "是否做首批（数据字典的键值或配置档案的编码）")
    private String[] isProduceSpList;

    @TableField(exist = false)
    // @Excel(name = "计划完成量(首批)")
    @ApiModelProperty(value = "计划产量(首批)")
    private BigDecimal planQuantitySp;

    @TableField(exist = false)
    // @Excel(name = "计划完成日期(首批)", width = 30 , dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划完工日期(首批)")
    private Date planEndDateSp;

    @TableField(exist = false)
    @ApiModelProperty(value = "计划完工日期(首批)(开始)")
    private String planEndDateSpBegin;

    @TableField(exist = false)
    @ApiModelProperty(value = "计划完工日期(首批)(结束)")
    private String planEndDateSpEnd;

    @TableField(exist = false)
    // @Excel(name = "完成状态(首批)", dictType = "s_end_status")
    @ApiModelProperty(value = "完工状态(首批)（数据字典的键值或配置档案的编码）")
    private String completeStatusSp;

    @TableField(exist = false)
    @ApiModelProperty(value = "完工状态(首批)（数据字典的键值或配置档案的编码）")
    private String[] completeStatusSpList;

    @TableField(exist = false)
    // @Excel(name = "实际完成日期(首批)", width = 30 , dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "实际完工日期(首批)")
    private Date actualEndDateSp;

    @TableField(exist = false)
    @ApiModelProperty(value = "实际产量(首批)")
    private Integer actualQuantitySp;

    /**
     * 入库状态
     */
    @ApiModelProperty(value = "入库状态")
    private String inStoreStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "入库状态list")
    private String[] inStoreStatusList;

    /**
     * 已完工量
     */
    @Digits(integer = 8, fraction = 3, message = "已完工量整数位上限为8位，小数位上限为3位")
    @ApiModelProperty(value = "已完工量")
    private BigDecimal completeQuantity;

    @TableField(exist = false)
    @ApiModelProperty(value = "报表的已完工量")
    private BigDecimal totalCompleteQuantity;

    @TableField(exist = false)
    @ApiModelProperty(value = "待完工量：计划产量-已完工量")
    private BigDecimal daiQuantity;

    /**
     * 入库量
     */
    @Digits(integer = 8, fraction = 3, message = "已入库量整数位上限为8位，小数位上限为3位")
    @ApiModelProperty(value = "入库量")
    private BigDecimal inStoreQuantity;

    @ApiModelProperty(value = "计量单位")
    private String unitBase;

    @TableField(exist = false)
    @ApiModelProperty(value = "计划投产日期从")
    private String planStartBeginDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "计划投产日期至")
    private String planStartEndDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "计划完工日期从")
    private String planEndBeginDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "计划完工日期至")
    private String planEndEndDate;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划下达日期")
    private Date planReleaseDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "计划下达日期从")
    private String planReleaseBeginDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "计划下达日期至")
    private String planReleaseEndDate;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认下达日期")
    private Date actualReleaseDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "确认下达日期从")
    private String actualReleaseBeginDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "确认下达日期至")
    private String actualReleaseEndDate;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "实际投产日期")
    private Date actualStartDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "实际投产日期从")
    private String actualStartBeginDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "实际投产日期至")
    private String actualStartEndDate;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "实际完工日期")
    private Date actualEndDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "实际完工日期从")
    private String actualEndBeginDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "实际完工日期至")
    private String actualEndEndDate;

    /**
     * 当前审批节点名称
     */
    @Excel(name = "当前审批节点")
    @ApiModelProperty(value = "当前审批节点名称")
    @TableField(exist = false)
    private String approvalNode;

    /**
     * 当前审批人
     */
    @Excel(name = "当前审批人")
    @ApiModelProperty(value = "当前审批人")
    @TableField(exist = false)
    private String approvalUserName;

    /**
     * 提交人
     */
    @ApiModelProperty(value = "提交人")
    @TableField(exist = false)
    private String submitUserName;

    /**
     * 提交日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "提交日期")
    @TableField(exist = false)
    private Date submitDate;

    /**
     * 当前审批人id
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "当前审批人id")
    private String approvalUserId;

    /**
     * 当前审批人id
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "当前审批人id")
    private String[] approvalUserIdList;

    /**
     * 处理状态
     */
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态list")
    private String[] handleStatusList;

    /**
     * 单据日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "单据日期")
    private Date documentDate;

    /**
     * 系统SID-公司档案
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-公司档案")
    private Long companySid;

    /**
     * 整单生产方式（数据字典的键值或配置档案的编码），自产、外发
     */
    @ApiModelProperty(value = "整单生产方式（数据字典的键值或配置档案的编码），自产、外发")
    private String productionMode;

    /**
     * 排产维度（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "排产维度（数据字典的键值或配置档案的编码）")
    private String planDimension;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "BOM版本号")
    private Long bomVersionId;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "获取BOM版本的时间")
    private Date bomVersionDateGet;

    @ApiModelProperty(value = "获取BOM版本的用户名称")
    private String bomVersionNameGet;

    @TableField(exist = false)
    @ApiModelProperty(value = "工序sidList")
    private Long[] processSidList;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-关注事项组")
    private Long concernTaskGroupSid;

    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 创建人账号
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    /**
     * 创建人账号
     */
    @TableField(exist = false)
    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccountName;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /**
     * 更新人账号
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccount;

    /**
     * 更新人账号
     */
    @TableField(exist = false)
    @Excel(name = "更改人")
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccountName;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更改日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 确认人账号
     */
    @ApiModelProperty(value = "确认人账号")
    private String confirmerAccount;

    /**
     * 确认时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 数据源系统
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;

    @Excel(name = "到期提醒天数")
    @ApiModelProperty(value = "即将到期提醒天数")
    private Long toexpireDays;

    @TableField(exist = false)
    @ApiModelProperty(value = "即将到期提醒天数")
    private Long toexpireDaysDefalut;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "计划完工日期(初始)", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "初始计划完工日期")
    private Date initialPlanEndDate;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "物料SKU1档案")
    private Long sku1Sid;

    @TableField(exist = false)
    @ApiModelProperty(value = "sku1Name")
    private String sku1Name;

    @TableField(exist = false)
    @ApiModelProperty(value = "sku1Name")
    private String sku2Name;

    @TableField(exist = false)
    @ApiModelProperty(value = "需求量")
    private BigDecimal requireQuantity;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品最大行号")
    private Long productMaxItemNum;

    @TableField(exist = false)
    @ApiModelProperty(value = "工序最大行号")
    private Long ProcessMaxItemNum;

    @TableField(exist = false)
    @ApiModelProperty(value = "用于批量操作")
    private List<Long> manufactureOrderSids;

    @TableField(exist = false)
    @ApiModelProperty(value = "生产订单-组件对象")
    private List<ManManufactureOrderComponent> manManufactureOrderComponentList;

    @Valid
    @TableField(exist = false)
    @ApiModelProperty(value = "生产订单-工序对象")
    private List<ManManufactureOrderProcess> manManufactureOrderProcessList;

    @Valid
    @TableField(exist = false)
    @ApiModelProperty(value = "生产订单-产品明细")
    private List<ManManufactureOrderProduct> manManufactureOrderProductList;

    @Valid
    @TableField(exist = false)
    @ApiModelProperty(value = "生产订单-关注事项对象明细")
    private List<ManManufactureOrderConcernTask> concernTaskList;

    @Valid
    @TableField(exist = false)
    @ApiModelProperty(value = "生产订单-附件")
    private List<ManManufactureOrderAttach> attachmentList;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品-附件")
    private List<BasMaterialAttachment> basMaterialAttachmentList;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品明细汇总")
    private List<ManManufactureOrderProduct> ItemSummaryList;

    @TableField(exist = false)
    @ApiModelProperty(value = "实裁量")
    private BigDecimal isCaichuangQuantity;

    @ApiModelProperty(value = "系统SID-工作中心(班组)")
    @TableField(exist = false)
    private Long[] workCenterSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "分配量")
    private BigDecimal distributionQuantity;


    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-工作中心(班组)")
    @TableField(exist = false)
    private Long workCenterSid;

    /** 物料&商品sku1编码 */
    @ApiModelProperty(value = "物料&商品sku1编码")
    @TableField(exist = false)
    private String sku1Code;

    /** SKU1类型（数据字典的键值或配置档案的编码） */
    @ApiModelProperty(value = "SKU1类型（数据字典的键值或配置档案的编码）")
    @TableField(exist = false)
    private String sku1Type;

    @TableField(exist = false)
    @ApiModelProperty(value = "完工量录入维度（数据字典的键值或配置档案的编码)")
    private String progressDimension;

    @TableField(exist = false)
    @ApiModelProperty(value = "合同交期开始")
    private String contractDateBegin;

    @TableField(exist = false)
    @ApiModelProperty(value = "合同交期结束")
    private String contractDateEnd;
}
