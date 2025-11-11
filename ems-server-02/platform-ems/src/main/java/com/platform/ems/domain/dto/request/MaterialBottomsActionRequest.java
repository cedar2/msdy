package com.platform.ems.domain.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 上下装尺码对照状态信息
 *
 * @author yangqize
 *
 */
@Data
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MaterialBottomsActionRequest{
    /** id值 */
    @ApiModelProperty(value = "上下装尺码对照sid")
    private  Long[] bottomsSkuSid;
    /** 处理状态 */
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;
    /** 启用/停用 */
    @ApiModelProperty(value = "状态")
    private String status;
}
