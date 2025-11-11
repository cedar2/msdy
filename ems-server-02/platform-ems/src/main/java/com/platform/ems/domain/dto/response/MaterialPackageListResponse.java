package com.platform.ems.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.platform.common.annotation.Excel;
import lombok.Data;

import java.util.Date;
@Data
public class MaterialPackageListResponse {
    /** 系统ID-常规辅料包档案 */
    @Excel(name = "系统ID-常规辅料包档案")
    private Long materialPackageSid;

    /** 辅料包编码 */
    @Excel(name = "辅料包编码")
    private String packageCode;

    /** 辅料包名称 */
    @Excel(name = "辅料包名称")
    private String packageName;

    /** 产品季编码 */
    @Excel(name = "产品季编码")
    private Long productSeasonSid;

    /** 品牌编码 */
    @Excel(name = "品牌编码")
    private String brand;

    /** 客户编码 */
    @Excel(name = "客户编码")
    private Long customerSid;

    /** 客方品牌编码 */
    @Excel(name = "客方品牌编码")
    private String customerBrand;

    /** 启用/停用状态 */
    @Excel(name = "启用/停用状态")
    private String status;

    /** 处理状态 */
    @Excel(name = "处理状态")
    private String handleStatus;

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

}
