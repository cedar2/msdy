package com.platform.ems.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

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

import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

/**
 * 资产台账对象 s_ass_asset_record
 *
 * @author chenkw
 * @date 2022-03-01
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_ass_asset_record")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssAssetRecord extends EmsBaseEntity {
    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-资产台账
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-资产台账")
    private Long assetSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] assetSidList;

    /**
     * 资产编码（系统自动生成）
     */
    @Excel(name = "资产流水号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "资产编码（系统自动生成）")
    private Long assetCode;

    /**
     * 资产名称
     */
    @NotBlank(message = "资产名称不能为空")
    @Length(max = 200, message = "资产名称最大只支持输入200位")
    @Excel(name = "资产名称")
    @ApiModelProperty(value = "资产名称")
    private String assetName;

    /**
     * 资产类型（数据字典的键值或配置档案的编码）
     */
    @NotBlank(message = "资产类型不能为空")
    @Excel(name = "资产类型", dictType = "s_asset_type")
    @ApiModelProperty(value = "资产类型（数据字典的键值或配置档案的编码）")
    private String assetType;

    @TableField(exist = false)
    @Excel(name = "公司")
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司名称")
    private String companyShortName;

    /**
     * 资产管理员
     */
    @Length(max = 30, message = "资产管理员最大只支持输入30位")
    @Excel(name = "资产管理员")
    @ApiModelProperty(value = "资产管理员")
    private String assetAdministrator;


    /**
     * 资产卡片编号
     */
    @Length(max = 20, message = "资产卡片编号最大只支持输入20位")
    @Excel(name = "资产卡片编号")
    @ApiModelProperty(value = "资产卡片编号")
    private String assetCardNumber;

    /**
     * 资产数
     */
    @ApiModelProperty(value = "资产数")
    @TableField(exist = false)
    private String assetCount;

    /**
     * 系统SID-公司档案
     */
    @NotNull(message = "公司不能为空")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-公司档案")
    private Long companySid;

    @ApiModelProperty(value = "公司编码")
    private String companyCode;


    /**
     * 资产状态（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "资产状态", dictType = "s_asset_status")
    @ApiModelProperty(value = "资产状态（数据字典的键值或配置档案的编码）")
    private String assetState;

    /**
     * 销售方
     */
    @Length(max = 300, message = "销售方最大只支持输入300位")
    @Excel(name = "销售方")
    @ApiModelProperty(value = "销售方")
    private String seller;

    /**
     * 厂家
     */
    @Length(max = 300, message = "厂家最大只支持输入300位")
    @Excel(name = "厂家")
    @ApiModelProperty(value = "厂家")
    private String factory;

    /**
     * 品牌
     */
    @Length(max = 120, message = "品牌最大只支持输入120位")
    @Excel(name = "品牌")
    @ApiModelProperty(value = "品牌")
    private String brand;

    /**
     * 规格型号
     */
    @Length(max = 8, message = "规格型号最大只支持输入8位")
    @Excel(name = "规格型号")
    @ApiModelProperty(value = "规格型号")
    private String specification;

    /**
     * 采购人
     */
    @Length(max = 30, message = "采购人最大只支持输入30位")
    @Excel(name = "采购员")
    @ApiModelProperty(value = "采购人")
    private String buyer;

    /**
     * 采购日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "采购日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "采购日期")
    private Date purchaseDate;

    /**
     * 采购金额
     */
    @Digits(integer = 10, fraction = 5, message = "采购金额整数位上限为10位，小数位上限为5位")
    @Excel(name = "采购金额(元)")
    @ApiModelProperty(value = "采购金额")
    private BigDecimal currencyAmount;

    /**
     * 当前净值
     */
    @Digits(integer = 10, fraction = 5, message = "当前净值整数位上限为10位，小数位上限为5位")
    @Excel(name = "当前净值(元)")
    @ApiModelProperty(value = "当前净值")
    private BigDecimal currentNetValue;

    @Excel(name = "当前使用部门")
    @ApiModelProperty(value = "当前使用部门名称")
    private String departmentName;

    /**
     * 预计可使用年限（年）
     */
    @Excel(name = "预计可使用年限(年)")
    @Digits(integer = 4, fraction = 1, message = "预计可使用年限（年）整数位上限为4位，小数位上限为1位")
    @ApiModelProperty(value = "预计可使用年限（年）")
    private BigDecimal estimatedRemainYears;

    /**
     * 是否需保养（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "是否需保养", dictType = "s_yesno_flag")
    @ApiModelProperty(value = "是否需保养（数据字典的键值或配置档案的编码）")
    private String isNeedMaintenance;

    /**
     * 保养周期（天）
     */
    @Excel(name = "保养周期(天)")
    @Digits(integer = 4, fraction = 1, message = "保养周期（天）整数位上限为4位，小数位上限为1位")
    @ApiModelProperty(value = "保养周期（天）")
    private BigDecimal maintenanceCycle;

    /**
     * 最近保养日期
     */
    @Excel(name = "最近保养日期", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "最近保养日期")
    private Date lastestMaintenanceDate;


    /**
     * 年折旧率（存值，即：不含百分号，如20%，就存0.2）
     */
    @Digits(integer = 3, fraction = 4, message = "年折旧率整数位上限为3位，小数位上限为4位")
    @ApiModelProperty(value = "年折旧率（存值，即：不含百分号，如20%，就存0.2）")
    private BigDecimal annualDepreciationRate;

    @TableField(exist = false)
    @ApiModelProperty(value = "年折旧率（带百分号）")
    private String annualDepreciationRateString;

    /**
     * 投用日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "投用日期")
    private Date enableDate;

    /**
     * 报废/处置日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "报废/处置日期")
    private Date scrapDate;

    /**
     * 资产来源（数据字典的键值或配置档案的编码）s_asset_source
     */
    @ApiModelProperty(value = "资产来源（数据字典的键值或配置档案的编码）")
    private String assetSource;

    /**
     * 计量单位（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "计量单位（数据字典的键值或配置档案的编码）")
    private String unitBase;

    @TableField(exist = false)
    @ApiModelProperty(value = "计量单位（数据字典的键值或配置档案的编码）")
    private String unitBaseName;

    /**
     * 数量
     */
    @NotNull(message = "数量不能为空")
    @Digits(integer = 5, fraction = 0, message = "数量最大只支持输入5位")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "数量")
    private Long quantity;

    /**
     * 当前使用部门sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "当前使用部门sid")
    private Long departmentSid;

    @ApiModelProperty(value = "当前使用部门编码")
    private String departmentCode;

    @ApiModelProperty(value = "资产图片")
    private String picturePath;

    @TableField(exist = false)
    @ApiModelProperty(value = "资产图片")
    private String[] picturePathList;

    /**
     * 投用时已用年限（年）
     */
    @Digits(integer = 4, fraction = 1, message = "投用时已用年限（年）整数位上限为4位，小数位上限为1位")
    @ApiModelProperty(value = "投用时已用年限（年）")
    private BigDecimal hasUsedYears;

    /**
     * 使用时间单位（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "使用时间单位（数据字典的键值或配置档案的编码）")
    private String useTimeUnit;

    /**
     * 保养周期时间单位（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "保养周期时间单位（数据字典的键值或配置档案的编码）")
    private String maintenanceCycleUnit;

    /**
     * 保养等级（数据字典的键值或配置档案的编码）s_maintenance_level
     */
    @ApiModelProperty(value = "保养等级（数据字典的键值或配置档案的编码）")
    private String maintenanceLevel;

    /**
     * 是否需巡检（数据字典的键值或配置档案的编码）s_yesno_flag
     */
    @ApiModelProperty(value = "是否需巡检（数据字典的键值或配置档案的编码）")
    private String isNeedInspection;

    /**
     * 巡检周期（天）
     */
    @Digits(integer = 4, fraction = 1, message = "巡检周期（天）整数位上限为4位，小数位上限为1位")
    @Excel(name = "巡检周期(天)")
    @ApiModelProperty(value = "巡检周期（天）")
    private BigDecimal inspectionCycle;

    /**
     * 巡检周期时间单位（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "巡检周期时间单位（数据字典的键值或配置档案的编码）")
    private String inspectionCycleTimeUnit;

    /**
     * 最近巡检日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "最近巡检日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "最近巡检日期")
    private Date lastestInspectionDate;

    /**
     * 最近巡检结果（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "最近巡检结果（数据字典的键值或配置档案的编码）")
    private String lastestInspectionOutcome;

    /**
     * 币种（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "币种（数据字典的键值或配置档案的编码）")
    private String currency;

    /**
     * 货币单位（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "货币单位（数据字典的键值或配置档案的编码）")
    private String currencyUnit;

    /**
     * 接收人/验收人
     */
    @Length(max = 30, message = "接收人/验收人最大只支持输入30位")
    @ApiModelProperty(value = "接收人/验收人")
    private String recipient;

    /**
     * 存放位置
     */
    @Length(max = 600, message = "存放位置最大只支持输入600位")
    @Excel(name = "存放位置")
    @ApiModelProperty(value = "存放位置")
    private String location;

    /**
     * 供货方联系方式（联系人/电话/地址）
     */
    @Length(max = 600, message = "供货方联系方式最大只支持输入600位")
    @ApiModelProperty(value = "供货方联系方式（联系人/电话/地址）")
    private String supplierPhone;

    /**
     * 厂家联系方式（联系人/电话/地址）
     */
    @Length(max = 600, message = "厂家联系方式最大只支持输入600位")
    @ApiModelProperty(value = "厂家联系方式（联系人/电话/地址）")
    private String factoryPhone;

    @TableField(exist = false)
    @ApiModelProperty(value = "当前审批节点名称")
    private String approvalNode;

    @TableField(exist = false)
    @ApiModelProperty(value = "当前审批人")
    private String approvalUserName;

    /**
     * 处理状态
     */
    @NotBlank(message = "处理状态不能为空")
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 创建人账号
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号")
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
     * 更改人账号
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更改人账号")
    private String updaterAccount;

    @TableField(exist = false)
    private String updaterAccountName;

    /**
     * 更改时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd hh:mm:ss")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更改时间")
    private Date updateDate;

    /**
     * 确认人账号
     */
    @ApiModelProperty(value = "确认人账号")
    private String confirmerAccount;

    @TableField(exist = false)
    private String confirmerAccountName;

    /**
     * 确认时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd hh:mm:ss")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;

    @Valid
    @TableField(exist = false)
    @ApiModelProperty(value = "附件清单")
    private List<AssAssetRecordAttach> attachmentList;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态(多选)")
    private String[] handleStatusList;

    @TableField(exist = false)
    @ApiModelProperty(value = "资产类型(多选)")
    private String[] assetTypeList;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-公司档案(多选)")
    private Long[] companySidList;


    /**
     * 是否是导入
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "是否为导入")
    private String importStatus;
}
