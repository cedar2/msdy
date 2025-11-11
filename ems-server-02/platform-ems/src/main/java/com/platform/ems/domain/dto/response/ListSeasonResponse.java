package com.platform.ems.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.platform.common.annotation.Excel;
import lombok.Data;

import java.util.Date;

@Data
public class ListSeasonResponse{

    @Excel(name = "系统ID-产品季档案")
    private String productSeasonSid;

    /** 产品季编码 */
    @Excel(name = "产品季编码")
    private String productSeasonCode;

    /** 产品季名称 */
    @Excel(name = "产品季名称")
    private String productSeasonName;

    /** 年份（年份的编码） */
    @Excel(name = "年份")
    private String year;

    /** 季度（季度的编码） */
    @Excel(name = "季度")
    private String seasonCode;

    /** 公司（公司档案的sid） */
    @Excel(name = "公司")
    private String companySid;

    /** 产品季所属阶段编码 */
    @Excel(name = "产品季所属阶段编码")
    private String productSeasonStage;

    /** 启用/停用状态 */
    @Excel(name = "启用/停用状态")
    private String status;

    /** 处理状态 */
    @Excel(name = "处理状态")
    private String handleStatus;

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

    /** 确认人账号 */
    @Excel(name = "确认人账号")
    private String confirmerAccount;

    /** 确认时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "确认时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date confirmDate;


}
