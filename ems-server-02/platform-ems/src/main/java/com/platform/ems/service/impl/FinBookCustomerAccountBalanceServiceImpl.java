package com.platform.ems.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.ems.domain.FinBookCustomerAccountBalance;
import com.platform.ems.mapper.FinBookCustomerAccountBalanceMapper;
import com.platform.ems.service.IFinBookCustomerAccountBalanceService;

/**
 * 财务流水账-客户账互抵Service业务层处理
 *
 * @author qhq
 * @date 2021-06-11
 */
@Service
@SuppressWarnings("all")
public class FinBookCustomerAccountBalanceServiceImpl extends ServiceImpl<FinBookCustomerAccountBalanceMapper,FinBookCustomerAccountBalance>  implements IFinBookCustomerAccountBalanceService {
    @Autowired
    private FinBookCustomerAccountBalanceMapper finBookCustomerAccountBalanceMapper;

    /**
     * 查报表
     * @param entity
     * @return
     */
    @Override
    public List<FinBookCustomerAccountBalance> getReportForm(FinBookCustomerAccountBalance entity){
        return finBookCustomerAccountBalanceMapper.getReportForm(entity);
    }
}
