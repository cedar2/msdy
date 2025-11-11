package com.platform.ems.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.ems.domain.FinBookVendorAccountBalanceItem;
import com.platform.ems.mapper.FinBookVendorAccountBalanceItemMapper;
import com.platform.ems.service.IFinBookVendorAccountBalanceItemService;

/**
 * 财务流水账-明细-供应商账互抵Service业务层处理
 *
 * @author linhongwei
 * @date 2021-06-18
 */
@Service
@SuppressWarnings("all")
public class FinBookVendorAccountBalanceItemServiceImpl extends ServiceImpl<FinBookVendorAccountBalanceItemMapper,FinBookVendorAccountBalanceItem>  implements IFinBookVendorAccountBalanceItemService {

}
