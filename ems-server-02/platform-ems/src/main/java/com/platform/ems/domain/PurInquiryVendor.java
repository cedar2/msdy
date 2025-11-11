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

/**
 * 物料询价单-供应商对象 s_pur_inquiry_vendor
 *
 * @author chenkw
 * @date 2022-03-21
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_pur_inquiry_vendor")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PurInquiryVendor extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-物料询价单供应商信息
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料询价单供应商信息")
    private Long inquiryVendorSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "sid数组")
    private Long[] inquiryVendorSidList;

    /**
     * 系统SID-物料询价单
     */
    @Excel(name = "系统SID-物料询价单")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料询价单")
    private Long inquirySid;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料询价单号")
    private Long inquiryCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-物料询价单数组")
    private Long[] inquirySidList;

    /**
     * 系统SID-供应商档案
     */
    @Excel(name = "系统SID-供应商档案")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商档案")
    private Long vendorSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-供应商档案数组")
    private Long[] vendorSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "供应商档案编码")
    private Long vendorCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "供应商档案名称")
    private String vendorName;

    @TableField(exist = false)
    @ApiModelProperty(value = "供应商档案简称")
    private String shortName;

    /**
     * 创建人账号（用户名称）
     */
    @Excel(name = "创建人账号（用户名称）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    @TableField(exist = false)
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
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "数据源系统（数据字典的键值或配置档案的编码）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "报价单号")
    private Long quoteBargainCode;

    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "报价日期")
    private Date dateQuote;

    /*
    未报价(WBJ)
    报价中(BJZ）
    已报价(YBJ)
    采用中文
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "报价状态")
    private String quoteStatus;

}
