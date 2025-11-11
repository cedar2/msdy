package com.platform.ems.domain;

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

import javax.validation.constraints.NotEmpty;

/**
 * 客户-客方品牌信息对象 s_bas_customer_brand
 *
 * @author qhq
 * @date 2021-03-24
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_bas_customer_brand")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasCustomerBrand extends EmsBaseEntity {

    /** 客户端口号 */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /** 系统ID-客户品牌信息 */
    @Excel(name = "系统ID-客户品牌信息")
    @ApiModelProperty(value = "系统ID-客户品牌信息")
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    private Long customerBrandSid;

    /** 系统ID-客户档案 */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-客户档案")
    private Long customerSid;

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

    /** 客方品牌编码 */
    @Length(max = 8,message = "客户品牌编码不能超过8个字符")
    @ApiModelProperty(value = "客方品牌编码")
    @NotEmpty(message = "客方品牌编码不能为空")
    private String brandCode;

    /** 客方品牌名称 */
    @Excel(name = "客方品牌名称")
    @Length(max = 120,message = "客户品牌名称不能超过120个字符")
    @ApiModelProperty(value = "客方品牌名称")
    @NotEmpty(message = "客方品牌名称不能为空")
    private String brandName;

    /** 创建人账号 */
    @Excel(name = "创建人账号")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    @TableField(exist = false)
    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccountName;

    /** 创建时间 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
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
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /** 数据源系统 */
    @Excel(name = "数据源系统")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;


    private String remark;

    private String status;

    @ApiModelProperty(value ="品牌品标组合信息列表")
    @TableField(exist = false)
    private List<BasCustomerBrandMarkGroup> groupList;

}
