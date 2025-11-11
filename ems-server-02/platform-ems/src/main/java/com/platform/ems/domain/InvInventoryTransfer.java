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
import com.platform.ems.domain.dto.request.InvInventoryDocumentCodeRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.List;

/**
 * 调拨单对象 s_inv_inventory_transfer
 *
 * @author linhongwei
 * @date 2021-06-04
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_inv_inventory_transfer")
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class InvInventoryTransfer extends EmsBaseEntity{

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-调拨单
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-调拨单")
    private Long inventoryTransferSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] inventoryTransferSidList;
    /**
     * 调拨单号
     */
    @Excel(name = "调拨单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "调拨单号")
    private Long inventoryTransferCode;

    /**
     * 单据日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "单据日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "单据日期")
    private Date documentDate;

    /**
     * 作业类型（移动类型）sid
     */
    @Excel(name = "作业类型（移动类型）编码")
    @ApiModelProperty(value = "作业类型（移动类型）编码")
    private String movementType;

    @ApiModelProperty(value = "单据类型编码code")
    private String documentType;

    @Excel(name = "单据类型")
    @TableField(exist = false)
    @ApiModelProperty(value = "单据类型")
    private String documentTypeName;

    @ApiModelProperty(value = "业务类型编码code")
    private String businessType;

    @Excel(name = "业务类型")
    @TableField(exist = false)
    @ApiModelProperty(value = "业务类型")
    private String businessTypeName;

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

    /**
     * 系统SID-仓库档案
     */
    @Excel(name = "系统SID-仓库档案")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-仓库档案")
    private Long storehouseSid;

    /**
     * 系统SID-库位
     */
    @Excel(name = "系统SID-库位")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-库位")
    private Long storehouseLocationSid;

    /**
     * 系统SID-仓库档案（目的仓库）
     */
    @Excel(name = "系统SID-仓库档案（目的仓库）")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-仓库档案（目的仓库）")
    private Long destStorehouseSid;

    /**
     * 系统SID-库位（目的库位）
     */
    @Excel(name = "系统SID-库位（目的库位）")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-库位（目的库位）")
    private Long destStorehouseLocationSid;

    /**
     * 库存类型（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "库存类型（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "库存类型（数据字典的键值或配置档案的编码）")
    private String stockType;

    /**
     * 特殊库存（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "特殊库存（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "特殊库存（数据字典的键值或配置档案的编码）")
    private String specialStock;

    /**
     * 特殊库存供应商sid
     */
    @Excel(name = "特殊库存供应商sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "特殊库存供应商sid")
    private Long vendorSid;

    /**
     * 特殊库存客户sid
     */
    @Excel(name = "特殊库存客户sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "特殊库存客户sid")
    private Long customerSid;

    /**
     * 过账人（用户名称）
     */
    @Excel(name = "过账人（用户名称）")
    @ApiModelProperty(value = "过账人（用户名称）")
    private String accountor;

    /**
     * 过账日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "过账日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "过账日期")
    private Date accountDate;

    /**
     * 处理状态（数据字典的键值或配置档案的编码）
     */
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "处理状态（数据字典的键值或配置档案的编码）", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    /**
     * 创建人账号（用户名称）
     */
    @Excel(name = "创建人账号（用户名称）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    @ApiModelProperty(value = "出入库状态")
    @Excel(name = "出库状态")
    private String OutStockStatus;

    @ApiModelProperty(value = "出入库状态")
    @Excel(name = "入库状态")
    private String inStockStatus;

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
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "数据源系统（数据字典的键值或配置档案的编码）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    @Valid
    @TableField(exist = false)
    @ApiModelProperty(value = "调拨单明细对象")
    private List<InvInventoryTransferItem> listInvInventoryTransfer;

    @TableField(exist = false)
    @ApiModelProperty(value = "调拨单附件对象")
    private List<InvInventoryTransferAttachment> attachList;

    @TableField(exist = false)
    @Excel(name = "公司编码")
    @ApiModelProperty(value = "公司编码")
    private String companyCode;

    @TableField(exist = false)
    @Excel(name = "供应商名称")
    @ApiModelProperty(value = "供应商名称")
    private String vendorName;

    @TableField(exist = false)
    @Excel(name = "供应商编码")
    @ApiModelProperty(value = "供应商编码")
    private String vendorCode;


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

    @TableField(exist = false)
    @ApiModelProperty(value = "作业类型名称")
    private String movementTypeName;
    @TableField(exist = false)
    @Excel(name = "目标仓库名称")
    @ApiModelProperty(value = "目标仓库名称")
    private String destStorehouseName;

    @TableField(exist = false)
    @Excel(name = "目标仓库编码")
    @ApiModelProperty(value = "目标仓库编码")
    private String destStorehouseCode;

    @TableField(exist = false)
    @Excel(name = "目标库位名称")
    @ApiModelProperty(value = "目标库位名称")
    private String destLocationName;

    @TableField(exist = false)
    @Excel(name = "目标库位编码")
    @ApiModelProperty(value = "目标库位编码")
    private String destLocationCode;

    @TableField(exist = false)
    @Excel(name = "库存凭证类别编码")
    @ApiModelProperty(value = "库存凭证类别编码")
    private Long inventoryDocumentCode;

    /**********************************************************************************/
    @TableField(exist = false)
    @ApiModelProperty(value = "查询：作业类型")
    private String[] movementTypeList;

    @ApiModelProperty(value = "查询：单据日期起")
    @TableField(exist = false)
    private String documentBeginTime;

    @ApiModelProperty(value = "查询：单据日期至")
    @TableField(exist = false)
    private String documentEndTime;

    @ApiModelProperty(value = "查询：处理状态")
    @TableField(exist = false)
    private String[] handleStatusList;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：供应商")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long[] vendorSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：客户")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long[] customerSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：库位")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long[] storehouseLocationSidList;

    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(exist = false)
    @ApiModelProperty(value = "查询:目标库位")
    private Long[] destStorehouseLocationSidList;

    @ApiModelProperty(value = "特殊库存（数据字典的键值或配置档案的编码）")
    @TableField(exist = false)
    private String specialStockName;

    @ApiModelProperty(value = "查询：出库状态")
    @TableField(exist = false)
    private String[] OutStockStatusList;


    @ApiModelProperty(value = "查询：入库状态")
    @TableField(exist = false)
    private String[] inStockStatusList;

    @ApiModelProperty(value = "查询：库存凭证编号（入库）")
    @TableField(exist = false)
    private String inventoryDocumentCodeRu;

    @ApiModelProperty(value = "查询：库存凭证编号（出库）")
    @TableField(exist = false)
    private String inventoryDocumentCodeChk;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "入库日期")
    @TableField(exist = false)
    private Date accountDateRu;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "出库日期")
    @TableField(exist = false)
    private Date accountDateChk;

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
    @ApiModelProperty(value = "查询：特殊库存")
    private String[] specialStockList;

    @TableField(exist = false)
    private List<InvInventoryDocumentCodeRequest> listDocumentCode;

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
