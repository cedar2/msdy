package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinClearLogAdvanceReceiptPayment;

/**
 * 核销客户已预收款日志Mapper接口
 *
 * @author platform
 * @date 2024-03-28
 */
public interface FinClearLogAdvanceReceiptPaymentMapper  extends BaseMapper<FinClearLogAdvanceReceiptPayment> {

    /**
     * 查询详情
     * @param clearLogAdvanceReceiptPaymentSid 单据sid
     * @return FinClearLogAdvanceReceiptPayment
     */
    FinClearLogAdvanceReceiptPayment selectFinClearLogAdvanceReceiptPaymentById(Long clearLogAdvanceReceiptPaymentSid);

    /**
     * 查询列表
     * @param finClearLogAdvanceReceiptPayment FinClearLogAdvanceReceiptPayment
     * @return List
     */
    List<FinClearLogAdvanceReceiptPayment> selectFinClearLogAdvanceReceiptPaymentList(FinClearLogAdvanceReceiptPayment finClearLogAdvanceReceiptPayment);

    /**
     * 添加多个
     * @param list List FinClearLogAdvanceReceiptPayment
     * @return int
     */
    int inserts(@Param("list") List<FinClearLogAdvanceReceiptPayment> list);

}
