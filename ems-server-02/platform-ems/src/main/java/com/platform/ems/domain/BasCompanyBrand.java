package com.platform.ems.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * 公司-品牌信息对象 s_bas_company_brand
 *
 * @author linhongwei
 * @date 2021-03-22
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_bas_company_brand")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasCompanyBrand extends EmsBaseEntity implements Serializable {


    private static final long serialVersionUID = 1905122041950251207L;
    /**
     * 系统SID-公司品牌信息sid
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-公司品牌信息sid")
    private Long companyBrandSid;

    /**
     * 公司编码（公司档案的sid）
     */
    @Excel(name = "公司编码（公司档案的sid）")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司编码（公司档案的sid）")
    private Long companySid;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司sid多选")
    private Long[] companySidList;

    public void setBrandCode(String brandCode) {
        if (StrUtil.isNotBlank(brandCode)){
            brandCode = brandCode.replaceAll("\\s*", "");
        }
        this.brandCode = brandCode;
    }

    public void setBrandName(String brandName) {
        if (StrUtil.isNotBlank(brandName)){
            brandName = brandName.trim();
        }
        this.brandName = brandName;
    }

    /**
     * 品牌编码
     */
    @Excel(name = "品牌编码")
    @NotBlank(message = "品牌编码不能为空")
    @Length(max = 8, message = "品牌编码长度不能超过8位")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "品牌编码")
    private String brandCode;

    /**
     * 品牌名称
     */
    @Excel(name = "品牌名称")
    @NotBlank(message = "品牌名称不能为空")
    @Length(max = 120, message = "品牌名称长度不能超过120位")
    @ApiModelProperty(value = "品牌名称")
    private String brandName;

    /**
     * 启用/停用状态（数据字典的键值）
     */
    @Excel(name = "启用/停用状态（数据字典的键值）")
    @ApiModelProperty(value = "启用/停用状态（数据字典的键值）")
    private String status;

    /**
     * 创建人账号（用户名称）
     */
    @Excel(name = "创建人账号（用户名称）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    @TableField(exist = false)
    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人名称")
    private String creatorAccountName;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss:sss")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss:sss")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /**
     * 更新人账号（用户名称）
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss:sss")
    @Excel(name = "更新人账号（用户名称）", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss:sss")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss:sss")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss:sss")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 数据源系统（数据字典的键值）
     */
    @Excel(name = "数据源系统（数据字典的键值）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值）")
    private String dataSourceSys;

    private String remark;

    /**
     * 客户端口号
     */
    @Excel(name = "客户端口号")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    @TableField(exist = false)
    private String[] companyBrandSidList;

    @ApiModelProperty(value ="品牌品标组合信息列表")
    @TableField(exist = false)
    private transient List<BasCompanyBrandMarkGroup> groupList;

}
