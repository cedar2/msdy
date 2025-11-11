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

import java.math.BigDecimal;
import java.util.Date;

/**
 * 供应商返修台账-明细对象 s_inv_record_vendor_repair_item
 *
 * @author linhongwei
 * @date 2021-10-27
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_inv_record_vendor_repair_item")
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class InvRecordVendorRepairItem extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-供应商返修台账明细
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商返修台账明细")
    private Long vendorRepairItemSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] vendorRepairItemSidList;
    /**
     * 系统SID-供应商返修台账
     */
    @Excel(name = "系统SID-供应商返修台账")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商返修台账")
    private Long vendorRepairSid;

    /**
     * 系统SID-物料&商品
     */
    @Excel(name = "系统SID-物料&商品")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品")
    private Long materialSid;

    /**
     * 系统SID-物料&商品sku1
     */
    @Excel(name = "系统SID-物料&商品sku1")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品sku1")
    private Long sku1Sid;

    /**
     * 系统SID-物料&商品sku2
     */
    @Excel(name = "系统SID-物料&商品sku2")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品sku2")
    private Long sku2Sid;

    /**
     * 系统SID-商品条码（物料&商品）
     */
    @Excel(name = "系统SID-商品条码（物料&商品）")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品条码（物料&商品）")
    private Long barcodeSid;

    /**
     * 基本计量单位（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "基本计量单位（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "基本计量单位（数据字典的键值或配置档案的编码）")
    private String unitBase;

    /**
     * 返修量
     */
    @Excel(name = "返修量")
    @ApiModelProperty(value = "返修量")
    private BigDecimal repairQuantity;

    /**
     * 退还量
     */
    @Excel(name = "退还量")
    @ApiModelProperty(value = "退还量")
    private BigDecimal returnQuantity;

    /**
     * 计划退还日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "计划退还日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划退还日期")
    private Date planReturnDate;

    /**
     * 退还状态（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "退还状态（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "退还状态（数据字典的键值或配置档案的编码）")
    private String returnStatus;

    /**
     * 行号
     */
    @Excel(name = "行号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "行号")
    private Long itemNum;

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
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "数据源系统（数据字典的键值或配置档案的编码）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    @TableField(exist = false)
    @ApiModelProperty(value ="物料名称")
    private String materialName;

    @TableField(exist = false)
    @ApiModelProperty(value ="物料编码")
    private String materialCode;

    @TableField(exist = false)
    private String sku1Code;

    @TableField(exist = false)
    private String sku1Name;

    @TableField(exist = false)
    private String sku2Code;

    @TableField(exist = false)
    private String sku2Name;

    @ApiModelProperty(value = "基本计量单位名称")
    @TableField(exist = false)
    private String unitBaseName;

    @ApiModelProperty(value = "创建人账号（用户名称）")
    @TableField(exist = false)
    private String creatorAccountName;

    @ApiModelProperty(value = "商品条码")
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long barcode;

    @TableField(exist = false)
    private String firstSort;

    @TableField(exist = false)
    private String secondSort;

    @TableField(exist = false)
    private String thirdSort;
}
