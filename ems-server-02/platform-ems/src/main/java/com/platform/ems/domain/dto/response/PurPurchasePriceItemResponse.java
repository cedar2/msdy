package com.platform.ems.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.platform.common.annotation.Excel;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author Pinzhen Chen
 * @Date 2021/2/4 15:13
 */
@Data
public class PurPurchasePriceItemResponse implements Serializable {
    /** 系统ID-物料采购价明细信息 */
    @Excel(name = "系统ID-物料采购价明细信息")
    private String purchasePriceInforItemSid;

    /** 系统ID-物料采购价信息 */
    private String purchasePriceInforSid;

    /** 有效期（起） */
    private Date startDate;

    /** 有效期（止） */
    private Date endDate;

    /** 阶梯类型 */
    private String cascadeType;

    /** 价格录入方式 */
    @Excel(name = "价格录入方式")
    private String priceEnterType;

    /** 递增减计量单位 */
    @Excel(name = "递增减计量单位")
    private String unitRecursion;

    /** 基准量 */
    @Excel(name = "基准量")
    private BigDecimal referenceQuantity;

    /** 递增量 */
    @Excel(name = "递增量")
    private BigDecimal increQuantity;

    /** 递减量 */
    @Excel(name = "递减量")
    private BigDecimal decreQuantity;

    /** 价格最小起算量 */
    @Excel(name = "价格最小起算量")
    private BigDecimal priceMixQuantity;

    /** 取整方式(递增减) */
    @Excel(name = "取整方式(递增减)")
    private String quantityRoundType;

    /** 递增采购价(含税) */
    @Excel(name = "递增采购价(含税)")
    private BigDecimal increPurPriceTax;

    /** 递增采购价(不含税) */
    @Excel(name = "递增采购价(不含税)")
    private BigDecimal increPurPrice;

    /** 递减采购价(含税) */
    @Excel(name = "递减采购价(含税)")
    private BigDecimal decPurPriceTax;

    /** 递减采购价(不含税) */
    @Excel(name = "递减采购价(不含税)")
    private BigDecimal decPurPrice;

    /** 采购价(含税) */
    @Excel(name = "采购价(含税)")
    private BigDecimal purchasePriceTax;

    /** 采购价(不含税) */
    @Excel(name = "采购价(不含税)")
    private BigDecimal purchasePrice;

    /** 税率 */
    @Excel(name = "税率")
    private BigDecimal taxRate;

    /** 币种 */
    @Excel(name = "币种")
    private String currency;

    /** 货币单位 */
    @Excel(name = "货币单位")
    private String currencyUnit;

    /** 采购价格单位 */
    @Excel(name = "采购价格单位")
    private String priceUnit;

    /** 采购价更新人账号 */
    @Excel(name = "采购价更新人账号")
    private String purchaseUpdaterAccount;

    /** 采购价更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "采购价更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date purchaseUpdateDate;

    /** 创建人账号 */
    @Excel(name = "创建人账号")
    private String creatorAccount;

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

    /** 确认人账号 */
    @Excel(name = "确认人账号")
    private String confirmerAccount;

    /** 确认时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "确认时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date confirmDate;

    /**
     * 备注
     */
    private String remark;


}
