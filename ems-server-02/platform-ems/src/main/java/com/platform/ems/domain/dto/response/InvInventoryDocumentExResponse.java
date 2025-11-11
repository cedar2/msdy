package com.platform.ems.domain.dto.response;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 入库 导出
 *
 * @author yangqz
 * @date 2021-7-13
 */
@Data
@ApiModel
@Accessors(chain = true)
public class InvInventoryDocumentExResponse implements Serializable {

    @Excel(name = "库存凭证号")
    @ApiModelProperty(value = "库存凭证号")
    private Long inventoryDocumentCode;

    @ApiModelProperty(value = "作业类型名称")
    @Excel(name = "作业类型")
    private String movementTypeName;

    @Excel(name = "业务单据类别")
    @ApiModelProperty(value = "业务单据类别")
    private String referDocCategoryName;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "开单日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "开单日期")
    private Date documentDate;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "出入库日期（过账）", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "出入库日期（过账）")
    private Date accountDate;

    @Excel(name = "作业单号")
    @ApiModelProperty(value = "查询：关联业务单号")
    private String referDocumentCode;

    @Excel(name = "仓库名称")
    @ApiModelProperty(value = "仓库名称")
    private String storehouseName;

    @Excel(name = "库位名称")
    @ApiModelProperty(value = "库位名称")
    private String locationName;

    @Excel(name = "供方送货单号")
    @ApiModelProperty(value = "供方送货单号（供方交货单号）")
    private String supplierDeliveryCode;

    @Excel(name = "公司名称")
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @Excel(name = "特殊库存")
    @ApiModelProperty(value = "特殊库存")
    private String specialStockName;

    @Excel(name = "供应商名称")
    @ApiModelProperty(value = "供应商名称")
    private String vendorName;

    @Excel(name = "客户名称")
    @ApiModelProperty(value = "客户名称")
    private String customerName;

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

    @Excel(name = "出入库操作人")
    @ApiModelProperty(value = "出入库操作人")
    private String storehouseOperatorName;

}
