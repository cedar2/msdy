package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.PayProductProcessStep;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品道序-主Mapper接口
 *
 * @author linhongwei
 * @date 2021-09-08
 */
public interface PayProductProcessStepMapper extends BaseMapper<PayProductProcessStep> {


    PayProductProcessStep selectPayProductProcessStepById(Long productProcessStepSid);

    List<PayProductProcessStep> selectPayProductProcessStepList(PayProductProcessStep payProductProcessStep);

    /**
     * 添加多个
     *
     * @param list List PayProductProcessStep
     * @return int
     */
    int inserts(@Param("list") List<PayProductProcessStep> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity PayProductProcessStep
     * @return int
     */
    int updateAllById(PayProductProcessStep entity);

    /**
     * 更新状态和是否变更栏位
     * @param entity
     * @return
     */
    int updateAllBysId(PayProductProcessStep entity);

    /**
     * 更新多个
     *
     * @param list List PayProductProcessStep
     * @return int
     */
    int updatesAllById(@Param("list") List<PayProductProcessStep> list);

    /**
     * 商品道序下拉框接口
     */
    List<PayProductProcessStep> getList(PayProductProcessStep payProductProcessStep);
}
