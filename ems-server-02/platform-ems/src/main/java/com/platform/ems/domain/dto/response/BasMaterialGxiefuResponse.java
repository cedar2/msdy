package com.platform.ems.domain.dto.response;

import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品(鞋服)导出实体
 *
 * @author chenkw
 * @date 2023-4-4
 */
@Data
@ApiModel
@Accessors(chain = true)
public class BasMaterialGxiefuResponse implements Serializable {

    @Excel(name = "商品编码(款号)")
    @ApiModelProperty(value = "物料（商品/服务）编码")
    private String materialCode;

    @Excel(name = "商品名称")
    @ApiModelProperty(value = "物料（商品/服务）名称")
    private String materialName;

    @Excel(name = "商品类型")
    @ApiModelProperty(value = "商品类型名称（物料/商品/服务）")
    private String materialTypeName;

    @Excel(name = "产品季")
    @ApiModelProperty(value = "产品季名称")
    private String productSeasonName;

    @ApiModelProperty(value = "启用/停用状态")
    @Excel(name = "启用/停用" ,dictType = "s_valid_flag")
    private String status;

    @ApiModelProperty(value = "处理状态")
    @Excel(name = "处理状态" , dictType = "s_handle_status")
    private String handleStatus;

    @Excel(name = "我方跟单员")
    @ApiModelProperty(value = "我方跟单员")
    private String buOperatorName;

    @Excel(name = "供方业务员")
    @ApiModelProperty(value = "供方业务员")
    private String buOperatorVendor;

    @Excel(name = "客方业务员")
    @ApiModelProperty(value = "客方业务员")
    private String buOperatorCustomer;

    @ApiModelProperty(value ="商品分类")
    @Excel(name = "商品分类")
    private String materialClassName;

    @Excel(name = "我司样衣号")
    @ApiModelProperty(value = "我司样衣号（初始）")
    private String sampleCodeSelfInitial;

    @Excel(name = "设计师")
    @ApiModelProperty(value = "设计师")
    private String designerAccountName;

    @Excel(name = "工艺单是否要上传", dictType = "s_yesno_flag")
    @ApiModelProperty(value = "是否要上传生产制造单附件（数据字典的键值或配置档案的编码）")
    private String isUploadZhizaodan;

    @Excel(name = "工艺单是否已上传", dictType = "s_yesno_flag")
    @ApiModelProperty(value = "是否已上传生产制造单附件（数据字典的键值或配置档案的编码）")
    private String isHasUploadedZhizaodan;

    @Excel(name = "工艺单更新日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "工艺单更新日期")
    private Date zhizaodanUploadDate;

    @Excel(name = "版型")
    @ApiModelProperty(value ="版型名称")
    private String modelName;

    @Excel(name = "尺码组")
    @ApiModelProperty(value = "尺码组")
    private String sku2GroupName;

    @Excel(name = "上下装/套装", dictType = "s_up_down_suit")
    @ApiModelProperty(value = "上下装/套装（数据字典的键值）")
    private String upDownSuit;

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

    @Excel(name = "生产工艺类型")
    @ApiModelProperty(value = "生产工艺类型(编织方法)名称")
    private String productTechniqueTypeName;

    @Excel(name = "男女装",dictType = "s_suit_gender")
    @ApiModelProperty(value = "男女装标识")
    private String maleFemaleFlag;

    @Excel(name = "风格", dictType = "s_style")
    @ApiModelProperty(value = "风格编码")
    private String style;

    @Excel(name = "系列", dictType = "s_series")
    @ApiModelProperty(value = "系列编码")
    private String series;

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

    @Excel(name = "基本计量单位")
    @ApiModelProperty(value = "基本计量单位名称")
    private String unitBaseName;

    @Excel(name = "库存价核算方式", dictType = "s_inventory_price_method")
    @ApiModelProperty(value = "库存价核算方式（数据字典的键值）")
    private String inventoryPriceMethod;

    @Excel(name = "固定价（人民币/元）")
    @ApiModelProperty(value = "库存固定价")
    private BigDecimal inventoryStandardPrice;

    @Excel(name = "是否存在SKU1", dictType = "s_yesno_flag")
    @ApiModelProperty(value = "是否存在SKU1（数据字典的键值或配置档案的编码）")
    private String isHasCreatedSku1;

    @Excel(name = "是否存在SKU2", dictType = "s_yesno_flag")
    @ApiModelProperty(value = "是否存在SKU2（数据字典的键值或配置档案的编码）")
    private String isHasCreatedSku2;

    @Excel(name = "是否快反款", dictType = "sys_yes_no")
    @ApiModelProperty(value = "是否快反款")
    private String isKuaifankuan;

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

    @Excel(name = "是否创建商品线用量",dictType = "s_yesno_flag")
    @ApiModelProperty(value = "是否创建商品线用量")
    private String isCreateProductLine;

    @Excel(name = "是否已建商品线用量",dictType = "s_yesno_flag")
    @ApiModelProperty(value = "是否已建商品线用量")
    private String isHasCreatedProductLine;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    @Excel(name = "负责生产工厂(默认)")
    @ApiModelProperty(value = "负责生产工厂简称(默认)")
    private String producePlantShortName;

    @Excel(name = "最低起订量", scale = 2)
    @ApiModelProperty(value = "最低起订量")
    private BigDecimal minOrderQuantity;

    @Excel(name = "未排产提醒天数")
    @ApiModelProperty(value = "商品未排产提醒天数")
    private Integer wpcRemindDays;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccountName;

    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @Excel(name = "更新人")
    @ApiModelProperty(value = "更新人")
    private String updaterAccountName;

    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    @Excel(name = "确认人")
    @ApiModelProperty(value = "确认人")
    private String confirmerAccountName;

    @Excel(name = "确认时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

}
