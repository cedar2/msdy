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
 * 商品计件工资统计报表
 *
 * @author chenkw
 * @date 2023-03-30
 */
@Data
@Accessors(chain = true)
@ApiModel
public class ProductProcessCompleteStatisticsSalary {

    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    @ApiModelProperty(value = "所属年月(起)")
    private String yearmonthBegin;

    @ApiModelProperty(value = "所属年月(至)")
    private String yearmonthEnd;

    @Excel(name = "时间段(起-至)")
    @ApiModelProperty(value = "时间段(起-至)")
    private String intervalYearmonth;

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

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-商品信息")
    private Long productSid;

    @Excel(name = "商品编码(款号)")
    @ApiModelProperty(value = "商品编码(款号)")
    private String productCode;

    @ApiModelProperty(value = "商品名称")
    private String productName;

    @Excel(name = "排产批次号")
    @ApiModelProperty(value = "排产批次号")
    private String paichanBatch;

    @ApiModelProperty(value = "操作部门编码")
    private String department;

    @ApiModelProperty(value = "操作部门编码 多选")
    private String[] departmentList;

    @Excel(name = "操作部门")
    @ApiModelProperty(value = "操作部门名称")
    private String departmentName;

    @Excel(name = "商品工价类型", dictType = "s_product_price_type")
    @ApiModelProperty(value = "商品工价类型")
    private String productPriceType;

    @Excel(name = "计薪完工类型", dictType = "s_jixin_wangong_type")
    @ApiModelProperty(value = "计薪完工类型")
    private String jixinWangongType;

    @Excel(name = "金额小计(元)", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "金额小计(元)")
    private BigDecimal amountTotal;

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
    @ApiModelProperty(value = "系统自增长ID-产品季信息")
    private Long productSeasonSid;

    @ApiModelProperty(value = "系统自增长ID-产品季信息 多选")
    private Long[] productSeasonSidList;

    @ApiModelProperty(value = "产品季编码")
    private String productSeasonCode;

    @Excel(name = "产品季")
    @ApiModelProperty(value = "产品季")
    private String productSeasonName;

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
