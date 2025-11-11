package com.platform.ems.domain.dto.response.form;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 报表中心类目明细报表
 *
 * @author chenkw
 * @date 2023-01-30
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasMaterialSaleStationCategoryForm {

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "大类")
    private Long bigClassSid;

    @Excel(name = "大类")
    @ApiModelProperty(value = "大类")
    private String bigClassName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "中类")
    private Long middleClassSid;

    @Excel(name = "中类")
    @ApiModelProperty(value = "中类")
    private String middleClassName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "小类")
    private Long smallClassSid;

    @Excel(name = "小类")
    @ApiModelProperty(value = "小类")
    private String smallClassName;

    @Excel(name = "组别", dictType = "s_product_group")
    @ApiModelProperty(value = "组别编码")
    private String groupType;

    @ApiModelProperty(value = "组别编码 多选")
    private String[] groupTypeList;

    @Excel(name = "电商平台", dictType = "s_platform_dianshang")
    @ApiModelProperty(value = "电商平台")
    private String platformDianshang;

    @ApiModelProperty(value = "电商平台 多选")
    private String[] platformDianshangList;

    @Excel(name = "所属区域", dictType = "s_sale_station_region")
    @ApiModelProperty(value = "所属区域")
    private String region;

    @ApiModelProperty(value = "所属区域 多选")
    private String[] regionList;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售站点Sid")
    private Long saleStationSid;

    @Excel(name = "销售站点")
    @ApiModelProperty(value = "销售站点/网店名称")
    private String saleStationName;

    @ApiModelProperty(value = "销售站点/网店sid 多选")
    private Long[] saleStationSidList;

    @Excel(name = "SPU合计数", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "SPU合计数")
    private BigDecimal spuCount;

    @Excel(name = "存量SPU数", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "存量SPU数")
    private BigDecimal spuStock;

    @Excel(name = "计划增量SPU数", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "计划增量SPU数")
    private BigDecimal spuPlanInc;

    @Excel(name = "规划SPU数", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "规划SPU数")
    private BigDecimal spuPlan;

    @Excel(name = "弃置SPU数", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "弃置SPU数")
    private BigDecimal spuDiscard;

    @TableField(exist = false)
    @ApiModelProperty(value ="每页个数")
    private Integer pageNum;

    @TableField(exist = false)
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

    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

}
