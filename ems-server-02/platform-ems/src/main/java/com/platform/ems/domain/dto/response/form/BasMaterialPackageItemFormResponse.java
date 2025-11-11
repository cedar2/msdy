package com.platform.ems.domain.dto.response.form;

import com.baomidou.mybatisplus.annotation.TableField;
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

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 辅料包明细报表 BasMaterialPackageItemFormResponse
 *
 * @author chenkaiwen
 * @date 2021-11-12
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasMaterialPackageItemFormResponse extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "物料（商品/服务）")
    private Long materialSid;

    @ApiModelProperty(value = "系统ID-常规辅料包明细")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long materialPackItemSid;

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @Excel(name = "物料包编码")
    @ApiModelProperty(value = "物料包编码")
    private String packageCode;

    @Excel(name = "物料包名称")
    @ApiModelProperty(value = "辅料包名称")
    private String packageName;

    @Excel(name = "序号")
    @ApiModelProperty(value = "序号")
    private BigDecimal sort;

    @Excel(name = "物料编码")
    @ApiModelProperty(value = "物料编码")
    private String materialCode;

    @Excel(name = "物料名称")
    @ApiModelProperty(value = "物料名称")
    private String materialName;

    @Excel(name = "SKU1名称")
    @ApiModelProperty(value = "SKU名称")
    private String sku1Name;

    // wp 添加SKU2名称
    @Excel(name = "SKU2名称")
    @ApiModelProperty(value = "SKU2名称")
    private String sku2Name;

    @Excel(name = "用量")
    @ApiModelProperty(value = "用量")
    private BigDecimal quantity;

    @Excel(name = "计量单位")
    @ApiModelProperty(value = "计量单位")
    private String unitName;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "启用/停用状态")
    private String status;

    @Excel(name = "启用/停用",dictType = "s_valid_flag")
    @ApiModelProperty(value = "物料启用/停用状态")
    private String materialStatus;

    @Excel(name = "处理状态",dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @ApiModelProperty(value = "物料处理状态")
    private String materialHandleStatus;

    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccountName;

    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @ApiModelProperty(value = "更新人账号")
    private String updaterAccount;

    @ApiModelProperty(value = "更新人账号")
    private String updaterAccountName;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    @ApiModelProperty(value = "确认人账号")
    private String confirmerAccount;

    @ApiModelProperty(value = "确认人账号")
    private String confirmerAccountName;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认日期")
    private String confirmDate;

    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;

    @ApiModelProperty(value = "物料类型名称")
    private String materialType;

    @ApiModelProperty(value = "物料类型名称")
    private String materialTypeName;

    @ApiModelProperty(value = "采购类型名称")
    private String purchaseType;

    @ApiModelProperty(value = "采购类型名称")
    private String purchaseTypeName;

    @ApiModelProperty(value = "供方编码")
    private String supplierProductCode;

    @ApiModelProperty(value = "BOM用量取整方式 / BOM用量取整方式")
    private String roundingType;

    @ApiModelProperty(value = "用量计量单位名称（数据字典的键值），用于保存BOM用量计量量单位")
    private String unitQuantity;

    @ApiModelProperty(value = "用量计量单位名称（数据字典的键值），用于保存BOM用量计量量单位")
    private String unitQuantityName;

    @ApiModelProperty(value = "主图片路径")
    private String picturePath;

    @ApiModelProperty(value = "所属生产环节（数据字典的键值或配置档案的编码），如：车间、后道、包装")
    private String touseProduceStage;

    @ApiModelProperty(value = "所属生产环节（数据字典的键值或配置档案的编码），如：车间、后道、包装")
    private String touseProduceStageName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "所属生产环节（数据字典的键值或配置档案的编码），如：车间、后道、包装")
    private Long vendorSid;

    @ApiModelProperty(value = "所属生产环节（数据字典的键值或配置档案的编码），如：车间、后道、包装")
    private String vendorName;

    @ApiModelProperty(value = "所属生产环节（数据字典的键值或配置档案的编码），如：车间、后道、包装")
    private String vendorShortName;

    @ApiModelProperty(value = "幅宽（厘米）")
    private String width;

    @ApiModelProperty(value = "克重")
    private String gramWeight;

    @ApiModelProperty(value = "纱支")
    private String yarnCount;

    @ApiModelProperty(value = "密度")
    private String density;

    @ApiModelProperty(value = "成分")
    private String composition;

    @ApiModelProperty(value = "规格")
    private String specificationSize;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "物料（商品/服务）分类编码")
    private Long materialClassSid;

    @ApiModelProperty(value = "物料（商品/服务）分类编码")
    private String materialClassName;

    @ApiModelProperty(value = "基本计量单位编码")
    private String unitBase;

    @ApiModelProperty(value = "基本计量单位编码")
    private String unitBaseName;
}
