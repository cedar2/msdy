package com.platform.ems.domain.base;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * 合同模板信息 s_contract_template_attach
 *
 * @author chenkw
 * @date 2023-09-05
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_contract_template_attach")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContractTemplateAttach {

    /**
     * 客户端口号
     */
    @Excel(name = "客户端口号")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /**
     * 系统自增长ID-采购合同附件信息
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-采购合同附件信息")
    private Long contractTemplateAttachSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] contractTemplateAttachSidList;

    /**
     * 系统ID-表单id
     */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-表单id")
    private Long formSid;

    /**
     * 对象类别编码
     */
    @NotBlank(message = "对象类别编码不能为空")
    @ApiModelProperty(value = "对象类别编码")
    private String objectCategory;

    /**
     * 对象类别
     */
    @TableField(exist = false)
    @Excel(name = "对象类别")
    @ApiModelProperty(value = "对象类别")
    private String objectCategoryName;

    /**
     * 附件类型（数据字典的键值）
     */
    @NotBlank(message = "附件类型不能为空")
    @ApiModelProperty(value = "附件类型（数据字典的键值）")
    private String fileType;

    @TableField(exist = false)
    @Excel(name = "附件类型")
    @ApiModelProperty(value ="文件类型名称")
    private String fileTypeName;

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

    @ApiModelProperty(value ="备注")
    private String remark;

    /**
     * 创建人账号
     */
    @Excel(name = "创建人账号")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    @TableField(exist = false)
    @ApiModelProperty(value ="创建人")
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
    @ApiModelProperty(value ="创建日期开始时间")
    private String beginTime;

    @TableField(exist = false)
    @ApiModelProperty(value ="创建日期结束时间")
    private String endTime;

    @TableField(exist = false)
    @ApiModelProperty(value ="每页个数")
    private Integer pageNum;

    @TableField(exist = false)
    @ApiModelProperty(value ="每页个数")
    private Integer pageSize;

}
