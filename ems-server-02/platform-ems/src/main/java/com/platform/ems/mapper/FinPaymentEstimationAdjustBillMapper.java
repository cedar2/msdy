package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinPaymentEstimationAdjustBill;

/**
 * 应付暂估调价量单Mapper接口
 *
 * @author chenkw
 * @date 2022-01-10
 */
public interface FinPaymentEstimationAdjustBillMapper extends BaseMapper<FinPaymentEstimationAdjustBill> {

    FinPaymentEstimationAdjustBill selectFinPaymentEstimationAdjustBillById(Long paymentEstimationAdjustBillSid);

    List<FinPaymentEstimationAdjustBill> selectFinPaymentEstimationAdjustBillList(FinPaymentEstimationAdjustBill finPaymentEstimationAdjustBill);

    /**
     * 添加多个
     *
     * @param list List FinPaymentEstimationAdjustBill
     * @return int
     */
    int inserts(@Param("list") List<FinPaymentEstimationAdjustBill> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FinPaymentEstimationAdjustBill
     * @return int
     */
    int updateAllById(FinPaymentEstimationAdjustBill entity);

}
