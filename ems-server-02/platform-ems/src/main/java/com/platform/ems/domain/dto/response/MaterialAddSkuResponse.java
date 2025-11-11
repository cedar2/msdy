package com.platform.ems.domain.dto.response;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 按款添加对应的sku
 */
@Data
@Accessors(chain = true)
@ApiModel
public class MaterialAddSkuResponse {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long barCodeSid;

    @JsonSerialize(using = ToStringSerializer.class)
    private String skuName;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long skuSid;

    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal quantity;

    @TableField(exist = false)
    private String firstSort;

    @TableField(exist = false)
    private String secondSort;

    @TableField(exist = false)
    private String thirdSort;
}
