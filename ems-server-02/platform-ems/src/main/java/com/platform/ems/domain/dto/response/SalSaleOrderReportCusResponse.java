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
 * 销售订单明细导出响应实体
 *
 * @author yangqz
 * @date 2021-7-12
 */
@Data
@ApiModel
@Accessors(chain = true)
public class SalSaleOrderReportCusResponse {

    @Excel(name = "客户寄售结算单号")
    @ApiModelProperty(value = "客户寄售结算单号")
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

    @Excel(name = "SKU1（颜色）")
    @ApiModelProperty(value = "sku1名称")
    private String sku1Name;

    @Excel(name = "SKU2（尺码）")
    @ApiModelProperty(value = "sku2名称")
    private String sku2Name;

    @Excel(name = "结算量")
    @ApiModelProperty(value = "结算量")
    private BigDecimal quantity;


    @Excel(name = "基本计量单位")
    @ApiModelProperty(value = "计量单位名称")
    private String unitBaseName;

    @Excel(name = "销售单位")
    @ApiModelProperty(value = "销售单位")
    private String unitPriceName;

    @Excel(name = "单位换算比例")
    @ApiModelProperty(value = "单位换算比例")
    private BigDecimal unitConversionRate;

    @TableField(exist = false)
    @Excel(name = "折扣")
    @ApiModelProperty(value = "折扣")
    private String discountTypeName;

    @Excel(name = "免费", dictType = "s_yesno_flag")
    @ApiModelProperty(value = "免费")
    private String freeFlag;

    /**
     * 采购价(含税)
     */
    @Excel(name = "销售价(含税)")
    @ApiModelProperty(value = "销售价(含税)")
    private BigDecimal salePriceTax;

    /**
     * 采购价(不含税)
     */
    @Excel(name = "销售价(不含税)")
    @ApiModelProperty(value = "销售价(不含税)")
    private BigDecimal salePrice;

    /**
     * 采购价(含税)
     */
    @Excel(name = "金额(含税)")
    @ApiModelProperty(value = "金额(含税)")
    private BigDecimal priceTax;

    /**
     * 采购价(不含税)
     */
    @Excel(name = "金额(不含税)")
    @ApiModelProperty(value = "金额(不含税)")
    private BigDecimal price;

    @Excel(name = "税率")
    @ApiModelProperty(value = "税率")
    private BigDecimal taxRate;

    @ApiModelProperty(value = "仓库名称")
    @Excel(name = "仓库")
    private String storehouseName;

    @ApiModelProperty(value = "库位名称")
    @Excel(name = "库位")
    private String locationName;

    @ApiModelProperty(value = "销售合同号(纸质合同)")
    private String paperSaleContractCode;

    @Excel(name = "销售合同/协议号")
    @ApiModelProperty(value = "销售协议号")
    private String saleContractCode;

    @Excel(name = "下单季")
    @ApiModelProperty(value = "下单季")
    private String productSeasonName;

    @Excel(name = "单据类型")
    @ApiModelProperty(value = "单据类型名称")
    private String documentTypeName;

    @Excel(name = "业务类型")
    @ApiModelProperty(value = "业务类型名称")
    private String businessTypeName;

    @ApiModelProperty(value = "备注")
    @Excel(name = "备注")
    private String remark;

    @ApiModelProperty(value = "商品条码")
    @Excel(name = "商品条码")
    private String barcode;

    @Excel(name = "行号")
    @ApiModelProperty(value = "行号")
    private Integer itemNum;

    @Excel(name = "物料类型")
    @ApiModelProperty(value = "物料类型名称")
    private String materialTypeName;

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

    @Excel(name = "明细行状态",dictType ="s_order_item_status")
    @ApiModelProperty(value = "明细行状态")
    private String itemStatus;
}
