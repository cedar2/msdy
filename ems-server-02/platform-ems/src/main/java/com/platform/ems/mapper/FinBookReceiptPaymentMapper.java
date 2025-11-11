package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinBookReceiptPayment;

/**
 * 财务流水账-收款Mapper接口
 *
 * @author linhongwei
 * @date 2021-06-09
 */
public interface FinBookReceiptPaymentMapper  extends BaseMapper<FinBookReceiptPayment> {

    FinBookReceiptPayment selectFinBookReceiptPaymentById(Long bookReceiptPaymentSid);

    List<FinBookReceiptPayment> selectFinBookReceiptPaymentList(FinBookReceiptPayment finBookReceiptPayment);

    /**
     * 添加多个
     * @param list List FinBookReceiptPayment
     * @return int
     */
    int inserts(@Param("list") List<FinBookReceiptPayment> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity FinBookReceiptPayment
    * @return int
    */
    int updateAllById(FinBookReceiptPayment entity);

    List<FinBookReceiptPayment> getReportForm(FinBookReceiptPayment entity);
}
