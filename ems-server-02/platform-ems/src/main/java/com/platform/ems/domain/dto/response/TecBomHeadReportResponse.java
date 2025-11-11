package com.platform.ems.domain.dto.response;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.ems.domain.BasMaterial;
import com.platform.ems.domain.TecBomSizeQuantity;
import com.platform.ems.domain.dto.request.BasBomColorRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Digits;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * bom 报表
 *
 * @author qhq
 * @date 2021-03-15
 */
@Data
@ApiModel
@Accessors(chain = true)
public class TecBomHeadReportResponse {


    @Excel(name = "商品编码(款号)")
    @ApiModelProperty(value = "商品编码(款号)")
    private String materialCode ;

    @ApiModelProperty(value = "商品名称")
    @Excel(name = "商品名称")
    private String materialName;

    private String materialSid;

    @Excel(name = "产品季")
    @ApiModelProperty(value = "产品季名称")
    private String productSeasonName;

    @Excel(name = "客户")
    @ApiModelProperty(value = "客户名称")
    private String customerName;

    /** 我司样衣号 */
    @Excel(name = "我司样衣号")
    @ApiModelProperty(value = "我司样衣号")
    private String sampleCodeSelf;

    @Excel(name = "设计师")
    @ApiModelProperty(value = "设计师")
    private String designerAccountName;

    @Excel(name = "处理状态(BOM)",dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @ApiModelProperty(value = "基准尺码")
    @Excel(name = "基准尺码")
    private String standardSkuName;

    @Excel(name = "成衣颜色")
    @ApiModelProperty(value = "成衣颜色")
    private String sku1Name;

    @Excel(name = "物料分类")
    @ApiModelProperty(value = "物料分类名称")
    private String nodeName;

    @Excel(name = "是否主面料")
    @ApiModelProperty(value = "是否主面料")
    private String isMainFabric;


    /** 系统ID-物料BOM档案 */
    @ApiModelProperty(value = "系统ID-物料BOM档案")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long bomSid;

    @ApiModelProperty(value = "物料编码")
    @Excel(name = "物料编码")
    private String bomMaterialCode;

    /** BOM组件物料编码 */
    @ApiModelProperty(value = "物料名称")
    @JsonSerialize(using = ToStringSerializer.class)
    @Excel(name = "物料名称")
    private String bomMaterialName;

    private String bomMaterialSid;

    private String bomItemSid;



    @ApiModelProperty(value = "物料颜色")
    @Excel(name = "物料颜色")
    @JsonSerialize(using = ToStringSerializer.class)
    private String bomMaterialSku1Name;

    @JsonSerialize(using = ToStringSerializer.class)
    private String bomMaterialSku1Sid;

    @JsonSerialize(using = ToStringSerializer.class)
    private String bomMaterialSku1code;

    @Excel(name = "供方编码")
    @ApiModelProperty(value = "供方编码（物料/商品/服务）")
    private String supplierProductCode;

    @Excel(name = "采购类型")
    @ApiModelProperty(value = "采购类型")
    private String purchaseTypeName;

    @Excel(name = "物料类型")
    @ApiModelProperty(value = "物料类型名称")
    private String materialTypeName;

    @Excel(name = "部位")
    @ApiModelProperty(value = "部位名称")
    private String positionName;


    @ApiModelProperty(value = "内部用量(不含损耗)")
    private BigDecimal innerQuantity;


    @ApiModelProperty(value = "内部用量(含损耗)")
    private BigDecimal lossInnerQuantity;


    @Excel(name = "用量")
    @ApiModelProperty(value = "用量/订单数量（报表）")
    private BigDecimal quantity;


    @ApiModelProperty(value = "内部损耗率（%）")
    private BigDecimal innerLossRate;

    @Excel(name = "损耗率(%)")
    @ApiModelProperty(value = "损耗率（存值，不含百分号，如20%，就存0.2）")
    private BigDecimal lossRate;


    @ApiModelProperty(value = "报价用量")
    private BigDecimal quoteQuantity;


    @ApiModelProperty(value = "报价损耗率（%）")
    private BigDecimal quoteLossRate;


    @Excel(name = "损耗取整方式",dictType = "s_rounding_type")
    @ApiModelProperty(value = "取整方式（损耗）")
    private String roundingType;


    @Excel(name = "基本计量单位")
    @ApiModelProperty(value = "基本计量单位")
    private String unitBaseName;

    @Excel(name = "BOM用量单位")
    @ApiModelProperty(value = "bom用量计量单位")
    private String unitQuantityName;

    /** 计价量 */
    @Excel(name = "计价量")
    @ApiModelProperty(value = "计价量")
    @Digits(integer=10,fraction = 3,message = "计价量上限为10位，小数位上限为3位")
    private BigDecimal priceQuantity;

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

    @Excel(name = "幅宽(厘米)")
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

    @Excel(name = "启用/停用(物料)", dictType = "s_valid_flag" )
    @ApiModelProperty(value = "启停用状态")
    private String status;

    @Excel(name = "处理状态(物料)", dictType = "s_handle_status" )
    @ApiModelProperty(value = "处理状态(物料)")
    private String materialHandleStatus;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人")
    private String creatorAccountName;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @Excel(name = "行号")
    @ApiModelProperty(value = "行号")
    private Integer itemNum;

    @Excel(name = "备注")
    @ApiModelProperty(value ="备注")
    private String remark;

    @ApiModelProperty(value = "处理状态(BOM)")
    private String bomHandleStatus;

    @ApiModelProperty(value = "查询：启用/停用(物料)")
    private String materialStatus;
}
