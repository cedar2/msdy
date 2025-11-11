package com.platform.ems.plug.domain;

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
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.Date;

/**
 * 作业类型(移动类型)对象 s_con_movement_type
 *
 * @author linhongwei
 * @date 2021-05-20
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_con_movement_type")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConMovementType extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-作业类型(移动类型)sid
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-作业类型(移动类型)sid")
    private Long sid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] sidList;
    /**
     * 作业类型(移动类型)编码
     */
    @NotBlank(message = "作业类型编码不能为空")
    @Length(max = 8, message = "作业类型编码不能超过8个字符")
    @Excel(name = "作业类型编码")
    @ApiModelProperty(value = "作业类型(移动类型)编码")
    private String code;

    /**
     * 作业类型(移动类型)名称
     */
    @Length(max = 300, message = "作业类型名称不能超过300个字符")
    @NotBlank(message = "作业类型名称不能为空")
    @Excel(name = "作业类型名称")
    @ApiModelProperty(value = "作业类型(移动类型)名称")
    private String name;

    /**
     * 是否允许冲销（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "是否允许冲销", dictType = "sys_yes_no")
    @ApiModelProperty(value = "是否允许冲销（数据字典的键值或配置档案的编码）")
    private String isAllowReverse;

    /** 数量对应计量单位 */
    @Excel(name = "数量对应计量单位", dictType = "s_refer_unit_type")
    @ApiModelProperty(value = "数量对应计量单位")
    private String referUnitType;

    @Excel(name = "货品标识", dictType = "s_storelocation_material_flag")
    @ApiModelProperty(value = "货品标识")
    private String materialFlag;

    @ApiModelProperty(value = "是否允许仓库可编辑")
    private String isStorehouseEdit;

    @ApiModelProperty(value = "是否允许库位可编辑")
    private String isStorehouseLocationEdit;

    @NotBlank(message = "请选择是否启用预留")
    @Excel(name = "是否启用预留", dictType = "s_yesno_flag")
    @ApiModelProperty(value = "是否启用预留")
    private String isUseReserve;

    @ApiModelProperty(value = "特殊库存")
    private String specialStock;

    @Excel(name = "特殊库存")
    @TableField(exist = false)
    @ApiModelProperty(value = "特殊库存")
    private String specialStockName;

    @Excel(name = "是否一步调拨", dictType = "sys_yes_no")
    @ApiModelProperty(value = "是否一步调拨（数据字典的键值或配置档案的编码）")
    private String isTransferOnestep;

    @TableField(exist = false)
    @ApiModelProperty(value = "单据类别,出入库对应单据类别表（s_con_in_out_stock_doc_category）")
    private String docCategoryCode;

    @Excel(name = "备注")
    @ApiModelProperty(value ="备注")
    private String remark;

    /**
     * 处理状态（数据字典的键值）
     */
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值）")
    private String handleStatus;

    /**
     * 启用/停用状态（数据字典的键值）
     */
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "启用/停用", dictType = "s_valid_flag")
    @ApiModelProperty(value = "启用/停用状态（数据字典的键值）")
    private String status;

    /**
     * 创建人账号（用户名称）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    @Excel(name = "创建人")
    @TableField(exist = false)
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
     * 数据源系统（数据字典的键值）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值）")
    private String dataSourceSys;

    /**
     * 是否允许租户自定义
     */
    @ApiModelProperty(value = " 是否允许租户自定义")
    private String isClientDefine;

    /**
     * 增减标识（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "增减标识（数据字典的键值或配置档案的编码）")
    private String plusMinusFlag;

    /**
     * 关联单据类别（数据字典的键值或配置档案的编码），如：领退料单、调拨单、库存调整单、盘点单、采购交货单、销售发货单、采购订单、生产订单（删除）
     */
    @ApiModelProperty(value = "关联单据类别（数据字典的键值或配置档案的编码），如：领退料单、调拨单、库存调整单、盘点单、采购交货单、销售发货单、采购订单、生产订单（删除）")
    private String referDocCategory;

    /**
     * 入库功能是否显示此作业类型（数据字典的键值）
     */
    @ApiModelProperty(value = "入库功能是否显示此作业类型（数据字典的键值）")
    private String isGrDisplay;

    /**
     * 出库功能是否显示此作业类型（数据字典的键值）
     */
    @ApiModelProperty(value = "出库功能是否显示此作业类型（数据字典的键值）")
    private String isGiDisplay;

    /**
     * 移库功能是否显示此作业类型（数据字典的键值）
     */
    @ApiModelProperty(value = "移库功能是否显示此作业类型（数据字典的键值）")
    private String isGtDisplay;

    /**
     * 收货功能是否显示此作业类型（数据字典的键值）
     */
    @ApiModelProperty(value = "收货功能是否显示此作业类型（数据字典的键值）")
    private String isGrnDisplay;

    /**
     * 发货功能是否显示此作业类型（数据字典的键值）
     */
    @ApiModelProperty(value = "发货功能是否显示此作业类型（数据字典的键值）")
    private String isGinDisplay;

    /**
     * 调拨功能是否显示此作业类型（数据字典的键值）
     */
    @ApiModelProperty(value = "调拨功能是否显示此作业类型（数据字典的键值）")
    private String isItnDisplay;

    /**
     * 盘点功能是否显示此作业类型（数据字典的键值）
     */
    @ApiModelProperty(value = "盘点功能是否显示此作业类型（数据字典的键值）")
    private String isIsnDisplay;

    /**
     * 库存调整功能是否显示此作业类型（数据字典的键值）
     */
    @ApiModelProperty(value = "库存调整功能是否显示此作业类型（数据字典的键值）")
    private String isIanDisplay;

    /**
     * 领退料功能是否显示此作业类型（数据字典的键值）
     */
    @ApiModelProperty(value = "领退料功能是否显示此作业类型（数据字典的键值）")
    private String isMrnDisplay;

    /**
     * 期初库存功能是否显示此作业类型（数据字典的键值）
     */
    @ApiModelProperty(value = "期初库存功能是否显示此作业类型（数据字典的键值）")
    private String isInitDisplay;

    /**
     * 串色串码功能是否显示此作业类型（数据字典的键值）
     */
    @ApiModelProperty(value = "串色串码功能是否显示此作业类型（数据字典的键值）")
    private String isCstDisplay;

    /**
     * 借出单功能是否显示此作业类型（数据字典的键值）
     */
    @ApiModelProperty(value = "借出单功能是否显示此作业类型（数据字典的键值）")
    private String isBnDisplay;

    /**
     * 退还单功能是否显示此作业类型（数据字典的键值）
     */
    @ApiModelProperty(value = "退还单功能是否显示此作业类型（数据字典的键值）")
    private String isRnDisplay;

    /**
     * 样品处理单功能是否显示此作业类型（数据字典的键值）
     */
    @ApiModelProperty(value = "样品处理单功能是否显示此作业类型（数据字典的键值）")
    private String isDwnDisplay;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long movementTypeSid;

    @TableField(exist = false)
    private String movementTypeName;

    @TableField(exist = false)
    private String movementTypeCode;

    @ApiModelProperty(value = "是否同仓")
    private String isSameStorehouse;

    /**
     * 序号
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "序号")
    private Long sort;

    @TableField(exist = false)
    @ApiModelProperty(value = "库存凭证类别编码")
    private String invDocCategoryCode;

}
