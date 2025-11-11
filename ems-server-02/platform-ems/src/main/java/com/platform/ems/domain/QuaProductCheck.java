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

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.List;

/**
 * 成衣检测单-主对象 s_qua_product_check
 *
 * @author linhongwei
 * @date 2022-04-13
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_qua_product_check")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuaProductCheck extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-成衣检测单
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-成衣检测单")
    private Long productCheckSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] productCheckSidList;
    /**
     * 成衣检测单号
     */
    @Excel(name = "成衣检测单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "成衣检测单号")
    private Long productCheckCode;

    /**
     * 产品季sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "产品季sid")
    private Long productSeasonSid;

    @TableField(exist = false)
    private Long[] productSeasonSidList;

    /**
     * 产品季编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "产品季编码")
    private String productSeasonCode;

    /**
     * 产品季名称
     */
    @TableField(exist = false)
    @Excel(name = "产品季")
    @ApiModelProperty(value = "产品季名称")
    private String productSeasonName;

    /**
     * 处理状态（数据字典的键值或配置档案的编码）
     */
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    @TableField(exist = false)
    private String[] handleStatusList;

    /**
     * 款号信息
     */
    @TableField(exist = false)
    @Excel(name = "款号信息")
    @ApiModelProperty(value = "款号信息")
    private String styleInfo;

    /**
     * 样品类别（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "样品类别", dictType = "s_sample_category")
    @ApiModelProperty(value = "样品类别（数据字典的键值或配置档案的编码）")
    private String sampleCategory;

    @TableField(exist = false)
    private String[] sampleCategoryList;

    /**
     * 检测类别（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "检测类别", dictType = "s_check_category")
    @ApiModelProperty(value = "检测类别（数据字典的键值或配置档案的编码）")
    private String checkCategory;

    @TableField(exist = false)
    private String[] checkCategoryList;

    /**
     * 检测类型（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "检测类型", dictType = "s_check_type")
    @ApiModelProperty(value = "检测类型（数据字典的键值或配置档案的编码）")
    private String checkType;

    @TableField(exist = false)
    private String[] checkTypeList;

    /**
     * 供应商sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "供应商sid")
    private Long vendorSid;

    @TableField(exist = false)
    private Long[] vendorSidList;

    /**
     * 供应商编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "供应商编码")
    private Long vendorCode;

    /**
     * 供应商名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "供应商名称")
    private String vendorName;

    /**
     * 供应商简称
     */
    @TableField(exist = false)
    @Excel(name = "供应商")
    @ApiModelProperty(value = "供应商简称")
    private String shortName;

    /**
     * 检测紧急度（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "检测紧急度", dictType = "s_check_degree")
    @ApiModelProperty(value = "检测紧急度（数据字典的键值或配置档案的编码）")
    private String checkDegree;

    @TableField(exist = false)
    private String[] checkDegreeList;

    /**
     * 是否供方自测（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "供方自测", dictType = "sys_yes_no")
    @ApiModelProperty(value = "是否供方自测（数据字典的键值或配置档案的编码）")
    private String isSupplierCheck;

    /**
     * 检测批次
     */
    @Excel(name = "检测批次")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "检测批次")
    private Long checkBatch;

    /**
     * 等级（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "等级", dictType = "s_grade_product")
    @ApiModelProperty(value = "等级（数据字典的键值或配置档案的编码）")
    private String grade;

    /**
     * 安全类别（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "安全类别", dictType = "s_safe_category")
    @ApiModelProperty(value = "安全类别（数据字典的键值或配置档案的编码）")
    private String safeCategory;

    /**
     * 检测标准sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "检测标准sid")
    private Long checkStandard;

    /**
     * 送检实验室sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "送检实验室sid")
    private Long checkLabSid;

    @TableField(exist = false)
    private Long[] checkLabSidList;

    /**
     * 实验室编码
     */
    @ApiModelProperty(value = "实验室编码")
    private String laboratoryCode;

    /**
     * 实验室名称
     */
    @TableField(exist = false)
    @Excel(name = "送检实验室")
    @ApiModelProperty(value = "实验室名称")
    private String laboratoryName;

    /**
     * IQC（用户账号）
     */
    @ApiModelProperty(value = "IQC（用户账号）")
    private String iqc;

    /**
     * IQC（用户账号）
     */
    @TableField(exist = false)
    @Excel(name = "IQC")
    @ApiModelProperty(value = "IQC（用户账号）")
    private String iqcName;

    /**
     * 报告号
     */
    @Excel(name = "报告号")
    @ApiModelProperty(value = "报告号")
    private String reportCode;

    /**
     * 判定结果（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "判定结果", dictType = "s_check_result")
    @ApiModelProperty(value = "判定结果（数据字典的键值或配置档案的编码）")
    private String result;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 原特殊工艺检测单sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "原特殊工艺检测单sid")
    private Long preProductCheckSid;

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
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /**
     * 更新人账号（用户账号）
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户账号）")
    private String updaterAccount;

    @Excel(name = "更改人")
    @TableField(exist = false)
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
     * 确认人账号（用户账号）
     */
    @ApiModelProperty(value = "确认人账号（用户账号）")
    private String confirmerAccount;

    @Excel(name = "确认人")
    @TableField(exist = false)
    private String confirmerAccountName;

    /**
     * 确认时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "确认日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    @Valid
    @TableField(exist = false)
    @ApiModelProperty(value = "成衣检测单-款明细")
    private List<QuaProductCheckProducts> productList;

    @Valid
    @TableField(exist = false)
    @ApiModelProperty(value = "成衣检测单-检测项目")
    private List<QuaProductCheckItem> itemList;

    @Valid
    @TableField(exist = false)
    @ApiModelProperty(value = "成衣检测单-附件")
    private List<QuaProductCheckAttach> attachList;

}
