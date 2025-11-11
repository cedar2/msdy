package com.platform.ems.domain.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 出入库新增明细查询数据
 */
@Data
@Accessors( chain = true)
public class DocumentAddItemRequest {

    @ApiModelProperty(value = "业务单号")
    private  String code;

    @ApiModelProperty(value = "关联单据类别")
    private  String referDocCategory;

}
