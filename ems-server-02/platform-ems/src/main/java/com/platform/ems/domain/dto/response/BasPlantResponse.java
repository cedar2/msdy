package com.platform.ems.domain.dto.response;

import com.platform.common.annotation.Excel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;


/**
 * 工厂档案查询响应
 */
@Data
@Accessors( chain = true)
public class BasPlantResponse implements Serializable {

    private Long plantSid;

    @Excel(name = "工厂编码")
    @ApiModelProperty("工厂编码")
    private String plantCode;

    @Excel(name = "工厂名称")
    @ApiModelProperty("工厂名称")
    private String plantName;

    @ApiModelProperty("经营类别")
    private String operateType;

    @Excel(name = "处理状态")
    @ApiModelProperty("处理状态")
    private String handleStatus;

    @Excel(name = "是否外协厂")
    @ApiModelProperty("是否外协厂")
    private String isOutsource;

    @Excel(name = "所属城市")
    @ApiModelProperty("所属城市")
    private String city;

    @Excel(name = "所属公司")
    @ApiModelProperty("所属公司")
    private String company;

    @Excel(name = "所属供应商")
    @ApiModelProperty("所属供应商")
    private String vendor;


    @Excel(name = "启用/停用状态")
    @ApiModelProperty("状态 ")
    private String status;

    @Excel(name = "外协工厂性质")
    @ApiModelProperty("外协工厂性质")
    private String outsourceAttribute;

    @Excel(name = "厂址")
    @ApiModelProperty("厂址")
    private String address;

    @Excel(name = "创建人")
    @ApiModelProperty("创建人")
    private String creatorAccount;

}
