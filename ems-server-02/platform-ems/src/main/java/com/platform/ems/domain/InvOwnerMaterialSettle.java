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
import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.List;

/**
 * 甲供料结算单对象 s_inv_owner_material_settle
 *
 * @author c
 * @date 2021-09-13
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_inv_owner_material_settle")
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class InvOwnerMaterialSettle extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-甲供料结算单
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-甲供料结算单")
    private Long settleSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] settleSidList;
    /**
     * 甲供料结算单号
     */
    @Excel(name = "甲供料结算单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "甲供料结算单号")
    private Long settleCode;

    /**
     * 单据日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "单据日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "单据日期")
    private Date documentDate;

    /**
     * 作业类型（移动类型）code
     */
    @Excel(name = "作业类型（移动类型）code")
    @ApiModelProperty(value = "作业类型（移动类型）code")
    private String movementType;

    /**
     * 单据类型编码code
     */
    @Excel(name = "单据类型编码code")
    @ApiModelProperty(value = "单据类型编码code")
    private String documentType;

    /**
     * 业务类型编码code
     */
    @Excel(name = "业务类型编码code")
    @ApiModelProperty(value = "业务类型编码code")
    private String businessType;

    /**
     * 参考业务单号
     */
    @Excel(name = "参考业务单号")
    @ApiModelProperty(value = "参考业务单号")
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
     * 出入库状态（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "出入库状态（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "出入库状态（数据字典的键值或配置档案的编码）")
    private String inOutStockStatus;

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
     * 物料类别（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "物料类别（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "物料类别（数据字典的键值或配置档案的编码）")
    private String materialCategory;

    /**
     * 单据类别（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "单据类别（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "单据类别（数据字典的键值或配置档案的编码）")
    private String docCategory;

    @ApiModelProperty(value = "过账人（用户名称）")
    private String accountor;

    @ApiModelProperty(value = "过账人（用户名称）")
    @TableField(exist = false)
    private String accountorName;

    /**
     * 过账日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "过账日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "过账日期")
    private Date accountDate;

    /**
     * 产品季sid
     */
    @Excel(name = "产品季sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "产品季sid")
    private Long productSeasonSid;

    /**
     * 项目sid
     */
    @Excel(name = "项目sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "项目sid")
    private Long projectSid;

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
    private List<InvOwnerMaterialSettleItem> InvOwnerMaterialSettleItemList;

    @TableField(exist = false)
    private List<InvOwnerMaterialSettleAttach> InvOwnerMaterialSettleAttachList;

    @ApiModelProperty(value = "查询：单据日期起")
    @TableField(exist = false)
    private String documentBeginTime;

    @ApiModelProperty(value = "查询：单据日期至")
    @TableField(exist = false)
    private String documentEndTime;

    @ApiModelProperty(value = "查询：处理状态")
    @TableField(exist = false)
    private String[] handleStatusList;

    @ApiModelProperty(value = "查询：创建人")
    @TableField(exist = false)
    private String[] creatorAccountList;

    @ApiModelProperty(value = "查询：出入库时间起")
    @TableField(exist = false)
    private String accountDateBeginTime;

    @ApiModelProperty(value = "查询：出入库时间至")
    @TableField(exist = false)
    private String accountDateEndTime;

    @ApiModelProperty(value = "查询：特殊库存")
    @TableField(exist = false)
    private String[] specialStockList;

    @TableField(exist = false)
    @Excel(name = "公司编码")
    @ApiModelProperty(value = "公司编码")
    private String companyCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "作业类型名称")
    private String movementTypeName;

    @TableField(exist = false)
    @Excel(name = "供应商名称")
    @ApiModelProperty(value = "供应商名称")
    private String vendorName;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：供应商")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long[] vendorSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：库位")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long[] storehouseLocationSidList;

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
    @ApiModelProperty(value = "特殊库存")
    private String specialStockName;

    @TableField(exist = false)
    @ApiModelProperty(value = "库存凭证类别")
    private String documentCategoryName;

    @TableField(exist = false)
    @ApiModelProperty(value = "创建人名称")
    private String creatorAccountName;

    @TableField(exist = false)
    @ApiModelProperty(value = "创建人名称")
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

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：作业类型")
    private String[] movementTypeList;

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
