package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinBookCustomerAccountBalance;

/**
 * 财务流水账-客户账互抵Service接口
 *
 * @author linhongwei
 * @date 2021-06-11
 */
public interface IFinBookCustomerAccountBalanceService extends IService<FinBookCustomerAccountBalance>{

    /**
     * 查报表
     * @param entity
     * @return
     */
    List<FinBookCustomerAccountBalance> getReportForm(FinBookCustomerAccountBalance entity);
}
