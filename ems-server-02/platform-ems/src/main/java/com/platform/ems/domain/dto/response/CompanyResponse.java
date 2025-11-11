package com.platform.ems.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.platform.common.annotation.Excel;

import java.util.Date;

/**
 * @description: 公司档案
 * @author: hjj
 * @date: 2021-01-22
 */
public class CompanyResponse {

    /** 系统ID-公司档案 */
    private String companySid;

    /** 公司代码 */
    @Excel(name = "公司代码")
    private String companyCode;

    /** 公司名称 */
    @Excel(name = "公司名称")
    private String companyName;

    /** 法人/负责人姓名 */
    @Excel(name = "法人/负责人姓名")
    private String ownerName;

    /** 公司类型的编码 */
    @Excel(name = "公司类型的编码")
    private String companyType;

    /** 统一信用代码证号 */
    @Excel(name = "统一信用代码证号")
    private String creditCode;

    /** 公司类别的编码 */
    @Excel(name = "公司类别的编码")
    private String companyCategory;

    /** 营业执照-有效期从 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "营业执照-有效期从", width = 30, dateFormat = "yyyy-MM-dd")
    private Date creditStartDate;

    /** 营业执照-有效期至 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "营业执照-有效期至", width = 30, dateFormat = "yyyy-MM-dd")
    private Date creditEndDate;

    /** 处理状态 */
    @Excel(name = "处理状态")
    private String handleStatus;

    /** 启用/停用状态 */
    @Excel(name = "启用/停用状态")
    private String status;

    /** 开票电话 */
    @Excel(name = "开票电话")
    private String invoiceTel;

    /** 办公地址 */
    @Excel(name = "办公地址")
    private String officeAddr;

    /** 开票地址 */
    @Excel(name = "开票地址")
    private String invoiceAddr;

    /** 注册地址 */
    @Excel(name = "注册地址")
    private String registerAddr;

    /** 备注 */
    private String remark;

    /** 营业范围 */
    private String businessScope;

    /** 创建人账号 */
    @Excel(name = "创建人账号")
    private String creatorAccount;

    /** 创建时间 **/
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date createDate;

    /** 更新人账号 */
    @Excel(name = "更新人账号")
    private String updaterAccount;

    /** 更新时间 **/
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date updateDate;

    /** 确认人账号 */
    @Excel(name = "确认人账号")
    private String confirmerAccount;

    /** 确认时间 **/
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "确认时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date confirmDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间从", width = 30, dateFormat = "yyyy-MM-dd")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间至", width = 30, dateFormat = "yyyy-MM-dd")
    private Date endTime;

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getBusinessScope() {
        return businessScope;
    }

    public void setBusinessScope(String businessScope) {
        this.businessScope = businessScope;
    }

    public String getCompanySid() {
        return companySid;
    }

    public void setCompanySid(String companySid) {
        this.companySid = companySid;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getCompanyType() {
        return companyType;
    }

    public void setCompanyType(String companyType) {
        this.companyType = companyType;
    }

    public String getCreditCode() {
        return creditCode;
    }

    public void setCreditCode(String creditCode) {
        this.creditCode = creditCode;
    }

    public String getCompanyCategory() {
        return companyCategory;
    }

    public void setCompanyCategory(String companyCategory) {
        this.companyCategory = companyCategory;
    }

    public Date getCreditStartDate() {
        return creditStartDate;
    }

    public void setCreditStartDate(Date creditStartDate) {
        this.creditStartDate = creditStartDate;
    }

    public Date getCreditEndDate() {
        return creditEndDate;
    }

    public void setCreditEndDate(Date creditEndDate) {
        this.creditEndDate = creditEndDate;
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

    public String getInvoiceTel() {
        return invoiceTel;
    }

    public void setInvoiceTel(String invoiceTel) {
        this.invoiceTel = invoiceTel;
    }

    public String getOfficeAddr() {
        return officeAddr;
    }

    public void setOfficeAddr(String officeAddr) {
        this.officeAddr = officeAddr;
    }

    public String getInvoiceAddr() {
        return invoiceAddr;
    }

    public void setInvoiceAddr(String invoiceAddr) {
        this.invoiceAddr = invoiceAddr;
    }

    public String getRegisterAddr() {
        return registerAddr;
    }

    public void setRegisterAddr(String registerAddr) {
        this.registerAddr = registerAddr;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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
