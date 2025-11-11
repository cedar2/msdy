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
 * 商品-网店运营信息对象 s_bas_material_sale_station
 *
 * @author chenkw
 * @date 2023-01-13
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_bas_material_sale_station")
public class BasMaterialSaleStation extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-商品网店运营信息
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品网店运营信息")
    private Long materialSaleStationSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] materialSaleStationSidList;

    /**
     * 系统SID-物料档案（物料/商品/服务）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料档案（物料/商品/服务）")
    private Long materialSid;

    /**
     * 销售站点/网店sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售站点/网店sid")
    private Long saleStationSid;

    /**
     * 销售站点/网店编码
     */
    @Excel(name = "销售站点/网店编码")
    @ApiModelProperty(value = "销售站点/网店编码")
    private Long saleStationCode;

    /**
     * 销售站点/网店编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "销售站点/网店名称")
    private String saleStationName;

    /**
     * 运营状态（数据字典）
     */
    @Excel(name = "运营状态")
    @ApiModelProperty(value = "运营状态（数据字典）")
    private String operateStatus;

    /**
     * 运营级别（数据字典）
     */
    @Excel(name = "运营级别")
    @ApiModelProperty(value = "运营级别（数据字典）")
    private String operateLevel;


    /**
     * 所属区域
     */
    @TableField(exist = false)
    @Excel(name = "所属区域", dictType = "s_sale_station_region")
    @ApiModelProperty(value = "所属区域")
    private String region;

    /**
     * 电商平台
     */
    @TableField(exist = false)
    @Excel(name = "电商平台", dictType = "s_platform_dianshang")
    @ApiModelProperty(value = "电商平台")
    private String platformDianshang;

    /**
     * 创建人账号（用户名称）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

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
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

    @Excel(name = "更改人")
    @ApiModelProperty(value = "更改人昵称")
    @TableField(exist = false)
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
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "大类")
    private Long bigClassSid;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "中类")
    private Long middleClassSid;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "小类")
    private Long smallClassSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "组别")
    private String groupType;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品编码(款号)")
    private String materialCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品/样品名称")
    private String materialName;

    @TableField(exist = false)
    @ApiModelProperty(value = "我司样衣号(样品号)")
    private String sampleCodeSelf;

}
