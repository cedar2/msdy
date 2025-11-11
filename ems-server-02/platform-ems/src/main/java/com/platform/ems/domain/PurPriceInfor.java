package com.platform.ems.domain;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import javax.validation.constraints.NotEmpty;
import lombok.experimental.Accessors;

/**
 * 采购价格记录主(报价/核价/议价)对象 s_pur_price_infor
 *
 * @author linhongwei
 * @date 2021-04-26
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_pur_price_infor")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PurPriceInfor extends EmsBaseEntity {

    /** 租户ID */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /** 系统SID-采购价格记录 */
    @TableId
    @ApiModelProperty(value = "系统SID-采购价格记录")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long priceInforSid;

    /** 采购价格记录编码 */
    @Excel(name = "采购价格记录编码")
    @ApiModelProperty(value = "采购价格记录编码")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long priceInforCode;

    /** 系统SID-供应商档案sid */
    @Excel(name = "系统SID-供应商档案sid")
    @ApiModelProperty(value = "系统SID-供应商档案sid")
    private Long vendorSid;

    /** 系统SID-物料档案（物料/商品/服务） */
    @Excel(name = "系统SID-物料档案（物料/商品/服务）")
    @ApiModelProperty(value = "系统SID-物料档案（物料/商品/服务）")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long materialSid;

    /** 系统SID-SKU1档案sid */
    @Excel(name = "系统SID-SKU1档案sid")
    @ApiModelProperty(value = "系统SID-SKU1档案sid")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long sku1Sid;

    @Excel(name = "商品条码Sid")
    @ApiModelProperty(value = "商品条码Sid")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long barcodeSid;

    @Excel(name = "商品条码")
    @ApiModelProperty(value = "商品条码")
    @TableField(exist = false)
    private Long barcode;

    /** 物料类别编码 */
    @Excel(name = "物料类别编码", dictType = "s_material_category")
    @ApiModelProperty(value = "物料类别编码")
    private String materialCategory;

    /** 系统SID-SKU2档案sid */
    @Excel(name = "系统SID-SKU2档案sid")
    @ApiModelProperty(value = "系统SID-SKU2档案sid")
    private Long sku2Sid;

    /** 甲供料方式（数据字典的键值） */
    @Excel(name = "甲供料方式（数据字典的键值）")
    @ApiModelProperty(value = "甲供料方式（数据字典的键值）")
    private String rawMaterialMode;

    /** 采购模式（数据字典的键值） */
    @Excel(name = "采购模式（数据字典的键值）")
    @ApiModelProperty(value = "采购模式（数据字典的键值）")
    private String purchaseMode;

    /** 价格维度（数据字典的键值） */
    @Excel(name = "价格维度（数据字典的键值）")
    @ApiModelProperty(value = "价格维度（数据字典的键值）")
    private String priceDimension;

    /** 采购组织（数据字典的键值） */
    @Excel(name = "采购组织（数据字典的键值）")
    @ApiModelProperty(value = "采购组织（数据字典的键值）")
    private Long companySid;

    /** 成本组织（数据字典的键值） */
    @Excel(name = "成本组织（数据字典的键值）")
    @ApiModelProperty(value = "成本组织（数据字典的键值）")
    private String purchaseOrg;

    /** 递增减SKU类型（数据字典的键值） */
    @Excel(name = "递增减SKU类型（数据字典的键值）")
    @ApiModelProperty(value = "递增减SKU类型（数据字典的键值）")
    private String costOrg;

    /** 备注 */
    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String skuTypeRecursion;

    /** 处理状态（数据字典的键值） */
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "处理状态（数据字典的键值）",dictType = "s_valid_flag")
    @ApiModelProperty(value = "处理状态（数据字典的键值）")
    private String status;

    /** 创建人账号（用户名称） */
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "创建人账号（用户名称）",dictType = "s_handle_status")
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String handleStatus;

    /** 创建时间 */
    @Excel(name = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private String creatorAccount;

    /** 更新人账号（用户名称） */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "更新人账号（用户名称）", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private Date createDate;

    /** 更新时间 */
    @Excel(name = "更新时间")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private String updaterAccount;

    /** 确认人账号（用户名称） */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "确认人账号（用户名称）", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "确认人账号（用户名称）")
    private Date updateDate;

    /** 确认时间 */
    @Excel(name = "确认时间")
    @ApiModelProperty(value = "确认时间")
    private String confirmerAccount;

    /** 备注 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "备注", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "备注")
    private Date confirmDate;

    /** 数据源系统（数据字典的键值） */
    @Excel(name = "数据源系统（数据字典的键值）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值）")
    private String dataSourceSys;


    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "创建日期开始时间")
    @TableField(exist = false)
    private String beginTime;

    @ApiModelProperty(value = "创建日期结束时间")
    @TableField(exist = false)
    private String endTime;

    @ApiModelProperty(value = "页数")
    @TableField(exist = false)
    private Integer pageNum;

    @ApiModelProperty(value = "每页个数")
    @TableField(exist = false)
    private Integer pageSize;

    @TableField(exist = false)
    @ApiModelProperty(value = "采购价格记录明细")
    private List<PurPriceInforItem> purPriceInforItemList;

}
