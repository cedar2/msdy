package com.platform.ems.domain;

import java.util.Date;
import java.util.List;

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


/**
 * 商品成本核算-BOM主对象 s_cos_product_cost_bom
 *
 * @author qhq
 * @date 2021-04-25
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_cos_product_cost_bom")
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class CosProductCostBom extends EmsBaseEntity {
    /** 租户ID */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /** 商品成本BOM sid */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "商品成本BOM sid")
    private Long productCostBomSid;

    /** 系统SID-成品/半成品成本核算 */
    @Excel(name = "系统SID-成品/半成品成本核算")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-成品/半成品成本核算")
    private Long productCostSid;

    /** 系统SID-商品BOM档案 */
    @Excel(name = "系统SID-商品BOM档案")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品BOM档案")
    private Long bomSid;

    /** 商品编码sid（物料/商品/服务） */
    @Excel(name = "商品编码sid（物料/商品/服务）")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "商品编码sid（物料/商品/服务）")
    private Long materialSid;

    /** 商品SKU1档案sid */
    @Excel(name = "商品SKU1档案sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "商品SKU1档案sid")
    private Long sku1Sid;

    /** 商品SKU2档案sid */
    @Excel(name = "商品SKU2档案sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "商品SKU2档案sid")
    private Long sku2Sid;

    /** BOM版本号 */
    @Excel(name = "BOM版本号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "BOM版本号")
    private Long bomVersionId;

    /** 获取BOM版本的时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "获取BOM版本的时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "获取BOM版本的时间")
    private Date bomVersionDateGet;

    /** 获取BOM版本的用户名称 */
    @Excel(name = "获取BOM版本的用户名称")
    @ApiModelProperty(value = "获取BOM版本的用户名称")
    private String bomVersionNameGet;

    /** 创建人账号（用户名称） */
    @Excel(name = "创建人账号（用户名称）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    /** 创建时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /** 更新人账号（用户名称） */
    @Excel(name = "更新人账号（用户名称）")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

    /** 更新时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /** 确认人账号（用户名称） */
    @Excel(name = "确认人账号（用户名称）")
    @ApiModelProperty(value = "确认人账号（用户名称）")
    private String confirmerAccount;

    /** 确认时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "确认时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /** 数据源系统（数据字典的键值） */
    @Excel(name = "数据源系统（数据字典的键值）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值）")
    private String dataSourceSys;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "创建日期开始时间")
    @TableField(exist = false)
    private String beginTime;

    @ApiModelProperty(value = "创建日期结束时间")
    @TableField(exist = false)
    private String endTime;

    @ApiModelProperty(value = "页数")
    @TableField(exist = false)
    private Integer pageNum;

    @ApiModelProperty(value = "每页个数")
    @TableField(exist = false)
    private Integer pageSize;

    /**
     * 每条bom对应的物料信息
     */
    @TableField(exist = false)
    private List<CosProductCostMaterial> costMaterialList;
}
