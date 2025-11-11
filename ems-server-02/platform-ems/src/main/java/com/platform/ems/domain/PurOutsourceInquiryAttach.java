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
 * 加工询价单-附件对象 s_pur_outsource_inquiry_attach
 *
 * @author chenkw
 * @date 2022-01-11
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_pur_outsource_inquiry_attach")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PurOutsourceInquiryAttach extends EmsBaseEntity {

    /**
     * 客户端口号
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /**
     * 系统SID-加工询价单附件信息
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-加工询价单附件信息")
    private Long outsourceInquiryAttachmentSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] outsourceInquiryAttachmentSidList;
    /**
     * 系统SID-物料询价单
     */
    @Excel(name = "系统SID-物料询价单")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料询价单")
    private Long outsourceInquirySid;

    /**
     * 附件类型（数据字典的键值）
     */
    @ApiModelProperty(value = "附件类型（数据字典的键值）")
    private String fileType;

    @TableField(exist = false)
    @Excel(name = "附件类型")
    @ApiModelProperty(value = "附件类型（数据字典的键值）")
    private String fileTypeName;

    /**
     * 附件名称
     */
    @Excel(name = "附件名称")
    @ApiModelProperty(value = "附件名称")
    private String fileName;

    /**
     * 附件路径
     */
    @Excel(name = "附件路径")
    @ApiModelProperty(value = "附件路径")
    private String filePath;

    /**
     * 创建人账号
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    @TableField(exist = false)
    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人账号（用户昵称）")
    private String creatorAccountName;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /**
     * 更新人账号
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccount;

    @TableField(exist = false)
    @Excel(name = "更新人")
    @ApiModelProperty(value = "更新人账号（用户昵称）")
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
     * 数据源系统
     */
    @Excel(name = "数据源系统")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;


}
