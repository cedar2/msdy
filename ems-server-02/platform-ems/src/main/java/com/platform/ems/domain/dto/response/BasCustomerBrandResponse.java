package com.platform.ems.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.platform.common.annotation.Excel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author Pinzhen Chen
 * @Date 2021/1/27 15:08
 */
@Data
public class BasCustomerBrandResponse implements Serializable {
    /** 系统ID-客户品牌信息 */
    @Excel(name = "系统ID-客户品牌信息")
    private String customerBrandSid;

    /** 系统ID-客户档案 */
    private String customerSid;

    /** 客方品牌编码 */
    private String contactBrandCode;

    /** 客方品牌名称 */
    @Excel(name = "客方品牌名称")
    private String contacterName;

    /**
     * 备注
     */
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
