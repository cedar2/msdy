package com.platform.ems.domain;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotEmpty;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;


/**
 * 销售扣款单对象 s_fin_sale_deduction
 *
 * @author qhq
 * @date 2021-04-29
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_fin_sale_deduction_bill")
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class FinSaleDeductionBill extends EmsBaseEntity {
    /** 租户ID */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /** 系统SID-销售扣款单 */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-销售扣款单")
    private Long saleDeductionBillSid;

    /** 销售扣款单号 */
    @Excel(name = "销售扣款单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售扣款单号")
    private Long saleDeductionBillCode;

    /** 单据日期 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "单据日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "单据日期")
    private Date documentDate;

    /** 单据类型（数据字典的键值） */
    @Excel(name = "单据类型（数据字典的键值）")
    @ApiModelProperty(value = "单据类型（数据字典的键值）")
    private String documentType;

    /** 业务类型（数据字典的键值） */
    @Excel(name = "业务类型（数据字典的键值）")
    @ApiModelProperty(value = "业务类型（数据字典的键值）")
    private String businessType;

    /** 系统SID-客户 */
    @Excel(name = "系统SID-客户")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-客户")
    private Long customerSid;

    /** 系统SID-公司档案 */
    @Excel(name = "系统SID-公司档案")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-公司档案")
    private Long companySid;

    /** 系统SID-产品季 */
    @Excel(name = "系统SID-产品季")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-产品季")
    private Long productSeasonSid;

    /** 销售组织（数据字典的键值） */
    @Excel(name = "销售组织（数据字典的键值）")
    @ApiModelProperty(value = "销售组织（数据字典的键值）")
    private String saleOrg;

    /** 销售员（用户名称） */
    @Excel(name = "销售员（用户名称）")
    @ApiModelProperty(value = "销售员（用户名称）")
    private String salePerson;

    /** 物料类型（数据字典的键值） */
    @Excel(name = "物料类型（数据字典的键值）")
    @ApiModelProperty(value = "物料类型（数据字典的键值）")
    private String materialType;

    /** 处理状态（数据字典的键值） */
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "处理状态（数据字典的键值）", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值）")
    private String handleStatus;

    /** 清账状态（数据字典的键值） */
    @Excel(name = "清账状态（数据字典的键值）")
    @ApiModelProperty(value = "清账状态（数据字典的键值）")
    private String accountClear;

    /** 创建人账号（用户名称） */
    @Excel(name = "创建人账号（用户名称）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    /** 创建时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
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
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /** 确认人账号（用户名称） */
    @Excel(name = "确认人账号（用户名称）")
    @ApiModelProperty(value = "确认人账号（用户名称）")
    private String confirmerAccount;

    /** 确认时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "确认时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /** 数据源系统（数据字典的键值） */
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

    /** 销售扣款单sids */
    @TableField(exist = false)
    @ApiModelProperty(value = "销售扣款单sids")
    private Long[] saleDeductionSids;

    /**
     * 销售扣款单-明细对象
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "销售扣款单-明细对象")
    private List<FinSaleDeductionBillItem> finSaleDeductionItemList;

    /**
     * 销售扣款单-附件对象
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "销售扣款单-附件对象")
    private List<FinSaleDeductionBillAttachment> finSaleDeductionAttachmentList;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "客户名称")
    private String customerName;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "产品季名称")
    private String productSeasonName;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售员昵称")
    private String nickName;
}
