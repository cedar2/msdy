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

import java.util.Date;

/**
 * 封样记录-附件对象 s_tec_record_fengyang_attach
 *
 * @author linhongwei
 * @date 2021-10-11
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_tec_record_fengyang_attach")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TecRecordFengyangAttach extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-封样记录
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-封样记录")
    private Long attachmentSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] attachmentSidList;
    /**
     * 封样记录sid
     */
    @Excel(name = "封样记录sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "封样记录sid")
    private Long recordFengyangSid;

    /**
     * 附件类型（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "附件类型（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "附件类型（数据字典的键值或配置档案的编码）")
    private String fileType;

    @TableField(exist = false)
    @ApiModelProperty(value = "附件类型名称")
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
     * 创建人账号（用户账号）
     */
    @Excel(name = "创建人账号（用户账号）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户账号）")
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
     * 更新人账号（用户账号）
     */
    @Excel(name = "更新人账号（用户账号）")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户账号）")
    private String updaterAccount;

    @TableField(exist = false)
    private String creatorAccountName;

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
    @Excel(name = "数据源系统（数据字典的键值或配置档案的编码）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;


}
