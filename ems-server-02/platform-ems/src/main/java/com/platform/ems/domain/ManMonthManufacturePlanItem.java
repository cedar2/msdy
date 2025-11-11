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
import com.platform.ems.domain.dto.response.ManProduceConcernTaskResponse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 生产月计划-明细对象 s_man_month_manufacture_plan_item
 *
 * @author linhongwei
 * @date 2022-08-08
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_man_month_manufacture_plan_item")
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class ManMonthManufacturePlanItem extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-生产月计划单明细
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-生产月计划单明细")
    private Long monthManufacturePlanItemSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] monthManufacturePlanItemSidList;
    /**
     * 系统SID-生产月计划单
     */
    @Excel(name = "系统SID-生产月计划单")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-生产月计划单")
    private Long monthManufacturePlanSid;

    /**
     * 系统SID-生产订单
     */
    @Excel(name = "系统SID-生产订单")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-生产订单")
    private Long manufactureOrderSid;

    /**
     * 生产订单号
     */
    @Excel(name = "生产订单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "生产订单号")
    private Long manufactureOrderCode;

    /**
     * 系统SID-物料&商品&服务
     */
    @Excel(name = "系统SID-物料&商品&服务")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品&服务")
    private Long materialSid;

    /**
     * 商品编码
     */
    @Excel(name = "商品编码")
    @ApiModelProperty(value = "商品编码")
    private String materialCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品名称")
    private String materialName;

    /**
     * 排产批次号
     */
    @Excel(name = "排产批次号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "排产批次号")
    private Long paichanBatch;

    /**
     * 实际差异天数
     */
    @Excel(name = "实际差异天数")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "实际差异天数")
    private Long daysNumberChayi;

    /**
     * 行号
     */
    @Excel(name = "行号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "行号")
    private Long itemNum;

    /**
     * 计划天数
     */
    @Excel(name = "计划天数")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "计划天数")
    private Long daysNumberPlan;

    /**
     * 计划组日产量
     */
    @Excel(name = "计划组日产量")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "计划组日产量")
    private Long planQuantityDayBanzu;

    /**
     * 创建人账号（用户名称）
     */
    @Excel(name = "创建人账号（用户名称）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    /**
     * 洗水完成日期(头缸)
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "洗水完成日期(头缸)", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "洗水完成日期(头缸)")
    private Date endDateXishuiTg;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /**
     * 洗水完成日期(首批)
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "洗水完成日期(首批)", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "洗水完成日期(首批)")
    private Date endDateXishuiSp;

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
     * 进度说明
     */
    @Excel(name = "进度说明")
    @ApiModelProperty(value = "进度说明")
    private String jinduRemark;

    /**
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "数据源系统（数据字典的键值或配置档案的编码）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    /**
     * 进度说明(头缸)
     */
    @Excel(name = "进度说明(头缸)")
    @ApiModelProperty(value = "进度说明(头缸)")
    private String jinduRemarkTg;

    /**
     * 进度说明(首批)
     */
    @Excel(name = "进度说明(首批)")
    @ApiModelProperty(value = "进度说明(首批)")
    private String jinduRemarkSp;

    /**
     * 系统SID-工作中心(班组)
     */
    @Excel(name = "系统SID-工作中心(班组)")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-工作中心(班组)")
    private Long workCenterSid;

    /**
     * 系统code-工作中心(班组)
     */
    @Excel(name = "系统code-工作中心(班组)")
    @ApiModelProperty(value = "系统code-工作中心(班组)")
    private String workCenterCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统code-工作中心(班组)")
    private String workCenterName;


    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人昵称")
    @TableField(exist = false)
    private String creatorAccountName;

    @Excel(name = "更改人")
    @ApiModelProperty(value = "更改人昵称")
    @TableField(exist = false)
    private String updaterAccountName;

    @Excel(name = "确认人")
    @ApiModelProperty(value = "确认人昵称")
    @TableField(exist = false)
    private String confirmerAccountName;

    @ApiModelProperty(value = "关注事项组")
    @TableField(exist = false)
    private List<ManProduceConcernTaskResponse> manProduceConcernList=new ArrayList<>();

    @ApiModelProperty(value = "工艺路线对应的工序")
    @TableField(exist = false)
    private List<ManMonthManufacturePlanProcess> manufacturePlanProcessList;

    @Excel(name = "实裁量")
    @TableField(exist = false)
    @ApiModelProperty(value = "实裁量")
    private BigDecimal isCaichuangQuantity;

    @TableField(exist = false)
    @ApiModelProperty(value = "分配量（停用）")
    private BigDecimal distributionQuantity;

    @Excel(name = "计划产量（整单）")
    @TableField(exist = false)
    @ApiModelProperty(value = "计划产量（整单）")
    private BigDecimal quantity;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "计划完工日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划完工日期")
    @TableField(exist = false)
    private Date planEndDate;

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
