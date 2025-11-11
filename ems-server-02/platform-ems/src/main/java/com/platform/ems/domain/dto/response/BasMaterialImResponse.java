package com.platform.ems.domain.dto.response;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.ems.domain.TecBomItemReport;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 商品导入-物料需求测算
 *
 * @author yangqz
 * @date 2021-7-13
 */
@Data
@ApiModel
@Accessors(chain = true)
public class BasMaterialImResponse implements Serializable {

    @ApiModelProperty(value = "物料（商品/服务）名称")
    private String materialName;

    @ApiModelProperty(value = "物料（商品/服务）名称")
    private String materialCode;

    @ApiModelProperty(value = "系统ID-物料档案")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long materialSid;

    @TableField(exist = false)
    private BigDecimal quantity;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "SKU1的Sid")
    private Long sku1Sid;

    @Excel(name = "SKU1名称")
    @TableField(exist = false)
    @ApiModelProperty(value = "SKU1的name")
    private String sku1Name;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "SKU2的Sid")
    private Long sku2Sid;

    @TableField(exist = false)
    @ApiModelProperty(value = "SKU2的code")
    private String sku2Code;

    @Excel(name = "SKU2名称")
    @TableField(exist = false)
    @ApiModelProperty(value = "SKU2的name")
    private String sku2Name;

    /**
     * 基本计量单位编码
     */
    @ApiModelProperty(value = "基本计量单位编码")
    private String unitBase;

    /**
     * 基本计量单位名称
     */
    @Excel(name = "基本计量单位")
    @ApiModelProperty(value = "基本计量单位名称")
    @TableField(exist = false)
    private String unitBaseName;

    @ApiModelProperty(value = "商品条码code")
    private String barcode;

}
