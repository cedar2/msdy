package com.platform.ems.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableId;

import com.platform.common.core.domain.EmsBaseEntity;
import com.platform.ems.util.data.KeepTwoDecimalsSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import lombok.experimental.Accessors;

/**
 * 生产进度日报-款生产进度对象 s_man_day_manufacture_kuan_progress
 *
 * @author chenkw
 * @date 2022-08-03
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_man_day_manufacture_kuan_progress")
public class ManDayManufactureKuanProgress extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-生产进度日报单款进度
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-生产进度日报单款进度")
    private Long dayManufactureKuanProgressSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] dayManufactureKuanProgressSidList;
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
     * 生产订单号
     */
    @Excel(name = "生产订单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "生产订单号")
    private Long manufactureOrderCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "工作中心/班组sid")
    private Long workCenterSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "工作中心/班组sid")
    private Long[] workCenterSidList;

    @ApiModelProperty(value = "工作中心/班组编码")
    private String workCenterCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "工作中心/班组名称")
    private String workCenterName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "操作部门")
    private Long departmentSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "操作部门")
    private Long[] departmentSidList;

    @ApiModelProperty(value = "操作部门编码")
    private String departmentCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "操作部门名称")
    private String departmentName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品sku1")
    private Long sku1Sid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-物料&商品sku1")
    private Long[] sku1SidList;

    @ApiModelProperty(value = "SKU1类型（数据字典的键值或配置档案的编码）")
    private String sku1Type;

    @TableField(exist = false)
    @ApiModelProperty(value = "SKU1编码")
    private String sku1Code;

    @TableField(exist = false)
    @ApiModelProperty(value = "SKU1名称")
    private String sku1Name;

    /**
     * 商品sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "商品sid")
    private Long productSid;

    /**
     * 商品code
     */
    @Excel(name = "商品code")
    @ApiModelProperty(value = "商品code")
    private String productCode;

    /**
     * 排产批次号
     */
    @Excel(name = "排产批次号")
    @ApiModelProperty(value = "排产批次号")
    private Long paichanBatch;

    /**
     * 今日完成量
     */
    @Excel(name = "今日完成量")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "今日完成量")
    private Long quantity;

    @TableField(exist = false)
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "今日目标")
    private BigDecimal planQuantity;

    /**
     * 欠数原因
     */
    @Excel(name = "欠数原因")
    @ApiModelProperty(value = "欠数原因")
    private String reasonQianshu;

    /**
     * 解决方案
     */
    @Excel(name = "解决方案")
    @ApiModelProperty(value = "解决方案")
    private String solution;

    /**
     * 组内裁片数
     */
    @Excel(name = "组内裁片数")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "组内裁片数")
    private Long quantityCaip;

    /**
     * 半成品数
     */
    @Excel(name = "半成品数")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "半成品数")
    private Long quantityBancp;

    /**
     * 未封腰数
     */
    @Excel(name = "未封腰数")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "未封腰数")
    private Long quantityWeify;

    /**
     * 未送洗水数
     */
    @Excel(name = "未送洗水数")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "未送洗水数")
    private Long quantityWeisxs;

    /**
     * 创建人账号（用户名称）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人昵称")
    @TableField(exist = false)
    private String creatorAccountName;

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
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

    @Excel(name = "更改人")
    @ApiModelProperty(value = "更改人昵称")
    @TableField(exist = false)
    private String updaterAccountName;

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
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

}
