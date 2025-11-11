package com.platform.ems.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * undefined对象 s_sal_sale_price_attachment
 *
 * @author linhongwei
 * @date 2021-03-05
 */
@Data
@TableName("s_sal_sale_price_attachment")
@ApiModel
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SalSalePriceAttachment  extends EmsBaseEntity {
    private static final long serialVersionUID = 1L;

    /** 客户端口号 */
    @Excel(name = "客户端口号")
    @ApiModelProperty(value = "客户端口号")
    @TableField(fill = FieldFill.INSERT)
    private String clientId;

    /** 系统自增长ID-销售价附件信息 */
    @ApiModelProperty(value = "系统自增长ID-销售价附件信息")
    @TableId
    private Long salePriceAttachmentSid;

    /** 系统自增长ID-销售价 */
    @Excel(name = "系统自增长ID-销售价")
    @ApiModelProperty(value = "系统自增长ID-销售价")
    private Long salePriceSid;

    /** 附件类型编码 */
    @Excel(name = "附件类型编码")
    @ApiModelProperty(value = "附件类型编码")
    private String fileType;

    /** 附件名称 */
    @Excel(name = "附件名称")
    @ApiModelProperty(value = "附件名称")
    private String fileName;

    /** 附件路径 */
    @Excel(name = "附件路径")
    @ApiModelProperty(value = "附件路径")
    private String filePath;

    @ApiModelProperty(value = "备注")
    private String remark;

    /** 创建人账号 */
    @Excel(name = "创建人账号")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createDate;

    /** 更新人账号 */
    @Excel(name = "更新人账号")
    @ApiModelProperty(value = "更新人账号")
    @TableField(fill = FieldFill.UPDATE)
    private String updaterAccount;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /** 数据源系统 */
    @Excel(name = "数据源系统")
    @ApiModelProperty(value = "数据源系统")
    @TableField(fill = FieldFill.INSERT)
    private String dataSourceSys;

    @TableField(exist = false)
    @ApiModelProperty(value ="文件类型名称")
    private String fileTypeName;

    @TableField(exist = false)
    @ApiModelProperty(value ="创建人")
    private String creatorAccountName;

}
