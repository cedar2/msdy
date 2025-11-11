package com.platform.ems.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * 生产日进度报表 查看详情 行转列 ManDayProgressDetailTableItem
 * 每一行数据
 * @author chenkaiwen
 * @date 2022-11-07
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ManDayProgressDetailTableItem {

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "完工明细的SKU1sid")
    private Long sku1Sid;

    @ApiModelProperty(value = "完工明细的SKU1名称")
    private String sku1Name;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "完工明细的SKU2sid")
    private Long sku2Sid;

    @ApiModelProperty(value = "完工明细的SKU2名称")
    private String sku2Name;

    @ApiModelProperty(value = "小计")
    private BigDecimal totalQuantity;

    @ApiModelProperty(value = "计划产量(商品)")
    private BigDecimal planQuantity;

    @ApiModelProperty(value = "未完成量=计划产量(商品)–小计")
    private BigDecimal weiQuantity;

    @ApiModelProperty(value = "完成量")
    private List<ManDayProgressDetailTableQuantity> quantityList;

    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;

}
