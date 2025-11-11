package com.platform.ems.domain;

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

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * 公司银行账户信息对象 s_bas_company_bank_account
 *
 * @author qhq
 * @date 2024-04-29
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_bas_company_bank_account")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasCompanyBankAccount extends EmsBaseEntity {

    /** 客户端口号 */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /** 系统ID-公司银行账户信息 */
    @Excel(name = "系统ID-公司银行账户信息")
    @ApiModelProperty(value = "系统ID-公司银行账户信息")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableId
    private Long companyBankAccountSid;

    /** 系统ID-公司档案 */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-公司档案")
    private Long companySid;

    /** 账户类型编码 */
    @Excel(name = "账户类型编码")
    @ApiModelProperty(value = "账户类型编码")
    private String accountType;

    /** 银行名称 */
    @Excel(name = "银行名称")
    @ApiModelProperty(value = "银行名称")
    private String bankName;

    /** 分行名称 */
    @Excel(name = "分行名称")
    @ApiModelProperty(value = "分行名称")
    private String bankBranchName;

    /** 分行编号 */
    @Excel(name = "分行编号")
    @ApiModelProperty(value = "分行编号")
    private String bankBranchCode;

    /** 分行所属国家编码 */
    @Excel(name = "分行所属国家编码")
    @ApiModelProperty(value = "分行所属国家编码")
    private String country;

    /** 分行所属省份编码 */
    @Excel(name = "分行所属省份编码")
    @ApiModelProperty(value = "分行所属省份编码")
    private String province;

    /** 分行所属城市编码 */
    @Excel(name = "分行所属城市编码")
    @ApiModelProperty(value = "分行所属城市编码")
    private String city;

    /** 所属区域-国家区域sid */
    @Excel(name = "所属区域-国家区域sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "所属区域-国家区域sid")
    private Long countryRegion;

    /** 银行账号 */
    @Excel(name = "银行账号")
    @ApiModelProperty(value = "银行账号")
    private String bankAccount;

    /** 收款方名称 */
    @Excel(name = "收款方名称")
    @ApiModelProperty(value = "收款方名称")
    private String bankAccountName;

    /** 图片路径 */
    @Excel(name = "图片路径")
    @ApiModelProperty(value = "图片路径")
    private String picturePath;

    /** 启用/停用状态 */
    @Excel(name = "启用/停用状态")
    @ApiModelProperty(value = "启用/停用状态")
    private String status;

    /** 处理状态 */
    @Excel(name = "处理状态")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    /** 创建人账号 */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    @Excel(name = "创建人")
    @TableField(exist = false)
    @ApiModelProperty(value = "创建人昵称")
    private String creatorAccountName;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /** 更新人账号 */
    @Excel(name = "更新人账号")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccount;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /** 数据源系统 */
    @Excel(name = "数据源系统")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;

    @ApiModelProperty(value = "备注")
    private String remark;
}
