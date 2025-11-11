package com.platform.ems.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.platform.common.annotation.Excel;

import java.io.Serializable;
import java.util.Date;

/**
 * 服务档案对象 s_bas_material
 *
 * @author linhongwei
 * @date 2021-01-21
 */
public class BasServeResponse implements Serializable {

    /** 客户端口号 */
    private String clientId;

    /** 系统ID-物料档案 */
    private String materialSid;

    /** 物料（商品/服务）编码 */
    @Excel(name = "物料（商品/服务）编码")
    private String materialCode;

    /** 物料（商品/服务）类型编码 */
    @Excel(name = "物料", readConverterExp = "商=品/服务")
    private String materialType;

    /** 物料（商品/服务）分类编码 */
    @Excel(name = "物料", readConverterExp = "商=品/服务")
    private String materialClass;

    /** 基本计量单位编码 */
    @Excel(name = "基本计量单位编码")
    private String unitBase;

    /** 物料（商品/服务）名称 */
    @Excel(name = "物料名称")
    private String materialName;

    /** 商品款号（用于服务类档案） */
    @Excel(name = "商品款号", readConverterExp = "用于服务类档案")
    private Long productCode;

    /** 部位（用于服务类档案） */
    @Excel(name = "部位", readConverterExp = "用于服务类档案")
    private String position;

    /** 规格 */
    @Excel(name = "规格")
    private String specification;

    /** 公司 */
    @Excel(name = "公司")
    private String companySid;

    /** 公司名称 */
    @Excel(name = "公司名称")
    private String companyName;

    /** 创建人账号 */
    @Excel(name = "创建人账号")
    private String creatorAccount;

    /** 处理状态 */
    @Excel(name = "处理状态")
    private String handleStatus;

    /** 启用/停用状态 */
    @Excel(name = "启用/停用状态")
    private String status;

    /** 备注 */
    @Excel(name = "备注")
    private String remark;

    /** 工艺说明 */
    @Excel(name = "工艺说明")
    private String processDesc;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date createDate;

    /** 更新人账号 */
    @Excel(name = "更新人账号")
    private String updaterAccount;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date updateDate;

    /** 确认账号 */
    @Excel(name = "确认账号")
    private String confirmerAccount;

    /** 确认时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "确认时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date confirmDate;

    /** 数据源系统 */
    private String dataSourceSys;

    /** 开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date beginTime;

    /** 结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endTime;

    private String industryField;
    private String isSkuMaterial;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getIndustryField() {
        return industryField;
    }

    public void setIndustryField(String industryField) {
        this.industryField = industryField;
    }

    public String getIsSkuMaterial() {
        return isSkuMaterial;
    }

    public void setIsSkuMaterial(String isSkuMaterial) {
        this.isSkuMaterial = isSkuMaterial;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
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

    public String getMaterialType() {
        return materialType;
    }

    public void setMaterialType(String materialType) {
        this.materialType = materialType;
    }

    public String getMaterialClass() {
        return materialClass;
    }

    public void setMaterialClass(String materialClass) {
        this.materialClass = materialClass;
    }

    public String getUnitBase() {
        return unitBase;
    }

    public void setUnitBase(String unitBase) {
        this.unitBase = unitBase;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public Long getProductCode() {
        return productCode;
    }

    public void setProductCode(Long productCode) {
        this.productCode = productCode;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public String getCompanySid() {
        return companySid;
    }

    public void setCompanySid(String companySid) {
        this.companySid = companySid;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getProcessDesc() {
        return processDesc;
    }

    public void setProcessDesc(String processDesc) {
        this.processDesc = processDesc;
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

    public String getDataSourceSys() {
        return dataSourceSys;
    }

    public void setDataSourceSys(String dataSourceSys) {
        this.dataSourceSys = dataSourceSys;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
