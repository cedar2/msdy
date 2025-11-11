package com.platform.system.domain.dto.request;

import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class SysRoleRequest {

    @ApiModelProperty(value = "用户账号")
    private String[] userNameList;

    @Excel(name = "用户昵称")
    @ApiModelProperty(value = "用户昵称")
    private String nickName;

    @ApiModelProperty(value = "用户账号")
    private String userName;

    @ApiModelProperty(value = "角色名称")
    private String roleName;

    @Excel(name = "角色名称")
    @ApiModelProperty(value = "角色名称")
    private String[] roleNameList;


    @Excel(name = "角色类型")
    @ApiModelProperty(value = "角色类型")
    private String[] roleTypeList;

    @Excel(name = "账号类型")
    @ApiModelProperty(value = "账号类型")
    private String[] userTypeList;

    @Excel(name = "员工")
    @ApiModelProperty(value = "员工")
    private String[] staffSidList;

    @Excel(name = "供应商")
    @ApiModelProperty(value = "供应商")
    private String[] vendorSidList;

    @ApiModelProperty(value = "客户")
    private String[] customerSidList;

    @Excel(name = "帐号状态")
    @ApiModelProperty(value = "帐号状态")
    private String status;

    @Excel(name = "角色状态")
    @ApiModelProperty(value = "角色状态")
    private String roleStatus;

    @Excel(name = "角色权限标识字符")
    @ApiModelProperty(value = "角色权限标识字符")
    private String roleKey;

    @Excel(name = "租户ID")
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @ApiModelProperty(value ="每页个数")
    private Integer pageNum;

    @ApiModelProperty(value ="每页个数")
    private Integer pageSize;
}

