package com.platform.ems.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import com.platform.common.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableId;

import com.platform.common.core.domain.EmsBaseEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 供应商-附件对象 s_bas_vendor_attachment
 *
 * @author chenkw
 * @date 2021-09-13
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_bas_vendor_attachment")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasVendorAttachment extends EmsBaseEntity{

    /** 客户端口号 */
    @Excel(name = "客户端口号")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /** 系统SID-供应商附件信息sid */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商附件信息sid")
    private Long vendorAttachmentSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long [] vendorAttachmentSidList;
    /** 系统SID-供应商档案sid */
    @Excel(name = "系统SID-供应商档案sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商档案sid")
    private Long vendorSid;

    /** 附件类型（数据字典的键值） */
    @Excel(name = "附件类型（数据字典的键值）")
    @ApiModelProperty(value = "附件类型（数据字典的键值）")
    private String fileType;

    @TableField(exist = false)
    @ApiModelProperty(value = "附件类型的数据对象")
    private String dataobjectCategoryCode;

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

    @TableField(exist = false)
    @ApiModelProperty(value = "附件类型（名称）")
    private String fileTypeName;

    @TableField(exist = false)
    @ApiModelProperty(value = "创建人（昵称）")
    private String creatorAccountName;


}
