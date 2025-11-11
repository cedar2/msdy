package com.platform.ems.domain.dto.response.form;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 商品生产统计报表
 *
 * @author chenkw
 * @date 2023-03-31
 */
@Data
@Accessors(chain = true)
@ApiModel
public class ManManufactureOrderProductStatistics {

    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-客户信息")
    private Long customerSid;

    @ApiModelProperty(value = "系统自增长ID-客户信息 多选")
    private Long[] customerSidList;

    @ApiModelProperty(value = "客户编码")
    private String customerCode;

    @ApiModelProperty(value = "客户名称")
    private String customerName;

    @Excel(name = "客户")
    @ApiModelProperty(value = "客户简称")
    private String customerShortName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-销售合同")
    private Long saleContractSid;

    @Excel(name = "销售合同号")
    @ApiModelProperty(value = "销售合同号")
    private String saleContractCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-商品信息")
    private Long materialSid;

    @Excel(name = "商品编码(款号)")
    @ApiModelProperty(value = "商品编码(款号)")
    private String materialCode;

    @ApiModelProperty(value = "商品名称")
    private String materialName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-款颜色")
    private Long sku1Sid;

    @ApiModelProperty(value = "款颜色编码")
    private String sku1Code;

    @Excel(name = "款颜色")
    @ApiModelProperty(value = "款颜色名称")
    private String sku1Name;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-工厂信息")
    private Long plantSid;

    @ApiModelProperty(value = "系统自增长ID-工厂信息 多选")
    private Long[] plantSidList;

    @ApiModelProperty(value = "工厂编码")
    private String plantCode;

    @ApiModelProperty(value = "工厂名称")
    private String plantName;

    @Excel(name = "工厂")
    @ApiModelProperty(value = "工厂简称")
    private String plantShortName;

    @Excel(name = "实裁数", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "实裁数")
    private BigDecimal isCaichuangQuantity;

    @Excel(name = "送洗水数", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "实裁数")
    private BigDecimal songXishuiQuantity;

    @Excel(name = "收洗水数", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "实裁数")
    private BigDecimal shouXishuiQuantity;

    @Excel(name = "生产出货数", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "生产出货数")
    private BigDecimal shengchanQuantity;

    @Excel(name = "车缝(完成数)", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "车缝(完成数)")
    private BigDecimal chefengComplete;

    @Excel(name = "后整(完成数)", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "后整(完成数)")
    private BigDecimal houzhengComplete;

    @Excel(name = "车缝(欠数)", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "车缝(欠数)")
    private BigDecimal chefengDebt;

    @Excel(name = "后整(欠数)", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "后整(欠数)")
    private BigDecimal houzhengDebt;

    @ApiModelProperty(value = "计划完工日期启")
    private String planEndDateBegin;

    @ApiModelProperty(value = "计划完工日期止")
    private String planEndDateEnd;

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
