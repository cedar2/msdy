package com.platform.ems.domain.dto.request;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 交期状况报表 请求实体
 *
 */
@Data
@ApiModel
@Accessors(chain = true)
public class OrderProgressRequest {

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

    @ApiModelProperty(value = "供应商")
    private String[] vendorSidList;

    @ApiModelProperty(value = "下单季")
    private String[] productSeasonSidList;

    @ApiModelProperty(value = "供应商")
    private String vendorSid;

    @ApiModelProperty(value = "客户")
    private String customerSid;

    @ApiModelProperty(value = "产品季")
    private String productSeasonSid;

    @ApiModelProperty(value = "供应商")
    private String[] customerSidList;
}
