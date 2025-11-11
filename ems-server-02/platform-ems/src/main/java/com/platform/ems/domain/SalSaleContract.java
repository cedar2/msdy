package com.platform.ems.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;

import com.platform.ems.domain.dto.response.SaleReportResponse;
import com.platform.ems.util.data.KeepTwoDecimalsSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

/**
 * 销售合同信息对象 s_sal_sale_contract
 *
 * @author linhongwei
 * @date 2021-05-18
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_sal_sale_contract")
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class SalSaleContract extends EmsBaseEntity {

    /**
     * 客户端口号
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /**
     * 系统自增长ID-销售合同
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-销售合同")
    private Long saleContractSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] saleContractSidList;
    /**
     * 销售合同号
     */
    @Length(max = 20, message = "合同号长度不能超过20个字符")
    @ApiModelProperty(value = "销售合同号")
    @Excel(name = "销售合同号")
    private String saleContractCode;

    @ApiModelProperty(value = "销售合同号(用来精确查询的)")
    @TableField(exist = false)
    private String contractCode;

    /**
     * 系统自增长ID-客户信息
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-客户信息")
    private Long customerSid;

    /**
     * 客户名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "客户名称")
    private String customerName;

    @TableField(exist = false)
    @Excel(name = "客户简称")
    @ApiModelProperty(value = "客户简称")
    private String shortName;

    @TableField(exist = false)
    @ApiModelProperty(value = "客户简称")
    private String customerShortName;

    /**
     * 合同类型（数据字典的键值）
     */
    @ApiModelProperty(value = "合同类型（数据字典的键值）")
    @Excel(name = "合同类型", dictType = "s_contract_type")
    private String contractType;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售框架协议号sid")
    private Long outlineAgreementSid;

    @TableField(exist = false)
    @Excel(name = "框架协议号")
    @ApiModelProperty(value = "销售框架协议号code")
    private String outlineAgreementCode;

    /**
     * 合同名称
     */
    @Excel(name = "合同名称")
    @Length(max = 200, message = "合同名称长度不能超过200个字符")
    @ApiModelProperty(value = "合同名称")
    private String contractName;

    /**
     * 年份（数据字典的键值）
     */
    @Excel(name = "年份", dictType = "s_year")
    @ApiModelProperty(value = "年份（数据字典的键值）")
    private Integer year;

    /**
     * 产品季名称
     */
    @TableField(exist = false)
    @Excel(name = "下单季")
    @ApiModelProperty(value = "产品季名称")
    private String productSeasonName;

    /**
     * 上传状态(纸质合同)（数据字典的键值）
     */
    @Excel(name = "上传状态(纸质合同)", dictType = "s_upload_status")
    @ApiModelProperty(value = "上传状态(纸质合同)（数据字典的键值）")
    private String uploadStatus;

    @Excel(name = "客供料方式", dictType = "s_raw_material_mode")
    @ApiModelProperty(value = "客供料方式（数据字典的键值）")
    private String rawMaterialMode;

    /**
     * 公司名称
     */
    @TableField(exist = false)
    @Excel(name = "公司")
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司简称")
    private String companyShortName;

    /**
     * 用户昵称
     */
    @TableField(exist = false)
    @Excel(name = "销售员")
    private String nickName;

    @Excel(name = "合同签约人")
    @TableField(exist = false)
    @ApiModelProperty(value = "合同签约人账号（用户账号）")
    private String contractSignerName;

    /**
     * 签收状态(纸质合同)（数据字典的键值）
     */
    @Excel(name = "签收状态(纸质合同)", dictType = "s_sign_status")
    @ApiModelProperty(value = "签收状态(纸质合同)（数据字典的键值）")
    private String signInStatus;

    /**
     * 处理状态
     */
    @NotBlank(message = "处理状态不能为空")
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    /**
     * 收付款方式组合名称
     */
    @TableField(exist = false)
    @Excel(name = "收款方式组合")
    @ApiModelProperty(value = "收付款方式组合名称")
    private String accountsMethodGroupName;

    /**
     * 预收款收款方式名称
     */
    @TableField(exist = false)
    @Excel(name = "预收款结算方式")
    @ApiModelProperty(value = "预收款收款方式名称")
    private String advanceSettleModeName;

    /**
     * 尾款结算方式名称
     */
    @Excel(name = "尾款结算方式")
    @TableField(exist = false)
    @ApiModelProperty(value = "尾款结算方式")
    private String remainSettleModeName;

    /**
     * 物料类型名称
     */
    @TableField(exist = false)
    @Excel(name = "物料类型")
    @ApiModelProperty(value = "物料类型名称")
    private String materialTypeName;

    /**
     * 销售渠道名称
     */
    @TableField(exist = false)
    @Excel(name = "销售渠道")
    @ApiModelProperty(value = "销售渠道名称")
    private String businessChannelName;

    @Excel(name = "销售模式", dictType = "s_price_type")
    @ApiModelProperty(value = "销售模式（数据字典）")
    private String saleMode;

    /**
     * 合同金额(含税)
     */
    @Excel(name = "合同金额(含税)")
    @ApiModelProperty(value = "合同金额(含税)")
    @Digits(integer = 11, fraction = 4, message = "合同金额(含税)整数位上限为11位，小数位上限为4位")
    private BigDecimal currencyAmountTax;

    /**
     * 有效期从
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "有效期(起)", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "有效期从")
    private Date startDate;

    /**
     * 有效期至
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "有效期(至)", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "有效期至")
    private Date endDate;

    @Excel(name = "到期提醒天数")
    @ApiModelProperty(value = "即将到期提醒天数")
    private Long toexpireDays;

    /**
     * 客方合同号
     */
    @Length(max = 20, message = "合同号长度不能超过20个字符")
    @Excel(name = "客方合同号")
    @ApiModelProperty(value = "客方合同号")
    private String customerContractCode;

    /**
     * 货期免责天数
     */
    @Excel(name = "货期免责天数")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "货期免责天数")
    private Long exemptionDay;

    /**
     * 质保期
     */
    @Excel(name = "质保期")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "质保期")
    private Long warranty;

    @Excel(name = "特殊用途", dictType = "s_contract_purpose")
    @ApiModelProperty(value = "特殊用途（数据字典的键值或配置档案的编码）")
    private String contractPurpose;

    @TableField(exist = false)
    @ApiModelProperty(value = "特殊用途（多选）")
    private String[] contractPurposeList;

    /**
     * 短交允许比率（存值，即：不含百分号，如20%，就存0.2）
     */
    @ApiModelProperty(value = "短发允许比率（存值，即：不含百分号，如20%，就存0.2）")
    @Digits(integer = 1, fraction = 4, message = "短发允许比例不能超过100%，且只允许输入2位小数")
    private BigDecimal allowableRatioShort;

    /**
     * 超交允许比率（存值，即：不含百分号，如20%，就存0.2）
     */
    @ApiModelProperty(value = "超发允许比率（存值，即：不含百分号，如20%，就存0.2）")
    @Digits(integer = 1, fraction = 4, message = "超发允许比例不能超过100%，且只允许输入2位小数")
    private BigDecimal allowableRatioMore;

    @Excel(name = "短发允许比例(%)")
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "短发允许比率（%）")
    @TableField(exist = false)
    private BigDecimal allowableRatioShortPct;

    @Excel(name = "超发允许比例(%)")
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "超发允许比率（%）")
    @TableField(exist = false)
    private BigDecimal allowableRatioMorePct;

    @Excel(name = "合同标识",dictType ="s_contract_tag")
    @ApiModelProperty(value = "合同标识/属性（数据字典的键值或配置档案的编码），如：意向、常规")
    private String contractTag;


    /**
     * 税率（存值，即：不含百分号，如20%，就存0.2）
     */
    @Excel(name = "税率")
    @ApiModelProperty(value = "税率（存值，即：不含百分号，如20%，就存0.2）")
    private BigDecimal taxRate;

    /**
     * 币种（数据字典的键值）
     */
    @Excel(name = "币种", dictType = "s_currency")
    @ApiModelProperty(value = "币种（数据字典的键值）")
    private String currency;

    /**
     * 货币单位（数据字典的键值）
     */
    @Excel(name = "货币单位", dictType = "s_currency_unit")
    @ApiModelProperty(value = "货币单位（数据字典的键值）")
    private String currencyUnit;

    /**
     * 原销售合同号sid
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "原销售合同号")
    private String originalSaleContractCode;

    /**
     * 公司编码（公司档案的sid）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司编码（公司档案的sid）")
    private Long companySid;

    /**
     * 公司品牌sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司品牌sid")
    private Long companyBrandSid;

    /**
     * 销售部门（数据字典的键值）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售部门（数据字典的键值）")
    private Long saleDepartment;

    /**
     * 销售组（数据字典的键值）
     */
    @ApiModelProperty(value = "销售组（数据字典的键值）")
    private String saleGroup;

    /**
     * 销售员（用户名称）
     */
    @ApiModelProperty(value = "销售员（用户名称）")
    private String salePerson;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售员用户表的id")
    private Long salePersonId;

    @TableField(exist = false)
    @ApiModelProperty(value = "租户默认设置销售财务对接人员多值;")
    private String saleFinanceAccountId;

    /**
     * 业务归属项目sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "业务归属项目sid")
    private Long referProject;

    /**
     * 质保期时间单位
     */
    @ApiModelProperty(value = "质保期时间单位")
    private String warrantyUnit;

    /**
     * 预收款收款方式（数据字典的键值）
     */
    @ApiModelProperty(value = "预收款收款方式（数据字典的键值）")
    private String advanceSettleMode;

    /**
     * 尾款结算方式（数据字典的键值）
     */
    @ApiModelProperty(value = "尾款结算方式（数据字典的键值）")
    private String remainSettleMode;

    @ApiModelProperty(value = " 预收款账期(天)")
    private Integer ysAccountValidDays;

    @ApiModelProperty(value = " 中期款账期(天)")
    private Integer zqAccountValidDays;

    @ApiModelProperty(value = " 尾款账期(天)")
    private Integer wqAccountValidDays;

    @ApiModelProperty(value = "账期天类型编码（数据字典的键值）")
    private String dayType;

    /**
     * 业务渠道/销售渠道（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "业务渠道/销售渠道（数据字典的键值或配置档案的编码）")
    private String businessChannel;

    @TableField(exist = false)
    @ApiModelProperty(value = "客供料方式（数据字典的键值）")
    private String[] rawMaterialModeList;

    /**
     * 销售渠道编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "销售渠道编码")
    private String businessChannelCode;

    /**
     * 系统ID-产品季档案
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-产品季档案")
    private Long productSeasonSid;

    /**
     * 业务类型（数据字典的键值）
     */
    @ApiModelProperty(value = "业务类型（数据字典的键值）")
    private String businessType;

    /**
     * 物料类型（数据字典的键值）
     */
    @NotBlank(message = "物料类型不能为空")
    @ApiModelProperty(value = "物料类型（数据字典的键值）")
    private String materialType;

    /**
     * 收付款方式组合sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "收付款方式组合sid")
    private Long accountsMethodGroup;

    @ApiModelProperty(value = "收付款方式组合编码")
    private String accountsMethodGroupCode;

    /**
     * 税率编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "税率编码")
    private String taxRateCode;

    /**
     * 税率名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "税率名称")
    private String taxRateName;

    /**
     * 合同金额(不含税)
     */
    @ApiModelProperty(value = "合同金额(不含税)")
    @Digits(integer = 11, fraction = 4, message = "合同金额(不含税)整数位上限为11位，小数位上限为4位")
    private BigDecimal currencyAmount;

    /**
     * 财务结算归口项目
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "财务结算归口项目")
    private Long settleProject;

    /**
     * 销售组织（数据字典的键值）
     */
    @ApiModelProperty(value = "销售组织（数据字典的键值）")
    private String saleOrg;

    @ApiModelProperty(value = "预收款说明")
    private String yushoukuanRemark;

    @ApiModelProperty(value = "中期款说明")
    private String zhongqikuanRemark;

    @ApiModelProperty(value = "尾款说明")
    private String weikuanRemark;

    @TableField(exist = false)
    @ApiModelProperty(value = "合同签约人账号（用户账号）,多选框")
    private String[] contractSignerList;

    @ApiModelProperty(value = "合同签约人账号（用户账号）")
    private String contractSigner;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "合同签约日期")
    private Date contractSignDate;

    @ApiModelProperty(value = "作废说明")
    private String cancelRemark;

    @ApiModelProperty(value = "'e签宝签署ID")
    private String esignFlowId;

    @ApiModelProperty(value = "e签宝签署状态")
    private String esignFlowStatus;

    @ApiModelProperty(value = "e签宝签署状态说明")
    private String esignFlowDesc;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "e签宝签署时间")
    private Date esignFlowTime;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 结案状态（数据字典的键值）
     */
    @ApiModelProperty(value = "结案状态（数据字典的键值）")
    private String closeStatus;

    /**
     * 原销售合同号sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "原销售合同号sid")
    private Long originalSaleContractSid;

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
     * 创建人账号
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    @TableField(exist = false)
    @ApiModelProperty(value = "创建人ID")
    private Long creatorUserId;

    @Excel(name = "创建人")
    @TableField(exist = false)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccountName;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /**
     * 更新人账号
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccount;

    @Excel(name = "更改人")
    @TableField(exist = false)
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccountName;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更新日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 确认人账号
     */
    @ApiModelProperty(value = "确认人账号")
    private String confirmerAccount;

    @TableField(exist = false)
    @ApiModelProperty(value = "确认人账号")
    private String confirmerAccountName;

    /**
     * 确认时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 数据源系统
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;

    /**
     * 客户编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "客户编码")
    private String customerCode;

    /**
     * 公司代码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "公司代码")
    private String companyCode;

    /**
     * 产品季编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "产品季编码")
    private String productSeasonCode;

    /**
     * 销售合同信息sids
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "销售合同信息sids")
    private List<Long> saleContractSids;

    /**
     * 销售合同信息-附件对象
     */
    @Valid
    @TableField(exist = false)
    @ApiModelProperty(value = "销售合同信息-附件对象")
    private List<SalSaleContractAttachment> attachmentList;

    @Valid
    @TableField(exist = false)
    @ApiModelProperty(value = "销售合同信息-支付方式-预收")
    private List<SalSaleContractPayMethod> payMethodListYusf;

    @Valid
    @TableField(exist = false)
    @ApiModelProperty(value = "销售合同信息-支付方式-中期")
    private List<SalSaleContractPayMethod> payMethodListZq;

    @Valid
    @TableField(exist = false)
    @ApiModelProperty(value = "销售合同信息-支付方式-尾款")
    private List<SalSaleContractPayMethod> payMethodListWq;

    public void setAttachmentList(List<SalSaleContractAttachment> attachmentList) {
        if (attachmentList == null){ this.attachmentList = new ArrayList<>(); }
        else{ this.attachmentList = attachmentList; }
    }

    public void setPayMethodListYusf(List<SalSaleContractPayMethod> payMethodListYusf) {
        if (payMethodListYusf == null){ this.payMethodListYusf = new ArrayList<>(); }
        else{ this.payMethodListYusf = payMethodListYusf; }
    }

    public void setPayMethodListZq(List<SalSaleContractPayMethod> payMethodListZq) {
        if (payMethodListZq == null){ this.payMethodListZq = new ArrayList<>(); }
        else{ this.payMethodListZq = payMethodListZq; }
    }

    public void setPayMethodListWq(List<SalSaleContractPayMethod> payMethodListWq) {
        if (payMethodListWq == null){ this.payMethodListWq = new ArrayList<>(); }
        else{ this.payMethodListWq = payMethodListWq; }
    }

    @ApiModelProperty(value = "流程ID")
    private String instanceId;

    /**
     * 流程状态 0：普通记录 1：待审批记录 2：审批结束记录
     */
    @ApiModelProperty(value = "流程状态")
    private String processType;

    @TableField(exist = false)
    @ApiModelProperty(value = "节点名称")
    private String node;

    @TableField(exist = false)
    @ApiModelProperty(value = "审批人")
    private String approval;

    /**
     * 创建人账号list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "创建人账号list")
    private String[] creatorAccountList;

    /**
     * 系统自增长ID-客户信息list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "系统自增长ID-客户信息list")
    private Long[] customerSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统自增长ID-公司信息list")
    private Long[] companySidList;

    /**
     * 年份（数据字典的键值）list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "年份（数据字典的键值）list")
    private String[] yearList;

    /**
     * 合同类型（数据字典的键值）list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "合同类型（数据字典的键值）list")
    private String[] contractTypeList;

    /**
     * 销售渠道（数据字典的键值）list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "销售渠道（数据字典的键值）list")
    private String[] businessChannelList;

    /**
     * 系统ID-产品季档案list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "系统ID-产品季档案list")
    private Long[] productSeasonSidList;

    /**
     * 处理状态list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态list")
    private String[] handleStatusList;

    /**
     * 销售员（用户名称）list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "销售员（用户名称）list")
    private String[] salePersonList;

    /**
     * 预收款收款方式（数据字典的键值）list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "预收款收款方式（数据字典的键值）list")
    private String[] advanceSettleModeList;

    /**
     * 物料类型（数据字典的键值）list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "物料类型（数据字典的键值）list")
    private String[] materialTypeList;

    /**
     * 上传状态(纸质合同)（数据字典的键值）list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "上传状态(纸质合同)（数据字典的键值）list")
    private String[] uploadStatusList;

    /**
     * 签收状态(纸质合同)（数据字典的键值）list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "签收状态(纸质合同)（数据字典的键值）list")
    private String[] signInStatusList;

    /**
     * 流程状态 0：普通记录 1：审批中 2：审批结束
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "审批状态")
    private String[] processTypeList;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售订单中回写销售合同sid")
    private List<String> saleOrderSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售合同附件路径")
    private String filePath;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售合同附件类型")
    private String fileType;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售价明细")
    private List<SaleReportResponse> salePriceItems=new ArrayList<>();

    @TableField(exist = false)
    @ApiModelProperty(value = "销售订单明细")
    private List<SalSalesOrderItem> SalSalesOrderItems=new ArrayList<>();

    @TableField(exist = false)
    @ApiModelProperty(value = "销售意向单明细")
    private List<SalSalesIntentOrderItem> intentOrderItemList;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售意向单明细金额小计")
    private BigDecimal intentOrderAmountTax;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售意向单明细订单量小计")
    private BigDecimal intentOrderQuantity;

    @ApiModelProperty(value = "是否是最后一个节点")
    @TableField(exist = false)
    private String isFinallyNode;

    @ApiModelProperty(value = "合同履行说明")
    private String performRemark;

    @ApiModelProperty(value = "订单量小计")
    @TableField(exist = false)
    private BigDecimal sumQuantity;

    @ApiModelProperty(value = "金额小计")
    @TableField(exist = false)
    private BigDecimal sumAmount;

    @ApiModelProperty(value = "待批金额小计")
    @TableField(exist = false)
    private BigDecimal sumWaitApprovalAmount;

}
