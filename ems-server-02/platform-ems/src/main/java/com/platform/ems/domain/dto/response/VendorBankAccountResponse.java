package com.platform.ems.domain.dto.response;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 新增银行账号返回的数据
 *
 * @author chr
 * @date 2021-01-31
 */
@Data
@Accessors( chain = true)
public class VendorBankAccountResponse {

    /** "系统ID-供应商银行账户信息" */
    private String vendorBankAccountSid;

    /** "系统ID-供应商档案" */
    private String vendorSid;

    /** "账户类型编码" */
    private String accountType;

    /** "银行名称" */
    private String bankName;

    /** "分行名称" */
    private String bankBranchName;

    /** "分行编号" */
    private String bankBranchCode;

    /** "分行所属国家编码" */
    private String country;

    /** "分行所属省份编码" */
    private String province;

    /** "分行所属城市编码" */
    private String city;

    /** "银行账号" */
    private String bankAccount;

    /** "收款方名称" */
    private String bankAccountName;

    /** "图片路径" */
    private String picturePath;

    /** "启用/停用状态" */
    private String status;

    /** "处理状态" */
    private String handleStatus;

    /** "创建人账号" */
    private String creatorAccount;

    /** "创建时间" */
    private String createDate;

    /** "更新人账号" */
    private String updaterAccount;

    /** "更新时间" */
    private String updateDate;
}
