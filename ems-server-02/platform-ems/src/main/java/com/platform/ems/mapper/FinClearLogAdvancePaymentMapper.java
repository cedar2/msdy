package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinClearLogAdvancePayment;

/**
 * 核销供应商已预付款日志Mapper接口
 *
 * @author platform
 * @date 2024-03-28
 */
public interface FinClearLogAdvancePaymentMapper  extends BaseMapper<FinClearLogAdvancePayment> {

    /**
     * 查询详情
     * @param clearLogAdvancePaymentSid 单据sid
     * @return FinClearLogAdvancePayment
     */
    FinClearLogAdvancePayment selectFinClearLogAdvancePaymentById(Long clearLogAdvancePaymentSid);

    /**
     * 查询列表
     * @param finClearLogAdvancePayment FinClearLogAdvancePayment
     * @return List
     */
    List<FinClearLogAdvancePayment> selectFinClearLogAdvancePaymentList(FinClearLogAdvancePayment finClearLogAdvancePayment);

    /**
     * 添加多个
     * @param list List FinClearLogAdvancePayment
     * @return int
     */
    int inserts(@Param("list") List<FinClearLogAdvancePayment> list);

}
