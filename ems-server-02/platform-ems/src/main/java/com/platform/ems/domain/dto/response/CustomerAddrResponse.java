package com.platform.ems.domain.dto.response;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 新增联系方式返回的数据,带sid的客户联系方式
 *
 * @author chr
 * @date 2021-01-31
 */
@Data
@Accessors( chain = true)
public class CustomerAddrResponse {

    /** "系统ID-客户联系信息" */
    private Long customerContactSid;

    /** "系统ID-客户档案" */
    private Long customerSid;

    /** "姓名" */
    private String contacterName;

    /** "职务" */
    private String contacterPosition;

    /** "移动电话" */
    private String contacterMobphone;

    /** "所属业务部门" */
    private String contacterDepartment;

    /** "固定电话" */
    private String contacterTelephone;

    /** "传真" */
    private String contacterFax;

    /** "电子邮箱" */
    private String contacterEmail;

    /** "归属业务类型编码" */
    private String businessType;

    /** "联系地址" */
    private String contacterAddress;

    /** "备注" */
    private String remark;

    /** "创建人账号" */
    private String creatorAccount;

    /** "创建时间" */
    private String createDate;

    /** "更新人账号" */
    private String updaterAccount;

    /** "更新时间" */
    private String updateDate;
}