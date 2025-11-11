package com.platform.ems.plug.domain;

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
import javax.validation.constraints.NotEmpty;
import java.util.Date;

/**
 * 出入库对应的单据类别对象 s_con_in_out_stock_doc_category
 *
 * @author linhongwei
 * @date 2021-06-15
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_con_in_out_stock_doc_category")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConInOutStockDocCategory extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     *
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "sid")
    private Long sid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] sidList;
    /**
     * 库存凭证类别编码
     */
    @NotBlank(message = "库存凭证类别不能为空")
    @ApiModelProperty(value = "库存凭证类别编码")
    private String invDocCategoryCode;

    @Excel(name = "库存凭证类别名称")
    @TableField(exist = false)
    @ApiModelProperty(value = "库存凭证类别名称")
    private String invDocCategoryName;

    /**
     * 作业类型(移动类型)编码
     */
    @NotBlank(message = "作业类型不能为空")
    @ApiModelProperty(value = "作业类型(移动类型)编码")
    private String movementTypeCode;

    @Excel(name = "作业类型名称")
    @TableField(exist = false)
    private String movementTypeName;

    @Excel(name = "单据类别名称")
    @TableField(exist = false)
    @ApiModelProperty(value = "单据类别")
    private String docCategoryName;

    @TableField(exist = false)
    private String movementTypeSid;

    @Excel(name = "是否允许仓库可编辑", dictType = "s_yesno_flag")
    @ApiModelProperty(value = "是否允许仓库可编辑")
    private String isStorehouseEdit;

    @Excel(name = "是否允许库位可编辑", dictType = "s_yesno_flag")
    @ApiModelProperty(value = "是否允许库位可编辑")
    private String isStorehouseLocationEdit;

    /**
     * 系统SID-库存凭证类别sid
     */
    @NotBlank(message = "单据类别不能为空")
    @ApiModelProperty(value = "单据类别")
    private String docCategoryCode;

    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 启用/停用状态（数据字典的键值）
     */
    @NotEmpty(message = "启停状态不能为空")
    @ApiModelProperty(value = "启用/停用状态（数据字典的键值）")
    private String status;

    /**
     * 处理状态（数据字典的键值）
     */
    @NotEmpty(message = "处理状态不能为空")
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值）")
    private String handleStatus;

    /**
     * 创建人账号（用户名称）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    @Excel(name = "创建人")
    @TableField(exist = false)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccountName;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /**
     * 更新人账号（用户名称）
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 确认人账号（用户名称）
     */
    @ApiModelProperty(value = "确认人账号（用户名称）")
    private String confirmerAccount;

    /**
     * 确认时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 数据源系统（数据字典的键值）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值）")
    private String dataSourceSys;

    /**
     * 是否允许租户自定义
     */
    @ApiModelProperty(value = "是否允许租户自定义")
    private String isClientDefine;

    @TableField(exist = false)
    @ApiModelProperty(value = "单据类型名称")
    private String name;

    /**
     * 序号
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "序号")
    private Long sort;

}
