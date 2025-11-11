package com.platform.ems.domain.dto.response.export;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 外采样导出 BasMaterialWcyExportResponse
 *
 * @author chenkaiwen
 * @date 2021-12-16
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasMaterialWcyExportResponse extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Excel(name = "外采样编码")
    @ApiModelProperty(value = "外采样编码")
    private String materialCode;

    @Excel(name = "外采样名称")
    @ApiModelProperty(value = "外采样名称")
    private String materialName;

    @Excel(name = "外采样颜色")
    @ApiModelProperty(value = "外采样颜色(外采样)")
    private String osbSampleColor;

    @Excel(name = "外采样尺码")
    @ApiModelProperty(value = "外采样尺码(外采样)")
    private String osbSampleSize;

    @Excel(name = "报销状态", dictType = "s_reimburse_status")
    @ApiModelProperty(value = "报销状态(外采样)")
    private String reimburseStatus;

    @Excel(name = "购买人")
    @ApiModelProperty(value = "购买人(外采样)")
    private String osbSampleBuyerName;

    @Excel(name = "购买公司")
    @ApiModelProperty(value = "购买公司(外采样)")
    private String purchaseCompanyName;

    @Excel(name = "购买部门")
    @ApiModelProperty(value = "购买部门(外采样)")
    private String purchaseOrgName;

    @Excel(name = "购买地")
    @ApiModelProperty(value = "购买地(外采样)")
    private String purchaseFrom;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "购买日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "购买日期(外采样)")
    private Date purchaseDate;

    @Excel(name = "采购价")
    @ApiModelProperty(value = "采购价(外采样)")
    private BigDecimal purchasePrice;

    @Excel(name = "基本计量单位")
    @ApiModelProperty(value = "基本计量单位名称")
    private String unitBaseName;

    @Excel(name = "购买量")
    @ApiModelProperty(value = "购买量(外采样)")
    private Long purchaseQuantity;

    @Excel(name = "样品类型")
    @ApiModelProperty(value = "物料类型名称（物料/商品/服务）")
    private String materialTypeName;

    @Excel(name = "样品分类")
    @ApiModelProperty(value = "物料分类名称")
    private String materialClassName;

    @Excel(name = "产品季")
    @ApiModelProperty(value = "产品季名称")
    private String productSeasonName;

    @Excel(name = "外采样品牌")
    @ApiModelProperty(value = "外采样品牌(外采样)")
    private String osbSampleBrand;

    @Excel(name = "外采样品牌货号")
    @ApiModelProperty(value = "外采样品牌货号(外采样)")
    private String osbSampleCode;

    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @ApiModelProperty(value = "备注")
    private String remark;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人")
    private String creatorAccountName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @Excel(name = "更改人")
    @ApiModelProperty(value = "更改人")
    private String updaterAccountName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更改时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "更改时间")
    private Date updateDate;

    @Excel(name = "确认人")
    @ApiModelProperty(value = "确认人")
    private String confirmerAccountName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "确认时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

}
