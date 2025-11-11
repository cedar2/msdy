package com.platform.ems.domain.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author olive
 */
@Data
@ApiModel
public class GetInfoProductPosInfoResponse  {
    @ApiModelProperty(value = "部位信息id")
    private String materialPosInforSid;
    @ApiModelProperty(value = "部位档案id")
    private String modelPositionSid;
    @ApiModelProperty(value = "部位档案名称")
    private String modelPositionName;
    @ApiModelProperty(value = "序号")
    private BigDecimal serialNum;
    @ApiModelProperty(value = "计量单位编码")
    private String unit;
    @ApiModelProperty(value = "公差")
    private BigDecimal deviation;
    @ApiModelProperty(value = "备注")
    private String remark;
    @ApiModelProperty(value = "尺寸信息列表")
    private List<GetInfoProductPosSizeResponse> productPosSizeResponses;
}
