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

import javax.validation.constraints.NotBlank;

import lombok.experimental.Accessors;

/**
 * 商品道序完成量台账-主对象 s_man_process_step_complete_record
 *
 * @author chenkw
 * @date 2022-10-20
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_man_process_step_complete_record")
public class ManProcessStepCompleteRecord extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-商品道序完成量台账单
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品道序完成量台账单")
    private Long stepCompleteRecordSid;

    /**
     * 系统SID-商品道序完成量台账单 多选
     */
    @ApiModelProperty(value = "sid数组多选")
    @TableField(exist = false)
    private Long[] stepCompleteRecordSidList;

    /**
     * 商品道序完成量台账单号
     */
    @Excel(name = "道序完成台账编号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "商品道序完成量台账单号")
    private Long stepCompleteRecordCode;

    /**
     * 工厂sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "工厂sid")
    private Long plantSid;

    /**
     * 工厂sid 多选
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "工厂sid多选")
    private Long[] plantSidList;

    /**
     * 工厂编码
     */
    @ApiModelProperty(value = "工厂编码")
    private String plantCode;

    /**
     * 工厂名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "工厂名称")
    private String plantName;

    /**
     * 工厂简称
     */
    @TableField(exist = false)
    @Excel(name = "工厂(工序)")
    @ApiModelProperty(value = "工厂简称")
    private String plantShortName;

    /**
     * 班组sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "班组sid")
    private Long workCenterSid;

    /**
     * 班组sid 多选
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "班组sid多选")
    private Long[] workCenterSidList;

    /**
     * 班组编码
     */
    @ApiModelProperty(value = "班组编码")
    private String workCenterCode;

    /**
     * 班组名称
     */
    @Excel(name = "班组")
    @TableField(exist = false)
    @ApiModelProperty(value = "班组名称")
    private String workCenterName;

    /**
     * 操作部门（配置档案的编码）  s_con_manufacture_department
     */
    @ApiModelProperty(value = "操作部门（配置档案的编码）")
    private String department;

    /**
     * 操作部门（配置档案的编码） 多选
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "操作部门（配置档案的编码）多选")
    private String[] departmentList;

    /**
     * 操作部门
     */
    @Excel(name = "操作部门")
    @TableField(exist = false)
    @ApiModelProperty(value = "操作部门")
    private String departmentName;

    /**
     * 完成日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "完成日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "完成日期")
    private Date completeDate;

    /**
     * 完成日期 起
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "完成日期 起")
    private String completeDateBegin;

    /**
     * 完成日期 至
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "完成日期 至")
    private String completeDateEnd;

    /**
     * 商品sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "商品sid")
    private Long productSid;

    /**
     * 商品sid 多选
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "商品sid 多选")
    private Long[] productSidList;

    /**
     * 商品编码(款号)
     */
    @Excel(name = "商品编码(款号)")
    @ApiModelProperty(value = "商品编码(款号)")
    private String productCode;

    /**
     * 商品工价类型（数据字典的键值）
     */
    @Excel(name = "商品工价类型", dictType = "s_product_price_type")
    @ApiModelProperty(value = "商品工价类型（数据字典的键值）s_product_price_type")
    private String productPriceType;

    /**
     * 商品工价类型（数据字典的键值） 多选
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "商品工价类型（数据字典的键值）多选")
    private String[] productPriceTypeList;

    /**
     * 计薪完工类型（数据字典的键值）
     */
    @Excel(name = "计薪完工类型", dictType = "s_jixin_wangong_type")
    @ApiModelProperty(value = "计薪完工类型（数据字典的键值）s_jixin_wangong_type")
    private String jixinWangongType;

    /**
     * 计薪完工类型（数据字典的键值）多选
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "计薪完工类型（数据字典的键值）多选")
    private String[] jixinWangongTypeList;

    /**
     * 录入方式（数据字典的键值）
     */
    @Excel(name = "录入方式", dictType = "s_jixin_enter_mode")
    @ApiModelProperty(value = "录入方式（数据字典的键值）s_jixin_enter_mode")
    private String enterMode;

    /**
     * 录入方式（数据字典的键值） 多选
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "录入方式（数据字典的键值）多选")
    private String[] enterModeList;

    /**
     * 登记人账号sid
     */
    @ApiModelProperty(value = "登记人账号sid")
    private Long reporter;

    /**
     * 登记人账号sid 多选
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "登记人账号sid多选")
    private Long[] reporterList;

    /**
     * 登记人
     */
    @Excel(name = "登记人")
    @TableField(exist = false)
    @ApiModelProperty(value = "登记人")
    private String reporterName;

    /**
     * 登记日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "登记日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "登记日期")
    private Date reportDate;

    /**
     * 登记日期 起
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "登记日期 起")
    private String reportDateBegin;

    /**
     * 登记日期 至
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "登记日期 至")
    private String reportDateEnd;

    /**
     * 申报周期（数据字典的键值）
     */
    @Excel(name = "申报周期", dictType = "s_report_cycle")
    @ApiModelProperty(value = "申报周期（数据字典的键值）s_report_cycle")
    private String reportCycle;

    /**
     * 申报周期（数据字典的键值） 多选
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "申报周期（数据字典的键值）多选")
    private String[] reportCycleList;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 处理状态（数据字典的键值）
     */
    @NotBlank(message = "处理状态不能为空")
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值）s_handle_status")
    private String handleStatus;

    /**
     * 处理状态（数据字典的键值） 多选
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态（数据字典的键值）多选")
    private String[] handleStatusList;

    /**
     * 商品名称
     */
    @Excel(name = "商品名称")
    @TableField(exist = false)
    @ApiModelProperty(value = "商品名称")
    private String productName;

    /**
     * 录入维度（数据字典的键值）
     */
    @Excel(name = "录入维度", dictType = "s_enter_dimension")
    @ApiModelProperty(value = "录入维度（数据字典的键值）s_enter_dimension")
    private String enterDimension;

    /**
     * 录入维度（数据字典的键值） 多选
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "录入维度（数据字典的键值）多选")
    private String[] enterDimensionList;

    /**
     * 创建人账号（用户名称）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    /**
     * 创建人账号（用户名称） 多选
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "创建人账号（用户名称）多选")
    private String[] creatorAccountList;

    /**
     * 创建人
     */
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
     * 更新人账号（用户名称）
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

    /**
     * 更新人
     */
	@Excel(name = "更改人")
	@ApiModelProperty(value = "更改人昵称")
	@TableField(exist = false)
	private String updaterAccountName;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更新日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 确认人账号（用户名称）
     */
    @ApiModelProperty(value = "确认人账号（用户名称）")
    private String confirmerAccount;

    /**
     * 确认人
     */
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
     * 明细最大行号（默认0）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "明细最大行号（默认0）")
    private int itemNum;


    @TableField(exist = false)
    @ApiModelProperty(value = "序号(商品道序)")
    private BigDecimal sort;

    /**
     * 生产订单号
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "生产订单号")
    private String manufactureOrderCode;

    /**
     * 排产批次号
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "排产批次号")
    private Integer paichanBatch;

    /**
     * 明细列表
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "明细列表")
    private List<ManProcessStepCompleteRecordItem> stepCompleteRecordItemList;

    /**
     * 附件列表
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "附件列表")
    private List<ManProcessStepCompleteRecordAttach> attachmentList;

}
