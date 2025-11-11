package com.platform.ems.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.core.domain.BaseEntity;
import com.platform.common.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableId;
import java.util.List;
import com.platform.common.core.domain.EmsBaseEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import com.platform.common.core.domain.document.UserOperLog;
import lombok.Data;
import javax.validation.constraints.NotEmpty;
import lombok.experimental.Accessors;

/**
 * 供应商业务台账-附件-预付对象 s_fin_record_advance_payment_attachment
 *
 * @author linhongwei
 * @date 2021-05-29
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_fin_record_advance_payment_attachment")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FinRecordAdvancePaymentAttachment extends EmsBaseEntity{

    /** 租户ID */
        @Excel(name = "租户ID")
        @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

        /** 系统SID-供应商预付台账流水附件 */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商预付台账流水附件")
    private Long recordAdvancePaymentAttachmentSid;

        @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long [] recordAdvancePaymentAttachmentSidList;
        /** 系统SID-供应商预付台账流水 */
        @Excel(name = "系统SID-供应商预付台账流水")
        @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商预付台账流水")
    private Long recordAdvancePaymentSid;

        /** 附件类型（数据字典的键值或配置档案的编码） */
        @Excel(name = "附件类型（数据字典的键值或配置档案的编码）")
        @ApiModelProperty(value = "附件类型（数据字典的键值或配置档案的编码）")
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

        /** 数据源系统（数据字典的键值或配置档案的编码） */
        @Excel(name = "数据源系统（数据字典的键值或配置档案的编码）")
        @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;


}
