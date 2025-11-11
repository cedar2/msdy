package com.platform.ems.domain.dto.response;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 调拨单 导出
 *
 * @author yangqz
 * @date 2021-7-13
 */
@Data
@ApiModel
@Accessors(chain = true)
public class InvTransferExReponse implements Serializable {

    @Excel(name = "出库状态",dictType = "s_in_out_store_status")
    @ApiModelProperty(value = "出入库状态")
    private String OutStockStatus;

    @Excel(name = "入库状态",dictType = "s_in_out_store_status")
    @ApiModelProperty(value = "出入库状态")
    private String inStockStatus;

    @Excel(name = "调拨单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "调拨单号")
    private Long inventoryTransferCode;

    @ApiModelProperty(value = "作业类型名称")
    @Excel(name = "作业类型")
    private String movementTypeName;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "开单日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "开单日期")
    private Date documentDate;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "出库日期")
    @Excel(name = "出库日期", dateFormat = "yyyy-MM-dd")
    private Date accountDateChk;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "入库日期")
    @Excel(name = "入库日期", dateFormat = "yyyy-MM-dd")
    private Date accountDateRu;

    @Excel(name = "仓库名称")
    @ApiModelProperty(value = "仓库名称")
    private String storehouseName;

    @Excel(name = "库位名称")
    @ApiModelProperty(value = "库位名称")
    private String locationName;

    @Excel(name = "目标仓库")
    @ApiModelProperty(value = "目标仓库名称")
    private String destStorehouseName;

    @Excel(name = "目标库位")
    @ApiModelProperty(value = "目标库位名称")
    private String destLocationName;

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

    /** 当前审批节点名称 */
    @Excel(name = "当前审批节点")
    @ApiModelProperty(value = "当前审批节点名称")
    @TableField(exist = false)
    private String approvalNode;

    /** 当前审批人 */
    @Excel(name = "当前审批人")
    @ApiModelProperty(value = "当前审批人")
    @TableField(exist = false)
    private String approvalUserName;

    @Excel(name = "处理状态",dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    @ApiModelProperty(value = "查询：出库库存凭证编号")
    @Excel(name = "出库库存凭证编号")
    private String inventoryDocumentCodeChk;

    @Excel(name = "入库库存凭证编号")
    @ApiModelProperty(value = "入库库存凭证编号")
    private String inventoryDocumentCodeRu;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人名称")
    private String creatorAccountName;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @Excel(name = "更改人")
    @ApiModelProperty(value = "更改人名称")
    private String updaterAccountName;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;
}
