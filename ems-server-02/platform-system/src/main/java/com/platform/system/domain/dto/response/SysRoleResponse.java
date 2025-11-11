package com.platform.system.domain.dto.response;

import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class SysRoleResponse {

    @Excel(name = "用户账号")
    @ApiModelProperty(value = "用户账号")
    private String userName;

    private String userId;

    @Excel(name = "用户昵称")
    @ApiModelProperty(value = "用户昵称")
    private String nickName;

    @Excel(name = "账号状态",readConverterExp="0=正常,1=停用")
    @ApiModelProperty(value = "帐号状态")
    private String status;

    @Excel(name = "用户类型", dictType = "s_user_type")
    private String userType;

    @Excel(name = "角色名称")
    @ApiModelProperty(value = "角色名称")
    private String roleName;

    @Excel(name = "角色权限标识字符")
    @ApiModelProperty(value = "角色权限标识字符")
    private String roleKey;

    @Excel(name = "角色状态",readConverterExp="0=正常,1=停用")
    @ApiModelProperty(value = "角色状态")
    private String roleStatus;


    @Excel(name = "角色类型",dictType ="s_role_type")
    @ApiModelProperty(value = "角色类型")
    private String roleType;

    private String roleId;

    @Excel(name = "账号类型",dictType ="s_user_account_type")
    @ApiModelProperty(value = "账号类型")
    private String accountType;

    @Excel(name = "员工")
    @ApiModelProperty(value = "员工")
    private String staffName;

    @Excel(name = "供应商")
    @ApiModelProperty(value = "供应商")
    private String vendorName;

    @Excel(name = "客户")
    @ApiModelProperty(value = "客户")
    private String customerName;

    @Excel(name = "租户ID")
    @ApiModelProperty(value = "租户ID")
    private String clientId;
}
