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
 * 商品领用物料统计报表
 *
 * @author chenkw
 * @date 2023-04-20
 */
@Data
@Accessors(chain = true)
@ApiModel
public class InvInventoryProductUserMaterial {

    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-客户")
    private Long customerSid;

    @ApiModelProperty(value = "系统SID-客户")
    private Long[] customerSidList;

    @ApiModelProperty(value = "客户编码")
    private String customerCode;

    @ApiModelProperty(value = "客户名称")
    private String customerName;

    @Excel(name = "客户")
    @ApiModelProperty(value = "客户简称")
    private String customerShortName;

    @Excel(name = "时间段(起~至)")
    @ApiModelProperty(value = "时间段(起~至)")
    private String accountDateDuring;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品")
    private Long productSid;

    @Excel(name = "商品编码（款号）")
    @ApiModelProperty(value = "商品编码（款号）")
    private String productCodes;

    @ApiModelProperty(value = "商品）")
    private String productName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-颜色")
    private Long sku1Sid;

    @ApiModelProperty(value = "系统SID-颜色")
    private Long[] sku1SidList;

    @ApiModelProperty(value = "SKU类型")
    private String sku1Type;

    @ApiModelProperty(value = "颜色编码")
    private String sku1Code;

    @ApiModelProperty(value = "颜色名称")
    private String sku1Name;

    @Excel(name = "款颜色")
    @ApiModelProperty(value = "款颜色")
    private String productSku1Names;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料")
    private Long materialSid;

    @Excel(name = "物料编码")
    @ApiModelProperty(value = "物料编码")
    private String materialCode;

    @Excel(name = "物料名称")
    @ApiModelProperty(value = "物料")
    private String materialName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料分类")
    private Long materialClassSid;

    @ApiModelProperty(value = "系统SID-物料分类")
    private Long[] materialClassSidList;

    @ApiModelProperty(value = "物料分类编码")
    private String materialClassCode;

    @Excel(name = "物料分类")
    @ApiModelProperty(value = "物料分类")
    private String materialClassName;

    @Excel(name = "领用量", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "领用量")
    private BigDecimal quantity;

    @ApiModelProperty(value = "计量单位")
    private String unitBase;

    @Excel(name = "计量单位")
    @ApiModelProperty(value = "计量单位")
    private String unitBaseName;

    @Excel(name = "含税价格(元)", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "含税价格(元)")
    private BigDecimal priceTax;

    @Excel(name = "含税金额(元)", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "含税金额(元)")
    private BigDecimal currentAmountTax;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-仓库")
    private Long storehouseSid;

    @ApiModelProperty(value = "系统SID-仓库")
    private Long[] storehouseSidList;

    @ApiModelProperty(value = "仓库编码")
    private String storehouseCode;

    @Excel(name = "仓库")
    @ApiModelProperty(value = "仓库")
    private String storehouseName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-公司")
    private Long companySid;

    @ApiModelProperty(value = "系统SID-公司")
    private Long[] companySidList;

    @ApiModelProperty(value = "公司编码")
    private String companyCode;

    @ApiModelProperty(value = "公司")
    private String companyName;

    @Excel(name = "公司")
    @ApiModelProperty(value = "公司")
    private String companyShortName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-产品季")
    private Long productSeasonSid;

    @ApiModelProperty(value = "系统SID-产品季")
    private Long[] productSeasonSidList;

    @ApiModelProperty(value = "产品季编码")
    private String productSeasonCode;

    @Excel(name = "产品季")
    @ApiModelProperty(value = "产品季")
    private String productSeasonName;

    @ApiModelProperty(value = "作业类型")
    private String movementType;

    @ApiModelProperty(value = "作业类型多选")
    private String[] movementTypeList;

    @ApiModelProperty(value = "出库日期起")
    private String accountDateBegin;

    @ApiModelProperty(value = "出库日期至")
    private String accountDateEnd;

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
