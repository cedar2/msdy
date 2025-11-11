package com.platform.ems.domain.dto.response;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import java.io.Serializable;
import java.math.BigDecimal;
/**
 * 外部接口 物料采购订单明细
 *
 * @author yangqz
 * @date 2022-2-21
 */
@Data
@ApiModel
@Accessors(chain = true)
public class PurPurchaseOrderItemOutResponse implements Serializable {

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购订单明细sid")
    private Long purchaseOrderItemSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "物料 sid")
    private Long materialSid;

    @ApiModelProperty(value = "物料名称")
    private String materialName;

    @ApiModelProperty(value = "物料编码")
    private String materialCode;

    @ApiModelProperty(value = "物料sku1sid")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long materialSku1Sid;

    @ApiModelProperty(value = "物料sku1名称")
    private String materialSku1Name;

    @ApiModelProperty(value = "物料sku1编码")
    private String materialSku1Code;

    @ApiModelProperty(value = "物料sku2sid")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long materialSku2Sid;

    @ApiModelProperty(value = "物料sku2名称")
    private String materialSku2Name;

    @ApiModelProperty(value = "物料sk2编码")
    private String materialSku2Code;

    @ApiModelProperty(value = "数量")
    private BigDecimal quantity;

    @ApiModelProperty(value = "基本计量单位编码")
    private String unitBase;

    @ApiModelProperty(value = "基本计量单位名称")
    private String unitBaseName;

    @ApiModelProperty(value = "产品季名称")
    private String productSeasonName;

    @ApiModelProperty(value = "款号sid")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long productSid;

    @ApiModelProperty(value = "款号名称")
    private String productName;

    @ApiModelProperty(value = "款号code")
    private String productCode;

    @ApiModelProperty(value = "款颜色sid")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long productSku1Sid;

    @TableField(exist = false)
    @ApiModelProperty(value = "款颜色")
    private String productSku1Name;

    @ApiModelProperty(value = "款尺码sid")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long productSku2Sid;

    @TableField(exist = false)
    @ApiModelProperty(value = "款尺码")
    private String productSku2Name;

    @ApiModelProperty(value = "款商品条码")
    private String productBarcodeSid;

}
