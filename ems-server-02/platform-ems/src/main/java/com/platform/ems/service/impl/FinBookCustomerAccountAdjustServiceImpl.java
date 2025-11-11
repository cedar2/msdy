package com.platform.ems.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.ems.domain.FinBookCustomerAccountAdjust;
import com.platform.ems.mapper.FinBookCustomerAccountAdjustMapper;
import com.platform.ems.service.IFinBookCustomerAccountAdjustService;

/**
 * 财务流水账-客户调账Service业务层处理
 *
 * @author linhongwei
 * @date 2021-06-08
 */
@Service
@SuppressWarnings("all")
public class FinBookCustomerAccountAdjustServiceImpl extends ServiceImpl<FinBookCustomerAccountAdjustMapper,FinBookCustomerAccountAdjust>  implements IFinBookCustomerAccountAdjustService {
    @Autowired
    private FinBookCustomerAccountAdjustMapper finBookCustomerAccountAdjustMapper;

    /**
     * 查报表
     * @param entity
     * @return
     */
    @Override
    public List<FinBookCustomerAccountAdjust> getReportForm(FinBookCustomerAccountAdjust entity){
        List<FinBookCustomerAccountAdjust> responseList = finBookCustomerAccountAdjustMapper.getReportForm(entity);
        return responseList;
    }
}
