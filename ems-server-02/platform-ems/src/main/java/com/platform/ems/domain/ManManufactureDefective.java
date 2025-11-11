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
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 生产次品台账对象 s_man_manufacture_defective
 *
 * @author c
 * @date 2022-03-02
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_man_manufacture_defective")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ManManufactureDefective extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-生产次品台账信息
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-生产次品台账信息")
    private Long manufactureDefectiveSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] manufactureDefectiveSidList;
    /**
     * 生产次品台账流水号
     */
    @Excel(name = "生产次品台账流水号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "生产次品台账流水号")
    private Long manufactureDefectiveCode;

    /**
     * 工厂sid
     */
    @NotNull(message = "工厂不能为空")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "工厂sid")
    private Long plantSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "工厂编码")
    private String plantCode;

    @Excel(name = "工厂")
    @TableField(exist = false)
    @ApiModelProperty(value = "工厂简称")
    private String shortName;

    /**
     * 工作中心/班组sid
     */
    @NotNull(message = "工作中心/班组不能为空")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "工作中心/班组sid")
    private Long workCenterSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "工作中心/班组编码")
    private String workCenterCode;

    @Excel(name = "工作中心/班组")
    @TableField(exist = false)
    @ApiModelProperty(value = "工作中心/班组名称")
    private String workCenterName;

    /**
     * 系统SID-生产订单
     */
    @NotNull(message = "生产订单不能为空")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-生产订单")
    private Long manufactureOrderSid;

    /**
     * 生产订单号
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "生产订单号")
    private Long manufactureOrderCode;

    /**
     * 系统SID-商品sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品sid")
    private Long productSid;

    @Excel(name = "商品编码")
    @TableField(exist = false)
    @ApiModelProperty(value = "商品编码")
    private String materialCode;

    @Excel(name = "商品名称")
    @TableField(exist = false)
    @ApiModelProperty(value = "商品名称")
    private String materialName;

    @Excel(name = "快速编码")
    @TableField(exist = false)
    @ApiModelProperty(value = "快速编码")
    private String simpleCode;

    /**
     * 系统SID-商品sku1
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品sku1")
    private Long sku1Sid;

    @Excel(name = "SKU1名称")
    @TableField(exist = false)
    @ApiModelProperty(value = "SKU1名称")
    private String sku1Name;

    /**
     * 系统SID-商品sku2
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品sku2")
    private Long sku2Sid;

    @Excel(name = "SKU2名称")
    @TableField(exist = false)
    @ApiModelProperty(value = "SKU2称")
    private String sku2Name;

    /**
     * 系统SID-商品条码（物料&商品&服务）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品条码（物料&商品&服务）")
    private Long barcodeSid;

    /**
     * 基本计量单位（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "基本计量单位（数据字典的键值或配置档案的编码）")
    private String unitBase;

    /**
     * 次品量
     */
    @Digits(integer = 8, fraction = 3, message = "次品量整数位上限为8位，小数位上限为3位")
    @Excel(name = "次品量")
    @ApiModelProperty(value = "次品量")
    private BigDecimal quantityDefective;

    /**
     * 计划修复完成日期
     */
    @NotNull(message = "计划修复完成日期不能为空")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "计划修复完成日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划修复完成日期")
    private Date resolveDate;

    /**
     * 说明
     */
    @Excel(name = "说明")
    @ApiModelProperty(value = "说明")
    private String description;

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

    @Excel(name = "确认人")
    @TableField(exist = false)
    private String confirmerAccountName;

    /**
     * 确认时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "确认日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统自增长ID-工厂list")
    private Long[] plantSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统自增长ID-工作中心/班组list")
    private Long[] workCenterSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态list")
    private String[] handleStatusList;

    /**
     * 生产次品台账-附件对象
     */
    @Valid
    @TableField(exist = false)
    @ApiModelProperty(value = "生产次品台账-附件对象")
    private List<ManManufactureDefectiveAttach> attachList;

}
