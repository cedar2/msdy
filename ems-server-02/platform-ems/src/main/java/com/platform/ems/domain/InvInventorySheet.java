package com.platform.ems.domain;

import java.util.Date;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

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

/**
 * 盘点单对象 s_inv_inventory_sheet
 *
 * @author linhongwei
 * @date 2021-04-20
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_inv_inventory_sheet")
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class InvInventorySheet extends EmsBaseEntity {

    /** 租户ID */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /** 系统SID-盘点单 */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-盘点单")
    private Long inventorySheetSid;

    @ApiModelProperty(value = "作业类型")
    private String movementType;

    /** 盘点单号 */
    @Excel(name = "盘点单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "盘点单号")
    private Long inventorySheetCode;

    /** 系统SID-仓库档案 */
    @Excel(name = "系统SID-仓库档案")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-仓库档案")
    private Long storehouseSid;

    /** 系统SID-库位 */
    @Excel(name = "系统SID-库位")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-库位")
    private Long storehouseLocationSid;

    /** 特殊库存（数据字典的键值） */
    @Excel(name = "特殊库存（数据字典的键值）")
    @ApiModelProperty(value = "特殊库存（数据字典的键值）")
    private String specialStock;

    @Excel(name = "库存类型")
    @ApiModelProperty(value = "库存类型")
    private String  stockType;

    /** 单据日期 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "单据日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "单据日期")
    private Date documentDate;

    /** 计划盘点日期 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "计划盘点日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划盘点日期")
    private Date planCountDate;

    /** 是否冻结出入库作业（数据字典的键值） */
    @Excel(name = "是否冻结出入库作业（数据字典的键值）")
    @ApiModelProperty(value = "是否冻结出入库作业（数据字典的键值）")
    private String isFreezeStock;

    /** 是否记录账面库存（数据字典的键值） */
    @Excel(name = "是否记录账面库存（数据字典的键值）")
    @ApiModelProperty(value = "是否记录账面库存（数据字典的键值）")
    private String isQuantityRecord;

    @Excel(name = "是否重盘")
    @ApiModelProperty(value = "是否重盘")
    private String isRepeatCount;

    /** 年度 */
    @Excel(name = "年度")
    @ApiModelProperty(value = "年度")
    private String year;

    @Excel(name = "单据类型编码code")
    @ApiModelProperty(value = "单据类型编码code")
    private String documentType;

    /** 盘点作业编号 */
    @Excel(name = "盘点作业编号")
    @ApiModelProperty(value = "盘点作业编号")
    private String countTaskDocument;

    /** 参考单号 */
    @Excel(name = "参考单号")
    @ApiModelProperty(value = "参考单号")
    private String referDocument;

    /** 盘点状态（数据字典的键值） */
    @Excel(name = "盘点状态（数据字典的键值）")
    @ApiModelProperty(value = "盘点状态（数据字典的键值）")
    private String countStatus;

    /** 实际盘点日期 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "实际盘点日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "实际盘点日期")
    private Date actualCountDate;

    /** 盘点结果录入日期 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "盘点结果录入日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "盘点结果录入日期")
    private Date countResultEnterDate;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "过账日期(盘点过账日期)", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "过账日期(盘点过账日期)")
    private Date accountDate;

    @Excel(name = "盘点过账人（用户名称）")
    @ApiModelProperty(value = "盘点过账人（用户名称）")
    private String accountor;

    /** 系统SID-公司档案 */
    @Excel(name = "系统SID-公司档案")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-公司档案")
    private Long companySid;

    /** 处理状态（数据字典的键值） */
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "处理状态（数据字典的键值）",dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值）")
    private String handleStatus;

    /** 创建人账号（用户名称） */
    @Excel(name = "创建人账号（用户名称）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    /** 创建时间 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /** 更新人账号（用户名称） */
    @Excel(name = "更新人账号（用户名称）")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

    /** 更新时间 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /** 确认人账号（用户名称） */
    @Excel(name = "确认人账号（用户名称）")
    @ApiModelProperty(value = "确认人账号（用户名称）")
    private String confirmerAccount;

    /** 确认时间 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "确认时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /** 数据源系统（数据字典的键值） */
    @Excel(name = "数据源系统（数据字典的键值）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值）")
    private String dataSourceSys;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "特殊库存供应商sid")
    private Long vendorSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "特殊库存客户sid")
    private Long customerSid;

    @ApiModelProperty(value = "外围系统盘点单号(wms)")
    private String otherSystemInventorySheetCode;

    @ApiModelProperty(value = "是否已推送外围系统(极限云)")
    private String isPushOtherSystem;

    @ApiModelProperty(value = "外围系统推送结果(极限云)")
    private String pushResultOtherSystem;

    @ApiModelProperty(value = "外围系统推送返回信息(极限云)")
    private String pushReturnMsgOtherSystem;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "外围系统推送时间(极限云)")
    private Date pushTimeOtherSystem;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "创建日期开始时间")
    @TableField(exist = false)
    private String beginTime;

    @ApiModelProperty(value = "创建日期结束时间")
    @TableField(exist = false)
    private String endTime;

    @ApiModelProperty(value = "页数")
    @TableField(exist = false)
    private Integer pageNum;

    @ApiModelProperty(value = "每页个数")
    @TableField(exist = false)
    private Integer pageSize;

    /** 仓库编码 */
    @TableField(exist = false)
    @ApiModelProperty(value = "仓库编码")
    private String storehouseCode;

    /** 仓库名称 */
    @TableField(exist = false)
    @Excel(name = "仓库名称")
    @ApiModelProperty(value = "仓库名称")
    private String storehouseName;

    /** 库位编码 */
    @TableField(exist = false)
    @ApiModelProperty(value = "库位编码")
    private String locationCode;

    /** 库位名称 */
    @TableField(exist = false)
    @Excel(name = "库位名称")
    @ApiModelProperty(value = "库位名称")
    private String locationName;

    /** 公司代码 */
    @TableField(exist = false)
    @ApiModelProperty(value = "公司代码")
    private Long companyCode;

    /** 公司名称 */
    @TableField(exist = false)
    @Excel(name = "公司名称")
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    /** 系统SID-盘点单sids */
    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-盘点单sids")
    private Long[] inventorySheetSids;

    /** 盘点单-明细对象list */
    @Valid
    @TableField(exist = false)
    @ApiModelProperty(value = "盘点单-明细对象")
    private List<InvInventorySheetItem> invInventorySheetItemList;

    /** 盘点单-附件对象list */
    @TableField(exist = false)
    @ApiModelProperty(value = "盘点单-附件对象")
    private List<InvInventorySheetAttachment> invInventorySheetAttachmentList;

    @ApiModelProperty(value = "流程ID")
    private String instanceId;

    /** 流程状态 0：普通记录 1：待审批记录 2：审批结束记录*/
    @ApiModelProperty(value = "流程状态")
    private String processType;

    @TableField(exist = false)
    @ApiModelProperty(value = "节点名称")
    private String node;

    @TableField(exist = false)
    @ApiModelProperty(value = "审批人")
    private String approval;

    @TableField(exist = false)
    @Excel(name = "供应商名称")
    @ApiModelProperty(value = "供应商名称")
    private String vendorName;

    @TableField(exist = false)
    @Excel(name = "供应商编码")
    @ApiModelProperty(value = "供应商编码")
    private String vendorCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品编码")
    private String materialCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品编码")
    private String materialName;

    @TableField(exist = false)
    @Excel(name = "客户编码")
    @ApiModelProperty(value = "客户编码")
    private String customerCode;

    @TableField(exist = false)
    @Excel(name = "客户名称")
    @ApiModelProperty(value = "客户名称")
    private String customerName;

    @TableField(exist = false)
    @ApiModelProperty(value = "库存凭证编码")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long inventoryDocumentCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "过账/暂存")
    private String type;

    @TableField(exist = false)
    @Excel(name = "创建人名称")
    @ApiModelProperty(value = "创建人名称")
    private String creatorAccountName;

    @TableField(exist = false)
    @Excel(name = "过账人名称")
    @ApiModelProperty(value = "过账人名称")
    private String accountorName;

    @TableField(exist = false)
    @Excel(name = "变更人名称")
    @ApiModelProperty(value = "变更人名称")
    private String updaterAccountName;

    @TableField(exist = false)
    @ApiModelProperty(value = "特殊库存")
    private String specialStockName;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：库位")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long[] storehouseLocationSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：特殊库存")
    private String[] specialStockList;

    @ApiModelProperty(value = "查询：处理状态")
    @TableField(exist = false)
    private String[] handleStatusList;

    @ApiModelProperty(value = "查询：盘点状态")
    @TableField(exist = false)
    private String[] countStatusList;

    @ApiModelProperty(value = "计划盘点日期(起)")
    @TableField(exist = false)
    private String beginTimePlan;

    @ApiModelProperty(value = "计划盘点日期(至)")
    @TableField(exist = false)
    private String endTimePlan;

    @ApiModelProperty(value = "盘点过账日期(起)")
    @TableField(exist = false)
    private String beginTimeAccount;

    @ApiModelProperty(value = "盘点过账日期(至)")
    @TableField(exist = false)
    private String endTimeAccount;

    /** 当前审批节点名称 */
    @Excel(name = "当前审批节点名称")
    @ApiModelProperty(value = "当前审批节点名称")
    @TableField(exist = false)
    private String approvalNode;

    /** 当前审批人 */
    @Excel(name = "当前审批人")
    @ApiModelProperty(value = "当前审批人")
    @TableField(exist = false)
    private String approvalUserName;

    /** 提交人 */
    @Excel(name = "提交人")
    @ApiModelProperty(value = "提交人")
    @TableField(exist = false)
    private String submitUserName;

    /**
     * 提交日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "提交日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "提交日期")
    @TableField(exist = false)
    private Date submitDate;

    @ApiModelProperty(value = "sid")
    @TableField(exist = false)
    List<Long> itemSidList;

    @ApiModelProperty(value = "是否带入0库存量")
    @TableField(exist = false)
    private Boolean isZero;
}
