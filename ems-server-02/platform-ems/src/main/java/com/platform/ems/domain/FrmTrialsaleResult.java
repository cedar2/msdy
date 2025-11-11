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

import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;

import lombok.experimental.Accessors;

/**
 * 试销结果单对象 s_frm_trialsale_result
 *
 * @author chenkw
 * @date 2022-12-19
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_frm_trialsale_result")
public class FrmTrialsaleResult extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-试销结果单
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-试销结果单")
    private Long trialsaleResultSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] trialsaleResultSidList;

    /**
     * 试销结果单号
     */
	@Excel(name = "试销结果单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "试销结果单号")
    private Long trialsaleResultCode;

    /**
     * 系统SID-开发计划
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-开发计划")
    private Long developPlanSid;

    /**
     * 开发计划号
     */
    @Excel(name = "开发计划号")
    @ApiModelProperty(value = "开发计划号")
    private String developPlanCode;

	/**
	 * 开发计划名称
	 */
	@Excel(name = "开发计划名称")
	@TableField(exist = false)
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
	 * 物料商品款号
	 */
	@TableField(exist = false)
	@ApiModelProperty(value = "物料商品款号")
	private String materialCode;

	/**
	 * 物料商品名称
	 */
	@TableField(exist = false)
	@ApiModelProperty(value = "物料商品名称")
	private String materialName;

	/**
	 * 颜色编码；如存多值，用英文分号“;”隔开
	 */
	@ApiModelProperty(value = "颜色编码；如存多值，用英文分号“;”隔开")
	private String sku1Code;

	/**
	 * 颜色编码数组
	 */
	@TableField(exist = false)
	@ApiModelProperty(value = "颜色编码数组")
	private String[] sku1CodeList;

	/**
	 * 颜色
	 */
	@TableField(exist = false)
	@Excel(name = "颜色")
	@ApiModelProperty(value = "颜色；如存多值，用英文分号“;”隔开")
	private String sku1Name;

    /**
     * 系统SID-项目档案
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-项目档案")
    private Long projectSid;

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
	 * 项目名称
	 */
	@TableField(exist = false)
	@ApiModelProperty(value = "项目名称")
	private String projectName;

	/**
	 * 试销类型（数据字典的键值或配置档案的编码）
	 */
	@TableField(exist = false)
	@Excel(name = "试销类型", dictType = "s_trialsale_type")
	@ApiModelProperty(value = "试销类型（数据字典的键值或配置档案的编码）")
	private String trialsaleType;

	/**
	 * 销售站点name
	 */
	@TableField(exist = false)
	@Excel(name = "销售站点/网店")
	@ApiModelProperty(value = "销售站点name")
	private String saleStationName;

	/**
	 * 大类sid
	 */
	@TableField(exist = false)
	@Excel(name = "大类")
	@ApiModelProperty(value = "大类")
	private String bigClassName;

	/**
	 * 中类sid
	 */
	@TableField(exist = false)
	@Excel(name = "中类")
	@ApiModelProperty(value = "中类")
	private String middleClassName;

	/**
	 * 小类sid
	 */
	@TableField(exist = false)
	@Excel(name = "小类")
	@ApiModelProperty(value = "小类")
	private String smallClassName;

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
	 * 试销市场编码；如存多值，用英文分号“;”隔开
	 */
	@Excel(name = "试销市场", dictType = "s_market_region")
	@ApiModelProperty(value = "试销市场编码；如存多值，用英文分号“;”隔开")
	private String trialsaleMarket;

	/**
	 * 试销市场
	 */
	@TableField(exist = false)
	@ApiModelProperty(value = "试销市场")
	private String trialsaleMarketName;

	/**
	 * 试销市场 (多选)
	 */
	@TableField(exist = false)
	@ApiModelProperty(value = "试销市场 (多选)")
	private String[] trialsaleMarketList;

    /**
     * 试销数量
     */
    @Excel(name = "试销数量")
    @ApiModelProperty(value = "试销数量")
    private Integer trialsaleQuantity;

    /**
     * 试销员sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "试销员sid")
    private Long trialsalePersonSid;

	/**
	 * 试销员sid (多选)
	 */
	@TableField(exist = false)
	@ApiModelProperty(value = "试销员sid (多选)")
	private Long[] trialsalePersonSidList;

    /**
     * 试销员（员工号）
     */
    @ApiModelProperty(value = "试销员（员工号）")
    private String trialsalePersonCode;

	/**
	 * 试销员（员工名称）
	 */
	@TableField(exist = false)
	@Excel(name = "试销员")
	@ApiModelProperty(value = "试销员（员工名称）")
	private String trialsalePersonName;

    /**
     * 试销日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "试销结束日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "试销结束日期")
    private Date trialsaleDate;

	/**
	 * 试销日期起
	 */
	@TableField(exist = false)
	@ApiModelProperty(value = "试销日期起")
	private String trialsaleDateBegin;

	/**
	 * 试销日期止
	 */
	@TableField(exist = false)
	@ApiModelProperty(value = "试销日期止")
	private String trialsaleDateEnd;

    /**
     * 评价数
     */
    @Excel(name = "评价数")
    @ApiModelProperty(value = "评价数")
    private Integer feedbackNum;

    /**
     * 星级
     */
    @Excel(name = "星级", dictType = "s_star_level")
    @ApiModelProperty(value = "星级")
    private String starLevel;

	/**
	 * 星级 （多选）
	 */
	@TableField(exist = false)
	@ApiModelProperty(value = "星级 （多选）")
	private String[] starLevelList;

    /**
     * 买家不满意率，如是2%，则存储的值为：0.02   提示用 百分比的规则提示
     */
	@Digits(integer = 1, fraction = 4, message = "买家不满意率(%)整数位上限为3位，小数位上限为2位")
    @ApiModelProperty(value = "买家不满意率，如是2%，则存储的值为：0.02")
    private BigDecimal customerUnsatisfyRate;

	/**
	 * 买家不满意率 起
	 */
	@TableField(exist = false)
	@ApiModelProperty(value = "买家不满意率 起")
	private BigDecimal customerUnsatisfyRateBegin;

	/**
	 * 买家不满意率 止
	 */
	@TableField(exist = false)
	@ApiModelProperty(value = "买家不满意率 止")
	private BigDecimal customerUnsatisfyRateEnd;

	/**
	 * 买家不满意率，如是2%，则存储的值为：0.02
	 */
	@TableField(exist = false)
	@Excel(name = "买家不满意率(%)")
	@ApiModelProperty(value = "买家不满意率")
	private String customerUnsatisfyRateString;

    /**
     * 买家之声
     */
    @ApiModelProperty(value = "买家之声")
    private String customerFeedback;

    /**
     * 评价分析
     */
    @ApiModelProperty(value = "评价分析")
    private String feedbackAnalysis;

    /**
     * 产品分析
     */
    @ApiModelProperty(value = "产品分析")
    private String productAnalysis;

    /**
     * 是否翻单（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "是否翻单", dictType = "sys_yes_no")
    @ApiModelProperty(value = "是否翻单（数据字典的键值或配置档案的编码）")
    private String isRepeatOrder;

    /**
     * 是否升级（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "是否升级", dictType = "sys_yes_no")
    @ApiModelProperty(value = "是否升级（数据字典的键值或配置档案的编码）")
    private String isUpdate;

    /**
     * 运营评分
     */
	@Excel(name = "运营评分")
	@Digits(integer = 3, fraction = 2, message = "运营评分整数位上限为3位，小数位上限为2位")
    @ApiModelProperty(value = "运营评分")
    private BigDecimal operationScore;

	/**
	 * 运营评分 起
	 */
	@TableField(exist = false)
	@ApiModelProperty(value = "运营评分 起")
	private BigDecimal operationScoreBegin;

	/**
	 * 运营评分 止
	 */
	@TableField(exist = false)
	@ApiModelProperty(value = "运营评分 止")
	private BigDecimal operationScoreEnd;

    /**
     * 主管评分
     */
	@Digits(integer = 3, fraction = 2, message = "主管评分整数位上限为3位，小数位上限为2位")
    @Excel(name = "主管评分")
    @ApiModelProperty(value = "主管评分")
    private BigDecimal leaderScore;

	/**
	 * 主管评分 起
	 */
	@TableField(exist = false)
	@ApiModelProperty(value = "主管评分 起")
	private BigDecimal leaderScoreBegin;

	/**
	 * 主管评分 止
	 */
	@TableField(exist = false)
	@ApiModelProperty(value = "主管评分 止")
	private BigDecimal leaderScoreEnd;

	/**
	 * 点击率，如是2%，则存储的值为：0.02   提示用 百分比的规则提示
	 */
	@Digits(integer = 1, fraction = 4, message = "点击率(%)整数位上限为3位，小数位上限为2位")
	@ApiModelProperty(value = "点击率，如是2%，则存储的值为：0.02")
	private BigDecimal clickRate;

	/**
	 * 点击率（%）
	 */
	@TableField(exist = false)
	@Excel(name = "点击率(%)")
	@ApiModelProperty(value = "点击率（%）")
	private String clickRateString;

	/**
	 * 转化率，如是2%，则存储的值为：0.02   提示用 百分比的规则提示
	 */
	@Digits(integer = 1, fraction = 4, message = "转化率(%)整数位上限为3位，小数位上限为2位")
	@ApiModelProperty(value = "转化率，如是2%，则存储的值为：0.02")
	private BigDecimal conversionRate;

	/**
	 * 转化率，如是2%
	 */
	@TableField(exist = false)
	@Excel(name = "转化率(%)")
	@ApiModelProperty(value = "转化率，如是2%")
	private String conversionRateString;

    /**
     * 执行总结
     */
    @ApiModelProperty(value = "执行总结")
    private String performSummary;

    /**
     * 建议
     */
    @ApiModelProperty(value = "建议")
    private String adviceComment;

	/**
	 * 样品sid
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty(value = "样品sid")
	private Long sampleSid;

	/**
	 * 样品号
	 */
	@Excel(name = "样品号")
	@ApiModelProperty(value = "样品号")
	private String sampleCode;

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
	@ApiModelProperty(value = "处理状态（多选）")
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
	 * 优化建议对象列表
	 */
	@Valid
	@TableField(exist = false)
	@ApiModelProperty(value = "优化建议对象列表")
	private List<FrmTrialsaleResultAdvice> adviceList;

	/**
	 * 计划项列表
	 */
	@Valid
	@TableField(exist = false)
	@ApiModelProperty(value = "计划项列表")
	private List<FrmTrialsaleResultPlanItem> planItemList;

	/**
	 * 定价方案对象列表
	 */
	@Valid
	@TableField(exist = false)
	@ApiModelProperty(value = "定价方案对象列表")
	private List<FrmTrialsaleResultPriceScheme> priceSchemeList;

	/**
	 * 附件列表
	 */
	@TableField(exist = false)
	@ApiModelProperty(value = "附件列表")
	private List<FrmTrialsaleResultAttach> attachmentList;

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
