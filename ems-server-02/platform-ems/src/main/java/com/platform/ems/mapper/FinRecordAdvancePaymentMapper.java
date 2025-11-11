package com.platform.ems.mapper;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.FinRecordAdvancePayment;

/**
 * 供应商业务台账-预付Mapper接口
 * 
 * @author qhq
 * @date 2021-05-29
 */
public interface FinRecordAdvancePaymentMapper  extends BaseMapper<FinRecordAdvancePayment> {


    FinRecordAdvancePayment selectFinRecordAdvancePaymentById(Long recordAdvancePaymentSid);

    List<FinRecordAdvancePayment> selectFinRecordAdvancePaymentList(FinRecordAdvancePayment finRecordAdvancePayment);

    /**
     * 添加多个
     * @param list List FinRecordAdvancePayment
     * @return int
     */
    int inserts(@Param("list") List<FinRecordAdvancePayment> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity FinRecordAdvancePayment
    * @return int
    */
    int updateAllById(FinRecordAdvancePayment entity);

    /**
     * 更新多个
     * @param list List FinRecordAdvancePayment
     * @return int
     */
    int updatesAllById(@Param("list") List<FinRecordAdvancePayment> list);
    
    /**
     * 获取报表
     * @param finRecordAdvancePayment
     * @return
     */
    List<FinRecordAdvancePayment> getReportForm(FinRecordAdvancePayment finRecordAdvancePayment);


}
