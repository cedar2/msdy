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
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 店铺档案对象 s_bas_shop
 *
 * @author c
 * @date 2022-03-31
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_bas_shop")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasShop extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-店铺档案
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-店铺档案")
    private Long shopSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] shopSidList;
    /**
     * 店铺编码
     */
    @Excel(name = "店铺编码")
    @ApiModelProperty(value = "店铺编码")
    private String shopCode;

    /**
     * 店铺名称
     */
    @Length(max = 60, message = "店铺名称不能超过300个字符")
    @NotEmpty(message = "店铺名称不能为空")
    @Excel(name = "店铺名称")
    @ApiModelProperty(value = "店铺名称")
    private String shopName;

    /**
     * 所属公司sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "所属公司sid")
    private Long companySid;

    @TableField(exist = false)
    private Long[] companySidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司代码")
    private String companyCode;

    @Excel(name = "所属公司")
    @TableField(exist = false)
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司简称")
    private String companyShortName;

    /**
     * 分销商sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "分销商sid")
    private Long customerSid;

    @TableField(exist = false)
    private Long[] customerSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "分销商代码")
    private String customerCode;

    @Excel(name = "分销商")
    @TableField(exist = false)
    @ApiModelProperty(value = "分销商名称")
    private String customerName;

    @TableField(exist = false)
    @ApiModelProperty(value = "分销商简称")
    private String customerShortName;

    /**
     * 店铺类别（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "店铺类别", dictType = "s_shop_category")
    @ApiModelProperty(value = "店铺类别（数据字典的键值或配置档案的编码）")
    private String shopCategory;

    @TableField(exist = false)
    private String[] shopCategoryList;

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

    @TableField(exist = false)
    private String[] handleStatusList;

    /**
     * 经营形式（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "经营形式", dictType = "s_manage_style")
    @ApiModelProperty(value = "经营形式（数据字典的键值或配置档案的编码）")
    private String manageStyle;

    @TableField(exist = false)
    private String[] manageStyleList;

    /**
     * 店铺级别（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "店铺级别", dictType = "s_shop_level")
    @ApiModelProperty(value = "店铺级别（数据字典的键值或配置档案的编码）")
    private String shopLevel;

    @TableField(exist = false)
    private String[] shopLevelList;

    /**
     * 器架类型（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "器架类型", dictType = "s_hangrack_type")
    @ApiModelProperty(value = "器架类型（数据字典的键值或配置档案的编码）")
    private String hangrackType;

    @TableField(exist = false)
    private String[] hangrackTypeList;

    /**
     * 渠道类型（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "渠道类型", dictType = "s_channel_type")
    @ApiModelProperty(value = "渠道类型（数据字典的键值或配置档案的编码）")
    private String channelType;

    @TableField(exist = false)
    private String[] channelTypeList;

    /**
     * 渠道类别（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "渠道类别", dictType = "s_channel_category")
    @ApiModelProperty(value = "渠道类别（数据字典的键值或配置档案的编码）")
    private String channelCategory;

    /**
     * 所属区域sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "所属区域sid")
    private Long countryRegion;

    /**
     * 经度
     */
    @Length(max = 20, message = "经度不能超过20个字符")
    @Excel(name = "经度")
    @ApiModelProperty(value = "经度")
    private String longitude;

    /**
     * 纬度
     */
    @Length(max = 20, message = "纬度不能超过20个字符")
    @Excel(name = "纬度")
    @ApiModelProperty(value = "纬度")
    private String latitude;

    /**
     * 地址
     */
    @Excel(name = "地址")
    @ApiModelProperty(value = "地址")
    private String address;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 创建人账号（用户名称）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    /**
     * 创建人账号（用户名称）
     */
    @TableField(exist = false)
    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人账号（用户名称）")
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
     * 更新人账号（用户名称）
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

    /**
     * 更新人账号（用户名称）
     */
    @TableField(exist = false)
    @Excel(name = "更改人")
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccountName;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更改日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 确认人账号（用户名称）
     */
    @ApiModelProperty(value = "确认人账号（用户名称）")
    private String confirmerAccount;

    /**
     * 确认人账号（用户名称）
     */
    @TableField(exist = false)
    @Excel(name = "确认人")
    @ApiModelProperty(value = "确认人账号（用户名称）")
    private String confirmerAccountName;

    /**
     * 确认时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
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
    @ApiModelProperty(value = "店铺-联系方式信息")
    private List<BasShopAddr> addrList;

    @Valid
    @TableField(exist = false)
    @ApiModelProperty(value = "店铺-附件")
    private List<BasShopAttach> attachList;

}
