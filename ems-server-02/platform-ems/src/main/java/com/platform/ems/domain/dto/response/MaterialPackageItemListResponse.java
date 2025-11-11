package com.platform.ems.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.platform.common.annotation.Excel;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class MaterialPackageItemListResponse {
    /**
     * 系统ID-常规辅料包明细
     */
    @Excel(name = "系统ID-常规辅料包明细")
    private Long materialPackItemSid;

    /**
     * 系统ID-常规辅料包档案
     */
    @Excel(name = "系统ID-常规辅料包档案")
    private Long materialPackageSid;

    /**
     * 系统ID-物料档案
     */
    @Excel(name = "系统ID-物料档案")
    private Long materialSid;

    /**
     * SKU1编码
     */
    @Excel(name = "SKU1编码")
    private String sku1Sid;

    /**
     * SKU2编码
     */
    @Excel(name = "SKU2编码")
    private String sku2Sid;

    /**
     * 用量
     */
    @Excel(name = "用量")
    private BigDecimal quantity;

    /**
     * 计量单位编码
     */
    @Excel(name = "计量单位编码")
    private String unit;

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
}
