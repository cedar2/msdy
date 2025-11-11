package com.platform.ems.domain.dto.response;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;
import com.platform.ems.domain.dto.request.TecBomSizeSkuRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @description: bom尺码拉链长度-详情
 * @author: yang
 * @date: 2021-07-20
 */
@Data
@ApiModel
@Accessors(chain = true)
public class TecBomSizeRequestResponse extends EmsBaseEntity implements Serializable {

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-物料BOM组件具体尺码用量")
    private Long bomSizeQuantitySid;

    @Excel(name = "系统ID-物料BOM组件明细")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-物料BOM组件明细")
    private Long bomItemSid;

    @Excel(name = "上级商品SKU档案")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "上级商品SKU档案")
    private Long skuSid;

    @Excel(name = "上级商品SKU类型编码")
    @ApiModelProperty(value = "上级商品SKU类型编码")
    private String skuType;

    @Excel(name = "用量")
    @ApiModelProperty(value = "用量")
    private BigDecimal quantity;

    @Excel(name = "用量计量单位编码")
    @ApiModelProperty(value = "用量计量单位编码")
    private String unitQuantity;

    @Excel(name = "商品编码sid（物料/商品/服务）")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "商品编码sid（物料/商品/服务）")
    private Long materialSid;

    @Excel(name = "系统SID-商品BOM档案")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品BOM档案")
    private Long bomSid;

    @NotEmpty(message = "状态不能为空")
    @Excel(name = "处理状态",dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @Excel(name = "创建人账号")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @Excel(name = "更新人账号")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccount;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    @Excel(name = "确认人账号")
    @ApiModelProperty(value = "确认人账号")
    private String confirmerAccount;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "确认时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    @TableField(exist = false)
    private List<TecBomSizeSkuRequest> skuList;
}
