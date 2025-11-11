package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinPaymentEstimationAdjustBillAttach;

/**
 * 应付暂估调价量单-附件Mapper接口
 *
 * @author chenkw
 * @date 2022-01-10
 */
public interface FinPaymentEstimationAdjustBillAttachMapper extends BaseMapper<FinPaymentEstimationAdjustBillAttach> {


    FinPaymentEstimationAdjustBillAttach selectFinPaymentEstimationAdjustBillAttachById(Long paymentEstimationAdjustBillAttachSid);

    List<FinPaymentEstimationAdjustBillAttach> selectFinPaymentEstimationAdjustBillAttachList(FinPaymentEstimationAdjustBillAttach finPaymentEstimationAdjustBillAttach);

    /**
     * 添加多个
     *
     * @param list List FinPaymentEstimationAdjustBillAttach
     * @return int
     */
    int inserts(@Param("list") List<FinPaymentEstimationAdjustBillAttach> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FinPaymentEstimationAdjustBillAttach
     * @return int
     */
    int updateAllById(FinPaymentEstimationAdjustBillAttach entity);

    /**
     * 更新多个
     *
     * @param list List FinPaymentEstimationAdjustBillAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<FinPaymentEstimationAdjustBillAttach> list);


}
