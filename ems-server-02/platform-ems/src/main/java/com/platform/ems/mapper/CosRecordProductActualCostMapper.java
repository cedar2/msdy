package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.CosRecordProductActualCost;

/**
 * 商品实际成本台账表Mapper接口
 *
 * @author chenkw
 * @date 2023-04-27
 */
public interface CosRecordProductActualCostMapper extends BaseMapper<CosRecordProductActualCost> {

    CosRecordProductActualCost selectCosRecordProductActualCostById(Long recordCostSid);

    List<CosRecordProductActualCost> selectCosRecordProductActualCostList(CosRecordProductActualCost cosRecordProductActualCost);

    /**
     * 添加多个
     *
     * @param list List CosRecordProductActualCost
     * @return int
     */
    int inserts(@Param("list") List<CosRecordProductActualCost> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity CosRecordProductActualCost
     * @return int
     */
    int updateAllById(CosRecordProductActualCost entity);

    /**
     * 更新多个
     *
     * @param list List CosRecordProductActualCost
     * @return int
     */
    int updatesAllById(@Param("list") List<CosRecordProductActualCost> list);

}
