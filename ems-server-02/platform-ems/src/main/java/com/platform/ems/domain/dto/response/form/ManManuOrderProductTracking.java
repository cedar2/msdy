package com.platform.ems.domain.dto.response.form;

import com.fasterxml.jackson.annotation.JsonFormat;
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
 * 生产进度跟踪报表（商品）
 *
 * @author chenkw
 * @date 2023-02-22
 */
@Data
@Accessors(chain = true)
@ApiModel
public class ManManuOrderProductTracking {

    @ApiModelProperty(value = "警示灯 0 红灯，-1 不显示")
    private String light;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "合同交期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "合同交期")
    private Date contractDate;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-客户信息")
    private Long customerSid;

    @ApiModelProperty(value = "系统自增长ID-客户信息")
    private Long[] customerSidList;

    @ApiModelProperty(value = "客户名称")
    private String customerName;

    @Excel(name = "客户")
    @ApiModelProperty(value = "客户名称")
    private String customerShortName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-商品&物料&服务")
    private Long materialSid;

    @Excel(name = "商品编码(款号)")
    @ApiModelProperty(value = "物料/商品编码")
    private String materialCode;

    @Excel(name = "商品名称")
    @ApiModelProperty(value = "物料/商品名称")
    private String materialName;

    @Excel(name = "待完工量", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "待完工量(商品) = 已完工量 - 计划产量")
    private BigDecimal daiQuantity;

    @Excel(name = "已完工量", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "生产订单商品明细报表：已完工量(商品)")
    private BigDecimal completeSpQuantity;

    @Excel(name = "计划产量", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "计划产量/本次排产量")
    private BigDecimal quantity;

    @Excel(name = "待出库量",  cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "待出库量")
    private BigDecimal daichukuQuantity;

    @Excel(name = "已出库量",  cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "已出库量")
    private BigDecimal yichukuQuantity;

    @Excel(name = "订单量",  cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "订单量")
    private BigDecimal orderQuantity;

    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    @ApiModelProperty(value = "合同交期起")
    private String contractDateBegin;

    @ApiModelProperty(value = "合同交期止")
    private String contractDateEnd;

    @ApiModelProperty(value = "单据类型编码")
    private String documentType;

    @ApiModelProperty(value = "单据类型编码list")
    private String[] documentTypeList;

    @ApiModelProperty(value = "单据类型")
    private String documentTypeName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-工厂信息")
    private Long plantSid;

    @ApiModelProperty(value = "系统自增长ID-工厂信息")
    private Long[] plantSidList;

    @ApiModelProperty(value = "工厂名称")
    private String plantName;

    @ApiModelProperty(value = "工厂名称")
    private String plantShortName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划完工日期")
    private Date planEndDate;

    @ApiModelProperty(value = "计划完工日期开始")
    private String planEndDateBegin;

    @ApiModelProperty(value = "计划完工日期结束")
    private String planEndDateEnd;

    @ApiModelProperty(value = "完工状态")
    private String completeStatus;

    @ApiModelProperty(value = "完工状态")
    private String[] completeStatusList;

    @ApiModelProperty(value ="每页个数")
    private Integer pageNum;

    @ApiModelProperty(value ="每页个数")
    private Integer pageSize;

    @ApiModelProperty(value ="分页起始数")
    private Integer pageBegin;

    public Integer getPageBegin() {
        if (pageSize != null && pageNum != null){
            return pageSize*(pageNum-1);
        }else {
            return pageBegin;
        }
    }

    public void setPageBegin(Integer pageBegin) {
        if (pageSize != null && pageNum != null){
            this.pageBegin = this.pageSize*(this.pageNum-1);
        }else {
            this.pageBegin = pageBegin;
        }
    }

}
