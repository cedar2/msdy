package com.platform.common.core.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 数据角色对象 sys_role_data
 *
 * @author chenkw
 * @date 2023-05-16
 */
@Data
@Accessors(chain = true)
@ApiModel
public class SysRoleDataAuthFieldValue {

    @ApiModelProperty(value = "权限字段名称")
    private String fieldName;

    @ApiModelProperty(value = "权限字段编码")
    private String fieldCode;

    @ApiModelProperty(value = "参数名称")
    private String authorityFieldParam;

    @ApiModelProperty(value = "权限字段值")
    private String authorityFieldValue;

    @ApiModelProperty(value = "权限对象名称")
    private String objectName;

    @ApiModelProperty(value = "权限对象编码")
    private String objectCode;

    @ApiModelProperty(value = "数据角色名称")
    private String roleDataName;

    @ApiModelProperty(value = "数据角色编码")
    private String roleDataCode;

}

