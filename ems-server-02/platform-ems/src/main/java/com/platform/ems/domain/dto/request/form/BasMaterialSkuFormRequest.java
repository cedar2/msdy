package com.platform.ems.domain.dto.request.form;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 物料/商品SKU明细报表 BasMaterialSkuFormRequest
 *
 * @author chenkaiwen
 * @date 2021-12-16
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasMaterialSkuFormRequest extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @ApiModelProperty(value = "系统ID-物料档案")
    private Long materialSid;

    @ApiModelProperty(value = "系统ID-物料SKU信息,多选")
    private Long[] materialSkuSidList;

    @ApiModelProperty(value = "系统ID-物料SKU信息")
    private Long materialSkuSid;

    @ApiModelProperty(value = "物料（商品/服务）类别")
    private String materialCategory;

    @ApiModelProperty(value = "物料（商品/服务）类别，多选框")
    private String[] materialCategoryList;

    @ApiModelProperty(value = "物料（商品/服务）编码")
    private String materialCode;

    @ApiModelProperty(value = "物料（商品/服务）编码 精确查询")
    private String materialCodes;

    @ApiModelProperty(value = "物料（商品/服务）名称")
    private String materialName;

    @ApiModelProperty(value = "SKU编码")
    private String skuCode;

    @ApiModelProperty(value = "SKU名称")
    private String skuName;

    /** 物料sku明细中的状态 */
    @ApiModelProperty(value = "启用/停用状态")
    private String status;

    /** 物料sku明细中的状态 */
    @ApiModelProperty(value = "启用/停用状态,多选")
    private String[] statusList;

    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @ApiModelProperty(value = "处理状态,多选")
    private String[] handleStatusList;

    @ApiModelProperty(value = "SKU类型编码")
    private String skuType;

    @ApiModelProperty(value = "SKU类型编码")
    private String[] skuTypeList;

    @ApiModelProperty(value = "物料类型-多选")
    private String[] materialTypeList;

    @ApiModelProperty(value = "是否创建BOM")
    private String isCreateBom;

    @ApiModelProperty(value = "bom处理状态")
    private String bomHandleStatus;

    @ApiModelProperty(value = "bom处理状态")
    private String[] bomHandleStatusList;

    @ApiModelProperty(value = "创建人")
    private String creatorAccount;

    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;
}
