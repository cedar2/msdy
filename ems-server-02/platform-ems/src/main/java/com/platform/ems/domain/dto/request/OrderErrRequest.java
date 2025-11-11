package com.platform.ems.domain.dto.request;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.ems.domain.dto.response.CommonErrMsgResponse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 采购订单提交批量报错
 *
 */
@Data
@ApiModel
public class OrderErrRequest {

    @ApiModelProperty(value = "报错信息")
    private List<CommonErrMsgResponse> msgList;

    @ApiModelProperty(value = "是否跳过-免费行")
    private String isSkipFree;

    @ApiModelProperty(value = "是否跳过-合同金额")
    private String isSkipConstract;

    @ApiModelProperty(value = "是否跳过-甲供料")
    private String isSkipRaw;

    @ApiModelProperty(value = "是否跳过-价格为空")
    private String isPriceNull;

    @ApiModelProperty(value = "是否跳过-条码停用")
    private String isStatus;

    @ApiModelProperty(value = "所选sid")
    private List<Long> sidList;

}
