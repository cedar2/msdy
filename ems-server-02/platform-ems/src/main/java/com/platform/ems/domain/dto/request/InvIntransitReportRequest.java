package com.platform.ems.domain.dto.request;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 在途库存报表请求实体
 *
 * @author yangqz
 * @date 2021-7-22
 */
@Data
@ApiModel
@Accessors(chain = true)
public class InvIntransitReportRequest extends EmsBaseEntity implements Serializable {

    @ApiModelProperty(value = "调拨单号")
    private Long inventoryTransferCode;

    @ApiModelProperty(value = "查询：库位")
    private Long[] storehouseLocationSidList;

    @ApiModelProperty(value = "查询：仓库")
    private Long storehouseSid;

    @ApiModelProperty(value = "查询：仓库")
    private Long[] storehouseSidList;

    @ApiModelProperty(value = "商品编码")
    private String materialCode;

    @ApiModelProperty(value = "商品名称")
    private String materialName;

    @ApiModelProperty(value = "查询：物料分类")
    private Long[] materialClassSidList;

    @ApiModelProperty(value = "查询：物料类型")
    private String[] materialTypeList;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "库存凭证号")
    private Long inventoryDocumentCode;
}
