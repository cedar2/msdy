package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinReceiptEstimationAdjustBillItem;

/**
 * 应收暂估调价量单-明细Mapper接口
 *
 * @author chenkw
 * @date 2022-01-10
 */
public interface FinReceiptEstimationAdjustBillItemMapper extends BaseMapper<FinReceiptEstimationAdjustBillItem> {

    FinReceiptEstimationAdjustBillItem selectFinReceiptEstimationAdjustBillItemById(Long receiptEstimationAdjustBillItemSid);

    List<FinReceiptEstimationAdjustBillItem> selectFinReceiptEstimationAdjustBillItemList(FinReceiptEstimationAdjustBillItem finReceiptEstimationAdjustBillItem);

    /**
     * 添加多个
     *
     * @param list List FinReceiptEstimationAdjustBillItem
     * @return int
     */
    int inserts(@Param("list") List<FinReceiptEstimationAdjustBillItem> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FinReceiptEstimationAdjustBillItem
     * @return int
     */
    int updateAllById(FinReceiptEstimationAdjustBillItem entity);

}
