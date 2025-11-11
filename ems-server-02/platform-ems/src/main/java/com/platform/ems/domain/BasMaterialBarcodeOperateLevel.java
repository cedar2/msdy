package com.platform.ems.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableId;

import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import lombok.experimental.Accessors;

/**
 * 商品SKU条码-网店运营信息对象 s_bas_material_barcode_operate_level
 *
 * @author chenkw
 * @date 2023-01-18
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_bas_material_barcode_operate_level")
public class  BasMaterialBarcodeOperateLevel extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-商品SKU条码网店运营信息
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品SKU条码网店运营信息")
    private Long materialBarcodeOperateLevelSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] materialBarcodeOperateLevelSidList;

    /**
     * 系统SID-商品SKU条码
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品SKU条码")
    private Long materialBarcodeSid;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品")
    private Long materialSid;

    @TableField(exist = false)
    @Excel(name = "商品编码(款号)")
    @ApiModelProperty(value = "商品编码(款号)")
    private String materialCode;

    @TableField(exist = false)
    @Excel(name = "商品名称")
    @ApiModelProperty(value = "商品名称")
    private String materialName;

    /**
     * 商品SKU条码
     */
    @Excel(name = "商品SKU条码")
    @ApiModelProperty(value = "商品SKU条码")
    private String materialBarcode;

    @TableField(exist = false)
    @Excel(name = "商品SKU编码(ERP)")
    @ApiModelProperty(value = "ERP系统SKU条码编码")
    private String erpMaterialSkuBarcode;

    @Excel(name = "商品MSKU编码(ERP)")
    @ApiModelProperty(value = "商品MSKU编码(ERP)")
    private String erpMaterialMskuCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "样品号")
    private String sampleCodeSelf;

    /**
     * 销售站点/网店sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售站点/网店sid")
    private Long saleStationSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售站点/网店sid 多选")
    private Long[] saleStationSidList;

    /**
     * 销售站点/网店编码
     */
    @ApiModelProperty(value = "销售站点/网店编码")
    private Long saleStationCode;

	@TableField(exist = false)
	@Excel(name = "销售站点/网店")
	@ApiModelProperty(value = "销售站点/网店名称")
	private String saleStationName;

    @TableField(exist = false)
    @Excel(name = "所属区域", dictType = "s_sale_station_region")
    @ApiModelProperty(value = "所属区域")
    private String region;

    @TableField(exist = false)
    @ApiModelProperty(value = "所属区域 多选")
    private String[] regionList;

    @TableField(exist = false)
    @Excel(name = "电商平台", dictType = "s_platform_dianshang")
    @ApiModelProperty(value = "电商平台")
    private String platformDianshang;

    @TableField(exist = false)
    @ApiModelProperty(value = "电商平台 多选")
    private String[] platformDianshangList;

    /**
     * 运营状态（数据字典）
     */
    @ApiModelProperty(value = "运营状态（数据字典）")
    private String operateStatus;

    /**
     * 运营级别（数据字典）
     */
    @Excel(name = "运营级别", dictType = "s_sale_station_operate_level")
    @ApiModelProperty(value = "运营级别（数据字典）")
    private String operateLevel;

    @Excel(name = "产品级别", dictType = "s_product_level")
    @ApiModelProperty(value = "产品级别（数据字典的键值或配置档案的编码）")
    private String productLevel;

    @TableField(exist = false)
    @ApiModelProperty(value = "产品级别（数据字典的键值或配置档案的编码）")
    private String[] productLevelList;

    /**
     * 一次采购标识
     */
    @Excel(name = "一次采购", dictType = "s_yesno_flag")
    @ApiModelProperty(value = "一次采购标识")
    private String firstPuchaseFlag;

    @TableField(exist = false)
    @ApiModelProperty(value = "是否修改一次采购标识")
    private String firstPuchaseFlagIsUpd;

    /**
     * 二次采购标识
     */
    @Excel(name = "二次采购", dictType = "s_yesno_flag")
    @ApiModelProperty(value = "二次采购标识")
    private String secondPuchaseFlag;

    @TableField(exist = false)
    @ApiModelProperty(value = "是否修改二次采购标识")
    private String secondPuchaseFlagIsUpd;

    /**
     * 一次采购到货通知标识
     */
    @Excel(name = "一次采购到货通知", dictType = "s_yesno_flag")
    @ApiModelProperty(value = "一次采购到货通知标识")
    private String arrivalNoticeFlagFirstPurchase;

    @TableField(exist = false)
    @ApiModelProperty(value = "是否修改一次采购到货通知标识")
    private String arrivalNoticeFlagFirstPurchaseIsUpd;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "大类")
    private Long bigClassSid;

    @TableField(exist = false)
    @Excel(name = "大类")
    @ApiModelProperty(value = "大类")
    private String bigClassName;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "中类")
    private Long middleClassSid;

    @TableField(exist = false)
    @Excel(name = "中类")
    @ApiModelProperty(value = "中类")
    private String middleClassName;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "小类")
    private Long smallClassSid;

    @TableField(exist = false)
    @Excel(name = "小类")
    @ApiModelProperty(value = "小类")
    private String smallClassName;

    @TableField(exist = false)
    @Excel(name = "组别", dictType = "s_product_group")
    @ApiModelProperty(value = "组别编码")
    private String groupType;

    @TableField(exist = false)
    @ApiModelProperty(value = "组别编码 多选")
    private String[] groupTypeList;

    @TableField(exist = false)
    @ApiModelProperty(value = "基本单位编码")
    private String unitBase;

    @TableField(exist = false)
    @Excel(name = "基本单位")
    @ApiModelProperty(value = "基本单位")
    private String unitBaseName;

    /**
     * 创建人账号（用户名称）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

	@ApiModelProperty(value = "创建人昵称")
	@TableField(exist = false)
	private String creatorAccountName;

	/**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /**
     * 更新人账号（用户名称）
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

	@ApiModelProperty(value = "更改人昵称")
	@TableField(exist = false)
	private String updaterAccountName;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

}
