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
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 版型-附件对象 s_tec_model_attachment
 *
 * @author linhongwei
 * @date 2021-01-31
 */
@Data
@Accessors( chain = true)
@TableName("s_tec_model_attachment")
@ApiModel
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TecModelAttachment  extends EmsBaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 客户端口号
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value ="客户端口号")
    private String clientId;

    /**
     * 系统ID-版型附件信息
     */
    @TableId
    @Excel(name = "系统ID-版型附件信息")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long modelAttachmentSid;

    /**
     * 系统ID-版型档案
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value ="系统ID-版型档案")
    private Long modelSid;

    @TableField(exist = false)
    @ApiModelProperty(value ="版型档案编码")
    private String modelCode;

    /**
     * 附件类型编码
     */
    @Excel(name = "附件类型编码")
    @ApiModelProperty(value ="附件类型编码")
    private String fileType;

    @TableField(exist = false)
    @ApiModelProperty(value ="附件类型名称")
    private String fileTypeName;

    /**
     * 附件名称
     */
    @Excel(name = "附件名称")
    @ApiModelProperty(value ="附件名称")
    private String fileName;

    @ApiModelProperty(value = "备注")
    private String remark;
    /**
     * 附件路径
     */
    @Excel(name = "附件路径")
    @ApiModelProperty(value ="附件路径")
    private String filePath;

    /**
     * 创建人账号
     */
    @Excel(name = "创建人账号")
    @ApiModelProperty(value ="创建人账号")
    @TableField(fill = FieldFill.INSERT)
    private String creatorAccount;

    @TableField(exist = false)
    @ApiModelProperty(value ="创建人昵称/更新人昵称")
    private String creatorAccountName;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value ="创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createDate;

    /**
     * 更新人账号
     */
    @Excel(name = "更新人账号")
    @ApiModelProperty(value ="更新人账号")
    @TableField(fill = FieldFill.UPDATE)
    private String updaterAccount;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value ="更新时间")
    private Date updateDate;

    /**
     * 数据源系统
     */
    @Excel(name = "数据源系统")
    @ApiModelProperty(value ="数据源系统")
    @TableField(fill = FieldFill.INSERT)
    private String dataSourceSys;


}
