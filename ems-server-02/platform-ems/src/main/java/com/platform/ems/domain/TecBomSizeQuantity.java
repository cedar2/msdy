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
import com.platform.common.core.domain.BaseEntity;
import com.platform.common.core.domain.EmsBaseEntity;
import com.platform.ems.domain.dto.request.TecBomSizeSkuRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


/**
 * 物料清单（BOM）组件具体尺码用量对象 s_tec_bom_size_quantity
 *
 * @author yangqz
 * @date 2021-07-20
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_tec_bom_size_quantity")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TecBomSizeQuantity extends EmsBaseEntity{

    /** 客户端口号 */
    @Excel(name = "客户端口号")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /** 系统ID-物料BOM组件具体尺码用量 */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-物料BOM组件具体尺码用量")
    private Long bomSizeQuantitySid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long [] bomSizeQuantitySidList;

    /** 系统ID-物料BOM组件明细 */
    @Excel(name = "系统ID-物料BOM组件明细")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-物料BOM组件明细")
    private Long bomItemSid;

    /** 上级商品SKU档案 */
    @Excel(name = "上级商品SKU档案")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "上级商品SKU档案")
    private Long skuSid;

    /** 上级商品SKU类型编码 */
    @Excel(name = "上级商品SKU类型编码")
    @ApiModelProperty(value = "上级商品SKU类型编码")
    private String skuType;

    @Excel(name = "sku名称")
    @TableField(exist = false)
    @ApiModelProperty(value = "上级商品SKU类型编码")
    private String skuName;

    /** 用量 */
    @Excel(name = "用量")
    @ApiModelProperty(value = "用量")
    private BigDecimal quantity;

    /** 用量计量单位编码 */
    @Excel(name = "用量计量单位编码")
    @ApiModelProperty(value = "用量计量单位编码")
    private String unitQuantity;

    /** 商品编码sid（物料/商品/服务） */
    @Excel(name = "商品编码sid（物料/商品/服务）")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "商品编码sid（物料/商品/服务）")
    private Long materialSid;

    /** 系统SID-商品BOM档案 */
    @Excel(name = "系统SID-商品BOM档案")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品BOM档案")
    private Long bomSid;

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
    private  List<TecBomSizeSkuRequest>  skuList;


}
