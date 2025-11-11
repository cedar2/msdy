package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.PayUpdateProductProcessStep;

/**
 * 商品道序变更-主Mapper接口
 *
 * @author chenkw
 * @date 2022-11-08
 */
public interface PayUpdateProductProcessStepMapper extends BaseMapper<PayUpdateProductProcessStep> {


    PayUpdateProductProcessStep selectPayUpdateProductProcessStepById(Long updateProductProcessStepSid);

    List<PayUpdateProductProcessStep> selectPayUpdateProductProcessStepList(PayUpdateProductProcessStep payUpdateProductProcessStep);

    /**
     * 添加多个
     *
     * @param list List PayUpdateProductProcessStep
     * @return int
     */
    int inserts(@Param("list") List<PayUpdateProductProcessStep> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity PayUpdateProductProcessStep
     * @return int
     */
    int updateAllById(PayUpdateProductProcessStep entity);

    /**
     * 更新多个
     *
     * @param list List PayUpdateProductProcessStep
     * @return int
     */
    int updatesAllById(@Param("list") List<PayUpdateProductProcessStep> list);


}
