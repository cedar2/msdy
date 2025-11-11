package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinBookPayment;

/**
 * 财务流水账-付款Mapper接口
 *
 * @author linhongwei
 * @date 2021-06-07
 */
public interface FinBookPaymentMapper  extends BaseMapper<FinBookPayment> {

    FinBookPayment selectFinBookPaymentById(Long bookPaymentSid);

    List<FinBookPayment> selectFinBookPaymentList(FinBookPayment finBookPayment);

    /**
     * 添加多个
     * @param list List FinBookPayment
     * @return int
     */
    int inserts(@Param("list") List<FinBookPayment> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity FinBookPayment
    * @return int
    */
    int updateAllById(FinBookPayment entity);

    /**
     * 查报表
     * @param entity
     * @return
     */
    List<FinBookPayment> getReportForm(FinBookPayment entity);
}
