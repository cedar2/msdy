package com.platform.ems.domain.dto.response;

import com.baomidou.mybatisplus.annotation.TableField;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 销售订单明细汇总 尺码
 *
 * @author yangqz
 * @date 2021-7-12
 */
@Data
@ApiModel
@Accessors(chain = true)
public class SalSalesOrderTotalSku2Response {

    @ApiModelProperty(value = "物料编码")
    private String materialCode;

    @Excel(name = "sku2(颜色)")
    @ApiModelProperty(value = "sku2名称")
    @TableField(exist = false)
    private String sku2Name;

    @Excel(name = "数量")
    @ApiModelProperty(value = "数量")
    @TableField(exist = false)
    private BigDecimal sku2Quantity;
}
