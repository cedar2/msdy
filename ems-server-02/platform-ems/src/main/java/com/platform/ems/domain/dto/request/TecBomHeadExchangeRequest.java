package com.platform.ems.domain.dto.request;

import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * bom物料替换
 *
 * @author
 * @date 2021-03-15
 */
@Data
@ApiModel
@Accessors(chain = true)
public class TecBomHeadExchangeRequest {

    @ApiModelProperty(value = "物料编码(旧)sid")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long bomMaterialSidOld;

    @ApiModelProperty(value = "物料编码(旧)code")
    private String bomMaterialCodeOld;

    @ApiModelProperty(value = "物料编码(新)sid")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long bomMaterialSidNew;

    @ApiModelProperty(value = "物料编码(新)code")
    private String bomMaterialCodeNew;

    @ApiModelProperty(value = "物料颜色(旧)sid")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long bomMaterialSku1SidOld;

    @ApiModelProperty(value = "物料颜色(旧)code")
    @JsonSerialize(using = ToStringSerializer.class)
    private String bomMaterialSku1CodeOld;

    @ApiModelProperty(value = "物料颜色(新)sid")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long bomMaterialSku1SidNew;

    @ApiModelProperty(value = "物料颜色(新)code")
    private String bomMaterialSku1CodeNew;

    @ApiModelProperty(value = "说明")
    private String explain;

    @ApiModelProperty(value = "所选明细行Sid")
    @JsonSerialize(using = ToStringSerializer.class)
    private List<TecBomHeadReportExSidRequest> sidList;

    @ApiModelProperty(value = "是否只替换编码")
    private String isExchangeCode;
}
