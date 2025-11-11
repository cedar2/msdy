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
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 生产周计划-明细对象 s_man_week_manufacture_plan_item
 *
 * @author c
 * @date 2021-07-16
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_man_week_manufacture_plan_item")
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class ManWeekManufacturePlanItem extends EmsBaseEntity {

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-生产周计划单明细
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-生产周计划单明细")
    private Long weekManufacturePlanItemSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] weekManufacturePlanItemSidList;
    /**
     * 系统SID-生产周计划单
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-生产周计划单")
    private Long weekManufacturePlanSid;

    /**
     * 系统SID-生产订单
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-生产订单")
    private Long manufactureOrderSid;

    /**
     * 生产周计划单号
     */
    @TableField(exist = false)
    @Excel(name = "生产周计划编号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "生产周计划单号")
    private Long weekManufacturePlanCode;

    /**
     * 工厂名称
     */
    @Excel(name = "工厂")
    @TableField(exist = false)
    @ApiModelProperty(value = "工厂（工序）")
    private String plantName;

    /**
     * 工作中心/班组名称
     */
    @Excel(name = "工作中心/班组")
    @TableField(exist = false)
    @ApiModelProperty(value = "工作中心/班组名称")
    private String workCenterName;

    @ApiModelProperty(value = "系统SID-工作中心(班组)")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long workCenterSid ;

    @ApiModelProperty(value = "系统code-工作中心(班组)")
    @JsonSerialize(using = ToStringSerializer.class)
    private String workCenterCode ;

    /**
     * 周计划日期(起)
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "周计划日期(起)", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(exist = false)
    @ApiModelProperty(value = "周计划日期(起)")
    private Date dateStart;

    /**
     * 周计划日期(至)
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "周计划日期(至)", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(exist = false)
    @ApiModelProperty(value = "周计划日期(至)")
    private Date dateEnd;

    /**
     * 生产订单号
     */
    @Excel(name = "生产订单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "生产订单号")
    private Long manufactureOrderCode;


    /**
     * 商品名称
     */
    @Excel(name = "商品名称")
    @TableField(exist = false)
    @ApiModelProperty(value = "商品名称")
    private String materialName;

    /**
     * 工序名称
     */
    @Excel(name = "工序名称")
    @TableField(exist = false)
    @ApiModelProperty(value = "工序名称")
    private String processName;

    /**
     * 生产批次号
     */
    @Excel(name = "生产批次")
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "生产批次号")
    private Long productionBatchNum;

    /**
     * 计划产量
     */
    @Excel(name = "分配量")
    @TableField(exist = false)
    @ApiModelProperty(value = "分配量")
    private BigDecimal quantity;

    /**
     * 已完成量
     */
    @Excel(name = "已完成量")
    @TableField(exist = false)
    @ApiModelProperty(value = "已完成量")
    private BigDecimal currentCompleteQuantity;


    /**
     * 基本计量单位
     */
    @Excel(name = "计量单位")
    @TableField(exist = false)
    @ApiModelProperty(value = "基本计量单位")
    private String unitBaseName;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 计划完成日期(工序)
     */
    @Excel(name = "计划完成日期(工序)", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划完成日期(工序)")
    private Date planEndDate;

    @Excel(name = "计划开始日期(工序)", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划开始日期(工序)")
    private Date planStartDate;

    /**
     * 完成状态
     */
    @Excel(name = "完成状态")
    @TableField(exist = false)
    @ApiModelProperty(value = "完成状态")
    private String endStatus;

    /**
     * 行号
     */
    @Excel(name = "行号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "行号")
    private Long itemNum;

    /**
     * 工序序号
     */
    @Excel(name = "工序序号")
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "工序序号")
    private Double serialNum;

    /**
     * 创建人账号（用户名称）
     */
    @Excel(name = "创建人")
    @TableField(exist = false)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccountName;

    /**
     * 基本计量单位（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "基本计量单位（数据字典的键值或配置档案的编码）")
    private String unitBase;

    /**
     * 创建人账号（用户名称）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /**
     * 更新人账号（用户名称）
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    /**
     * 工厂sid
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "工厂sid")
    private Long[] plantSidList;

    /**
     * 工作中心/班组sid
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "工作中心/班组sid")
    private Long[] workCenterSidList;

    /**
     * 系统自增长ID-工序
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "工序sid")
    private Long[] processSidList;

    /**
     * 创建人账号（用户名称）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String[] creatorAccountList;

    /**
     * 处理状态（数据字典的键值或配置档案的编码）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String[] handleStatusList;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "工序行号")
    private Long processItemNum;

    @TableField(exist = false)
    @ApiModelProperty(value = "生产所属阶段名称")
    private String produceStageName;


    @ApiModelProperty(value = "生产订单-工序总览行号")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long manufactureOrderProcessItemNum;

    @ApiModelProperty(value = "生产订单-工序总览行sid")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long manufactureOrderProcessSid;
    /**
     * 生产日计划-明细对象
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "生产日计划-明细对象")
    private List<ManDayManufacturePlanItem> manDayManufacturePlanItemList;

    @ApiModelProperty(value = "查询：生产所属阶段")
    @TableField(exist = false)
    private String[] produceStageList;

    @Excel(name = "操作部门")
    @ApiModelProperty(value = "操作部门")
    @TableField(exist = false)
    private String departmentName;

    @TableField(exist = false)
    @ApiModelProperty(value = "部门查询")
    private String[] departmentList;

    /** 商品sid */
    @Excel(name = "商品sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "商品sid")
    private Long materialSid;

    /** 商品编码 */
    @Excel(name = "商品编码")
    @ApiModelProperty(value = "商品编码")
    private String materialCode;

    /** 排产批次号 */
    @Excel(name = "排产批次号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "排产批次号")
    private Long paichanBatch;

    /** 分配车缝组 */
    @Excel(name = "分配车缝组")
    @ApiModelProperty(value = "分配车缝组")
    private String fenpeiChefengzu;

    /** 开始日期(车间)/车间上线日期 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "开始日期(车间)/车间上线日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "开始日期(车间)/车间上线日期")
    private Date startDateChejian;

    /** 计划送印绣花日期 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "计划送印绣花日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划送印绣花日期")
    private Date planDateSongyxh;

    /** 计划收印绣花日期 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "计划收印绣花日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划收印绣花日期")
    private Date planDateShouyxh;

    /** 印绣部位 */
    @Excel(name = "印绣部位")
    @ApiModelProperty(value = "印绣部位")
    private String positionXiuyin;

    /** 计划报单日期 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "计划报单日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划报单日期")
    private Date planDateBaodan;

    /** 计划出货数 */
    @Excel(name = "计划出货数")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "计划出货数")
    private Long planQuantityChuh;

    /** 完成日期(车间)/车间下线日期 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "完成日期(车间)/车间下线日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "完成日期(车间)/车间下线日期")
    private Date endDateChejian;

    /** 进度说明 */
    @Excel(name = "进度说明")
    @ApiModelProperty(value = "进度说明")
    private String jinduRemark;

    /** 进度说明(头缸) */
    @Excel(name = "进度说明(头缸)")
    @ApiModelProperty(value = "进度说明(头缸)")
    private String jinduRemarkTg;

    /** 生产订单-工序编码 */
    @Excel(name = "生产订单-工序编码")
    @ApiModelProperty(value = "生产订单-工序编码")
    private String manufactureOrderProcessCode;

    /** 进度说明(首批) */
    @Excel(name = "进度说明(首批)")
    @ApiModelProperty(value = "进度说明(首批)")
    private String jinduRemarkSp;


    @Excel(name = "实裁量")
    @TableField(exist = false)
    @ApiModelProperty(value = "实裁量")
    private BigDecimal isCaichuangQuantity;

    @Excel(name = "头缸量")
    @TableField(exist = false)
    @ApiModelProperty(value = "头缸量")
    private BigDecimal touGangQuantity;


    @Excel(name = "首批量")
    @TableField(exist = false)
    @ApiModelProperty(value = "首批量")
    private BigDecimal shouPiQuantity;

    @Excel(name = "计划产量（整单）")
    @TableField(exist = false)
    @ApiModelProperty(value = "计划产量（整单）")
    private BigDecimal planTotalQuantity;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "商品sid")
    @TableField(exist = false)
    private Long plantSid;

    @Excel(name = "未完成量")
    @TableField(exist = false)
    @ApiModelProperty(value = "未完成量")
    private BigDecimal notCompleteQuantity;


    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long processSid;

    /** 送洗水数 */
    @Excel(name = "送洗水数")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "送洗水数")
    private Long quantitySongxs;

    /** 收洗水数 */
    @Excel(name = "收洗水数")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "收洗水数")
    private Long quantityShouxs;

    @TableField(exist = false)
    @ApiModelProperty(value = "小计")
    private BigDecimal sumQuantity;


    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "计划完工日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划完工日期")
    @TableField(exist = false)
    private Date planEndDateHead;


    @Excel(name = "已完成量(工序)")
    @TableField(exist = false)
    @ApiModelProperty(value = "已完成量(工序)")
    private BigDecimal totalCompleteQuantity;



    @ApiModelProperty(value = "系统SID-物料&商品sku1")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long sku1Sid;

    @ApiModelProperty(value = "物料&商品sku1编码")
    @TableField(exist = false)
    private String sku1Name;

    /** 物料&商品sku1编码 */
    @Excel(name = "物料&商品sku1编码")
    @ApiModelProperty(value = "物料&商品sku1编码")
    private String sku1Code;

    /** SKU1类型（数据字典的键值或配置档案的编码） */
    @Excel(name = "SKU1类型（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "SKU1类型（数据字典的键值或配置档案的编码）")
    private String sku1Type;

    /** 录入维度（数据字典的键值或配置档案的编码） */
    @Excel(name = "录入维度（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "录入维度（数据字典的键值或配置档案的编码）")
    private String enterDimension;


    @Excel(name = "分配量")
    @ApiModelProperty(value = "分配量")
    private BigDecimal quantityFenpei  ;

}
