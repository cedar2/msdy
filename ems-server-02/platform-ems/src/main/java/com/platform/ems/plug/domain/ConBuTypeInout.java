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
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;

/**
 * 业务类型-出入库对象 s_con_bu_type_inout
 *
 * @author linhongwei
 * @date 2022-10-09
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_con_bu_type_inout")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConBuTypeInout extends EmsBaseEntity {

    /**
     * 租户ID
     */
//    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-业务类型(出入库)sid
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-业务类型(出入库)sid")
    private Long sid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] sidList;
    /**
     * 业务类型(出入库)编码
     */
    @Length(max = 8, message = "所属业务类型编码最大长度不能超过8位")
    @Excel(name = "所属业务类型编码")
    @ApiModelProperty(value = "业务类型(出入库)编码")
    private String code;

    /**
     * 业务类型(出入库)名称
     */
    @Length(max = 300, message = "所属业务类型名称最大长度不能超过300位")
    @Excel(name = "所属业务类型名称")
    @ApiModelProperty(value = "业务类型(出入库)名称")
    private String name;

    /**
     * 序号
     */
//    @Excel(name = "序号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "序号")
    private Long sort;

    /**
     * 库存凭证类别编码：出库，入库
     */
//    @Excel(name = "库存凭证类别编码：出库，入库")
    @ApiModelProperty(value = "库存凭证类别编码：出库，入库")
    private String documentCategory;

    /**
     * 库存凭证类别编码数组：出库，入库等
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "库存凭证类别编码数组：出库，入库等")
    private String[] documentCategoryList;

    /**
     * 库存凭证类别名称：出库，入库等
     */
    @Excel(name = "库存凭证类别")
    @TableField(exist = false)
    @ApiModelProperty(value = "库存凭证类别名称：出库，入库等")
    private String documentCategoryName;

    /**
     * 库存凭证类别名称：出库，入库等
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "库存凭证类别名称数组：出库，入库等")
    private List<String> documentCategoryNameList;

    /**
     * 备注
     */
    @Excel(name = "备注")
    @ApiModelProperty(value ="备注")
    private String remark;


    /**
     * 处理状态（数据字典的键值）
     */
    @NotBlank(message = "启停状态不能为空")
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值）")
    private String handleStatus;


    /**
     * 启用/停用状态（数据字典的键值）
     */
    @NotBlank(message = "确认状态不能为空")
    @Excel(name = "启用/停用", dictType = "s_valid_flag")
    @ApiModelProperty(value = "启用/停用状态（数据字典的键值）")
    private String status;


    /**
     * 创建人账号（用户名称）
     */
//    @Excel(name = "创建人账号（用户名称）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;


    /**
     * 创建人数组
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "创建人数组（用于查询）")
    private String[] creatorAccountList;


    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人昵称")
    @TableField(exist = false)
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
//    @Excel(name = "更新人账号（用户名称）")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
//    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 确认人账号（用户名称）
     */
//    @Excel(name = "确认人账号（用户名称）")
    @ApiModelProperty(value = "确认人账号（用户名称）")
    private String confirmerAccount;

    /**
     * 确认时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
//    @Excel(name = "确认时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 数据源系统（数据字典的键值）
     */
//    @Excel(name = "数据源系统（数据字典的键值）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值）")
    private String dataSourceSys;


//    @Excel(name = "更改人")
    @ApiModelProperty(value = "更改人昵称")
    @TableField(exist = false)
    private String updaterAccountName;

//    @Excel(name = "确认人")
    @ApiModelProperty(value = "确认人昵称")
    @TableField(exist = false)
    private String confirmerAccountName;

}
