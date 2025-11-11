package com.platform.ems.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 样品 出库导出
 *
 * @author yangqz
 * @date 2021-7-13
 */
@Data
@ApiModel
@Accessors(chain = true)
public class InvInventoryDocumentExChkYPResponse {

    @Excel(name = "出库凭证编号")
    @ApiModelProperty(value = "库存凭证号")
    private Long inventoryDocumentCode;

    @ApiModelProperty(value = "作业类型名称")
    @Excel(name = "作业类型")
    private String movementTypeName;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "开单日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "开单日期")
    private Date documentDate;

    @Excel(name = "仓库")
    @ApiModelProperty(value = "仓库名称")
    private String storehouseName;

    @Excel(name = "库位")
    @ApiModelProperty(value = "库位名称")
    private String locationName;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人名称")
    private String creatorAccountName;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @Excel(name = "出库人")
    @ApiModelProperty(value = "出入库操作人")
    private String storehouseOperatorName;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "出库日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "出入库日期（过账）")
    private Date accountDate;
}
