package com.platform.ems.domain;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.core.domain.BaseEntity;
import com.platform.common.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import lombok.experimental.Accessors;

/**
 * 库存调整单对象 s_inv_inventory_adjust
 *
 * @author linhongwei
 * @date 2021-04-19
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_inv_inventory_adjust")
public class InvInventoryAdjust extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-库存调整单
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-库存调整单")
    private Long inventoryAdjustSid;

    /**
     * 库存调整单号
     */
    @Excel(name = "库存调整单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "库存调整单号")
    private Long inventoryAdjustCode;

    /**
     * 单据日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "单据日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "单据日期")
    private Date documentDate;

    /**
     * 作业类型sid（冗余）
     */
    @Excel(name = "作业类型sid（冗余）")
    @ApiModelProperty(value = "作业类型sid（冗余）")
    private String movementType;

    /**
     * 单据类型（数据字典的键值）
     */
    @Excel(name = "单据类型（数据字典的键值）")
    @ApiModelProperty(value = "单据类型（数据字典的键值）")
    private String documentType;

    @Excel(name = "单据类型（数据字典的键值）")
    @ApiModelProperty(value = "单据类型（数据字典的键值）")
    private String specialStock;


    @ApiModelProperty(value = "库存类型")
    private String stockType;

    /**
     * 系统SID-仓库档案
     */
    @Excel(name = "系统SID-仓库档案")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-仓库档案")
    private Long storehouseSid;


    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-仓库档案")
    private String storehouseCode;

    /**
     * 系统SID-库位
     */
    @Excel(name = "系统SID-库位")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-库位")
    private Long storehouseLocationSid;

    /**
     * 业务类型（数据字典的键值）
     */
    @Excel(name = "业务类型（数据字典的键值）")
    @ApiModelProperty(value = "业务类型（数据字典的键值）")
    private String businessType;

    /**
     * 参考单号
     */
    @Excel(name = "参考作业单号")
    @ApiModelProperty(value = "参考作业单号")
    private String referDocument;

    /**
     * 系统SID-公司档案
     */
    @Excel(name = "系统SID-公司档案")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-公司档案")
    private Long companySid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "供应商")
    private Long vendorSid;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "过账日期 ", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "过账日期")
    private Date accountDate;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "客户")
    private Long customerSid;

    /**
     * 处理状态（数据字典的键值）
     */
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "处理状态（数据字典的键值）", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值）")
    private String handleStatus;

    /**
     * 创建人账号（用户名称）
     */
    @Excel(name = "创建人账号（用户名称）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
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
     * 更新人账号（用户名称）
     */
    @Excel(name = "更新人账号（用户名称）")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

    @Excel(name = "过账人")
    @ApiModelProperty(value = "过账人")
    private String accountor;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 确认人账号（用户名称）
     */
    @Excel(name = "确认人账号（用户名称）")
    @ApiModelProperty(value = "确认人账号（用户名称）")
    private String confirmerAccount;

    /**
     * 确认时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "确认时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 数据源系统（数据字典的键值）
     */
    @Excel(name = "数据源系统（数据字典的键值）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值）")
    private String dataSourceSys;


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

    /**
     * 公司名称
     */
    @TableField(exist = false)
    @Excel(name = "公司名称")
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    /**
     * 公司代码
     */
    @TableField(exist = false)
    @Excel(name = "公司代码")
    @ApiModelProperty(value = "公司代码")
    private String companyCode;

    /**
     * 系统SID-库存调整单sids
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-库存调整单sids")
    private Long[] inventoryAdjustSids;

    /**
     * 库存调整单-明细对象list
     */
    @Valid
    @TableField(exist = false)
    @ApiModelProperty(value = "库存调整单-明细对象")
    private List<InvInventoryAdjustItem> invInventoryAdjustItemList;

    /**
     * 库存调整单-附件对象list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "库存调整单-附件对象")
    private List<InvInventoryAdjustAttachment> invInventoryAdjustAttachmentList;

    @TableField(exist = false)
    @Excel(name = "仓库名称")
    @ApiModelProperty(value = "仓库名称")
    private String storehouseName;

    @TableField(exist = false)
    @Excel(name = "库位名称")
    @ApiModelProperty(value = "库位名称")
    private String locationName;

    @TableField(exist = false)
    @Excel(name = "供应商名称")
    @ApiModelProperty(value = "供应商名称")
    private String vendorName;

    @TableField(exist = false)
    @Excel(name = "客户名称")
    @ApiModelProperty(value = "客户名称")
    private String customerName;

    @TableField(exist = false)
    @ApiModelProperty(value = "库存凭证编码")
    private String inventoryDocumentCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "特殊库存")
    private String specialStockName;

    @TableField(exist = false)
    @ApiModelProperty(value = "作业类型")
    private String movementTypeName;

    @TableField(exist = false)
    @ApiModelProperty(value = "创建人")
    private String creatorAccountName;

    @TableField(exist = false)
    @ApiModelProperty(value = "过账人")
    private String accountorName;

    @TableField(exist = false)
    @ApiModelProperty(value = "更改人")
    private String updaterAccountName;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：库位")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long[] storehouseLocationSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：作业类型")
    private String[] movementTypeList;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：客户")
    private Long[] customerSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：供应商")
    private Long[] vendorSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：作业类型")
    private String[] handleStatusList;

    @ApiModelProperty(value = "查询：过账日期起")
    @TableField(exist = false)
    private String accountDateBeginTime;

    @ApiModelProperty(value = "查询：过账日期至")
    @TableField(exist = false)
    private String accountDateEndTime;

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
}
