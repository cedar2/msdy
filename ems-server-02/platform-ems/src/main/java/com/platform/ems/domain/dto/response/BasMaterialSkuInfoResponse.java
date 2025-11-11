package com.platform.ems.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.platform.common.annotation.Excel;

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
public class BasMaterialSkuInfoResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 系统ID-物料SKU信息 */
    @Excel(name = "系统ID-物料SKU信息")
    @NotBlank(message = "系统ID-物料SKU信息不能为空")
    private String materialSkuSid;

    /** 系统ID-物料档案 */
    @NotBlank(message = "系统ID-物料档案不能为空")
    private String materialSid;

    /** 系统ID-SKU档案 */
    @NotBlank(message = "系统ID-SKU档案不能为空")
    private String skuSid;

    /** SKU类型编码 */
    @Excel(name = "SKU类型编码")
    @NotBlank(message = "SKU类型编码不能为空")
    private String skuType;

    /** 启用/停用状态 */
    @Excel(name = "启用/停用状态")
    @NotBlank(message = "启用/停用状态不能为空")
    private String status;

    /** 处理状态 */
    @Excel(name = "处理状态")
    @NotBlank(message = "处理状态不能为空")
    private String handleStatus;

    /** 创建人账号 */
    @Excel(name = "创建人账号")
    @NotBlank(message = "创建人账号不能为空")
    private String creatorAccount;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "创建时间不能为空")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date createDate;

    /** 更新人账号 */
    @Excel(name = "更新人账号")
    private String updaterAccount;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date updateDate;

    public String getMaterialSkuSid() {
        return materialSkuSid;
    }

    public void setMaterialSkuSid(String materialSkuSid) {
        this.materialSkuSid = materialSkuSid;
    }

    public String getMaterialSid() {
        return materialSid;
    }

    public void setMaterialSid(String materialSid) {
        this.materialSid = materialSid;
    }

    public String getSkuSid() {
        return skuSid;
    }

    public void setSkuSid(String skuSid) {
        this.skuSid = skuSid;
    }

    public String getSkuType() {
        return skuType;
    }

    public void setSkuType(String skuType) {
        this.skuType = skuType;
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
}
