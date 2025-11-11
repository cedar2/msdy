package com.platform.ems.domain.dto.response;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 生产订单-明细汇总
 */
@Data
@Accessors(chain = true)
@ApiModel
public class ItemSummary {

    @ApiModelProperty(value = "SKU2(尺码)")
    private String sku2Name;

    @ApiModelProperty(value = "排产量汇总")
    private BigDecimal quantity;

    @TableField(exist = false)
    private String firstSort;

    @TableField(exist = false)
    private String secondSort;

    @TableField(exist = false)
    private String thirdSort;
}
