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
 * 商品外加工费统计报表
 *
 * @author chenkw
 * @date 2023-04-20
 */
@Data
@Accessors(chain = true)
@ApiModel
public class ManOutsourceSettleStatistics {

    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品&服务")
    private Long materialSid;

    @Excel(name = "商品编码(款号)")
    @ApiModelProperty(value = "商品编码")
    private String materialCode;

    @ApiModelProperty(value = "商品名称")
    private String materialName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品sku1")
    private Long sku1Sid;

    @ApiModelProperty(value = "系统CODE-物料&商品sku1")
    private String sku1Code;

    @Excel(name = "颜色")
    @ApiModelProperty(value = "物料&商品sku1")
    private String sku1Name;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-工序")
    private Long processSid;

    @ApiModelProperty(value = "系统SID-工序")
    private Long[] processSidList;

    @ApiModelProperty(value = "系统CODE-工序")
    private String processCode;

    @Excel(name = "工序(外发)")
    @ApiModelProperty(value = "工序名称")
    private String processName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商信息（外发加工商）")
    private Long vendorSid;

    @ApiModelProperty(value = "系统SID-供应商信息（外发加工商）")
    private Long[] vendorSidList;

    @ApiModelProperty(value = "供应商编码")
    private String vendorCode;

    @ApiModelProperty(value = "供应商名称")
    private String vendorName;

    @Excel(name = "供应商")
    @ApiModelProperty(value = "供应商简称")
    private String vendorShortName;

    @Excel(name = "完成量合计", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "完成量")
    private BigDecimal completeQuantity;

    @Excel(name = "合格量合计", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "合格量")
    private BigDecimal settleQuantity;

    @Excel(name = "次品量合计", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "次品量")
    private BigDecimal defectiveQuantity;

    @Excel(name = "次品量合计(允许范围内)", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "次品量(允许范围内)")
    private BigDecimal inDefectiveQuantity;

    @Excel(name = "次品量合计(超出范围)", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "次品量(超出范围)")
    private BigDecimal outDefectiveQuantity;

    @Excel(name = "加工金额合计(含税)", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "加工金额(含税)")
    private BigDecimal processAmountTax;

    @Excel(name = "明细扣款合计", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "扣款小计(含税)")
    private BigDecimal sumDeductionTax;

    @Excel(name = "结算金额合计(含税)", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "结算金额(含税)")
    private BigDecimal settleAmountTax;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-客户信息")
    private Long customerSid;

    @ApiModelProperty(value = "系统SID-客户信息")
    private Long[] customerSidList;

    @ApiModelProperty(value = "客户编码")
    private String customerCode;

    @ApiModelProperty(value = "客户名称")
    private String customerName;

    @Excel(name = "客户(商品档案)")
    @ApiModelProperty(value = "客户(商品档案)")
    private String customerShortName;

    @ApiModelProperty(value = "单据日期 起")
    private String documentDateBegin;

    @ApiModelProperty(value = "单据日期 止")
    private String documentDateEnd;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-工厂")
    private Long plantSid;

    @ApiModelProperty(value = "系统SID-工厂")
    private Long[] plantSidList;

    @ApiModelProperty(value = "工厂编码")
    private String plantCode;

    @ApiModelProperty(value = "工厂名称")
    private String plantName;

    @ApiModelProperty(value = "工厂简称")
    private String plantShortName;

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
