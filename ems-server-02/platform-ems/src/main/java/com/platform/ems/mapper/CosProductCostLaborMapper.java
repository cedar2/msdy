package com.platform.ems.mapper;
import java.util.List;

import com.platform.ems.domain.dto.request.CosProductCostLaborRequest;
import com.platform.ems.domain.dto.response.CosProductCostLaborResponse;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.CosProductCostLabor;

/**
 * 商品成本核算-工价成本明细Mapper接口
 * 
 * @author qhq
 * @date 2021-04-02
 */
public interface CosProductCostLaborMapper  extends BaseMapper<CosProductCostLabor> {


    CosProductCostLabor selectCosProductCostLaborById(Long productCostLaborSid);

    List<CosProductCostLabor> selectCosProductCostLaborList(CosProductCostLabor cosProductCostLabor);
    List<CosProductCostLaborResponse> reportProductCostLabor(CosProductCostLaborRequest cosProductCostLaborRequest);
    /**
     * 添加多个
     * @param list List CosProductCostLabor
     * @return int
     */
    int inserts(@Param("list") List<CosProductCostLabor> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity CosProductCostLabor
    * @return int
    */
    int updateAllById(CosProductCostLabor entity);

    /**
     * 更新多个
     * @param list List CosProductCostLabor
     * @return int
     */
    int updatesAllById(@Param("list") List<CosProductCostLabor> list);

    int deleteByProductCostSid(Long productCostSid);
}
