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

import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.List;

/**
 * 供应商返修台账对象 s_inv_record_vendor_repair
 *
 * @author linhongwei
 * @date 2021-10-27
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_inv_record_vendor_repair")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InvRecordVendorRepair extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-供应商返修台账
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商返修台账")
    private Long vendorRepairSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] vendorRepairSidList;
    /**
     * 供应商返修台账流水号
     */
    @Excel(name = "供应商返修台账流水号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "供应商返修台账流水号")
    private Long vendorRepairCode;

    /**
     * 系统SID-供应商信息
     */
    @Excel(name = "系统SID-供应商信息")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商信息")
    private Long vendorSid;

    /**
     * 系统SID-公司档案
     */
    @Excel(name = "系统SID-公司档案")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-公司档案")
    private Long companySid;

    /**
     * 单据日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "单据日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "单据日期")
    private Date documentDate;

    /**
     * 单据类型编码code
     */
    @Excel(name = "单据类型编码code")
    @ApiModelProperty(value = "单据类型编码code")
    private String documentType;

    /**
     * 业务类型编码code
     */
    @Excel(name = "业务类型编码code")
    @ApiModelProperty(value = "业务类型编码code")
    private String businessType;

    /**
     * 送货单号
     */
    @Excel(name = "送货单号")
    @ApiModelProperty(value = "送货单号")
    private String carrierNoteCode;

    /**
     * 产品季sid
     */
    @Excel(name = "产品季sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "产品季sid")
    private Long productSeasonSid;

    /**
     * 处理状态（数据字典的键值或配置档案的编码）
     */
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "处理状态（数据字典的键值或配置档案的编码）", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    /**
     * 创建人账号（用户账号）
     */
    @Excel(name = "创建人账号（用户账号）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户账号）")
    private String creatorAccount;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /**
     * 更新人账号（用户账号）
     */
    @Excel(name = "更新人账号（用户账号）")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户账号）")
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
     * 确认人账号（用户账号）
     */
    @Excel(name = "确认人账号（用户账号）")
    @ApiModelProperty(value = "确认人账号（用户账号）")
    private String confirmerAccount;

    /**
     * 确认时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "确认时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "数据源系统（数据字典的键值或配置档案的编码）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    @TableField(exist = false)
    List<InvRecordVendorRepairItem> ListItem;

    @TableField(exist = false)
    List<InvRecordVendorRepairAttach> ListAttach;

    @ApiModelProperty(value = "查询：供应商")
    @TableField(exist = false)
    private Long[] vendorSidList;

    @ApiModelProperty(value = "查询：公司")
    @TableField(exist = false)
    private Long[] companySidList;

    @ApiModelProperty(value = "查询：产品季")
    @TableField(exist = false)
    private Long[] productSeasonSidList;

    @ApiModelProperty(value = "查询：处理状态")
    @TableField(exist = false)
    private String[] handleStatusList;

    @ApiModelProperty(value = "公司")
    @TableField(exist = false)
    private String companyName;

    @ApiModelProperty(value = "供应商")
    @TableField(exist = false)
    private String vendorName;

    @ApiModelProperty(value = "产品季")
    @TableField(exist = false)
    private String productSeasonName;

    @ApiModelProperty(value = "创建人")
    @TableField(exist = false)
    private String creatorAccountName;

    @ApiModelProperty(value = "变更人")
    @TableField(exist = false)
    private String updaterAccountName;

    @ApiModelProperty(value = "确认人")
    @TableField(exist = false)
    private String confirmerAccountName;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品条码（物料&商品）")
    private Long barcodeSid;

    @TableField(exist = false)
    @ApiModelProperty(value ="物料名称")
    private String materialName;

}
