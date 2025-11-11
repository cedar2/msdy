package com.platform.ems.domain.dto.response.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.ems.util.data.KeepTwoDecimalsSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * 生产订单进度报表 --- 销售订单明细
 * SaleManufactureOrderProcessFormResponse
 *
 * @author chenkw
 * @date 2022-06-07
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SaleManufactureOrderProcessFormResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @ApiModelProperty(value = "商品编码")
    private String materialCode;

    @ApiModelProperty(value = "商品名称")
    private String materialName;

    @ApiModelProperty(value = "合同交期")
    private String contractDate;

    @ApiModelProperty(value = "合同交期")
    private String contractDateQuery;

    @ApiModelProperty(value = "警示灯:red,yellow,green")
    private String light;

    @ApiModelProperty(value = "系统自增长ID-生产订单")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long manufactureOrderSid;

    @Excel(name = "生产订单号")
    @ApiModelProperty(value = "生产订单号")
    private String manufactureOrderCode;

    @ApiModelProperty(value = "工厂名称")
    private String plantName;

    @Excel(name = "工厂")
    @ApiModelProperty(value = "工厂简称")
    private String plantShortName;

    @Excel(name = "计划产量", scale = 2)
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "计划产量/订单量")
    private BigDecimal quantity;

    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "待排产量")
    private BigDecimal notQuantity;

    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "已排产量")
    private BigDecimal alreadyQuantity;

    @Excel(name = "已完工量", scale = 2)
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "已完工量")
    private BigDecimal completeQuantity;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "计划开始日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划开始日期")
    private Date planStartDate;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "计划完成日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划完成日期")
    private Date planEndDate;

    @ApiModelProperty(value = "合同交期起")
    private String contractDateBegin;

    @ApiModelProperty(value = "合同交期至")
    private String contractDateEnd;

    @Excel(name = "裁床(计划量)", scale = 2)
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "裁床(计划量)")
    private BigDecimal ccQuantity;

    @Excel(name = "裁床(完工量)", scale = 2)
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "裁床(完工量)")
    private BigDecimal ccCurrentCompleteQuantity;

    @Excel(name = "车缝(计划量)", scale = 2)
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "车缝(计划量)")
    private BigDecimal cfQuantity;

    @Excel(name = "车缝(完工量)", scale = 2)
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "车缝(完工量)")
    private BigDecimal cfCurrentCompleteQuantity;

    @Excel(name = "后整(计划量)", scale = 2)
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "后整(计划量)")
    private BigDecimal hzQuantity;

    @Excel(name = "后整(完工量)", scale = 2)
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "后整(完工量)")
    private BigDecimal hzCurrentCompleteQuantity;

    @Excel(name = "洗水(计划量)", scale = 2)
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "洗水(计划量)")
    private BigDecimal xsQuantity;

    @Excel(name = "洗水(完工量)", scale = 2)
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "洗水(完工量)")
    private BigDecimal xsCurrentCompleteQuantity;

    @Excel(name = "印花(计划量)", scale = 2)
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "印花(计划量)")
    private BigDecimal yhQuantity;

    @Excel(name = "印花(完工量)", scale = 2)
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "印花(完工量)")
    private BigDecimal yhCurrentCompleteQuantity;

    @Excel(name = "绣花(计划量)", scale = 2)
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "绣花(计划量)")
    private BigDecimal xhQuantity;

    @Excel(name = "绣花(完工量)", scale = 2)
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "绣花(完工量)")
    private BigDecimal xhCurrentCompleteQuantity;

    @ApiModelProperty(value ="每页个数")
    private Integer pageNum;

    @ApiModelProperty(value ="每页个数")
    private Integer pageSize;

    @ApiModelProperty(value ="数据权限过滤参数")
    private Map<String, Object> params;

    @ApiModelProperty(value = "数据源系统（数据字典的键值）")
    private String dataSourceSys;
}
