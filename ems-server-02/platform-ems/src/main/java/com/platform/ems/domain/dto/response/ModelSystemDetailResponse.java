package com.platform.ems.domain.dto.response;


import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 版型档案详情响应
 * @author cwp
 * @date 2021-03-16
 */
@Data
@ApiModel
@Accessors( chain = true)
public class ModelSystemDetailResponse {
    @ApiModelProperty(value = "序号")
    private String serialNum;

    @ApiModelProperty(value = "版型部位ID")
    private String modelPositionSid;

    @ApiModelProperty(value = "版型部位编码")
    private String modelPositionCode;

    @ApiModelProperty(value = "版型部位名称")
    private String modelPositionName;

    @ApiModelProperty(value = "度量方法说明")
    private String measureDescription;

    @ApiModelProperty(value = "公差+")
    private String deviation;

    @ApiModelProperty(value = "公差-")
    private String deviationMinus;

    @ApiModelProperty(value = "尺码详情列表")
    @TableField(exist = false)
    private List<ModelSystemSkuDetailResponse> skuDetailList;


}
