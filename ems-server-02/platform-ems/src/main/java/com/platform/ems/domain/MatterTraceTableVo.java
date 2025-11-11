package com.platform.ems.domain;

import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author :wance
 * @date : 2024/2/1 14:21
 */
@Data
public class MatterTraceTableVo implements Serializable {

    @Excel(name = "预警", dictType = "s_early_warning")
    @ApiModelProperty(value = "预警")
    private String warning;

    @Excel(name = "事项名称")
    @ApiModelProperty(value = "事项名称")
    private String matterName;

    @Excel(name = "事项状态")
    @ApiModelProperty(value = "事项状态")
    private String matterStatus;

    @Excel(name = "项目Sid")
    private String projectSid;

    @Excel(name = "项目编码")
    @ApiModelProperty(value = "项目编码")
    private String projectCode;

    @Excel(name = "门店名称")
    @ApiModelProperty(value = "门店名称")
    private String storeName;

    @Excel(name = "门店编码")
    @ApiModelProperty(value = "门店编码")
    private String storeCode;

    @Excel(name = "项目类型")
    @ApiModelProperty(value = "项目类型")
    private String projectType;

    @Excel(name = "事项处理人")
    @ApiModelProperty(value = "事项处理人")
    private String matterHandler;

    @Excel(name = "事项主管")
    @ApiModelProperty(value = "事项主管")
    private String matterManager;

    @Excel(name = "计划开始时间")
    @ApiModelProperty(value = "计划开始时间")
    private String planStartDate;

    @Excel(name = "计划完成时间")
    @ApiModelProperty(value = "计划完成时间")
    private String planEndDate;

    @Excel(name = "事项所属业务")
    @ApiModelProperty(value = "事项所属业务")
    private String matterBusiness;

    @Excel(name = "门店地址")
    @ApiModelProperty(value = "门店地址")
    private String storeAddrDetail;

    @ApiModelProperty(value = "所属区域编码")
    private String saleRegionCode;

    @Excel(name = "所属区域")
    @ApiModelProperty(value = "所属区域名称")
    private String saleRegionName;

    @Excel(name = "经营模式")
    @ApiModelProperty(value = "经营模式")
    private String operateMode;

    @ApiModelProperty(value = "加盟商编码")
    private String customerCode;

    @Excel(name = "加盟商")
    @ApiModelProperty(value = "加盟商名称")
    private String customerName;

}

