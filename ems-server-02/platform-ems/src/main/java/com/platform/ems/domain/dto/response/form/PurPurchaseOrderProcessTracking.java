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
 * 采购入库进度跟踪报表
 *
 * @author chenkw
 * @date 2023-02-08
 */
@Data
@Accessors(chain = true)
@ApiModel
public class PurPurchaseOrderProcessTracking {

    @ApiModelProperty(value = "警示灯 0 红灯，-1 不显示")
    private String light;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "合同交期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "合同交期")
    private Date contractDate;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-供应商信息")
    private Long vendorSid;

    @ApiModelProperty(value = "供应商名称")
    private String vendorName;

    @Excel(name = "供应商")
    @ApiModelProperty(value = "供应商名称")
    private String vendorShortName;

    @ApiModelProperty(value = "采购员")
    private String buyer;

    @Excel(name = "采购员")
    @ApiModelProperty(value = "采购员")
    private String buyerName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-商品&物料&服务")
    private Long materialSid;

    @Excel(name = "物料/商品编码")
    @ApiModelProperty(value = "物料/商品编码")
    private String materialCode;

    @Excel(name = "物料/商品名称")
    @ApiModelProperty(value = "物料/商品名称")
    private String materialName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-SKU1")
    private Long sku1Sid;

    @Excel(name = "SKU1名称")
    @ApiModelProperty(value = "SKU1名称")
    private String sku1Name;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-SKU2")
    private Long sku2Sid;

    @Excel(name = "SKU2名称")
    @ApiModelProperty(value = "SKU2名称")
    private String sku2Name;

    @Excel(name = "订单量",  cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "订单量")
    private BigDecimal quantity;

    @Excel(name = "待入库量",  cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "待入库量")
    private BigDecimal dairukuQuantity;

    @Excel(name = "已入库量",  cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "已入库量")
    private BigDecimal yirukuQuantity;

    @ApiModelProperty(value = "采购单位编码")
    private String unitPrice;

    @Excel(name = "采购单位")
    @ApiModelProperty(value = "采购单位名称")
    private String unitPriceName;

    @ApiModelProperty(value = "物料类型编码")
    private String materialType;

    @Excel(name = "物料类型")
    @ApiModelProperty(value = "物料类型名称")
    private String materialTypeName;

    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    @ApiModelProperty(value = "合同交期起")
    private String contractDateBegin;

    @ApiModelProperty(value = "合同交期止")
    private String contractDateEnd;

    @ApiModelProperty(value = "系统自增长ID-供应商信息 多选")
    private Long[] vendorSidList;

    @ApiModelProperty(value = "采购员 多选")
    private String[] buyerList;

    @ApiModelProperty(value = "物料类型 多选")
    private String[] materialTypeList;

    @ApiModelProperty(value = "处理状态 多选")
    private String[] handleStatusList;

    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @ApiModelProperty(value = "是否采购退货")
    private String isReturnGoods;

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
