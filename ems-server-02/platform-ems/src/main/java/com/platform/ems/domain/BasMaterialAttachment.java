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
import com.platform.common.core.domain.BaseEntity;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * 物料&商品-附件对象 s_bas_material_attachment
 *
 * @author linhongwei
 * @date 2021-03-12
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_bas_material_attachment")
public class BasMaterialAttachment extends EmsBaseEntity {

    /**
     * 客户端口号
     */
    @Excel(name = "客户端口号")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /**
     * 系统ID-版型附件信息
     */
    @TableId
    @ApiModelProperty(value = "系统ID-版型附件信息")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long materialAttachmentSid;

    /**
     * 系统ID-商品档案
     */
    @Excel(name = "系统ID-商品档案")
    @ApiModelProperty(value = "系统ID-商品档案")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long materialSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统ID-商品档案 多选")
    private Long[] materialSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料（商品/服务）编码")
    private String materialCode;

    /**
     * 类型
     */
    @Excel(name = "类型")
    @ApiModelProperty(value = "类型")
    private String fileType;

    /**
     * 文件名
     */
    @Excel(name = "文件名")
    @NotBlank(message = "文件名不能为空")
    @ApiModelProperty(value = "文件名")
    private String fileName;

    /**
     * 附件路径
     */
    @Excel(name = "附件路径")
    @NotBlank(message = "文件不能为空")
    @ApiModelProperty(value = "附件路径")
    private String filePath;

    /**
     * 创建人账号
     */
    @Excel(name = "创建人账号")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /**
     * 更新人账号
     */
    @Excel(name = "更新人账号")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccount;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
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

    @TableField(exist = false)
    @ApiModelProperty(value ="文件类型名称")
    private String fileTypeName;

    @TableField(exist = false)
    @ApiModelProperty(value ="创建人")
    private String creatorAccountName;
}
