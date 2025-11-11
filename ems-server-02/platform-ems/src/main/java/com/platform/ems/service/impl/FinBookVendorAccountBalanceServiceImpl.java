package com.platform.ems.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.ems.domain.FinBookVendorAccountBalance;
import com.platform.ems.mapper.FinBookVendorAccountBalanceMapper;
import com.platform.ems.service.IFinBookVendorAccountBalanceService;

/**
 * 财务流水账-供应商账互抵Service业务层处理
 *
 * @author linhongwei
 * @date 2021-06-18
 */
@Service
@SuppressWarnings("all")
public class FinBookVendorAccountBalanceServiceImpl extends ServiceImpl<FinBookVendorAccountBalanceMapper,FinBookVendorAccountBalance>  implements IFinBookVendorAccountBalanceService {
    @Autowired
    private FinBookVendorAccountBalanceMapper finBookVendorAccountBalanceMapper;

    /**
     * 查流水
     * @param entity
     * @return
     */
    public List<FinBookVendorAccountBalance> getReportForm(FinBookVendorAccountBalance entity) {
        return finBookVendorAccountBalanceMapper.getReportForm(entity);
    }

}
