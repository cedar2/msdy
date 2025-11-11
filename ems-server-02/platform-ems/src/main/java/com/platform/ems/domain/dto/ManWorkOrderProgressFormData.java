package com.platform.ems.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 报表中心班组生产进度报表 ManWorkOrderProgressFormData
 *
 * @author chenkaiwen
 * @date 2022-08-25
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ManWorkOrderProgressFormData {

    @ApiModelProperty(value = "工序列表")
    private List<ManWorkOrderProgressFormProcess> processNameList;

    @ApiModelProperty(value = "关注事项列表")
    private List<ManWorkOrderProgressFormConcern> concernNameList;

    @ApiModelProperty(value = "表单数据")
    List<ManWorkOrderProgressForm> formList;

    /**
     * 表单数据 的 总记录数
     */
    private long total;
}
