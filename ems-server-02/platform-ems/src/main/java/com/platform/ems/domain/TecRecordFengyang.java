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
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * 封样记录(标准封样、产前封样)对象 s_tec_record_fengyang
 *
 * @author linhongwei
 * @date 2021-10-11
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_tec_record_fengyang")
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class TecRecordFengyang extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-封样记录
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-封样记录")
    private Long recordFengyangSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] recordFengyangSidList;
    /**
     * 封样记录单号
     */
    @Excel(name = "封样单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "封样单号")
    private Long recordFengyangCode;

    /**
     * 我司样衣号
     */
    @TableField(exist = false)
    @Excel(name = "我司样衣号")
    @ApiModelProperty(value = "我司样衣号")
    private String sampleCodeSelf;

    /**
     * 供方样衣号
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "供方样衣号")
    private String sampleCodeVendor;

    /**
     * 商品编码（款号）
     */
    @TableField(exist = false)
    @Excel(name = "商品编码（款号）")
    @ApiModelProperty(value = "商品编码（款号）")
    private String materialCode;

    @TableField(exist = false)
    @Excel(name = "商品名称")
    @ApiModelProperty(value = "商品名称")
    private String materialName;

    /**
     * 图片
     */
    @TableField(exist = false)
    @Excel(name = "图片")
    @ApiModelProperty(value = "图片")
    private String picturePath;

    @Excel(name = "颜色")
    @TableField(exist = false)
    @ApiModelProperty(value = "颜色名称")
    private String skuName;

    /**
     * 封样类型
     */
    @NotEmpty(message = "封样类型不能为空")
    @Excel(name = "封样类型", dictType = "s_fengyang_type")
    @ApiModelProperty(value = "封样类型")
    private String fengyangType;

    /**
     * 样品/商品sid
     */
    @NotNull(message = "商品不能为空")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "样品/商品sid")
    private Long sampleSid;

    /**
     * 样品/商品颜色sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "样品/商品颜色sid")
    private Long skuSid;

    /**
     * 标准封样结果（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "封样结果", dictType = "s_fengyang_result")
    @ApiModelProperty(value = "封样结果（数据字典的键值或配置档案的编码）")
    private String fengyangResult;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品类型")
    private String materialType;

    @Excel(name = "商品类型")
    @TableField(exist = false)
    @ApiModelProperty(value = "商品类型名称")
    private String materialTypeName;

    /**
     * 系统ID-产品季档案
     */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-产品季档案")
    private Long productSeasonSid;

    /**
     * 产品季名称
     */
    @TableField(exist = false)
    @Excel(name = "产品季")
    @ApiModelProperty(value = "产品季名称")
    private String productSeasonName;

    /**
     * 商品分类sid
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "商品分类sid")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long materialClassSid;

    /**
     * 商品分类名称
     */
    @Excel(name = "商品分类")
    @TableField(exist = false)
    @ApiModelProperty(value = "商品分类名称")
    private String nodeName;

    /**
     * 设计师账号
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "设计师账号")
    private String designerAccount;

    @Excel(name = "设计师")
    @TableField(exist = false)
    @ApiModelProperty(value = "设计师名称")
    private String designerAccountName;

    /**
     * 版型sid
     */
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

    /**
     * 上下装/套装
     */
    @TableField(exist = false)
    @Excel(name = "上下装/套装", dictType = "s_up_down_suit")
    @ApiModelProperty(value = "上下装/套装")
    private String upDownSuit;

    /**
     * 客户sid）
     */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "客户sid）")
    private Long customerSid;

    /**
     * 客户名称
     */
    @TableField(exist = false)
    @Excel(name = "客户")
    @ApiModelProperty(value = "客户名称")
    private String customerName;

    /**
     * 客方商品编码
     */
    @TableField(exist = false)
    @Excel(name = "客方商品编码")
    @ApiModelProperty(value = "客方商品编码")
    private String customerProductCode;

    /**
     * 客方样衣号
     */
    @TableField(exist = false)
    @Excel(name = "客方样衣号")
    @ApiModelProperty(value = "客方样衣号")
    private String sampleCodeCustomer;

    /**
     * 快速编码
     */
    @TableField(exist = false)
    @Excel(name = "快速编码")
    @ApiModelProperty(value = "快速编码")
    private String simpleCode;

    /**
     * 公司编码（公司档案的sid）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "公司sid")
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

    /**
     * 基本计量单位
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "基本计量单位")
    private String unitBase;

    /**
     * 基本计量单位名称
     */
    @Excel(name = "基本计量单位")
    @ApiModelProperty(value = "基本计量单位名称")
    @TableField(exist = false)
    private String unitBaseName;

    /**
     * 标准封样说明
     */
    @ApiModelProperty(value = "标准封样说明")
    private String fengyangComment;

    /**
     * 处理状态（数据字典的键值或配置档案的编码）
     */
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    /**
     * 创建人账号（用户名称）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    /**
     * 创建人账号（用户名称）
     */
    @TableField(exist = false)
    @Excel(name = "创建人")
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
     * 更新人账号（用户名称）
     */
    @TableField(exist = false)
    @Excel(name = "更改人")
    @ApiModelProperty(value = "更新人账号（用户名称）")
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
     * 确认人账号（用户名称）
     */
    @ApiModelProperty(value = "确认人账号（用户名称）")
    private String confirmerAccount;

    /**
     * 确认人账号（用户名称）
     */
    @TableField(exist = false)
//    @Excel(name = "确认人")
    @ApiModelProperty(value = "确认人账号（用户名称）")
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

    /**
     * 供应商sid
     */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "供应商sid")
    private Long vendorSid;

    /**
     * 供应商名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "供应商名称")
    private String vendorName;

    /**
     * 客方品牌sid
     */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "客方品牌sid")
    private Long customerBrandSid;

    /**
     * 客方品牌名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "客方品牌名称")
    private String customerBrandName;

    @TableField(exist = false)
    private Long[] productSeasonSidList;

    @TableField(exist = false)
    private Long[] customerSidList;

    @TableField(exist = false)
    private String[] creatorAccountList;

    @TableField(exist = false)
    private String[] materialTypeList;

    /**
     * 封样记录-附件对象
     */
    @Valid
    @TableField(exist = false)
    @ApiModelProperty(value = "封样记录-附件对象")
    private List<TecRecordFengyangAttach> attachList;

}
