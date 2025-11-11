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

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 生产完工确认单对象 s_man_manufacture_complete_note
 *
 * @author linhongwei
 * @date 2021-06-09
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_man_manufacture_complete_note")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ManManufactureCompleteNote extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-生产完工确认单
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-生产完工确认单")
    private Long manufactureCompleteNoteSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] manufactureCompleteNoteSidList;
    /**
     * 生产完工确认单号
     */
    @Excel(name = "生产完工确认单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "生产完工确认单号")
    private Long manufactureCompleteNoteCode;

    /**
     * 系统SID-工厂
     */
    @Excel(name = "系统SID-工厂")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-工厂")
    private Long plantSid;

    /**
     * 工厂名称
     */
    @TableField(exist = false)
    @Excel(name = "工厂")
    @ApiModelProperty(value = "工厂名称")
    private String plantName;

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

    /**
     * 单据日期
     */
    @NotNull(message = "单据日期不能为空")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "单据日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "单据日期")
    private Date documentDate;

    @TableField(exist = false)
    @Excel(name = "商品编码")
    @ApiModelProperty(value = "商品编码")
    private String materialCode;

    @TableField(exist = false)
    @Excel(name = "商品名称")
    @ApiModelProperty(value = "商品名称")
    private String materialName;

    @Excel(name = "完工量")
    @TableField(exist = false)
    @ApiModelProperty(value = "完工量")
    private BigDecimal completeQuantity;

    @Excel(name = "仓库")
    @TableField(exist = false)
    @ApiModelProperty(value = "仓库名称")
    private String storehouseName;

    @Excel(name = "库位")
    @TableField(exist = false)
    @ApiModelProperty(value = "库位名称")
    private String locationName;

    /**
     * 单据类型编码code
     */
    @ApiModelProperty(value = "单据类型编码code")
    private String documentType;

    /**
     * 业务类型编码code
     */
    @ApiModelProperty(value = "业务类型编码code")
    private String businessType;

    /**
     * 系统SID-公司档案
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-公司档案")
    private Long companySid;

    /**
     * 系统SID-仓库档案
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-仓库档案")
    private Long storehouseSid;

    /**
     * 系统SID-仓库库位信息
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-仓库库位信息")
    private Long storehouseLocationSid;

    /** 当前审批节点名称 */
    @ApiModelProperty(value = "当前审批节点名称")
    @TableField(exist = false)
    private String approvalNode;

    /** 当前审批人 */
    @ApiModelProperty(value = "当前审批人")
    @TableField(exist = false)
    private String approvalUserName;

    /** 提交人 */
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
     * 处理状态（数据字典的键值或配置档案的编码）
     */
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    /**
     * 创建人账号（用户名称）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    @Excel(name = "创建人")
    @TableField(exist = false)
    private String creatorAccountName;

    @TableField(exist = false)
    private String[] creatorAccountList;

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

    @Excel(name = "更改人")
    @TableField(exist = false)
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
     * 确认人账号（用户名称）
     */
    @ApiModelProperty(value = "确认人账号（用户名称）")
    private String confirmerAccount;

    /**
     * 确认时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    /**
     * 工厂编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "工厂编码")
    private String plantCode;

    /**
     * 工厂简称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "工厂简称")
    private String shortName;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统ID-物料档案")
    private Long materialSid;

    @TableField(exist = false)
    private String confirmerAccountName;

    @TableField(exist = false)
    @ApiModelProperty(value = "已完工量")
    private BigDecimal quantity;

    @TableField(exist = false)
    @ApiModelProperty(value = "最大行号")
    private Long maxItemNum;

    /**
     * 系统SID-工厂
     */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-工厂")
    private Long[] plantSidList;

    /**
     * 处理状态
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态")
    private String[] handleStatusList;

    /**
     * 生产完工确认单sids
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "生产完工确认单sids")
    private List<Long> manufactureCompleteNoteSids;

    /**
     * 生产完工确认单-明细对象
     */
    @Valid
    @TableField(exist = false)
    @ApiModelProperty(value = "生产完工确认单-明细对象")
    private List<ManManufactureCompleteNoteItem> manManufactureCompleteNoteItemList;

    /**
     * 生产完工确认单-附件对象
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "生产完工确认单-附件对象")
    private List<ManManufactureCompleteNoteAttach> manManufactureCompleteNoteAttachList;
}
