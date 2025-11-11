package com.platform.ems.domain.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 采购订单-附件对象
 *
 * @author linhongwei
 * @date 2021-03-03
 */
@Data
@ApiModel
public class PurPurchaseOrderAttachmentAddRequest {

    @ApiModelProperty(value = "系统自增长ID-采购订单")
    private String purchaseOrderSid;

    @ApiModelProperty(value = "附件类型编码")
    private String fileType;

    @ApiModelProperty(value = "附件名称")
    private String fileName;

    @ApiModelProperty(value = "附件路径")
    private String filePath;
}
