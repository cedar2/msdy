package com.platform.ems.domain.dto.request.material;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author c
 */

@Data
@ApiModel
@Accessors(chain = true)
public class BasMaterialSkuRequest extends EmsBaseEntity implements Serializable {

    @ApiModelProperty(value ="物料sid")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long materialSid;

    @ApiModelProperty(value ="skuCode")
    private String[] skuCodeList;

    @ApiModelProperty(value ="sku类型")
    private String skuType;

    @ApiModelProperty(value ="物料编码")
    private String materialCode;

    @ApiModelProperty(value ="物料名称")
    private String materialName;

    @ApiModelProperty(value = "客方样衣号")
    private String sampleCodeCustomer;

    @ApiModelProperty(value ="物料类别")
    private String materialCategory;

    @ApiModelProperty(value ="物料类别")
    private String[] materialCategoryList;

    @ApiModelProperty(value ="采购类型")
    private String purchaseType;

    @ApiModelProperty(value ="采购类型")
    private String[] purchaseTypeList;

    @ApiModelProperty(value ="客户sid")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long customerSid;

    @ApiModelProperty(value ="客户多选")
    private String[] customerSidList;

    @ApiModelProperty(value ="设计师")
    private String designerAccount;

    @ApiModelProperty(value ="设计师账号")
    private String[] designerAccountList;

    @ApiModelProperty(value ="状态")
    private String status;

    @ApiModelProperty(value ="处理状态")
    private String handleStatus;

    @ApiModelProperty(value = "物料类型")
    private String materialType;

    @ApiModelProperty(value = "是否已创建BOM（数据字典的键值或配置档案的编码）")
    private String isHasCreatedBom;

    @ApiModelProperty(value = "供应商sid")
    private Long vendorSid;

    @ApiModelProperty(value = "供应商sid")
    private Long[] vendorSidList;

    @ApiModelProperty(value = "物料类型")
    private String[] materialTypeList;

    @ApiModelProperty(value = "供方编码")
    private String supplierProductCode;

    @ApiModelProperty(value = "价格维度")
    private String priceDimension;

    @ApiModelProperty(value = "封样结果")
    private String fengyangResult;

    @ApiModelProperty(value = "封样类型")
    private String fengyangType;

    @ApiModelProperty(value = "我司样衣号")
    private String sampleCodeSelf;

}
