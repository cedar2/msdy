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
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

/**
 * 供应商注册-基础对象 s_bas_vendor_register
 *
 * @author chenkw
 * @date 2022-02-21
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_bas_vendor_register")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasVendorRegister extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-供应商注册基本信息
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商注册基本信息")
    private Long vendorRegisterSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] vendorRegisterSidList;

    /**
     * 供应商注册流水号
     */
    @Excel(name = "供应商注册流水号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "供应商注册流水号")
    private Long vendorRegisterNum;

    /**
     * 供应商注册码
     */
    @Excel(name = "供应商注册码")
    @ApiModelProperty(value = "供应商注册码")
    private String vendorRegisterCode;

    /**
     * 供应商编码
     */
    @ApiModelProperty(value = "供应商编码")
    private String vendorCode;

    /**
     * 供应商名称
     */
    @NotBlank(message = "供应商名称不能为空")
    @Length(max = 300, message = "供应商名称最大只支持输入300位")
    @Excel(name = "供应商名称")
    @ApiModelProperty(value = "供应商名称")
    private String vendorName;

    /**
     * 供应商简称
     */
    @Length(max = 180, message = "供应商简称最大只支持输入180位")
    @Excel(name = "供应商简称")
    @ApiModelProperty(value = "供应商简称")
    private String shortName;

    /**
     * 供应商组（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "供应商组",dictType = "s_vendor_group")
    @ApiModelProperty(value = "供应商组（数据字典的键值）")
    private String vendorGroup;

    /**
     * 公司档案sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司档案sid")
    private Long companySid;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司档案编码")
    private String companyCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司档案名称")
    private String companyName;

    /**
     * 关联公司档案sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "关联公司档案sid")
    private Long relateCompanySid;

    @TableField(exist = false)
    @ApiModelProperty(value = "关联公司档案编码")
    private String relateCompanyCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "关联公司档案名称")
    private String relateCompanyName;

    /**
     * 关联客户档案sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "关联客户档案sid")
    private Long customerSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "关联客户档案编码")
    private String customerCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "关联客户档案名称")
    private String customerName;

    /**
     * 原始供应商编码sid，用于记录供应商变更执照前的供应商编码
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "原始供应商编码sid，用于记录供应商变更执照前的供应商编码")
    private Long originalVendorSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "原始供应商编码")
    private Long originalVendorCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "原始供应商名称")
    private String originalVendorName;

    /**
     * 所属行业（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "所属行业（数据字典的键值）")
    private String industryCode;

    /**
     * 供应商类别（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "供应商类别（数据字典的键值）")
    private String vendorCategory;

    @TableField(exist = false)
    @ApiModelProperty(value = "供应商类别（数据字典的键值）")
    private String[] vendorCategoryList;

    /**
     * 供应商类型（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "供应商类型", dictType = "s_vendor_type")
    @ApiModelProperty(value = "供应商类型（数据字典的键值）")
    private String vendorType;

    /**
     * 经营性质（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "经营性质", dictType = "s_bussiness_property_v")
    @ApiModelProperty(value = "经营性质（数据字典的键值）")
    private String businessNature;

    /**
     * 供应商性质（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "供应商性质", dictType = "s_vendor_property")
    @ApiModelProperty(value = "供应商性质（数据字典的键值）")
    private String vendorProperty;

    @TableField(exist = false)
    @ApiModelProperty(value = "供应商性质（数据字典的键值）")
    private String[] vendorPropertyList;

    /**
     * 主营产品
     */
    @Length(max = 300, message = "主营产品最大只支持输入300位")
    @Excel(name = "主营产品")
    @ApiModelProperty(value = "主营产品")
    private String coreProduct;

    /**
     * 法人/负责人姓名
     */
    @Length(max = 120, message = "法人/负责人姓名最大只支持输入120位")
    @Excel(name = "法人/负责人姓名")
    @ApiModelProperty(value = "法人/负责人姓名")
    private String ownerName;

    /**
     * 纳税人识别号
     */
    @Length(max = 30,message = "纳税人识别号最大只支持输入30位")
    @Excel(name = "纳税人识别号")
    @ApiModelProperty(value = "纳税人识别号")
    private String taxpayerIdentifyNumber;

    /**
     * 统一信用代码证号/身份证号
     */
    @Length(max = 30, message = "统一信用代码证号/身份证号最大只支持输入30位")
    @Excel(name = "统一信用代码证号/身份证号")
    @ApiModelProperty(value = "统一信用代码证号/身份证号")
    private String creditCode;

    /**
     * 注册资金（万元）
     */
    @Digits(integer = 11, fraction = 4, message = "注册资金整数位上限为11位，小数位上限为4位")
    @Excel(name = "注册资金（万元）")
    @ApiModelProperty(value = "注册资金（万元）")
    private BigDecimal registerCapital;

    /**
     * 注册资金（币种）（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "注册资金（币种）", dictType = "s_currency")
    @ApiModelProperty(value = "注册资金（币种）（数据字典的键值）")
    private String registerCapitalCurrency;

    /**
     * 年度产值（万元）
     */
    @Digits(integer = 11, fraction = 4, message = "年度产值整数位上限为11位，小数位上限为4位")
    @Excel(name = "年度产值（万元）")
    @ApiModelProperty(value = "年度产值（万元）")
    private BigDecimal outputValue;

    @Excel(name = "年度产值（币种）", dictType = "s_currency")
    @ApiModelProperty(value = "年度产值（币种）（数据字典的键值）")
    private String outputValueCurrency;

    @Excel(name = "年份（年度产值）", dictType = "s_year")
    @ApiModelProperty(value = "年份（年度产值）（数据字典的键值）")
    private String outputValueYear;

    /**
     * 所属区域-国家区域sid
     */

    @Excel(name = "所在区域")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "所属区域-国家区域sid")
    private Long countryRegion;

    /**
     * 是否上市（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "是否上市", dictType = "s_yesno_flag")
    @ApiModelProperty(value = "是否上市（数据字典的键值）")
    private String onMarket;

    /**
     * 是否有检测实验室（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "是否有检测实验室", dictType = "s_yesno_flag")
    @ApiModelProperty(value = "是否有检测实验室（数据字典的键值）")
    private String withTestlab;

    /**
     * 是否快反战略合作供应商（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "是否快反战略合作供应商（数据字典的键值）")
    private String isAgileVendor;

    /**
     * 技术等级（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "技术等级（数据字典的键值）")
    private String technicalGrade;

    /**
     * 管理等级（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "管理等级（数据字典的键值）")
    private String managementClass;

    /**
     * 固定资产规模（万元）
     */
    @Digits(integer = 11, fraction = 4, message = "固定资产规模整数位上限为11位，小数位上限为4位")
    @ApiModelProperty(value = "固定资产规模（万元）")
    private BigDecimal fixedAssetValue;

    /**
     * 固定资产规模（币种）（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "固定资产规模（币种）（数据字典的键值或配置档案的编码）")
    private String fixedAssetValueCurrency;

    /**
     * 设备资产（万元）
     */
    @Digits(integer = 11, fraction = 4, message = "设备资产整数位上限为11位，小数位上限为4位")
    @ApiModelProperty(value = "设备资产（万元）")
    private BigDecimal equipmentAssetValue;

    /**
     * 设备资产（币种）（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "设备资产（币种）（数据字典的键值或配置档案的编码）")
    private String equipmentAssetCurrency;

    /**
     * 注册人
     */
    @Length(max = 120,message = "注册人最大只支持输入120位")
    @Excel(name = "注册人")
    @ApiModelProperty(value = "注册人")
    private String registerer;

    /**
     * 注册日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "注册日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "注册日期")
    private Date registerDate;

    /**
     * 注册人电话
     */
    @Length(max = 30,message = "注册人电话最大只支持输入30位")
    @Excel(name = "注册人电话")
    @ApiModelProperty(value = "注册人电话")
    private String registerTel;

    /**
     * 注册人邮箱
     */
    @NotBlank(message = "为方便正常接收注册码，请填写正确的邮箱地址")
    @Length(max = 60,message = "邮箱最大只支持输入60位")
    @Excel(name = "注册人邮箱")
    @ApiModelProperty(value = "邮箱")
    private String registerEmail ;

    /**
     * 推荐人
     */
    @Length(max = 120,message = "推荐人最大只支持输入120位")
    @ApiModelProperty(value = "推荐人")
    private String referrer;

    /**
     * 推荐人电话
     */
    @Length(max = 30,message = "推荐人电话最大只支持输入30位")
    @ApiModelProperty(value = "推荐人电话")
    private String referrerTel;

    /**
     * 营业执照-有效期起
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "营业执照-有效期起", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "营业执照-有效期起")
    private Date creditStartDate;

    /**
     * 营业执照-有效期至
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "营业执照-有效期至", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "营业执照-有效期至")
    private Date creditEndDate;

    /**
     * 官方网站
     */
    @Length(max = 200,message = "官方网站最大只支持输入200位")
    @Excel(name = "官方网站")
    @ApiModelProperty(value = "官方网站")
    private String internetUrl;

    /**
     * 开票电话
     */
    @Length(max = 30,message = "开票电话最大只支持输入30位")
    @Excel(name = "开票电话")
    @ApiModelProperty(value = "开票电话")
    private String invoiceTel;

    /**
     * 开票地址
     */
    @Length(max = 300,message = "开票地址最大只支持输入300位")
    @Excel(name = "开票地址")
    @ApiModelProperty(value = "开票地址")
    private String invoiceAddr;

    /**
     * 注册地址
     */
    @Length(max = 300,message = "注册地址最大只支持输入300位")
    @Excel(name = "注册地址")
    @ApiModelProperty(value = "注册地址")
    private String registerAddr;

    /**
     * 办公地址
     */
    @Length(max = 300,message = "办公地址最大只支持输入300位")
    @Excel(name = "办公地址")
    @ApiModelProperty(value = "办公地址")
    private String businessAddr;

    /**
     * 营业范围
     */
    @Length(max = 3000,message = "营业范围最大只支持输入3000位")
    @Excel(name = "营业范围")
    @ApiModelProperty(value = "营业范围")
    private String businessScope;

    /**
     * 图片路径
     */
    @ApiModelProperty(value = "图片路径")
    private String picturePath;

    /**
     * 我方跟单员
     */
    @Length(max = 30,message = "我方跟单员最大只支持输入30位")
    @ApiModelProperty(value = "我方跟单员")
    private String buOperator;

    /**
     * 供方业务员
     */
    @Length(max = 30,message = "供方业务员最大只支持输入30位")
    @ApiModelProperty(value = "供方业务员")
    private String buOperatorVendor;

    /**
     * 处理状态（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值）")
    private String handleStatus;

    @Excel(name = "当前审批节点")
    @ApiModelProperty(value = "当前审批节点名称")
    @TableField(exist = false)
    private String approvalNode;

    @Excel(name = "当前审批人")
    @ApiModelProperty(value = "当前审批人")
    @TableField(exist = false)
    private String approvalUserName;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态（数据字典的键值）")
    private String[] handleStatusList;

    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 创建人账号（用户账号）
     */
    @ApiModelProperty(value = "创建人账号（用户账号）")
    private String creatorAccount;

    @TableField(exist = false)
    @ApiModelProperty(value = "创建人")
    private String creatorAccountName;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd hh:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /**
     * 更新人账号（用户账号）
     */
    @ApiModelProperty(value = "更新人账号（用户账号）")
    private String updaterAccount;

    @TableField(exist = false)
    @ApiModelProperty(value = "更新人")
    private String updaterAccountName;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd hh:mm:ss")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 确认人账号（用户账号）
     */
    @ApiModelProperty(value = "确认人账号（用户账号）")
    private String confirmerAccount;


    @Excel(name = "确认人")
    @TableField(exist = false)
    @ApiModelProperty(value = "确认人")
    private String confirmerAccountName;

    /**
     * 确认时间
     */

    @Excel(name = "确认时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd hh:mm:ss")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    @Valid
    @TableField(exist = false)
    @ApiModelProperty(value = "供应商注册-联系人")
    private List<BasVendorRegisterAddr> addrList;

    @Valid
    @TableField(exist = false)
    @ApiModelProperty(value = "供应商注册-财务信息-基本户")
    private List<BasVendorRegisterBankAccount> baseBankAccountList;

    @Valid
    @TableField(exist = false)
    @ApiModelProperty(value = "供应商注册-财务信息-一般户")
    private List<BasVendorRegisterBankAccount> basVendorBankAccountList;

    @Valid
    @TableField(exist = false)
    @ApiModelProperty(value = "供应商注册-主要客户")
    private List<BasVendorRegisterCustomer> basMainCustomerList;

    @Valid
    @TableField(exist = false)
    @ApiModelProperty(value = "供应商注册-上游供货商")
    private List<BasVendorRegisterSupplier> basMainVendorList;

    @Valid
    @TableField(exist = false)
    @ApiModelProperty(value = "供应商注册-产能信息")
    private List<BasVendorRegisterProductivity> basVendorProductivityList;

    @Valid
    @TableField(exist = false)
    @ApiModelProperty(value = "供应商注册-设备信息")
    private List<BasVendorRegisterMachine> basVendorMachineList;

    @Valid
    @TableField(exist = false)
    @ApiModelProperty(value = "供应商注册-人员信息")
    private BasVendorRegisterTeam basVendorTeam;

    @Valid
    @TableField(exist = false)
    @ApiModelProperty(value = "供应商注册-附件")
    private List<BasVendorRegisterAttach> attachmentList;
}
