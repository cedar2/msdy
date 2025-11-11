package com.platform.ems.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;
import com.platform.ems.domain.dto.response.BasMaterialSkuResponse;
import com.platform.ems.domain.dto.response.ListSeasonResponse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

/**
 * 物料&商品&服务档案对象 s_bas_material
 *
 * @author linhongwei
 * @date 2021-03-23
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_bas_material")
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasMaterial extends EmsBaseEntity {

    /**
     * 客户端口号
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /**
     * 系统ID-物料档案
     */
    @TableId
    @ApiModelProperty(value = "系统ID-物料档案")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long materialSid;

    public void setMaterialCode(String materialCode) {
        if (StrUtil.isNotBlank(materialCode)) {
            materialCode = materialCode.replaceAll("\\s*", "");
        }
        this.materialCode = materialCode;
    }

    public void setMaterialName(String materialName) {
        if (StrUtil.isNotBlank(materialName)) {
            materialName = materialName.trim();
        }
        this.materialName = materialName;
    }

    /**
     * 物料（商品/服务）编码
     */
    @Excel(name = "物料（商品/服务）编码")
    @Length(max = 20, message = "编码最大长度不能超过20位")
    @ApiModelProperty(value = "物料（商品/服务）编码")
    private String materialCode;

    /**
     * 物料（商品/服务）名称
     */
    @Excel(name = "物料（商品/服务）名称")
    @NotNull(message = "名称不能为空")
    @Length(min = 1, max = 300, message = "名称最大长度不能超过300位")
    @ApiModelProperty(value = "物料（商品/服务）名称")
    private String materialName;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料（商品/服务）编码和名称一起模糊查询")
    private String materialCodeName;

    /**
     * 物料类型编码（物料/商品/服务）
     */
    @ApiModelProperty(value = "物料类型编码（物料/商品/服务）")
    private String materialType;

    /**
     * 物料类型名称（物料/商品/服务）
     */
    @Excel(name = "物料类型（物料/商品/服务）")
    @ApiModelProperty(value = "物料类型名称（物料/商品/服务）")
    @TableField(exist = false)
    private String materialTypeName;

    /**
     * 行业领域编码
     */
    @Excel(name = "行业领域", dictType = "s_industry_field")
    @ApiModelProperty(value = "行业领域编码")
    private String industryField;

    /**
     * 物料（商品/服务）分类编码
     */
    @ApiModelProperty(value = "物料（商品/服务）分类编码")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long materialClassSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料分类名称")
    private String materialClassName;

    /**
     * 大类
     */
    @ApiModelProperty(value = "大类")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long bigClassSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "大类")
    private Long[] bigClassSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "中类")
    private Long[] middleClassSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "小类")
    private Long[] smallClassSidList;

    /**
     * 大类
     */
    @ApiModelProperty(value = "大类")
    private String bigClassCode;

    /**
     * 中类
     */
    @ApiModelProperty(value = "中类")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long middleClassSid;

    /**
     * 中类
     */
    @ApiModelProperty(value = "中类")
    private String middleClassCode;

    /**
     * 小类
     */
    @ApiModelProperty(value = "小类")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long smallClassSid;

    /**
     * 小类
     */
    @ApiModelProperty(value = "小类")
    private String smallClassCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "大类")
    private String bigClassName;

    @TableField(exist = false)
    @ApiModelProperty(value = "中类")
    private String middleClassName;

    @TableField(exist = false)
    @ApiModelProperty(value = "小类")
    private String smallClassName;

    /**
     * 物料分类名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "物料分类名称")
    private String nodeName;

    /**
     * 物料分类编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "物料分类编码")
    private String nodeCode;

    /**
     * 季节编码
     */
    @Excel(name = "季节", dictType = "s_season")
    @ApiModelProperty(value = "季节编码")
    private String season;

    /**
     * 供方编码（物料/商品/服务）
     */
    @Length(max = 30, message = "供方编码长度不能超过30个字符")
    @ApiModelProperty(value = "供方编码（物料/商品/服务）")
    private String supplierProductCode;

    public void setSupplierProductCode(String supplierProductCode) {
        if (StrUtil.isNotBlank(supplierProductCode)) {
            supplierProductCode = supplierProductCode.replaceAll("\\s*", "");
        }
        this.supplierProductCode = supplierProductCode;
    }

    /**
     * 价格维度
     */
    @Excel(name = "价格维度", dictType = "s_price_dimension")
    @ApiModelProperty(value = "价格维度")
    private String priceDimension;

    /**
     * 基本计量单位编码
     */
    @ApiModelProperty(value = "基本计量单位编码")
    private String unitBase;

    /**
     * 基本计量单位名称
     */
    @Excel(name = "基本计量单位")
    @ApiModelProperty(value = "基本计量单位名称")
    @TableField(exist = false)
    private String unitBaseName;

    /**
     * 纱支
     */
    @Excel(name = "纱支")
    @ApiModelProperty(value = "纱支")
    @Length(max = 180, message = "纱支不能超过180个字符")
    private String yarnCount;

    /**
     * 密度
     */
    @Excel(name = "密度")
    @ApiModelProperty(value = "密度")
    @Length(max = 180, message = "密度不能超过180个字符")
    private String density;

    /**
     * 成分
     */
    @Excel(name = "成分")
    @ApiModelProperty(value = "成分")
    @Length(max = 180, message = "成分不能超过180个字符")
    private String composition;

    /**
     * 采购类型编码（默认）
     */
    @ApiModelProperty(value = "采购类型编码（默认）")
    private String purchaseType;

    /**
     * 采购类型名称（默认）
     */
    @Excel(name = "采购类型（默认）")
    @ApiModelProperty(value = "采购类型名称（默认）")
    @TableField(exist = false)
    private String purchaseTypeName;

    /**
     * 口型
     */
    @Excel(name = "口型")
    @ApiModelProperty(value = "口型")
    @Length(max = 180, message = "口型不能超过180个字符")
    private String zipperMonth;

    /**
     * 号型
     */
    @Excel(name = "号型")
    @ApiModelProperty(value = "号型")
    @Length(max = 180, message = "号型不能超过180个字符")
    private String zipperSize;

    /**
     * 材质
     */
    @Excel(name = "材质")
    @ApiModelProperty(value = "材质")
    @Length(max = 180, message = "材质不能超过180个字符")
    private String materialComposition;

    /**
     * 规格
     */
    @Excel(name = "规格")
    @ApiModelProperty(value = "规格")
    @Length(max = 180, message = "规格尺寸不能超过180个字符")
    private String specificationSize;

    /**
     *  负责生产工厂sid(默认)
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = " 负责生产工厂sid(默认)")
    private Long producePlantSid;

    @TableField(exist = false)
    @ApiModelProperty(value = " 负责生产工厂sid(默认)")
    private Long[] producePlantSidList;

    @ApiModelProperty(value = "负责生产工厂code(默认)")
    private String producePlantCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "负责生产工厂名称(默认)")
    private String producePlantName;

    @TableField(exist = false)
    @ApiModelProperty(value = "负责生产工厂简称(默认)")
    private String producePlantShortName;

    /**
     * 供应商编码（默认）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "供应商编码（默认）")
    private Long vendorSid;

    /**
     * 供应商名称
     */
    @Excel(name = "供应商")
    @TableField(exist = false)
    @ApiModelProperty(value = "供应商名称")
    private String vendorName;

    /**
     * 供应商编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "供应商编码")
    private String vendorCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "版型类型数组")
    private String[] modelTypeList;

    /**
     * 上游供应商名称
     */
    @Excel(name = "上游供应商")
    @ApiModelProperty(value = "上游供应商名称")
    private String upstreamSupplier;

    /**
     * 开发员
     */
    @ApiModelProperty(value = "开发员")
    private String developer;

    /**
     * 系统自增长ID-工序
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-工序")
    private Long processSid;

    /**
     * 织造工艺编码
     */
    @ApiModelProperty(value = "织造工艺编码")
    private String weaveProcess;

    /**
     * 染色类型编码
     */
    @ApiModelProperty(value = "染色类型编码")
    private String dyeType;

    /**
     * 幅宽（厘米）
     */
    @Excel(name = "幅宽（厘米）")
    @ApiModelProperty(value = "幅宽（厘米）")
    @Length(max = 180, message = "幅宽不能超过180个字符")
    private String width;

    /**
     * 克重
     */
    @Excel(name = "克重")
    @ApiModelProperty(value = "克重")
    @Length(max = 180, message = "克重不能超过180个字符")
    private String gramWeight;

    /**
     * 特殊后整理
     */
    @Excel(name = "特殊后整理")
    @ApiModelProperty(value = "特殊后整理")
    private String specialFinishing;

    /**
     * 型号
     */
    @Excel(name = "型号")
    @ApiModelProperty(value = "型号")
    @Length(max = 180, message = "型号不能超过180个字符")
    private String modelSize;

    /**
     * 吊牌说明
     */
    @Excel(name = "吊牌说明")
    @ApiModelProperty(value = "吊牌说明")
    private String tagDesc;

    /**
     * 工艺说明
     */
    @Excel(name = "工艺说明")
    @ApiModelProperty(value = "工艺说明")
    private String processDesc;

    /**
     * 面辅料功能性说明
     */
    @Excel(name = "面辅料功能性说明")
    @ApiModelProperty(value = "面辅料功能性说明")
    private String functionRemark;

    /**
     * 商品款号（用于服务类档案）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @Excel(name = "商品款号（用于服务类档案）")
    @ApiModelProperty(value = "商品款号（用于服务类档案）")
    private String productSid;

    /**
     * 产品类别编码
     */
    @ApiModelProperty(value = "产品类别编码")
    private String productCategory;

    /**
     * 风险说明
     */
    @Excel(name = "风险说明")
    @ApiModelProperty(value = "风险说明")
    private String riskRemark;

    /**
     * 是否快反款
     */
    @ApiModelProperty(value = "是否快反款")
    private String isKuaifankuan;

    /**
     * 是否复核面料
     */
    @Excel(name = "是否复核面料", dictType = "sys_yes_no")
    @ApiModelProperty(value = "是否复核面料")
    private String isCompositeMaterial;

    /**
     * 胚布/纱线说明
     */
    @Excel(name = "胚布/纱线说明")
    @ApiModelProperty(value = "胚布/纱线说明")
    @Length(max = 600, message = "胚布/纱线说明不能超过600个字符")
    private String calicoYarnDescription;

    /**
     * 特殊工艺说明
     */
    @Excel(name = "特殊工艺说明")
    @ApiModelProperty(value = "特殊工艺说明")
    private String specialCraft;

    /**
     * 自测报告备注
     */
    @Excel(name = "自测报告备注")
    @ApiModelProperty(value = "自测报告备注")
    private String selfDetectRemark;

    /**
     * 拉链标识
     */
    @Excel(name = "拉链标识", dictType = "s_zipper_flag")
    @ApiModelProperty(value = "拉链标识")
    private String zipperFlag;

    /**
     * 物料类别编码
     */
    @Excel(name = "物料类别编码", dictType = "s_material_category")
    @ApiModelProperty(value = "物料类别编码")
    @NotNull
    private String materialCategory;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料类别编码")
    private String materialCategoryName;

    @TableField(exist = false)
    private String[] materialCategoryList;

    @ApiModelProperty(value = "是否创建于样品档案（数据字典的键值或配置档案的编码）")
    private String isCreateFromSample;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询 按款 K / 按款色 K1 / 按款色码 K12")
    private String dataDimension;

    /**
     * 是否SKU物料
     */
    @Excel(name = "是否SKU物料", dictType = "sys_yes_no")
    @ApiModelProperty(value = "是否SKU物料")
    private String isSkuMaterial;

    /**
     * SKU维度数
     */
    @Excel(name = "SKU维度数", dictType = "s_sku_dimension")
    @ApiModelProperty(value = "SKU维度数")
    private Integer skuDimension;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "SKU1的Sid")
    private Long sku1Sid;

    @TableField(exist = false)
    @ApiModelProperty(value = "SKU1的code")
    private String sku1Code;

    @Excel(name = "SKU1名称")
    @TableField(exist = false)
    @ApiModelProperty(value = "SKU1的name")
    private String sku1Name;

    /**
     * SKU1类型编码
     */
    @Excel(name = "SKU1类型", dictType = "s_sku_type")
    @ApiModelProperty(value = "SKU1类型编码")
    private String sku1Type;

    /**
     * SKU2类型编码
     */
    @Excel(name = "SKU2类型", dictType = "s_sku_type")
    @ApiModelProperty(value = "SKU2类型编码")
    private String sku2Type;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "SKU2的Sid")
    private Long sku2Sid;

    @TableField(exist = false)
    @ApiModelProperty(value = "SKU2的code")
    private String sku2Code;

    @Excel(name = "SKU2名称")
    @TableField(exist = false)
    @ApiModelProperty(value = "SKU2的name")
    private String sku2Name;

    @TableField(exist = false)
    @ApiModelProperty(value = "尺码名称2")
    private String skuName2;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "SKU1组的Sid")
    private Long sku1GroupSid;

    @ApiModelProperty(value = "SKU1组的Code")
    private String sku1GroupCode;

    @ApiModelProperty(value = "SKU2组的Code")
    private String sku2GroupCode;

    @Excel(name = "库存价核算方式", dictType = "s_inventory_price_method")
    @ApiModelProperty(value = "库存价核算方式（数据字典的键值）")
    private String inventoryPriceMethod;

    @TableField(exist = false)
    @ApiModelProperty(value = "库存价核算方式（数据字典的键值）（多选）")
    private String[] inventoryPriceMethodList;

    @Excel(name = "库存固定价")
    @Digits(integer = 7, fraction = 6, message = "库存固定价整数位上限为7位，小数位上限为6位")
    @ApiModelProperty(value = "库存固定价")
    private BigDecimal inventoryStandardPrice;

    /**
     * 价格类型
     */
    @Excel(name = "价格类型")
    @ApiModelProperty(value = "价格类型")
    private String priceType;

    /**
     * 成本类型
     */
    @Excel(name = "成本类型")
    @ApiModelProperty(value = "成本类型")
    private String costType;

    /**
     * 20211012
     */
    @ApiModelProperty(value = "是否已上传生产制造单附件（数据字典的键值或配置档案的编码）")
    private String isHasUploadedZhizaodan;

    /**
     * 20220121
     */
    @ApiModelProperty(value = "是否需要上传生产制造单附件（数据字典的键值或配置档案的编码）")
    private String isUploadZhizaodan;

    /**
     * 20211012
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "生产制造单附件最新上传时间(保存时分秒)")
    private Date zhizaodanUploadDate;

    /**
     * 20211013
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "附件类型")
    private String fileType;

    @TableField(exist = false)
    @ApiModelProperty(value = "工艺单附件路径")
    private String filePath;

    /**
     * 20211013
     */
    @ApiModelProperty(value = "所属生产环节（数据字典的键值或配置档案的编码），如：车间、后道、包装")
    private String touseProduceStage;

    /**
     * 20211013
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "所属生产环节（数据字典的键值或配置档案的编码），如：车间、后道、包装")
    private String[] touseProduceStageList;

    /**
     * 20211013
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "所属生产环节（数据字典的键值或配置档案的编码），如：车间、后道、包装")
    private String touseProduceStageName;

    /**
     * 20211013
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "所属品类规划sid")
    private Long productPlanSid;

    /**
     * 20211013
     */
    @ApiModelProperty(value = "购买人(外采样)")
    private String osbSampleBuyer;

    @TableField(exist = false)
    @ApiModelProperty(value = "购买人(外采样)")
    private String[] osbSampleBuyerList;

    @TableField(exist = false)
    @ApiModelProperty(value = "购买人(外采样)")
    private String osbSampleBuyerName;

    /**
     * 20211013
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "购买日期(外采样)")
    private Date purchaseDate;

    /**
     * 20211013
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "购买公司(外采样)")
    private Long purchaseCompany;

    @TableField(exist = false)
    @ApiModelProperty(value = "购买公司(外采样)")
    private Long[] purchaseCompanyList;

    @TableField(exist = false)
    @ApiModelProperty(value = "购买公司(外采样)")
    private String purchaseCompanyName;

    /**
     * 20211013
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "购买部门(外采样)")
    private Long purchaseOrg;

    @TableField(exist = false)
    @ApiModelProperty(value = "购买部门(外采样)")
    private Long[] purchaseOrgList;

    @TableField(exist = false)
    @ApiModelProperty(value = "购买部门(外采样)")
    private String purchaseOrgName;

    /**
     * 20211013
     */
    @Length(max = 30, message = "购买地不能超过30个字符")
    @ApiModelProperty(value = "购买地(外采样)")
    private String purchaseFrom;

    /**
     * 20211013
     */
    @Length(max = 30, message = "外采样品牌不能超过30个字符")
    @ApiModelProperty(value = "外采样品牌(外采样)")
    private String osbSampleBrand;

    /**
     * 20211013
     */
    @Length(max = 30, message = "外采样品牌货号不能超过30个字符")
    @ApiModelProperty(value = "外采样品牌货号(外采样)")
    private String osbSampleCode;

    @Length(max = 30, message = "外采样颜色不能超过30个字符")
    @ApiModelProperty(value = "外采样颜色(外采样)")
    private String osbSampleColor;

    @Length(max = 30, message = "外采样尺码不能超过30个字符")
    @ApiModelProperty(value = "外采样尺码(外采样)")
    private String osbSampleSize;

    /**
     * 20211013
     */
    @Digits(integer = 5, fraction = 5, message = "采购价(外采样)整数位上限为5位，小数位上限为5位")
    @ApiModelProperty(value = "采购价(外采样)")
    private BigDecimal purchasePrice;

    /**
     * 20211013
     */
    @ApiModelProperty(value = "货币(外采样)")
    private String currency;

    /**
     * 20211013
     */
    @ApiModelProperty(value = "货币单位(外采样)")
    private String currencyUnit;

    /**
     * 20211013
     */
    @ApiModelProperty(value = "购买量(外采样)")
    private Long purchaseQuantity;


    /**
     * 20211013
     */
    @Excel(name = "报销状态", dictType = "s_reimburse_status")
    @ApiModelProperty(value = "报销状态(外采样)")
    private String reimburseStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "报销状态(外采样)")
    private String[] reimburseStatusList;

    /**
     * 系统ID-产品季档案
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-产品季档案")
    private Long productSeasonSid;

    /**
     * 产品季名称
     */
    @TableField(exist = false)
    @Excel(name = "产品季")
    @ApiModelProperty(value = "产品季名称")
    private String productSeasonName;

    /**
     * 产品季编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "产品季编码")
    private String productSeasonCode;

    /**
     * 我司样衣号
     */
    @Excel(name = "我司样衣号")
    @ApiModelProperty(value = "我司样衣号")
    @Length(max = 30, message = "我司样衣号不能超过30个字符")
    private String sampleCodeSelf;

    /**
     * 供方样衣号
     */
    @Excel(name = "供方样衣号")
    @ApiModelProperty(value = "供方样衣号")
    @Length(max = 30, message = "供方样衣号不能超过30个字符")
    private String sampleCodeVendor;

    /**
     * 客方样衣号
     */
    @Excel(name = "客方样衣号")
    @ApiModelProperty(value = "客方样衣号")
    @Length(max = 30, message = "客方样衣号不能超过30个字符")
    private String sampleCodeCustomer;

    /**
     * 吊牌零售价（元）
     */
    @Excel(name = "吊牌零售价（元）")
    @ApiModelProperty(value = "吊牌零售价（元）")
    @Digits(integer = 9, fraction = 4, message = "吊牌零售价整数位上限为9位，小数位上限为4位")
    private BigDecimal retailPrice;

    /**
     * 客方编码（物料/商品/服务）
     */
    @Excel(name = "客方编码（物料/商品/服务）")
    @ApiModelProperty(value = "客方编码（物料/商品/服务）")
    @Length(max = 15, message = "客方编码不能超过15个字符")
    private String customerProductCode;

    /**
     * 快速编码
     */
    @Excel(name = "快速编码")
    @ApiModelProperty(value = "快速编码")
    @Length(max = 20, message = "快速编码不能超过20个字符")
    private String simpleCode;

    /**
     * 研产销阶段编码
     */
    @Excel(name = "研产销阶段编码")
    @ApiModelProperty(value = "研产销阶段编码")
    private String cycleStage;

    /**
     * 公司编码（公司档案的sid）
     */
    @ApiModelProperty(value = "公司编码（公司档案的sid）")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long companySid;

    /**
     * 公司名称
     */
    @TableField(exist = false)
    @Excel(name = "公司")
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司名称")
    private String companyShortName;

    /**
     * 公司代码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "公司代码")
    private String companyCode;

    /**
     * 组别编码
     */
    @ApiModelProperty(value = "组别编码")
    private String groupType;

    @TableField(exist = false)
    @ApiModelProperty(value = "组别编码")
    private String[] groupTypeList;

    /**
     * 设计师账号
     */
    @ApiModelProperty(value = "设计师账号")
    private String designerAccount;

    @TableField(exist = false)
    @ApiModelProperty(value = "设计师账号List")
    private List<String> designerAccountList;

    @TableField(exist = false)
    @ApiModelProperty(value = "设计师")
    private String designerAccountName;


    /**
     * 风格编码
     */
    @Excel(name = "风格", dictType = "s_style")
    @ApiModelProperty(value = "风格编码")
    private String style;

    /**
     * 系列编码
     */
    @Excel(name = "系列", dictType = "s_series")
    @ApiModelProperty(value = "系列编码")
    private String series;

    /**
     * 款式编码
     */
    @Excel(name = "款式", dictType = "s_kuan_type")
    @ApiModelProperty(value = "款式编码")
    private String kuanType;

    /**
     * 风格细分编码
     */
    @Excel(name = "风格细分", dictType = "s_style_d")
    @ApiModelProperty(value = "风格细分编码")
    private String styleDetail;

    /**
     * 系列细分编码
     */
    @Excel(name = "系列细分", dictType = "s_series_d")
    @ApiModelProperty(value = "系列细分编码")
    private String seriesDetail;

    /**
     * 款式细分编码
     */
    @Excel(name = "款式细分", dictType = "s_kuan_type_d")
    @ApiModelProperty(value = "款式细分编码")
    private String kuanTypeDetail;

    /**
     * 波段编码
     */
    @Excel(name = "波段", dictType = "s_wave_band")
    @ApiModelProperty(value = "波段编码")
    private String waveBand;

    /**
     * 项目编码
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "项目编码")
    private String projectSid;

    /**
     * 客户（定制）sid）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "客户（定制）sid）")
    private Long customerSid;

    /**
     * 客户编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "客户编码")
    private String customerCode;

    /**
     * 客户名称
     */
    @TableField(exist = false)
    @Excel(name = "客户")
    @ApiModelProperty(value = "客户名称")
    private String customerName;

    /**
     * 客户简称
     */
    @TableField(exist = false)
    @Excel(name = "客户简称")
    @ApiModelProperty(value = "客户简称")
    private String customerShortName;

    @TableField(exist = false)
    @Excel(name = "供应商简称")
    @ApiModelProperty(value = "供应商简称")
    private String vendorShortName;

    /**
     * 客方品牌sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "客方品牌sid")
    private Long customerBrandSid;

    /**
     * 客方品牌名称
     */
    @Excel(name = "客方品牌")
    @TableField(exist = false)
    @ApiModelProperty(value = "客方品牌名称")
    private String customerBrandName;

    /**
     * 客方品牌编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "客方品牌编码")
    private String customerBrandCode;

    /**
     * 公司品牌sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司品牌sid")
    private Long companyBrandSid;

    /**
     * 公司品牌名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "公司品牌名称")
    private String companyBrandName;

    /**
     * 公司品牌编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "公司品牌编码")
    private String companyBrandCode;

    /**
     * 版型编码
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "版型编码")
    private Long modelSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "版型类型")
    private String modelType;

    /**
     * 版型名称
     */
    @TableField(exist = false)
    @Excel(name = "版型名称")
    @ApiModelProperty(value = "版型名称")
    private String modelName;

    @TableField(exist = false)
    @ApiModelProperty(value = "版型编码")
    private String modelCode;

    /**
     * 领型编码
     */
    @ApiModelProperty(value = "领型编码")
    private String collarType;

    /**
     * 袖型编码
     */
    @ApiModelProperty(value = "袖型编码")
    private String sleeveType;

    /**
     * 年龄段编码
     */
    @Excel(name = "年龄段", dictType = "s_age_group")
    @ApiModelProperty(value = "年龄段编码")
    private String ageGroup;

    /**
     * 内外下编码
     */
    @Excel(name = "内外下编码")
    @ApiModelProperty(value = "内外下编码")
    private String inOutUnder;

    /**
     * 主题编码
     */
    @Excel(name = "主题编码")
    @ApiModelProperty(value = "主题编码")
    private String topic;

    /**
     * 主面料成分编码
     */
    @Excel(name = "主面料成分编码")
    @ApiModelProperty(value = "主面料成分编码")
    private String mainFabricType;

    @TableField(exist = false)
    @ApiModelProperty(value = "主面料成分名称")
    private String mainFabricTypeName;

    /**
     * 客供合作模式编码（默认）
     */
    @Excel(name = "客供合作模式编码（默认）")
    @ApiModelProperty(value = "客供合作模式编码（默认）")
    private String customerVendorCoopModel;

    /**
     * 价格段编码
     */
    @Excel(name = "价格段编码")
    @ApiModelProperty(value = "价格段编码")
    private String priceGroup;

    /**
     * 折扣类型编码
     */
    @Excel(name = "折扣类型编码")
    @ApiModelProperty(value = "折扣类型编码")
    private String discountType;

    /**
     * 折扣类型名称
     */
    @Excel(name = "折扣类型名称")
    @ApiModelProperty(value = "折扣类型名称")
    @TableField(exist = false)
    private String discountTypeName;

    /**
     * 折扣率
     */
    @Excel(name = "折扣率")
    @Digits(integer = 2, fraction = 3, message = "折扣率整数位上限为2位，小数位上限为3位")
    @ApiModelProperty(value = "折扣率")
    private BigDecimal discountRate;

    /**
     * 工艺路线编码（默认）
     */
    @Excel(name = "工艺路线编码（默认）")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "工艺路线编码（默认）")
    private Long productRouting;

    /**
     * 生产类型编码（默认）
     */
    @Excel(name = "生产类型编码（默认）")
    @ApiModelProperty(value = "生产类型编码（默认）")
    private String productType;

    /**
     * 成本价(含税)
     */
    @Excel(name = "成本价(含税)")
    @ApiModelProperty(value = "成本价(含税)")
    @Digits(integer = 11, fraction = 4, message = "成本价整数位上限为11位，小数位上限为4位")
    private BigDecimal priceCostTax;

    /**
     * 目标成本(含税)
     */
    @Excel(name = "目标成本(含税)")
    @ApiModelProperty(value = "目标成本(含税)")
    @Digits(integer = 11, fraction = 4, message = "成本价整数位上限为11位，小数位上限为4位")
    private BigDecimal costTargetTax;

    /**
     * 国标码
     */
    @Excel(name = "国标码")
    @ApiModelProperty(value = "国标码")
    private String nationalStandardCode;

    /**
     * 吊牌功能性说明
     */
    @Excel(name = "吊牌功能性说明")
    @ApiModelProperty(value = "吊牌功能性说明")
    private String featureTag;

    /**
     * DNA编码
     */
    @Excel(name = "DNA编码")
    @ApiModelProperty(value = "DNA编码")
    private String dna;

    /**
     * 工艺编码
     */
    @Excel(name = "工艺编码")
    @ApiModelProperty(value = "工艺编码")
    private String craft;

    /**
     * 图案花型编码
     */
    @Excel(name = "图案花型编码")
    @ApiModelProperty(value = "图案花型编码")
    private String patternFlower;

    /**
     * 特殊类型编码
     */
    @Excel(name = "特殊类型编码")
    @ApiModelProperty(value = "特殊类型编码")
    private String specialType;

    /**
     * 脚口编码
     */
    @Excel(name = "脚口编码")
    @ApiModelProperty(value = "脚口编码")
    private String footMouth;

    /**
     * 卖点说明
     */
    @Excel(name = "卖点说明")
    @ApiModelProperty(value = "卖点说明")
    private String sellPointDesc;

    /**
     * 推广类型编码
     */
    @Excel(name = "推广类型编码")
    @ApiModelProperty(value = "推广类型编码")
    private String spreadType;

    /**
     * 推荐业态编码
     */
    @Excel(name = "推荐业态编码")
    @ApiModelProperty(value = "推荐业态编码")
    private String spreadField;

    /**
     * 客群编码
     */
    @Excel(name = "客群编码")
    @ApiModelProperty(value = "客群编码")
    private String customerGroup;

    /**
     * 商品编码/物料编码（初始）
     */
    @Excel(name = "商品编码/物料编码（初始）")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "商品编码/物料编码（初始）")
    private Long materialSidInitial;

    /**
     * 我司样衣号（初始）
     */
    @Excel(name = "我司样衣号（初始）")
    @ApiModelProperty(value = "我司样衣号（初始）")
    private String sampleCodeSelfInitial;

    /**
     * 腰高编码
     */
    @Excel(name = "腰高编码")
    @ApiModelProperty(value = "腰高编码")
    private String waistHeight;

    /**
     * 弹力指数编码
     */
    @Excel(name = "弹力指数编码")
    @ApiModelProperty(value = "弹力指数编码")
    private String elasticityIndex;

    /**
     * 厚薄指数编码
     */
    @Excel(name = "厚薄指数编码")
    @ApiModelProperty(value = "厚薄指数编码")
    private String thickIndex;

    /**
     * ODM/OEM编码
     */
    @Excel(name = "ODM/OEM编码")
    @ApiModelProperty(value = "ODM/OEM编码")
    private String odmOem;

    /**
     * 销售渠道编码
     */
    @Excel(name = " 业务渠道/销售渠道")
    @ApiModelProperty(value = " 业务渠道/销售渠道")
    private String businessChannel;

    /**
     * 男女装标识
     */
    @Excel(name = "男女装标识")
    @ApiModelProperty(value = "男女装标识")
    private String maleFemaleFlag;

    /**
     * 等级编码
     */
    @Excel(name = "等级编码")
    @ApiModelProperty(value = "等级编码")
    private String grade;

    /**
     * 执行标准编码
     */
    @Excel(name = "执行标准编码")
    @ApiModelProperty(value = "执行标准编码")
    private String executiveStandard;

    /**
     * 执行标准编码（套装下装）
     */
    @Excel(name = "执行标准编码（套装下装）")
    @ApiModelProperty(value = "执行标准编码（套装下装）")
    private String executiveStandardBottoms;

    /**
     * 图片路径
     */
    @ApiModelProperty(value = "主图片路径")
    private String picturePath;

    @ApiModelProperty(value = "副图片路径（多图）")
    private String picturePathSecond;

    @TableField(exist = false)
    @ApiModelProperty(value = "图片路径（副图）")
    private String[] picturePathList;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询无主图：WZT；查询无副图：WFT；查询无色图：WST")
    private String isPicture;
/*
    @TableField(exist = false)
    @ApiModelProperty(value = "查询无副图：N")
    private String isPictureSecond;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询无色图：N")
    private String isPictureSku;*/

    /**
     * 备注
     */
    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "启用/停用状态")
    @Excel(name = "启用/停用状态", dictType = "s_valid_flag")
    private String status;

    @ApiModelProperty(value = "停用说明")
    private String disableRemark;

    /**
     * 物料sku启用/停用状态
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "sku启用/停用状态")
    private String sku1Status;

    /**
     * 物料sku启用/停用状态
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "sku启用/停用状态")
    private String sku2Status;

    @ApiModelProperty(value = "处理状态")
    @Excel(name = "处理状态", dictType = "s_handle_status")
    private String handleStatus;

    /**
     * 商品未排产提醒天数
     */
    @Excel(name = "未排产提醒天数")
    @ApiModelProperty(value = "商品未排产提醒天数")
    private Integer wpcRemindDays;

    /**
     * 创建人账号
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    /**
     * 因为 creatorAccount 这个字段被拿去查询 nickName 了，特此再加这个字段查询创建人账号
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "创建人账号")
    private String creatorUserName;

    @Excel(name = "创建人")
    @TableField(exist = false)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccountName;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /**
     * 更新人账号
     */
    @Excel(name = "更新人账号")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccount;

    @Excel(name = "更新人")
    @TableField(exist = false)
    @ApiModelProperty(value = "更新人")
    private String updaterAccountName;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 确认人账号
     */
    @Excel(name = "确认人账号")
    @ApiModelProperty(value = "确认人账号")
    private String confirmerAccount;

    @Excel(name = "确认人")
    @TableField(exist = false)
    @ApiModelProperty(value = "确认人")
    private String confirmerAccountName;

    /**
     * 确认时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "确认时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 数据源系统
     */
    @Excel(name = "数据源系统")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;

    @TableField(exist = false)
    @ApiModelProperty(value = "是否有sku1")
    private String isHasCreatedSku1;

    @TableField(exist = false)
    @ApiModelProperty(value = "是否有sku2")
    private String isHasCreatedSku2;

    /**
     * 是否创建BOM
     */
    @ApiModelProperty(value = "是否创建BOM")
    private String isCreateBom;

    /**
     * 是否创建产前成本核算
     */
    @ApiModelProperty(value = "是否创建产前成本核算")
    private String isCreateProductcost;

    /**
     * 是否创建商品线用量
     */
    @ApiModelProperty(value = "是否创建商品线用量")
    private String isCreateProductLine;

    @ApiModelProperty(value = "是否已创建BOM（数据字典的键值或配置档案的编码）")
    private String isHasCreatedBom;

    @ApiModelProperty(value = "是否已创建产前成本核算（数据字典的键值或配置档案的编码）")
    private String isHasCreatedProductcost;

    @ApiModelProperty(value = "是否已建商品线用量（数据字典的键值或配置档案的编码）")
    private String isHasCreatedProductLine;

    @ApiModelProperty(value = "我方跟单员")
    private String buOperator;

    @TableField(exist = false)
    @ApiModelProperty(value = "我方跟单员")
    private String buOperatorList;

    @TableField(exist = false)
    @ApiModelProperty(value = "我方跟单员")
    private String buOperatorName;

    @Length(max = 30, message = "供方业务员不能超过30个字符")
    @ApiModelProperty(value = "供方业务员")
    private String buOperatorVendor;

    @Length(max = 30, message = "客方业务员不能超过30个字符")
    @ApiModelProperty(value = "客方业务员")
    private String buOperatorCustomer;


    /**
     * bom状态
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "bom处理状态")
    private String bomHandleStatus;

    /**
     * 产前成本核算状态
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "产前成本核算处理状态")
    private String costHandleStatus;

    /**
     *
     */
    @Excel(name = "")
    @ApiModelProperty(value = "")
    private String safeCategory;

    /**
     * 生产工艺类型(编织方法)编码
     */
    @Excel(name = "生产工艺类型(编织方法)编码", dictType = "s_product_technique_typ")
    @ApiModelProperty(value = "生产工艺类型(编织方法)编码")
    private String productTechniqueType;

    /**
     * 生产工艺类型(编织方法)名称
     */
    @Excel(name = "生产工艺类型(编织方法)名称", dictType = "s_product_technique_typ")
    @ApiModelProperty(value = "生产工艺类型(编织方法)名称")
    @TableField(exist = false)
    private String productTechniqueTypeName;

    /**
     * 报价单位（数据字典的键值）
     */
    @Excel(name = "报价单位（数据字典的键值）")
    @ApiModelProperty(value = "报价单位（数据字典的键值）")
    private String unitPrice;

    /**
     * 报价单位名称（配置档案的键值）
     */
    @Excel(name = "报价单位名称（配置档案的键值）")
    @ApiModelProperty(value = "报价单位名称（配置档案的键值）")
    @TableField(exist = false)
    private String unitPriceName;

    /**
     * 用量计量单位（数据字典的键值），用于保存BOM用量计量量单位
     */
    @Excel(name = "用量计量单位（数据字典的键值），用于保存BOM用量计量量单位")
    @ApiModelProperty(value = "用量计量单位（数据字典的键值），用于保存BOM用量计量量单位")
    private String unitQuantity;

    /**
     * 用量计量单位名称（数据字典的键值），用于保存BOM用量计量量单位
     */
    @Excel(name = "用量计量单位名称（数据字典的键值），用于保存BOM用量计量量单位")
    @ApiModelProperty(value = "用量计量单位名称（数据字典的键值），用于保存BOM用量计量量单位")
    @TableField(exist = false)
    private String unitQuantityName;

    /**
     * 上下装/套装（数据字典的键值）
     */
    @Excel(name = "上下装/套装（数据字典的键值）")
    @ApiModelProperty(value = "上下装/套装（数据字典的键值）")
    private String upDownSuit;

    @ApiModelProperty(value = "研发状态（数据字典的键值或配置档案的编码）：规划、图稿、首样、齐色样、订货样、下单款")
    private String radStatus;

    @ApiModelProperty(value = "是否主推款（数据字典的键值或配置档案的编码）")
    private String isPopularizeProduct;

    /**
     * 甲供料方式（数据字典的键值）
     */
    @Excel(name = "甲供料方式（数据字典的键值）")
    @ApiModelProperty(value = "甲供料方式（数据字典的键值）")
    private String rawMaterialMode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司品标sid")
    private Long companyBrandMarkSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司品标名称")
    private String companyBrandMarkName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "客方品标sid")
    private Long customerBrandMarkSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "开始时间")
    private String beginTime;

    @TableField(exist = false)
    @ApiModelProperty(value = "结束时间")
    private String endTime;

    @TableField(exist = false)
    @ApiModelProperty(value = "页码")
    private Integer pageNum;

    @TableField(exist = false)
    @ApiModelProperty(value = "页容量")
    private Integer pageSize;

    /**
     * 系统ID-物料档案
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "系统ID-物料档案")
    private Long[] materialSidList;

    /**
     * 业务类型
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "业务类型")
    private String businessType;

    /**
     * 物料类型数组（物料/商品/服务）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "物料类型数组（物料/商品/服务）")
    private String[] materialTypeList;

    /**
     * 处理状态数组
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态数组")
    private String[] handleStatusList;

    @TableField(exist = false)
    @ApiModelProperty(value = "用来判断是否是导入操作")
    private String importType;

    /**
     * 产品季sid数组
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "产品季sid数组")
    private Long[] productSeasonSidList;

    /**
     * 公司sid数组
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "公司sid数组")
    private Long[] companySidList;

    /**
     * 供应商sid数组（默认）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "供应商sid数组（默认）")
    private Long[] vendorSidList;

    /**
     * 客户sid）数组
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "客户sid）数组")
    private Long[] customerSidList;

    /**
     * 版型编码数组
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "版型编码数组")
    private String[] modelCodeList;

    /**
     * 生产工艺类型编码数组
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "生产工艺类型编码数组")
    private String[] productTechniqueTypeList;

    /**
     * 主面料成分编码数组
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "主面料成分编码数组")
    private String[] mainFabricTypeList;

    /**
     * 风格编码数组
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "风格编码数组")
    private String[] styleList;

    /**
     * 系列编码数组
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "系列编码数组")
    private String[] seriesList;

    /**
     * 款式编码数组
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "款式编码数组")
    private String[] kuanTypeList;

    /**
     * 是否套装
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "是否套装")
    private String[] isSuitList;

    /**
     * 研产销阶段编码数组
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "研产销阶段编码数组")
    private String[] cycleStageList;

    /**
     * 采购类型编码数组
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "采购类型编码数组")
    private String[] purchaseTypeList;

    /**
     * 季节编码数组
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "季节编码数组")
    private String[] seasonList;

    /**
     * 上下装/套装数组
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "上下装/套装数组")
    private String[] upDownSuitList;

    /**
     * 版型sid数组
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "版型sid数组")
    private Long[] modelSidList;

    /**
     * 商品销售站点对象
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "商品销售站点对象")
    private List<BasMaterialSaleStation> saleStationList;

    /**
     * 物料&商品-附件对象
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "物料&商品-附件对象")
    private List<BasMaterialAttachment> attachmentList;

    /**
     * 物料&商品-SKU明细对象
     */
    @Valid
    @TableField(exist = false)
    @ApiModelProperty(value = "物料&商品-SKU明细对象")
    private List<BasMaterialSku> basMaterialSkuList;

    @TableField(exist = false)
    @ApiModelProperty(value = "尺寸表上装部位信息列表")
    private List<TecModelPosInfor> modelPosInforList;

    @TableField(exist = false)
    @ApiModelProperty(value = "尺寸表下装部位信息列表")
    private List<TecModelPosInforDown> modelPosInforDownList;

    /**
     * 行号
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "行号")
    private int itemNum;

    @TableField(exist = false)
    @ApiModelProperty(value = "sku组详情列表")
    private List<BasSkuGroup> basSkuResponseList;

    @TableField(exist = false)
    @ApiModelProperty(value = "sku组详情列表")
    private List<BasSkuGroup> basSkuDownResponseList;

    @TableField(exist = false)
    @ApiModelProperty(value = "产品季档案列表")
    private ListSeasonResponse seasonResponse;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售价详细信息")
    private SalSalePriceItem salePriceDetail;


    @TableField(exist = false)
    @ApiModelProperty(value = "采购价详细信息")
    private PurPurchasePriceItem purchasePriceDetail;

    @TableField(exist = false)
    @ApiModelProperty(value = "外发加工价详细信息")
    private PurOutsourcePurchasePriceItem outsourcePurchasePriceDetail;

    @TableField(exist = false)
    @ApiModelProperty(value = "采购价/销售价")
    private BigDecimal price;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品条码Sid")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long barcodeSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品条码code")
    private String barcode;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品条码code")
    private String barcode2;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-物料询报议价单号")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long requestQuotationSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-物料采购价格记录明细表")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long priceInforItemSid;

    @ApiModelProperty(value = "报价（含税）")
    @Digits(integer = 11, fraction = 4, message = "报价整数位上限为11位，小数位上限为4位")
    private BigDecimal quotePriceTax;

    @Digits(integer = 6, fraction = 2, message = "最低起订量整数位上限为6位，小数位上限为2位")
    @ApiModelProperty(value = "最低起订量")
    private BigDecimal minOrderQuantity;

    @ApiModelProperty(value = "计价/递增减计量单位（数据字典的键值或配置档案的编码）")
    private String unitRecursion;

    @TableField(exist = false)
    @ApiModelProperty(value = "计价/递增减计量单位（数据字典的键值或配置档案的编码）")
    private String unitRecursionName;

    @Digits(integer = 4, fraction = 4, message = "单位换算比例（采购价单位/基本单位）整数位上限为4位，小数位上限为4位")
    @ApiModelProperty(value = "单位换算比例（采购价单位/基本单位）")
    private BigDecimal unitConversionRatePrice;

    @TableField(exist = false)
    @ApiModelProperty(value = "税率")
    private String taxRate;

    @TableField(exist = false)
    @ApiModelProperty(value = "税率值")
    private String taxRateName;

    @TableField(exist = false)
    @ApiModelProperty(value = "税率值")
    private BigDecimal taxRateValue;

    @TableField(exist = false)
    @ApiModelProperty(value = "采购价（含税）")
    private BigDecimal confirmPriceTax;

    @TableField(exist = false)
    @ApiModelProperty(value = "上装基准尺码")
    private String standardSku;

    @TableField(exist = false)
    @ApiModelProperty(value = "下装基准尺码")
    private String downStandardSku;

    @TableField(exist = false)
    @ApiModelProperty(value = "套装下装尺码编码")
    private String bottomsSkuCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "套装下装尺码名称")
    private String bottomsSkuName;

    @TableField(exist = false)
    @ApiModelProperty(value = "去重id")
    private String wheightRemoveId;

    /**
     * 工序编码
     */
    @ApiModelProperty(value = "工序编码")
    @TableField(exist = false)
    private String processCode;

    /**
     * 工序名称
     */
    @TableField(exist = false)
    @Excel(name = "工序名称")
    @ApiModelProperty(value = "工序名称")
    private String processName;

    /**
     * 物料/商品/服务编号（人工编码）
     */
    @Excel(name = "物料/商品/服务编号（人工编码）")
    @ApiModelProperty(value = "物料/商品/服务编号（人工编码）")
    private String manualMaterialCode;

    /**
     * 单位换算比例（基本计量单位/用量计量单位）
     */
    @Excel(name = "单位换算比例（基本计量单位/用量计量单位）")
    @Digits(integer = 4, fraction = 4, message = "单位换算比例整数位上限为4位，小数位上限为4位")
    @ApiModelProperty(value = "单位换算比例（基本计量单位/用量计量单位）")
    private BigDecimal unitConversionRate;

    @TableField(exist = false)
    @ApiModelProperty(value = "产品级别（数据字典的键值或配置档案的编码）")
    private String productLevel;

    @TableField(exist = false)
    @ApiModelProperty(value = "产品级别（数据字典的键值或配置档案的编码）")
    private String[] productLevelList;

    @TableField(exist = false)
    private BigDecimal innerPrice;

    @TableField(exist = false)
    private BigDecimal quotePrice;

    @TableField(exist = false)
    private BigDecimal confirmPrice;

    @TableField(exist = false)
    private BigDecimal checkPrice;

    @TableField(exist = false)
    private BigDecimal innerPriceTax;


    @TableField(exist = false)
    private BigDecimal checkPriceTax;

    @TableField(exist = false)
    private String exportType;

    @TableField(exist = false)
    private Boolean exit = false;

    @TableField(exist = false)
    private String[] zipperFlagList;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品线部位-线料对象")
    private List<TecProductLineposMat> tecProductLineposMatList;

    @TableField(exist = false)
    @ApiModelProperty(value = "生成标签pdf用，选择导出pdf的路径")
    private String outPath;

    @ApiModelProperty(value = "尺码组sid")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long sku2GroupSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "尺码组")
    private String sku2GroupName;

    @TableField(exist = false)
    @ApiModelProperty(value = "尺码组sid（多选）")
    private Long[] sku2GroupSidList;

    @Excel(name = "库存量")
    @ApiModelProperty(value = "库存量")
    @TableField(exist = false)
    private BigDecimal stockQuantity;

    @Excel(name = "取整方式")
    @ApiModelProperty(value = "取整方式")
    @TableField(exist = false)
    private String roundingType;

    @ApiModelProperty(value = "采购价含税")
    @TableField(exist = false)
    private BigDecimal purchasePriceTax;

    @TableField(exist = false)
    private BigDecimal quantity;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "合同交期")
    @TableField(exist = false)
    private Date contractDate;

    public String getZipperFlag() {
        if (zipperFlag == null) {
            return "";
        }
        return zipperFlag;
    }
}
