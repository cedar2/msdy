package com.platform.ems.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.annotation.SqlParser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import com.platform.ems.domain.dto.response.form.BasMatBarcodeOperLvlCategorySkuForm;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.BasMaterialBarcodeOperateLevel;

/**
 * 商品SKU条码-网店运营信息Mapper接口
 *
 * @author chenkw
 * @date 2023-01-18
 */
public interface BasMaterialBarcodeOperateLevelMapper extends BaseMapper<BasMaterialBarcodeOperateLevel> {

    BasMaterialBarcodeOperateLevel selectBasMaterialBarcodeOperateLevelById(Long materialBarcodeOperateLevelSid);

    List<BasMaterialBarcodeOperateLevel> selectBasMaterialBarcodeOperateLevelList(BasMaterialBarcodeOperateLevel basMaterialBarcodeOperateLevel);

    /**
     * 添加多个
     *
     * @param list List BasMaterialBarcodeOperateLevel
     * @return int
     */
    int inserts(@Param("list") List<BasMaterialBarcodeOperateLevel> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity BasMaterialBarcodeOperateLevel
     * @return int
     */
    int updateAllById(BasMaterialBarcodeOperateLevel entity);

    /**
     * 更新多个
     *
     * @param list List BasMaterialBarcodeOperateLevel
     * @return int
     */
    int updatesAllById(@Param("list") List<BasMaterialBarcodeOperateLevel> list);

    /**
     * 报表中心类目明细报表
     *
     * @param request BasMatBarcodeOperLvlCategorySkuForm
     * @return 报表中心类目明细报表
     */
    @SqlParser(filter=true)
    List<BasMatBarcodeOperLvlCategorySkuForm> selectMaterialBarcodeOperateLevelCategorySkuForm(BasMatBarcodeOperLvlCategorySkuForm request);

}
