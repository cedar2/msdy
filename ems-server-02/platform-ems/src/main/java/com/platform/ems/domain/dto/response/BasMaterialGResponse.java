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
 * 商品导出实体
 *
 * @author yangqz
 * @date 2021-7-14
 */
@Data
@ApiModel
@Accessors(chain = true)
public class BasMaterialGResponse  implements Serializable {

    @Excel(name = "商品编码")
    @ApiModelProperty(value = "物料（商品/服务）编码")
    private String materialCode;

    @Excel(name = "商品名称")
    @ApiModelProperty(value = "物料（商品/服务）名称")
    private String materialName;

    @Excel(name = "商品类型")
    @ApiModelProperty(value = "商品类型名称（物料/商品/服务）")
    private String materialTypeName;

    @Excel(name = "商品分类")
    @ApiModelProperty(value ="商品分类")
    private String materialClassName;

    @Excel(name = "基本计量单位")
    @ApiModelProperty(value = "基本计量单位名称")
    private String unitBaseName;

    @Excel(name = "规格")
    @ApiModelProperty(value = "规格")
    private String specificationSize;

    @Excel(name = "型号")
    @ApiModelProperty(value = "型号")
    private String modelSize;

    @Excel(name = "材质")
    @ApiModelProperty(value = "材质")
    private String materialComposition;

    @Excel(name = "成分")
    @ApiModelProperty(value = "成分")
    private String composition;

    @Excel(name = "供应商")
    @ApiModelProperty(value = "供应商简称")
    private String vendorShortName;

    @Excel(name = "供方编码")
    @ApiModelProperty(value = "供方编码（物料/商品/服务）")
    private String supplierProductCode;

    @Excel(name = "公司")
    @ApiModelProperty(value = "公司名称")
    private String companyShortName;

    @Excel(name = "品牌")
    @ApiModelProperty(value = "公司品牌名称")
    private String companyBrandName;

    @Excel(name = "客户")
    @ApiModelProperty(value = "客户名称")
    private String customerShortName;

    @Excel(name = "客方品牌")
    @ApiModelProperty(value = "客方品牌名称")
    private String customerBrandName;

    @Excel(name = "负责生产工厂")
    @ApiModelProperty(value = "负责生产工厂简称(默认)")
    private String producePlantShortName;

    @Excel(name = "成本价")
    @ApiModelProperty(value = "成本价(含税)")
    private BigDecimal priceCostTax;

    @Excel(name = "工艺单是否要上传", dictType = "s_yesno_flag")
    @ApiModelProperty(value = "是否要上传生产制造单附件（数据字典的键值或配置档案的编码）")
    private String isUploadZhizaodan;

    @Excel(name = "工艺单是否已上传", dictType = "s_yesno_flag")
    @ApiModelProperty(value = "是否已上传生产制造单附件（数据字典的键值或配置档案的编码）")
    private String isHasUploadedZhizaodan;

    @Excel(name = "是否要建BOM",dictType = "s_yesno_flag")
    @ApiModelProperty(value = "是否创建BOM（数据字典的键值或配置档案的编码）")
    private String isCreateBom;

    @Excel(name = "是否已建BOM",dictType = "s_yesno_flag")
    @ApiModelProperty(value = "是否已创建BOM")
    private String isHasCreatedBom;

    @Excel(name = "启用/停用" ,dictType = "s_valid_flag")
    @ApiModelProperty(value = "启用/停用状态")
    private String status;

    @Excel(name = "处理状态" , dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @Excel(name = "最低起订量", scale = 2)
    @ApiModelProperty(value = "最低起订量")
    private BigDecimal minOrderQuantity;

    @Excel(name = "未排产提醒天数")
    @ApiModelProperty(value = "商品未排产提醒天数")
    private Integer wpcRemindDays;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccountName;

    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @Excel(name = "更新人")
    @ApiModelProperty(value = "更新人")
    private String updaterAccountName;

    @Excel(name = "更改日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    @Excel(name = "确认人")
    @ApiModelProperty(value = "确认人")
    private String confirmerAccountName;

    @Excel(name = "确认日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

}
