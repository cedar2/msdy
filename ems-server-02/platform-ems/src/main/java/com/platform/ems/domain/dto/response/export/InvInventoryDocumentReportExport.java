package com.platform.ems.domain.dto.response.export;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 销售出入库明细报表
 *
 * @author chenkw
 * @date 2023-7-04
 */
@Data
@ApiModel
@Accessors(chain = true)
public class InvInventoryDocumentReportExport {

    @Excel(name = "库存凭证单号")
    @ApiModelProperty(value = "库存凭证号")
    private String inventoryDocumentCode;

    @Excel(name = "凭证类别")
    @ApiModelProperty(value = "凭证类别")
    private String documentCategoryName;

    @Excel(name = "作业类型")
    @ApiModelProperty(value = "作业类型名称")
    private String movementTypeName;

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

    @Excel(name = "商品/物料编码")
    @ApiModelProperty(value ="物料编码")
    private String materialCode;

    @Excel(name = "商品/物料名称")
    @ApiModelProperty(value ="物料名称")
    private String materialName;

    @Excel(name = "sku1名称")
    @ApiModelProperty(value ="sku1名称")
    private String sku1Name;

    @Excel(name = "sku2名称")
    @ApiModelProperty(value ="sku2名称")
    private String sku2Name;

    @Excel(name = "数量")
    @ApiModelProperty(value = "数量")
    private BigDecimal quantity;

    @Excel(name = "基本计量单位")
    @ApiModelProperty(value = "基本计量单位")
    private String unitBaseName;

    @Excel(name = "价格（含税）",scale = 5)
    @ApiModelProperty(value = "加权平均价")
    private BigDecimal price;

    @Excel(name = "金额",scale = 2)
    @ApiModelProperty(value = "金额")
    private BigDecimal currencyAmount;

    @Excel(name = "数量(价格单位)")
    @ApiModelProperty(value = "价格计量单位对应的出入库数量（采购订单/销售订单）")
    private BigDecimal priceQuantity;

    @Excel(name = "价格单位")
    @ApiModelProperty(value = "价格计量单位（数据字典的键值）采购订单/销售订单")
    private String unitPriceName;

    @Excel(name = "单位换算比例")
    @ApiModelProperty(value = "单位换算比例（价格单位/基本单位）")
    private BigDecimal unitConversionRate;

    @Excel(name = "免费",dictType = "sys_yes_no")
    @ApiModelProperty(value = "免费")
    private String freeFlag;

    @Excel(name = "所属业务类型")
    @ApiModelProperty(value = "其它出入库所属业务类型（数据字典的键值或配置档案的编码）")
    private String businessTypeName;

    @Excel(name = "客户")
    @ApiModelProperty(value = "客户名称")
    private String customerName;

    @Excel(name = "货运单号")
    @ApiModelProperty(value = "货运单号")
    private String carrierNoteCode;

    @Excel(name = "过账人")
    @ApiModelProperty(value = "过账人")
    private String storehouseOperatorName;

    @Excel(name = "价格（不含税）",scale = 6)
    @ApiModelProperty(value = "价格（不含税）")
    private BigDecimal ExcludingPriceTax;

    @Excel(name = "税率")
    @ApiModelProperty(value = "税率")
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

    @Excel(name = "物料分类")
    @ApiModelProperty(value = "物料分类名称")
    private String materialClassName;

    @Excel(name = "规格尺寸")
    @ApiModelProperty(value = "规格")
    private String specificationSize;

    @Excel(name = "商品条码")
    @ApiModelProperty(value = "系统SID-商品条码")
    private Long barcode;

    @Excel(name = "款备注")
    @ApiModelProperty(value = "商品编码备注(适用于物料对应的商品)，如合格证、洗唛；可以保存多个")
    private String productCodes;

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

}
