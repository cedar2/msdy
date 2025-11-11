package com.platform.ems.domain.dto.response.external;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * 物料/商品合格证洗唛 BasMaterialCertificateExternal
 *
 * @author chenkaiwen
 * @date 2022-02-23
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasMaterialCertificateExternal {

    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    @ApiModelProperty(value = "系统ID-商品合格证洗唛信息")
    private Long materialCertificateSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "商品档案sid")
    private Long productSid;

    @ApiModelProperty(value = "商品编码")
    private String productCode;

    @ApiModelProperty(value = "商品名称")
    private String productName;

    @ApiModelProperty(value = "计量单位")
    private String unitBase;

    @ApiModelProperty(value = "启用/停用")
    private String status;

    @ApiModelProperty(value = "建议零售价（元）")
    private String suggestedPrice;

    /** 数据字典 **/
    @ApiModelProperty(value = "等级名称")
    private String grade;

    /** 数据字典 **/
    @ApiModelProperty(value = "执行标准名称")
    private String executiveStandard;

    /** 数据字典 **/
    @ApiModelProperty(value = "执行标准（套装下装）名称")
    private String executiveStandardBottoms;

    @ApiModelProperty(value = "检验员")
    private String checker;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd hh:mm:ss")
    @ApiModelProperty(value = "生产日期")
    private Date productDate;

    /** 数据字典 **/
    @ApiModelProperty(value = "安全类别名称")
    private String safeCategory;

    @ApiModelProperty(value = "产地名称")
    private String productPlace;

    @ApiModelProperty(value = "制造商名称")
    private String manufacturer;

    @ApiModelProperty(value = "检测成分")
    private String detectComposition;

    @ApiModelProperty(value = "备注")
    private String comment;

    @ApiModelProperty(value = "操作类型")
    private String operateType;

    @ApiModelProperty(value = "商品SKU实测成分对象")
    List<BasMaterialSkuComponentExternal> skuComponentList;

    @ApiModelProperty(value = "商品SKU羽绒充绒量对象")
    List<BasMaterialSkuDownExternal> skuDownList;

    @ApiModelProperty(value = "商品合格证洗唛自定义字段对象")
    List<BasMaterialCertificateFieldValueExternal> fieldValueList;

    @ApiModelProperty(value = "商品合格证洗唛附件清单")
    List<BasMaterialCertificateAttachExternal> attachmentList;
}
