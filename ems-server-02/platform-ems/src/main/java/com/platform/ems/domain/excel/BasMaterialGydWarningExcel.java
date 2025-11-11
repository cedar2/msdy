package com.platform.ems.domain.excel;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author chenkw
 */
@Data
public class BasMaterialGydWarningExcel {

    @ApiModelProperty(value ="商品编码")
    private String materialCode;
    @ApiModelProperty(value ="商品名称")
    private String materialName;
    @ApiModelProperty(value ="客户")
    private String customerName;
    @ApiModelProperty(value ="我司样衣号")
    private String sampleCodeSelf;
    @ApiModelProperty(value ="设计师")
    private String designerAccountName;
    @ApiModelProperty(value ="版型编码")
    private String modelCode;
    @ApiModelProperty(value ="创建人")
    private String creatorAccountName;

}
