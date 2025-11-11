package com.platform.ems.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 移库 导出
 *
 * @author yangqz
 * @date 2021-7-13
 */
@Data
@ApiModel
@Accessors(chain = true)
public class InvInventoryDocumentExYkResponse implements Serializable {


    @Excel(name = "库存凭证编号")
    @ApiModelProperty(value = "库存凭证号")
    private Long inventoryDocumentCode;

    @ApiModelProperty(value = "作业类型名称")
    @Excel(name = "作业类型")
    private String movementTypeName;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "开单日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "开单日期")
    private Date documentDate;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "移库日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "移库日期")
    private Date accountDate;

    @Excel(name = "仓库")
    @ApiModelProperty(value = "仓库名称")
    private String storehouseName;

    @Excel(name = "库位")
    @ApiModelProperty(value = "库位名称")
    private String locationName;

    @Excel(name = "目标仓库")
    @ApiModelProperty(value = "目标仓库名称")
    private String destStorehouseName;

    @Excel(name = "目的库位")
    @ApiModelProperty(value = "目标库位名称")
    private String destLocationName;

    @Excel(name = "特殊库存")
    @ApiModelProperty(value = "特殊库存")
    private String specialStockName;

    @Excel(name = "供应商")
    @ApiModelProperty(value = "供应商名称")
    private String vendorName;

    @Excel(name = "客户")
    @ApiModelProperty(value = "客户名称")
    private String customerName;

    @Excel(name = "移库方式", dictType = "s_stock_transfer_mode")
    @ApiModelProperty(value = "移库方式")
    private String stockTransferMode;

    @Excel(name = "入库状态", dictType = "s_in_out_store_status")
    @ApiModelProperty(value = "入库状态，用于标识两步法移库的出库凭证的入库状态")
    private String inOutStockStatus;

    @Excel(name = "下单季")
    @ApiModelProperty(value = "产品季/下单季档案编码（数据字典的键值或配置档案的编码）")
    private String productSeasonName;

    @Excel(name = "货运单号")
    @ApiModelProperty(value = "货运单号")
    private String carrierNoteCode;

    @Excel(name = "承运商")
    @ApiModelProperty(value = "货运方（承运商）")
    private String carrierName;

    @Excel(name = "收货日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "到货日期")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date daohuoDate;

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

    @ApiModelProperty(value = "出入库操作人")
    private String storehouseOperatorName;

}
