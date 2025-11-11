package com.platform.ems.mapper;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.FinBookCustomerAccountBalance;

/**
 * 财务流水账-客户账互抵Mapper接口
 *
 * @author qhq
 * @date 2021-06-11
 */
public interface FinBookCustomerAccountBalanceMapper  extends BaseMapper<FinBookCustomerAccountBalance> {


    FinBookCustomerAccountBalance selectFinBookCustomerAccountBalanceById(Long bookAccountBalanceSid);

    List<FinBookCustomerAccountBalance> selectFinBookCustomerAccountBalanceList(FinBookCustomerAccountBalance finBookCustomerAccountBalance);

    /**
     * 添加多个
     * @param list List FinBookCustomerAccountBalance
     * @return int
     */
    int inserts(@Param("list") List<FinBookCustomerAccountBalance> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity FinBookCustomerAccountBalance
    * @return int
    */
    int updateAllById(FinBookCustomerAccountBalance entity);

    /**
     * 更新多个
     * @param list List FinBookCustomerAccountBalance
     * @return int
     */
    int updatesAllById(@Param("list") List<FinBookCustomerAccountBalance> list);

    /**
     * 查报表
     * @param entity
     * @return
     */
    List<FinBookCustomerAccountBalance> getReportForm(FinBookCustomerAccountBalance entity);
}
