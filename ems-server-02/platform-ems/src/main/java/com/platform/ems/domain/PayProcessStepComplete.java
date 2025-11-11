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
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 计薪量申报-主对象 s_pay_process_step_complete
 *
 * @author linhongwei
 * @date 2021-09-08
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_pay_process_step_complete")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PayProcessStepComplete extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-计薪量申报单
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-计薪量申报单")
    private Long stepCompleteSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] stepCompleteSidList;
    /**
     * 计薪量申报单号
     */
    @Excel(name = "计薪量申报单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "计薪量申报单号")
    private Long stepCompleteCode;

    /**
     * 工厂sid
     */
    @NotNull(message = "工厂不能为空")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "工厂sid")
    private Long plantSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "工厂list")
    private Long[] plantSidList;


    @TableField(exist = false)
    @ApiModelProperty(value = "对应商品编码在商品档案中的主图")
    private String picture;

    /**
     * 工厂编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "工厂编码")
    private String plantCode;

    /**
     * 工厂名称
     */
    @TableField(exist = false)
    @Excel(name = "工厂(工序)")
    @ApiModelProperty(value = "工厂名称")
    private String plantName;

    /**
     * 工作中心/班组sid
     */
    @NotNull(message = "工作中心/班组不能为空")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "工作中心/班组sid")
    private Long workCenterSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "工作中心/班组list")
    private Long[] workCenterSidList;

    /**
     * 工作中心/班组编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "工作中心/班组编码")
    private String workCenterCode;

    /**
     * 工作中心/班组名称
     */
    @Excel(name = "班组")
    @TableField(exist = false)
    @ApiModelProperty(value = "工作中心/班组名称")
    private String workCenterName;

    @ApiModelProperty(value = "操作部门（数据字典的键值或配置档案的编码）")
    private String department;

    @Excel(name = "操作部门")
    @TableField(exist = false)
    @ApiModelProperty(value = "操作部门（数据字典的键值或配置档案的编码）")
    private String departmentName;

    @TableField(exist = false)
    @ApiModelProperty(value = "操作部门（数据字典的键值或配置档案的编码）")
    private String[] departmentList;

    @NotBlank(message = "所属年月不能为空")
    @Excel(name = "所属年月")
    @ApiModelProperty(value = "所属年月")
    private String yearmonth;

    @TableField(exist = false)
    @ApiModelProperty(value = "所属年月（起）")
    private String yearmonthBegin;

    @TableField(exist = false)
    @ApiModelProperty(value = "所属年月（止）")
    private String yearmonthEnd;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "商品sid")
    private Long productSid;

    @Excel(name = "商品编码(款号)")
    @ApiModelProperty(value = "商品编码")
    private String productCode;

    @NotBlank(message = "商品工价类型不能为空")
    @Excel(name = "商品工价类型", dictType = "s_product_price_type")
    @ApiModelProperty(value = "商品工价类型（数据字典的键值或配置档案的编码）")
    private String productPriceType;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品工价类型(多选)")
    private String[] productPriceTypeList;

    @Excel(name = "计薪完工类型", dictType = "s_jixin_wangong_type")
    @ApiModelProperty(value = "计薪完工类型（数据字典的键值或配置档案的编码）")
    private String jixinWangongType;

    @Excel(name = "录入方式", dictType = "s_jixin_enter_mode")
    @ApiModelProperty(value = "录入方式(数据字典)")
    private String enterMode;

    @TableField(exist = false)
    @ApiModelProperty(value = "计薪完工类型(多选)")
    private String[] jixinWangongTypeList;

    /**
     * 申报人账号sid
     */
    @NotNull(message = "申报人不能为空")
    @ApiModelProperty(value = "申报人账号sid")
    private Long reporter;

    @TableField(exist = false)
    @ApiModelProperty(value = "申报人list")
    private Long[] reporterList;

    /**
     * 申报人名称
     */
    @Excel(name = "申报人")
    @TableField(exist = false)
    @ApiModelProperty(value = "申报人名称")
    private String reporterName;

    /**
     * 申报日期(YYYYMMDD)
     */
    @NotNull(message = "申报日期不能为空")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "申报日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "申报日期(YYYYMMDD)")
    private Date reportDate;

    @Excel(name = "申报周期", dictType = "s_report_cycle")
    @ApiModelProperty(value = "申报周期（数据字典的键值或配置档案的编码）")
    private String reportCycle;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    @TableField(exist = false)
    @ApiModelProperty(value = "申报日期开始")
    private String reportBeginDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "申报日期结束")
    private String reportEndDate;

    /**
     * 当前审批节点
     */
    @ApiModelProperty(value = "当前审批节点")
    @TableField(exist = false)
    private String approvalNode;

    /**
     * 当前审批人
     */
    @ApiModelProperty(value = "当前审批人")
    @TableField(exist = false)
    private String approvalUserName;

    /**
     * 处理状态（数据字典的键值或配置档案的编码）
     */
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态list")
    private String[] handleStatusList;

    @Excel(name = "商品名称")
    @TableField(exist = false)
    @ApiModelProperty(value = "商品名称")
    private String productName;

    @Excel(name = "录入维度", dictType = "s_enter_dimension")
    @ApiModelProperty(value = "录入维度（数据字典的键值或配置档案的编码")
    private String enterDimension;

    @TableField(exist = false)
    @ApiModelProperty(value = "序号(商品道序)")
    private String sort;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品道序名称")
    private String processStepName;

    /**
     * 创建人账号（用户名称）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    @TableField(exist = false)
    @ApiModelProperty(value = "创建人账号list")
    private String[] creatorAccountList;

    /**
     * 创建人名称
     */
    @TableField(exist = false)
    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人名称")
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
     * 更新人名称
     */
    @TableField(exist = false)
    @Excel(name = "更改人")
    @ApiModelProperty(value = "更新人名称")
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
     * 确认人名称
     */
    @Excel(name = "确认人")
    @TableField(exist = false)
    @ApiModelProperty(value = "确认人名称")
    private String confirmerAccountName;

    /**
     * 确认时间
     */
    @Excel(name = "确认日期", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "排产批次号")
    private Long paichanBatch;

    /**
     * 计薪量申报-明细对象
     */
    @Valid
    @TableField(exist = false)
    @ApiModelProperty(value = "计薪量申报-明细对象")
    private List<PayProcessStepCompleteItem> payProcessStepCompleteItemList;

    @TableField(exist = false)
    @ApiModelProperty(value = "计薪量申报-明细勾选更新工价的列表（计薪量申报编辑页面）")
    private List<PayProcessStepCompleteItem> updatePriceItemList;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品计件结算信息对象")
    private List<PayProductJijianSettleInfor> payProductJijianSettleInforList;

    /**
     * 计薪量申报-附件对象
     */
    @Valid
    @TableField(exist = false)
    @ApiModelProperty(value = "计薪量申报-附件对象")
    private List<PayProcessStepCompleteAttach> payProcessStepCompleteAttachList;

    /**
     * 前端要定义一个多余的字段去判断，low的一批
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "勾选")
    private String gouxuan;
}
