package com.platform.ems.domain.dto.response;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 库存凭证/甲供料结算单单明细报表响应实体
 *
 * @author yangqz
 * @date 2021-7-14
 */
@Data
@ApiModel
@Accessors(chain = true)
public class InvInventoryDocumentReportResponse implements Serializable {

    @Excel(name = "库存凭证单号")
    @ApiModelProperty(value = "库存凭证号")
    private String inventoryDocumentCode;

    private String  inventoryDocumentItemSid;

    @Excel(name = "凭证类别")
    @ApiModelProperty(value = "凭证类别")
    private String documentCategoryName;

    @Excel(name = "作业类型")
    @ApiModelProperty(value = "作业类型名称")
    private String movementTypeName;

    @ApiModelProperty(value = "作业类型名称")
    private String movementType;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "过账日期 ", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "过账日期")
    private Date accountDate;

    @Excel(name = "仓库")
    @ApiModelProperty(value = "仓库名称")
    private String storehouseName;

    @Excel(name = "库位")
    @ApiModelProperty(value = "库位名称")
    private String locationName;

    @ApiModelProperty(value = "物料类型")
    private String materialType;

    @ApiModelProperty(value = "物料类型")
    private String materialTypeName;

    @Excel(name = "物料/商品编码")
    @ApiModelProperty(value ="物料编码")
    private String materialCode;

    @Excel(name = "物料/商品名称")
    @ApiModelProperty(value ="物料名称")
    private String materialName;

    @ApiModelProperty(value = "物料类别")
    private String materialCategory;

    @Excel(name = "规格尺寸")
    @ApiModelProperty(value = "规格")
    @Length(max = 180, message = "规格尺寸不能超过180个字符")
    private String specificationSize;

    @Excel(name = "sku1名称")
    @ApiModelProperty(value ="sku1名称")
    private String sku1Name;

    @Excel(name = "sku2名称")
    @ApiModelProperty(value ="sku2名称")
    private String sku2Name;

    @TableField(exist = false)
    @ApiModelProperty(value = "sku1的序号")
    private BigDecimal sort1;

    @TableField(exist = false)
    @ApiModelProperty(value = "sku2的序号")
    private BigDecimal sort2;

    @Excel(name = "价格单位")
    @ApiModelProperty(value = "价格计量单位（数据字典的键值）采购订单/销售订单")
    private String unitPriceName;


    @Excel(name = "数量(价格单位)")
    @ApiModelProperty(value = "价格计量单位对应的出入库数量（采购订单/销售订单）")
    private BigDecimal priceQuantity;


    @Excel(name = "数量(基本单位)")
    @ApiModelProperty(value = "数量")
    private BigDecimal quantity;

    @Excel(name = "基本计量单位")
    @ApiModelProperty(value = "基本计量单位")
    private String unitBaseName;

    @Excel(name = "款备注")
    @ApiModelProperty(value = "商品编码备注(适用于物料对应的商品)，如合格证、洗唛；可以保存多个")
    private String productCodes;

    @ApiModelProperty(value = "型号")
    @Length(max = 180, message = "型号不能超过180个字符")
    private String modelSize;

    @Excel(name = "目的仓库")
    @ApiModelProperty(value = "目的仓库名称")
    private String destStorehouseName;

    @Excel(name = "目的库位")
    @ApiModelProperty(value = "目的库位名称")
    private String destLocationName;

    @Excel(name = "单价（含税）",scale = 5)
    @ApiModelProperty(value = "加权平均价")
    private BigDecimal price;

    @Excel(name = "金额(价格单位)",scale = 2)
    @ApiModelProperty(value = "金额(价格单位)")
    private BigDecimal currencyAmount;


    @Excel(name = "特殊库存")
    @ApiModelProperty(value = "特殊库存（数据字典的键值或配置档案的编码）")
    private String specialStockName;



    @Excel(name = "单位换算比例")
    @ApiModelProperty(value = "单位换算比例（价格单位/基本单位）")
    private BigDecimal unitConversionRate;


    @Excel(name = "免费",dictType = "sys_yes_no")
    @ApiModelProperty(value = "免费")
    private String freeFlag;

    @Excel(name = "业务标识", dictType = "s_stock_business_flag")
    @ApiModelProperty(value = "其它出入库对应的业务标识（数据字典的键值或配置档案的编码）")
    private String businessFlag;

    @Excel(name = "所属业务类型")
    @ApiModelProperty(value = "其它出入库所属业务类型（数据字典的键值或配置档案的编码）")
    private String businessTypeName;

    @Excel(name = "供应商")
    @ApiModelProperty(value = "供应商名称")
    private String vendorName;

    @ApiModelProperty(value ="入库状态")
    private String inOutStockStatus;

    @Excel(name = "客户")
    @ApiModelProperty(value = "客户名称")
    private String customerName;

    @ApiModelProperty(value = "我司样衣号")
    private String sampleCodeSelf;

    @ApiModelProperty(value = "季节")
    private String seasonName;

    @ApiModelProperty(value = "成分")
    private String composition;

    @ApiModelProperty(value = "版型名称")
    private String modelName;

    @ApiModelProperty(value = "吊牌零售价（元）")
    private BigDecimal retailPrice;

    @ApiModelProperty(value = "快速编码")
    private String simpleCode;

    @Excel(name = "供方交货单号")
    @ApiModelProperty(value = "供方送货单号（供方交货单号）")
    private String supplierDeliveryCode;

    @Excel(name = "货运单号")
    @ApiModelProperty(value = "货运单号")
    private String carrierNoteCode;

    @Excel(name = "盘点结果",dictType = "s_stock_count_result")
    @ApiModelProperty(value = "盘点结果标识")
    private String stockCountResult;

    @ApiModelProperty(value = "价格（不含税）")
    @Excel(name = "单价（不含税）",scale = 6)
    private BigDecimal ExcludingPriceTax;

    @ApiModelProperty(value = "税率")
    @Excel(name = "税率")
    private BigDecimal taxRate;

    @Excel(name = "过账年份")
    @ApiModelProperty(value = "过账年份")
    private String year;

    @Excel(name = "过账月份")
    @ApiModelProperty(value = "过账月份")
    private String month;

    @Excel(name = "下单季")
    @ApiModelProperty(value = "产品季/下单季档案编码（数据字典的键值或配置档案的编码）")
    private String productSeasonName;

    @Excel(name = "过账人")
    @ApiModelProperty(value = "过账人")
    private String storehouseOperatorName;

    @Excel(name = "公司")
    @ApiModelProperty(value = "公司")
    private String companyName;

    @Excel(name = "物料分类")
    @ApiModelProperty(value = "物料分类名称")
    private String materialClassName;



    @Excel(name = "商品条码")
    @ApiModelProperty(value = "系统SID-商品条码")
    private Long barcode;

    @Excel(name = "款颜色备注")
    @ApiModelProperty(value = "款颜色备注(适用于物料对应的商品)，如合格证、洗唛；可以保存多个")
    private String productSku1Names;

    @Excel(name = "款尺码备注")
    @ApiModelProperty(value = "款尺码备注(适用于物料对应的商品)；可以保存多个")
    private String productSku2Names;

    @Excel(name = "款数量备注")
    @ApiModelProperty(value = "款数量备注(适用于物料对应的商品)；可以保存多个")
    private String productQuantityRemark;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value ="处理状态")
    private String handleStatus;

    @Excel(name = "行号")
    @ApiModelProperty(value = "行号")
    private int itemNum;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人")
    private String creatorAccountName;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建日期")
    private Date createDate;

    @Excel(name = "关联单据类别")
    @ApiModelProperty(value = "单据类别名称")
    private String referDocCategoryName;

    @Excel(name = "关联业务单号")
    @ApiModelProperty(value = "关联业务单号")
    private String referDocumentCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-关联业务单行")
    private Long referDocumentItemSid;

    @ApiModelProperty(value = "采购订单号")
    private String purchaseOrderCode;

    @ApiModelProperty(value = "销售订单号")
    private String salesOrderCode;

    @TableField(exist = false)
    private String firstSort;

    @TableField(exist = false)
    private String secondSort;

    @TableField(exist = false)
    private String thirdSort;

}
