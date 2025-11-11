package com.platform.system.domain.dto.response;

import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class MenuResponse {

    @Excel(name = "角色名称")
    @ApiModelProperty(value = "角色名称")
    private String roleName;

    @Excel(name = "角色权限标识字符")
    @ApiModelProperty(value = "角色权限标识字符")
    private String roleKey;

    @Excel(name = "角色类型",dictType ="s_role_type")
    @ApiModelProperty(value = "角色类型")
    private String roleType;

    @Excel(name = "角色状态",readConverterExp="0=正常,1=停用")
    @ApiModelProperty(value = "角色状态")
    private String roleStatus;

    private String roleId;

    @Excel(name = "目录名称")
    @ApiModelProperty(value = "目录")
    private String catalogue;

    @Excel(name = "菜单名称")
    @ApiModelProperty(value = "菜单名称")
    private String menuName;

    @Excel(name = "菜单状态",readConverterExp="0=正常,1=停用")
    @ApiModelProperty(value = "菜单状态")
    private String menuStatus;

    @Excel(name = "按钮名称")
    @ApiModelProperty(value = "按钮")
    private String button;

    @Excel(name = "按钮状态",readConverterExp="0=正常,1=停用")
    @ApiModelProperty(value = "按钮状态")
    private String buttonStatus;

    @Excel(name = "租户ID")
    @ApiModelProperty(value = "租户ID")
    private String clientId;
}

