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
import com.platform.flowable.domain.vo.FlowTaskVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;

import lombok.experimental.Accessors;

/**
 * 样品评审单对象 s_frm_sample_review
 *
 * @author chenkw
 * @date 2022-12-12
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_frm_sample_review")
public class FrmSampleReview extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-样品评审单
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-样品评审单")
    private Long sampleReviewSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] sampleReviewSidList;

    /**
     * 样品评审单号
     */
    @Excel(name = "样品评审单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "样品评审单号")
    private Long sampleReviewCode;

    /**
     * 系统SID-开发计划
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-开发计划")
    private Long developPlanSid;

    /**
     * 系统SID-开发计划 多选
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-开发计划 多选")
    private Long[] developPlanSidList;

    /**
     * 开发计划号
     */
    @Excel(name = "开发计划号")
    @ApiModelProperty(value = "开发计划号")
    private String developPlanCode;

    /**
     * 开发计划号
     */
    @TableField(exist = false)
    @Excel(name = "开发计划名称")
    @ApiModelProperty(value = "开发计划名称")
    private String developPlanName;

    /**
     * 商品款号code
     */
    @TableField(exist = false)
    @Excel(name = "商品款号/SPU号")
    @ApiModelProperty(value = "商品款号code")
    private String productCode;

    /**
     * 商品SKU条码code
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "商品SKU条码code")
    private String materialBarcodeCode;

    @TableField(exist = false)
    @Excel(name = "商品SKU编码(ERP)")
    @ApiModelProperty(value = "ERP系统SKU条码编码")
    private String erpMaterialSkuBarcode;

    /**
     * 组别
     */
    @TableField(exist = false)
    @Excel(name = "组别", dictType = "s_product_group")
    @ApiModelProperty(value = "组别")
    private String groupType;

    @TableField(exist = false)
    @ApiModelProperty(value = "组别（数据字典的键值或配置档案的编码）")
    private String[] groupTypeList;

    @TableField(exist = false)
    @Excel(name = "所属年月(项目)")
    @ApiModelProperty(value = "所属年月(项目)")
    private String yearmonthProject;

    @TableField(exist = false)
    @ApiModelProperty(value = "所属年月(项目)起")
    private String yearmonthProjectBegin;

    @TableField(exist = false)
    @ApiModelProperty(value = "所属年月(项目)止")
    private String yearmonthProjectEnd;

    /**
     * 系统SID-项目档案
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-项目档案")
    private Long projectSid;

    /**
     * 系统SID-项目档案 多选
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-项目档案 多选")
    private Long[] projectSidList;

    /**
     * 项目编号
     */
    @Excel(name = "项目编号")
    @ApiModelProperty(value = "项目编号")
    private String projectCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-项目档案任务明细")
    private Long projectTaskSid;

    /**
     * 颜色编码；如存多值，用英文分号“;”隔开
     */
    @ApiModelProperty(value = "颜色编码；如存多值，用英文分号“;”隔开")
    private String sku1Code;

    /**
     * 颜色编码；如存多值，用英文分号“;”隔开
     */
    @Excel(name = "颜色")
    @TableField(exist = false)
    @ApiModelProperty(value = "颜色编码；如存多值，用英文分号“;”隔开")
    private String sku1Name;

    /**
     * 颜色编码 (数组)
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "颜色编码 (数组)")
    private String[] sku1CodeList;

    /**
     * 评审阶段（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "评审阶段", readConverterExp = "CS=初审,ZS=终审")
    @ApiModelProperty(value = "评审阶段（数据字典的键值或配置档案的编码）")
    private String reviewPhase;

    /**
     * 评审阶段（多选）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "评审阶段（多选）")
    private String[] reviewPhaseList;

    /**
     * 评审结果（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "评审结果", dictType = "s_review_result")
    @ApiModelProperty(value = "评审结果（数据字典的键值或配置档案的编码）")
    private String reviewResult;

    /**
     * 评审结果（多选）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "评审结果（数据字典的键值或配置档案的编码）")
    private String[] reviewResultList;

    /**
     * 样品成本(元/人民币)
     */
    @Excel(name = "样品成本(元/人民币)")
    @Digits(integer = 10, fraction = 5, message = "样品成本(元/人民币)整数位上限为10位，小数位上限为5位")
    @ApiModelProperty(value = "样品成本(元/人民币)")
    private BigDecimal sampleCost;

    /**
     * 大类
     */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-大类")
    private Long bigClassSid;

    /**
     * 大类
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "大类")
    private String bigClassCode;

    /**
     * 大类 多选
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-大类 多选")
    private Long[] bigClassSidList;

    /**
     * 大类
     */
    @Excel(name = "大类")
    @TableField(exist = false)
    @ApiModelProperty(value = "大类")
    private String bigClassName;

    /**
     * 中类
     */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-中类")
    private Long middleClassSid;

    /**
     * 中类
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "中类")
    private String middleClassCode;

    /**
     * 中类 多选
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-中类 多选")
    private Long[] middleClassSidList;

    /**
     * 中类
     */
    @Excel(name = "中类")
    @TableField(exist = false)
    @ApiModelProperty(value = "中类")
    private String middleClassName;

    /**
     * 小类
     */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-小类")
    private Long smallClassSid;

    /**
     * 小类
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "小类")
    private String smallClassCode;

    /**
     * 小类 多选
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-小类 多选")
    private Long[] smallClassSidList;

    /**
     * 小类
     */
    @Excel(name = "小类")
    @TableField(exist = false)
    @ApiModelProperty(value = "小类")
    private String smallClassName;

    /**
     * 币种
     */
    @ApiModelProperty(value = "币种")
    private String currency;

    /**
     * 货币单位
     */
    @ApiModelProperty(value = "货币单位")
    private String currencyUnit;

    /**
     * 设计师sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "设计师sid")
    private Long designerSid;

    /**
     * 设计师sid 多选
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "设计师sid 多选")
    private Long[] designerSidList;

    /**
     * 设计师编号
     */
    @ApiModelProperty(value = "设计师编号")
    private String designerCode;

    /**
     * 设计师
     */
    @TableField(exist = false)
    @Excel(name = "设计师")
    @ApiModelProperty(value = "设计师")
    private String designerName;

    /**
     * 设计师助理sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "设计师助理sid")
    private Long designerAssistantSid;

    /**
     * 设计师助理sid 多选
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "设计师助理sid 多选")
    private Long[] designerAssistantSidList;

    /**
     * 设计师助理编号
     */
    @ApiModelProperty(value = "设计师助理编号")
    private String designerAssistantCode;

    /**
     * 设计师助理
     */
    @TableField(exist = false)
    @Excel(name = "设计助理")
    @ApiModelProperty(value = "设计师助理")
    private String designerAssistantName;

    /**
     * 规格尺寸
     */
    @Excel(name = "规格尺寸")
    @ApiModelProperty(value = "规格尺寸")
    private String specificationSize;

    /**
     * 包装尺寸
     */
    @Excel(name = "包装尺寸")
    @ApiModelProperty(value = "包装尺寸")
    private String packingSize;

    /**
     * 净重量（g）
     */
    @Excel(name = "净重量（g）")
    @Digits(integer = 10, fraction = 5, message = "净重量（g）整数位上限为10位，小数位上限为5位")
    @ApiModelProperty(value = "净重量（g）")
    private BigDecimal pureWeight;

    /**
     * 包装重量（g）
     */
    @Excel(name = "包装重量（g）")
    @Digits(integer = 10, fraction = 5, message = "包装重量（g）整数位上限为10位，小数位上限为5位")
    @ApiModelProperty(value = "包装重量（g）")
    private BigDecimal packgeWeight;

    /**
     * 打样供应商sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "打样供应商sid")
    private Long sampleVendorSid;

    /**
     * 打样供应商sid 多选
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "打样供应商sid")
    private Long[] sampleVendorSidList;

    /**
     * 打样供应商编号
     */
    @ApiModelProperty(value = "打样供应商编号")
    private String sampleVendorCode;

    /**
     * 打样供应商
     */
    @Excel(name = "打样供应商")
    @TableField(exist = false)
    @ApiModelProperty(value = "打样供应商")
    private String sampleVendorName;

    /**
     * 生产供应商sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "生产供应商sid")
    private Long productVendorSid;

    /**
     * 生产供应商sid 多选
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "生产供应商sid")
    private Long[] productVendorSidList;

    /**
     * 生产供应商编号
     */
    @ApiModelProperty(value = "生产供应商编号")
    private String productVendorCode;

    /**
     * 生产供应商
     */
    @TableField(exist = false)
    @Excel(name = "生产供应商")
    @ApiModelProperty(value = "生产供应商")
    private String productVendorName;

    /**
     * 样品sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "样品sid")
    private Long sampleSid;

    /**
     * 样品sid 多选
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "样品sid 多选")
    private Long[] sampleSidList;

    /**
     * 样品号
     */
    @Excel(name = "样品号")
    @ApiModelProperty(value = "样品号")
    private String sampleCode;

    /**
     * 评审意见
     */
    @ApiModelProperty(value = "评审意见")
    private String reviewComment;

    /**
     * 样品其它信息
     */
    @ApiModelProperty(value = "样品其它信息")
    private String otherComment;

    /**
     * 商品编码(款号)
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "商品编码(款号)")
    private String materialCode;

    /**
     * 当前审批节点名称
     */
    @TableField(exist = false)
    @Excel(name = "当前审批节点")
    @ApiModelProperty(value = "当前审批节点名称")
    private String approvalNode;

    /**
     * 当前审批人
     */
    @TableField(exist = false)
    @Excel(name = "当前审批人")
    @ApiModelProperty(value = "当前审批人")
    private String approvalUserName;

    /**
     * 处理状态（数据字典的键值或配置档案的编码）
     */
    @NotBlank(message = "处理状态不能为空")
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    /**
     * 处理状态（多选）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String[] handleStatusList;

    /**
     * 备注
     */
    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 创建人账号（用户账号）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户账号）")
    private String creatorAccount;

    @TableField(exist = false)
    @ApiModelProperty(value = "创建人账号（用户账号）多选")
    private String[] creatorAccountList;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人昵称")
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
    @ApiModelProperty(value = "更改人昵称")
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
    @ApiModelProperty(value = "确认人昵称")
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

    /**
     * 附件列表
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "附件列表")
    private List<FrmSampleReviewAttach> attachmentList;

    /**
     * 工作流任务相关--请求参数
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "工作流任务相关--请求参数")
    private FlowTaskVo flowTaskVo;

    /**
     * 操作类型（提交/审批通过/审批驳回）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "操作类型（提交/审批通过/审批驳回）")
    private String businessType;
}
