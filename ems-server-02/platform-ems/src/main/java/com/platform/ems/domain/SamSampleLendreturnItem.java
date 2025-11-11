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
import java.util.List;

/**
 * 样品借还单-明细对象 s_sam_sample_lendreturn_item
 *
 * @author linhongwei
 * @date 2021-12-20
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_sam_sample_lendreturn_item")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SamSampleLendreturnItem extends EmsBaseEntity{

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @TableField(exist = false)
    @ApiModelProperty(value = "借出人（用户账号）")
    private String lenderName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "样品借还单号")
    @TableField(exist = false)
    private Long lendreturnCode;

    /**
     * 系统SID-样品借还单明细
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-样品借还单明细")
    private Long lendreturnItemSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] lendreturnItemSidList;
    /**
     * 系统SID-样品借还单
     */
    @Excel(name = "系统SID-样品借还单")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-样品借还单")
    private Long lendreturnSid;

    /**
     * 样品sid
     */
    @Excel(name = "样品sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "样品sid")
    private Long sampleSid;

    /**
     * 样品编码
     */
    @Excel(name = "样品编码")
    @ApiModelProperty(value = "样品编码")
    private String sampleCode;

    /**
     * 颜色sid
     */
    @Excel(name = "颜色sid")
    @ApiModelProperty(value = "颜色sid")
    private String sku1Sid;

    /**
     * 尺码sid
     */
    @Excel(name = "尺码sid")
    @ApiModelProperty(value = "尺码sid")
    private String sku2Sid;

    /**
     * 数量(借出/归还/遗失)
     */
    @Excel(name = "数量(借出/归还/遗失)")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "数量(借出/归还/遗失)")
    private Integer quantity;


    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "借出数量")
    @TableField(exist = false)
    private int quantityJ;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "已归还量")
    @TableField(exist = false)
    private int quantityYG;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "待归还量")
    @TableField(exist = false)
    private int quantityDG;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "归还中量")
    @TableField(exist = false)
    private int quantityGHZ;

    /**
     * 基本计量单位（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "基本计量单位（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "基本计量单位（数据字典的键值或配置档案的编码）")
    private String unitBase;

    /**
     * 库存价
     */
    @Excel(name = "库存价")
    @ApiModelProperty(value = "库存价")
    private BigDecimal price;

    /**
     * 商品条码
     */
    @Excel(name = "商品条码")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "商品条码")
    private Long barcodeSid;

    /**
     * 行号
     */
    @Excel(name = "行号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "行号")
    private int itemNum;

    /**
     * 归还单/遗失单对应的借出单SID
     */
    @Excel(name = "归还单/遗失单对应的借出单SID")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "归还单/遗失单对应的借出单SID")
    private Long preLendreturnSid;

    /**
     * 借还状态（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "借还状态（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "借还状态（数据字典的键值或配置档案的编码）")
    private String returnStatus;

    /**
     * 计划归还日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "计划归还日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划归还日期")
    private Date planReturnDate;

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
    @ApiModelProperty(value = "物料名称")
    private String materialName;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料编码")
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
    private  String[] handleStatusList;

    @TableField(exist = false)
    private  String handleStatus;



    @ApiModelProperty(value = "单据类型")
    @TableField(exist = false)
    private String documentType;

    @Excel(name = "仓库sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "仓库sid")
    @TableField(exist = false)
    private Long storehouseSid;

    /**
     * 库位sid
     */
    @Excel(name = "库位sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "库位sid")
    @TableField(exist = false)
    private Long storehouseLocationSid;

    @TableField(exist = false)
    private List<Long> preLendreturnSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "单据类型")
    private String documentTypeName;

    @ApiModelProperty(value = "异常原因")
    private String exceptionReason;

    @ApiModelProperty(value = "客户code")
    private String customerCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "客户")
    private String customerName;

    @ApiModelProperty(value = "供应商code")
    private String vendorCode;

    @ApiModelProperty(value = "客户sid")
    private String customerSid;

    @ApiModelProperty(value = "供应商sid")
    private String vendorSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "供应商")
    private String vendorName;

}
