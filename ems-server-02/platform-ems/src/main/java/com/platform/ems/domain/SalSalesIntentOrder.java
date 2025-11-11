package com.platform.ems.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableId;

import com.platform.common.core.domain.EmsBaseEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.experimental.Accessors;

/**
 * 销售意向单对象 s_sal_sales_intent_order
 *
 * @author chenkw
 * @date 2022-10-17
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_sal_sales_intent_order")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SalSalesIntentOrder extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-销售意向单
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-销售意向单")
    private Long salesIntentOrderSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] salesIntentOrderSidList;

    /**
     * 销售意向单号
     */
    @Excel(name = "销售意向单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售意向单号")
    private Long salesIntentOrderCode;

    /**
     * 系统SID-客户信息
     */
    @NotNull(message = "客户不能为空")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-客户信息")
    private Long customerSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-客户信息(多选)")
    private Long[] customerSidList;

    /**
     * 客户编码
     */
    @ApiModelProperty(value = "客户编码")
    private String customerCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "客户名称")
    private String customerName;

    @Excel(name = "客户")
    @TableField(exist = false)
    @ApiModelProperty(value = "客户简称")
    private String customerShortName;

    /**
     * 系统SID-公司档案
     */
    @NotNull(message = "公司不能为空")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-公司档案")
    private Long companySid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-公司信息(多选)")
    private Long[] companySidList;

    /**
     * 业务类型编码
     */
    @NotBlank(message = "业务类型不能为空")
    @ApiModelProperty(value = "业务类型编码")
    private String businessType;

    @TableField(exist = false)
    @ApiModelProperty(value = "业务类型(多选)")
    private String[] businessTypeList;

    @TableField(exist = false)
    @Excel(name = "业务类型")
    @ApiModelProperty(value = "业务类型")
    private String businessTypeName;

    /**
     * 公司编码
     */
    @ApiModelProperty(value = "公司编码")
    private String companyCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @Excel(name = "公司")
    @TableField(exist = false)
    @ApiModelProperty(value = "公司简称")
    private String companyShortName;

    /**
     * 系统SID-产品季档案
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-产品季档案")
    private Long productSeasonSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-产品季信息(多选)")
    private Long[] productSeasonSidList;

    /**
     * 产品季编码
     */
    @ApiModelProperty(value = "产品季编码")
    private String productSeasonCode;

    @Excel(name = "下单季")
    @TableField(exist = false)
    @ApiModelProperty(value = "产品季编码")
    private String productSeasonName;

    /**
     * 销售员（用户名称）
     */
    @NotBlank(message = "销售员不能为空")
    @ApiModelProperty(value = "销售员（用户名称）")
    private String salePerson;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售员（用户ID）")
    private Long salePersonId;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售员(多选)")
    private String[] salePersonList;

    @TableField(exist = false)
    @Excel(name = "销售员")
    @ApiModelProperty(value = "销售员（用户名称）")
    private String salePersonName;

    @TableField(exist = false)
    @ApiModelProperty(value = "租户默认设置销售财务对接人员多值;")
    private String saleFinanceAccountId;

    /**
     * 单据日期
     */
    @NotNull(message = "单据日期不能为空")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "单据日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "单据日期")
    private Date documentDate;

    /**
     * 客供料方式（数据字典的键值或配置档案的编码）
     */
    @NotBlank(message = "客供料方式不能为空")
    @Excel(name = "客供料方式", dictType = "s_raw_material_mode")
    @ApiModelProperty(value = "客供料方式（数据字典的键值或配置档案的编码）")
    private String rawMaterialMode;

    @TableField(exist = false)
    @ApiModelProperty(value = "客供料方式(多选)")
    private String[] rawMaterialModeList;

    /**
     * 物料类型（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "物料类型（数据字典的键值或配置档案的编码）")
    private String materialType;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料类型(多选)")
    private String[] materialTypeList;

    @TableField(exist = false)
    @Excel(name = "商品类型")
    @ApiModelProperty(value = "物料类型（数据字典的键值或配置档案的编码）")
    private String materialTypeName;

    /**
     * 销售模式（数据字典的键值或配置档案的编码）
     */
    @NotBlank(message = "销售模式不能为空")
    @Excel(name = "销售模式", dictType = "s_price_type")
    @ApiModelProperty(value = "销售模式（数据字典的键值或配置档案的编码）")
    private String saleMode;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售模式(多选)")
    private String[] saleModeList;

    /**
     * 意向销售合同号/协议号
     */
    @Excel(name = "意向合同号/协议号")
    @ApiModelProperty(value = "意向销售合同号/协议号")
    private String saleIntentContractCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "意向销售合同号(纸质合同)是否为空")
    private String paperSaleIntentContractCodeIsNull;

    /**
     * 销售意向订单合同(盖章版)
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "销售意向订单合同(盖章版)")
    private String saleOrderIntentContractGzbName;

    /**
     * 销售意向订单合同(盖章版)
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "销售意向订单合同(盖章版)")
    private String saleOrderIntentContractGzbPath;

    /**
     * 销售部门（数据字典的键值或配置档案的编码）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售部门（数据字典的键值或配置档案的编码）")
    private Long saleDepartment;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售部门(多选)")
    private Long[] saleDepartmentList;

    @TableField(exist = false)
    @Excel(name = "销售部门")
    @ApiModelProperty(value = "销售部门（数据字典的键值或配置档案的编码）")
    private String saleDepartmentName;

    /**
     * 业务渠道/销售渠道（数据字典的键值或配置档案的编码）
     */
    @NotBlank(message = "销售渠道不能为空")
    @ApiModelProperty(value = "业务渠道/销售渠道（数据字典的键值或配置档案的编码）")
    private String businessChannel;

    @TableField(exist = false)
    @ApiModelProperty(value = "业务渠道/销售渠道(多选)")
    private String[] businessChannelList;

    @TableField(exist = false)
    @Excel(name = "销售渠道")
    @ApiModelProperty(value = "业务渠道/销售渠道（数据字典的键值或配置档案的编码）")
    private String businessChannelName;

    /**
     * 销售组织（数据字典的键值或配置档案的编码）
     */
    @TableField(exist = false)
    @Excel(name = "销售组织")
    @ApiModelProperty(value = "销售组织（数据字典的键值或配置档案的编码）")
    private String saleOrg;

    /**
     * 销售组（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "销售组（数据字典的键值或配置档案的编码）")
    private String saleGroup;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售组(多选)")
    private String[] saleGroupList;

    @TableField(exist = false)
    @Excel(name = "销售组")
    @ApiModelProperty(value = "销售组（数据字典的键值或配置档案的编码）")
    private String saleGroupName;

    /**
     * 纸质下单合同号
     */
    @Excel(name = "纸质下单合同号")
    @ApiModelProperty(value = "意向销售合同号(纸质合同)")
    private String paperSaleIntentContractCode;

    /**
     * 上传状态(纸质合同)（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "上传状态(纸质合同)（数据字典的键值或配置档案的编码）")
    private String uploadStatus;

    /**
     * 签收状态(纸质合同)（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "签收状态(纸质合同)（数据字典的键值或配置档案的编码）")
    private String signInStatus;

    /**
     * 客方跟单员
     */
    @ApiModelProperty(value = "客方跟单员")
    private String customerBusinessman;

    /**
     * 币种（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "币种（数据字典的键值或配置档案的编码）")
    private String currency;

    /**
     * 货币单位（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "货币单位（数据字典的键值或配置档案的编码）")
    private String currencyUnit;

    /**
     * 意向销售合同/协议sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "意向销售合同/协议sid")
    private Long saleIntentContractSid;

    /**
     * 作废原因类型
     */
    @ApiModelProperty(value = "作废原因类型")
    private String cancelType;

    /**
     * 作废原因备注
     */
    @ApiModelProperty(value = "作废原因备注")
    private String cancelRemark;

    /**
     * 处理状态（数据字典的键值或配置档案的编码）
     */
    @NotBlank(message = "处理状态不能为空")
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态(多选)")
    private String[] handleStatusList;

    /**
     * 创建人账号（用户名称）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    @TableField(exist = false)
    @ApiModelProperty(value = "创建人账号(多选 userName)")
    private String[] creatorAccountList;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人昵称(nickName)")
    @TableField(exist = false)
    private String creatorAccountName;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /**
     * 更新人账号（用户名称）
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

    @Excel(name = "更改人")
    @ApiModelProperty(value = "更改人昵称")
    @TableField(exist = false)
    private String updaterAccountName;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 确认人账号确认人账号（用户名称）
     */
    @ApiModelProperty(value = "确认人账号确认人账号（用户名称）")
    private String confirmerAccount;

    @Excel(name = "确认人")
    @ApiModelProperty(value = "确认人昵称")
    @TableField(exist = false)
    private String confirmerAccountName;

    /**
     * 确认时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "确认时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    @TableField(exist = false)
    @ApiModelProperty(value = "总订单量")
    private BigDecimal sumQuantity;

    @TableField(exist = false)
    @ApiModelProperty(value = "总金额")
    private BigDecimal sumMoneyAmount;

    @TableField(exist = false)
    @ApiModelProperty(value = "总款数")
    private int sumQuantityCode;

    /**
     * 销售意向单对象列表
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "销售意向单对象列表")
    private List<SalSalesIntentOrderItem> intentOrderItemList;

    /**
     * 附件清单列表
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "附件清单列表")
    private List<SalSalesIntentOrderAttach> attachmentList;

}
