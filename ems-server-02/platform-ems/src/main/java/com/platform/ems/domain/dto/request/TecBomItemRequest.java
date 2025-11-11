package com.platform.ems.domain.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.platform.common.annotation.Excel;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @description:
 * @author: Hu JJ
 * @date: 2021-02-03
 */
public class TecBomItemRequest implements Serializable {

    /** 系统ID-物料BOM档案 */
    @Excel(name = "系统ID-物料BOM档案")
    private String bomSid;

    /** 系统ID-物料档案 */
    private String materialSid;

    /** 系统ID-物料BOM组件明细 */
    @Excel(name = "系统ID-物料BOM组件明细")
    private String bomItemSid;

    /** 物料（商品/服务）编码 */
    @NotBlank(message = "物料（商品/服务）编码不能为空")
    private String materialCode;

    /** 物料（商品/服务）名称 */
    @Excel(name = "物料", readConverterExp = "商=品/服务")
    @NotBlank(message = "物料（商品/服务）名称不能为空")
    private String materialName;

    /** 是否主面料 */
    @Excel(name = "是否主面料")
    private String isMainFabric;

    /** BOM组件物料SKU1档案 */
    @Excel(name = "BOM组件物料SKU1档案")
    private String bomMaterialSku1Sid;

    /** BOM组件物料SKU2档案 */
    @Excel(name = "BOM组件物料SKU2档案")
    private String bomMaterialSku2Sid;

    /** 部位编码 */
    @Excel(name = "部位编码")
    private String positionCode;

    /** 内部用量 */
    @Excel(name = "内部用量")
    private BigDecimal innerQuantity;

    /** 内部损耗率（%） */
    @Excel(name = "内部损耗率", readConverterExp = "%=")
    private BigDecimal innerLossRate;

    /** 报价用量 */
    @Excel(name = "报价用量")
    private BigDecimal quoteQuantity;

    /** 报价损耗率（%） */
    @Excel(name = "报价损耗率", readConverterExp = "%=")
    private BigDecimal quoteLossRate;

    /** 核价用量 */
    @Excel(name = "核价用量")
    private BigDecimal checkQuantity;

    /** 核价损耗率（%） */
    @Excel(name = "核价损耗率", readConverterExp = "%=")
    private BigDecimal checkLossRate;

    /** 确认用量 */
    @Excel(name = "确认用量")
    private BigDecimal confirmQuantity;

    /** 确认损耗率（%） */
    @Excel(name = "确认损耗率", readConverterExp = "%=")
    private BigDecimal confirmLossRate;

    /** 基本计量单位编码 */
    @Excel(name = "基本计量单位编码")
    private String unitBase;

    /** 取整方式（损耗） */
    @Excel(name = "取整方式", readConverterExp = "损=耗")
    private String roundingType;

    /** 计价量 */
    @Excel(name = "计价量")
    private Long priceQuantity;

    /** 供方编码（物料/商品/服务） */
    @Excel(name = "供方编码", readConverterExp = "物=料/商品/服务")
    private String supplierProductCode;

    /** 供应商编码（默认） */
    @Excel(name = "供应商编码", readConverterExp = "默=认")
    @NotBlank(message = "供应商编码不能为空")
    private String vendorSid;

    /** 幅宽（厘米） */
    @Excel(name = "幅宽", readConverterExp = "厘=米")
    private String width;

    /** 克重 */
    @Excel(name = "克重")
    private String gramWeight;

    /** 纱支 */
    @Excel(name = "纱支")
    private String yarnCount;

    /** 密度 */
    @Excel(name = "密度")
    private String density;

    /** 成分 */
    @Excel(name = "成分")
    private String composition;

    /** 规格 */
    @Excel(name = "规格")
    private String specification;

    /** 材质 */
    @Excel(name = "材质")
    private String materialComposition;

    /** 内部量备注 */
    @Excel(name = "内部量备注")
    private String remarkInnerQuantity;

    /** 报价量备注 */
    @Excel(name = "报价量备注")
    private String remarkQuoteQuantity;

    /** 核价量备注 */
    @Excel(name = "核价量备注")
    private String remarkCheckQuantity;

    /** 确认价量备注 */
    @Excel(name = "确认价量备注")
    private String remarkConfirmQuantity;

    /** 备注 */
    @Excel(name = "备注")
    private String remark;

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
}
