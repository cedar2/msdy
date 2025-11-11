package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.SalSalesIntentOrderItem;

/**
 * 销售意向单-明细Mapper接口
 *
 * @author chenkw
 * @date 2022-10-17
 */
public interface SalSalesIntentOrderItemMapper extends BaseMapper<SalSalesIntentOrderItem> {

    SalSalesIntentOrderItem selectSalSalesIntentOrderItemById(Long salesIntentOrderItemSid);

    List<SalSalesIntentOrderItem> selectSalSalesIntentOrderItemList(SalSalesIntentOrderItem salSalesIntentOrderItem);

    /**
     * 添加多个
     *
     * @param list List SalSalesIntentOrderItem
     * @return int
     */
    int inserts(@Param("list") List<SalSalesIntentOrderItem> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity SalSalesIntentOrderItem
     * @return int
     */
    int updateAllById(SalSalesIntentOrderItem entity);

    /**
     * 更新多个
     *
     * @param list List SalSalesIntentOrderItem
     * @return int
     */
    int updatesAllById(@Param("list") List<SalSalesIntentOrderItem> list);


}
