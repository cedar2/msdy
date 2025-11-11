package com.platform.ems.domain.dto.response;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 销售价导出响应实体
 *
 * @author yangqz
 * @date 2021-7-12
 */
@Data
@ApiModel
@Accessors(chain = true)
public class SaleReportExResponse implements Serializable {

    @ApiModelProperty(value = "销售价信息编号")
    @Excel(name = "销售价信息编号")
    private String salePriceCode;

    @ApiModelProperty(value = "查询:客户名称")
    @Excel(name = "客户(销售价)")
    private String customerName;

    @ApiModelProperty(value = "查询：物料编码")
    @Excel(name = "物料/商品编码")
    private String materialCode;

    @ApiModelProperty(value = "查询：物料名称")
    @Excel(name = "物料/商品名称")
    private String materialName;

    @Excel(name = "价格维度",dictType = "s_price_dimension")
    @ApiModelProperty(value = "价格维度")
    private String priceDimension;

    @Excel(name = "SKU1名称")
    @ApiModelProperty(value = "SKU1类型")
    private String sku1Name;

    @Excel(name = "客供料方式",dictType = "s_raw_material_mode")
    @ApiModelProperty(value = "客供料方式")
    @NotBlank(message = "客供料方式不能为空")
    private String rawMaterialMode;

    @Excel(name = "销售模式",dictType = "s_price_type")
    @ApiModelProperty(value = "销售模式")
    private String saleMode;


    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "有效期（起）", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "有效期（起）")
    @NotEmpty(message = "有效期（起）不能为空")
    private Date startDate;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "有效期（至）", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "有效期（至）")
    @NotEmpty(message = "有效期（至）不能为空")
    private Date endDate;

    @Excel(name = "销售价(含税)")
    @ApiModelProperty(value = "销售价(含税)")
    private BigDecimal salePriceTax;

    @Excel(name = "报价(含税)")
    @ApiModelProperty(value = "报价(含税)")
    private BigDecimal quotePriceTax;

    @ApiModelProperty(value = "销售价单位名称")
    @Excel(name = "销售价单位")
    private String unitPriceName;

    @Excel(name = "基本计量单位")
    @ApiModelProperty(value = "基本计量单位名称")
    private String unitBaseName;

    @Excel(name = "单位换算比例")
    @ApiModelProperty(value = "单位换算比例")
    private BigDecimal unitConversionRate;

    @ApiModelProperty(value = "税率名称")
    @Excel(name = "税率")
    private BigDecimal taxRateName;

    @ApiModelProperty(value = "物料类型名称（物料/商品/服务）")
    @Excel(name = "物料类型")
    private String materialTypeName;

    @ApiModelProperty(value = "变更说明")
    @Excel(name = "变更说明")
    private String updateRemark;

    @Excel(name = "价格说明")
    @ApiModelProperty(value = "价格说明")
    private String priceRemark;

    @Excel(name = "产品季")
    @ApiModelProperty(value = "查询:产品季")
    private String productSeasonName;

    /** 当前审批节点名称 */
    @ApiModelProperty(value = "当前审批节点名称")
    @Excel(name = "当前审批节点")
    private String approvalNode;

    /** 当前审批人 */
    @ApiModelProperty(value = "当前审批人")
    @Excel(name = "当前审批人")
    private String approvalUserName;

    @Excel(name = "处理状态",dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;
    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccountName;

    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;


    @Excel(name = "更改人")
    @TableField(exist = false)
    private String updaterAccountName;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    @Excel(name = "更改日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date updateDate;

    @Excel(name = "处理状态(主表)")
    @ApiModelProperty(value = "主表处理状态")
    private String headHandleStatus;

    @Excel(name = "客户(档案)")
    @ApiModelProperty(value = "档案:客户名称")
    private String customerNameMaterial;
}
