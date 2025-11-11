package com.platform.ems.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.BaseEntity;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * 采购价信息主对象 s_pur_purchase_price
 *
 * @author ChenPinzhen
 * @date 2021-02-04
 */
@ApiModel
@TableName("s_pur_purchase_price")
@Accessors(chain = true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PurPurchasePrice extends EmsBaseEntity {
    private static final long serialVersionUID = 1L;

    /** 客户端口号 */
    @ApiModelProperty(value = "客户端口号")
    @TableField(fill = FieldFill.INSERT)
    private String clientId;

    /** 系统ID-物料采购价信息 */
    @ApiModelProperty(value = "系统ID-物料采购价信息")
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    private Long purchasePriceSid;

    @TableField(exist = false)
    private Long[] purchasePriceSidList;

    /** 物料采购价信息编码 */
    @ApiModelProperty(value = "物料采购价信息编码")
    @Excel(name = "物料采购价信息编码")
    private String purchasePriceCode;


    /** 系统ID-供应商档案 */
    @ApiModelProperty(value = "系统ID-供应商档案")
    @NotNull(message = "供应商不能为空")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long vendorSid;

    @ApiModelProperty(value = "系统ID-物料档案")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long materialSid;

    @TableField(exist = false)
    @Excel(name = "供应商编码")
    @ApiModelProperty(value = "查询：供应商编码")
    private String vendorCode;

    @TableField(exist = false)
    @Excel(name = "供应商名称")
    @ApiModelProperty(value = "查询：供应商名称")
    private String vendorName;


    @TableField(exist = false)
    @Excel(name = "物料名称")
    @ApiModelProperty(value = "查询：物料名称")
    private String materialName;

    /** 物料类别编码 */
    @Excel(name = "物料类别编码", dictType = "s_material_category")
    @ApiModelProperty(value = "物料类别编码")
    private String materialCategory;


    @TableField(exist = false)
    @Excel(name = "物料编码")
    @ApiModelProperty(value = "查询：物料编码")
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

    @ApiModelProperty(value = "系统ID-公司档案")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long companySid;

    @ApiModelProperty(value = "公司名称")
    @Excel(name = "公司名称")
    @TableField(exist = false)
    private String companyName;

    @ApiModelProperty(value = "采购组织编码")
    @Excel(name = "采购组织编码",dictType = "s_purchase_org")
    private String purchaseOrg;

    /** 价格维度 */
    @ApiModelProperty(value = "价格维度")
    @Excel(name = "价格类别",dictType = "s_price_dimension")
    @NotBlank(message = "价格维度不能为空")
    private String priceDimension;

    /** 系统ID-SKU1档案 */
    @ApiModelProperty(value = "系统ID-SKU1档案")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long sku1Sid;

    /** 系统ID-SKU2档案 */
    @ApiModelProperty(value = "系统ID-SKU2档案")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long sku2Sid;

    @Excel(name = "SKU2类型")
    @TableField(exist = false)
    @ApiModelProperty(value = "SKU2类型")
    private String sku2Name;

    @TableField(exist = false)
    @ApiModelProperty(value = "SKU1类型")
    private String sku1Type;

    @TableField(exist = false)
    @ApiModelProperty(value = "SKU2类型")
    private String sku2Type;

    @Excel(name = "采购模式")
    @ApiModelProperty(value = "采购模式")
    @NotBlank(message = "采购模式不能为空")
    private String purchaseMode;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：采购模式")
    private String[] purchaseModes;

    @Excel(name = "SKU1类型")
    @TableField(exist = false)
    @ApiModelProperty(value = "SKU1类型")
    private String sku1Name;

    /** 递增减SKU类型 */
    @Excel(name = "递增减SKU类型")
    @ApiModelProperty(value = "递增减SKU类型")
    private String skuTypeRecursion;

    /** 启用/停用状态 */
    @Excel(name = "启用/停用状态", dictType = "s_valid_flag")
    @ApiModelProperty(value = "启用/停用状态")
    private String status;

    @ApiModelProperty(value = "供料方式")
    @Excel(name = "供料方式")
    private String rawMaterialMode;

    @ApiModelProperty(value = "查询：供料方式")
    @TableField(exist = false)
    private String[] rawMaterialModes;

    /** 处理状态 */
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    /** 创建人账号 */
    @Excel(name = "创建人账号")
    @ApiModelProperty(value = "创建人账号")
    @TableField(fill = FieldFill.INSERT)
    private String creatorAccount;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    private Date createDate;

    @ApiModelProperty(value = "备注")
    private String remark;

    /** 更新人账号 */
    @Excel(name = "更新人账号")
    @ApiModelProperty(value = "更新人账号")
    @TableField(fill = FieldFill.UPDATE)
    private String updaterAccount;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    private Date updateDate;

    /** 确认人账号 */
    @Excel(name = "确认人账号")
    @ApiModelProperty(value = "确认人账号")
    private String confirmerAccount;

    /** 确认时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    @Excel(name = "确认时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date confirmDate;

    @Excel(name = "商品条码Sid")
    @ApiModelProperty(value = "商品条码Sid")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long barcodeSid;

    @Excel(name = "商品条码")
    @ApiModelProperty(value = "商品条码")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(exist = false)
    private Long barcode;

    /** 数据源系统 */
    @Excel(name = "数据源系统")
    @ApiModelProperty(value = "数据源系统")
    @TableField(fill = FieldFill.INSERT)
    private String dataSourceSys;

    @TableField(exist = false)
    @ApiModelProperty(value = "附件表")
    List<PurPurchasePriceAttachment> attachmentList;

    @TableField(exist = false)
    @ApiModelProperty(value = "明细表")
    List<PurPurchasePriceItem> listPurPurchasePriceItem;

    @TableField(exist = false)
    @ApiModelProperty(value = "单个明细表")
    PurPurchasePriceItem listPurPurchasePrice;
     /***************************查询参数***********************************/
    @ApiModelProperty(value = "创建日期起")
    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    private String createDateStart;

    /** 创建日期至 */
    @ApiModelProperty(value = "创建日期至")
    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    private String createDateEnd;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：供应商")
    private List<Long> vendorSids;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：物料")
    private List<Long> materialSids;

    @TableField(exist = false)
    @ApiModelProperty(value = "供方编码（物料/商品/服务）")
    private String supplierProductCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：公司")
    private Long[] companySids;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：价格类型")
    private String[] priceTypes;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：处理状态")
    private String[] handleStatuses;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：采购组织")
    private String[] purchaseOrgs;

    @TableField(exist = false)
    private Integer pageNum;

    @TableField(exist = false)
    private Integer pageSize;

    @TableField(exist = false)
    private String unitBaseName;

    @TableField(exist = false)
    private String unitBase;

    @Excel(name = "更新人")
    @TableField(exist = false)
    private String updaterAccountName;

    @Excel(name = "创建人")
    @TableField(exist = false)
    private String creatorAccountName;

    @Excel(name = "确认人")
    @TableField(exist = false)
    private String confirmerAccountName;

    @TableField(exist = false)
    private String importHandle;

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

    @ApiModelProperty(value = "校验-是否跳过插入 Y 是")
    @TableField(exist = false)
    private String skipInsert;

    @ApiModelProperty(value = "采购类型编码（默认）")
    @TableField (exist = false)
    private String purchaseType;

    @ApiModelProperty(value = "合同获取待审批状态")
    @TableField(exist = false)
    private String[] notApprovalStatus;
}
