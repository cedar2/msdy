package com.platform.ems.domain.dto.request;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 特殊库存明细报表请求实体
 *
 * @author yangqz
 * @date 2021-7-13
 */
@Data
@ApiModel
@Accessors(chain = true)
public class InvInventorySpecialRequest extends EmsBaseEntity implements Serializable {

    @ApiModelProperty(value = "商品编码")
    private String materialCode;

    @ApiModelProperty(value = "商品名称")
    private String materialName;

    @ApiModelProperty(value = "查询：特殊库存")
    private List<String> specialStockList;

    @ApiModelProperty(value = "查询：供应商")
    private Long[] vendorSidList;

    @ApiModelProperty(value = "查询：客户")
    private Long[] customerSidList;

    @ApiModelProperty(value = "查询：库位")
    private Long[] storehouseLocationSidList;

    @ApiModelProperty(value = "查询：仓库")
    private Long storehouseSid;

    @ApiModelProperty(value = "查询：物料分类")
    private Long[] materialClassSidList;

    @ApiModelProperty(value = "查询：物料类型")
    private String[] materialTypeList;

    @ApiModelProperty(value = "查询：是否显示0库存")
    private String whether;

    @ApiModelProperty(value = "特殊库存类型")
    private String type;

    @ApiModelProperty(value = "sku1名称")
    private String sku1Name;

    @ApiModelProperty(value = "sku2名称")
    private String sku2Name;

    @ApiModelProperty(value = "查询：仓库(多选)")
    @TableField(exist = false)
    private Long[] storehouseSidList;
}
