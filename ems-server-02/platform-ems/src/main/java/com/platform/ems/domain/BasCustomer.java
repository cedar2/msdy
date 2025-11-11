package com.platform.ems.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
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
import javax.validation.constraints.NotEmpty;

/**
 * 客户档案对象 s_bas_customer
 *
 * @author qhq
 * @date 2021-03-22
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_bas_customer")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasCustomer extends EmsBaseEntity {

    /** 客户端口号 */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /** 系统自增长ID-客户信息 */
    @ApiModelProperty(value = "系统自增长ID-客户信息")
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    private Long customerSid;

    /** 客户编码 */
    @Excel(name = "客户编码")
    @ApiModelProperty(value = "客户编码")
    @Length(max = 12,message = "客户编码不能超过12个字符")
    @NotEmpty(message = "客户编码不能为空")
    private String customerCode;

    /** 客户名称 */
    @Excel(name = "客户名称")
    @ApiModelProperty(value = "客户名称")
    @Length(max = 300,message = "客户名称不能超过300个字符")
    @NotEmpty(message = "客户名称不能为空")
    private String customerName;

    /** 去除首尾空格 */
    public void setCustomerCode(String customerCode) {
        if (StrUtil.isNotBlank(customerCode)){
            customerCode = customerCode.replaceAll("\\s*", "");
        }
        this.customerCode = customerCode;
    }

    /** 去除首尾空格 */
    public void setCustomerName(String customerName) {
        if (StrUtil.isNotBlank(customerName)){
            customerName = customerName.trim();
        }
        this.customerName = customerName;
    }

    /** 去除首尾空格 */
    public void setShortName(String shortName) {
        if (StrUtil.isNotBlank(shortName)){
            shortName = shortName.trim();
        }
        this.shortName = shortName;
    }

    /** 客户简称 */
    @Excel(name = "客户简称")
    @ApiModelProperty(value = "客户简称")
    @Length(max = 180,message = "客户简称不能超过180个字符")
    @NotEmpty(message = "客户简称不能为空")
    private String shortName;

    @TableField(exist = false)
    @ApiModelProperty(value = "模糊查询编码名称简称")
    private String customerCodeName;

    /** 客户组编码 */
    @Excel(name = "客户组", dictType = "s_customer_group")
    @ApiModelProperty(value = "客户组编码")
    @NotEmpty(message = "客户组编码不能为空")
    private String customerGroup;

    /** 所属行业编码 */
    @Excel(name = "所属行业", dictType = "s_industry")
    @ApiModelProperty(value = "所属行业编码")
    private String industryCode;

    /** 启用/停用状态 */
    @Excel(name = "启用/停用", dictType = "s_valid_flag")
    @ApiModelProperty(value = "启用/停用状态")
    private String status;

    @ApiModelProperty(value = "停用说明")
    private String disableRemark;

    /** 处理状态 */
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    /**
     * 合作状态
     */
    @Excel(name = "合作状态", dictType = "s_customer_cooperate_status")
    @ApiModelProperty(value = "合作状态(数据字典的键值或配置档案的编码)")
    private String cooperateStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "合作状态(多选)")
    private String[] cooperateStatusList;

    @Excel(name = "我方跟单员")
    @TableField(exist = false)
    @ApiModelProperty(value = "我方跟单员")
    private String buOperatorName;

    @Excel(name = "客方业务员")
    @Length(max = 30, message = "客方业务员不能超过30个字符")
    @ApiModelProperty(value = "客方业务员")
    private String buOperatorCustomer;

    /** 分销渠道类别（数据字典的键值），如：代理商、分销商 */
    @ApiModelProperty(value = "分销渠道类别（数据字典的键值），如：代理商、分销商")
    private String distributeChannelCategory;

    /** 分销渠道类别（数据字典的键值），如：代理商、分销商 */
    @Excel(name = "分销渠道类别")
    @ApiModelProperty(value = "分销渠道类别名称（数据字典的键值），如：代理商、分销商")
    @TableField(exist = false)
    private String distributeChannelCategoryName;

    /** 客户性质编码 */
    @Excel(name = "客户性质", dictType = "s_customer_property")
    @ApiModelProperty(value = "客户性质编码")
    private String customerProperty;

    /** 客户级别编码 */
    @Excel(name = "客户级别", dictType = "s_customer_level")
    @ApiModelProperty(value = "客户级别编码")
    private String customerLevel;

    /** 客户类别编码 */
    @ApiModelProperty(value = "客户类别编码")
    private String customerCategory;

    /** 客户类型编码 */
    @ApiModelProperty(value = "客户类型编码")
    private String customerType;

    /** 公司编码 */
    @ApiModelProperty(value = "公司编码")
    private String relateCompanySid;
    /**
     * 关联公司名称
     */
    @Excel(name = "关联公司")
    @ApiModelProperty(value = "关联公司名称")
    @TableField(exist = false)
    private String relateCompanyName;

    /** 关联公司编码 */
    @ApiModelProperty(value = "关联公司编码")
    private String companySid;

    /** 关联公司名称 */
    @TableField(exist = false)
    @ApiModelProperty(value = "关联公司名称")
    private String companyName;

    /** 关联供应商编码 */
    @ApiModelProperty(value = "关联供应商编码")
    private String vendorSid;

    /** 关联供应商名称 */
    @Excel(name = "关联供应商")
    @TableField(exist = false)
    @ApiModelProperty(value = "关联供应商名称")
    private String vendorName;

    /** 隶属上一级客户编码 */
    @ApiModelProperty(value = "隶属上一级客户编码")
    private String superiorCustomerSid;

    /** 隶属上一级客户名称 */
    @TableField(exist = false)
    @ApiModelProperty(value = "隶属上一级客户名称")
    private String superiorCustomerName;

    /** 法人/负责人姓名 */
    @ApiModelProperty(value = "法人/负责人姓名")
    private String ownerName;

    /** 统一信用代码证号/身份证号 */
    @ApiModelProperty(value = "统一信用代码证号/身份证号")
    @Length(max = 30,message = "统一信用代码证号/身份证号不能超过30个字符")
    private String creditCode;

    /** 原始客户编码 */
    @ApiModelProperty(value = "原始客户编码")
    private String originalCustomerSid;

    /** 营业执照-有效期从 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "营业执照-有效期从")
    private Date creditStartDate;

    /** 营业执照-有效期至 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "营业执照-有效期至")
    private Date creditEndDate;

    /** 所属国家编码 */
    @ApiModelProperty(value = "所属国家编码")
    private String country;

    /** 所属省份编码 */
    @ApiModelProperty(value = "所属省份编码")
    private String province;

    /** 所属城市编码 */
    @ApiModelProperty(value = "所属城市编码")
    private String city;

    /** 注册日期 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "注册日期")
    private Date registerDate;

    /** 注册资金（万元） */
    @ApiModelProperty(value = "注册资金（万元）")
    @Digits(integer=11,fraction = 4,message = "注册资金整数位上限为11位，小数位上限为4位")
    private BigDecimal registerCapital;

    /** 注册资金（币种） */
    @ApiModelProperty(value = "注册资金（币种）")
    private String registerCapitalCurrency;

    /** 网址 */
    @ApiModelProperty(value = "网址")
    @Length(max = 200,message = "网址不能超过200个字符")
    private String internetUrl;

    /** 纳税人识别号 */
    @ApiModelProperty(value = "纳税人识别号")
    @Length(max = 30,message = "纳税人识别号不能超过30个字符")
    private String taxpayerIdentifyNumber;

    /** 开票电话 */
    @ApiModelProperty(value = "开票电话")
    @Length(max = 30,message = "开票电话不能超过30个字符")
    private String invoiceTel;

    /** 开票地址 */
    @ApiModelProperty(value = "开票地址")
    @Length(max = 300,message = "开票地址不能超过300个字符")
    private String invoiceAddr;

    /** 办公地址 */
    @ApiModelProperty(value = "办公地址")
    @Length(max = 300,message = "办公地址不能超过300个字符")
    private String officeAddr;

    /** 营业范围 */
    @ApiModelProperty(value = "营业范围")
    @Length(max = 3000,message = "营业范围不能超过3000个字符")
    private String businessScope;

    /** 图片路径 */
    @ApiModelProperty(value = "图片路径")
    private String picturePath;

    @ApiModelProperty(value = "我方跟单员")
    private String buOperator;

    @TableField(exist = false)
    @ApiModelProperty(value = "我方跟单员")
    private String buOperatorList;

    /** 创建人账号 */
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    @Excel(name = "创建人")
    @TableField(exist = false)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccountName;

    /** 创建时间 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /** 更新人账号 */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccount;

    @Excel(name = "更改人")
    @TableField(exist = false)
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccountName;

    /** 更新时间 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "更改日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /** 确认人账号 */
    @ApiModelProperty(value = "确认人账号")
    private String confirmerAccount;

    /** 确认人账号 */
    @Excel(name = "确认人")
    @TableField(exist = false)
    @ApiModelProperty(value = "确认人账号")
    private String confirmerAccountName;

    /** 确认时间 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "确认日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /** 数据源系统 */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;

    /** 客户品牌*/
    @Valid
    @ApiModelProperty(value = "客户品牌List")
    @TableField(exist = false)
    private List<BasCustomerBrand> brandList;

    /** 客户品标*/
    @Valid
    @ApiModelProperty(value = "客户品标List")
    @TableField(exist = false)
    private transient List<BasCustomerBrandMark> markList;

    @Valid
    @ApiModelProperty(value = "客户联系方式信息List")
    @TableField(exist = false)
    private List<BasCustomerAddr> addrList;

    @ApiModelProperty(value = "附件清单")
    @TableField(exist = false)
    private List<BasCustomerAttach> attachmentList;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "开始时间")
    @TableField(exist = false)
    private String beginTime;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value ="结束时间")
    @TableField(exist = false)
    private String endTime;

    /** 批量启用停用传参 */
    @TableField(exist = false)
    private List<Long> customerSids;

    @TableField(exist = false)
    private Integer pageNum;

    @TableField(exist = false)
    private Integer pageSize;

    /** 客户组list */
    @TableField(exist = false)
    @ApiModelProperty(value = "客户组list")
    private String[] customerGroupList;

    /** 关联公司list */
    @ApiModelProperty(value = "关联公司list")
    @TableField(exist = false)
    private String[] relateCompanySidList;

    @ApiModelProperty(value = "处理状态list")
    @TableField(exist = false)
    private String[] handleStatusList;

    /** 客户类别list */
    @ApiModelProperty(value = "客户类别list")
    @TableField(exist = false)
    private String[] customerCategoryList;

    /** 客户性质list */
    @ApiModelProperty(value = "客户性质list")
    @TableField(exist = false)
    private String[] customerPropertyList;

    /** 关联供应商list */
    @ApiModelProperty(value = "关联供应商list")
    @TableField(exist = false)
    private String[] vendorSidList;
}
