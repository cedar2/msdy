package com.platform.ems.domain;

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

import javax.validation.constraints.NotBlank;

import lombok.experimental.Accessors;

/**
 * 商品检测问题台账对象 s_qua_product_check_record
 *
 * @author admin
 * @date 2024-03-06
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_qua_product_check_record")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuaProductCheckRecord extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-商品检测记录
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品检测记录")
    private Long productCheckRecordSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] productCheckRecordSidList;

    /**
     * 商品检测记录编号
     */
    @Excel(name = "记录编号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "商品检测记录编号")
    private Long productCheckRecordCode;

    /**
     * 系统SID-商品
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品")
    private Long materialSid;

    /**
     * 商品编码
     */
    @Excel(name = "商品编码")
    @ApiModelProperty(value = "商品编码")
    private String materialCode;

    @TableField(exist = false)
    @Excel(name = "商品名称")
    @ApiModelProperty(value = "商品名称")
    private String materialName;

    /**
     * 颜色
     */
    @Excel(name = "颜色")
    @ApiModelProperty(value = "颜色")
    private String colorName;

    /**
     * 检测项（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "检测项（数据字典的键值或配置档案的编码）")
    private String checkItem;

    /**
     * 检测项（数据字典的键值或配置档案的编码）
     */
    @TableField(exist = false)
    @Excel(name = "检测项")
    @ApiModelProperty(value = "检测项（数据字典的键值或配置档案的编码）")
    private String checkItemName;

    @TableField(exist = false)
    @ApiModelProperty(value = "检测项（数据字典的键值或配置档案的编码）")
    private String[] checkItemList;

    /**
     * 判定结果（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "判定结果", dictType = "s_check_result")
    @ApiModelProperty(value = "判定结果（数据字典的键值或配置档案的编码）")
    private String result;

    @TableField(exist = false)
    @ApiModelProperty(value = "判定结果（数据字典的键值或配置档案的编码）")
    private String[] resultList;

    /**
     * 检测结果值
     */
    @Excel(name = "检测结果值")
    @ApiModelProperty(value = "检测结果值")
    private String checkResultValue;

    /**
     * 检测参考值
     */
    @Excel(name = "检测参考值")
    @ApiModelProperty(value = "检测参考值")
    private String checkReferenceValue;

    /**
     * 样品类别（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "样品类别", dictType = "s_sample_category")
    @ApiModelProperty(value = "样品类别（数据字典的键值或配置档案的编码）")
    private String sampleCategory;

    @TableField(exist = false)
    @ApiModelProperty(value = "样品类别（数据字典的键值或配置档案的编码）")
    private String[] sampleCategoryList;

    /**
     * 样品数量
     */
    @Excel(name = "样品数量", cellType = Excel.ColumnType.NUMERIC)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "样品数量")
    private Long quantity;

    /**
     * 问题类型（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "问题类型", dictType = "s_defect_type")
    @ApiModelProperty(value = "问题类型（数据字典的键值或配置档案的编码）")
    private String defectType;

    @TableField(exist = false)
    @ApiModelProperty(value = "问题类型（数据字典的键值或配置档案的编码）")
    private String[] defectTypeList;

    /**
     * 问题描述
     */
    @Excel(name = "问题描述")
    @ApiModelProperty(value = "问题描述")
    private String defectDescription;

    /**
     * 解决状态（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "解决状态", dictType = "s_resolve_status")
    @ApiModelProperty(value = "解决状态（数据字典的键值或配置档案的编码）")
    private String resolveStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "解决状态（数据字典的键值或配置档案的编码）")
    private String[] resolveStatusList;

    /**
     * 解决说明
     */
    @Excel(name = "解决说明")
    @ApiModelProperty(value = "解决说明")
    private String solutionRemark;

    /**
     * 系统SID-产品季
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-产品季")
    private Long productSeasonSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-产品季")
    private Long[] productSeasonSidList;

    /**
     * 产品季编码
     */
    @ApiModelProperty(value = "产品季编码")
    private String productSeasonCode;

    @TableField(exist = false)
    @Excel(name = "产品季")
    @ApiModelProperty(value = "产品季")
    private String productSeasonName;

    /**
     * 检测类型（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "检测类型", dictType = "s_check_type")
    @ApiModelProperty(value = "检测类型（数据字典的键值或配置档案的编码）")
    private String checkType;

    @TableField(exist = false)
    @ApiModelProperty(value = "检测类型（数据字典的键值或配置档案的编码）")
    private String[] checkTypeList;

    /**
     * 检测标准（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "检测标准（数据字典的键值或配置档案的编码）")
    private String checkStandard;

    @TableField(exist = false)
    @ApiModelProperty(value = "检测标准（数据字典的键值或配置档案的编码）")
    private String[] checkStandardList;

    @TableField(exist = false)
    @Excel(name = "检测标准")
    @ApiModelProperty(value = "检测标准（数据字典的键值或配置档案的编码）")
    private String checkStandardName;

    /**
     * 检测批次
     */
    @Excel(name = "检测批次")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "检测批次")
    private Long checkBatch;

    /**
     * 系统SID-工厂
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-工厂")
    private Long plantSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-工厂")
    private Long[] plantSidList;

    /**
     * 工厂编码
     */
    @ApiModelProperty(value = "工厂编码")
    private String plantCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "工厂名称")
    private String plantName;

    @TableField(exist = false)
    @Excel(name = "工厂")
    @ApiModelProperty(value = "工厂简称")
    private String plantShortName;

    /**
     * 供应商
     */
    @Excel(name = "供应商")
    @ApiModelProperty(value = "供应商")
    private String vendorName;

    /**
     * 等级（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "等级", dictType = "s_grade_product")
    @ApiModelProperty(value = "等级（数据字典的键值或配置档案的编码）")
    private String grade;

    @TableField(exist = false)
    @ApiModelProperty(value = "等级（数据字典的键值或配置档案的编码）")
    private String[] gradeList;

    /**
     * 安全类别（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "安全类别", dictType = "s_safe_category")
    @ApiModelProperty(value = "安全类别（数据字典的键值或配置档案的编码）")
    private String safeCategory;

    @TableField(exist = false)
    @ApiModelProperty(value = "安全类别（数据字典的键值或配置档案的编码）")
    private String[] safeCategoryList;

    /**
     * 报告号
     */
    @Excel(name = "报告号")
    @ApiModelProperty(value = "报告号")
    private String reportCode;

    /**
     * 检测方类型
     */
    @Excel(name = "检测方类型",dictType = "s_check_partner")
    @ApiModelProperty(value = "检测方类型")
    private String checkPartner;

    /**
     * 检测日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "检测日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "检测日期")
    private Date checkDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "检测日期 起")
    private String checkDateBegin;

    @TableField(exist = false)
    @ApiModelProperty(value = "检测日期 止")
    private String checkDateEnd;

    /**
     * 检测类别（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "检测类别（数据字典的键值或配置档案的编码）")
    private String checkCategory;

    @TableField(exist = false)
    @ApiModelProperty(value = "检测类别（数据字典的键值或配置档案的编码）")
    private String[] checkCategoryList;

    /**
     * 检测图片路径，可放多个链接，每个链接用”;“隔开
     */
    @ApiModelProperty(value = "检测图片路径，可放多个链接，每个链接用”;“隔开")
    private String picturePath;

    @TableField(exist = false)
    @ApiModelProperty(value = "检测图片路径，可放多个链接，每个链接用”;“隔开")
    private String[] picturePathList;

    /**
     * 检测视频路径，可放多个链接，每个链接用”;“隔开
     */
    @ApiModelProperty(value = "检测视频路径，可放多个链接，每个链接用”;“隔开")
    private String videoPath;

    @TableField(exist = false)
    @ApiModelProperty(value = "检测图片路径，可放多个链接，每个链接用”;“隔开")
    private String[] videoPathList;

    /**
     * 处理状态（数据字典的键值或配置档案的编码）
     */
    @NotBlank(message = "处理状态不能为空")
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String[] handleStatusList;

    /**
     * 创建人账号（用户账号）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户账号）")
    private String creatorAccount;

    @TableField(exist = false)
    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人昵称")
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

    @TableField(exist = false)
    @Excel(name = "更改人")
    @ApiModelProperty(value = "更改人昵称")
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

    @TableField(exist = false)
    @Excel(name = "确认人")
    @ApiModelProperty(value = "确认人昵称")
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

    @TableField(exist = false)
    @ApiModelProperty(value = "附件清单")
    private List<QuaProductCheckRecordAttach> attachmentList;

}
