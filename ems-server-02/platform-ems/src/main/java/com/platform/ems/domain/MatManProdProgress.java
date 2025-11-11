package com.platform.ems.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 商品生产进度报表
 *
 * @author wangp
 * @date 2022-10-20
 */
@Data
@Accessors(chain = true)
@ApiModel
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class MatManProdProgress extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-生产进度日报单明细
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-生产进度日报单明细")
    private Long dayManufactureProgressItemSid;

    @ApiModelProperty(value = "sid数组")
    private Long[] dayManufactureProgressItemSidList;

    /**
     * 系统SID-生产订单
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-生产订单")
    private Long manufactureOrderSid;

    /**
     * 系统SID-生产订单-工序
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-生产订单-工序")
    private Long manufactureOrderProcessSid;


    @ApiModelProperty(value = "日期开始")
    private String documentDateStartTime;

    @ApiModelProperty(value = "日期结束")
    private String documentDateEndTime;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "工厂sid")
    private Long plantSid;

    @ApiModelProperty(value = "工厂sid数组")
    private Long[] plantSidList;

    @ApiModelProperty(value = "工厂编码")
    private String plantCode;

    @Excel(name = "工厂(工序)")
    @ApiModelProperty(value = "工厂名称")
    private String plantName;

    @Excel(name = "商品编码")
    @ApiModelProperty(value = "商品编码")
    private String materialCode;

    @Excel(name = "商品名称")
    @ApiModelProperty(value = "商品名称")
    private String materialName;


    @Excel(name = "排产批次号")
    @ApiModelProperty(value = "排产批次号")
    private Long paichanBatch;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-工序")
    private Long processSid;

    @ApiModelProperty(value = "系统自增长ID-工序")
    private Long[] processSidList;

    @Excel(name = "工序名称")
    @ApiModelProperty(value = "工序名称")
    private String processName;

    @ApiModelProperty(value = "sku编码")
    private String skuCode;

    @Excel(name = "颜色")
    @ApiModelProperty(value = "颜色")
    private String skuName;



}
