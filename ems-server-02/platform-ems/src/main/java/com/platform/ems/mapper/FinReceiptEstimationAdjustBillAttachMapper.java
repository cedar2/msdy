package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinReceiptEstimationAdjustBillAttach;

/**
 * 应收暂估调价量单-附件Mapper接口
 *
 * @author chenkw
 * @date 2022-01-10
 */
public interface FinReceiptEstimationAdjustBillAttachMapper extends BaseMapper<FinReceiptEstimationAdjustBillAttach> {


    FinReceiptEstimationAdjustBillAttach selectFinReceiptEstimationAdjustBillAttachById(Long receiptEstimationAdjustBillAttachSid);

    List<FinReceiptEstimationAdjustBillAttach> selectFinReceiptEstimationAdjustBillAttachList(FinReceiptEstimationAdjustBillAttach finReceiptEstimationAdjustBillAttach);

    /**
     * 添加多个
     *
     * @param list List FinReceiptEstimationAdjustBillAttach
     * @return int
     */
    int inserts(@Param("list") List<FinReceiptEstimationAdjustBillAttach> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FinReceiptEstimationAdjustBillAttach
     * @return int
     */
    int updateAllById(FinReceiptEstimationAdjustBillAttach entity);

    /**
     * 更新多个
     *
     * @param list List FinReceiptEstimationAdjustBillAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<FinReceiptEstimationAdjustBillAttach> list);


}
