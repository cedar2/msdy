package com.platform.ems.domain;

import java.util.Date;

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
 * 采购扣款单-附件对象 s_fin_purchase_deduction_attachmemt
 *
 * @author linhongwei
 * @date 2021-04-10
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_fin_purchase_deduction_bill_attachment")
public class FinPurchaseDeductionBillAttachment extends EmsBaseEntity {

    /** 租户ID */
        @Excel(name = "租户ID")
        @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /** 系统SID-采购扣款单附件 */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-采购扣款单附件")
    private Long purchaseDeductionBillAttachmentSid;

    /** 系统SID-采购扣款单 */
        @Excel(name = "系统SID-采购扣款单")
        @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-采购扣款单")
    private Long purchaseDeductionBillSid;

    /** 附件类型（数据字典的键值） */
        @Excel(name = "附件类型（数据字典的键值）")
        @ApiModelProperty(value = "附件类型（数据字典的键值）")
    private String fileType;

    /** 附件名称 */
        @Excel(name = "附件名称")
        @ApiModelProperty(value = "附件名称")
    private String fileName;

    /** 附件路径 */
        @Excel(name = "附件路径")
        @ApiModelProperty(value = "附件路径")
    private String filePath;

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



}
