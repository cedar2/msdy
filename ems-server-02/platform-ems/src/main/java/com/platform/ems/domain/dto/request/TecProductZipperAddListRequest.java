package com.platform.ems.domain.dto.request;

import com.platform.ems.domain.TecProductZipper;
import com.platform.common.core.domain.EmsBaseEntity;
import com.platform.ems.domain.dto.response.TecProductZipperSkuResponse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 尺码拉链对象-新增 请求实体
 *
 * @author yangqz
 * @date 2021-7-13
 */
@Data
@ApiModel
@Accessors(chain = true)
public class TecProductZipperAddListRequest extends EmsBaseEntity implements Serializable {

    @ApiModelProperty(value = "物料数据")
    private List<TecProductZipper> listMaterial;

    @ApiModelProperty(value = "bom尺码信息")
    private List<TecProductZipperSkuResponse> listSku;
}
