package com.platform.ems.domain.dto.request;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * bom 报表
 *
 * @author
 * @date 2021-03-15
 */
@Data
@ApiModel
@Accessors(chain = true)
public class TecBomHeadReportRequest {

    @ApiModelProperty(value = "商品名称")
    private String materialName;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品编码")
    private String materialCode ;

    @ApiModelProperty(value = "查询：客户")
    private String[] customerSidList;

    @ApiModelProperty(value = "查询：产品季")
    private Long[] productSeasonSidList;

    @ApiModelProperty(value = "查询：成衣颜色")
    private Long[] sku1SidList;

    @ApiModelProperty(value = "物料名称")
    @JsonSerialize(using = ToStringSerializer.class)
    private String bomMaterialName;

    @ApiModelProperty(value = "物料编码")
    private String bomMaterialCode;

    @ApiModelProperty(value = "查询：物料颜色")
    private Long[] bomMaterialSku1SidList;

    @ApiModelProperty(value = "供方编码")
    private String supplierProductCode;

    @ApiModelProperty(value = "查询：采购类型")
    private String[] purchaseTypeList;

    @ApiModelProperty(value = "查询：物料类型")
    private String[]  materialTypeList;

    @ApiModelProperty(value = "查询：供应商")
    private Long[] vendorSidList;

    @ApiModelProperty(value = "查询：所属生产环节")
    private String[] touseProduceStageList;

    @ApiModelProperty(value ="每页个数")
    @TableField(exist = false)
    private Integer pageNum;

    @ApiModelProperty(value ="每页个数")
    @TableField(exist = false)
    private Integer pageSize;

    @ApiModelProperty(value = "查询：处理状态(BOM)")
    private String[] bomHandleStatusList;

    @ApiModelProperty(value = "查询：处理状态(物料)")
    private String[] materialHandleStatusList;

    @ApiModelProperty(value = "查询：启用/停用(物料)")
    private String materialStatus;

    @ApiModelProperty(value = "我司样衣号")
    private String sampleCodeSelf;
}
