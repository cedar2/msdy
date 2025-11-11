package com.platform.ems.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author :wance
 * @date : 2023/12/7 15:43
 */
@Data
public class DataTotal<T> implements Serializable {

    /**
     * 数据
     */
    @ApiModelProperty(value = "数据")
    private List<T> list;

    /**
     * 总数
     */
    @ApiModelProperty(value = "总数")
    private Integer total;

}

