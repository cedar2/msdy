package com.platform.ems.domain.dto.response;

import com.baomidou.mybatisplus.annotation.TableField;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 畅销款排行榜
 *
 * @author yangqz
 * @date
 */
@Data
@ApiModel
@Accessors(chain = true)
public class OrderBestSellingResponse {

    private String  productSeasonSid;

    private String materialSid;

    @ApiModelProperty(value ="下单日期起")
    @Excel(name = "下单日期起")
    private String documentBeginTime;

    @ApiModelProperty(value ="下单日期至")
    @Excel(name = "下单日期至")
    private String documentEndTime;

    @ApiModelProperty(value = "序号")
    private Integer sort;

    @ApiModelProperty(value = "商品名称")
    @Excel(name = "款号")
    private String materialName;

    @ApiModelProperty(value = "商品编码")
    @Excel(name = "款名称")
    private String materialCode;

    @ApiModelProperty(value = "颜色")
    @Excel(name = "颜色")
    private String sku1Name;

    @ApiModelProperty(value = "尺码")
    @Excel(name = "尺码")
    private String sku2Name;

    @ApiModelProperty(value = "图片路径")
    private String picturePath;

    @ApiModelProperty(value = "下单季")
    private String  productSeasonName;

    @ApiModelProperty(value = "订单总量")
    @Excel(name = "订单总量")
    private BigDecimal orderTotalQuantity;

    @ApiModelProperty(value = "订单总数")
    private BigDecimal orderCountQuantity;

    @ApiModelProperty(value = "订单总金额")
    @Excel(name = "订单总金额(万)")
    private BigDecimal orderTotalPrice;

    @ApiModelProperty(value = "已出入库总数")
    @Excel(name = "已出入库总量")
    private BigDecimal invTotalQuantity;

    @ApiModelProperty(value = "已出入库总金额")
    @Excel(name = "已出入库总金额(万)")
    private BigDecimal invTotalPrice;

    @ApiModelProperty(value = "基本计量单位")
    @Excel(name = "计量单位")
    private String unitBaseName;

}
