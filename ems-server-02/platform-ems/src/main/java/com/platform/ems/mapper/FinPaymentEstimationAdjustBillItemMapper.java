package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinPaymentEstimationAdjustBillItem;

/**
 * 应付暂估调价量单-明细Mapper接口
 *
 * @author chenkw
 * @date 2022-01-10
 */
public interface FinPaymentEstimationAdjustBillItemMapper extends BaseMapper<FinPaymentEstimationAdjustBillItem> {

    FinPaymentEstimationAdjustBillItem selectFinPaymentEstimationAdjustBillItemById(Long paymentEstimationAdjustBillItemSid);

    List<FinPaymentEstimationAdjustBillItem> selectFinPaymentEstimationAdjustBillItemList(FinPaymentEstimationAdjustBillItem finPaymentEstimationAdjustBillItem);

    /**
     * 添加多个
     *
     * @param list List FinPaymentEstimationAdjustBillItem
     * @return int
     */
    int inserts(@Param("list") List<FinPaymentEstimationAdjustBillItem> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FinPaymentEstimationAdjustBillItem
     * @return int
     */
    int updateAllById(FinPaymentEstimationAdjustBillItem entity);

    /**
     * 更新多个
     *
     * @param list List FinPaymentEstimationAdjustBillItem
     * @return int
     */
    int updatesAllById(@Param("list") List<FinPaymentEstimationAdjustBillItem> list);


}
