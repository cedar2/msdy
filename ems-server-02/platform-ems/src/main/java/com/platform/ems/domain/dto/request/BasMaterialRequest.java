package com.platform.ems.domain.dto.request;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.ems.domain.TecBomItemReport;
import com.platform.common.core.domain.EmsBaseEntity;
import com.platform.ems.domain.dto.response.BasMaterialImResponse;
import com.platform.ems.domain.dto.response.TecBomZipperItemReport;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 物料需求测算报表
 *
 * @author linhongwei
 * @date 2021-03-12
 */
@Data
@ApiModel
public class BasMaterialRequest implements Serializable {

    @ApiModelProperty(value = "商品明细")
    private List<BasMaterialImResponse> materialList;

    @ApiModelProperty(value = "物料需求报表按款色")
    private List<TecBomItemReport>  TecBomZipperList;

    @ApiModelProperty(value = "物料需求报表按款色码")
    private List<TecBomZipperItemReport>  TecBomZipperSkuList;
}
