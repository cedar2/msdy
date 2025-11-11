package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.SalCustomerMonthAccountBill;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 客户对账单Mapper接口
 *
 */
public interface SalCustomerMonthAccountBillMapper extends BaseMapper<SalCustomerMonthAccountBill> {

    SalCustomerMonthAccountBill selectSalCustomerMonthAccountBillById(Long customerMonthAccountBillSid);

    List<SalCustomerMonthAccountBill> selectSalCustomerMonthAccountBillList(SalCustomerMonthAccountBill salCustomerMonthAccountBill);

    /**
     * 添加多个
     *
     * @param list List SalCustomerMonthAccountBill
     * @return int
     */
    int inserts(@Param("list") List<SalCustomerMonthAccountBill> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity SalCustomerMonthAccountBill
     * @return int
     */
    int updateAllById(SalCustomerMonthAccountBill entity);

}
