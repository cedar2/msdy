package com.platform.ems.domain.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 *
 * 商品成本物料明细报表
 */
@Data
@Accessors( chain = true)
public class CosProductCostMaterialRequest {

    @ApiModelProperty(value = "商品编码")
    private String materialCode;

    @ApiModelProperty(value = "商品名称")
    private String materialName;

    @ApiModelProperty(value = "我司样衣号")
    private String sampleCodeSelf;

    @ApiModelProperty(value = "商品类型")
    private String[] materialTypeList;

    @ApiModelProperty(value = "商品分类")
    private String[] materialClassSidList;

    @ApiModelProperty(value = "上下装")
    private String[] upDownSuitList;

    @ApiModelProperty(value = "产品季")
    private String[] productSeasonSidList;

    @ApiModelProperty(value = "客户")
    private String[] customerSidList;

    @ApiModelProperty(value = "供应商")
    private String[] vendorSidList;

    @ApiModelProperty(value = "处理状态")
    private String[] handleStatusList;

    @ApiModelProperty(value = "创建人")
    private String[] creatorAccountList;

    @ApiModelProperty(value = "业务类型")
    private String[] businessTypeList;

}
