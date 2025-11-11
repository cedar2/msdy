package com.platform.ems.domain.dto.request;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;
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
 * @description: bom尺码拉链长度编辑
 * @author: yang
 * @date: 2021-07-20
 */
@Data
@ApiModel
@Accessors(chain = true)
public class TecBomSizeUpdateRequest extends EmsBaseEntity implements Serializable {

    @ApiModelProperty(value = "系统ID-物料BOM组件具体尺码用量")
    private Long bomSizeQuantitySid;

    @ApiModelProperty(value = "系统ID-物料BOM组件明细")
    private Long bomItemSid;

    @ApiModelProperty(value = "上级商品SKU档案")
    private Long skuSid;

    @ApiModelProperty(value = "上级商品SKU类型编码")
    private String skuType;

    @ApiModelProperty(value = "用量")
    private BigDecimal quantity;

    @ApiModelProperty(value = "用量计量单位编码")
    private String unitQuantity;

    @ApiModelProperty(value = "商品编码sid（物料/商品/服务）")
    private Long materialSid;

    @ApiModelProperty(value = "系统SID-商品BOM档案")
    private Long bomSid;

    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @TableField(exist = false)
    private List<TecBomSizeSkuRequest> skuList;

}
