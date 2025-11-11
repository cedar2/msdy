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

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 库存调整单明细报表响应实体
 *
 * @author yangqz
 * @date 2021-7-13
 */
@Data
@ApiModel
@Accessors(chain = true)
public class InvInventorySheetReportResponse implements Serializable {

    @Excel(name = "盘点单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "盘点单号")
    private Long inventorySheetCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "盘点单号")
    private Long inventorySheetSid;

    @Excel(name = "仓库")
    @ApiModelProperty(value = "仓库名称")
    private String storehouseName;

    @Excel(name = "库位")
    @ApiModelProperty(value = "库位名称")
    private String locationName;


    @Excel(name = "商品/物料编码")
    @ApiModelProperty(value = "商品编码")
    private String materialCode;

    @Excel(name = "商品/物料名称")
    @ApiModelProperty(value = "商品名称")
    private String materialName;

    @Excel(name = "sku1名称")
    @ApiModelProperty(value = "sku1名称")
    private String sku1Name;

    @Excel(name = "sku2名称")
    @ApiModelProperty(value = "sku2名称")
    private String sku2Name;

    @Excel(name = "盘点结果",dictType ="s_stock_count_result")
    @ApiModelProperty(value = "盘点结果标识")
    private String stockCountResult;

    @Excel(name = "账面库存量")
    @ApiModelProperty(value = "账面库存量")
    private BigDecimal stockQuantity;

    @Excel(name = "实盘量")
    @ApiModelProperty(value = "实盘量")
    private BigDecimal countQuantity;

    @Excel(name = "差异量")
    @ApiModelProperty(value = "差异量")
    private BigDecimal divQuantity;

    @Excel(name = "基本单位")
    @ApiModelProperty(value = "基本计量单位名称")
    private String unitBaseName;

    @Excel(name = "加权平均价")
    @ApiModelProperty(value = "价格（加权平均价）")
    private BigDecimal price;

    @Excel(name = "差异金额")
    @ApiModelProperty(value = "差异金额）")
    private BigDecimal divPrice;

    @Excel(name = "原因类型")
    @ApiModelProperty(value = "原因类型（数据字典的键值）")
    private String reasonTypeName;

    @Excel(name = "特殊库存")
    @ApiModelProperty(value = "特殊库存（数据字典的键值或配置档案的编码）")
    private String specialStockName;

    @Excel(name = "客户")
    @ApiModelProperty(value = "客户名称")
    private String customerName;

    @Excel(name = "供应商")
    @ApiModelProperty(value = "供应商名称")
    private String vendorName;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "开单日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "开单日期")
    private Date documentDate;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "计划盘点日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划盘点日期")
    private Date planCountDate;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "盘点结果录入日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "盘点结果录入日期")
    private Date countResultEnterDate;


    @Excel(name = "参考盘点作业单号")
    @ApiModelProperty(value = "参考单号")
    private String countTaskDocument;


    @Excel(name = "盘点过账人")
    @ApiModelProperty(value = "盘点过账人（用户名称）")
    private String accountorName;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "盘点过账日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "过账日期(盘点过账日期)")
    private Date accountDate;


    @Excel(name = "盘点凭证编号")
    @ApiModelProperty(value = "盘点凭证编码")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long inventoryDocumentCode;

    @Excel(name = "作业类型")
    @ApiModelProperty(value = "作业类型")
    private String movementTypeName;

    @Excel(name = "商品条码")
    @ApiModelProperty(value = "系统SID-商品条码")
    private Long barcode;


   // @Excel(name = "盘点状态",dictType = "s_count_status")
    @ApiModelProperty(value = "盘点状态（数据字典的键值）")
    private String countStatus;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    @Excel(name = "行号")
    @ApiModelProperty(value = "行号")
    private String itemNum;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人")
    private String creatorAccountName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @Excel(name = "处理状态",dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值）")
    private String handleStatus;
}
