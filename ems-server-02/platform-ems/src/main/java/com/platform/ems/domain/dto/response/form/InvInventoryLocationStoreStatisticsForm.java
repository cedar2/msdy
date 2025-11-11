package com.platform.ems.domain.dto.response.form;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 库存统计报表按SKU
 *
 * @author chenkw
 * @date 2023-01-31
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InvInventoryLocationStoreStatisticsForm {

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品&服务")
    private Long materialSid;

    @Excel(name = "物料/商品编码")
    @ApiModelProperty(value ="物料编码")
    private String materialCode;

    @Excel(name = "物料/商品名称")
    @ApiModelProperty(value ="物料名称")
    private String materialName;

    @ApiModelProperty(value ="物料编码名称模糊查询")
    private String materialCodeName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品sku1")
    private Long sku1Sid;

    @ApiModelProperty(value = "sku1类型")
    private String sku1Type;

    @ApiModelProperty(value = "sku1编码")
    private String sku1Code;

    @Excel(name = "SKU1属性名称")
    @ApiModelProperty(value = "sku1名称")
    private String sku1Name;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品sku2")
    private Long sku2Sid;

    @ApiModelProperty(value = "sku2类型")
    private String sku2Type;

    @ApiModelProperty(value = "sku2编码")
    private String sku2Code;

    @Excel(name = "SKU2属性名称")
    @ApiModelProperty(value = "sku2名称")
    private String sku2Name;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-仓库")
    private Long storehouseSid;

    @ApiModelProperty(value = "系统SID-仓库（多选）")
    private Long[] storehouseSidList;

    @Excel(name = "仓库")
    @ApiModelProperty(value = "仓库")
    private String storehouseName;

    @Excel(name = "库存量")
    @ApiModelProperty(value = "库存量")
    private BigDecimal totalQuantity;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品条码")
    private Long barcodeSid;

    @ApiModelProperty(value = "基本计量单位编码")
    private String unitBase;

    @Excel(name = "基本计量单位")
    @ApiModelProperty(value = "基本计量单位名称")
    private String unitBaseName;

    @ApiModelProperty(value = "物料类型编码")
    private String materialType;

    @Excel(name = "物料类型")
    @ApiModelProperty(value = "物料类型名称")
    private String materialTypeName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料分类")
    private Long materialClassSid;

    @Excel(name = "物料分类")
    @ApiModelProperty(value = "物料分类名称")
    private String materialClassName;

    @Excel(name = "规格尺寸")
    @ApiModelProperty(value = "物料档案规格尺寸")
    private String specificationSize;

    @Excel(name = "幅宽")
    @ApiModelProperty(value = "幅宽（厘米）")
    private String width;

    @Excel(name = "克重")
    @ApiModelProperty(value = "克重")
    private String gramWeight;

    @Excel(name = "纱支")
    @ApiModelProperty(value = "纱支")
    private String yarnCount;

    @Excel(name = "密度")
    @ApiModelProperty(value = "密度")
    private String density;

    @Excel(name = "口型")
    @ApiModelProperty(value = "口型")
    private String zipperMonth;

    @Excel(name = "号型")
    @ApiModelProperty(value = "号型")
    private String zipperSize;

    @Excel(name = "商品SKU条码")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品条码")
    private Long barcode;

    @Excel(name = "预留库存量")
    @ApiModelProperty(value = "预留库存量")
    private BigDecimal yuliuQuantity;

    @Excel(name = "可用库存量")
    @ApiModelProperty(value = "可用库存量")
    private BigDecimal keyongQuantity;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-仓库库位库存信息")
    private Long storehouseMaterialSid;

    @Excel(name = "滞仓天数")
    @ApiModelProperty(value = "滞仓天数")
    private Integer latestStockEntryThroughDays;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "最近一次采购入库日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "最近一次采购入库日期")
    private Date latestPurchaseEntryDate;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "最近一次生产入库日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "最近一次生产入库日期")
    private Date latestManufactEntryDate;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "最近一次调拨入库日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "最近一次调拨入库日期")
    private Date latestTransferEntryDate;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "最近一次入库日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "最近一次入库日期")
    private Date latestStockEntryDate;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "最近一次销售出库日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "最近一次销售出库日期")
    private Date latestSaleOutDate;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "最近一次调拨出库日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "最近一次调拨出库日期")
    private Date latestTransferOutDate;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "最近一次领料出库日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "最近一次领料出库日期")
    private Date latestRequisitionOutDate;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "最近一次出库日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "最近一次出库日期")
    private Date latestStockOutDate;

    @ApiModelProperty(value = "物料类型（多选）")
    private String[] materialTypeList;

    @TableField(exist = false)
    @ApiModelProperty(value ="每页个数")
    private Integer pageNum;

    @TableField(exist = false)
    @ApiModelProperty(value ="每页个数")
    private Integer pageSize;

    @ApiModelProperty(value ="分页起始数")
    private Integer pageBegin;

    public Integer getPageBegin() {
        if (pageSize != null && pageNum != null){
            return pageSize*(pageNum-1);
        }else {
            return pageBegin;
        }
    }

    public void setPageBegin(Integer pageBegin) {
        if (pageSize != null && pageNum != null){
            this.pageBegin = this.pageSize*(this.pageNum-1);
        }else {
            this.pageBegin = pageBegin;
        }
    }

    @ApiModelProperty(value = "数据源系统（数据字典的键值）")
    private String dataSourceSys;

}
