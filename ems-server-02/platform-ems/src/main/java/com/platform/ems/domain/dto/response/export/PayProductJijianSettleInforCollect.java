package com.platform.ems.domain.dto.response.export;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 商品计件结算汇总导出
 *
 * @author
 * @date
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PayProductJijianSettleInforCollect {

    @Excel(name = "工厂")
    @ApiModelProperty(value = "工厂简称 ")
    private String plantShortName;

    @Excel(name = "商品编码(款号)")
    @ApiModelProperty(value = "商品编码")
    private String productCode;

    @Excel(name = "排产批次号")
    @ApiModelProperty(value = "排产批次号")
    private String paichanBatchToString;

    @ApiModelProperty(value = "操作部门（数据字典的键值或配置档案的编码）")
    private String department;

    @Excel(name = "操作部门")
    @ApiModelProperty(value = "操作部门（数据字典的键值或配置档案的编码）")
    private String departmentName;

    @Excel(name = "商品工价类型", dictType = "s_product_price_type")
    @ApiModelProperty(value = "商品工价类型（数据字典的键值或配置档案的编码）")
    private String productPriceType;

    @Excel(name = "计薪完工类型", dictType = "s_jixin_wangong_type")
    @ApiModelProperty(value = "计薪完工类型（数据字典的键值或配置档案的编码）")
    private String jixinWangongType;

    @Excel(name = "结算数累计(已确认)")
    @ApiModelProperty(value = "结算数累计(已确认)")
    private String settleQuantityCheckToString;

    @Excel(name = "结算数累计(含保存)")
    @ApiModelProperty(value = "结算数累计(含保存)")
    private String settleQuantityTotalToString;

    @Excel(name = "商品工价小计（倍率前）")
    @ApiModelProperty(value = "道序工价小计（倍率前）")
    private String totalPriceBlqToString;

    @Excel(name = "实裁数")
    @TableField(exist = false)
    @ApiModelProperty(value = "实裁量")
    private String shicaiQuantityToString;

    @Excel(name = "未结算数累计(已确认)")
    @TableField(exist = false)
    @ApiModelProperty(value = "未结算数累计(已确认)")
    private String weiSettleQuantityCheckToString;

    @Excel(name = "未结算数累计(含保存)")
    @TableField(exist = false)
    @ApiModelProperty(value = "未结算数累计(含保存)")
    private String weiSettleQuantityTotalToString;

    @Excel(name = "客户")
    @ApiModelProperty(value = "客户简称 ")
    private String customerShortName;

    @Excel(name = "商品名称")
    @ApiModelProperty(value = "商品名称")
    private String materialName;

    @Excel(name = "商品工价小计（倍率后）")
    @ApiModelProperty(value = "道序工价小计（倍率后）")
    private String totalPriceBlhToString;

}
