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
 * @description: bom尺码拉链长度 新增时校验
 * @author: yang
 * @date: 2021-07-20
 */
@Data
@ApiModel
@Accessors(chain = true)
public class TecBomSizeSkuInsertRequest extends EmsBaseEntity implements Serializable {

    @ApiModelProperty(value = "系统ID-物料BOM组件明细")
    private Long bomItemSid;

    @ApiModelProperty(value = "物料sid")
    private Long materialSid;
}
