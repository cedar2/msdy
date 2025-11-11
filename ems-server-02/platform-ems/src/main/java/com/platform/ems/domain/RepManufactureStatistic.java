package com.platform.ems.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import lombok.experimental.Accessors;

/**
 * 生产统计报对象
 *
 * @author chenkw
 * @date 2022-05-11
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_rep_manufacture_statistic")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RepManufactureStatistic {

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 数据记录sid
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "数据记录sid")
    private Long dataRecordSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] dataRecordSidList;
    /**
     * 工厂sid
     */
    @Excel(name = "工厂sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "工厂sid")
    private Long plantSid;

    /**
     * 工厂code
     */
    @Excel(name = "工厂code")
    @ApiModelProperty(value = "工厂code")
    private String plantCode;

    /**
     * 工厂简称
     */
    @Excel(name = "工厂简称")
    @ApiModelProperty(value = "工厂简称")
    private String plantShortName;

    /**
     * 产品季code
     */
    @Excel(name = "产品季code")
    @ApiModelProperty(value = "产品季code")
    private String productSeasonCode;

    /**
     * 生产订单的业务类型
     */
    @Excel(name = "生产订单的业务类型")
    @ApiModelProperty(value = "生产订单的业务类型")
    private String moBusinessType;

    /**
     * 物料/商品sid
     */
    @Excel(name = "物料/商品sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "物料/商品sid")
    private Long materialSid;

    /**
     * 物料/商品编码
     */
    @Excel(name = "物料/商品编码")
    @ApiModelProperty(value = "物料/商品编码")
    private String materialCode;

    /**
     * 待排产量
     */
    @Excel(name = "待排产量")
    @ApiModelProperty(value = "待排产量")
    private BigDecimal quantityDaipc;

    /**
     * 在产量
     */
    @Excel(name = "在产量")
    @ApiModelProperty(value = "在产量")
    private BigDecimal quantityZaic;

    /**
     * 次品量
     */
    @Excel(name = "次品量")
    @ApiModelProperty(value = "次品量")
    private BigDecimal quantityCip;

    /**
     * 款图片
     */
    @Excel(name = "款图片")
    @ApiModelProperty(value = "款图片")
    private String picturePath;

    /**
     * 创建日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建日期")
    private Date createDate;

    @ApiModelProperty(value = "创建日期开始时间")
    @TableField(exist = false)
    private String beginTime;

    @ApiModelProperty(value = "创建日期结束时间")
    @TableField(exist = false)
    private String endTime;

    @ApiModelProperty(value = "每页个数")
    @TableField(exist = false)
    private Integer pageNum;

    @ApiModelProperty(value = "每页个数")
    @TableField(exist = false)
    private Integer pageSize;

}
