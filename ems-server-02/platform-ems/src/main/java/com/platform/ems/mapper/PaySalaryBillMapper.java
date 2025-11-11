package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.PaySalaryBill;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 工资单-主Mapper接口
 *
 * @author linhongwei
 * @date 2021-09-14
 */
public interface PaySalaryBillMapper extends BaseMapper<PaySalaryBill> {


    PaySalaryBill selectPaySalaryBillById(Long salaryBillSid);

    List<PaySalaryBill> selectPaySalaryBillList(PaySalaryBill paySalaryBill);

    /**
     * 添加多个
     *
     * @param list List PaySalaryBill
     * @return int
     */
    int inserts(@Param("list") List<PaySalaryBill> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity PaySalaryBill
     * @return int
     */
    int updateAllById(PaySalaryBill entity);

    /**
     * 更新多个
     *
     * @param list List PaySalaryBill
     * @return int
     */
    int updatesAllById(@Param("list") List<PaySalaryBill> list);


}
