package com.platform.ems.domain.dto.response.export;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 供应商寄售结算单查询导出返回类
 *
 * @author chenkw
 * @date 2023-06-06
 */
@Data
public class PurPurchaseOrderVendorJsExport {

    @Excel(name = "供应商寄售结算单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "供应商寄售结算单号")
    private Long purchaseOrderCode;

    @Excel(name = "供应商")
    @ApiModelProperty(value = "供应商名称")
    private String vendorName;

    @Excel(name = "单据类型")
    @ApiModelProperty(value = "单据类型名称")
    private String documentTypeName;

    @Excel(name = "业务类型")
    @ApiModelProperty(value = "业务类型名称")
    private String businessTypeName;

    @Excel(name = "公司")
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @Excel(name = "下单季")
    @ApiModelProperty(value = "产品季名称")
    private String productSeasonName;

    @Excel(name = "采购员")
    @ApiModelProperty(value = "采购员")
    private String nickName;

    @Excel(name = "单据日期", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "单据日期")
    private Date documentDate;

    @Excel(name = "甲供料方式", dictType = "s_raw_material_mode")
    @ApiModelProperty(value = "甲供料方式")
    private String rawMaterialMode;

    @Excel(name = "采购模式", dictType = "s_price_type")
    @ApiModelProperty(value = "采购模式")
    private String purchaseMode;

    @Excel(name = "物料类型")
    @ApiModelProperty(value = "物料类型名称")
    private String materialTypeName;

    @Excel(name = "交货状态", dictType = "s_delivery_status")
    @ApiModelProperty(value = "交货状态")
    private String deliveryStatus;

    @Excel(name = "仓库")
    @ApiModelProperty(value = "仓库名称")
    private String storehouseName;

    @Excel(name = "款数", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "总款数")
    private int sumQuantityCode;

    @Excel(name = "总订单量", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "总订单量")
    private BigDecimal sumQuantity;

    @Excel(name = "总金额", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "总金额")
    private BigDecimal sumMoneyAmount;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    @Excel(name = "采购合同")
    @ApiModelProperty(value = "采购合同/协议号")
    private String purchaseContractCode;

    @ApiModelProperty(value = "采购合同号(纸质合同)")
    private String paperPurchaseContractCode;

    @Excel(name = "合同特殊用途", dictType = "s_contract_purpose")
    @ApiModelProperty(value = "合同特殊用途")
    private String contractPurpose;

    @Excel(name = "采购组织")
    @ApiModelProperty(value = "采购组织")
    private String purchaseOrgName;

    @Excel(name = "采购组")
    @ApiModelProperty(value = "采购组名称")
    private String purchaseGroupName;

    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @Excel(name = "当前审批节点")
    @ApiModelProperty(value = "当前审批节点名称")
    private String approvalNode;

    @Excel(name = "当前审批人")
    @ApiModelProperty(value = "当前审批人")
    private String approvalUserName;

    @Excel(name = "盖章件签收状态", dictType = "s_sign_status")
    @ApiModelProperty(value = "签收状态")
    private String signInStatus;

    @Excel(name = "委托人")
    @ApiModelProperty(value = "委托人")
    private String trustorAccountName;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人")
    private String creatorAccountName;

    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @Excel(name = "更改人")
    @ApiModelProperty(value = "更改人")
    private String updaterAccountName;

    @Excel(name = "更改日期", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "更改时间")
    private Date updateDate;

    @Excel(name = "确认人")
    @ApiModelProperty(value = "确认人")
    private String confirmerAccountName;

    @Excel(name = "确认日期", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

}
