package com.platform.ems.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.ems.domain.FinBookCustomerAccountBalanceItem;
import com.platform.ems.mapper.FinBookCustomerAccountBalanceItemMapper;
import com.platform.ems.service.IFinBookCustomerAccountBalanceItemService;

/**
 * 财务流水账-明细-客户账互抵Service业务层处理
 *
 * @author linhongwei
 * @date 2021-06-11
 */
@Service
@SuppressWarnings("all")
public class FinBookCustomerAccountBalanceItemServiceImpl extends ServiceImpl<FinBookCustomerAccountBalanceItemMapper,FinBookCustomerAccountBalanceItem>  implements IFinBookCustomerAccountBalanceItemService {

}
