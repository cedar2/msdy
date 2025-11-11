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

import com.platform.common.core.domain.EmsBaseEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import lombok.experimental.Accessors;

/**
 * 加工采购价格记录主(报价/核价/议价)对象 s_pur_outsource_price_infor
 *
 * @author linhongwei
 * @date 2022-04-01
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_pur_outsource_price_infor")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PurOutsourcePriceInfor extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-加工采购价格记录
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-加工采购价格记录")
    private Long outsourcePriceInforSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] outsourcePriceInforSidList;
    /**
     * 加工采购价格记录编码
     */
    @Excel(name = "加工采购价格记录编码")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "加工采购价格记录编码")
    private Long outsourcePriceInforCode;

    /**
     * 系统SID-供应商档案sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商档案sid")
    private Long vendorSid;

    @Excel(name = "供应商编码")
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "供应商档案编码")
    private Long vendorCode;

    @Excel(name = "供应商名称")
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "供应商档案名称")
    private String vendorName;

    @Excel(name = "供应商简称")
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "供应商档案简称")
    private String vendorShortName;

    /**
     * 系统SID-物料档案（物料/商品/服务）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料档案（物料/商品/服务）")
    private Long materialSid;

    @Excel(name = "物料/商品档案编码")
    @TableField(exist = false)
    @ApiModelProperty(value = "物料档案（物料/商品/服务）编码")
    private String materialCode;

    @Excel(name = "物料/商品档案名称")
    @TableField(exist = false)
    @ApiModelProperty(value = "物料档案（物料/商品/服务）名称")
    private String materialName;

    /**
     * 系统SID-SKU1档案sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-SKU1档案sid")
    private Long sku1Sid;

    @Excel(name = "SKU1档案编码")
    @TableField(exist = false)
    @ApiModelProperty(value = "SKU1档案编码")
    private String sku1Code;

    @Excel(name = "SKU1档案档案名称")
    @TableField(exist = false)
    @ApiModelProperty(value = "SKU1档案名称")
    private String sku1Name;

    /**
     * 系统SID-SKU2档案sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-SKU2档案sid")
    private Long sku2Sid;

    @Excel(name = "SKU2档案编码")
    @TableField(exist = false)
    @ApiModelProperty(value = "SKU2档案编码")
    private String sku2Code;

    @Excel(name = "SKU2档案档案名称")
    @TableField(exist = false)
    @ApiModelProperty(value = "SKU2档案名称")
    private String sku2Name;

    /**
     * 系统SID-工序
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-工序")
    private Long processSid;

    @Excel(name = "工序编码")
    @TableField(exist = false)
    @ApiModelProperty(value = "工序编码")
    private String processCode;

    @Excel(name = "工序名称")
    @TableField(exist = false)
    @ApiModelProperty(value = "工序名称")
    private String processName;

    /**
     * 系统SID-商品条码（物料&商品&服务）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品条码（物料&商品&服务）")
    private Long barcodeSid;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "商品条码")
    private Long barcode;

    /**
     * 系统SID-工价费用项sid（工价项）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-工价费用项sid（工价项）")
    private Long laborTypeItemSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "工价费用项编码（工价项）")
    private String laborTypeItemCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "工价费用项名称（工价项）")
    private String laborTypeItemName;

    /**
     * 物料类别（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "物料类别")
    @ApiModelProperty(value = "物料类别（数据字典的键值或配置档案的编码）")
    private String materialCategory;

    /**
     * 价格维度（数据字典的键值）
     */
    @Excel(name = "价格维度")
    @ApiModelProperty(value = "价格维度（数据字典的键值）")
    private String priceDimension;

    /**
     * 公司编码（公司档案的sid）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司编码（公司档案的sid）")
    private Long companySid;

    @Excel(name = "公司编码")
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司档案编码")
    private Long companyCode;

    @Excel(name = "公司名称")
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司档案名称")
    private String companyName;

    @Excel(name = "公司简称")
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司档案简称")
    private String companyShortName;

    /**
     * 采购组织（数据字典的键值）
     */
    @Excel(name = "采购组织")
    @ApiModelProperty(value = "采购组织（数据字典的键值）")
    private String purchaseOrg;

    /**
     * 创建人账号（用户名称）
     */
    @Excel(name = "创建人")
    @TableField(exist = false)
    @ApiModelProperty(value = "创建人昵称（用户昵称）")
    private String creatorAccountName;

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
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
     * 更新人账号（用户名称）
     */
    @Excel(name = "更新人")
    @TableField(exist = false)
    @ApiModelProperty(value = "更新人昵称（用户昵称）")
    private String updaterAccountName;

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
     * 确认人账号（用户名称）
     */
    @Excel(name = "确认人")
    @TableField(exist = false)
    @ApiModelProperty(value = "确认人昵称（用户昵称）")
    private String confirmerAccountName;

    @ApiModelProperty(value = "确认人账号（用户名称）")
    private String confirmerAccount;

    /**
     * 确认时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "确认时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 数据源系统（数据字典的键值）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值）")
    private String dataSourceSys;

    @TableField(exist = false)
    @ApiModelProperty(value = "加工采购价格记录明细表")
    private List<PurOutsourcePriceInforItem> purOutsourcePriceInforItemList;

}
