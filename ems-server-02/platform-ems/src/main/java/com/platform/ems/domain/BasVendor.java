
package com.platform.ems.domain;

import cn.hutool.core.util.StrUtil;
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
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 供应商档案对象 s_bas_vendor
 *
 * @author qhq
 * @date 2021-03-12
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_bas_vendor")
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasVendor extends EmsBaseEntity {

    /**
     * 客户端口号
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /**
     * 系统ID-供应商档案
     */
    @ApiModelProperty(value = "系统ID-供应商档案")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableId
    private Long vendorSid;

    /**
     * 供应商编码
     */
    @Excel(name = "供应商编码")
    @ApiModelProperty(value = "供应商编码")
    private Long vendorCode;

    /**
     * 供应商编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "供应商编码list")
    private List<String> vendorCodeList;

    /**
     * 去除首尾空格
     */
    public void setVendorName(String vendorName) {
        if (StrUtil.isNotBlank(vendorName)) {
            vendorName = vendorName.trim();
        }
        this.vendorName = vendorName;
    }

    /**
     * 去除首尾空格
     */
    public void setShortName(String shortName) {
        if (StrUtil.isNotBlank(shortName)) {
            shortName = shortName.trim();
        }
        this.shortName = shortName;
    }

    /**
     * 供应商名称
     */
    @NotNull(message = "供应商名称不能为空")
    @Excel(name = "供应商名称")
    @ApiModelProperty(value = "供应商名称")
    @Length(max = 300, message = "供应商名称不能超过300个字符")
    private String vendorName;

    @TableField(exist = false)
    @ApiModelProperty(value = "供应商名称 精确查询")
    private List<String> vendorNameList;

    @TableField(exist = false)
    @ApiModelProperty(value = "供应商模糊查询")
    private String vendorCodeName;

    /**
     * 供应商简称
     */
    @Excel(name = "供应商简称")
    @ApiModelProperty(value = "供应商简称")
    @Length(max = 180, message = "供应商简称不能超过180个字符")
    @NotNull(message = "供应商简称不能为空")
    private String shortName;

    @TableField(exist = false)
    @ApiModelProperty(value = "供应商简称 精确查询")
    private List<String> shortNameList;

    /**
     * 供应商组编码
     */
    @Excel(name = "供应商组", dictType = "s_vendor_group")
    @ApiModelProperty(value = "供应商组编码")
    private String vendorGroup;

    /**
     * 所属行业编码
     */
    @Excel(name = "所属行业", dictType = "s_industry")
    @ApiModelProperty(value = "所属行业编码")
    private String industryCode;

    /**
     * 启用/停用状态
     */
    @Excel(name = "启用/停用", dictType = "s_valid_flag")
    @ApiModelProperty(value = "启用/停用状态")
    private String status;

    @ApiModelProperty(value = "停用说明")
    private String disableRemark;

    /**
     * 处理状态
     */
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    /**
     * 合作状态
     */
    @Excel(name = "合作状态", dictType = "s_vendor_cooperate_status")
    @ApiModelProperty(value = "合作状态(数据字典的键值或配置档案的编码)")
    private String cooperateStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "合作状态(多选)")
    private String[] cooperateStatusList;

    @Excel(name = "我方跟单员")
    @TableField(exist = false)
    @ApiModelProperty(value = "我方跟单员")
    private String buOperatorName;

    @Excel(name = "供方业务员")
    @Length(max = 30, message = "供方业务员不能超过30个字符")
    @ApiModelProperty(value = "供方业务员")
    private String buOperatorVendor;

    /**
     * 供应商类别编码
     */
    @Excel(name = "供应商类别", dictType = "s_vendor_category")
    @ApiModelProperty(value = "供应商类别编码")
    private String vendorCategory;

    /**
     * 供应商类型编码
     */
    @Excel(name = "供应商类型", dictType = "s_vendor_type")
    @ApiModelProperty(value = "供应商类型编码")
    private String vendorType;

    /**
     * 供应商性质编码
     */
    @Excel(name = "供应商性质", dictType = "s_vendor_property")
    @ApiModelProperty(value = "供应商性质编码")
    private String vendorProperty;

    /**
     * 经营性质编码
     */
    @Excel(name = "经营性质", dictType = "s_bussiness_property_v")
    @ApiModelProperty(value = "经营性质编码")
    private String businessNature;

    /**
     * 是否快反战略合作供应商
     */
    @Excel(name = "是否快反供应商", dictType = "s_yesno_flag")
    @ApiModelProperty(value = "是否快反战略合作供应商")
    private String isAgileVendor;

    @Excel(name = "是否物流供应商", dictType = "s_yesno_flag")
    @ApiModelProperty(value = "是否物流供应商")
    private String isLogisticsVendor;

    /**
     * 关联供应商名称
     */
    @TableField(exist = false)
    @Excel(name = "关联公司")
    @ApiModelProperty(value = "关联公司名称")
    private String relateCompanyName;

    /**
     * 关联公司编码
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "关联公司编码")
    private Long relateCompanySid;

    /**
     * 推荐人电话
     */
    @ApiModelProperty(value = "推荐人电话")
    private String referrerTel;

    /**
     * 公司编码
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司编码")
    private Long companySid;

    @ApiModelProperty(value = "纳税人识别号")
    private String taxpayerIdentifyNumber;

    /**
     * 返回展示名称
     */
    @TableField(exist = false)
    private String companyName;

    /**
     * 关联客户编码
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "关联客户编码")
    private Long customerSid;

    /**
     * 返回展示名称
     */
    @Excel(name = "关联客户")
    @TableField(exist = false)
    private String customerName;

    /**
     * 主营产品
     */
    @ApiModelProperty(value = "主营产品")
    @Length(max = 300, message = "主营产品不能超过300个字符")
    private String coreProduct;

    /**
     * 法人/负责人姓名
     */
    @ApiModelProperty(value = "法人/负责人姓名")
    private String ownerName;

    /**
     * 统一信用代码证号/身份证号
     */
    @ApiModelProperty(value = "统一信用代码证号/身份证号")
    private String creditCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "统一信用代码证号/身份证号 精确查询")
    private List<String> creditCodeList;

    /**
     * 注册日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "注册日期")
    private Date registerDate;

    /**
     * 注册资金（万元）
     */
    @ApiModelProperty(value = "注册资金（万元）")
    private BigDecimal registerCapital;

    /**
     * 注册资金（币种）
     */
    @ApiModelProperty(value = "注册资金（币种）")
    private String registerCapitalCurrency;

    /**
     * 所属国家编码
     */
    @ApiModelProperty(value = "所属国家编码")
    private String country;

    /**
     * 所属省份编码
     */
    @ApiModelProperty(value = "所属省份编码")
    private String province;

    /**
     * 所属城市编码
     */
    @ApiModelProperty(value = "所属城市编码")
    private String city;

    /**
     * 是否上市
     */
    @ApiModelProperty(value = "是否上市")
    private String onMarket;

    /**
     * 是否有检测实验室
     */
    @ApiModelProperty(value = "是否有检测实验室")
    private String withTestlab;

    /**
     * 技术等级编码
     */
    @ApiModelProperty(value = "技术等级编码")
    private String technicalGrade;

    /**
     * 管理等级编码
     */
    @ApiModelProperty(value = "管理等级编码")
    private String managementClass;

    /**
     * 固定资产规模（万元）
     */
    @ApiModelProperty(value = "固定资产规模（万元）")
    private BigDecimal fixedAssetValue;

    /**
     * 固定资产规模（币种）
     */
    @ApiModelProperty(value = "固定资产规模（币种）")
    private String fixedAssetValueCurrency;

    /**
     * 设备资产（万元）
     */
    @ApiModelProperty(value = "设备资产（万元）")
    private BigDecimal equipmentAssetValue;

    /**
     * 设备资产（币种）
     */
    @ApiModelProperty(value = "设备资产（币种）")
    private String equipmentAssetCurrency;

    /**
     * 原始供应商编码
     */
    @ApiModelProperty(value = "原始供应商编码")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long originalVendorSid;

    /**
     * 年度产值（万元）
     */
    @Digits(integer = 11, fraction = 4, message = "年度产值整数位上限为11位，小数位上限为4位")
    @ApiModelProperty(value = "年度产值（万元）")
    private BigDecimal outputValue;

    @ApiModelProperty(value = "年度产值（币种）（数据字典的键值）")
    private String outputValueCurrency;

    @ApiModelProperty(value = "年份（年度产值）（数据字典的键值）")
    private String outputValueYear;

    /**
     * 推荐人
     */
    @ApiModelProperty(value = "推荐人")
    private String referrer;

    /**
     * 营业执照-有效期从
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "营业执照-有效期从")
    private Date creditStartDate;

    /**
     * 营业执照-有效期至
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "营业执照-有效期至")
    private Date creditEndDate;

    /**
     * 网址
     */
    @ApiModelProperty(value = "网址")
    private String internetUrl;

    /**
     * 开票电话
     */
    @ApiModelProperty(value = "开票电话")
    private String invoiceTel;

    /**
     * 开票地址
     */
    @ApiModelProperty(value = "开票地址")
    private String invoiceAddr;

    /**
     * 注册地址
     */
    @ApiModelProperty(value = "注册地址")
    private String registerAddr;

    /**
     * 办公地址
     */
    @ApiModelProperty(value = "办公地址")
    private String businessAddr;

    /**
     * 营业范围
     */
    @ApiModelProperty(value = "营业范围")
    private String businessScope;

    /**
     * 图片路径
     */
    @ApiModelProperty(value = "图片路径")
    private String picturePath;

    /**
     * 其它系统名称
     */
    @ApiModelProperty(value = "其它系统名称")
    private String otherSystemVendorCode;

    @ApiModelProperty(value = "我方跟单员")
    private String buOperator;

    @TableField(exist = false)
    @ApiModelProperty(value = "我方跟单员")
    private String buOperatorList;

    /**
     * 所属区域-国家区域sid
     */
    @Excel(name = "所在区域")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "所属区域-国家区域sid")
    private Long countryRegion;

    /**
     * 创建人账号
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    /**
     * 创建人账号
     */
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
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "更改日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 确认人账号
     */
    @ApiModelProperty(value = "确认人账号")
    private String confirmerAccount;

    @Excel(name = "确认人")
    @TableField(exist = false)
    @ApiModelProperty(value = "确认人账号")
    private String confirmerAccountName;

    /**
     * 确认时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "确认日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 数据源系统
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;

    @Valid
    @TableField(exist = false)
    @ApiModelProperty(value = "供应商财务信息List基本户")
    private List<BasVendorBankAccount> baseBankAccountList;

    @Valid
    @TableField(exist = false)
    @ApiModelProperty(value = "供应商财务信息List一般户")
    private List<BasVendorBankAccount> basVendorBankAccountList;

    @Valid
    @ApiModelProperty(value = "供应商联系方式信息List")
    @TableField(exist = false)
    private List<BasVendorAddr> addrList;

    @Valid
    @ApiModelProperty(value = "客户与供应商-主要客户")
    @TableField(exist = false)
    private List<BasVendorCustomer> basMainCustomerList;

    @Valid
    @ApiModelProperty(value = "客户与供应商-主要供应商")
    @TableField(exist = false)
    private List<BasVendorSupplier> basMainVendorList;

    @Valid
    @ApiModelProperty(value = "供应商的产能信息表")
    @TableField(exist = false)
    private List<BasVendorProductivity> basVendorProductivityList;

    @Valid
    @ApiModelProperty(value = "供应商的人员信息表")
    @TableField(exist = false)
    private BasVendorTeam basVendorTeam;

    @Valid
    @ApiModelProperty(value = "供应商的设备信息表")
    @TableField(exist = false)
    private List<BasVendorMachine> basVendorMachineList;

    /**
     * 批量启用停用传参
     */
    @TableField(exist = false)
    private List<Long> vendorSids;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品附件对象")
    private List<BasVendorAttachment> attachmentList;

    /**
     * 供应商组list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "供应商组list")
    private String[] vendorGroupList;

    /**
     * 关联公司list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "关联公司list")
    private Long[] relateCompanySidList;

    /**
     * 所属行业list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "所属行业list")
    private String[] industryCodeList;

    /**
     * 供应商类别list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "供应商类别list")
    private String[] vendorCategoryList;

    /**
     * 供应商性质list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "供应商性质list")
    private String[] vendorPropertyList;

    /**
     * 关联客户list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "关联客户list")
    private Long[] customerSidList;

    @ApiModelProperty(value = "处理状态list")
    @TableField(exist = false)
    private String[] handleStatusList;
}
