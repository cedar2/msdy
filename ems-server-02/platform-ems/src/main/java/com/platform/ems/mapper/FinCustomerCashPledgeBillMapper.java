package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinCustomerCashPledgeBill;

/**
 * 客户押金Mapper接口
 *
 * @author chenkw
 * @date 2021-09-22
 */
public interface FinCustomerCashPledgeBillMapper extends BaseMapper<FinCustomerCashPledgeBill> {

    FinCustomerCashPledgeBill selectFinCustomerCashPledgeBillById(Long cashPledgeBillSid);

    List<FinCustomerCashPledgeBill> selectFinCustomerCashPledgeBillList(FinCustomerCashPledgeBill finCustomerCashPledgeBill);

    /**
     * 添加多个
     *
     * @param list List FinCustomerCashPledgeBill
     * @return int
     */
    int inserts(@Param("list") List<FinCustomerCashPledgeBill> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FinCustomerCashPledgeBill
     * @return int
     */
    int updateAllById(FinCustomerCashPledgeBill entity);

}
