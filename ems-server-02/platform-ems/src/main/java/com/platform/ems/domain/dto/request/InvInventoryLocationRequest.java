package com.platform.ems.domain.dto.request;

import com.baomidou.mybatisplus.annotation.TableField;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 库存报表请求实体
 *
 * @author yangqz
 * @date 2021-7-22
 */
@Data
@ApiModel
@Accessors(chain = true)
public class InvInventoryLocationRequest extends EmsBaseEntity implements Serializable {

    @ApiModelProperty(value = "商品编码")
    private String materialCode;

    @ApiModelProperty(value = "商品名称")
    private String materialName;

    @ApiModelProperty(value = "sku1名称")
    private String sku1Name;

    @ApiModelProperty(value = "sku2名称")
    private String sku2Name;

    @ApiModelProperty(value = "查询：库位")
    private Long[] storehouseLocationSidList;

    @ApiModelProperty(value = "查询：仓库(多选)")
    private Long[] storehouseSidList;

    @ApiModelProperty(value = "查询：仓库")
    private Long storehouseSid;

    @ApiModelProperty(value = "查询：物料分类")
    private Long[] materialClassSidList;

    @ApiModelProperty(value = "查询：物料类型")
    private String[] materialTypeList;

    @ApiModelProperty(value = "查询：是否显示0库存")
    private String whether;

    @ApiModelProperty(value = "查询：是否显示0库存")
    private String usableType;

    @ApiModelProperty(value = "使用频率标识（数据字典的键值或配置档案的编码）")
    private String usageFrequencyFlag;

    @ApiModelProperty(value ="每页个数")
    @TableField(exist = false)
    private Integer pageNum;

    @ApiModelProperty(value ="每页个数")
    @TableField(exist = false)
    private Integer pageSize;
}
