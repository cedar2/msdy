package com.platform.ems.domain;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.platform.common.annotation.Excel;

import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 商品成本核算-附件对象 s_cos_product_cost_attachment
 *
 * @author linhongwei
 * @date 2021-02-26
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_cos_product_cost_attachment")
public class CosProductCostAttachment extends EmsBaseEntity {

    @ApiModelProperty(value = "客户端口号")
    @TableField(fill = FieldFill.INSERT)
    @Excel(name = "客户端口号")
    private String clientId;

    @ApiModelProperty(value = "系统ID-商品成本核算附件信息")
    @TableId
    private String productCostAttachmentSid;

    @ApiModelProperty(value = "系统ID-商品成本核算")
    @Excel(name = "系统ID-商品成本核算")
    private Long productCostSid;

    @ApiModelProperty(value = "附件类型编码")
    @Excel(name = "附件类型编码")
    private String fileType;

    @ApiModelProperty(value = "附件名称")
    @Excel(name = "附件名称")
    private String fileName;

    @ApiModelProperty(value = "附件路径")
    @Excel(name = "附件路径")
    private String filePath;

    @ApiModelProperty(value = "创建人账号")
    @Excel(name = "创建人账号")
    @TableField(fill = FieldFill.INSERT)
    private String creatorAccount;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    private Date createDate;

    @ApiModelProperty(value = "更新人账号")
    @Excel(name = "更新人账号")
    @TableField(fill = FieldFill.UPDATE)
    private String updaterAccount;

    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    private Date updateDate;

    @ApiModelProperty(value = "数据源系统")
    @Excel(name = "数据源系统")
    @TableField(fill = FieldFill.INSERT)
    private String dataSourceSys;

    @TableField(exist = false)
    @ApiModelProperty(value ="文件类型名称")
    private String fileTypeName;

    @TableField(exist = false)
    @ApiModelProperty(value ="创建人")
    private String creatorAccountName;
}
