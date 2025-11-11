package com.platform.ems.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;
import com.platform.ems.util.data.KeepTwoDecimalsSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Digits;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 外发加工费结算单-明细对象 s_man_manufacture_outsource_settle_item
 *
 * @author linhongwei
 * @date 2021-06-10
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_man_manufacture_outsource_settle_item")
public class ManManufactureOutsourceSettleItem extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-外发加工费结算单明细
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-外发加工费结算单明细")
    private Long manufactureOutsourceSettleItemSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "sid数组")
    private Long[] manufactureOutsourceSettleItemSidList;

    /**
     * 系统SID-外发加工费结算单
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-外发加工费结算单")
    private Long manufactureOutsourceSettleSid;

    @Excel(name = "外发加工费结算单号")
    @ApiModelProperty(value = "外发加工费结算单号")
    private String manufactureOutsourceSettleCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-生产订单")
    private Long manufactureOrderSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "生产订单号")
    private Long manufactureOrderCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-生产订单-工序")
    private Long manufactureOrderProcessSid;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "加工商sid")
    private Long vendorSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "加工商list")
    private Long[] vendorSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "加工商编码")
    private String vendorCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "加工商名称")
    private String vendorName;

    @Excel(name = "加工商")
    @TableField(exist = false)
    @ApiModelProperty(value = "加工商名称")
    private String vendorShortName;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "工厂sid")
    private Long plantSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "工厂list")
    private Long[] plantSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "工厂编码")
    private String plantCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "工厂名称")
    private String plantName;

    @Excel(name = "工厂")
    @TableField(exist = false)
    @ApiModelProperty(value = "工厂简称")
    private String plantShortName;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司sid")
    private Long companySid;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司list")
    private Long[] companySidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司编码")
    private String companyCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @Excel(name = "公司")
    @TableField(exist = false)
    @ApiModelProperty(value = "公司简称")
    private String companyShortName;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品&服务")
    private Long purchaseContractSid;

    @TableField(exist = false)
    @Excel(name = "采购合同号")
    @ApiModelProperty(value = "采购合同号")
    private String purchaseContractCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品&服务")
    private Long materialSid;

    @Excel(name = "商品编码")
    @ApiModelProperty(value = "商品编码")
    private String materialCode;

    @TableField(exist = false)
    @Excel(name = "商品名称")
    @ApiModelProperty(value = "商品名称")
    private String materialName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品sku1")
    private Long sku1Sid;

    @ApiModelProperty(value = "系统CODE-物料&商品sku1")
    private String sku1Code;

    @TableField(exist = false)
    @Excel(name = "SKU1名称")
    @ApiModelProperty(value = "物料&商品sku1")
    private String sku1Name;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品sku2")
    private Long sku2Sid;

    @ApiModelProperty(value = "系统CODE-物料&商品sku2")
    private String sku2Code;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料&商品sku2")
    private String sku2Name;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-工序")
    private Long processSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统自增长ID-工序")
    private Long[] processSidList;

    @ApiModelProperty(value = "系统CODE-工序")
    private String processCode;

    @TableField(exist = false)
    @Excel(name = "工序名称")
    @ApiModelProperty(value = "工序名称")
    private String processName;

    @TableField(exist = false)
    @ApiModelProperty(value = "工序批次号")
    private String processBatchNum;

    @Excel(name = "完成量", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "完成量")
    private BigDecimal completeQuantity;

    @Excel(name = "次品量", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "次品量")
    private BigDecimal defectiveQuantity;

    @Digits(integer = 8, fraction = 3, message = "合格量整数位上限为8位，小数位上限为3位")
    @Excel(name = "合格量", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "合格量")
    private BigDecimal settleQuantity;

    @Excel(name = "加工价(含税)")
    @ApiModelProperty(value = "加工价(含税)")
    private BigDecimal priceTax;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统最新的加工价")
    private BigDecimal nowPriceTax;

    @ApiModelProperty(value = "加工价(不含税)")
    private BigDecimal price;

    @Excel(name = "次品允许比例(%)")
    @ApiModelProperty(value = "次品允许比例（存值，即：不含百分号，如20%，就存0.2）")
    private String defectiveAllowableRate;

    @Digits(integer = 8, fraction = 3, message = "次品允许数整数位上限为8位，小数位上限为3位")
    @Excel(name = "次品允许数", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "次品允许数")
    private BigDecimal defectiveAllowableQuantity;

    @Digits(integer = 8, fraction = 3, message = "次品量(允许范围内)整数位上限为8位，小数位上限为3位")
    @ApiModelProperty(value = "次品量(允许范围内)")
    private BigDecimal inDefectiveQuantity;

    @Excel(name = "次品扣款价(允许范围内)", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "次品扣款价(允许范围内)(含税)")
    private BigDecimal inDefectivePriceTax;

    @ApiModelProperty(value = "次品扣款价格(允许范围内)(不含税)")
    private BigDecimal inDefectivePrice;

    @Digits(integer = 8, fraction = 3, message = "次品量(超出范围)整数位上限为8位，小数位上限为3位")
    @Excel(name = "次品量(超出范围)", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "次品量(超出范围)")
    private BigDecimal outDefectiveQuantity;

    @Excel(name = "次品扣款价(超出范围)", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "次品扣款价格(超出范围)(含税)")
    private BigDecimal outDefectivePriceTax;

    @ApiModelProperty(value = "次品扣款价格(超出范围)(不含税)")
    private BigDecimal outDefectivePrice;

    @Excel(name = "其它扣款", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "其它扣款(含税)")
    private BigDecimal otherDeductionTax;

    @ApiModelProperty(value = "其它扣款(不含税)")
    private BigDecimal otherDeduction;

    @Excel(name = "加工金额(含税)", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "加工金额(含税)")
    private BigDecimal processAmountTax;

    @Excel(name = "扣款小计", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "扣款小计(含税)")
    private BigDecimal sumDeductionTax;

    @Excel(name = "结算金额(含税)", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "结算金额(含税)")
    private BigDecimal settleAmountTax;

    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @Digits(integer = 11, fraction = 4, message = "次品成本价格(含税)整数位上限为11位，小数位上限为4位")
    @ApiModelProperty(value = "次品成本价格(含税)")
    private BigDecimal costPriceTax;

    @TableField(exist = false)
    @ApiModelProperty(value = "待结算量")
    private String tobeQuantity;

    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @TableField(exist = false)
    @ApiModelProperty(value = "结算金额")
    private BigDecimal amount;

    @ApiModelProperty(value = "次品成本价格(不含税)")
    private BigDecimal costPrice;

    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @TableField(exist = false)
    @ApiModelProperty(value = "次品抵扣金额(含税)")
    private BigDecimal defectiveAmount;

    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @TableField(exist = false)
    @ApiModelProperty(value = "应付金额(含税)")
    private BigDecimal amountPayable;

    @TableField(exist = false)
    @ApiModelProperty(value = "已收货量")
    private String receivingQuantity;

    @TableField(exist = false)
    @ApiModelProperty(value = "计划产量")
    private String planQuantity;

    @Excel(name = "部位说明")
    @ApiModelProperty(value = "部位说明")
    private String positionDesc;

    @Excel(name = "工艺说明")
    @ApiModelProperty(value = "工艺说明")
    private String processDesc;

    @ApiModelProperty(value = "工艺图片")
    private String processPicture;

    @ApiModelProperty(value = "采购价计量单位（数据字典的键值或配置档案的编码）")
    private String unitPrice;

    @TableField(exist = false)
    @Excel(name = "采购价单位")
    @ApiModelProperty(value = "采购价计量单位名称")
    private String unitPriceName;

    @ApiModelProperty(value = "基本计量单位（数据字典的键值或配置档案的编码）")
    private String unitBase;

    @TableField(exist = false)
    @Excel(name = "基本计量单位")
    @ApiModelProperty(value = "计量单位名称")
    private String unitBaseName;

    @Excel(name = "单位换算比例")
    @ApiModelProperty(value = "单位换算比例（采购价单位/基本单位）")
    private BigDecimal unitConversionRate;

    @Excel(name = "税率")
    @ApiModelProperty(value = "税率（存值，即：不含百分号，如20%，就存0.2）")
    private BigDecimal taxRate;

    @ApiModelProperty(value = "是否免费（数据字典的键值或配置档案的编码）")
    private String freeFlag;

    @Excel(name = "行号")
    @ApiModelProperty(value = "行号")
    private Long itemNum;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    @TableField(exist = false)
    @ApiModelProperty(value = "单据类型")
    private String documentType;

    @TableField(exist = false)
    @ApiModelProperty(value = "单据类型多选")
    private String[] documentTypeList;

    @TableField(exist = false)
    @Excel(name = "单据类型")
    @ApiModelProperty(value = "单据类型")
    private String documentTypeName;

    @TableField(exist = false)
    @ApiModelProperty(value = "业务类型")
    private String businessType;

    @TableField(exist = false)
    @ApiModelProperty(value = "业务类型多选")
    private String[] businessTypeList;

    @TableField(exist = false)
    @Excel(name = "业务类型")
    @ApiModelProperty(value = "业务类型")
    private String businessTypeName;

    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "单据日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "单据日期")
    private Date documentDate;

    @TableField(exist = false)
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态多选")
    private String[] handleStatusList;

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    @TableField(exist = false)
    @ApiModelProperty(value = "创建人多选")
    private String[] creatorAccountList;

    @Excel(name = "创建人")
    @TableField(exist = false)
    @ApiModelProperty(value = "创建人名称")
    private String creatorAccountName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

}
