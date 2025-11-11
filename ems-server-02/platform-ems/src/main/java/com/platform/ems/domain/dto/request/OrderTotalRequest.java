package com.platform.ems.domain.dto.request;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 销售、采购统计报表 请求实体
 *
 */
@Data
@ApiModel
@Accessors(chain = true)
public class OrderTotalRequest {


    @ApiModelProperty(value ="每页个数")
    @TableField(exist = false)
    private Integer pageNum;

    @ApiModelProperty(value ="每页个数")
    @TableField(exist = false)
    private Integer pageSize;

    @ApiModelProperty(value = "供应商")
    private String[] vendorSidList;

    @ApiModelProperty(value = "处理状态")
    private String[] handleStatusList;

    private String productSeasonSid;

    private String vendorSid;

    private String customerSid;

    private String documentType;

    @ApiModelProperty(value = "下单季")
    private String[] productSeasonSidList;

    @ApiModelProperty(value = "客户")
    private String[] customerSidList;

    @ApiModelProperty(value ="下单日期起")
    @TableField(exist = false)
    private String documentBeginTime;

    @ApiModelProperty(value ="下单日期至")
    @TableField(exist = false)
    private String documentEndTime;
}
