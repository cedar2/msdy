package com.platform.ems.domain.dto.request;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 畅销款排行榜
 *
 * @author yangqz
 * @date
 */
@Data
@ApiModel
@Accessors(chain = true)
public class OrderBestSellingRequest {

    @ApiModelProperty(value ="下单季")
    private String  productSeasonSid;

    @ApiModelProperty(value ="物料sid")
    private String materialSid;

    @ApiModelProperty(value ="物料编码")
    private String materialCode;

    @ApiModelProperty(value ="维度")
    private String dimension;

    @ApiModelProperty(value ="下单日期起")
    @TableField(exist = false)
    private String documentBeginTime;

    @ApiModelProperty(value ="下单日期至")
    @TableField(exist = false)
    private String documentEndTime;

    @ApiModelProperty(value ="每页个数")
    @TableField(exist = false)
    private Integer pageNum;

    @ApiModelProperty(value ="每页个数")
    @TableField(exist = false)
    private Integer pageSize;

    @ApiModelProperty(value = "处理状态")
    private String[] handleStatusList;

    @ApiModelProperty(value = "下单季")
    private String[] productSeasonSidList;

    @ApiModelProperty(value = "单据类型")
    private String[] documentTypeList;

}
