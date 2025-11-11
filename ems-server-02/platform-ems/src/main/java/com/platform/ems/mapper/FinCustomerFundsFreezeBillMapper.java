package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinCustomerFundsFreezeBill;

/**
 * 客户暂押款Mapper接口
 *
 * @author chenkw
 * @date 2021-09-22
 */
public interface FinCustomerFundsFreezeBillMapper extends BaseMapper<FinCustomerFundsFreezeBill> {

    FinCustomerFundsFreezeBill selectFinCustomerFundsFreezeBillById(Long fundsFreezeBillSid);

    List<FinCustomerFundsFreezeBill> selectFinCustomerFundsFreezeBillList(FinCustomerFundsFreezeBill finCustomerFundsFreezeBill);

    /**
     * 添加多个
     *
     * @param list List FinCustomerFundsFreezeBill
     * @return int
     */
    int inserts(@Param("list") List<FinCustomerFundsFreezeBill> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FinCustomerFundsFreezeBill
     * @return int
     */
    int updateAllById(FinCustomerFundsFreezeBill entity);

}
