package com.platform.ems.mapper;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.FinRecordAdvanceReceipt;

/**
 * 客户业务台账-预收Mapper接口
 * 
 * @author qhq
 * @date 2021-06-16
 */
public interface FinRecordAdvanceReceiptMapper  extends BaseMapper<FinRecordAdvanceReceipt> {


    FinRecordAdvanceReceipt selectFinRecordAdvanceReceiptById(Long recordAdvanceReceiptSid);

    List<FinRecordAdvanceReceipt> selectFinRecordAdvanceReceiptList(FinRecordAdvanceReceipt finRecordAdvanceReceipt);

    /**
     * 添加多个
     * @param list List FinRecordAdvanceReceipt
     * @return int
     */
    int inserts(@Param("list") List<FinRecordAdvanceReceipt> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity FinRecordAdvanceReceipt
    * @return int
    */
    int updateAllById(FinRecordAdvanceReceipt entity);

    /**
     * 更新多个
     * @param list List FinRecordAdvanceReceipt
     * @return int
     */
    int updatesAllById(@Param("list") List<FinRecordAdvanceReceipt> list);

    List<FinRecordAdvanceReceipt> getReportForm(FinRecordAdvanceReceipt entity);

}
