package com.platform.ems.domain.dto.request;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * 新增,编辑,详情客户联系方式,不带sid的数据
 * @author chr
 * @date 2021-01-31
 */
@Data
@Accessors( chain = true)
public class CustomerAddrRequest {

    /** 姓名 */
    @NotBlank(message = "打 * 字段为必填项，不能为空")
    private String contacterName;

    /** 职务 */
    @NotBlank(message = "打 * 字段为必填项，不能为空")
    private String contacterPosition;

    /** 移动电话 */
    @NotBlank(message = "打 * 字段为必填项，不能为空")
    private String contacterMobphone;

    /** 所属业务部门 */
    private String contacterDepartment;

    /** 固定电话 */
    private String contacterTelephone;

    /** 传真 */
    private String contacterFax;

    /** 电子邮箱 */
    @NotBlank(message = "打 * 字段为必填项，不能为空")
    private String contacterEmail;

    /** 归属业务类型编码 */
    private String businessType;

    /** 联系地址 */
    @NotBlank(message = "打 * 字段为必填项，不能为空")
    private String contacterAddress;

    /** 备注 */
    private String remark;
}
