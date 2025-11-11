package com.platform.ems.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 外发加工费结算单对象 s_man_manufacture_outsource_settle
 *
 * @author linhongwei
 * @date 2021-06-10
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_man_manufacture_outsource_settle")
public class ManManufactureOutsourceSettle extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-外发加工费结算单
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-外发加工费结算单")
    private Long manufactureOutsourceSettleSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] manufactureOutsourceSettleSidList;

    /**
     * 外发加工费结算单号
     */
    @Excel(name = "外发加工费结算单号")
    @ApiModelProperty(value = "外发加工费结算单号")
    private String manufactureOutsourceSettleCode;

    /**
     * 系统SID-供应商信息（外发加工商）
     */
    @NotNull(message = "加工商不能为空")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商信息（外发加工商）")
    private Long vendorSid;

    /**
     * 系统SID-供应商信息（外发加工商）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-供应商信息（外发加工商）")
    private Long[] vendorSidList;

    /**
     * 供应商编码
     */
    @ApiModelProperty(value = "供应商编码")
    private String vendorCode;

    /**
     * 供应商名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "供应商名称")
    private String vendorName;

    /**
     * 供应商简称
     */
    @TableField(exist = false)
    @Excel(name = "加工商")
    @ApiModelProperty(value = "供应商简称")
    private String vendorShortName;

    /**
     * 系统SID-工厂
     */
    @NotNull(message = "工厂不能为空")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-工厂")
    private Long plantSid;

    /**
     * 系统SID-工厂
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-工厂")
    private Long[] plantSidList;

    /**
     * 工厂编码
     */
    @ApiModelProperty(value = "工厂编码")
    private String plantCode;

    /**
     * 工厂名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "工厂名称")
    private String plantName;

    /**
     * 工厂简称
     */
    @TableField(exist = false)
    @Excel(name = "工厂")
    @ApiModelProperty(value = "工厂简称")
    private String plantShortName;

    /**
     * 单据日期
     */
    @NotNull(message = "单据日期不能为空")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "单据日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "单据日期")
    private Date documentDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "单据日期 起")
    private String documentDateBegin;

    @TableField(exist = false)
    @ApiModelProperty(value = "单据日期 至")
    private String documentDateEnd;

    /**
     * 系统SID-公司档案
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-公司档案")
    private Long companySid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-公司档案")
    private Long[] companySidList;

    @ApiModelProperty(value = "公司")
    private String companyCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @Excel(name = "公司")
    @TableField(exist = false)
    @ApiModelProperty(value = "公司简称")
    private String companyShortName;

    /**
     * 款数
     */
    @Excel(name = "款数", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "款数")
    private BigDecimal productNum;

    /**
     * 完成量小计
     */
    @Excel(name = "完成量小计", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "完成量小计")
    private BigDecimal totalCompleteQuantity;

    /**
     * 合格量小计
     */
    @Excel(name = "合格量小计", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "合格量小计")
    private BigDecimal totalSettleQuantity ;

    /**
     * 次品量小计
     */
    @Excel(name = "次品量小计", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "次品量小计")
    private BigDecimal totalDefectiveQuantity ;

    /**
     * 加工金额小计(含税)
     */
    @Excel(name = "加工金额小计(含税)", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "加工金额小计(含税)")
    private BigDecimal totalProcessAmountTax;

    /**
     *  明细扣款小计(含税)
     */
    @Excel(name = "明细扣款小计(含税)", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "明细扣款小计(含税)")
    private BigDecimal totalDeductionTax;

    /**
     * 额外扣款(含税)
     */
    @Excel(name = "额外扣款(含税)", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "结算量额外扣款(含税)")
    private BigDecimal extraDeductionTax;

    /**
     * 额外扣款
     */
    @ApiModelProperty(value = "额外扣款(不含税)")
    private BigDecimal extraDeduction;

    /**
     * 额外补贴(含税)
     */
    @Digits(integer = 9, fraction = 6, message = "次品允许比例整数位上限为2位，小数位上限为3位")
    @Excel(name = "额外补贴(含税)", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "额外补贴(含税)")
    private BigDecimal extraAllowanceTax;

    /**
     * 额外补贴(不含税)
     */
    @Digits(integer = 9, fraction = 6, message = "次品允许比例整数位上限为2位，小数位上限为3位")
    @ApiModelProperty(value = "额外补贴(不含税)")
    private BigDecimal extraAllowance;

    /**
     * 结算金额小计(含税)
     */
    @Excel(name = "结算金额小计(含税)", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "结算金额小计(含税)")
    private BigDecimal totalSettleAmountTax;

    @TableField(exist = false)
    @ApiModelProperty(value = "本次申请金额(含税)(支付中)")
    private BigDecimal currencyAmountTaxZfz;

    @TableField(exist = false)
    @ApiModelProperty(value = "本次申请金额(含税)(已支付)")
    private BigDecimal currencyAmountTaxYzf;

    @TableField(exist = false)
    @ApiModelProperty(value = "本次申请金额(含税)(未支付)")
    private BigDecimal currencyAmountTaxWzf;

    /**
     * 单据类型编码code
     */
    @ApiModelProperty(value = "单据类型编码code")
    private String documentType;

    @Excel(name = "单据类型")
    @TableField(exist = false)
    @ApiModelProperty(value = "单据类型编码code")
    private String documentTypeName;

    /**
     * 单据类型
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "单据类型")
    private String[] documentTypeList;

    /**
     * 业务类型编码code
     */
    @ApiModelProperty(value = "业务类型编码code")
    private String businessType;

    @Excel(name = "业务类型")
    @TableField(exist = false)
    @ApiModelProperty(value = "业务类型编码code")
    private String businessTypeName;

    /**
     * 业务类型
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "业务类型")
    private String[] businessTypeList;

    /**
     * 系统SID-采购合同
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-采购合同")
    private Long purchaseContractSid;

    /**
     * 采购合同号
     */
    @Excel(name = "采购合同号")
    @ApiModelProperty(value = "采购合同号")
    private String purchaseContractCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "采购合同名称")
    private String purchaseContractName;

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

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 当前审批节点名称
     */
    @Excel(name = "当前审批节点")
    @ApiModelProperty(value = "当前审批节点名称")
    @TableField(exist = false)
    private String approvalNode;

    /**
     * 当前审批人
     */
    @Excel(name = "当前审批人")
    @ApiModelProperty(value = "当前审批人")
    @TableField(exist = false)
    private String approvalUserName;

    /**
     * 提交人
     */
    @ApiModelProperty(value = "提交人")
    @TableField(exist = false)
    private String submitUserName;

    /**
     * 提交日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "提交日期")
    @TableField(exist = false)
    private Date submitDate;

    /**
     * 当前审批人id
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "当前审批人id")
    private String approvalUserId;

    /**
     * 处理状态（数据字典的键值或配置档案的编码）
     */
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    /**
     * 处理状态
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态")
    private String[] handleStatusList;

    /**
     * 创建人账号（用户名称）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    @Excel(name = "创建人")
    @TableField(exist = false)
    private String creatorAccountName;

    @TableField(exist = false)
    @ApiModelProperty(value = "创建人")
    private String[] creatorAccountList;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
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
    @TableField(exist = false)
    private String updaterAccountName;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更改日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 确认人账号（用户名称）
     */
    @ApiModelProperty(value = "确认人账号（用户名称）")
    private String confirmerAccount;

    @TableField(exist = false)
    @Excel(name = "确认人")
    @ApiModelProperty(value = "确认人")
    private String confirmerAccountName;

    /**
     * 确认时间
     */
    @Excel(name = "确认日期", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    /**
     * 生产订单号
     */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "生产订单号")
    private Long manufactureOrderCode;

    /**
     * 结算量
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "结算量")
    private BigDecimal settleQuantitySum;

    /**
     * 结算金额
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "结算金额")
    private BigDecimal settleAmount;

    @TableField(exist = false)
    @ApiModelProperty(value = "最大行号")
    private Long maxItemNum;

    /**
     * 外发加工费结算单-明细对象
     */
    @Valid
    @TableField(exist = false)
    @ApiModelProperty(value = "外发加工费结算单-明细对象")
    private List<ManManufactureOutsourceSettleItem> manManufactureOutsourceSettleItemList;

    /**
     * 外发加工费结算单-额外扣款明细对象
     */
    @Valid
    @TableField(exist = false)
    @ApiModelProperty(value = "外发加工费结算单-额外扣款明细对象")
    private List<ManOutsourceSettleExtraDeductionItem> extraDeductionItemList;

    /**
     * 外发加工费结算单-附件对象
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "外发加工费结算单-附件对象")
    private List<ManManufactureOutsourceSettleAttach> attachmentList;
}
