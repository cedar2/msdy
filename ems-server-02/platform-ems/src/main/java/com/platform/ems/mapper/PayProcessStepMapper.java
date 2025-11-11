package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.PayProcessStep;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 通用道序Mapper接口
 *
 * @author linhongwei
 * @date 2021-09-07
 */
public interface PayProcessStepMapper extends BaseMapper<PayProcessStep> {


    PayProcessStep selectPayProcessStepById(Long processStepSid);

    List<PayProcessStep> selectPayProcessStepList(PayProcessStep payProcessStep);

    /**
     * 添加多个
     *
     * @param list List PayProcessStep
     * @return int
     */
    int inserts(@Param("list") List<PayProcessStep> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity PayProcessStep
     * @return int
     */
    int updateAllById(PayProcessStep entity);

    /**
     * 更新多个
     *
     * @param list List PayProcessStep
     * @return int
     */
    int updatesAllById(@Param("list") List<PayProcessStep> list);


}
