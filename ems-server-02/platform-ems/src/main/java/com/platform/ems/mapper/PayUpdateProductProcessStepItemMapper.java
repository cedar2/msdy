package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.PayUpdateProductProcessStepItem;

/**
 * 商品道序变更-明细Mapper接口
 *
 * @author chenkw
 * @date 2022-11-08
 */
public interface PayUpdateProductProcessStepItemMapper extends BaseMapper<PayUpdateProductProcessStepItem> {


    PayUpdateProductProcessStepItem selectPayUpdateProductProcessStepItemById(Long updateStepItemSid);

    List<PayUpdateProductProcessStepItem> selectPayUpdateProductProcessStepItemList(PayUpdateProductProcessStepItem payUpdateProductProcessStepItem);

    /**
     * 查询商品道序变更-明细 标志位为删除的明细
     *
     * @param payUpdateProductProcessStepItem 商品道序变更-主表ID
     * @return 商品道序变更-明细
     */
    List<PayUpdateProductProcessStepItem> selectDeleteListById(PayUpdateProductProcessStepItem payUpdateProductProcessStepItem);

    /**
     * 添加多个
     *
     * @param list List PayUpdateProductProcessStepItem
     * @return int
     */
    int inserts(@Param("list") List<PayUpdateProductProcessStepItem> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity PayUpdateProductProcessStepItem
     * @return int
     */
    int updateAllById(PayUpdateProductProcessStepItem entity);

    /**
     * 更新多个
     *
     * @param list List PayUpdateProductProcessStepItem
     * @return int
     */
    int updatesAllById(@Param("list") List<PayUpdateProductProcessStepItem> list);


}
