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

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * 销售合同信息-附件对象 s_sal_sale_contract_attach
 *
 * @author linhongwei
 * @date 2021-05-18
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_sal_sale_contract_attach")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SalSaleContractAttachment extends EmsBaseEntity {

    /**
     * 客户端口号
     */
    @Excel(name = "客户端口号")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /**
     * 系统自增长ID-销售合同附件信息
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-销售合同附件信息")
    private Long saleContractAttachmentSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] saleContractAttachmentSidList;
    /**
     * 系统自增长ID-销售合同
     */
    @Excel(name = "系统自增长ID-销售合同")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-销售合同")
    private Long saleContractSid;

    /**
     * 附件类型编码
     */
    @NotBlank(message = "附件类型不能为空")
    @Excel(name = "附件类型编码")
    @ApiModelProperty(value = "附件类型编码")
    private String fileType;

    /**
     * 附件名称
     */
    @Excel(name = "附件名称")
    @NotBlank(message = "文件名不能为空")
    @ApiModelProperty(value = "附件名称")
    private String fileName;

    /**
     * 附件路径
     */
    @Excel(name = "附件路径")
    @NotBlank(message = "文件不能为空")
    @ApiModelProperty(value = "附件路径")
    private String filePath;

    @ApiModelProperty(value = "e签宝文件上传ID")
    private String esignFileId;

    @ApiModelProperty(value = "e签宝文件上传状态")
    private String esignFileStatus;

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
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
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

    @TableField(exist = false)
    @ApiModelProperty(value ="文件类型名称")
    private String fileTypeName;

    @TableField(exist = false)
    @ApiModelProperty(value ="创建人")
    private String creatorAccountName;
}
