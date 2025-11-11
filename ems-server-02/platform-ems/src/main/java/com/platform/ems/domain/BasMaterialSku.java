package com.platform.ems.domain;

import java.math.BigDecimal;
import java.util.Date;

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
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Digits;

/**
 * 物料&商品-SKU明细对象 s_bas_material_sku
 *
 * @author linhongwei
 * @date 2021-03-12
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_bas_material_sku")
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasMaterialSku extends EmsBaseEntity {

    @TableField(exist = false)
    private String firstSort;

    @TableField(exist = false)
    private String secondSort;

    @TableField(exist = false)
    private String thirdSort;

    /**
     * 客户端口号
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /**
     * 系统ID-物料SKU信息
     */
    @TableId
    @Excel(name = "系统ID-物料SKU信息")
    @ApiModelProperty(value = "系统ID-物料SKU信息")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long materialSkuSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统ID-物料SKU信息 多选")
    private Long[] materialSkuSidList;

    /**
     * 系统ID-物料档案
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-物料档案")
    private Long materialSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统ID-物料档案")
    private Long[] materialSidList;

    /** 供方色号（冗余） */
    @Excel(name = "供方色号（冗余）")
    @ApiModelProperty(value = "供方色号（冗余）")
    private String supplierColorCode;
    /**
     * 系统ID-SKU档案
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-SKU档案")
    private Long skuSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "skuSids")
    private Long[] skuSidList;

    @Excel(name = "系统SID-SKU档案sid（套装的下装）")
    @ApiModelProperty(value = "系统SID-SKU档案sid（套装的下装）")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long skuSidDown;

    /**
     * SKU类型编码
     */
    @Excel(name = "SKU类型编码")
    @ApiModelProperty(value = "SKU类型编码")
    private String skuType;

    @TableField(exist = false)
    @ApiModelProperty(value = "SKU1类型编码")
    private String sku1Type;

    @TableField(exist = false)
    @ApiModelProperty(value = "SKU2类型编码")
    private String sku2Type;

    @TableField(exist = false)
    @ApiModelProperty(value = "SKU类型编码")
    private String[] skuTypeList;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品的尺码组sid")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long sku2GroupSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品的尺码组")
    private String sku2GroupName;

    @TableField(exist = false)
    @Excel(name = "计量单位")
    @ApiModelProperty(value = "计量单位")
    private String unitBaseName;

    @TableField(exist = false)
    @Excel(name = "bom用量单位")
    @ApiModelProperty(value = "bom用量单位")
    private String unitQuantityName;

    @TableField(exist = false)
    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人")
    private String creatorAccountName;

    /**
     * 图片路径
     */
    @Excel(name = "图片路径")
    @ApiModelProperty(value = "图片路径")
    private String picturePath;

    /**
     * 开发样检测结果
     */
    @Excel(name = "开发样检测结果")
    @ApiModelProperty(value = "开发样检测结果")
    private String sampleDetectResult;

    /**
     * 大货样检测结果
     */
    @Excel(name = "大货样检测结果")
    @ApiModelProperty(value = "大货样检测结果")
    private String productDetectResult;

    /**
     * 腰围市尺
     */
    @Excel(name = "腰围市尺")
    @ApiModelProperty(value = "腰围市尺")
    private String waistSize;

    /**
     * 童装尺码段
     */
    @Excel(name = "童装尺码段")
    @ApiModelProperty(value = "童装尺码段")
    private String sizeGroupKid;

    /**
     * 行号
     */
    @Excel(name = "行号")
    @ApiModelProperty(value = "行号")
    private int itemNum;

    @Digits(integer = 4, fraction = 2, message = "明细序号整数位上限为4位，小数位上限为2位")
    @ApiModelProperty(value = "序号")
    private BigDecimal sort;

    /**
     * 备注
     */
    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 物料&商品-SKU明细启用/停用状态
     */
    @ApiModelProperty(value = "物料&商品-SKU明细启用/停用状态")
    private String status;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料&商品-SKU明细启用/停用状态,多选")
    private String[] statusList;

    /**
     * 物料商品档案启用/停用状态
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "物料商品档案启用/停用状态")
    private String materialStatus;

    /**
     * sku启用/停用状态
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "sku启用/停用状态")
    private String skuStatus;

    /**
     * sku处理状态
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料&商品处理状态,多选")
    private String[] handleStatusList;

    /**
     * 创建人账号
     */
    @Excel(name = "创建人账号")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /**
     * 更新人账号
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccount;

    @TableField(exist = false)
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccountName;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 数据源系统
     */
    @Excel(name = "数据源系统")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;

    @TableField(exist = false)
    @ApiModelProperty(value = "是否已创建BOM（数据字典的键值或配置档案的编码）")
    private String isHasCreatedBom;

    /** SKU编码 */
    @TableField(exist = false)
    @ApiModelProperty(value = "SKU编码")
    private String skuCode;

    /** SKU编码 */
    @TableField(exist = false)
    @ApiModelProperty(value = "SKU编码")
    private String[] skuCodeList;

    /** SKU名称 */
    @TableField(exist = false)
    @ApiModelProperty(value = "SKU名称")
    private String skuName;

    @TableField(exist = false)
    @ApiModelProperty(value = "SKU名称2")
    private String skuName2;

    @TableField(exist = false)
    @ApiModelProperty(value = "sku数值")
    private BigDecimal skuNumeralValue;

    /** 下装尺码编码 */
    @TableField(exist = false)
    @ApiModelProperty(value = "下装尺码编码")
    private String bottomsSkuCode;

    /** 下装尺码名称 */
    @TableField(exist = false)
    @ApiModelProperty(value = "下装尺码名称")
    private String bottomsSkuName;

    @TableField(exist = false)
    @ApiModelProperty(value = "开始时间")
    private String beginTime;

    @TableField(exist = false)
    @ApiModelProperty(value ="结束时间")
    private String endTime;

    @TableField(exist = false)
    @ApiModelProperty(value = "页码")
    private Integer pageNum;

    @TableField(exist = false)
    @ApiModelProperty(value = "页容量")
    private Integer pageSize;


    @TableField(exist = false)
    @ApiModelProperty(value = "物料名称")
    private String materialName;

    @TableField(exist = false)
    @ApiModelProperty(value = "幅宽")
    private String width;

    @TableField(exist = false)
    @ApiModelProperty(value = "克重")
    private String gramWeight;

    @TableField(exist = false)
    @ApiModelProperty(value = "规格")
    private String specificationSize;

    @TableField(exist = false)
    @ApiModelProperty(value = "材质")
    private String materialComposition;

    @TableField(exist = false)
    @ApiModelProperty(value = "成分")
    private String composition;

    @TableField(exist = false)
    @ApiModelProperty(value = "型号")
    private String modelSize;

    @TableField(exist = false)
    @Length(max = 30, message = "供方编码长度不能大于30")
    @ApiModelProperty(value = "供方编码")
    private String supplierProductCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料类型-多选")
    private String[] materialTypeList;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料类型")
    private String materialType;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料类型")
    private String materialTypeName;

    @TableField(exist = false)
    @ApiModelProperty(value = "采购类型")
    private String purchaseType;

    @TableField(exist = false)
    @ApiModelProperty(value = "供应商sid")
    private Long vendorSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "供应商sid")
    private Long[] vendorSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "供应商编码")
    private String vendorCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "基本计量单位")
    private String unitBase;

    @TableField(exist = false)
    @ApiModelProperty(value = "bom用量单位")
    private String unitQuantity;

    @TableField(exist = false)
    @ApiModelProperty(value = "供应商名称")
    private String vendorName;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "物料分类sid")
    private Long materialClassSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料分类名称")
    private String materialClassName;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料编码")
    private String materialCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料（商品/服务）编码 精确查询")
    private String materialCodes;

    @TableField(exist = false)
    @ApiModelProperty(value = "开发员")
    private String developer;

    @TableField(exist = false)
    @ApiModelProperty(value = "采购价详细信息")
    private  PurPurchasePriceItem priceDetail;

    /** SKU维度数 */
    @TableField(exist = false)
    @Excel(name = "SKU维度数", dictType = "s_sku_dimension")
    @ApiModelProperty(value = "SKU维度数")
    private Integer skuDimension;

    @ApiModelProperty(value = "报价（含税）")
    @Digits(integer=11,fraction = 4,message = "报价整数位上限为11位，小数位上限为4位")
    private BigDecimal quotePriceTax;

    @TableField(exist = false)
    @ApiModelProperty(value = "税率")
    private String taxRate;

    @TableField(exist = false)
    @ApiModelProperty(value = "采购价（含税）")
    private BigDecimal confirmPriceTax;

    public void setSupplierSkuCode(String supplierSkuCode) {
        if (StrUtil.isNotBlank(supplierSkuCode)){
            supplierSkuCode = supplierSkuCode.replaceAll("\\s*", "");
        }
        this.supplierSkuCode = supplierSkuCode;
    }

    public void setSupplierSkuName(String supplierSkuName) {
        if (StrUtil.isNotBlank(supplierSkuName)){
            supplierSkuName = supplierSkuName.trim();
        }
        this.supplierSkuName = supplierSkuName;
    }

    @Length(max = 60, message = "供方sku编码长度不能超过60位")
    @ApiModelProperty(value = "供方sku编码")
    private String supplierSkuCode;

    @Length(max = 60, message = "供方sku名称长度不能超过60位")
    @ApiModelProperty(value = "供方sku名称")
    private String supplierSkuName;

    public void setCustomerSkuCode(String customerSkuCode) {
        if (StrUtil.isNotBlank(customerSkuCode)){
            customerSkuCode = customerSkuCode.replaceAll("\\s*", "");
        }
        this.customerSkuCode = customerSkuCode;
    }

    public void setCustomerSkuName(String customerSkuName) {
        if (StrUtil.isNotBlank(customerSkuName)){
            customerSkuName = customerSkuName.trim();
        }
        this.customerSkuName = customerSkuName;
    }

    @Length(max = 60, message = "客方SKU编码长度不能超过60位")
    @ApiModelProperty(value =" 客方SKU编码")
    private String customerSkuCode;

    @Length(max = 60, message = "客方SKU名称长度不能超过60位")
    @ApiModelProperty(value ="客方SKU名称")
    private String customerSkuName;

    @TableField(exist = false)
    @ApiModelProperty(value = "税率值")
    private String taxRateName;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-物料询报议价单号")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long requestQuotationSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-物料采购价格记录明细表")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long priceInforItemSid;

    @TableField(exist = false)
    private String customerSid;

    @TableField(exist = false)
    private Long[] customerSidList;

    @ApiModelProperty(value ="客户")
    @TableField(exist = false)
    private String customerName;

    @TableField(exist = false)
    @ApiModelProperty(value ="物料类别")
    private String materialCategory;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料（商品/服务）类别,多选")
    private String[] materialCategoryList;

    @TableField(exist = false)
    private String customerCode;

    @ApiModelProperty(value ="设计师账号")
    @TableField(exist = false)
    private String designerAccount;

    @ApiModelProperty(value ="设计师账号")
    @TableField(exist = false)
    private String[] designerAccountList;

    @TableField(exist = false)
    private String designerAccountName;

    @ApiModelProperty(value ="我司样衣号")
    @TableField(exist = false)
    private String sampleCodeSelf;

    @ApiModelProperty(value ="产品季")
    @TableField(exist = false)
    private String productSeasonSid;

    @TableField(exist = false)
    private String productSeasonCode;

    @TableField(exist = false)
    private String productSeasonName;

    /**
     * 版型编码
     */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "版型编码")
    private Long modelSid;

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
     * 生产工艺类型(编织方法)编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "生产工艺类型(编织方法)编码")
    private String productTechniqueType;

    /**
     * 生产工艺类型(编织方法)名称
     */
    @ApiModelProperty(value = "生产工艺类型(编织方法)名称")
    @TableField(exist = false)
    private String productTechniqueTypeName;

    /**
     * 客方编码（物料/商品/服务）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "客方编码（物料/商品/服务）")
    private String customerProductCode;

    /**
     * 客方样衣号
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "客方样衣号")
    private String sampleCodeCustomer;

    /**
     * 风格编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "风格编码")
    private String style;

    /**
     * 系列编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "系列编码")
    private String series;

    @TableField(exist = false)
    @ApiModelProperty(value = "采购类型")
    private String purchaseTypeName;

    @TableField(exist = false)
    @ApiModelProperty(value ="采购类型")
    private String[] purchaseTypeList;

    /**
     * 公司编码（公司档案的sid）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "公司编码（公司档案的sid）")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long companySid;

    /**
     * 公司名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    /**
     * 公司品牌sid
     */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司品牌sid")
    private Long companyBrandSid;

    /**
     * 公司品牌名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "公司品牌名称")
    private String companyBrandName;

    @TableField(exist = false)
    @ApiModelProperty(value = "价格维度")
    private String priceDimension;

    @TableField(exist = false)
    @ApiModelProperty(value = "供方样衣号")
    private String sampleCodeVendor;

    /**
     * 客方品牌sid
     */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "客方品牌sid")
    private Long customerBrandSid;

    /**
     * 客方品牌名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "客方品牌名称")
    private String customerBrandName;

    @TableField(exist = false)
    @ApiModelProperty(value = "上下装/套装（数据字典的键值）")
    private String upDownSuit;

    @TableField(exist = false)
    @ApiModelProperty(value = "封样结果")
    private String fengyangResult;

    @TableField(exist = false)
    @ApiModelProperty(value = "封样类型")
    private String fengyangType;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "样品/商品sid")
    private Long sampleSid;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-封样记录")
    private Long recordFengyangSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "技术转移结果")
    private String techtransferResult;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-技术转移记录")
    private Long recordTechtransferSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "是否创建BOM")
    private String isCreateBom;

    @TableField(exist = false)
    @ApiModelProperty(value = "bom处理状态")
    private String bomHandleStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "bom处理状态")
    private String[] bomHandleStatusList;

    @TableField(exist = false)
    @ApiModelProperty(value = "BOM启用/停用(按色)")
    private String bomStatus;
}
