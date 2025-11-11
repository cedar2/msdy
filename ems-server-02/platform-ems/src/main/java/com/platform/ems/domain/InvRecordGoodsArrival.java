package com.platform.ems.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.core.domain.BaseEntity;
import com.platform.common.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableId;

import java.util.List;

import com.platform.common.core.domain.EmsBaseEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import com.platform.common.core.domain.document.UserOperLog;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

import lombok.experimental.Accessors;

/**
 * 采购到货台账对象 s_inv_record_goods_arrival
 *
 * @author linhongwei
 * @date 2022-06-27
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_inv_record_goods_arrival")
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class InvRecordGoodsArrival extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-采购到货台账
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-采购到货台账")
    private Long goodsArrivalSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] goodsArrivalSidList;
    /**
     * 采购到货台账编号
     */
    @Excel(name = "采购到货台账编号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购到货台账编号")
    private Long goodsArrivalCode;

    /**
     * 系统SID-仓库档案
     */
    @Excel(name = "系统SID-仓库档案")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-仓库档案")
    private Long storehouseSid;

    /**
     * 到货日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "到货日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "到货日期")
    private Date arrivalDate;

    /**
     * 供方送货单号
     */
    @Excel(name = "供方送货单号")
    @ApiModelProperty(value = "供方送货单号")
    private String supplierDeliveryCode;

    /**
     * 货运方sid（承运商）
     */
    @Excel(name = "货运方sid（承运商）")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "货运方sid（承运商）")
    private Long carrier;

    /**
     * 货运方名称（承运商）
     */
    @Excel(name = "货运方名称（承运商）")
    @ApiModelProperty(value = "货运方名称（承运商）")
    private String carrierName;

    /**
     * 货运单号
     */
    @Excel(name = "货运单号")
    @ApiModelProperty(value = "货运单号")
    private String carrierNoteCode;

    /**
     * 处理状态（数据字典的键值或配置档案的编码）
     */
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "处理状态（数据字典的键值或配置档案的编码）", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    /**
     * 创建人账号（用户账号）
     */
    @Excel(name = "创建人账号（用户账号）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户账号）")
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
     * 更新人账号（用户账号）
     */
    @Excel(name = "更新人账号（用户账号）")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户账号）")
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
     * 确认人账号（用户账号）
     */
    @Excel(name = "确认人账号（用户账号）")
    @ApiModelProperty(value = "确认人账号（用户账号）")
    private String confirmerAccount;

    /**
     * 确认时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "确认时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "数据源系统（数据字典的键值或配置档案的编码）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;


    @TableField(exist = false)
    private String creatorAccountName;

    @TableField(exist = false)
    private String updaterAccountName;

    @TableField(exist = false)
    private String confirmerAccountName;

    @TableField(exist = false)
    @ApiModelProperty(value = "采购到货台账-明细对象")
    private List<InvRecordGoodsArrivalItem> itemList;

    @TableField(exist = false)
    @ApiModelProperty(value = "采购到货台账-缸号明细对象")
    private List<InvRecordGoodsArrivalAttach> attachList;

    @ApiModelProperty(value = "仓库")
    @TableField(exist = false)
    private String storehouseName;

    @ApiModelProperty(value = "查询：下单季")
    @TableField(exist = false)
    private String[] productSeasonSidList;

    @ApiModelProperty(value = "查询：公司")
    @TableField(exist = false)
    private String[] companySidList;

    @ApiModelProperty(value = "查询：供应商")
    @TableField(exist = false)
    private String[] vendorSidList;

    @ApiModelProperty(value = "查询：采购订单号")
    @TableField(exist = false)
    private String purchaseOrderCode;

    @ApiModelProperty(value = "查询：采购员")
    @TableField(exist = false)
    private String[] buyerList;

    @ApiModelProperty(value = "查询：处理状态")
    @TableField(exist = false)
    private String[] handleStatusList;

    @ApiModelProperty(value = "查询：创建人")
    @TableField(exist = false)
    private String[] creatorAccountList;

    @ApiModelProperty(value = "查询：创建人")
    @TableField(exist = false)
    private String[] createDateStart;

    @ApiModelProperty(value = "创建日期开始时间")
    @TableField(exist = false)
    private String beginTime;

    @ApiModelProperty(value = "创建日期结束时间")
    @TableField(exist = false)
    private String endTime;

    @ApiModelProperty(value = "到货日期(起)")
    @TableField(exist = false)
    private String arrivalDateStart;

    @ApiModelProperty(value = "到货日期(至)")
    @TableField(exist = false)
    private String arrivalDateEnd;

    @ApiModelProperty(value = "作废说明")
    @TableField(exist = false)
    private String explain;
}
