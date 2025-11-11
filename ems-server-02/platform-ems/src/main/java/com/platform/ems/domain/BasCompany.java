package com.platform.ems.domain;

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
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 公司档案对象 s_bas_company
 *
 * @author linhongwei
 * @date 2021-03-19
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_bas_company")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasCompany extends EmsBaseEntity implements Serializable {


    @ApiModelProperty(value = "每页个数")
    @TableField(exist = false)
    private Integer pageNum;

    @ApiModelProperty(value = "每页个数")
    @TableField(exist = false)
    private Integer pageSize;

    private static final long serialVersionUID = 1L;

    /**
     * 客户端口号
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /**
     * 系统ID-公司档案
     */

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-公司档案")
    @TableId
    private Long companySid;

    public void setCompanyCode(String companyCode) {
        if (StrUtil.isNotBlank(companyCode)) {
            companyCode = companyCode.replaceAll("\\s*", "");
        }
        this.companyCode = companyCode;
    }

    public void setCompanyName(String companyName) {
        if (StrUtil.isNotBlank(companyName)) {
            companyName = companyName.trim();
        }
        this.companyName = companyName;
    }

    public void setShortName(String shortName) {
        if (StrUtil.isNotBlank(shortName)) {
            shortName = shortName.trim();
        }
        this.shortName = shortName;
    }

    public void setOwnerName(String ownerName) {
        if (StrUtil.isNotBlank(ownerName)) {
            ownerName = ownerName.trim();
        }
        this.ownerName = ownerName;
    }

    /**
     * 公司代码
     */
    @Excel(name = "公司代码")
    @NotBlank(message = "公司代码不能为空")
    @Length(max = 8, message = "公司代码长度不能超过8位")
    @ApiModelProperty(value = "公司代码")
    private String companyCode;

    /**
     * 公司名称
     */
    @NotBlank(message = "公司名称不能为空")
    @Length(max = 300, message = "公司名称长度不能超过300位")
    @Excel(name = "公司名称")
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    /**
     * 公司名称
     */
    @NotBlank(message = "公司简称不能为空")
    @Length(max = 300, message = "公司简称长度不能超过300位")
    @Excel(name = "公司简称")
    @ApiModelProperty(value = "公司简称")
    private String shortName;

    /**
     * 官方网站
     */
    @Length(max = 200, message = "官方网站长度不能超过200位")
    @ApiModelProperty(value = "官方网站")
    private String internetUrl;

    /**
     * 法人/负责人姓名
     */
    @NotBlank(message = "法人/负责人不能为空")
    @Excel(name = "法人/负责人姓名")
    @ApiModelProperty(value = "法人/负责人姓名")
    private String ownerName;

    /**
     * 公司类型的编码
     */
    @Excel(name = "类型", dictType = "s_company_type")
    @ApiModelProperty(value = "公司类型的编码")
    private String companyType;

    /**
     * 启用/停用状态
     */
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "启用/停用", dictType = "s_valid_flag")
    @ApiModelProperty(value = "启用/停用状态")
    private String status;

    @ApiModelProperty(value = "停用说明")
    private String disableRemark;

    /**
     * 处理状态
     */
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    /**
     * 公司类别的编码
     */
    @ApiModelProperty(value = "公司类别的编码")
    private String companyCategory;

    /**
     * 统一信用代码证号
     */
    @Excel(name = "统一信用代码证号")
    @Length(max = 30, message = "统一信用代码证号长度不能超过30位")
    @ApiModelProperty(value = "统一信用代码证号")
    private String creditCode;

    /**
     * 营业执照-有效期从
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "执照有效期起", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "营业执照-有效期从")
    private Date creditStartDate;

    /**
     * 营业执照-有效期至
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "执照有效期至", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "营业执照-有效期至")
    private Date creditEndDate;

    /**
     * 开票电话
     */
    @Excel(name = "开票电话")
    @Length(max = 30, message = "开票电话长度不能超过30位")
    @ApiModelProperty(value = "开票电话")
    private String invoiceTel;

    /**
     * 办公地址
     */
    @Excel(name = "办公地址")
    @Length(max = 300, message = "办公地址长度不能超过300位")
    @ApiModelProperty(value = "办公地址")
    private String officeAddr;

    /**
     * 开票地址
     */
    @Excel(name = "开票地址")
    @Length(max = 300, message = "开票地址长度不能超过300位")
    @ApiModelProperty(value = "开票地址")
    private String invoiceAddr;

    /**
     * 注册地址
     */
    @Excel(name = "注册地址")
    @Length(max = 300, message = "注册地址长度不能超过300位")
    @ApiModelProperty(value = "注册地址")
    private String registerAddr;

    /**
     * 营业范围
     */
    @Length(max = 3000, message = "营业范围长度不能超过3000位")
    @ApiModelProperty(value = "营业范围")
    private String businessScope;

    /**
     * 图片路径
     */
    @ApiModelProperty(value = "图片路径")
    private String picturePath;

    /**
     * 纳税人识别号
     */
    @Length(max = 30, message = "纳税人识别号长度不能超过30位")
    @ApiModelProperty(value = "纳税人识别号")
    private String taxpayerIdentifyNumber;

    /**
     * 创建人账号
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    /**
     * 创建人账号
     */
    @TableField(exist = false)
    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人名称")
    private String creatorAccountName;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss:sss")
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

    /**
     * 更新人账号
     */
    @Excel(name = "更改人")
    @TableField(exist = false)
    @ApiModelProperty(value = "更新人名称")
    private String updaterAccountName;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss:sss")
    @Excel(name = "更改日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 确认人账号
     */
    @ApiModelProperty(value = "确认人账号")
    private String confirmerAccount;

    /**
     * 确认人账号
     */
    @TableField(exist = false)
    @Excel(name = "确认人")
    @ApiModelProperty(value = "确认人名称")
    private String confirmerAccountName;

    /**
     * 确认时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss:sss")
    @Excel(name = "确认日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 数据源系统
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;

    @ApiModelProperty(value = "公司数组")
    @TableField(exist = false)
    private String[] companySidList;

    @ApiModelProperty(value = "类型数组")
    @TableField(exist = false)
    private String[] companyTypeList;

    @ApiModelProperty(value = "类别数组")
    @TableField(exist = false)
    private String[] companyCategoryList;

    @ApiModelProperty(value = "处理状态数组")
    @TableField(exist = false)
    private String[] handleStatusList;

    @TableField(exist = false)
    private List<Long> companySidLists;

    @TableField(exist = false)
    private String button;

    @Valid
    @TableField(exist = false)
    private transient List<BasCompanyBrand> companyBrandList;

    @Valid
    @TableField(exist = false)
    private transient List<BasCompanyBrandMark> companyBrandMarkList;

    @Valid
    @TableField(exist = false)
    @ApiModelProperty(value = "供应商财务信息List基本户")
    private List<BasCompanyBankAccount> baseBankAccountList;

    @Valid
    @TableField(exist = false)
    @ApiModelProperty(value = "供应商财务信息List一般户")
    private List<BasCompanyBankAccount> basCompanyBankAccountList;

    @ApiModelProperty(value = "附件清单")
    @TableField(exist = false)
    private List<BasCompanyAttach> attachmentList;


}
