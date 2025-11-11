package com.platform.ems.domain.dto.response;

import com.platform.ems.domain.TecProductZipper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 商品所用拉链对象
 *
 * @author yangqz
 * @date 2021-7-12
 */
@Data
@ApiModel
@Accessors(chain = true)
public class TecProductZipperInfoResponse implements Serializable {

    @ApiModelProperty(value = "物料数据")
    private List<TecProductZipper> listMaterial;

    @ApiModelProperty(value = "bom尺码信息")
    private List<TecProductZipperSkuResponse> listSku;

}
