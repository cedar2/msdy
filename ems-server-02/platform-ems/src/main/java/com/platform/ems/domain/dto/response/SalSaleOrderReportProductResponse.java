package com.platform.ems.domain.dto.response;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 销售订单明细-排产进度报表
 *
 * @author yangqz
 * @date 2021-7-12
 */
@Data
@ApiModel
@Accessors(chain = true)
public class SalSaleOrderReportProductResponse {

    @Excel(name = "销售订单号")
    @ApiModelProperty(value = "销售订单号")
    private String salesOrderCode;

    @Excel(name = "客户")
    @ApiModelProperty(value = "客户名称")
    private String customerName;

    @ApiModelProperty(value = "销售员")
    @Excel(name = "销售员")
    private String nickName;

    @ApiModelProperty(value = "物料编码")
    @Excel(name = "商品/物料编码")
    private String materialCode;

    @Excel(name = "商品/物料名称")
    @ApiModelProperty(value = "物料名称")
    private String materialName;

    @Excel(name = "sku1（颜色）")
    @ApiModelProperty(value = "sku1名称")
    private String sku1Name;

    @Excel(name = "sku2（尺码）")
    @ApiModelProperty(value = "sku2名称")
    private String sku2Name;

    @Excel(name = "订单量")
    @ApiModelProperty(value = "订单量")
    private BigDecimal quantity;

    @Excel(name = "排产状态", dictType = "s_produce_status")
    @ApiModelProperty(value = "排产状态")
    private String quantityStatus;

    @Excel(name = "待排产量")
    @ApiModelProperty(value = "待排产量")
    private BigDecimal notQuantity;

    @Excel(name = "已排产量")
    @ApiModelProperty(value = "已排产量")
    private BigDecimal alreadyQuantity;

    @Excel(name = "已生产完工量")
    @ApiModelProperty(value = "已生产完工量")
    private BigDecimal completeQuantity;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "合同交期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "合同交期")
    private Date contractDate;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "需求日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "需求日期")
    private Date demandDate;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "最晚需求日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "最晚需求日期")
    private Date latestDemandDate;

    @Excel(name = "基本计量单位")
    @ApiModelProperty(value = "计量单位名称")
    private String unitBaseName;

    @Excel(name = "销售价单位")
    @ApiModelProperty(value = "销售单位")
    private String unitPriceName;

    @Excel(name = "单位换算比例")
    @ApiModelProperty(value = "单位换算比例")
    private BigDecimal unitConversionRate;

    @Excel(name = "物料类型")
    @ApiModelProperty(value = "物料类型名称")
    private String materialTypeName;

    @ApiModelProperty(value = "销售合同号(纸质合同)")
    private String paperSaleContractCode;

    @Excel(name = "销售合同号")
    @ApiModelProperty(value = "合同名称")
    private String saleContractCode;

    @ApiModelProperty(value = "备注")
    @Excel(name = "备注")
    private String remark;

    @ApiModelProperty(value = "商品条码")
    @Excel(name = "商品条码")
    private String barcode;

    @Excel(name = "客供料方式",dictType ="s_raw_material_mode" )
    @ApiModelProperty(value = "供料方式")
    private String rawMaterialMode;

    @Excel(name = "处理状态",dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;


    @Excel(name = "行号")
    @ApiModelProperty(value = "行号")
    private Integer itemNum;

    /** 创建人账号 */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccountName;

    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;
}
