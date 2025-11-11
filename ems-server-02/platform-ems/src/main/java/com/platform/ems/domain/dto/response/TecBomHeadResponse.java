package com.platform.ems.domain.dto.response;

import com.platform.common.annotation.Excel;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @description:
 * @author: Hu JJ
 * @date: 2021-02-03
 */
public class TecBomHeadResponse implements Serializable {

    /** 客户端口号 */
    @Excel(name = "客户端口号")
    private String clientId;

    /** 系统ID-物料BOM档案 */
    @Excel(name = "系统ID-物料BOM档案")
    private String bomSid;

    /** 系统ID-物料档案 */
    @Excel(name = "系统ID-物料档案")
    private String materialSid;

    /** 物料（商品/服务）编码 */
    private String materialCode;

    /** 我司样衣号 */
    @Excel(name = "我司样衣号")
    private String sampleCodeSelf;

    /** 物料（商品/服务）名称 */
    @Excel(name = "物料", readConverterExp = "商=品/服务")
    private String materialName;

    /** 产品季编码 */
    @Excel(name = "产品季编码")
    private Long seasonSid;

    /** 设计师账号 */
    @Excel(name = "设计师账号")
    private String designerAccount;

    /** 公司 */
    @Excel(name = "公司")
    private Long companySid;

    /** 品牌编码 */
    @Excel(name = "品牌编码")
    private String brand;

    /** BOM版本号 */
    @Excel(name = "BOM版本号")
    private String bomVersionId;

    /** 客方编码（物料/商品/服务） */
    @Excel(name = "客方编码", readConverterExp = "物=料/商品/服务")
    private String customerProductCode;

    /** 供方样衣号 */
    @Excel(name = "供方样衣号")
    private String sampleCodeVendor;

    /** 创建人账号 */
    @Excel(name = "创建人账号")
    private String creatorAccount;

    /** 处理状态 */
    @Excel(name = "处理状态")
    private String handleStatus;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getBomSid() {
        return bomSid;
    }

    public void setBomSid(String bomSid) {
        this.bomSid = bomSid;
    }

    public String getMaterialSid() {
        return materialSid;
    }

    public void setMaterialSid(String materialSid) {
        this.materialSid = materialSid;
    }

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }

    public String getSampleCodeSelf() {
        return sampleCodeSelf;
    }

    public void setSampleCodeSelf(String sampleCodeSelf) {
        this.sampleCodeSelf = sampleCodeSelf;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public Long getSeasonSid() {
        return seasonSid;
    }

    public void setSeasonSid(Long seasonSid) {
        this.seasonSid = seasonSid;
    }

    public String getDesignerAccount() {
        return designerAccount;
    }

    public void setDesignerAccount(String designerAccount) {
        this.designerAccount = designerAccount;
    }

    public Long getCompanySid() {
        return companySid;
    }

    public void setCompanySid(Long companySid) {
        this.companySid = companySid;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getBomVersionId() {
        return bomVersionId;
    }

    public void setBomVersionId(String bomVersionId) {
        this.bomVersionId = bomVersionId;
    }

    public String getCustomerProductCode() {
        return customerProductCode;
    }

    public void setCustomerProductCode(String customerProductCode) {
        this.customerProductCode = customerProductCode;
    }

    public String getSampleCodeVendor() {
        return sampleCodeVendor;
    }

    public void setSampleCodeVendor(String sampleCodeVendor) {
        this.sampleCodeVendor = sampleCodeVendor;
    }

    public String getCreatorAccount() {
        return creatorAccount;
    }

    public void setCreatorAccount(String creatorAccount) {
        this.creatorAccount = creatorAccount;
    }

    public String getHandleStatus() {
        return handleStatus;
    }

    public void setHandleStatus(String handleStatus) {
        this.handleStatus = handleStatus;
    }
}
