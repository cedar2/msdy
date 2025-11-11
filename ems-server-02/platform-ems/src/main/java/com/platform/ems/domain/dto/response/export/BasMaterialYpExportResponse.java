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
 * 样品导出 BasMaterialYpExportResponse
 *
 * @author chenkaiwen
 * @date 2021-12-16
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasMaterialYpExportResponse extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Excel(name = "我司样品号")
    @ApiModelProperty(value = "我司样衣号")
    private String sampleCodeSelf;

    @Excel(name = "商品编码(款号)")
    @ApiModelProperty(value = "外采样编码")
    private String materialCode;

    @Excel(name = "商品名称")
    @ApiModelProperty(value = "外采样名称")
    private String materialName;

    @Excel(name = "产品季")
    @ApiModelProperty(value = "产品季名称")
    private String productSeasonName;

    @Excel(name = "类别", dictType = "s_material_category")
    @ApiModelProperty(value = "类别（物料/商品/服务）")
    private String materialCategory;

    @Excel(name = "商品类型")
    @ApiModelProperty(value = "物料类型名称（物料/商品/服务）")
    private String materialTypeName;

    @Excel(name = "商品分类")
    @ApiModelProperty(value = "物料分类名称")
    private String materialClassName;

    @Excel(name = "设计师")
    @ApiModelProperty(value = "设计师")
    private String designerAccountName;

    @Excel(name = "上下装/套装", dictType = "s_up_down_suit")
    @ApiModelProperty(value = "上下装/套装（数据字典的键值）")
    private String upDownSuit;

    @Excel(name = "版型名称")
    @ApiModelProperty(value = "版型名称")
    private String modelName;

    @Excel(name = "客户")
    @ApiModelProperty(value = "客户名称")
    private String customerName;

    @Excel(name = "客方商品编码")
    @ApiModelProperty(value = "客方编码（物料/商品/服务）")
    private String customerProductCode;

    @Excel(name = "客方样衣号")
    @ApiModelProperty(value = "客方样衣号")
    private String sampleCodeCustomer;

    @Excel(name = "快速编码")
    @ApiModelProperty(value = "快速编码")
    private String simpleCode;

    @Excel(name = "生产工艺类型", dictType = "s_product_technique_typ")
    @ApiModelProperty(value = "生产工艺类型(编织方法)名称")
    private String productTechniqueTypeName;

    @Excel(name = "男女装", dictType = "s_suit_gender")
    @ApiModelProperty(value = "男女装标识")
    private String maleFemaleFlag;

    @Excel(name = "风格", dictType = "s_style")
    @ApiModelProperty(value = "风格编码")
    private String style;

    @Excel(name = "款式", dictType = "s_kuan_type")
    @ApiModelProperty(value = "款式编码")
    private String kuanType;

    @Excel(name = "组别", dictType = "s_product_group")
    @ApiModelProperty(value = "组别")
    private String groupType;

    @Excel(name = "研产销阶段",dictType = "s_cycle_stage")
    @ApiModelProperty(value = "研产销阶段编码")
    private String cycleStage;

    @Excel(name = "公司")
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @Excel(name = "品牌")
    @ApiModelProperty(value = "公司品牌名称")
    private String companyBrandName;

    @Excel(name = "是否创建BOM",dictType = "s_yesno_flag")
    @ApiModelProperty(value = "是否创建BOM（数据字典的键值或配置档案的编码）")
    private String isCreateBom;

    @Excel(name = "是否已创建BOM",dictType = "s_yesno_flag")
    @ApiModelProperty(value = "是否已创建BOM")
    private String isHasCreatedBom;

    @Excel(name = "是否创建产前成本核算",dictType = "s_yesno_flag")
    @ApiModelProperty(value = "是否创建产前成本核算")
    private String isCreateProductcost;

    @Excel(name = "是否已创建产前成本核算",dictType = "s_yesno_flag")
    @ApiModelProperty(value = "是否已创建产前成本核算")
    private String isHasCreatedProductcost;

    @Excel(name = "基本计量单位")
    @ApiModelProperty(value = "基本计量单位")
    private String unitBaseName;

    @Excel(name = "库存价核算方式", dictType = "s_inventory_price_method")
    @ApiModelProperty(value = "库存价核算方式（数据字典的键值）")
    private String inventoryPriceMethod;

    @Excel(name = "固定价（人民币/元）")
    @ApiModelProperty(value = "库存固定价")
    private BigDecimal inventoryStandardPrice;

    @Excel(name = "启用/停用", dictType = "s_valid_flag")
    @ApiModelProperty(value = "启用/停用状态")
    private String status;

    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccountName;

    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @Excel(name = "更新人")
    @ApiModelProperty(value = "更新人")
    private String updaterAccountName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    @Excel(name = "确认人")
    @ApiModelProperty(value = "确认人")
    private String confirmerAccountName;

    @Excel(name = "确认时间", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

}
