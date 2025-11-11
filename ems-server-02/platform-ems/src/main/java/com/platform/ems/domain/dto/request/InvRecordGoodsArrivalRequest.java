package com.platform.ems.domain.dto.request;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 采购到货台账请求实体
 *
 * @author yangqz
 *
 */
@Data
@ApiModel
@Accessors(chain = true)
public class InvRecordGoodsArrivalRequest {

    @ApiModelProperty(value ="每页个数")
    @TableField(exist = false)
    private Integer pageNum;

    @ApiModelProperty(value ="每页个数")
    @TableField(exist = false)
    private Integer pageSize;

    @ApiModelProperty(value = "物料编码")
    @TableField(exist = false)
    private String materialCode;

    @ApiModelProperty(value = "物料（商品/服务）名称")
    @TableField(exist = false)
    private String materialName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购到货台账编号")
    private Long goodsArrivalCode;

    @ApiModelProperty(value = "查询：下单季")
    @TableField(exist = false)
    private String[] productSeasonSidList;

    @ApiModelProperty(value = "查询：公司")
    @TableField(exist = false)
    private String[] companySidList;

    @ApiModelProperty(value = "查询：供应商")
    @TableField(exist = false)
    private String[] vendorSidList;

    @ApiModelProperty(value = "查询：采购订单号")
    @TableField(exist = false)
    private String purchaseOrderCode;

    @ApiModelProperty(value = "查询：采购员")
    @TableField(exist = false)
    private String[] buyerList;

    @ApiModelProperty(value = "查询：处理状态")
    @TableField(exist = false)
    private String[] handleStatusList;

    @ApiModelProperty(value = "查询：创建人")
    @TableField(exist = false)
    private String[] creatorAccountList;

    @ApiModelProperty(value = "查询：创建人")
    @TableField(exist = false)
    private String[] createDateStart;

    @ApiModelProperty(value = "创建日期开始时间")
    @TableField(exist = false)
    private String beginTime;

    @ApiModelProperty(value = "创建日期结束时间")
    @TableField(exist = false)
    private String endTime;

    @ApiModelProperty(value = "到货日期(起)")
    @TableField(exist = false)
    private String arrivalDateStart;

    @ApiModelProperty(value = "到货日期(至)")
    @TableField(exist = false)
    private String arrivalDateEnd;

    @ApiModelProperty(value = "检测状态")
    private String[] checkStatusList;

    @ApiModelProperty(value = "检测结果")
    private String[] checkResultList;
}
