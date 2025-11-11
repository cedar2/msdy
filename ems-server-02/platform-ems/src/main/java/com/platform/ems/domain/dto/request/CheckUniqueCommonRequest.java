package com.platform.ems.domain.dto.request;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * 校验同一时刻只允许存在一笔采购价
 *
 * @author yangqz
 * @date 2021-3-31
 */
@Data
@ApiModel
@Accessors(chain = true)
public class CheckUniqueCommonRequest {

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商档案sid")
    private Long vendorSid;

    @ApiModelProperty(value = "甲供料方式")
    private String rawMaterialMode;

    @ApiModelProperty(value = "采购模式（数据字典的键值）")
    private String purchaseMode;

    @ApiModelProperty(value = "系统SID-物料档案（物料/商品/服务）")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long materialSid;

    @ApiModelProperty(value = "物料名称")
    private String materialName;

    @ApiModelProperty(value = "系统SID-SKU1档案sid")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long sku1Sid;

    @ApiModelProperty(value = "sid")
    private Long id;

    @ApiModelProperty(value = "价格维度（数据字典的键值）")
    private String priceDimension;

    @ApiModelProperty(value = "处理状态")
    private String[] handleStatusList;

    private String code;
}
