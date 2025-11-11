package com.platform.system.domain.dto.request;

import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class MenuRequest {


    @Excel(name = "角色名称")
    @ApiModelProperty(value = "角色名称")
    private String[] roleNameList;

    @Excel(name = "角色类型")
    @ApiModelProperty(value = "角色类型")
    private String[] roleTypeList;

    @Excel(name = "菜单状态")
    @ApiModelProperty(value = "菜单状态")
    private String[] menuStatusList;

    @Excel(name = "菜单类型")
    @ApiModelProperty(value = "菜单类型")
    private String[] menuTypeList;

    @Excel(name = "角色状态")
    @ApiModelProperty(value = "角色状态")
    private String roleStatus;

    private String roleName;

    @Excel(name = "目录")
    @ApiModelProperty(value = "目录")
    private String catalogue;

    @Excel(name = "菜单名称")
    @ApiModelProperty(value = "菜单名称")
    private String menuName;

    @Excel(name = "按钮")
    @ApiModelProperty(value = "按钮")
    private String button;

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

    @Excel(name = "按钮状态",readConverterExp="0=正常,1=停用")
    @ApiModelProperty(value = "按钮状态")
    private String buttonStatus;
}

