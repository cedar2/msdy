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

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.List;

/**
 * 打样准许单对象 s_dev_make_sample_form
 *
 * @author linhongwei
 * @date 2022-03-24
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_dev_make_sample_form")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DevMakeSampleForm extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-打样准许单
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-打样准许单")
    private Long makeSampleFormSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] makeSampleFormSidList;
    /**
     * 打样准许单号
     */
    @Excel(name = "打样准许单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "打样准许单号")
    private Long makeSampleFormCode;

    @TableField(exist = false)
    @Excel(name = "我司样衣号")
    @ApiModelProperty(value = "我司样衣号")
    private String sampleCodeSelf;

    /**
     * 系统ID-产品季档案
     */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-产品季档案")
    private Long productSeasonSid;

    @TableField(exist = false)
    private Long[] productSeasonSidList;

    /**
     * 产品季名称
     */
    @TableField(exist = false)
    @Excel(name = "产品季")
    @ApiModelProperty(value = "产品季名称")
    private String productSeasonName;

    /**
     * 产品季编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "产品季编码")
    private String productSeasonCode;

    @TableField(exist = false)
    @Excel(name = "季节", dictType = "s_season")
    @ApiModelProperty(value = "季节编码")
    private String season;

    @TableField(exist = false)
    private String[] seasonList;

    @TableField(exist = false)
    @ApiModelProperty(value = "设计师账号")
    private String designerAccount;

    @TableField(exist = false)
    private String[] designerAccountList;

    @TableField(exist = false)
    @Excel(name = "设计师")
    @ApiModelProperty(value = "设计师名称")
    private String designerAccountName;

    @TableField(exist = false)
    @ApiModelProperty(value = "样品分类sid")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long materialClassSid;

    @TableField(exist = false)
    private Long[] materialClassSidList;

    @TableField(exist = false)
    @Excel(name = "样品分类")
    @ApiModelProperty(value = "样品分类名称")
    private String materialClassName;

    @TableField(exist = false)
    @Excel(name = "款式", dictType = "s_kuan_type")
    @ApiModelProperty(value = "款式编码")
    private String kuanType;

    @TableField(exist = false)
    @Excel(name = "风格", dictType = "s_style")
    @ApiModelProperty(value = "风格编码")
    private String style;

    @TableField(exist = false)
    @Excel(name = "系列", dictType = "s_series")
    @ApiModelProperty(value = "系列编码")
    private String series;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "版型sid")
    private Long modelSid;

    /**
     * 版型名称
     */
    @TableField(exist = false)
    @Excel(name = "版型")
    @ApiModelProperty(value = "版型名称")
    private String modelName;

    @TableField(exist = false)
    @ApiModelProperty(value = "版型编码")
    private String modelCode;

    @Excel(name = "版型类型", dictType = "s_model_type")
    @TableField(exist = false)
    @ApiModelProperty(value = "版型类型")
    private String modelType;

    @TableField(exist = false)
    private String[] modelTypeList;

    /**
     * 公司编码（公司档案的sid）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "公司编码（公司档案的sid）")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long companySid;

    /**
     * 公司名称
     */
    @TableField(exist = false)
    @Excel(name = "公司")
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    /**
     * 公司代码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "公司代码")
    private String companyCode;

    /**
     * 公司品牌sid
     */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司品牌sid")
    private Long companyBrandSid;

    /**
     * 公司品牌名称
     */
    @Excel(name = "品牌")
    @TableField(exist = false)
    @ApiModelProperty(value = "公司品牌名称")
    private String companyBrandName;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司品标sid")
    private Long companyBrandMarkSid;

    @Excel(name = "品标")
    @TableField(exist = false)
    @ApiModelProperty(value = "品标名称")
    private String brandMarkName;

    /**
     * 样品/商品sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "样品/商品sid")
    private Long productSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "样品名称")
    private String materialName;

    /**
     * 批复结果（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "批复结果（数据字典的键值或配置档案的编码）")
    private String approveStatus;

    /**
     * 是否主推款
     */
    @Excel(name = "是否主推款", dictType = "sys_yes_no")
    @ApiModelProperty(value = "是否主推款")
    private String isRecommend;

    /**
     * 当前审批节点名称
     */
    @Excel(name = "当前审批节点")
    @ApiModelProperty(value = "当前审批节点名称")
    @TableField(exist = false)
    private String approvalNode;

    /**
     * 当前审批人
     */
    @Excel(name = "当前审批人")
    @ApiModelProperty(value = "当前审批人")
    @TableField(exist = false)
    private String approvalUserName;

    /**
     * 提交人
     */
    @ApiModelProperty(value = "提交人")
    @TableField(exist = false)
    private String submitUserName;

    /**
     * 提交日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "提交日期")
    @TableField(exist = false)
    private Date submitDate;

    /**
     * 当前审批人id
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "当前审批人id")
    private String approvalUserId;

    /**
     * 评语
     */
    @ApiModelProperty(value = "评语")
    private String approveComment;

    /**
     * 处理状态（数据字典的键值或配置档案的编码）
     */
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    @TableField(exist = false)
    private String[] handleStatusList;

//    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 创建人账号（用户账号）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户账号）")
    private String creatorAccount;

    @TableField(exist = false)
    @Excel(name = "创建人")
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
     * 更新人账号（用户账号）
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户账号）")
    private String updaterAccount;

    @TableField(exist = false)
    @Excel(name = "更改人")
    private String updaterAccountName;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更改日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 确认人账号（用户账号）
     */
    @ApiModelProperty(value = "确认人账号（用户账号）")
    private String confirmerAccount;

    @TableField(exist = false)
//    @Excel(name = "确认人")
    private String confirmerAccountName;

    /**
     * 确认时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
//    @Excel(name = "确认日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    @Valid
    @TableField(exist = false)
    @ApiModelProperty(value = "打样准许单-附件")
    private List<DevMakeSampleFormAttach> attachList;

}
