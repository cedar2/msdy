package com.platform.ems.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author :wance
 * @date : 2023/12/14 16:13
 */
@Data
public class PrjProjectQuery implements Serializable {

    @ApiModelProperty(value = "区域")
    private String saleRegionCode;

    @ApiModelProperty(value = "门店编码")
    private String storeCode;

    @ApiModelProperty(value = "门店名称")
    private String storeName;

    @ApiModelProperty(value = "项目跟进人")
    private String projectLeaderCode;

    @ApiModelProperty(value = "任务处理人")
    private String taskHandler;

    @ApiModelProperty(value = "所属业务")
    private List<String> taskBusiness;

    @ApiModelProperty(value = "事项处理人")
    private String matterHandler;

    @ApiModelProperty(value = "事项所属业务")
    private List<String> matterBusiness;

    @ApiModelProperty(value = "项目类型")
    private List<String> projectTypes;

    @ApiModelProperty(value = "项目状态")
    private List<String> projectStatusList;

    @ApiModelProperty(value = "类型")
    private String type;

    @ApiModelProperty(value = "年度")
    private List<String> year;

    @ApiModelProperty(value = "项目跟进人")
    private List<String> userName;

    @ApiModelProperty(value = "计划完成开始日期")
    private String planEndStartDate;

    @ApiModelProperty(value = "计划完成结束日期")
    private String planEndEndDate;

    @ApiModelProperty(value = "实际完成开始日期")
    private String actualEndStartDate;

    @ApiModelProperty(value = "实际完成结束日期")
    private String actualEndEndDate;

    private Integer pageNum;
    private Integer pageSize;

}

