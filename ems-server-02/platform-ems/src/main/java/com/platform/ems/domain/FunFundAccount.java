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
 * 资金账户信息对象 s_fun_fund_account
 *
 * @author chenkw
 * @date 2022-03-01
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_fun_fund_account")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FunFundAccount extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-资金账户
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-资金账户")
    private Long fundAccountSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] fundAccountSidList;

    /**
     * 账户编码
     */
    @Excel(name = "账户编码")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "账户编码")
    private Long fundAccountCode;

    /**
     * 账户名称
     */
    @NotBlank(message = "账户名称不能为空")
    @Excel(name = "账户名称")
    @ApiModelProperty(value = "账户名称")
    private String accountName;

    /**
     * 类型（数据字典的键值或配置档案的编码）
     */
    @NotBlank(message = "类型不能为空")
    @Excel(name = "类型", dictType = "s_fund_account_type")
    @ApiModelProperty(value = "类型（数据字典的键值或配置档案的编码）")
    private String accountType;

    /**
     * 公司档案sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司档案sid")
    private Long companySid;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司编码")
    private String companyCode;

    @TableField(exist = false)
    @Excel(name = "公司")
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    /**
     * 账号
     */
    @NotNull(message = "账号不能为空")
    @Length(max = 25, message = "账号最大只支持输入25位")
    @Excel(name = "账号")
    @ApiModelProperty(value = "账号")
    private String accountNumber;

    @TableField(exist = false)
    @ApiModelProperty(value = "名称—账号")
    private String nameNumber;

    /**
     * 银行名称（数据字典的键值或配置档案的编码）
     */
    @Length(max = 30, message = "银行名称最大只支持输入30位")
    @Excel(name = "银行名称", dictType = "s_bank")
    @ApiModelProperty(value = "银行名称（数据字典的键值或配置档案的编码）")
    private String bankName;
    /**
     * 银行名称支行名称
     */
    @Excel(name = "银行支行名称")
    @ApiModelProperty(value = "银行支行名称")
    private String bankBranchName;

    /**
     * 存款金额
     */
    @Digits(integer = 10, fraction = 5, message = "存款金额整数位上限为10位，小数位上限为5位")
    @Excel(name = "存款金额(元)", scale = 2)
    @ApiModelProperty(value = "存款金额")
    private BigDecimal currencyAmount;

    /**
     * 汇票金额
     */
    @Digits(integer = 10, fraction = 5, message = "汇票金额整数位上限为10位，小数位上限为5位")
    @Excel(name = "汇票金额(元)", scale = 2)
    @ApiModelProperty(value = "汇票金额")
    private BigDecimal huipiaoCurrencyAmount;

    /**
     * 币种（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "币种", dictType = "s_currency")
    @ApiModelProperty(value = "币种（数据字典的键值或配置档案的编码）")
    private String currency;

    /**
     * 货币单位（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "货币单位", dictType = "s_currency_unit")
    @ApiModelProperty(value = "货币单位（数据字典的键值或配置档案的编码）")
    private String currencyUnit;

    /**
     * 最近更新时间(存款金额)
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "最近更新时间(存款金额)", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "最近更新时间(存款金额)")
    private Date latestUpdateDate;

    /**
     * 最近更新时间(汇票金额)
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "最近更新时间(汇票金额)", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "最近更新时间(汇票金额)")
    private Date huipiaoLatestUpdateDate;

    /**
     * 账户金额
     */
    @Digits(integer = 10, fraction = 5, message = "金额（变更前）整数位上限为10位，小数位上限为5位")
    @ApiModelProperty(value = "账户金额（变更前）")
    private BigDecimal currencyAmountBgq;

    /**
     * 账户金额
     */
    @Digits(integer = 10, fraction = 5, message = "金额（作废前）整数位上限为10位，小数位上限为5位")
    @ApiModelProperty(value = "金额（作废前）")
    private BigDecimal currencyAmountZfq;

    @TableField(exist = false)
    @Excel(name = "当前审批节点名称")
    @ApiModelProperty(value = "当前审批节点名称")
    private String approvalNode;

    @TableField(exist = false)
    @Excel(name = "当前审批人")
    @ApiModelProperty(value = "当前审批人")
    private String approvalUserName;

    /**
     * 处理状态（数据字典的键值或配置档案的编码）
     */
    @NotBlank(message = "处理状态不能为空")
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

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
     * 确认人账号（用户账号）
     */
    @ApiModelProperty(value = "确认人账号（用户账号）")
    private String confirmerAccount;

    /**
     * 确认人
     */
    @Excel(name = "确认人")
    @TableField(exist = false)
    private String confirmerAccountName;

    /**
     * 确认时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 图片路径，可放多个链接，每个链接用”;“隔开
     */
    @ApiModelProperty(value = "图片路径，可放多个链接，每个链接用”;“隔开")
    private String picturePath;

    @TableField(exist = false)
    @ApiModelProperty(value = "图片路径，可放多个链接，每个链接用”;“隔开")
    private String[] picturePathList;

    /**
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;

    @TableField(exist = false)
    @ApiModelProperty(value = "附件清单")
    private List<FunFundAccountAttach> attachmentList;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态(多选)")
    private String[] handleStatusList;

    @TableField(exist = false)
    @ApiModelProperty(value = "账户类型(多选)")
    private String[] accountTypeList;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-公司档案(多选)")
    private Long[] companySidList;

    /**
     * 是否是导入
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "是否为导入")
    private String importStatus;
}
