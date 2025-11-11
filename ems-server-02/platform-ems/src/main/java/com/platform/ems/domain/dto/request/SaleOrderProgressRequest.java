package com.platform.ems.domain.dto.request;

import com.baomidou.mybatisplus.annotation.TableField;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 销售订单统计报表 请求实体
 *
 */
@Data
@ApiModel
@Accessors(chain = true)
public class SaleOrderProgressRequest {

    @Excel(name = "物料/商品编码")
    @ApiModelProperty(value = "物料/商品编码")
    private String materialCode;

    @ApiModelProperty(value = "合同交期起")
    private String contractDateBeginTime;

    @ApiModelProperty(value = "合同交期至")
    private String contractDateEndTime;

    @ApiModelProperty(value = "合同交期")
    private String contractDateQuery;

    @ApiModelProperty(value ="每页个数")
    @TableField(exist = false)
    private Integer pageNum;

    @ApiModelProperty(value ="每页个数")
    @TableField(exist = false)
    private Integer pageSize;

    @ApiModelProperty(value = "销售员")
    private String[] salePersonList;
}
