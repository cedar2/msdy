package com.platform.ems.domain.dto.response.export;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品道序明细报表导出 PayProductProcessStepItemFormExport
 *
 * @author chenkw
 * @date 2022-07-07
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PayProductProcessStepItemFormExport {

    @Excel(name = "商品编码(款号)")
    @ApiModelProperty(value = "商品编码(款号)")
    private String productCode;

    @Excel(name = "商品名称")
    @ApiModelProperty(value = "商品名称")
    private String materialName;

    @Excel(name = "我司样衣号")
    @ApiModelProperty(value = "我司样衣号")
    private String sampleCodeSelf;

    @ApiModelProperty(value = "工厂名称")
    private String plantName;

    @Excel(name = "工厂")
    @ApiModelProperty(value = "工厂简称")
    private String plantShortName;

    @Excel(name = "商品工价类型", dictType = "s_product_price_type")
    @ApiModelProperty(value = "商品工价类型（数据字典的键值或配置档案的编码）")
    private String productPriceType;

    @Excel(name = "计薪完工类型", dictType = "s_jixin_wangong_type")
    @ApiModelProperty(value = "计薪完工类型（数据字典的键值或配置档案的编码）")
    private String jixinWangongType;

    @ApiModelProperty(value = "操作部门")
    private String department;

    @Excel(name = "操作部门")
    @ApiModelProperty(value = "操作部门（数据字典的键值或配置档案的编码）")
    private String departmentName;

    @Excel(name = "道序工价小计(元)")
    @ApiModelProperty(value = "道序工价小计(元)")
    private BigDecimal priceTotal;

    @ApiModelProperty(value = "序号")
    private BigDecimal sort;

    @Excel(name = "序号")
    @ApiModelProperty(value = "序号")
    private String sortToString;

    @Excel(name = "道序名称")
    @ApiModelProperty(value = "道序名称")
    private String processStepName;

    @Excel(name = "工价(元)")
    @ApiModelProperty(value = "工价(元)")
    private BigDecimal price;

    @ApiModelProperty(value = "倍率(道序)")
    private BigDecimal priceRate;

    @Excel(name = "倍率(道序)")
    @ApiModelProperty(value = "倍率(道序)")
    private String priceRateToString;

    @Excel(name = "价格(工价*倍率)", scale = 3)
    @ApiModelProperty(value = "价格(工价*倍率)")
    private BigDecimal actualPrice;

    @Excel(name = "工价标准(元)")
    @ApiModelProperty(value = "工价标准(元)")
    private BigDecimal standardPrice;

    @Excel(name = "所属生产工序")
    @ApiModelProperty(value = "所属生产工序名称")
    private String processName;

    @Excel(name = "工序的最后一道道序", dictType = "s_yesno_flag")
    @ApiModelProperty(value = "工序的最后一道道序（数据字典的键值或配置档案的编码）")
    private String isFinal;

    @Excel(name = "作业单位")
    @ApiModelProperty(value = "作业计量单位名称")
    private String taskUnitName;

    @Excel(name = "道序备注")
    @ApiModelProperty(value = "道序备注")
    private String remark;

    @Excel(name = "道序类别", dictType = "s_process_step_category")
    @ApiModelProperty(value = "道序类别（数据字典的键值或配置档案的编码）")
    private String stepCategory;

    @Excel(name = "道序编码")
    @ApiModelProperty(value = "道序编码")
    private String processStepCode;

    @Excel(name = "商品工价上限(元)")
    @ApiModelProperty(value = "商品工价上限(元)")
    private BigDecimal limitPrice;

    @ApiModelProperty(value = "工价倍率(商品)")
    private BigDecimal productPriceRate;

    @ApiModelProperty(value = "工价倍率(商品)")
    private String productPriceRateToString;

    @Excel(name = "快速编码")
    @ApiModelProperty(value = "快速编码")
    private String simpleCode;

    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人")
    private String creatorAccountName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

}
