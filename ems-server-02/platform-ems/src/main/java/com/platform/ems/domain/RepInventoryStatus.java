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
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 库存状况对象 s_rep_inventory_status
 *
 * @author linhongwei
 * @date 2022-02-25
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_rep_inventory_status")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RepInventoryStatus {

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
     * 物料类型code
     */
    @Excel(name = "物料类型code")
    @ApiModelProperty(value = "物料类型code")
    private String materialTypeCode;

    /**
     * 物料类型名称
     */
    @Excel(name = "物料类型名称")
    @ApiModelProperty(value = "物料类型名称")
    private String materialTypeName;

    /**
     * 物料分类/商品分类code
     */
    @Excel(name = "物料分类/商品分类code")
    @ApiModelProperty(value = "物料分类/商品分类code")
    private String materialClassCode;

    /**
     * 物料分类/商品分类名称
     */
    @Excel(name = "物料分类/商品分类名称")
    @ApiModelProperty(value = "物料分类/商品分类名称")
    private String materialClassName;

    /**
     * 仓库code
     */
    @Excel(name = "仓库code")
    @ApiModelProperty(value = "仓库code")
    private String storehouseCode;

    /**
     * 仓库简称
     */
    @Excel(name = "仓库简称")
    @ApiModelProperty(value = "仓库简称")
    private String storehouseShortName;

    /**
     * 库位code
     */
    @Excel(name = "库位code")
    @ApiModelProperty(value = "库位code")
    private String locationCode;

    /**
     * 库位简称
     */
    @Excel(name = "库位简称")
    @ApiModelProperty(value = "库位简称")
    private String locationShortName;

    /**
     * 库存量
     */
    @Excel(name = "库存量")
    @ApiModelProperty(value = "库存量")
    private BigDecimal quantity;

    /**
     * 库存价值
     */
    @Excel(name = "库存价值")
    @ApiModelProperty(value = "库存价值")
    private BigDecimal moneyAmount;

    /**
     * 币种
     */
    @Excel(name = "币种")
    @ApiModelProperty(value = "币种")
    private String currency;

    /**
     * 货币单位
     */
    @Excel(name = "货币单位")
    @ApiModelProperty(value = "货币单位")
    private String currencyUnit;

    /**
     * 创建日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建日期")
    private Date createDate;

    @ApiModelProperty(value ="创建日期开始时间")
    @TableField(exist = false)
    private String beginTime;

    @ApiModelProperty(value ="创建日期结束时间")
    @TableField(exist = false)
    private String endTime;

    @ApiModelProperty(value ="每页个数")
    @TableField(exist = false)
    private Integer pageNum;

    @ApiModelProperty(value ="每页个数")
    @TableField(exist = false)
    private Integer pageSize;
}
