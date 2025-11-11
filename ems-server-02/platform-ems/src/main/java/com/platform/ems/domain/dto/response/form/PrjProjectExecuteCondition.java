package com.platform.ems.domain.dto.response.form;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 试销站点执行状况报表
 *
 * @author chenkw
 * @date 2023-02-27
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PrjProjectExecuteCondition {

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-项目档案")
    private Long projectSid;

    @Excel(name = "是否存在差异(站点)", dictType = "s_yesno_flag")
    @ApiModelProperty(value = "是否存在差异(站点):Y是黄灯N是空白")
    private String lightSaleStation;

    @Excel(name = "是否存在差异(区域)", dictType = "s_yesno_flag")
    @ApiModelProperty(value = "是否存在差异(区域):Y是黄灯N是空白")
    private String lightMarketRegion;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "开发计划sid")
    private Long developPlanSid;

    @Excel(name = "开发计划号")
    @ApiModelProperty(value = "开发计划号")
    private String developPlanCode;

    @Excel(name = "开发计划名称")
    @ApiModelProperty(value = "开发计划名称")
    private String developPlanName;

    @Excel(name = "开发项目号")
    @ApiModelProperty(value = "项目编号")
    private String projectCode;

    @Excel(name = "销售站点(开发项目)")
    @ApiModelProperty(value = "销售站点(开发项目)")
    private String saleStationName;

    @Excel(name = "市场区域(开发项目)")
    @ApiModelProperty(value = "市场区域(开发项目)")
    private String marketRegionName;

    @Excel(name = "销售站点(试销项目)")
    @ApiModelProperty(value = "销售站点(试销项目)")
    private String preSaleStationName;

    @Excel(name = "市场区域(试销项目)")
    @ApiModelProperty(value = "市场区域(试销项目)")
    private String preMarketRegionName;

    @Excel(name = "年度", dictType = "s_year")
    @ApiModelProperty(value = "年度（数据字典的键值或配置档案的编码）")
    private String year;

    @Excel(name = "组别", dictType = "s_product_group")
    @ApiModelProperty(value = "组别（数据字典的键值或配置档案的编码）")
    private String groupType;

    @Excel(name = "开发项目名称")
    @ApiModelProperty(value = "开发项目名称")
    private String projectName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "商品SKU条码sid")
    private Long materialBarcodeSid;

    @ApiModelProperty(value = "商品SKU条码code")
    private String materialBarcodeCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "样品号Sid")
    private Long sampleSid;

    @ApiModelProperty(value = "ERP系统SKU条码编码")
    private String erpMaterialSkuBarcode;

    @ApiModelProperty(value = "样品号")
    private String sampleCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "商品款号code")
    private Long productSid;

    @ApiModelProperty(value = "商品款号code")
    private String productCode;

    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    @ApiModelProperty(value = "年度（多选）")
    private String[] yearList;

    @ApiModelProperty(value = "组别（多选）")
    private String[] groupTypeList;

    @ApiModelProperty(value = "销售站点(开发项目)")
    private String[] saleStationCodeList;

    @ApiModelProperty(value = "市场区域(开发项目)")
    private String[] marketRegionList;

    @ApiModelProperty(value = "创建人账号（下拉框）")
    private String creatorAccount;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "当前用户对应的员工sid")
    private Long currentUserIsLeaderSid;

    @ApiModelProperty(value = "创建人昵称（输入框）")
    private String creatorAccountName;

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

    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;


}
