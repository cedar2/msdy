package com.platform.ems.domain.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author olive
 */
@Data
@ApiModel
public class GetInfoProductSizeResponse implements Serializable {
    @ApiModelProperty(value = "尺寸表id")
    private String materialSizeSid;
    @ApiModelProperty(value = "物料档案id")
    private String materialSid;
    @ApiModelProperty(value = "图片地址")
    private String picturePath;
    @ApiModelProperty(value = "备注")
    private String remark;
    @ApiModelProperty(value = "状态")
    private String status;
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;
    @ApiModelProperty(value = "创建人")
    private String creatorAccount;
    @ApiModelProperty(value = "创建时间")
    private String createDate;
    @ApiModelProperty(value = "更新人")
    private String updaterAccount;
    @ApiModelProperty(value = "更新时间")
    private String updateDate;
    @ApiModelProperty(value = "确认人")
    private String confirmerAccount;
    @ApiModelProperty(value = "确认时间")
    private String confirmDate;
    @ApiModelProperty(value = "商品部位详情列表")
    private List<GetInfoProductPosInfoResponse> productPosInfoResponses;
}
