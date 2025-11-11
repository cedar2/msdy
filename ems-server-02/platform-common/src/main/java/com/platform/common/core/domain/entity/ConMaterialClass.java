package com.platform.common.core.domain.entity;

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
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 物料分类对象 s_con_material_class
 *
 * @author linhongwei
 * @date 2021-09-29
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_con_material_class")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConMaterialClass extends EmsBaseEntity {

    /**
     * 客户端口号
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /**
     * 系统ID-节点ID（物料分类）
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-节点ID（物料分类）")
    private Long materialClassSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统ID-节点ID（物料分类）")
    private String sids;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] materialClassSidList;
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(exist = false)
    Long bigClassSid;

    /**
     * 节点编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "节点编码")
    private String materialClassCode;
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(exist = false)
    Long middleClassSid;

    /**
     * 节点名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "节点名称")
    private String materialClassName;

    /**
     * 节点类型
     */
    @ApiModelProperty(value = "节点类型")
    private String nodeType;

    /**
     * 节点类型(需要特殊处理情况，所以另写了这个参数)
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "节点类型")
    private String materialType;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料类型名称")
    private String nodeTypeName;

    /**
     * 节点层级
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "节点层级")
    private Long level;

    /**
     * 节点名称简称
     */
    @ApiModelProperty(value = "节点名称简称")
    private String nodeShortName;

    /**
     * 父节点ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "父节点ID")
    private Long parentCodeSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "上一节点编码")
    private String parentCodeCode;

    /**
     * 上一节点名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "上一节点名称")
    private String parentCodeName;

    /**
     * 物料类别（数据字典的键值或配置档案的编码），用于区分是“物料、商品、服务”分类，只需一级分类维护此字段
     */
    @ApiModelProperty(value = "物料类别（数据字典的键值或配置档案的编码），用于区分是“物料、商品、服务”分类，只需一级分类维护此字段")
    private String materialCategory;

    /**
     * 排序
     */
    @Excel(name = "排序")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "排序")
    private Long nodeSort;
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(exist = false)
    Long smallClassSid;
    /**
     * 节点编码
     */
    @NotBlank(message = "物料分类编码不能为空")
    @Length(max = 8,
            message = "物料分类编码不能超过8个字符")
    @ApiModelProperty(value = "节点编码")
    private String nodeCode;
    /**
     * 节点名称
     */
    @NotBlank(message = "物料分类名称不能为空")
    @Length(max = 300,
            message = "物料分类名称不能超过300个字符")
    @Excel(name = "名称")
    @ApiModelProperty(value = "节点名称")
    private String nodeName;

    /**
     * 创建人账号
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    @Excel(name = "创建人")
    @TableField(exist = false)
    @ApiModelProperty(value = "创建人")
    private String creatorAccountName;
    /**
     * 启用/停用状态
     */
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "启用/停用",
           dictType = "s_valid_flag")
    @ApiModelProperty(value = "启用/停用状态")
    private String status;

    /**
     * 更新人账号
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccount;
    /**
     * 处理状态
     */
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "处理状态",
           dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    /**
     * 确认人账号
     */
    @ApiModelProperty(value = "确认人账号")
    private String confirmerAccount;
    /**
     * 拉链标识（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "拉链标识",
           dictType = "s_zipper_flag")
    @ApiModelProperty(value = "拉链标识（数据字典的键值或配置档案的编码）")
    private String zipperFlag;

    /**
     * 数据源系统
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;

    @TableField(exist = false)
    @ApiModelProperty(value = "子集列表")
    private List<ConMaterialClass> children = new ArrayList<>();

    /**
     * 校验字段
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "校验字段(1-存在,2-不存在)")
    private String checkUnique;
    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8",
                pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间",
           width = 30,
           dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;
    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8",
                pattern = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;
    /**
     * 确认时间
     */
    @JsonFormat(timezone = "GMT+8",
                pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;
}
