package com.platform.ems.domain.dto.response;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Digits;
import java.math.BigDecimal;
import java.util.Date;

/**
 * BOM物料报表
 *
 * @author
 * @date 2021-03-15
 */
@Data
@ApiModel
@Accessors(chain = true)
public class TecBomHeadMaterialReportResponse {


    @Excel(name = "商品编码(款号)")
    @ApiModelProperty(value = "商品编码(款号)")
    private String materialCode ;

    @ApiModelProperty(value = "商品名称")
    @Excel(name = "商品名称")
    private String materialName;

    @Excel(name = "我司样衣号")
    @ApiModelProperty(value = "我司样衣号")
    private String sampleCodeSelf;

    @Excel(name = "物料分类")
    @ApiModelProperty(value = "物料分类名称")
    private String nodeName;

    /** 系统ID-物料BOM档案 */
    @ApiModelProperty(value = "系统ID-物料BOM档案")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long bomSid;

    @ApiModelProperty(value = "物料编码")
    @Excel(name = "物料编码")
    private String bomMaterialCode;

    private String materialSid;


    /** BOM组件物料编码 */
    @ApiModelProperty(value = "物料名称")
    @JsonSerialize(using = ToStringSerializer.class)
    @Excel(name = "物料名称")
    private String bomMaterialName;

    @ApiModelProperty(value = "物料颜色")
    @JsonSerialize(using = ToStringSerializer.class)
    private String bomMaterialSku1Name;

    @JsonSerialize(using = ToStringSerializer.class)
    private String bomMaterialSku1Sid;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long bomMaterialSid;

    @Excel(name = "供方编码")
    @ApiModelProperty(value = "供方编码（物料/商品/服务）")
    private String supplierProductCode;

    @Excel(name = "采购类型")
    @ApiModelProperty(value = "采购类型")
    private String purchaseTypeName;

    @Excel(name = "物料类型")
    @ApiModelProperty(value = "物料类型名称")
    private String materialTypeName;

    @Excel(name = "基本计量单位")
    @ApiModelProperty(value = "基本计量单位")
    private String unitBaseName;

    @Excel(name = "BOM用量单位")
    @ApiModelProperty(value = "bom用量计量单位")
    private String unitQuantityName;

    @Excel(name = "递增减价单位")
    @ApiModelProperty(value = "递增减价单位")
    private String unitRecursionName;


    @Excel(name = "所用生产环节",dictType = "s_touse_produce_stage")
    @ApiModelProperty(value = "所用生产环节")
    private String touseProduceStage;

    /** 供应商名称 */
    @Excel(name = "物料供应商")
    @TableField(exist = false)
    @ApiModelProperty(value = "供应商名称")
    private String vendorName;

    @Excel(name = "幅宽（厘米）")
    @ApiModelProperty(value = "幅宽")
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

    @Excel(name = "成分")
    @ApiModelProperty(value = "成分")
    private String composition;

    @Excel(name = "规格")
    @ApiModelProperty(value = "规格")
    private String specificationSize;

    @Excel(name = "材质")
    @ApiModelProperty(value = "材质")
    private String materialComposition;

    @ApiModelProperty(value = "查询：启用/停用(物料)")
    @Excel(name = "启用/停用(物料)", dictType = "s_valid_flag" )
    private String materialStatus;

    @ApiModelProperty(value = "处理状态(物料)")
    @Excel(name = "处理状态(物料)", dictType = "s_handle_status" )
    private String materialHandleStatus;

    @Excel(name = "行号")
    @ApiModelProperty(value = "行号")
    private String itemNum;

    @ApiModelProperty(value = "基准尺码")
    @Excel(name = "基准尺码")
    private String standardSkuName;


    @Excel(name = "产品季")
    @ApiModelProperty(value = "产品季名称")
    private String productSeasonName;

    @Excel(name = "客户")
    @ApiModelProperty(value = "客户名称")
    private String customerName;


    @Excel(name = "设计师")
    @ApiModelProperty(value = "设计师")
    private String designerAccountName;

    @Excel(name = "处理状态（BOM）",dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String bomHandleStatus;

}
