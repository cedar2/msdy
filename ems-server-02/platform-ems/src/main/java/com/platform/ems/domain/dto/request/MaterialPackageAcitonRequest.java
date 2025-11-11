package com.platform.ems.domain.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 常规辅料包新建状态信息
 *
 * @author yangqize
 *
 */
@Data
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MaterialPackageAcitonRequest {

    @ApiModelProperty(value = "常规辅料包表id")
    private Long[] materialPackageSid;

    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @ApiModelProperty(value = "启用/停用")
    private String status;

    @ApiModelProperty(value = "停用说明")
    private String disableRemark;
}
