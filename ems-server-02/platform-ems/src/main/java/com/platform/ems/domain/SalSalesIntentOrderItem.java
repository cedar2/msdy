package com.platform.ems.domain;

import java.math.BigDecimal;
import java.util.Date;

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

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.experimental.Accessors;

/**
 * 销售意向单-明细对象 s_sal_sales_intent_order_item
 *
 * @author chenkw
 * @date 2022-10-17
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_sal_sales_intent_order_item")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SalSalesIntentOrderItem extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-销售意向单明细表
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-销售意向单明细表")
    private Long salesIntentOrderItemSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] salesIntentOrderItemSidList;

    /**
     * 系统SID-销售意向单
     */
    @Excel(name = "系统SID-销售意向单")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-销售意向单")
    private Long salesIntentOrderSid;

    /**
     * 商品&物料&服务sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "商品&物料&服务sid")
    private Long materialSid;

    /**
     * 物料（商品/服务）名称
     */
    @Excel(name = "物料（商品/服务）名称")
    @ApiModelProperty(value = "物料（商品/服务）名称")
    private String materialName;

    /**
     * 商品&物料&服务编码
     */
    @Excel(name = "商品&物料&服务编码")
    @ApiModelProperty(value = "商品&物料&服务编码")
    private String materialCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品&物料&服务类型")
    private String materialTypeName;

    /**
     * 商品&物料sku1 sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "商品&物料sku1 sid")
    private Long sku1Sid;

    /**
     * 商品&物料sku1类型
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "商品&物料sku1类型")
    private String sku1Type;

    /**
     * 商品&物料sku1编码
     */
    @Excel(name = "商品&物料sku1编码")
    @ApiModelProperty(value = "商品&物料sku1编码")
    private String sku1Code;

    /**
     * 商品&物料sku1
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "商品&物料sku1名称")
    private String sku1Name;

    /**
     * 商品&物料sku2 sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "商品&物料sku2 sid")
    private Long sku2Sid;

    /**
     * 商品&物料sku2类型
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "商品&物料sku2类型")
    private String sku2Type;


    /**
     * 商品&物料sku2编码
     */
    @Excel(name = "商品&物料sku2编码")
    @ApiModelProperty(value = "商品&物料sku2编码")
    private String sku2Code;

    /**
     * 商品&物料sku2
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "商品&物料sku2名称")
    private String sku2Name;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料商品主图片路径")
    private String picturePath;

    @TableField(exist = false)
    @ApiModelProperty(value = "sku1的序号")
    private BigDecimal sort1;

    @TableField(exist = false)
    @ApiModelProperty(value = "sku2的序号")
    private BigDecimal sort2;

    /**
     * 物料&商品&服务条码sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "物料&商品&服务条码sid")
    private Long barcodeSid;

    /**
     * 物料&商品&服务条码
     */
    @Excel(name = "物料&商品&服务条码")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "物料&商品&服务条码")
    private Long barcode;

    /**
     * 基本计量单位（数据字典的键值或配置档案的编码）
     */
    @NotBlank(message = "基本计量单位不能为空")
    @ApiModelProperty(value = "基本计量单位（数据字典的键值或配置档案的编码）")
    private String unitBase;

    @TableField(exist = false)
    @Excel(name = "基本计量单位")
    @ApiModelProperty(value = "基本计量单位")
    private String unitBaseName;

    /**
     * 销售价计量单位（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "销售价计量单位（数据字典的键值或配置档案的编码）")
    private String unitPrice;

    @TableField(exist = false)
    @Excel(name = "销售价计量单位")
    @ApiModelProperty(value = "销售价计量单位")
    private String unitPriceName;

    /**
     * 销售量
     */
    @NotNull(message = "销售量不能为空")
    @Digits(integer = 7,fraction = 4, message = "销售量整数位上限为7位，小数位上限为4位")
    @Excel(name = "销售量")
    @ApiModelProperty(value = "销售量")
    private BigDecimal quantity;

    /**
     * 销售价(含税)
     */
    @Digits(integer = 10,fraction = 5, message = "销售价(含税)整数位上限为10位，小数位上限为5位")
    @Excel(name = "销售价(含税)")
    @ApiModelProperty(value = "销售价(含税)")
    private BigDecimal salePriceTax;

    /**
     * 合同交期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "合同交期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "合同交期")
    private Date contractDate;

    /**
     * 行号
     */
    @Excel(name = "行号")
    @ApiModelProperty(value = "行号")
    private Long itemNum;

    /**
     * 是否变更中
     */
    @Excel(name = "是否变更中", dictType = "s_yes_no")
    @ApiModelProperty(value = "是否变更中")
    private String isModifying;

    /**
     * 初始销售量(即销售意向单首次审批通过的销售量)
     */
    @Excel(name = "初始销售量(即销售意向单首次审批通过的销售量)")
    @ApiModelProperty(value = "初始销售量(即销售意向单首次审批通过的销售量)")
    private BigDecimal initialQuantity;

    /**
     * 初始合同交期(即销售意向单首次审批通过的合同交期)
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "初始合同交期(即销售意向单首次审批通过的合同交期)", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "初始合同交期(即销售意向单首次审批通过的合同交期)")
    private Date initialContractDate;

    /**
     * 新销售量(变更中)
     */
    @Excel(name = "新销售量(变更中)")
    @ApiModelProperty(value = "新销售量(变更中)")
    private BigDecimal newQuantity;

    /**
     * 新合同交期(变更中)
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "新合同交期(变更中)", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "新合同交期(变更中)")
    private Date newContractDate;

    /**
     * 创建人账号（用户名称）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

	@Excel(name = "创建人")
	@ApiModelProperty(value = "创建人昵称")
	@TableField(exist = false)
	private String creatorAccountName;

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
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

	@Excel(name = "更改人")
	@ApiModelProperty(value = "更改人昵称")
	@TableField(exist = false)
	private String updaterAccountName;

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

    /**
     * 销售意向单号
     */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售意向单号")
    private Long salesIntentOrderCode;

    /**
     * 意向销售合同/协议sid
     */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "意向销售合同/协议sid")
    private Long saleIntentContractSid;

    /**
     * 意向销售合同号/协议号
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "意向销售合同号/协议号")
    private String saleIntentContractCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    /**
     * 业务类型
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "业务类型")
    private String businessTypeName;

    /**
     * 公司名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    /**
     * 公司简称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "公司简称")
    private String companyShortName;

    /**
     * 客户名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "客户名称")
    private String customerName;

    /**
     * 客户简称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "客户简称")
    private String customerShortName;

    /**
     * 产品季
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "产品季")
    private String productSeasonName;

    /**
     * 销售员
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "销售员")
    private String salePersonName;

    /**
     * 金额含税 = 量 * 价
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "金额含税")
    private BigDecimal currentAmountTax;

    @TableField(exist = false)
    private String firstSort;

    @TableField(exist = false)
    private String secondSort;

    @TableField(exist = false)
    private String thirdSort;
}
