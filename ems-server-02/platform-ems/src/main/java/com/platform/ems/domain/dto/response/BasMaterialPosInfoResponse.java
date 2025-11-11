package com.platform.ems.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.platform.common.annotation.Excel;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 物料&商品&服务档案对象 s_bas_material
 *
 * @author linhongwei
 * @date 2021-01-21
 */
public class BasMaterialPosInfoResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 系统ID-物料档案
     */
    @Excel(name = "系统ID-物料档案")
    @NotBlank(message = "系统ID-物料档案不能为空")
    private String materialSid;

    /**
     * 物料（商品/服务）名称
     */
    @Excel(name = "物料", readConverterExp = "商=品/服务")
    @NotBlank(message = "物料（商品/服务）名称不能为空")
    private String materialName;

    /**
     * 物料（商品/服务）类型编码
     */
    @Excel(name = "物料", readConverterExp = "商=品/服务")
    @NotBlank(message = "物料（商品/服务）类型编码不能为空")
    private String materialType;

    /**
     * 行业领域编码
     */
    @Excel(name = "行业领域编码")
    @NotBlank(message = "行业领域编码不能为空")
    private String industryField;

    /**
     * 物料（商品/服务）分类编码
     */
    @Excel(name = "物料", readConverterExp = "商=品/服务")
    @NotBlank(message = "物料（商品/服务）分类编码不能为空")
    private String materialClass;

    /**
     * 季节编码
     */
    @Excel(name = "季节编码") private String season;

    /**
     * 基本计量单位编码
     */
    @Excel(name = "基本计量单位编码")
    @NotBlank(message = "基本计量单位编码")
    private String unitBase;

    /**
     * 采购类型编码（默认）
     */
    @Excel(name = "采购类型编码", readConverterExp = "默=认") private String purchaseType;

    /**
     * 纱支
     */
    @Excel(name = "纱支") private String yarnCount;

    /**
     * 密度
     */
    @Excel(name = "密度") private String density;

    /**
     * 成分
     */
    @Excel(name = "成分") private String composition;

    /**
     * 幅宽（厘米）
     */
    @Excel(name = "幅宽", readConverterExp = "厘=米") private String width;

    /**
     * 材质
     */
    @Excel(name = "材质") private String materialComposition;

    /**
     * 型号
     */
    @Excel(name = "型号") private String modelSize;

    /**
     * 规格
     */
    @Excel(name = "规格") private String specification;

    /**
     * 克重
     */
    @Excel(name = "克重") private String gramWeight;

    /**
     * 尺寸
     */
    @Excel(name = "尺寸") private String size;

    /**
     * 号型
     */
    @Excel(name = "号型") private String zipperSize;

    /**
     * 口型
     */
    @Excel(name = "口型") private String zipperMonth;

    /**
     * 开发员
     */
    @Excel(name = "开发员") private String developer;

    /**
     * 工艺说明
     */
    @Excel(name = "工艺说明") private String processDesc;

    /**
     * 风险说明
     */
    @Excel(name = "风险说明") private String riskRemark;

    /**
     * 是否复核面料
     */
    @Excel(name = "是否复核面料")
    @Length(max = 1, message = "是否复核面料值太长")
    private String isCompositeMaterial;

    /**
     * 胚布/纱线说明
     */
    @Excel(name = "胚布/纱线说明") private String calicoYarnDescription;

    /**
     * 拉链标识
     */
    @Excel(name = "拉链标识")
    @Length(max = 1, message = "是拉链标识限制1个长度")
    private String zipperFlag;

    /**
     * 公司
     */
    @Excel(name = "公司") private String companySid;

    /**
     * 客户编码（默认）
     */
    @Excel(name = "客户编码", readConverterExp = "默=认") private Long customerSid;

    /**
     * 是否SKU物料
     */
    @Excel(name = "是否SKU物料")
    @NotBlank(message = "是否SKU物料不能为空")
    private String isSkuMaterial;

    /**
     * SKU维度数
     */
    @Excel(name = "SKU维度数")
    @NotNull(message = "SKU维度数不能为空")
    private Long skuDimension;

    /**
     * SKU1类型编码
     */
    @Excel(name = "SKU1类型编码") private String sku1Type;

    /**
     * SKU2类型编码
     */
    @Excel(name = "SKU2类型编码") private String sku2Type;

    /**
     * 物料类别编码
     */
    @Excel(name = "物料类别编码") private String materialCategory;

    /**
     * 启用/停用状态
     */
    @Excel(name = "启用/停用状态")
    @NotBlank(message = "启用/停用状态不能为空")
    private String status;

    /**
     * 处理状态
     */
    @Excel(name = "处理状态")
    @NotBlank(message = "处理状态不能为空")
    private String handleStatus;

    /**
     * 创建人账号
     */
    @Excel(name = "创建人账号")
    @NotBlank(message = "创建人账号不能为空")
    private String creatorAccount;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date createDate;

    /**
     * 更新人账号
     */
    @Excel(name = "更新人账号") private String updaterAccount;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date updateDate;

    /**
     * 确认账号
     */
    @Excel(name = "确认账号") private String confirmerAccount;

    /**
     * 确认时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "确认时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date confirmDate;

    /** 物料（商品/服务）编码 */
    @NotBlank(message = "物料（商品/服务）编码不能为空")
    private String materialCode;

    /** 产品类别编码 */
    @Excel(name = "产品类别编码")
    private String productCategory;

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }

    public String getMaterialSid() {
        return materialSid;
    }

    public void setMaterialSid(String materialSid) {
        this.materialSid = materialSid;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getMaterialType() {
        return materialType;
    }

    public void setMaterialType(String materialType) {
        this.materialType = materialType;
    }

    public String getIndustryField() {
        return industryField;
    }

    public void setIndustryField(String industryField) {
        this.industryField = industryField;
    }

    public String getMaterialClass() {
        return materialClass;
    }

    public void setMaterialClass(String materialClass) {
        this.materialClass = materialClass;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public String getUnitBase() {
        return unitBase;
    }

    public void setUnitBase(String unitBase) {
        this.unitBase = unitBase;
    }

    public String getPurchaseType() {
        return purchaseType;
    }

    public void setPurchaseType(String purchaseType) {
        this.purchaseType = purchaseType;
    }

    public String getYarnCount() {
        return yarnCount;
    }

    public void setYarnCount(String yarnCount) {
        this.yarnCount = yarnCount;
    }

    public String getDensity() {
        return density;
    }

    public void setDensity(String density) {
        this.density = density;
    }

    public String getComposition() {
        return composition;
    }

    public void setComposition(String composition) {
        this.composition = composition;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getMaterialComposition() {
        return materialComposition;
    }

    public void setMaterialComposition(String materialComposition) {
        this.materialComposition = materialComposition;
    }

    public String getModelSize() {
        return modelSize;
    }

    public void setModelSize(String modelSize) {
        this.modelSize = modelSize;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public String getGramWeight() {
        return gramWeight;
    }

    public void setGramWeight(String gramWeight) {
        this.gramWeight = gramWeight;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getZipperSize() {
        return zipperSize;
    }

    public void setZipperSize(String zipperSize) {
        this.zipperSize = zipperSize;
    }

    public String getZipperMonth() {
        return zipperMonth;
    }

    public void setZipperMonth(String zipperMonth) {
        this.zipperMonth = zipperMonth;
    }

    public String getDeveloper() {
        return developer;
    }

    public void setDeveloper(String developer) {
        this.developer = developer;
    }

    public String getProcessDesc() {
        return processDesc;
    }

    public void setProcessDesc(String processDesc) {
        this.processDesc = processDesc;
    }

    public String getRiskRemark() {
        return riskRemark;
    }

    public void setRiskRemark(String riskRemark) {
        this.riskRemark = riskRemark;
    }

    public String getIsCompositeMaterial() {
        return isCompositeMaterial;
    }

    public void setIsCompositeMaterial(String isCompositeMaterial) {
        this.isCompositeMaterial = isCompositeMaterial;
    }

    public String getCalicoYarnDescription() {
        return calicoYarnDescription;
    }

    public void setCalicoYarnDescription(String calicoYarnDescription) {
        this.calicoYarnDescription = calicoYarnDescription;
    }

    public String getZipperFlag() {
        return zipperFlag;
    }

    public void setZipperFlag(String zipperFlag) {
        this.zipperFlag = zipperFlag;
    }

    public String getCompanySid() {
        return companySid;
    }

    public void setCompanySid(String companySid) {
        this.companySid = companySid;
    }

    public Long getCustomerSid() {
        return customerSid;
    }

    public void setCustomerSid(Long customerSid) {
        this.customerSid = customerSid;
    }

    public String getIsSkuMaterial() {
        return isSkuMaterial;
    }

    public void setIsSkuMaterial(String isSkuMaterial) {
        this.isSkuMaterial = isSkuMaterial;
    }

    public Long getSkuDimension() {
        return skuDimension;
    }

    public void setSkuDimension(Long skuDimension) {
        this.skuDimension = skuDimension;
    }

    public String getSku1Type() {
        return sku1Type;
    }

    public void setSku1Type(String sku1Type) {
        this.sku1Type = sku1Type;
    }

    public String getSku2Type() {
        return sku2Type;
    }

    public void setSku2Type(String sku2Type) {
        this.sku2Type = sku2Type;
    }

    public String getMaterialCategory() {
        return materialCategory;
    }

    public void setMaterialCategory(String materialCategory) {
        this.materialCategory = materialCategory;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getHandleStatus() {
        return handleStatus;
    }

    public void setHandleStatus(String handleStatus) {
        this.handleStatus = handleStatus;
    }

    public String getCreatorAccount() {
        return creatorAccount;
    }

    public void setCreatorAccount(String creatorAccount) {
        this.creatorAccount = creatorAccount;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getUpdaterAccount() {
        return updaterAccount;
    }

    public void setUpdaterAccount(String updaterAccount) {
        this.updaterAccount = updaterAccount;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getConfirmerAccount() {
        return confirmerAccount;
    }

    public void setConfirmerAccount(String confirmerAccount) {
        this.confirmerAccount = confirmerAccount;
    }

    public Date getConfirmDate() {
        return confirmDate;
    }

    public void setConfirmDate(Date confirmDate) {
        this.confirmDate = confirmDate;
    }
}
