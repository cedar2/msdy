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
import com.platform.ems.domain.base.CreatorInfo;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 货架档案-物料分类明细对象 s_bas_goods_shelf_material_class
 *
 * @author linhongwei
 * @date 2023-02-02
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_bas_goods_shelf_material_class")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasGoodsShelfMaterialClass extends EmsBaseEntity implements CreatorInfo {

    /**
     * 客户端口号
     */
    @Excel(name = "客户端口号")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /**
     * 系统SID-货架档案明细
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-货架档案明细")
    private Long goodsShelfMaterialClassSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] goodsShelfMaterialClassSidList;
    /**
     * 系统SID-货架档案
     */
    @Excel(name = "系统SID-货架档案")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-货架档案")
    private Long goodsShelfSid;

    /**
     * 货架档案编码
     */
    @Excel(name = "货架档案编码")
    @ApiModelProperty(value = "货架档案编码")
    private String goodsShelfCode;

    /**
     * 物料分类sid（物料分类/商品分类/服务分类）
     */
    @Excel(name = "物料分类sid（物料分类/商品分类/服务分类）")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "物料分类sid（物料分类/商品分类/服务分类）")
    private Long materialClassSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料分类sid（物料分类/商品分类/服务分类）")
    private Long[] materialClassSidList;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "库位SID")
    private Long storehouseLocationSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "库位SID-多选")
    private Long[] storehouseLocationSidList;

    /**
     * 物料分类编码
     */
    @Excel(name = "物料分类编码")
    @ApiModelProperty(value = "物料分类编码")
    private String materialClassCode;

    /**
     * 创建人账号
     */
    @Excel(name = "创建人账号")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

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
     * 数据源系统
     */
    @Excel(name = "数据源系统")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;


    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人昵称")
    @TableField(exist = false)
    private String creatorAccountName;

}
