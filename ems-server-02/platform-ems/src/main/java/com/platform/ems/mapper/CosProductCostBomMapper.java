package com.platform.ems.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.CosProductCostBom;

/**
 * 商品成本核算-BOM主Mapper接口
 * 
 * @author qhq
 * @date 2021-04-25
 */
public interface CosProductCostBomMapper  extends BaseMapper<CosProductCostBom> {


    CosProductCostBom selectCosProductCostBomById(Long productCostBomSid);

    List<CosProductCostBom> selectCosProductCostBomList(CosProductCostBom cosProductCostBom);

    /**
     * 添加多个
     * @param list List CosProductCostBom
     * @return int
     */
    int inserts(@Param("list") List<CosProductCostBom> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity CosProductCostBom
    * @return int
    */
    int updateAllById(CosProductCostBom entity);

    /**
     * 更新多个
     * @param list List CosProductCostBom
     * @return int
     */
    int updatesAllById(@Param("list") List<CosProductCostBom> list);
    
    int deleteProductBomAndMaterialByProductCostSid(Long productCostSid);


}
