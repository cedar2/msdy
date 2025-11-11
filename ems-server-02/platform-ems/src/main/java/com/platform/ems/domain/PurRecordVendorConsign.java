package com.platform.ems.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.core.domain.BaseEntity;
import com.platform.common.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableId;
import java.util.List;
import com.platform.common.core.domain.EmsBaseEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import com.platform.common.core.domain.document.UserOperLog;
import lombok.Data;
import javax.validation.constraints.NotEmpty;
import lombok.experimental.Accessors;

/**
 * s_pur_record_vendor_consign对象 s_pur_record_vendor_consign
 *
 * @author linhongwei
 * @date 2021-06-23
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_pur_record_vendor_consign")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PurRecordVendorConsign extends EmsBaseEntity{

    /** 租户ID */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /** 系统SID-供应商寄售待结算台账 */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商寄售待结算台账")
    private Long recordVendorConsignSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long [] recordVendorConsignSidList;
    /** 系统SID-商品&物料&服务 */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品&物料&服务")
    private Long materialSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "供应商sid")
    private Long vendorSid;

    /** 系统SID-物料&商品sku1 */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品sku1")
    private Long sku1Sid;

    /** 系统SID-物料&商品sku2 */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品sku2")
    private Long sku2Sid;

    /** 系统SID-商品条码（物料&商品&服务） */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品条码（物料&商品&服务）")
    private Long barcodeSid;

    @TableField(exist = false)
    @Excel(name = "供应商编码")
    @ApiModelProperty(value = "供应商编码")
    private String vendorCode;

    @TableField(exist = false)
    @Excel(name = "供应商名称")
    @ApiModelProperty(value = "供应商名称")
    private String vendorName;

    @Excel(name = "物料/商品编码")
    @TableField(exist = false)
    @ApiModelProperty(value ="物料/商品编码")
    private String materialCode;

    /** 物料（商品/服务）名称 */
    @Excel(name = "物料/商品名称")
    @ApiModelProperty(value = "物料（商品/服务）名称")
    private String materialName;

    @TableField(exist = false)
    private String sku1Code;

    @Excel(name = "SKU1名称")
    @TableField(exist = false)
    private String sku1Name;

    @TableField(exist = false)
    private String sku2Code;

    @Excel(name = "SKU2名称")
    @TableField(exist = false)
    private String sku2Name;

    /** 待结算量 */
    @Excel(name = "待结算量")
    @ApiModelProperty(value = "待结算量")
    private BigDecimal quantity;

    /**
     * 计量单位名称
     */
    @TableField(exist = false)
    @Excel(name = "基本计量单位")
    @ApiModelProperty(value = "计量单位名称")
    private String unitBaseName;

    /** 基本计量单位（数据字典的键值或配置档案的编码） */
    @ApiModelProperty(value = "基本计量单位（数据字典的键值或配置档案的编码）")
    private String unitBase;

    /** 采购价计量单位（数据字典的键值或配置档案的编码） */
    @ApiModelProperty(value = "采购价计量单位（数据字典的键值或配置档案的编码）")
    private String purchaseUnit;

    /** 单位换算比例（采购价单位/基本单位） */
    @ApiModelProperty(value = "单位换算比例（采购价单位/基本单位）")
    private BigDecimal unitConversionRate;

    /** 行号 */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "行号")
    private Long itemNum;

    /** 创建人账号（用户名称） */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    /** 创建时间 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /** 更新人账号（用户名称） */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

    /** 更新时间 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /** 确认人账号（用户名称） */
    @ApiModelProperty(value = "确认人账号（用户名称）")
    private String confirmerAccount;

    /** 确认时间 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /** 数据源系统（数据字典的键值或配置档案的编码） */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    @TableField(exist = false)
    @ApiModelProperty(value = "类型 1出库 2入库")
    private String type;

    @TableField(exist = false)
    @ApiModelProperty(value = "供应商sid")
    private Long[] vendorSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料类型")
    private String[] materialTypeList;

    @TableField(exist = false)
    @ApiModelProperty(value = "跳过校验")
    private String isSkipInsert;
}
