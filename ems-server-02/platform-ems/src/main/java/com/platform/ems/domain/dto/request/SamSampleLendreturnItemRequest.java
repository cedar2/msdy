package com.platform.ems.domain.dto.request;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.ems.domain.SamSampleLendreturnItem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @description: 借还单 添加样品时获取价格
 * @author: yang
 * @date: 2021-07-20
 */
@Data
@ApiModel
@Accessors(chain = true)
public class SamSampleLendreturnItemRequest {

    @TableField(exist = false)
    List<SamSampleLendreturnItem> listSamSampleLendreturnItem;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "仓库sid")
    private Long storehouseSid;
}
