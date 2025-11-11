package com.platform.ems.domain.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 库存凭证下拉列表响应实体
 *
 * @author yangqz
 * @date 2021-7-29
 */
@Data
@ApiModel
@Accessors(chain = true)
public class ConInventoryDocumentCategoryResponse implements Serializable {

    @ApiModelProperty(value = "系统SID-库存凭证类别sid")
    private Long sid;

    @ApiModelProperty(value = "库存凭证类别编码")
    private String code;

    @ApiModelProperty(value = "库存凭证类别名称")
    private String name;

    @ApiModelProperty(value = "启用/停用")
    private String status;

    @ApiModelProperty(value = "处理状态")
    private String handleStatus;
}
