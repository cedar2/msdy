package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinReceiptEstimationAdjustBill;

/**
 * 应收暂估调价量单Mapper接口
 *
 * @author chenkw
 * @date 2022-01-10
 */
public interface FinReceiptEstimationAdjustBillMapper extends BaseMapper<FinReceiptEstimationAdjustBill> {

    FinReceiptEstimationAdjustBill selectFinReceiptEstimationAdjustBillById(Long receiptEstimationAdjustBillSid);

    List<FinReceiptEstimationAdjustBill> selectFinReceiptEstimationAdjustBillList(FinReceiptEstimationAdjustBill finReceiptEstimationAdjustBill);

    /**
     * 添加多个
     *
     * @param list List FinReceiptEstimationAdjustBill
     * @return int
     */
    int inserts(@Param("list") List<FinReceiptEstimationAdjustBill> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FinReceiptEstimationAdjustBill
     * @return int
     */
    int updateAllById(FinReceiptEstimationAdjustBill entity);

}
