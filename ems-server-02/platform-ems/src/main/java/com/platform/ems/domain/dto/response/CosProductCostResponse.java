package com.platform.ems.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.BaseEntity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品成本核算主对象 s_cos_product_cost
 *
 * @author linhongwei
 * @date 2021-02-26
 */
public class CosProductCostResponse extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 客户端口号
     */
    private String clientId;

    /**
     * 系统ID-物料成本核算
     */
    @Excel(name = "系统ID-物料成本核算")
    private String productCostSid;

    /**
     * 物料编码
     */
    private String materialSid;

    /**
     * 物料SKU1档案
     */
    private String sku1Sid;

    /**
     * 物料SKU2档案
     */
    private String sku2Sid;

    /**
     * 成本核算维度
     */
    @Excel(name = "成本核算维度")
    private String costType;

    /**
     * 价格类型
     */
    @Excel(name = "价格类型")
    private String priceType;

    /**
     * 内部价小计(含税)-标准/采卖模式
     */
    @Excel(name = "内部价小计(含税)-标准/采卖模式")
    private BigDecimal innerPriceTaxStandard;

    /**
     * 业务类型
     */
    @Excel(name = "业务类型")
    private String businessType;

    /**
     * 供应商编码
     */
    @Excel(name = "供应商编码")
    private String vendorSid;

    /**
     * 内部价小计(不含税)-标准/采卖模式
     */
    @Excel(name = "内部价小计(不含税)-标准/采卖模式")
    private BigDecimal innerPriceStandard;

    /**
     * 内部价小计(含税)-委外模式
     */
    @Excel(name = "内部价小计(含税)-委外模式")
    private BigDecimal innerPriceTaxOutsource;

    /**
     * 客户编码
     */
    @Excel(name = "客户编码")
    private String customerSid;

    /**
     * 价格录入方式(特殊工艺)
     */
    @Excel(name = "价格录入方式(特殊工艺)")
    private String specialCraftPriceEnterType;

    /**
     * 内部价小计(不含税)-委外模式
     */
    @Excel(name = "内部价小计(不含税)-委外模式")
    private BigDecimal innerPriceOutsource;

    /**
     * 价格录入方式(生产费)
     */
    @Excel(name = "价格录入方式(生产费)")
    private String productPriceEnterType;

    /**
     * 报价小计(含税)-标准/采卖模式
     */
    @Excel(name = "报价小计(含税)-标准/采卖模式")
    private BigDecimal quotePriceTaxStandard;

    /**
     * 报价小计(不含税)-标准/采卖模式
     */
    @Excel(name = "报价小计(不含税)-标准/采卖模式")
    private BigDecimal quotePriceStandard;

    /**
     * 价格录入方式(其它费)
     */
    @Excel(name = "价格录入方式(其它费)")
    private String otherPriceEnterType;

    /**
     * 价格录入方式(协议价)
     */
    @Excel(name = "价格录入方式(协议价)")
    private String priceEnterType;

    /**
     * 报价小计(含税)-委外模式
     */
    @Excel(name = "报价小计(含税)-委外模式")
    private BigDecimal quotePriceTaxOutsource;

    /**
     * 报价小计(不含税)-委外模式
     */
    @Excel(name = "报价小计(不含税)-委外模式")
    private BigDecimal quotePriceOutsource;

    /**
     * 核定价小计(含税)-标准/采卖模式
     */
    @Excel(name = "核定价小计(含税)-标准/采卖模式")
    private BigDecimal checkPriceTaxStandard;

    /**
     * 核定价小计(不含税)-标准/采卖模式
     */
    @Excel(name = "核定价小计(不含税)-标准/采卖模式")
    private BigDecimal checkPriceStandard;

    /**
     * 核定价小计(含税)-委外模式
     */
    @Excel(name = "核定价小计(含税)-委外模式")
    private BigDecimal checkPriceTaxOutsource;

    /**
     * 核定价小计(不含税)-委外模式
     */
    @Excel(name = "核定价小计(不含税)-委外模式")
    private BigDecimal checkPriceOutsource;

    /**
     * 确认价小计(含税)-标准/采卖模式
     */
    @Excel(name = "确认价小计(含税)-标准/采卖模式")
    private BigDecimal confirmPriceTaxStandard;

    /**
     * 确认价小计(不含税)-标准/采卖模式
     */
    @Excel(name = "确认价小计(不含税)-标准/采卖模式")
    private BigDecimal confirmPriceStandard;

    /**
     * 确认价小计(含税)-委外模式
     */
    @Excel(name = "确认价小计(含税)-委外模式")
    private BigDecimal confirmPriceTaxOutsource;

    /**
     * 确认价小计(不含税)-委外模式
     */
    @Excel(name = "确认价小计(不含税)-委外模式")
    private BigDecimal confirmPriceOutsource;

    /**
     * 采购协议价(含税)-标准/采卖模式
     */
    @Excel(name = "采购协议价(含税)-标准/采卖模式")
    private BigDecimal purchasePriceTaxStandard;

    /**
     * 采购协议价(不含税)-标准/采卖模式
     */
    @Excel(name = "采购协议价(不含税)-标准/采卖模式")
    private BigDecimal purchasePriceStandard;

    /**
     * 采购协议价(含税)-委外模式
     */
    @Excel(name = "采购协议价(含税)-委外模式")
    private BigDecimal purchasePriceTaxOutsource;

    /**
     * 采购协议价(不含税)-委外模式
     */
    @Excel(name = "采购协议价(不含税)-委外模式")
    private BigDecimal purchasePriceOutsource;

    /**
     * 产前核算标准成本(含税)
     */
    @Excel(name = "产前核算标准成本(含税)")
    private BigDecimal standardCostTax;

    /**
     * 产前核算标准成本(不含税)
     */
    @Excel(name = "产前核算标准成本(不含税)")
    private BigDecimal standardCost;

    /**
     * 税率
     */
    @Excel(name = "税率")
    private BigDecimal taxRate;

    /**
     * 成本版本号
     */
    @Excel(name = "成本版本号")
    private String costVersionId;

    /**
     * 成本版本号（上一版本号）
     */
    @Excel(name = "成本版本号", readConverterExp = "上=一版本号")
    private String costVersionIdPre;

    /**
     * 系统ID-物料清单档案
     */
    @Excel(name = "系统ID-物料清单档案")
    private String bomSid;

    /**
     * 获取BOM版本号时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "获取BOM版本号时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date bomVersionDateGet;

    /**
     * 获取BOM版本号人员
     */
    @Excel(name = "获取BOM版本号人员")
    private String bomVersionNameGet;

    /**
     * 说明（报价）
     */
    @Excel(name = "说明", readConverterExp = "报=价")
    private String remarkInner;

    /**
     * 说明（报价）
     */
    @Excel(name = "说明", readConverterExp = "报=价")
    private String remarkQuote;

    /**
     * 货币
     */
    @Excel(name = "货币")
    private String currency;

    /**
     * 货币单位
     */
    @Excel(name = "货币单位")
    private String currencyUnit;

    /**
     * 说明（核定价）
     */
    @Excel(name = "说明", readConverterExp = "核=定价")
    private String remarkCheck;

    /**
     * 说明（确认价）
     */
    @Excel(name = "说明", readConverterExp = "确=认价")
    private String remarkConfirm;

    /**
     * 启用/停用状态
     */
    @Excel(name = "启用/停用状态")
    private String status;

    /**
     * 处理状态
     */
    @Excel(name = "处理状态")
    private String handleStatus;

    /**
     * 创建人账号
     */
    @Excel(name = "创建人账号")
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
    @Excel(name = "更新人账号")
    private String updaterAccount;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date updateDate;

    /**
     * 确认人账号
     */
    @Excel(name = "确认人账号")
    private String confirmerAccount;

    /**
     * 确认时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "确认时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date confirmDate;

    /**
     * 数据源系统
     */
    @Excel(name = "数据源系统")
    private String dataSourceSys;

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setProductCostSid(String productCostSid) {
        this.productCostSid = productCostSid;
    }

    public String getProductCostSid() {
        return productCostSid;
    }

    public void setMaterialSid(String materialSid) {
        this.materialSid = materialSid;
    }

    public String getMaterialSid() {
        return materialSid;
    }

    public void setSku1Sid(String sku1Sid) {
        this.sku1Sid = sku1Sid;
    }

    public String getSku1Sid() {
        return sku1Sid;
    }

    public void setSku2Sid(String sku2Sid) {
        this.sku2Sid = sku2Sid;
    }

    public String getSku2Sid() {
        return sku2Sid;
    }

    public void setCostType(String costType) {
        this.costType = costType;
    }

    public String getCostType() {
        return costType;
    }

    public void setPriceType(String priceType) {
        this.priceType = priceType;
    }

    public String getPriceType() {
        return priceType;
    }

    public void setInnerPriceTaxStandard(BigDecimal innerPriceTaxStandard) {
        this.innerPriceTaxStandard = innerPriceTaxStandard;
    }

    public BigDecimal getInnerPriceTaxStandard() {
        return innerPriceTaxStandard;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setVendorSid(String vendorSid) {
        this.vendorSid = vendorSid;
    }

    public String getVendorSid() {
        return vendorSid;
    }

    public void setInnerPriceStandard(BigDecimal innerPriceStandard) {
        this.innerPriceStandard = innerPriceStandard;
    }

    public BigDecimal getInnerPriceStandard() {
        return innerPriceStandard;
    }

    public void setInnerPriceTaxOutsource(BigDecimal innerPriceTaxOutsource) {
        this.innerPriceTaxOutsource = innerPriceTaxOutsource;
    }

    public BigDecimal getInnerPriceTaxOutsource() {
        return innerPriceTaxOutsource;
    }

    public void setCustomerSid(String customerSid) {
        this.customerSid = customerSid;
    }

    public String getCustomerSid() {
        return customerSid;
    }

    public void setSpecialCraftPriceEnterType(String specialCraftPriceEnterType) {
        this.specialCraftPriceEnterType = specialCraftPriceEnterType;
    }

    public String getSpecialCraftPriceEnterType() {
        return specialCraftPriceEnterType;
    }

    public void setInnerPriceOutsource(BigDecimal innerPriceOutsource) {
        this.innerPriceOutsource = innerPriceOutsource;
    }

    public BigDecimal getInnerPriceOutsource() {
        return innerPriceOutsource;
    }

    public void setProductPriceEnterType(String productPriceEnterType) {
        this.productPriceEnterType = productPriceEnterType;
    }

    public String getProductPriceEnterType() {
        return productPriceEnterType;
    }

    public void setQuotePriceTaxStandard(BigDecimal quotePriceTaxStandard) {
        this.quotePriceTaxStandard = quotePriceTaxStandard;
    }

    public BigDecimal getQuotePriceTaxStandard() {
        return quotePriceTaxStandard;
    }

    public void setQuotePriceStandard(BigDecimal quotePriceStandard) {
        this.quotePriceStandard = quotePriceStandard;
    }

    public BigDecimal getQuotePriceStandard() {
        return quotePriceStandard;
    }

    public void setOtherPriceEnterType(String otherPriceEnterType) {
        this.otherPriceEnterType = otherPriceEnterType;
    }

    public String getOtherPriceEnterType() {
        return otherPriceEnterType;
    }

    public void setPriceEnterType(String priceEnterType) {
        this.priceEnterType = priceEnterType;
    }

    public String getPriceEnterType() {
        return priceEnterType;
    }

    public void setQuotePriceTaxOutsource(BigDecimal quotePriceTaxOutsource) {
        this.quotePriceTaxOutsource = quotePriceTaxOutsource;
    }

    public BigDecimal getQuotePriceTaxOutsource() {
        return quotePriceTaxOutsource;
    }

    public void setQuotePriceOutsource(BigDecimal quotePriceOutsource) {
        this.quotePriceOutsource = quotePriceOutsource;
    }

    public BigDecimal getQuotePriceOutsource() {
        return quotePriceOutsource;
    }

    public void setCheckPriceTaxStandard(BigDecimal checkPriceTaxStandard) {
        this.checkPriceTaxStandard = checkPriceTaxStandard;
    }

    public BigDecimal getCheckPriceTaxStandard() {
        return checkPriceTaxStandard;
    }

    public void setCheckPriceStandard(BigDecimal checkPriceStandard) {
        this.checkPriceStandard = checkPriceStandard;
    }

    public BigDecimal getCheckPriceStandard() {
        return checkPriceStandard;
    }

    public void setCheckPriceTaxOutsource(BigDecimal checkPriceTaxOutsource) {
        this.checkPriceTaxOutsource = checkPriceTaxOutsource;
    }

    public BigDecimal getCheckPriceTaxOutsource() {
        return checkPriceTaxOutsource;
    }

    public void setCheckPriceOutsource(BigDecimal checkPriceOutsource) {
        this.checkPriceOutsource = checkPriceOutsource;
    }

    public BigDecimal getCheckPriceOutsource() {
        return checkPriceOutsource;
    }

    public void setConfirmPriceTaxStandard(BigDecimal confirmPriceTaxStandard) {
        this.confirmPriceTaxStandard = confirmPriceTaxStandard;
    }

    public BigDecimal getConfirmPriceTaxStandard() {
        return confirmPriceTaxStandard;
    }

    public void setConfirmPriceStandard(BigDecimal confirmPriceStandard) {
        this.confirmPriceStandard = confirmPriceStandard;
    }

    public BigDecimal getConfirmPriceStandard() {
        return confirmPriceStandard;
    }

    public void setConfirmPriceTaxOutsource(BigDecimal confirmPriceTaxOutsource) {
        this.confirmPriceTaxOutsource = confirmPriceTaxOutsource;
    }

    public BigDecimal getConfirmPriceTaxOutsource() {
        return confirmPriceTaxOutsource;
    }

    public void setConfirmPriceOutsource(BigDecimal confirmPriceOutsource) {
        this.confirmPriceOutsource = confirmPriceOutsource;
    }

    public BigDecimal getConfirmPriceOutsource() {
        return confirmPriceOutsource;
    }

    public void setPurchasePriceTaxStandard(BigDecimal purchasePriceTaxStandard) {
        this.purchasePriceTaxStandard = purchasePriceTaxStandard;
    }

    public BigDecimal getPurchasePriceTaxStandard() {
        return purchasePriceTaxStandard;
    }

    public void setPurchasePriceStandard(BigDecimal purchasePriceStandard) {
        this.purchasePriceStandard = purchasePriceStandard;
    }

    public BigDecimal getPurchasePriceStandard() {
        return purchasePriceStandard;
    }

    public void setPurchasePriceTaxOutsource(BigDecimal purchasePriceTaxOutsource) {
        this.purchasePriceTaxOutsource = purchasePriceTaxOutsource;
    }

    public BigDecimal getPurchasePriceTaxOutsource() {
        return purchasePriceTaxOutsource;
    }

    public void setPurchasePriceOutsource(BigDecimal purchasePriceOutsource) {
        this.purchasePriceOutsource = purchasePriceOutsource;
    }

    public BigDecimal getPurchasePriceOutsource() {
        return purchasePriceOutsource;
    }

    public void setStandardCostTax(BigDecimal standardCostTax) {
        this.standardCostTax = standardCostTax;
    }

    public BigDecimal getStandardCostTax() {
        return standardCostTax;
    }

    public void setStandardCost(BigDecimal standardCost) {
        this.standardCost = standardCost;
    }

    public BigDecimal getStandardCost() {
        return standardCost;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setCostVersionId(String costVersionId) {
        this.costVersionId = costVersionId;
    }

    public String getCostVersionId() {
        return costVersionId;
    }

    public void setCostVersionIdPre(String costVersionIdPre) {
        this.costVersionIdPre = costVersionIdPre;
    }

    public String getCostVersionIdPre() {
        return costVersionIdPre;
    }

    public void setBomSid(String bomSid) {
        this.bomSid = bomSid;
    }

    public String getBomSid() {
        return bomSid;
    }

    public void setBomVersionDateGet(Date bomVersionDateGet) {
        this.bomVersionDateGet = bomVersionDateGet;
    }

    public Date getBomVersionDateGet() {
        return bomVersionDateGet;
    }

    public void setBomVersionNameGet(String bomVersionNameGet) {
        this.bomVersionNameGet = bomVersionNameGet;
    }

    public String getBomVersionNameGet() {
        return bomVersionNameGet;
    }

    public void setRemarkInner(String remarkInner) {
        this.remarkInner = remarkInner;
    }

    public String getRemarkInner() {
        return remarkInner;
    }

    public void setRemarkQuote(String remarkQuote) {
        this.remarkQuote = remarkQuote;
    }

    public String getRemarkQuote() {
        return remarkQuote;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrencyUnit(String currencyUnit) {
        this.currencyUnit = currencyUnit;
    }

    public String getCurrencyUnit() {
        return currencyUnit;
    }

    public void setRemarkCheck(String remarkCheck) {
        this.remarkCheck = remarkCheck;
    }

    public String getRemarkCheck() {
        return remarkCheck;
    }

    public void setRemarkConfirm(String remarkConfirm) {
        this.remarkConfirm = remarkConfirm;
    }

    public String getRemarkConfirm() {
        return remarkConfirm;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setHandleStatus(String handleStatus) {
        this.handleStatus = handleStatus;
    }

    public String getHandleStatus() {
        return handleStatus;
    }

    public void setCreatorAccount(String creatorAccount) {
        this.creatorAccount = creatorAccount;
    }

    public String getCreatorAccount() {
        return creatorAccount;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setUpdaterAccount(String updaterAccount) {
        this.updaterAccount = updaterAccount;
    }

    public String getUpdaterAccount() {
        return updaterAccount;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setConfirmerAccount(String confirmerAccount) {
        this.confirmerAccount = confirmerAccount;
    }

    public String getConfirmerAccount() {
        return confirmerAccount;
    }

    public void setConfirmDate(Date confirmDate) {
        this.confirmDate = confirmDate;
    }

    public Date getConfirmDate() {
        return confirmDate;
    }

    public void setDataSourceSys(String dataSourceSys) {
        this.dataSourceSys = dataSourceSys;
    }

    public String getDataSourceSys() {
        return dataSourceSys;
    }

}
