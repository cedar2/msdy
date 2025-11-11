package com.platform.ems.mapper;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.CosProductCost;

/**
 * 商品成本核算主Mapper接口
 * 
 * @author qhq
 * @date 2021-04-02
 */
public interface CosProductCostMapper  extends BaseMapper<CosProductCost> {


    CosProductCost selectCosProductCostById(@Param("productCostSid") Long productCostSid);

    List<CosProductCost> selectCosProductCostList(CosProductCost cosProductCost);

    /**
     * 添加多个
     * @param list List CosProductCost
     * @return int
     */
    int inserts(@Param("list") List<CosProductCost> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity CosProductCost
    * @return int
    */
    int updateAllById(CosProductCost entity);

    /**
     * 更新多个
     * @param list List CosProductCost
     * @return int
     */
    int updatesAllById(@Param("list") List<CosProductCost> list);
    
    /**
     * 修改流程参数
     * @param cosProductCost
     * @return
     */
    int updateProcessValue(CosProductCost cosProductCost);


}
