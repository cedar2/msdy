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
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import lombok.experimental.Accessors;

/**
 * 领退料单对象 s_inv_material_requisition
 *
 * @author linhongwei
 * @date 2021-04-08
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_inv_material_requisition")
public class InvMaterialRequisition extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-领退料单
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-领退料单")
    private Long materialRequisitionSid;

    /**
     * 领退料单号
     */
    @Excel(name = "领退料单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "领退料单号")
    private Long materialRequisitionCode;

    /**
     * 单据日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "单据日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "单据日期")
    private Date documentDate;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "过账日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "过账日期")
    private Date accountDate;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "需求日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "需求日期")
    private Date demandDate;

    @Excel(name="领料人")
    @ApiModelProperty(value = "领料人")
    private  String materialReceiver;

    @ApiModelProperty(value = "领料人")
    @TableField(exist = false)
    private  String materialReceiverName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "产品季sid")
    private Long productSeasonSid;

    /**
     * 作业类型sid（冗余）
     */
    @Excel(name = "作业类型sid（冗余）")
    @ApiModelProperty(value = "作业类型sid（冗余）")
    private String movementType;

    @ApiModelProperty(value = "工作中心sid")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long workCenterSid;

    @Excel(name = "工作中心名称")
    @ApiModelProperty(value = "工作中心名称")
    @TableField(exist = false)
    private String workCenterName;

    @Excel(name = "作业类型名称")
    @ApiModelProperty(value = "作业类型名称")
    @TableField(exist = false)
    private String movementTypeName;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：工作中心")
    private Long[] workCenterSidList;

    @Excel(name = "数据来源类别")
    @ApiModelProperty(value = "数据来源类别")
    private String referDocCategory;

    /**
     * 单据类型（数据字典的键值）
     */
    @Excel(name = "单据类型（数据字典的键值）")
    @ApiModelProperty(value = "单据类型（数据字典的键值）")
    private String documentType;

    @ApiModelProperty(value = "特殊库存")
    private String specialStock;

    @Excel(name = "特殊库存")
    @ApiModelProperty(value = "特殊库存")
    @TableField(exist = false)
    private String specialStockName;

    /**
     * 业务类型（数据字典的键值）
     */
    @Excel(name = "业务类型（数据字典的键值）")
    @ApiModelProperty(value = "业务类型（数据字典的键值）")
    private String businessType;

    /**
     * 需求部门sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "需求部门sid")
    private Long requireDepartment;

    @Excel(name = "需求部门")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "需求部门")
    @TableField(exist = false)
    private String requireDepartmentName;

    @Excel(name = "用途")
    @ApiModelProperty(value = "用途")
    private String requireUsage;

    /**
     * 参考单号
     */
    @Excel(name = "参考单号")
    @ApiModelProperty(value = "参考单号")
    private String referDocument;

    /**
     * 系统SID-公司档案
     */
    @Excel(name = "系统SID-公司档案")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-公司档案")
    private Long companySid;

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

    /**
     * 系统SID-生产订单
     */
    @Excel(name = "系统SID-生产订单")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-生产订单")
    private Long manufactureOrderSid;

    /**
     * 生产订单号
     */
    @Excel(name = "生产订单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "生产订单号")
    private Long manufactureOrderCode;

    /**
     * 是否预留库存（数据字典的键值）
     */
    @Excel(name = "是否预留库存（数据字典的键值）")
    @ApiModelProperty(value = "是否预留库存（数据字典的键值）")
    private String isReserveStock;

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
     * 部门编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "部门编码")
    private String departmentCode;

    /**
     * 部门名称
     */
    @TableField(exist = false)
    @Excel(name = "部门名称")
    @ApiModelProperty(value = "部门名称")
    private String departmentName;

    /**
     * 公司代码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "公司代码")
    private String companyCode;

    /**
     * 公司名称
     */
    @TableField(exist = false)
    @Excel(name = "公司名称")
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    /**
     * 领退料单sids
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "领退料单sids")
    private Long[] materialRequisitionSids;

    @ApiModelProperty(value = "出入库状态")
    @Excel(name = "出入库状态")
    private String inOutStockStatus;

    @ApiModelProperty(value = "出入库状态")
    @TableField(exist = false)
    private String[] inOutStockStatusList;

    @TableField(exist = false)
    private Long[] companySidList;

    @TableField(exist = false)
    private Long[] vendorSidList;

    @TableField(exist = false)
    private Long[] customerSidList;
    /**
     * 领退料单-明细对象
     */
    @Valid
    @TableField(exist = false)
    @ApiModelProperty(value = "领退料单-明细对象")
    private List<InvMaterialRequisitionItem> invMaterialRequisitionItemList;

    /**
     * 领退料单-附件对象
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "领退料单-附件对象")
    private List<InvMaterialRequisitionAttachment> invMaterialRequisitionAttachmentList;

    @ApiModelProperty(value = "流程ID")
    private String instanceId;

    /** 流程状态 0：普通记录 1：待审批记录 2：审批结束记录*/
    @Excel(name = "流程状态")
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

    @ApiModelProperty(value = "供应商sid")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long vendorSid;

    @ApiModelProperty(value = "客户sid")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long customerSid;

    @TableField(exist = false)
    @Excel(name = "仓库名称")
    @ApiModelProperty(value = "仓库名称")
    private String storehouseName;

    @TableField(exist = false)
    @Excel(name = "仓库编码")
    @ApiModelProperty(value = "仓库编码")
    private String storehouseCode;

    @TableField(exist = false)
    @Excel(name = "库位名称")
    @ApiModelProperty(value = "库位名称")
    private String locationName;

    @TableField(exist = false)
    @Excel(name = "库位编码")
    @ApiModelProperty(value = "库位编码")
    private String locationCode;

    @TableField(exist = false)
    @Excel(name = "客户编码")
    @ApiModelProperty(value = "客户编码")
    private String customerCode;

    @TableField(exist = false)
    @Excel(name = "客户名称")
    @ApiModelProperty(value = "客户名称")
    private String customerName;
    /**********************************************************************************/
    @ApiModelProperty(value = "查询：需求日期起")
    @TableField(exist = false)
    private String demandDateBeginTime;

    @ApiModelProperty(value = "查询：需求日期至")
    @TableField(exist = false)
    private String demandDateEndTime;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：作业类型")
    private String[] movementTypeList;

    @ApiModelProperty(value = "查询：处理状态")
    @TableField(exist = false)
    private String[] handleStatusList;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：库位")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long[] storehouseLocationSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "业务类型")
    private String businessTypeName;

    @TableField(exist = false)
    @ApiModelProperty(value = "单据类型")
    private String documentTypeName;

    @TableField(exist = false)
    @ApiModelProperty(value = "出入库状态")
    private String inOutStockStatusName;

    @TableField(exist = false)
    @ApiModelProperty(value = "创建人名称")
    private String creatorAccountName;


    @TableField(exist = false)
    @ApiModelProperty(value = "更改人名称")
    private String updaterAccountName;

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

    @ApiModelProperty(value = "输出PDF路径")
    @TableField(exist = false)
    public String outputPath;

    @ApiModelProperty(value = "sid")
    @TableField(exist = false)
    List<Long> itemSidList;

    @ApiModelProperty(value = "领退料业务标识")
    @TableField(exist = false)
    public String businessFlag;
}
