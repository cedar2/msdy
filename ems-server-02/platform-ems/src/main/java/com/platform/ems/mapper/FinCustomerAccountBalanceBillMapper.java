package com.platform.ems.mapper;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.FinCustomerAccountBalanceBill;

/**
 * 客户账互抵单Mapper接口
 * 
 * @author qhq
 * @date 2021-05-27
 */
public interface FinCustomerAccountBalanceBillMapper  extends BaseMapper<FinCustomerAccountBalanceBill> {


    FinCustomerAccountBalanceBill selectFinCustomerAccountBalanceBillById(Long customerAccountBalanceBillSid);

    List<FinCustomerAccountBalanceBill> selectFinCustomerAccountBalanceBillList(FinCustomerAccountBalanceBill finCustomerAccountBalanceBill);

    /**
     * 添加多个
     * @param list List FinCustomerAccountBalanceBill
     * @return int
     */
    int inserts(@Param("list") List<FinCustomerAccountBalanceBill> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity FinCustomerAccountBalanceBill
    * @return int
    */
    int updateAllById(FinCustomerAccountBalanceBill entity);

    /**
     * 更新多个
     * @param list List FinCustomerAccountBalanceBill
     * @return int
     */
    int updatesAllById(@Param("list") List<FinCustomerAccountBalanceBill> list);


}
