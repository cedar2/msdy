package com.platform.ems.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableId;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

import lombok.experimental.Accessors;

/**
 * 商品条码对象 s_bas_material_barcode
 *
 * @author linhongwei
 * @date 2021-04-23
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_bas_material_barcode")
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasMaterialBarcode extends EmsBaseEntity {

    /**
     * 客户端口号
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /**
     * 系统ID-SKU商品条码
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-SKU商品条码")
    private Long barcodeSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统ID-SKU商品条码")
    private Long[] barcodeSidList;

    /**
     * 系统ID-商品档案
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-商品档案")
    private Long materialSid;

    /**
     * 系统ID-SKU1档案
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-SKU1档案")
    private Long sku1Sid;

    /**
     * 系统ID-SKU2档案
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-SKU2档案")
    private Long sku2Sid;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "SKU1组的Sid")
    private Long sku1GroupSid;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "SKU1组的Sid")
    private Long sku2GroupSid;

    /**
     * 商品条码
     */
    @Excel(name = "商品SKU编码(系统)")
    @ApiModelProperty(value = "商品条码")
    private String barcode;

    /**
     * 商品条形码
     */
    @Excel(name = "商品条形码")
    @ApiModelProperty(value = "商品条形码")
    private String shangpinTiaoxingma;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品条码")
    private String barcodeCode;

    @Excel(name = "物料/商品编码")
    @ApiModelProperty(value = "商品编码")
    @TableField(exist = false)
    private String materialCode;

    @Excel(name = "物料/商品名称")
    @ApiModelProperty(value = "商品名称")
    @TableField(exist = false)
    private String materialName;

    @Excel(name = "基本计量单位")
    @ApiModelProperty(value = "基本计量单位")
    @TableField(exist = false)
    private String unitBaseName;

    @TableField(exist = false)
    @ApiModelProperty(value = "用量计量单位（数据字典的键值），用于保存BOM用量计量量单位")
    private String unitQuantity;

    @TableField(exist = false)
    @ApiModelProperty(value = "用量计量单位（数据字典的键值），用于保存BOM用量计量量单位")
    private String unitQuantityName;

    @NotEmpty(message = "状态不能为空")
    @Excel(name = "启用/停用", dictType = "s_valid_flag")
    @ApiModelProperty(value = "启用/停用状态")
    private String status;

    @TableField(exist = false)
    @ApiModelProperty(value = "启用/停用状态,传”停用“的值过来，用来过滤停用数据")
    private String statusNot;

    @Excel(name = "sku1属性编码")
    @ApiModelProperty(value = "sku1编码")
    private String sku1Code;

    @Excel(name = "sku1属性名称")
    @ApiModelProperty(value = "sku1名称")
    @TableField(exist = false)
    private String sku1Name;

    @Excel(name = "sku2属性编码")
    @ApiModelProperty(value = "sku2编码")
    private String sku2Code;

    @Excel(name = "sku2属性名称")
    @ApiModelProperty(value = "sku2名称")
    @TableField(exist = false)
    private String sku2Name;

    @Excel(name = "商品SKU编码(ERP)")
    @ApiModelProperty(value = "ERP系统SKU条码编码")
    private String erpMaterialSkuBarcode;

    /**
     * 商品条码2
     */
    @Excel(name = "商品SKU编码(极限云)")
    @ApiModelProperty(value = "商品条码2")
    private String barcode2;

    @Excel(name = "我司样衣号")
    @ApiModelProperty(value = "我司样衣号")
    @TableField(exist = false)
    private String sampleCodeSelf;

    @Excel(name = "处理状态", dictType = "s_handle_status")
    @TableField(exist = false)
    private String handleStatus;

    @Excel(name = "产品级别", dictType = "s_product_level")
    @ApiModelProperty(value = "产品级别（数据字典的键值或配置档案的编码）")
    private String productLevel;

    @TableField(exist = false)
    @ApiModelProperty(value = "产品级别（数据字典的键值或配置档案的编码）")
    private String[] productLevelList;

    @Excel(name = "物料类别", dictType = "s_material_category")
    @ApiModelProperty(value = "物料类别")
    @TableField(exist = false)
    private String materialCategory;

    @Excel(name = "物料/商品/服务类型")
    @ApiModelProperty(value = "物料类型名称")
    @TableField(exist = false)
    private String materialTypeName;

    @Excel(name = "物料/商品/服务分类")
    @ApiModelProperty(value = "分类")
    @TableField(exist = false)
    private String materialClassName;

    @TableField(exist = false)
    @ApiModelProperty(value = "大类")
    private String bigClassName;

    @TableField(exist = false)
    @ApiModelProperty(value = "中类")
    private String middleClassName;

    @TableField(exist = false)
    @ApiModelProperty(value = "小类")
    private String smallClassName;

    @TableField(exist = false)
    @ApiModelProperty(value = "组别")
    private String groupType;

    @TableField(exist = false)
    @ApiModelProperty(value = "款式编码")
    private String kuanType;

    @Excel(name = "产品季")
    @ApiModelProperty(value = "产品季名称")
    @TableField(exist = false)
    private String productSeasonName;

    @Excel(name = "公司")
    @ApiModelProperty(value = "公司名称")
    @TableField(exist = false)
    private String companyName;

    @ApiModelProperty(value = "公司品牌名称")
    @TableField(exist = false)
    private String companyBrandName;

    @ApiModelProperty(value = "公司简称")
    @TableField(exist = false)
    private String companyShortName;

    @Excel(name = "品牌")
    @ApiModelProperty(value = "品牌名称")
    @TableField(exist = false)
    private String brandName;

    @Excel(name = "版型")
    @ApiModelProperty(value = "版型名称")
    @TableField(exist = false)
    private String modelName;

    @Excel(name = "生产工艺类型")
    @ApiModelProperty(value = "生产工艺类型")
    @TableField(exist = false)
    private String productTechniqueTypeName;

    @Excel(name = "风格", dictType = "s_style")
    @ApiModelProperty(value = "风格")
    @TableField(exist = false)
    private String style;

    @Excel(name = "系列", dictType = "s_series")
    @ApiModelProperty(value = "系列")
    @TableField(exist = false)
    private String series;

    //@Excel(name = "客户")
    @ApiModelProperty(value = "客户名称")
    @TableField(exist = false)
    private String customerName;

    @Excel(name = "客户")
    @ApiModelProperty(value = "客户简称")
    @TableField(exist = false)
    private String customerShortName;

    @Excel(name = "客方商品编码")
    @ApiModelProperty(value = "客方商品编码")
    @TableField(exist = false)
    private String customerProductCode;

    @Excel(name = "客方样衣号")
    @ApiModelProperty(value = "客方样衣号")
    @TableField(exist = false)
    private String sampleCodeCustomer;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 创建人账号
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    @Excel(name = "创建人")
    @TableField(exist = false)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccountName;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /**
     * 商品条码3
     */
    @ApiModelProperty(value = "商品条码3")
    private String barcode3;

    /**
     * 商品条码4
     */
    @ApiModelProperty(value = "商品条码4")
    private String barcode4;

    @TableField(exist = false)
    @ApiModelProperty(value = "采购类型编码（默认）")
    private String purchaseType;

    @TableField(exist = false)
    @ApiModelProperty(value = "采购类型编码（默认）")
    private String purchaseTypeName;

    @TableField(exist = false)
    @ApiModelProperty(value = "采购类型编码（默认）,多选")
    private String[] purchaseTypeList;

    /**
     * 商品条码5
     *
     */
    @ApiModelProperty(value = "商品条码5")
    private String barcode5;

    /**
     * 商品条码6
     */
    @ApiModelProperty(value = "商品条码6")
    private String barcode6;

    /**
     * 商品条码7
     */
    @ApiModelProperty(value = "商品条码7")
    private String barcode7;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品物料sku1的启用/停用状态")
    private String sku1Status;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品物料sku2的启用/停用状态")
    private String sku2Status;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品物料的启用/停用状态")
    private String materialStatus;

    /**
     * 更新人账号
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccount;

    @Excel(name = "更改人")
    @TableField(exist = false)
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccountName;

    /**
     * 更新时间
     */
    @Excel(name = "更改日期", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更改日期")
    private Date updateDate;

    /**
     * 确认人账号
     */
    @ApiModelProperty(value = "确认人账号")
    private String confirmerAccount;

    @TableField(exist = false)
    @ApiModelProperty(value = "确认人账号")
    private String confirmerAccountName;

    /**
     * 确认时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认日期")
    private Date confirmDate;

    /**
     * 数据源系统
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;

    @ApiModelProperty(value = "创建日期开始时间")
    @TableField(exist = false)
    private String beginTime;

    @ApiModelProperty(value = "创建日期结束时间")
    @TableField(exist = false)
    private String endTime;

    @ApiModelProperty(value = "页数")
    @TableField(exist = false)
    private Integer pageNum;

    @ApiModelProperty(value = "每页个数")
    @TableField(exist = false)
    private Integer pageSize;

    @ApiModelProperty(value = "图片")
    @TableField(exist = false)
    private String picturePath;

    @ApiModelProperty(value = "sku1类型")
    private String sku1Type;

    @ApiModelProperty(value = "sku2类型")
    private String sku2Type;

    @ApiModelProperty(value = "开发员")
    @TableField(exist = false)
    private String developer;

    @ApiModelProperty(value = "幅宽")
    @TableField(exist = false)
    private String width;

    @ApiModelProperty(value = "克重")
    @TableField(exist = false)
    private String gramWeight;

    @ApiModelProperty(value = "成分")
    @TableField(exist = false)
    private String composition;

    @ApiModelProperty(value = "规格尺寸")
    @TableField(exist = false)
    private String specificationSize;

    @ApiModelProperty(value = "型号")
    @TableField(exist = false)
    private String modelSize;

    @ApiModelProperty(value = "材质")
    @TableField(exist = false)
    private String materialComposition;

    @ApiModelProperty(value = "商品类型")
    @TableField(exist = false)
    private String materialType;

    @ApiModelProperty(value = "商品类型多选")
    @TableField(exist = false)
    private String[] materialTypeList;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "商品分类sid")
    @TableField(exist = false)
    private Long materialClassSid;

    @ApiModelProperty(value = "供应商sid")
    @TableField(exist = false)
    private String vendorSid;

    @ApiModelProperty(value = "供应商sid,多选")
    @TableField(exist = false)
    private String[] vendorSidList;

    @ApiModelProperty(value = "供应商编码")
    @TableField(exist = false)
    private String vendorCode;

    @ApiModelProperty(value = "供应商名称")
    @TableField(exist = false)
    private String vendorName;

    @ApiModelProperty(value = "供应商简称")
    @TableField(exist = false)
    private String vendorShortName;

    @ApiModelProperty(value = "供方编码")
    @TableField(exist = false)
    private String supplierProductCode;

    @ApiModelProperty(value = "基本计量单位")
    @TableField(exist = false)
    private String unitBase;

    @ApiModelProperty(value = "价格计量单位")
    @TableField(exist = false)
    private String unitPrice;

    @ApiModelProperty(value = "产品季")
    @TableField(exist = false)
    private String productSeasonSid;

    @ApiModelProperty(value = "产品季编码")
    @TableField(exist = false)
    private String productSeasonCode;

    @ApiModelProperty(value = "客户sid")
    @TableField(exist = false)
    private String customerSid;

    @ApiModelProperty(value = "客户sid")
    @TableField(exist = false)
    private Long[] customerSidList;

    @ApiModelProperty(value = "客户编码")
    @TableField(exist = false)
    private String customerCode;

    @ApiModelProperty(value = "上下装")
    @TableField(exist = false)
    private String upDownSuit;

    @ApiModelProperty(value = "设计师")
    @TableField(exist = false)
    private String designerAccount;

    @ApiModelProperty(value = "设计师昵称")
    @TableField(exist = false)
    private String designerAccountName;

    @ApiModelProperty(value = "版型sid")
    @TableField(exist = false)
    private String modelSid;

    @ApiModelProperty(value = "版型编码")
    @TableField(exist = false)
    private String modelCode;

    @ApiModelProperty(value = "公司sid")
    @TableField(exist = false)
    private String companySid;

    @ApiModelProperty(value = "公司编码")
    @TableField(exist = false)
    private String companyCode;

    @ApiModelProperty(value = "国际码")
    @TableField(exist = false)
    private String nationalStandardCode;

    @ApiModelProperty(value = "生产工艺类型")
    @TableField(exist = false)
    private String productTechniqueType;

    @ApiModelProperty(value = "类别")
    @TableField(exist = false)
    private String[] materialCategoryList;

    @TableField(exist = false)
    @ApiModelProperty(value = "吊牌零售价（元）")
    private BigDecimal retailPrice;

    @TableField(exist = false)
    @ApiModelProperty(value = "季节编码")
    private String season;

    @TableField(exist = false)
    @ApiModelProperty(value = "快速编码")
    private String simpleCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "主面料成分编码")
    private String mainFabricType;

    @TableField(exist = false)
    @ApiModelProperty(value = "主面料成分名称")
    private String mainFabricTypeName;

    @TableField(exist = false)
    private String firstSort;

    @TableField(exist = false)
    private String secondSort;

    @TableField(exist = false)
    private String thirdSort;
}
