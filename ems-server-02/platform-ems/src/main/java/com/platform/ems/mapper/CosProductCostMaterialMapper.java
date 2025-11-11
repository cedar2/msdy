package com.platform.ems.mapper;
import java.util.List;

import com.platform.ems.domain.dto.request.CosProductCostMaterialRequest;
import com.platform.ems.domain.dto.response.CosProductCostMaterialResponse;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.CosProductCostMaterial;

/**
 * 商品成本核算-物料成本明细Mapper接口
 * 
 * @author qhq
 * @date 2021-04-02
 */
public interface CosProductCostMaterialMapper  extends BaseMapper<CosProductCostMaterial> {


    List<CosProductCostMaterial> selectCosProductCostMaterialById(Long productCostSid);

    List<CosProductCostMaterial> selectCosProductCostMaterialList(CosProductCostMaterial cosProductCostMaterial);
    List<CosProductCostMaterialResponse> reportMaterialList(CosProductCostMaterialRequest cosProductCostMaterialRequest);
    /**
     * 添加多个
     * @param list List CosProductCostMaterial
     * @return int
     */
    int inserts(@Param("list") List<CosProductCostMaterial> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity CosProductCostMaterial
    * @return int
    */
    int updateAllById(CosProductCostMaterial entity);

    /**
     * 更新多个
     * @param list List CosProductCostMaterial
     * @return int
     */
    int updatesAllById(@Param("list") List<CosProductCostMaterial> list);

    int deleteByProductCostSid(Long productCostSid);
}
