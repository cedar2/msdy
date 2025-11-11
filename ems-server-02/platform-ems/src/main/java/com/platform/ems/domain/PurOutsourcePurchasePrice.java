package com.platform.ems.domain;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

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

/**
 * 加工采购价主对象 s_pur_outsource_purchase_price
 *
 * @author linhongwei
 * @date 2021-05-12
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_pur_outsource_purchase_price")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PurOutsourcePurchasePrice extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-加工采购价
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-加工采购价")
    private Long outsourcePurchasePriceSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] outsourcePurchasePriceSidList;
    /**
     * 加工采购价编码
     */
    @Excel(name = "加工采购价编码")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "加工采购价编码")
    private Long outsourcePurchasePriceCode;

    /**
     * 系统SID-供应商档案sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商档案sid")
    @NotNull(message = "供应商不能为空")
    private Long vendorSid;

    /**
     * 系统SID-物料档案（物料/商品/服务）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料档案（物料/商品/服务）")
    @NotNull(message = "物料不能为空")
    private Long materialSid;

    /**
     * 系统SID-SKU1档案sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-SKU1档案sid")
    private Long sku1Sid;

    /**
     * 系统SID-SKU2档案sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-SKU2档案sid")
    private Long sku2Sid;

    /**
     * 系统SID-商品条码（物料&商品&服务）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品条码（物料&商品&服务）")
    private Long barcodeSid;

    /** 系统自增长ID-工序 */
    @NotNull(message = "加工项不能为空")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-加工项")
    private Long processSid;


    @ApiModelProperty(value = "加工项")
    @TableField(exist = false)
    private String processName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "查询:加工项")
    @TableField(exist = false)
    private Long[] processSidList;

    @ApiModelProperty(value = "查询：供应商")
    @TableField(exist = false)
    private Long[] vendorSidList;

    /**
     * 公司编码（公司档案的sid）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司编码（公司档案的sid）")
    private Long companySid;

    @ApiModelProperty(value = "查询：公司")
    @TableField(exist = false)
    private Long[] companySidList;

    /**
     * 采购组织（数据字典的键值）
     */
    @Excel(name = "采购组织（数据字典的键值）")
    @ApiModelProperty(value = "采购组织（数据字典的键值）")
    private String purchaseOrg;

    /** 物料类别编码 */
    @Excel(name = "物料类别编码", dictType = "s_material_category")
    @ApiModelProperty(value = "物料类别编码")
    private String materialCategory;

    @TableField(exist = false)
    private String creatorAccountName;

    @TableField(exist = false)
    private String updaterAccountName;

    @TableField(exist = false)
    private String confirmerAccountName;

    /**
     * 价格维度（数据字典的键值）
     */
    @Excel(name = "价格维度（数据字典的键值）")
    @ApiModelProperty(value = "价格维度（数据字典的键值）")
    @NotEmpty(message = "价格维度不能为空")
    private String priceDimension;

    /**
     * 启用/停用状态（数据字典的键值）
     */
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "启用/停用状态（数据字典的键值）", dictType = "s_valid_flag")
    @ApiModelProperty(value = "启用/停用状态（数据字典的键值）")
    private String status;

    /**
     * 处理状态（数据字典的键值）
     */
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "处理状态（数据字典的键值）", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值）")
    private String handleStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：处理状态")
    private String[]  handleStatusList;


    /**
     * 创建人账号（用户名称）
     */
    @Excel(name = "创建人账号（用户名称）")
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
    @Excel(name = "更新人账号（用户名称）")
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
    @Excel(name = "确认人账号（用户名称）")
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
    @Excel(name = "数据源系统（数据字典的键值）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值）")
    private String dataSourceSys;


    @ApiModelProperty(value = "附件列表")
    @TableField(exist = false)
    private List<PurOutsourcePurchasePriceAttachment> attachmentList;

    @ApiModelProperty(value = "明细列表")
    @TableField(exist = false)
    private List<PurOutsourcePurchasePriceItem> itemList;

    @ApiModelProperty(value = "物料名称")
    @TableField(exist = false)
    private String materialName;

    @ApiModelProperty(value = "物料编码")
    @TableField(exist = false)
    private String materialCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料商品主图片路径")
    private String picturePath;

    @TableField(exist = false)
    @ApiModelProperty(value = "副图片路径（多图）")
    private String picturePathSecond;

    @TableField(exist = false)
    @ApiModelProperty(value = "图片路径（副图）")
    private String[] picturePathList;

    @ApiModelProperty(value = "sku1编码")
    @TableField(exist = false)
    private String sku1Code;

    @ApiModelProperty(value = "sku1名称")
    @TableField(exist = false)
    private String sku1Name;

    @ApiModelProperty(value = "sku2编码")
    @TableField(exist = false)
    private String sku2Code;

    @ApiModelProperty(value = "sku2名称")
    @TableField(exist = false)
    private String sku2Name;

    @ApiModelProperty(value = "商品条码")
    @TableField(exist = false)
    private String barcode;

    @ApiModelProperty(value = "")
    @TableField(exist = false)
    private String laborTypItemName;

    @ApiModelProperty(value = "")
    @TableField(exist = false)
    private String laborTypeItemCode;

    @ApiModelProperty(value = "供应商名称")
    @TableField(exist = false)
    private String vendorName;

    @ApiModelProperty(value = "供应商编码")
    @TableField(exist = false)
    private String vendorCode;

    @ApiModelProperty(value = "公司编码")
    @TableField(exist = false)
    private String companyCode;

    @ApiModelProperty(value = "公司名称")
    @TableField(exist = false)
    private String companyName;

    @TableField(exist = false)
    private String unitBaseName;

    @TableField(exist = false)
    private String unitBase;

    /** 系统自增长ID-工厂 */
    @TableField(exist = false)
    @ApiModelProperty(value = "系统自增长ID-工厂")
    private String plantSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "供方编码（物料/商品/服务）")
    private String supplierProductCode;

    /** 当前审批节点名称 */
    @Excel(name = "当前审批节点名称")
    @ApiModelProperty(value = "当前审批节点名称")
    @TableField(exist = false)
    private String approvalNode;

    /** 当前审批人 */
    @Excel(name = "当前审批人")
    @ApiModelProperty(value = "当前审批人")
    @TableField(exist = false)
    private String approvalUserName;

    /** 提交人 */
    @Excel(name = "提交人")
    @ApiModelProperty(value = "提交人")
    @TableField(exist = false)
    private String submitUserName;

    /**
     * 提交日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "提交日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "提交日期")
    @TableField(exist = false)
    private Date submitDate;

    @TableField(exist = false)
    private String judgeSubmit;

    @TableField(exist = false)
    private String importHandle;

    @ApiModelProperty(value = "校验-是否跳过插入 Y 是")
    @TableField(exist = false)
    private String skipInsert;
}
