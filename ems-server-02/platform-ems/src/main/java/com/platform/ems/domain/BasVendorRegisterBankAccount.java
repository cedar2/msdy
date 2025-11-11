package com.platform.ems.domain;

import java.util.Date;

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
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * 供应商注册-银行账户信息对象 s_bas_vendor_register_bank_account
 *
 * @author chenkw
 * @date 2022-02-21
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_bas_vendor_register_bank_account")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasVendorRegisterBankAccount extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-供应商注册银行账户信息
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商注册银行账户信息")
    private Long vendorRegisterBankAccountSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] vendorRegisterBankAccountSidList;

    /**
     * 系统SID-供应商注册基本信息
     */
    @Excel(name = "系统SID-供应商注册基本信息")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商注册基本信息")
    private Long vendorRegisterSid;

    /**
     * 账户类型（数据字典的键值或配置档案的编码）
     */
    @NotBlank(message = "账户类型不能为空")
    @Excel(name = "账户类型", dictType = "s_bank_account_type")
    @ApiModelProperty(value = "账户类型（数据字典的键值）")
    private String accountType;

    /**
     * 银行名称
     */
    @NotBlank(message = "银行名称不能为空")
    @Length(max = 180,message = "银行名称最大只支持输入180位")
    @Excel(name = "银行名称")
    @ApiModelProperty(value = "银行名称")
    private String bankName;

    /**
     * 银行账号
     */
    @NotBlank(message = "银行账号不能为空")
    @Length(max = 60,message = "银行账号最大只支持输入60位")
    @Excel(name = "银行账号")
    @ApiModelProperty(value = "银行账号")
    private String bankAccount;

    /**
     * 收款方名称
     */
    @NotBlank(message = "收款方名称不能为空")
    @Length(max = 180,message = "收款方名称最大只支持输入180位")
    @Excel(name = "收款方名称")
    @ApiModelProperty(value = "收款方名称")
    private String bankAccountName;

    /**
     * 分行名称
     */
    @Length(max = 180,message = "分行名称最大只支持输入180位")
    @Excel(name = "分行名称")
    @ApiModelProperty(value = "分行名称")
    private String bankBranchName;

    /**
     * 分行编号
     */
    @Length(max = 30,message = "分行编号最大只支持输入30位")
    @Excel(name = "分行编号")
    @ApiModelProperty(value = "分行编号")
    private String bankBranchCode;

    /**
     * 所属区域-国家区域sid
     */
    @Excel(name = "所属区域-国家区域sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "所属区域-国家区域sid")
    private Long countryRegion;

    /**
     * 图片路径
     */
    @Excel(name = "图片路径")
    @ApiModelProperty(value = "图片路径")
    private String picturePath;

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
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd hh:mm:ss")
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
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd hh:mm:ss")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "数据源系统（数据字典的键值或配置档案的编码）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

}
