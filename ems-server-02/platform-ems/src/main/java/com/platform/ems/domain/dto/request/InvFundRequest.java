package com.platform.ems.domain.dto.request;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 按款选料 响应实体
 *
 * @author yangqz
 * @date 2021-7-13
 */
@Data
@ApiModel
@Accessors(chain = true)
public class InvFundRequest {

    @ApiModelProperty(value ="商品编码（款号）")
    private String materialCodeK;

    @ApiModelProperty(value ="物料名称")
    private String materialName;

    @ApiModelProperty(value ="物料编码")
    private String materialCode;

    @ApiModelProperty(value = "查询：采购类型")
    private String[] purchaseTypeList;

}
