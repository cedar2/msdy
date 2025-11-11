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
 * 外部接口 交货单明细
 *
 * @author yangqz
 * @date 2022-2-21
 */
@Data
@ApiModel
@Accessors(chain = true)
public class DelDeliveryNoteItemOutResponse implements Serializable {

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "物料sid")
    private Long materialSid;

    @ApiModelProperty(value = "物料编码")
    private String materialCode;

    @ApiModelProperty(value = "物料名称")
    private String materialName;

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

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "商品条码sid")
    private Long barcodeSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "商品条码")
    private Long barcode;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料商品主图片路径")
    private String picturePath;

    @ApiModelProperty(value = "交货量")
    private BigDecimal deliveryQuantity;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购订单明细sid")
    private Long purchaseOrderItemSid;

    @ApiModelProperty(value ="备注")
    private String remark;
}
