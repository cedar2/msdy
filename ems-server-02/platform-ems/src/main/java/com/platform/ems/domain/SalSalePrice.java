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
 * 销售价信息对象 s_sal_sale_price
 *
 * @author linhongwei
 * @date 2021-03-05
 */
@Data
@TableName("s_sal_sale_price")
@ApiModel
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SalSalePrice  extends EmsBaseEntity {
    private static final long serialVersionUID = 1L;

    /** 客户端口号 */
    @ApiModelProperty(value = "客户端口号")
    @TableField(fill = FieldFill.INSERT)
    private String clientId;

    /** 销售价信息编号 */
    @ApiModelProperty(value = "销售价信息编号")
    @Excel(name = "销售价信息编号")
    private String salePriceCode;

    /** 系统自增长ID-销售价 */
    @TableId
    @ApiModelProperty(value = "系统自增长ID-销售价")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long salePriceSid;

    @TableField(exist = false)
    private Long[] salePriceSidList;

    /** 系统自增长ID-物料档案 */
    @ApiModelProperty(value = "系统自增长ID-物料档案")
    @NotNull(message = "物料编码不能为空")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long materialSid;

    /** 系统自增长ID-客户信息 */
    @ApiModelProperty(value = "系统自增长ID-客户信息")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long customerSid;

    /** 系统自增长ID-公司档案 */
    @ApiModelProperty(value = "系统自增长ID-公司档案")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long companySid;


    @ApiModelProperty(value = "查询：公司档案")
    @TableField(exist = false)
    private Long[] companySids;

    @ApiModelProperty(value = "公司名称")
    @Excel(name = "公司名称")
    @TableField(exist = false)
    private String companyName;

    /** 销售组织编码 */
    @Excel(name = "销售组织编码")
    @ApiModelProperty(value = "销售组织编码")
    private String saleOrg;

    @TableField(exist = false)
    private String importHandle;

    /** 价格维度 */
    @Excel(name = "价格维度")
    @ApiModelProperty(value = "价格维度")
    private String priceDimension;

    /** SKU1类型 */
    @ApiModelProperty(value = "SKU1编码sid")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long sku1Sid;

    @Excel(name = "SKU1类型")
    @TableField(exist = false)
    @ApiModelProperty(value = "SKU1类型")
    private String sku1Name;

    @TableField(exist = false)
    @ApiModelProperty(value = "SKU1类型")
    private String sku1Type;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：物料名称")
    @Excel(name = "物料名称")
    private String materialName;

    /** SKU2类型 */
    @Excel(name = "SKU2编码sid")
    @ApiModelProperty(value = "SKU2编码sid")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long sku2Sid;

    @Excel(name = "SKU2类型")
    @TableField(exist = false)
    @ApiModelProperty(value = "SKU2类型")
    private String sku2Name;

    @TableField(exist = false)
    @ApiModelProperty(value = "SKU2类型")
    private String sku2Type;

    @Excel(name = "销售模式")
    @ApiModelProperty(value = "销售模式")
    @NotBlank(message = "销售模式不能为空")
    private String saleMode;

    /** 递增减SKU类型 */
    @Excel(name = "递增减SKU类型")
    @ApiModelProperty(value = "递增减SKU类型")
    private String skuTypeRecursion;


    /** 物料类别编码 */
    @Excel(name = "物料类别编码", dictType = "s_material_category")
    @ApiModelProperty(value = "物料类别编码")
    private String materialCategory;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料商品主图片路径")
    private String picturePath;

    @TableField(exist = false)
    @ApiModelProperty(value = "副图片路径（多图）")
    private String picturePathSecond;

    @TableField(exist = false)
    @ApiModelProperty(value = "图片路径（副图）")
    private String[] picturePathList;

    /** 处理状态 */
    @Excel(name = "处理状态")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;


    @Excel(name = "业务渠道/销售渠道")
    @ApiModelProperty(value = "业务渠道/销售渠道")
    private String businessChannel;

    @Excel(name = "状态")
    @ApiModelProperty(value = "状态")
     private String status;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询:供料方式")
    private String[] rawMaterialModes;

    @Excel(name = "客供料方式")
    @ApiModelProperty(value = "客供料方式")
    @NotBlank(message = "客供料方式不能为空")
    private String rawMaterialMode;

    /** 创建人账号 */
    @Excel(name = "创建人账号")
    @ApiModelProperty(value = "创建人账号")
    @TableField(fill = FieldFill.INSERT)
    private String creatorAccount;

    /** 创建时间 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss:sss")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createDate;

    /** 更新人账号 */
    @Excel(name = "更新人账号")
    @ApiModelProperty(value = "更新人账号")
    @TableField(fill = FieldFill.UPDATE)
    private String updaterAccount;

    /** 更新时间 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss:sss")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.UPDATE)
    private Date updateDate;

    /** 确认人账号 */
    @Excel(name = "确认人账号")
    @ApiModelProperty(value = "确认人账号")
    private String confirmerAccount;

    /** 确认时间 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss:sss")
    @Excel(name = "确认时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    @ApiModelProperty(value = "备注")
    private String remark;

    @Excel(name = "数据源系统")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;

    @ApiModelProperty(value = "商品条码Sid")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long barcodeSid;

    @Excel(name = "商品条码")
    @ApiModelProperty(value = "商品条码")
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long barcode;

    @ApiModelProperty(value = "附件表")
    @TableField(exist = false)
    List<SalSalePriceAttachment> attachmentList;

    @ApiModelProperty(value = "明细表")
    @TableField(exist = false)
    List<SalSalePriceItem> listSalSalePriceItem;
    /**************************查询参数***************************/
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
    @ApiModelProperty(value = "查询:客户名称")
    @Excel(name = "客户名称")
    private String customerName;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询:客户编码")
    private String customerCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询:客户")
    private List<Long> customerSids;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：物料编码")
    private String materialCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：物料")
    private List<Long> materialSids;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：销售组织")
    private String[] saleOrgs;

    /** 价格类型 */
    @ApiModelProperty(value = "查询：价格类型")
    @TableField(exist = false)
    private String[] priceTypes;

    @ApiModelProperty(value = "查询：处理状态")
    @TableField(exist = false)
    private String[] handleStatuses;

    @ApiModelProperty(value = "查询：销售渠道")
    @TableField(exist = false)
    private String[] saleChannels;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：销售模式")
    private String[] saleModes;

    @TableField(exist = false)
    private Integer pageNum;

    @TableField(exist = false)
    private Integer pageSize;

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
    private String unitBaseName;

    @TableField(exist = false)
    private String unitBase;

    @TableField(exist = false)
    private String isCostSale;

    @ApiModelProperty(value = "校验-是否跳过插入 Y 是")
    @TableField(exist = false)
    private String skipInsert;


    @ApiModelProperty(value = "合同获取待审批状态")
    @TableField(exist = false)
    private String[] notApprovalStatus;
}
