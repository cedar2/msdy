package com.platform.ems.domain;

import java.util.Date;

import cn.hutool.core.util.StrUtil;
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

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

/**
 * 客户-客方品标信息对象 s_bas_customer_brand_mark
 *
 * @author linhongwei
 * @date 2021-06-24
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_bas_customer_brand_mark")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasCustomerBrandMark extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-客方品标信息
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-客方品标信息")
    private Long brandMarkSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] brandMarkSidList;

    /**
     * 系统SID-客户档案
     */
    @Excel(name = "系统SID-客户档案")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-客户档案")
    private Long customerSid;

    public void setBrandMarkCode(String brandMarkCode) {
        if (StrUtil.isNotBlank(brandMarkCode)){
            brandMarkCode = brandMarkCode.replaceAll("\\s*", "");
        }
        this.brandMarkCode = brandMarkCode;
    }

    public void setBrandMarkName(String brandMarkName) {
        if (StrUtil.isNotBlank(brandMarkName)){
            brandMarkName = brandMarkName.trim();
        }
        this.brandMarkName = brandMarkName;
    }

    /**
     * 品标编码（人工编码）
     */
    @Excel(name = "品标编码（人工编码）")
    @NotBlank(message = "品标编码不能为空")
    @Length(max = 8 , message = "品标编码长度不能超过8位")
    @ApiModelProperty(value = "品标编码（人工编码）")
    private String brandMarkCode;

    /**
     * 品标名称
     */
    @Excel(name = "品标名称")
    @Length(max = 120 , message = "品标名称长度不能超过120位")
    @NotBlank(message = "品标名称不能为空")
    @ApiModelProperty(value = "品标名称")
    private String brandMarkName;

    /**
     * 状态
     */
    @NotNull(message = "状态不能为空")
    @Excel(name = "状态", dictType = "s_valid_flag")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "状态")
    private Long status;

    /**
     * 创建人账号（用户名称）
     */
    @Excel(name = "创建人账号（用户名称）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    @TableField(exist = false)
    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人账号（用户名称）")
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
     * 更新人账号（用户名称）
     */
    @Excel(name = "更新人账号（用户名称）")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
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
