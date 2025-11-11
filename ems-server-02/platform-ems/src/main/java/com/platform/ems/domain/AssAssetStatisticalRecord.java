package com.platform.ems.domain;


import com.baomidou.mybatisplus.annotation.TableField;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

/**
 * 资产统计台账对象 s_ass_asset_record
 *
 * @author zhuangyz
 * @date 2022-07-06
 */
@Data
@Builder
public class AssAssetStatisticalRecord{

    /**
     * 资产类型（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "资产类型",dictType = "s_asset_type")
    private String assetType;

    /**
     * 公司名称
     */
    @Excel(name = "公司")
    private String companyName;

    /**
     * 资产数
     */
    @Excel(name = "资产数")
    private String assetCount;


    /**
     * 采购金额
     */
    @Excel(name = "估值(万)")
    private BigDecimal currencyAmount;

}
