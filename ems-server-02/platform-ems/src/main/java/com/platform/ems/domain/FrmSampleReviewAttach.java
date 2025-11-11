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
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import lombok.experimental.Accessors;

/**
 * 样品评审单-附件对象 s_frm_sample_review_attach
 *
 * @author linhongwei
 * @date 2022-12-12
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_frm_sample_review_attach")
public class FrmSampleReviewAttach extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-样品评审单附件信息
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-样品评审单附件信息")
    private Long sampleReviewAttachSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] sampleReviewAttachSidList;

    /**
     * 系统SID-样品评审单
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-样品评审单")
    private Long sampleReviewSid;

    /**
     * 样品评审单号
     */
    @Excel(name = "样品评审单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "样品评审单号")
    private Long sampleReviewCode;

    /**
     * 附件类型（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "附件类型")
    @ApiModelProperty(value = "附件类型（数据字典的键值或配置档案的编码）")
    private String fileType;

    /**
     * 附件类型
     */
    @TableField(exist = false)
    @Excel(name = "附件类型")
    @ApiModelProperty(value = "附件类型（数据字典的键值或配置档案的编码）")
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
     * 创建人账号（用户名称）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人昵称")
    @TableField(exist = false)
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
     * 更新人账号（用户名称）
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

    @Excel(name = "更改人")
    @ApiModelProperty(value = "更改人昵称")
    @TableField(exist = false)
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
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

}
