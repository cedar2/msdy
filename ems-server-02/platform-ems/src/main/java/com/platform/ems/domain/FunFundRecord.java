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

import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

/**
 * 资金流水对象 s_fun_fund_record
 *
 * @author chenkw
 * @date 2022-03-01
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_fun_fund_record")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FunFundRecord extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-资金流水
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-资金流水")
    private Long fundRecordSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] fundRecordSidList;

    /**
     * 资金流水编码
     */
    @Excel(name = "资金流水编码")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "资金流水编码")
    private Long fundRecordCode;

    /**
     * 公司档案sid
     */
    @NotNull(message = "公司不能为空")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司档案sid")
    private Long companySid;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司档案sid")
    private Long[] companySidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司编码")
    private String companyCode;

    @TableField(exist = false)
    @Excel(name = "公司")
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    /**
     * 收付款类型（数据字典的键值或配置档案的编码）
     */
//    @NotBlank(message = "收付款类型不能为空")
    @Excel(name = "收付款",dictType = "s_shoufukuan_type")
    @ApiModelProperty(value = "收付款类型（数据字典的键值或配置档案的编码）")
    private String paymentType;

    @TableField(exist = false)
    @ApiModelProperty(value = "收付款类型（数据字典的键值或配置档案的编码）")
    private String[] paymentTypeList;

    /**
     * 金额
     */
    @Digits(integer = 10, fraction = 5, message = "金额整数位上限为10位，小数位上限为5位")
    @Excel(name = "金额", scale = 2)
    @ApiModelProperty(value = "金额")
    private BigDecimal currencyAmount;

    /**
     *  收付款方式（数据字典的键值或配置档案的编码）
     */
//    @NotBlank(message = "收付款方式不能为空")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "收付款方式（数据字典的键值或配置档案的编码）")
    private String paymentMethod;

    @TableField(exist = false)
    @ApiModelProperty(value = "收付款方式")
    private String[] paymentMethodList;

    @TableField(exist = false)
    @Excel(name = "收付款方式")
    @ApiModelProperty(value = "收付款名称")
    private String paymentMethodName;

    @TableField(exist = false)
    @Excel(name = "资金账户")
    @ApiModelProperty(value = "资金账户名称")
    private String accountName;

    /**
     * 流水经办人
     */
    @Length(max = 30,message = "流水经办人最大只支持输入30位")
    @Excel(name = "流水经办人")
    @ApiModelProperty(value = "流水经办人")
    private String flowOperator;

    /**
     * 流水交易日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "流水交易日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "流水交易日期")
    private Date transactionDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "流水交易日期-起")
    private String transactionDateBegin;

    @TableField(exist = false)
    @ApiModelProperty(value = "流水交易日期-至")
    private String transactionDateEnd;

    /**
     * 用途（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "用途",dictType = "s_fund_record_category")
    @ApiModelProperty(value = "用途（数据字典的键值或配置档案的编码）")
    private String flowCategory;

    @TableField(exist = false)
    @ApiModelProperty(value = "用途（数据字典的键值或配置档案的编码）")
    private String[] flowCategoryList;

    /**
     * 流水记账日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "流水记账日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "流水记账日期")
    private Date transactionAccountDate;

    /**
     * 流水业务归属（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "业务归属",dictType = "s_fund_business_type")
    @ApiModelProperty(value = "流水业务归属（数据字典的键值或配置档案的编码）")
    private String businessType;

    @TableField(exist = false)
    @ApiModelProperty(value = "流水业务归属（数据字典的键值或配置档案的编码）")
    private String[] businessTypeList;

    /**
     * 交易流水号
     */
    @Excel(name = "交易流水号")
    @Length(max = 20,message = "交易流水号最大只支持输入20位")
    @ApiModelProperty(value = "交易流水号")
    private String transactionNumber;

    /**
     * 流水凭证图片路径，可放多个链接，每个链接用”;“隔开
     */
    @Excel(name = "流水凭证")
    @ApiModelProperty(value = "流水凭证图片路径，可放多个链接，每个链接用”;“隔开")
    private String picturePath;

    @TableField(exist = false)
    @ApiModelProperty(value = "流水凭证图片路径，可放多个链接，每个链接用”;“隔开")
    private String[] picturePathList;
    /**
     * 收付款方名称
     */
    @Length(max = 200,message = "收付款方最大只支持输入200位")
    @Excel(name = "收付款方名称")
    @ApiModelProperty(value = "收付款方名称")
    private String paymentName;

    /**
     * 收付款方银行名称
     */
    @Length(max = 200,message = "收付款方银行名称最大只支持输入200位")
    @Excel(name = "收付款方银行名称",dictType = "s_bank")
    @ApiModelProperty(value = "收付款方银行名称")
    private String paymentBankName;

    @TableField(exist = false)
    @ApiModelProperty(value = "收付款方银行名称")
    private String[] paymentBankNameList;

    /**
     *  收付款方银行支行名称（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "收付款方银行支行名称")
    @ApiModelProperty(value = "收付款方银行支行名称")
    private String paymentBankBranchName;

    /**
     * 流水纸质单号
     */
    @Length(max = 20,message = "流水纸质单号最大只支持输入20位")
    @Excel(name = "流水纸质单号")
    @ApiModelProperty(value = "流水纸质单号")
    private String transactionBillNumber;

    /**
     * 参考业务单号
     */
    @Length(max = 60,message = "参考业务单号最大只支持输入60位")
    @Excel(name = "参考业务单号")
    @ApiModelProperty(value = "参考业务单号")
    private String referBusinessNote;

    /**
     *  收单机构
     */
    @Excel(name = "收单机构")
    @ApiModelProperty(value = "收单机构")
    private String acquirer;

    /**
     *  清算机构
     */
    @Excel(name = "清算机构")
    @ApiModelProperty(value = "清算机构")
    private String liquidationInstitution;

    /**
     * 资金类型（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "资金类型",dictType = "s_fund_type")
    @ApiModelProperty(value = "资金类型（数据字典的键值或配置档案的编码）")
    private String fundType;

    @TableField(exist = false)
    @ApiModelProperty(value = "资金类型（数据字典的键值或配置档案的编码）")
    private String[] fundTypeList;

    /** 收付款账号 */
    @Length(max = 25,message = "收付款方账号最大只支持输入25位")
    @Excel(name = "收付款账号")
    @ApiModelProperty(value = "收付款账号")
    private String paymentAccountNumber;

    /**
     * 币种（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "币种",dictType = "s_currency")
    @ApiModelProperty(value = "币种（数据字典的键值或配置档案的编码）")
    private String currency;

    @Excel(name = "备注")
    @ApiModelProperty(value ="备注")
    private String remark;

    /**
     *  流水说明
     */
    @Excel(name = "流水说明")
    @ApiModelProperty(value = "流水说明")
    private String fundRecordRemark;


    @TableField(exist = false)
    @ApiModelProperty(value = "当前审批节点名称")
    private String approvalNode;

    @TableField(exist = false)
    @ApiModelProperty(value = "当前审批人")
    private String approvalUserName;

    /**
     * 处理状态（数据字典的键值或配置档案的编码）
     */
//    @NotBlank(message = "处理状态不能为空")
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;





    /**
     * 创建人账号（用户账号）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户账号）")
    private String creatorAccount;

    @Excel(name = "创建人")
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
     * 更新人账号（用户账号）
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户账号）")
    private String updaterAccount;

    @Excel(name = "更新人")
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
     * 系统SID-资金账户
     *
     @NotNull(message = "资金账户不能为空")
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-资金账户")
    private Long fundAccountSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-资金账户")
    private Long[] fundAccountSidList;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "资金账户编码")
    private Long fundAccountCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "资金账户名称")
    private String[] accountNameList;

    /**
     * 货币单位（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "货币单位（数据字典的键值或配置档案的编码）")
    private String currencyUnit;

    /**
     * 我方收付款银行名称
     */
    @Length(max = 200,message = "我方收付款银行名称最大只支持输入200位")
    @ApiModelProperty(value = "我方收付款银行名称")
    private String fundAccountBankName;

    /** 我方资金账号 */
    @Length(max = 25,message = "我方资金账号最大只支持输入25位")
    @ApiModelProperty(value = "我方资金账号")
    private String fundAccountNumber;

    @TableField(exist = false)
    @ApiModelProperty(value = "流水记账日期-起")
    private String transactionAccountDateBegin;

    @TableField(exist = false)
    @ApiModelProperty(value = "流水记账日期-至")
    private String transactionAccountDateEnd;


    @TableField(exist = false)
    @ApiModelProperty(value = "我方收付款银行名称")
    private String[] fundAccountBankNameList;

    /**
     * 确认人账号（用户账号）
     */
    @ApiModelProperty(value = "确认人账号（用户账号）")
    private String confirmerAccount;

    @TableField(exist = false)
    private String confirmerAccountName;

    /**
     * 确认时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 票据号码（汇票）
     */
    @ApiModelProperty(value = "票据号码（汇票）")
    private String huipiaoCode;

    /**
     * 款项类别（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "款项类别（数据字典的键值或配置档案的编码）")
    private String accountCategory;

    /**
     * 收付款单号
     */
    @ApiModelProperty(value = "收付款单号")
    private String shoufukuanCode;

    /**
     * 款项类别是否更改
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "款项类别是否更改")
    private String planAccountCategory;

    /**
     * 收付款方式更改
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "收付款方式更改")
    private String planPaymentMethod;

    /**
     * 用途是否更改
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "用途是否更改")
    private String planFlowCategory;

    /**
     * 资金类型是否更改
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "资金类型是否更改")
    private String planFundType;

    /**
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;

    @TableField(exist = false)
    @ApiModelProperty(value = "附件清单")
    private List<FunFundRecordAttach> attachmentList;

    /**
     * 是否是导入
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "是否为导入")
    private String importStatus;

}
