package com.platform.ems.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableId;

import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import lombok.experimental.Accessors;

import javax.validation.constraints.Digits;

/**
 * 商品道序完成量台账-明细对象 s_man_process_step_complete_record_item
 *
 * @author chenkw
 * @date 2022-10-20
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_man_process_step_complete_record_item")
public class ManProcessStepCompleteRecordItem extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-商品道序完成量台账信息
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品道序完成量台账信息")
    private Long stepCompleteRecordItemSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] stepCompleteRecordItemSidList;

    /**
     * 系统SID-商品道序完成量台账单
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品道序完成量台账单")
    private Long stepCompleteRecordSid;

    /**
     * 员工账号sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "员工账号sid")
    private Long workerSid;

    /**
     * 员工号
     */
    @ApiModelProperty(value = "员工号")
    private String workerCode;

    /**
     * 员工
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "员工")
    private String workerName;

    /**
     * 隶属班组
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "员工班组(隶属班组)")
    private String staffWorkCenterName;

    /**
     * 生产订单sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "生产订单sid")
    private Long manufactureOrderSid;

    /**
     * 生产订单号
     */
    @Excel(name = "生产订单号")
    @ApiModelProperty(value = "生产订单号")
    private String manufactureOrderCode;

    /**
     * 商品sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "商品sid")
    private Long productSid;

    /**
     * 商品编码
     */
    @Excel(name = "商品编码")
    @ApiModelProperty(value = "商品编码")
    private String productCode;

    /**
     * 商品名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "商品名称")
    private String productName;

    /**
     * 商品道序sid(商品道序明细表中的sid)
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "商品道序sid(商品道序明细表中的sid)")
    private Long processStepItemSid;

    /**
     * 道序编码
     */
    @Excel(name = "道序编码")
    @ApiModelProperty(value = "道序编码")
    private String processStepCode;

    /**
     * 道序名称
     */
    @Excel(name = "道序名称")
    @ApiModelProperty(value = "道序名称")
    private String processStepName;

    /**
     * 道序类别
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "道序类别（数据字典的键值或配置档案的编码）")
    private String stepCategory;

    /**
     * 商品道序序号
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "序号(商品道序)")
    private BigDecimal sort;

    /**
     * 完成量(当天)
     */
    @Excel(name = "完成量(当天)")
    @Digits(integer = 8, fraction = 3, message = "完成量(当天)整数位上限为8位，小数位上限为3位")
    @ApiModelProperty(value = "完成量(当天)")
    private BigDecimal completeQuantity;

    /**
     * 行号
     */
    @Excel(name = "行号")
    @ApiModelProperty(value = "行号")
    private Integer itemNum;

    /**
     * 排产批次号
     */
    @Excel(name = "排产批次号")
    @ApiModelProperty(value = "排产批次号")
    private Integer paichanBatch;

    @TableField(exist = false)
    @ApiModelProperty(value = "排产批次号精确查询传Y")
    private String paichanBatchIs;

    /**
     * 商品道序明细的所属生产工序sid
     */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "商品道序明细的所属生产工序sid")
    private Long processSid;

    /**
     * 所属生产工序编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "所属生产工序编码")
    private String processCode;

    /**
     * 所属生产工序名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "所属生产工序名称")
    private String processName;

    /**
     * 操作部门编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "操作部门编码")
    private String department;

    /**
     * 操作部门 商品道序主表
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "操作部门")
    private String departmentName;

    /**
     * 创建人账号（用户名称）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

	@Excel(name = "创建人")
	@ApiModelProperty(value = "创建人昵称")
	@TableField(exist = false)
	private String creatorAccountName;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /**
     * 更新人账号（用户名称）
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

	@Excel(name = "更改人")
	@ApiModelProperty(value = "更改人昵称")
	@TableField(exist = false)
	private String updaterAccountName;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

}
