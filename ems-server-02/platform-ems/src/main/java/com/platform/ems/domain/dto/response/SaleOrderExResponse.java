package com.platform.ems.domain.dto.response;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 物料销售订单导出响应实体
 *
 * @author yangqz
 * @date 2021-7-12
 */
@Data
@ApiModel
@Accessors(chain = true)
public class SaleOrderExResponse implements Serializable {

    @Excel(name = "销售订单号")
    @ApiModelProperty(value = "销售订单号")
    private String salesOrderCode;

    @ApiModelProperty(value = "查询:客户名称")
    @Excel(name = "客户")
    private String customerName;

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

    @Excel(name = "销售员")
    private String nickName;

    @Excel(name = "处理状态",dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "单据日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "单据日期")
    private Date documentDate;

    @Excel(name = "出入库状态", dictType = "s_in_out_store_status")
    @ApiModelProperty(value = "出入库状态")
    private String inOutStockStatus;

    @Excel(name = "客供料方式",dictType = "s_raw_material_mode")
    @ApiModelProperty(value = "客供料方式")
    private String rawMaterialMode;

    @Excel(name = "物料类型")
    @ApiModelProperty(value = "物料类型名称")
    private String materialTypeName;

    @Excel(name = "销售模式",dictType = "s_price_type")
    @ApiModelProperty(value = "销售模式")
    private String saleMode;

    @Excel(name = "销售部门")
    @ApiModelProperty(value = "销售部门名称")
    private String departmentName;

    @Excel(name = "销售合同号")
    @ApiModelProperty(value = "销售合同号")
    private String saleContractCode;

    @Excel(name = "客供料方式(合同)",dictType = "s_raw_material_mode")
    @ApiModelProperty(value = "客供料方式")
    private String rawMaterialModeContract;

    @Excel(name = "合同特殊用途",dictType = "s_contract_purpose")
    private String contractPurpose;

    @Excel(name = "销售渠道")
    @ApiModelProperty(value = "业务渠道/销售渠道名称")
    private String businessChannelName;

    @Excel(name = "仓库")
    @ApiModelProperty(value = "仓库名称")
    private String storehouseName;

    @Excel(name = "库位")
    @ApiModelProperty(value = "库位")
    private String locationName;


    @ApiModelProperty(value = "总款数")
    @Excel(name = "款数")
    private int sumQuantityCode;

    @ApiModelProperty(value = "总订单量")
    @Excel(name = "总订单量")
    private BigDecimal sumQuantity;

    @ApiModelProperty(value = "总金额")
    @Excel(name = "总金额")
    private BigDecimal sumMoneyAmount;

    @ApiModelProperty(value = "备注")
    @Excel(name = "备注")
    private String remark;

    @Excel(name = "销售组织")
    @ApiModelProperty(value = "销售组织名称")
    private String saleOrgName;

    @Excel(name = "销售组")
    @ApiModelProperty(value = "销售组名称")
    private String saleGroupName;

    @Excel(name = "当前审批节点")
    @ApiModelProperty(value = "当前审批节点名称")
    @TableField(exist = false)
    private String approvalNode;

    /** 当前审批人 */
    @Excel(name = "当前审批人")
    @ApiModelProperty(value = "当前审批人")
    @TableField(exist = false)
    private String approvalUserName;

    @ApiModelProperty(value = "是否退货")
    @Excel(name = "是否退货",dictType = "sys_yes_no")
    private String isReturnGoods;

    @ApiModelProperty(value = "是否生成待收预收流水（数据字典的键值或配置档案的编码）")
    @Excel(name = "是否生成待收预收流水",dictType = "sys_yes_no")
    private String isFinanceBookDsys;

    @ApiModelProperty(value = "是否生成应收暂估（数据字典的键值或配置档案的编码）")
    @Excel(name = "是否生成应收暂估流水",dictType = "sys_yes_no")
    private String isFinanceBookYszg;

    @ApiModelProperty(value = " 是否寄售结算（数据字典的键值或配置档案的编码）")
    @Excel(name = "是否寄售结算",dictType = "sys_yes_no")
    private String isConsignmentSettle;

    @ApiModelProperty(value = "销售发货类型（数据字典的键值或配置档案的编码）")
    @Excel(name = "销售发货类型",dictType = "s_so_delivery_type")
    private String  deliveryType;

    @ApiModelProperty(value = "库存管理方式（数据字典的键值或配置档案的编码）")
    @Excel(name = "库存管理方式",dictType = "s_inventory_control_mode")
    private String inventoryControlMode;

    @ApiModelProperty(value = "是否排产（数据字典的键值或配置档案的编码）")
    @Excel(name = "是否排产",dictType = "sys_yes_no")
    private String isManufacture;

    private String uploadStatus;

    @ApiModelProperty(value = "签收状态")
    private String signInStatus;

    /** 创建人账号 */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    @ApiModelProperty(value = "委托人")
    @Excel(name = "委托人")
    private String trustorAccountName;

    @Excel(name = "客方跟单员")
    @ApiModelProperty(value = "客方跟单员")
    private String customerBusinessman;

    @Excel(name = "纸质下单合同号")
    @ApiModelProperty(value = "销售合同号(纸质合同)")
    private String paperSaleContractCode;

    @Excel(name = "客户名称备注")
    @ApiModelProperty(value = "客户名称备")
    private String customerNameRemark;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccountName;

    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;


    @ApiModelProperty(value = "更新人账号")
    private String updaterAccount;

    @Excel(name = "更改人")
    @ApiModelProperty(value = "更新人")
    private String updaterAccountName;

    @Excel(name = "更改日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

}
