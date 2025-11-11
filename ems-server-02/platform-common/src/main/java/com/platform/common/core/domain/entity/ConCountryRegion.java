package com.platform.common.core.domain.entity;

import java.util.ArrayList;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableId;
import java.util.List;

import com.platform.common.core.domain.EmsBaseEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import javax.validation.constraints.NotEmpty;
import lombok.experimental.Accessors;

/**
 * 国家区域对象 s_con_country_region
 *
 * @author linhongwei
 * @date 2021-06-25
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_con_country_region")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConCountryRegion extends EmsBaseEntity {

    /** 客户端口号 */
    @Excel(name = "客户端口号")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /** 系统ID-节点ID（国家区域） */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-节点ID（国家区域）")
    private Long countryRegionSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long [] countryRegionSidList;
    /** 节点ID（原先是node_code) */
    @Excel(name = "节点ID（原先是node_code)")
    @ApiModelProperty(value = "节点ID（原先是node_code)")
    private String nodeId;

    /** 节点名称 */
    @Excel(name = "节点名称")
    @ApiModelProperty(value = "节点名称")
    private String nodeName;

    /** 节点类型 */
    @Excel(name = "节点类型")
    @ApiModelProperty(value = "节点类型")
    private String nodeType;

    /** 节点层级 */
    @Excel(name = "节点层级")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "节点层级")
    private Long level;

    /** 节点名称简称 */
    @Excel(name = "节点名称简称")
    @ApiModelProperty(value = "节点名称简称")
    private String nodeShortName;

    /** 上一级节点ID */
    @Excel(name = "上一级节点ID")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "上一级节点ID")
    private Long parentCodeId;

    /** 节点名称简称拼音 */
    @Excel(name = "节点名称简称拼音")
    @ApiModelProperty(value = "节点名称简称拼音")
    private String pinyin;

    /** 城市编码 */
    @Excel(name = "城市编码")
    @ApiModelProperty(value = "城市编码")
    private String citycode;

    /** 邮政编码 */
    @Excel(name = "邮政编码")
    @ApiModelProperty(value = "邮政编码")
    private String yzcode;

    /** 启用/停用状态 */
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "启用/停用状态",dictType = "s_valid_flag")
    @ApiModelProperty(value = "启用/停用状态")
    private String status;

    /** 处理状态 */
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "处理状态",dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    /** 创建人账号 */
    @Excel(name = "创建人账号")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    /** 创建时间 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /** 更新人账号 */
    @Excel(name = "更新人账号")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccount;

    /** 更新时间 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /** 确认人账号 */
    @Excel(name = "确认人账号")
    @ApiModelProperty(value = "确认人账号")
    private String confirmerAccount;

    /** 确认时间 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "确认时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /** 数据源系统 */
    @Excel(name = "数据源系统")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;


    @TableField(exist = false)
    @ApiModelProperty(value ="子集列表")
    private List<ConCountryRegion> children = new ArrayList<>();


}
