package com.platform.ems.domain.dto.request;

import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 尺码拉链对象请求实体
 *
 * @author yangqz
 * @date 2021-7-13
 */
@Data
@ApiModel
@Accessors(chain = true)
public class TecProductZipperRequest extends EmsBaseEntity implements Serializable {

    @ApiModelProperty(value = "商品code")
    private String bomMaterialCode;

    @ApiModelProperty(value = "所选物料sid")
    private List<Long> materialSids;
}
