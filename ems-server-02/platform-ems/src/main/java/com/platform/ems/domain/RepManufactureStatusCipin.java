package com.platform.ems.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 生产状况-次品对象 s_rep_manufacture_status_cipin
 *
 * @author c
 * @date 2022-03-17
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_rep_manufacture_status_cipin")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RepManufactureStatusCipin extends EmsBaseEntity {

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
     * 次品类别：生产次品
     */
    @Excel(name = "次品类别：生产次品")
    @ApiModelProperty(value = "次品类别：生产次品")
    private String repairType;

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
     * SKU1 sid
     */
    @Excel(name = "SKU1 sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "SKU1 sid")
    private Long sku1Sid;

    /**
     * SKU1 code
     */
    @Excel(name = "SKU1 code")
    @ApiModelProperty(value = "SKU1 code")
    private String sku1Code;

    /**
     * SKU2 sid
     */
    @Excel(name = "SKU2 sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "SKU2 sid")
    private Long sku2Sid;

    /**
     * SKU2 code
     */
    @Excel(name = "SKU2 code")
    @ApiModelProperty(value = "SKU2 code")
    private String sku2Code;

    /**
     * 次品量
     */
    @Excel(name = "次品量")
    @ApiModelProperty(value = "次品量")
    private BigDecimal quantityCipin;

    /**
     * 客户sid
     */
    @Excel(name = "客户sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "客户sid")
    private Long customerSid;

    /**
     * 客户编码
     */
    @Excel(name = "客户编码")
    @ApiModelProperty(value = "客户编码")
    private String customerCode;

    /**
     * 客户简称
     */
    @Excel(name = "客户简称")
    @ApiModelProperty(value = "客户简称")
    private String customerShortName;

    /**
     * 生产订单sid
     */
    @Excel(name = "生产订单sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "生产订单sid")
    private Long manufactureOrderSid;

    /**
     * 生产订单号
     */
    @Excel(name = "生产订单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "生产订单号")
    private Long manufactureOrderCode;

    /**
     * 生产订单的业务类型
     */
    @Excel(name = "生产订单的业务类型")
    @ApiModelProperty(value = "生产订单的业务类型")
    private String moBusinessType;

    /**
     * 创建日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建日期")
    private Date createDate;

    @ApiModelProperty(value = "处理状态")
    @TableField(exist = false)
    private String handleStatus;

}
