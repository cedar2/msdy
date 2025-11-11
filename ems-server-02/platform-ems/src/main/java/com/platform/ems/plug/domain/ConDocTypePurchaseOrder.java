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
import java.util.List;

/**
 * 单据类型_采购订单对象 s_con_doc_type_purchase_order
 *
 * @author chenkw
 * @date 2021-05-20
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_con_doc_type_purchase_order")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConDocTypePurchaseOrder extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-单据类型(采购订单)sid
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-单据类型(采购订单)sid")
    private Long sid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] sidList;
    /**
     * 单据类型(采购订单)编码
     */
    @NotBlank(message = "单据类型编码不能为空")
    @Length(max = 8, message = "单据类型编码不能超过8个字符")
    @Excel(name = "单据类型编码")
    @ApiModelProperty(value = "单据类型(采购订单)编码")
    private String code;

    /**
     * 单据类型(采购订单)名称
     */
    @NotBlank(message = "单据类型名称不能为空")
    @Length(max = 300, message = "单据类型名称不能超过300个字符")
    @Excel(name = "单据类型名称")
    @ApiModelProperty(value = "单据类型(采购订单)名称")
    private String name;

    /**
     * 采购模式（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "采购模式", dictType = "s_price_type")
    @ApiModelProperty(value = "采购模式（数据字典的键值或配置档案的编码）")
    private String purchaseMode;

    /**
     * 是否退货（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "是否退货", dictType = "sys_yes_no")
    @ApiModelProperty(value = "是否退货（数据字典的键值或配置档案的编码）")
    private String isReturnGoods;

    /**
     * 是否生成财务待付预付流水（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "是否生成待付预付流水", dictType = "sys_yes_no")
    @ApiModelProperty(value = "是否生成财务待付预付流水（数据字典的键值或配置档案的编码）")
    private String isFinanceBookDfyf;

    /**
     * 是否生成财务流水（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "是否生成应付暂估流水", dictType = "sys_yes_no")
    @ApiModelProperty(value = "是否生成财务应付暂估流水（数据字典的键值或配置档案的编码）")
    private String isFinanceBookYfzg;

    /**
     * 是否寄售结算（数据字典的键值或配置档案的编码
     */
    @Excel(name = "是否寄售结算", dictType = "sys_yes_no")
    @ApiModelProperty(value = "是否寄售结算（数据字典的键值或配置档案的编码")
    private String isConsignmentSettle;

    /**
     * 序号
     */
    @Excel(name = "序号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "序号")
    private Long sort;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 过滤
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "过滤")
    private String filter;

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
    @ApiModelProperty(value = "创建人账号（用户名称）")
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
    @ApiModelProperty(value = "是否允许租户自定义")
    private String isClientDefine;

    @TableField(exist = false)
    @ApiModelProperty(value = "业务类型编码list")
    private List<String> buTypeCodeList;

    @TableField(exist = false)
    @ApiModelProperty(value = "是否一次性采购")
    private String isOncePurchase;

}
