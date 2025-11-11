package com.platform.ems.domain.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 生产日计划-明细对象 s_man_Mont_manufacture_plan_item
 *
 * @author c
 * @date 2021-07-16
 */
@Data
@Accessors(chain = true)
@ApiModel
public class ManDayManufacturePlanItemRequest implements Serializable {
    /**
     * 工厂sid
     */
    @ApiModelProperty(value = "工厂sid")
    private Long[] plantSidList;

    /**
     * 工作中心sid
     */
    @ApiModelProperty(value = "工作中心sid")
    private Long[] workCenterSidList;

    /**
     * 生产订单号
     */
    @ApiModelProperty(value = "生产订单号")
    private Long manufactureOrderCode;

    /**
     * 系统自增长ID-工序
     */
    @ApiModelProperty(value = "工序sid")
    private Long[] processSidList;

    /**
     * 商品编码
     */
    @ApiModelProperty(value = "商品编码")
    private String materialCode;

    /**
     * 创建人账号（用户名称）
     */
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String[] creatorAccountList;

    /**
     * 处理状态（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String[] handleStatusList;

    /**
     * 周计划日期(起)
     */
    @ApiModelProperty(value = "周计划日期(起)")
    private Date dateStart;

    /**
     * 周计划日期(至)
     */
    @ApiModelProperty(value = "周计划日期(至)")
    private Date dateEnd;
}
