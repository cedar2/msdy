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
 * 商品销售成本报表
 *
 * @author chenkw
 * @date 2023-03-21
 */
@Data
@Accessors(chain = true)
@ApiModel
public class SalSaleProductCostForm {

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
    @ApiModelProperty(value = "客户名称")
    private String customerShortName;

    @ApiModelProperty(value = "销售合同号(纸质合同)")
    private String paperSaleContractCode;

    @Excel(name = "销售合同号")
    @ApiModelProperty(value = "销售合同号")
    private String saleContractCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-签约季")
    private Long productSeasonSid;

    @ApiModelProperty(value = "系统自增长ID-签约季 多选")
    private Long[] productSeasonSidList;

    @ApiModelProperty(value = "签约季编码")
    private String productSeasonCode;

    @Excel(name = "签约季")
    @ApiModelProperty(value = "签约季")
    private String productSeasonName;

    @ApiModelProperty(value = "业务渠道/销售渠道（数据字典的键值或配置档案的编码）")
    private String businessChannel;

    @ApiModelProperty(value = "销售渠道（数据字典的键值）list")
    private String[] businessChannelList;

    @Excel(name = "销售渠道")
    @ApiModelProperty(value = "销售渠道名称")
    private String businessChannelName;

    @Excel(name = "销售模式", dictType = "s_price_type")
    @ApiModelProperty(value = "销售模式（数据字典）")
    private String saleMode;

    @ApiModelProperty(value = "销售模式（数据字典）")
    private String[] saleModeList;

    @Excel(name = "合同金额", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "合同金额")
    private BigDecimal amountTaxContract;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-商品编码(款号)")
    private Long materialSid;

    @ApiModelProperty(value = "系统自增长ID-商品编码(款号) 多选")
    private Long[] materialSidList;

    @Excel(name = "商品编码(款号)")
    @ApiModelProperty(value = "商品编码(款号)")
    private String materialCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-颜色信息")
    private Long sku1Sid;

    @ApiModelProperty(value = "系统自增长ID-颜色信息 多选")
    private Long[] sku1SidList;

    @ApiModelProperty(value = "颜色编码")
    private String sku1Code;

    @Excel(name = "颜色")
    @ApiModelProperty(value = "颜色")
    private String sku1Name;

    @Excel(name = "订单量", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "订单量")
    private BigDecimal quantity;

    @Excel(name = "出库量", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "出库量")
    private BigDecimal chukuQuantity;

    @Excel(name = "订单金额", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "订单金额")
    private BigDecimal amountTaxOrder;

    @Excel(name = "出库金额", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "出库金额")
    private BigDecimal amountTaxStore;

    /**
     * 查库存量获取逻辑的区间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "过账日期(起)")
    private String invDocumentDateBegin;

    /**
     * 查库存量获取逻辑的区间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "过账日期(至)")
    private String invDocumentDateEnd;

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
