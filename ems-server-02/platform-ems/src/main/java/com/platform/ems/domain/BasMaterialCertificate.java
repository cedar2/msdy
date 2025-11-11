package com.platform.ems.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * 商品合格证洗唛信息对象 s_bas_material_certificate
 *
 * @author linhongwei
 * @date 2021-03-19
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_bas_material_certificate")
public class BasMaterialCertificate extends EmsBaseEntity {

    /**
     * 客户端口号
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /**
     * 系统ID-商品合格证洗唛信息
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-商品合格证洗唛信息")
    private Long materialCertificateSid;

    /**
     * 系统ID-商品档案
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-商品档案")
    private Long materialSid;

    /**
     * 物料（商品/服务）编码
     */
    @TableField(exist = false)
    @Excel(name = "商品编码(款号)")
    @ApiModelProperty(value = "物料（商品/服务）编码")
    private String materialCode;

    /**
     * 物料（商品/服务）名称
     */
    @TableField(exist = false)
    @Excel(name = "商品名称")
    @ApiModelProperty(value = "物料（商品/服务）名称")
    private String materialName;

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
     * 产品季编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "产品季编码")
    private String productSeasonCode;

    /**
     * 建议零售价（元）
     */
    @Excel(name = "建议零售价")
    @ApiModelProperty(value = "建议零售价（元）")
    private String suggestedPrice;

    /**
     * 等级编码
     */
    @Excel(name = "等级", dictType = "s_grade_product")
    @ApiModelProperty(value = "等级编码")
    private String grade;

    /**
     * 执行标准编码
     */
    @Excel(name = "执行标准", dictType = "s_executive_standard")
    @ApiModelProperty(value = "执行标准编码")
    private String executiveStandard;

    /**
     * 执行标准编码（套装下装）
     */
    @Excel(name = "执行标准（套装下装）", dictType = "s_executive_standard")
    @ApiModelProperty(value = "执行标准编码（套装下装）")
    private String executiveStandardBottoms;

    /**
     * 安全类别编码
     */
    @Excel(name = "安全类别", dictType = "s_safe_category")
    @ApiModelProperty(value = "安全类别编码")
    private String safeCategory;

    /**
     * 产地
     */
    @Excel(name = "产地")
    @ApiModelProperty(value = "产地")
    private String productPlace;

    /**
     * 检验员
     */
    @Excel(name = "检验员")
    @ApiModelProperty(value = "检验员")
    private String checker;

    /**
     * 生产日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "生产日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "生产日期")
    private Date productDate;

    /**
     * 制造商
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "制造商")
    private Long manufacturer;

    /**
     * 制造商名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "制造商名称")
    private String vendorName;

    @TableField(exist = false)
    @Excel(name = "制造商")
    @ApiModelProperty(value = "制造商简称")
    private String manufacturerShortName;

    /**
     * 公司名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @TableField(exist = false)
    @Excel(name = "公司")
    @ApiModelProperty(value = "公司简称")
    private String companyShortName;

    /**
     * 公司品牌名称
     */
    @TableField(exist = false)
    @Excel(name = "品牌")
    @ApiModelProperty(value = "公司品牌名称")
    private String companyBrandName;

    /**
     * 我司样衣号
     */
    @Excel(name = "我司样衣号")
    @TableField(exist = false)
    @ApiModelProperty(value = "我司样衣号")
    private String sampleCodeSelf;

    /**
     * 客户名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "客户名称")
    private String customerName;

    @TableField(exist = false)
    @Excel(name = "客户")
    @ApiModelProperty(value = "客户简称")
    private String customerShortName;

    /**
     * 客方品牌名称
     */
    @TableField(exist = false)
    @Excel(name = "客方品牌")
    @ApiModelProperty(value = "客方品牌名称")
    private String customerBrandName;

    /**
     * 客方编码（物料/商品/服务）
     */
    @Excel(name = "客方商品编码")
    @TableField(exist = false)
    @ApiModelProperty(value = "客方编码（物料/商品/服务）")
    private String customerProductCode;

    /**
     * 客方样衣号
     */
    @Excel(name = "客方样衣号")
    @TableField(exist = false)
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
     * 版型档案sid
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "版型档案sid")
    private String modelSid;

    @TableField(exist = false)
    @Excel(name = "版型")
    @ApiModelProperty(value = "版型档案")
    private String modelName;

    /**
     * 设计师账号
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "设计师账号")
    private String designerAccount;

    @TableField(exist = false)
    @Excel(name = "设计师")
    @ApiModelProperty(value = "设计师")
    private String designerAccountName;

    /**
     * 上下装/套装（数据字典的键值）
     */
    @TableField(exist = false)
    @Excel(name = "上下装/套装", dictType = "s_up_down_suit")
    @ApiModelProperty(value = "上下装/套装（数据字典的键值）")
    private String upDownSuit;

    /**
     * 物料/商品/服务类型编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "物料/商品/服务类型编码")
    private String materialType;

    @TableField(exist = false)
    @Excel(name = "商品类型")
    @ApiModelProperty(value = "物料/商品/服务类型编码")
    private String materialTypeName;

    /**
     * 处理状态
     */
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    /**
     * 存放图标集
     */
    @ApiModelProperty(value = "存放图标集")
    private String iconGroup;

    /**
     * 检测成分
     */
    @ApiModelProperty(value = "检测成分")
    private String detectComposition;

    /**
     * 图片路径
     */
    @ApiModelProperty(value = "图片路径")
    private String picturePath;

    /**
     * 备注
     */
    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 创建人账号
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    @Excel(name = "创建人")
    @TableField(exist = false)
    @ApiModelProperty(value = "创建人账号")
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
     * 更新人账号
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccount;

    @Excel(name = "更改人")
    @TableField(exist = false)
    @ApiModelProperty(value = "更新人账号")
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
     * 确认人账号
     */
    @ApiModelProperty(value = "确认人账号")
    private String confirmerAccount;

    @Excel(name = "确认人")
    @TableField(exist = false)
    @ApiModelProperty(value = "确认人账号")
    private String confirmerAccountName;

    /**
     * 确认时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "确认日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 数据源系统
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;

    /**
     * 公司档案sid
     */
    @ApiModelProperty(value = "公司档案sid")
    private Long companySid;

    /**
     * 公司代码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "公司代码")
    private String companyCode;

    /**
     * 是否套装
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "是否套装")
    private String isSuit;

    /**
     * 款式编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "款式编码")
    private String kuanType;

    /**
     * 启用/停用状态
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "启用/停用状态")
    private String status;

    @TableField(exist = false)
    @ApiModelProperty(value = "开始时间")
    private String beginTime;

    @TableField(exist = false)
    @ApiModelProperty(value = "结束时间")
    private String endTime;

    /**
     * 公司品牌编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "公司品牌编码")
    private String companyBrandCode;

    /**
     * 客户（定制）sid）
     */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "客户（定制）sid）")
    private Long customerSid;

    /**
     * 客户编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "客户编码")
    private String customerCode;

    /**
     * 客方品牌编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "客方品牌编码")
    private String customerBrandCode;

    /**
     * 供应商编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "供应商编码")
    private String vendorCode;

    /**
     * 系统ID-商品合格证洗唛信息list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "系统ID-商品合格证洗唛信息list")
    private Long[] materialCertificateSidList;

    /**
     * 物料档案sids
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "物料档案sids")
    private List<String> materialSids;

    /**
     * 商品SKU实测成分对象
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "商品SKU实测成分对象")
    private List<BasMaterialSkuComponent> basMaterialSkuComponentList;

    /**
     * 商品SKU羽绒充绒量对象
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "商品SKU羽绒充绒量对象")
    private List<BasMaterialSkuDown> basMaterialSkuDownList;

    /**
     * 商品合格证洗唛自定义字段对象
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "商品合格证洗唛自定义字段对象")
    private List<BasMaterialCertificateFieldValue> basMaterialCertificateFieldValueList;

    /**
     * 商品合格证洗唛-附件对象
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "商品合格证洗唛-附件对象")
    private List<BasMaterialCertificateAttachment> attachmentList;

    /**
     * 系统ID-产品季档案list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "系统ID-产品季档案list")
    private Long[] productSeasonSidList;

    /**
     * 系统自增长ID-客户信息list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "系统自增长ID-客户信息list")
    private Long[] customerSidList;

    /**
     * 处理状态list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态list")
    private String[] handleStatusList;

    /**
     * 上下装/套装list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "上下装/套装list")
    private String[] upDownSuitList;

    /**
     * 版型档案list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "版型档案list")
    private String[] modelSidList;

    /**
     * 物料/商品/服务类型编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "物料/商品/服务类型编码")
    private String[] materialTypeList;

    /**
     * 系统ID-公司档案list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "系统ID-公司档案list")
    private Long[] companySidList;
}
